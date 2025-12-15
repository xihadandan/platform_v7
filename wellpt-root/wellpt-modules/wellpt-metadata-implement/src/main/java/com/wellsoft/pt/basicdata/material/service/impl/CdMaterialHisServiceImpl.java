/*
 * @(#)4/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.service.impl;

import com.wellsoft.pt.basicdata.material.dao.CdMaterialHisDao;
import com.wellsoft.pt.basicdata.material.entity.CdMaterialHisEntity;
import com.wellsoft.pt.basicdata.material.service.CdMaterialHisService;
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
 * 4/27/23.1	zhulh		4/27/23		Create
 * </pre>
 * @date 4/27/23
 */
@Service
public class CdMaterialHisServiceImpl extends AbstractJpaServiceImpl<CdMaterialHisEntity, CdMaterialHisDao, Long>
        implements CdMaterialHisService {
}
