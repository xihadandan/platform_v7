/*
 * @(#)2021-03-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgPwdSettingDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgPwdSettingEntity;

/**
 * Description: 数据库表MULTI_ORG_PWD_SETTING的service服务接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-03-23.1	zenghw		2021-03-23		Create
 * </pre>
 * @date 2021-03-23
 */
public interface MultiOrgPwdSettingService extends JpaService<MultiOrgPwdSettingEntity, MultiOrgPwdSettingDao, String> {

}
