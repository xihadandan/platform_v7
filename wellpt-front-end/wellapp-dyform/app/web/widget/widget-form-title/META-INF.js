export default {
  wtype: 'WidgetFormTitle',
  iconClass: 'pticon iconfont icon-a-icjichuzujianbiaodanbiaoti',
  name: '表单标题',
  scope: ['dyform'],
  category: 'basicComponent',
  configuration: {
    dividerShow: false,
    dividerStyle: {},
    syncLabel2FormItem: true,
    defaultDisplayState: 'edit',
    uneditableDisplayState: 'label',
    isDatabaseField: false,
    style: {
      align: 'center',
      fontSize: 32
    },
    title: {
      value: '标题',
      variables: [{ edit: false, label: '标题', value: '标题' }]
    },
    subtitle: {
      value: '副标题',
      variables: [{ edit: false, label: '副标题', value: '副标题' }]
    },
    subtitleShow: false,
    subtitleStyle: {
      align: 'center',
      fontSize: 14
    }
  }
};
