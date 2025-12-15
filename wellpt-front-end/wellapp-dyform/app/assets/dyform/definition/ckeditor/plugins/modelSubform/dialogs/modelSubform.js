(function() {
	if(typeof CkPlugin == "undefined"){
		return;
	}
	var pluginName = CkPlugin.MODELSUBFORM; 
	
    CKEDITOR.dialog.add(pluginName, 
    function(editor) { 
    	 
		var containerID = "container_" + pluginName;
        return {
            title: "从表设置",
            minHeight:100,
            minWidth:300,
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
                    	html:"<div id='" + containerID + "'>从表设置</div>",
                    	//html:"<iframe  id=container_" + pluginName + " src='" + ctx +"/resources/ckeditor4.1/plugins/dysubform/index.html' />",
                    	onLoad:function(){  }
					} 
                ]
            }],
            onOk: function() {
            	modelSub.fillCkeditor();
            	modelSub.exitDialog();
            },
            onCancel: function(){//退出窗口时清空属性窗口的缓存 
            	if( (typeof modelSub.exitDialog) != "undefined"){
            		modelSub.exitDialog(); 
            	}
            } ,
            onShow:function(){
            	var focusedDomPropertyUrl = editor.focusedDomPropertyUrl4ModelSub;  
	        	//以GET方式(不得以POST方式)加载属性窗口的html
	        	$("#" + containerID).load(focusedDomPropertyUrl, function(){  
	        		modelSub.initPropertyDialog(editor);//初始化属性窗口
	        	});
            }
        };
        
    });
     
})();

 



