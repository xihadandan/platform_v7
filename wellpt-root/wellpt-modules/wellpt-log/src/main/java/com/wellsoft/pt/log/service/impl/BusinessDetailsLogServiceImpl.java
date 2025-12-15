/*
 * @(#)2021-01-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.log.dao.BusinessDetailsLogDao;
import com.wellsoft.pt.log.dto.BusinessDetailsLogItem;
import com.wellsoft.pt.log.entity.BusinessDetailsLog;
import com.wellsoft.pt.log.service.BusinessDetailsLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表LOG_BUSINESS_DETAILS的service服务接口实现类
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
@Service
public class BusinessDetailsLogServiceImpl extends AbstractJpaServiceImpl<BusinessDetailsLog, BusinessDetailsLogDao, String> implements BusinessDetailsLogService {

    @Override
    public void saveDetail(BusinessDetailsLog entity) {
        dao.getSession().save(entity);
    }

    @Override
    public List<BusinessDetailsLogItem> getBusinessDetailsLogItemByLogIds(List<String> logIds) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("logIds", logIds);
        //QueryItem implements BaseQueryItem
        List<BusinessDetailsLogItem> queryItems = this.dao.listItemByNameSQLQuery(
                "getBusinessDetailsLogItemByLogIds", BusinessDetailsLogItem.class, values, new PagingInfo(1,
                        Integer.MAX_VALUE));

        return queryItems;
    }
}
