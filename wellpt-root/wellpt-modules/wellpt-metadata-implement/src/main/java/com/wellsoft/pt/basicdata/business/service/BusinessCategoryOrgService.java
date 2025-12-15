/*
 * @(#)2019-02-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.business.dao.BusinessCategoryOrgDao;
import com.wellsoft.pt.basicdata.business.dto.BusinessCategoryOrgDto;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryOrgEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据库表BUSINESS_CATEGORY_ORG的service服务接口
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-02-14.1	leo		2019-02-14		Create
 * </pre>
 * @date 2019-02-14
 */
public interface BusinessCategoryOrgService extends
        JpaService<BusinessCategoryOrgEntity, BusinessCategoryOrgDao, String> {

    public TreeNode findAsTree(String categoryId);

    public BusinessCategoryOrgDto get(String uuid);

    public String save(BusinessCategoryOrgDto dto);

    public void deleteById(String uuid);

    public JqGridQueryData queryByManage(JqGridQueryInfo queryInfo, String value);

    List<BusinessCategoryOrgEntity> listByParentUuid(String uuid);

    /**
     * @param categoryUuid
     * @param deptId
     * @return
     */
    public BusinessCategoryOrgEntity getByCategoryUuidAndDeptId(String categoryUuid, String deptId);

    /**
     * 获取业务通讯录实体
     *
     * @param id
     * @return
     */
    public BusinessCategoryOrgEntity getBusinessById(String id);

    /**
     * 更新旧分类Id
     */
    public String updateOldId();

    List<BusinessCategoryOrgEntity> getBusinessByIds(List<String> ids);

    List<BusinessCategoryOrgEntity> listByUuid(String categoryUuid);
}
