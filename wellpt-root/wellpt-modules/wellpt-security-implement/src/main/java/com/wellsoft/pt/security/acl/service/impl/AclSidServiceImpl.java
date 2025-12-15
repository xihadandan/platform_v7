/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.acl.dao.AclSidDao;
import com.wellsoft.pt.security.acl.entity.AclSid;
import com.wellsoft.pt.security.acl.service.AclSidService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
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
 * 2018年4月12日.1	chenqiong		2018年4月12日		Create
 * </pre>
 * @date 2018年4月12日
 */
@Service
public class AclSidServiceImpl extends AbstractJpaServiceImpl<AclSid, AclSidDao, String> implements AclSidService {

    private static final String LIST_BY_SIDS = "from AclSid t where t.sid in(:sids)";

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclSidService#get(com.wellsoft.pt.security.acl.entity.AclSid)
     */
    @Override
    public AclSid get(AclSid aclSid) {
        return dao.get(aclSid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclSidService#isExists(boolean, java.lang.String)
     */
    @Override
    public boolean isExists(boolean principal, String sid) {
        return dao.isExists(principal, sid);
    }

    @Override
    public List<AclSid> listBySids(List<String> sids) {
        List<AclSid> aclSids = Lists.newArrayList();
        Map<String, Object> params = Maps.newHashMap();
        if (CollectionUtils.size(sids) > 1000) {
            ListUtils.handleSubList(sids, 1000, subList -> {
                params.put("sids", subList);
                aclSids.addAll(this.dao.listByHQL(LIST_BY_SIDS, params));
            });
        } else {
            params.put("sids", sids);
            aclSids.addAll(this.dao.listByHQL(LIST_BY_SIDS, params));
        }
        return aclSids;
    }

}
