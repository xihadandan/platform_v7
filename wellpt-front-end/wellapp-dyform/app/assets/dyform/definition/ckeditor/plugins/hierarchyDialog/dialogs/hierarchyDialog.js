(function () {
  var pluginName = CkPlugin.HIERARCHYDIALOGS;
  CKEDITOR.dialog.add(pluginName, function (editor) {
    var widthHeight = getWidthAndHeight();
    var containerID = 'container_' + pluginName;
    return {
      title: '表单层级结构',
      minHeight: widthHeight.height - 160,
      minWidth: widthHeight.width - 160,
      contents: [
        {
          id: 'form-tree',
          label: 'label',
          title: 'title',
          expand: true,
          padding: 0,
          elements: [
            {
              id: 'table_html',
              type: 'html',
              style: 'width: 100%;',
              html: "<div id='" + containerID + "'>表格层级结构</div>"
            }
          ]
        }
      ],
      onOk: function () {},
      onCancel: function () {},
      onShow: function () {
        formDefinition.updateFormTree();
        var hierarchyDialogURL = editor.hierarchyDialogURL;

        $('#' + containerID).load(hierarchyDialogURL, function () {
          hierarchyDialog.init(editor); //初始化属性窗口
        });
      }
    };
  });
})();
