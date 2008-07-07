package org.mule.galaxy.event;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Workspace;
import static org.mule.galaxy.event.DefaultEvents.PROPERTY_UPDATED;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;
import org.mule.galaxy.security.User;

import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

public class EventsTest extends TestCase {

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

        public List<ArtifactVersion> getVersions() {
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
    }
}
