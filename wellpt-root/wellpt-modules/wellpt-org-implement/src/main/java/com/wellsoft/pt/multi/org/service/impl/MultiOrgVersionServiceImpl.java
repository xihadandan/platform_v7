/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgVersionDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.service.MultiOrgVersionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
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
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
@Service
public class MultiOrgVersionServiceImpl extends AbstractJpaServiceImpl<MultiOrgVersion, MultiOrgVersionDao, String>
        implements MultiOrgVersionService {
    public List<MultiOrgVersion> queryChildrenVersionByUuid(String uuid) {
        MultiOrgVersion q = new MultiOrgVersion();
        q.setSourceVersionUuid(uuid);
        return this.dao.listByEntity(q);
    }

    @Override
    public List<MultiOrgVersion> queryHistoryVersionOfOrg(String maxVersionUuid) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        MultiOrgVersion maxVer = this.getOne(maxVersionUuid);
        params.put("systemUnitId", maxVer.getSystemUnitId());
        params.put("version", maxVer.getVersion());
        params.put("functionType", maxVer.getFunctionType());
        params.put("rootVersionId", maxVer.getRootVersionId());
        return listByNameSQLQuery("queryHistoryVersionOfOrg", params);
    }

    /**
     * 生成一个新的版本号
     */
    @Override
    public String createNewVersionNum(String systemUnitId, String rootVersionId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("systemUnitId", systemUnitId);
        params.put("rootVersionId", rootVersionId);
        List<MultiOrgVersion> objs = listByNameSQLQuery("getMaxVersionOfOrgBySystemUnitIdAndRootVersionId", params);
        if (CollectionUtils.isEmpty(objs)) {
            return "1.0";
        }
        String max = objs.get(0).getVersion();
        Double d = new Double(max);
        return String.format("%.1f", d + 0.1);
    }

    @Override
    public List<MultiOrgVersion> queryCurrentActiveVersionListOfAllUnit() {
        MultiOrgVersion q = new MultiOrgVersion();
        q.setStatus(1);
        return this.dao.listByEntity(q, null, "systemUnitId asc", null);
    }

    @Override
    public List<MultiOrgVersion> queryCurrentActiveVersionListOfSystemUnit(String systemUnitId) {
        MultiOrgVersion q = new MultiOrgVersion();
        if (StringUtils.isNotBlank(systemUnitId)) {
            q.setSystemUnitId(systemUnitId);
        }
        q.setStatus(1);
        List<MultiOrgVersion> multiOrgVersionList = this.dao.listByEntity(q, null, "isDefault desc", null);
        return multiOrgVersionList;
    }

    @Override
    public MultiOrgVersion enabledByDefault(String unitId) {
        MultiOrgVersion q = new MultiOrgVersion();
        q.setSystemUnitId(unitId);
        q.setStatus(1);
        q.setIsDefault(true);
        List<MultiOrgVersion> list = this.listByEntity(q);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<MultiOrgVersion> queryMaxVersionOfAllOrg() {
        return listByNameSQLQuery("queryMaxVersionOfAllOrg", null);
    }

    @Override
    public List<MultiOrgVersion> queryAllVersionListBySystemUnitId(String id) {
        MultiOrgVersion q = new MultiOrgVersion();
        q.setSystemUnitId(id);
        return this.dao.listByEntity(q);
    }

    @Override
    public List<MultiOrgVersion> queryCurrentActiveVersionListOfUnitAndRootVersionId(String systemUnitId,
                                                                                     String rootVersionId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("systemUnitId", systemUnitId);
        params.put("rootVersionId", rootVersionId);
        return listByNameSQLQuery("queryCurrentActiveVersionListOfUnitAndRootVersionId", params);
    }

    @Override
    public MultiOrgVersion getById(String orgVersionId) {
        MultiOrgVersion q = new MultiOrgVersion();
        q.setId(orgVersionId);
        List<MultiOrgVersion> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public List<MultiOrgVersion> findByExample(MultiOrgVersion q) {
        return this.dao.listByEntity(q);
    }

    @Override
    public List<MultiOrgVersion> queryByUserId(String userId) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("userId", userId);
        String hql = "select distinct a.orgVersionId from MultiOrgUserTreeNode a where a.userId=:userId";
        List<String> verIds = this.dao.listCharSequenceByHQL(hql, query);
        query.put("ids", verIds);
        String hql2 = "from MultiOrgVersion where id in (:ids) and status=1";
        List<MultiOrgVersion> versions = this.listByHQL(hql2, query);
        return versions;
    }

    @Override
    @Transactional
    public void updateUnDefault(String systemUnitId) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("unitId", systemUnitId);
        this.dao.updateByHQL("update MultiOrgVersion set isDefault=false where isDefault=true and systemUnitId=:unitId",
                query);
    }

    @Override
    public long countBySystemUnitId(String systemUnitId) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("unitId", systemUnitId);
        long count = this.dao.countByHQL("from MultiOrgVersion where systemUnitId=:unitId", query);
        return count;
    }

    @Override
    public long countBySystemUnitIds(List<String> systemUnitIds) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("unitIds", systemUnitIds);
        long count = this.dao.countByHQL("from MultiOrgVersion where systemUnitId in (:unitIds)", query);
        return count;
    }
}
