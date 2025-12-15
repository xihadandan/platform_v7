/*
 * @(#)11/11/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.workflow.dao.WfFlowBusinessDefinitionDao;
import com.wellsoft.pt.workflow.entity.WfFlowBusinessDefinitionEntity;
import org.springframework.stereotype.Repository;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/11/22.1	zhulh		11/11/22		Create
 * </pre>
 * @date 11/11/22
 */
@Repository
public class WfFlowBusinessDefinitionDaoImpl extends AbstractJpaDaoImpl<WfFlowBusinessDefinitionEntity, String>
        implements WfFlowBusinessDefinitionDao {
}
