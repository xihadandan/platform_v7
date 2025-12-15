define([ "constant", "commons", "server", "WidgetDevelopment" ],
		function(constant, commons, server, WidgetDevelopment) {
			// 页面组件二开基础
			var FileManagerWidgetDevelopment = function() {
				WidgetDevelopment.apply(this, arguments);
			};
			// 接口方法
			commons.inherit(FileManagerWidgetDevelopment, WidgetDevelopment, {
				// 加载数据前回调
				beforeLoadData : function() {
				},
				// 内容展示完回调
				onPostBody : function() {
				},
				// 更新按钮前回调处理
				beforeUpdateToolbar : function(actions) {
				},
				// 获取选择的数据
				getSelection : function() {
					return this.getWidget().getSelection();
				},
				// 获取所有数据
				getData : function() {
					return this.getWidget().getData();
				},
				// 数据项单击
				onItemClick : function(index, item, $element) {
				},
				// 点击排序，列表视图有效
				onSort : function(name, order) {
				},
				// 获取数据管理文件服务
				getFileServices : function() {
					return this.getWidget().dmsFileServices;
				}
			});
			return FileManagerWidgetDevelopment;
		});