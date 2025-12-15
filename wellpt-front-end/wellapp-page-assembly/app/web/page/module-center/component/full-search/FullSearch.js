export const viewDataFormSourceOptions = [
  { label: '数据创建表单', value: 'auto' },
  { label: '指定表单', value: 'custom' }
];

export const matchOptions = [
  { label: '满足全部条件', value: 'all' },
  { label: '满足任一条件', value: 'any' }
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

export const createModel = ({ categoryUuid, dataModelUuid }) => {
  return {
    categoryUuid,
    dataModelUuid,
    matchJson: '',
    viewDataFormSource: 'auto',
    viewDataFormUuid: undefined
  };
};
