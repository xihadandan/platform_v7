package com.wellsoft.pt.repository.entity.jcr;

import javax.jcr.Session;
import java.io.Serializable;
import java.util.Calendar;

public class JcrSessionInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Calendar creation;
    private Calendar lastAccess;
    private Session session;

    public Calendar getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Calendar lastAccess) {
        this.lastAccess = lastAccess;
    }

    public Calendar getCreation() {
        return creation;
    }

    public void setCreation(Calendar creation) {
        this.creation = creation;
    }

    public javax.jcr.Session getSession() {
        return session;
    }

    public void setSession(javax.jcr.Session session) {
        this.session = session;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("session=");
        sb.append(session);
        sb.append(", creation=");
        sb.append(creation == null ? null : creation.getTime());
        sb.append(", lastAccess=");
        sb.append(lastAccess == null ? null : lastAccess.getTime());
        sb.append("]");
        return sb.toString();
    }
}