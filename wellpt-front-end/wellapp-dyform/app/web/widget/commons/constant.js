export const selectModeOptions = [
  { label: '单选', value: 'default' },
  { label: '多选', value: 'multiple' }
];

export const selectTypeOptions = [
  { label: '下拉框', value: 'select' },
  { label: '分组下拉框', value: 'select-group' }
  // { label: '树形下拉框', value: 'select-tree' }
];

export const conditionOptions = [
  { label: '=', value: 'eq' },
  { label: '!=', value: 'ne' },
  { label: '<', value: 'lt' },
  { label: '>', value: 'gt' },
  { label: '≤', value: 'le' },
  { label: '≥', value: 'ge' },
  { label: '包含', value: 'like' },
  { label: '不包含', value: 'nlike' }
];

export const sourceOptions = [
  { label: '表单字段', value: 'field' },
  { label: '变量', value: 'variable' }
];

export const optionSourceTypes = [
  { label: '常量', value: 'selfDefine' },
  { label: '数据字典', value: 'dataDictionary' },
  { label: '数据仓库', value: 'dataSource' },
  { label: '数据模型', value: 'dataModel' },
  { label: 'API 服务', value: 'apiLinkService' }
];

export const layoutOptions = [
  { label: '水平', value: 'horizontal' },
  { label: '垂直', value: 'vertical' }
];

export const datePatterns = [
  'YYYY-MM-DD HH:mm:ss',
  'YYYY-MM-DD HH:mm',
  'YYYY-MM-DD HH',
  'YYYY-MM-DD',
  'YYYY-MM',
  'YYYY',
  'YYYY年MM月DD日 HH时mm分ss秒',
  'YYYY年MM月DD日 HH时mm分',
  'YYYY年MM月DD日 HH时',
  'YYYY年MM月DD日',
  'YYYY年MM月',
  'YYYY年',
  'HH:mm:ss',
  'HH:mm',
  'HH时mm分ss秒',
  'HH时mm分'
];

export const fileUploadTypeOptions = [
  {
    label: '简易列表上传',
    value: 'simpleList'
  },
  {
    label: '高级列表上传',
    value: 'advancedList'
  },
  {
    label: '图片上传',
    value: 'picture'
  }
];

export const advancedFileListTypeOptions = [
  {
    label: '列表视图',
    value: 'listView'
  },
  {
    label: '表格视图',
    value: 'tableView'
  },
  {
    label: '图标视图',
    value: 'iconView'
  }
];

export const uploadAccept = ['.doc', '.docx', '.ppt', '.pptx', '.xls', '.xlsx', '.zip', '.txt', '.png', '.jpg', '.jpeg', '.gif', '.pdf'];

export const pictureAccept = ['.jpg', '.png', '.jpeg', '.bmp', '.gif', '.heic', '.tif', '.dib', '.jfif'];

/*
  btnShowType: 'show' 显示类操作, 'edit':编辑类操作
  btnType: 1 内置按钮, 0:扩展按钮
  defaultFlag: 1 默认打开，0 默认关闭
*/
// 行按钮 高级视图和正文多个'编辑'
export const rowBtnCommon = [
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onClickReplace',
    code: 'onClickReplace',
    buttonName: '替换',
    defaultFlag: false,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onClickRename',
    code: 'onClickRename',
    buttonName: '重命名',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onClickDelFile',
    code: 'onClickDelFile',
    buttonName: '删除',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'show',
    id: 'onClickPreviewFile',
    code: 'onClickPreviewFile',
    buttonName: '预览',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'show',
    id: 'onClickDownloadFile',
    code: 'onClickDownloadFile',
    buttonName: '下载',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'show',
    id: 'onClickSaveAs',
    code: 'onClickSaveAs',
    buttonName: '另存为',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'show',
    id: 'onClickCopyFilename',
    code: 'onClickCopyFilename',
    buttonName: '复制名称',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'show',
    id: 'onClickLookUp',
    code: 'onClickLookUp',
    buttonName: '查阅',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onClickSeal',
    code: 'onClickSeal',
    buttonName: '盖章',
    defaultFlag: false,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'show',
    id: 'onClickShowHistory',
    code: 'onClickShowHistory',
    buttonName: '历史记录',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onClickMoveUp',
    code: 'onClickMoveUp',
    buttonName: '上移',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onClickMoveDown',
    code: 'onClickMoveDown',
    buttonName: '下移',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  }
];

export const rowBtnEdit = {
  btnType: '1',
  btnShowType: 'edit',
  id: 'onClickEdit',
  code: 'onClickEdit',
  buttonName: '编辑',
  defaultFlag: true,
  style: { icon: undefined },
  customEvent: {}
};

// 简易视图
export const headerBtnSimple = [
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onClickUpload',
    code: 'onClickUpload',
    buttonName: '上传',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'show',
    id: 'onClickAllDownload',
    code: 'onClickAllDownload',
    buttonName: '全部下载',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  }
];

// 高级视图
export const headerBtnAdvanced = [
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onAdvancedAddFile',
    code: 'onAdvancedAddFile',
    buttonName: '添加附件',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onPasteFile',
    code: 'onPasteFile',
    buttonName: '粘贴附件',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onAdvancedBatchDel',
    code: 'onAdvancedBatchDel',
    buttonName: '批量删除',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'show',
    id: 'onAdvancedBatchDownload',
    code: 'onAdvancedBatchDownload',
    buttonName: '批量下载',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  }
];

// 正文
export const headerBtnText = [
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onNewText',
    code: 'onNewText',
    buttonName: '新建正文',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onImportText',
    code: 'onImportText',
    buttonName: '导入正文',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onPasteFile',
    code: 'onPasteFile',
    buttonName: '粘贴附件',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onAdvancedBatchDel',
    code: 'onAdvancedBatchDel',
    buttonName: '批量删除',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'show',
    id: 'onAdvancedBatchDownload',
    code: 'onAdvancedBatchDownload',
    buttonName: '批量下载',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  }
];

// 图片行按钮
export const rowBtnPicture = [
  {
    btnType: '1',
    btnShowType: 'show',
    id: 'onClickPreviewPicture',
    code: 'onClickPreviewPicture',
    buttonName: '预览',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'show',
    id: 'onClickDownloadFile',
    code: 'onClickDownloadFile',
    buttonName: '下载',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  },
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onClickDelFile',
    code: 'onClickDelFile',
    buttonName: '删除',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  }
];

export const pictureBatchDownload = {
  btnType: '1',
  btnShowType: 'show',
  id: 'onPictureBatchDownload',
  code: 'onPictureBatchDownload',
  buttonName: '批量下载',
  defaultFlag: false,
  style: { icon: undefined },
  customEvent: {}
};

export const headerBtnPicture = [
  {
    btnType: '1',
    btnShowType: 'edit',
    id: 'onAddPicture',
    code: 'onAddPicture',
    buttonName: '添加',
    defaultFlag: true,
    style: { icon: undefined },
    customEvent: {}
  }
];

export const commonRegExp = {
  cellphoneNumber: /^(1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[2-8]|8[0-9]|9[0-35-9])\d{8})$/g,
  identity: /\d{17}[0-9Xx]|\d{15}/gi,
  chinese: /^[\u4e00-\u9fa5]{0,}$/,
  email: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
  url: /^http:\/\/([\w-]+\.)+[\w-]+(\/[\w-.\/?%&=]*)?$/,
  telephoneNumber: /\d{3}-\d{8}|\d{4}-\d{7}/,
  postCode: /^[0-9]{6}$/
};

export const dateSysVariables = {
  '当前日期(2000-01-01)': '{CURRENTYEARMONTHDATE}',
  '当前日期(2000年1月1日)': '{CURRENTYEARMONTHDATECN}',
  '当前日期(2000年)': '{CURRENTYEARCN}',
  '当前日期(2000年1月)': '{CURRENTYEARMONTHCN}',
  '当前日期(1月1日)': '{CURRENTMONTHDATECN}',
  '当前日期(星期一)': '{CURRENTWEEKCN}',
  '当前年份(2000)': '{CURRENTYEAR}',
  '当前时间(12:00)': '{CURRENTTIMEMIN}',
  '当前时间(12:00:00)': '{CURRENTTIMESEC}',
  '当前日期时间(2000-01-01 12:00)': '{CURRENTDATETIMEMIN}',
  '当前日期时间(2000-01-01 12:00:00)': '{CURRENTDATETIMESEC}'
};

export const userSysVariables = {
  当前用户ID: '{CURRENTUSERID}',
  当前用户姓名: '{CURRENTUSERNAME}',
  当前用户部门: '{CURRENTUSERDEPARTMENT}',
  当前用户部门ID: '{CURRENTUSERDEPARTMENTID}',
  '当前用户部门(短名称)': '{CURRENTUSERDEPARTMENTNAME}',
  '当前用户部门(长名称)': '{CURRENTUSERDEPARTMENTPATH}',
  当前用户主职位: '{CURRENTUSERMAINJOB}',
  当前用户主职位ID: '{CURRENTUSERMAINJOBID}',
  '当前用户主职位(短名称)': '{CURRENTUSERMAINJOBNAME}',
  '当前用户主职位(长名称)': '{CURRENTUSERMAINJOBPATH}',
  // 当前用户业务单位: '{CURRENTUSERBIZUNIT}',
  // 当前用户业务单位ID: '{CURRENTUSERBIZUNITID}',
  // 当前用户业务单位名称: '{CURRENTUSERBIZUNITNAME}',
  当前用户单位: '{CURRENTUSERSYSUNIT}',
  当前用户单位ID: '{CURRENTUSERSYSUNITID}',
  当前用户单位名称: '{CURRENTUSERSYSUNITNAME}',
  创建人ID: '{CURRENTCREATORID}',
  创建人姓名: '{CURRENTCREATORNAME}',
  创建人部门ID: '{CURRENTCREATORDEPARTMENTID}',
  '创建人部门(长名称)': '{CURRENTCREATORDEPARTMENTPATH}',
  '创建人部门(短名称)': '{CURRENTCREATORDEPARTMENTNAME}',
  创建人主职位: '{CURRENTCREATORMAINJOBNAME}'
};

export const fileSignatures = {
  png: [
    [0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a] // PNG
  ],
  jpg: [
    [0xff, 0xd8, 0xff, 0xe0], // JPEG
    [0xff, 0xd8, 0xff, 0xe1], // JPEG
    [0xff, 0xd8, 0xff, 0xe2] // JPEG
  ],
  jpeg: [
    [0xff, 0xd8, 0xff, 0xe0], // JPEG
    [0xff, 0xd8, 0xff, 0xe1], // JPEG
    [0xff, 0xd8, 0xff, 0xe2] // JPEG
  ],
  jfif: [[0xff, 0xd8, 0xff, 0xe0]],
  heic: [[0x00, 0x00, 0x00, 0x20, 0x66, 0x74, 0x79, 0x70, 0x68, 0x65, 0x69, 0x63]],
  gif: [
    [0x47, 0x49, 0x46, 0x38] // GIF
  ],
  bmp: [
    [0x42, 0x4d] // BMP
  ],
  dib: [[0x42, 0x4d]],
  tiff: [
    [0x49, 0x49, 0x2a, 0x00], // TIFF (little-endian)
    [0x4d, 0x4d, 0x00, 0x2a] // TIFF (big-endian)
  ],
  tif: [
    [0x49, 0x49, 0x2a, 0x00], // TIFF (little-endian)
    [0x4d, 0x4d, 0x00, 0x2a] // TIFF (big-endian)
  ],
  webp: [
    [0x52, 0x49, 0x46, 0x46] // WEBP
  ],
  pdf: [
    [0x25, 0x50, 0x44, 0x46] // PDF
  ],
  zip: [
    [0x50, 0x4b, 0x03, 0x04] // ZIP
  ],
  rar: [
    [0x52, 0x61, 0x72, 0x21, 0x1a, 0x07, 0x00] // RAR
  ],
  '7z': [
    [0x37, 0x7a, 0xbc, 0xaf, 0x27, 0x1c] // 7-Zip
  ],
  gzip: [
    [0x1f, 0x8b] // GZIP
  ],
  mp3: [
    [0x49, 0x44, 0x33], // MP3 (ID3v2)
    [0xff, 0xfb] // MP3 (no ID3)
  ],
  wav: [
    [0x52, 0x49, 0x46, 0x46] // WAV
  ],
  flac: [
    [0x66, 0x4c, 0x61, 0x43] // FLAC
  ],
  mp4: [
    [0x00, 0x00, 0x00, 0x18, 0x66, 0x74, 0x79, 0x70, 0x6d, 0x70, 0x34, 0x32] // MP4
  ],
  avi: [
    [0x52, 0x49, 0x46, 0x46] // AVI
  ],
  mkv: [
    [0x1a, 0x45, 0xdf, 0xa3] // MKV
  ],
  exe: [
    [0x4d, 0x5a] // EXE
  ],
  dll: [
    [0x4d, 0x5a] // DLL
  ],
  doc: [
    [0xd0, 0xcf, 0x11, 0xe0, 0xa1, 0xb1, 0x1a, 0xe1] // DOC (Word 97-2003)
  ],
  docx: [
    [0x50, 0x4b, 0x03, 0x04] // DOCX (ZIP-based)
  ],
  xls: [
    [0xd0, 0xcf, 0x11, 0xe0, 0xa1, 0xb1, 0x1a, 0xe1] // XLS (Excel 97-2003)
  ],
  xlsx: [
    [0x50, 0x4b, 0x03, 0x04] // XLSX (ZIP-based)
  ],
  ppt: [
    [0xd0, 0xcf, 0x11, 0xe0, 0xa1, 0xb1, 0x1a, 0xe1] // PPT (PowerPoint 97-2003)
  ],
  pptx: [
    [0x50, 0x4b, 0x03, 0x04] // PPTX (ZIP-based)
  ],
  wps: [
    [0xd0, 0xcf, 0x11, 0xe0, 0xa1, 0xb1, 0x1a, 0xe1] // WPS (与 DOC 相同)
  ],
  et: [
    [0xd0, 0xcf, 0x11, 0xe0, 0xa1, 0xb1, 0x1a, 0xe1] // ET (与 XLS 相同)
  ],
  dps: [
    [0xd0, 0xcf, 0x11, 0xe0, 0xa1, 0xb1, 0x1a, 0xe1] // DPS (与 PPT 相同)
  ],

  dmg: [
    [0x78, 0x01, 0x73, 0x0d, 0x62, 0x62, 0x60] // DMG
  ]
};
