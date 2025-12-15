(function () {
  var pluginName = CkPlugin.PROPERTIESDIALOGS;
  CKEDITOR.plugins.add(pluginName, {
    requires: ['dialog'],
    init: function (a) {
      //属性窗口中的html对应的地址,dysubform.js中要使用到该变量
      a.propertiesDialogURL = CKEDITOR.plugins.basePath + pluginName + '/' + 'dialogs/' + pluginName + '.html';

      //定义"设置从表"对话框
      CKEDITOR.dialog.add(pluginName, CKEDITOR.plugins.basePath + pluginName + '/' + 'dialogs/' + pluginName + '.js');

      //定义命令，用于打开"设置从表"对话框
      a.addCommand(pluginName, new CKEDITOR.dialogCommand(pluginName));

      //定义一个按钮,用于触发打开"设置从表"对话框的命令
      a.ui.addButton(pluginName, {
        label: '字段属性列表窗口', //调用dialog时显示的名称
        command: pluginName,
        icon: CKEDITOR.plugins.basePath + pluginName + '/' + 'images/properties.png' //在toolbar中的图标
      });
    }
  });
})();
