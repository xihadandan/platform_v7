/*
 * @(#)2018年12月7日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年12月7日.1	zhulh		2018年12月7日		Create
 * </pre>
 * @date 2018年12月7日
 */
public interface DataImportLogService extends BaseService {

    /**
     * 记录日志
     *
     * @param iexportData
     */
    void log(IexportData iexportData);

    /**
     * 记录日志
     *
     * @param mongoFileEntity
     * @param importIds
     */
    void log(MongoFileEntity mongoFileEntity, String importIds);

}
