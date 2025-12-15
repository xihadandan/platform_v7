package com.wellsoft.pt.org.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.org.dao.UserNameI18nDao;
import com.wellsoft.pt.user.entity.UserNameI18nEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年03月14日   chenq	 Create
 * </pre>
 */
@Repository
public class UserNameI18nDaoImpl extends AbstractJpaDaoImpl<UserNameI18nEntity, Long> implements UserNameI18nDao {
    @Override
    @Transactional
    public void saveAllAfterDelete(String userUuid, List<UserNameI18nEntity> newList) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userUuid", userUuid);
        this.deleteByHQL("delete from UserNameI18nEntity where  userUuid=:userUuid", params);
        this.saveAll(newList);
    }

    @Override
    @Transactional
    public void saveOrUpdateUserName(String userName, String userId, String userUuid, String locale) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("locale", locale);
        UserNameI18nEntity entity = getOneByHQL("from UserNameI18nEntity where userId=:userId and locale=:locale", params);
        if (entity != null) {
            entity.setUserName(userName);
        } else {
            entity = new UserNameI18nEntity();
            entity.setUserName(userName);
            entity.setUserId(userId);
            entity.setUserUuid(userUuid);
            entity.setLocale(locale);
        }
        save(entity);

    }
}
