/*
 * @(#)2013-7-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.workflow.dao.FlowOpinionDao;
import com.wellsoft.pt.workflow.entity.FlowOpinion;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-7-30.1	zhulh		2013-7-30		Create
 * </pre>
 * @date 2013-7-30
 */
@Repository
public class FlowOpinionDaoImpl extends AbstractJpaDaoImpl<FlowOpinion, String> implements FlowOpinionDao {

    private static final String DELETE_BY_CATEGORY = "delete from FlowOpinion flow_opinion where flow_opinion.opinionCategoryUuid = :categoryUuid";

    /**
     * @param categoryUuid
     */
    public void deleteByCategoryUuid(String categoryUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("categoryUuid", categoryUuid);
        this.deleteByHQL(DELETE_BY_CATEGORY, values);
    }

}
