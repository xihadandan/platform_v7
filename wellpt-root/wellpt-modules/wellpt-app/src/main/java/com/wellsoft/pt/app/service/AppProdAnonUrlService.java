package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppProdAnonUrlDao;
import com.wellsoft.pt.app.entity.AppProdAnonUrlEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

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
public interface AppProdAnonUrlService extends JpaService<AppProdAnonUrlEntity, AppProdAnonUrlDao, Long> {

    void saveProdVersionAnonUrls(Long prodVersionUuid, List<AppProdAnonUrlEntity> urls);

    List<AppProdAnonUrlEntity> listByProdVersionUuid(Long prodVersionUuid);

    List<AppProdAnonUrlEntity> listByProdVersionId(String prodVersionId);

    List<AppProdAnonUrlEntity> getAllPublishedAnonProdUrls();
}
