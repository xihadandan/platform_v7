/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights
 *          reserved. For licensing, see LICENSE.html or
 *          http://ckeditor.com/license
 */

var CkPluginObj = {
  FORM: {
    modes: [],
    code: 'dyform',
    name: 'dyform',
    isControl: false
  },
  SUBFORM: {
    modes: [],
    code: 'dysubform',
    name: '从表',
    isControl: true
  },
  DATECTL: {
    modes: ['30'],
    code: 'control4date',
    name: '日期',
    isControl: true
  },
  LABEL: {
    modes: [],
    code: 'control4label',
    name: '标签',
    isControl: false
  },

  PREVIEW: {
    modes: [],
    code: 'dyformpreview',
    name: '预览',
    isControl: false
  },
  TREESELECTCTL: {
    modes: ['16'],
    code: 'control4treeselect',
    name: '树形下拉',
    isControl: true
  },
  NUMBERCTL: {
    modes: ['31'],
    code: 'control4number',
    name: '数字输入框',
    isControl: true
  },
  RADIOCTL: {
    modes: ['17'],
    code: 'control4radio',
    name: '单选框',
    isControl: true
  },
  COMBOBOXCTL: {
    modes: ['19'],
    code: 'control4combobox',
    name: '下拉框(废弃)',
    isControl: true
  },
  CHECKBOXCTL: {
    modes: ['18'],
    code: 'control4checkbox',
    name: '复选框',
    isControl: true
  },
  SELECTCTL: {
    modes: ['199'],
    code: 'control4select',
    name: '下拉框(new)',
    isControl: true
  },
  COMBOSELECTCTL: {
    modes: ['191'],
    code: 'control4comboselect',
    name: '分组下拉框',
    isControl: true
  },
  COPYFORM: {
    modes: [],
    code: 'copyForm',
    name: 'copyForm',
    inUsed: false,
    isControl: false
  },
  RATY: {
    modes: [],
    code: 'control4raty',
    name: 'control4raty',
    inUsed: false,
    isControl: false
  },
  TAGS: {
    modes: [],
    code: 'control4tag',
    name: 'control4tag',
    inUsed: false,
    isControl: false
  },
  TIPS: {
    modes: [],
    code: 'control4tip',
    name: 'control4tip',
    inUsed: false,
    isControl: false
  },
  LAYOUT: {
    modes: [],
    code: 'layout',
    name: 'layout',
    inUsed: false,
    isControl: false
  },
  TEMPLATECONTAINER: {
    modes: [],
    code: 'templatecontainer',
    name: '子表单',
    inUsed: false,
    isControl: false
  },
  CKEDITORCTL: {
    modes: ['2'],
    code: 'control4ckeditor',
    name: '富文本框',
    isControl: true
  },
  TEXTCTL: {
    modes: ['1'],
    code: 'control4text',
    name: '单行文本框',
    isControl: true
  },
  TEXTAREACTL: {
    modes: ['20'],
    code: 'control4textarea',
    name: '多行文本框',
    isControl: true
  },

  SERIALNUMBERCTL: {
    modes: ['7'],
    code: 'control4serialnumber',
    name: '流水号',
    isControl: true
  },
  DIALOGCTL: {
    modes: ['26'],
    code: 'control4dialog',
    name: '弹出框|搜索框',
    isControl: true
  },
  UNITCTL: {
    modes: [],
    code: 'control4unit',
    name: '组织选择框',
    isControl: false
  },
  MULTIORG: {
    modes: ['43', '8', '9', '10', '11', '28', '51', '52', '53', '54', '55', '56', '57'],
    code: 'control4multiOrg',
    name: '组织选择框',
    isControl: true
  },
  FILEUPLOADCTL: {
    modes: ['6'],
    code: 'control4fileupload',
    name: '列表式附件',
    isControl: true
  },
  FILEUPLOAD4ICONCTL: {
    modes: ['4'],
    code: 'control4fileupload4icon',
    name: '图标式附件',
    isControl: true
  },
  FILEUPLOAD4IMAGECTL: {
    modes: ['33'],
    code: 'control4fileupload4image',
    name: '图片附件',
    isControl: true
  },
  VIEWDISPLAYCTL: {
    modes: [],
    code: 'control4viewdisplay',
    name: '视图显示',
    isControl: false,
    inUsed: false
  },
  PROPERTIESDIALOGS: {
    modes: [],
    code: 'propertiesDialog',
    name: '字段属性列表窗口',
    isControl: false
  },
  HIERARCHYDIALOGS: {
    modes: [],
    code: 'hierarchyDialog',
    name: '表单层级结构',
    isControl: false
  },
  BLOCK: {
    modes: [],
    code: 'block',
    name: '区块',
    isControl: false
  },
  BUTTON: {
    modes: [],
    code: 'control4btn',
    name: '按钮',
    isControl: false
  },
  CONTROL4TIMEEMPLOY: {
    modes: ['12', '13', '14', '15'],
    code: 'control4timeEmploy',
    name: '时间资源',
    isControl: false
  },
  EMBEDDED: {
    modes: [],
    code: 'control4embedded',
    name: '嵌入页面',
    isControl: true
  },
  JOBS: {
    modes: ['41'],
    code: 'control4jobs',
    name: '职位',
    isControl: true
  },
  TABS: {
    modes: [],
    code: 'layout4tab',
    name: '页签',
    isControl: false
  },
  UPLOADIMAGE: {
    modes: [],
    code: 'uploadimage',
    name: 'uploadimage',
    inUsed: false,
    isControl: false
  },
  UPLOADWIDGET: {
    modes: [],
    code: 'uploadwidget',
    name: 'uploadwidget',
    inUsed: false,
    isControl: false
  },
  FILETOOLS: {
    modes: [],
    code: 'filetools',
    name: 'filetools',
    inUsed: false,
    isControl: false
  },
  NOTIFICATIONAGGREGATOR: {
    modes: [],
    code: 'notificationaggregator',
    name: 'notificationaggregator',
    inUsed: false,
    isControl: false
  },
  WIDGET: {
    modes: [],
    code: 'widget',
    name: 'widget',
    inUsed: false,
    isControl: false
  },
  LINEUTILS: {
    modes: [],
    code: 'lineutils',
    name: 'lineutils',
    inUsed: false,
    isControl: false
  },
  CLIPBOARD: {
    modes: [],
    code: 'clipboard',
    name: 'clipboard',
    inUsed: false,
    isControl: false
  },
  NOTIFICATION: {
    modes: [],
    code: 'notification',
    name: 'notification',
    inUsed: false,
    isControl: false
  },
  TABLEVIEWCTL: {
    modes: [],
    code: 'control4tableview',
    name: 'control4tableview',
    inUsed: false,
    isControl: false
  },
  CHAINED: {
    modes: ['61'],
    code: 'control4chained',
    name: '级联控件',
    isControl: true
  },
  TAGGROUP: {
    modes: ['126'],
    code: 'control4taggroup',
    name: '标签组控件',
    isControl: true
  },
  TIPS: {
    modes: [],
    code: 'control4tips',
    name: '提示',
    isControl: false
  },
  COLORS: {
    modes: ['127'],
    code: 'control4color',
    name: '颜色控件',
    isControl: true
  },
  SWITCHS: {
    modes: ['128'],
    code: 'control4switch',
    name: '开关按钮',
    isControl: true
  },
  PROGRESS: {
    modes: ['129'],
    code: 'control4progress',
    name: '进度条控件',
    isControl: true
  },
  PLACEHOLDER: {
    modes: ['130'],
    code: 'control4placeholder',
    name: '真实值占位符控件',
    isControl: true
  },
  FILELIBRARYCTL: {
    modes: [],
    code: 'control4filelibrary',
    name: 'control4filelibrary',
    inUsed: false,
    isControl: false
  }
  // 页签
};

var mode2PluginCodeMap = {},
  modes = null;
for (var i in CkPluginObj) {
  if ((modes = CkPluginObj[i].modes) && modes.length > 0) {
    // 加此判断: 为了兼容IE
    for (var j = 0; j < modes.length; j++) {
      mode2PluginCodeMap[modes[j]] = CkPluginObj[i].code;
    }
  }
}

function getPlubinCodeByInputMode(inputMode) {
  return mode2PluginCodeMap[inputMode];
}

function getControlPlugins() {
  var plugins = [];
  for (var i in CkPluginObj) {
    if (CkPluginObj.hasOwnProperty(i)) {
      // 加此判断: 为了兼容IE
      var code = CkPluginObj[i].code;
      var name = CkPluginObj[i].name;
      var isCtl = CkPluginObj[i].isControl;
      if (isCtl) {
        plugins.push({
          code: code,
          name: name
        });
      }
    }
  }
  return plugins;
}

window.CkPlugin = $.extend({}, window.CkPlugin);
for (var i in CkPluginObj) {
  if (CkPluginObj.hasOwnProperty(i)) {
    // 加此判断: 为了兼容IE
    CkPlugin[i] = CkPluginObj[i].code;
  }
}

CKEDITOR.editorConfig = function (config) {
  // config.fillEmptyBlocks = false; // Prevent filler nodes in all empty
  // blocks.

  // config.pasteFromWordRemoveFontStyles = false;//是否清楚word字体样式

  config.extraPlugins =
    CkPlugin.FORM +
    ',' +
    CkPlugin.SUBFORM +
    ',' +
    CkPlugin.DATECTL +
    ',' +
    CkPlugin.TREESELECTCTL +
    ',' +
    CkPlugin.SERIALNUMBERCTL +
    ',' +
    CkPlugin.MULTIORG +
    ',' +
    // + CkPlugin.UNITCTL2 + ","
    CkPlugin.NUMBERCTL +
    ',' +
    CkPlugin.RADIOCTL +
    ',' +
    CkPlugin.DIALOGCTL +
    ',' +
    CkPlugin.CHECKBOXCTL +
    ',' +
    CkPlugin.SELECTCTL +
    ',' +
    CkPlugin.COMBOBOXCTL +
    ',' +
    CkPlugin.COMBOSELECTCTL +
    ',' +
    CkPlugin.FILEUPLOADCTL +
    ',' +
    CkPlugin.FILEUPLOAD4ICONCTL +
    ',' +
    CkPlugin.FILEUPLOAD4IMAGECTL +
    ',' +
    CkPlugin.TEXTCTL +
    ',' +
    CkPlugin.TEXTAREACTL +
    ',' +
    CkPlugin.CKEDITORCTL +
    ',' +
    CkPlugin.LABEL +
    ',' +
    CkPlugin.PREVIEW +
    ',' +
    CkPlugin.PROPERTIESDIALOGS +
    ',' +
    CkPlugin.HIERARCHYDIALOGS +
    ',' +
    CkPlugin.BUTTON +
    ',' +
    CkPlugin.PROPERTIESDIALOGS4MODEL +
    ',' +
    CkPlugin.MODELFIELD +
    ',' +
    CkPlugin.MODELSUBFORM +
    ',' +
    CkPlugin.COPYFORM +
    ',' +
    CkPlugin.EMBEDDED +
    ',' +
    CkPlugin.JOBS +
    ',' +
    CkPlugin.TABS +
    ',' +
    CkPlugin.RATY +
    ',' +
    CkPlugin.TAGS +
    ',' +
    CkPlugin.TIPS +
    ',' +
    CkPlugin.BLOCK +
    ',' +
    CkPlugin.LAYOUT +
    ',' +
    CkPlugin.TEMPLATECONTAINER +
    ',' +
    CkPlugin.UPLOADIMAGE +
    ',' +
    CkPlugin.UPLOADWIDGET +
    ',' +
    CkPlugin.FILETOOLS +
    ',' +
    CkPlugin.NOTIFICATIONAGGREGATOR +
    ',' +
    CkPlugin.WIDGET +
    ',' +
    CkPlugin.LINEUTILS +
    ',' +
    CkPlugin.CLIPBOARD +
    ',' +
    CkPlugin.NOTIFICATION +
    ',' +
    'table' +
    ',' +
    CkPlugin.TABLEVIEWCTL +
    ',' +
    CkPlugin.CHAINED +
    ',' +
    CkPlugin.TAGGROUP +
    ',' +
    CkPlugin.TIPS +
    ',' +
    CkPlugin.COLORS +
    ',' +
    CkPlugin.SWITCHS +
    ',' +
    CkPlugin.PROGRESS +
    ',' +
    CkPlugin.PLACEHOLDER +
    ',' +
    CkPlugin.FILELIBRARYCTL; // 添加插件，多个时用","隔开
  // console.log(config.extraPlugins);
  config.pasteFromWordRemoveStyles = true;
  (config.filebrowserImageUploadUrl = '/pt/dyform/data/uploadImage?status=1&_csrf=' + getCookie('_csrfToken')),
    (config.baseHref = ctx + '/');
  config.pasteFromWordPromptCleanup = true; // 是否提示保留word样式
  config.pasteFromWordNumberedHeadingToList = true;
  config.removeDialogTabs = 'image:advanced;link:advanced';

  config.enterMode = CKEDITOR.ENTER_P;
  config.shiftEnterMode = CKEDITOR.ENTER_P;

  var theme = getUserTheme();
  config.contentsCss = theme.themeFiles[0];

  config.tabIndex = 4;
  config.tabSpaces = 8; // 制表键走的空格数
  config.ignoreEmptyParagraph = false; // 是否忽略段落中的空格
  config.enterMode = CKEDITOR.ENTER_BR; // 编辑器中回车产生的标签
  // config.font_names = 'Arial;Times New Roman;Verdana';
  config.font_names = '宋体/宋体;黑体/黑体;仿宋/仿宋_GB2312;楷体/楷体_GB2312;隶书/隶书;幼圆/幼圆;微软雅黑/微软雅黑;' + config.font_names; // 添加中文字体
  config.defaultLanguage = 'zh-cn';
  // config.font_defaultLabel = '宋体';
  config.toolbarCanCollapse = true; // 工具栏是否可伸缩
  config.autoParagraph = false;
  // 显示表格边框
  config.startupShowBorders = false;
  config.format_p = {
    element: 'p',
    attributes: {
      class: 'normalPara'
    }
  };
  /*
   * config.format_pre = { element: 'pre', attributes: { 'class': 'code' } }; //
   * config.fullPage = true; config.format_div = { element : 'div', attributes : {
   * 'class' : 'normalDiv' } }; config.format_address = { element: 'address',
   * attributes: { 'class': 'styledAddress' } };
   */

  // config.filebrowserUploadUrl="/web/ckfinder/upload/image?command=upload",
  // config.filebrowserBrowseUrl = '/web/resources/ckfinder/ckfinder.html',
  // config.filebrowserImageBrowseUrl =
  // '/web/resources/ckfinder/ckfinder.html?type=Images',
  // config.filebrowserFlashBrowseUrl =
  // '/web/resources/ckfinder/ckfinder.html?type=Flash',
  // config.filebrowserUploadUrl =
  // '/web/resources/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Files',
  // config.filebrowserImageUploadUrl =
  // '/web/resources/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Images',
  // config.filebrowserFlashUploadUrl =
  // '/web/resources/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Flash',
  config.width = '100%'; // 宽度
  config.height = '400px'; // 高度
  config.toolbarStartupExpanded = false;
  config.allowedContent = true;
  // config.filebrowserWindowWidth = '1000',
  // config.filebrowserWindowHeight = '700';

  loadCommonJsFile();
};

function loadCommonJsFile() {
  var head = document.getElementsByTagName('head').item(0);
  var script = document.createElement('script');
  script.src = staticPrefix + '/js/ckeditor4.5.11/common.js';
  script.type = 'text/javascript';
  head.appendChild(script);

  script = document.createElement('script');
  script.src = staticPrefix + '/js/layout/1.3.0/js/jquery.layout-latest.js';
  script.type = 'text/javascript';
  head.appendChild(script);

  script = document.createElement('script');
  script.src = staticPrefix + '/js/layout/1.3.0/js/jquery-ui-latest.js';
  script.type = 'text/javascript';
  head.appendChild(script);

  script = document.createElement('script');
  script.src = staticPrefix + '/dyform/commons/FormClass.js';
  script.type = 'text/javascript';
  head.appendChild(script);

  script = document.createElement('script');
  script.src = staticPrefix + '/dyform/commons/dyform_constant.js';
  script.type = 'text/javascript';
  head.appendChild(script);

  script = document.createElement('script');
  script.src = staticPrefix + '/dyform/commons/function.js';
  script.type = 'text/javascript';
  head.appendChild(script);

  script = document.createElement('script');
  script.src = staticPrefix + '/js/jqueryplugin/jquery.placeholder.min.js';
  script.type = 'text/javascript';
  head.appendChild(script);
}
