/*
 * @(#)4/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.service.impl;

import com.wellsoft.pt.basicdata.material.dao.CdMaterialDao;
import com.wellsoft.pt.basicdata.material.entity.CdMaterialEntity;
import com.wellsoft.pt.basicdata.material.service.CdMaterialService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 4/27/23.1	zhulh		4/27/23		Create
 * </pre>
 * @date 4/27/23
 */
@Service
public class CdMaterialServiceImpl extends AbstractJpaServiceImpl<CdMaterialEntity, CdMaterialDao, Long>
        implements CdMaterialService {

    /**
     * 根据材料编码、业务数据UUID及业务场景获取业务材料
     *
     * @param materialCode
     * @param dataUuid
     * @param purpose
     * @return
     */
    @Override
    public CdMaterialEntity getByMaterialCodeAndDataUuidAndPurpose(String materialCode, String dataUuid, String purpose) {
        Assert.hasLength(materialCode, "材料编码不能为空！");
        Assert.hasLength(dataUuid, "所在业务数据UUID！");
        Assert.hasLength(purpose, "所在业务场景不能为空！");

        CdMaterialEntity entity = new CdMaterialEntity();
        entity.setMaterialCode(materialCode);
        entity.setDataUuid(dataUuid);
        entity.setPurpose(purpose);
        List<CdMaterialEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

}
