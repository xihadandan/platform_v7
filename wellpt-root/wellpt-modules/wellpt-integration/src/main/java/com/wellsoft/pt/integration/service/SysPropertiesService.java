/*
 * @(#)2018年4月17日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service;

import com.wellsoft.pt.integration.dao.SysPropertiesDao;
import com.wellsoft.pt.integration.entity.SysProperties;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 2018年4月17日.1	chenqiong		2018年4月17日		Create
 * </pre>
 * @date 2018年4月17日
 */
public interface SysPropertiesService extends JpaService<SysProperties, SysPropertiesDao, String> {

    Boolean saveSysProperties(List<SysProperties> sysPropertiesList);

    List<SysProperties> getAllSysProperties(String moduleId);

    /**
     * @param example
     * @return
     */
    List<SysProperties> findByExample(SysProperties example);

}
