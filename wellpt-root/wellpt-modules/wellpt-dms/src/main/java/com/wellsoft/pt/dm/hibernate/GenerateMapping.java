package com.wellsoft.pt.dm.hibernate;


import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年05月22日   chenq	 Create
 * </pre>
 */
public class GenerateMapping implements Serializable {
    private static final long serialVersionUID = 2523155713569455274L;

    private String tableName;
    private String comment;
    private String schema;

    private Id id;

    private List<Property> propertyList = Lists.newArrayList();

    public GenerateMapping() {
    }

    public GenerateMapping(String tableName, String comment, Id id) {
        this.tableName = tableName;
        this.comment = comment;
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<Property> propertyList) {
        this.propertyList = propertyList;
    }

    public void addProperty(Property property) {
        this.propertyList.add(property);
    }

    public void addProperty(Property... properties) {
        this.propertyList.addAll(Lists.newArrayList(properties));

    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public boolean hasProperty(String name) {
        if (this.id != null && this.id.getName().equalsIgnoreCase(name)) {
            return true;
        }
        for (Property p : this.propertyList) {
            if (p.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }


    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Property getProperty(String name) {
        for (Property prop : this.propertyList) {
            if (prop.getName().equalsIgnoreCase(name)) {
                return prop;
            }
        }
        return null;
    }

    public static class Id implements Serializable {
        String name;
        String type;
        Integer length;


        public Id() {
        }

        public Id(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public Id(String name, String type, Integer length) {
            this.name = name;
            this.type = type;
            this.length = length;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }
    }


}
