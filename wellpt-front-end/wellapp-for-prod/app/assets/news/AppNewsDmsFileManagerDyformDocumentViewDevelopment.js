define([ "jquery", "server", "commons", "constant", "appContext", "appModal", "DmsFileManagerDyformDocumentView" ],
		function($, server, commons, constant, appContext, appModal, DmsFileManagerDyformDocumentView) {
			var StringBuilder = commons.StringBuilder;
			// 平台应用_新闻_文件库表单二开
			var AppNewsDmsFileManagerDyformDocumentViewDevelopment = function() {
				DmsFileManagerDyformDocumentView.apply(this, arguments);
			};
			commons.inherit(AppNewsDmsFileManagerDyformDocumentViewDevelopment, DmsFileManagerDyformDocumentView, {
				// 表单初始化成功处理
				onInitDyformSuccess : function() {
					var _self = this;
					var dyform = _self.getDyform();
					// 是否显示为文本
					if (dyform.isDisplayAsLabel()) {
						// 清空新闻内容的title属性
						$("span[name='news_content']", _self.getDyformSelector()).title("");
					}
				}
			});
			return AppNewsDmsFileManagerDyformDocumentViewDevelopment;
		});