package org.mule.galaxy.maven.publish;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;

import java.io.File;
import java.util.HashSet;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.easymock.classextension.EasyMock;
import org.mule.galaxy.test.AbstractAtomTest;

public class PublishMojoTest extends AbstractAtomTest {
    private static final String WORKSPACE_URL = "http://localhost:9002/api/registry/Default%20Workspace";
    private static final String SERVER_ID = "testServer";
    private static String basedirPath;
    private HashSet<Artifact> artifacts;
    private MavenProject project;
    private Server server;
    private Settings settings;
    private Artifact projectArtifact;

    public void setUp() throws Exception {
        super.setUp();
        artifacts = new HashSet<Artifact>();
        
        // Set up a mock maven project
        project = createMock(MavenProject.class);
        expect(project.getArtifacts()).andStubReturn(artifacts);
        
        projectArtifact = org.easymock.EasyMock.createMock(Artifact.class);
        expect(project.getArtifact()).andStubReturn(projectArtifact);
        org.easymock.EasyMock.expect(projectArtifact.getVersion()).andStubReturn("1.0");
        
        server = createMock(Server.class);
        expect(server.getUsername()).andStubReturn("admin");
        expect(server.getPassword()).andStubReturn("admin");
        
        settings = createMock(Settings.class);
        expect(settings.getServer(SERVER_ID)).andStubReturn(server);
        
        EasyMock.replay(project, server, settings);
        org.easymock.EasyMock.replay(projectArtifact);
        
        assertNotNull(project.getArtifacts());
        
    }
    public void testPublishingMainArtifact() throws Exception {
        Artifact mavenArtifact = org.easymock.EasyMock.createMock(Artifact.class);
        org.easymock.EasyMock.expect(mavenArtifact.getVersion()).andStubReturn("1.0");
        
        // Just mock up soem arbitrary file
        File artifactFile = new File("pom.xml");
        assertTrue(artifactFile.exists());
        org.easymock.EasyMock.expect(mavenArtifact.getFile()).andStubReturn(artifactFile);
        
        artifacts.add(mavenArtifact);
        org.easymock.EasyMock.replay(mavenArtifact);
        
        assertNotNull(project.getArtifacts());
        
        PublishMojo mojo = new PublishMojo();
        mojo.setProject(project);
        mojo.setServerId(SERVER_ID);
        mojo.setSettings(settings);
        mojo.setUrl(WORKSPACE_URL);
        mojo.setClearWorkspace(true);
        
        mojo.execute();
        
        AbderaClient client = new AbderaClient();
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        
        ClientResponse res = client.get(WORKSPACE_URL);
        assertEquals(200, res.getStatus());
        
        Document<Feed> feedDoc = res.getDocument();
        Feed feed = feedDoc.getRoot();
        
        assertEquals(1, feed.getEntries().size());
        
        res.release();
        
        // Try updating
        mojo.setClearWorkspace(false);
        mojo.execute();
        
        // Check that we still have just one entry;
        res = client.get(WORKSPACE_URL);
        assertEquals(200, res.getStatus());
        
        feedDoc = res.getDocument();
        feed = feedDoc.getRoot();
        
        assertEquals(1, feed.getEntries().size());
        
        // Upload a second version
        org.easymock.EasyMock.reset(mavenArtifact);
        org.easymock.EasyMock.expect(mavenArtifact.getVersion()).andStubReturn("2.0");
        org.easymock.EasyMock.expect(mavenArtifact.getFile()).andStubReturn(artifactFile);
        org.easymock.EasyMock.replay(mavenArtifact);
        
        mojo.execute();
        
        // Ensure the second version is there
        res = client.get(WORKSPACE_URL + "/pom.xml;history");
        assertEquals(200, res.getStatus());
        
        feedDoc = res.getDocument();
        feed = feedDoc.getRoot();
        assertEquals(2, feed.getEntries().size());
    }
    
    public void testPublishingResources() throws Exception {
        PublishMojo mojo = new PublishMojo();
        mojo.setProject(project);
        mojo.setServerId(SERVER_ID);
        mojo.setSettings(settings);
        mojo.setUrl(WORKSPACE_URL);
        mojo.setClearWorkspace(true);
        mojo.setIncludes(new String[] { "src/test/resources/test**" });
        mojo.setBasedir(getBasedir());
        mojo.execute();
        
        AbderaClient client = new AbderaClient();
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        
        ClientResponse res = client.get(WORKSPACE_URL);
        assertEquals(200, res.getStatus());
        
        Document<Feed> feedDoc = res.getDocument();
        Feed feed = feedDoc.getRoot();
        
        assertEquals(2, feed.getEntries().size());
        
        res.release();
        
        // Try updating
        mojo.setExcludes(new String[] { "src/test/resources/test2" });
        mojo.execute();
        
        // Check that we still have just one entry;
        res = client.get(WORKSPACE_URL);
        assertEquals(200, res.getStatus());
        
        feedDoc = res.getDocument();
        feed = feedDoc.getRoot();
        
        assertEquals(1, feed.getEntries().size());
    }

    public static File getBasedir()
    {
        if ( basedirPath != null )
        {
            return new File(basedirPath);
        }

        basedirPath = System.getProperty( "basedir" );

        if ( basedirPath == null )
        {
            basedirPath = new File( "" ).getAbsolutePath();
        }

        return new File(basedirPath);
    }
}
