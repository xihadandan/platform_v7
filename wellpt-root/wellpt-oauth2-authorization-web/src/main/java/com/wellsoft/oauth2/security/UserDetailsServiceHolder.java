package com.wellsoft.oauth2.security;

import com.wellsoft.oauth2.entity.UserAccountEntity;
import com.wellsoft.oauth2.entity.UserAuthorityEntity;
import com.wellsoft.oauth2.entity.UserInfoEntity;
import com.wellsoft.oauth2.service.UserAccountService;
import com.wellsoft.oauth2.service.UserAuthorityService;
import com.wellsoft.oauth2.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/24
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/24    chenq		2019/9/24		Create
 * </pre>
 */
@Component
public class UserDetailsServiceHolder {


    @Autowired
    UserAccountService userAccountService;

    @Autowired
    UserAuthorityService userAuthorityService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;

    public UserAccountEntity loadUserByAccountNumber(String accountNumber) {
        UserAccountEntity userAccountEntity = userAccountService.getByAccountNumber(accountNumber);
        if (userAccountEntity != null) {
            UserInfoEntity userInfoEntity = userInfoService.getBy("accountNumber", accountNumber);
            if (userInfoEntity != null) {
                userAccountEntity.setUserName(userInfoEntity.getUserName());
            }

            return userAccountEntity;
        }

        return null;
    }

    public List<UserAuthorityEntity> getAllAuthorityByAccountNumber(String accountNumber) {
        return userAuthorityService.listByAccountNumber(accountNumber);

    }

    public void changePassword(String newPassword, String oldPassword, String username) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
        userAccountService.updateUserPassword(username, null,
                passwordEncoder.encode(newPassword));
    }
}
