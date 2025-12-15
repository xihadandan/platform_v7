/*
 * @(#)2/28/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizProcessAssembleDao;
import com.wellsoft.pt.biz.entity.BizProcessAssembleEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2/28/24.1	zhulh		2/28/24		Create
 * </pre>
 * @date 2/28/24
 */
public interface BizProcessAssembleService extends JpaService<BizProcessAssembleEntity, BizProcessAssembleDao, Long> {

    /**
     * 根据业务流程定义UUID获取业务装配信息
     *
     * @param processDefUuid
     * @return
     */
    BizProcessAssembleEntity getByProcessDefUuid(String processDefUuid);

}
