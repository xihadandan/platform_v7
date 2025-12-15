package com.wellsoft.oauth2.repository;

import com.wellsoft.oauth2.entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/23    chenq		2019/9/23		Create
 * </pre>
 */
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Long> {
}
