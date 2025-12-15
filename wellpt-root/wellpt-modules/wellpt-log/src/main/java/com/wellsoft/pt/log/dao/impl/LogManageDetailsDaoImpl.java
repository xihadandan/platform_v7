/*
 * @(#)2021-06-28 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.log.dao.LogManageDetailsDao;
import com.wellsoft.pt.log.entity.LogManageDetailsEntity;
import org.springframework.stereotype.Repository;

/**
 * Description: 数据库表LOG_MANAGE_DETAILS的DAO接口实现类
 *
 * @author zenghw
 * @version 1.0
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-06-28.1    zenghw        2021-06-28		Create
 * </pre>
 * @date 2021-06-28
 */
@Repository
public class LogManageDetailsDaoImpl extends AbstractJpaDaoImpl<LogManageDetailsEntity, String>
        implements LogManageDetailsDao {

}
