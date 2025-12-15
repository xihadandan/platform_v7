/*
 * @(#)2022-03-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.handover.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.handover.dao.WhWorkTypeToHandoverDao;
import com.wellsoft.pt.handover.entity.WhWorkTypeToHandoverEntity;
import com.wellsoft.pt.handover.service.WhWorkTypeToHandoverService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表WH_WORK_TYPE_TO_HANDOVER的service服务接口实现类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-03-22.1	zenghw		2022-03-22		Create
 * </pre>
 * @date 2022-03-22
 */
@Service
public class WhWorkTypeToHandoverServiceImpl
        extends AbstractJpaServiceImpl<WhWorkTypeToHandoverEntity, WhWorkTypeToHandoverDao, String>
        implements WhWorkTypeToHandoverService {

    @Override
    public void deleteByWorkHandoverUuid(String workHandoverUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("workHandoverUuid", workHandoverUuid);
        this.dao.deleteByNamedSQL("deleteByWorkHandoverUuid", values);
    }

    @Override
    public List<WhWorkTypeToHandoverEntity> getAllListByWorkHandoverUuid(String workHandoverUuid) {
        WhWorkTypeToHandoverEntity entity = new WhWorkTypeToHandoverEntity();
        entity.setWhWorkHandoverUuid(workHandoverUuid);
        List<WhWorkTypeToHandoverEntity> entities = this.dao.listByEntity(entity);
        return entities;
    }
}
