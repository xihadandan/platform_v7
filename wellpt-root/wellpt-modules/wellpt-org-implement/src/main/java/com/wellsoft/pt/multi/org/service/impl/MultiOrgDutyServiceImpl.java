/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgDutyDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgDuty;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobDuty;
import com.wellsoft.pt.multi.org.service.MultiOrgDutyService;
import com.wellsoft.pt.multi.org.service.MultiOrgJobDutyService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class MultiOrgDutyServiceImpl extends AbstractJpaServiceImpl<MultiOrgDuty, MultiOrgDutyDao, String> implements
        MultiOrgDutyService {

    @Autowired
    MultiOrgJobDutyService multiOrgJobDutyService;

    @Override
    public MultiOrgDuty getById(String dutyId) {
        MultiOrgDuty q = new MultiOrgDuty();
        q.setId(dutyId);
        List<MultiOrgDuty> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public MultiOrgDuty getByJobId(String jobId) {
        MultiOrgJobDuty jobDuty = multiOrgJobDutyService.getJobDutyByJobId(jobId);
        if (jobDuty != null) {
            return getById(jobDuty.getDutyId());
        }
        return null;
    }

    @Override
    public Select2QueryData queryDutyListForSelect2(Select2QueryInfo select2QueryInfo) {
        String systemUnitId = select2QueryInfo.getOtherParams("systemUnitId", "");
        String name = select2QueryInfo.getOtherParams("name", "");
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("systemUnitId", systemUnitId);
        params.put("name", name);

        List<MultiOrgDuty> list = this.dao.listByNameSQLQuery("queryDutyListForSelect2", params,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

    @Override
    public MultiOrgDuty getByName(String name, String systemUnitId) {
        MultiOrgDuty q = new MultiOrgDuty();
        q.setName(name);
        q.setSystemUnitId(systemUnitId);
        List<MultiOrgDuty> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public List<MultiOrgDuty> queryAllDutyBySystemUnitId(String systemUnitId) {
        return this.dao.listByFieldEqValue("systemUnitId", systemUnitId);
    }

    @Override
    public List<MultiOrgDuty> getDutysByIds(List<String> ids) {
        return this.dao.listByFieldInValues("id", ids);
    }

    @Override
    public long countByDutySeqUuid(String dutySeqUuid) {
        MultiOrgDuty entity = new MultiOrgDuty();
        entity.setDutySeqUuid(dutySeqUuid);
        return dao.countByEntity(entity);
    }

    @Override
    public long countBySystemUnitIds(List<String> systemUnitIds) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("unitIds", systemUnitIds);
        long count = this.dao.countByHQL("from MultiOrgDuty where systemUnitId in (:unitIds)", query);
        return count;
    }

    /**
     * @param system
     * @param systemUnitId
     * @return
     */
    @Override
    public List<MultiOrgDuty> listBySystemOrSystemUnitId(String system, String systemUnitId) {
        String hql = "from MultiOrgDuty t where 1 = 1 ";
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        params.put("systemUnitId", systemUnitId);
        if (StringUtils.isNotBlank(system) || StringUtils.isNotBlank(systemUnitId)) {
            List<String> conditions = Lists.newArrayList();
            if (StringUtils.isNotBlank(system)) {
                conditions.add("t.system = :system");
            }
            if (StringUtils.isNotBlank(systemUnitId)) {
                conditions.add("t.systemUnitId = :systemUnitId");
            }
            hql += "and (" + StringUtils.join(conditions, " or ") + ")";
        } else {
            hql += "and 1 = 2";
        }
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<MultiOrgDuty> listByJobIds(List<String> jobIds) {
        if (CollectionUtils.isEmpty(jobIds)) {
            return Collections.emptyList();
        }

        String hql = "from MultiOrgDuty t where t.id in (select jd.dutyId from MultiOrgJobDuty jd where jd.jobId in (:jobIds))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("jobIds", jobIds);
        return this.dao.listByHQL(hql, params);
    }

}
