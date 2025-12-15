/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.pt.integration.dao.DXExchangeBatchDao;
import com.wellsoft.pt.integration.entity.DXExchangeBatch;
import com.wellsoft.pt.integration.service.DXExchangeBatchService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月16日.1	chenqiong		2018年4月16日		Create
 * </pre>
 * @date 2018年4月16日
 */
@Service
public class DXExchangeBatchServiceImpl extends AbstractJpaServiceImpl<DXExchangeBatch, DXExchangeBatchDao, String>
        implements DXExchangeBatchService {

    @Override
    public List<DXExchangeBatch> getDXExchangeBatchByTypeId(String typeId) {
        return dao.getDXExchangeBatchByTypeId(typeId);
    }

    @Override
    public DXExchangeBatch getDXExchangeBatchById(String id) {
        return dao.getDXExchangeBatchById(id);
    }

    @Override
    public List<String> getDistinctFromUnitList() {
        return dao.getDistinctFromUnitList();
    }

    @Override
    public List<String> getDistinctFromUnitListByTypeId(String typeId) {
        return dao.getDistinctFromUnitListByTypeId(typeId);
    }

    @Override
    public List<String> getFromUnitList() {
        return dao.getFromUnitList();
    }

    @Override
    public List<String> getAllUnitId() {
        return dao.getAllUnitId();
    }

}
