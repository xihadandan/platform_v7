package com.wellsoft.pt.log.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.log.dao.UserBehaviorLogDao;
import com.wellsoft.pt.log.entity.UserBehaviorLogEntity;
import com.wellsoft.pt.log.service.UserBehaviorLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
public class UserBehaviorLogServiceImpl extends AbstractJpaServiceImpl<UserBehaviorLogEntity, UserBehaviorLogDao, Long> implements UserBehaviorLogService {
    @Override
    @Transactional
    public void saveUserBehaviorLog(UserBehaviorLogEntity entity) {
        if (StringUtils.isNotBlank(entity.getBehaviorDesc()) && entity.getBehaviorDesc().length() > 300) {
            entity.setBehaviorDesc(entity.getBehaviorDesc().substring(0, 300));
        }
        dao.save(entity);
    }
}
