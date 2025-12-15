/*
 * @(#)2019年12月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.owner.impl;

import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.dms.dp.owner.AbstractCurrentUserDataOwnerProvider;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年12月12日.1	zhulh		2019年12月12日		Create
 * </pre>
 * @date 2019年12月12日
 */
@Component
public class CurrentUserBusinessUnitIdDataOwnerProviderImpl extends AbstractCurrentUserDataOwnerProvider {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.dp.owner.DataOwnerProvider#getName()
     */
    @Override
    public String getName() {
        return "当前用户所在业务单位";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.dp.owner.DataOwnerProvider#getType()
     */
    @Override
    public String getType() {
        return "currentUserBusinessUnitId";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.dp.owner.DataOwnerProvider#getOwnerIds()
     */
    @Override
    public List<String> getOwnerIds() {
        return getCurrentUserBusinessUnitIds();
    }

    /**
     * @return
     */
    private List<String> getCurrentUserBusinessUnitIds() {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        Set<String> bizUnitIds = Sets.newLinkedHashSet();
        if (userDetails != null) {
            OrgTreeNodeDto mainJob = userDetails.getMainJob();
            if (mainJob != null) {
                if (StringUtils.isNotBlank(mainJob.getEleIdPath())) {
                    bizUnitIds.add(MultiOrgTreeNode.getNearestEleIdByType(mainJob.getEleIdPath(),
                            IdPrefix.BUSINESS_UNIT.getValue()));
                }
            }
            List<OrgTreeNodeDto> otherJobs = userDetails.getOtherJobs();
            if (CollectionUtils.isNotEmpty(otherJobs)) {
                for (OrgTreeNodeDto otherJob : otherJobs) {
                    if (StringUtils.isNotBlank(otherJob.getEleIdPath())) {
                        bizUnitIds.add(MultiOrgTreeNode.getNearestEleIdByType(otherJob.getEleIdPath(),
                                IdPrefix.BUSINESS_UNIT.getValue()));
                    }
                }
            }
        }
        return Arrays.asList(bizUnitIds.toArray(new String[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 40;
    }

}
