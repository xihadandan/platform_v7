/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.security.passport.bean.IpSecurityConfigBean;

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
 * 2018年4月12日.1	chenqiong		2018年4月12日		Create
 * </pre>
 * @date 2018年4月12日
 */
public interface IpSecurityConfigFacadeService extends Facade {
    /**
     * 保存所有IP配置VO类
     *
     * @param beans
     */
    void saveAllBean(IpSecurityConfigBean... beans);

    /**
     * 获取所有的IP安全配置
     *
     * @return
     */
    List<IpSecurityConfigBean> getAllBean();
}
