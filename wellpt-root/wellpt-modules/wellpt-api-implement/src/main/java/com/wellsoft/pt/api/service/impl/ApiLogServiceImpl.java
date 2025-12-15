package com.wellsoft.pt.api.service.impl;

import com.wellsoft.pt.api.dao.impl.ApiLogDaoImpl;
import com.wellsoft.pt.api.entity.ApiLogEntity;
import com.wellsoft.pt.api.service.ApiLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/13    chenq		2018/8/13		Create
 * </pre>
 */
@Service
public class ApiLogServiceImpl extends
        AbstractJpaServiceImpl<ApiLogEntity, ApiLogDaoImpl, String> implements ApiLogService {
}
