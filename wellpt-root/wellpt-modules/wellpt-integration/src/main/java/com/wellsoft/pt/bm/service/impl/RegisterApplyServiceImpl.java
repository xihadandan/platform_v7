/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bm.service.impl;

import com.wellsoft.pt.bm.dao.RegisterApplyDao;
import com.wellsoft.pt.bm.entity.RegisterApply;
import com.wellsoft.pt.bm.service.RegisterApplyService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月16日.1	chenqiong		2018年4月16日		Create
 * </pre>
 * @date 2018年4月16日
 */
@Service
public class RegisterApplyServiceImpl extends AbstractJpaServiceImpl<RegisterApply, RegisterApplyDao, String> implements
        RegisterApplyService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bm.service.RegisterApplyService#queryByZch(java.lang.String, java.lang.String)
     */
    @Override
    public RegisterApply queryByZch(String string, String zch) {
        List<RegisterApply> registerApplies = dao.listByFieldEqValue("zch", zch);
        return CollectionUtils.isNotEmpty(registerApplies) ? registerApplies.get(0) : null;
    }

}
