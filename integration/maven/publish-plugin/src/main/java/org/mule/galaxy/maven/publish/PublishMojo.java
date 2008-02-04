package org.mule.galaxy.maven.publish;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.activation.MimeType;

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
     * The Galaxy workspace URL.
     *
     * @parameter
     */
    private String url;
    
    /**
     * The server id to use for username/password information.
     *
     * @parameter
     */
    private String serverId;

    /**
     * The password for Galaxy.
     *
     * @parameter
     */
    private Dependency[] artifacts;

    /**
     * Whether or not to publish the project's artifacts which this
     * plugin is attached to.
     *
     * @parameter
     */
    private boolean publishProject = true;

    /**
     * Whether or not to clear the artifacts from the workspace before
     * uploading new ones.
     *
     * @parameter
     */
    private boolean clearWorkspace = false;

    private AbderaClient client;

    private String authorization;

    public void execute() throws MojoExecutionException, MojoFailureException {
        
        Server server = settings.getServer(serverId);
        
        client = new AbderaClient();
        String auth = server.getUsername() + ":" + server.getPassword();
        authorization = "Basic " + Base64.encode(auth.getBytes());
    
        if (clearWorkspace) {
            clearWorkspace();
        }
        
        if (publishProject) {
            Set artifacts = project.getArtifacts();
            
            for (Iterator itr = artifacts.iterator(); itr.hasNext();) {
                Artifact a = (Artifact)itr.next();
                
                publishArtifact(a);
            }
            
        }
        
        // TODO
        for (Dependency d : artifacts) {
            
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
        File file = a.getFile();
        String name = file.getName();

        RequestOptions opts = new RequestOptions();
        opts.setAuthorization(authorization);
        opts.setContentType("application/octet-stream");
        opts.setSlug(name);
        opts.setHeader("X-Artifact-Version", a.getVersion());
        
        try {
            String artifactUrl = url;
            if (!url.endsWith("/")) {
                artifactUrl += "/";
            }
            artifactUrl += name;
            
            // Check to see if this artifact exists already.
            ClientResponse res = client.head(artifactUrl + "?version=" + a.getVersion(), opts);
            if (res.getStatus() == 404) {
                getLog().debug("Uploading artifact " + name + ".");
                client.post(url, new FileInputStream(file), opts);
            } else if (res.getStatus() >= 300) {
                throw new MojoFailureException("Could not determine if resource was already uploaded: " + name
                    + ". Got status " + res.getStatus() + " for URL " + artifactUrl + ".");
            } else {
                getLog().debug("Skipping artifact " + name + " as the current version already exists in the destination workspace.");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Could not upload artifact to Galaxy: " 
                                             + name, e);
        }
    }
    
}
