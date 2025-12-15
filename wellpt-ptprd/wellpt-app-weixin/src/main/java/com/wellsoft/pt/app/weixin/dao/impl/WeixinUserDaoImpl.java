/*
 * @(#)5/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.dao.impl;

import com.wellsoft.pt.app.weixin.dao.WeixinUserDao;
import com.wellsoft.pt.app.weixin.entity.WeixinUserEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/21/25.1	    zhulh		5/21/25		    Create
 * </pre>
 * @date 5/21/25
 */
@Repository
public class WeixinUserDaoImpl extends AbstractJpaDaoImpl<WeixinUserEntity, Long> implements WeixinUserDao {
}
