/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.acl.dao.AclClassDao;
import com.wellsoft.pt.security.acl.entity.AclClass;
import com.wellsoft.pt.security.acl.service.AclClassService;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月12日.1	chenqiong		2018年4月12日		Create
 * </pre>
 * @date 2018年4月12日
 */
@Service
public class AclClassServiceImpl extends AbstractJpaServiceImpl<AclClass, AclClassDao, String> implements
        AclClassService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclClassService#getByClasssName(java.lang.String)
     */
    @Override
    public AclClass getByClasssName(String classsName) {
        return dao.getByClasssName(classsName);
    }

}
