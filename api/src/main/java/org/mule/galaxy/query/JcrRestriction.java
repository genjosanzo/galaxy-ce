package org.mule.galaxy.query;

/**
 * Allows you to make a raw JCR query within the search predicate.
 */
public class JcrRestriction extends Restriction {
    private String predicateRestriction;

    public JcrRestriction(String predicateRestriction) {
        super();
        this.predicateRestriction = predicateRestriction;
    }

    public String getPredicateRestriction() {
        return predicateRestriction;
    }

    @Override
    public void toString(StringBuilder sb) {
        sb.append("raw(\"").append(predicateRestriction).append("\")");
    }   
}