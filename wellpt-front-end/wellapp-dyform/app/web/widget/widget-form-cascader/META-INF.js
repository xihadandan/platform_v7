export default {
  wtype: 'WidgetFormCascader',
  iconClass: 'pticon iconfont icon-a-icjichuzujianjilianxuanze',
  name: '级联选择框',
  scope: ['dyform', 'mobileDyform'],
  category: 'basicComponent',
  configuration: {
    code: '',
    name: '',
    length: 120,
    isLabelValueWidget: true,
    isDatabaseField: true,
    validateRule: { trigger: 'change', regExp: {} },
    uneditableDisplayState: 'label',
    displayValueField: undefined,
    textWidgetId: undefined, //  真实值 只能映射到文本组件 WidgetFormTexts
    tokenSeparators: '/',
    placeholder: '',
    syncLabel2FormItem: true,
    optionDataAutoSet: false,
    datalevel: undefined,
    options: {
      type: 'selfDefine',
      dataProviderId: undefined, // 数据服务
      dataDictionaryUuid: undefined, // 数据字典
      dataDictValueColumn: undefined, // 数据字典-值字段
      dataSourceId: '', // 数据仓库
      dataSourceLabelColumn: '', // 数据仓库-展示字段
      dataSourceValueColumn: '', // 数据仓库-值字段
      dataSourceKeyColumn: '', // 数据仓库-唯一值字段
      dataSourceParentColumn: '', // 数据仓库-父级字段
      dataSourceExtendColumn: '', // 数据仓库-扩展字段 解析端上显示在label 右侧
      defaultCondition: '', // 数据仓库-默认过滤条件 sql
      dataSourceLoadEveryTime: false, // 数据仓库-每次点击加载数据 解析端点击v-model时
      dataSourceDataMapping: [], // 字段映射,
      sortField: '',
      sortType: 'asc'
    },
    treeData: [], // 常量备选项
    defaultDisplayState: 'edit',
    editMode: {
      loadAsync: false,
      allPath: false, // 显示值全路径
      valueAllPath: false, // 真实值全路径
      allowClear: true,
      showSearch: true,
      changeOnSelect: true,
      expandTrigger: 'click',
      gradualLoad: false, // 数据字典-逐级加载
    },
    relateKey: '',
    relateKeyLabel: '',
    relateList: [],
    style: {},
    popupClassName: '',
    uniConfiguration: {
      bordered: false
    }
  }
};
