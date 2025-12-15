package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

import java.util.Objects;

/**
 * @author yt
 * @title: SimpleUser
 * @date 2020/8/6 09:34
 */
public class SimpleUser implements BaseQueryItem {

    private String id;
    private String name;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleUser)) return false;
        SimpleUser that = (SimpleUser) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
