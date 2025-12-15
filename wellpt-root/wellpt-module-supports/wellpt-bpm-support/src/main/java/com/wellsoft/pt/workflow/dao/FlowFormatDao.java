/*
 * @(#)2012-12-3 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.dao;

import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.workflow.entity.FlowFormat;
import org.hibernate.criterion.Criterion;

/**
 * Description: 工作流信息格式持久层操作类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-3.1	zhulh		2012-12-3		Create
 * </pre>
 * @date 2012-12-3
 */
public interface FlowFormatDao extends JpaDao<FlowFormat, String> {

    FlowFormat getByCode(String code);

    /**
     * 如何描述该方法
     *
     * @param page
     * @param criterions
     * @return
     */
    Page<FlowFormat> findPage(Page<FlowFormat> page, Criterion... criterions);
}
