package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SearchPredicate implements IsSerializable {
    public static final int MATCHES       = 0;
    public static final int CONTAINS      = 1;
    public static final int BEGINS_WITH   = 2;
    public static final int ENDS_WITH     = 3;
    public static final int IS            = 4;
    
    private String fieldName;
    private int matchType;
    private String queryValue;
    
    public SearchPredicate() {
    }
    
    public SearchPredicate(String fieldName, int matchType, String queryValue) {
        this.fieldName  = fieldName;
        this.matchType  = matchType;
        this.queryValue = queryValue.toLowerCase();
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public void setMatchType(int matchType) {
        this.matchType = matchType;
    }
    
    public void setQueryValue(String queryValue) {
        this.queryValue = queryValue;
    }
    
    public boolean matches(Object object) {
        return true;
    }
}
