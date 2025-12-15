/*
 * @(#)8/9/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.wellsoft.pt.biz.dao.BizFormStateHistoryDao;
import com.wellsoft.pt.biz.entity.BizFormStateHistoryEntity;
import com.wellsoft.pt.biz.service.BizFormStateHistoryService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/9/24.1	    zhulh		8/9/24		    Create
 * </pre>
 * @date 8/9/24
 */
@Service
public class BizFormStateHistoryServiceImpl extends AbstractJpaServiceImpl<BizFormStateHistoryEntity, BizFormStateHistoryDao, Long> implements BizFormStateHistoryService {
}
