/**
 * 该文件中的常量与后台的DyFormConfig要实时保持同步
 */
(function (root, factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery'], factory);
  } else if (typeof exports === 'object') {
    // Node. Does not work with strict CommonJS, but
    // only CommonJS-like environments that support module.exports,
    // like Node.
    module.exports = factory(require('jquery'));
  } else {
    // Browser globals (root is window)
    root.dyform_constant = factory(root.jQuery);
  }
})(this, function init($) {
  var exports = {};
  // 操作结果
  var dyResult = {
    success: '1', // 成功
    error: '0' // 失败
  };
  exports.dyResult = window.dyResult = dyResult;

  // 字段类型
  var dyFormInputType = {
    _text: '1', // 文本输入框，允许输入字符串
    _date: '2', // 日期选择，只允许日期控件丢或者手动输入合法的日期
    _double: '12', // 双精度浮点数
    _int: '13', // 文本框整数输入
    _int_positive: '131', // 文本框正整数输入
    _int_negtive: '132', // 文本框负整数输入
    _long: '14', // 长整型输入
    _float: '15', // 浮点数输入
    _clob: '16', // 大字段
    _number: '17'
  };
  exports.dyFormInputType = window.dyFormInputType = dyFormInputType;

  // 单据类型P:存储单据, V:展现单据, M: 手机单据,T:模板单据
  // 参照枚举类DyformTypeEnum
  var FORM_TYPE = {
    PFORM: 'P',
    VFORM: 'V',
    MFORM: 'M',
    MSTFORM: 'MST'
  };
  exports.FORM_TYPE = window.FORM_TYPE = FORM_TYPE;
  var CRITERIONOPERATOR = {
    '<': 'lt',
    '<=': 'le',
    '>': 'gt',
    '>=': 'ge',
    '=': 'eq',
    '<>': 'ne',
    like: 'like',
    notlike: 'nlike',
    between: 'between',
    isnull: 'is null',
    isnotnull: 'is not null',
    in: 'in'
  };
  exports.CRITERIONOPERATOR = window.CRITERIONOPERATOR = CRITERIONOPERATOR;
  // 字段类型
  var dyFormInputTypeObj = {
    _text: {
      code: dyFormInputType._text,
      name: '文本'
    },
    _date: {
      code: dyFormInputType._date,
      name: '日期'
    },
    _double: {
      code: dyFormInputType._double,
      name: '双精度浮点数'
    },
    _int: {
      code: dyFormInputType._int,
      name: '整型'
    },
    _int_positive: {
      code: dyFormInputType._int_positive,
      name: '正整型'
    },
    _int_negtive: {
      code: dyFormInputType._int_negtive,
      name: '负整型'
    },
    _long: {
      code: dyFormInputType._long,
      name: '长整型'
    },
    _float: {
      code: dyFormInputType._float,
      name: '浮点型'
    },
    _clob: {
      code: dyFormInputType._clob,
      name: '大字段'
    },
    _number: {
      code: dyFormInputType._number,
      name: 'number'
    }
  };
  exports.dyFormInputTypeObj = window.dyFormInputTypeObj = dyFormInputTypeObj;

  // 字段值
  var dyFormInputValue = {
    userImport: '1', // 用户输入
    jsEquation: '2', // JS公式
    creatOperation: '3', // 由后台创建时计算
    showOperation: '4', // 由前台显示时计算
    twoDimensionCode: '5', // 二维码
    shapeCod: '6', // 条形码
    relationDoc: '7' // 关联文档
  };
  exports.dyFormInputValue = window.dyFormInputValue = dyFormInputValue;

  var dyFormOrgSelectType = {
    orgSelect: '8', // 组织选择框
    orgSelectStaff: '9', // 组织选择框（人员）
    orgSelectDepartment: '10', // 组织选择框（部门）
    orgSelectStaDep: '11', // 组织选择框（部门+人员）
    orgSelectAddress: '28', // 组织选择框 (单位通讯录)
    orgSelectJob: '51', // 组织选择框 (部门+职位)
    orgSelectPublicGroup: '52', // 组织选择框 (群组(群组+人员))
    orgSelectMyDept: '53', // 组织选择框(我的部门)
    orgSelectMyParentDept: '54', // 组织选择框(上级部门)
    orgSelectMyUnit: '55', // 组织选择框(部门+职位+人员)
    orgSelectPublicGroupSta: '56', // 组织选择框 (群组(人员))
    orgSelectGroup: '57' // 组织选择框 (集团通讯录)
  };
  exports.dyFormOrgSelectType = window.dyFormOrgSelectType = dyFormOrgSelectType;

  // 组织弹出框 6.0 版本对应的数据
  var ORG_OPTS = [
    {
      id: 'MyUnit',
      name: '我的单位'
    },
    {
      id: 'MyDept',
      name: '我的部门'
    },
    {
      id: 'MyLeader',
      name: '我的领导'
    },
    {
      id: 'MyUnderling',
      name: '我的下属'
    },
    {
      id: 'MyCompany',
      name: '我的集团'
    },
    {
      id: 'DutyGroup',
      name: '职务群组'
    },
    {
      id: 'PublicGroup',
      name: '公共群组'
    },
    {
      id: 'MyGroup',
      name: '个人群组'
    }
  ];
  exports.ORG_OPTS = window.ORG_OPTS = ORG_OPTS;

  var ORG_SELECT_TYPES = [
    {
      code: 'O',
      name: '组织'
    },
    {
      code: 'B',
      name: '单位'
    },
    {
      code: 'D',
      name: '部门'
    },
    {
      code: 'J',
      name: '职位'
    },
    {
      code: 'U',
      name: '人员'
    },
    {
      code: 'G',
      name: '群组'
    },
    {
      code: 'DU',
      name: '职务'
    },
    {
      code: 'E',
      name: '通讯录单位'
    },
    {
      code: 'C',
      name: '通讯录分类'
    }
  ];
  exports.ORG_SELECT_TYPES = window.ORG_SELECT_TYPES = ORG_SELECT_TYPES;

  // 输入样式
  var dyFormInputMode = {
    text: '1', // 普通表单输入
    ckedit: '2', // 富文本编辑
    // accessory:'3',//附件
    accessory1: '4', // 图标显示
    // accessory2:'5',//图标显示（含正文）
    accessory3: '6', // 列表显示（不含正文）
    accessoryImg: '33', // 图片附件
    serialNumber: '7', // 可编辑流水号
    unEditSerialNumber: '29', // 不可编辑流水号

    orgSelect: dyFormOrgSelectType.orgSelect, // 组织选择控件 valuemap
    orgSelectStaff: dyFormOrgSelectType.orgSelectStaff, // 组织选择框（人员）
    orgSelectDepartment: dyFormOrgSelectType.orgSelectDepartment, // 组织选择框（部门）
    orgSelectStaDep: dyFormOrgSelectType.orgSelectStaDep, // 组织选择框（部门+人员）
    orgSelectAddress: dyFormOrgSelectType.orgSelectAddress, // 组织选择框 (单位通讯录)

    orgSelect2: '43',

    timeEmploy: '12', // 资源选择
    timeEmployForMeet: '13', // 资源选择（会议）
    timeEmployForCar: '14', // 资源选择（车辆）
    timeEmployForDriver: '15', // 资源选择（司机）
    treeSelect: '16', // 树形下拉框 valuemap
    radio: '17', // radio表单元素 valuemap
    checkbox: '18', // checkbox表单元素 valuemap
    selectMutilFase: '19', // 下拉单选框 valuemap
    comboSelect: '191', // 下拉选项框 valuemap
    select: '199', // select2普通下拉框（新版）
    textArea: '20', // 文本域输入
    // fileUpload:'21',//附件上传
    textBody: '22', // 正文
    dialog: '26', // 弹出框
    xml: '27', // XML

    date: '30', // 日期
    number: '31', // 数字控件
    viewdisplay: '32', // 视图展示
    embedded: '40', // url嵌入页面
    job: '41', // 职位控件
    relevant: '42', // 相关数据控件
    template: '44', // 母版
    chained: '61', // 级联
    taggroup: '126', // 标签组
    colors: '127', // 颜色
    switchs: '128', // 开关按钮
    progress: '129', // 进度条
    placeholder: '130' // 真实值占位符
  };
  exports.dyFormInputMode = window.dyFormInputMode = dyFormInputMode;

  // 手机模块映射
  // 输入样式
  var muiFieldModule = {};
  var muiInput = dyFormInputMode;
  var muiUnit = dyFormOrgSelectType;
  muiFieldModule[muiInput.text] = 'mui-wTextInput'; // 普通表单输入
  muiFieldModule[muiInput.ckedit] = 'mui-wRichEditor'; // 富文本编辑
  muiFieldModule[muiInput.accessory] = 'mui-wFileUpload'; // 附件
  muiFieldModule[muiInput.accessory1] = 'mui-wFileUpload'; // 图标显示
  muiFieldModule[muiInput.accessory2] = 'mui-wFileUpload'; // 图标显示（含正文）
  muiFieldModule[muiInput.accessory3] = 'mui-wFileUpload'; // 列表显示（不含正文）
  muiFieldModule[muiInput.accessoryImg] = 'mui-wFileUpload'; // 图片附件
  muiFieldModule[muiInput.serialNumber] = 'mui-wSerialNumber'; // 可编辑流水号
  muiFieldModule[muiInput.unEditSerialNumber] = 'mui-wSerialNumber'; // 不可编辑流水号
  muiFieldModule[muiUnit.orgSelect] = 'mui-wUnit'; // 组织选择控件 valuemap
  muiFieldModule[muiUnit.orgSelectStaff] = 'mui-wUnit'; // 组织选择框（人员）
  muiFieldModule[muiUnit.orgSelectDepartment] = 'mui-wUnit'; // 组织选择框（部门）
  muiFieldModule[muiUnit.orgSelectStaDep] = 'mui-wUnit'; // 组织选择框（部门+人员）
  muiFieldModule[muiUnit.orgSelectAddress] = 'mui-wUnit'; // 组织选择框 (单位通讯录)
  muiFieldModule[muiInput.orgSelect2] = 'mui-wUnit';
  muiFieldModule[muiInput.timeEmploy] = 'mui-wTimeEmploy'; // 资源选择
  muiFieldModule[muiInput.timeEmployForMeet] = 'mui-wTimeEmploy'; // 资源选择（会议）
  muiFieldModule[muiInput.timeEmployForCar] = 'mui-wTimeEmploy'; // 资源选择（车辆）
  muiFieldModule[muiInput.timeEmployForDriver] = 'mui-wTimeEmploy'; // 资源选择（司机）
  muiFieldModule[muiInput.treeSelect] = 'mui-wComboTree'; // 树形下拉框 valuemap
  muiFieldModule[muiInput.radio] = 'mui-wRadio'; // radio表单元素 valuemap
  muiFieldModule[muiInput.checkbox] = 'mui-wCheckbox'; // checkbox表单元素 valuemap
  muiFieldModule[muiInput.selectMutilFase] = 'mui-wSelect'; // 下拉单选框
  muiFieldModule[muiInput.comboSelect] = 'mui-wSelect'; // 下拉选项框 valuemap
  muiFieldModule[muiInput.select] = 'mui-wSelect'; // 下拉选项框
  muiFieldModule[muiInput.textArea] = 'mui-wTextArea'; // 文本域输入
  muiFieldModule[muiInput.fileUpload] = 'mui-wFileUpload'; // 附件上传
  muiFieldModule[muiInput.textBody] = 'mui-wTextBody'; // 正文
  muiFieldModule[muiInput.dialog] = 'mui-wDialog'; // 弹出框
  muiFieldModule[muiInput.xml] = 'mui-wXml'; // XML
  muiFieldModule[muiInput.date] = 'mui-wDatePicker'; // 日期
  muiFieldModule[muiInput.number] = 'mui-wNumberInput'; // 数字控件
  muiFieldModule[muiInput.viewdisplay] = 'mui-wViewdisplay'; // 视图展示
  muiFieldModule[muiInput.embedded] = 'mui-wEmbedded'; // url嵌入页面
  muiFieldModule[muiInput.job] = 'mui-wJobSelect'; // 职位控件
  muiFieldModule[muiInput.relevant] = 'mui-wRelevant'; // 相关数据控件
  muiFieldModule.subform = 'mui-wSubForm'; // 从表
  exports.muiFieldModule = window.muiFieldModule = muiFieldModule;

  // PC端模块映射
  // 一致性维护：JsonUtils.object2Json(DyformTagSupport.dyformInputModeJavaScriptModules)
  exports.dyformFieldModule = window.dyformFieldModule = {
    133: 'wFormFileLibrary',
    57: 'wUnit2',
    19: 'wComboBox',
    56: 'wUnit2',
    55: 'wUnit2',
    17: 'wRadio',
    18: 'wCheckBox',
    33: 'wFileUpload4Image',
    15: 'wTimeEmploy',
    16: 'wComboTree',
    13: 'wTimeEmploy',
    191: 'wComboSelect,wSelectCommonMethod',
    14: 'wTimeEmploy',
    11: 'wUnit2',
    12: 'wTimeEmploy',
    20: 'wTextArea',
    43: 'wUnit2',
    199: 'wSelect',
    41: 'wJobSelect',
    40: 'wEmbedded',
    130: 'wPlaceholder',
    61: 'wChained',
    126: 'wTagGroup',
    127: 'wColor',
    128: 'wSwitchs',
    45: 'wTableView',
    129: 'wProgress',
    26: 'wDialog',
    28: 'wUnit2',
    29: 'wSerialNumber',
    2: 'wCkeditor',
    1: 'wTextInput',
    10: 'wUnit2',
    7: 'wSerialNumber',
    30: 'wDatePicker',
    6: 'wFileUpload',
    5: 'wFileUpload4Body',
    32: 'wViewDisplay',
    4: 'wFileUpload4Icon,wFileUpload4Body',
    31: 'wNumberInput',
    51: 'wUnit2',
    52: 'wUnit2',
    9: 'wUnit2',
    53: 'wUnit2',
    8: 'wUnit2',
    54: 'wUnit2'
  };

  // 表单事件
  var dyformEvent = {
    // 表单创建完成
    DyformCreationComplete: 'DyformCreationComplete'
  };
  exports.dyformEvent = dyformEvent;

  // 元素展示
  var elementShow = {
    show: '1', // 展示
    hidden: '2' // 隐藏
  };
  exports.elementShow = window.elementShow = elementShow;

  // 字段展示
  var dyFormDataShow = {
    directShow: '1', // 编辑展示
    indirect: '2' // 显示展示
  };
  exports.dyFormDataShow = window.dyFormDataShow = dyFormDataShow;

  // 后台数据类型
  var dyFormDataType = {
    string: dyFormInputType._text, // 字符串
    date: dyFormInputType._date, // 日期
    double: dyFormInputType._double,
    int: dyFormInputType._int, // 整数
    int_positive: dyFormInputType._int_positive, // 正整数
    int_negtive: dyFormInputType._int_negtive, // 负整数
    long: dyFormInputType._long, // 长整型
    float: dyFormInputType._float, // 浮点数
    clob: dyFormInputType._clob, // 大字段
    number: dyFormInputType._number
    // number类型
  };
  exports.dyFormDataType = window.dyFormDataType = dyFormDataType;

  // 从表编辑类型
  var dySubFormEdittype = {
    rowEdit: '1', // 行内编辑
    newWin: '2' // 弹出窗口编辑
  };
  exports.dySubFormEdittype = window.dySubFormEdittype = dySubFormEdittype;

  // 从表按钮编码
  var dySubFormBtnCode = {
    add: 'btn_add', // 添加
    del: 'btn_del', // 删除
    clear: 'btn_clear', // 清除空行
    up: 'btn_up', // 上移
    down: 'btn_down', // 下移
    addSub: 'btn_add_sub', // 添加子行
    expSubform: 'btn_exp_subform', // 导出
    impSubform: 'btn_imp_subform' // 导入
  };
  exports.dySubFormBtnCode = window.dySubFormBtnCode = dySubFormBtnCode;

  // 从表展示类型
  var dySubFormShowType = {
    jqgridShow: '1', // jqgrid展示
    tableShow: '2' // 普通列表展示
  };
  exports.dySubFormShowType = window.dySubFormShowType = dySubFormShowType;

  // 从表是否隐藏操作按钮
  var dySubFormHideButtons = {
    show: '1', // 展示
    hide: '2', // 隐藏
    showAdd: '3', // 只显示添加
    showDel: '4' // 只显示删除
  };
  exports.dySubFormHideButtons = window.dySubFormHideButtons = dySubFormHideButtons;

  // 从表是否隐藏操作按钮
  var dySubFormTableOpen = {
    open: '1', // 展开
    notOpen: '2' // 折叠
  };
  exports.dySubFormTableOpen = window.dySubFormTableOpen = dySubFormTableOpen;

  // 从表数据分组展示
  var dySubFormGroupShow = {
    show: '1', // 展开
    notShow: '2' // 折叠
  };
  exports.dySubFormGroupShow = window.dySubFormGroupShow = dySubFormGroupShow;

  // 从表字段展示或隐藏
  var dySubFormFieldShow = {
    show: '1', // 展示
    notShow: '2' // 隐藏
  };
  exports.dySubFormFieldShow = window.dySubFormFieldShow = dySubFormFieldShow;

  // 从表字段展示或隐藏
  var dySubFormFieldEdit = {
    notEdit: '0', // 不可编辑
    edit: '1' // 可编辑
  };
  exports.dySubFormFieldEdit = window.dySubFormFieldEdit = dySubFormFieldEdit;

  // 在从表字段可编辑的前提一下,将在光标离开时，还一样显示为控件而非标签
  var dySubFormFieldCtl = {
    label: '0', // 不可编辑
    control: '1' // 可编辑
  };
  exports.dySubFormFieldCtl = window.dySubFormFieldCtl = dySubFormFieldCtl;

  // 校验规则
  var dyCheckRule = {
    // 约束条件
    notNull: '1', // 非空
    unique: '5', // 全局唯一校验
    // 文本样式，校验规则
    common: '10', //
    url: '11',
    email: '12',
    idCard: '13',
    tel: '14', // 固定电话
    mobilePhone: '15', // 手机
    customizeRegular: '16', // 自定义规则
    postcode: '17', // 自定义规则
    // 数字控件校验
    num_int: 'n13', // 整数
    num_int_positive: 'n131', // 正整数
    num_int_negtive: 'n132', // 负整数
    num_long: 'n14', // 长整数
    num_float: 'n15', // 浮点数
    num_double: 'n12' // 双精度浮点数
  };
  exports.dyCheckRule = window.dyCheckRule = dyCheckRule;

  // 表类型
  var dyTableType = {
    mainTable: '1', // 主表
    subtable: '2' // 从表
  };
  exports.dyTableType = window.dyTableType = dyTableType;

  // 供选项来源
  var dyDataSourceType = {
    dataConstant: '1', // 常量
    dataDictionary: '2', // 字典
    dataView: '3', // 视图
    dataSource: '4', // 数据源
    custom: '5' // 用户自定义
  };
  exports.dyDataSourceType = window.dyDataSourceType = dyDataSourceType;

  // 编辑模式
  var dyshowType = {
    edit: '1', // 可编辑
    showAsLabel: '2', // 直接以文本的形式显示
    readonly: '3', // 有输入框但只读
    disabled: '4', // 有输入框但被disabled
    hide: '5' // 隐藏
  };
  exports.dyshowType = window.dyshowType = dyshowType;

  // 表单数据状态
  var dyStatusType = {
    NORMAL: '', // 正常
    DELETED: '' // 已被删除
  };
  exports.dyStatusType = window.dyStatusType = dyStatusType;

  /**
   * 是否启用签名
   */
  var signature = {
    enable: '2', // 启用
    disable: '1' // 不启用
  };
  exports.signature = window.signature = signature;

  /**
   * 用于标识表单中各字段的类型
   */
  var dyFieldSysType = {
    system: 0, // 系统字段
    custom: 2, // 用户自定义字段
    admin: 1, // 管理员定义字段
    // parentForm:3//该字段用于保存对应的记录的主表的数据uuid
    assist: 4
    // 辅助性字段
  };
  exports.dyFieldSysType = window.dyFieldSysType = dyFieldSysType;

  var dyDateFmt = {
    yearMonthDate: '1', // 当前日期(2000-01-01)
    yearMonthDateCn: '2', // 当前日期(2000年1月1日)
    yearCn: '3', // 当前日期(2000年)
    yearMonthCn: '4', // 当前日期(2000年1月)
    monthDateCn: '5', // 当前日期(1月1日)
    weekCn: '6', // 当前日期(星期一)
    year: '7', // 当前年份(2000)
    timeHour: '8', // 当前时间(12)
    timeMin: '9', // 当前时间(12:00)
    timeSec: '10', // 当前时间(12:00:00)
    dateTimeHour: '11', // 日期到时 当前日期时间(2000-01-01 12)
    dateTimeMin: '12', // 日期到分 当前日期时间(2000-01-01 12:00)
    dateTimeSec: '13', // 日期到秒 当前日期时间(2000-01-01 12:00:00)
    yearMonth: '14', // 当前日期(2000-01)
    week: '15', // 周
    yearMonthDate2: '16', // 当前日期(2000-1-1)
    yearMonthDateCn2: '17' // 当前日期(2000年01月01日)
  };
  exports.dyDateFmt = window.dyDateFmt = dyDateFmt;

  var dyDpStyle = {
    BootstrapDatePicker: 'BootstrapDatePicker', //
    LayDate: 'LayDate'
  };
  exports.dyDpStyle = window.dyDpStyle = dyDpStyle;
  /**
   * 系统变量定义，用于text文本框的默认值
   */
  var dySysVariable = {
    currentYearMonthDate: '{CURRENTYEARMONTHDATE}', // 当前日期(2000-01-01)
    currentYearMonthDateCn: '{CURRENTYEARMONTHDATECN}', // 当前日期(2000年1月1日)
    currentYearCn: '{CURRENTYEARCN}', // 当前日期(2000年)
    currentYearMonthCn: '{CURRENTYEARMONTHCN}', // 当前日期(2000年1月)
    currentMonthDateCn: '{CURRENTMONTHDATECN}', // 当前日期(1月1日)
    currentWeekCn: '{CURRENTWEEKCN}', // 当前日期(星期一)
    currentYear: '{CURRENTYEAR}', // 当前年份(2000)
    currentTimeMin: '{CURRENTTIMEMIN}', // 当前时间(12:00)
    currentTimeSec: '{CURRENTTIMESEC}', // 当前时间(12:00:00)
    currentDateTimeMin: '{CURRENTDATETIMEMIN}', // 日期到分 当前日期时间(2000-01-01
    // 12:00)
    currentDateTimeSec: '{CURRENTDATETIMESEC}', // 日期到秒 当前日期时间(2000-01-01
    // 12:00:00)

    currentUser: '{CURRENTUSER}', // {当前用户}
    currentUserId: '{CURRENTUSERID}', // {当前用户ID}
    currentUserName: '{CURRENTUSERNAME}', // {当前用户姓名}

    currentUserDepartment: '{CURRENTUSERDEPARTMENT}', // {当前用户部门}
    currentUserDepartmentId: '{CURRENTUSERDEPARTMENTID}', // {当前用户部门ID}
    currentUserDepartmentName: '{CURRENTUSERDEPARTMENTNAME}', // {当前用户部门(短名称)}
    currentUserDepartmentPath: '{CURRENTUSERDEPARTMENTPATH}', // {当前用户部门(长名称)}

    currentUserMainJob: '{CURRENTUSERMAINJOB}', // {当前用户主职位}
    currentUserMainJobId: '{CURRENTUSERMAINJOBID}', // {当前用户主职位ID}
    currentUserMainJobName: '{CURRENTUSERMAINJOBNAME}', // {当前用户主职位(短名称)}
    currentUserMainJobPath: '{CURRENTUSERMAINJOBPATH}', // {当前用户主职位(长名称)}

    currentUserBizUnit: '{CURRENTUSERBIZUNIT}', // {当前用户业务单位}
    currentUserBizUnitId: '{CURRENTUSERBIZUNITID}', // {当前用户业务单位ID}
    currentUserBizUnitName: '{CURRENTUSERBIZUNITNAME}', // {当前用户业务单位名称}

    currentUserSysUnit: '{CURRENTUSERSYSUNIT}', // {当前用户系统单位}
    currentUserSysUnitId: '{CURRENTUSERSYSUNITID}', // {当前用户系统单位ID}
    currentUserSysUnitName: '{CURRENTUSERSYSUNITNAME}', // {当前用户系统单位名称}

    currentCreatorId: '{CURRENTCREATORID}', // {创建人ID}
    currentCreatorName: '{CURRENTCREATORNAME}', // {创建人姓名}
    currentCreatorDepartmentId: '{CURRENTCREATORDEPARTMENTID}', // {创建人部门ID}
    currentCreatorDepartmentPath: '{CURRENTCREATORDEPARTMENTPATH}', // {创建人部门(长名称)}
    currentCreatorDepartmentName: '{CURRENTCREATORDEPARTMENTNAME}', // {创建人部门(短名称)}
    currentCreatorMainJobName: '{CURRENTCREATORMAINJOBNAME}' // {创建人主职位}
  };
  exports.dySysVariable = window.dySysVariable = dySysVariable;

  /**
   * 关联控件的展示方式
   */
  var relationShowType = {
    dialog: '1', // 单击文本输入框弹出对话框，供选择的数据展示在对话框里面
    dropdiv: '2' // 在文本框中输入关键字，将搜索到的供选择的数据展示在下拉的div中
  };
  exports.relationShowType = window.relationShowType = relationShowType;

  // 控件位置
  var dyControlPos = {
    mainForm: '0', // 主表
    subForm: '1' // 子表
  };
  exports.dyControlPos = window.dyControlPos = dyControlPos;

  var groupUsed = {
    uniqueGroup: 'uniqueGroup' // 拥有这个属性的元素间的值互斥，则值不得相等
  };
  exports.groupUsed = window.groupUsed = groupUsed;

  /* 时间资源控件的类型 */
  var timeResouceType = {
    MEET_RESOURCE: 'MEET_RESOURCE',
    CAR_RESOURCE: 'CAR_RESOURCE',
    DRIVER_RESOURCE: 'DRIVER_RESOURCE'
  };
  exports.timeResouceType = window.timeResouceType = timeResouceType;

  /**
   * 数据关联方式 数据关联控件有两种，一种是通过弹出框，一种是通过搜索框,通过这两种控件将关联数据加载进控件中
   */
  var dyRelativeMethod = {
    DIALOG: '1', // 弹出框
    SEARCH: '2' // 搜索框
  };
  exports.dyRelativeMethod = window.dyRelativeMethod = dyRelativeMethod;

  /**
   * 单选和多选
   */
  var dySelectType = {
    single: '1', // 单选
    multiple: '2' // 多选
  };
  exports.dySelectType = window.dySelectType = dySelectType;

  var layOutInput = {
    tab: '1',
    block: '2'
  };
  exports.layOutInput = window.layOutInput = layOutInput;

  // 验证分组类型
  var validateGroupType = {
    unique: 'uniqueGroup', // 在同一类分组,唯一
    required: 'requiredGroup'
  };
  exports.validateGroupType = window.validateGroupType = validateGroupType;

  // 是否痕迹保留
  var keepOpLogType = {
    yes: '1',
    no: '0'
  };
  exports.keepOpLogType = window.keepOpLogType = keepOpLogType;

  var searchCtlAllowInput = {
    yes: keepOpLogType.yes,
    no: keepOpLogType.no
  };
  exports.searchCtlAllowInput = window.searchCtlAllowInput = searchCtlAllowInput;

  // add by wujx 20160623
  var applyToOptions = {
    SERIAL_NO: 'WORKFLOW_SERIAL_NO'
  };
  exports.applyToOptions = window.applyToOptions = applyToOptions;

  return exports;
});
