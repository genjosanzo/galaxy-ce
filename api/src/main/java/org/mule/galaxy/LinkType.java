package org.mule.galaxy;

public class LinkType implements Identifiable {
    public static final String DEPENDS = "depends";
    
    private String id;
    private String relationship;
    private String reciprocal;
    
    public LinkType() {
        super();
    }

    public LinkType(String id, String relationship) {
        super();
        this.id = id;
        this.relationship = relationship;
    }

    public LinkType(String id, String relationship, String reciprical) {
        super();
        this.id = id;
        this.relationship = relationship;
        this.reciprocal = reciprical;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getReciprocal() {
        return reciprocal;
    }

    public void setReciprocal(String reciprical) {
        this.reciprocal = reciprical;
    }

}
