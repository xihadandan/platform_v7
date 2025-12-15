/*
 * @(#)7/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.dao;

import com.wellsoft.pt.fulltext.entity.FulltextSearchWordTermEntity;
import com.wellsoft.pt.jpa.dao.JpaDao;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/25/25.1	    zhulh		7/25/25		    Create
 * </pre>
 * @date 7/25/25
 */
public interface FulltextSearchWordTermDao extends JpaDao<FulltextSearchWordTermEntity, Long> {
    void deleteBySearchUuid(Long searchWordUuid);
}
