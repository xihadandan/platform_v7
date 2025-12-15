/*
 * @(#)2018年4月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskTransferDao;
import com.wellsoft.pt.bpm.engine.entity.TaskTransfer;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 2018年4月9日.1	chenqiong		2018年4月9日		Create
 * </pre>
 * @date 2018年4月9日
 */
public interface TaskTransferService extends JpaService<TaskTransfer, TaskTransferDao, String> {

    /**
     * 判断任务是否在转办中
     *
     * @param taskInstUuid
     * @return
     */
    boolean isTransfering(String taskInstUuid);

    /**
     * 判断任务是否为代办人的任务
     *
     * @param uuid
     * @param agent
     * @return
     */
    boolean isTransfering(String taskInstUuid, String agent);

    /**
     * @param taskInstUuid
     * @param agent
     * @return
     */
    TaskTransfer getTransfer(String taskInstUuid, String agent);

    /**
     * 获取已存在的转办发起人
     *
     * @param taskInstUuid
     * @return
     */
    List<String> getTransferCreators(String taskInstUuid);

    /**
     * 如何描述该方法
     *
     * @param example
     * @return
     */
    List<TaskTransfer> findByExample(TaskTransfer example);

}
