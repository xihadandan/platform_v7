/*
 * @(#)2020年12月8日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.preferences.service.impl;

import com.wellsoft.pt.basicdata.preferences.dao.CdUserPreferencesDao;
import com.wellsoft.pt.basicdata.preferences.entity.CdUserPreferencesEntity;
import com.wellsoft.pt.basicdata.preferences.service.CdUserPreferencesService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月8日.1	zhulh		2020年12月8日		Create
 * </pre>
 * @date 2020年12月8日
 */
@Service
public class CdUserPreferencesServiceImpl extends
        AbstractJpaServiceImpl<CdUserPreferencesEntity, CdUserPreferencesDao, String> implements
        CdUserPreferencesService {

}
