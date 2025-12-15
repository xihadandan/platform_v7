/*
 * @(#)2019-02-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.basicdata.business.dao.BusinessCategoryDao;
import com.wellsoft.pt.basicdata.business.dto.BusinessCategoryDto;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据库表BUSINESS_CATEGORY的service服务接口
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
public interface BusinessCategoryService extends JpaService<BusinessCategoryEntity, BusinessCategoryDao, String> {

    public JqGridQueryData query(JqGridQueryInfo queryInfo);

    public JqGridQueryData queryByApplication(JqGridQueryInfo queryInfo, String value);

    public BusinessCategoryDto getBeanByUuid(String uuid);

    public void deleteByIds(String[] ids);

    public String save(BusinessCategoryDto vo);

    public String getBasicClassOaUnitElePath(String unitId);

    public void updateManageUser(List<String> list, String userId, String userName);

    public Select2QueryData querySelectDataFromMultiOrgSystemUnit(Select2QueryInfo select2QueryInfo);

    public Select2QueryData querySelectDataFromMultiOrgSystemUnitAll(Select2QueryInfo select2QueryInfo);

    public Select2QueryData loadSelectDataFromMultiOrgSystemUnit(Select2QueryInfo select2QueryInfo);

    public Select2QueryData querySelectDataFromBusinessCategory(Select2QueryInfo select2QueryInfo);

    public Select2QueryData loadSelectDataFromBusinessCategory(Select2QueryInfo select2QueryInfo);
}
