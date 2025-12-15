package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.BizOrgRoleDaoImpl;
import com.wellsoft.pt.org.entity.BizOrgRoleEntity;

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
public interface BizOrgRoleService extends JpaService<BizOrgRoleEntity, BizOrgRoleDaoImpl, Long> {

    BizOrgRoleEntity getById(String id);

    BizOrgRoleEntity getByIdAndBizOrgUuid(String id, Long bizOrgUuid);
}
