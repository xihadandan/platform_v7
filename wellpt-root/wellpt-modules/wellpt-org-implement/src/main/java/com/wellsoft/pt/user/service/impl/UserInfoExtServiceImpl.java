package com.wellsoft.pt.user.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.user.dao.UserInfoExtDaoImpl;
import com.wellsoft.pt.user.entity.UserInfoExtEntity;
import com.wellsoft.pt.user.service.UserInfoExtService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月01日   chenq	 Create
 * </pre>
 */
@Service
public class UserInfoExtServiceImpl extends AbstractJpaServiceImpl<UserInfoExtEntity, UserInfoExtDaoImpl, Long> implements UserInfoExtService {
    @Override
    @Transactional
    public void saveUserInfoExt(UserInfoExtEntity entity) {
        UserInfoExtEntity infoExtEntity = getByUserUuidAndAttrKey(entity.getUserUuid(), entity.getAttrKey());
        if (infoExtEntity == null) {
            infoExtEntity = new UserInfoExtEntity();
            infoExtEntity.setAttrKey(entity.getAttrKey());
            infoExtEntity.setUserUuid(entity.getUserUuid());
        }
        infoExtEntity.setAttrValue(entity.getAttrValue());
        save(infoExtEntity);
    }

    @Override
    public List<UserInfoExtEntity> getByUserUuid(String userUuid) {
        return this.dao.listByFieldEqValue("userUuid", userUuid);
    }

    @Override
    public UserInfoExtEntity getByUserUuidAndAttrKey(String userUuid, String attrKey) {
        UserInfoExtEntity infoExtEntity = new UserInfoExtEntity();
        infoExtEntity.setAttrKey(attrKey);
        infoExtEntity.setUserUuid(userUuid);
        List<UserInfoExtEntity> list = dao.listByEntity(infoExtEntity);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }
}
