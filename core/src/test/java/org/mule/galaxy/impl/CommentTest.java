package org.mule.galaxy.impl;


import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mule.galaxy.Item;
import org.mule.galaxy.collab.Comment;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class CommentTest extends AbstractGalaxyTest {
    public void testComments() throws Exception {
        Item artifact = importHelloWsdl();
        
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
        

        Comment c3 = new Comment();
        c3.setParent(c2);
        
        cal = Calendar.getInstance();
        cal.setTime(new Date());
        c3.setDate(cal);
        c3.setUser(getAdmin());
        c3.setText("Hello.");

        commentManager.addComment(c3);
        
        comments = commentManager.getComments(artifact.getId());
        assertEquals(1, comments.size());
        
        Comment c4 = comments.get(0);
        Set<Comment> comments2 = c4.getComments();
        assertNotNull(comments2);
        assertEquals(1, comments2.size());
    }
}
