/*
 * @(#)2018-11-07 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskInstanceToppingDao;
import com.wellsoft.pt.bpm.engine.entity.TaskInstanceTopping;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 数据库表WF_TASK_INSTANCE_TOPPING的DAO接口实现类
 *
 * @author lst
 * @version 1.0
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018-11-07.1    lst        2018-11-07		Create
 * </pre>
 * @date 2018-11-07
 */
@Repository
public class TaskInstanceToppingDaoImpl extends AbstractJpaDaoImpl<TaskInstanceTopping, String> implements
        TaskInstanceToppingDao {

}
