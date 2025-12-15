/*
 * @(#)2018年3月1日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailTagDto;
import com.wellsoft.pt.webmail.dao.WmMailTagDao;
import com.wellsoft.pt.webmail.entity.WmMailTagEntity;
import com.wellsoft.pt.webmail.service.WmMailRelaTagService;
import com.wellsoft.pt.webmail.service.WmMailTagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Description: 邮件标签服务类
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
public class WmMailTagServiceImpl extends AbstractJpaServiceImpl<WmMailTagEntity, WmMailTagDao, String> implements
        WmMailTagService {

    @Resource
    WmMailRelaTagService wmMailRelaTagService;

    @Override
    @Transactional
    public void updateTag(WmMailTagDto dto) {
        WmMailTagEntity entity = getOne(dto.getUuid());
        if (entity != null) {
            if (StringUtils.isNotBlank(dto.getTagName())) {
                entity.setTagName(dto.getTagName());
            }
            if (StringUtils.isNotBlank(dto.getTagColor())) {
                entity.setTagColor(dto.getTagColor());
            }
            this.dao.save(entity);
        }

    }

    @Override
    @Transactional
    public WmMailTagEntity addMailTag(WmMailTagDto dto) {
        WmMailTagEntity entity = new WmMailTagEntity();
        entity.setTagName(dto.getTagName());
        entity.setTagColor(dto.getTagColor());
        entity.setUserId(SpringSecurityUtils.getCurrentUserId());
        entity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        this.dao.save(entity);
        return entity;
    }

    @Override
    public List<WmMailTagEntity> queryUserMailTags(String userId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("userId", userId);
        return this.dao.listByHQL("from WmMailTagEntity where userId=:userId order by seq asc,createTime asc", param);
    }

    @Override
    @Transactional
    public void deleteTagAndEmailRela(String uuid) {
        this.dao.delete(uuid);
        wmMailRelaTagService.deleteByTagUuid(uuid);
    }
}
