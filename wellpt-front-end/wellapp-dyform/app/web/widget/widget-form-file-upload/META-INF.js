// webpack 查找组件目录下的 META.js 形成组件元数据在设计器中管理

export default {
  wtype: 'WidgetFormFileUpload',
  name: '文件上传',
  iconClass: 'pticon iconfont icon-a-icjichuzujianwenjianshangchuan',
  scope: ['dyform', 'mobileDyform'],
  category: 'basicComponent', // 通过分类把组件归类到设计器内
  configuration: {
    code: '',
    name: '',
    accept: [],
    length: 1,
    inputMode: '6',
    fileLimitNum: null, // 附件个数限制
    fileSizeLimit: null, // 附件大小限制
    fileSizeLimitUnit: 'MB', // 附件大小限制单位
    type: 'simpleList', // picture : 图片 / simpleList: 简易列表上传 / advancedList: 高级列表上传
    advancedFileListType: 'listView', // listView: 列表视图 tableView: 表格视图 iconView: 图标视图
    isLabelValueWidget: true, // 标记该组件是由显示值的属性特性的组件：提供从表控件进行显示值逻辑处理
    defaultDisplayState: 'edit',
    uneditableDisplayState: 'label',
    syncLabel2FormItem: true,
    downloadAllType: '1', // 下载文件的类型
    isShowFileFormatIcon: false, // 是否显示文件格式图标
    fileSourceIds: [], // 文件来源
    // contentEdit: false, // 是否可配置操作选项
    // displayClassConfig: [], // 配置项: 显示类
    // editClassConfig: [], // 配置项: 编辑类
    advancedViewList: ['listView'],
    fileNormalOrText: 'normal',
    fileNameRepeat: true, // 文件名重复
    operateBtnIds: [], // 上传操作按钮
    headerButton: [], //表头按钮
    rowButton: [], //行按钮
    relatedMaterial: {
      // 关联材料配置
      enabled: false, // 是否关联材料
      way: '1', // 材料关联方式，1指定材料，2指定材料编码字段
      materialCodes: [], // 指定材料时选择的材料编码
      materialCodeFields: [], // 指定材料编码字段时选择的字段
      ownerType: '1', // 材料数据所有者，1当前创建人，2指定所有者字段
      ownerFields: [] // 材料所有者字段
    },
    pictureUploadMode: 'local', // 图片上传方式
    pictureLib: [],
    uniConfiguration: {
      fileNormalOrText: 'normal'
    }
  }
};
