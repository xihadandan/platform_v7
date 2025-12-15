package com.wellsoft.pt.multi.org.dto;

import java.util.List;

/**
 * Description:
 * 下拉项DTO
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/4   Create
 * </pre>
 */
public class SelectOptionDto {

    private String id;
    private String name;
    private boolean isParent;
    private int nodeLevel;
    private List<SelectOptionDto> children;
    private Object data;

    public SelectOptionDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    public int getNodeLevel() {
        return nodeLevel;
    }

    public void setNodeLevel(int nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    public List<SelectOptionDto> getChildren() {
        return children;
    }

    public void setChildren(List<SelectOptionDto> children) {
        this.children = children;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
