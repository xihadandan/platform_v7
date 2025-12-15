(function() {
	if(typeof CkPlugin == "undefined"){
		return;
	}
	var pluginName = CkPlugin.TEMPLATECONTAINER; 
	
    CKEDITOR.dialog.add(pluginName, 
    function(editor) { 
    	 
		var containerID = "container_" + pluginName;
        return {
            title: "选择子表单",
            minHeight:210,
            minWidth:320,
            buttons: [CKEDITOR.dialog.okButton, {
                type: 'button',
                id: 'deleteTemplate',
                label: '删除子表单',
                onClick: function () {
                	var templateUuid = editor.focusedDom.getAttribute("templateUuid");
                	formDefinition.deleteTemplate(templateUuid);
                	// 清空document
                	editor.focusedDom.remove();
                	templatecontainer.exitDialog();
                	// 退出dailog
                	CKEDITOR.dialog.getCurrent().hide();
                }
            },
            CKEDITOR.dialog.cancelButton,],
            contents: [{
                id: "setTitle",
                label: "label",
                title: "title",
                expand: true,
                padding: 0,
                elements: [
					{	id:"setForm",
                    	type:"html",
                    	style: "width: 100%;",
                    	html:"<div id='" + containerID + "'>选择子表单</div>",
                    	//html:"<iframe  id=container_" + pluginName + " src='" + ctx +"/resources/ckeditor4.1/plugins/dysubform/index.html' />",
                    	onLoad:function(){ 
                    		
                    	}
					} 
                ]
            }],
            onOk: function() {
            	if(!templatecontainer.fillCkeditor()){
            		return false;
            	}
            	templatecontainer.exitDialog();
            },
            onCancel: function(){//退出窗口时清空属性窗口的缓存 
            	if( (typeof templatecontainer.exitDialog) != "undefined"){
            		templatecontainer.exitDialog(); 
            	}
            } ,
            onShow:function(){
            	var focusedDomPropertyUrl = editor.templatePropertyUrl4Form;  
	        	$("#" + containerID).load(focusedDomPropertyUrl, function(){
	        		if(editor.focusedDom && editor.focusedDom.getAttribute("templateUuid")){
	        			CKEDITOR.dialog.getCurrent().getButton("deleteTemplate").getElement().show();
	        		}else {
	        			CKEDITOR.dialog.getCurrent().getButton("deleteTemplate").getElement().hide();
	        		}
	        		templatecontainer.initPropertyDialog(editor);//初始化属性窗口
	        		$.ControlConfigUtil.enableFocus(editor, CKEDITOR.dialog._.currentTop, containerID);
	        	}); 
            }
        };
        
    });
     
})();

 



