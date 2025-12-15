/*
 * @(#)10/22/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.bpm.engine.dao.TaskDeleteLogDao;
import com.wellsoft.pt.bpm.engine.entity.TaskDeleteLog;
import com.wellsoft.pt.bpm.engine.service.TaskDeleteLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

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
@Service
public class TaskDeleteLogServiceImpl extends AbstractJpaServiceImpl<TaskDeleteLog, TaskDeleteLogDao, String>
        implements TaskDeleteLogService {

    @Override
    @Transactional
    public void deleteBeforeCreateTime(Date createTime) {
        String hql = "delete from TaskDeleteLog t where t.createTime < :createTime";
        Map<String, Object> params = Maps.newHashMap();
        this.dao.deleteByHQL(hql, params);
    }

}
