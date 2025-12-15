const _ = [
  {
    wtype: 'WidgetFormSelect',
    name: '下拉框',
    iconClass: 'pticon iconfont icon-a-icjichuzujianxialakuang',
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
      type: 'select',
      tokenSeparators: ';',
      placeholder: '',
      syncLabel2FormItem: true,
      optionDataAutoSet: false,
      options: {
        type: 'selfDefine',
        dataSourceLabelColumn: '',
        dataSourceValueColumn: '',
        dataSourceExtendColumn: '', // 数据仓库-扩展字段 解析端上显示在label 右侧
        dataSourceId: '',
        defineOptions: [], // 常量备选项
        dataDictionaryUuid: '',
        defaultCondition: '',
        dataSourceLoadEveryTime: false,
        dataSourceDataMapping: [],
        groupOptions: [],
        groupColumn: ''
      },
      defaultDisplayState: 'edit',
      editMode: {
        selectCheckAll: false,
        selectMultiple: false,
        selectMode: 'default',
        allowClear: true,
        showSearch: true,
        showMore: false,
        optionAdd: true,
        optionSort: true,
        optionDel: true,
        textWidgetId: undefined, //  真实值 只能映射到文本组件 WidgetFormTexts
        optionAddFetchUrl: '',
        optionSortFetchUrl: '',
        optionDelFetchUrl: ''
      },
      relateKey: '',
      relateKeyLabel: '',
      relateList: [],
      style: {},
      uniConfiguration: {
        dropType: 'picker',
        bordered: false
      }
    }
  },
  {
    wtype: 'WidgetFormSelect$SelectTree',
    name: '树形下拉框',
    iconClass: 'pticon iconfont icon-a-icjichuzujianshuxingxialakuang',
    scope: ['dyform', 'mobileDyform'],
    category: 'basicComponent',
    configuration: {
      code: '', // 字段编码
      name: '', // 字段名称
      length: 120,
      isLabelValueWidget: true, // 标记该组件是由显示值的属性特性的组件：提供从表控件进行显示值逻辑处理
      isDatabaseField: true, // 默认为数据库持久化字段
      displayValueField: undefined, // 显示值字段 只能映射input组件  WidgetFormInputs
      type: 'select-tree', // selct selectGroup selectTree
      tokenSeparators: ';', // 多选分隔符
      placeholder: '',
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
      optionDataAutoSet: false, // 联动设置
      relateKey: '', // 联动字段
      relateKeyLabel: '',
      relateList: [], // 联动条件列表
      defaultDisplayState: 'edit', //默认状态  edit可编辑 unedit不可编辑 hidden隐藏
      editMode: {
        // 编辑模式属性
        loadAsync: false,
        allPath: false,
        selectCheckAll: true,
        selectMultiple: true,
        selectMode: 'multiple', // default multiple
        allowClear: true,
        showSearch: true,
        selectParent: true,
        allCollapse: true,
        showMore: true,
        optionAdd: true,
        optionSort: true,
        optionDel: true,
        minCount: '',
        maxCount: ''
      },
      uneditableDisplayState: 'label', // 不可编辑模式属性 label纯文本  readonly只读
      validateRule: { trigger: 'change', regExp: {} }, // 校验规则
      uniConfiguration: {
        dropType: 'picker',
        bordered: false
      }
    }
  }
];
export default _;
