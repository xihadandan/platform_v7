/*
 * @(#)2018年9月6日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.util;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月6日.1	zhulh		2018年9月6日		Create
 * </pre>
 * @date 2018年9月6日
 */
public class PermissionGranularityUtils {

    public static List<String> getCurrentUserSids() {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        return getSids(userDetails);
    }

    public static List<String> getSids(UserDetails userDetails) {
        UserDetails user = userDetails;
        if (user == null) {
            user = SpringSecurityUtils.getCurrentUser();
        }

        String userRelatedIds = Objects.toString(userDetails.getExtraData("userRelatedIds"), StringUtils.EMPTY);
        if (StringUtils.isNotBlank(userRelatedIds)) {
            return Arrays.asList(StringUtils.split(userRelatedIds, Separator.SEMICOLON.getValue()));
        }

        Set<String> sids = new LinkedHashSet<String>();
        sids.add(user.getUserId());

        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        Set<String> userOrgIds = workflowOrgService.getUserRelatedIds(user.getUserId());
        List<String> deletes = new ArrayList<String>();
        for (String orgId : userOrgIds) {
            if (orgId.startsWith(IdPrefix.USER.getValue())) {
                deletes.add(orgId);
            }
        }
        userOrgIds.removeAll(deletes);
        // userOrgIds.add(userId);
        sids.addAll(userOrgIds);

        userDetails.putExtraData("userRelatedIds", StringUtils.join(sids, Separator.SEMICOLON.getValue()));

        return Arrays.asList(sids.toArray(new String[0]));
    }

    /**
     * @param userId
     * @return
     */
    public static List<String> getUserSids(String userId) {
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        Set<String> userOrgIds = workflowOrgService.getUserRelatedIds(userId);
        return Arrays.asList(userOrgIds.toArray(new String[0]));
    }

}
