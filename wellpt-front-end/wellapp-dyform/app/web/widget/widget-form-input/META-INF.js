const _ = [
  {
    wtype: 'WidgetFormInput',
    name: '输入框',
    iconClass: 'pticon iconfont icon-a-icjichuzujiandanhangwenbenkuang',
    scope: ['dyform', 'mobileDyform'],
    category: 'basicComponent',
    configuration: {
      type: 'input',
      code: '',
      length: 64,
      applyToDatas: [],
      dbDataType: null,
      defaultDisplayState: 'edit',
      uneditableDisplayState: 'label',
      syncLabel2FormItem: true,
      style: {
        textAlign: 'left'
      },
      clearBtnShow: true,
      contentFormat: 'text', //文本类型：默认普通文本
      wordCountPrompt: false, //显示字符数提示：默认关
      uniConfiguration: { inputBorder: false } //移动端配置
    }
  },
  {
    wtype: 'WidgetFormInput$Textarea',
    name: '多行文本',
    iconClass: 'pticon iconfont icon-a-icjichuzujianduohangwenbenkuang',
    scope: ['dyform', 'mobileDyform'],
    category: 'basicComponent',
    configuration: {
      type: 'textarea',
      code: '',
      length: 3000,
      applyToDatas: [],
      dbDataType: null,
      defaultDisplayState: 'edit',
      uneditableDisplayState: 'label',
      syncLabel2FormItem: true,
      wordCountPrompt: false, //显示字符数提示：默认关
      clearBtnShow: true,
      style: {
        textAlign: 'left'
      },
      uniConfiguration: {
        inputBorder: false
      } //移动端配置
    }
  }
];
export default _;
