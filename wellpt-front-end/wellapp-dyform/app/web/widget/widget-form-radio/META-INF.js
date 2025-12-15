export default {
  wtype: 'WidgetFormRadio',
  name: '单选框',
  iconClass: 'pticon iconfont icon-ptkj-danxuan-xuanzhong',
  scope: ['dyform', 'mobileDyform'],
  category: 'basicComponent',
  configuration: {
    code: '',
    length: 64,
    layout: 'horizontal',
    styleType: 'radio',
    buttonSelectedStyle: 'solid',
    alignType: 'fixedSpace',
    itemWidth: 100,
    displayValueField: undefined,
    isLabelValueWidget: true, // 标记该组件是由显示值的属性特性的组件：提供从表控件进行显示值逻辑处理
    optionDataAutoSet: false,
    options: {
      type: 'selfDefine',
      dataSourceLabelColumn: '',
      dataSourceValueColumn: '',
      dataSourceId: '',
      defineOptions: [
        { id: '1', label: '选项1', value: '选项1' },
        { id: '2', label: '选项2', value: '选项2' }
      ],
      dataDictionaryUuid: ''
    },
    relateList: [], // 联动条件列表
    defaultDisplayState: 'edit',
    uneditableDisplayState: 'label',
    syncLabel2FormItem: true,
    style: {},
    uniConfiguration: {}
  }
};
