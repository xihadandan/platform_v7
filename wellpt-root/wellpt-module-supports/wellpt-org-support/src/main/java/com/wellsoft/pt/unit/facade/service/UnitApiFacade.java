/*
 * @(#)2018年1月10日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.unit.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.unit.bean.BusinessManage;
import com.wellsoft.pt.unit.entity.BusinessType;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.entity.CommonUnitTree;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年1月10日.1	chenqiong		2018年1月10日		Create
 * </pre>
 * @date 2018年1月10日
 */
public interface UnitApiFacade extends Facade {

    CommonUnitTree getCommonUnitTreeByUuid(String uuid);

    List<CommonUnit> getCommonUnitByTenantId(String tenantId);

    String getTenantIdByCommonUnitUnitId(String unitId);

    String getTenantIdByCommonUnitId(String unitId);

    List<CommonUnit> getCommonUnitsByBlurUnitName(String unitNameKey);

    List<String> getBusinessManagerByBusinessTypeId(String businessTypeId);

    List<CommonUnit> getCommonUnitsByBusinessTypeId(String businessTypeId);

    List<CommonUnit> getCommonUnitsByParentIdAndBusinessTypeId(String parentUnitId, String businessTypeId);

    List<CommonUnit> getCommonUnitsByBusinessTypeIdAndUserId(String businessTypeId, String userId);

    CommonUnit getCommonUnitByBusinessTypeId(String businessTypeId);

    BusinessManage getBusinessManage(String businessTypeId, String unitId, String userId);

    List<BusinessType> getBusinessTypes();

    List<BusinessType> getBusinessTypeList();

    List<OrgUserVo> getBusinessUnitManagerById(String businessTypeId, String unitId);

    List<User> getUnitManagerById(String businessTypeId);

    List<CommonUnit> getAllCommonUnits();

    List<CommonUnit> getCommonUnitsByIds(String ids);

    List<CommonUnit> getCommonUnitListByIds(String ids);

    CommonUnit getCommonUnitById(String id);

    List<TreeNode> getBusinessUnitTree(String businessTypeId, String commonUnitId);

    List<CommonUnit> getByUserId(String userId);

    CommonUnit getCommonUnitByUserId(String userId);

    List<String> getBusinessUnitUserIds(String businessTypeId, String commonUnitId, String bizRole);

    List<String> getBusinessManageUserIds(String businessTypeId, String commonUnitId, int type);

    List<com.wellsoft.context.component.tree.TreeNode> getBusinessUnitTreeByBusinessTypeId(String businessTypeId);

    List<BusinessManage> getAllBusinessManage(String businessTypeId, String userId);

}
