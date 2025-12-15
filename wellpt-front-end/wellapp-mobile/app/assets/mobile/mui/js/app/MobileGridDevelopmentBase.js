define([ "commons", "constant", "server" ], function(commons, constant, server) {
	var MobileGridDevelopment = function(wWidget) {
		this.wWidget = wWidget;
	};
	/**
	 * 渲染前回调方法，子类可覆盖
	 */
	MobileGridDevelopment.prototype.beforeRender = function(options, configuration) {
	};
	/**
	 * 渲染后回调方法，子类可覆盖
	 */
	MobileGridDevelopment.prototype.afterRender = function(options, configuration) {
	};
	
	/**
	 * 
	 */
	MobileGridDevelopment.prototype.onload = function(options, configuration) {
	};
	return MobileGridDevelopment;
});