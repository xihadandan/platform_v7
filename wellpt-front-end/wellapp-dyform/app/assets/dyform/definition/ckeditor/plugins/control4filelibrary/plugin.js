(function () {
    //有两种方法可进入该对话框
    //1:通过点击工具栏的插入从表,如果通过这种方式，需要先判断焦点的位置，不得在从表所在的位置再创建另一个从表
    //2:通过双击ckeditor编辑框内的从表元素
    var pluginName = CkPlugin.FILELIBRARYCTL;

    CKEDITOR.plugins.add(pluginName, {
        requires: ["dialog"],
        init: function (a) {
            //定义"设置从表"对话框
            CKEDITOR.dialog.add(pluginName, this.path + "dialogs/" + pluginName + ".js");

            //定义命令，用于打开"设置文件库列表"对话框
            a.addCommand(pluginName, new CKEDITOR.dialogCommand(pluginName));

            //定义一个按钮,用于触发打开"设置文件库列表"对话框的命令
            a.ui.addButton(pluginName, {
                label: "插入文件夹目录维护组件",//调用dialog时显示的名称
                command: pluginName,
                icon: this.path + "images/anchor.png"//在toolbar中的图标
            });

            //定义双击事件
            a.on('doubleclick', function (evt) {
                ckUtils.allDoubleClick(evt, pluginName, this);
            });
        }
    });
})();