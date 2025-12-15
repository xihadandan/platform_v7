package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgGroupRoleDaoImpl;
import com.wellsoft.pt.org.entity.OrgGroupRoleEntity;

import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月10日   chenq	 Create
 * </pre>
 */
public interface OrgGroupRoleService extends JpaService<OrgGroupRoleEntity, OrgGroupRoleDaoImpl, Long> {

    Set<String> queryRoleUuidsByGroupMemberIds(List<String> memberIds);

    void deleteByRoleUuid(String roleUuid);
}
