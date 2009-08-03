/*
 * $Id: ContextPathResolver.java 794 2008-04-23 22:23:10Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.web;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.mule.galaxy.impl.artifact.UploadService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Takes artifact uploads and sticks them in the {@link UploadService}.
 */
public class ArtifactUploadServlet implements Controller {
    private UploadService uploadService;
    
    public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        FileItem uploadItem = null;

        resp.setContentType("text/plain");

        PrintWriter writer = resp.getWriter();

        FileItemFactory factory = new DiskFileItemFactory();
        
        ServletFileUpload upload = new ServletFileUpload(factory);
        
        try {
            List items = upload.parseRequest(req);
            Iterator it = items.iterator();
            while (it.hasNext()) {
                FileItem item = (FileItem) it.next();

                String f = item.getFieldName();

                if ("file".equals(f)) {
                    uploadItem = item;
                }
            }
        } catch (FileUploadException e) {
            throw new ServletException(e);
        }

        if (uploadItem == null) {
            writer.write("No file was specified.");
            return null;
        }
        
        String id = uploadService.upload(uploadItem.getInputStream());
        writer.write("OK " + id);
        writer.close();

        uploadItem.delete();
        
        return null;
    }

    public void setUploadService(UploadService uploadService) {
        this.uploadService = uploadService;
    }

}
