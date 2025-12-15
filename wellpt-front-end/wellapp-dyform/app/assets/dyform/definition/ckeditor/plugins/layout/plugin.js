(function() {
	var pluginName = CkPlugin.LAYOUT;
	CKEDITOR.plugins.add(pluginName, {
		requires : [ "dialog" ],
		init : function(a) {
			a.focusedDomPropertyUrl4Layout = CKEDITOR.plugins.basePath + pluginName + "/" + "dialogs/" + pluginName + ".html";// 属性窗口中的html对应的地址,dysubform.js中要使用到该变量

			// 定义"设置表单"对话框
			CKEDITOR.dialog.add(pluginName, CKEDITOR.plugins.basePath + pluginName + "/" + "dialogs/" + pluginName + ".js");

			// 定义命令，用于打开"设置表单"对话框
			a.addCommand(pluginName, new CKEDITOR.dialogCommand(pluginName));

			// 定义一个按钮,用于触发打开"设置表单"对话框的命令
			var tablegrid = "menugrid";
			a.addCommand(tablegrid, {
				exec : function( editor, data )
			    {	
					// $(this.document.find("body").getItem(0)).attr("class", "dyform");
					var noGridClass = "dyform_nogrid";
					var bodyElement = editor.document.find("body").getItem(0);
					if(this.state === CKEDITOR.TRISTATE_ON){
						this.setState(CKEDITOR.TRISTATE_OFF);
						bodyElement.removeClass(noGridClass);
					}else if(this.state === CKEDITOR.TRISTATE_OFF){
						this.setState(CKEDITOR.TRISTATE_ON);
						bodyElement.addClass(noGridClass);
					}
			    },
			    canUndo : true    // No support for undo/redo
			});
			a.ui.addButton(tablegrid, {
				label : "布局网格",// 调用dialog时显示的名称
				command : tablegrid,
				icon : CKEDITOR.plugins.basePath + pluginName + "/" + "images/menu_grid.png"// 在toolbar中的图标
			});
		}
	});
})();