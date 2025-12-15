package com.wellsoft.oauth2.service.impl;

import com.wellsoft.oauth2.dto.UserDto;
import com.wellsoft.oauth2.entity.UserAccountEntity;
import com.wellsoft.oauth2.entity.UserAuthorityEntity;
import com.wellsoft.oauth2.entity.UserExtAccountEntity;
import com.wellsoft.oauth2.entity.UserInfoEntity;
import com.wellsoft.oauth2.repository.UserAccountRepository;
import com.wellsoft.oauth2.repository.UserExtAccountRepository;
import com.wellsoft.oauth2.repository.UserInfoRepository;
import com.wellsoft.oauth2.service.UserAccountService;
import com.wellsoft.oauth2.service.UserAuthorityService;
import com.wellsoft.oauth2.token.PrincipalSourceEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

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
@Service
public class UserAccountServiceImpl extends
        AbstractJpaServiceImpl<UserAccountEntity, Long, UserAccountRepository> implements
        UserAccountService {

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    UserExtAccountRepository userExtAccountRepository;

    @Autowired
    UserAuthorityService userAuthorityService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public int updateUserPassword(String accoutNumber, String old, String _new) {
        return StringUtils.isBlank(old) ? this.repository.updateUserPassword(accoutNumber,
                _new) : this.repository.updateUserPassword(accoutNumber, old
                , _new);
    }

    @Override
    @Transactional
    public Long addUser(UserDto userDto) {
        UserAccountEntity accountEntity = new UserAccountEntity();
        accountEntity.setAccountNumber(userDto.getAccountNumber());
        accountEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        this.repository.save(accountEntity);

        UserInfoEntity userInfoEntity = new UserInfoEntity();
        BeanUtils.copyProperties(userDto, userInfoEntity);
        userInfoRepository.save(userInfoEntity);
        return userInfoEntity.getUuid();
    }


    @Override
    public UserAccountEntity getByAccountNumber(String accountNumber) {
        return getBy("accountNumber", accountNumber);
    }


    @Override
    @Transactional
    public UserAccountEntity addExtAccountWhenNotExist(String externalAccount,
                                                       PrincipalSourceEnum sourceEnum,
                                                       String name) {
        UserExtAccountEntity extAccountEntity = userExtAccountRepository.findByExtAccountNumberAndSource(
                externalAccount, sourceEnum.toString());
        if (extAccountEntity != null) {
            return repository.findOne(extAccountEntity.getAccountUuid());
        }

        //新增外部账号
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.setAccountNumber(generateRandomAccountNumber(4));//生成随机账号
        userAccountEntity.setPassword(userAccountEntity.DEFAULT_PASSWORD);
        userAccountEntity.setEnabled(true);
        repository.save(userAccountEntity);

        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setAccountNumber(userAccountEntity.getAccountNumber());
        userInfoEntity.setUserName(
                StringUtils.isBlank(name) ? userAccountEntity.getAccountNumber() : name);
        userInfoRepository.save(userInfoEntity);

        extAccountEntity = new UserExtAccountEntity();
        extAccountEntity.setAccountUuid(userAccountEntity.getUuid());
        extAccountEntity.setExtAccountNumber(externalAccount);
        extAccountEntity.setSource(sourceEnum.toString());
        userExtAccountRepository.save(extAccountEntity);

        //添加默认用户角色
        UserAuthorityEntity userAuthorityEntity = new UserAuthorityEntity();
        userAuthorityEntity.setAccountNumber(userAccountEntity.getAccountNumber());
        userAuthorityEntity.setAuthority("ROLE_USER");
        userAuthorityService.save(userAuthorityEntity);

        return userAccountEntity;
    }

    @Override
    @Transactional
    public void expiredAccount(String accountNumber) {
        UserAccountEntity userAccountEntity = getByAccountNumber(accountNumber);
        if (userAccountEntity != null) {
            userAccountEntity.setEnabled(false);
            this.save(userAccountEntity);
        }
    }

    private String generateRandomAccountNumber(int num) {
        char[] chars = new char[]{
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
        };
        Object[] c = new Object[num];
        for (int i = 0; i < num; i++) {
            c[i] = chars[new Random(System.nanoTime()).nextInt(chars.length)];
        }
        return "U" + System.currentTimeMillis() + StringUtils.join(c);
    }
}
