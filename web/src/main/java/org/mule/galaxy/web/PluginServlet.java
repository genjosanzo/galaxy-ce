package org.mule.galaxy.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mule.galaxy.util.IOUtils;

/**
 * Serves out files from plugins which are located in galaxy/web on the classpath.
 */
public class PluginServlet extends HttpServlet {
    private static List<File> pluginLocations = new ArrayList<File>();
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
        IOException {
        
        String path = req.getPathInfo();

        InputStream resource = getClass().getResourceAsStream("/galaxy/web" + path);
        
        if (resource == null) {
            for (File plugin : pluginLocations) {
                File file = new File(plugin, path);
                if (file.exists()) {
                    resource = new FileInputStream(file);
                    break;
                }
            }
        }
        
        if (resource == null) {
            resp.setStatus(404);
            return;
        }

        if (path.endsWith(".html")) {
            resp.setContentType("text/html");
        } else if (path.endsWith(".js")) {
            resp.setContentType("text/javascript");
        } if (path.endsWith(".png")) {
            resp.setContentType("image/png");
        }
        
        ServletOutputStream out = resp.getOutputStream();
        try {
            IOUtils.copy(resource, out);
        } finally {
            out.close();
        }
    }

    public static void addPluginLocation(File location) {
        pluginLocations.add(location);
    }
}
