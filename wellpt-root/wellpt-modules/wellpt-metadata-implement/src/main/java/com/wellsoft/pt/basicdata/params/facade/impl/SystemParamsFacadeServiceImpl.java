/*
 * @(#)2018年1月20日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.params.facade.impl;

import com.wellsoft.context.config.service.SystemParamsFacadeService;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年1月20日.1	chenqiong		2018年1月20日		Create
 * </pre>
 * @date 2018年1月20日
 */
@Service
public class SystemParamsFacadeServiceImpl implements SystemParamsFacadeService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.context.config.service.SystemParamsFacadeService#getValue(java.lang.String)
     */
    @Override
    public String getValue(String key) {
        return SystemParams.getValue(key);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.context.config.service.SystemParamsFacadeService#getValue(java.lang.String, java.lang.String)
     */
    @Override
    public String getValue(String key, String defaultValue) {
        return SystemParams.getValue(key, defaultValue);
    }

}
