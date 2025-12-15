// webpack 查找组件目录下的 META.js 形成组件元数据在设计器中管理

export default {
  wtype: 'WidgetTree',
  name: '树形',
  iconClass: 'pticon iconfont icon-ptkj-zuzhijiagoufenjishitu',
  scope: ['page', 'dyform'],
  category: 'displayComponent',
  configuration: {
    buildType: 'define',
    showSearch: false,
    showIcon: false,
    treeNodes: [],
    jsModules: [],
    globalSetting: { operations: [], treeNodeTitleWidth: 150 },
    nodeClickEventHandler: {
      eventParams: []
    }
  }
};
