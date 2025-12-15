/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgElementLeaderDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgElementLeader;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.MultiOrgElementLeaderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
@Service
public class MultiOrgElementLeaderServiceImpl extends AbstractJpaServiceImpl<MultiOrgElementLeader, MultiOrgElementLeaderDao, String> implements
        MultiOrgElementLeaderService {

    @Override
    public List<MultiOrgElementLeader> addLeaderListByType(String eleId, String eleOrgVersionId, String ids, String names, Integer leaderType) {
        String[] pathArray = ids.split(";");
        // String[] nameArray = names.split(";");
        ArrayList<MultiOrgElementLeader> list = new ArrayList<MultiOrgElementLeader>();
        for (int i = 0; i < pathArray.length; i++) {
            MultiOrgElementLeader m = new MultiOrgElementLeader();
            String idPath = pathArray[i];
            String[] idAndVer = idPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            String objId = idAndVer[1];
            m.setEleId(eleId);
            m.setEleOrgVersionId(eleOrgVersionId);
            m.setTargetObjId(objId);
            // m.setTargetObjName(nameArray[i]);
            m.setLeaderType(leaderType);
            m.setTargetObjOrgVersionId(idAndVer[0]);
            list.add(m);
        }
        this.saveAll(list);
        return list;
    }

    @Override
    public List<MultiOrgElementLeader> queryLeaderListByType(String eleId, String eleOrgVersionId, Integer leaderType) {
        MultiOrgElementLeader q = new MultiOrgElementLeader();
        q.setEleId(eleId);
        q.setEleOrgVersionId(eleOrgVersionId);
        q.setLeaderType(leaderType);
        return this.dao.listByEntity(q);
    }

    @Override
    public boolean deleteLeaderList(String eleId, String eleOrgVersionId) {
        MultiOrgElementLeader q = new MultiOrgElementLeader();
        q.setEleId(eleId);
        q.setEleOrgVersionId(eleOrgVersionId);
        List<MultiOrgElementLeader> objs = this.dao.listByEntity(q);
        if (!CollectionUtils.isEmpty(objs)) {
            this.deleteByEntities(objs);
        }
        return true;
    }

    // 获取我的分管下属
    @Override
    public List<MultiOrgElementLeader> queryMyBranchUnderlingListByEleId(String jobId, String eleOrgVerisonId) {
        MultiOrgElementLeader q = new MultiOrgElementLeader();
        q.setTargetObjId(jobId);
        if (StringUtils.isNotBlank(eleOrgVerisonId)) {
            q.setTargetObjOrgVersionId(eleOrgVerisonId);
        }
        q.setLeaderType(MultiOrgElementLeader.TYPE_BRANCHED_LEADER);
        return this.dao.listByEntity(q);
    }

    // 获取我负责的下属
    @Override
    public List<MultiOrgElementLeader> queryMyBossUnderlingListByEleId(String jobId, String eleOrgVersionId) {
        // 我负责的的节点，刚好跟我分管的逻辑是倒过来的
        // eleId代表的是下属，targetObjId是领导节点
        MultiOrgElementLeader q = new MultiOrgElementLeader();
        q.setTargetObjId(jobId);
        q.setEleOrgVersionId(eleOrgVersionId);
        q.setLeaderType(MultiOrgElementLeader.TYPE_BOSS);
        return this.dao.listByEntity(q);
    }

    // 获取指定节点的分管领导
    @Override
    public List<MultiOrgElementLeader> queryMyBranchLeaderListByEleId(String eleId) {
        MultiOrgElementLeader q = new MultiOrgElementLeader();
        q.setEleId(eleId);
        q.setLeaderType(MultiOrgElementLeader.TYPE_BRANCHED_LEADER);
        return this.dao.listByEntity(q);
    }

    // 获取指定节点的负责人
    @Override
    public List<MultiOrgElementLeader> queryMyBossLeaderListByEleId(String eleId, String eleOrgVersionId) {
        // 负责人
        // eleId代表的是下属，targetObjId是领导
        MultiOrgElementLeader q = new MultiOrgElementLeader();
        q.setEleId(eleId);
        q.setEleOrgVersionId(eleOrgVersionId);
        q.setLeaderType(MultiOrgElementLeader.TYPE_BOSS);
        return this.dao.listByEntity(q);
    }

    // 复制一份数据，并且将targetId 改成 newEleId,
    // 并且将targetObjOrgVersionId,变更为当前的orgVersionId
    @Override
    public void dealElementIdChangeEvent(String oldEleId, String newEleId, String orgVersionId) {
        MultiOrgElementLeader q = new MultiOrgElementLeader();
        q.setTargetObjId(oldEleId);
        q.setEleOrgVersionId(orgVersionId);
        List<MultiOrgElementLeader> objs = this.dao.listByEntity(q);
        if (!CollectionUtils.isEmpty(objs)) {
            for (MultiOrgElementLeader obj : objs) {
                obj.setTargetObjId(newEleId);
                obj.setTargetObjOrgVersionId(orgVersionId);
                this.save(obj);
            }
        }

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.service.MultiOrgElementLeaderService#queryLeaderByEleOrgVersionId(java.lang.String)
     */
    @Override
    public List<MultiOrgElementLeader> queryLeaderByEleOrgVersionId(String id) {
        MultiOrgElementLeader q = new MultiOrgElementLeader();
        q.setEleOrgVersionId(id);
        return this.dao.listByEntity(q);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.service.MultiOrgElementLeaderService#deleteAllLeaderByEleId(java.lang.String)
     */
    @Override
    public void deleteAllLeaderByEleId(String eleId, String orgVersionId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("eleId", eleId);
        params.put("versionId", orgVersionId);
        this.dao.updateByNamedSQL("deleteAllLeaderByEleId", params);
    }

}
