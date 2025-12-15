/*
 * @(#)2019-08-10 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.service.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.wellsoft.pt.di.dao.DiDataInterationLogDao;
import com.wellsoft.pt.di.entity.DiDataInterationLogEntity;
import com.wellsoft.pt.di.service.DiDataInterationLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表DI_DATA_INTERATION_LOG的service服务接口实现类
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
@Service
public class DiDataInterationLogServiceImpl extends
        AbstractJpaServiceImpl<DiDataInterationLogEntity, DiDataInterationLogDao, String> implements
        DiDataInterationLogService {


    @Override
    @Transactional
    public void saveBeginLog(String diConfigUuid, String exchangeId) {
        DiDataInterationLogEntity log = new DiDataInterationLogEntity();
        log.setDiConfigUuid(diConfigUuid);
        log.setExchangeId(exchangeId);
        log.setStatus(0);
        log.setIsLatest(false);
        save(log);
    }


    @Override
    @Transactional
    public void saveEndLog(String exchangeId, Date beginDate, Date endDate, Integer pageIndex,
                           Integer totalPage,
                           Integer pageLimit) {

        List<DiDataInterationLogEntity> list = this.dao.listByFieldEqValue("exchangeId",
                exchangeId);

        if (CollectionUtils.isNotEmpty(list)) {
            DiDataInterationLogEntity entity = list.get(0);

            DiDataInterationLogEntity example = new DiDataInterationLogEntity();
            example.setIsLatest(true);
            example.setDiConfigUuid(entity.getDiConfigUuid());

            //查询最近的一条交换日志，置为“非最近”状态
            List<DiDataInterationLogEntity> latests = this.dao.listByEntity(example);
            if (CollectionUtils.isNotEmpty(latests)) {
                latests.get(0).setIsLatest(false);
                save(latests.get(0));
            }

            entity.setStatus(1);
            entity.setDataBeginTime(beginDate);
            entity.setDataEndTime(endDate);
            entity.setIsLatest(true);
            entity.setPageIndex(pageIndex);
            entity.setPageLimit(pageLimit);
            entity.setTotalPage(totalPage);
            save(entity);
        }
    }


    @Override
    public Date getLatestDataTimeByDiConfigUuid(String diConfigUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("diUuid", diConfigUuid);
        List list = this.listByHQL(
                "select dataEndTime from DiDataInterationLogEntity where diConfigUuid=:diUuid and isLatest=true",
                param);
        return CollectionUtils.isNotEmpty(list) ? (Date) list.get(0) : null;
    }

    @Override
    public DiDataInterationLogEntity getLatestDataLogByDiConfigUuid(String diConfigUuid) {
        DiDataInterationLogEntity example = new DiDataInterationLogEntity();
        example.setDiConfigUuid(diConfigUuid);
        example.setIsLatest(true);
        List<DiDataInterationLogEntity> logEntities = this.dao.listByEntity(example);
        return CollectionUtils.isNotEmpty(logEntities) ? logEntities.get(0) : null;
    }

    @Override
    @Transactional
    public void saveException(Exception exception, String exchangeId) {
        List<DiDataInterationLogEntity> list = this.dao.listByFieldEqValue("exchangeId",
                exchangeId);
        if (CollectionUtils.isNotEmpty(list)) {
            list.get(0).setException(Throwables.getStackTraceAsString(exception));
            list.get(0).setStatus(-1);
            save(list.get(0));
        }
    }

}
