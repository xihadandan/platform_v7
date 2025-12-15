(function() {
	if(typeof CkPlugin == "undefined"){
		return;
	}
	var pluginName =   CkPlugin.LABEL; 
    CKEDITOR.plugins.add(pluginName, {
        requires: ["dialog"],
        init: function(a) { 
        	a.focusedDomPropertyUrl4Label =  CKEDITOR.plugins.basePath + pluginName + "/" + "dialogs/" + pluginName + ".html";//属性窗口中的html对应的地址,dysubform.js中要使用到该变量
        	
        	//定义"设置标签"对话框
            CKEDITOR.dialog.add(pluginName, CKEDITOR.plugins.basePath + pluginName + "/" + "dialogs/" + pluginName +".js");
            
        	//定义命令，用于打开"设置标签"对话框
            a.addCommand(pluginName, new CKEDITOR.dialogCommand(pluginName));
            
            //定义一个按钮,用于触发打开"设置标签"对话框的命令
            a.ui.addButton(pluginName, {
                label: "插入标签",//调用dialog时显示的名称
                command: pluginName,
                icon: CKEDITOR.plugins.basePath + pluginName + "/" + "images/anchor.jpg"//在toolbar中的图标
            });
            
            
            
           
            //定义双击事件
           a.on( 'doubleclick', function( evt ) {
        	   ckUtils.allDoubleClick(evt,pluginName,this);
					/*var element = evt.data.element;  //element是CKEDITOR.dom.node类的对象 
					
					var pluginContainerDomElement = CKEDITOR.getPluginContainerDomElement(pluginName, element); 
					if(pluginContainerDomElement != null){ 
   						 a.focusedDom = pluginContainerDomElement;
   						 evt.data.dialog = pluginName;
   						
   					// window.setTimeout(function(){ 
   					//	label.initPropertyDialog(a);//重新初始化属性窗口
					//	 }, 200);
						
					}*/
        		}
           );
           
           
           
           
           
         /*   
            if ( a.contextMenu )
    		{
    			a.contextMenu.addListener( function( element, selection )
    				{
    					if ( !element )
    						return null;

    					var isTable	= element.is( 'table' ) || element.hasAscendant( 'table' );

    					if ( isTable )
    					{
    						return {
    							tabledelete : CKEDITOR.TRISTATE_OFF,
    							table : CKEDITOR.TRISTATE_OFF
    						};
    					}

    					return null;
    				} );
    		}*/
            
            
            
        }
    });
})();