export default {
  wtype: 'WidgetFormColor',
  name: '颜色选择',
  iconClass: 'pticon iconfont icon-a-icjichuzujianyanse',
  scope: ['dyform', 'mobileDyform'],
  category: 'basicComponent',
  configuration: {
    code: '',
    isLabelValueWidget: true, // 标记该组件是由显示值的属性特性的组件：提供从表控件进行显示值逻辑处理
    defaultDisplayState: 'edit',
    uneditableDisplayState: 'label',
    syncLabel2FormItem: true,
    showType: '1', // 显示类型 1,2,3,4 datashow
    length: 64,
    size: 'default',
    valueCreateMethod: '4', // 显示时计算
    hasDefaultValue: false,
    defaultValue: null, // 默认值
    description: '', // 描述
    picker: 'Twitter',
    pickerChange: false,
    defaultColorType: 'define', //预设颜色类型
    defaultColors: ['#1677FF', '#8ABBFF', '#74C042', '#BADFA0', '#FA8C16', '#FDC58A', '#E60012', '#F38088', '#BE41D8', '#ABB8C3'],
    showText: false,
    uniConfiguration: { inputBorder: false } //移动端配置
  }
};
