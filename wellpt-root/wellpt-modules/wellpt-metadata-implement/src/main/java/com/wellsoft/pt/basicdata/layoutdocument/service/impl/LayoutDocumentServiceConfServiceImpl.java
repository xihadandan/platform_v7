/*
 * @(#)2021-12-02 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.layoutdocument.service.impl;

import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.pt.basicdata.layoutdocument.dao.LayoutDocumentServiceConfDao;
import com.wellsoft.pt.basicdata.layoutdocument.dto.LayoutDocumentServiceConfDto;
import com.wellsoft.pt.basicdata.layoutdocument.entity.LayoutDocumentServiceConfEntity;
import com.wellsoft.pt.basicdata.layoutdocument.service.LayoutDocumentServiceConfService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

/**
 * Description: 数据库表LAYOUT_DOCUMENT_SERVICE_CONF的service服务接口实现类
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-12-02.1	shenhb		2021-12-02		Create
 * </pre>
 * @date 2021-12-02
 */
@Service
public class LayoutDocumentServiceConfServiceImpl extends AbstractJpaServiceImpl<LayoutDocumentServiceConfEntity, LayoutDocumentServiceConfDao, String> implements LayoutDocumentServiceConfService {

    public static String ERROR_HAS_OTHER_ENABLE_CONFIG = "HAS_OTHER_ENABLE_CONFIG";

    public static String STATUS_ENABLE = "1";

    public static String STATUS_DISENABLE = "0";

    @Override
    @Transactional
    public void saveBean(LayoutDocumentServiceConfDto layoutDocumentServiceConfDto) {
        LayoutDocumentServiceConfEntity layoutDocumentServiceConfEntity;
        if (StringUtils.isBlank(layoutDocumentServiceConfDto.getUuid())) {
            layoutDocumentServiceConfEntity = new LayoutDocumentServiceConfEntity();
        } else {
            layoutDocumentServiceConfEntity = this.getOne(layoutDocumentServiceConfDto.getUuid());
        }

        BeanUtils.copyProperties(layoutDocumentServiceConfDto, layoutDocumentServiceConfEntity);

        // 启用，更新其它配置status
        if (STATUS_ENABLE.equals(layoutDocumentServiceConfDto.getStatus())) {
            LayoutDocumentServiceConfEntity queryItem = new LayoutDocumentServiceConfEntity();
            queryItem.setStatus(STATUS_ENABLE);
            List<LayoutDocumentServiceConfEntity> layoutDocumentServiceConfEntities = this.listByEntity(queryItem);
            if (CollectionUtils.isNotEmpty(layoutDocumentServiceConfEntities)) {
                for (LayoutDocumentServiceConfEntity iterEntity : layoutDocumentServiceConfEntities) {
                    if (!StringUtils.equals(layoutDocumentServiceConfEntity.getUuid(), iterEntity.getUuid())) {
                        iterEntity.setStatus(STATUS_DISENABLE);
                        this.save(iterEntity);
                    }
                }
            }
        }
        this.save(layoutDocumentServiceConfEntity);
    }

    @Override
    public String beforeEnableLayoutDocumentConfig(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new BusinessException("uuid不能为空！");
        }

        LayoutDocumentServiceConfEntity queryItem = new LayoutDocumentServiceConfEntity();
        queryItem.setStatus(STATUS_ENABLE);
        List<LayoutDocumentServiceConfEntity> layoutDocumentServiceConfEntities = this.listByEntity(queryItem);
        if (CollectionUtils.isNotEmpty(layoutDocumentServiceConfEntities)) {
            for (Iterator<LayoutDocumentServiceConfEntity> iterator = layoutDocumentServiceConfEntities.iterator(); iterator.hasNext(); ) {
                LayoutDocumentServiceConfEntity next = iterator.next();
                if (uuid.equals(next.getUuid())) {
                    iterator.remove();
                }
            }
        }

        if (CollectionUtils.isNotEmpty(layoutDocumentServiceConfEntities)) {
            return ERROR_HAS_OTHER_ENABLE_CONFIG;
        }
        return StringUtils.EMPTY;
    }

    @Override
    @Transactional
    public void changeLayoutDocumentConfigStatus(String uuid, String status) {
        if (StringUtils.isBlank(uuid)) {
            throw new BusinessException("uuid不能为空！");
        }

        LayoutDocumentServiceConfEntity entity = null;
        entity = this.getOne(uuid);
        if (entity == null) {
            throw new BusinessException("uuid有误，请校验！");
        }

        if (STATUS_DISENABLE.equals(status)) {
            entity.setStatus(STATUS_DISENABLE);
            this.save(entity);
        } else {
            entity = null;
            LayoutDocumentServiceConfEntity queryItem = new LayoutDocumentServiceConfEntity();
            queryItem.setStatus(STATUS_ENABLE);
            List<LayoutDocumentServiceConfEntity> layoutDocumentServiceConfEntities = this.listByEntity(queryItem);
            if (CollectionUtils.isNotEmpty(layoutDocumentServiceConfEntities)) {
                for (LayoutDocumentServiceConfEntity layoutDocumentServiceConfEntity : layoutDocumentServiceConfEntities) {
                    if (uuid.equals(layoutDocumentServiceConfEntity.getUuid())) {
                        entity = layoutDocumentServiceConfEntity;
                    } else {
                        layoutDocumentServiceConfEntity.setStatus(STATUS_DISENABLE);
                        this.save(layoutDocumentServiceConfEntity);
                    }
                }
            }

            if (entity == null) {
                entity = this.getOne(uuid);
                entity.setStatus(STATUS_ENABLE);
                this.save(entity);
            }
        }
    }


}
