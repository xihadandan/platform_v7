/*
 * @(#)2021-01-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.log.dao.BusinessDetailsLogDao;
import com.wellsoft.pt.log.dto.BusinessDetailsLogItem;
import com.wellsoft.pt.log.entity.BusinessDetailsLog;

import java.util.List;

/**
 * Description: 数据库表LOG_BUSINESS_DETAILS的service服务接口
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-01-08.1	zhongzh		2021-01-08		Create
 * </pre>
 * @date 2021-01-08
 */
public interface BusinessDetailsLogService extends JpaService<BusinessDetailsLog, BusinessDetailsLogDao, String> {

    public abstract void saveDetail(BusinessDetailsLog entity);

    /**
     * 通过日志ID集合，获取业务日志数量
     *
     * @param logIds
     * @return java.util.List<com.wellsoft.pt.log.dto.BusinessDetailsLogItem>
     **/
    public List<BusinessDetailsLogItem> getBusinessDetailsLogItemByLogIds(List<String> logIds);

}
