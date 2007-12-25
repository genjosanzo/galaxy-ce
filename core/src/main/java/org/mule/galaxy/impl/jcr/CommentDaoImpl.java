package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springmodules.jcr.JcrCallback;

import org.mule.galaxy.Comment;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;

public class CommentDaoImpl extends AbstractReflectionDao<Comment> {

    public CommentDaoImpl() throws Exception {
        super(Comment.class, "comments", true);
    }

//    @SuppressWarnings("unchecked")
//    public List<Comment> getComments(String artifactId) {
//        return (List<Comment>) execute(new JcrCallback() {
//            public Object doInJcr(Session session) throws IOException, RepositoryException {
//                StringBuilder qstr = new StringBuilder();
//                qstr.append("/*/")
//                    .append(rootNode)
//                    .append("/*[not(@parent) and artifact'")
//                    + "']";
//                return query(stmt, session);
//            }
//        });
//    }
}
