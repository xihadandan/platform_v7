/*
 * @(#)2/28/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.wellsoft.pt.biz.dao.BizProcessAssembleDao;
import com.wellsoft.pt.biz.entity.BizProcessAssembleEntity;
import com.wellsoft.pt.biz.service.BizProcessAssembleService;
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
 * 2/28/24.1	zhulh		2/28/24		Create
 * </pre>
 * @date 2/28/24
 */
@Service
public class BizProcessAssembleServiceImpl extends AbstractJpaServiceImpl<BizProcessAssembleEntity, BizProcessAssembleDao, Long>
        implements BizProcessAssembleService {

    /**
     * 根据业务流程定义UUID获取业务装配信息
     *
     * @param processDefUuid
     * @return
     */
    @Override
    public BizProcessAssembleEntity getByProcessDefUuid(String processDefUuid) {
        Assert.hasLength(processDefUuid, "业务流程定义UUID不能为空！");

        BizProcessAssembleEntity entity = new BizProcessAssembleEntity();
        entity.setProcessDefUuid(processDefUuid);
        List<BizProcessAssembleEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

}
