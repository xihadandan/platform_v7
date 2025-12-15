/*
 * @(#)2018年4月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.dao.WebserviceParmDao;
import com.wellsoft.pt.message.entity.WebServiceParm;
import com.wellsoft.pt.message.service.WebServiceParmService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月19日.1	chenqiong		2018年4月19日		Create
 * </pre>
 * @date 2018年4月19日
 */
@Service
public class WebServiceParmServiceImpl extends
        AbstractJpaServiceImpl<WebServiceParm, WebserviceParmDao, String>
        implements WebServiceParmService {

    @Override
    @Transactional
    public void deleteByMsgTemplateUuid(String uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("muuid", uuid);
        this.dao.deleteByNamedSQL("deleteWsParamsByMsgTemplateUuid", params);
    }
}
