/*
 * @(#)2018年4月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.wellsoft.pt.bpm.engine.dao.WorkFrequencyRecordDao;
import com.wellsoft.pt.bpm.engine.entity.WorkFrequencyRecord;
import com.wellsoft.pt.bpm.engine.service.WorkFrequencyRecordService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

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
@Service
public class WorkFrequencyRecordServiceImpl extends
        AbstractJpaServiceImpl<WorkFrequencyRecord, WorkFrequencyRecordDao, String> implements
        WorkFrequencyRecordService {

}
