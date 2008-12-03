package org.mule.galaxy.impl;


import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.collab.Comment;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class CommentTest extends AbstractGalaxyTest {
    public void testComments() throws Exception {
        Artifact artifact = importHelloWsdl();
        
        Comment c = new Comment();
        c.setItem(artifact);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        c.setDate(cal);
        c.setUser(getAdmin());
        c.setText("Hello.");
        c.setItem(artifact);
        
        commentManager.addComment(c);
        
        assertNotNull(c.getId());
    
        List<Comment> comments = commentManager.getComments(artifact.getId());
        assertEquals(1, comments.size());
        
        Comment c2 = new Comment();
        c2.setParent(c);
        
        cal = Calendar.getInstance();
        cal.setTime(new Date());
        c2.setDate(cal);
        c2.setUser(getAdmin());
        c2.setText("Hello.");

        commentManager.addComment(c2);
        
        comments = commentManager.getComments(artifact.getId());
        assertEquals(1, comments.size());
        
        Comment c3 = comments.get(0);
        Set<Comment> comments2 = c3.getComments();
        assertNotNull(comments2);
        assertEquals(1, comments2.size());
        
        
    }
}
