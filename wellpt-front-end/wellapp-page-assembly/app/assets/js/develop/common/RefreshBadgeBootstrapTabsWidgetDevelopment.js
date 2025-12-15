define([ "constant", "commons", "server", "BootstrapTabsWidgetDevelopment" ], function(constant, commons, server,
                                                                                       BootstrapTabsWidgetDevelopment) {
	// 平台组件_公共功能_tab组件刷新徽章数量_视图组件二开
	var RefreshBadgeBootstrapTabsWidgetDevelopment = function() {
        BootstrapTabsWidgetDevelopment.apply(this, arguments);
	};

 	commons.inherit(RefreshBadgeBootstrapTabsWidgetDevelopment, BootstrapTabsWidgetDevelopment, {
        afterRender : function( options, configuration){
            this.getPageContainer().trigger(constant.WIDGET_EVENT.PageContainerCreationComplete);
        },
	});
	return RefreshBadgeBootstrapTabsWidgetDevelopment;
});