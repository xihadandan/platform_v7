define([ "jquery", "commons", "constant", "server", "ViewDevelopmentBase" ], function($, commons, constant, server, ViewDevelopmentBase) {
	var ViewDevelopmentTest = function() {
		ViewDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(ViewDevelopmentTest, ViewDevelopmentBase, {
		beforeRender : function(options) {
			this.addParam('SOME', "SOME");
		},
		add : function(event, option, row) {
			console.log(this.getViewConfiguration());
			console.log(this.getDataProvider().getDefinitionJson());
			// this.wWidget.$tableElement.bootstrapTable("nextPage");
			this.addOtherConditions([ {
				columnIndex : 'ID',
				value : [ 'U0010000001', 'U0010000013' ],
				type : 'in'
			} ]);
			this.refresh();
		},
		modify : function(event, options) {
			this.wWidget.$tableElement.bootstrapTable("selectPage", 1);
			// alert(JSON.stringify(this.getSelectionIndexes()));
		},
		onClickRow : function(row) {
		},
		onDblClickRow : function(row) {

		}
	});

	return ViewDevelopmentTest;
});