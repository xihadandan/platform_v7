/*
 * @(#)2022-03-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.handover.service;

import com.wellsoft.pt.handover.dao.WhWorkHandoverItemDao;
import com.wellsoft.pt.handover.entity.WhWorkHandoverItemEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据库表WH_WORK_HANDOVER_ITEM的service服务接口
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
public interface WhWorkHandoverItemService extends JpaService<WhWorkHandoverItemEntity, WhWorkHandoverItemDao, String> {

    public List<WhWorkHandoverItemEntity> getListByWorkHandoverUuid(String handoverUuid);

    /**
     * 删除工作交接项
     *
     * @param workHandoverUuid
     * @return void
     **/
    public void deleteByWorkHandoverUuid(String workHandoverUuid);
}
