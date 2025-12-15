package com.wellsoft.pt.api.service.impl;

import com.wellsoft.pt.api.dao.impl.ApiOutSystemServiceConfigDaoImpl;
import com.wellsoft.pt.api.entity.ApiOutSysServiceConfigEntity;
import com.wellsoft.pt.api.service.ApiOutSysServiceConfigService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
public class ApiOutSysServiceConfigServiceImpl extends
        AbstractJpaServiceImpl<ApiOutSysServiceConfigEntity, ApiOutSystemServiceConfigDaoImpl, String> implements
        ApiOutSysServiceConfigService {
    @Override
    public List<ApiOutSysServiceConfigEntity> listBySystemUuid(String uuid) {
        return dao.listByFieldEqValue("systemUuid", uuid);
    }

    @Override
    @Transactional
    public void deleteBySystemUuids(List<String> uuids) {
        dao.deleteBySystemUuids(uuids);
    }

    @Override
    public ApiOutSysServiceConfigEntity getServiceConfig(String systemCode, String serviceCode) {
        return dao.getServiceConfig(systemCode, serviceCode);
    }
}
