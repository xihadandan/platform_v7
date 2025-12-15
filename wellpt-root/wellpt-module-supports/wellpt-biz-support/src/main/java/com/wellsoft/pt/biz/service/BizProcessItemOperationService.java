/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizProcessItemOperationDao;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemOperationEntity;
import com.wellsoft.pt.biz.enums.EnumOperateType;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface BizProcessItemOperationService extends JpaService<BizProcessItemOperationEntity, BizProcessItemOperationDao, String> {

    /**
     * 业务事项记录日志
     *
     * @param itemInstUuid
     * @param enumOperateType
     */
    void log(String itemInstUuid, EnumOperateType enumOperateType);

    /**
     * 业务事项记录日志
     *
     * @param itemInstanceEntity
     * @param enumOperateType
     */
    void log(BizProcessItemInstanceEntity itemInstanceEntity, EnumOperateType enumOperateType);

    /**
     * 根据业务事项实例UUID获取业务事项操作列表
     *
     * @param itemInstUuid
     * @return
     */
    List<BizProcessItemOperationEntity> listByItemInstUuid(String itemInstUuid);
}
