package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgSettingDaoImpl;
import com.wellsoft.pt.org.entity.OrgSettingEntity;

import java.util.List;

/**
 * Description: 组织参数设置服务
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
public interface OrgSettingService extends JpaService<OrgSettingEntity, OrgSettingDaoImpl, Long> {

    List<OrgSettingEntity> listBySystemAndTenant(String system, String tenant);

    List<OrgSettingEntity> listBySystemIsNull();

    void updateOrgSetting(OrgSettingEntity orgSettingEntity);


    void enableByUuid(long uuid, boolean enable);

    void deleteByAttrKeyAndSystem(String attrKey, String system);

    boolean isEnable(String attrKey, String system, String tenant);


    OrgSettingEntity getOrgSettingBySystemTenantAndAttrKey(String system, String tenant, String attrKey);

}
