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

package org.mule.galaxy.atom;

import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.collab.Comment;
import org.mule.galaxy.collab.CommentManager;

import java.util.Date;
import java.util.List;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;

public class CommentCollectionProvider extends AbstractEntityCollectionAdapter<Comment> {
    
    private CommentManager commentManager;
    private Registry registry;
    
    public CommentCollectionProvider(CommentManager commentManager, Registry registry) {
        this.commentManager = commentManager;
        this.registry = registry;
    }
    @Override
    public String getHref(RequestContext request) {
        String href = (String) request.getAttribute(Scope.REQUEST, EntryResolver.COLLECTION_HREF);
        if (href == null) {
            href = request.getTargetBasePath() + "/comments";
        }
        return href;
    }
    @Override
    public Comment postEntry(String arg0, IRI arg1, String arg2, Date arg3, List<Person> arg4,
                               Content arg5, RequestContext request) throws ResponseContextException {
        throw new ResponseContextException(501);
    }

    @Override
    public void deleteEntry(String arg0, RequestContext request) throws ResponseContextException {
        throw new ResponseContextException(501);
    }

    @Override
    public String getAuthor(RequestContext request) throws ResponseContextException {
        return "Mule Galaxy";
    }

    @Override
    public Object getContent(Comment c, RequestContext request) throws ResponseContextException {
        return c.getText();
    }

    @Override
    public Iterable<Comment> getEntries(RequestContext request) throws ResponseContextException {
        return commentManager.getRecentComments(100);
    }

    @Override
    public Comment getEntry(String name, RequestContext request) throws ResponseContextException {
        try {
            return commentManager.getComment(name);
        } catch (NotFoundException e) {
            throw new ResponseContextException(404);
        }
    }

    @Override
    public String getId(RequestContext request) {
        return "tag:galaxy.mulesource.com,2008:registry:" + registry.getUUID() + ":comments:feed";
    }

    @Override
    public String getId(Comment c) throws ResponseContextException {
        return "urn:galaxy:comment:" + c.getId();
    }

    @Override
    public String getName(Comment c) throws ResponseContextException {
        return c.getId();
    }

    @Override
    public String getTitle(Comment c) throws ResponseContextException {
        return "Comment on " + c.getArtifact().getPath() + " by " + c.getUser().getName();
    }

    public String getTitle(RequestContext request) {
        return "Mule Galaxy Comments";
    }

    @Override
    public Date getUpdated(Comment c) throws ResponseContextException {
        return c.getDate().getTime();
    }

    @Override
    public void putEntry(Comment c, String arg1, Date arg2, List<Person> arg3, String arg4,
                         Content arg5, RequestContext arg6) throws ResponseContextException {
        throw new ResponseContextException(501);
    }

}
