/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.dao;

import com.wellsoft.pt.di.entity.DiDataProcessorEntity;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;


/**
 * Description: 数据库表DI_DATA_PROCESSOR的DAO接口
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
public interface DiDataProcessorDao extends JpaDao<DiDataProcessorEntity, String> {


    List<DiDataProcessorEntity> listByDiConfigUuidOrderBySeqAsc(String uuid);
}

