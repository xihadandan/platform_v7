package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dao.AppProdAnonUrlDao;
import com.wellsoft.pt.app.entity.AppProdAnonUrlEntity;
import com.wellsoft.pt.app.entity.AppProdVersionEntity;
import com.wellsoft.pt.app.service.AppProdAnonUrlService;
import com.wellsoft.pt.app.service.AppProdVersionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
 * 2023年08月10日   chenq	 Create
 * </pre>
 */
@Service
public class AppProdAnonUrlServiceImpl extends AbstractJpaServiceImpl<AppProdAnonUrlEntity, AppProdAnonUrlDao, Long> implements AppProdAnonUrlService {

    @Autowired
    AppProdVersionService appProdVersionService;

    @Override
    @Transactional
    public void saveProdVersionAnonUrls(Long prodVersionUuid, List<AppProdAnonUrlEntity> urls) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("prodVersionUuid", prodVersionUuid);
        dao.deleteByHQL("delete from AppProdAnonUrlEntity where prodVersionUuid=:prodVersionUuid", params);
        AppProdVersionEntity versionEntity = appProdVersionService.getOne(prodVersionUuid);
        if (versionEntity != null && CollectionUtils.isNotEmpty(urls)) {
            List<AppProdAnonUrlEntity> anonUrls = Lists.newArrayListWithCapacity(urls.size());
            for (AppProdAnonUrlEntity url : urls) {
                AppProdAnonUrlEntity e = new AppProdAnonUrlEntity();
                if (StringUtils.isBlank(url.getUrl()) && StringUtils.isBlank(url.getPageId())) {
                    continue;
                }
                e.setUrl(url.getUrl());
                e.setPageId(url.getPageId());
                e.setType(url.getType());
                e.setProdVersionUuid(versionEntity.getUuid());
                e.setProdVersionId(versionEntity.getVersionId());
                e.setDeviceType(url.getDeviceType());
                anonUrls.add(e);
            }
            saveAll(anonUrls);
        }
    }

    @Override
    public List<AppProdAnonUrlEntity> listByProdVersionUuid(Long prodVersionUuid) {
        return dao.listByFieldEqValue("prodVersionUuid", prodVersionUuid);
    }

    @Override
    public List<AppProdAnonUrlEntity> listByProdVersionId(String prodVersionId) {
        return dao.listByFieldEqValue("prodVersionId", prodVersionId);
    }

    @Override
    public List<AppProdAnonUrlEntity> getAllPublishedAnonProdUrls() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("status", AppProdVersionEntity.Status.PUBLISHED);
        return dao.listByHQL("from AppProdAnonUrlEntity a where exists (" +
                "select 1 from AppProdVersionEntity v where v.status=:status and v.uuid =a.prodVersionUuid  )", params);
    }


}
