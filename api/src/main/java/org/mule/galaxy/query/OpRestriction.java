package org.mule.galaxy.query;

import java.util.Collection;

public class OpRestriction extends Restriction {
    public enum Operator {
        EQUALS,
        NOT,
        IN,
        LIKE
    }

    private Object value;
    private Object left;
    private Operator operator;
    
    protected OpRestriction(Operator o, Object left, Object right) {
        this.operator = o;
        this.left = left;
        this.value = right;
    }
    
    protected OpRestriction(Operator not, OpRestriction restriction) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb);
        return sb.toString();
    }
    
    public void toString(StringBuilder sb) {
        toString(sb, false);
    }
    
    private void toString(StringBuilder sb, boolean not) {
        sb.append(left);
        switch (operator) {
        case EQUALS:
            if (not) {
                sb.append(" != '");
            } else {
                sb.append(" = '");
            }
            sb.append(value);
            sb.append("'");
            break;
        case LIKE:
            if (not) {
                sb.append(" not");
            } 
            sb.append(" like '");
            sb.append(value);
            sb.append("'");
            break;
        case IN:
            sb.append(" in ('");
            
            boolean first = true;
            for (Object val : ((Collection) value)) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(val)
                  .append("'");
                
            }
            sb.append(")");
            
            break;
        case NOT:
            ((OpRestriction) value).toString(sb, true);
            break;
        }
    }

    public static OpRestriction eq(String property, Object value) {
        return new OpRestriction(Operator.EQUALS, property, value);
    }

    public static OpRestriction not(OpRestriction restriction) {
        return new OpRestriction(Operator.NOT, restriction);
    }

    public static OpRestriction like(String property, Object value)
    {
        return new OpRestriction(Operator.LIKE, property, value);
    }

    public static OpRestriction in(String property, Collection<?> values) {
        return new OpRestriction(Operator.IN, property, values);
    }
}

