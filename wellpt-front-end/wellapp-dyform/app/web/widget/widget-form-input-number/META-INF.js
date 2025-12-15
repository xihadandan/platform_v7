export default {
  wtype: 'WidgetFormInputNumber',
  name: '数字输入框',
  iconClass: 'pticon iconfont icon-a-icjichuzujianshuzishurukuang',
  scope: ['dyform', 'mobileDyform'],
  category: 'basicComponent',
  configuration: {
    type: 'input-number',
    code: '',
    length: 10,
    dataType: 'number',
    applyToDatas: [],
    dbDataType: '13', //默认：整数
    defaultDisplayState: 'edit',
    uneditableDisplayState: 'label',
    syncLabel2FormItem: true,
    stepBtnShow: true, //加减按钮：开启
    stepBtnType: 'LR', //按钮样式：LR左右，TB上下
    step: 1, //步长
    formatNumber: false, //千位分隔符
    style: {},
    uniConfiguration: { inputBorder: false },
    isCapital: false,
    capitalPosition: 'outside'
  }
};
