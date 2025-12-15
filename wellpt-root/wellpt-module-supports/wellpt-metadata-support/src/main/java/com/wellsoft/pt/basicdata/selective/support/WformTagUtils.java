/*
 * @(#)2015-9-26 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.selective.support;

import com.wellsoft.pt.basicdata.selective.facade.SelectiveDatas;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-9-26.1	zhulh		2015-9-26		Create
 * </pre>
 * @date 2015-9-26
 */
public class WformTagUtils {

    /**
     * @param configKey
     * @return
     */
    public static Object getItems(String configKey) {
        return SelectiveDatas.getItems(configKey);
    }

    /**
     * @param itemLabel
     * @param configKey
     * @return
     */
    public static String getItemLabel(String itemLabel, String configKey) {
        if (StringUtils.isNotBlank(itemLabel) || StringUtils.isBlank(configKey)) {
            return itemLabel;
        }

        SelectiveData selectiveData = SelectiveDatas.get(configKey);
        if (selectiveData == null) {
            return itemLabel;
        }

        return selectiveData.getItemLabel();
    }

    /**
     * @param itemValue
     * @param configKey
     * @return
     */
    public static String getItemValue(String itemValue, String configKey) {
        if (StringUtils.isNotBlank(itemValue) || StringUtils.isBlank(configKey)) {
            return itemValue;
        }

        SelectiveData selectiveData = SelectiveDatas.get(configKey);
        if (selectiveData == null) {
            return itemValue;
        }

        return selectiveData.getItemValue();
    }

}
