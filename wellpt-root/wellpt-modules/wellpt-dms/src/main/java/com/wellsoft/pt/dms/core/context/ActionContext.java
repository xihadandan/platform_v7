/*
 * @(#)Mar 2, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.context;

import com.wellsoft.pt.dms.config.support.Configuration;
import com.wellsoft.pt.dms.core.web.ActionProxy;

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
 * Mar 2, 2017.1	zhulh		Mar 2, 2017		Create
 * </pre>
 * @date Mar 2, 2017
 */
public interface ActionContext {

    /**
     * 获取当前操作ID
     *
     * @return
     */
    String getActionId();

    /**
     * 获取数据管理服务ID
     *
     * @return
     */
    String getDmsId();

    /**
     * 获取文档标题
     *
     * @return
     */
    String getDocumentTitle();

    /**
     * 获取文档标题
     *
     * @param object
     * @return
     */
    String getDocumentTitle(Object data);

    /**
     * 获取配置信息
     *
     * @return
     */
    Configuration getConfiguration();

    /**
     * 构建操作对象
     *
     * @param actionId
     * @return
     */
    ActionProxy buildAction(String actionId);

    /**
     * 构建操作对象
     *
     * @param actionId
     * @param text
     * @return
     */
    ActionProxy buildAction(String actionId, String text);

    /**
     * @return
     */
    List<ActionProxy> getActions();

    /**
     * @param actions
     */
    void setActions(List<ActionProxy> actions);

    /**
     * @param actionId
     */
    ActionProxy getAction(String actionId);

    /**
     * @param actionId
     */
    void removeAction(String actionId);

    /**
     * @param action
     */
    void removeAction(ActionProxy action);

    /**
     * 如何描述该方法
     *
     * @return
     */
    String getFormUuid();

    /**
     * 操作权限判断
     *
     * @param actionId
     * @return
     */
    boolean isGrantedAction(String actionId);

    /**
     * 操作权限判断
     *
     * @param actionId
     * @return
     */
    boolean isGrantedAction(ActionProxy action);


    Map<String, Object> getDyformData();

    void setDyformData(Map<String, Object> dyformData);
}
