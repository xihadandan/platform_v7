/*
 * @(#)2018年4月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao;

import com.wellsoft.pt.bpm.engine.entity.TaskActivity;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月9日.1	chenqiong		2018年4月9日		Create
 * </pre>
 * @date 2018年4月9日
 */
public interface TaskActivityDao extends JpaDao<TaskActivity, String> {

    public abstract List<TaskActivity> getByFlowInstUuid(String flowInstUuid);

    public abstract List<TaskActivity> getHistoryActivities(String flowInstUuid);

}
