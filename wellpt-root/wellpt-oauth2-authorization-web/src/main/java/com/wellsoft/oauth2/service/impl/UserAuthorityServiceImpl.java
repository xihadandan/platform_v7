package com.wellsoft.oauth2.service.impl;

import com.wellsoft.oauth2.entity.UserAuthorityEntity;
import com.wellsoft.oauth2.repository.UserAuthorityRepository;
import com.wellsoft.oauth2.service.UserAuthorityService;
import org.springframework.stereotype.Service;

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
@Service
public class UserAuthorityServiceImpl extends
        AbstractJpaServiceImpl<UserAuthorityEntity, Long, UserAuthorityRepository> implements
        UserAuthorityService {
    @Override
    public List<UserAuthorityEntity> listByAccountNumber(String accountNumber) {
        UserAuthorityEntity inst = new UserAuthorityEntity();
        inst.setAccountNumber(accountNumber);
        return listByExample(inst);
    }
}
