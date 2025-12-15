/*
 * @(#)2019-02-21 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.dto;

import com.wellsoft.pt.basicdata.business.entity.BusinessApplicationConfigEntity;


/**
 * Description: 数据库表BUSINESS_APPLICATION_CONFIG的对应的DTO类
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
public class BusinessApplicationConfigDto extends BusinessApplicationConfigEntity {

    private static final long serialVersionUID = 1550738921971L;

    private String dict;

    public String getDict() {
        return dict;
    }

    public void setDict(String dict) {
        this.dict = dict;
    }


}
