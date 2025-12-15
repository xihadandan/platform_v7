/*
 * @(#)2013-4-10 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao;

import com.wellsoft.pt.bpm.engine.entity.TaskTransfer;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-10.1	rzhu		2013-4-10		Create
 * </pre>
 * @date 2013-4-10
 */
public interface TaskTransferDao extends JpaDao<TaskTransfer, String> {

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
}
