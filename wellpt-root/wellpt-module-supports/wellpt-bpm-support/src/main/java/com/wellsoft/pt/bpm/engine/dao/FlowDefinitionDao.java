/*
 * @(#)2012-10-24 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao;

import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-24.1	zhulh		2012-10-24		Create
 * </pre>
 * @date 2012-10-24
 */
public interface FlowDefinitionDao extends JpaDao<FlowDefinition, String> {

    void getLatest(Page<FlowDefinition> dataPage);

    Long countByCategory(String category);

    Long countById(String id);

    List<FlowDefinition> getByCategory(String categorySN);

    List<FlowDefinition> getByFormUuid(String formUuid);

    FlowDefinition getById(String id);

    Double getLatestVersionById(String id);

}
