define([ "constant", "commons", "server", "ListViewWidgetDevelopment", "DmsTagFragment" ], function(constant, commons,
		server, ListViewWidgetDevelopment, DmsTagFragment) {
	var JDS = server.JDS;
	// 标签管理二开
	var PersonalDocumentMyTagManagerListViewWidgetDevelopment = function() {
		ListViewWidgetDevelopment.apply(this, arguments);
	};
	var dmsTagFragment = new DmsTagFragment();
	var prototype = dmsTagFragment.extend({
		prepare : function() {
		},
		onPostBody : function() {
			var _self = this;
			var widget = _self.getWidget();
			$("input[name='tag-color']", widget.element).each(function() {
				_self.initTagColor($(this));
			});
		},
		onCreateTagSuccess : function() {
			this.getWidget().refresh();
		},
		getSelection : function() {
			return this.getSelections();
		}
	});
	// 接口方法
	commons.inherit(PersonalDocumentMyTagManagerListViewWidgetDevelopment, ListViewWidgetDevelopment, prototype);
	return PersonalDocumentMyTagManagerListViewWidgetDevelopment;
});