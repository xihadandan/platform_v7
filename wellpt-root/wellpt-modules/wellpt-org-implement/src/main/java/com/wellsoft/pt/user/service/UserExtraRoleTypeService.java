package com.wellsoft.pt.user.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.user.dao.UserExtraRoleTypeDaoImpl;
import com.wellsoft.pt.user.entity.UserExtraRoleTypeEntity;
import com.wellsoft.pt.user.enums.UserTypeEnum;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年11月04日   shenhb	 Create
 * </pre>
 */
public interface UserExtraRoleTypeService extends JpaService<UserExtraRoleTypeEntity, UserExtraRoleTypeDaoImpl, String> {

    Set<UserTypeEnum> getUserExtraUserTypeEnums(String loginName);

    @Transactional
    void saveUserExtraRoleType(String loginName, UserTypeEnum type);

    @Transactional
    void deleteUserExtraRoleType(String loginName, UserTypeEnum type);
}
