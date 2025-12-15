/*
 * @(#)2020年1月20日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.ext.enumcls.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.dto.DataItem;
import com.wellsoft.context.util.enumtool.EnumClassUtils;
import com.wellsoft.pt.basicdata.datastore.ext.enumcls.service.EnumClassSelect2QueryService;
import com.wellsoft.pt.jpa.util.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月20日.1	zhulh		2020年1月20日		Create
 * </pre>
 * @date 2020年1月20日
 */
@Service
public class EnumClassSelect2QueryServiceImpl implements EnumClassSelect2QueryService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.ext.enumcls.service.EnumClassSelect2QueryService#queryAll4SelectOptions(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData queryAll4SelectOptions(Select2QueryInfo queryInfo) {
        List<DataItem> dataItems = Lists.newArrayList();
        Map<String, Class<?>> enumClsMap = ClassUtils.getEnumClasses();
        for (Entry<String, Class<?>> entry : enumClsMap.entrySet()) {
            if (StringUtils.isBlank(entry.getValue().getSimpleName())) {
                continue;
            }
            DataItem dataItem = new DataItem();
            dataItem.setLabel(entry.getValue().getSimpleName());
            dataItem.setValue(entry.getKey());
            dataItems.add(dataItem);
        }
        return new Select2QueryData(dataItems, "value", "label");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.ext.enumcls.service.EnumClassSelect2QueryService#loadEnumClassFieldsSelectData(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadEnumClassFieldsSelectData(Select2QueryInfo queryInfo) {
        String className = queryInfo.getOtherParams("enumClass");
        List<String> propertyNames = EnumClassUtils.getPropertyNames(className);
        List<DataItem> dataItems = Lists.newArrayList();
        for (String propertyName : propertyNames) {
            DataItem dataItem = new DataItem();
            dataItem.setLabel(propertyName);
            dataItem.setValue(propertyName);
            dataItems.add(dataItem);
        }
        return new Select2QueryData(dataItems, "value", "label");
    }

}
