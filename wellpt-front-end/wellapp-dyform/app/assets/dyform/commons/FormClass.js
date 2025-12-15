/**
 * 主表属性类
 */
var MainFormClass = function () {
  this.uuid = ''; // 1、 数据表单的唯一ID
  this.createTime = ''; // 1、 创建时间
  this.creator = ''; // 创建者
  this.modifyTime = ''; // 1、 最后更新时间
  this.recVer = ''; //
  this.modifier = ''; // 修改人
  // this.applyTo = ""; // 应用于
  this.relationTbl = ''; // 与数据表对应的数据关系表
  this.formType = ''; // 单据类型P:存储单据, V:展现单据, M: 手机单据,T:模板单据
  // 参照枚举类DyformTypeEnum
  this.pFormUuid = ''; // 如果为展现单据，则该字段的值为该单据对应的存储单据

  // 表单属性id
  // 该字段也是该记录的唯一标识,
  // 和表名一样的性质，所以通常和表名取相同的名字，
  // 但是ID还有另一个功能，就是ID是暴露出去给系统外的其他应用使用，
  // 所以这时就不能使用直接使用表名作为ID
  this.id = '';
  //表单事件
  this.events = [];

  this.tableName = ''; // 表名//该字段也是该表的唯一标识

  this.name = ''; // 显示名称
  this.remark = ''; // 描述
  // this.formDisplay = ""; // 表单显示形式 ： 两种 一种是可编辑展示、 一种是直接展示文本
  // this.moduleId = ""; // 模块ID
  // this.moduleName = ""; // 模块名
  // this.printTemplateId = ""; // 打印模板的ID
  // this.printTemplateName = ""; // 打印模板的名称
  // this.displayFormModelName = ""; // 显示单据的名称
  // this.displayFormModelId = ""; // 显示单据对应的表单uuid
  this.code = ''; // 表单编号
  this.version = '1.0'; // 版本 ,形式：1.0
  this.enableSignature = ''; // 是否启用表单签名
  // this.html = "";
  this.fields = {}; // 字段定义
  this.subforms = {};
  this.formTree = [];
  // this.customJs = "";// 自定义JS//这个已放弃，不再使用。
  // this.customJsNew = "";
  this.systemUnitId = ''; // 表单的归属系统ID
};

/**
 * 主表字段属性类
 */
var MainFormFieldClass = function () {
  this.applyTo = '';
  this.name = ''; // 最新的名字
  this.displayName = '';
  this.oldName = ''; // 旧的字段名
  // this.isFromPform = false;// 用于判断字段字义是否来自存储单据

  // 数据库里面对应的字段类型
  // text:'1',//文本输入框
  // date:'2',//日期选择，只允许日期控件丢或者手动输入合法的日期
  // dateTimeHour:'6',//日期到时
  // dateTimeMin:'7',//日期到分
  // dateTimeSec:'8',//日期到秒
  // timeHour:'9',//时间到时
  // timeMin:'10',//时间到分
  // timeSec:'11',//时间到秒
  // int:'13',//文本框整数输入
  // long:'14',//长整型输入
  // float:'15',//浮点数输入
  // clob:'16',//大字段
  this.dbDataType = dyFormDataType._string;

  this.indexed = ''; // 该列要不要索引
  this.showed = ''; // ???是否界面表格显示
  this.sorted = ''; // ???是否排序
  this.sysType = dyFieldSysType.custom; // ???系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
  this.length = ''; // 字段长度

  // 1：可编辑;
  // 2：直接以文的形式显示;
  // 3：有输入框但只读;
  // 4:有输入框但被disabled(这时html中的form是没办法收集到该元素的值)。
  // 5:隐藏行
  this.showType = dyshowType.edit;

  this.defaultValue = ''; // 默认值

  // 表示值是通过什么方式产生的
  // 1:用户输入
  // 2:JS公式
  // 3:创建时计算
  // 4:显示时计算
  // 5:二维码
  // 6:条形码
  // 7:关联文档
  this.valueCreateMethod = dyFormInputValue.userImport;

  // 只读状态下设置跳转的url
  // (在字段为只读的状态下，可为该字段的文本值设置超级链接,例如该字段的//值为某人的姓名，那么通过点击姓名跳转到该人的简历页面)
  this.onlyreadUrl = '';

  this.inputMode = dyFormInputMode.text; // 控件类型
  this.textAlign = 'left';
  this.ctlWidth = '';
  this.ctlHight = '';
  this.fontSize = '12';
  // this.fontColor = 'black';
  this.fieldCheckRules = []; // 检验规则,该字段的值为JSON如[{"value":"2","label":"必须是URL","rule":""},{"value":"3","label":"必须是邮箱地址","rule":""}]

  this.realDisplay = {
    real: '',
    display: ''
  }; // 真实值与显示值的字段名，一个JSON对象,key为真实值的字段名，value为显示值的字段名
  /*
   * 私有属性挪到控件各自对应的class类中. this.relationDataText = ""; this.relationDataValue =
   * ""; this.relationDataSql = ""; this.relationDataTextTwo = "";
   * this.relationDataValueTwo = ""; this.relationDataTwoSql = "";
   * this.relationDataDefiantion = ""; this.relationDataShowMethod = "";
   * this.relationDataShowType = "";
   *
   *
   *
   * this.designatedId = ""; this.designatedType = ""; this.isOverride = "";
   * this.isSaveDb = ""; this.decimal = 2; //浮点数的计算结果保留位数 this.ctrlField = "";
   * //checkbox的属性 this.optionDataSource = "1"; //备选项来源1:常量,2:字典 //字典代码
   * this.dictCode = ""; //Select,radio, checkbox选项 //以map的形式保存key为值，value为备注
   * Checkbox/radio/select都有option,在optionDataSource属性为1的情况下,option的值则来源于该字段.
   * 该字段的值以map的形式保存key为值，value为备注 this.optionSet=[];
   *
   *
   *
   * this.serviceName = ""; this.methodName = "";
   */
  // 对于文本框来说
  // 内容格式
  // 0:普通文本//默认值
  // 1:URL
  // 2:EMAIL
  // 3:身份证
  // 4:固定电话
  // 5:手机号
  // 对于日期来说
  // yearMonthDate:'1',//当前日期(2000-01-01)
  // yearMonthDateCn:'2',//当前日期(2000年1月1日)
  // yearCn:'3',//当前日期(2000年)
  // yearMonthCn:'4',//当前日期(2000年1月)
  // /monthDateCn:'5',//当前日期(1月1日)
  // weekCn:'6',//当前日期(星期一)
  // year:'7',//当前年份(2000)
  // timeHour:'8',//当前时间(12)
  // timeMin:'9',//当前时间(12:00)
  // TimeSec:'10',//当前时间(12:00:00)
  // dateTimeHour:'11',//日期到时 当前日期时间(2000-01-01 12)
  // dateTimeMin:'12',//日期到分 当前日期时间(2000-01-01 12:00)
  // dateTimeSec:'13',//日期到秒 当前日期时间(2000-01-01 12:00:00)
  // this.contentFormat = "0";
  this.keepOpLog = keepOpLogType.no; // 给附件控件使用,是否痕迹保留,0表示不保留,1 表示保留
};

/**
 * 从表属性类
 */
var SubFormClass = function () {
  this.formUuid = '';
  this.outerId = '';
  this.name = '';
  this.displayName = '';

  this.isGroupShowTitle = ''; // 是否要分组展示
  this.groupShowTitle = ''; // 分组展示标题
  this.isGroupColumnShow = ''; // 分组字段是否展示 20140701 add

  // this.isFromPform = false;// 用于判断字段字义是否来自存储单据

  this.subformApplyTableId = '';
  this.subrRelationDataDefiantion = '';
  this.tableOpen = ''; // 从表是展示还是折叠（收缩）

  // 编辑模式1.行内编辑 2.弹出窗口编辑
  this.editMode = '';

  this.hideButtons = ''; // 1：不隐藏;2:隐藏
  this.fields = {}; // 参照表单从表字段定义
  this.tableButtonInfo = {}; // 从表按钮事件字段定义
};

/**
 * 从表按钮事件属性类
 */
var TableButtonClass = function () {
  this.text = '';
  this.code = '';
  this.uuid = '';
  this.buttonEvents = {};
  this.displayText = '';
};

/**
 * 从表字段属性类
 */
var SubFormFieldClass = function () {
  this.name = '';
  this.displayName = '';
  this.order = '0'; // 在展示从表时各字段的排列顺序
  this.sortable = false; // 在展示从表时是否允许列排序
  this.srcFieldName = ''; // 来源字段
  this.hidden = dySubFormFieldShow.show; // 从表字段是否展示
  this.width = '';
  this.editable = dySubFormFieldEdit.edit;
  this.controlable = dySubFormFieldCtl.label; // 在从表中的控件在光标移开之后仍然展示为控件，不展示为标签,在editable的属性为edit的前提下该属性才有效
  this.formula = ''; // 运算公式
};

/**
 * 布局
 */
var LayOut = function () {
  this.name = ''; // 布局唯一标识
  this.inputMode = ''; // 布局类型
  this.displayName = ''; // 布局标题
  // this.isActive = false;//是否激活,对页签而言即是否激活,对于区块而言就是是否展开.
  this.isHide = false; // 是否隐藏
};
