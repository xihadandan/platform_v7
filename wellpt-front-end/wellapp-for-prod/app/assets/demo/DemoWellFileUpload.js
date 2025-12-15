define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;
  var AppWellFileDemoWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppWellFileDemoWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var self = this;
      var $widgetElement = self.getWidgetElement();
      $('#wellfileupload .title').text('requirejs.config附件示例');
      requirejs.config({
        paths: {
          wellfileupload: '/static/js/pt/js/fileupload/well.fileupload',
          'jquery-ui': '/static/js/jqueryui/1.12.1/jquery-ui',
          jquery: '/static/js/jquery/1.12.4/jquery-1.12.4',
          'jquery-migrate': '/static/js/jquery/1.12.4/jquery-migrate-1.4.1.min',
          'jquery.fileupload': '/static/js/fileupload/js/jquery.fileupload',
          'jquery.iframe-transport': '/static/js/fileupload/js/jquery.iframe-transport',
          'jquery.fileupload-process': '/static/js/fileupload/js/jquery.fileupload-process',
          'jquery.fileupload-validate': '/static/js/fileupload/js/jquery.fileupload-validate',
          fileuploadconstant: '/static/js/pt/js/fileupload/well.fileupload.constant',
          global: '/static/js/pt/js/global',
          cookie: '/static/js/cookie/jquery.cookie',
          wellMessage: '/static/js/pt/js/webmessage/wellMessage',
          SockJS: '/static/js/websocket/js/sockjs',
          stomp: '/static/js/websocket/js/stomp',
          'spark-md5': '/static/js/spark-md5/spark-md5.min'
        },
        shim: {
          wellfileupload: {
            deps: [
              'jquery-ui',
              'jquery',
              'jquery-migrate',
              'jquery.fileupload',
              'jquery.iframe-transport',
              'jquery.fileupload-process',
              'jquery.fileupload-validate',
              'fileuploadconstant',
              'global',
              'cookie',
              'wellMessage',
              'SockJS',
              'stomp',
              'spark-md5'
            ],
            exports: 'wellfileupload'
          },
          'jquery-ui': {
            deps: ['jquery', 'jquery-migrate'],
            exports: 'jquery-ui'
          },
          jquery: {
            exports: 'jquery'
          },
          'jquery-migrate': {
            deps: ['jquery'],
            exports: 'jquery-migrate'
          },
          'jquery.fileupload': {
            deps: ['jquery-ui', 'jquery', 'jquery-migrate'],
            exports: 'jquery.fileupload'
          },
          'jquery.iframe-transport': {
            deps: ['jquery-ui', 'jquery', 'jquery-migrate', 'jquery.fileupload'],
            exports: 'jquery.iframe-transport'
          },
          'jquery.fileupload-process': {
            deps: ['jquery-ui', 'jquery', 'jquery-migrate', 'jquery.fileupload'],
            exports: 'jquery.fileupload-process'
          },
          'jquery.fileupload-validate': {
            deps: ['jquery-ui', 'jquery', 'jquery-migrate', 'jquery.fileupload-process', 'jquery.fileupload'],
            exports: 'jquery.fileupload-validate'
          },
          fileuploadconstant: {
            deps: ['jquery', 'jquery-migrate', 'global', 'cookie', 'wellMessage', 'SockJS', 'stomp'],
            exports: 'fileuploadconstant'
          },
          global: {
            deps: ['jquery', 'jquery-migrate', 'cookie', 'wellMessage', 'SockJS', 'stomp'],
            exports: 'global'
          },
          cookie: {
            deps: ['jquery', 'jquery-migrate'],
            exports: 'cookie'
          },
          wellMessage: {
            deps: ['jquery', 'SockJS', 'stomp'],
            exports: 'wellMessage'
          },
          SockJS: {
            deps: ['jquery'],
            exports: 'SockJS'
          },
          stomp: {
            deps: ['jquery'],
            exports: 'stomp'
          },
          'spark-md5': {
            exports: 'spark-md5'
          }
        },
        waitSeconds: 0,
        map: {
          '*': {
            css: '/static/js/requirejs/css.min.js'
          }
        },
        baseUrl: '/static'
      });
      requirejs(
        ['wellfileupload', 'css!/static/dyform/css/explain/fileupload.css', 'css!/static/js/wellBtnLib/wellBtnLib.css'],
        function () {
          var ctlID = 'wellfileupload_1';
          var $attachContainer = $('#' + ctlID);
          var fileuploadobj = new WellFileUpload(
            ctlID, // ctlID,控件唯一ID
            null //options//其他选项,比如默认上传按钮名称{uploadButtonLable:""}
          );
          fileuploadobj.initAllowUploadDeleteDownload(
            true, // allowUpload,
            true, // allowDelete,
            true // allowDownload
          );
          var btns = {
            upload_btn: '00b13afb-8afc-4a9e-b1e2-28f321f48924',
            preview_btn: '5f82f10a-9450-4a18-8c8d-e38e3767b466',
            download_btn: '4ee25050-f1e5-49d2-a635-9c19ef1785dc',
            delete_btn: '09691fb3-fdd3-4790-863d-6b77d4aa4bbd',
            re_upload_btn: 'ea62b928-c050-4f15-9766-0aa3758ea8ac',
            cancel_upload_btn: '3f4e80c9-4ee1-4edf-ae21-a945a67abdab',
            copy_name_btn: '954dac1c-8ae8-4bd2-86a6-e3283325989c'
          };
          fileuploadobj.initFileUploadExtraParam(
            true, // isShowFileFormatIcon,显示格式图标
            false, //isShowFileSourceIcon,显示来源图标
            Object.values(btns).join(';'), //secDevBtnIdStr,附件二开按钮权限，见：表单管理》附件列表配置》附件按钮
            null, //fileSourceIdStr,附件来源二开权限，见：表单管理》附件列表配置》附件来源
            null //flowSecDevBtnIdStr//流程二开按钮权限
          );
          fileuploadobj.init(
            false, // readOnly,是否只读,例如只给一些用户只读的权限
            $attachContainer, // $attachContainer, 存放该附件的容器
            false, //signature, 是否签名
            true, //multiple, 多文件为true,单文件为false
            [] // dbFiles, 初始化的附件
          );
        }
      );
    }
  });
  return AppWellFileDemoWidgetDevelopment;
});
