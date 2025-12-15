/*
 * @(#)Jan 22, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.support;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
public class View extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8018576662659119221L;

    private String dataViewName;
    private String dataViewType;
    private String listViewName;
    private String listViewId;
    private String tagTreeName;
    private String tagTreeId;
    private String unitTreeName;
    private String unitTreeId;
    private String previewName;
    private String previewId;

    /**
     * @return the dataViewName
     */
    public String getDataViewName() {
        return dataViewName;
    }

    /**
     * @param dataViewName 要设置的dataViewName
     */
    public void setDataViewName(String dataViewName) {
        this.dataViewName = dataViewName;
    }

    /**
     * @return the dataViewType
     */
    public String getDataViewType() {
        return dataViewType;
    }

    /**
     * @param dataViewType 要设置的dataViewType
     */
    public void setDataViewType(String dataViewType) {
        this.dataViewType = dataViewType;
    }

    /**
     * @return the listViewName
     */
    public String getListViewName() {
        return listViewName;
    }

    /**
     * @param listViewName 要设置的listViewName
     */
    public void setListViewName(String listViewName) {
        this.listViewName = listViewName;
    }

    /**
     * @return the listViewId
     */
    public String getListViewId() {
        return listViewId;
    }

    /**
     * @param listViewId 要设置的listViewId
     */
    public void setListViewId(String listViewId) {
        this.listViewId = listViewId;
    }

    /**
     * @return the tagTreeName
     */
    public String getTagTreeName() {
        return tagTreeName;
    }

    /**
     * @param tagTreeName 要设置的tagTreeName
     */
    public void setTagTreeName(String tagTreeName) {
        this.tagTreeName = tagTreeName;
    }

    /**
     * @return the tagTreeId
     */
    public String getTagTreeId() {
        return tagTreeId;
    }

    /**
     * @param tagTreeId 要设置的tagTreeId
     */
    public void setTagTreeId(String tagTreeId) {
        this.tagTreeId = tagTreeId;
    }

    /**
     * @return the unitTreeName
     */
    public String getUnitTreeName() {
        return unitTreeName;
    }

    /**
     * @param unitTreeName 要设置的unitTreeName
     */
    public void setUnitTreeName(String unitTreeName) {
        this.unitTreeName = unitTreeName;
    }

    /**
     * @return the unitTreeId
     */
    public String getUnitTreeId() {
        return unitTreeId;
    }

    /**
     * @param unitTreeId 要设置的unitTreeId
     */
    public void setUnitTreeId(String unitTreeId) {
        this.unitTreeId = unitTreeId;
    }

    /**
     * @return the previewName
     */
    public String getPreviewName() {
        return previewName;
    }

    /**
     * @param previewName 要设置的previewName
     */
    public void setPreviewName(String previewName) {
        this.previewName = previewName;
    }

    /**
     * @return the previewId
     */
    public String getPreviewId() {
        return previewId;
    }

    /**
     * @param previewId 要设置的previewId
     */
    public void setPreviewId(String previewId) {
        this.previewId = previewId;
    }

}
