package com.wellsoft.pt.multi.org.bean;

import java.io.Serializable;

/**
 * @author yt
 * @title: OrgElementVo
 * @date 2020/6/15 11:18 上午
 */
public class OrgElementVo implements Serializable {

    // ID
    private String id;

    // NAME
    private String name;

    private OrgElementVo parent;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrgElementVo getParent() {
        return parent;
    }

    public void setParent(OrgElementVo parent) {
        this.parent = parent;
    }
}
