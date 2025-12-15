/*
 * @(#)10/22/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskDeleteLogDao;
import com.wellsoft.pt.bpm.engine.entity.TaskDeleteLog;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Date;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 10/22/24.1	    zhulh		10/22/24		    Create
 * </pre>
 * @date 10/22/24
 */
public interface TaskDeleteLogService extends JpaService<TaskDeleteLog, TaskDeleteLogDao, String> {

    /**
     * @param createTime
     */
    void deleteBeforeCreateTime(Date createTime);
    
}
