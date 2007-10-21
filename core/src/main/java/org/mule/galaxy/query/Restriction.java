package org.mule.galaxy.query;

public class Restriction {
    public enum Comparison {
        EQUALS,
        NOT,
        GT,
        LT,
        LIKE
    }

    private Object value;
    private String property;
    private Comparison comparison;
    
    protected Restriction(Comparison c, String prop, Object value) {
        this.comparison = c;
        this.property = prop;
        this.value = value;
    }
    
    protected Restriction(Comparison not, Restriction restriction) {
        this.comparison = not;
        this.value = restriction;
    }

    public Object getValue() {
        return value;
    }

    public String getProperty() {
        return property;
    }

    public Comparison getComparison() {
        return comparison;
    }

    public static Restriction eq(String property, Object value) {
        return new Restriction(Comparison.EQUALS, property, value);
    }

    public static Restriction not(Restriction restriction) {
        return new Restriction(Comparison.NOT, restriction);
    }
}

