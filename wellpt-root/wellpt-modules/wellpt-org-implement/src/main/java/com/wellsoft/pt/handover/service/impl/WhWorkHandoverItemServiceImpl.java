/*
 * @(#)2022-03-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.handover.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.handover.dao.WhWorkHandoverItemDao;
import com.wellsoft.pt.handover.entity.WhWorkHandoverItemEntity;
import com.wellsoft.pt.handover.service.WhWorkHandoverItemService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表WH_WORK_HANDOVER_ITEM的service服务接口实现类
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
public class WhWorkHandoverItemServiceImpl
        extends AbstractJpaServiceImpl<WhWorkHandoverItemEntity, WhWorkHandoverItemDao, String>
        implements WhWorkHandoverItemService {

    @Override
    public List<WhWorkHandoverItemEntity> getListByWorkHandoverUuid(String handoverUuid) {
        WhWorkHandoverItemEntity entity = new WhWorkHandoverItemEntity();
        entity.setWhWorkHandoverUuid(handoverUuid);
        List<WhWorkHandoverItemEntity> entities = this.dao.listByEntity(entity);
        return entities;
    }

    @Override
    public void deleteByWorkHandoverUuid(String workHandoverUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("workHandoverUuid", workHandoverUuid);
        this.dao.deleteByNamedSQL("deleteHandoverItemByWorkHandoverUuid", values);
    }
}
