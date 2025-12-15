package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.jdbc.entity.JpaEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yt
 * @title: ProtoDataBeanTree
 * @date 2020/8/14 11:36
 */
public class ProtoDataBeanTree<T extends JpaEntity, P extends JpaEntity, C extends JpaEntity> {

    private Map<String, Set<P>> parentMap = new HashMap<>();

    private Map<String, Set<C>> childerMap = new HashMap<>();

    private ProtoDataBean<T> protoDataBean;

    public Map<String, Set<P>> getParentMap() {
        return parentMap;
    }

    public void setParentMap(Map<String, Set<P>> parentMap) {
        this.parentMap = parentMap;
    }

    public Map<String, Set<C>> getChilderMap() {
        return childerMap;
    }

    public void setChilderMap(Map<String, Set<C>> childerMap) {
        this.childerMap = childerMap;
    }

    public ProtoDataBean<T> getProtoDataBean() {
        return protoDataBean;
    }

    public void setProtoDataBean(ProtoDataBean<T> protoDataBean) {
        this.protoDataBean = protoDataBean;
    }
}
