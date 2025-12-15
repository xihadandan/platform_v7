(function () {
  if (typeof CkPlugin == 'undefined') {
    return;
  }
  var pluginName = CkPlugin.BLOCK;

  CKEDITOR.dialog.add(pluginName, function (editor) {
    var containerID = 'container_' + pluginName;
    return {
      title: '设置区块标题',
      minHeight: 100,
      minWidth: 300,
      contents: [
        {
          id: 'setTitle',
          label: 'label',
          title: 'title',
          expand: true,
          padding: 0,
          elements: [
            {
              id: 'setForm',
              type: 'html',
              style: 'width: 100%;',
              html: "<div id='" + containerID + "'>设置区块标题</div>",
              onLoad: function () {}
            }
          ]
        }
      ],
      onOk: function () {
        if (!block.fillCkeditor()) return false;
        block.exitDialog();
      },
      onCancel: function () {
        //退出窗口时清空属性窗口的缓存
        if (typeof block.exitDialog != 'undefined') {
          block.exitDialog();
        }
      },
      onShow: function () {
        var focusedDomPropertyUrl = editor.focusedDomPropertyUrl4Form;
        //以GET方式(不得以POST方式)加载属性窗口的html
        $('#' + containerID).load(focusedDomPropertyUrl, function () {
          block.initPropertyDialog(editor); //初始化属性窗口
          $.ControlConfigUtil.enableFocus(editor, CKEDITOR.dialog._.currentTop, containerID);
        });
      }
    };
  });
})();
