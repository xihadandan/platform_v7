/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.dao.WmMailConfigDao;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;

import java.util.List;

/**
 * Description: 邮件配置服务
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-06-03.1	t		2016-06-03		Create
 * </pre>
 * @date 2016-06-03
 */
public interface WmMailConfigService extends
        JpaService<WmMailConfigEntity, WmMailConfigDao, String> {

    /**
     * 根据实例查询邮件配置列表
     *
     * @param example
     * @return
     */
    List<WmMailConfigEntity> findByExample(WmMailConfigEntity example);

    /**
     * 获取指定系统组织ID的邮件配置
     *
     * @param systemUnitId
     * @return
     */
    WmMailConfigEntity getBySystemUnitId(String systemUnitId);


    /**
     * 根据域名查询邮件配置列表
     *
     * @param domain
     * @return
     */
    List<WmMailConfigEntity> listByDomain(String domain);

}
