define([ "constant", "commons", "server", "WidgetDevelopment" ],
		function(constant, commons, server, WidgetDevelopment) {
			// 页面组件二开基础
			var DyformDataViewerWidgetDevelopment = function() {
				WidgetDevelopment.apply(this, arguments);
			};
			// 接口方法
			commons.inherit(DyformDataViewerWidgetDevelopment, WidgetDevelopment,  {
				//表单渲染前
				beforeRenderView : function( options, configuration){
					
					console.log("WidgetDevelopment.beforeRenderView=========");
				}
			});
			return DyformDataViewerWidgetDevelopment;
		});