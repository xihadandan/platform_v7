package com.wellsoft.pt.user.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.user.dao.UserTypeRoleDaoImpl;
import com.wellsoft.pt.user.entity.UserTypeRoleEntity;
import com.wellsoft.pt.user.enums.UserTypeEnum;

import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月13日   chenq	 Create
 * </pre>
 */
public interface UserTypeRoleService extends JpaService<UserTypeRoleEntity, UserTypeRoleDaoImpl, String> {

    Set<String> getUserTypeRoles(UserTypeEnum type);

    void saveUserTypeRole(UserTypeEnum type, Set<String> roles);

    void saveUserTypeRole(Map<String, Set<String>> data);
}
