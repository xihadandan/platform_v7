(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery', 'commons', 'server', 'constant'], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, commons, server, constant) {
	"use strict";
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	var url = ctx + "/web/dyform/data/viewer?"; 
	$.widget("ui.wDyformDataViewer", $.ui.wWidget, {
		_dataUuid : null,
		$dyform : null,
		$dyformExplain : null,
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {},
			callback : {}
		},
		_createView : function() {
			this._renderView();
		},
		getDataUuid : function(){
			if( this._dataUuid ){
				return this._dataUuid;
			}else{
				var configuration = this.getConfiguration();				
				return configuration.dataUuid; 
			}
		},
		setDataUuid : function( str ){
			this._dataUuid = str;
		},
		
		_renderView : function() {
			var _self = this;
			_self.invokeDevelopmentMethod('beforeRenderView', [ _self.options, _self.getConfiguration() ]);
			var headerHeight = $(".ui-wHeader") ? $(".ui-wHeader").height() : 0;
			var footerHeight = $(".ui-wFooter") ? $(".ui-wFooter").height() : 0;
			var iframeHeight = window.innerHeight - 10 - headerHeight - footerHeight;
			var configuration = _self.getConfiguration();
			var isEdit = configuration.isEdit;
			var dataUuid = this.getDataUuid();
			var $iframe = $("<iframe>", {
				"src" : url + "formUuid=" + configuration.formUuid + "&dataUuid=" + dataUuid + "&isEdit=" + isEdit,
				"style" : "width:100%; overflow-x: hidden;border: 0px;",
				"height" : iframeHeight + "px",
			});
			$(_self.element).empty().append($iframe);

			appModal.showMask();
			$iframe.load(function(){
				console.log("----------------表单加载完毕-----------");
				$("title").text( configuration.name );
				_self.$dyform = $iframe.contents().find("#dyform");
				//将后台定义的class，配置上去
				$iframe.contents().find(".container-fluid").first().addClass( configuration.customClass );
			});
		},
	});
}));