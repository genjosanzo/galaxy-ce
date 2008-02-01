package org.mule.galaxy.view;

public class ViewLink {
    public enum ViewLevel {
        WORKSPACE,
        ARTIFACT
    }
    
    private String viewName;
    private String criteria;
    
    public String getViewName() {
        return viewName;
    }
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    public String getCriteria() {
        return criteria;
    }
    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }
}
