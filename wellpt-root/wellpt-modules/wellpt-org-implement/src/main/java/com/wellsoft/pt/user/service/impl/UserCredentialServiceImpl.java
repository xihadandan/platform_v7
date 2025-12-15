package com.wellsoft.pt.user.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.user.dao.UserCredentialDaoImpl;
import com.wellsoft.pt.user.entity.UserCredentialEntity;
import com.wellsoft.pt.user.service.UserCredentialService;
import org.springframework.stereotype.Service;

import java.util.Map;

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
@Service
public class UserCredentialServiceImpl extends AbstractJpaServiceImpl<UserCredentialEntity, UserCredentialDaoImpl, String> implements UserCredentialService {
    @Override
    public void deleteByLoginName(String loginName) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("loginName", loginName);
        this.dao.deleteBySQL("delete from USER_CREDENTIAL where login_name = :loginName", params);
    }
}
