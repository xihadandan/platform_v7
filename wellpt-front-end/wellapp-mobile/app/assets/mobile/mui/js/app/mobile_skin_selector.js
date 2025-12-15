define([ "mui", "commons", "constant", "server", "WorkView", "WorkViewProxy","formBuilder" ], function($, commons, constant, server, WorkView, WorkViewProxy, formBuilder) {
	var browser = commons.Browser;
	var MobileNoticeDevelopment = function(wWidget) {
		this.wWidget = wWidget;
	};
	/**
	 * 渲染前回调方法，子类可覆盖
	 */
	MobileNoticeDevelopment.prototype.beforeRender = function(options, configuration) {
		console.log("beforeRender");
	};
	/**
	 * 渲染后回调方法，子类可覆盖
	 */
	MobileNoticeDevelopment.prototype.afterRender = function(options, configuration) {
		
		// console.log("afterRender");
	};
	return MobileNoticeDevelopment;
});