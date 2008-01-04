package org.mule.galaxy.web;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeTypeParseException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.mule.galaxy.security.User;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ArtifactUploadServlet extends HttpServlet {

    private WebApplicationContext context;
    private Registry registry;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
        IOException {
        String artifactId = req.getParameter("artifactId");
        
        try {
            Workspace wkspc = registry.getWorkspace(req.getParameter("workspaceId"));
            String name = req.getParameter("name");
            String versionLabel = req.getParameter("versionLabel");
    
            UserDetailsWrapper wrapper = (UserDetailsWrapper)SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
            User user = wrapper.getUser();
    
            FileItem uploadItem = getFileItem(req);
            if (uploadItem == null) {
                resp.getWriter().write("NO-SCRIPT-DATA");
                return;
            }
            
            if (artifactId == null) {
                registry.createArtifact(wkspc, req.getContentType(), name, 
                                        versionLabel, req.getInputStream(), user);
            }
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RegistryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ArtifactPolicyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MimeTypeParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        resp.setContentType("text/plain");

        resp.getWriter().write(new String("OK"));
    }

    private FileItem getFileItem(HttpServletRequest request) {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            List items = upload.parseRequest(request);
            Iterator it = items.iterator();
            while (it.hasNext()) {
                FileItem item = (FileItem)it.next();
                if (!item.isFormField() && "artifactFile".equals(item.getFieldName())) {
                    return item;
                }
            }
        } catch (FileUploadException e) {
            return null;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        registry = (Registry)context.getBean("registry");
    }

}
