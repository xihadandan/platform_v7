package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * @author zyj
 * @title: OrgGroupMemberDto
 * @date 2020/6/29 13:56
 */
public class OrgGroupMemberDto implements BaseQueryItem {

    private String id;
    private String type;
    private String name;
    private Integer childrenCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(Integer childrenCount) {
        this.childrenCount = childrenCount;
    }
}
