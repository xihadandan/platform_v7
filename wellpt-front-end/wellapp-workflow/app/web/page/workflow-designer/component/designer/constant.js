const NodeDefaultWidth = 170;
const NodeDefaultHeight = 56;
const NodeCircleWidth = 56;
const NodeCircleHeight = 56;

const SwimlaneColumnWidth = 224;
const SwimlaneSelectionWidth = 112;
const NodeSwimlaneWidth = SwimlaneColumnWidth;
const SwimlaneRowHeight = 336;
const SwimlaneSelectionHeight = 56;
const NodeSwimlaneHeight = SwimlaneRowHeight + SwimlaneSelectionHeight;
const NodeSwimlaneIconWidth = 32;
const NodeSwimlaneIconHeight = 32;

const NodeGuideWidth = 392;
const NodeGuideHeight = 224;

export const MinimapWidth = 360;
export const MinimapHeight = 240;

const NodeTask = 'NodeTask'
const NodeCollab = 'NodeCollab'

const constant = {
  BEGIN: 'BEGIN',
  BEGINNAME: '开始',
  END: 'END',
  ENDNAME: '结束',
  StartFlowId: '<StartFlow>',
  EndFlowId: '<EndFlow>',
  NodeDefaultWidth,
  NodeDefaultHeight,
  NodeCircleWidth,
  NodeCircleHeight,
  NodeTaskWidth: NodeDefaultWidth,
  NodeTaskHeight: NodeDefaultHeight,
  NodeConditionWidth: NodeDefaultWidth,
  NodeConditionHeight: NodeDefaultHeight,
  NodeSubflowWidth: NodeDefaultWidth,
  NodeSubflowHeight: NodeDefaultHeight,
  NodeSwimlaneWidth,
  NodeSwimlaneHeight,
  SwimlaneColumnWidth,
  SwimlaneSelectionHeight,
  SwimlaneSelectionWidth,
  SwimlaneRowHeight,
  NodeSwimlaneIconWidth,
  NodeSwimlaneIconHeight,
  NodeGuideWidth,
  NodeGuideHeight,
  MinimapWidth,
  MinimapHeight,
  NodeTask,
  NodeCollab
};
export default constant;

export const selectionType = [
  {
    title: '工具',
    type: 'tools',
    list: [
      { type: 'DEFAULT', title: '选择工具', icon: 'icon-liucheng-xuanze-01' },
      // { type: 'MOVE', title: '移动画布', icon: 'icon-liucheng-tuodong-01' },
      { type: 'BEELINE', title: '直线流向', icon: 'icon-liucheng-zhixian2-01' },
      { type: 'CURVE', title: '曲线流向', icon: 'icon-liucheng-quxian2-01' }
    ]
  },
  {
    title: '节点',
    type: 'nodes',
    list: [
      { type: constant.BEGIN, title: constant.BEGINNAME, svg: 'start', draggable: true },
      { type: 'TASK', title: '环节', svg: 'task', draggable: true },
      { type: 'ROBOT', title: '机器', svg: 'robot', draggable: true },
      { type: 'COLLAB', title: '协作', svg: 'collab', draggable: true },
      { type: 'CONDITION', title: '判断点', svg: 'condition', draggable: true },
      { type: constant.END, title: constant.ENDNAME, svg: 'end', draggable: true }
    ]
  },
  {
    title: '子流程',
    type: 'subWorkFlows',
    list: [{ type: 'SUBFLOW', title: '子流程', svg: 'subflow', draggable: true }]
  },
  {
    title: '辅助',
    type: 'supports',
    list: [
      { type: 'SWIMLANE', title: '泳道', icon: 'icon-yongdao-01', draggable: true },
      { type: 'LABEL', title: '注释', icon: 'icon-liucheng-biaozhu-01', draggable: true }
    ]
  }
];

export const langMap = {
  TASK_NAME: '环节',
  SUBFLOW_NAME: '子流程',
  CONDITION_NAME: '判断点',
  SWIMLANE_NAME: '泳道',
  COLLAB_NAME: '协作',
  ROBOT_NAME: '机器'
};

export const shapeMapConfig = {
  NodeTask: '环节',
  EdgeDirection: '流向',
  NodeCondition: '条件',
  NodeSubflow: '子流程',
  NodeSwimlane: '泳道',
  NodeCollab: '协作',
  NodeRobot: '机器'
};

export const getCustomRules = rules => {
  const validator = params => {
    return (rule, value, callback) => {
      let valid = true;
      if (value) {
        if (Array.isArray(value) && !value.length) {
          valid = false;
        }
      } else {
        valid = false;
      }
      if (valid) {
        callback();
      } else {
        callback(params);
      }
    };
  };
  let rulesCustom = {};
  for (const key in rules) {
    if (rules[key]['required']) {
      rulesCustom[key] = {
        required: true,
        msg: rules[key]['message'], // 不能和message相同的key
        label: rules[key]['label'] || '',
        trigger: ['change']
      };
      if (rules[key]['validator']) {
        rulesCustom[key]['validator'] = rules[key]['validator'](rules[key]);
      } else {
        rulesCustom[key]['validator'] = validator(rules[key]);
      }
    }
  }
  return rulesCustom;
};

export const flowPropertyRules = {
  name: { required: true, message: '请输入名称', tabKey: '0', label: '名称' },
  id: { required: true, message: '请输入ID', tabKey: '0' },
  categorySN: { required: true, message: '请选择分类', tabKey: '0' },
  moduleId: { required: true, message: '请选择模块', tabKey: '0' },
  formID: { required: true, message: '请选择表单', tabKey: '0' },
  accessPermissionProvider: { required: true, message: '请选择接口', tabKey: '0' },
  'equalFlow.id': { required: true, message: '请选择等价流程', tabKey: '3' },
  orgId: {
    required: true,
    message: '请选择组织',
    tabKey: '3',
    condition: data => {
      // 是否需要校验
      let needVerify = false;
      if (data.useDefaultOrg === '0') {
        needVerify = true;
      }
      return needVerify;
    }
  }
};

export const userSelectItemValidator = item => {
  let valid = true;
  if (item.type === 1 || item.type === 32 || item.type === 8) {
    if (!item.value && !item.userOptions.length) {
      valid = false;
    }
    if (item.orgId) {
      if (item.enabledJobGrade === '1' && item.jobGrade) {
        valid = true;
      } else if (item.enabledJobRank === '1' && item.jobRankId) {
        valid = true;
      }
    }
  }
  return valid;
};

const userSelectValidator = args => {
  return (rule, list, callback) => {
    let valid = true;
    for (let index = 0; index < list.length; index++) {
      const item = list[index];
      if (!userSelectItemValidator(item)) {
        valid = false;
        break;
      }
    }
    if (!list.length) {
      valid = false;
    }
    if (valid) {
      callback();
    } else {
      callback(args);
    }
  };
};

export const taskRules = {
  name: { required: true, message: '名称不能为空', tabKey: '0' },
  id: { required: true, message: 'ID必须有且唯一！', tabKey: '0' },
  users: {
    required: true,
    message: '当办理人为指定办理人时不能为空！',
    tabKey: '0',
    condition: data => {
      // 是否需要校验
      let needVerify = false;
      if (data.isSetUser === '1') {
        needVerify = true;
      }
      return needVerify;
    },
    validator: userSelectValidator
  },
  transferUsers: {
    required: true,
    message: '指定转办范围不能为空！',
    tabKey: '0',
    condition: data => {
      let needVerify = false;
      if (data.isSetTransferUser === '1') {
        needVerify = true;
      }
      return needVerify;
    },
    validator: userSelectValidator
  },
  copyUsers: {
    required: true,
    message: '指定抄送人时不能为空！',
    tabKey: '0',
    condition: data => {
      let needVerify = false;
      if (data.isSetCopyUser === '1') {
        needVerify = true;
      }
      return needVerify;
    },
    validator: userSelectValidator
  },
  monitors: {
    required: true,
    message: '当办理人为指定督办人时不能为空！',
    tabKey: '0',
    condition: data => {
      let needVerify = false;
      if (data.isSetMonitor === '1') {
        needVerify = true;
      }
      return needVerify;
    },
    validator: userSelectValidator
  },
  optNames: {
    required: true,
    message: '意见立场不能为空！',
    tabKey: '1',
    condition: data => {
      let needVerify = false;
      if (data.enableOpinionPosition === '1') {
        needVerify = true;
      }
      return needVerify;
    }
  }
};
export const subflowRules = {
  name: { required: true, message: '名称必须有且唯一', tabKey: '0' },
  id: { required: true, message: 'ID必须有且唯一！', tabKey: '0' },
  businessType: { required: true, message: '请选择分发组织', tabKey: '0' }
};
export const conditionRules = {
  name: { required: true, message: '判断点必须有名称', tabKey: '0' }
};
export const directionRules = {
  name: { required: true, message: '请输入流向名称', tabKey: '0' },
  buttonOrder: {
    required: true,
    message: '请输入按钮排序号',
    tabKey: '3',
    condition: data => {
      let needVerify = false;
      if (data.useAsButton === '1') {
        needVerify = true;
      }
      return needVerify;
    }
  }
};

export const swimlaneRules = {
  layout: { required: true, message: '布局不能为空', tabKey: '0' }
};

export const propertyAddKey = [
  'name',
  'id',
  'code',
  'version',
  'moduleId',
  'titleExpression',
  'applyId',
  'orgVersionId',
  'orgVersionIds',
  'titleExpressionMode'
];

export const constDefault = 'default';
export const constCustom = 'custom';
export const constDefine = 'define';
export const constReadonly = 'readonly';
export const constNone = 'none';

export const titleExpressionConfig = [
  { label: '默认', value: constDefault },
  { label: '自定义标题格式', value: constCustom }
];

export const defineConfig = [
  { label: '默认', value: constDefault },
  { label: '自定义', value: constDefine }
];

export const yesOrNoConfig = [
  { label: '否', value: '0' },
  { label: '是', value: '1' }
];

export const permissionDefaultOrCustom = [
  { label: '所有人', value: constDefault },
  { label: '指定人员', value: constCustom }
];

export const handlerList = [
  {
    type: 'PriorUser',
    label: '前办理人维度',
    list: [
      {
        id: 'useroptions_1',
        name: '前办理人',
        value: 'PriorUser'
      },
      {
        id: 'useroptions_21',
        name: '前办理人的直接汇报人',
        value: 'DirectLeaderOfPriorUser'
      },
      {
        id: 'useroptions_23',
        name: '前办理人的部门领导',
        value: 'DeptLeaderOfPriorUser'
      },
      {
        id: 'useroptions_2',
        name: '前办理人的上级领导',
        value: 'LeaderOfPriorUser'
      },
      {
        id: 'useroptions_27',
        name: '前办理人的分管领导',
        value: 'BranchedLeaderOfPriorUser'
      },
      {
        id: 'useroptions_3',
        name: '前办理人的所有上级领导',
        value: 'AllLeaderOfPriorUser'
      },
      {
        id: 'useroptions_8',
        name: '前办理人的部门人员',
        value: 'DeptOfPriorUser'
      },
      {
        id: 'useroptions_9',
        name: '前办理人的上级部门人员',
        value: 'ParentDeptOfPriorUser'
      },
      {
        id: 'useroptions_10',
        name: '前办理人的根部门人员',
        value: 'RootDeptOfPriorUser'
      },
      {
        id: 'useroptions_90',
        name: '前办理人的根节点人员',
        value: 'RootNodeOfPriorUser'
      },
      {
        id: 'useroptions_29',
        name: '前办理人的单位人员',
        value: 'BizUnitOfPriorUser'
      },
      {
        id: 'useroptions_52',
        name: '前办理人直接下属',
        value: 'SubordinateOfPriorUser'
      },
      {
        id: 'useroptions_53',
        name: '前办理人的所有下属',
        value: 'AllSubordinateOfPriorUser'
      },
      // {
      //   id: 'useroptions_91',
      //   name: '前办理人的同业务角色人员',
      //   value: 'SameBizRoleOfPriorUser'
      // },
      {
        id: 'useroptions_7',
        name: '前一个环节办理人',
        value: 'PriorTaskUser'
      }
    ]
  },
  {
    type: 'Creator',
    label: '申请人维度',
    list: [
      {
        id: 'useroptions_4',
        name: '申请人',
        value: 'Creator'
      },
      {
        id: 'useroptions_51',
        name: '申请人的直接汇报人',
        value: 'DirectLeaderOfCreator'
      },
      {
        id: 'useroptions_24',
        name: '申请人的部门领导',
        value: 'DeptLeaderOfCreator'
      },
      {
        id: 'useroptions_5',
        name: '申请人的上级领导',
        value: 'LeaderOfCreator'
      },
      {
        id: 'useroptions_28',
        name: '申请人的分管领导',
        value: 'BranchedLeaderOfCreator'
      },
      {
        id: 'useroptions_6',
        name: '申请人的所有上级领导',
        value: 'AllLeaderOfCreator'
      },
      {
        id: 'useroptions_11',
        name: '申请人的部门人员',
        value: 'DeptOfCreator'
      },
      {
        id: 'useroptions_12',
        name: '申请人的上级部门人员',
        value: 'ParentDeptOfCreator'
      },
      {
        id: 'useroptions_13',
        name: '申请人的根部门人员',
        value: 'RootDeptOfCreator'
      },
      {
        id: 'useroptions_14',
        name: '申请人的根节点人员',
        value: 'RootNodeOfCreator'
      },
      {
        id: 'useroptions_30',
        name: '申请人的单位人员',
        value: 'BizUnitOfCreator'
      }
      // {
      //   id: 'useroptions_92',
      //   name: '申请人的同业务角色人员',
      //   value: 'SameBizRoleOfCreator'
      // }
    ]
  }
];

export const handlerFilterList = [
  {
    type: 'PriorUser',
    label: '前办理人维度',
    list: [
      {
        id: 'useroptions_15',
        name: '限于前办理人的同一部门人员',
        value: 'SameDeptAsPrior'
      },
      {
        id: 'useroptions_16',
        name: '限于前办理人的同一根部门人员',
        value: 'SameRootDeptAsPrior'
      },
      {
        id: 'useroptions_80',
        name: '限于前办理人的同一根节点人员',
        value: 'SameRootNodeAsPrior'
      },
      {
        id: 'useroptions_31',
        name: '限于前办理人的同一单位人员',
        value: 'SameBizUnitAsPrior'
      },
      {
        id: 'useroptions_39',
        name: '限于前办理人的直接汇报人',
        value: 'SameDirectLeaderAsPrior'
      },
      {
        id: 'useroptions_34',
        name: '限于前办理人的部门领导',
        value: 'SameDeptLeaderAsPrior'
      },
      {
        id: 'useroptions_33',
        name: '限于前办理人的上级领导',
        value: 'SameLeaderAsPrior'
      },
      {
        id: 'useroptions_35',
        name: '限于前办理人的分管领导',
        value: 'SameBranchLeaderAsPrior'
      },
      {
        id: 'useroptions_17',
        name: '限于前办理人的所有上级领导',
        value: 'SameAllLeaderAsPrior'
      },
      {
        id: 'useroptions_54',
        name: '限于前办理人的直接下属',
        value: 'SameSubordinateOfPrior'
      },
      {
        id: 'useroptions_55',
        name: '限于前办理人的所有下属',
        value: 'SameAllSubordinateOfPrior'
      }
      // {
      //   id: 'useroptions_83',
      //   name: '限于前办理人的同业务角色人员',
      //   value: 'SameBizRoleAsPrior'
      // }
    ]
  },
  {
    type: 'Creator',
    label: '申请人维度',
    list: [
      {
        id: 'useroptions_18',
        name: '限于申请人的同一部门人员',
        value: 'SameDeptAsCreator'
      },
      {
        id: 'useroptions_19',
        name: '限于申请人的同一根部门人员',
        value: 'SameRootDeptAsCreator'
      },
      {
        id: 'useroptions_81',
        name: '限于申请人的同一根节点人员',
        value: 'SameRootNodeAsCreator'
      },
      {
        id: 'useroptions_32',
        name: '限于申请人的同一单位人员',
        value: 'SameBizUnitAsCreator'
      },
      {
        id: 'useroptions_40',
        name: '限于申请人的直接汇报人',
        value: 'SameDirectLeaderAsCreator'
      },
      {
        id: 'useroptions_37',
        name: '限于申请人的部门领导',
        value: 'SameDeptLeaderAsCreator'
      },
      {
        id: 'useroptions_36',
        name: '限于申请人的上级领导',
        value: 'SameLeaderAsCreator'
      },
      {
        id: 'useroptions_38',
        name: '限于申请人的分管领导',
        value: 'SameBranchLeaderAsCreator'
      },
      {
        id: 'useroptions_20',
        name: '限于申请人的所有上级领导',
        value: 'SameAllLeaderAsCreator'
      }
      // {
      //   id: 'useroptions_82',
      //   name: '限于申请人的同业务角色人员',
      //   value: 'SameBizRoleAsCreator'
      // }
    ]
  }
];

// 权限颗粒
export const granularityOptions = [
  { label: '动态', value: 'A' },
  { label: '用户', value: 'U' },
  { label: '职位', value: 'J' },
  { label: '部门', value: 'D' },
  { label: '单位', value: 'S' }
];

// 开关组件0，1转false，true
export const zeroOne2FalseTrue = [
  'autoUpdateTitle',
  'isActive', // 流程状态
  'pcShowFlag', // PC端流程发起设置
  'isMobileShow', // 移动端流程发起设置
  'keepRuntimePermission',
  'enableAccessPermissionProvider',
  'onlyUseAccessPermissionProvider',
  'autoDelay', // 计时器
  'enableAlarm', // 计时器
  'enableDueDoing', // 计时器
  'isFree',
  'enableMultiOrg'
];

export const warnMsgUserTypeOther = 'Other';
// 提醒消息发送人员类型
export const warnMsgUserTypeList = [
  { type: 32, value: 'Doing', label: '在办人员' },
  { type: 32, value: 'DoingSuperior', label: '在办人员上级领导' },
  { type: 32, value: 'Monitor', label: '督办人员' },
  { type: 32, value: 'Tracer', label: '跟踪人员' },
  { type: 32, value: 'Admin', label: '流程管理人员' },
  { type: 32, value: warnMsgUserTypeOther, label: '其他人员' }
];
// 提醒消息时间单位
export const warnMsgUnitList = [
  {
    id: '3',
    text: '工作日'
  },
  {
    id: '2',
    text: '工作小时'
  },
  {
    id: '1',
    text: '工作分钟'
  },
  {
    id: '86400',
    text: '天'
  },
  {
    id: '3600',
    text: '小时'
  },
  {
    id: '60',
    text: '分钟'
  }
];

export const fieldAndConstantOptions = [
  { label: '常量', value: '1' },
  { label: '字段', value: '2' },
]

export const timingModeUnitOptions = [
  {
    id: '1',
    text: '天'
  },
  {
    id: '2',
    text: '小时'
  },
  {
    id: '3',
    text: '分钟'
  },
]

// 计时方式
export const timingMode = [
  {
    id: '1',
    text: '工作日'
  },
  {
    id: '2',
    text: '工作日（一天24小时）'
  },
  {
    id: '3',
    text: '自然日'
  },
  {
    id: '-1',
    text: '从表单字段读取'
  }
];
export const timingModeUnit1 = [
  {
    id: '3',
    text: '工作日'
  },
  {
    id: '2',
    text: '工作小时'
  },
  {
    id: '1',
    text: '工作分钟'
  }
];
export const timingModeUnit2 = [
  {
    id: '13',
    text: '工作日'
  },
  {
    id: '12',
    text: '小时（工作日）'
  },
  {
    id: '11',
    text: '分钟（工作日）'
  }
];
export const timingModeUnit3 = [
  {
    id: '86400',
    text: '天'
  },
  {
    id: '3600',
    text: '小时'
  },
  {
    id: '60',
    text: '分钟'
  }
];

// 时限类型
export const timeLimitType = [
  {
    id: '1',
    text: '固定时限'
  },
  {
    id: '4',
    text: '固定截止时间'
  },
  {
    id: '2',
    text: '动态时限'
  },
  {
    id: '3',
    text: '动态截止时间'
  }
];
// 固定时限的时限单位 || 动态时限的时限单位
export const timeLimitUnit1 = [
  {
    id: '1',
    text: '天'
  },
  {
    id: '2',
    text: '小时'
  },
  {
    id: '3',
    text: '分钟'
  }
];
// 固定截止时间的时限单位 || 动态截止时间的时限单位
export const timeLimitUnit4 = [
  {
    id: '1',
    text: '日期 2000-01-01'
  },
  {
    id: '2',
    text: '日期到时 2000-01-01 12'
  },
  {
    id: '3',
    text: '日期到分 2000-01-01 12:00'
  }
];

export const dueActionOptions = [
  {
    value: '0',
    label: '默认不自动处理'
  },
  {
    value: '1',
    label: '自动移交给B岗人员办理'
  },
  {
    value: '2',
    label: '自动移交给督办人员办理'
  },
  {
    value: '4',
    label: '自动移交给其他人员办理'
  },
  {
    value: '8',
    label: '自动退回上一个办理环节'
  },
  {
    value: '16',
    label: '自动进入下一个办理环节'
  }
];

export const timeEndTypeConfig = [
  { label: '流程办结结束计时', value: '0' },
  { label: '流出计时环节结束计时', value: '1' },
  { label: '流经指定流向结束计时', value: '2' }
];

export const timerArr1 = [
  {
    id: '3',
    text: '工作日(工作时间)'
  },
  {
    id: '2',
    text: '工作小时(工作时间)'
  },
  {
    id: '1',
    text: '工作分钟(工作时间)'
  },
  {
    id: '13',
    text: '工作日(24小时制)'
  },
  {
    id: '12',
    text: '工作小时(24小时制)'
  },
  {
    id: '11',
    text: '工作分钟(24小时制)'
  },
  {
    id: '86400',
    text: '天'
  },
  {
    id: '3600',
    text: '小时'
  },
  {
    id: '60',
    text: '分钟'
  }
];
export const timerArr2 = [
  {
    id: '20',
    text: '日期 2000-01-01 (工作时间)'
  },
  {
    id: '19',
    text: '日期 2000-01-01 12 (工作时间)'
  },
  {
    id: '18',
    text: '日期 2000-01-01 12:00 (工作时间)'
  },
  {
    id: '23',
    text: '日期 2000-01-01 (24小时制)'
  },
  {
    id: '22',
    text: '日期 2000-01-01 12 (24小时制)'
  },
  {
    id: '21',
    text: '日期 2000-01-01 12:00 (24小时制)'
  }
];
export const timerArr3 = [
  {
    id: '3',
    text: '工作日'
  },
  {
    id: '2',
    text: '工作小时'
  },
  {
    id: '1',
    text: '工作分钟'
  },
  {
    id: '86400',
    text: '天'
  },
  {
    id: '3600',
    text: '小时'
  },
  {
    id: '60',
    text: '分钟'
  }
];

export const useOrgConfig = [
  { label: '系统默认组织', value: '1' },
  { label: '指定组织', value: '0' }
];

export const autoUpgradeOrgVersionConfig = [
  { label: '使用原组织和组织版本', value: '0' },
  { label: '使用新组织和组织版本', value: '1' }
];

export const isSetUserConfig = [
  { label: '由前一环节办理人指定', value: '2' },
  { label: '指定办理人', value: '1' }
];

export const isSetTransferUserConfig = [
  { label: '可转办全组织', value: '2' },
  { label: '指定转办范围', value: '1' }
];

export const isSetUserEmptyConfig = [
  { label: '由前一环节办理人指定', value: '0' },
  { label: '自动进入下一个环节', value: '1' },
  { label: '指定其他办理人', value: '2' }
];

export const sameUserSubmitConfig = [
  {
    text: '不自动提交并关闭页面',
    id: '2'
  },
  {
    text: '不自动提交并刷新页面',
    id: '3'
  },
  {
    text: '不自动提交并刷新页面，且不自动继承意见',
    id: '4'
  },
  {
    text: '自动提交，让办理人确认是否继承上一环节意见',
    id: '0'
  },
  {
    text: '自动提交，且自动继承意见',
    id: '1'
  }
];

export const defaultMessageTemplate = [
  {
    distributers: [
      {
        dtypeName: '办理人',
        id: 'WF_WORK_TODO'
      }
    ],
    isSendMsg: '1',
    type: 'TODO',
    typeName: '流程到达通知'
  },
  {
    distributers: [
      {
        dtypeName: '抄送人员',
        id: 'WF_WORK_COPY'
      }
    ],
    isSendMsg: '1',
    type: 'COPY',
    typeName: '流程到达抄送通知'
  },
  {
    distributers: [
      {
        dtypeName: '督办人员',
        id: 'WF_WORK_SUPERVISE'
      }
    ],
    isSendMsg: '1',
    type: 'SUPERVISE',
    typeName: '督办流程到达通知'
  },
  {
    distributers: [
      {
        dtypeName: '办理人',
        id: 'WF_WORK_COUNTER_SIGN'
      }
    ],
    isSendMsg: '1',
    type: 'COUNTER_SIGN',
    typeName: '会签流程到达通知'
  },
  {
    distributers: [
      {
        dtypeName: '办理人',
        id: 'WF_WORK_ADD_SIGN'
      }
    ],
    isSendMsg: '1',
    type: 'ADD_SIGN',
    typeName: '加签流程到达通知'
  },
  {
    distributers: [
      {
        dtypeName: '办理人',
        id: 'WF_WORK_TRANSFER'
      }
    ],
    isSendMsg: '1',
    type: 'TRANSFER',
    typeName: '转办流程到达通知'
  },
  {
    distributers: [
      {
        dtypeName: '办理人',
        id: 'WF_WORK_ENTRUST'
      }
    ],
    isSendMsg: '1',
    type: 'ENTRUST',
    typeName: '流程委托到达通知'
  },
  {
    distributers: [
      {
        dtypeName: '办理人',
        id: 'WF_WORK_COUNTER_SIGN_RETURN'
      }
    ],
    isSendMsg: '1',
    type: 'COUNTER_SIGN_RETURN',
    typeName: '会签流程返回通知'
  },
  {
    distributers: [
      {
        dtypeName: '办理人',
        id: 'WF_WORK_ROLL_BACK'
      }
    ],
    isSendMsg: '1',
    type: 'ROLL_BACK',
    typeName: '流程退回通知'
  },
  {
    distributers: [
      {
        dtypeName: '原办理人',
        id: 'WF_WORK_REVOKE'
      }
    ],
    isSendMsg: '1',
    type: 'REVOKE',
    typeName: '流程撤回通知'
  },
  {
    distributers: [
      {
        dtypeName: '办理人',
        id: 'WF_WORK_ALARM_DOING'
      },
      {
        dtypeName: '办理人的上级领导',
        id: 'WF_WORK_ALARM_DOING_SUPERIOR'
      },
      {
        dtypeName: '督办人',
        id: 'WF_WORK_ALARM_SUPERVISE'
      },
      {
        dtypeName: '跟踪人',
        id: 'WF_WORK_ALARM_TRACER'
      },
      {
        dtypeName: '其他人员',
        id: 'WF_WORK_ALARM_OTHER'
      },
      {
        dtypeName: '流程管理员',
        id: 'WF_WORK_ALARM_ADMIN'
      }
    ],
    isSendMsg: '1',
    type: 'FLOW_ALARM',
    typeName: '预警提醒'
  },
  {
    distributers: [
      {
        dtypeName: '办理人',
        id: 'WF_WORK_DUE_DOING'
      },
      {
        dtypeName: '办理人的上级领导',
        id: 'WF_WORK_DUE_DOING_SUPERIOR'
      },
      {
        dtypeName: '督办人',
        id: 'WF_WORK_DUE_SUPERVISE'
      },
      {
        dtypeName: '跟踪人',
        id: 'WF_WORK_DUE_TRACER'
      },
      {
        dtypeName: '其他人员',
        id: 'WF_WORK_DUE_OTHER'
      },
      {
        dtypeName: '流程管理员',
        id: 'WF_WORK_DUE_ADMIN'
      }
    ],
    isSendMsg: '1',
    type: 'FLOW_DUE',
    typeName: '逾期提醒'
  },
  {
    distributers: [
      {
        dtypeName: '当前办理人',
        id: 'WF_WORK_EMPTY_NOTE_DONE'
      }
    ],
    isSendMsg: '1',
    type: 'EMPTY_NOTE_DONE',
    typeName: '办理人为空跳过环节消息通知'
  }
];
// 消息分发类型对应的分发人员
export const messageTemplateTypes = {
  TODO: [
    {
      label: '办理人',
      defaultValue: 'WF_WORK_TODO'
    }
  ],
  COPY: [
    {
      label: '抄送人员',
      defaultValue: 'WF_WORK_COPY'
    }
  ],
  SUPERVISE: [
    {
      label: '督办人员',
      defaultValue: 'WF_WORK_SUPERVISE'
    }
  ],
  COUNTER_SIGN: [
    {
      label: '办理人',
      defaultValue: 'WF_WORK_COUNTER_SIGN'
    }
  ],
  ADD_SIGN: [
    {
      label: '办理人',
      defaultValue: 'WF_WORK_ADD_SIGN'
    }
  ],
  TRANSFER: [
    {
      label: '办理人',
      defaultValue: 'WF_WORK_TRANSFER'
    }
  ],
  ENTRUST: [
    {
      label: '办理人',
      defaultValue: 'WF_WORK_ENTRUST'
    }
  ],
  COUNTER_SIGN_RETURN: [
    {
      label: '办理人',
      defaultValue: 'WF_WORK_COUNTER_SIGN_RETURN'
    }
  ],
  ROLL_BACK: [
    {
      label: '办理人',
      defaultValue: 'WF_WORK_ROLL_BACK'
    },
    {
      label: '全部已办人员',
      defaultValue: 'WF_WORK_ROLL_BACK_DONE'
    }
  ],
  REVOKE: [
    {
      label: '原办理人',
      defaultValue: 'WF_WORK_REVOKE'
    }
  ],
  EMPTY_NOTE_DONE: [
    {
      label: '当前办理人',
      defaultValue: 'WF_WORK_EMPTY_NOTE_DONE'
    }
  ],
  READ_RETURN_RECEIPT: [
    {
      label: '上一环节办理人',
      defaultValue: 'WF_WORK_READ_RETURN_RECEIPT'
    }
  ],
  REMIND: [
    {
      label: '办理人',
      defaultValue: 'WF_WORK_REMIND'
    }
  ],
  OVER: [
    {
      label: '流程发起人',
      defaultValue: 'WF_WORK_OVER'
    },
    {
      label: '指定人员|other',
      defaultValue: 'WF_WORK_OVER_NOTIFY_SPECIFIC_USER'
    }
  ],
  FLOW_ALARM: [
    {
      label: '办理人',
      defaultValue: 'WF_WORK_ALARM_DOING'
    },
    {
      label: '办理人的上级领导',
      defaultValue: 'WF_WORK_ALARM_DOING_SUPERIOR'
    },
    {
      label: '督办人',
      defaultValue: 'WF_WORK_ALARM_SUPERVISE'
    },
    {
      label: '跟踪人',
      defaultValue: 'WF_WORK_ALARM_TRACER'
    },
    {
      label: '其他人员',
      defaultValue: 'WF_WORK_ALARM_OTHER'
    },
    {
      label: '流程管理员',
      defaultValue: 'WF_WORK_ALARM_ADMIN'
    }
  ],
  FLOW_DUE: [
    {
      label: '办理人',
      defaultValue: 'WF_WORK_DUE_DOING'
    },
    {
      label: '办理人的上级领导',
      defaultValue: 'WF_WORK_DUE_DOING_SUPERIOR'
    },
    {
      label: '督办人',
      defaultValue: 'WF_WORK_DUE_SUPERVISE'
    },
    {
      label: '跟踪人',
      defaultValue: 'WF_WORK_DUE_TRACER'
    },
    {
      label: '其他人员',
      defaultValue: 'WF_WORK_DUE_OTHER'
    },
    {
      label: '流程管理员',
      defaultValue: 'WF_WORK_DUE_ADMIN'
    }
  ],
  DUE_TURN_OVER_TRUSTEE: [
    {
      label: '逾期流程代理B岗人员',
      defaultValue: 'WF_WORK_DUE_TURN_OVER_TRUSTEE'
    }
  ],
  DUE_TURN_OVER_SUPERVISE: [
    {
      label: '督办人',
      defaultValue: 'WF_WORK_DUE_TURN_OVER_SUPERVISE'
    },
    {
      label: '原办理人',
      defaultValue: 'WF_WORK_DUE_TURN_OVER_SUPERVISE_NOTIFY_DOING'
    }
  ],
  DUE_TURN_OVER_OTHER: [
    {
      label: '原办理人',
      defaultValue: 'WF_WORK_DUE_TURN_OVER_OTHER_NOTIFY_OLD_DOING'
    },
    {
      label: '其他人员',
      defaultValue: 'WF_WORK_DUE_TURN_OVER_OTHER'
    }
  ],
  DUE_RETURN_PREV_TASK: [
    {
      label: '原办理人',
      defaultValue: 'WF_WORK_DUE_RETRUN_PREV_TASK_NOTIFY_OLD_DOING'
    },
    {
      label: '当前办理人',
      defaultValue: 'WF_WORK_DUE_RETRUN_PREV_TASK'
    }
  ],
  DUE_ENTER_NEXT_TASK: [
    {
      label: '原办理人',
      defaultValue: 'WF_WORK_DUE_RETRUN_NEXT_TASK_NOTIFY_OLD_DOING'
    },
    {
      label: '当前办理人',
      defaultValue: 'WF_WORK_DUE_ENTER_NEXT_TASK'
    }
  ],
  NOTIFY_SUB_FLOW_DOING: [
    {
      label: '子流程办理人',
      defaultValue: 'WF_WORK_NOTIFY_SUB_FLOW_DOING'
    }
  ],
  MAIN_SUB_FLOW_RETURN_OVER_OTHER_SUB_FLOW: [
    {
      label: '子流程办理人',
      defaultValue: 'WF_WORK_MAIN_SUB_FLOW_RETURN_OVER_OTHER_SUB_FLOW'
    }
  ],
  SUB_FLOW_REDO: [
    {
      label: '子流程办理人',
      defaultValue: 'WF_WORK_SUB_FLOW_REDO'
    },
    {
      label: '子流程全部已办人员',
      defaultValue: 'WF_WORK_SUB_FLOW_REDO'
    }
  ],
  SUB_FLOW_END: [
    {
      label: '子流程办理人',
      defaultValue: 'WF_WORK_SUB_FLOW_STOP'
    },
    {
      label: '子流程全部已办人员',
      defaultValue: 'WF_WORK_SUB_FLOW_STOP'
    }
  ],
  SUB_FLOW_REMIND: [
    {
      label: '办理人',
      defaultValue: 'WF_WORK_SUB_FLOW_REMIND'
    },
    {
      label: '子流程办理人上级领导',
      defaultValue: 'WF_WORK_SUB_FLOW_REMIND'
    },
    {
      label: '子流程督办人员',
      defaultValue: 'WF_WORK_SUB_FLOW_REMIND'
    },
    {
      label: '子流程跟踪人员',
      defaultValue: 'WF_WORK_SUB_FLOW_REMIND'
    },
    {
      label: '子流程流程管理人员',
      defaultValue: 'WF_WORK_SUB_FLOW_REMIND'
    }
  ],
  SUB_FLOW_TIMELIMIT_MODIFY: [
    {
      label: '子流程办理人',
      defaultValue: 'WF_WORK_SUB_FLOW_TIMELIMIT_MODIFY'
    }
  ],
  TASK_ARRIVE_NOTIFY: [
    {
      label: '指定人员|other',
      defaultValue: 'WF_WORK_TASK_ARRIVE_NOTIFY'
    }
  ],
  TASK_LEAVE_NOTIFY: [
    {
      label: '指定人员|other',
      defaultValue: 'WF_WORK_TASK_LEAVE_NOTIFY'
    }
  ],
  TASK_JUMP_FORWARD: [
    {
      label: '指定人员|other',
      defaultValue: 'WF_WORK_TASK_JUMP_FORWARD_NOTIFY'
    }
  ],
  TASK_SUBMIT_NOTIFY: [
    {
      //环节提交消息通知
      label: '指定人员|other',
      defaultValue: ''
    }
  ],
  TASK_TRANSFER_NOTIFY: [
    {
      //环节转办消息通知
      label: '指定人员|other',
      defaultValue: ''
    }
  ],
  TASK_COUNTERSIGN_NOTIFY: [
    {
      //环节会签消息通知
      label: '指定人员|other',
      defaultValue: ''
    }
  ],
  TASK_ADD_SIGN_NOTIFY: [
    {
      // 环节加签消息通知
      label: '指定人员|other',
      defaultValue: ''
    }
  ],
  TASK_RETURN_NOTIFY: [
    {
      //环节退回消息通知
      label: '指定人员|other',
      defaultValue: ''
    }
  ],
  DIRECTION_SEND_MSG: [
    {
      label: '指定人员|other',
      defaultValue: 'WF_WORK_DIRECTION_SEND_MSG'
    }
  ]
};

// 消息分发的分发节点
export const distributeNodeType = [
  {
    id: 'all',
    text: '全部'
  },
  {
    id: 'task',
    text: '指定环节'
  },
  {
    id: 'direction',
    text: '指定流向'
  },
  {
    id: 'jumptask',
    text: '环节跳转'
  }
];

// 消息分发的分发条件
export const distributeConditionType = [
  {
    id: 'and',
    text: '全部'
  },
  {
    id: 'or',
    text: '任何'
  }
];

// 逻辑操作符
export const logicalOperators = [
  {
    id: '10',
    value: '==',
    text: '等于',
    data: '=='
  },
  {
    id: '11',
    value: '!=',
    text: '不等于',
    data: '!='
  },
  {
    id: '8',
    value: '>',
    text: '大于',
    data: '>'
  },
  {
    id: '9',
    value: '>=',
    text: '大于等于',
    data: '>='
  },
  {
    id: '12',
    value: '<',
    text: '小于',
    data: '<'
  },
  {
    id: '13',
    value: '<=',
    text: '小于等于',
    data: '<='
  },
  {
    id: '14',
    value: 'like',
    text: '包含',
    data: 'contains'
  },
  {
    id: '15',
    value: 'notlike',
    text: '不包含',
    data: 'not contains'
  }
];

// 职等对应的操作符/职等对应的操作符
export const levelOperateors = [
  {
    id: 'equal',
    text: '等于'
  },
  {
    id: 'notEqual',
    text: '不等于'
  },
  {
    id: 'overTop',
    text: '高于'
  },
  {
    id: 'overTopEqual',
    text: '高于等于'
  },
  {
    id: 'lowerThan',
    text: '低于'
  },
  {
    id: 'lowerThanEqual',
    text: '低于等于'
  }
];

export const likeOperateor = [
  {
    id: 'like',
    text: '包含'
  },
  {
    id: 'notlike',
    text: '不包含'
  }
];

export const flowStates = [
  {
    code: '0',
    name: '草稿'
  },
  {
    code: '1',
    name: '审批'
  },
  {
    code: '2',
    name: '办结'
  }
];

export const pointcut = [
  {
    id: 'created',
    text: '流程创建'
  },
  {
    id: 'started',
    text: '流程启动'
  },
  {
    id: 'end',
    text: '流程结束'
  },
  {
    id: 'deleted',
    text: '流程删除'
  }
];

export const customPointCut = [
  {
    id: 'created',
    text: '环节创建'
  },
  {
    id: 'completed',
    text: '环节完成'
  }
];

export const scriptRemark = [
  'applicationContext：spring应用上下文',
  'event：流程相关事件信息',
  'taskInstUuid：环节实例UUID',
  'formUuid：表单定义UUID',
  'dyFormData：表单数据',
  'ctionType：操作类型，如Submit、Rollback等',
  'currentUser：当前用户信息',
  'flowInstUuid：流程实例UUID',
  'taskData：环节数据',
  'dataUuid：表单数据UUID',
  'dyFormFacade：表单接口',
  'opinionText：办理意见',
  'resultMessage：事件脚本执行结果，调用resultMessage.isSuccess()方法返回true，通过resultMessage.setSuccess(true/false)设置脚本执行是否成功'
];

export const opinionPositionList = [
  { type: 16, argValue: '同意', value: '1' },
  { type: 16, argValue: '不同意', value: '0' }
];

export const forkModeConfig = [
  { label: '单一分支', value: 1 },
  { label: '分支模式', value: 2 }
];

export const joinModeConfig = [
  { label: '单路', value: 1 },
  { label: '聚合', value: 2 }
];

export const branchModeConfig = [
  { label: '静态分支', value: '1' },
  { label: '动态多分支', value: '2' }
];

export const isDefaultConfig = [
  { label: '条件分支', value: '0' },
  { label: '缺省分支', value: '1' }
];

export const childLookParentConfig = [
  { label: '默认不可查看主流程', value: '0' },
  { label: '默认可查看主流程', value: '1' }
];

export const createWayOptions = [
  { label: '表单字段', value: '12' },
  { label: '接口定义', value: '3' }
];

export const selectDataList = [
  {
    valueField: 'setOperation',
    data: [
      {
        id: '∪',
        text: '并'
      },
      {
        id: '∩',
        text: '交'
      }
    ],
    searchable: false
  },
  {
    valueField: 'leftBracket_1',
    data: [
      {
        id: '(',
        text: '('
      }
    ],
    searchable: false
  },
  {
    valueField: 'userType',
    data: [
      {
        id: 'Unit',
        text: '组织机构'
      },
      {
        id: 'FormField',
        text: '文档域'
      },
      {
        id: 'TaskHistory',
        text: '办理环节'
      },
      {
        id: 'Option',
        text: '可选项'
      },
      {
        id: 'Interface',
        text: '接口实现'
      }
    ],
    searchable: false
  },
  {
    valueField: 'orgVersionType',
    data: [
      {
        id: '1',
        text: '现在确定'
      },
      {
        id: '2',
        text: '取文档域'
      }
    ],
    searchable: false
  },
  {
    valueField: 'orgVersionId_1',
    data: [],
    searchable: false
  },
  {
    valueField: 'orgVersionId_2',
    data: [],
    searchable: false
  },
  {
    valueField: 'optionOf',
    data: [
      {
        id: 'LeaderOf',
        text: '直接领导'
      },
      {
        id: 'DeptLeaderOf',
        text: '部门领导'
      },
      {
        id: 'BranchedLeaderOf',
        text: '分管领导'
      },
      {
        id: 'AllLeaderOf',
        text: '所有领导'
      },
      {
        id: 'DeptOf',
        text: '部门'
      },
      {
        id: 'ParentDeptOf',
        text: '上级部门'
      },
      {
        id: 'RootDeptOf',
        text: '根部门'
      },
      {
        id: 'SameDeptOf',
        text: '同一直接部门人员'
      },
      {
        id: 'SameRootDeptOf',
        text: '同一根部门人员'
      }
    ],
    searchable: false
  },
  {
    valueField: 'rightBracket_1',
    data: [
      {
        id: ')',
        text: ')'
      }
    ],
    searchable: false
  }
];

export const createInstanceWayOptions = [
  { label: '生成单一实例', value: '1' },
  { label: '按办理人生成实例', value: '2' }
];

export const placeHolderConfig = [
  { label: '同主流程显示位置', value: '1' },
  { label: '指定显示位置', value: '2' }
];

// 签署意见校验场景
export const opinionCheckScene = [
  { label: '提交签署意见', value: 'sceneS001' },
  { label: '退回签署意见', value: 'sceneS002' },
  { label: '转办签署意见', value: 'sceneS003' },
  { label: '会签签署意见', value: 'sceneS004' }
];

export const sortRuleConfig = [
  { label: '默认排序', value: constDefault },
  { label: '自定义排序', value: constCustom }
];

export const fixedField = [
  {
    id: 'todoName',
    text: '承办部门'
  },
  {
    id: 'currentTaskName',
    text: '当前环节'
  },
  {
    id: 'currentTodoUserName',
    text: '当前环节办理人'
  },
  {
    id: 'dueTime',
    text: '办理时限'
  },
  {
    id: 'remainingTime',
    text: '剩余时限'
  },
  {
    id: 'workProcesses',
    text: '办理情况'
  },
  {
    id: 'resultFiles',
    text: '附件'
  }
];

export const fixedSortField = [
  {
    id: 'todoName',
    text: '承办部门'
  },
  {
    id: 'currentTaskName',
    text: '当前环节'
  },
  {
    id: 'currentTodoUserName',
    text: '当前环节办理人'
  },
  {
    id: 'dueTime',
    text: '办理时限'
  }
];

export const btnSource = [
  {
    id: 'add-subflow',
    text: '添加承办(全部)'
  },
  {
    id: 'add-major-flow',
    text: '添加主办'
  },
  {
    id: 'add-minor-flow',
    text: '添加协办'
  },
  {
    id: 'remind',
    text: '催办'
  },
  {
    id: 'send-message',
    text: '信息分发'
  },
  {
    id: 'limit-time',
    text: '协办时限'
  },
  {
    id: 'redo',
    text: '重办'
  },
  {
    id: 'stop',
    text: '终止'
  },
  {
    id: 'closeSubView',
    text: '关闭子流程查看本流程'
  },
  {
    id: 'allowSubView',
    text: '允许子流程查看本流程'
  }
];

export const sortSource = [
  {
    id: 'asc',
    text: '升序'
  },
  {
    id: 'desc',
    text: '降序'
  }
];

export const branchInstanceTypeConfig = [
  { label: '独立分支', value: '1' },
  { label: '主办分支', value: '2' },
  { label: '协办分支', value: '3' }
];

// 信息记录 记录方式
export const recordWay = [
  { label: '重复记录时不替换', value: '1' },
  { label: '重复记录时替换', value: '2' },
  { label: '附加', value: '3' }
];
// 信息记录 内容组织
export const recordAssembler = [
  {
    //     id: 'userJobGradeTaskFormOpinionAssembler',
    //     text: '按用户职位级别组装'
    // },{
    id: 'defaultTaskFormOpinionAssembler',
    text: '按时间升序组织'
  },
  {
    id: 'descTaskFormOpinionAssembler',
    text: '按时间降序组织'
  }
];

// 信息记录 历史内容来源
export const recordContentOrigin = [
  { label: '流程信息记录', value: '1' },
  { label: '表单字段值', value: '2' }
];

// 前置条件类型
export const conditionLogicType = [
  { label: '通过字段值比较', value: '1' },
  { label: '通过投票比例设置条件', value: '2' },
  { label: '通过意见立场判断', value: '6' },
  { label: '通过办理人归属判断', value: '3' },
  { label: '通过职等职级判断', value: '7' },
  { label: '自定义条件', value: '5' },
  { label: '逻辑条件', value: '4' }
];

// 前置条件-职级
export const conditionDutyData = [
  {
    text: '申请人职等',
    id: 'A1'
  },
  {
    text: '申请人职级',
    id: 'A2'
  },
  {
    text: '当前办理人职等',
    id: 'A3'
  },
  {
    text: '当前办理人职级',
    id: 'A4'
  }
];

//前置条件-逻辑关系
export const conditionAndOr = [
  {
    id: '&',
    text: '并且',
    value: '&&'
  },
  {
    id: '|',
    text: '或者',
    value: '||'
  }
];
// 左括号
export const conditionLBracket = [
  {
    id: '(',
    text: '('
  }
];

// 右括号
export const conditionRBracket = [
  {
    id: ')',
    text: ')'
  }
];
// 前置条件-成员
export const conditionGroupType = [
  {
    id: '1',
    text: '当前办理人为所选人员之一'
  },
  {
    id: '0',
    text: '表单字段所选人员为所选人员之一'
  }
];

// 子流程分发条件-成员
export const conditionGroupTypeSub = [
  {
    id: '1',
    text: '分发的办理人在指定人员范围内'
  },
  {
    id: '0',
    text: '分发的组织节点在指定组织内'
  }
];

// 环节权限-自定义按钮-按钮类型
export const customAuthBtnSource = [
  {
    id: '1',
    text: '内置功能'
  },
  {
    id: '2',
    text: '事件处理'
  }
];

// 环节权限-自定义按钮-使用方式
export const customAuthUseWay = [
  {
    id: '2',
    text: '新增按钮'
  },
  {
    id: '1',
    text: '替换原按钮'
  }
];

// 环节权限-自定义按钮-应用场景
export const customAuthBtnRoles = [
  {
    id: 'DRAFT',
    text: '草稿'
  },
  {
    id: 'TODO',
    text: '待办'
  },
  {
    id: 'DONE',
    text: '已办'
  },
  {
    id: 'OVER',
    text: '办结'
  },
  {
    id: 'UNREAD',
    text: '未阅'
  },
  {
    id: 'FLAG_READ',
    text: '已阅'
  },
  {
    id: 'ATTENTION',
    text: '关注'
  },
  {
    id: 'SUPERVISE',
    text: '督办'
  },
  {
    id: 'MONITOR',
    text: '监控'
  }
];

export const archiveWayOptions = [
  {
    id: '1',
    text: '流程数据归档'
  },
  {
    id: '2',
    text: '表单数据归档'
  },
  {
    id: '3',
    text: '表单数据转换后归档'
  },
  {
    id: '4',
    text: '脚本归档'
  },
  {
    id: '5',
    text: '弹窗由用户确认表单数据归档范围'
  }
];

export const archiveStrategyOptions = [
  { label: '新增', value: '1' },
  { label: '替换', value: '2' },
  { label: '忽略', value: '3' }
];

// 流程环节权限 settingKey:后台配置的权限key 对应62的数据字典的key
export const flowTaskRights = {
  startRights: { label: '发起权限', title: '发起权限设置', settingKey: 'start', configKey: 'startRightConfig' },
  rights: { label: '待办权限', title: '待办权限设置', settingKey: 'todo', configKey: 'todoRightConfig' },
  doneRights: { label: '已办权限', title: '已办权限设置', settingKey: 'done', configKey: 'doneRightConfig' },
  monitorRights: { label: '督办权限', title: '督办权限设置', settingKey: 'supervise', configKey: 'monitorRightConfig' },
  adminRights: { label: '监控权限', title: '监控权限设置', settingKey: 'monitor', configKey: 'adminRightConfig' },
  copyToRights: { label: '抄送对象权限', title: '抄送对象权限设置', settingKey: 'copyTo', configKey: 'copyToRightConfig' },
  viewerRights: { label: '查阅人员权限', title: '查阅人员权限设置', settingKey: 'viewer', configKey: 'viewerRightConfig' }
};

export const requiredOpinion = {
  B004002: 'requiredSubmitOpinion',
  B004003: 'requiredRollbackOpinion',
  B004005: 'requiredCancelOpinion',
  B004006: 'requiredTransferOpinion',
  B004007: 'requiredCounterSignOpinion',
  B004042: 'requiredAddSignOpinion',
  B004014: 'requiredRemindOpinion',
  B004015: 'requiredHandOverOpinion',
  B004016: 'requiredGotoTaskOpinion'
};

export const submitModeOfAfterRollback = [
  { label: '重新逐环节提交', value: constDefault },
  { label: '可选提交至本环节', value: 'alternative' },
  { label: '直接提交至本环节', value: 'direct' }
];
export const isSetTransferUser = [
  { label: '可转办全组织', value: '2' },
  { label: '指定可转办范围', value: '1' }
];
export const isSetCounterSignUser = [
  { label: '可会签全组织', value: '2' },
  { label: '指定可会签范围', value: '1' }
];
export const isSetAddSignUser = [
  { label: '可加签全组织', value: '2' },
  { label: '指定可加签范围', value: '1' }
];
export const isSetCopyUser = [
  { label: '可抄送全组织', value: '2' },
  { label: '指定可抄送范围', value: '1' }
];

export const transferViewFormMode = [
  { label: '同转办人表单权限', value: constDefault },
  { label: '只读权限', value: constReadonly },
  { label: '由转办人选择', value: constCustom }
];
export const transferOperateRight = [
  { label: '同转办人操作权限', value: constDefault },
  { label: '不可再次转办', value: constNone }
];

export const counterSignViewFormMode = [
  { label: '同会签人表单权限', value: constDefault },
  { label: '只读权限', value: constReadonly },
  { label: '由会签人选择', value: constCustom }
];
export const counterSignOperateRight = [
  { label: '同会签人操作权限', value: constDefault },
  { label: '不可再次会签', value: constNone }
];

export const addSignViewFormMode = [
  { label: '同加签人表单权限', value: constDefault },
  { label: '只读权限', value: constReadonly },
  { label: '由加签人选择', value: constCustom }
];
export const addSignOperateRight = [
  { label: '同加签人操作权限', value: constDefault },
  { label: '不可再次加签', value: constNone }
];
export const swimlaneLayoutOptions = [
  { label: '纵向', value: 'column' },
  { label: '横向', value: 'row' }
];

export const directionOptions = [
  { label: '直线', value: 'normal' },
  { label: '折线', value: 'curve' }
];

export const subflowDefTitleExpression = '${流程名称}_${子流程实例办理人}';

export const availableBizOrgOptions = [
  { label: '不可用', value: 'none' },
  { label: '全部业务组织', value: 'all' },
  { label: '指定业务组织', value: 'assign' }
];

export const userSelectType = [
  {
    label: '行政组织',
    value: 1,
    type: 'unit',
    icon: 'icon-ptkj-zuzhiguanli'
  },
  {
    label: '业务组织',
    value: 32,
    type: 'bizOrg',
    icon: 'icon-ptkj-zuzhijiagoufenjishitu'
  },
  { label: '表单字段', value: 2, type: 'field', icon: 'icon-ptkj-biaodanziduan' },
  { label: '办理环节', value: 4, type: 'task', icon: 'icon-ptkj-yewuliucheng' }, // icon-ptkj-liuchengguanli
  { label: '自定义', value: 16, type: 'custom', icon: 'icon-ptkj-qiehuanshitu' },
  { label: '人员过滤', value: 8, type: 'filter' }
];

// 业务组织人员选项指定角色
export const bizOrgAssignRole = [
  'RoleUserOfBizItemOfPriorUser',
  'RoleUserOfDeptOfPriorUser',
  'RoleUserOfParentDeptOfPriorUser',
  'RoleUserOfRootDeptOfPriorUser',
  'RoleUserOfRootNodeOfPriorUser',
  'RoleUserOfBizItemOfCreator',
  'RoleUserOfDeptOfCreator',
  'RoleUserOfParentDeptOfCreator',
  'RoleUserOfRootDeptOfCreator',
  'RoleUserOfRootNodeOfCreator'
];
// 业务组织人员过滤指定角色
export const bizOrgFilterAssignRole = [
  'SameRoleUserOfBizItemAsPrior',
  'SameRoleUserOfDeptAsPrior',
  'SameRoleUserOfParentDeptAsPrior',
  'SameRoleUserOfRootDeptAsPrior',
  'SameRoleUserOfRootNodeAsPrior',
  'SameRoleUserOfBizItemAsCreator',
  'SameRoleUserOfDeptAsCreator',
  'SameRoleUserOfParentDeptAsCreator',
  'SameRoleUserOfRootDeptAsCreator',
  'SameRoleUserOfRootNodeAsCreator'
];

// 业务组织人员选项指定角色-应用于“业务维度节点”的角色
export const bizOrgAssignRoleDimensionElement = [
  'RoleUserOfBizItemOfPriorUser',
  'RoleUserOfBizItemOfCreator',
  'SameRoleUserOfBizItemAsPrior', // 过滤-前办理人维度
  'SameRoleUserOfBizItemAsCreator'  // 过滤-申请人维度
]

export const bizOrgUserOptions = [
  {
    type: 'PriorUser',
    label: '前办理人维度',
    list: [
      {
        id: 'PriorUser',
        name: '前办理人',
        value: 'PriorUser'
      },
      {
        id: 'BizItemOfPriorUser',
        name: '前办理人的同业务维度节点人员',
        value: 'BizItemOfPriorUser'
      },
      {
        id: 'RoleUserOfBizItemOfPriorUser',
        name: '前办理人同业务维度节点的指定角色人员',
        value: 'RoleUserOfBizItemOfPriorUser'
      },
      {
        id: 'DeptOfPriorUser',
        name: '前办理人的同部门人员',
        value: 'DeptOfPriorUser'
      },
      {
        id: 'DeptAndBizRoleOfPriorUser',
        name: '前办理人的同部门同角色人员',
        value: 'DeptAndBizRoleOfPriorUser'
      },
      {
        id: 'RoleUserOfDeptOfPriorUser',
        name: '前办理人同部门的指定角色人员',
        value: 'RoleUserOfDeptOfPriorUser'
      },
      {
        id: 'ParentDeptOfPriorUser',
        name: '前办理人的上级部门人员',
        value: 'ParentDeptOfPriorUser'
      },
      {
        id: 'RoleUserOfParentDeptOfPriorUser',
        name: '前办理人上级部门的指定角色人员',
        value: 'RoleUserOfParentDeptOfPriorUser'
      },
      {
        id: 'RootDeptOfPriorUser',
        name: '前办理人的根部门人员',
        value: 'RootDeptOfPriorUser'
      },
      {
        id: 'RoleUserOfRootDeptOfPriorUser',
        name: '前办理人根部门的指定角色人员',
        value: 'RoleUserOfRootDeptOfPriorUser'
      },
      {
        id: 'RootNodeOfPriorUser',
        name: '前办理人的根节点人员',
        value: 'RootNodeOfPriorUser'
      },
      {
        id: 'RoleUserOfRootNodeOfPriorUser',
        name: '前办理人根节点的指定角色人员',
        value: 'RoleUserOfRootNodeOfPriorUser'
      },
      {
        id: 'BizRoleOfPriorUser',
        name: '前办理人的同角色人员',
        value: 'BizRoleOfPriorUser'
      }
    ]
  },
  {
    type: 'Creator',
    label: '申请人维度',
    list: [
      {
        id: 'Creator',
        name: '申请人',
        value: 'Creator'
      },
      {
        id: 'BizItemOfCreator',
        name: '申请人的同业务维度节点人员',
        value: 'BizItemOfCreator'
      },
      {
        id: 'RoleUserOfBizItemOfCreator',
        name: '申请人同业务维度节点的指定角色人员',
        value: 'RoleUserOfBizItemOfCreator'
      },
      {
        id: 'DeptOfCreator',
        name: '申请人的同部门人员',
        value: 'DeptOfCreator'
      },
      {
        id: 'DeptAndBizRoleOfCreator',
        name: '申请人的同部门同角色人员',
        value: 'DeptAndBizRoleOfCreator'
      },
      {
        id: 'RoleUserOfDeptOfCreator',
        name: '申请人同部门的指定角色人员',
        value: 'RoleUserOfDeptOfCreator'
      },
      {
        id: 'ParentDeptOfCreator',
        name: '申请人的上级部门人员',
        value: 'ParentDeptOfCreator'
      },
      {
        id: 'RoleUserOfParentDeptOfCreator',
        name: '申请人上级部门的指定角色人员',
        value: 'RoleUserOfParentDeptOfCreator'
      },
      {
        id: 'RootDeptOfCreator',
        name: '申请人的根部门人员',
        value: 'RootDeptOfCreator'
      },
      {
        id: 'RoleUserOfRootDeptOfCreator',
        name: '申请人根部门的指定角色人员',
        value: 'RoleUserOfRootDeptOfCreator'
      },
      {
        id: 'RootNodeOfCreator',
        name: '申请人的根节点人员',
        value: 'RootNodeOfCreator'
      },
      {
        id: 'RoleUserOfRootNodeOfCreator',
        name: '申请人根节点的指定角色人员',
        value: 'RoleUserOfRootNodeOfCreator'
      },
      {
        id: 'BizRoleOfCreator ',
        name: '申请人的同角色人员',
        value: 'BizRoleOfCreator '
      }
    ]
  }
];

export const bizOrgUserFilter = [
  {
    type: 'PriorUser',
    label: '前办理人维度',
    list: [
      {
        id: 'SameBizItemAsPrior',
        name: '限于前办理人的同业务维度节点人员',
        value: 'SameBizItemAsPrior'
      },
      {
        id: 'SameRoleUserOfBizItemAsPrior',
        name: '限于前办理人同业务维度节点的指定角色人员',
        value: 'SameRoleUserOfBizItemAsPrior'
      },
      {
        id: 'SameDeptAsPrior',
        name: '限于前办理人的同部门人员',
        value: 'SameDeptAsPrior'
      },
      {
        id: 'SameDeptAndBizRoleAsPrior',
        name: '限于前办理人的同部门同角色人员',
        value: 'SameDeptAndBizRoleAsPrior'
      },
      {
        id: 'SameRoleUserOfDeptAsPrior',
        name: '限于前办理人同部门的指定角色人员',
        value: 'SameRoleUserOfDeptAsPrior'
      },
      {
        id: 'SameParentDeptAsPrior',
        name: '限于前办理人的上级部门人员',
        value: 'SameParentDeptAsPrior'
      },
      {
        id: 'SameRoleUserOfParentDeptAsPrior',
        name: '限于前办理人上级部门的指定角色人员',
        value: 'SameRoleUserOfParentDeptAsPrior'
      },
      {
        id: 'SameRootDeptAsPrior',
        name: '限于前办理人的根部门人员',
        value: 'SameRootDeptAsPrior'
      },
      {
        id: 'SameRoleUserOfRootDeptAsPrior',
        name: '限于前办理人根部门的指定角色人员',
        value: 'SameRoleUserOfRootDeptAsPrior'
      },
      {
        id: 'SameRootNodeAsPrior',
        name: '限于前办理人的根节点人员',
        value: 'SameRootNodeAsPrior'
      },
      {
        id: 'SameRoleUserOfRootNodeAsPrior',
        name: '限于前办理人根节点的指定角色人员',
        value: 'SameRoleUserOfRootNodeAsPrior'
      },
      {
        id: 'SameBizRoleAsPrior',
        name: '限于前办理人的同角色人员',
        value: 'SameBizRoleAsPrior'
      }
    ]
  },
  {
    type: 'Creator',
    label: '申请人维度',
    list: [
      {
        id: 'SameBizItemAsCreator',
        name: '限于申请人的同业务维度节点人员',
        value: 'SameBizItemAsCreator'
      },
      {
        id: 'SameRoleUserOfBizItemAsCreator',
        name: '限于申请人同业务维度节点的指定角色人员',
        value: 'SameRoleUserOfBizItemAsCreator'
      },
      {
        id: 'SameDeptAsCreator',
        name: '限于申请人的同部门人员',
        value: 'SameDeptAsCreator'
      },
      {
        id: 'SameDeptAndBizRoleAsCreator',
        name: '限于申请人的同部门同角色人员',
        value: 'SameDeptAndBizRoleAsCreator'
      },
      {
        id: 'SameRoleUserOfDeptAsCreator',
        name: '限于申请人同部门的指定角色人员',
        value: 'SameRoleUserOfDeptAsCreator'
      },
      {
        id: 'SameParentDeptAsCreator',
        name: '限于申请人的上级部门人员',
        value: 'SameParentDeptAsCreator'
      },
      {
        id: 'SameRoleUserOfParentDeptAsCreator',
        name: '限于申请人上级部门的指定角色人员',
        value: 'SameRoleUserOfParentDeptAsCreator'
      },
      {
        id: 'SameRootDeptAsCreator',
        name: '限于申请人的根部门人员',
        value: 'SameRootDeptAsCreator'
      },
      {
        id: 'SameRoleUserOfRootDeptAsCreator',
        name: '限于申请人根部门的指定角色人员',
        value: 'SameRoleUserOfRootDeptAsCreator'
      },
      {
        id: 'SameRootNodeAsCreator',
        name: '限于申请人的根节点人员',
        value: 'SameRootNodeAsCreator'
      },
      {
        id: 'SameRoleUserOfRootNodeAsCreator',
        name: '限于申请人根节点的指定角色人员',
        value: 'SameRoleUserOfRootNodeAsCreator'
      },
      {
        id: 'SameBizRoleAsCreator',
        name: '限于申请人的同角色人员',
        value: 'SameBizRoleAsCreator'
      }
    ]
  }
];

export const orgGranularityOptions = [
  { label: '部门', value: 'dept', desc: '仅可选择部门分发子流程' },
  { label: '职位', value: 'job', desc: '可选择部门、职位分发子流程' },
  { label: '人员', value: 'user', desc: '可选择部门、职位、人员分发子流程' }
];
export const bizOrgGranularityOptions = [
  {
    label: '业务维度节点/部门',
    value: 'bizItem',
    desc: '可以业务维度节点/部门为单位分发子流程，子流程将自动分发至各部门指定业务角色下的成员'
  },
  { label: '业务角色', value: 'bizRole', desc: '可选业务维度节点/部门、业务角色分发子流程' },
  { label: '成员', value: 'member', desc: '可选业务维度节点/部门、业务角色或成员分发子流程' }
];

export const multiJobFlowTypeConfig = [
  {
    id: 'flow_by_user_all_jobs',
    text: '全部身份流转'
  },
  {
    id: 'flow_by_user_main_job',
    text: '以主身份流转'
  },
  {
    id: 'flow_by_user_select_job',
    text: '选择具体身份流转'
  }
];

export const notFoundMainIdentityOptions = [
  { label: '选择具体身份', value: 'flow_by_user_select_job' },
  { label: '以全部身份流转', value: 'flow_by_user_all_jobs' }
];
export const identitySelectOptions = [
  { label: '单选身份', value: 'single' },
  { label: '多选身份', value: 'multiple' }
];
export const datePatterns = [
  'YYYY-MM-DD HH:mm:ss',
  'YYYY-MM-DD HH:mm',
  'YYYY-MM-DD HH',
  'YYYY-MM-DD',
  'YYYY-MM',
  'YYYY',
  'YYYY年MM月DD日 HH时mm分ss秒',
  'YYYY年MM月DD日 HH时mm分',
  'YYYY年MM月DD日 HH时',
  'YYYY年MM月DD日',
  'YYYY年MM月',
  'YYYY年',
  'HH:mm:ss',
  'HH:mm',
  'HH时mm分ss秒',
  'HH时mm分'
];
