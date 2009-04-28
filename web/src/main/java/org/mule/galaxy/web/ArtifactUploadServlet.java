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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.MimeTypeParseException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.BooleanUtils;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.artifact.ArtifactType;
import org.mule.galaxy.artifact.ArtifactTypeDao;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ArtifactUploadServlet implements Controller {

    private Registry registry;
    private ArtifactTypeDao artifactTypeDao;
    
    public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        String artifactId = null;
        String wkspcPath = null;
        String name = null;
        String versionLabel = null;
        String contentType = null;
        FileItem uploadItem = null;
        boolean disablePrevious = false;

        resp.setContentType("text/plain");

        PrintWriter writer = resp.getWriter();

        try {
            FileItemFactory factory = new DiskFileItemFactory();
            
            ServletFileUpload upload = new ServletFileUpload(factory);
            
            try {
                List items = upload.parseRequest(req);
                Iterator it = items.iterator();
                while (it.hasNext()) {
                    FileItem item = (FileItem) it.next();

                    String f = item.getFieldName();

                    if ("artifactFile".equals(f)) {
                        uploadItem = item;
                        contentType = item.getContentType();
                        
                        if (name == null) {
                            name = item.getName();
                        }
                    } else if ("workspacePath".equals(f)) {
                        wkspcPath = item.getString();
                    } else if ("name".equals(f)) {
                        name = item.getString();
                    } else if ("versionLabel".equals(f)) {
                        versionLabel = item.getString();
                    } else if ("artifactId".equals(f)) {
                        artifactId = item.getString();
                    } else if ("disablePrevious".equals(f)) {
                        disablePrevious = BooleanUtils.toBoolean(item.getString());
                    }
                }
            } catch (FileUploadException e) {
                throw new ServletException(e);
            }

            if (uploadItem == null) {
                writer.write("No file was specified.");
                return null;
            }

            if (versionLabel == null) {
                writer.write("No version label was specified.");
                return null;
            }

            NewItemResult result = null;
            if (artifactId == null) {
                if (wkspcPath == null) {
                    writer.write("No workspace was specified.");
                    return null;
                }

                Item wkspc = (Item) registry.getItemByPath(wkspcPath);
                if (wkspc == null) {
                    writer.write("The workspace that was specified is invalid.");
                    return null;
                }
                
                // pull out the original file name
                if (name == null || "".equals(name)) {
                    name = uploadItem.getName();

                    int idx = name.lastIndexOf('/');
                    if (idx == -1) {
                        idx = name.lastIndexOf('\\');
                    }
                    name = name.substring(idx + 1);
                }

                // browsers send along weird content types sometimes...
                if (contentType == null || isUnrecognized(contentType)) {
                    contentType = "application/octet-stream";
                }

//                result = wkspc.createArtifact(contentType, name, versionLabel, uploadItem.getInputStream());
            } else {
//                ArtifactImpl a = (ArtifactImpl) registry.getItemById(artifactId);
//
//                result = a.newVersion(uploadItem.getInputStream(), versionLabel);
//
//                if (disablePrevious) {
//                    ArtifactVersion previous = (ArtifactVersion) result.getEntryVersion().getPrevious();
//                    
//                    if (previous != null) {
//                        previous.setEnabled(false);
//                    }
//                }
            }

            writer.write("OK " + result.getItem().getId());
        } catch (NotFoundException e) {
            writer.write("Workspace could not be found.");
        } catch (RegistryException e) {
            writer.write("No version label was specified.");
//        } catch (PolicyException e) {
//            writer.write("PolicyException\n");
//
//            for (Map.Entry<Item, List<ApprovalMessage>> entry : e.getPolicyFailures().entrySet()) {
//                List<ApprovalMessage> approvals = entry.getValue();
//
//                Collections.sort(approvals, new Comparator<ApprovalMessage>() {
//
//                    public int compare(ApprovalMessage o1, ApprovalMessage o2) {
//                        return o1.getMessage().compareTo(o2.getMessage());
//                    }
//
//                });
//                
//                for (ApprovalMessage a : approvals) {
//                    if (a.isWarning()) {
//                        writer.write("WARNING: ");
//                    } else {
//                        writer.write("FAILURE: ");
//                    }
//                    writer.write(a.getMessage());
//                    writer.write("\n");
//                }
//            }
//
//        } catch (MimeTypeParseException e) {
//            writer.write("Invalid mime type.");
//        } catch (DuplicateItemException e) {
////            resp.setStatus(409);
//            if (artifactId == null) {
//                writer.write("An item with that name already exists.");
//            } else {
//                writer.write("A version with that label already exists.");
//            }
        } catch (AccessException e) {
//            resp.setStatus(401);
            writer.write("AccessException.");
        }
        writer.close();
        return null;
    }

    private boolean isUnrecognized(String contentType) {
        ArtifactType artifactType = artifactTypeDao.getArtifactType(contentType, null);
        
        ArtifactType defaultType = artifactTypeDao.getArtifactType("application/octet-stream", null);
        
        return artifactType.equals(defaultType);
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setArtifactTypeDao(ArtifactTypeDao artifactTypeDao) {
        this.artifactTypeDao = artifactTypeDao;
    }

}
