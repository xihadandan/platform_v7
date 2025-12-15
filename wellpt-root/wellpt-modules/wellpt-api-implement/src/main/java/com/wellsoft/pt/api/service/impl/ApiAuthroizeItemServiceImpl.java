package com.wellsoft.pt.api.service.impl;

import com.wellsoft.pt.api.dao.impl.ApiAuthroizeItemDaoImpl;
import com.wellsoft.pt.api.entity.ApiAuthorizeItemEntity;
import com.wellsoft.pt.api.service.ApiAuthroizeItemService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/10/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/10/25    chenq		2018/10/25		Create
 * </pre>
 */
@Service
public class ApiAuthroizeItemServiceImpl extends
        AbstractJpaServiceImpl<ApiAuthorizeItemEntity, ApiAuthroizeItemDaoImpl, String> implements
        ApiAuthroizeItemService {
    @Override
    @Transactional
    public void deleteBySytemUuids(List<String> systemUuids) {
        this.dao.deleteBySytemUuids(systemUuids);
    }

    @Override
    public List<ApiAuthorizeItemEntity> listBySystemUuid(String systemUuid) {
        return this.dao.listBySystemUuid(systemUuid);
    }

    @Override
    public List<ApiAuthorizeItemEntity> listBySystemCode(String systemCode, boolean authorized) {
        return this.dao.listBySystemCode(systemCode, authorized);
    }
}
