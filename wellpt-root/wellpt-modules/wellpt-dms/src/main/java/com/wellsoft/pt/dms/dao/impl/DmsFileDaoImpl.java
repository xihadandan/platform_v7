/*
 * @(#)5/22/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dao.impl;

import com.wellsoft.pt.dms.dao.DmsFileDao;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 5/22/24.1	zhulh		5/22/24		Create
 * </pre>
 * @date 5/22/24
 */
@Repository
public class DmsFileDaoImpl extends AbstractJpaDaoImpl<DmsFileEntity, String> implements DmsFileDao {
}
