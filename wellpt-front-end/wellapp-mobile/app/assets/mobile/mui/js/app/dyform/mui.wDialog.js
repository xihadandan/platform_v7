define(
		[ "mui", "constant", "commons", "server", "appContext", "mui-DyformField", "formBuilder", 'dataStoreBase',
				'appModal' ],
		function($, constant, commons, server, appContext, DyformField, formBuilder, DataStore, appModal) {
			// 弹出框
			var wDialog = function($placeHolder, options) {
				DyformField.apply(this, arguments);
			};

			var tabBuffer = '<nav class="mui-bar mui-bar-tab">';
			tabBuffer += '<a class="mui-tab-item mui-active btn-ok" href="#">确认</a>';
			tabBuffer += '<a class="mui-tab-item" href="#"></a>';
			tabBuffer += '<a class="mui-action-back mui-tab-item mui-active btn-cancel" href="#">取消</a>';
			tabBuffer += '</nav>';
			var searchBuffer = '<form class="mui-input-row mui-search" action="" onsubmit="return false;">';// style="top:10px;" 
			searchBuffer += '<input type="search" class="mui-input-clear" placeholder="搜索">';
			searchBuffer += '</form>';
			var wrapperBuffer = '<div class="mui-content mui-scroll-wrapper" style="top: 98px;">';
			wrapperBuffer += '<div class="mui-scroll">';
			wrapperBuffer += '<ul class="mui-table-view mui-table-view-striped mui-table-view-condensed"></ul>';
			wrapperBuffer += '</div>';
			wrapperBuffer += '</div>';
			commons.inherit(wDialog, DyformField, {
				// 渲染
				render : function() {
					var that = this, options = that.options.fieldDefinition;
					that._superApply(arguments);
					var _relationDataDefiantion = options.relationDataDefiantion;
					if (!_relationDataDefiantion && options.isRelevantWorkFlow != "1") {
						$.alert("请添加弹出框控件的字段映射关系:" + that.getDisplayName());
						return;
					}
					if (options.isRelevantWorkFlow != "1") { // 弹出框非关联流程
						var hasSearch = false;
						var relationFieldMappingArray = [];
						var relationFieldMappingstrArray = _relationDataDefiantion.split("|");
						for (var i = 0; i < relationFieldMappingstrArray.length; i++) {
							var columnDef = JSON.parse(relationFieldMappingstrArray[i]);
							columnDef.name = columnDef.sqlField;
							columnDef.mapColumn = columnDef.sqlField;
							relationFieldMappingArray.push(columnDef);
							hasSearch = hasSearch || columnDef.isSearch;
						}
						options.hasSearch = hasSearch;
						options.relationFieldMappingstrArray = relationFieldMappingArray;
					}
					if (!options.readOnly && options.relativeMethod == "1") { // 弹出框
						var defineBean = null;
						if (options.relationDataValueTwoId) { // 新版视图,基于数据仓库
							defineBean = appContext.getWidgetDefinition(options.relationDataValueTwoId);
						} else if (options.relationDataValueTwo) {// 旧视图
							var configuration = {
								type : 2,
								dataStoreId : options.relationDataValueTwo
							};
							var definitionJson = {
								configuration : configuration
							};
							defineBean = {
								definitionJson : definitionJson
							}
						}else {
							// TODO 关联流程
							return;
						}
						if (typeof defineBean.definitionJson === "string") {
							defineBean.definitionJson = $.parseJSON(defineBean.definitionJson);
						}
						var define = options.definitionJson = defineBean.definitionJson;
						var configuration = define.configuration;
						var muiStyle = configuration.multiSelect === true ? "mui-checkbox" : "mui-radio";
						var templateHtml = "<div class='" + muiStyle + " mui-left '>";
						if (configuration.multiSelect === true) {
							templateHtml += "<input type='checkbox' name='" + that.getName() + "'/>";
						} else {
							templateHtml += "<input type='radio' name='" + that.getName() + "'/>";
						}
						templateHtml += "<label class='mui-ellipsis'>"
						$.each(options.relationFieldMappingstrArray, function(i, field) {
							if (field.isHide === true || !field.sqlField) {
								return;
							}
							templateHtml += (i === 0 ? "&nbsp;&nbsp;" : "&nbsp;&nbsp;|&nbsp;&nbsp;"); // 
							templateHtml += "{" + field.sqlField + "}";
						});
						templateHtml += "</label></div>";
						options.templateHtml = templateHtml;
						options.definitionJson.configuration = $.extend(configuration, {
							hasSearch : options.hasSearch,
							templateHtml : options.templateHtml,
							templateProperties : options.relationFieldMappingstrArray
						});
						that.$editableElem.each(function(i, element) {
							element.setAttribute("readonly", "readonly");
							// panel保存起来
							var panel = document.createElement("div");
							panel.id = "wDialogId-" + that.getName() + (++$.uuid);
							formBuilder.buildPanel({
								title : that.getDisplayName(),
								content : "",
								container : panel
							})
							panel.classList.add("table-view-picker");
							var content = $(".mui-content", panel)[0];
							content.id = "wDialogContentId-" + that.getName() + $.uuid;
							panel.insertBefore($.dom(tabBuffer)[0], content);
							var fnDef = function(event) {
								var self = this;
								document.body.appendChild(panel);
								$.ui.loadContent(panel);
								$(content).wMobileListView({
									widgetDefinition : options.definitionJson,
									containerDefinition : {}
								});
								$(".mui-scroll-wrapper", content)[0].classList.remove("table-view-container");
								var okSelector = "nav.mui-bar-tab>.btn-ok";
								$(panel).off("tap", okSelector).on("tap", okSelector, function(event) {
									var selecteds = [];
									var $content = $(content);
									$("input[name=" + that.getName() + "]:checked", content).each(function(i, elem) {
										var cellElem = elem;
										do {
											cellElem = cellElem.parentNode;
											var cellCalss = cellElem.classList;
											if (cellCalss && cellCalss.contains("mui-table-view-cell")) {
												break;
											}
										} while (cellElem != content);
										selecteds.push($content.wMobileListView("getData", cellElem.index));
									});
									$.back(); // 返回
									that.applyValue2Control(options.relationFieldMappingstrArray, selecteds);
								});
								self.blur();
								// event.preventDefault();
								// event.stopPropagation();
								return false;
							}
							if (options.showIcon === true) {
								element.classList.add("ViewPicker");
							}
							element.removeEventListener("focus", fnDef);
							element.addEventListener("focus", fnDef, true);
							element.setAttribute("style", "ime-mode:disabled");
							element.setAttribute("readonly", "readonly");
						})
					} else if (!options.readOnly && options.relativeMethod == "2") { // 搜索框
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
						function getDyviewDefinedtion(dyviewDefId) {
							var dyviewDefinition = null;
							server.JDS.call({
								service : "viewDefinitionNewService.getBeanByViewId",
								data : [ dyviewDefId ],
								async : false,
								success : function(result) {
									dyviewDefinition = result.data;
								}
							});
							return dyviewDefinition;
						}
						var widgetDefinition = {};
						if (widgetDefinition === null || typeof widgetDefinition === "undefined") {
							return;
						}
						options.widgetDefinition = widgetDefinition;
						options.searchMultiple = options.searchMultiple === true || options.searchMultiple === "true";
						var muiStyle = options.searchMultiple ? "mui-checkbox" : "mui-radio";
						var templateHtml = "<div class='" + muiStyle + " mui-left '>";
						if (options.searchMultiple === true) {
							templateHtml += "<input type='checkbox' name='" + that.getName() + "'/>";
						} else {
							templateHtml += "<input type='radio' name='" + that.getName() + "'/>";
						}
						templateHtml += "<label class='mui-ellipsis'>"
						var sqlFields = [];
						$.each(options.relationFieldMappingstrArray, function(i, field) {
							sqlFields.push(field.sqlField);
							if (field.isHide === true || !field.sqlField) {
								return;
							}
							templateHtml += (i === 0 ? "&nbsp;&nbsp;" : "&nbsp;&nbsp;|&nbsp;&nbsp;");
							templateHtml += "${" + field.sqlField + "}";
						});
						templateHtml += "</label></div>";
						options.templateHtml = templateHtml;
						var templateEngine = appContext.getJavaScriptTemplateEngine();
						that.$editableElem.each(function(i, element) {
							element.setAttribute("readonly", "readonly");
							var fnDef = function(event) {
								var self = this;
								var panel = document.createElement("div");
								panel.id = "wDialogId-" + that.getName() + (++$.uuid);
								document.body.appendChild(panel);
								formBuilder.buildPanel({
									title : that.getDisplayName(),
									content : "",
									container : panel
								})
								$.ui.loadContent(panel);
								panel.classList.add("table-view-picker");
								var content = $(".mui-content", panel)[0];
								content.id = "wDialogContentId-" + that.getName() + $.uuid;
								panel.insertBefore($.dom(tabBuffer)[0], content);
								// 
								var $searchInput, searchword;
								var pageSize = options.pageSize || 20;
								var hasSearch = function(relationFieldMappingstrArray) {
									for (var i = 0; i < relationFieldMappingstrArray.length; i++) {
										if (relationFieldMappingstrArray[i].isSearch === true) {
											return true;
										}
									}
									return false;
								}
								if (hasSearch(options.relationFieldMappingstrArray)) {
									content.appendChild($.dom(searchBuffer)[0]);
									var $searchInput = $("input[type=search]", content);
									$searchInput.input();
								}
								var wrapper = $.dom(wrapperBuffer)[0];
								content.appendChild(wrapper);
								var tableView = $(".mui-table-view", wrapper)[0];
								var dataProvider = new DataStore({
									type : 1,
									sqlFields : sqlFields,
									dataStoreId : options.relationDataValueTwo,
									onDataChange : function(data, count, refresh) {
										appModal.hideMask()
										if (data == null || typeof data === "undefined") {
											return;
										}
										if (refresh) {
											tableView.innerHTML = "";
										}
										for (var i = 0; i < data.length; i++) {
											var jsondata = data[i];
											var strdata = encodeURI(JSON.stringify(jsondata));
											var lidata = '<li class="mui-table-view-cell" data-json="' + strdata + '">'
													+ templateEngine.render(templateHtml, jsondata) + "</li>";
											tableView.appendChild($.dom(lidata)[0]);
										}
										if (refresh) {
											$(wrapper).pullRefresh().endPulldownToRefresh();
											$(wrapper).pullRefresh().refresh(true);
										} else {
											$(wrapper).pullRefresh().endPullupToRefresh(data.length < pageSize);
										}
									},
									receiver : this,
									autoCount : false,
									pageSize : pageSize
								});

								var deceleration = mui.os.ios ? 0.003 : 0.0009;
								$(wrapper).scroll({
									bounce : false,
									indicators : true, // 是否显示滚动条
									deceleration : deceleration
								});

								/**
								 * 收集查询条件
								 */
								var collectSearchText = function() {
									return searchword;
								};
								/**
								 * 收集查询条件
								 */
								var collectContraint = function() {
									var relationDataTwoSql = options.relationDataTwoSql;
									var contraint = {};
									if (typeof relationDataTwoSql != "undefined"
											&& $.trim(relationDataTwoSql).length > 0) {
										var fieldvalues = relationDataTwoSql.split(";");
										for (var i = 0; i < fieldvalues.length; i++) {
											var fieldValue = fieldvalues[i].split("=");
											if (fieldValue.length != 2) {
												continue;
											}
											var field = fieldValue[0];
											var valueField = fieldValue[1];
											var value = that.dyform.getFieldValue(valueField);
											if (value == null || typeof value === "undefined") {// 没有设值
												continue;
											}
											contraint[field] = value;
										}
									}
									return contraint;
								};

								/**
								 * 收集查询条件(新版)
								 */
								var collectCriterion = function() {
									var criterions = [];
									if (searchword) {
										var orcriterion = {
											conditions : [],
											type : 'or'
										};
										$.each(options.relationFieldMappingstrArray, function(index, property) {
											if (property.isHide === true || property.isSearch === false) {
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

								$(wrapper).pullRefresh({
									down : {
										callback : function(event) {
											that.trigger("onPulldown", event);
											$(wrapper).pullRefresh().endPulldownToRefresh();
											var criterions = collectContraint();// TODO
											// 判断新版视图和旧视图,拼装查询条件
											var searchText = collectSearchText();
											appModal.showMask("");
											dataProvider.load({
												pagination : {
													currentPage : 1
												},
												keyword : searchText,
												criterions : criterions
											}, true);
										}
									},
									up : {
										contentrefresh : '正在加载...',
										callback : function() {
											that.trigger("onPullup", event);
											$(wrapper).pullRefresh().endPullupToRefresh();
											appModal.showMask();
											dataProvider.nextPage(false);
										}
									}
								}).pulldownLoading();

								var searchFn = function(event) {
									var text = this.value || '';
									if (searchword != text) {
										searchword = text;
										$(wrapper).pullRefresh().pulldownLoading();
									}
								}
								var header = document.querySelector('.panel:not(.mui-hidden)>header.mui-bar');
								if($searchInput){
									$searchInput[0].addEventListener('blur', searchFn, false);
									$searchInput[0].addEventListener('search', searchFn, false);
									$('.mui-search', wrapper).on('tap', '.mui-icon-clear', searchFn, false);
									$(".mui-content", content)[0].style.top = (header.offsetHeight + 60) + 'px';;
								}else {
									$(".mui-content", content)[0].style.top = header.offsetHeight + 'px';;
								}
								var okSelector = "nav.mui-bar-tab>.btn-ok";
								$(panel).off("tap", okSelector).on("tap", okSelector, function(event) {
									var self = this;
									var selecteds = [];
									$("input[name=" + that.getName() + "]:checked", content).each(function(i, elem) {
										var cellElem = elem;
										do {
											cellElem = cellElem.parentNode;
											var cellCalss = cellElem.classList;
											if (cellCalss && cellCalss.contains("mui-table-view-cell")) {
												break;
											}
										} while (cellElem != content);
										var strjson = decodeURI(cellElem.getAttribute("data-json"));
										selecteds.push($.parseJSON(strjson));
									});
									$.back(); // 返回
									that.applyValue2Control(options.relationFieldMappingstrArray, selecteds);
								});

								self.blur();
								// event.preventDefault();
								// event.stopPropagation();
								return false;
							}
							if (options.showIcon === true) {
								element.classList.add("ViewPicker");
							}
							element.removeEventListener("focus", fnDef);
							element.addEventListener("focus", fnDef, true);
							element.setAttribute("style", "ime-mode:disabled");
							element.setAttribute("readonly", "readonly");
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
						var dyform = self.dyform;
						var formScope = self.formScope;
						var seperate = options.seperate || ";";
						$(relationFieldMappingArray).each(function(idx, data) {
							var sqlField = data.sqlField, formField = data.formField, fieldValue = "";
							for (var i = 0; i < datas.length; i++) {
								var selectedata = datas[i];
								var endseperate = (i < datas.length - 1) ? seperate : "";
								fieldValue += (selectedata[sqlField] || "") + endseperate;
							}
							var field = formScope.getField(formField);
							if(!field || !field.setValue){
								field = dyform.getField(formField);
							}
							if(field && field.setValue) {
								field.setValue(fieldValue, true);
							}
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
					self._superApply(arguments);
					var editableElem = self.$editableElem[0];
					editableElem.blur();
				}
			});
			return wDialog;
		});