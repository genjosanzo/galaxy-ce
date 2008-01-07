package org.mule.galaxy.query;

public class Restriction {
    public enum Operator {
        EQUALS,
        NOT,
        GT,
        LT,
        LIKE
    }

    private Object value;
    private Object left;
    private Operator operator;
    
    protected Restriction(Operator o, Object left, Object right) {
        this.operator = o;
        this.left = left;
        this.value = right;
    }
    
    protected Restriction(Operator not, Restriction restriction) {
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

    public static Restriction eq(String property, Object value) {
        return new Restriction(Operator.EQUALS, property, value);
    }

    public static Restriction not(Restriction restriction) {
        return new Restriction(Operator.NOT, restriction);
    }

    public static Restriction like(String property, Object value)
    {
        // TODO Auto-generated method stub
        return null;
    }
}

