package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dao.AppProdVersionSettingDao;
import com.wellsoft.pt.app.entity.AppProdVersionEntity;
import com.wellsoft.pt.app.entity.AppProdVersionSettingEntity;
import com.wellsoft.pt.app.service.AppProdVersionSettingService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
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
public class AppProdVersionSettingServiceImpl extends AbstractJpaServiceImpl<AppProdVersionSettingEntity, AppProdVersionSettingDao, Long> implements AppProdVersionSettingService {
    @Override
    @Transactional
    public void saveAppProdVersionSetting(AppProdVersionSettingEntity setting) {
        AppProdVersionSettingEntity entity = setting.getUuid() != null ? getOne(setting.getUuid()) :
                (setting.getProdVersionUuid() != null ?
                        getByProdVersionUuid(setting.getProdVersionUuid()) : null);
        if (entity != null) {
            BeanUtils.copyProperties(setting, entity, ArrayUtils.addAll(entity.BASE_FIELDS, "prodVersionUuid", "prodVersionId"));
        } else {
            entity = new AppProdVersionSettingEntity();
            BeanUtils.copyProperties(setting, entity);
        }
        save(entity);
    }

    @Override
    public AppProdVersionSettingEntity getByProdVersionId(String prodVersionId) {
        return dao.getOneByFieldEq("prodVersionId", prodVersionId);
    }

    @Override
    public AppProdVersionSettingEntity getByProdVersionUuid(Long prodVersionUuid) {
        return dao.getOneByFieldEq("prodVersionUuid", prodVersionUuid);
    }

    @Override
    public AppProdVersionSettingEntity getLatestPublishedVersionSetting(String prodId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("prodId", prodId);
        param.put("status", AppProdVersionEntity.Status.PUBLISHED);
        List<AppProdVersionSettingEntity> list = dao.listByHQL("from AppProdVersionSettingEntity s where exists (" +
                " select 1 from AppProdVersionEntity v where v.prodId=:prodId and v.status=:status and s.prodVersionUuid = v.uuid and v.publishTime = (" +
                " select max(t.publishTime) from AppProdVersionEntity t where t.prodId=:prodId and t.status=:status" +
                ")" +
                ")", param);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }
}
