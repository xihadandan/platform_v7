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
      $('#wellfileupload .title').text('formBuilder附件示例');
      appContext.require(['formBuilder'], function (formBuilder) {
        var ctlID = 'wellfileupload_1';
        var $attachContainer = $('#' + ctlID);
        var btns = {
          upload_btn: '00b13afb-8afc-4a9e-b1e2-28f321f48924',
          preview_btn: '5f82f10a-9450-4a18-8c8d-e38e3767b466',
          download_btn: '4ee25050-f1e5-49d2-a635-9c19ef1785dc',
          delete_btn: '09691fb3-fdd3-4790-863d-6b77d4aa4bbd',
          re_upload_btn: 'ea62b928-c050-4f15-9766-0aa3758ea8ac',
          cancel_upload_btn: '3f4e80c9-4ee1-4edf-ae21-a945a67abdab',
          copy_name_btn: '954dac1c-8ae8-4bd2-86a6-e3283325989c'
        };
        formBuilder.buildFileUpload(
          {
            name: 'apply-file', // name,控件名
            label: '申请书', // label,字段标签名
            container: $attachContainer, // container,容器
            controlOption: {
              singleFile: true, // singleFile,多文件为false,单文件为true
              addFileText: '上传申请书', // addFileText,上传按钮名称
              isShowFileFormatIcon: true, // isShowFileFormatIcon,显示格式图标
              isShowFileSourceIcon: false, //isShowFileSourceIcon,显示来源图标
              secDevBtnIdStr: Object.values(btns).join(';'), //secDevBtnIdStr,附件二开按钮权限，见：表单管理》附件列表配置》附件按钮
              fileSourceIdStr: null, //fileSourceIdStr,附件来源二开权限，见：表单管理》附件列表配置》附件来源
              flowSecDevBtnIdStr: null //flowSecDevBtnIdStr//流程二开按钮权限
            } // controlOption,控件选项
          }, //options,
          false //isAsLabel
        );
      });
    }
  });
  return AppWellFileDemoWidgetDevelopment;
});
