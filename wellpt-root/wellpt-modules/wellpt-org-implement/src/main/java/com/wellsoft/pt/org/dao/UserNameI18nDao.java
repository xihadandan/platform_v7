package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.user.entity.UserNameI18nEntity;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年03月11日   chenq	 Create
 * </pre>
 */
public interface UserNameI18nDao extends JpaDao<UserNameI18nEntity, Long> {

    void saveAllAfterDelete(String userUuid, List<UserNameI18nEntity> newList);

    void saveOrUpdateUserName(String userName, String userId, String userUuid, String locale);
}
