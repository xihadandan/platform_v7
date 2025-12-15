/*
 * @(#)2016年9月10日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;

import java.util.List;

/**
 * Description: 平台应用发起工作门面服务接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年9月10日.1	zhulh		2016年9月10日		Create
 * </pre>
 * @date 2016年9月10日
 */
public interface WorkflowNewWorkService extends BaseService {
    List<TreeNode> getFlowDefinitions();

    List<TreeNode> getMobileFlowDefinitions();

    TreeNode getUserRecentUseFlowDefinitions();

    List<TreeNode> getUserAllFlowDefinitions();
}
