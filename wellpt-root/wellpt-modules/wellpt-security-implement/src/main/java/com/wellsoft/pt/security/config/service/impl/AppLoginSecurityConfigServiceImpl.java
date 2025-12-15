package com.wellsoft.pt.security.config.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.config.dao.impl.AppLoginSecurityConfigDaoImpl;
import com.wellsoft.pt.security.config.entity.AppLoginSecurityConfigEntity;
import com.wellsoft.pt.security.config.service.AppLoginSecurityConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月07日   chenq	 Create
 * </pre>
 */
@Service
public class AppLoginSecurityConfigServiceImpl extends AbstractJpaServiceImpl<AppLoginSecurityConfigEntity, AppLoginSecurityConfigDaoImpl, String> implements AppLoginSecurityConfigService {
    @Override
    public boolean isAllowMultiDeviceLogin(String systemUnitId) {
        AppLoginSecurityConfigEntity config = dao.getOneByFieldEq("systemUnitId", systemUnitId);
        return config == null ? true : config.getIsAllowMultiDeviceLogin();
    }

    @Override
    public AppLoginSecurityConfigEntity getBySystemUnitId(String systemUnitId) {
        return dao.getOneByFieldEq("systemUnitId", systemUnitId);
    }

    @Override
    public AppLoginSecurityConfigEntity getBySystemAndTenant(String system, String tenant) {
        Map<String, Object> param = Maps.newHashMap();
        StringBuilder hql = new StringBuilder("from AppLoginSecurityConfigEntity where 1=1 ");
        if (StringUtils.isNotBlank(tenant)) {
            param.put("tenant", tenant);
            hql.append(" and tenant =:tenant ");
        }
        if (StringUtils.isNotBlank(system)) {
            param.put("system", system);
            hql.append(" and system =:system ");

        } else {
            hql.append(" and system is null ");
        }
        List<AppLoginSecurityConfigEntity> list = dao.listByHQL(hql.toString(), param);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public boolean isAllowMultiDeviceLogin(String system, String tenant) {
        AppLoginSecurityConfigEntity entity = this.getBySystemAndTenant(system, tenant);
        return entity != null ? entity.getIsAllowMultiDeviceLogin() : false;
    }
}
