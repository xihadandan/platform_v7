package com.wellsoft.oauth2.repository;

import com.wellsoft.oauth2.entity.UserExtAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

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
public interface UserExtAccountRepository extends JpaRepository<UserExtAccountEntity, Long> {

    UserExtAccountEntity findByExtAccountNumberAndSource(String externalAccount, String source);
}
