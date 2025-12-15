/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bm.service.impl;

import com.wellsoft.pt.bm.dao.AdvisoryComplaintsDao;
import com.wellsoft.pt.bm.entity.AdvisoryComplaints;
import com.wellsoft.pt.bm.service.AdvisoryComplaintsService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

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
public class AdvisoryComplaintsServiceImpl extends
        AbstractJpaServiceImpl<AdvisoryComplaints, AdvisoryComplaintsDao, String> implements AdvisoryComplaintsService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bm.service.AdvisoryComplaintsService#countAdvisoryCmpliants(java.lang.String, java.util.Map)
     */
    @Override
    public Long countAdvisoryCmpliants(String hql, Map<String, Object> values) {
        return dao.getNumberByHQL(hql, values);
    }

}
