define([ "jquery", "constant", "commons", "appContext", "HtmlWidgetDevelopment" ], function($, constant, commons, appContext, HtmlWidgetDevelopment) {
	//处理首页头部信息展示部分的二开js
	var indexHtmlWidgetDevelopment = function() {
		HtmlWidgetDevelopment.apply(this, arguments);
	};
	commons.inherit(indexHtmlWidgetDevelopment, HtmlWidgetDevelopment, {
		init : function() {
			var htmlBox = this.getWidget().getRenderPlaceholder();
			var _self = this.getWidget();
			var bootgrid = htmlBox.parent().parent();
			var BootstrapTableViewGetCount = "BootstrapTableViewGetCount";
			var tableId = bootgrid.find('.ui-wBootstrapTable').attr('id');
			var pageContainer = this.getWidget().pageContainer
			pageContainer.onWidgetCreated(tableId, function(e, listViewWidget) {
				var changeCount = function(e, ui) {
					if (ui.getDataProvider()) {
						var totalCount = ui.getDataProvider().getCount();
						if (totalCount != 0) {
							htmlBox.find(".new-count").text(totalCount);
						} else {
							htmlBox.find(".new-count").text(0);
						}
					}
				};
				changeCount(e, listViewWidget);
				var freshCallBack = function(e, ui) {
					changeCount(e, ui);
					ui.off(constant.WIDGET_EVENT.Refresh, freshCallBack);
				}
				listViewWidget.on(constant.WIDGET_EVENT.Refresh, freshCallBack);
				listViewWidget.on(constant.WIDGET_EVENT.Change, changeCount);
			});
			pageContainer.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function() {
				var widget = appContext.getWidgetById(tableId);
				if (widget != null) {
					return;
				}
				var data = {
					bootstrapView : tableId,
					widgetDefId : tableId
				};
				_self.startApp({
					isJsModule : true,
					jsModule : BootstrapTableViewGetCount,
					action : "getCount",
					data : data,
					callback : function(count, realCount, options) {
						htmlBox.find(".new-count").text(realCount);
					}
				});
			});
		}
	});
	return indexHtmlWidgetDevelopment;
})