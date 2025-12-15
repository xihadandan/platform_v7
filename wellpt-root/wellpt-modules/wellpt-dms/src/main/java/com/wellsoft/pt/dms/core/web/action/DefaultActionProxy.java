/*
 * @(#)Feb 19, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.action;

import com.wellsoft.pt.dms.core.web.ActionProxy;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 19, 2017.1	zhulh		Feb 19, 2017		Create
 * </pre>
 * @date Feb 19, 2017
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultActionProxy extends ActionProxy {

    private String name;
    private String id;

    /**
     *
     */
    public DefaultActionProxy() {
        super();
    }

    /**
     * @param actionSupport
     * @param name
     * @param id
     * @param actionMethod
     * @param extras
     */
    public DefaultActionProxy(ActionSupport actionSupport, String name, String id, Method actionMethod,
                              Map<String, Object> extras) {
        super(actionSupport, name, name, id, actionMethod, extras);
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

}
