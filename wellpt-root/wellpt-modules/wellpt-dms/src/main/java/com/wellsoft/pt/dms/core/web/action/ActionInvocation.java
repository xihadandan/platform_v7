/*
 * @(#)Feb 21, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.action;

import org.springframework.web.method.HandlerMethod;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 21, 2017.1	zhulh		Feb 21, 2017		Create
 * </pre>
 * @date Feb 21, 2017
 */
public class ActionInvocation extends HandlerMethod {

    private Action action;

    /**
     * @param action
     */
    public ActionInvocation(Action action) {
        super(action.getAction(), action.getActionMethod());
        this.action = action;
    }

    /**
     * @return the action
     */
    public Action getAction() {
        return action;
    }

}
