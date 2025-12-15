package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * @author yt
 * @title: OrgNodeUserDto
 * @date 2020/6/19 14:07
 */
public class OrgNodeUserDto implements BaseQueryItem {

    private String id;
    private String name;
    private String sex;
    private String code;
    private String eleId;

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEleId() {
        return eleId;
    }

    public void setEleId(String eleId) {
        this.eleId = eleId;
    }

}
