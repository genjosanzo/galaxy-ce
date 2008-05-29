package org.mule.galaxy.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        
        URL resource = cl.getResource("embedded.war");
        
        if (resource == null) {
            System.err.println("Could not find webapp.war on classpath!");
            System.exit(-1);
        }
        
        System.out.println("Extracting galaxy.war....");
        copy(resource.openStream(), new FileOutputStream(new File("galaxy.war")), 8096);
        System.out.println("Extracted! Launching Jetty...");
        
        if (args == null) {
            args = new String[0];
        }
        
        List<String> argsList = new ArrayList<String>();
        argsList.addAll(Arrays.asList(args));
        argsList.add(0, "galaxy.war");
        
        org.mortbay.jetty.runner.Runner.main(argsList.toArray(new String[argsList.size()]));
        
        System.exit(0);
    }

    public static int copy(final InputStream input, final OutputStream output,
            int bufferSize) throws IOException {
        int avail = input.available();
        if (avail > 262144) {
            avail = 262144;
        }
        if (avail > bufferSize) {
            bufferSize = avail;
        }
        final byte[] buffer = new byte[bufferSize];
        int n = 0;
        n = input.read(buffer);
        int total = 0;
        while (-1 != n) {
            output.write(buffer, 0, n);
            total += n;
            n = input.read(buffer);
        }
        return total;
    }
}
