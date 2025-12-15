package com.wellsoft.pt.repository.entity.jcr;

import java.io.Serializable;

public class Lock implements Serializable {

    private static final long serialVersionUID = 1712741213945512957L;
    private String owner;
    private String nodePath;
    private String token;

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("owner=");
        sb.append(owner);
        sb.append(", nodePath=");
        sb.append(nodePath);
        sb.append(", token=");
        sb.append(token);
        sb.append("}");
        return sb.toString();
    }
}