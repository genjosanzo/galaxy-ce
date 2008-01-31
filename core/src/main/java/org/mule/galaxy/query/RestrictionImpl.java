package org.mule.galaxy.query;

import org.mule.galaxy.api.query.Restriction;

import java.util.Collection;

public class RestrictionImpl implements Restriction
{
    private Object value;
    private Object left;
    private Operator operator;
    
    protected RestrictionImpl(Operator o, Object left, Object right) {
        this.operator = o;
        this.left = left;
        this.value = right;
    }
    
    protected RestrictionImpl(Operator not, Restriction restriction) {
        this.operator = not;
        this.value = restriction;
    }

    public Object getRight() {
        return value;
    }

    public Object getLeft() {
        return left;
    }

    public Operator getOperator() {
        return operator;
    }

    public static RestrictionImpl eq(String property, Object value) {
        return new RestrictionImpl(Operator.EQUALS, property, value);
    }

    public static RestrictionImpl not(Restriction restriction) {
        return new RestrictionImpl(Operator.NOT, restriction);
    }

    public static RestrictionImpl like(String property, Object value)
    {
        return new RestrictionImpl(Operator.LIKE, property, value);
    }

    public static RestrictionImpl in(String property, Collection<?> values) {
        return new RestrictionImpl(Operator.IN, property, values);
    }
}

