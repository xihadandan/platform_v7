/*
 * @(#)2018年10月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.model;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年10月12日.1	zhulh		2018年10月12日		Create
 * </pre>
 * @date 2018年10月12日
 */
public class DmsFolderDataViewConfiguration extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5097553449458348136L;

    // 展示视图ID
    private String listViewId;
    // 是否继承上级夹视图
    private boolean isInherit;
    // 是否文件库数据源
    private boolean isFileManagerDataStore;

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
     * @return the isInherit
     */
    public boolean isInherit() {
        return isInherit;
    }

    /**
     * @param isInherit 要设置的isInherit
     */
    public void setInherit(boolean isInherit) {
        this.isInherit = isInherit;
    }

    /**
     * @return the isFileManagerDataStore
     */
    public boolean isFileManagerDataStore() {
        return isFileManagerDataStore;
    }

    /**
     * @param isFileManagerDataStore 要设置的isFileManagerDataStore
     */
    public void setFileManagerDataStore(boolean isFileManagerDataStore) {
        this.isFileManagerDataStore = isFileManagerDataStore;
    }

}
