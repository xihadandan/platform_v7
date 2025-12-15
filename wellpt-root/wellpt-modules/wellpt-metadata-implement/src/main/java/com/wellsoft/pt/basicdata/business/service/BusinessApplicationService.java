/*
 * @(#)2019-02-21 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.basicdata.business.dao.BusinessApplicationDao;
import com.wellsoft.pt.basicdata.business.dto.BusinessApplicationConfigDto;
import com.wellsoft.pt.basicdata.business.dto.BusinessApplicationDto;
import com.wellsoft.pt.basicdata.business.entity.BusinessApplicationEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据库表BUSINESS_APPLICATION的service服务接口
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-02-21.1	leo		2019-02-21		Create
 * </pre>
 * @date 2019-02-21
 */
public interface BusinessApplicationService extends
        JpaService<BusinessApplicationEntity, BusinessApplicationDao, String> {

    public List<BusinessApplicationConfigDto> findBusinessApplicationConfig(String uuid);

    public void save(BusinessApplicationDto dto);

    public void deleteByIds(String[] uuids);

    public Select2QueryData querySelectDataForFormApp(Select2QueryInfo select2QueryInfo);

    public Select2QueryData loadSelectDataForFormApp(Select2QueryInfo select2QueryInfo);

    /**
     * @param categoryUuid
     * @return
     */
    public List<BusinessApplicationConfigDto> findBusinessApplicationConfigByCategoryUuid(String categoryUuid);

}
