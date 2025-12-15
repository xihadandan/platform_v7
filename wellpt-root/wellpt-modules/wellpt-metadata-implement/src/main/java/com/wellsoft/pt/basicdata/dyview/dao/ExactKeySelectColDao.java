/*
 * @(#)2013-3-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.dao;

import com.wellsoft.pt.basicdata.dyview.entity.ExactKeySelectCol;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-30.1	wubin		2013-3-30		Create
 * </pre>
 * @date 2013-3-30
 */
@Repository
public class ExactKeySelectColDao extends HibernateDao<ExactKeySelectCol, String> {
    public ExactKeySelectColDao() {

    }
}
