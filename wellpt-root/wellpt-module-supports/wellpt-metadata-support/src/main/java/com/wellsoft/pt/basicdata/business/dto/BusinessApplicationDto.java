/*
 * @(#)2019-02-21 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.dto;

import com.wellsoft.pt.basicdata.business.entity.BusinessApplicationConfigEntity;
import com.wellsoft.pt.basicdata.business.entity.BusinessApplicationEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * Description: 数据库表BUSINESS_APPLICATION的对应的DTO类
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
public class BusinessApplicationDto extends BusinessApplicationEntity {

    private static final long serialVersionUID = 1550738838582L;


    private List<BusinessApplicationConfigEntity> configs = new ArrayList<BusinessApplicationConfigEntity>();


    public List<BusinessApplicationConfigEntity> getConfigs() {
        return configs;
    }


    public void setConfigs(List<BusinessApplicationConfigEntity> configs) {
        this.configs = configs;
    }


}
