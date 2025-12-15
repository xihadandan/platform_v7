/*
 * @(#)2022-03-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.handover.service.impl;

import com.wellsoft.pt.handover.dao.WhFlowDatasRecordDao;
import com.wellsoft.pt.handover.entity.WhFlowDatasRecordEntity;
import com.wellsoft.pt.handover.service.WhFlowDatasRecordService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 数据库表WH_FLOW_DATAS_RECORD的service服务接口实现类
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
public class WhFlowDatasRecordServiceImpl
        extends AbstractJpaServiceImpl<WhFlowDatasRecordEntity, WhFlowDatasRecordDao, String>
        implements WhFlowDatasRecordService {

    @Override
    public WhFlowDatasRecordEntity getDatasRecordByHandoverUuid(String handoverUuid) {
        WhFlowDatasRecordEntity entity = new WhFlowDatasRecordEntity();
        entity.setWhWorkHandoverUuid(handoverUuid);
        List<WhFlowDatasRecordEntity> entities = this.dao.listByEntity(entity);
        if (entities.size() == 0) {
            return null;
        }
        return entities.get(0);
    }
}
