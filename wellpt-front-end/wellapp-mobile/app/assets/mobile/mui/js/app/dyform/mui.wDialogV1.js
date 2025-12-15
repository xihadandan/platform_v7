define([ "mui", "constant", "commons", "server", "appContext", "mui-DyformField", "formBuilder", 'dataStoreBase', "mui-listpicker" ], function($, constant, commons, server, appContext, DyformField, formBuilder, DataStore) {
	var TableViewPicker = $.TableViewPicker = $.ListPicker.extend({
		setItems : function(items) {
			var self = this;
			var buffer = [];
			for (index in items) {
				var item = items[index] || {
					text : 'null',
					value : 'null' + index
				};
				var itemJson = JSON.stringify(item);
				var liCell = "<li class='mui-table-view-cell' data-value='" + item.value + "' data-item='" + itemJson + "'>\
<div class='mui-slider-right'><a class='mui-btn mui-btn-red' href='javascript:void(0);'>删除</a></div>\
<a class='mui-slider-handle' title='点击下载' herf='javascript:void(0);'>\
<span class='filename'>" + item.text + "</span>\
<span class='mui-badge filesize'>" + (item.filesize ? item.filesize : "0") + "</span>\
</a></li>";
				buffer.push(liCell);
			}
			self.list.innerHTML = buffer.join('');
			if (self._scrollerApi && self._scrollerApi.refresh) {
				self._scrollerApi.refresh();
			}
			self.refresh();
			self._handleHighlight();
			self._triggerChange();
		},
		setSelectedIndex : function(index, noAni) {
			var self = this;
			index = (index || 0);
			// 纠正选中项居中
			// self.setScrollTop(self.itemHeight * index, noAni ? 0 :
			// self.options.fiexdDur);
		}
	});
	// 定义弹出选择器类
	var panelBuffer = '<div class="mui-poppicker">\
		<div class="mui-poppicker-header">\
		<button class="mui-btn mui-poppicker-btn-cancel">取消</button>\
		<button class="mui-btn mui-btn-blue mui-poppicker-btn-ok">确定</button>\
		</div>\
		<div class="mui-poppicker-body" style="overflow-y: auto;">\
		</div>\
		</div>';
	var pickerBuffer = '<div class="mui-listpicker">\
		<div class="mui-listpicker-inner mui-scroll-wrapper">\
			<div class="mui-scroll">\
				<ul class="mui-table-view attach-list">\
					<!--<li class="mui-table-view-cell"><span style="display: block;text-align: center;color: #dd524d;font-weight: 600;">没有数据项</span></li>-->\
				</ul>\
			</div>\
		</div>\
		</div>';
	var PopTableViewPicker = $.PopTableViewPicker = $.PopPicker.extend({
		panelBuffer : panelBuffer,
		pickerBuffer : pickerBuffer,
		// 构造函数
		init : function(options) {
			var self = this;
			self.options = options || {};
			self.options.buttons = self.options.buttons || [ '取消', '确定' ];
			self.panel = $.dom(self.options.panelBuffer ? self.options.panelBuffer : this.panelBuffer)[0];
			document.body.appendChild(self.panel);
			self.ok = self.panel.querySelector('.mui-poppicker-btn-ok');
			self.cancel = self.panel.querySelector('.mui-poppicker-btn-cancel');
			self.body = self.panel.querySelector('.mui-poppicker-body');
			self.mask = $.createMask();
			self.cancel.innerText = self.options.buttons[0];
			self.ok.innerText = self.options.buttons[1];
			self.cancel.addEventListener('tap', function(event) {
				self.hide();
			}, false);
			self.ok.addEventListener('tap', function(event) {
				if (self.callback) {
					var rs = self.callback(self.getSelectedItems());
					if (rs !== false) {
						self.hide();
					}
				}
			}, false);
			self.mask[0].addEventListener('tap', function() {
				self.hide();
			}, false);
			self._createPicker();
			// 防止滚动穿透
			self.panel.addEventListener($.EVENT_START, function(event) {
				event.preventDefault();
			}, false);
			self.panel.addEventListener($.EVENT_MOVE, function(event) {
				event.preventDefault();
			}, false);
			self.body.addEventListener($.EVENT_START, function(event) {
				event.preventDefault();
			}, false);
			self.body.addEventListener($.EVENT_MOVE, function(event) {
				event.preventDefault();
			}, false);

			// 
			self.ok.classList.add("dt-btn-ok");
			self.cancel.classList.add("dt-btn-cancel");
		},
		_createPicker : function() {
			var self = this;
		}
	});

	// 弹出框
	var wDialog = function($placeHolder, options) {
		DyformField.apply(this, arguments);
	};

	var popBuffer = '<div class="panel mui-hidden table-view-picker" data-back="hidden">\
	<header class="mui-bar mui-bar-nav" style="position: fixed!important">\
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>\
		<h1 class="mui-title">请选择</h1>\
		<!--<a class="mui-icon mui-icon-bars mui-pull-right"></a>-->\
	</header>\
	<div class="mui-content"></div>\
	<nav class="mui-bar mui-bar-tab">\
		<a class="mui-tab-item mui-active btn-cancel" href="#">取消</a>\
		<a class="mui-tab-item" href="#"></a>\
		<a class="mui-tab-item mui-active btn-ok" href="#">确认</a>\
	</nav>\
	</div>';
	var searchBuffer = '<div class="mui-input-row mui-search" style="top:4px;">\
			<input type="search" class="mui-input-clear" placeholder="搜索">\
		</div>';
	var wrapperBuffer = '<div class="mui-content mui-scroll-wrapper" style="top: 98px;">\
		<div class="mui-scroll">\
			<ul class="mui-table-view mui-table-view-striped mui-table-view-condensed"></ul>\
		</div>\
		</div>';
	$.extend(wDialog, {
		panel : (function() {
			var dview = $.dom(popBuffer);
			var panel = dview[0];
			var $panel = $(panel);
			var ok = $(".btn-ok", panel)[0];
			var title = $(".mui-title", panel)[0];
			var cancel = $(".btn-cancel", panel)[0];
			var content = $(".mui-content", panel)[0];
			var ui = {
				"ok" : ok,
				"title" : title,
				"panel" : panel,
				"cancel" : cancel,
				"content" : content,
				"close" : function(options){
					
				},
				"show" : function(){
					
				},
				"$" : function(selector){
					return $(selector, panel);
				}
			};
			document.body.appendChild(panel);
			return function(options) {
				if (options == null || typeof options === "undefined") {
					console.log("options is not null");
					return;
				}
				panel.id = options.id || "";
				title.innerHTML = options.title || "请选择";
				content.innerHTML = options.content || "";
				$panel.off("tap", ".mui-bar>.btn-ok").on("tap", ".mui-bar>.btn-ok", function(event) {
					if ($.isFunction(options.ok)) {
						options.ok.call(ui, options);
					}
					$.back();
				});
				$panel.off("tap", ".mui-bar>.btn-cancel").on("tap", ".mui-bar>.btn-cancel", function(event) {
					if ($.isFunction(options.cancel) && options.cancel.call(ui, options) === false) {
						return event.stopPropagation();
					}
					$.back();
				});
				if ($.isFunction(options.show) && options.show.call(ui, options) === false) {
					return false;
				}
				$.ui.loadContent(panel);
				if ($.isFunction(options.shown)) {
					options.shown.call(ui, options);
				}
				return ui;
			}
		})(),
		dialog : (function() {
			var dialog;
			return function(options) {
				return dialog;
			}
		})()
	});
	commons.inherit(wDialog, DyformField, {
		init : function() {
		},
		// 渲染
		render : function() {
			var that = this, options = that.options.fieldDefinition;
			that._superApply(arguments);
			var _relationDataDefiantion = options.relationDataDefiantion;
			if (!_relationDataDefiantion && options.isRelevantWorkFlow != "1") {
				$.alert("请添加弹出框控件的字段映射关系:" + that.getName());
				return;
			}
			var _relationDataValueTwo, _relationDataTwoSql, hasSearch = false;
			if (options.isRelevantWorkFlow != "1") {
				// alert(_relationDataDefiantion + "----" + ctlName);
				var relationFieldMappingstrArray = _relationDataDefiantion.split("|");
				var relationFieldMappingArray = [];
				for (var i = 0; i < relationFieldMappingstrArray.length; i++) {
					var columnDef = JSON.parse(relationFieldMappingstrArray[i]);
					columnDef.name = columnDef.sqlField;
					columnDef.mapColumn = columnDef.sqlField;
					relationFieldMappingArray.push(columnDef);
					hasSearch = hasSearch || columnDef.isSearch;
				}
				_relationDataTwoSql = options.relationDataTwoSql;
				_relationDataValueTwo = options.relationDataValueTwo;// 数据来源id
				options.relationFieldMappingstrArray = relationFieldMappingArray;
			}
			if (!options.readOnly && options.relativeMethod == "1") {
				if (options.relationDataValueTwoId) {
					var defineBean = appContext.getWidgetDefinition(options.relationDataValueTwoId);
					var define = options.definitionJson = $.parseJSON(defineBean.definitionJson);
					var configuration = define.configuration;
					var muiStyle = configuration.multiSelect === true ? "mui-checkbox" : "mui-radio";
					var templateHtml = "<div class='" + muiStyle + " mui-left '>";
					if (configuration.multiSelect === true) {
						templateHtml += "<input type='checkbox' name='" + that.getName() + "'/>";
					} else {
						templateHtml += "<input type='radio' name='" + that.getName() + "'/>";
					}
					templateHtml += "<label class='mui-ellipsis-2'>"
					$.each(options.relationFieldMappingstrArray, function(i, field) {
						if(field.isHide === true){
							return;
						}
						templateHtml += (i === 0 ? "&nbsp;&nbsp;" : "&nbsp;&nbsp;|&nbsp;&nbsp;") + "{" + field.sqlField + "}";
					});
					templateHtml += "</label></div>";

					options.definitionJson.configuration = $.extend(configuration, {
						hasSearch : hasSearch,
						templateHtml : templateHtml,
						templateProperties : relationFieldMappingArray
					});
				}
				that.$editableElem.each(function(i, element) {
					var fn = function(event) {
						var picker = $.data[element.getAttribute("tableview-picker")];
						if (!picker) {
							var id = "tableview-picker" + (++$.uuid);
							element.setAttribute("tableview-picker", id);
							picker = $.data[id] = new $.PopTableViewPicker(options);
						}
						var value = that.getValue();
						var $listView = $(picker.body);
						picker.show(function(rs) {
							var selecteds = [];
							$("input[name=" + that.getName() + "]:checked", picker.body).each(function(i, elem) {
								var cellElem = elem;
								do {
									cellElem = cellElem.parentNode;
									var cellCalss = cellElem.classList;
									if (cellCalss && cellCalss.contains("mui-table-view-cell")) {
										break;
									}
								} while (cellElem != picker.body);
								selecteds.push($listView.wMobileListView("getData", cellElem.index));
							});
							// console.log(selecteds);
							that.applyValue2Control(options.relationFieldMappingstrArray, selecteds);
						});
						picker.body.setAttribute("id", options.relationDataValueTwoId);
						picker.panel.classList.add("table-view-picker");
						$listView.wMobileListView({
							widgetDefinition : options.definitionJson,
							containerDefinition : {}
						});
						$.focus(picker.ok);
						return false;
					};
					var fnDef = function(event) {
						var self = this;
						self.blur();
						event.preventDefault();
						event.stopPropagation();
						setTimeout(function() {
							fn.call(self, event);
						}, 100);
						return false;
					}
					if (options.showIcon !== false) {
						element.classList.add("ViewPicker");
					}
					element.removeEventListener("focus", fnDef);
					element.addEventListener("focus", fnDef, true);
					element.setAttribute("style", "ime-mode:disabled");
				})
			} else if (!options.readOnly) {
				var ctlId = options.relationDataValueTwo + that.getName();
				function getWidgetDefinition(widgetDefId) {
					var widgetDefinition = null;
					server.JDS.call({
						service : "cdDataStoreService.getBeanById",
						data : [ widgetDefId ],
						async : false,
						success : function(result) {
							widgetDefinition = result.data;
						}
					});
					return widgetDefinition;
				}
				var widgetDefinition = getWidgetDefinition(options.relationDataValueTwo);
				if (widgetDefinition === null || typeof widgetDefinition === "undefined") {
					return;
				}
				options.widgetDefinition = widgetDefinition;
				that.$editableElem.each(function(i, element) {
					options.searchMultiple = options.searchMultiple === true || options.searchMultiple === "true";
					var muiStyle = options.searchMultiple ? "mui-checkbox" : "mui-radio";
					var templateHtml = "<div class='" + muiStyle + " mui-left '>";
					if (options.searchMultiple === true) {
						templateHtml += "<input type='checkbox' name='" + that.getName() + "'/>";
					} else {
						templateHtml += "<input type='radio' name='" + that.getName() + "'/>";
					}
					templateHtml += "<label class='mui-ellipsis-2'>"
					$.each(options.relationFieldMappingstrArray, function(i, field) {
						if(field.isHide === true){
							return;
						}
						templateHtml += (i === 0 ? "&nbsp;&nbsp;" : "&nbsp;&nbsp;|&nbsp;&nbsp;") + "${" + field.sqlField + "}";
					});
					templateHtml += "</label></div>";
					var templateEngine = appContext.getJavaScriptTemplateEngine();
					var fn = function(event) {
						options.id = ctlId;
						wDialog.panel($.extend(options, {
							id : ctlId,
							title : that.getDisplayName(),
							ok : function(){
								var self = this;
								var selecteds = [];
								$("input[name=" + that.getName() + "]:checked", self.content).each(function(i, elem) {
									var cellElem = elem;
									do {
										cellElem = cellElem.parentNode;
										var cellCalss = cellElem.classList;
										if (cellCalss && cellCalss.contains("mui-table-view-cell")) {
											break;
										}
									} while (cellElem != self.content);
									var strjson = decodeURI(cellElem.getAttribute("data-json"));
									selecteds.push($.parseJSON(strjson));
								});
								// console.log(selecteds);
								that.applyValue2Control(options.relationFieldMappingstrArray, selecteds);
							},
							show : function() {
								var self = this;
								var $searchInput, searchword;
								var pageSize = options.pageSize || 20;
								var hasSearch = function(relationFieldMappingstrArray) {
									for(var i = 0; i < relationFieldMappingstrArray.length; i++){
										if(relationFieldMappingstrArray[i].isSearch === true){
											return true;
										}
									}
									return false;
								}
								if(hasSearch(options.relationFieldMappingstrArray)){
									self.content.innerHTML = searchBuffer;
									var $searchInput =  $("input[type=search]", self.content);
									$searchInput.input();
								}
								var wrapper = $.dom(wrapperBuffer)[0];
								var tableView = $(".mui-table-view", wrapper)[0];
								self.content.appendChild(wrapper);
								var dataProvider = new DataStore({
									dataStoreId : options.relationDataValueTwo,
									onDataChange : function(data, count, refresh) {
										if(data == null || typeof data === "undefined"){
											return;
										}
										if(refresh) {
											tableView.innerHTML = "";
										}
										for(var i = 0; i < data.length; i++){
											var jsondata = data[i];
											var strdata = encodeURI(JSON.stringify(jsondata));
											var lidata = '<li class="mui-table-view-cell" data-json="' +strdata+ '">' + templateEngine.render(templateHtml, jsondata) + "</li>";
											tableView.appendChild($.dom(lidata)[0]);
										}
										if (refresh) {
											$(wrapper).pullRefresh().endPulldownToRefresh();
											$(wrapper).pullRefresh().refresh(true);
										} else {
											$(wrapper).pullRefresh().endPullupToRefresh(data.length < pageSize);
										}
									},
									renderers : [],
									defaultOrders : [],// TODO soft code order property
									receiver : this,
									autoCount : false,
									pageSize : pageSize
								});
								
								/**
								 * 收集查询条件
								 */
								var collectCriterion = function() {
									var criterions = [];
									if ($searchInput && (searchword = $searchInput[0].value)) {
										var orcriterion = {
												conditions : [],
												type : 'or'
										};
										$.each(options.relationFieldMappingstrArray, function(index, property) {
											if(property.isHide === true || property.isSearch === false){
												return;
											}
											orcriterion.conditions.push({
												columnIndex : property.sqlField,
												value : searchword,
												type : 'like'
											});
										});
										criterions.push(orcriterion);
									}
									return criterions;
								};
								
								var deceleration = mui.os.ios ? 0.003 : 0.0009;
								$('.mui-scroll-wrapper', wrapper).scroll({
									bounce : false,
									indicators : true, // 是否显示滚动条
									deceleration : deceleration
								});
								
								$(wrapper).pullRefresh({
									down : {
										callback : function(event) {
											that.trigger("onPulldown", event);
											$(wrapper).pullRefresh().endPulldownToRefresh();
											dataProvider.load({
												pagination : {
													currentPage : 1
												},
												criterions : collectCriterion()
											}, true);
										}
									},
									up : {
										contentrefresh : '正在加载...',
										callback : function() {
											that.trigger("onPullup", event);
											$(wrapper).pullRefresh().endPullupToRefresh();
											dataProvider.nextPage(false);
										}
									}
								}).pulldownLoading();
								
								$searchInput[0].addEventListener('input', function() {
									var text = this.value || '';
									if (searchword != text) {
										$(wrapper).pullRefresh().pulldownLoading();
									}
								}, false);
								$('.mui-search', wrapper).on('tap', '.mui-icon-clear', function() {
									var text = this.value || '';
									if (searchword != text) {
										$(wrapper).pullRefresh().pulldownLoading();
									}
								}, false);
							},
							shown : function(){
								
							}
						}));
					};
					var fnDef = function(event) {
						var self = this;
						self.blur();
						event.preventDefault();
						event.stopPropagation();
						setTimeout(function() {
							fn.call(self, event);
						}, 100);
						return false;
					}
					if (options.showIcon !== false) {
						element.classList.add("ViewPicker");
					}
					element.removeEventListener("focus", fnDef);
					element.addEventListener("focus", fnDef, true);
					element.setAttribute("style", "ime-mode:disabled");
				})
			}

		},
		applyValue2Control : function(relationFieldMappingArray, datas) {
			var self = this;
			var options = self.options.fieldDefinition;
			if (self.trigger("beforePressConfirm", datas) === false) {
				return;
			}
			// 从表 TODO dyControlPos dependency
			if (options.destType === dyControlPos.subForm) {
				self.addRowData(options.destSubform, relationFieldMappingArray, datas);// 添加到从表中
			} else {
				var dyform = self.dyform, seperate = options.seperate || ";";
				$(relationFieldMappingArray).each(function(idx, data) {
					var sqlField = data.sqlField, formField = data.formField, fieldValue = "";
					for (var i = 0; i < datas.length; i++) {
						var selectedata = datas[i];
						var endseperate = (i < datas.length - 1) ? seperate : "";
						fieldValue += (selectedata[sqlField] || "") + endseperate;
					}
					dyform.setFieldValue(formField, fieldValue);
				});
			}
		},
		addRowData : function(destSubform, relationFieldMappingArray, datas) {
			var self = this;
			var paramsObjs = [];
			for (var index = 0; index < datas.length; index++) {
				var fieldsKeyValue = datas[index];
				var fieldsKeyValueObj = {};
				for ( var i in fieldsKeyValue) {
					fieldsKeyValueObj[i.toLowerCase()] = fieldsKeyValue[i];
				}
				fieldsKeyValue = fieldsKeyValueObj;
				var paramsObj = {};

				for (var i = 0; i < relationFieldMappingArray.length; i++) {
					var tempObj = relationFieldMappingArray[i];
					var sqlField = tempObj.sqlField;
					var formField = tempObj.formField;
					var value = fieldsKeyValue[sqlField.toLowerCase()];
					if (value == null || typeof value === "undefined") {
						value = "";
					}
					paramsObj[formField] = value;
				}
				paramsObjs.push(paramsObj);
				var subForm = self.dyform.getSubform(destSubform);
				if (subForm && subForm.addRowData) {
					// 添加到从表中
					subForm.addRowData(paramsObj);
				}
			}
			self.trigger("afterDialogSelect", [ self.getName(), paramsObjs, datas ]);
		},
		// 设置字段值
		setValue : function(value) {
			var self = this;
			var editableElem = this.$editableElem[0];
			editableElem.value = value;
			editableElem.blur();
		},
		/* 设值到标签中 */
		setValue2LabelElem : function() {
			var that = this;
			if (that.$labelElem == null) {
				return;
			}
			var value = that.getValue();
			if (value == null || value.trim().length == 0) {
				that.$labelElem.html("");
				return;
			}
			this.$labelElem.html(value);
			this.$labelElem.attr("title", value);
		}
	});
	return wDialog;
});