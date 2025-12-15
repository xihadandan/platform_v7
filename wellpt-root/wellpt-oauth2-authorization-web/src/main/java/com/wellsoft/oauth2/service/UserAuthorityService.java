package com.wellsoft.oauth2.service;

import com.wellsoft.oauth2.entity.UserAuthorityEntity;
import com.wellsoft.oauth2.repository.UserAuthorityRepository;

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
public interface UserAuthorityService extends
        JpaService<UserAuthorityEntity, Long, UserAuthorityRepository> {

    List<UserAuthorityEntity> listByAccountNumber(String accountNumber);
}
