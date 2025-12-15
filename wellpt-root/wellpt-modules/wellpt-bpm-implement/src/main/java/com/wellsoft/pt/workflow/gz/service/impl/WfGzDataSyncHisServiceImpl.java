/*
 * @(#)2015-07-17 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.service.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.workflow.gz.entity.WfGzDataSyncHis;
import com.wellsoft.pt.workflow.gz.service.WfGzDataSyncHisService;
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
 * 2015-07-17.1	zhulh		2015-07-17		Create
 * </pre>
 * @date 2015-07-17
 */
@Service
@Transactional
public class WfGzDataSyncHisServiceImpl extends BaseServiceImpl implements WfGzDataSyncHisService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncHisService#get(java.lang.String)
     */
    @Override
    public WfGzDataSyncHis get(String uuid) {
        return this.getCommonDao().get(WfGzDataSyncHis.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncHisService#getAll()
     */
    @Override
    public List<WfGzDataSyncHis> getAll() {
        return this.getCommonDao().getAll(WfGzDataSyncHis.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncHisService#findByExample(WfGzDataSyncHis)
     */
    @Override
    public List<WfGzDataSyncHis> findByExample(WfGzDataSyncHis example) {
        return this.getCommonDao().findByExample(example);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncHisService#findByExampleByModifyTimeAsc(com.wellsoft.pt.workflow.gz.entity.WfGzDataSync)
     */
    @Override
    public List<WfGzDataSyncHis> findByExampleByModifyTimeAsc(WfGzDataSyncHis example) {
        return this.getCommonDao().findByExample(example, "modifyTime asc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncHisService#save(com.wellsoft.pt.workflow.gz.entity.WfGzDataSyncHis)
     */
    @Override
    public void save(WfGzDataSyncHis entity) {
        this.getCommonDao().save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncHisService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<WfGzDataSyncHis> entities) {
        this.getCommonDao().saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncHisService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.getCommonDao().deleteByPk(WfGzDataSyncHis.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.WfGzDataSyncHisService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<WfGzDataSyncHis> entities) {
        this.getCommonDao().deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncHisService#remove(WfGzDataSyncHis)
     */
    @Override
    public void remove(WfGzDataSyncHis entity) {
        this.getCommonDao().delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.WfGzDataSyncHisService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.getCommonDao().deleteAllByPk(WfGzDataSyncHis.class, list);
    }

}
