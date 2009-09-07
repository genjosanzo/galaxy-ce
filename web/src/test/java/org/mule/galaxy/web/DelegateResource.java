package org.mule.galaxy.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.mortbay.resource.Resource;

public class DelegateResource extends Resource {
    private Resource resource;
    private List<Resource> otherLocations;

    public DelegateResource(Resource resource, List<Resource> other) {
        super();
        this.resource = resource;
        otherLocations = other;
    }

    public Resource addPath(String path) throws IOException, MalformedURLException {
        System.out.println("Finding " + path);
        for (Resource r : otherLocations) {
            Resource child = r.addPath(path);
            
            if (child != null && child.exists()) {
                System.out.println("Found " + path + " in overlay " + r.getName());
                return child;
            }
        }
        return resource.addPath(path);
    }

    public boolean delete() throws SecurityException {
        return resource.delete();
    }

    public String encode(String uri) {
        return resource.encode(uri);
    }

    public boolean equals(Object obj) {
        return resource.equals(obj);
    }

    public boolean exists() {
        return resource.exists();
    }

    public URL getAlias() {
        return resource.getAlias();
    }

    public Object getAssociate() {
        return resource.getAssociate();
    }

    public File getFile() throws IOException {
        return resource.getFile();
    }

    public InputStream getInputStream() throws IOException {
        return resource.getInputStream();
    }

    public String getListHTML(String base, boolean parent) throws IOException {
        return resource.getListHTML(base, parent);
    }

    public String getName() {
        return resource.getName();
    }

    public OutputStream getOutputStream() throws IOException, SecurityException {
        return resource.getOutputStream();
    }

    public URL getURL() {
        return resource.getURL();
    }

    public int hashCode() {
        return resource.hashCode();
    }

    public boolean isDirectory() {
        return resource.isDirectory();
    }

    public long lastModified() {
        return resource.lastModified();
    }

    public long length() {
        return resource.length();
    }

    public String[] list() {
        return resource.list();
    }

    public void release() {
        resource.release();
    }

    public boolean renameTo(Resource dest) throws SecurityException {
        return resource.renameTo(dest);
    }

    public void setAssociate(Object o) {
        resource.setAssociate(o);
    }

    public String toString() {
        return resource.toString();
    }

    public void writeTo(OutputStream out, long start, long count) throws IOException {
        resource.writeTo(out, start, count);
    }
    
}
