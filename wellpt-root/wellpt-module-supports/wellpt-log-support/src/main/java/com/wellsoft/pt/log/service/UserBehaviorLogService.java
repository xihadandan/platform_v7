package com.wellsoft.pt.log.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.log.dao.UserBehaviorLogDao;
import com.wellsoft.pt.log.entity.UserBehaviorLogEntity;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年09月16日   chenq	 Create
 * </pre>
 */
public interface UserBehaviorLogService extends JpaService<UserBehaviorLogEntity, UserBehaviorLogDao, Long> {

    void saveUserBehaviorLog(UserBehaviorLogEntity entity);
}
