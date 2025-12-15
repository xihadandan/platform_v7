/* 
{
  fieldCount: 3, // 自动获取数据源的前n个字段作为索引摘要
  excludeFields: ['cellphoneNumber', 'telephoneNumber', 'identity', 'email', 'password', 'clob'], // 获取字段时排除以下字段
  builtIn: {// 平台内置
    workflow: {// 流程数据
       categoryName: '数据分类名称',
       titleExpression: '索引标题',
       contentExpression: '索引摘要'
    }
  },
  updateMode: 'realtime实时，定时regular',
  regularTimePoint: '定时时间点',
  rebuildRules: [{
      repeatType: '重复周期，none不重复、repeat重复',
      repeatInterval: '重复间隔数',
      repeatUnit: '重复间隔单位，day天、week周、month月、year年',
      repeatDaysOfWeek: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']// 周一～日 
      repeatDayOfMonth: 'first第一天、fifteen第十五天、last最后一天',
      repeatMonthOfYear: '1~12月'
      timePoint: '执行时间点',
      executeTime: '不重复的执行时间',
      state: '1已启用、2已停用、3已完成'
}]// 索引重建任务
}
*/

class SearchSetting {
  constructor({ fieldCount = 3 } = {}) {
    this.fieldCount = fieldCount;
    this.excludeFields = ['cellphoneNumber', 'telephoneNumber', 'identity', 'email', 'password', 'clob'];
    this.indexAttachment = false
    this.builtIn = {
      workflow: {
        categoryName: '流程',
        titleExpression: '${流程标题}${流程名称}${发起人姓名}',
        contentExpression: '${流程标题}${流程名称}${流程ID}${流程编号}${发起人姓名}${发起人所在部门名称}',
        indexAttachment: false
      },
      dms_file: {
        categoryName: '文档',
        indexAttachment: true
      }
    };
    this.updateMode = 'realtime';
    this.regularTimePoint = '03:00';
    this.rebuildRules = [];
  }
}

export default SearchSetting;

export const fieldCountOptions = [
  { label: '1', value: 1 },
  { label: '2', value: 2 },
  { label: '3', value: 3 },
  { label: '4', value: 4 },
  { label: '5', value: 5 },
  { label: '6', value: 6 }
];

export const excludeFieldsOptions = [
  { label: '手机号码', value: 'cellphoneNumber' },
  { label: '电话', value: 'telephoneNumber' },
  { label: '身份证', value: 'identity' },
  { label: '邮箱', value: 'email' },
  { label: '密码', value: 'password' },
  { label: '大文本字段', value: 'clob' }
];

export const updateModeOptions = [
  { label: '实时', value: 'realtime' },
  { label: '定时', value: 'regular' }
];

export const createRebuildRule = () => {
  return {
    repeatType: 'none',
    repeatInterval: '1',
    repeatUnit: 'day',
    repeatDaysOfWeek: [], // 周一～日
    repeatDayOfMonth: 'first',
    repeatMonthOfYear: '1月',
    timePoint: null,
    executeTime: null,
    state: '2'
  };
};

export const repeatTypeOptions = [
  { label: '不重复', value: 'none' },
  { label: '重复', value: 'repeat' }
];

export const repeatUnitOptions = [
  { label: '天', value: 'day' },
  { label: '周', value: 'week' },
  { label: '月', value: 'month' },
  { label: '年', value: 'year' }
];

export const repeatDaysOfWeekOptions = [
  { label: '周一', value: 'Mon' },
  { label: '周二', value: 'Tue' },
  { label: '周三', value: 'Wed' },
  { label: '周四', value: 'Thu' },
  { label: '周五', value: 'Fri' },
  { label: '周六', value: 'Sat' },
  { label: '周日', value: 'Sun' }
];

export const repeatDayOfMonthOptions = [
  { label: '第一天', value: 'first' },
  { label: '第十五天', value: 'fifteen' },
  { label: '最后一天', value: 'last' }
];

let repeatMonthOfYearMap = {};
let repeatMonthOfYearOptions = [];
for (let index = 1; index <= 12; index++) {
  repeatMonthOfYearOptions.push({
    label: `${index}月`,
    value: `${index}月`
  });
  repeatMonthOfYearMap[`${index}月`] = `${index}月`;
}
export { repeatMonthOfYearOptions };

export const statOptions = [
  { label: '已启用', value: '1', color: 'green' },
  { label: '已停用', value: '2', color: 'red' },
  { label: '已完成', value: '3' }
];

let repeatTypeMap = {};
repeatTypeOptions.forEach(item => {
  repeatTypeMap[item.value] = item;
});

let repeatUnitMap = {};
repeatUnitOptions.forEach(item => {
  repeatUnitMap[item.value] = item;
});

let repeatDaysOfWeekMap = {};
repeatDaysOfWeekOptions.forEach(item => {
  repeatDaysOfWeekMap[item.value] = item;
});

let repeatDayOfMonthMap = {};
repeatDayOfMonthOptions.forEach(item => {
  repeatDayOfMonthMap[item.value] = item;
});

let stateMap = {};
statOptions.forEach(item => {
  stateMap[item.value] = item;
});
export { repeatTypeMap, repeatUnitMap, repeatDaysOfWeekMap, repeatDayOfMonthMap, repeatMonthOfYearMap, stateMap };
