'use strict';

module.exports = app => {
  const { controller, router } = app;
  router.get('/web/login/config/index', controller.loginConfController.index);
  router.post('/web/login/config/save', controller.loginConfController.save);
  router.post('/web/login/config/saveUnitConf', controller.loginConfController.saveUnitConf);
  router.post('/web/login/config/preview', controller.loginConfController.preview);
  router.post('/web/login/config/saveLoginSecurityConfig', controller.loginConfController.saveLoginSecurityConfig);
  router.get('/web/login/config/getLoginSecurityConfig', controller.loginConfController.getLoginSecurityConfig);
  router.get('/web/login/config/loginPageSettings', controller.loginConfController.loginPageSettings);
  router.get('/web/login/config/unitLoginPageSetting', controller.loginConfController.unitLoginPageSetting);

  /**
   * 登录/登出相关控制地址
   */
  router.get('/login/unit/*', controller.loginController.login); // login登录页
  router.get('/login', controller.loginController.systemLogin); // login登录页
  router.get('/sys/:system/login', controller.loginController.systemLogin); // 系统登录页
  router.get('/sys/:system/user_reg', controller.loginController.systemUserReg); // 系统用户注册地址
  router.get('/sys/:system/user_forget_pwd', controller.loginController.systemUserForgetPwd); // 系统用户注册地址
  router.get('/user_modify_password', controller.loginController.systemUserModifyPwd);

  router.get('/logout', controller.loginController.logout);
  router.get('/j_spring_security_logout', controller.loginController.logout);
  router.get('/security_logout', controller.loginController.logout);
  router.get('/superadmin/login', controller.loginController.login); // login登录页
  router.get('/captcha', controller.loginController.captcha);
  router.get('/getOnlineUser', controller.loginController.getOnlineUser); // 获取在线用户列表
  router.post('/destroySession', controller.loginController.destroySession); // 获取在线用户列表
  router.post('/heartbeat', controller.loginController.heartbeat); // 用户在线心跳检测
  router.post('/login/feishuUserTokenInfo', controller.loginController.feishuUserTokenInfo); // 飞书扫码登录
  // =========================================================================

  /**
   * 数据服务类控制地址
   */
  router.post('/json/data/services', controller.jsonDataServiceController.forward);
  router.post('/json/data/services/pinYin', controller.jsonDataServiceController.pinYin);
  router.post('/common/select2/:method', controller.selectMetaController.query); // 下拉数据查询
  router.post('/common/select2/group/:method', controller.selectMetaController.query); // 分组下拉数据查询
  router.get('/basicdata/system/param/get', controller.basicDataController.getSystemParam); // 系统参数获取
  router.get('/basicdata/selective/data/get', controller.basicDataController.selectiveDataGet); // 基础选择项数据获取
  router.post('/basicdata/treecomponent/loadTree', controller.basicDataController.treeComponetLoadTree); // 树形数据查询
  router.post('/basicdata/treecomponent/loadDataStoreTree', controller.basicDataController.loadDataStoreTree); // 树形数据查询
  router.post('/common/validate/check/exists', controller.validationMetaController.checkExist);
  router.get('/common/validation/metadata', controller.validationMetaController.metadata);
  router.get('/basicdata/workhour/save.action', controller.basicDataController.workhourDaySave);
  router.post('/basicdata/workhour/:type', controller.basicDataController.workhourDayList); // 工作日

  // 图片库相关
  router.get('/basicdata/img/category/:uuid', controller.pictureLibCategoryController.getCategoryByUuid);
  router.post('/basicdata/img/category/save', controller.pictureLibCategoryController.saveCategory);
  router.delete('/basicdata/img/category/:uuid', controller.pictureLibCategoryController.deleteCategoryByUuid);
  router.get('/basicdata/img/category/queryAllCategory', controller.pictureLibCategoryController.queryAllCategory);
  router.post('/basicdata/img/category/addImgs/:uuid', controller.pictureLibCategoryController.addImagesToCategory);
  router.get('/basicdata/img/category/queryImgs/:uuid', controller.pictureLibCategoryController.getImagesByCategory);
  router.post('/basicdata/img/category/delImgs/:uuid', controller.pictureLibCategoryController.deleteImageFromCategoryByUuid);

  // =========================================================================

  /**
   * 用户类地址控制
   */
  router.get('/security/user/details', controller.userController.queryDetails); // 用户详情信息获取
  router.get('/multi/org/dialog.html', controller.userController.orgUserDialogPage); // 组织弹窗页
  // router.get('/org/user/view/photo/:uuid', controller.userController.userImg);
  router.post('/personalinfo/show/infoBody', controller.userController.personalinfo);
  router.post('/api/user/behavior/log', controller.userController.commitUserBehaviorLog); // 用户详情信息获取
  // =========================================================================
  router.get('/pt/dingtalk/:method', controller.dingtalkController.forwardDingtalkMethod);
  router.post('/pt/dingtalk/:method', controller.dingtalkController.forwardDingtalkMethod);
  // 钉钉免密登录校验
  router.get('/mobile/pt/dingtalk/start', controller.dingtalkController.dingtalkOauth2Start);
  router.get('/mobile/pt/dingtalk/getconnect/oauth2', controller.dingtalkController.dingtalkOauth2Contect);

  router.all('/mobile/pt/dingtalk/callback', controller.dingtalkController.proxy);
  router.all('/mobile/pt/dingtalk/getJsApiConfig', controller.dingtalkController.proxy);
  // 钉钉-多部门人员审核
  router.post('/pt/dingtalk/getMultiDeptUserAudit', controller.dingtalkController.dingtalkGetMultiDeptUserAudit);

  /**
   * 组织管理地址控制
   */
  router.post('multi/org/version/export', controller.orgController.orgVersionExport);
  router.get('/org/manager', controller.orgController.orgManagerDetail);
  router.get('/biz-org/manager', controller.orgController.bizOrgManagerDetail);

  /**
   * 组织版本模块接口
   */
  //配置
  router.put('/api/org/multi/modifyOrgChildNode', controller.multiOrgController.modifyOrgChildNode);
  router.post('/api/org/multi/addOrgChildNode', controller.multiOrgController.addOrgChildNode);
  router.post('/api/org/multi/deleteOrgChildNode', controller.multiOrgController.deleteOrgChildNode);
  router.get('/api/org/multi/getOrgNodeByTreeUuid', controller.multiOrgController.getOrgNodeByTreeUuid);
  router.get('/api/org/multi/getOrgNodePrivilegeResultTree', controller.multiOrgController.getOrgNodePrivilegeResultTree);
  router.get('/api/org/multi/getOrgAsTreeByVersionId', controller.multiOrgController.getOrgAsTreeByVersionId);

  //系统单位管理-系统单位
  router.post('/api/org/multi/modifySystemUnit', controller.multiOrgController.modifySystemUnit);
  router.post('/api/org/multi/addSystemUnit', controller.multiOrgController.addSystemUnit);
  router.get('/api/org/multi/getSystemUnitVo', controller.multiOrgController.getSystemUnitVo);
  //系统单位管理-单位管理员
  router.post('/api/org/multi/modifyUnitAdmin', controller.multiOrgController.modifyUnitAdmin);
  router.post('/api/org/multi/addUnitAdmin', controller.multiOrgController.addUnitAdmin);
  router.get('/api/org/multi/getUser', controller.multiOrgController.getUser);
  router.get('/api/org/multi/getUserPrivilegeResultTree', controller.multiOrgController.getUserPrivilegeResultTree);
  router.get('/api/security/role/getRoleTree', controller.roleController.getRoleTree);
  //系统单位管理-组织选择项
  router.post('/api/org/multi/modifyOrgOption', controller.multiOrgController.modifyOrgOption);
  router.post('/api/org/multi/addOrgOption', controller.multiOrgController.addOrgOption);
  router.get('/api/org/multi/getOrgOption', controller.multiOrgController.getOrgOption);
  router.get('/api/org/option/getOptionStyle', controller.multiOrgController.getOptionStyle);

  /**
   * 组织管理-组织版本
   */
  router.post('/api/org/version/addMultiOrgVersion', controller.multiOrgVersionController.addMultiOrgVersion);
  router.post('/api/org/version/modifyOrgVersionBaseInfo', controller.multiOrgVersionController.modifyOrgVersionBaseInfo);
  router.get('/api/org/version/getOrgVersionVo', controller.multiOrgVersionController.getOrgVersionVo);
  router.post('/api/org/version/addNewOrgVersionForUpgrade', controller.multiOrgVersionController.addNewOrgVersionForUpgrade);
  router.post('/api/org/version/activeOrgVersion', controller.multiOrgVersionController.activeOrgVersion);
  router.post('/api/org/version/unactiveOrgVersion', controller.multiOrgVersionController.unactiveOrgVersion);
  router.get('/api/org/version/getVersionById', controller.multiOrgVersionController.getVersionById);
  router.get('/api/org/version/queryVersionTreeOfMySubUnit', controller.multiOrgVersionController.queryVersionTreeOfMySubUnit);
  router.get('/api/org/version/getDefaultVersionBySystemUnitId', controller.multiOrgVersionController.getDefaultVersionBySystemUnitId);

  /**
   * 组织管理-用户
   */
  router.post('/api/org/multi/modifyUser', controller.multiOrgController.modifyUser);
  router.post('/api/org/multi/addUser', controller.multiOrgController.addUser);
  router.post('/api/org/multi/clearAllUserOfUnit', controller.multiOrgController.clearAllUserOfUnit);
  router.get('/api/org/multi/getUserAllPrivilegeResultTree', controller.multiOrgController.getUserAllPrivilegeResultTree);
  router.post('/api/org/multi/deleteUsers', controller.multiOrgController.deleteUsers);
  router.get('/api/security/role/queryRoleByCurrentUserUnitId', controller.roleController.queryRoleByCurrentUserUnitId);
  router.post('/api/security/role/publishRoleUpdatedEvent', controller.roleController.publishRoleUpdatedEvent);
  router.post(
    '/api/org/facade/queryAllUserRoleInfoDtoListByUserIdAndRoleUuids',
    controller.orgController.queryAllUserRoleInfoDtoListByUserIdAndRoleUuids
  );
  router.get('/api/org/facade/queryAllUserRoleInfoDtoListByUserId', controller.orgController.queryAllUserRoleInfoDtoListByUserId);
  router.get('/security/privilege/publishPrivilegeUpdatedEvent', controller.privilegeController.publishPrivilegeUpdatedEvent);
  router.get('/security/privilege/getOtherResourceTreeOnlyCheck', controller.privilegeController.getOtherResourceTreeOnlyCheck);
  router.post('/api/org/user/account/resetUserPwd', controller.multiOrgUserAccountController.resetUserPwd);
  router.post('/api/org/user/account/resetAllUserPwd', controller.multiOrgUserAccountController.resetAllUserPwd);

  router.post('/api/org/user/account/unlockAccount', controller.multiOrgUserAccountController.unlockAccount);
  router.post('/api/org/user/account/lockAccount', controller.multiOrgUserAccountController.lockAccount);
  router.post('/api/org/user/account/forbidAccount', controller.multiOrgUserAccountController.forbidAccount);
  router.post('/api/org/user/account/unforbidAccount', controller.multiOrgUserAccountController.unforbidAccount);
  router.post('/api/org/user/account/pwdUnlockAccount', controller.multiOrgUserAccountController.pwdUnlockAccount);
  router.post('/api/org/user/account/saveRelationAccount', controller.multiOrgUserAccountController.saveRelationAccount);
  router.post('/api/org/user/account/unbound/:account', controller.multiOrgUserAccountController.unboundAccount);
  router.get('/api/org/user/account/getRelationAccounts', controller.multiOrgUserAccountController.getRelationAccounts);
  router.get('/api/org/user/idnumber/update/redirect', controller.multiOrgUserController.redirectUpdateUserIdnumber);
  router.get('/api/security/queryAllGrantedAuthoritiesByUser', controller.securityApiController.queryAllGrantedAuthoritiesByUser);
  router.get('/api/page/definition/listPath', controller.appPageDefinitionMgrController.listPath);

  router.post('/api/org/tree/dialog/children', controller.multiOrgTreeDialogController.children);
  router.post('/api/org/tree/dialog/search', controller.multiOrgTreeDialogController.search);
  router.post('/api/org/tree/dialog/full', controller.multiOrgTreeDialogController.full);
  router.post('/api/org/tree/dialog/allUserSearch', controller.multiOrgTreeDialogController.allUserSearch);
  router.post('/api/org/tree/dialog/smartName', controller.multiOrgTreeDialogController.smartName);
  router.post('/api/org/tree/dialog/fullNamePath', controller.multiOrgTreeDialogController.fullNamePath);

  router.post('/api/org/user/modifyUser', controller.multiOrgUserController.modifyUser);
  router.get('/api/org/user/getUserById', controller.multiOrgUserController.getUserById);
  router.post('/api/org/user/recomputeUserWorkInfoByEleId', controller.multiOrgUserController.recomputeUserWorkInfoByEleId);
  router.post('/api/org/user/recomputeUserWorkInfoByVersions', controller.multiOrgUserController.recomputeUserWorkInfoByVersions);

  /**
   * 组织管理-组织类型
   */
  router.get('/api/org/multi/getOrgType', controller.multiOrgController.getOrgType);
  router.post('/api/org/multi/modifyOrgType', controller.multiOrgController.modifyOrgType);
  router.post('/api/org/multi/addOrgType', controller.multiOrgController.addOrgType);

  /**
   * 组织管理-职务
   */
  router.post('/api/org/multi/modifyDuty', controller.multiOrgController.modifyDuty);
  router.post('/api/org/multi/addDuty', controller.multiOrgController.addDuty);
  router.get('/api/org/multi/getDuty', controller.multiOrgController.getDuty);

  /**
   * 组织管理-职级
   */
  router.post('/api/org/multi/modifyJobRank', controller.multiOrgController.modifyJobRank);
  router.post('/api/org/multi/addJobRank', controller.multiOrgController.addJobRank);
  router.get('/api/org/multi/getJobRank', controller.multiOrgController.getJobRank);

  /**
   * 组织管理-群组
   */
  router.get('/api/org/group/getGroupVo', controller.multiOrgGroupController.getGroupVo);
  router.post('/api/org/group/addGroup', controller.multiOrgGroupController.addGroup);
  router.post('/api/org/group/modifyGroup', controller.multiOrgGroupController.modifyGroup);
  router.post('/api/org/group/deleteGroup', controller.multiOrgGroupController.deleteGroup);
  router.post('/api/org/group/deleteGroups', controller.multiOrgGroupController.deleteGroups);
  router.get('/api/org/group/getGroupPrivilegeResultTree', controller.multiOrgGroupController.getGroupPrivilegeResultTree);

  /**
   * 组织管理-common
   */
  router.get('/api/org/multi/getOrgOptionListByUnitIdAndOnlyShow', controller.multiOrgController.getOrgOptionListByUnitIdAndOnlyShow);
  router.get(
    '/api/org/multi/queryOrgOptionListBySystemUnitIdAndOptionOfPT',
    controller.multiOrgController.queryOrgOptionListBySystemUnitIdAndOptionOfPT
  );
  router.get('/api/org/multi/queryAllSystemUnitList', controller.multiOrgController.queryAllSystemUnitList);
  router.post('/api/org/tree/dialog/queryUnitTreeDialogDataByType', controller.multiOrgTreeDialogController.queryUnitTreeDialogDataByType);
  //根据系统单位id，获取组织版本
  router.get('/api/org/version/getVersionBySystemUnitId', controller.importExportMgrController.getRequire);

  /**
   * 组织管理-orgApifacade调用
   */
  router.post('/api/org/facade/getNameByOrgEleIds', controller.orgController.getNameByOrgEleIds);
  router.get('/api/org/facade/getCurrentUserProperty', controller.orgController.getCurrentUserProperty);

  /**
   * 单据转换规则
   */
  router.post('/bot/ruleConfig/isDebug', controller.botController.ruleConfigIsDebug);

  /**
   * 同步管理
   */
  router.post('/syn/trigger/create', controller.synController.synTriggerCreate);
  router.post('/syn/trigger/drop', controller.synController.synTriggerDrop);
  router.post('/syn/trigger/disable', controller.synController.synTriggerDisable);
  router.post('/syn/trigger/enable', controller.synController.synTriggerEnable);
  router.post('/syn/trigger/reGenerate', controller.synController.synTriggerReGenerate);

  /**
   * 用户偏好
   */
  router.post('/api/user/preferences/save', controller.userController.savePreferences); // 保存用户偏好
  router.get('/api/user/preferences/getValue', controller.userController.getValuePreferences); // 获取用户偏好数值
  router.get('/api/user/preferences/get', controller.userController.getPreferences); // 获取用户偏好

  /**
   * 流程定义
   */
  router.post('/api/workflow/category/getAllBySystemUnitIdsLikeName', controller.flowCategoryController.getAllBySystemUnitIdsLikeName);
  // 获取流程分类
  router.get('/api/workflow/category/get', controller.flowCategoryController.get);
  // 保存流程分类
  router.post('/api/workflow/category/save', controller.flowCategoryController.save);
  // 生成流程分类编号
  router.post('/api/workflow/category/generateFlowCategoryCode', controller.flowCategoryController.generateFlowCategoryCode);
  // 删除流程分类
  router.post('/api/workflow/category/deleteAll', controller.flowCategoryController.deleteAll);
  // 删除没用的流程分类
  router.post('/api/workflow/category/deleteWhenNotUsed', controller.flowCategoryController.deleteWhenNotUsed);
  // 复制
  router.post('/api/workflow/definition/copy', controller.flowDefinitionController.copy);
  // 删除
  router.post('/api/workflow/definition/delete', controller.flowDefinitionController.delete);
  // 查看修改日记
  router.get('/api/workflow/definition/listLogs', controller.flowDefinitionController.listLogs);
  // 查看修改日记比较的XML
  router.get('/api/workflow/definition/getLogCompareXml', controller.flowDefinitionController.getLogCompareXml);
  // 流程信息格式
  router.get('/api/workflow/format/get', controller.flowFormatController.get);
  router.post('/api/workflow/format/save', controller.flowFormatController.save);
  router.post('/api/workflow/format/deleteAll', controller.flowFormatController.deleteAll);
  // 流程意见
  // 获取意见
  router.get('/api/workflow/opinion/get', controller.flowOpinionController.get);
  // 获取意见
  router.get('/api/workflow/opinion/getByOpinionCategory', controller.flowOpinionController.getByOpinionCategory);
  // 保存意见
  router.post('/api/workflow/opinion/save', controller.flowOpinionController.save);
  // 删除意见
  router.post('/api/workflow/opinion/deleteAll', controller.flowOpinionController.deleteAll);
  // 获取意见分类
  router.get('/api/workflow/opinion/category/get', controller.flowOpinionController.getCategory);
  // 保存意见分类
  router.post('/api/workflow/opinion/category/save', controller.flowOpinionController.saveCategory);
  // 删除意见分类
  router.post('/api/workflow/opinion/category/delete', controller.flowOpinionController.deleteCategory);
  // 删除意见分类及意见
  router.post('/api/workflow/opinion/categoryAndOpinion/delete', controller.flowOpinionController.deleteCategoryAndOpinion);
  // 根据数据字典获取意见分类树
  router.get(
    '/api/workflow/opinion/getFlowOpinionCategoryTreeByBusinessAppDataDic',
    controller.flowOpinionController.getFlowOpinionCategoryTreeByBusinessAppDataDic
  );
  /**
   * 流程签署意见规则
   */
  //保存流程签署意见规则
  router.post('/api/opinion/rule/saveOpinionRule', controller.flowOpinionRuleController.saveOpinionRule);
  //获取流程签署意见规则详情
  router.get('/api/opinion/rule/getOpinionRuleDetail', controller.flowOpinionRuleController.getOpinionRuleDetail);
  //判断意见规则是否被引用
  router.post('/api/opinion/rule/isReferencedByOpinionRuleUuids', controller.flowOpinionRuleController.isReferencedByOpinionRuleUuids);
  //根据主键uuid集合删除意见规则
  router.post('/api/opinion/rule/delete', controller.flowOpinionRuleController.delete);

  /**
   * 系统参数
   */
  router.get('/api/basicdata/system/param/query', controller.basicDataController.querySystemParam); // 查询系统参数

  router.get('/well/jobinfo', controller.taskMaintainController.taskAssignmentCenter); // 任务维护

  router.get('/api/pwd/setting/getSystemParamsPwdTiming', controller.orgController.getSystemParamsPwdTiming); // 获取密码相关的定时操作开关
  router.get('/api/pwd/setting/getMultiOrgPwdSetting', controller.orgController.getMultiOrgPwdSetting); // 获取密码规则
  router.post('/api/pwd/setting/saveMultiOrgPwdSetting', controller.orgController.saveMultiOrgPwdSetting); // 保存密码规则

  router.post('/api/personalinforest/modifyCurrentUserPasswordEncrypt', controller.orgController.modifyCurrentUserPasswordEncrypt); // 保存当前用户密码
  router.get('/api/pwd/setting/getPwdSettingTimerPageUrl', controller.orgController.getPwdSettingTimerPageUrl); // 获取定时操作实现方式管理地址

  router.post('/api/org/user/account/resetUserDefinedPwd', controller.userController.resetUserDefinedPwd); // 保存当前用户密码
  router.post('/api/org/user/account/resetAllUserDefinedPwd', controller.userController.resetAllUserDefinedPwd); // 获取定时操作实现方式管理地址

  /**
   * 计时服务
   */
  // 获取计时器配置列表
  router.get('/api/timer/config/selectdata', controller.timerConfigController.selectdata);

  // 计时服务分类
  router.post('/api/ts/timer/category/getAllBySystemUnitIdsLikeName', controller.timeServiceController.getAllBySystemUnitIdsLikeName);
  router.get('/api/ts/timer/category/get', controller.timeServiceController.getCategory);
  router.post('/api/ts/timer/category/save', controller.timeServiceController.saveCategory);
  router.post('/api/ts/timer/category/deleteAll', controller.timeServiceController.deleteAllCategory);
  router.post('/api/ts/timer/category/isUsed', controller.timeServiceController.isUsedCategory);

  // 计时器配置
  router.get('/api/ts/timer/config/get', controller.timeServiceController.getConfig);
  router.get('/api/ts/timer/config/selectdata', controller.timeServiceController.selectDataConfig);
  router.post('/api/ts/timer/config/isUsed', controller.timeServiceController.isUsedConfig);
  router.post('/api/ts/timer/config/save', controller.timeServiceController.saveConfig);
  router.post('/api/ts/timer/config/deleteAll', controller.timeServiceController.deleteAllConfig);

  // 节假日管理
  router.get('/api/ts/holiday/get', controller.timeServiceController.getHoliday);
  router.get('/api/ts/holiday/getHolidayInstanceDate', controller.timeServiceController.getHolidayInstanceDate);
  router.post('/api/ts/holiday/save', controller.timeServiceController.saveHoliday);
  router.post('/api/ts/holiday/deleteAll', controller.timeServiceController.deleteAllHoliday);
  router.post('/api/ts/holiday/getAllBySystemUnitIdsLikeName', controller.timeServiceController.getAllHolidayBySystemUnitIdsLikeName);
  router.post('/api/ts/holiday/getAllBySystemUnitIdsLikeFields', controller.timeServiceController.getAllBySystemUnitIdsLikeFields);
  router.post('/api/ts/holiday/isUsed', controller.timeServiceController.isUsedHoliday);

  // 节假日安排
  router.get('/api/ts/holiday/schedule/listByYear', controller.timeServiceController.getHolidayScheduleListByYear);
  router.get('/api/ts/holiday/schedule/listAllYear', controller.timeServiceController.getHolidayScheduleListAllYear);
  router.post('/api/ts/holiday/schedule/saveAll', controller.timeServiceController.saveAllHolidaySchedule);
  router.post('/api/ts/holiday/schedule/deleteAll', controller.timeServiceController.deleteAllAllHolidaySchedule);

  // 工作时间方案
  router.get('/api/ts/work/time/plan/get', controller.timeServiceController.getWorkTimePlan);
  router.get('/api/ts/work/time/plan/getWorkDate', controller.timeServiceController.getWorkDate);
  router.post('/api/ts/work/time/plan/save', controller.timeServiceController.saveWorkTimePlan);
  router.post('/api/ts/work/time/plan/saveAsNewVersion', controller.timeServiceController.saveAsNewVersion);
  router.get('/api/ts/work/time/plan/setAsDefault', controller.timeServiceController.setAsDefault);
  router.post('/api/ts/work/time/plan/deleteAll', controller.timeServiceController.deleteAllWorkTimePlan);
  router.post('/api/ts/work/time/plan/isUsed', controller.timeServiceController.isUsedWorkTimePlan);
  router.get('/api/ts/work/time/plan/getSysDate', controller.timeServiceController.getSysDate);
  router.post('/api/ts/work/time/plan/getAllBySystemUnitIdsLikeName', controller.timeServiceController.getAllWorkPlan);
  router.get('/api/ts/work/time/plan/listNewVersionTipByUuids', controller.timeServiceController.listNewVersionTipByUuids);
  router.get('/api/ts/work/time/plan/getMaxVersionByUuid', controller.timeServiceController.getMaxVersionByUuid);
  router.get('/api/ts/work/time/plan/history/get', controller.timeServiceController.getPlanHistory);

  // 打印模板分类
  router.post('/api/printTemplate/category/save', controller.appPrintTemplateCateController.saveCate);
  router.post('/api/printTemplate/category/getTreeAllBySystemUnitIdsLikeName', controller.appPrintTemplateCateController.queryCate);
  router.post('/api/printTemplate/category/deleteWhenNotUsed', controller.appPrintTemplateCateController.deleteCate);
  router.get('/api/printTemplate/category/get', controller.appPrintTemplateCateController.getCateDetail);

  // 职务体系管理
  router.get('/api/org/duty/hierarchy/switch', controller.multiOrgRankController.hierarchySwitch);
  router.post('/api/org/duty/hierarchy/dutySeqSave', controller.multiOrgRankController.dutySeqSave);
  router.post('/api/org/duty/hierarchy/dutySeqUpdate', controller.multiOrgRankController.dutySeqUpdate);
  router.get('/api/org/duty/hierarchy/dutySeqTree', controller.multiOrgRankController.dutySeqTree);
  router.get('/api/org/duty/hierarchy/jobGradeList', controller.multiOrgRankController.jobGradeList);
  router.post('/api/org/duty/hierarchy/jobGradeSave', controller.multiOrgRankController.jobGradeSave);
  router.delete('/api/org/duty/hierarchy/dutySeqDelete/:uuid', controller.multiOrgRankController.dutySeqDelete);
  router.get('/api/org/duty/hierarchy/dutySeqInfo/:uuid', controller.multiOrgRankController.dutySeqInfo);
  router.get('/api/org/duty/hierarchy/jobRankTree', controller.multiOrgRankController.jobRankTree);
  router.get('/api/org/duty/hierarchy/jobRankTree', controller.multiOrgRankController.jobRankTree);
  router.get('/api/org/duty/hierarchy/dutyHierarchyExport', controller.multiOrgRankController.dutyHierarchyExport);
  router.get('/api/org/duty/hierarchy/dutyHierarchyView', controller.multiOrgRankController.dutyHierarchyView);
  router.get('/api/org/multi/listJobRankByDutySeqUuid/:uuid', controller.multiOrgRankController.listJobRankByDutySeqUuid);
  router.post('/api/org/multi/deleteDuty', controller.multiOrgRankController.batchDeleteDuty);
  router.delete('/api/org/multi/deleteDuty/:uuid', controller.multiOrgRankController.deleteDuty);
  router.get('/api/org/multi/getJobRankByJobId/:id/:jid', controller.multiOrgRankController.getJobRankByJobId);
  router.delete('/api/org/multi/deleteJobRank/:uuid', controller.multiOrgRankController.deleteJobRank);

  // 数据导入导出管理
  // 校验路径
  router.get('/api/dataIO/checkPath', controller.importExportMgrController.getRequire);
  // 查看任务记录详情
  router.get('/api/dataIO/taskRecordInfo/:type/:uuid', controller.importExportMgrController.getRequire);
  // 任务取消
  router.get('/api/dataIO/cancelTask/:type/:uuid', controller.importExportMgrController.getRequire);
  // 重新发起任务(未完成)
  router.get('/api/dataIO/restartTask/:type/:uuid', controller.importExportMgrController.getRequire);
  // 默认临时文件夹
  router.get('/api/dataIO/defaultFilePath', controller.importExportMgrController.getRequire);
  // 导入记录数据详情页面查看源数据接口
  router.get('/api/dataIO/getImportSourceData/:sourceUuid', controller.importExportMgrController.getRequire);
  // 数据导出
  router.post('/api/dataIO/exportData', controller.importExportMgrController.postRequire);
  // 数据导入
  router.post('/api/dataIO/importData', controller.importExportMgrController.postRequire);
  // 数据归属单位
  router.get('/api/dataIO/querySystemUnitList', controller.importExportMgrController.getRequire);

  // 版式文档服务配置
  // 版式文档服务配置保存
  router.post('/api/basicdata/layoutDocumentServiceConf/saveBean', controller.layoutDocumentServiceConfController.saveBean);
  // 版式文档服务配置删除
  router.post('/api/basicdata/layoutDocumentServiceConf/deleteByUuids', controller.layoutDocumentServiceConfController.deleteByUuids);
  // 版式文档服务配置详情
  router.get('/api/basicdata/layoutDocumentServiceConf/getByUuid', controller.layoutDocumentServiceConfController.getByUuid);
  // 获取启用的配置
  router.get(
    '/api/basicdata/layoutDocumentServiceConf/getEnableConfigList',
    controller.layoutDocumentServiceConfController.getEnableConfigList
  );
  // 获取启用的配置
  router.get(
    '/api/basicdata/layoutDocumentServiceConf/beforeEnableLayoutDocumentConfig',
    controller.layoutDocumentServiceConfController.beforeEnableLayoutDocumentConfig
  );
  // 获取启用的配置
  router.post(
    '/api/basicdata/layoutDocumentServiceConf/changeLayoutDocumentConfigStatus',
    controller.layoutDocumentServiceConfController.changeLayoutDocumentConfigStatus
  );

  router.get('/api/log/manage/operation/getLogManageOperation', controller.flowDefinitionController.getLogManageOperation);
  router.get(
    '/api/log/manage/operation/saveFlowListExportLogManageOperation',
    controller.flowDefinitionController.saveFlowListExportLogManageOperation
  );

  // 集团组织
  router.post('/api/multi/group/addOrUpdate', controller.groupOrgController.addOrUpdate);
  router.post('/api/multi/group/del', controller.groupOrgController.del);
  router.get('/api/multi/group/get', controller.groupOrgController.get);
  router.post('/api/multi/group/isEnable', controller.groupOrgController.isEnable);

  // 头部组件
  // 在线用户列表
  // router.post('/getLoginingUser',controller.appNewheaderController.getLoginingUser);

  // 文档链接查看
  router.get('/api/document/link/view/:uuid', controller.documentLinkController.view);

  // 业务流程
  router.get('/biz/process/item/instance/new/:id', controller.bizController._newItem); // 发起业务办件
  router.get('/biz/process/item/instance/view', controller.bizController.viewItem); // 查看业务办件
  router.get('/biz/process/node/instance/view', controller.bizController.viewProcessNode); // 查看过程节点办件
  router.get('/biz/process/instance/view', controller.bizController.viewProcess); // 查看业务流程办件
  router.get('/biz/process/assemble/:uuid', controller.bizController.assemble); // 业务总线装配
  router.get('/process-designer/index', controller.bizController.desinger); // 业务流程设计器

  // 工作交接
  router.post('/api/wh/handover/saveWorkHandover', controller.workhandOverController.saveWorkHandover);
  router.post('/api/wh/handover/deleteWorkHandover', controller.workhandOverController.deleteWorkHandover);
  router.get('/api/wh/handover/getWorkHandoverByUuid', controller.workhandOverController.getWorkHandoverByUuid);
  router.get('/api/wh/handover/getWorkSettings', controller.workhandOverController.getWorkSettings);
  router.post('/api/wh/handover/saveWorkSettings', controller.workhandOverController.saveWorkSettings);
  router.get('/api/wh/handover/getFlowDatasRecords', controller.workhandOverController.getFlowDatasRecords);

  // 数据清理
  router.get('/data-cleanup/index', controller.dataCleanupController.index);
  router.get('/data-cleanup/system-clean', controller.dataCleanupController.systemClean);

  // api 集成数据
  router.post('/api-link-proxy/post', controller.apiLinkController.postProxy);
};
