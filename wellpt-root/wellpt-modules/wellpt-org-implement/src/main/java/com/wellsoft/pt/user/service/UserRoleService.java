package com.wellsoft.pt.user.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.entity.UserRoleEntity;
import com.wellsoft.pt.user.dao.UserRoleDaoImpl;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月28日   chenq	 Create
 * </pre>
 */
public interface UserRoleService extends JpaService<UserRoleEntity, UserRoleDaoImpl, Long> {
    void deleteByUserUuid(String userUuid);

    void deleteByRoleUuid(String roleUuid);

    List<String> getRolesByUserUuid(String userUuid);

    void saveUserRole(List<String> roleUuids, String userUuid);

    void deleteUserRoleByRoleUuidAndUserUuids(String roleUuid, List<String> userUuids);
}
