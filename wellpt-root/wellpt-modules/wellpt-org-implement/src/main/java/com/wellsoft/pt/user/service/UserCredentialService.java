package com.wellsoft.pt.user.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.user.dao.UserCredentialDaoImpl;
import com.wellsoft.pt.user.entity.UserCredentialEntity;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月22日   chenq	 Create
 * </pre>
 */
public interface UserCredentialService extends JpaService<UserCredentialEntity, UserCredentialDaoImpl, String> {
    void deleteByLoginName(String loginName);
}
