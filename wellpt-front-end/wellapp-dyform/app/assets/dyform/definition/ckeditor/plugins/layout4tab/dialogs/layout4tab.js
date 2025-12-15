(function() {
	if(typeof CkPlugin == "undefined"){
		return;
	}
	var pluginName = CkPlugin.TABS; 
	
    CKEDITOR.dialog.add(pluginName, 
    function(editor) {
		var containerID = "container_" + pluginName;
        return {
            title: "设置页签属性",
            minHeight:100,
            minWidth:800,
            contents: [{
                id: "setTitle",
                label: "label",
                title: "title",
                expand: true,
                padding:" 10px 0 0 0",
                elements: [
					{	id:"setTabProperty",
                    	type:"html",
                    	style: "width: 100%;",
                    	html:"<div id='" + containerID + "'>设置页签属性</div>", 
                    	onLoad:function(){
					        
                    	}
					} 
                ]
            }],
            onOk: function() {
            	if(tabP.collectAndFill() == false){
            		return false;
            	}
            	tabP.exitDialog();
            },
            onCancel: function(){//退出窗口时清空属性窗口的缓存 
            	if( (typeof tabP.exitDialog) != "undefined"){
            		tabP.exitDialog(); 
            	}
            } ,
            onShow:function(){
            	var focusedDomPropertyUrl = editor.focusedDomPropertyUrl4Tbl;  
	        	//以GET方式(不得以POST方式)加载属性窗口的html
	        	$("#" + containerID).load(focusedDomPropertyUrl, function(){  
	        		tabP.initPropertyDialog(editor);//初始化属性窗口
	        		$.ControlConfigUtil.enableFocus(editor, CKEDITOR.dialog._.currentTop, containerID);
	        	}); 
	        	
           	 /*
            	if(editor.focusedDom != null && (typeof editor.focusedDom != "undefined")){
            		//通过双击ckeditor中的从表元素,表示修改从表属性,则不需要做焦点位置判断
            		return;
            	}*/
            }
        };
        
    });
     
})();

 



