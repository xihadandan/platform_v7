/*
 * @(#)2018年3月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.webmail.dao.WmMailRecentContactDao;
import com.wellsoft.pt.webmail.entity.WmMailRecentContactEntity;
import org.springframework.stereotype.Repository;

/**
 * Description: 最近联系人dao实现类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月19日.1	chenqiong		2018年3月19日		Create
 * </pre>
 * @date 2018年3月19日
 */
@Repository
public class WmMailRecentContactDaoImpl extends AbstractJpaDaoImpl<WmMailRecentContactEntity, String> implements
        WmMailRecentContactDao {

}
