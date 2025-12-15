package com.wellsoft.pt.api.service.impl;

import com.wellsoft.pt.api.dao.impl.ApiCommandDetailDaoImpl;
import com.wellsoft.pt.api.entity.ApiCommandDetailEntity;
import com.wellsoft.pt.api.service.ApiCommandDetailService;
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
public class ApiCommandDetailServiceImpl extends
        AbstractJpaServiceImpl<ApiCommandDetailEntity, ApiCommandDetailDaoImpl, String> implements
        ApiCommandDetailService {
    @Override
    public ApiCommandDetailEntity getByCommandUuid(String commandUuid) {
        return dao.getByCommandUuid(commandUuid);
    }
}
