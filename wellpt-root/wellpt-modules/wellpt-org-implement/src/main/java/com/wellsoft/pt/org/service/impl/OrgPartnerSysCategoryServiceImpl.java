package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgPartnerSysCategoryDaoImpl;
import com.wellsoft.pt.org.entity.OrgPartnerSysCategoryEntity;
import com.wellsoft.pt.org.service.OrgPartnerSysApplyService;
import com.wellsoft.pt.org.service.OrgPartnerSysCategoryService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月07日   chenq	 Create
 * </pre>
 */
@Service
public class OrgPartnerSysCategoryServiceImpl extends AbstractJpaServiceImpl<OrgPartnerSysCategoryEntity, OrgPartnerSysCategoryDaoImpl, Long> implements OrgPartnerSysCategoryService {

    @Autowired
    OrgPartnerSysApplyService orgPartnerSystemService;


    @Override
    @Transactional
    public void deleteOrgPartnerSysCategory(Long uuid) {
        delete(uuid);
        //TODO: 更新协作系统的分类UUID为null
        orgPartnerSystemService.updateCategoryIsNullByCategoryUuid(uuid);
    }

    @Override
    @Transactional
    public Long saveOrgPartnerSysCategory(OrgPartnerSysCategoryEntity temp) {
        OrgPartnerSysCategoryEntity entity = temp.getUuid() != null ? getOne(temp.getUuid()) : new OrgPartnerSysCategoryEntity();
        BeanUtils.copyProperties(temp, entity, entity.BASE_FIELDS);
        entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        entity.setSystem(RequestSystemContextPathResolver.system());
        save(entity);
        return entity.getUuid();
    }

    @Override
    public List<OrgPartnerSysCategoryEntity> listBySystemAndTenant(String system, String tenant) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        params.put("tenant", tenant);
        return this.dao.listByHQL("from OrgPartnerSysCategoryEntity where 1=1" + (StringUtils.isNotBlank(system) ? " and system=:system" : "") + (StringUtils.isNotBlank(tenant) ? " and tenant=:tenant" : "") + " order by code asc", params);
    }
}
