package com.wellsoft.pt.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgSettingDaoImpl;
import com.wellsoft.pt.org.entity.OrgSettingEntity;
import com.wellsoft.pt.org.service.OrgSettingService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月10日   chenq	 Create
 * </pre>
 */
@Service
public class OrgSettingServiceImpl extends AbstractJpaServiceImpl<OrgSettingEntity, OrgSettingDaoImpl, Long> implements OrgSettingService {
    @Override
    public List<OrgSettingEntity> listBySystemAndTenant(String system, String tenant) {
        OrgSettingEntity example = new OrgSettingEntity();
        example.setSystem(system);
        example.setTenant(tenant);
        return dao.listByEntity(example);
    }

    @Override
    public List<OrgSettingEntity> listBySystemIsNull() {
        return this.dao.listByFieldIsNull("system");
    }

    @Override
    @Transactional
    public void updateOrgSetting(OrgSettingEntity orgSettingEntity) {
        OrgSettingEntity entity = getOne(orgSettingEntity.getUuid());
        if (entity != null) {
            BeanUtils.copyProperties(orgSettingEntity, entity, entity.BASE_FIELDS);
            this.save(entity);
        }
    }

    @Override
    @Transactional
    public void enableByUuid(long uuid, boolean enable) {
        OrgSettingEntity entity = this.getOne(uuid);
        if (entity != null) {
            entity.setEnable(enable);
            this.save(entity);
        }
    }

    @Override
    @Transactional
    public void deleteByAttrKeyAndSystem(String attrKey, String system) {
        OrgSettingEntity example = new OrgSettingEntity();
        example.setAttrKey(attrKey);
        example.setSystem(system);
        if (StringUtils.isNotBlank(system)) {
            example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        this.deleteByEntities(this.listByEntity(example));
    }

    @Override
    public boolean isEnable(String attrKey, String system, String tenant) {
        OrgSettingEntity example = new OrgSettingEntity();
        example.setAttrKey(attrKey);
        example.setSystem(system);
        example.setTenant(tenant);
        List<OrgSettingEntity> list = this.listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0).getEnable() : false;
    }


    @Override
    public OrgSettingEntity getOrgSettingBySystemTenantAndAttrKey(String system, String tenant, String attrKey) {
        OrgSettingEntity example = new OrgSettingEntity();
        example.setAttrKey(attrKey);
        example.setTenant(tenant);
        example.setSystem(system);
        List<OrgSettingEntity> list = this.listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

}
