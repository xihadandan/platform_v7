package com.wellsoft.pt.repository.entity.jcr;

import java.io.Serializable;

public class Notification implements Serializable {
    public static final String TYPE = "mix:notification";
    public static final String SUBSCRIPTORS = "okm:subscriptors";
    private static final long serialVersionUID = 7492226582552064878L;
    private String item;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("item=");
        sb.append(item);
        sb.append("}");
        return sb.toString();
    }
}