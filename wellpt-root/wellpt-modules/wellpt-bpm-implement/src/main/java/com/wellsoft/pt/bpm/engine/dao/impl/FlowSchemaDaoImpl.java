/*
 * @(#)2012-11-15 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.pt.bpm.engine.dao.FlowSchemaDao;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-15.1	zhulh		2012-11-15		Create
 * </pre>
 * @date 2012-11-15
 */
@Repository
public class FlowSchemaDaoImpl extends AbstractJpaDaoImpl<FlowSchema, String> implements FlowSchemaDao {
}
