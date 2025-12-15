/*
 * @(#)2019-02-21 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.service;


import com.wellsoft.pt.basicdata.business.dao.BusinessApplicationConfigDao;
import com.wellsoft.pt.basicdata.business.entity.BusinessApplicationConfigEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 数据库表BUSINESS_APPLICATION_CONFIG的service服务接口
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
public interface BusinessApplicationConfigService extends JpaService<BusinessApplicationConfigEntity, BusinessApplicationConfigDao, String> {

}
