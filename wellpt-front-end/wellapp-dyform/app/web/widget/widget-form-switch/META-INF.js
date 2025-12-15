export default {
  wtype: 'WidgetFormSwitch',
  name: '开关',
  iconClass: 'pticon iconfont icon-a-icjichuzujiankaiguan',
  scope: ['dyform', 'mobileDyform'],
  category: 'basicComponent',
  configuration: {
    code: '',
    checkedValue: '1',
    uncheckedValue: '0',
    checkedLabel: '',
    uncheckedLabel: '',
    checkedIcon: null,
    uncheckedIcon: null,
    switchStyle: '',
    isLabelValueWidget: true, // 标记该组件是由显示值的属性特性的组件：提供从表控件进行显示值逻辑处理
    defaultDisplayState: 'edit',
    uneditableDisplayState: 'label',
    syncLabel2FormItem: true,
    length: 2,
    hasDefaultValue: true,
    valueCreateMethod: '4',
    defaultValue: '0', // 默认值
    // applyToDatas: '', // 字段映射
    description: '', // 描述,
    uniConfiguration: {
      switchStyle: ''
    } //移动端配置
  }
};
