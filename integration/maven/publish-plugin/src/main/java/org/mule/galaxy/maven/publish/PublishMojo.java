package org.mule.galaxy.maven.publish;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

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

    private AbderaClient client;

    private String authorization;

    public void execute() throws MojoExecutionException, MojoFailureException {
        
        Server server = settings.getServer(serverId);
        
        client = new AbderaClient();
        String auth = server.getUsername() + ":" + server.getPassword();
        authorization = "Basic " + Base64.encode(auth.getBytes());
    
        if (publishProject) {
            Set artifacts = project.getArtifacts();
            
            for (Iterator itr = artifacts.iterator(); itr.hasNext();) {
                Artifact a = (Artifact)itr.next();
                
                publishArtifact(a);
            }
            
        }
        
        for (Dependency d : artifacts) {
            
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
            ClientResponse res = client.head(artifactUrl, opts);
            if (res.getStatus() == 404) {
                client.post(url, new FileInputStream(file), opts);
            } else if (res.getStatus() >= 300) {
                throw new MojoFailureException("Could not determine if resource was already uploaded: " + name
                    + ". Got status " + res.getStatus() + " for URL " + artifactUrl + ".");
            } else {
                client.put(name , new FileInputStream(file), opts);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Could not upload artifact to Galaxy: " 
                                             + name, e);
        }
    }
    
}
