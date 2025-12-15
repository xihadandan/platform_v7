/*
 * @(#)Mar 2, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.context;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.support.AppCacheUtils;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.support.PiItem;
import com.wellsoft.pt.dms.config.support.Button;
import com.wellsoft.pt.dms.config.support.Configuration;
import com.wellsoft.pt.dms.config.support.EventHandler;
import com.wellsoft.pt.dms.core.support.DataType;
import com.wellsoft.pt.dms.core.web.ActionProxy;
import com.wellsoft.pt.dms.core.web.AppActionProxy;
import com.wellsoft.pt.dms.core.web.action.ActionManager;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
 * Mar 2, 2017.1	zhulh		Mar 2, 2017		Create
 * </pre>
 * @date Mar 2, 2017
 */
public class ActionContextImpl implements ActionContext {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String actionId;
    private Map<String, String> actionParams;
    private String dmsId;
    private Configuration configuration;
    private List<ActionProxy> actions;
    private Map<String, Object> dyformData = Maps.newHashMap();

    /**
     * @param actionId
     * @param dmsId
     * @param configuration
     */
    public ActionContextImpl(String actionId, Map<String, String> actionParams, String dmsId,
                             Configuration configuration) {
        super();
        this.actionId = actionId;
        this.actionParams = actionParams;
        this.dmsId = dmsId;
        this.configuration = configuration;
    }

    /**
     * @return the actionId
     */
    public String getActionId() {
        return actionId;
    }

    /**
     * @return the actionParams
     */
    public Map<String, String> getActionParams() {
        return actionParams;
    }

    /**
     * @return the dmsId
     */
    public String getDmsId() {
        return dmsId;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.context.ActionContext#getDocumentTitle()
     */
    @Override
    public String getDocumentTitle() {
        return this.configuration.getDocument(null, null).getTitle();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.context.ActionContext#getDocumentTitle(java.lang.Object)
     */
    @Override
    public String getDocumentTitle(Object data) {
        // 标题
        String titleExpression = getDocumentTitle();
        String title = titleExpression;
        if (StringUtils.isNotBlank(titleExpression)) {
            Object documentData = data;
            try {
                DyFormData dyFormData = null;
                if (documentData instanceof DyFormData) {
                    dyFormData = (DyFormData) documentData;
                } else if (documentData == null) {
                    documentData = this.getDyformData();
                }
                Map<String, Object> root = new HashMap<String, Object>();
                if (actionParams == null) {
                    actionParams = Maps.newHashMapWithExpectedSize(0);
                }
                root.put("parameter", actionParams);
                root.put("actionId", actionId);
                root.put("document", documentData);
                if (dyFormData != null) {
                    Map<String, Object> mainData = dyFormData.getFormDataOfMainform();
                    if (mainData == null) {
                        mainData = new HashMap<String, Object>(0);
                    }
                    root.put("document", mainData);
                }
                title = TemplateEngineFactory.getDefaultTemplateEngine().process(titleExpression, root);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return title;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.context.ActionContext#getConfiguration()
     */
    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.context.ActionContext#buildAction(java.lang.String)
     */
    @Override
    public ActionProxy buildAction(String actionId) {
        return this.buildAction(actionId, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.context.ActionContext#buildAction(java.lang.String, java.lang.String)
     */
    public ActionProxy buildAction(String actionId, String text) {
        ActionManager actionManager = ApplicationContextHolder.getBean(ActionManager.class);
        ActionProxy source = actionManager.getAction(actionId);
        if (source == null) {
            logger.error("找不到ID为" + actionId + "的数据管理操作!");
            return null;
        }

        ActionProxy actionProxy = new ActionProxy();
        BeanUtils.copyProperties(source, actionProxy);

        if (StringUtils.isNotBlank(text)) {
            actionProxy.setName(text);
        }

        return actionProxy;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.context.ActionContext#getActions()
     */
    @Override
    public List<ActionProxy> getActions() {
        if (actions == null) {
            actions = new ArrayList<ActionProxy>();
            List<Button> buttons = configuration.getDocument(null, null).getButtons();
            for (Button button : buttons) {
                String text = button.getText();
                // 从配置的事件处理获取action
                if (button.getEventHandler() != null && StringUtils.isNotBlank(button.getEventHandler().getId())) {
                    PiItem piItem = AppCacheUtils.getPiItem(button.getEventHandler().getId());
                    if (piItem == null) {
                        continue;
                    } else {
                        ActionProxy actionProxy = buildAction(piItem.getId(), text);
                        if (actionProxy == null) {
                            actionProxy = new AppActionProxy(piItem, text);
                        }
                        addButtonProperties(button, actionProxy, piItem.getUuid());
                        actionProxy.setCode(button.getCode());
                        // 权限判断
                        if (isGrantedAction(actionProxy)) {
                            actions.add(actionProxy);
                        }
                    }
                } else {
                    // 从配置信息获取action
                    ActionProxy actionProxy = new ActionProxy();
                    actionProxy.setId(button.getCode());
                    actionProxy.setName(text);
                    actionProxy.setCode(button.getCode());
                    addButtonProperties(button, actionProxy, StringUtils.EMPTY);
                    actions.add(actionProxy);
                }
            }
        }
        return this.actions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.context.ActionContext#setActions(java.util.List)
     */
    @Override
    public void setActions(List<ActionProxy> actions) {
        this.actions = actions;
    }

    /**
     * @param button
     * @param piUuid
     * @param actionProxy
     */
    private void addButtonProperties(Button button, ActionProxy actionProxy, String piUuid) {
        Map<String, Object> properties = actionProxy.getProperties();
        properties.put("associatedFileAction", button.getAssociatedFileAction());
        properties.put("associatedDocEditModel", button.getAssociatedDocEditModel());
        properties.put("group", button.getGroup());
        properties.put("icon", button.getIcon());
        properties.put("cssClass", button.getCssClass());
        properties.put("hidden", button.getHidden());
        properties.put("piUuid", piUuid);
        properties.put("btnLib", button.getBtnLib());
        Map<String, Object> params = Maps.newHashMap();
        // 事件参数
        if (button.getEventParams() != null) {
            params.putAll(button.getEventParams().getParams());
        }
        // 事件处理锚点参数
        EventHandler eventHandler = button.getEventHandler();
        if (eventHandler != null && StringUtils.isNotBlank(eventHandler.getHashType())
                && StringUtils.isNotBlank(eventHandler.getHash())) {
            String[] hashStrings = StringUtils.split(eventHandler.getHash(), Separator.COMMA.getValue());
            List<String> selectionIds = Lists.newArrayList();
            for (String hashString : hashStrings) {
                String[] widgetSelections = StringUtils.split(hashString, Separator.SLASH.getValue());
                if (widgetSelections.length > 1) {
                    selectionIds.add(widgetSelections[widgetSelections.length - 1]);
                }
            }
            String selection = StringUtils.join(selectionIds, Separator.COMMA.getValue());
            params.put("menuid", selection);
            params.put("selection", selection);
            params.put("hash", eventHandler.getHash());
        }
        properties.put("params", params);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.context.ActionContext#getAction(java.lang.String)
     */
    @Override
    public ActionProxy getAction(String actionId) {
        List<ActionProxy> actionProxies = getActions();
        for (ActionProxy actionProxy : actionProxies) {
            if (StringUtils.equals(actionProxy.getId(), actionId)) {
                return actionProxy;
            }
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.context.ActionContext#removeAction(java.lang.String)
     */
    @Override
    public void removeAction(String actionId) {
        ActionManager actionManager = ApplicationContextHolder.getBean(ActionManager.class);
        getActions().remove(actionManager.getAction(actionId));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.context.ActionContext#removeAction(com.wellsoft.pt.dms.core.web.ActionProxy)
     */
    @Override
    public void removeAction(ActionProxy action) {
        getActions().remove(action);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.context.ActionContext#getFormUuid()
     */
    @Override
    public String getFormUuid() {
        if (DataType.DYFORM.getId().equals(configuration.getStore(null, null).getDataType())) {
            return configuration.getStore(null, null).getFormUuid();
        }

        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.context.ActionContext#isGrantedAction(java.lang.String)
     */
    @Override
    public boolean isGrantedAction(String actionId) {
        ActionProxy action = getAction(actionId);
        return isGrantedAction(action);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.context.ActionContext#isGrantedAction(com.wellsoft.pt.dms.core.web.ActionProxy)
     */
    @Override
    public boolean isGrantedAction(ActionProxy action) {
        if (action == null) {
            return false;
        }
        return ApplicationContextHolder.getBean(SecurityAuditFacadeService.class).isGranted(
                action.getProperties().get("piUuid"), AppFunctionType.AppProductIntegration);
    }

    @Override
    public Map<String, Object> getDyformData() {
        return dyformData;
    }

    @Override
    public void setDyformData(Map<String, Object> dyformData) {
        this.dyformData = dyformData;
    }
}
