/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bm.service.impl;

import com.wellsoft.pt.bm.dao.PublicityAttachDao;
import com.wellsoft.pt.bm.entity.PublicityAttach;
import com.wellsoft.pt.bm.service.PublicityAttachService;
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
public class PublicityAttachServiceImpl extends AbstractJpaServiceImpl<PublicityAttach, PublicityAttachDao, String>
        implements PublicityAttachService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bm.service.PublicityAttachService#listByPuuid(java.lang.String)
     */
    @Override
    public List<PublicityAttach> listByPuuid(String uuid) {
        return this.dao.listByFieldEqValue("puuid", uuid);
    }

}
