/*
 * @(#)2019年12月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.owner.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.dms.dp.owner.AbstractCurrentUserDataOwnerProvider;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

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
public class CurrentUserJobIdDataOwnerProviderImpl extends AbstractCurrentUserDataOwnerProvider {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.dp.owner.CurrentUserDataOwnerProvider#getName()
     */
    @Override
    public String getName() {
        return "当前用户职位";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.dp.owner.CurrentUserDataOwnerProvider#getType()
     */
    @Override
    public String getType() {
        return "currentUserJobId";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.dp.owner.CurrentUserDataOwnerProvider#getOwnerIds()
     */
    @Override
    public List<String> getOwnerIds() {
        return getCurrentUserJobIds();
    }

    /**
     * @return
     */
    private List<String> getCurrentUserJobIds() {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        List<String> jobIds = Lists.newArrayList();
        OrgTreeNodeDto mainJob = userDetails.getMainJob();
        if (mainJob != null) {
            if (StringUtils.isNotBlank(mainJob.getEleId())) {
                jobIds.add(mainJob.getEleId());
            }
        }
        List<OrgTreeNodeDto> otherJobs = userDetails.getOtherJobs();
        if (CollectionUtils.isNotEmpty(otherJobs)) {
            for (OrgTreeNodeDto otherJob : otherJobs) {
                if (StringUtils.isNotBlank(otherJob.getEleId())) {
                    jobIds.add(otherJob.getEleId());
                }
            }
        }
        return jobIds;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 20;
    }

}
