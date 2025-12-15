/*
 * @(#)2019-07-25 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.dao.impl;

import com.wellsoft.pt.di.dao.DiCallbackRequestDao;
import com.wellsoft.pt.di.entity.DiCallbackRequestEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;


/**
 * Description: 数据库表DI_CALLBACK_REQUEST的DAO接口实现类
 *
 * @author chenq
 * @version 1.0
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-25.1    chenq        2019-07-25		Create
 * </pre>
 * @date 2019-07-25
 */
@Repository
public class DiCallbackRequestDaoImpl extends AbstractJpaDaoImpl<DiCallbackRequestEntity, String> implements DiCallbackRequestDao {


}

