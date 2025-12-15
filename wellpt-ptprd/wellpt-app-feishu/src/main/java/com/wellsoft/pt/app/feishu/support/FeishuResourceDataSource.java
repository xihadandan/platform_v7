/*
 * @(#)3/31/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.support;

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
 * 3/31/25.1	    zhulh		3/31/25		    Create
 * </pre>
 * @date 3/31/25
 */
@Component
public class FeishuResourceDataSource extends AbstractResourceDataSource {

    @Override
    public List<TreeNode> getData(Map<String, Object> params) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getAnonymousResources() {
        return Lists.newArrayList("/api/feishu/getUserTokenInfo", "/api/feishu/getUserInfo", "/api/feishu/config/getEnabledAppId");
    }
}
