/*
 * @(#)Jan 22, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.support;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.wellsoft.context.base.BaseObject;

import java.util.HashMap;
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
 * Jan 22, 2018.1	zhulh		Jan 22, 2018		Create
 * </pre>
 * @date Jan 22, 2018
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Button extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8303968048203891383L;

    private String uuid;
    private String code;
    private String text;
    private String associatedFileAction;
    private String associatedDocEditModel;
    private String group;
    private Map<String, Object> icon;
    private String cssClass;
    private HashMap<String, Object> btnLib;
    private String hidden;
    private EventHandler eventHandler;
    private EventParams eventParams;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text 要设置的text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the associatedFileAction
     */
    public String getAssociatedFileAction() {
        return associatedFileAction;
    }

    /**
     * @param associatedFileAction 要设置的associatedFileAction
     */
    public void setAssociatedFileAction(String associatedFileAction) {
        this.associatedFileAction = associatedFileAction;
    }

    /**
     * @return the associatedDocEditModel
     */
    public String getAssociatedDocEditModel() {
        return associatedDocEditModel;
    }

    /**
     * @param associatedDocEditModel 要设置的associatedDocEditModel
     */
    public void setAssociatedDocEditModel(String associatedDocEditModel) {
        this.associatedDocEditModel = associatedDocEditModel;
    }

    /**
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @param group 要设置的group
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return the icon
     */
    public Map<String, Object> getIcon() {
        return icon;
    }

    /**
     * @param icon 要设置的icon
     */
    public void setIcon(Map<String, Object> icon) {
        this.icon = icon;
    }

    /**
     * @return the cssClass
     */
    public String getCssClass() {
        return cssClass;
    }

    /**
     * @param cssClass 要设置的cssClass
     */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * @return the hidden
     */
    public String getHidden() {
        return hidden;
    }

    /**
     * @param hidden 要设置的hidden
     */
    public void setHidden(String hidden) {
        this.hidden = hidden;
    }

    /**
     * @return the eventHandler
     */
    public EventHandler getEventHandler() {
        return eventHandler;
    }

    /**
     * @param eventHandler 要设置的eventHandler
     */
    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * @return the eventParams
     */
    public EventParams getEventParams() {
        return eventParams;
    }

    /**
     * @param eventParams 要设置的eventParams
     */
    public void setEventParams(EventParams eventParams) {
        this.eventParams = eventParams;
    }


    public HashMap<String, Object> getBtnLib() {
        return btnLib;
    }

    public void setBtnLib(HashMap<String, Object> btnLib) {
        this.btnLib = btnLib;
    }
}
