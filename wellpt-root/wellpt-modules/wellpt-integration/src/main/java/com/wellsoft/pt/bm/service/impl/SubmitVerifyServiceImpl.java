/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bm.service.impl;

import com.wellsoft.pt.bm.dao.SubmitVerifyDao;
import com.wellsoft.pt.bm.entity.SubmitVerify;
import com.wellsoft.pt.bm.service.SubmitVerifyService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
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
public class SubmitVerifyServiceImpl extends AbstractJpaServiceImpl<SubmitVerify, SubmitVerifyDao, String> implements
        SubmitVerifyService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bm.service.SubmitVerifyService#getObjByPuuidAndVerifyId(java.lang.String, java.lang.String)
     */
    @Override
    public List<SubmitVerify> getObjByPuuidAndVerifyId(String puuid, String id) {
        return dao.getObjByPuuidAndVerifyId(puuid, id);
    }

}
