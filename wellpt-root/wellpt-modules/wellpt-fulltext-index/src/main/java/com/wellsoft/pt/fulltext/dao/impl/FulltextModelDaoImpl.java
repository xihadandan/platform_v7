/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.dao.impl;

import com.wellsoft.pt.fulltext.dao.FulltextModelDao;
import com.wellsoft.pt.fulltext.entity.FulltextModelEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/13/25.1	    zhulh		6/13/25		    Create
 * </pre>
 * @date 6/13/25
 */
@Repository
public class FulltextModelDaoImpl extends AbstractJpaDaoImpl<FulltextModelEntity, Long> implements FulltextModelDao {
}
