/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgOptionDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgOption;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-11-21.1	zyguo		2017-11-21		Create
 * </pre>
 * @date 2017-11-21
 */

@Repository
public class MultiOrgOptionDaoImpl extends AbstractJpaDaoImpl<MultiOrgOption, String> implements
        MultiOrgOptionDao {

    @Override
    public Collection<MultiOrgOption> queryMultiOrgOptionsByLikeIdCodeName(String searchValue) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("value", searchValue);
        params.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        params.put("ptSystemUnitId", MultiOrgSystemUnit.PT_ID);
        return this.listByNameHQLQuery("queryMultiOrgOptionsByLikeIdCodeName", params);
    }

    @Override
    public List<MultiOrgOption> getOrgOptionsByIds(String[] ids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", ids);
        params.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        params.put("ptSystemUnitId", MultiOrgSystemUnit.PT_ID);
        return this.listByNameHQLQuery("getOrgOptionsByIds", params);
    }
}
