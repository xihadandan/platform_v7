/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.facade.service.impl;

import com.wellsoft.pt.security.passport.facade.service.SystemAccessFacadeService;
import com.wellsoft.pt.security.passport.service.SystemAccessService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月12日.1	chenqiong		2018年4月12日		Create
 * </pre>
 * @date 2018年4月12日
 */
@Service
public class SystemAccessFacadeServiceImpl implements SystemAccessFacadeService {

    @Resource
    SystemAccessService systemAccessService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.passport.facade.service.SystemAccessFacadeService#saveAllFromMap(java.util.Map)
     */
    @Override
    public void saveAllFromMap(Map<String, String> map) {
        systemAccessService.saveAllFromMap(map);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.passport.facade.service.SystemAccessFacadeService#getAllAsMap()
     */
    @Override
    public Map<String, String> getAllAsMap() {
        return systemAccessService.getAllAsMap();
    }
}
