export const columnSearchOperatorOptions = [
  { label: '小于', value: 'lt' },
  { label: '小于等于', value: 'le' },
  { label: '大于', value: 'gt' },
  { label: '大于等于', value: 'ge' },
  { label: '等于', value: 'eq' },
  { label: '匹配 (like)', value: 'like' },
  { label: '不匹配 (not like)', value: 'nlike' },
  { label: '在集合中 (in)', value: 'in' },
  { label: '区间', value: 'between' },
  { label: '是空', value: 'is null' },
  { label: '非空', value: 'is not null' },
  { label: '存在关联数据', value: 'exists' }
];

export const searchInputTypeOptions = [
  { label: '文本框', value: 'input' },
  { label: '日期框', value: 'datePicker' },
  { label: '组织弹出框', value: 'organization' },
  { label: '下拉框', value: 'select' },
  { label: '分组下拉框', value: 'groupSelect' },
  { label: '单选框', value: 'radio' },
  { label: '复选框', value: 'checkbox' },
  { label: '下拉树形', value: 'treeSelect' }
];

export const buttonTypeOptions = [
  { label: '主按钮', value: 'primary' },
  { label: '次按钮', value: 'default' },
  { label: '链接按钮', value: 'link' },
  { label: '危险按钮', value: 'danger' },
  { label: '虚线按钮', value: 'dashed' },
  { label: '开关按钮', value: 'switch' }
];
export const buttonShapeOptions = [
  { label: '圆形', value: 'circle' },
  { label: '椭圆形', value: 'round' }
];

export const buttonSizeOptions = [
  { value: 'default', label: '默认' },
  { value: 'small', label: '小' },
  { value: 'large', label: '大' }
];

export const operatorOptions = [
  { label: '等于', value: '==' },
  { label: '不等于', value: '!=' },
  { label: '为真', value: 'true' },
  { label: '为假', value: 'false' },
  { label: '大于', value: '>' },
  { label: '大于等于', value: '>=' },
  { label: '小于', value: '<' },
  { label: '小于等于', value: '<=' },
  { label: '包含于', value: 'in' },
  { label: '不包含于', value: 'not in' },
  { label: '包含', value: 'contain' },
  { label: '不包含', value: 'not contain' }
];

export const optionSourceTypes = [
  { label: '常量', value: 'selfDefine' },
  { label: '数据字典', value: 'dataDictionary' },
  { label: '数据仓库', value: 'dataSource' },
  { label: '数据模型', value: 'dataModel' },
  { label: 'API 服务', value: 'apiLinkService' }
];

export const layoutOptions = [
  { label: '水平', value: 'horizontal' },
  { label: '垂直', value: 'vertical' }
];

export const userVariableOptions = [
  { label: '用户名', value: '_USER_.userName' },
  { label: '用户ID', value: '_USER_.userId' },
  { label: '登录账号', value: '_USER_.loginName' },
  { label: '用户包含角色ID集合', value: '_USER_.roles' },
  { label: '用户主部门ID', value: '_USER_.mainDeptId' },
  { label: '用户主部门ID路径', value: '_USER_.mainDeptIdPath' },
  { label: '用户主部门名称', value: '_USER_.mainDeptName' },
  { label: '用户主部门名称路径', value: '_USER_.mainDeptNamePath' },
  { label: '用户主职位ID', value: '_USER_.mainJobId' },
  { label: '用户主职位ID路径', value: '_USER_.mainJobIdPath' },
  { label: '用户主职位名称', value: '_USER_.mainJobName' },
  { label: '用户主职位名称路径', value: '_USER_.mainJobNamePath' },
  { label: '用户单位ID', value: '_USER_.unitId' },
  { label: '用户单位名称', value: '_USER_.unitName' },
  { label: '用户职位ID集合', value: '_USER_.jobIds' },
  { label: '用户职位名称集合', value: '_USER_.jobNames' },
  { label: '用户部门ID集合', value: '_USER_.deptIds' },
  { label: '用户部门名称集合', value: '_USER_.deptNames' },
  { label: '用户系统ID', value: '_USER_.systemId' }
];

export const timeDataVariableOptions = [
  { label: `当前日期数值(${new Date().format('YYYYMMDD')})`, value: '_DATETIME_.currentDateString' },
  { label: `当前日期时间数值(${new Date().format('YYYYMMDDHHmmss')})`, value: '_DATETIME_.currentFullDateTimeString' },
  { label: '当前星期几(1~7)', value: '_DATETIME_.currentWeekDay' },
  { label: '当前月份(1~12)', value: '_DATETIME_.currentMonth' },
  { label: '当前季度(1~4)', value: '_DATETIME_.currentQuarter' },
  { label: '当前年份', value: '_DATETIME_.currentYear' },
  { label: '当前日(1~31)', value: '_DATETIME_.currentDay' },
  { label: '当前小时(0~23)', value: '_DATETIME_.currentHour' }
];
export const flowDataVariableOptions = [
  { label: '当前环节ID', value: '_WORKFLOW_.taskId' },
  { label: '当前环节名称', value: '_WORKFLOW_.taskName' },
  { label: '当前流程ID', value: '_WORKFLOW_.flowDefId' },
  { label: '当前流程版本号', value: '_WORKFLOW_.version' }
];
