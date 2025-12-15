/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgJobDutyDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobDuty;
import com.wellsoft.pt.org.dto.OrgJobDutyDto;

import java.util.List;
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
public interface MultiOrgJobDutyService extends JpaService<MultiOrgJobDuty, MultiOrgJobDutyDao, String> {
    /**
     * 如何描述该方法
     *
     * @param jobId
     */
    void deleteJobDutyByJobId(String jobId);

    /**
     * 如何描述该方法
     *
     * @param eleId
     * @return
     */
    MultiOrgJobDuty getJobDutyByJobId(String eleId);

    boolean isMemberOfSelectedJobDuty(String userId, Set<String> dutyIds, Set<String> jobId);

    boolean isMemberOfAllJobDuty(String userId, Set<String> dutyIds);

    boolean isMemberOfMainJobDuty(String userId, Set<String> dutyIds);

    void saveUpdateJobDutyByJobIdAndOrgVersionUuid(String jobId, String orgDutyId, Long orgVersionUuid);

    MultiOrgJobDuty getJobDutyByJobIdAndOrgVersionUuid(String jobId, Long orgVersionUuid);

    void deleteByJobIdAndOrgVersionUuid(String jobId, Long orgVersionUuid);

    List<MultiOrgJobDuty> listByOrgVersionUuid(Long orgVersionUuid);

    void deleteByJobIdAndOrgVersionUuid(Long orgVersionUuid);

    OrgJobDutyDto getJobDutyDetailsByJobIdAndOrgVersionUuid(String jobId, Long orgVersionUuid);

    List<String> listJobIdByDutyIds(List<String> dutyIds, String... orgVersionIds);
}
