/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgJobDutyDao;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.service.*;
import com.wellsoft.pt.org.dto.OrgJobDutyDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class MultiOrgJobDutyServiceImpl extends AbstractJpaServiceImpl<MultiOrgJobDuty, MultiOrgJobDutyDao, String> implements MultiOrgJobDutyService {

    @Autowired
    private MultiOrgUserWorkInfoService multiOrgUserWorkInfoService;

    @Autowired
    private MultiOrgUserTreeNodeService multiOrgUserTreeNodeService;

    @Autowired
    private MultiOrgDutyService dutyService;

    @Autowired
    private MultiOrgJobRankService multiOrgJobRankService;

    @Autowired
    private MultiOrgJobLevelService multiOrgJobLevelService;

    // 删除职位的职务信息
    @Override
    public void deleteJobDutyByJobId(String jobId) {
        MultiOrgJobDuty q = new MultiOrgJobDuty();
        q.setJobId(jobId);
        List<MultiOrgJobDuty> objs = this.dao.listByEntity(q);
        if (!CollectionUtils.isEmpty(objs)) {
            this.deleteByEntities(objs);
        }
    }

    @Override
    public MultiOrgJobDuty getJobDutyByJobId(String jobId) {
        MultiOrgJobDuty q = new MultiOrgJobDuty();
        q.setJobId(jobId);
        List<MultiOrgJobDuty> objs = this.dao.listByEntity(q);
        if (!CollectionUtils.isEmpty(objs)) {
            return objs.get(0);
        }
        return null;
    }

    @Override
    public boolean isMemberOfSelectedJobDuty(String userId, Set<String> dutyIds, Set<String> jobIds) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("jobIds", jobIds);
        param.put("dutyIds", dutyIds);
        return this.dao.countBySQL("select 1 from multi_org_job_duty where duty_id in (:dutyIds) and job_id in (:jobIds)", param) > 0;
    }

    @Override
    public boolean isMemberOfAllJobDuty(String userId, Set<String> dutyIds) {
        MultiOrgUserWorkInfo workInfo = multiOrgUserWorkInfoService.getUserWorkInfo(userId);
        if (workInfo == null || StringUtils.isBlank(workInfo.getEleIdPaths())) {
            return false;
        }
        Set<String> jobIds = Sets.newHashSet(workInfo.getJobIds().split(Separator.SEMICOLON.getValue()));
        return isMemberOfSelectedJobDuty(userId, dutyIds, jobIds);
    }

    @Override
    public boolean isMemberOfMainJobDuty(String userId, Set<String> dutyIds) {
        MultiOrgUserWorkInfo workInfo = multiOrgUserWorkInfoService.getUserWorkInfo(userId);
        if (workInfo == null || StringUtils.isBlank(workInfo.getEleIdPaths())) {
            return false;
        }
        String jobPaths = workInfo.getEleIdPaths();
        for (String p : jobPaths.split(Separator.SEMICOLON.getValue())) {
            String[] slashs = p.split(Separator.SLASH.getValue());
            String vid = slashs[0];
            MultiOrgUserTreeNode treeNode = this.multiOrgUserTreeNodeService.queryUserJobByOrgVersionEleId(userId, vid, slashs[slashs.length - 1]);
            if (treeNode == null) {
                logger.error("无法查询到用户职位关联组织树信息，用户ID={}", userId);
                continue;
            }
            if (1 == treeNode.getIsMain()) {
                Set<String> set = Sets.newHashSet(slashs[slashs.length - 1]);
                return isMemberOfSelectedJobDuty(userId, dutyIds, set);
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void saveUpdateJobDutyByJobIdAndOrgVersionUuid(String jobId, String orgDutyId, Long orgVersionUuid) {
        MultiOrgJobDuty example = new MultiOrgJobDuty();
        example.setOrgVersionUuid(orgVersionUuid);
        example.setJobId(jobId);
        List<MultiOrgJobDuty> duties = listByEntity(example);
        if (StringUtils.isBlank(orgDutyId)) {
            // 职务置空，则删除职位与职务的关系
            if (CollectionUtils.isNotEmpty(duties)) {
                deleteByEntities(duties);
            }
        } else {
            // 职务不为空，无则新增，有则更新
            if (CollectionUtils.isNotEmpty(duties)) {
                duties.get(0).setDutyId(orgDutyId);
                save(duties.get(0));
            } else {
                example.setDutyId(orgDutyId);
                save(example);
            }

        }
    }

    @Override
    public MultiOrgJobDuty getJobDutyByJobIdAndOrgVersionUuid(String jobId, Long orgVersionUuid) {
        MultiOrgJobDuty example = new MultiOrgJobDuty();
        example.setOrgVersionUuid(orgVersionUuid);
        example.setJobId(jobId);
        List<MultiOrgJobDuty> duties = listByEntity(example);
        return CollectionUtils.isNotEmpty(duties) ? duties.get(0) : null;
    }

    @Override
    @Transactional
    public void deleteByJobIdAndOrgVersionUuid(String jobId, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("jobId", jobId);
        this.dao.deleteByHQL("delete from MultiOrgJobDuty where orgVersionUuid=:orgVersionUuid"
                + (StringUtils.isNotBlank(jobId) ? " AND jobId=:jobId" : ""), params);
    }

    @Override
    public List<MultiOrgJobDuty> listByOrgVersionUuid(Long orgVersionUuid) {
        return this.dao.listByFieldEqValue("orgVersionUuid", orgVersionUuid);
    }

    @Override
    @Transactional
    public void deleteByJobIdAndOrgVersionUuid(Long orgVersionUuid) {
        this.deleteByJobIdAndOrgVersionUuid(null, orgVersionUuid);
    }

    @Override
    public OrgJobDutyDto getJobDutyDetailsByJobIdAndOrgVersionUuid(String jobId, Long orgVersionUuid) {
        MultiOrgJobDuty example = new MultiOrgJobDuty();
        example.setOrgVersionUuid(orgVersionUuid);
        example.setJobId(jobId);
        List<MultiOrgJobDuty> duties = listByEntity(example);
        if (CollectionUtils.isNotEmpty(duties)) {
            OrgJobDutyDto dto = new OrgJobDutyDto();
            dto.setDutyId(duties.get(0).getDutyId());
            // 查职务
            MultiOrgDuty duty = dutyService.getById(dto.getDutyId());
            if (duty != null) {
                dto.setDutyName(duty.getName());
                if (StringUtils.isNotBlank(duty.getJobRank())) {
                    String[] rankIds = duty.getJobRank().split(",|;");
                    // 查职级
                    List<MultiOrgJobRank> ranks = multiOrgJobRankService.getMultiOrgJobRankByJobRankId(rankIds);
                    if (CollectionUtils.isNotEmpty(ranks)) {
                        dto.setJobRanks(Lists.newArrayListWithCapacity(rankIds.length));
                        for (MultiOrgJobRank rk : ranks) {
                            OrgJobDutyDto.OrgJobRankDto rankDto = new OrgJobDutyDto.OrgJobRankDto();
                            rankDto.setId(rk.getId());
                            rankDto.setJobGrade(rk.getJobGrade());
                            rankDto.setJobRank(rk.getJobRank());
                            // 查职档
                            rankDto.setLevels(multiOrgJobLevelService.listByJobRankUuid(rk.getUuid()));
                            dto.getJobRanks().add(rankDto);
                        }
                    }

                }
            }
            return dto;
        }
        return null;
    }

    /**
     * @param dutyIds
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<String> listJobIdByDutyIds(List<String> dutyIds, String... orgVersionIds) {
        Set<String> jobIds = Sets.newHashSet();
        String hql = "select t.jobId as jobId from MultiOrgJobDuty t where t.dutyId in(:dutyIds) and t.orgVersionUuid in(select v.uuid from OrgVersionEntity v where v.id in(:orgVersionIds))";
        ListUtils.handleSubList(dutyIds, 1000, ids -> {
            Map<String, Object> params = Maps.newHashMap();
            params.put("dutyIds", ids);
            params.put("orgVersionIds", orgVersionIds);
            jobIds.addAll(this.dao.listCharSequenceByHQL(hql, params));
        });
        return Lists.newArrayList(jobIds);
    }

}
