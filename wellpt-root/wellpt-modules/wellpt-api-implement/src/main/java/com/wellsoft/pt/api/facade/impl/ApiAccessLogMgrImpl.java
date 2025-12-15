/*
 * @(#)2015-09-23 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.bean.ApiAccessLogBean;
import com.wellsoft.pt.api.entity.ApiAccessLog;
import com.wellsoft.pt.api.facade.ApiAccessLogMgr;
import com.wellsoft.pt.api.service.ApiAccessLogService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-09-23.1	zhulh		2015-09-23		Create
 * </pre>
 * @date 2015-09-23
 */
@Service
@Transactional
public class ApiAccessLogMgrImpl extends BaseServiceImpl implements ApiAccessLogMgr {

    @Autowired
    private ApiAccessLogService apiAccessLogService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.ApiAccessLogMgr#getBean(java.lang.String)
     */
    @Override
    public ApiAccessLogBean getBean(String uuid) {
        ApiAccessLog entity = apiAccessLogService.get(uuid);
        ApiAccessLogBean bean = new ApiAccessLogBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.ApiAccessLogMgr#saveBean(com.wellsoft.pt.api.bean.ApiAccessLogBean)
     */
    @Override
    public void saveBean(ApiAccessLogBean bean) {
        String uuid = bean.getUuid();
        ApiAccessLog entity = new ApiAccessLog();
        if (StringUtils.isNotBlank(uuid)) {
            entity = apiAccessLogService.get(uuid);
        }
        BeanUtils.copyProperties(bean, entity);
        apiAccessLogService.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.ApiAccessLogMgr#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        apiAccessLogService.remove(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.ApiAccessLogMgr#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        apiAccessLogService.removeAllByPk(uuids);
    }

}
