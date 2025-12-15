define(
		[ "constant", "commons", "server", "appContext", "appModal" ],
		function(constant, commons, server, appContext, appModal) {
			var JDS = server.JDS;
			var StringUtils = commons.StringUtils;
			var StringBuilder = commons.StringBuilder;
			var UUID = commons.UUID;
			var swatches = "#90caf9|#ef9a9a|#a5d6a7|#fff59d|#ffcc80|#bcaaa4|#eeeeee|#f44336|#2196f3|#4caf50|#ffeb3b|#ff9800|#795548|#9e9e9e";
			var defaultValue = '#90caf9';
			var tagTpl = "<div class='pull-right dms-tag-div' style='background:{2};'><span class='dms-tag' dataUuid='{0}' tagUuid='{1}' title='{3}'>{3}</span></div>";
			// 显示创建标签弹出框
			function showTagDialog(tagOptions, callback) {
				var dlgId = UUID.createUUID();
				var dlgIdSelector = '#' + dlgId;
				var msg = new StringBuilder();
				msg.appendFormat('<div id="{0}" class="tag-dialog row">', dlgId);
				msg.append('<div class="form-group">');
				msg.append('<div class="col-xs-2">');
				msg.append('</div>');
				msg.append('<div class="col-xs-10">');
				msg.append('<label>请输入标签名称</label>');
				msg.append('</div>');
				msg.append('</div>');
				msg.append('<div class="form-group">');
				msg.append('<div class="col-xs-2">');
				msg.append('<input type="hidden" class="form-control" name="tag-color" value=""/>');
				msg.append('</div>');
				msg.append('<div class="col-xs-10">');
				msg.append('<input type="text" class="form-control" name="tag-name" value=""/>');
				msg.append('</div>');
				msg.append('</div>');
				msg.append('</div>');
				var options = {
					title : tagOptions.title,
					message : msg.toString(),
					size : "small",
					shown : function() {
						var tagName = tagOptions.tagName || "";
						var tagColor = tagOptions.tagColor || defaultValue;
						var $tagNameElem = $("input[name='tag-name']", dlgIdSelector);
						$tagNameElem.val(tagName);
						// 判断是否显示颜色选择器
						if (tagOptions.showMinicolors === false) {
							$(".col-xs-2", dlgIdSelector).removeClass("col-xs-2").addClass("col-xs-0");
							$(".col-xs-10", dlgIdSelector).removeClass("col-xs-10").addClass("col-xs-12");
							return;
						}
						var $tagColorElem = $("input[name='tag-color']", dlgIdSelector);
						initMinicolors($tagColorElem, {
							defaultValue : tagColor
						});
					},
					buttons : {
						confirm : {
							label : "确定",
							className : "btn-primary",
							callback : function() {
								var $tagColorElem = $("input[name='tag-color']", dlgIdSelector);
								var $tagNameElem = $("input[name='tag-name']", dlgIdSelector);
								var tagColor = $tagColorElem.val();
								var tagName = $tagNameElem.val();
								if (StringUtils.isBlank(tagName)) {
									appModal.error("标签名称不能为空，请输入名称！");
									return false;
								}
								if ($.isFunction(callback)) {
									return callback.call(appModal, tagName, tagColor);
								}
								return true;
							}
						},
						cancel : {
							label : "取消",
							className : "btn-default",
							callback : function() {
							}
						}
					}
				};
				appModal.dialog(options);
			}

			function initMinicolors($element, options) {
				var opt = $.extend({
					control : 'hue',
					format : 'hex',
					position : 'bottom left',
					letterCase : 'lowercase',
					swatches : swatches.split("|")
				}, options)
				$($element).minicolors(opt);
			}

			// 数据标签功能片段
			var DmsTagFragment = function() {
			};
			$.extend(DmsTagFragment.prototype, {
				extend : function(prototype) {
					var dmsTagPrototype = {
						initTagColor : function($tagColor) {
							var _self = this;
							var dv = $tagColor.val() || defaultValue;
							$tagColor.closest("td").css({
								overflow : "visible"
							});
							$tagColor.closest(".fixed-table-body").css({
								overflow : "visible"
							});
							initMinicolors($tagColor, {
								defaultValue : dv,
								change : function() {
									$tagColor.data("change", true);
								},
								hide : function() {
									if ($tagColor.data("change") != true) {
										return;
									}
									if ($tagColor.data("updateSuccess")) {
										return;
									}
									var $tr = $tagColor.closest("tr");
									var index = parseInt($tr.attr("data-index"));
									var dataArray = _self.getData();
									var tagData = null;
									if (dataArray.length > index) {
										tagData = dataArray[index];
									}
									if (tagData == null) {
										return;
									}
									JDS.call({
										service : "dmsTagFacadeService.updateTagColor",
										data : [ tagData.UUID, $tagColor.val() ],
										async : false,
										success : function(result) {
											$tagColor.data("updateSuccess", true);
											appModal.info("标签更新成功！");
											_self.getWidget().refresh();
											// 重新加载左导航的标签数据
											_self.reloadMenuItem();
										}
									});
								}
							})
						},
						// 标题打标签
						markBodyTags : function() {
							var _self = this;
							var data = _self.getData();
							if (data.length == 0) {
								return;
							}
							var dataUuids = [];
							$.each(data, function() {
								dataUuids.push(this.uuid);
							});
							JDS.call({
								service : "dmsTagFacadeService.queryDataTags",
								data : [ dataUuids ],
								async : false,
								success : function(result) {
									_self._markBodyTags(data, result.data);
								}
							});
						},
						_markBodyTags : function(dataArray, dataTags) {
							var _self = this;
							var widget = _self.getWidget();
							$.each(dataArray, function(index, data) {
								var dataUuid = data.uuid;
								var tags = dataTags[dataUuid];
								var trSelector = "tr[data-index='" + index + "']," + "div[data-index='" + index + "']";
								var tagsHtml = new StringBuilder();
								$.each(tags, function() {
									tagsHtml.appendFormat(tagTpl, dataUuid, this.tagUuid, this.tagColor, this.tagName);
								});
								var $tr = $(trSelector, widget.element);
								$tr.find("td").attr("title", data.name);
								tagsHtml = tagsHtml.toString();
								$tr.find(".dms-file-tags").append(tagsHtml);
								$tr.find(".dms-file-tags").closest(".file-box").data("tagsHtml", tagsHtml);
								// 鼠标移入
								$tr.on("mouseenter", ".dms-file-tags .dms-tag-div", function(e) {
									console.log(e.type);
									var $tagDiv = $(this);
									var $tag = $tagDiv.find(".dms-tag");
									console.log($tag.data("show-remove-btn"));
									if ($tag.data("show-remove-btn")) {
										return;
									}
									$tag.data("show-remove-btn", true);
									// 添加动画效果
									var $tagRemove = $('<b class="glyphicon glyphicon-remove"></b>');
									$tagRemove.hide();
									if($tagDiv.find(".glyphicon-remove").length == 0){
										$tagDiv.append($tagRemove);
									}									
									$tagRemove.addClass("animated fadeInRight");
									$tagRemove.show();
									$tagDiv.find(".glyphicon-remove").one("click", function(e) {
										$(this).remove();
										$tag.data("show-remove-btn", false);
										$tag.remove();
										e.stopPropagation();
										var dataUuid = $tag.attr("dataUuid");
										var tagUuid = $tag.attr("tagUuid");
										_self.untagData(dataUuid, tagUuid);
									});
								});
								// 鼠标移出来
								$tr.on("mouseleave", ".dms-file-tags .dms-tag-div", function() {
									$(this).find(".dms-tag").data("show-remove-btn", false);
									var $tagRemove = $(this).find("b.glyphicon-remove");
									$tagRemove.removeClass("animated fadeOutRight");
									$tagRemove.addClass("animated fadeOutRight");
									// $tagRemove.hide();
									setTimeout(function() {
										$tagRemove.remove();
									}, 500);
								});
							});
						},
						// 取消数据标签
						untagData : function(dataUuid, tagUuid) {
							var _self = this;
							JDS.call({
								service : "dmsTagFacadeService.untagData",
								data : [ [ dataUuid ], tagUuid ],
								async : false,
								success : function(result) {
									_self.getWidget().refresh();
								}
							});
						},
						// 添加标记为按钮
						appendTagToolbar : function(fileActions) {
							var _self = this;
							var selection = _self.getSelection();
							if (selection.length == 0) {
								return;
							}
							var items = [];
							items.push({
								id : "cancelTag",
								name : "取消标签"
							});
							items.push({
								divider : true
							});
							var tagActions = _self.getCurrentUserTagActions();
							$.each(tagActions, function(i, tag) {
								var span = '<span class="glyphicon glyphicon-stop" style="color:' + tag.color + ';">';
								span += '</span>';
								items.push({
									id : tag.uuid,
									action : "tagDataAs",
									name : span + tag.name
								});
							});
							if (tagActions.length > 0) {
								items.push({
									divider : true
								});
							}
							items.push({
								id : "createTag",
								name : "新建标签"
							}, {
								id : "createTagAndMove",
								name : "新建标签并标记"
							});
							fileActions.push({
								name : "标记为...",
								cssClass : "btn-primary",
								items : items
							})
						},
						getCurrentUserTagActions : function() {
							var _self = this;
							if (_self.currentUserTags != null) {
								return _self.currentUserTags;
							}
							JDS.call({
								service : "dmsTagFacadeService.queryCurrentUserTags",
								async : false,
								success : function(result) {
									_self.currentUserTags = result.data;
								}
							});
							return _self.currentUserTags;
						},
						// 记录最近访问的文件
						recordRecentVisitFile : function(fileItem) {
							JDS.call({
								service : "personalDocumentService.recordRecentVisitFile",
								data : [ fileItem ]
							});
						},
						// 取消标签
						cancelTag : function() {
							var _self = this;
							if (!_self.checkSelection()) {
								return;
							}
							var selection = _self.getSelection();
							var dataUuids = [];
							$.each(selection, function() {
								dataUuids.push(this.uuid);
							});
							JDS.call({
								service : "dmsTagFacadeService.untagDataAll",
								data : [ dataUuids ],
								async : false,
								success : function(result) {
									appModal.info("标签取消成功！");
									_self.getWidget().refresh();
								}
							});
						},
						// 标记为
						tagDataAs : function(e) {
							var _self = this;
							if (!_self.checkSelection()) {
								return;
							}
							var tagUuid = $(e.target).attr("btnId");
							if (StringUtils.isBlank(tagUuid)) {
								tagUuid = $(e.target).parent().attr("btnId");
							}
							var selection = _self.getSelection();
							var dataUuids = [];
							$.each(selection, function() {
								dataUuids.push(this.uuid);
							});
							JDS.call({
								service : "dmsTagFacadeService.tagData",
								data : [ dataUuids, tagUuid ],
								success : function(result) {
									appModal.info("文件（夹）标记成功！");
									_self.getWidget().refresh();
								}
							});
						},
						// 新建标签
						createTag : function(e) {
							var _self = this;
							var tagDialogOptions = {
								title : "新建标签"
							};
							showTagDialog(tagDialogOptions, function(tagName, tagColor) {
								JDS.call({
									service : "dmsTagFacadeService.createTag",
									data : [ tagName, tagColor ],
									async : false,
									success : function(result) {
										appModal.info("标签创建成功！");
										// 清空当前用户的标签以加载最新的
										_self.currentUserTags = null;
										// 重新加载左导航的标签数据
										_self.reloadMenuItem();
										// 触发数据项选择发生变化
										_self.triggerItemSelectionChanged($(e.target));
									}
								});
								_self.onCreateTagSuccess();
							});
						},
						onCreateTagSuccess : function() {
						},
						// 新建标签并标记
						createTagAndMove : function(e) {
							var _self = this;
							if (!_self.checkSelection()) {
								return;
							}
							var selection = _self.getSelection();
							var tagDialogOptions = {
								title : "新建标签并标记"
							};
							showTagDialog(tagDialogOptions, function(tagName, tagColor) {
								var dataUuids = [];
								$.each(selection, function() {
									dataUuids.push(this.uuid);
								});
								JDS.call({
									service : "dmsTagFacadeService.createTagAndTagData",
									data : [ tagName, tagColor, dataUuids ],
									success : function(result) {
										_self.getWidget().refresh();
										// 清空当前用户的标签以加载最新的
										_self.currentUserTags = null;
										// 重新加载左导航的标签数据
										_self.reloadMenuItem();
										// 触发数据项选择发生变化
										_self.triggerItemSelectionChanged($(e.target));
									}
								});
							});
						},
						// 标签改名
						renameTag : function(e, options, row) {
							var _self = this;
							var selection = null;
							if (row == null) {
								if (!_self.checkSelection(true)) {
									return;
								}
								selection = _self.getSelection();
							} else {
								selection = [ row ];
							}
							var tag = selection[0];
							var tagDialogOptions = {
								title : "重命名标签",
								tagName : tag.NAME,
								tagColor : tag.COLOR,
								showMinicolors : false
							};
							showTagDialog(tagDialogOptions, function(tagName, tagColor) {
								if ($.trim(tag.NAME) == $.trim(tagName)) {
									appModal.error("请输入新的名称！");
									return false;
								}
								JDS.call({
									service : "dmsTagFacadeService.renameTag",
									data : [ tag.UUID, tagName ],
									success : function() {
										appModal.info("标签改名成功！");
										_self.getWidget().refresh();
										// 重新加载左导航的标签数据
										_self.reloadMenuItem();
									}
								});
							});
						},
						// 删除标签
						deleteTag : function(e, options, row) {
							var _self = this;
							var selection = null;
							if (row == null) {
								if (!_self.checkSelection()) {
									return;
								}
								selection = _self.getSelection();
							} else {
								selection = [ row ];
							}
							var tagUuids = [];
							$.each(selection, function() {
								tagUuids.push(this.UUID);
							});
							appModal.confirm("是否删除标签？<br/>相关文件（夹）也将移除此标签(文件（夹）不会被删除)。", function(result) {
								if (result) {
									JDS.call({
										service : "dmsTagFacadeService.deleteAll",
										data : [ tagUuids ],
										success : function() {
											appModal.info("标签删除成功！");
											_self.getWidget().refresh();
											// 重新加载左导航的标签数据
											_self.reloadMenuItem();
										}
									});
								}
							});
						},
						checkSelection : function(onlyOne) {
							var _self = this;
							var selection = _self.getSelection();
							if (selection.length == 0) {
								appModal.error("请选择记录！");
								return false;
							}
							// 只能选择一条记录
							if (onlyOne) {
								if (selection.length > 1) {
									appModal.error("只能选择一条记录！");
									return false;
								}
							}
							return true;
						},
						// 重新加载左导航的标签数据
						reloadMenuItem : function() {
							var $tagMenu = $("ul[loadintf='com.wellsoft.pt.dms.support.DmsMyTagTreeDataProvider']");
							if ($tagMenu) {
								$tagMenu.trigger('reloadMenuItem', arguments);
							}
						},
						// 触发数据项选择发生变化
						triggerItemSelectionChanged : function($target) {
							var _self = this;
							var selection = _self.getSelection();
							var eventData = {
								selectedItems : selection
							}
							var e = new jQuery.Event("wFileManager.ItemSelectionChanged", true, true);
							e.detail = eventData || {};
							$target.trigger(e);
						}
					};
					dmsTagPrototype = $.extend(dmsTagPrototype, prototype);
					return dmsTagPrototype;
				}
			});
			return DmsTagFragment;
		});