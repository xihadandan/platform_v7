(function() {
	if(typeof CkPlugin == "undefined"){
		return;
	}
	var pluginName = CkPlugin.BUTTON; 
	
    CKEDITOR.dialog.add(pluginName, 
    function(editor) { 
    	 
		var containerID = "container_" + pluginName;
        return {
            title: "按钮设置",
            minHeight:100,
            minWidth:300,
            contents: [{
                id: "setTitle",
                label: "label",
                title: "Rtitle",
                expand: true,
                padding: 0,
                elements: [
					{	id:"setForm",
                    	type:"html",
                    	style: "width: 100%;",
                    	html:"<div id='" + containerID + "'>按钮设置</div>",
                    	//html:"<iframe  id=container_" + pluginName + " src='" + ctx +"/resources/ckeditor4.1/plugins/dysubform/index.html' />",
                    	onLoad:function(){
//					        window.setTimeout(function(){
//					        	var focusedDomPropertyUrl = editor.focusedDomPropertyUrl4Btn;
//					        	//以GET方式(不得以POST方式)加载属性窗口的html
//					        	$("#" + containerID).load(focusedDomPropertyUrl, function(){
//					        		custombtn.initPropertyDialog(editor);//初始化属性窗口
//					        	});
//					        }, 100); 
                    	}
					} 
                ]
            }],
            onOk: function() {
            	custombtn.fillCkeditor();  
            	 
            	custombtn.exitDialog();
            },
            onCancel: function(){//退出窗口时清空属性窗口的缓存 
            	 
            	if( (typeof custombtn.exitDialog) != "undefined"){
            		custombtn.exitDialog(); 
            	}
            } ,
            onShow:function(){
            	var focusedDomPropertyUrl = editor.focusedDomPropertyUrl4Btn;
            	$("#" + containerID).load(focusedDomPropertyUrl, function(){
            		custombtn.initPropertyDialog(editor);//初始化属性窗口
            		$.ControlConfigUtil.enableFocus(editor, CKEDITOR.dialog._.currentTop, containerID);
	        	});
            }
        };
        
    });
     
})();

 



