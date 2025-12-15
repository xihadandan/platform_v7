/*
 * @(#)5/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.service.impl;

import com.wellsoft.pt.app.weixin.dao.WeixinEventDao;
import com.wellsoft.pt.app.weixin.entity.WeixinEventEntity;
import com.wellsoft.pt.app.weixin.service.WeixinEventService;
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
 * 5/23/25.1	    zhulh		5/23/25		    Create
 * </pre>
 * @date 5/23/25
 */
@Service
public class WeixinEventServiceImpl extends AbstractJpaServiceImpl<WeixinEventEntity, WeixinEventDao, Long> implements WeixinEventService {
}
