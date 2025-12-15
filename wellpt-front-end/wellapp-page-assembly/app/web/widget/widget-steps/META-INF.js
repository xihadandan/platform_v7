export default {
  wtype: 'WidgetSteps',
  name: '步骤条',
  iconClass: 'pticon iconfont icon-ptkj-zhedie',
  scope: ['page', 'dyform'],
  category: 'advanceContainer',
  configuration: {
    jsModules: [],
    type: 'default',
    direction: 'horizontal',
    labelPlacement: 'horizontal',
    size: 'default',
    asWindow: false,
    steps: [],
    options: {
      type: 'selfDefine',
      dataSourceId: '', // 数据仓库
      dataSourceLabelColumn: '', // 数据仓库-展示字段
      dataSourceValueColumn: '', // 数据仓库-值字段
      dataSourceDescColumn: '', // 数据仓库-描述字段
      dataSourceStatusColumn: '', // 数据仓库-状态字段
      dataSourceSubTileColumn: '', // 数据仓库-子标题
      dataSourceStatusConditions: [
        { operator: '==', value: 'wait', status: 'wait' }
      ], // 状态条件
      defaultCondition: '', // 数据仓库-默认过滤条件 sql
      dataSourcePageId: '', // 页面id
      pageType: 'page',
      dataSourceRenderUrl: ''
    },
    dataModel: {
      uuid: '',
      labelColumn: '',
      valueColumn: '',
      descColumn: '',
      statusColumn: '',
      subTileColumn: '',
      statusConditions: [
        { operator: '==', value: 'wait', status: 'wait' }
      ],
      defaultCondition: '',
      pageId: '',
      pageType: 'page',
      renderUrl: ''
    }
  }
};
