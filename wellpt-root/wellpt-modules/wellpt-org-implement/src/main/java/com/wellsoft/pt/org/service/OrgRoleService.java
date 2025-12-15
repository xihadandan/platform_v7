package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgRoleDaoImpl;
import com.wellsoft.pt.org.entity.OrgRoleEntity;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月23日   chenq	 Create
 * </pre>
 */
public interface OrgRoleService extends JpaService<OrgRoleEntity, OrgRoleDaoImpl, Long> {

    Long saveOrgRole(OrgRoleEntity entity);

    void deleteOrgRole(Long uuid);

    void delteOrgRoleByOrgVersionUuid(Long orgVersionUuid);

    List<OrgRoleEntity> listByOrgVersionUuid(Long orgVersionUuid);

    List<OrgRoleEntity> listByOrgId(String orgId);

    boolean existRoleIdExcludeUuid(String id, Long orgVersionUuid, Long uuid);
}
