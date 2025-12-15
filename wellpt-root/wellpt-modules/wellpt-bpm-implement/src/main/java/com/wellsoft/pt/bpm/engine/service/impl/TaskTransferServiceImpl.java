/*
 * @(#)2018年4月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskTransferDao;
import com.wellsoft.pt.bpm.engine.entity.TaskTransfer;
import com.wellsoft.pt.bpm.engine.service.TaskTransferService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

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
@Service
public class TaskTransferServiceImpl extends AbstractJpaServiceImpl<TaskTransfer, TaskTransferDao, String> implements
        TaskTransferService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTransferService#isTransfering(java.lang.String)
     */
    @Override
    public boolean isTransfering(String taskInstUuid) {
        return dao.isTransfering(taskInstUuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTransferService#isTransfering(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isTransfering(String taskInstUuid, String agent) {
        return dao.isTransfering(taskInstUuid, agent);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTransferService#getTransfer(java.lang.String, java.lang.String)
     */
    @Override
    public TaskTransfer getTransfer(String taskInstUuid, String agent) {
        return dao.getTransfer(taskInstUuid, agent);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTransferService#getTransferCreators(java.lang.String)
     */
    @Override
    public List<String> getTransferCreators(String taskInstUuid) {
        return dao.getTransferCreators(taskInstUuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTransferService#findByExample(com.wellsoft.pt.bpm.engine.entity.TaskTransfer)
     */
    @Override
    public List<TaskTransfer> findByExample(TaskTransfer example) {
        return dao.listByEntity(example);
    }
}
