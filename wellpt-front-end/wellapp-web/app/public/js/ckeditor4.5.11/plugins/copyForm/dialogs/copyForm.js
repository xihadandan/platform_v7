(function() {
	if(typeof CkPlugin == "undefined"){
		return;
	}
	var pluginName = CkPlugin.COPYFORM; 
	
    CKEDITOR.dialog.add(pluginName, 
    function(editor) {
		var containerID = "container_" + pluginName;
		var dialogObj = { 
	            title: "模板来源",
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
	                    	html:"<div id='" + containerID + "'>模板来源</div>",
	                    	onLoad:function(){
	                    	}
						} 
	                ]
	            }]
	        };
		
		$.extend(true, dialogObj, dialogMethod);
		
		
        return dialogObj;
        
    });
     
})();

 



