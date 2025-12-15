(function() {
	if(typeof CkPlugin == "undefined"){
		return;
	}
	var pluginName =   CkPlugin.LABEL; 
	
    CKEDITOR.dialog.add(pluginName, 
    function(editor) { 
    	 
		var containerID = "container_" + pluginName;
        return {
            title: "设置标题",
            minHeight:180,
            minWidth:320,
            contents: [{
                id: "setTitle",
                label: "label",
                title: "title",
                expand: true,
                padding: 0,
                elements: [
					{	id:"setTitleDiv",
                    	type:"html",
                    	style: "width: 100%;",
                    	html:"<div id='" + containerID + "'>设置标题</div>",
                    	//html:"<iframe  id=container_" + pluginName + " src='" + ctx +"/resources/ckeditor4.1/plugins/dysubform/index.html' />",
                    	onLoad:function(){  
                    	}
					} 
                ]
            }],
            onOk: function() {
            	label.fillCkeditor();  
            	 
            	label.exitDialog();
            },
            onCancel: function(){//退出窗口时清空属性窗口的缓存 
            	if( (typeof label.exitDialog) != "undefined"){
            		label.exitDialog(); 
            	}
            } ,
            onShow:function(){
            	var focusedDomPropertyUrl = editor.focusedDomPropertyUrl4Label;  
	        	//以GET方式(不得以POST方式)加载属性窗口的html
	        	$("#" + containerID).load(focusedDomPropertyUrl, function(){
	        		label.initPropertyDialog(editor);//初始化属性窗口
	        		$.ControlConfigUtil.enableFocus(editor, CKEDITOR.dialog._.currentTop, containerID);
	        	});
            }
        };
        
    });
     
})();

 



