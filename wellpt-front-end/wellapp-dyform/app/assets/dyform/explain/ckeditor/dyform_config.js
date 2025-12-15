var CkPlugin = {
  XML: 'xml',
  IMAGE2: 'image2',
  UPLOADIMAGE: 'uploadimage',
  UPLOADWIDGET: 'uploadwidget',
  FILETOOLS: 'filetools',
  NOTIFICATIONAGGREGATOR: 'notificationaggregator',
  WIDGET: 'widget',
  LINEUTILS: 'lineutils',
  CLIPBOARD: 'clipboard',
  NOTIFICATION: 'notification',
  INDENT: 'control4indent',
  EXPAND: 'control4expand',
  CHANGEMODE: 'changeMode',
  AJAX: 'ajax',
  PASTETOOLS: 'pastetools',
  PASTEFROMWORD: 'pastefromword',
  HTML5VIDEO: 'html5video'
};

CKEDITOR.editorConfig = function (config) {
  config.extraPlugins =
    CkPlugin.XML +
    ',' +
    CkPlugin.IMAGE2 +
    ',' +
    CkPlugin.UPLOADIMAGE +
    ',' +
    CkPlugin.UPLOADWIDGET +
    ',' +
    CkPlugin.FILETOOLS +
    ',' +
    CkPlugin.NOTIFICATION +
    ',' +
    CkPlugin.NOTIFICATIONAGGREGATOR +
    ',' +
    CkPlugin.WIDGET +
    ',' +
    CkPlugin.LINEUTILS +
    ',' +
    CkPlugin.CLIPBOARD +
    ',' +
    CkPlugin.INDENT +
    ',' +
    CkPlugin.EXPAND +
    ',' +
    CkPlugin.CHANGEMODE +
    ',' +
    CkPlugin.AJAX +
    ',' +
    CkPlugin.PASTETOOLS +
    ',' +
    CkPlugin.PASTEFROMWORD +
    ',' +
    CkPlugin.HTML5VIDEO; // 添加插件，多个时用","隔开

  // console.log(config.extraPlugins);
  config.pasteFromWordRemoveStyles = true;
  config.filebrowserImageUploadUrl = '/pt/dyform/data/uploadFile?status=1&_csrf=' + getCookie('_csrfToken');
  config.filebrowserHtml5videoUploadUrl = '/pt/dyform/data/uploadFile?status=1&_csrf=' + getCookie('_csrfToken'); //上传视频的地址
  config.baseHref = ctx + '/';
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
  config.font_names = '宋体/宋体;黑体/黑体;仿宋/仿宋_GB2312;楷体/楷体_GB2312;隶书/隶书;幼圆/幼圆;微软雅黑/微软雅黑;' + config.font_names; // 添加中文字体
  config.defaultLanguage = 'zh-cn';
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
  config.width = '100%'; // 宽度
  config.height = '400px'; // 高度
  config.toolbarStartupExpanded = false;
  CKEDITOR.tools.array = CKEDITOR.tools.array || {};
  CKEDITOR.tools.array.filter =
    CKEDITOR.tools.array.filter ||
    function (elements, fn) {
      return Array.prototype.filter.call(elements, fn);
    };
  //loadCommonJsFile();
};

function loadCommonJsFile() {
  var head = document.getElementsByTagName('head').item(0);
  var script = document.createElement('script');
  script.src = ctx + '/resources/ckeditor4.5.11/common.js';
  script.type = 'text/javascript';
  head.appendChild(script);

  script = document.createElement('script');
  script.src = ctx + '/resources/layout/1.3.0/js/jquery.layout-latest.js';
  script.type = 'text/javascript';
  head.appendChild(script);

  script = document.createElement('script');
  script.src = ctx + '/resources/layout/1.3.0/js/jquery-ui-latest.js';
  script.type = 'text/javascript';
  head.appendChild(script);

  script = document.createElement('script');
  script.src = ctx + '/resources/pt/js/dyform/commons/FormClass.js';
  script.type = 'text/javascript';
  head.appendChild(script);

  script = document.createElement('script');
  script.src = ctx + '/resources/pt/js/dyform/commons/dyform_constant.js';
  script.type = 'text/javascript';
  head.appendChild(script);

  script = document.createElement('script');
  script.src = ctx + '/resources/pt/js/dyform/commons/function.js';
  script.type = 'text/javascript';
  head.appendChild(script);

  script = document.createElement('script');
  script.src = ctx + '/resources/jqueryplugin/jquery.placeholder.min.js';
  script.type = 'text/javascript';
  head.appendChild(script);
}
