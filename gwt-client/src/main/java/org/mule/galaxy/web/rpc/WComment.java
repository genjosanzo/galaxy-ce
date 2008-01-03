package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

import org.mule.galaxy.web.client.RPCException;

public class WComment implements IsSerializable {
    private String id;
    private String user;
    private String date;
    private String text;
    
    /*
     * @gwt typeArgs org.mule.galaxy.web.rpc.WComment
     */
    private List comments;

    public WComment() {
        super();
    }

    public WComment(String id, String user, String date, String text) {
        super();
        this.id = id;
        this.user = user;
        this.date = date;
        this.text = text;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getUser() {
        return user;
    }


    public void setUser(String user) {
        this.user = user;
    }


    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }

    public List getComments() {
        if (comments == null) {
            comments = new ArrayList();
        }
        return comments;
    }
    
}
