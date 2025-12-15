(function() {
	 
	
	
	var pluginName =   CkPlugin.PROPERTIESDIALOGS;
	 
    CKEDITOR.dialog.add(pluginName, 
    function(editor) { 
    	var widthHeight = getWidthAndHeight();
    	
    	
    	
		var containerID = "container_" + pluginName;
        return {
            title: "字段属性列表窗口",
            minHeight:widthHeight.height-160,
            minWidth:widthHeight.width-160,
            contents: [{
                id: "propertieslist",
                label: "label",
                title: "title",
                expand: true,
                padding: 0,
                elements: [
					{	id:"table_html",
                    	type:"html",
                    	style: "width: 100%;",
                    	html:"<div id='" + containerID + "'>字段属属性列表</div>",
                    	//html:"<iframe  id=container_" + pluginName + " src='" + ctx +"/resources/ckeditor4.1/plugins/dysubform/index.html' />",
                    	onLoad:function(){
					        /*window.setTimeout(function(){
					        	var propertiesDialogURL = editor.propertiesDialogURL; 
					        	 
					        	//以GET方式(不得以POST方式)加载属性窗口的html
					        	$("#" + containerID).load(propertiesDialogURL, function(){ 
					        		propertiesDialog.init(editor);//初始化属性窗口
					        	}); 
					        }, 50); */
                    	}
					}
                ]
            }],
            onOk: function() {
            	propertiesDialog.collectData();
            },
            onCancel: function(){//退出窗口时清空属性窗口的缓存 
            	 
            } ,
            onShow:function(){
            	var propertiesDialogURL = editor.propertiesDialogURL; 
	        	 
	        	//以GET方式(不得以POST方式)加载属性窗口的html
	        	$("#" + containerID).load(propertiesDialogURL, function(){ 
	        		propertiesDialog.init(editor);//初始化属性窗口
	        	}); 
            }
        };
    });
})();

