/*
 * @(#)2017-02-14 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.action;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 操作
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-02-14.1	zhulh		2017-02-14		Create
 * </pre>
 * @date 2017-02-14
 */
public abstract class Action {

    protected String name;

    protected String id;

    protected String code;

    // AppContext appContext
    @JsonIgnore
    private Method actionMethod;

    private Map<String, Object> properties = new HashMap<String, Object>(0);

    /**
     *
     */
    public Action() {
    }

    /**
     * @param name
     * @param id
     */
    public Action(String name, String id) {
        this.name = name;
        this.id = id;
    }

    /**
     * @param name
     * @param id
     * @param properties
     */
    public Action(String name, String id, Method actionMethod, Map<String, Object> properties) {
        this.name = name;
        this.id = id;
        this.actionMethod = actionMethod;
        this.properties = properties;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the properties
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * @param properties 要设置的properties
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * @return
     */
    public Method getActionMethod() {
        return this.actionMethod;
    }

    /**
     * @return
     */
    public Object getAction() {
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Action other = (Action) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
