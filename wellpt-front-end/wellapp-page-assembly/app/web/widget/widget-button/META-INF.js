export default {
  wtype: 'WidgetButton',
  name: '按钮',
  iconClass: 'pticon iconfont icon-a-icjichuzujiananniu',
  scope: ['page', 'dyform', 'mobileDyform', 'mobilePage'],
  category: 'basicComponent',
  configuration: {
    title: '按钮',
    type: 'default',
    size: 'default',
    block: false,
    textHidden: false,
    align: 'left',
    icon: null,
    suffixIcon: null,
    groupType: null,
    buttonSpace: 8,
    enableSpace: false,
    group: [],
    eventHandler: { eventParams: [] },
    switch: {
      defaultChecked: true,
      checkedText: undefined,
      UnCheckedText: undefined
    }
  }
};
