/*
 * @(#)2013-9-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.task.dao.JobFiredDetailsHistoryDao;
import com.wellsoft.pt.task.entity.JobFiredDetailsHistory;
import com.wellsoft.pt.task.service.JobFiredDetailsHistoryService;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-9-17.1	zhulh		2013-9-17		Create
 * </pre>
 * @date 2013-9-17
 */
@Service
public class JobFiredDetailsHistoryServiceImpl extends
        AbstractJpaServiceImpl<JobFiredDetailsHistory, JobFiredDetailsHistoryDao, String> implements
        JobFiredDetailsHistoryService {

}
