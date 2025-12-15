/*
 * @(#)2018年12月7日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service.impl;

import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.dao.DataImportLogDao;
import com.wellsoft.pt.basicdata.iexport.entity.DataImportLogEntity;
import com.wellsoft.pt.basicdata.iexport.service.DataImportLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年12月7日.1	zhulh		2018年12月7日		Create
 * </pre>
 * @date 2018年12月7日
 */
@Service
public class DataImportLogServiceImpl extends AbstractJpaServiceImpl<DataImportLogEntity, DataImportLogDao, String>
        implements DataImportLogService {

    @Autowired
    private MongoFileService mongoFileService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.DataImportLogService#log(com.wellsoft.pt.basicdata.iexport.acceptor.IexportData)
     */
    @Override
    @Transactional
    public void log(IexportData iexportData) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        DataImportLogEntity dataImportLog = new DataImportLogEntity();
        // 数据UUID
        String dataUuid = iexportData.getUuid();
        // 数据名称
        String dataName = iexportData.getName();
        // 数据类型
        String dataType = iexportData.getType();
        // 登录名
        String loginName = userDetails.getLoginName();
        // 用户名
        String userName = userDetails.getUserName();
        // 部门职位
        String departmentJob = ObjectUtils.toString(userDetails.getMainDepartmentName(), StringUtils.EMPTY) + "/"
                + ObjectUtils.toString(userDetails.getMainJobName(), StringUtils.EMPTY);
        // 导入时间
        Date logTime = Calendar.getInstance().getTime();
        // 导入主机IP
        String clientIp = SpringSecurityUtils.getCurrentUserIp();

        dataImportLog.setDataUuid(dataUuid);
        dataImportLog.setDataName(dataName);
        dataImportLog.setDataType(dataType);
        dataImportLog.setLoginName(loginName);
        dataImportLog.setUserName(userName);
        dataImportLog.setDepartmentJob(departmentJob);
        dataImportLog.setLogTime(logTime);
        dataImportLog.setClientIp(clientIp);
        this.dao.save(dataImportLog);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.DataImportLogService#log(com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity, java.lang.String)
     */
    @Override
    public void log(MongoFileEntity mongoFileEntity, String importIds) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        DataImportLogEntity dataImportLog = new DataImportLogEntity();
        // 数据UUID
        String dataUuid = mongoFileEntity.getFileID();
        // 数据名称
        String dataName = mongoFileEntity.getFileName();
        // 数据类型
        String dataType = mongoFileEntity.getContentType();
        // 登录名
        String loginName = userDetails.getLoginName();
        // 用户名
        String userName = userDetails.getUserName();
        // 部门职位
        String departmentJob = ObjectUtils.toString(userDetails.getMainDepartmentName(), StringUtils.EMPTY) + "/"
                + ObjectUtils.toString(userDetails.getMainJobName(), StringUtils.EMPTY);
        // 导入时间
        Date logTime = Calendar.getInstance().getTime();
        // 导入主机IP
        String clientIp = SpringSecurityUtils.getCurrentUserIp();

        dataImportLog.setDataUuid(dataUuid);
        dataImportLog.setDataName(dataName);
        dataImportLog.setDataType(dataType);
        dataImportLog.setImportIds(importIds);
        dataImportLog.setLoginName(loginName);
        dataImportLog.setUserName(userName);
        dataImportLog.setDepartmentJob(departmentJob);
        dataImportLog.setLogTime(logTime);
        dataImportLog.setClientIp(clientIp);
        this.dao.save(dataImportLog);

        // 导入文件放入夹
        mongoFileService.pushFileToFolder(dataImportLog.getUuid(), dataUuid, null);
    }

}
