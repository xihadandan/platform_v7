package com.wellsoft.pt.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.BizOrgRoleDaoImpl;
import com.wellsoft.pt.org.entity.BizOrgRoleEntity;
import com.wellsoft.pt.org.service.BizOrgRoleService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月09日   chenq	 Create
 * </pre>
 */
@Service
public class BizOrgRoleServiceImpl extends AbstractJpaServiceImpl<BizOrgRoleEntity, BizOrgRoleDaoImpl, Long> implements BizOrgRoleService {
    @Override
    public BizOrgRoleEntity getById(String id) {
        BizOrgRoleEntity example = new BizOrgRoleEntity();
        example.setId(id);
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            example.setSystem(RequestSystemContextPathResolver.system());
            example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        List<BizOrgRoleEntity> list = listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public BizOrgRoleEntity getByIdAndBizOrgUuid(String id, Long bizOrgUuid) {
        BizOrgRoleEntity example = new BizOrgRoleEntity();
        example.setId(id);
        example.setBizOrgUuid(bizOrgUuid);
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            example.setSystem(RequestSystemContextPathResolver.system());
            example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        List<BizOrgRoleEntity> list = listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }
}
