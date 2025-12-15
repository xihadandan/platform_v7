/*
 * @(#)2013-5-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskSubFlowDispatchDao;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlowDispatch;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 任务子流程分发数据层访问类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-15.1	zhulh		2013-5-15		Create
 * </pre>
 * @date 2013-5-15
 */
@Repository
public class TaskSubFlowDispatchDaoImpl extends AbstractJpaDaoImpl<TaskSubFlowDispatch, String>
        implements TaskSubFlowDispatchDao {

}
