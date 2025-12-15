/*
 * @(#)2018年4月17日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.pt.integration.dao.ExchangeDataSGSetDao;
import com.wellsoft.pt.integration.entity.ExchangeDataSGSet;
import com.wellsoft.pt.integration.service.ExchangeDataSGSetService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
 * 2018年4月17日.1	chenqiong		2018年4月17日		Create
 * </pre>
 * @date 2018年4月17日
 */
@Service
public class ExchangeDataSGSetServiceImpl extends
        AbstractJpaServiceImpl<ExchangeDataSGSet, ExchangeDataSGSetDao, String> implements ExchangeDataSGSetService {

    @Override
    public ExchangeDataSGSet getByKey(String key_) {
        String hql = "from ExchangeDataSGSet o where o.key_= :key_ ";
        Map<String, Object> arg = new HashMap<String, Object>();
        arg.put("key_", key_);
        return this.dao.getOneByHQL(hql, arg);
    }

}
