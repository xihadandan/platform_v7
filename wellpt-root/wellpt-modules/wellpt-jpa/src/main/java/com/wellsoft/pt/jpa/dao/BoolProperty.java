package com.wellsoft.pt.jpa.dao;

/**
 * @author lilin
 * @ClassName: BoolProperty
 * @Description: TODO(这里用一句话描述这个类的作用)
 */
public class BoolProperty {

    private Integer id;
    private boolean name;

    public BoolProperty(Integer id, boolean name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isName() {
        return name;
    }

    public void setName(boolean name) {
        this.name = name;
    }
}
