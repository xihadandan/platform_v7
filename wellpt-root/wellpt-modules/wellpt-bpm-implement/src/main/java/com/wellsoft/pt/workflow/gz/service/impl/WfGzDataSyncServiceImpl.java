/*
 * @(#)2015-07-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.service.impl;

import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.workflow.gz.entity.WfGzDataSync;
import com.wellsoft.pt.workflow.gz.entity.WfGzDataSyncHis;
import com.wellsoft.pt.workflow.gz.service.WfGzDataSyncHisService;
import com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService;
import com.wellsoft.pt.workflow.gz.support.WfGzDataConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-07-16.1	zhulh		2015-07-16		Create
 * </pre>
 * @date 2015-07-16
 */
@Service
@Transactional
public class WfGzDataSyncServiceImpl extends BaseServiceImpl implements WfGzDataSyncService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private WfGzDataSyncHisService wfGzDataSyncHisService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService#get(java.lang.String)
     */
    @Override
    public WfGzDataSync get(String uuid) {
        return this.getCommonDao().get(WfGzDataSync.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService#getAll()
     */
    @Override
    public List<WfGzDataSync> getAll() {
        return this.getCommonDao().getAll(WfGzDataSync.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService#getAll()
     */
    @Override
    public List<WfGzDataSync> getAll(String orderBy) {
        return this.getCommonDao().getAll(WfGzDataSync.class, orderBy);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService#findByExample(WfGzDataSync)
     */
    @Override
    public List<WfGzDataSync> findByExample(WfGzDataSync example) {
        return this.getCommonDao().findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService#findByExampleByModifyTimeAsc(com.wellsoft.pt.workflow.gz.entity.WfGzDataSync)
     */
    @Override
    public List<WfGzDataSync> findByExampleByModifyTimeAsc(WfGzDataSync example) {
        return this.getCommonDao().findByExample(example, "modifyTime asc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService#save(com.wellsoft.pt.workflow.gz.entity.WfGzDataSync)
     */
    @Override
    public void save(WfGzDataSync entity) {
        this.getCommonDao().save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<WfGzDataSync> entities) {
        this.getCommonDao().saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.getCommonDao().deleteByPk(WfGzDataSync.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.WfGzDataSyncService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<WfGzDataSync> entities) {
        this.getCommonDao().deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService#remove(WfGzDataSync)
     */
    @Override
    public void remove(WfGzDataSync entity) {
        this.getCommonDao().delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.getCommonDao().deleteAllByPk(WfGzDataSync.class, list);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService#copyTo(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void copyTo(String userId, String taskInstUuid, String sourceTenantId, String targetTenantId) {
        String sourceFlowInstUuid = taskService.getTask(taskInstUuid).getFlowInstance().getUuid();
        WfGzDataSyncHis example = new WfGzDataSyncHis();
        example.setSourceFlowInstUuid(sourceFlowInstUuid);
        List<WfGzDataSyncHis> wfGzDataSyncHis = wfGzDataSyncHisService.findByExampleByModifyTimeAsc(example);

        WfGzDataSync wfGzDataSync = new WfGzDataSync();
        wfGzDataSync.setUserId(userId);
        wfGzDataSync.setTaskInstUuid(taskInstUuid);
        wfGzDataSync.setSourceFlowInstUuid(sourceFlowInstUuid);
        if (!wfGzDataSyncHis.isEmpty()) {
            wfGzDataSync.setTargetFlowInstUuid(wfGzDataSyncHis.get(0).getTargetFlowInstUuid());
        }
        wfGzDataSync.setSourceTenantId(sourceTenantId);
        wfGzDataSync.setTargetTenantId(targetTenantId);
        wfGzDataSync.setSuspensionState(WfGzDataConstant.STATE_COPY_TO);
        this.getCommonDao().save(wfGzDataSync);
    }

}
