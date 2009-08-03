package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.List;

public class WLinks implements IsSerializable, Serializable {
    private List<LinkInfo> links;
    private List<LinkInfo> reciprocal;
    private String reciprocalName;
    public List<LinkInfo> getLinks() {
        return links;
    }
    public void setLinks(List<LinkInfo> links) {
        this.links = links;
    }
    public List<LinkInfo> getReciprocal() {
        return reciprocal;
    }
    public void setReciprocal(List<LinkInfo> reciprocal) {
        this.reciprocal = reciprocal;
    }
    public String getReciprocalName() {
        return reciprocalName;
    }
    public void setReciprocalName(String reciprocalName) {
        this.reciprocalName = reciprocalName;
    }
}
