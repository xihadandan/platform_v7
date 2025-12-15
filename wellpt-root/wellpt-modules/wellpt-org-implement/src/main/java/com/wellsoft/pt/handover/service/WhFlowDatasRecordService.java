/*
 * @(#)2022-03-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.handover.service;

import com.wellsoft.pt.handover.dao.WhFlowDatasRecordDao;
import com.wellsoft.pt.handover.entity.WhFlowDatasRecordEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 数据库表WH_FLOW_DATAS_RECORD的service服务接口
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
public interface WhFlowDatasRecordService extends JpaService<WhFlowDatasRecordEntity, WhFlowDatasRecordDao, String> {

    /**
     * 获取执行完成流程数据量记录
     *
     * @param handoverUuid
     * @return com.wellsoft.pt.handover.entity.WhFlowDatasRecordEntity
     **/
    public WhFlowDatasRecordEntity getDatasRecordByHandoverUuid(String handoverUuid);
}
