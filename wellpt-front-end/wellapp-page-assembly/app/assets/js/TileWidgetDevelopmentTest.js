define([ "jquery", "commons", "constant", "server", "TileWidgetDevelopment" ], function($, commons, constant, server,
		TileWidgetDevelopment) {
	var TileDevelopmentTest = function() {
		TileWidgetDevelopment.apply(this, arguments);
	};
	commons.inherit(TileDevelopmentTest, TileWidgetDevelopment, {
		some6 : function() {
			alert(2);
		},
		beforeRender : function(options, configuration) {
			console.log("beforeRender")
		},
		afterRender : function(options, configuration) {
			console.log("afterRender")
		}
	});
	return TileDevelopmentTest;
});