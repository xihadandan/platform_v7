/*
 * @(#)11/29/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.pt.bpm.engine.dao.WfFlowDefinitionUserDao;
import com.wellsoft.pt.bpm.engine.entity.WfFlowDefinitionUserEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 11/29/24.1	    zhulh		11/29/24		    Create
 * </pre>
 * @date 11/29/24
 */
@Repository
public class WfFlowDefinitionUserDaoImpl extends AbstractJpaDaoImpl<WfFlowDefinitionUserEntity, Long> implements
        WfFlowDefinitionUserDao {
}
