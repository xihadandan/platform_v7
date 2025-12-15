/*
 * @(#)2013-7-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.workflow.entity.FlowOpinion;

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
public interface FlowOpinionDao extends JpaDao<FlowOpinion, String> {

    void deleteByCategoryUuid(String categoryUuid);
}
