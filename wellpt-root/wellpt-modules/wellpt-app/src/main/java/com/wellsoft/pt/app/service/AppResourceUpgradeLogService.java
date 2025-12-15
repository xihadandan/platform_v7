package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppResourceUpgradeLogDao;
import com.wellsoft.pt.app.entity.AppResourceUpgradeLogEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface AppResourceUpgradeLogService extends JpaService<AppResourceUpgradeLogEntity, AppResourceUpgradeLogDao, Long> {

    Long saveLog(AppResourceUpgradeLogEntity entity);

    List<AppResourceUpgradeLogEntity> getById(String id);
}
