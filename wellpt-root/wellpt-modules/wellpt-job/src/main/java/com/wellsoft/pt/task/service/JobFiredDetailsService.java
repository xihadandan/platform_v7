/*
 * @(#)2013-9-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.task.dao.JobFiredDetailsDao;
import com.wellsoft.pt.task.dto.JobFiredDetailsDTO;
import com.wellsoft.pt.task.entity.JobFiredDetails;
import com.wellsoft.pt.task.job.JobDetail;

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
 * 2013-9-17.1	zhulh		2013-9-17		Create
 * </pre>
 * @date 2013-9-17
 */
public interface JobFiredDetailsService extends JpaService<JobFiredDetails, JobFiredDetailsDao, String> {

    /**
     * @param firedType
     * @param jobDetail
     */
    void scheduleJobDetail(Integer firedType, JobDetail jobDetail);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<JobFiredDetailsDTO> getAll();

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @param result
     * @param resultMsg
     */
    void moveToHistory(String uuid, Integer result, String resultMsg);

    int updateOccupied(String uuid);

}
