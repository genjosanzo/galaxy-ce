package org.mule.galaxy.maven.publish;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.activation.MimeType;

import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * Publishes artifacts and resources to a workspace.
 * 
 * @goal execute
 */
public class PublishMojo extends AbstractMojo {
    /**
    * The maven project.
    *
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
    private MavenProject project;
    
    /**
     * The user's settings.
     *
     * @parameter expression="${settings}"
     * @required
     * @readonly
     */
    private Settings settings;
    
    /**
     * The base directory
     *
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    private File basedir;
    
    /**
     * The Galaxy workspace URL.
     *
     * @parameter
     */
    private String url;
    
    /**
     * The server id to use for username/password information inside your settings.xml.
     *
     * @parameter
     */
    private String serverId;
    
    /**
     * The username (see serverId as well).
     *
     * @parameter
     */
    private String username;

    /**
     * The username (see serverId as well).
     *
     * @parameter
     */
    private String password;
    
    /**
     * Resources to publish to Galaxy
     *
     * @parameter
     */
    private String[] includes;

    /**
     * Resources to exclude from publishing to Galaxy
     *
     * @parameter
     */
    private String[] excludes;

    /**
     * Dependency filter to determine which artifacts which are attached to this project 
     * will be published to Galaxy
     *
     * @parameter
     */
    private String[] dependencyIncludes;

    /**
     * Dependency filter to determine which artifacts which are attached to this project 
     * will NOT be published to Galaxy
     *
     * @parameter
     */
    private String[] dependencyExcludes;
    
    /**
     * Whether or not to publish this project's dependencies to Galaxy.
     *
     * @parameter
     */
    private boolean publishProjectDependencies = true;
    
    /**
     * Whether or not to publish the artifact produced by this build.
     *
     * @parameter
     */
    private boolean publishProjectArtifact = true;

    /**
     * Whether or not to clear the artifacts from the workspace before
     * uploading new ones.
     *
     * @parameter
     */
    private boolean clearWorkspace = false;

    private AbderaClient client;

    private String authorization;

    /**
     * Whether or not dependency excludes should act transitively.
     *
     * @parameter
     */
    private boolean actTransitively = true;


    /**
     * Whether or not the plugin should fail if there were filtering dependency criteria
     * which were not met.
     *
     * @parameter
     */
    private boolean strictFiltering;

    public void execute() throws MojoExecutionException, MojoFailureException {
        
        String auth = null;
        if (serverId != null) {
            Server server = settings.getServer(serverId);
            
            if (server == null) {
                throw new MojoFailureException("Could not find server: " + serverId);
            }

            if (server.getUsername() == null || server.getPassword() == null) {
                throw new MojoFailureException("You must specify a username & password in your settings.xml!");
            }
            
            auth = server.getUsername() + ":" + server.getPassword();
        } else {
            if (username == null || password == null) {
                throw new MojoFailureException("You must specify a username & password.");
            }
            auth = username + ":" + password;
        }
        
        client = new AbderaClient();
        
        authorization = "Basic " + Base64.encode(auth.getBytes());
    
        if (clearWorkspace) {
            clearWorkspace();
        }
        
        Set<Object> artifacts = new HashSet<Object>();
        
        if (publishProjectDependencies) {
            Set<?> deps = project.getArtifacts();
            if (deps != null && deps.size() > 0) artifacts.addAll(deps);
        }
        
        if (publishProjectArtifact) {
            artifacts.add(project.getArtifact());
        }
        
        if (artifacts.size() > 0) {
            List<?> filterIncludes = dependencyIncludes != null ? Arrays.asList(dependencyIncludes) : Collections.EMPTY_LIST;
            List<?> filterExcludes = dependencyExcludes != null ? Arrays.asList(dependencyExcludes) : Collections.EMPTY_LIST;
            
            FilterUtils.filterArtifacts(artifacts, 
                                        filterIncludes, 
                                        filterExcludes, 
                                        strictFiltering, 
                                        actTransitively, 
                                        Collections.EMPTY_LIST, 
                                        getLog());
            for (Iterator<Object> itr = artifacts.iterator(); itr.hasNext();) {
                Artifact a = (Artifact)itr.next();
                
                publishArtifact(a);
            }
        }
        
        if (includes != null || excludes != null) {
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setIncludes(includes);
            scanner.setExcludes(excludes);
            scanner.setBasedir(basedir);
            scanner.scan();
            
            String[] files = scanner.getIncludedFiles();
            if (files != null) {
                for (String file : files) {
                    publishFile(new File(file), project.getArtifact().getVersion());
                }
            }
        }
    }

    private void clearWorkspace() throws MojoFailureException {
        RequestOptions opts = new RequestOptions();
        opts.setAuthorization(authorization);
        
        getLog().info("Clearing workspace " + url);
        ClientResponse res = client.get(url, opts);
        if (res.getStatus() >= 300) {
            throw new MojoFailureException("Could not GET the workspace URL. Got status: " 
                                           + res.getStatus()
                                           + " (" + res.getStatusText() + ")");
        }
        
        assertResponseIsFeed(res);
        
        Document<Feed> doc = res.getDocument();
        Feed feed = doc.getRoot();
        
        for (Entry e : feed.getEntries()) {
            getLog().info("Deleting " + e.getTitle());
            client.delete(e.getContentSrc().toString(), opts);
        }
        
        res.release();
    }

    private void assertResponseIsFeed(ClientResponse res) throws MojoFailureException {
        MimeType contentType = res.getContentType();
        if ("application/atomcoll+xml".equals(contentType.getPrimaryType())) {
            throw new MojoFailureException("URL is not a valid Galaxy workspace. "
                                           + "It must be an Atom Collection. Received Content-Type: "
                                           + contentType);
        }
    }

    private void publishArtifact(Artifact a) throws MojoExecutionException, MojoFailureException {
        String version = a.getVersion();
        File file = a.getFile();
        
        publishFile(file, version);
    }

    private void publishFile(File file, String version) throws MojoFailureException, MojoExecutionException {
        if (version == null) {
            throw new NullPointerException("Version can not be null!");
        }
        
        String name = file.getName();
        
        RequestOptions opts = new RequestOptions();
        opts.setAuthorization(authorization);
        opts.setContentType("application/octet-stream");
        opts.setSlug(name);
        opts.setHeader("X-Artifact-Version", version);
        
        try {
            String artifactUrl = url;
            if (!url.endsWith("/")) {
                artifactUrl += "/";
            }
            artifactUrl += UrlEncoding.encode(name, Profile.PATH.filter());
            
            // Check to see if this artifact exists already.
            ClientResponse  res = client.head(artifactUrl, opts);
            int artifactExists = res.getStatus();
            res.release();
            
            // Check to see if this artifact version exists
            int artifactVersionExists = 404;
            if (artifactExists != 404 && artifactExists < 300) {
                res = client.head(artifactUrl + "?version=" + version, opts);
                artifactVersionExists = res.getStatus();
                res.release();
            }
            
            if (artifactExists == 404 && artifactVersionExists == 404) {
                // create a new artifact
                res = client.post(url, new FileInputStream(file), opts);
                res.release();
                getLog().debug("Uploaded artifact " + name + " (version " + version + ")");
            } else if (artifactVersionExists < 300) {
                getLog().debug("Skipping artifact " + name + " as the current version already exists in the destination workspace.");
            } else if (artifactVersionExists >= 300 && artifactVersionExists != 404) {
                throw new MojoFailureException("Could not determine if resource already exists in: " + name
                    + ". Got status " + res.getStatus() + " for URL " + artifactUrl + ".");
            } else {
                // update the artifact
                res = client.put(artifactUrl, new FileInputStream(file), opts);
                res.release();
            }
            
        } catch (IOException e) {
            throw new MojoExecutionException("Could not upload artifact to Galaxy: " 
                                             + name, e);
        }
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public void setPublishProjectDependencies(boolean publishProject) {
        this.publishProjectDependencies = publishProject;
    }

    public void setClearWorkspace(boolean clearWorkspace) {
        this.clearWorkspace = clearWorkspace;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    public void setBasedir(File basedir) {
        this.basedir = basedir;
    }

    public void setDependencyIncludes(String[] dependencyIncludes) {
        this.dependencyIncludes = dependencyIncludes;
    }

    public void setDependencyExcludes(String[] dependencyExcludes) {
        this.dependencyExcludes = dependencyExcludes;
    }
    
}
