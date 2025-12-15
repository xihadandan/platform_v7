/*
 * @(#)7/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.fulltext.dao.FulltextSearchWordTermDao;
import com.wellsoft.pt.fulltext.entity.FulltextSearchWordTermEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Map;

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
@Repository
public class FulltextSearchWordTermDaoImpl extends AbstractJpaDaoImpl<FulltextSearchWordTermEntity, Long> implements FulltextSearchWordTermDao {

    @Override
    public void deleteBySearchUuid(Long searchWordUuid) {
        Assert.notNull(searchWordUuid, "searchWordUuid cannot be null");

        String hql = "delete from FulltextSearchWordTermEntity where searchWordUuid = :searchWordUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("searchWordUuid", searchWordUuid);
        this.deleteByHQL(hql, params);
    }

}
