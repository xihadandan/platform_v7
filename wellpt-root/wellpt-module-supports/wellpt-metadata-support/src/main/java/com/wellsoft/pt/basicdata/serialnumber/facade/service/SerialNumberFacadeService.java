/*
 * @(#)2018年4月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.serialnumber.bean.SerialNumberMaintainBean;
import com.wellsoft.pt.basicdata.serialnumber.bean.SerialNumberSupplementBean;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumber;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberRecordParams;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月19日.1	chenqiong		2018年4月19日		Create
 * </pre>
 * @date 2018年4月19日
 */
public interface SerialNumberFacadeService extends Facade {

    /**
     * @param serialNo
     * @return
     */
    SerialNumber getById(String serialNo);


    ResultMessage addSerialNumberRecord(SerialNumberRecordParams params);

    /**
     * 流水号记录
     */
    boolean serialNumberRecord(SerialNumberRecordParams params);


    /**
     * 流水号补号数据查询
     *
     * @param snId
     * @param serialNumberMaintainList
     * @return
     */
    List<SerialNumberSupplementBean> supplement(String snId, List<SerialNumberMaintainBean> serialNumberMaintainList);


    List<SerialNumberSupplementBean> supplementByTableFieldName(String snId, String tableName, String fieldName, List<SerialNumberMaintainBean> serialNumberMaintainList);


}
