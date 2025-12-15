/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgJobRankDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobRank;
import com.wellsoft.pt.multi.org.service.MultiOrgJobRankService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class MultiOrgJobRankServiceImpl extends AbstractJpaServiceImpl<MultiOrgJobRank, MultiOrgJobRankDao, String>
        implements MultiOrgJobRankService {
    @Override
    public MultiOrgJobRank getById(String id) {
        MultiOrgJobRank q = new MultiOrgJobRank();
        q.setId(id);
        List<MultiOrgJobRank> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public Select2QueryData queryJobRankListForSelect2(Select2QueryInfo select2QueryInfo) {
        String systemUnitId = select2QueryInfo.getOtherParams("systemUnitId", "");
        String name = select2QueryInfo.getOtherParams("name", "");
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("systemUnitId", systemUnitId);
        params.put("name", name);
        List<MultiOrgJobRank> list = this.dao.listByNameSQLQuery("queryJobRankListForSelect2", params,
                select2QueryInfo.getPagingInfo());
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

    @Override
    public List<MultiOrgJobRank> queryJobRankListByParam(Map<String, Object> param) {
        param.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        return listByNameSQLQuery("queryJobRankListByParam", param);
    }

    @Override
    public List<Integer> getJobGradeByJobRankId(String... jobRankId) {
        return getJobGradeByJobRankId("ASC", jobRankId);
    }

    @Override
    public List<Integer> getJobGradeByJobRankId(String order, String... jobRankId) {
        List<MultiOrgJobRank> multiOrgJobRanks = getMultiOrgJobRankByJobRankId(order, jobRankId);
        List<Integer> collect = multiOrgJobRanks.stream().map(entity -> entity.getJobGrade())
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<MultiOrgJobRank> getMultiOrgJobRankByJobRankId(String order, String... jobRank) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", jobRank);
        if (StringUtils.isBlank(order)) {
            order = "ASC";
        }
        return dao.listByHQL(
                "FROM MultiOrgJobRank WHERE  id in (:id) order by jobGrade " + order,
                param);
    }

    @Override
    public List<MultiOrgJobRank> getMultiOrgJobRankByJobRankId(String... jobRankId) {
        return getMultiOrgJobRankByJobRankId("ASC", jobRankId);
    }

    @Override
    public long countByDutySeqUuid(String dutySeqUuid) {
        MultiOrgJobRank entity = new MultiOrgJobRank();
        entity.setDutySeqUuid(dutySeqUuid);
        return dao.countByEntity(entity);
    }

    @Override
    public List<MultiOrgJobRank> getMultiOrgJobRankDetailByDutySeqUuid(String dutySeqUuid) {
        MultiOrgJobRank entity = new MultiOrgJobRank();
        entity.setDutySeqUuid(dutySeqUuid);
        return this.dao.listByEntity(entity);
    }

    @Override
    public long countBySystemUnitIds(List<String> systemUnitIds) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("unitIds", systemUnitIds);
        long count = this.dao.countByHQL("from MultiOrgJobRank where systemUnitId in (:unitIds)", query);
        return count;
    }
}
