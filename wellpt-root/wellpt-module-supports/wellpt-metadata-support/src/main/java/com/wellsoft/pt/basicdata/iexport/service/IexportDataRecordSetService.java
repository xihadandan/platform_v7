/*
 * @(#)2016年1月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataRecordSet;

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
 * 2016年1月13日.1	zhulh		2016年1月13日		Create
 * </pre>
 * @date 2016年1月13日
 */
public interface IexportDataRecordSetService extends BaseService {

    /**
     * @param iexportData
     * @param iexportDataMetaData
     */
    IexportDataRecordSet getData(IexportData iexportData);

    /**
     * 获取行数据
     *
     * @param uuid
     * @param type
     * @return
     */
    Map<String, Object> getIexportRowData(String uuid, String type);

    /**
     * 如何描述该方法
     *
     * @param object
     */
    void save(IexportDataRecordSet object);

    /**
     * 判断数据是否存在
     *
     * @param iexportData
     * @return 存在返回true 不存在返回false
     */
    boolean isExists(IexportData iexportData);

    /**
     * 判断数据是否不同
     *
     * @param iexportData
     * @return 存在返回true 不存在返回false
     */
    boolean hasDifference(IexportData iexportData);

}
