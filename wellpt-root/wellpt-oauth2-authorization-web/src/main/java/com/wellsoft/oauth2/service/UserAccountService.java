package com.wellsoft.oauth2.service;

import com.wellsoft.oauth2.dto.UserDto;
import com.wellsoft.oauth2.entity.UserAccountEntity;
import com.wellsoft.oauth2.repository.UserAccountRepository;
import com.wellsoft.oauth2.token.PrincipalSourceEnum;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/21    chenq		2019/9/21		Create
 * </pre>
 */
public interface UserAccountService extends
        JpaService<UserAccountEntity, Long, UserAccountRepository> {
    int updateUserPassword(String username, String old, String aNew);

    Long addUser(UserDto userDto);

    UserAccountEntity getByAccountNumber(String accountNumber);

    UserAccountEntity addExtAccountWhenNotExist(String externalAccount,
                                                PrincipalSourceEnum sourceEnum,
                                                String name);


    void expiredAccount(String accountNumber);
}
