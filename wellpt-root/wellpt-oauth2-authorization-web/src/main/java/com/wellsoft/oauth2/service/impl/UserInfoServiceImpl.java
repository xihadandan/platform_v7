package com.wellsoft.oauth2.service.impl;

import com.wellsoft.oauth2.entity.UserAccountEntity;
import com.wellsoft.oauth2.entity.UserInfoEntity;
import com.wellsoft.oauth2.repository.UserInfoRepository;
import com.wellsoft.oauth2.service.UserAccountService;
import com.wellsoft.oauth2.service.UserInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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
public class UserInfoServiceImpl extends
        AbstractJpaServiceImpl<UserInfoEntity, Long, UserInfoRepository> implements
        UserInfoService {

    @Resource
    UserAccountService accountService;

    @Override
    @Transactional
    public void deleteUser(Long uuid) {
        UserInfoEntity infoEntity = getOne(uuid);
        UserAccountEntity accountEntity = accountService.getByAccountNumber(
                infoEntity.getAccountNumber());
        accountService.delete(accountEntity.getUuid());
        this.deleteEntity(infoEntity);
    }

    @Override
    @Transactional
    public void delteUsers(Long[] uuids) {
        for (Long id : uuids) {
            deleteUser(id);
        }
    }
}
