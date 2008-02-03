package org.mule.galaxy.maven.policy;

import java.util.List;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

/**
 * Ensures that artifacts meet the necessary policies.
 * @goal execute
 */
public class PolicyEnforcerMojo extends AbstractMojo {

    private Settings settings;
    
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
    private Workspace[] workspaces;

    public void execute() throws MojoExecutionException, MojoFailureException {
        
        Server server = settings.getServer(serverId);
        
        AbderaClient client = new AbderaClient();
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        String auth = server.getUsername() + ":" + server.getPassword();
        defaultOpts.setAuthorization("Basic " + Base64.encode(auth.getBytes()));
        
        for (Workspace w : workspaces) {
            
        }
    }
    
}
