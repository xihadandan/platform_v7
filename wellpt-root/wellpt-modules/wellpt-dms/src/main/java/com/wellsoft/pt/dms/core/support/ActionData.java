/*
 * @(#)Feb 20, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.dms.core.web.ActionProxy;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
 * Feb 20, 2017.1	zhulh		Feb 20, 2017		Create
 * </pre>
 * @date Feb 20, 2017
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionData extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6200430267808157413L;

    private ActionProxy action;

    private Map<String, Object> extras;

    /**
     * @return the action
     */
    public ActionProxy getAction() {
        return action;
    }

    /**
     * @param action 要设置的action
     */
    public void setAction(ActionProxy action) {
        this.action = action;
    }

    /**
     * @return the extras
     */
    public Map<String, Object> getExtras() {
        return extras;
    }

    /**
     * @param extras 要设置的extras
     */
    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

    /**
     * @param key
     * @return
     */
    public Object getExtra(String key) {
        if (this.extras == null) {
            return null;
        }
        return this.extras.get(key);
    }

    /**
     * @param key
     * @return
     */
    public String getExtraString(String key) {
        if (this.extras == null) {
            return null;
        }
        Object val = this.extras.get(key);
        if (val == null) {
            return null;
        }
        return val.toString();
    }
}
