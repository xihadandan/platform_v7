/*
 * @(#)2013-9-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.task.dao.JobFiredDetailsDao;
import com.wellsoft.pt.task.dto.JobFiredDetailsDTO;
import com.wellsoft.pt.task.entity.JobFiredDetails;
import com.wellsoft.pt.task.entity.JobFiredDetailsHistory;
import com.wellsoft.pt.task.job.JobDetail;
import com.wellsoft.pt.task.service.JobFiredDetailsHistoryService;
import com.wellsoft.pt.task.service.JobFiredDetailsService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.util.ArrayList;
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
 * 2013-9-17.1	zhulh		2013-9-17		Create
 * </pre>
 * @date 2013-9-17
 */
@Service
public class JobFiredDetailsServiceImpl extends AbstractJpaServiceImpl<JobFiredDetails, JobFiredDetailsDao, String>
        implements JobFiredDetailsService {

    @Autowired
    private JobFiredDetailsHistoryService jobFiredDetailsHistoryService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobFiredDetailsService#saveJobDetail(com.wellsoft.pt.task.job.JobDetail)
     */
    @Override
    @Transactional
    public void scheduleJobDetail(Integer firedType, JobDetail jobDetail) {
        JobFiredDetails jobFiredDetails = new JobFiredDetails();
        jobFiredDetails.setName(jobDetail.getName());
        jobFiredDetails.setType(jobDetail.getType());
        jobFiredDetails.setFiredType(firedType);
        if (jobDetail.getJobClass() != null) {
            jobFiredDetails.setJobClassName(jobDetail.getJobClass().getCanonicalName());
        }
        jobFiredDetails.setContent(getContent(jobDetail));
        this.dao.save(jobFiredDetails);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobFiredDetailsService#getAll()
     */
    @Override
    public List<JobFiredDetailsDTO> getAll() {
        List<JobFiredDetailsDTO> jobFiredDetailsDTOs = new ArrayList<JobFiredDetailsDTO>();
        List<JobFiredDetails> jobFiredDetails = this.listAllByOrderPage(new PagingInfo(0, 10, false),
                IdEntity.CREATE_TIME_ASC);
        for (JobFiredDetails jobFiredDetail : jobFiredDetails) {
            JobFiredDetailsDTO jobFiredDetailsDTO = new JobFiredDetailsDTO();
            BeanUtils.copyProperties(jobFiredDetail, jobFiredDetailsDTO);
            jobFiredDetailsDTO.setJobDetail(getJobDetail(jobFiredDetail));
            jobFiredDetailsDTOs.add(jobFiredDetailsDTO);
        }
        return jobFiredDetailsDTOs;
    }

    /**
     * @param jobDetail
     * @return
     */
    private Blob getContent(JobDetail jobDetail) {
        Blob content = null;
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = null;
        try {
            oo = new ObjectOutputStream(bo);
            oo.writeObject(jobDetail);
            byte[] bytes = bo.toByteArray();
            content = new SerialBlob(bytes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(bo);
            IOUtils.closeQuietly(oo);
        }
        return content;
    }

    /**
     * @param jobFiredDetail
     * @return
     */
    private JobDetail getJobDetail(JobFiredDetails jobFiredDetail) {
        JobDetail jobDetail = null;
        InputStream cin = null;
        ObjectInputStream oin = null;
        try {
            cin = jobFiredDetail.getContent().getBinaryStream();
            oin = new ObjectInputStream(cin);
            jobDetail = (JobDetail) oin.readObject();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(cin);
            IOUtils.closeQuietly(oin);
        }
        return jobDetail;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.service.JobFiredDetailsService#moveToHistory(java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Override
    @Transactional
    public void moveToHistory(String uuid, Integer result, String resultMsg) {
        JobFiredDetails jobFiredDetails = this.dao.getOne(uuid);
        JobFiredDetailsHistory jobFiredDetailsHistory = new JobFiredDetailsHistory();
        BeanUtils.copyProperties(jobFiredDetails, jobFiredDetailsHistory);
        jobFiredDetailsHistory.setUuid(null);
        jobFiredDetailsHistory.setContent(jobFiredDetails.getContent());
        jobFiredDetailsHistory.setResult(result);
        jobFiredDetailsHistory.setResultMsg(resultMsg);
        jobFiredDetailsHistoryService.save(jobFiredDetailsHistory);
        delete(jobFiredDetails);
    }

    @Override
    @Transactional
    public int updateOccupied(String uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        return this.dao.updateByHQL("update JobFiredDetails set occupied = true where occupied = false and uuid=:uuid", params);
    }

}
