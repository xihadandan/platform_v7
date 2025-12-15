'use strict';

/**
 * @param {Wellsoft.Application} app - wellsoft application
 */
module.exports = app => {
  const { router, controller } = app;

  router.get('/workflow-designer/index', controller.workflowVueController.workflowDesigner); // 工作流程设计器
  router.get('/workflow-viewer/index', controller.workflowVueController.workflowViewer); // 工作流程查阅

  /**
   * 流程类地址控制
   */
  router.get('/workflow/work/v53/new/:id', controller.workflowController._new); // 发起工作流
  router.get('/workflow/work/v53/view/todo', controller.workflowController.todo); // 待办工作流
  router.get('/workflow/work/v53/view/draft', controller.workflowController.draft); // 草稿工作流
  router.get('/workflow/work/v53/view/done', controller.workflowController.done); // 已办工作流
  router.get('/workflow/work/v53/view/attention', controller.workflowController.attention); // 关注工作流
  router.get('/workflow/work/v53/view/work', controller.workflowController.work);
  router.get('/workflow/work/v53/view/unread', controller.workflowController.unread);
  router.get('/workflow/work/v53/view/read', controller.workflowController.read);
  router.get('/workflow/work/v53/view/over', controller.workflowController.viewOver);
  router.get('/workflow/work/v53/view/supervise', controller.workflowController.viewSupervise);
  router.get('/workflow/work/v53/view/monitor', controller.workflowController.viewMonitor);
  router.get('/workflow/work/v53/view/viewReadLog', controller.workflowController.viewReadLog);
  router.get('/workflow/work/v53/view/subflow/share', controller.workflowController.subflowShare);
  router.get('/workflow/work/v53/view/flow/process', controller.workflowController.viewFlowProcess);
  router.get('/workflow/work/v53/view/flow/mobile/process', controller.workflowController.viewFlowMobileProcess);

  /**
   * 流程类vue版本地址控制
   */
  router.get('/workflow/work/new/:id', controller.workflowVueController._new); // 发起工作流
  router.get('/workflow/work/view/todo', controller.workflowVueController.todo); // 待办工作流
  router.get('/workflow/work/view/draft', controller.workflowVueController.draft); // 草稿工作流
  router.get('/workflow/work/view/done', controller.workflowVueController.done); // 已办工作流
  router.get('/workflow/work/view/attention', controller.workflowVueController.attention); // 关注工作流
  router.get('/workflow/work/view/work', controller.workflowVueController.work);
  router.get('/workflow/work/view/unread', controller.workflowVueController.unread);
  router.get('/workflow/work/view/read', controller.workflowVueController.read);
  router.get('/workflow/work/view/over', controller.workflowVueController.viewOver);
  router.get('/workflow/work/view/supervise', controller.workflowVueController.viewSupervise);
  router.get('/workflow/work/view/monitor', controller.workflowVueController.viewMonitor);
  router.get('/workflow/work/view/viewReadLog', controller.workflowController.viewReadLog);
  router.get('/workflow/work/view/subflow/share', controller.workflowVueController.subflowShare);

  /**
   * 流程仿真
   */
  router.get('/workflow-simulation/index', controller.workflowVueController.workflowSimulation); // 流程仿真

  // 获取流程数据详情
  router.post('/api/workflow/work/getWorkDataByWorkBean', controller.workflowController.getWorkDataByWorkBean);
  // 获取流程待办数据
  router.post('/api/workflow/work/getTodoWorkData', controller.workflowController.getTodoWorkData);
  // 保存流程
  router.post('/api/workflow/work/save', controller.workflowController.save);
  // 是否允许提交流程
  router.post('/api/workflow/work/isAllowedSubmit', controller.workflowController.isAllowedSubmit);
  // 是否需要提交意见
  router.post('/api/workflow/work/isRequiredSubmitOpinion', controller.workflowController.isRequiredSubmitOpinion);
  // 提交流程
  router.post('/api/workflow/work/submit', controller.workflowController.submit);
  // 是否允许直接退回流程
  router.post('/api/workflow/work/isAllowedDirectRollback', controller.workflowController.isAllowedDirectRollback);
  // 是否需要直接退回意见
  router.post('/api/workflow/work/isRequiredDirectRollbackOpinion', controller.workflowController.isRequiredDirectRollbackOpinion);
  // 退回流程
  router.post('/api/workflow/work/rollback', controller.workflowController.rollback);
  // 退回主流程
  router.post('/api/workflow/work/rollbackToMainFlow', controller.workflowController.rollbackToMainFlow);
  // 是否允许撤回流程
  router.post('/api/workflow/work/isAllowedCancel', controller.workflowController.isAllowedCancel);
  // 是否需要撤回意见
  router.post('/api/workflow/work/isRequiredCancelOpinion', controller.workflowController.isRequiredCancelOpinion);
  // 撤回流程
  router.post('/api/workflow/work/cancel', controller.workflowController.cancel);
  // 撤回流程
  router.post('/api/workflow/work/cancelWithWorkData', controller.workflowController.cancelWithWorkData);
  // 根据流程实例获取待办环节信息
  router.get('/api/workflow/work/getTodoTaskByFlowInstUuid', controller.workflowController.getTodoTaskByFlowInstUuid);
  // 是否允许转办流程
  router.post('/api/workflow/work/isAllowedTransfer', controller.workflowController.isAllowedTransfer);
  // 是否需要转办意见
  router.post('/api/workflow/work/isRequiredTransferOpinion', controller.workflowController.isRequiredTransferOpinion);
  // 转办
  router.post('/api/workflow/work/transfer', controller.workflowController.transfer);
  // 是否允许会签流程
  router.post('/api/workflow/work/isAllowedCounterSign', controller.workflowController.isAllowedCounterSign);
  // 是否需要会签意见
  router.post('/api/workflow/work/isRequiredCounterSignOpinion', controller.workflowController.isRequiredCounterSignOpinion);
  // 会签
  router.post('/api/workflow/work/counterSign', controller.workflowController.counterSign);
  // 加签
  router.post('/api/workflow/work/addSign', controller.workflowController.addSign);
  // 抄送
  router.post('/api/workflow/work/copyTo', controller.workflowController.copyTo);
  // 标记已阅
  router.post('/api/workflow/work/markRead', controller.workflowController.markRead);
  // 标记未阅
  router.post('/api/workflow/work/markUnread', controller.workflowController.markUnread);
  // 关注
  router.post('/api/workflow/work/attention', controller.workflowController.attentionTask);
  // 取消关注
  router.post('/api/workflow/work/unfollow', controller.workflowController.unfollow);
  // 子流程查看本流程
  router.post('/api/workflow/work/setViewMainFlow', controller.workflowController.setViewMainFlow);
  // 催办
  router.post('/api/workflow/work/remind', controller.workflowController.remind);
  // 获取套打模板
  router.post('/api/workflow/work/getPrintTemplateTree', controller.workflowController.getPrintTemplateTree);
  router.post('/api/workflow/work/getPrintTemplates', controller.workflowController.getPrintTemplates);
  // 获取套打模板
  router.get('/api/workflow/work/getPrintTemplateByIds', controller.workflowController.getPrintTemplateByIds);
  // 套打
  router.post('/api/workflow/work/print', controller.workflowController.print);
  // 获取表单字段定义
  router.get('/api/workflow/work/getDyformFileFieldDefinitions', controller.workflowController.getDyformFileFieldDefinitions);
  // 是否允许特送个人
  router.post('/api/workflow/work/isAllowedHandOver', controller.workflowController.isAllowedHandOver);
  // 是否需要移交个人意见
  router.post('/api/workflow/work/isRequiredHandOverOpinion', controller.workflowController.isRequiredHandOverOpinion);
  // 移交个人
  router.post('/api/workflow/work/handOver', controller.workflowController.handOver);
  // 是否允许特送环节
  router.post('/api/workflow/work/isAllowedGotoTask', controller.workflowController.isAllowedGotoTask);
  // 是否需要特送环节意见
  router.post('/api/workflow/work/isRequiredGotoTaskOpinion', controller.workflowController.isRequiredGotoTaskOpinion);
  // 特送环节
  router.post('/api/workflow/work/gotoTaskWithWorkData', controller.workflowController.gotoTaskWithWorkData);
  // 挂起
  router.post('/api/workflow/work/suspend', controller.workflowController.suspend);
  // 恢复
  router.post('/api/workflow/work/resume', controller.workflowController.resume);
  // 置顶
  router.post('/api/workflow/work/topping', controller.workflowController.topping);
  // 取消置顶
  router.post('/api/workflow/work/untopping', controller.workflowController.untopping);
  // 删除
  router.post('/api/workflow/work/delete', controller.workflowController.delete);
  // 删除草稿
  router.post('/api/workflow/work/deleteDraft', controller.workflowController.deleteDraft);
  // 管理员删除
  router.post('/api/workflow/work/deleteByAdmin', controller.workflowController.deleteByAdmin);
  // 管理员删除-假删除
  router.post('/api/workflow/work/logicalDeleteByAdmin', controller.workflowController.logicalDeleteByAdmin);
  // 获取最新的环节操作
  router.get('/api/task/operation/getLastestCancelAfterByFlowInstUuid', controller.workflowController.getLastestCancelAfterByFlowInstUuid);

  // 子流程、分支流
  // 加载子流程数据
  router.post('/api/workflow/work/loadSubTaskData', controller.workflowController.loadSubTaskData);
  // 加载分支流程数据
  router.post('/api/workflow/work/loadBranchTaskData', controller.workflowController.loadBranchTaskData);
  // 获取分支流办理过程
  router.post('/api/workflow/work/getBranchTaskProcesses', controller.workflowController.getBranchTaskProcesses);
  // 根据数据来源、操作类型、业务类别获取表单应用配置
  router.get('/api/workflow/work/getBusinessApplicationConfig', controller.workflowController.getBusinessApplicationConfig);
  // 发起分支流
  router.post('/api/workflow/work/startSubFlow', controller.workflowController.startSubFlow);
  // 发起分支流
  router.post('/api/workflow/work/startBranchTask', controller.workflowController.startBranchTask);
  // 根据环节实例UUID、主协办角色标记获取子流程定义标签信息
  router.get('/api/workflow/work/getNewFlowLabelInfos', controller.workflowController.getNewFlowLabelInfos);
  // 一键补发子流程
  router.post('/api/workflow/work/resendSubFlow', controller.workflowController.resendSubFlow);
  // 重做流程
  router.post('/api/workflow/work/redoFlow', controller.workflowController.redoFlow);
  // 重做分支流
  router.post('/api/workflow/work/redoBranchTask', controller.workflowController.redoBranchTask);
  // 终止流程
  router.post('/api/workflow/work/stopFlow', controller.workflowController.stopFlow);
  // 终止分支流
  router.post('/api/workflow/work/stopBranchTask', controller.workflowController.stopBranchTask);
  // 信息分发
  router.post('/api/workflow/work/distributeInfo', controller.workflowController.distributeInfo);
  // 根据流程环节数据，变更流程到期时间
  router.post('/api/workflow/work/changeTaskDueTime', controller.workflowController.changeTaskDueTime);
  // 根据流程实例UUID列表，变更流程到期时间
  router.post('/api/workflow/work/changeFlowDueTime', controller.workflowController.changeFlowDueTime);
  // 工作加锁
  router.post('/api/workflow/work/lockWork', controller.workflowController.lockWork);
  // 工作解锁
  router.post('/api/workflow/work/unlockWork', controller.workflowController.unlockWork);
  // 获取环节锁信息
  router.get('/api/workflow/work/listLockInfo', controller.workflowController.listLockInfo);
  // 获取环节锁信息
  router.get('/api/workflow/work/listAllLockInfo', controller.workflowController.listAllLockInfo);
  // 释放锁
  router.post('/api/workflow/work/releaseAllLock', controller.workflowController.releaseAllLock);

  router.get('/workflow/delegation/settings/add', controller.workflowDelegationController.delegationView);
  router.get('/workflow/delegation/settings/edit', controller.workflowDelegationController.delegationView);
  router.get('/workflow/delegation/settings/view/:uuid', controller.workflowDelegationController.vueDelegationView);
  router.get('/workflow/delegation/settings/consult/:uuid', controller.workflowDelegationController.vueDelegationView);
  // 流程工作委托设置
  // 获取工作委托设置
  router.get('/api/workflow/delegation/settiongs/get', controller.workflowDelegationController.get);
  // 保存工作委托设置
  router.post('/api/workflow/delegation/settiongs/save', controller.workflowDelegationController.save);
  // 工作委托设置激活
  router.post('/api/workflow/delegation/settiongs/activeAll', controller.workflowDelegationController.activeAll);
  // 工作委托设置终止
  router.post('/api/workflow/delegation/settiongs/deactiveAll', controller.workflowDelegationController.deactiveAll);
  // 工作委托设置删除
  router.post('/api/workflow/delegation/settiongs/deleteAll', controller.workflowDelegationController.deleteAll);
  // 工作委托生效
  router.post('/api/workflow/delegation/settiongs/delegationActive', controller.workflowDelegationController.delegationActive);
  // 工作委托拒绝
  router.post('/api/workflow/delegation/settiongs/delegationRefuse', controller.workflowDelegationController.delegationRefuse);

  // 流程设置
  router.post('/api/workflow/setting/save', controller.workflowSettingController.save);
  router.post('/api/workflow/setting/saveAll', controller.workflowSettingController.saveAll);
  // =========================================================================

  router.get('/workflow/scheme/diction/xml.action', controller.workflowDefinitionController.schemeDirectionXml);
  router.get('/workflow/scheme/flow/xml.action', controller.workflowDefinitionController.schemeFlowXml);
  router.post('/api/workflow/scheme/checkFlowXmlForUpdate', controller.workflowDefinitionController.checkFlowXmlForUpdate);
  router.post('/workflow/scheme/save.action', controller.workflowDefinitionController.schemeSaveXml);

  router.get('/api/workflow/work/getWorkProcessAndOpinionPositionConfigs', controller.workflowController.getWorkProcess);
  router.get('/api/workflow/work/getWorkProcesses', controller.workflowController.getWorkProcesses);
  router.get('/api/workflow/work/getWorkProcess', controller.workflowController.getOldWorkProcess);
  router.get('/api/workflow/work/getTaskProcess', controller.workflowController.getTaskProcess);
  // 获取数据快照列表
  router.get(
    '/api/workflow/work/listFlowDataSnapshotWithoutDyformDataByFlowInstUuid',
    controller.workflowController.listFlowDataSnapshotWithoutDyformDataByFlowInstUuid
  );
  router.post('/api/workflow/work/getFlowDataSnapshotByIds', controller.workflowController.getFlowDataSnapshotByIds);
  // 获取当前用户的意见用于签署
  router.get('/api/workflow/work/getCurrentUserOpinion2Sign', controller.workflowController.getCurrentUserOpinion2Sign);
  router.post('/api/workflow/opinion/saveFlowOpinionCategories', controller.workflowOpinionController.saveFlowOpinionCategories);
  router.delete('/api/workflow/opinion/deleteRecentOpinion', controller.workflowOpinionController.deleteRecentOpinion);

  //保存加载规则
  router.post('/api/workflow/user/preferences/saveLoadingRules', controller.workflowController.saveLoadingRules);
  //获取加载规则
  router.get('/api/workflow/user/preferences/getLoadingRules', controller.workflowController.getLoadingRules);
  //流程分类下拉数据查询接口
  router.get('/api/workflow/category/query', controller.workflowController.categoryQuery);
  //流程定义下拉数据查询接口
  router.get('/api/workflow/definition/query', controller.workflowController.definitionQuery);
  //待办工作数据查询接口
  router.post('/api/workflow/work/todo/query', controller.workflowController.todoQuery);
  //待办工作数据查询下一笔数据
  router.post('/api/workflow/work/todo/next/record', controller.workflowController.nextRecord);
  //稍后处理接口
  router.post('/api/workflow/work/todo/dealLater', controller.workflowController.todoDealLater);
  //待办工作数据按计时状态分组取总数
  router.post('/api/workflow/work/todo/groupAndCountByTimingState', controller.workflowController.groupAndCountByTimingState);
  //待办工作数据按流程定义ID分组取总数
  router.post('/api/workflow/work/todo/groupAndCountByFlowDefId', controller.workflowController.groupAndCountByFlowDefId);
  //待办工作数据查询总数接口
  router.post('/api/workflow/work/todo/count', controller.workflowController.todoCount);

  // 手机端入口
  router.get('/workflow/mobile/work/view/todo2', controller.mobileWorkflowController.todo);
  router.get('/workflow/mobile/work/view/share', controller.mobileWorkflowController.todo);

  // 流程定义升级
  router.get('/workflow/define/flow/upgrade/2v6_2_12', controller.workflowDefinitionUpgradeController.upgrade2v6_2_12);
  router.get('/workflow/define/flow/upgrade/2v6_2_9_1', controller.workflowDefinitionUpgradeController.upgrade2v6_2_9_1);
  router.get('/workflow/define/flow/upgrade/2v6_2_7', controller.workflowDefinitionUpgradeController.upgrade2v6_2_7);
  router.get('/workflow/define/flow/upgrade/2v6_2_5', controller.workflowDefinitionUpgradeController.upgrade2v6_2_5);
  // 流程计时器数据升级
  router.get('/workflow/task/timer/upgrade/2v6_2_7', controller.workflowTaskTimerUpgradeController.upgrade2v6_2_7);

  // 查看归档的流程数据
  router.get('/workflow/file/viewer/view/archive', controller.workflowFileViewerController.viewArchive);
  router.get('/workflow/file/viewer/view/dyform/archive', controller.workflowFileViewerController.viewDyformArchive);

  // 流程设计器
  // 获取所有可用的打印模板列表
  router.get('/api/workflow/definition/getMessageTemplates', controller.workflowDefinitionController.getMessageTemplates);
  // 获取所有流程消息模板分类
  router.get(
    '/api/workflow/definition/getSelectFlowMessageTemplateType',
    controller.workflowDefinitionController.getSelectFlowMessageTemplateType
  );
  // 获取套打模板
  router.get('/api/workflow/definition/getPrintTemplates', controller.workflowDefinitionController.getPrintTemplates);
  // 根据存储单据UUID获取对应的显示单据
  router.get('/api/workflow/definition/getVformsByPformUuid', controller.workflowDefinitionController.getVformsByPformUuid);
  // 根据动态表单UUID，获取表单字段信息
  router.get('/api/workflow/definition/getFormFields', controller.workflowDefinitionController.getFormFields);
  // 根据动态表单UUID，获取表单区块信息
  router.get('/api/workflow/definition/getFormBlocks', controller.workflowDefinitionController.getFormBlocks);
  // 根据流程定义ID，获取表单区块信息
  router.get('/api/workflow/definition/getFormBlocksByFlowDefId', controller.workflowDefinitionController.getFormBlocksByFlowDefId);
  // 根据动态表单UUID，获取表单页签信息
  router.get('/api/workflow/definition/getFormSubtabs', controller.workflowDefinitionController.getFormSubtabs);
  // 根据动态表单UUID，获取从表信息
  router.get('/api/workflow/definition/getSubForms', controller.workflowDefinitionController.getSubForms);
  // 根据流程定义ID，获取流程计时器信息
  router.get('/api/workflow/definition/listFlowTimerByFlowId', controller.workflowDefinitionController.listFlowTimerByFlowId);
  // 获取业务类别
  router.get('/api/workflow/definition/getBusinessTypes', controller.workflowDefinitionController.getBusinessTypes);
  // 根据业务类别获取业务角色
  router.get('/api/workflow/definition/getBusinessRoles', controller.workflowDefinitionController.getBusinessRoles);
  // 获取子流程分发的自定义接口
  router.get(
    '/api/workflow/definition/getSubtaskDispatcherCustomInterfaces',
    controller.workflowDefinitionController.getSubtaskDispatcherCustomInterfaces
  );
  // 获取分支流分发的自定义接口
  router.get(
    '/api/workflow/definition/getCustomDispatcherBranchTaskInterfaces',
    controller.workflowDefinitionController.getCustomDispatcherBranchTaskInterfaces
  );
  // 根据流程定义ID获取总条数
  router.get('/api/workflow/definition/countById', controller.workflowDefinitionController.countById);
  // 根据流程定义UUID判断是否存在未办结的流程实例
  router.get(
    '/api/workflow/definition/isExistsUnfinishedFlowInstanceByFlowDefUuid',
    controller.workflowDefinitionController.isExistsUnfinishedFlowInstanceByFlowDefUuid
  );
  // 获取流程实例UUID获取流程办理状态信息
  router.get('/api/workflow/definition/getFlowHandingStateInfo', controller.workflowDefinitionController.getFlowHandingStateInfo);
  // 根据流程定义ID，获取最高版本的流程定义的环节map<id, name>
  router.get('/api/workflow/definition/getFlowTasks', controller.workflowDefinitionController.getFlowTasks);
  // 根据流程定义ID，判断是否自动提交分支流
  router.get('/api/workflow/definition/isAutoSubmitForkTask', controller.workflowDefinitionController.isAutoSubmitForkTask);
  // 根据流程定义ID，获取表单字段信息
  router.get('/api/workflow/definition/getFormFieldsByFlowDefId', controller.workflowDefinitionController.getFormFieldsByFlowDefId);
  // 获取集合表达式
  router.get('/api/workflow/definition/getUserCustomExpression', controller.workflowDefinitionController.getUserCustomExpression);
  // 检测表达式
  router.get('/api/workflow/definition/checkUserCustomExpression', controller.workflowDefinitionController.checkUserCustomExpression);
  // 获取当前用户所在单位的组织版本
  router.get(
    '/api/workflow/definition/getCurrentUserUnitOrgVersions',
    controller.workflowDefinitionController.getCurrentUserUnitOrgVersions
  );
  // 获取流程表字段
  router.get('/api/workflow/work/sortFields', controller.workflowDefinitionController.getSortFields);

  // 送审批
  // 判断是否允许通过单据转换规则转换源表单数据
  router.get(
    '/api/workflow/approve/isAllowedConvertDyformDataByBotRuleId',
    controller.workflowApproveController.isAllowedConvertDyformDataByBotRuleId
  );
  // 通过单据转换规则转换源表单数据
  router.post('/api/workflow/approve/convertDyformDataByBotRuleId', controller.workflowApproveController.convertDyformDataByBotRuleId);
  // 送审批
  router.post('/api/workflow/approve/sendToApprove', controller.workflowApproveController.sendToApprove);

  // 流程仿真
  // 根据流程定义ID列出所有环节
  router.get('/api/workflow/simulation/listTaskByFlowDefId', controller.workflowSimulationController.listTaskByFlowDefId);
  // 获取仿真数据
  router.get('/api/workflow/simulation/getSimulationData', controller.workflowSimulationController.getSimulationData);
  // 仿真提交数据
  router.post('/api/workflow/simulation/simulationSubmit', controller.workflowSimulationController.simulationSubmit);
  // 清理仿真数据
  router.get('/api/workflow/simulation/cleanSimulationData', controller.workflowSimulationController.cleanSimulationData);

  // 流程删除、流程回收站相关
  // - 单个逻辑删除流程定义
  router.post('/api/workflow/definition/logical-delete', controller.workflowRecycleController.logicalDelete);
  // - 批量逻辑删除流程定义
  router.post('/api/workflow/definition/logical-delete-all', controller.workflowRecycleController.logicalDeleteAll);
  // - 单个恢复流程定义
  router.post('/api/workflow/definition/recovery', controller.workflowRecycleController.recovery);
  // - 批量恢复流程定义
  router.post('/api/workflow/definition/recovery-all', controller.workflowRecycleController.recoveryAll);
  // - 单个物理删除流程定义
  router.post('/api/workflow/definition/physical-delete', controller.workflowRecycleController.physicalDelete);
  // - 批量物理删除流程定义
  router.post('/api/workflow/definition/physical-delete-all', controller.workflowRecycleController.physicalDeleteAll);
  // - 获取流程定义定时清除设置
  router.get('/api/workflow/definition/cleanup/config', controller.workflowRecycleController.getCleanupConfig);
  // - 保存流程定义定时清除设置
  router.post('/api/workflow/definition/cleanup/config', controller.workflowRecycleController.saveCleanupConfig);

  //检查信息记录前置条件
  router.post('/api/workflow/work/checkRecordPreCondition', controller.workflowController.checkRecordPreCondition);
  router.get('/api/workflow/work/getWorkProcess', controller.workflowController.getOldWorkProcess);
  // 分页查询承办信息
  router.post('/api/workflow/work/loadShareDatasByPage', controller.workflowController.loadShareDatasByPage);
  // 分页查询信息分发
  router.post('/api/workflow/work/loadDistributeInfosByPage', controller.workflowController.loadDistributeInfosByPage);
  // 分页查询操作记录
  router.post('/api/workflow/work/loadRelateOperationByPage', controller.workflowController.loadRelateOperationByPage);
};
