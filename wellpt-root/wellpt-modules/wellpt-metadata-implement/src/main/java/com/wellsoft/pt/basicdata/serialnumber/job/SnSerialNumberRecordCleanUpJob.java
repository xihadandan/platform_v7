/*
 * @(#)10/24/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.job;

import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberRelationEntity;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberRelationService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.well.annotation.WellXxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 流水号记录清理任务
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/24/23.1	zhulh		10/24/23		Create
 * </pre>
 * @date 10/24/23
 */
@Component
public class SnSerialNumberRecordCleanUpJob {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SnSerialNumberRelationService serialNumberRelationService;

    /**
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("serialNumberRecordCleanUpJob")
    @WellXxlJob(jobDesc = "新版流水号记录清理任务",
            jobCron = "0 0 01 * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = true)
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("execute start");
        List<SnSerialNumberRelationEntity> serialNumberRelationEntities = serialNumberRelationService.listAll();
        for (SnSerialNumberRelationEntity serialNumberRelation : serialNumberRelationEntities) {
            // 删除不存在的流水号记录
            try {
                serialNumberRelationService.deleteAbsentRecord(serialNumberRelation);
            } catch (Exception e) {
                // 表已经不存在，记录错误日志
                String msg = String.format("流水号关系[%s]的对象[%s]或字段[%s]不存在", serialNumberRelation.getUuid(),
                        serialNumberRelation.getObjectName(), serialNumberRelation.getFieldName());
                logger.error(msg);
                XxlJobLogger.log(msg);
                XxlJobLogger.log(e);
            }
        }
        return ReturnT.SUCCESS;
    }

}
