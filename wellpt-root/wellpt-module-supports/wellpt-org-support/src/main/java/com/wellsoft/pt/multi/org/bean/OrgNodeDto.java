package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * @author yt
 * @title: OrgNodeDto
 * @date 2020/6/19 13:56
 */
public class OrgNodeDto implements BaseQueryItem {

    private String id;
    private String name;
    private String shortName;
    private String type;
    private String code;
    private String eleIdPath;

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

    public String getShortName() {
        return shortName;
    }

    public OrgNodeDto setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEleIdPath() {
        return eleIdPath;
    }

    public void setEleIdPath(String eleIdPath) {
        this.eleIdPath = eleIdPath;
    }

}
