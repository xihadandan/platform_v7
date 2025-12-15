/*
 * @(#)2013-6-23 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.generator.dao;

import com.wellsoft.pt.common.generator.entity.IdGenerator;
import com.wellsoft.pt.jpa.dao.OrgHibernateDao;
import org.springframework.stereotype.Repository;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-6-23.1	rzhu		2013-6-23		Create
 * </pre>
 * @date 2013-6-23
 */
@Repository
public class IdGeneratorDao extends OrgHibernateDao<IdGenerator, String> {

}
