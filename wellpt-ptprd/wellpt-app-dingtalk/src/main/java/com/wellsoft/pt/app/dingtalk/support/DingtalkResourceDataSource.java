/*
 * @(#)4/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.security.audit.support.AbstractResourceDataSource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/25/25.1	    zhulh		4/25/25		    Create
 * </pre>
 * @date 4/25/25
 */
@Component
public class DingtalkResourceDataSource extends AbstractResourceDataSource {

    @Override
    public List<TreeNode> getData(Map<String, Object> params) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getAnonymousResources() {
        return Lists.newArrayList("/api/dingtalk/config/getEnabledCorpId", "/api/dingtalk/config/getEnabledClientId",
                "/api/dingtalk/robot/callback/**", "/api/dingtalk/auth");
    }

}
