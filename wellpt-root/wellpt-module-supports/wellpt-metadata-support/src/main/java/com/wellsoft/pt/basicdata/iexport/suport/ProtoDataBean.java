package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.jdbc.entity.JpaEntity;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author yt
 * @title: ProtoDataBean
 * @date 2020/8/14 11:36
 */
public class ProtoDataBean<T extends JpaEntity> implements Serializable {

    private Collection<String> parentTypeUuids;//父级type_uuids

    private String type; //type

    private String treeName;

    private T data; //实体类

    private Collection<String> fileIds; //附件


    public ProtoDataBean(T data, String type, String treeName) {
        this.data = data;
        this.type = type;
        this.treeName = treeName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Collection<String> getParentTypeUuids() {
        return parentTypeUuids;
    }

    public void setParentTypeUuids(Collection<String> parentTypeUuids) {
        this.parentTypeUuids = parentTypeUuids;
    }

    public Collection<String> getFileIds() {
        return fileIds;
    }

    public void setFileIds(Collection<String> fileIds) {
        this.fileIds = fileIds;
    }

    public String getTreeName() {
        return treeName;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }
}
