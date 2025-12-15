package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.jdbc.entity.JpaEntity;

import java.util.List;
import java.util.Map;

/**
 * @author yt
 * @title: BusinessProcessor
 * @date 2020/10/21 13:41
 */
public abstract class BusinessProcessor<T extends JpaEntity> {

    private List<T> addList;

    private List<T> updateList;

    /**
     * key:T的uuid
     **/
    private Map<String, T> oldMap;

    public BusinessProcessor(List<T> addList, List<T> updateList) {
        this.addList = addList;
        this.updateList = updateList;
    }

    public BusinessProcessor(List<T> addList, List<T> updateList, Map<String, T> oldMap) {
        this.addList = addList;
        this.updateList = updateList;
        this.oldMap = oldMap;
    }

    public BusinessProcessor(List<T> addList) {
        this.addList = addList;
    }


    public Map<String, T> getOldMap() {
        return oldMap;
    }

    public void setOldMap(Map<String, T> oldMap) {
        this.oldMap = oldMap;
    }

    public List<T> getAddList() {
        return addList;
    }

    public void setAddList(List<T> addList) {
        this.addList = addList;
    }

    public List<T> getUpdateList() {
        return updateList;
    }

    public void setUpdateList(List<T> updateList) {
        this.updateList = updateList;
    }

    public abstract void handle(Map<String, ProtoDataBean> beanMap);

}
