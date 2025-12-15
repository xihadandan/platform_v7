/*
 * @(#)2020年9月26日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.util.dyform;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 表单数据选项
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年9月26日.1	zhongzh		2020年9月26日		Create
 * </pre>
 * @date 2020年9月26日
 */
public class DyformDataOptions extends JSONObject {

    private static Logger logger = LoggerFactory.getLogger(DyformDataOptions.class);

    /**
     *
     */
    public DyformDataOptions() {
        super();
    }

    /**
     * @param dyformDataOptions
     * @throws JSONException
     */
    public DyformDataOptions(String dyformDataOptions) throws JSONException {
        super(dyformDataOptions);
    }

    /**
     * @param dyformDataOptions
     */
    public DyformDataOptions(Map<String, Object> dyformDataOptions) {
        super(dyformDataOptions);
    }

    public Object getOptions(String key) {
        return opt(key);
    }

    public DyformDataOptions putOptions(String key, Object value) {
        try {
            put(key, value);
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return this;
    }

    /**
     * 表单保存时生存流水号
     *
     * @return
     */
    public boolean isSerialNumberConfirmed(String fieldName) {
        return optBoolean("serialNumberConfirmed-" + fieldName);
    }

}
