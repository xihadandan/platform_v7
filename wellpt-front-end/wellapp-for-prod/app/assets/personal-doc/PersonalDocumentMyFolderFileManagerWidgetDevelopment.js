define(
		[ "constant", "commons", "server", "appModal", "PersonalDocumentFileManagerWidgetDevelopment", "DmsTagFragment" ],
		function(constant, commons, server, appModal, PersonalDocumentFileManagerWidgetDevelopment, DmsTagFragment) {
			// 页面组件二开基础
			var PersonalDocumentMyFolderFileManagerWidgetDevelopment = function() {
				PersonalDocumentFileManagerWidgetDevelopment.apply(this, arguments);
			};
			var dmsTagFragment = new DmsTagFragment();
			// 接口方法
			commons.inherit(PersonalDocumentMyFolderFileManagerWidgetDevelopment,
					PersonalDocumentFileManagerWidgetDevelopment, dmsTagFragment.extend({
						// 将文件管理的归属夹调整为我的文件夹
						prepare : function() {
							var _self = this;
							// 调用父类提交方法
							_self._superApply(arguments);

							var widget = _self.getWidget();
							// 事件监听
							$(widget.element).on("wFileManager.ItemClick", function(e, ui) {
								var item = e.detail.item;
								if (widget.isSupportOnlinePreview(item)) {
									_self.recordRecentVisitFile(item);
								} else {
									appModal.toast("该文件不支持在线预览");
								}
							});
						},
						// 内容展示完回调
						onPostBody : function() {
							// 调用DmsTagFragment的markBodyTags进行打标签
							this.markBodyTags();
						},
						// 添加标记为按钮
						beforeUpdateToolbar : function(fileActions) {
							// 调用DmsTagFragment的appendTagToolbar生成标记为按钮
							this.appendTagToolbar(fileActions);
						}
					}));
			return PersonalDocumentMyFolderFileManagerWidgetDevelopment;
		});