/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.pt.integration.dao.DataOperationLogDao;
import com.wellsoft.pt.integration.entity.DataOperationLog;
import com.wellsoft.pt.integration.service.DataOperationLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2018年4月16日.1	chenqiong		2018年4月16日		Create
 * </pre>
 * @date 2018年4月16日
 */
@Service
public class DataOperationLogServiceImpl extends AbstractJpaServiceImpl<DataOperationLog, DataOperationLogDao, String>
        implements DataOperationLogService {

    @Override
    public List<DataOperationLog> getDataOperationLogSynchronous() {
        return dao.getDataOperationLogSynchronous();
    }

    @Override
    @Transactional
    public Boolean saveDataOperationLogOfNotEntity(String tableName, String uuid, Integer action) {
        return dao.saveDataOperationLogOfNotEntity(tableName, uuid, action);
    }

}
