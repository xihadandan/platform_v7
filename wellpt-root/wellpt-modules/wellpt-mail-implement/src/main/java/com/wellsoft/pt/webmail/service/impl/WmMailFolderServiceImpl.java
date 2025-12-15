/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailFolderDto;
import com.wellsoft.pt.webmail.dao.WmMailFolderDao;
import com.wellsoft.pt.webmail.entity.WmMailFolderEntity;
import com.wellsoft.pt.webmail.service.WmMailFolderService;
import com.wellsoft.pt.webmail.service.WmMailUseCapacityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Description: 邮件文件夹服务实现类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年2月27日.1	chenqiong		2018年2月27日		Create
 * </pre>
 * @date 2018年2月27日
 */
@Service
public class WmMailFolderServiceImpl extends
        AbstractJpaServiceImpl<WmMailFolderEntity, WmMailFolderDao, String>
        implements WmMailFolderService {
    @Resource
    WmMailUseCapacityService wmMailUseCapacityService;

    /**
     * 重命名文件夹名称
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.service.WmMailFolderService#renameFolder(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void renameFolder(String uuid, String rename) {
        WmMailFolderEntity entity = getOne(uuid);
        if (entity != null) {
            entity.setFolderName(rename);
            if (existSameNameFolder(entity.getUserId(), entity.getFolderName())) {
                throw new RuntimeException("已存在同名的文件夹");
            }
            this.dao.save(entity);
        }
    }

    /**
     * 新增文件夹
     */
    @Override
    @Transactional
    public WmMailFolderEntity addFolder(WmMailFolderDto dto) {
        WmMailFolderEntity entity = new WmMailFolderEntity();
        entity.setFolderName(dto.getFolderName());
        entity.setUserId(SpringSecurityUtils.getCurrentUserId());
        entity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        if (existSameNameFolder(entity.getUserId(), entity.getFolderName())) {
            throw new RuntimeException("已存在同名的文件夹");
        }
        entity.setFolderCode(
                String.format("%s_%s_%s", WmMailFolderEntity.FOLDER_CODE_PREFIX, entity.getUserId(),
                        DateFormatUtils.format(Calendar.getInstance(), "yyyyMMddHHmmss")));
        this.dao.save(entity);

        //保存用户使用空间
        if (StringUtils.isBlank(dto.getUuid())) {
            wmMailUseCapacityService.addUseCapacity(entity.getUserId(), entity.getFolderCode(), 0L);
        }

        return entity;
    }

    @Override
    public List<WmMailFolderEntity> queryUserFolders(String userId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        return this.dao.listByHQL(
                "FROM WmMailFolderEntity WHERE userId=:userId order by seq asc,createTime asc",
                params);
    }

    @Override
    public boolean existSameNameFolder(String userId, String folerName) {
        WmMailFolderEntity entity = new WmMailFolderEntity();
        entity.setFolderName(folerName);
        entity.setUserId(userId);
        return CollectionUtils.isNotEmpty(this.dao.listByEntity(entity));
    }

}
