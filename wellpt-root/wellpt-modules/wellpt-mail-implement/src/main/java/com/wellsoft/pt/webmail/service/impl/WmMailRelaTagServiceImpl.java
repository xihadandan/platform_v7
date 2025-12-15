/*
 * @(#)2018年3月1日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.dao.WmMailRelaTagDao;
import com.wellsoft.pt.webmail.entity.WmMailRelaTagEntity;
import com.wellsoft.pt.webmail.service.WmMailRelaTagService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 邮件关联标签服务实现类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月1日.1	chenqiong		2018年3月1日		Create
 * </pre>
 * @date 2018年3月1日
 */
@Service
public class WmMailRelaTagServiceImpl extends AbstractJpaServiceImpl<WmMailRelaTagEntity, WmMailRelaTagDao, String>
        implements WmMailRelaTagService {

    @Override
    @Transactional
    public void deleteByTagUuid(String tagUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("tagUuid", tagUuid);
        this.dao.deleteByHQL("delete from WmMailRelaTagEntity where tagUuid=:tagUuid", param);
    }

    @Override
    @Transactional
    public void addMailRelaTag(String tagUuid, List<String> emailUuids) {
        List<WmMailRelaTagEntity> entities = Lists.newArrayList();
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        Map<String, Object> param = Maps.newHashMap();
        param.put("tagUuid", tagUuid);
        for (String emailUuid : emailUuids) {
            param.put("mailUuid", emailUuid);
            List exists = this.dao.listByHQL(
                    "select uuid from WmMailRelaTagEntity where tagUuid=:tagUuid and mailUuid=:mailUuid", param);
            if (CollectionUtils.isNotEmpty(exists)) {
                continue;
            }
            WmMailRelaTagEntity entity = new WmMailRelaTagEntity();
            entity.setTagUuid(tagUuid);
            entity.setMailUuid(emailUuid);
            entity.setSystemUnitId(systemUnitId);
            entities.add(entity);
        }
        this.dao.saveAll(entities);
    }

    @Override
    @Transactional
    public void deleteEmailRelaTag(List<String> emailUuids, String tagUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("tagUuid", tagUuid);
        param.put("emailUuids", emailUuids);
        this.dao.deleteByNamedHQL("deleteEmailRelaTag", param);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.service.WmMailRelaTagService#queryMailRelaTag(java.util.List)
     */
    @Override
    public List<WmMailRelaTagEntity> queryMailRelaTag(List<String> emailUuids) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("mailUuids", emailUuids);
        return this.dao.listByHQL("from WmMailRelaTagEntity where mailUuid in (:mailUuids) order by createTime asc",
                param);
    }

}
