(function() {
	 
	var pluginName =  CkPlugin.COPYFORM; 
	
	 
	 CKEDITOR.plugins.add(pluginName, {
	        init: function(a) {
	        	a.createPlugin(pluginName, "模板来源", true);
	        }
	    });
})();


 


