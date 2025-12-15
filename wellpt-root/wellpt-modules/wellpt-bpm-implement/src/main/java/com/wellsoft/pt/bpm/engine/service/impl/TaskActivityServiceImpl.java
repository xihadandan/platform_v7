/*
 * @(#)2013-4-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.bpm.engine.dao.TaskActivityDao;
import com.wellsoft.pt.bpm.engine.entity.TaskActivity;
import com.wellsoft.pt.bpm.engine.query.TaskActivityQueryItem;
import com.wellsoft.pt.bpm.engine.service.TaskActivityService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-6.1	zhulh		2013-4-6		Create
 * </pre>
 * @date 2013-4-6
 */
@Service
public class TaskActivityServiceImpl extends AbstractJpaServiceImpl<TaskActivity, TaskActivityDao, String> implements
        TaskActivityService {

    private static final String GET_ALL_ACTIVITY_BY_TASK_INST_UUID = "from TaskActivity t1 where exists (select t2.uuid from TaskActivity t2 where t1.flowInstUuid = t2.flowInstUuid and t2.taskInstUuid = :taskInstUuid)";

    private static final String GET_ALL_ACTIVITY_BY_FLOW_INST_UUID = "from TaskActivity t1 where t1.flowInstUuid = :flowInstUuid";

    private static final String REMOVE_BY_FLOW_INST_UUID = "delete from TaskActivity t1 where t1.flowInstUuid = :flowInstUuid";

    @Autowired
    private TaskActivityDao dao;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskActivityService#get(java.lang.String)
     */
    @Override
    public TaskActivity get(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskActivityService#getByTaskInstUuid(java.lang.String)
     */
    @Override
    public TaskActivity getByTaskInstUuid(String taskInstUuid) {
        List<TaskActivity> taskActivityList = this.dao.listByFieldEqValue("taskInstUuid", taskInstUuid);
        if (CollectionUtils.isEmpty(taskActivityList)) {
            return null;
        }
        return taskActivityList.get(0);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskActivityService#findByExample(com.wellsoft.pt.bpm.engine.entity.TaskActivity)
     */
    @Override
    public List<TaskActivity> findByExample(TaskActivity taskActivity) {
        return dao.listByEntity(taskActivity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskActivityService#getHistoryActivities(java.lang.String)
     */
    @Override
    public List<TaskActivity> getHistoryActivities(String flowInstUuid) {
        return dao.getHistoryActivities(flowInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskActivityService#getByParallelTaskInstUuid(java.lang.String)
     */
    @Override
    public List<TaskActivity> getByParallelTaskInstUuid(String parallelTaskInstUuid, String flowInstUuid) {
        String hql = "from TaskActivity t1 where t1.flowInstUuid = :flowInstUuid and exists(select t2.uuid from TaskInstance t2 where t1.taskInstUuid = t2.uuid and (t2.uuid = :parallelTaskInstUuid or t2.parallelTaskInstUuid = :parallelTaskInstUuid)) order by t1.createTime desc";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        values.put("parallelTaskInstUuid", parallelTaskInstUuid);
        return dao.listByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskActivityService#isStartedTaskInstance(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isStartedTaskInstance(String flowInstUuid, String taskId) {
        String hql = "select count(uuid) from TaskActivity t where t.flowInstUuid = :flowInstUuid and t.taskId = :taskId and t.preTaskId is null and t.endTime is not null";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        values.put("taskId", taskId);
        Long count = dao.getNumberByHQL(hql, values);
        return count >= 1;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskActivityService#countByFlowInstUuid(java.lang.String)
     */
    @Override
    public Long countByFlowInstUuid(String flowInstUuid) {
        String hql = "select count(uuid) from TaskActivity t where t.flowInstUuid = :flowInstUuid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return dao.getNumberByHQL(hql, values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskActivityService#getAllActivityByTaskInstUuid2(java.lang.String)
     */
    @Override
    public List<TaskActivityQueryItem> getAllActivityByTaskInstUuid(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return this.dao.listItemByNameHQLQuery("getAllActivityByTaskInstUuid", TaskActivityQueryItem.class, values,
                null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskActivityService#getAllActivityByFlowInstUuid2(java.lang.String)
     */
    @Override
    public List<TaskActivityQueryItem> getAllActivityByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listItemByNameHQLQuery("getAllActivityByFlowInstUuid", TaskActivityQueryItem.class, values,
                null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskActivityService#getByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskActivity> getByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listByHQL(GET_ALL_ACTIVITY_BY_FLOW_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskActivityService#removeByFlowInstUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void removeByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        this.dao.deleteByHQL(REMOVE_BY_FLOW_INST_UUID, values);
    }

    @Override
    public String getPreTaskId(String flowInstUuid, String taskId) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        values.put("taskId", taskId);
        List<TaskActivity> list = this.dao.listByNameSQLQuery("getPreTaskId", values);
        if (list.size() == 0) {
            return null;
        } else {
            return list.get(0).getPreTaskId();
        }

    }

    @Override
    public TaskActivity getByCreatorAndPreTaskInstUuid(String creator, String preTaskInstUuid) {
        if (StringUtils.isBlank(creator) || StringUtils.isBlank(preTaskInstUuid)) {
            return null;
        }
        String hql = "from TaskActivity t where t.creator = :creator and t.preTaskInstUuid = :preTaskInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("creator", creator);
        values.put("preTaskInstUuid", preTaskInstUuid);
        List<TaskActivity> list = this.dao.listByHQL(hql, values);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

}
