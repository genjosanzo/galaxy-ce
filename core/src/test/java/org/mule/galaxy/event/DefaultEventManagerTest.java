package org.mule.galaxy.event;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import static org.mule.galaxy.event.DefaultEvents.PROPERTY_UPDATED;
import static org.mule.galaxy.event.DefaultEvents.WORKSPACE_DELETED;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.BindToEvents;
import org.mule.galaxy.event.annotation.OnEvent;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

public class DefaultEventManagerTest extends TestCase {

    public void testRemoveListener() {
        EventManager em = new DefaultEventManager(Collections.emptyList());
        TestSingleEventListener listener = new TestSingleEventListener();
        TestEvent event = new TestEvent(this);
        em.addListener(listener);

        em.fireEvent(event);

        assertNotNull(listener.getEvent());
        assertSame(event, listener.getEvent());

        // now remove the listeners for the event and re-fire
        em.removeListener(TestEvent.class);
        listener.reset();
        em.fireEvent(event);

        assertNull(listener.getEvent());
    }

    public void testNullInput() throws Exception {
        EventManager em = new DefaultEventManager(Collections.emptyList());
        try {
            em.addListener(null);
            fail("Should've thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testMissingOnEventAnnotation() throws Exception {
        EventManager em = new DefaultEventManager(Collections.emptyList());
        try {
            em.addListener(new ClassAnnotationMissingOnEvent());
            fail("Should've thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testMissingBindEventAnnotation() throws Exception {
        EventManager em = new DefaultEventManager(Collections.emptyList());
        try {
            em.addListener(new Object());
            fail("Should've thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testAnnotationInheritance() throws Exception {
        EventManager em = new DefaultEventManager(Collections.emptyList());
        final InheritedBindingWithOnEvent listener = new InheritedBindingWithOnEvent();
        em.addListener(listener);

        // should not fail by now, as there's an @OnEvent in a subclass, but @BindToEvent in superclass
    }

    public void testOnEventAnnotation() throws Exception {
        EventManager em = new DefaultEventManager(Collections.emptyList());
        final InheritedBindingWithOnEvent listener = new InheritedBindingWithOnEvent();
        em.addListener(listener);

        final PropertyUpdatedEvent event = new PropertyUpdatedEvent(new User(), "test message", new DummyArtifact(), "testProperty", "newValue");
        em.fireEvent(event);

        assertTrue(listener.called);
        assertSame(event, listener.event);
    }

    /**
     * Single event, multiple @OnEvent entrypoints should fail
     */
    public void testMultipleOnEventAnnotationsSingleListener() throws Exception {
        EventManager em = new DefaultEventManager(Collections.emptyList());
        SingleEventMultipleEntryPoints listener = new SingleEventMultipleEntryPoints();
        try {
            em.addListener(listener);
            fail("Should've failed");
        } catch (IllegalArgumentException e) {
            // expected
            assertTrue(e.getMessage().startsWith("Multiple @OnEvent"));
        }
    }

    public void testMultiEventListener() {
        EventManager em = new DefaultEventManager(Collections.emptyList());
        MultiEventListener listener = new MultiEventListener();
        em.addListener(listener);
        
        final PropertyUpdatedEvent event1 = new PropertyUpdatedEvent(new User(), "test message 1", new DummyArtifact(), "testProperty", "newValue");
        em.fireEvent(event1);

        final WorkspaceDeletedEvent event2 = new WorkspaceDeletedEvent("test workspace");
        em.fireEvent(event2);

        assertSame(event1, listener.puEvent);
        assertSame(event2, listener.wdEvent);
    }

    public void testNonMatchingOnEventParam() {
        EventManager em = new DefaultEventManager(Collections.emptyList());
        NonMatchingOnEventParam listener = new NonMatchingOnEventParam();
        try {
            em.addListener(listener);
            fail("Should've failed");
        } catch (IllegalArgumentException e) {
            assertTrue("Wrong exception?", e.getMessage().contains("doesn't match"));
        }
    }

    @BindToEvent(PROPERTY_UPDATED)
    private static class ClassAnnotationMissingOnEvent {

    }

    private static class InheritedBindingWithOnEvent extends ClassAnnotationMissingOnEvent {

        public volatile boolean called;
        public PropertyUpdatedEvent event;

        @OnEvent
        public void callback(PropertyUpdatedEvent e) {
            called = true;
            event = e;
        }

    }

    private static class SingleEventMultipleEntryPoints extends ClassAnnotationMissingOnEvent {

        @OnEvent
        public void callback1(PropertyUpdatedEvent e) {

        }

        @OnEvent
        public void callback2(PropertyUpdatedEvent e) {

        }

    }

    @BindToEvents({WORKSPACE_DELETED, PROPERTY_UPDATED})
    private static class MultiEventListener {

        public PropertyUpdatedEvent puEvent;
        public WorkspaceDeletedEvent wdEvent;

        @OnEvent
        public void callbackProperty(PropertyUpdatedEvent e) {
            puEvent = e;
        }

        @OnEvent
        public void callbackWorkspace(WorkspaceDeletedEvent e) {
            wdEvent = e;
        }
    }

    @BindToEvent("Test")
    private static class NonMatchingOnEventParam {

        @OnEvent
        public void onEvent(PropertyUpdatedEvent event) {}

    }

    private static final class DummyArtifact implements Artifact {

        public String getPath() {
            return null;
        }

        public String getId() {
            return null;
        }

        public Workspace getParent() {
            return null;
        }

        public void setProperty(final String name, final Object value) throws PropertyException {
        }

        public Object getProperty(final String name) {
            return null;
        }

        public boolean hasProperty(final String name) {
            return false;
        }

        public Iterator<PropertyInfo> getProperties() {
            return null;
        }

        public PropertyInfo getPropertyInfo(final String name) {
            return null;
        }

        public void setLocked(final String name, final boolean locked) {
        }

        public void setVisible(final String property, final boolean visible) {
        }

        public Calendar getCreated() {
            return null;
        }

        public Calendar getUpdated() {
            return null;
        }

        public String getName() {
            return null;
        }

        public void setName(final String name) {
        }

        public String getDescription() {
            return null;
        }

        public void setDescription(final String description) {
        }

        public MimeType getContentType() {
            return null;
        }

        public QName getDocumentType() {
            return null;
        }

        public void setDocumentType(final QName documentType) {
        }

        public List<EntryVersion> getVersions() {
            return null;
        }

        public ArtifactVersion getVersion(final String versionName) {
            return null;
        }

        public ArtifactVersion getDefaultOrLastVersion() {
            return null;
        }

        public ContentHandler getContentHandler() {
            return null;
        }

	public EntryResult newVersion(InputStream inputStream,
		String versionLabel, User user) throws RegistryException,
		PolicyException, IOException, DuplicateItemException,
		AccessException {
	    return null;
	}

	public EntryResult newVersion(Object data, String versionLabel,
		User user) throws RegistryException, PolicyException,
		IOException, DuplicateItemException, AccessException {
	    return null;
	}

	public void delete() throws RegistryException, AccessException {
	}

    }
}
