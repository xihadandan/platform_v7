(function() {
	if(typeof CkPlugin == "undefined"){
		return;
	}
	var pluginName =   CkPlugin.FORM; 
    CKEDITOR.plugins.add(pluginName, {
        requires: ["dialog"],
        init: function(a) { 
        	a.focusedDomPropertyUrl4Form =  CKEDITOR.plugins.basePath + pluginName + "/" + "dialogs/" + pluginName + ".html";//属性窗口中的html对应的地址,dysubform.js中要使用到该变量
        	
        	//定义"设置表单"对话框
            CKEDITOR.dialog.add(pluginName, CKEDITOR.plugins.basePath + pluginName + "/" + "dialogs/" + pluginName +".js");
            
        	//定义命令，用于打开"设置表单"对话框
            a.addCommand(pluginName, new CKEDITOR.dialogCommand(pluginName));
             
            
            function createDef( def ) {
    			return CKEDITOR.tools.extend( def || {}, {
    				contextSensitive: 1,
    				refresh: function( editor, path ) {
    					this.setState( path.contains( 'table', 1 ) ? CKEDITOR.TRISTATE_OFF : CKEDITOR.TRISTATE_DISABLED );
    				}
    			});
    		}

    		 
    		a.addCommand( 'tableDelete', createDef({//重新定义表格删除事件,主表不得删除
    			exec: function( editor ) {
    			 
    				var path = editor.elementPath(),
    					table = path.contains( 'table', 1 );
    				
    				if ( !table )
    					return;
    				

    				// If the table's parent has only one child remove it as well (unless it's the body or a table cell) (#5416, #6289)
    				var parent = table.getParent();
    				if ( parent.getChildCount() == 1 && !parent.is( 'body', 'td', 'th' ) )
    					table = parent;
    				/*var clazzOfTbl = $(table).attr("class");
    				if(typeof clazzOfTbl != "undefined" && clazzOfTbl.indexOf("mainform") != -1){
    					alert("主表不得删除");
    					return;
    				}*/
    				
    				
    				
    				var range = editor.createRange();
    				range.moveToPosition( table, CKEDITOR.POSITION_BEFORE_START ); 
    				table.remove();
    				range.select();
    				var formUuid = $(table).attr("formUuid");
    				if(typeof formUuid != "undefined"){//从表
    					//从JSON定义中删除从表的定义信息
    					delete formDefinition.subforms[formUuid];
    				}
    			}
    		}));

            
            //定义一个按钮,用于触发打开"设置表单"对话框的命令
            a.ui.addButton(pluginName, {
                label: "容器",//调用dialog时显示的名称
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
   						  window.setTimeout(function(){ 
   							 dyform.initPropertyDialog(a);//重新初始化属性窗口 
 						 }, 300); 
 						
					}*/
        		}
           );
            
          
            
        }
    });
})();