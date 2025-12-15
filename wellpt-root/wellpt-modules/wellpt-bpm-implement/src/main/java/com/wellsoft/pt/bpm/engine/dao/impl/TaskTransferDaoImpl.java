/*
 * @(#)2013-4-10 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskTransferDao;
import com.wellsoft.pt.bpm.engine.entity.TaskTransfer;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Repository
public class TaskTransferDaoImpl extends AbstractJpaDaoImpl<TaskTransfer, String> implements TaskTransferDao {

    private static final String IS_TRANSFER = "select count(*) from TaskTransfer task_transfer where task_transfer.taskInstUuid = :taskInstUuid and task_transfer.parent.uuid is null";

    private static final String IS_TRANSFER_BY_USER = "select count(*) from TaskTransfer task_transfer where task_transfer.taskInstUuid = :taskInstUuid and task_transfer.agent = :agent";

    private static final String GET_TRANSFER = "from TaskTransfer task_transfer where task_transfer.taskInstUuid = :taskInstUuid and task_transfer.agent = :agent";

    // 获取已存在的转办发起人
    private static final String QUERY_CREATOR = "select distinct task_transfer.owner from TaskTransfer task_transfer where task_transfer.taskInstUuid = :taskInstUuid";

    /**
     * 判断任务是否在转办中
     *
     * @param taskInstUuid
     * @return
     */
    public boolean isTransfering(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return (Long) this.getNumberByHQL(IS_TRANSFER, values) > 0;
    }

    /**
     * 判断任务是否为代办人的任务
     *
     * @param uuid
     * @param agent
     * @return
     */
    public boolean isTransfering(String taskInstUuid, String agent) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("agent", agent);
        return (Long) this.getNumberByHQL(IS_TRANSFER_BY_USER, values) > 0;
    }

    /**
     * @param taskInstUuid
     * @param agent
     * @return
     */
    public TaskTransfer getTransfer(String taskInstUuid, String agent) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("agent", agent);
        return this.getOneByHQL(GET_TRANSFER, values);
    }

    /**
     * 获取已存在的转办发起人
     *
     * @param taskInstUuid
     * @return
     */
    public List<String> getTransferCreators(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return this.listCharSequenceByHQL(QUERY_CREATOR, values);
    }

}
