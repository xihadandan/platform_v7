/*
 * @(#)2019-08-10 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.service;


import com.wellsoft.pt.di.dao.DiDataInterationLogDao;
import com.wellsoft.pt.di.entity.DiDataInterationLogEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Date;

/**
 * Description: 数据库表DI_DATA_INTERATION_LOG的service服务接口
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-08-10.1	chenq		2019-08-10		Create
 * </pre>
 * @date 2019-08-10
 */
public interface DiDataInterationLogService extends
        JpaService<DiDataInterationLogEntity, DiDataInterationLogDao, String> {


    void saveBeginLog(String diConfigUuid, String exchangeId);

    void saveEndLog(String exchangeId, Date beginDate, Date endDate, Integer pageIndex,
                    Integer totalPage,
                    Integer pageLimit);


    Date getLatestDataTimeByDiConfigUuid(String diConfigUuid);

    DiDataInterationLogEntity getLatestDataLogByDiConfigUuid(String diConfigUuid);

    void saveException(Exception exception, String exchangeId);
}
