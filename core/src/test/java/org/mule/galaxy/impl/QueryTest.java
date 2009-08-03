package org.mule.galaxy.impl;

import static org.mule.galaxy.query.OpRestriction.eq;
import static org.mule.galaxy.query.OpRestriction.like;
import static org.mule.galaxy.query.OpRestriction.not;
import static org.mule.galaxy.query.OpRestriction.or;

import java.util.Iterator;

import junit.framework.TestCase;

import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.query.OpRestriction.Operator;

public class QueryTest extends TestCase {
    
    public void testToString() throws Exception {
        Query q = new Query()
                .add(eq("phase",  "Default:Created"));
        
        assertEquals("select where phase = 'Default:Created'",
                     q.toString());
        
        q = new Query()
            .add(not(eq("name",  "foo")));
    
        assertEquals("select where name != 'foo'",
                 q.toString());

        q = new Query()
            .add(like("name",  "foo"));
    
        assertEquals("select where name like 'foo'",
                 q.toString());

        q = new Query()
            .add(or(like("name", "foo"), eq("name", "bar")));
    
        assertEquals("select where (name like 'foo' or name = 'bar')",
                 q.toString());
        
        q = new Query()
            .add(like("name", "foo"))
            .add(eq("phase", "bar"));
    
        assertEquals("select where name like 'foo' and phase = 'bar'",
                 q.toString());
        
        q = new Query()
            .fromPath("/foo", false)
            .add(eq("phase", "bar"));

        assertEquals("select from '/foo' where phase = 'bar'",
             q.toString());
        
        q = new Query()
            .fromPath("/foo", true)
            .add(eq("phase", "bar"));

        assertEquals("select from '/foo' recursive where phase = 'bar'",
             q.toString());
        
        q = new Query()
            .fromId("123", true)
            .add(eq("phase", "bar"));

        assertEquals("select from '@123' recursive where phase = 'bar'",
             q.toString());
    }
    
    public void testFromString() throws Exception {
        Query q = Query.fromString("select where name != 'foo'");

        assertEquals(1, q.getRestrictions().size());
        
        OpRestriction opr = (OpRestriction) q.getRestrictions().iterator().next();
        
        assertEquals(Operator.NOT, opr.getOperator());
        
        q = Query.fromString("select from '/foo' recursive");
        
        assertTrue(q.isFromRecursive());
        assertEquals("/foo", q.getFromPath());
        
        q = Query.fromString("select from '@123' recursive");
        
        assertTrue(q.isFromRecursive());
        assertEquals("123", q.getFromId());
        

        q = Query.fromString("select where (name = 'foo' or name = 'bar')");
        opr = (OpRestriction) q.getRestrictions().iterator().next();
        
        assertEquals(Operator.OR, opr.getOperator());

        q = Query.fromString("select where ((name = 'foo' or name = 'bar'))");
        opr = (OpRestriction) q.getRestrictions().iterator().next();
        
        assertEquals(Operator.OR, opr.getOperator());
        
        q = Query.fromString("select where ((name = 'foo' or name = 'bar') and (name = 'foo' or name = 'bar'))");
        Iterator<Restriction> iterator = q.getRestrictions().iterator();
        assertTrue(iterator.hasNext());
        opr = (OpRestriction) iterator.next();
        assertEquals(Operator.OR, opr.getOperator());
        
        assertTrue(iterator.hasNext());
        opr = (OpRestriction) iterator.next();
        assertEquals(Operator.OR, opr.getOperator());
        
        OpRestriction opr1 = (OpRestriction)opr.getLeft();
        assertEquals(Operator.EQUALS, opr1.getOperator());
        OpRestriction opr2 = (OpRestriction)opr.getRight();
        assertEquals(Operator.EQUALS, opr2.getOperator());
        
        q = Query.fromString("select where ( name = 'foo' or name = 'bar' )");
        opr = (OpRestriction) q.getRestrictions().iterator().next();
        
        assertEquals(Operator.OR, opr.getOperator());
    }
    
}
