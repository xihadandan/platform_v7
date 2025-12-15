define([ "jquery", "server", "commons", "constant", "appContext", "appModal", "DmsDataServices",
		"DmsDocumentViewDevelopment", "DmsActionDispatcher", "DyformExplain" ], function($, server, commons, constant,
		appContext, appModal, DmsDataServices, DmsDocumentViewDevelopment, DmsActionDispatcher, DyformExplain) {
	// 数据管理单据开发
	var DmsDocumentView = function() {
		DmsDocumentViewDevelopment.apply(this, arguments);
	};
	return DmsDocumentView;
});