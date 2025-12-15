/*
 * @(#)2021-03-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgPwdSettingDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgPwdSettingEntity;
import com.wellsoft.pt.multi.org.service.MultiOrgPwdSettingService;
import org.springframework.stereotype.Service;

/**
 * Description: 数据库表MULTI_ORG_PWD_SETTING的service服务接口实现类
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
@Service
public class MultiOrgPwdSettingServiceImpl
        extends AbstractJpaServiceImpl<MultiOrgPwdSettingEntity, MultiOrgPwdSettingDao, String>
        implements MultiOrgPwdSettingService {

}
