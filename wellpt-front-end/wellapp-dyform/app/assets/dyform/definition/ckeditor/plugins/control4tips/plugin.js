(function() {
	if(typeof CkPlugin == "undefined"){
		return;
	}
	var pluginName = CkPlugin.TIPS;
    CKEDITOR.plugins.add(pluginName, {
        requires: ["dialog"],
        init: function(a) {
        	a.focusedDomPropertyUrl4Tips =  CKEDITOR.plugins.basePath + pluginName + "/" + "dialogs/" + pluginName + ".html";
        	
        	//定义"设置表单"对话框
            CKEDITOR.dialog.add(pluginName, CKEDITOR.plugins.basePath + pluginName + "/" + "dialogs/" + pluginName +".js");
            
        	//定义命令，用于打开"设置表单"对话框
            a.addCommand(pluginName, new CKEDITOR.dialogCommand(pluginName));

            //定义一个按钮,用于触发打开"设置表单"对话框的命令
            a.ui.addButton(pluginName, {
                label: "提示",//调用dialog时显示的名称
                command: pluginName,
                icon: CKEDITOR.plugins.basePath + pluginName + "/" + "images/anchor.png"//在toolbar中的图标
            });
            
            //定义双击事件
           a.on( 'doubleclick', function( evt ) {
        	   ckUtils.allDoubleClick(evt,pluginName,this);
					
        		}
           );
        }
    });
})();