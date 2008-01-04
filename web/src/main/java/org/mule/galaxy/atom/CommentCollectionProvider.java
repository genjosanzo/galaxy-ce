package org.mule.galaxy.atom;

import java.util.Date;
import java.util.List;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.impl.AbstractCollectionProvider;
import org.apache.abdera.protocol.server.impl.ResponseContextException;
import org.mule.galaxy.Comment;
import org.mule.galaxy.Registry;

public class CommentCollectionProvider extends AbstractCollectionProvider<Comment> {

    private Registry registry;

    public CommentCollectionProvider(Registry registry) {
        this.registry = registry;
    }
    
    @Override
    public Comment createEntry(String arg0, IRI arg1, String arg2, Date arg3, List<Person> arg4,
                               Content arg5, RequestContext request) throws ResponseContextException {
        throw new ResponseContextException(501);
    }

    @Override
    public void deleteEntry(String arg0, RequestContext request) throws ResponseContextException {
        throw new ResponseContextException(501);
    }

    @Override
    public String getAuthor() throws ResponseContextException {
        return "Mule Galaxy";
    }

    @Override
    public Object getContent(Comment c, RequestContext request) throws ResponseContextException {
        return c.getText();
    }

    @Override
    public Iterable<Comment> getEntries(RequestContext request) throws ResponseContextException {
        return null;
    }

    @Override
    public Comment getEntry(String name, RequestContext request) throws ResponseContextException {
        
        return null;
    }

    @Override
    public String getId() {
        return "tag:galaxy.mulesource.com,2008:registry:feed";
    }

    @Override
    public String getId(Comment c) throws ResponseContextException {
        return "urn:galaxy:comment" + c.getId();
    }

    @Override
    public String getName(Comment c) throws ResponseContextException {
        return c.getId();
    }

    @Override
    public String getTitle(Comment c) throws ResponseContextException {
        return "Comment on " + c.getArtifact().getName() + " by " + c.getUser().getName();
    }

    @Override
    public String getTitle(RequestContext arg0) {
        return "Comments";
    }

    @Override
    public Date getUpdated(Comment c) throws ResponseContextException {
        return c.getDate().getTime();
    }

    @Override
    public void updateEntry(Comment c, String arg1, Date arg2, List<Person> arg3, String arg4,
                            Content arg5, RequestContext arg6) throws ResponseContextException {
        throw new ResponseContextException(501);
    }

}
