package org.mule.galaxy.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.activation.MimeType;
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
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.ContentService;
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
    private ContentService contentService;
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
        IOException {
        String artifactId = null;
        String wkspcId = null;
        String name = null;
        String versionLabel = null;
        FileItem uploadItem = null;
        
        resp.setContentType("text/plain");

        PrintWriter writer = resp.getWriter();
        
        try {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            
            try {
                List items = upload.parseRequest(req);
                Iterator it = items.iterator();
                while (it.hasNext()) {
                    FileItem item = (FileItem)it.next();
                    
                    String f = item.getFieldName();

                    if ("artifactFile".equals(f)) {
                        uploadItem = item;
                    } else if ("workspaceId".equals(f)) {
                        wkspcId = item.getString();
                    } else if ("name".equals(f)) {
                        name = item.getString();
                    } else if ("versionLabel".equals(f)) {
                        versionLabel = item.getString();
                    } else if ("artifactId".equals(f)) {
                        artifactId = item.getString();
                    }
                }
            } catch (FileUploadException e) {
                throw new ServletException(e);
            }
            
            if (uploadItem == null) {
                writer.write("No file was specified.");
                return;
            }

            if (versionLabel == null) {
                writer.write("No version label was specified.");
                return;
            }

            UserDetailsWrapper wrapper = (UserDetailsWrapper)SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
            User user = wrapper.getUser();
            
            ArtifactResult result = null;
            if (artifactId == null) {            
                if (wkspcId == null) {
                    writer.write("No workspace was specified.");
                    return;
                }
                
                Workspace wkspc = registry.getWorkspace(wkspcId);
                
                // pull out the original file name
                if (name == null || "".equals(name)) {
                    name = uploadItem.getName();
                    
                    int idx = name.lastIndexOf('/');
                    if (idx == -1) {
                        idx = name.lastIndexOf('\\');
                    }
                    name = name.substring(idx+1);
                }
                
                ContentHandler ch = contentService.getContentHandler(getExtension(uploadItem.getName()));
                
                Set<MimeType> types = ch.getSupportedContentTypes();
                String ct = uploadItem.getContentType();
                if (types.size() > 0) {
                    ct = types.iterator().next().toString();
                }
                
                result = registry.createArtifact(wkspc, ct, name, versionLabel, uploadItem.getInputStream(), user);
            } else {
                Artifact a = registry.getArtifact(artifactId);
                
                result = registry.newVersion(a, uploadItem.getInputStream(), versionLabel, user);
            }
            
            writer.write(new String("OK ") + result.getArtifact().getId());
        } catch (NotFoundException e) {
            writer.write("Workspace could not be found.");
        } catch (RegistryException e) {
            writer.write("No version label was specified.");
        } catch (ArtifactPolicyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MimeTypeParseException e) {
            writer.write("Invalid mime type.");
        }
    }

    private String getExtension(String name) {
        int idx = name.lastIndexOf('.');
        if (idx > 0) {
            return name.substring(idx+1);
        }
        
        return "";
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        registry = (Registry)context.getBean("registry");
        contentService = (ContentService)context.getBean("contentService");
    }

}
