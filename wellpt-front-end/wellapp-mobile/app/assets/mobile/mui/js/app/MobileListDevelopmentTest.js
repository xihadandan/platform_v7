define([ "commons", "constant", "server", "MobileListDevelopmentBase" ], function(commons, constant, server, MobileListDevelopmentBase) {
	var MobileListDevelopmentTest = function() {
		MobileListDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(MobileListDevelopmentTest, MobileListDevelopmentBase, {
		beforeRender : function() {
			console.log('beforeRender');
		},
		afterRender : function() {
			console.log('afterRender');
		},
		onClickRow : function(index, data, $element, event) {
			//this.setPullupStatus('done');
		},
		onAddLi : function(index, data, $element) {
			// console.log(data);
		},
		onPulldown : function(event) {
			console.log('onPulldown');
			// event.preventDefault()
		}
	});

	return MobileListDevelopmentTest;
});