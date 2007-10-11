package org.mule.galaxy;

public class ArtifactSearchCriteria {
    private String name;
    private String namespace;
    private int start;
    private int results;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getNamespace() {
        return namespace;
    }
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    public int getResults() {
        return results;
    }
    public void setResults(int results) {
        this.results = results;
    }
    public int getStart() {
        return start;
    }
    public void setStart(int start) {
        this.start = start;
    }
}
