/*
 * @(#)8/8/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.timer.dao.TsTimerAlarmDao;
import com.wellsoft.pt.timer.entity.TsTimerAlarmEntity;
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
 * 8/8/24.1	    zhulh		8/8/24		    Create
 * </pre>
 * @date 8/8/24
 */
@Repository
public class TsTimerAlarmDaoImpl extends AbstractJpaDaoImpl<TsTimerAlarmEntity, Long> implements TsTimerAlarmDao {
}
