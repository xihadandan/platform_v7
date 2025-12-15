/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.dao;

import com.wellsoft.pt.basicdata.dyview.entity.ColumnDefinition;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;

/**
 * Description: 如何描述该类
 *
 * @author jiangmb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2013-3-13.1	Administrator		2013-3-13		Create
 * </pre>
 * @date 2013-3-13
 */
@Repository
public class ColumnAttributeDao extends HibernateDao<ColumnDefinition, String> {

    public ColumnAttributeDao() {

    }

}
