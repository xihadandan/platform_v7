package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppProdVersionSettingDao;
import com.wellsoft.pt.app.entity.AppProdVersionEntity;
import com.wellsoft.pt.app.entity.AppProdVersionSettingEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年08月10日   chenq	 Create
 * </pre>
 */
public interface AppProdVersionSettingService extends JpaService<AppProdVersionSettingEntity, AppProdVersionSettingDao, Long> {

    void saveAppProdVersionSetting(AppProdVersionSettingEntity setting);

    AppProdVersionSettingEntity getByProdVersionId(String prodVersionId);

    AppProdVersionSettingEntity getByProdVersionUuid(Long prodVersionUuid);

    AppProdVersionSettingEntity getLatestPublishedVersionSetting(String prodId);
}
