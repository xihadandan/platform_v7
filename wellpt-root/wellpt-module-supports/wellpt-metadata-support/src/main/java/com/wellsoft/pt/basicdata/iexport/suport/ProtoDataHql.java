package com.wellsoft.pt.basicdata.iexport.suport;


import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yt
 * @title: ProtoDataHql
 * @date 2020/8/17 13:38
 */
public class ProtoDataHql {

    /**
     * 父type
     */
    private String parentType;
    /**
     * 实体类名称
     */
    private String entityName;

    /**
     * hql查询参数
     */
    private Map<String, Object> params = new HashMap<>();

    /**
     * 结果排序
     */
    private Comparator comparator;

    /**
     * 生成hql方法
     */
    private GenerateHql generateHql;

    private StringBuilder sbHql = new StringBuilder();

    public ProtoDataHql(String parentType, String entityName) {
        this.parentType = parentType;
        this.entityName = entityName;
    }

    /**
     * 获取hql
     *
     * @return
     */
    public StringBuilder getHql() {
        if (sbHql.length() > 0) {
            return sbHql;
        }
        if (generateHql != null) {
            generateHql.build(this);
        }
        return sbHql;
    }

    public StringBuilder getSbHql() {
        return sbHql;
    }

    public void setSbHql(StringBuilder sbHql) {
        this.sbHql = sbHql;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public GenerateHql getGenerateHql() {
        return generateHql;
    }

    public void setGenerateHql(GenerateHql generateHql) {
        this.generateHql = generateHql;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Comparator getComparator() {
        return comparator;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }
}
