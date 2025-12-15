/*
 * @(#)2020年12月10日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api;

import com.wellsoft.pt.jpa.query.Query;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月10日.1	zhulh		2020年12月10日		Create
 * </pre>
 * @date 2020年12月10日
 */
public interface FlowCategoryQuery extends Query<FlowCategoryQuery, FlowCategoryQueryItem> {

    FlowCategoryQuery uuid(String uuid);

    FlowCategoryQuery uuids(Collection<String> uuids);

    FlowCategoryQuery name(String name);

    FlowCategoryQuery systemUnitId(String systemUnitId);

    FlowCategoryQuery systemUnitIds(Collection<String> systemUnitIds);

    FlowCategoryQuery systemId(String systemId);

    FlowCategoryQuery nameLike(String name);

    FlowCategoryQuery orderByCodeAsc();

    FlowCategoryQuery orderByCodeDesc();

}
