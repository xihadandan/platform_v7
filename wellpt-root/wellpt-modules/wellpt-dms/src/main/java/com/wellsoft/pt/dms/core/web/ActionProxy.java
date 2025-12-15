/*
 * @(#)Feb 19, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web;

import com.wellsoft.pt.dms.core.web.action.Action;
import org.codehaus.jackson.annotate.JsonIgnore;
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
public class ActionProxy extends Action {

    private String fullName;

    @JsonIgnore
    private Object action;

    /**
     *
     */
    public ActionProxy() {
        super();
    }

    /**
     * @param actionSupport
     * @param name
     * @param id
     * @param actionMethod
     * @param extras
     */
    public ActionProxy(ActionSupport actionSupport, String fullName, String name, String id, Method actionMethod,
                       Map<String, Object> extras) {
        super(name, id, actionMethod, extras);
        this.fullName = fullName;
        this.action = actionSupport;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName 要设置的fullName
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Object getAction() {
        return action;
    }

}
