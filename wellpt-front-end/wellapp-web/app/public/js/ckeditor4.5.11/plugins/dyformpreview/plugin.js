(function() {
	
	  
	var pluginName =  CkPlugin.PREVIEW; 
	 
	var formpreview = {
			 exec:function(editor){
				 if(!collectFormDatas()) return;
				 
				 formPreview(formDefinition); 
			 }};
	
	 
	 
	 CKEDITOR.plugins.add(pluginName, {
	        init: function(a) {
	            a.addCommand(pluginName, formpreview);
	            a.ui.addButton(pluginName, {
	                label: "预览",//调用dialog时显示的名称
	                icon: this.path + "images/preview.png",//在toolbar中的图标
	                command: pluginName
	            });
	        }
	    });
	 
	 
	 
})();


 


