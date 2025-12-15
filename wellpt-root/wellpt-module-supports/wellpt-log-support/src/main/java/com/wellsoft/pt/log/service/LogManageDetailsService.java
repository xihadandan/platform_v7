/*
 * @(#)2021-06-28 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.log.dao.LogManageDetailsDao;
import com.wellsoft.pt.log.entity.LogManageDetailsEntity;

import java.util.List;

/**
 * Description: 数据库表LOG_MANAGE_DETAILS的service服务接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-06-28.1	zenghw		2021-06-28		Create
 * </pre>
 * @date 2021-06-28
 */
public interface LogManageDetailsService extends JpaService<LogManageDetailsEntity, LogManageDetailsDao, String> {

    /**
     * 通过LogId 获取管理操作日志详情列表
     *
     * @param logId
     * @return java.util.List<com.wellsoft.pt.log.entity.LogManageDetailsEntity>
     **/
    public List<LogManageDetailsEntity> getListByLogId(String logId);
}
