/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.wellsoft.pt.biz.dao.BizProcessItemOperationDao;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemOperationEntity;
import com.wellsoft.pt.biz.enums.EnumOperateType;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceService;
import com.wellsoft.pt.biz.service.BizProcessItemOperationService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;
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
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
@Service
public class BizProcessItemOperationServiceImpl extends AbstractJpaServiceImpl<BizProcessItemOperationEntity, BizProcessItemOperationDao, String> implements BizProcessItemOperationService {

    @Autowired
    private BizProcessItemInstanceService bizProcessItemInstanceService;

    /**
     * 业务事项记录日志
     *
     * @param itemInstUuid
     * @param enumOperateType
     */
    @Override
    @Transactional
    public void log(String itemInstUuid, EnumOperateType enumOperateType) {
        BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);
        this.log(itemInstanceEntity, enumOperateType);
    }

    /**
     * 业务事项记录日志
     *
     * @param itemInstanceEntity
     * @param enumOperateType
     */
    @Override
    @Transactional
    public void log(BizProcessItemInstanceEntity itemInstanceEntity, EnumOperateType enumOperateType) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String operatorName = userDetails.getUserName();
        String operatorId = userDetails.getUserId();
        String operateType = enumOperateType.getValue();
        Date operateTime = Calendar.getInstance().getTime();
        String opinionText = StringUtils.EMPTY;
        String repoFileUuids = StringUtils.EMPTY;
        String itemInstUuid = itemInstanceEntity.getUuid();
        String processNodeInstUuid = itemInstanceEntity.getProcessNodeInstUuid();
        String processInstUuid = itemInstanceEntity.getProcessInstUuid();

        BizProcessItemOperationEntity entity = new BizProcessItemOperationEntity();
        entity.setOperatorName(operatorName);
        entity.setOperatorId(operatorId);
        entity.setOperateType(operateType);
        entity.setOperateTime(operateTime);
        entity.setOpinionText(opinionText);
        entity.setRepoFileUuids(repoFileUuids);
        entity.setItemInstUuid(itemInstUuid);
        entity.setProcessNodeInstUuid(processNodeInstUuid);
        entity.setProcessInstUuid(processInstUuid);
        this.dao.save(entity);
    }

    /**
     * 根据业务事项实例UUID获取业务事项操作列表
     *
     * @param itemInstUuid
     * @return
     */
    @Override
    public List<BizProcessItemOperationEntity> listByItemInstUuid(String itemInstUuid) {
        Assert.hasText("itemInstUuid", "业务事项实例UUID不能为空！");

        BizProcessItemOperationEntity entity = new BizProcessItemOperationEntity();
        entity.setItemInstUuid(itemInstUuid);
        return this.dao.listByEntity(entity);
    }
}
