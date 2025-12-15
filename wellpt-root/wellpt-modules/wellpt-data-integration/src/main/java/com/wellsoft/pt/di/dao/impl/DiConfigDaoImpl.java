/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.dao.impl;

import com.wellsoft.pt.di.dao.DiConfigDao;
import com.wellsoft.pt.di.entity.DiConfigEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;


/**
 * Description: 数据库表DI_CONFIG的DAO接口实现类
 *
 * @author chenq
 * @version 1.0
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-23.1    chenq        2019-07-23		Create
 * </pre>
 * @date 2019-07-23
 */
@Repository
public class DiConfigDaoImpl extends AbstractJpaDaoImpl<DiConfigEntity, String> implements DiConfigDao {


}

