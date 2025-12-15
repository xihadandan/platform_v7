define([ "mui", "commons", "constant", "server", "formBuilder", "MobileListDevelopmentBase", "DmsListViewActionBase", "DmsDataServices" ], function
		($, commons, constant, server, formBuilder, MobileListDevelopmentBase, DmsListViewActionBase, DmsDataServices) {
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	var ACTION_OPEN_VIEW = "open_view";
	var ROW_CHECK_ITEM = "rowCheckItem";
	var MobileDmsListViewDevelopment = function() {
		MobileListDevelopmentBase.apply(this, arguments);
		var options = {
			ui : this.getWidget()
		};
		this.dmsListViewActionBase = new DmsListViewActionBase(options);
		this.dmsDataServices = new DmsDataServices();
	};

	commons.inherit(MobileDmsListViewDevelopment, MobileListDevelopmentBase, {
		beforeRender : function() {
			var _self = this;
			var configuration = _self.getViewConfiguration();
			if (configuration.dmsWidgetDefinition) {
				var dataProvider = _self.getDataProvider();
				if (dataProvider.options.proxy == null || dataProvider.options.proxy.type == null) {
					dataProvider.options.proxy = dataProvider.options.proxy || {};
					dataProvider.options.proxy.type = "com.wellsoft.pt.dms.core.criteria.DmsDyformCriteria";
					dataProvider.options.proxy.extras = {
						dms_id : configuration.dmsWidgetDefinition.id
					};
				}
			}
		},
		onClickRow : function(rowNum, row, $element, field) {
			var _self = this;
			// 选中复选框，不处理
			if (ROW_CHECK_ITEM === field) {
				return;
			}
			var widget = _self.getWidget();
			// 功能ID参数
			var paramOptions = {
				appFunction : {
					id : ACTION_OPEN_VIEW
				},
				// 行数据
				rowdata : row
			};
			var urlParams = _self.dmsListViewActionBase.getUrlParams(paramOptions);
			_self.dmsDataServices.openWindow({
				urlParams : urlParams,
				ui : widget
			});
		}
	});
	return MobileDmsListViewDevelopment;
});