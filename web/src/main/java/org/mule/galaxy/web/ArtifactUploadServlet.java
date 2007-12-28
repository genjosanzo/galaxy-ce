package org.mule.galaxy.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ArtifactUploadServlet extends HttpServlet {

    private WebApplicationContext context;

    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
        IOException {
        String artifactId = req.getParameter("artifactId");
        
        
        super.doPost(req, resp);
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
    }

}
