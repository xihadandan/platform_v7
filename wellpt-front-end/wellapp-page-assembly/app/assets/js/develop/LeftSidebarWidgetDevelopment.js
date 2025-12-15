define([ "constant", "commons", "server", "WidgetDevelopment" ],
		function(constant, commons, server, WidgetDevelopment) {
			// 页面组件二开基础
			var LeftSidebarWidgetDevelopment = function() {
				WidgetDevelopment.apply(this, arguments);
			};
			// 接口方法
			commons.inherit(LeftSidebarWidgetDevelopment, WidgetDevelopment, {

			});
			//菜单重载后的触发事件
			LeftSidebarWidgetDevelopment.prototype.afterReloadMenuItem = function(event, param) {
			};
			//组件渲染完后触发
			LeftSidebarWidgetDevelopment.prototype.afterRenderView = function(options) {
			};
			//刷新组件
			LeftSidebarWidgetDevelopment.prototype.refresh = function() {
				this.getWidget().refresh();
			};
			//通过menuId获取对应的menuItem
			LeftSidebarWidgetDevelopment.prototype.getMenuItem = function( menuId) {
				return this.getWidget().menuItemMap[menuId];
			};
			
			return LeftSidebarWidgetDevelopment;
		});