/*
 * @(#)2018年4月18日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.webmail.dao.WmMailConfigDao;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;
import org.springframework.stereotype.Repository;

/**
 * Description: 邮件配置dao实现
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月18日.1	chenqiong		2018年4月18日		Create
 * </pre>
 * @date 2018年4月18日
 */
@Repository
public class WmMailConfigDaoImpl extends AbstractJpaDaoImpl<WmMailConfigEntity, String> implements WmMailConfigDao {

}
