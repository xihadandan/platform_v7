package com.wellsoft.pt.security.config.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.config.dao.impl.AppLoginSecurityConfigDaoImpl;
import com.wellsoft.pt.security.config.entity.AppLoginSecurityConfigEntity;

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
public interface AppLoginSecurityConfigService extends JpaService<AppLoginSecurityConfigEntity, AppLoginSecurityConfigDaoImpl, String> {

    boolean isAllowMultiDeviceLogin(String systemUnitId);

    AppLoginSecurityConfigEntity getBySystemUnitId(String systemUnitId);

    AppLoginSecurityConfigEntity getBySystemAndTenant(String system, String tenant);

    boolean isAllowMultiDeviceLogin(String system, String tenant);

}
