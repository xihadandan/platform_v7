package com.wellsoft.oauth2.service;

import com.wellsoft.oauth2.entity.UserInfoEntity;
import com.wellsoft.oauth2.repository.UserInfoRepository;

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
public interface UserInfoService extends JpaService<UserInfoEntity, Long, UserInfoRepository> {
    void deleteUser(Long uuid);

    void delteUsers(Long[] uuids);
}
