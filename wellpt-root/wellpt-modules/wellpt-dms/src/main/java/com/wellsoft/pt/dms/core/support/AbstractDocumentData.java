/*
 * @(#)May 23, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.support;

import com.wellsoft.pt.dms.core.web.ActionProxy;

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
 * May 23, 2017.1	zhulh		May 23, 2017		Create
 * </pre>
 * @date May 23, 2017
 */
public abstract class AbstractDocumentData implements DocumentData {

    private List<ActionProxy> actions;

    private Map<String, Object> extras = new HashMap<String, Object>(0);

    /**
     *
     */
    public AbstractDocumentData() {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.support.DmsData#getActions()
     */
    @Override
    public List<ActionProxy> getActions() {
        return this.actions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.support.DmsData#setActions(java.util.List)
     */
    @Override
    public void setActions(List<ActionProxy> actions) {
        this.actions = actions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.support.DocumentData#getExtras()
     */
    @Override
    public Map<String, Object> getExtras() {
        return extras;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.support.DocumentData#setExtras(java.util.Map)
     */
    @Override
    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

    /**
     * @param key
     * @param value
     */
    public void putExtra(String key, Object value) {
        this.extras.put(key, value);
    }

}
