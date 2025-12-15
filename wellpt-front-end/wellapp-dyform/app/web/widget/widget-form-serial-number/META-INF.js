export default {
  wtype: 'WidgetFormSerialNumber',
  name: '流水号',
  iconClass: 'pticon iconfont icon-a-icjichuzujianliushuihao',
  scope: ['dyform', 'mobileDyform'],
  category: 'basicComponent',
  configuration: {
    code: '',
    length: 64,
    applyToDatas: [],
    dbDataType: null,
    inputMode: '29', // "7"可编辑流水号, "29"不可编辑流水号
    defaultDisplayState: 'edit',
    uneditableDisplayState: 'label',
    syncLabel2FormItem: true,
    style: {},
    generateMode: 'auto', // 流水号生成方式
    serialNumberDefinition: [], // 流水号定义
    serialNumberFill: 'none', // 不补号
    occupyMode: 'auto', // 占用设置，auto生成时占用，save保存时占用,
    uniConfiguration: {
      bordered: false
    }
  }
};
