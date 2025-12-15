/*
 * @(#)2019年12月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.event;

import com.google.gson.JsonElement;
import com.wellsoft.context.event.WellptEvent;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.web.action.ActionInvocation;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年12月19日.1	zhulh		2019年12月19日		Create
 * </pre>
 * @date 2019年12月19日
 */
public class DmsActionEvent extends WellptEvent {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1360137792197363354L;

    private ActionContext actionContext;
    private ActionInvocation actionInvocation;
    private JsonElement jsonParams;

    /**
     * @param actionContext
     * @param actionInvocation
     * @param jsonParams
     */
    public DmsActionEvent(ActionContext actionContext, ActionInvocation actionInvocation, JsonElement jsonParams) {
        super(actionContext.getActionId());
        this.actionContext = actionContext;
        this.actionInvocation = actionInvocation;
        this.jsonParams = jsonParams;
    }

    /**
     * @return the actionContext
     */
    public ActionContext getActionContext() {
        return actionContext;
    }

    /**
     * @return the actionInvocation
     */
    public ActionInvocation getActionInvocation() {
        return actionInvocation;
    }

    /**
     * @return the jsonParams
     */
    public JsonElement getJsonParams() {
        return jsonParams;
    }

}
