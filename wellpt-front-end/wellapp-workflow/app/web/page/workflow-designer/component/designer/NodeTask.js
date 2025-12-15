import { multiJobFlowTypeConfig, notFoundMainIdentityOptions, identitySelectOptions } from './constant';
// task 65个参数
// 子流程 95个参数，后30个参数是子流程自己的
class NodeTask {
  constructor({
    id = '',
    name = '',
    type = '1',
    startRights = [],
    rights = [],
    doneRights = [],
    monitorRights = [],
    adminRights = [],
    copyToRights = [],
    viewerRights = [],

    users = [],
    transferUsers = [],
    copyUsers = [],
    emptyToUsers = [],
    monitors = [],
    decisionMakers = [],
    startRightConfig = {},
    todoRightConfig = {},
    doneRightConfig = {},
    monitorRightConfig = {},
    adminRightConfig = {},
    copyToRightConfig = {},
    viewerRightConfig = {}
  } = {}) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.code = '';
    this.isSetUser = '2'; // 办理人设置

    this.isSetUserEmpty = '1'; // 找不到办理人时
    this.emptyToTask = undefined;
    this.emptyNoteDone = '';
    this.isSelectAgain = ''; // 由前一环节办理人选择具体办理人
    this.isOnlyOne = ''; // 只能选择一个办理人
    this.isAnyone = ''; // 只需要一个人办理完成即进入下一环节
    this.isByOrder = ''; // 按人员顺序依次办理
    this.sameUserSubmit = '0'; // 与前一环节办理人相同时

    this.isSetTransferUser = '2'; // 转办人员设置
    this.isSetCopyUser = '0'; // 抄送设置
    this.copyUserCondition = ''; // 抄送前置条件
    this.isSetMonitor = '0'; // 督办设置
    this.isAllowApp = ''; //  允许移动端审批

    this.startRights = startRights; // 发起权限
    this.rights = rights; // 待办权限
    this.doneRights = doneRights; // 已办权限
    this.monitorRights = monitorRights; // 督办权限
    this.adminRights = adminRights; // 监控权限
    this.enableOpinionPosition = '0'; // 待办意见立场开关
    this.optNames = []; // 待办意见立场
    this.requiredOpinionPosition = '0'; // 意见立场必填
    this.showUserOpinionPosition = '0'; // 显示用户意见立场值
    this.showOpinionPositionStatistics = '0'; // 显示意见立场统计

    this.canEditForm = '0'; // 表单可编辑 第一个环节默认打开'1'，显示编辑列；其他关闭
    this.hideBlocks = []; // 隐藏的区块；62默认是空，点击环节显示配置后才会赋值
    this.hideTabs = []; // 隐藏的标签
    this.hideFields = []; // 隐藏的字段
    this.readFields = [];
    this.editFields = []; // 编辑的字段
    this.notNullFields = []; // 必填字段
    this.allFormFields = []; // 所有表单字段
    this.allFormFieldWidgetIds = [];
    this.formBtnRightSettings = [
      // {
      //   type: 32,
      //   value: "169022864677142528={\"edit\":{\"headerButton\":\"addRow;delRow;moveUpRow\",\"rowButton\":\"editRow;copyRow;moveUpRow;moveDownRow\"},\"show\":{\"headerButton\":\"addRow;delRow;moveUpRow;moveDownRow\",\"rowButton\":\"editRow;copyRow;moveUpRow;moveDownRow\"}}",
      //   argValue: null
      // },
      // {
      //   type: 32,
      //   value: `169022864677142528=${JSON.stringify({
      //     show: {
      //       headerButton: '',
      //       rowButton: ''
      //     },
      //     edit: {
      //       headerButton: '',
      //       rowButton: ''
      //     }
      //   })}`
      // }
    ]; // 字段按钮设置（扩展设置）
    this.formID = undefined; // 环节展示表单
    this.parallelGateway = {
      forkMode: 1, // 分支模式
      chooseForkingDirection: '', // 提交时可选流向
      businessType: '',
      businessTypeName: '',
      businessRole: '',
      businessRoleName: '',
      branchTaskMonitors: [],
      undertakeSituationPlaceHolder: null,
      undertakeSituationTitleExpression: null,
      undertakeSituationButtons: [],
      undertakeSituationColumns: [],
      sortRule: null,
      undertakeSituationOrders: [],
      infoDistributionPlaceHolder: null,
      infoDistributionTitleExpression: null,
      operationRecordPlaceHolder: null,
      operationRecordTitleExpression: null,
      joinMode: 0
    }; // 流转设置
    this.allowReturnAfterRollback = '0';
    this.onlyReturnAfterRollback = '0';
    this.notRollback = '0';

    this.printTemplate = '';
    this.printTemplateUuid = '';
    this.listener = '';

    this.timerId = '';
    this.conditionName = '';
    this.conditionBody = '';
    this.untreadTasks = [];
    this.fileRights = [];
    this.fieldProperty = {
      name: null,
      value: null
    };
    this.buttons = [];
    this.records = [];

    this.users = users;
    this.transferUsers = transferUsers;
    this.copyUsers = copyUsers;
    this.emptyToUsers = emptyToUsers;
    this.monitors = monitors;
    this.decisionMakers = decisionMakers; // 决策人人员

    this.isConfirmCopyUser = null;
    this.isInheritMonitor = '1';
    this.granularity = '';
    this.untreadType = null;
    this.snName = '';
    this.serialNo = '';
    this.printTemplateId = '';
    this.customJsModule = '';
    this.eventScripts = [];
    this.expandList = null;

    this.notCancel = '0';
    this.copyToRights = copyToRights;
    this.viewerRights = viewerRights;
    this.startRightConfig = startRightConfig;
    this.todoRightConfig = todoRightConfig;
    this.doneRightConfig = doneRightConfig;
    this.monitorRightConfig = monitorRightConfig;
    this.adminRightConfig = adminRightConfig;
    this.copyToRightConfig = copyToRightConfig;
    this.viewerRightConfig = viewerRightConfig;

    this.enabledJobFlowType = false;
    this.multiJobFlowType = multiJobFlowTypeConfig[0]['id']; // 一人多职流转设置
    this.mainJobNotFoundFlowType = notFoundMainIdentityOptions[0]['value'];
    this.selectJobMode = identitySelectOptions[1]['value'];
    this.selectJobField = false;
    this.jobField = '';

    this.i18n = {
      zh_CN: {}
    };
    this.setDefaultI18n();
  }
  setDefaultI18n() {
    const nameCode = `${this.id}.taskName`;
    this.i18n.zh_CN[nameCode] = this.name;
  }
}

export default NodeTask;
