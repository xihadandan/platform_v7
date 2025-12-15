// 常量定义
'use strict';

module.exports = {
  pageDesingerTypes: [
    { text: '默认页面容器', id: 'wPage' },
    { text: '手机页面设计器', id: 'wMobilePage' },
    // { text: 'Layoutit页面容器', id: 'wLayoutit' },
    { text: '可视化页面设计器', id: 'vPage' },
    { text: 'uni-app页面设计器', id: 'vUniPage' },
  ],
  componentCategory: {
    layout: {
      name: '布局组件'
    },
    basic: {
      name: '基本组件'
    },
    app: {
      name: '应用组件'
    },
    report: {
      name: '报表组件'
    }
  },

  DMS: {
    wDataManagementViewer: {
      js: [
        'cmsWindown',
        'bootstrap',
        'appContext',
        'select2',
        'select2_locale_zh-cn',
        'layer',
        'appModal',
        'dataStoreBase',
        'slimScroll',
        'niceScroll',
        'AppTabs',
        'server',
        'DmsDocumentViewDevelopment',
        'jquery-dmsDocumentView',
        'formDefinitionMethod',
        'DyformFunction',
        'DyformDevelopment',
        'wControlManager',
        'DyformExplain',
        'dyform_explain',
        'bootstrap',
        'jquery-ui',
        'appContext',
        'appWindowManager',
        'wCommonDialog',
        'appModal',
        'appDispatcher',
        'wWidget',
        'uuid'
      ],
      css: [
        'bootstrap',
        'pace',
        'app-base',
        'font-awesome',
        'iconfont',
        'wellsoft-iconfont',
        'jquery-ui',
        'ui-jqgrid',
        'ui-multiselect',
        'ztree',
        'select2',
        'jquery-fileupload-ui',
        'dyform',
        'dyform-fileupload',
        'dyform-calendar',
        'bootstrap-datetimepicker',
        'AppTabs'
      ],
      viewPath: '/dyform/dyform_data_view.nj'
    },
    wMobileDataManagementViewer: {},
    wMobileFileManager: {},
    wDocExchanger: {
      js: [
        'cmsWindown',
        'bootstrap',
        'appContext',
        'select2',
        'select2_locale_zh-cn',
        'layer',
        'appModal',
        'dataStoreBase',
        'slimScroll',
        'niceScroll',
        'AppTabs',
        'server',
        'DmsDocumentViewDevelopment',
        'jquery-dmsDocumentView',
        'formDefinitionMethod',
        'DyformFunction',
        'DyformDevelopment',
        'wControlManager',
        'DyformExplain',
        'dyform_explain',
        'bootstrap',
        'jquery-ui',
        'appContext',
        'appWindowManager',
        'wCommonDialog',
        'appModal',
        'appDispatcher',
        'wWidget',
        'uuid',
        'wUnit2',
        'formBuilder',
        'bootstrap-datetimepicker',
        'wFileUpload',
        'DmsActionDispatcher',
        'bootstrapTable'
      ],
      css: [
        'bootstrap',
        'pace',
        'app-base',
        'font-awesome',
        'iconfont',
        'wellsoft-iconfont',
        'jquery-ui',
        'ui-jqgrid',
        'ui-multiselect',
        'ztree',
        'select2',
        'jquery-fileupload-ui',
        'dyform',
        'dyform-fileupload',
        'dyform-calendar',
        'bootstrap-datetimepicker',
        'AppTabs'
      ],
      viewPath: '/dms/doc_exchanger/doc_exchanger_data_view.nj'
    }
  }
};
