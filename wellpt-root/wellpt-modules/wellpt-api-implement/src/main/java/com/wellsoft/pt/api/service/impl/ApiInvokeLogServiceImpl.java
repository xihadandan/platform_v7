package com.wellsoft.pt.api.service.impl;

import com.wellsoft.pt.api.dao.impl.ApiInvokeLogDaoImpl;
import com.wellsoft.pt.api.entity.ApiInvokeLogEntity;
import com.wellsoft.pt.api.service.ApiInvokeLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年09月23日   chenq	 Create
 * </pre>
 */
@Service
public class ApiInvokeLogServiceImpl extends AbstractJpaServiceImpl<ApiInvokeLogEntity, ApiInvokeLogDaoImpl, Long> implements ApiInvokeLogService {
}
