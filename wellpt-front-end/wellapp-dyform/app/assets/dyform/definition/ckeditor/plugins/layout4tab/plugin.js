(function() {
	if(typeof CkPlugin == "undefined"){
		return;
	}
	var pluginName =   CkPlugin.TABS; 
    CKEDITOR.plugins.add(pluginName, {
        requires: ["dialog"],
        init: function(a) {
        	a.focusedDomPropertyUrl4Tbl =  CKEDITOR.plugins.basePath + pluginName + "/" + "dialogs/" + pluginName + ".html";//属性窗口中的html对应的地址,dysubform.js中要使用到该变量

            CKEDITOR.dialog.add(pluginName, CKEDITOR.plugins.basePath + pluginName + "/" + "dialogs/" + pluginName +".js");
            
            a.addCommand(pluginName, new CKEDITOR.dialogCommand(pluginName));
            
            a.ui.addButton(pluginName, {
                label: "页签",//调用dialog时显示的名称
                command: pluginName,
                icon: CKEDITOR.plugins.basePath + pluginName + "/" + "images/anchor.png"//在toolbar中的图标
            });
          
            //定义双击事件
           a.on( 'doubleclick', function( evt ) {
        	   ckUtils.allDoubleClick(evt,pluginName,this);
	        		/*var element = evt.data.element;  //element是CKEDITOR.dom.node类的对象 
					var pluginContainerDomElement = CKEDITOR.getPluginContainerDomElement(pluginName, element); 
					if(pluginContainerDomElement != null){
						 a.focusedDom = pluginContainerDomElement;
						 evt.data.dialog = pluginName;
					}*/
        		}
           );
        }
    });
})();