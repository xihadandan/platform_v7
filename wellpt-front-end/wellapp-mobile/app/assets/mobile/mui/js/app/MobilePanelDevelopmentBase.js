define([ "commons", "constant", "server" ], function(commons, constant, server) {
	var MobilePanelDevelopment = function(wWidget) {
		this.wWidget = wWidget;
	};
	/**
	 * 渲染前回调方法，子类可覆盖
	 */
	MobilePanelDevelopment.prototype.beforeRender = function(options, configuration) {
	};
	/**
	 * 渲染后回调方法，子类可覆盖
	 */
	MobilePanelDevelopment.prototype.afterRender = function(options, configuration) {
	};
	
	/**
	 * 
	 */
	MobilePanelDevelopment.prototype.onload = function(options, configuration) {
	};
	return MobilePanelDevelopment;
});