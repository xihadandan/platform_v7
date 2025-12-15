define([ "mui", "constant", "commons", "server", "mui-DyformField" ], function($, constant, commons, server,
		DyformField) {
	// 资源选择
	var wTimeEmploy = function($placeHolder, options) {
		DyformField.apply(this, arguments);
	};
	commons.inherit(wTimeEmploy, DyformField, {
		init : function() {
			var self = this;
			// json解码
			if(self.getValue() ){
				var jsonV = JSON.parse( self.getValue() );
				if( jsonV ){
					var v = jsonV["employName"] + "(" + jsonV["beginTime"] + "至" +  jsonV["endTime"] + ")";
					self.value = v;	
				}
			}
			self._superApply();
		}
	});
	return wTimeEmploy;
});