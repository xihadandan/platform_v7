package com.wellsoft.pt.multi.org.bean;

import java.io.Serializable;

/**
 * @author yt
 * @title: OrgPathVo
 * @date 2020/10/26 09:27
 */
public class OrgPathVo implements Serializable {

    private String idPath;

    private String namePath;

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    public String getNamePath() {
        return namePath;
    }

    public void setNamePath(String namePath) {
        this.namePath = namePath;
    }
}
