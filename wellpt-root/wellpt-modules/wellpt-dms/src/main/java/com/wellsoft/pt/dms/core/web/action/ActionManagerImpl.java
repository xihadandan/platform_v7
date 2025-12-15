/*
 * @(#)Feb 17, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.action;

import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.annotation.ListViewActionConfig;
import com.wellsoft.pt.dms.core.web.ActionProxy;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
 * Feb 17, 2017.1	zhulh		Feb 17, 2017		Create
 * </pre>
 * @date Feb 17, 2017
 */
@Component
public class ActionManagerImpl implements ActionManager, InitializingBean {

    private Map<String, ActionProxy> actionMap = new HashMap<String, ActionProxy>();

    @Autowired
    private List<ActionSupport> actionSupports;

    /**
     * @return the actionSupports
     */
    public List<ActionSupport> getActionSupports() {
        return actionSupports;
    }

    /**
     * @param actionSupports 要设置的actionSupports
     */
    public void setActionSupports(List<ActionSupport> actionSupports) {
        this.actionSupports = actionSupports;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.action.ActionManager#registerAction(java.lang.String, com.wellsoft.pt.dms.core.action.Action)
     */
    @Override
    public void registerAction(String actionId, ActionProxy action) {
        if (actionMap.containsKey(actionId)) {
            throw new RuntimeException("ID为[" + actionId + "]的数据管理操作已经存在!");
        }
        actionMap.put(actionId, action);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.action.ActionManager#getAction(java.lang.String)
     */
    @Override
    public ActionProxy getAction(String actionId) {
        return actionMap.get(actionId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.action.ActionManager#getAllActions()
     */
    @Override
    public Collection<ActionProxy> getAllActions() {
        return this.actionMap.values();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        for (ActionSupport actionSupport : actionSupports) {
            com.wellsoft.pt.dms.core.annotation.Action action = actionSupport.getClass().getAnnotation(
                    com.wellsoft.pt.dms.core.annotation.Action.class);
            if (action == null) {
                continue;
            }
            Class<?> actionClass = actionSupport.getClass();
            String className = actionClass.getCanonicalName();
            String actionName = className;
            String[] actionNames = action.value();
            if (actionNames.length > 0 && StringUtils.isNotBlank(actionNames[0])) {
                actionName = actionNames[0];
            }

            Method[] methods = actionSupport.getClass().getDeclaredMethods();
            for (Method method : methods) {
                ActionConfig actionConfig = method.getAnnotation(ActionConfig.class);
                ListViewActionConfig listViewActionConfig = method.getAnnotation(ListViewActionConfig.class);
                if (actionConfig == null && listViewActionConfig == null) {
                    continue;
                }
                String actionMethodName = null;
                String actionMethodId = null;
                String executeJsModule = null;
                boolean singleSelect = false;
                String singleSelectPromptMsg = null;
                String beforeConfirmAction = null;
                String promptMsg = null;
                String confirmMsg = null;
                boolean validate = false;
                if (actionConfig != null) {
                    actionMethodName = actionConfig.name();
                    actionMethodId = actionConfig.id();
                    executeJsModule = actionConfig.executeJsModule();
                    beforeConfirmAction = actionConfig.beforeConfirmAction();
                    confirmMsg = actionConfig.confirmMsg();
                    validate = actionConfig.validate();
                }
                if (listViewActionConfig != null) {
                    actionMethodName = listViewActionConfig.name();
                    actionMethodId = listViewActionConfig.id();
                    executeJsModule = listViewActionConfig.executeJsModule();
                    singleSelect = listViewActionConfig.singleSelect();
                    singleSelectPromptMsg = listViewActionConfig.singleSelectPromptMsg();
                    promptMsg = listViewActionConfig.promptMsg();
                    beforeConfirmAction = listViewActionConfig.beforeConfirmAction();
                    confirmMsg = listViewActionConfig.confirmMsg();
                }
                registerAction(actionSupport, className, actionName, method, actionMethodName, actionMethodId,
                        executeJsModule, singleSelect, singleSelectPromptMsg, promptMsg, beforeConfirmAction, confirmMsg, validate);
            }
        }
    }

    /**
     * @param actionSupport
     * @param className
     * @param actionName
     * @param method
     * @param actionMethodName
     * @param actionMethodId
     * @param executeJsModule
     * @param singleSelect
     * @param singleSelectPromptMsg
     * @param promptMsg
     * @param beforeConfirmAction
     * @param confirmMsg
     * @param validate
     */
    private void registerAction(ActionSupport actionSupport, String className, String actionName, Method method,
                                String actionMethodName, String actionMethodId, String executeJsModule, boolean singleSelect,
                                String singleSelectPromptMsg, String promptMsg, String beforeConfirmAction, String confirmMsg, boolean validate) {
        String methodName = method.getName();
        if (StringUtils.isBlank(actionMethodName)) {
            actionMethodName = methodName;
        }
        String fullName = actionName + "_" + actionMethodName;
        String id = className + "." + methodName;
        id = DigestUtils.md5Hex(id);
        if (StringUtils.isNotBlank(actionMethodId)) {
            id = actionMethodId;
        }

        // 注解属性
        Map<String, Object> extras = new HashMap<String, Object>();
        extras.put("className", className);
        extras.put("methodName", methodName);
        extras.put("actionName", actionName);
        extras.put("actionMethodName", actionMethodName);
        extras.put("executeJsModule", executeJsModule);
        extras.put("singleSelect", singleSelect);
        extras.put("singleSelectPromptMsg", singleSelectPromptMsg);
        extras.put("promptMsg", promptMsg);
        extras.put("beforeConfirmAction", beforeConfirmAction);
        extras.put("confirmMsg", confirmMsg);
        extras.put("validate", validate);
        ActionProxy proxy = new ActionProxy(actionSupport, fullName, actionMethodName, id, method, extras);
        registerAction(proxy.getId(), proxy);
    }
}
