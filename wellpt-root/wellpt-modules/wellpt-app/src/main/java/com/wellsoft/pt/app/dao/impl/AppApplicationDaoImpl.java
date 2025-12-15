/*
 * @(#)2018年4月2日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dao.impl;

import com.wellsoft.pt.app.dao.AppApplicationDao;
import com.wellsoft.pt.app.entity.AppApplication;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 应用实体类dao实现类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月2日.1	chenqiong		2018年4月2日		Create
 * </pre>
 * @date 2018年4月2日
 */
@Repository
public class AppApplicationDaoImpl extends AbstractJpaDaoImpl<AppApplication, String> implements AppApplicationDao {

}
