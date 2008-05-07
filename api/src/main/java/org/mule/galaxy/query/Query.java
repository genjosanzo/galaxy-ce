package org.mule.galaxy.query;

import java.util.LinkedList;
import java.util.List;

import org.mule.galaxy.ArtifactVersion;

public class Query {
    List<Restriction> restrictions = new LinkedList<Restriction>();
    boolean searchLatestVersionOnly = true;
    Class<?> selectType;
    private String groupBy;
    private String workspaceId;
    private boolean workspaceChildren;
    private String workspacePath;
    private int start = -1;
    private int maxResults = Integer.MAX_VALUE;
    
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public Query(Class selectType, OpRestriction restriction) {
        this.selectType = selectType;
        restrictions.add(restriction);
    }
    
    public Query(Class selectType) {
        this.selectType = selectType;
    }

    public Query add(Restriction restriction) {
        restrictions.add(restriction);
        return this;
    }

    public List<Restriction> getRestrictions() {
        return restrictions;
    }

    public Class<?> getSelectType() {
        return selectType;
    }

    public void setSelectType(Class<?> selectType) {
        this.selectType = selectType;
    }

    public Query orderBy(String field) {
        this.groupBy = field;
        return this;
    }
    
    public Query workspaceId(String workspace, boolean searchWorkspaceChildren) {
        this.workspaceId = workspace;
        this.workspaceChildren = searchWorkspaceChildren;
        return this;
    }
    
    public Query workspaceId(String workspace) {
        this.workspaceId = workspace;
        return this;
    }
    
    public Query workspacePath(String workspace, boolean searchWorkspaceChildren) {
        this.workspacePath = workspace;
        this.workspaceChildren = searchWorkspaceChildren;
        return this;
    }
    
    public Query workspacePath(String workspace) {
        this.workspacePath = workspace;
        return this;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public boolean isSearchWorkspaceChildren() {
        return workspaceChildren;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("select ");
        
        if (selectType.equals(ArtifactVersion.class)) {
            sb.append("artifactVersion ");
        } else {
            sb.append("artifact ");
        }
        
        if (workspaceId != null) {
            
        }
        
        if (workspacePath != null) {
            sb.append("from '")
              .append(workspacePath)
              .append("' ");
        }
        
        sb.append("where ");
        
        boolean first = true;
        
        for (Restriction r : restrictions) {
            sb.append(r.toString());
            
            if (first) first = false;
            else sb.append(" ");
        }

        return sb.toString();
    }
}
