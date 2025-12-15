export default {
  wtype: 'WidgetFormTag',
  name: '标签组',
  iconClass: 'pticon iconfont icon-a-icjichuzujianbiaoqianzu',
  scope: ['dyform'],
  category: 'basicComponent',
  configuration: {
    code: '',
    length: 64,
    isDatabaseField: true,
    isLabelValueWidget: true, // 标记该组件是由显示值的属性特性的组件：提供从表控件进行显示值逻辑处理
    defaultDisplayState: 'edit',
    uneditableDisplayState: 'label',
    syncLabel2FormItem: true,
    tagEditMode: 'select', // 编辑方式 选择/输入
    optionDataAutoSet: false,
    options: {
      type: 'selfDefine',
      defineOptions: [], // 常量备选项
      dataDictionaryUuid: '', // 数据字典
      dataSourceId: '', // 数据仓库
      defaultCondition: '', // 默认过滤条件
      dataSourceLabelColumn: '', // 显示值
      dataSourceValueColumn: '' // 真实值
    },
    editMode: {
      minCount: '',
      maxCount: ''
    },
    tokenSeparators: ';',
    displayValueField: undefined,
    selectCheckAll: false,
    minLength: '',
    maxLength: '',
    description: '', // 描述
    relateKey: '',
    relateKeyLabel: '',
    relateList: []
  }
};
