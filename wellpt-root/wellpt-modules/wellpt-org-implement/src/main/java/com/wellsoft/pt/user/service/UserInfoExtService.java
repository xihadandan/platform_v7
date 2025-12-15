package com.wellsoft.pt.user.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.user.dao.UserInfoExtDaoImpl;
import com.wellsoft.pt.user.entity.UserInfoExtEntity;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月01日   chenq	 Create
 * </pre>
 */
public interface UserInfoExtService extends JpaService<UserInfoExtEntity, UserInfoExtDaoImpl, Long> {

    void saveUserInfoExt(UserInfoExtEntity entity);

    List<UserInfoExtEntity> getByUserUuid(String userUuid);

    UserInfoExtEntity getByUserUuidAndAttrKey(String userUuid, String attrKey);
}
