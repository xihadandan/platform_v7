/*
 * @(#)11/13/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizProcessItemDefinitionDao;
import com.wellsoft.pt.biz.dao.BizProcessItemInstanceDao;
import com.wellsoft.pt.biz.entity.BizProcessItemDefinitionEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/13/23.1	zhulh		11/13/23		Create
 * </pre>
 * @date 11/13/23
 */
public interface BizProcessItemDefinitionService extends JpaService<BizProcessItemDefinitionEntity, BizProcessItemDefinitionDao, String> {

    /**
     * 根据业务流程定义UUID获取过程事项定义信息
     *
     * @param processDefUuid
     * @return
     */
    List<BizProcessItemDefinitionEntity> listByProcessDefUuid(String processDefUuid);

    /**
     * 根据业务流程定义UUID获取过程事项定义信息
     *
     * @param processDefUuid
     */
    void deleteByProcessDefUuid(String processDefUuid);
    
}
