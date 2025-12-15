export default {
  wtype: 'WidgetFormSetIcon',
  name: '图标选择',
  iconClass: 'ant-iconfont smile',
  scope: ['dyform'], //, 'mobileDyform'
  category: 'basicComponent',
  configuration: {
    code: '',
    isLabelValueWidget: true, // 标记该组件是由显示值的属性特性的组件：提供从表控件进行显示值逻辑处理
    defaultDisplayState: 'edit',
    uneditableDisplayState: 'label',
    syncLabel2FormItem: true,
    length: 300,
    size: 'default',
    valueCreateMethod: '4', // 显示时计算
    hasDefaultValue: false,
    defaultValue: null, // 默认值
    onlyIconClass: false,
    allowClear: true
  }
};
