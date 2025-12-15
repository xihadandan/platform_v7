package com.wellsoft.pt.app.service.impl;

import com.wellsoft.pt.app.dao.AppResourceUpgradeLogDao;
import com.wellsoft.pt.app.entity.AppResourceUpgradeLogEntity;
import com.wellsoft.pt.app.service.AppResourceUpgradeLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
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
 * 2023年07月18日   chenq	 Create
 * </pre>
 */
@Service
public class AppResourceUpgradeLogServiceImpl extends AbstractJpaServiceImpl<AppResourceUpgradeLogEntity, AppResourceUpgradeLogDao, Long> implements AppResourceUpgradeLogService {
    @Override
    @Transactional
    public Long saveLog(AppResourceUpgradeLogEntity entity) {
        save(entity);
        return entity.getUuid();
    }

    @Override
    public List<AppResourceUpgradeLogEntity> getById(String id) {
        return dao.listByFieldEqValue("id", id);
    }
}
