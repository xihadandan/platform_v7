/*
 * @(#)Feb 19, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.context.ActionContextHolder;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

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
public abstract class ActionSupport implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6159755850387866456L;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected ActionResult createActionResult() {
        return new ActionResult();
    }

    protected ActionContext getActionContext() {
        return ActionContextHolder.getContext();
    }

    /**
     * 删除操作
     *
     * @param actions
     * @param actionIds
     * @return
     */
    protected void removeAction(List<ActionProxy> actions, String... actionIds) {
        for (String actionId : actionIds) {
            actions.remove(getActionContext().buildAction(actionId));
        }
    }

    /**
     * 保留操作
     *
     * @param actions
     * @param actionIds
     */
    protected void retainAction(List<ActionProxy> actions, String... actionIds) {
        List<ActionProxy> retainActions = Lists.newArrayList();
        Set<String> retainActionSet = Sets.newHashSet(actionIds);
        for (ActionProxy action : actions) {
            if (retainActionSet.contains(action.getId())) {
                retainActions.add(action);
            }
        }
        actions.retainAll(retainActions);
    }

}
