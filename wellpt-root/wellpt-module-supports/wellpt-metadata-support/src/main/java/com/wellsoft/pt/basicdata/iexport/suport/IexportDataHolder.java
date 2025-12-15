/*
 * @(#)2016年8月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
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
 * 2016年8月4日.1	zhulh		2016年8月4日		Create
 * </pre>
 * @date 2016年8月4日
 */
public class IexportDataHolder {
    private List<IexportData> iexportDatas = new ArrayList<IexportData>();
    private Map<String, String> parentIexportDataMap = Maps.newHashMap();

    public void add(IexportData iexportData) {
        add(iexportData, null);
    }

    public void add(IexportData iexportData, IexportData parentIexportData) {
        iexportDatas.add(iexportData);
        if (parentIexportData != null) {
            parentIexportDataMap.put(parentIexportData.getId(), iexportData.getId());
        }
    }

    public boolean contains(IexportData iexportData) {
        return contains(iexportData, null);
    }

    public boolean contains(IexportData iexportData, IexportData parentIexportData) {
        String iexportDataId = iexportData.getId();
        if (parentIexportData != null) {
            String parentId = parentIexportData.getId();
            if (!StringUtils.equals(parentId, parentIexportDataMap.get(iexportDataId))) {
                return false;
            }
        }
        for (IexportData data : iexportDatas) {
            String dataId = data.getId();
            if (StringUtils.equals(dataId, iexportDataId)) {
                return true;
            }
        }
        return false;
    }

}
