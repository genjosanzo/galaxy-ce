package org.mule.galaxy.impl.jcr;

import org.mule.galaxy.Comment;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;

public class CommentDaoImpl extends AbstractReflectionDao<Comment> {

    public CommentDaoImpl() throws Exception {
        super(Comment.class, "comments", true);
    }

}
