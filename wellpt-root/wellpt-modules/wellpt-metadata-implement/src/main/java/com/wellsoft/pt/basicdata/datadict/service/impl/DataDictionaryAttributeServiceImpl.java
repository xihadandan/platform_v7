/*
 * @(#)2018年4月20日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.service.impl;

import com.wellsoft.pt.basicdata.datadict.dao.DataDictionaryAttributeDao;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionaryAttribute;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryAttributeService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月20日.1	chenqiong		2018年4月20日		Create
 * </pre>
 * @date 2018年4月20日
 */
@Service
public class DataDictionaryAttributeServiceImpl extends
        AbstractJpaServiceImpl<DataDictionaryAttribute, DataDictionaryAttributeDao, String> implements
        DataDictionaryAttributeService {

    @Override
    @Transactional
    public void evict(DataDictionaryAttribute attribute) {
        this.dao.getSession().evict(attribute);
    }

}
