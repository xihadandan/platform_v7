define([ "mui", "constant", "commons", "server", "mui-DyformField", "mui-DyformConstant", "mui-DyformCommons", "formBuilder" ], function($, constant, commons, server, DyformField, DyformConstant, DyformCommons,
		formBuilder) {
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	var CLS_HIDDEN = "mui-hidden";
	var addClass = DyformCommons.addClass;
	var removeClass = DyformCommons.removeClass;
	// 从表
	var wSubForm = function($placeHolder, options) {
		DyformField.apply(this, arguments);
	};
	// 从表数据行记录
	var RowRecord = function(subform, data) {
		this.subform = subform;
		this.data = data;
	}
	$.extend(RowRecord.prototype, {
		getField : function(fieldName) {
			return this.subform.getField(this.data.uuid + "_" + fieldName);
		},
		getData : function() {
			return this.data;
		},
		getId: function() {
			return this.data.uuid;
		},
		update : function() {
			this.each(function(field, value, data){
				if(field == null || typeof field === "undefined"){
					return;
				}
				field.setValue(value, true);
			}, false);
		},
		remove : function() {
			var _self = this;
			var fields = _self.subform.definition.fields;
			$.each(fields, function(i, fieldDefinition) {
				var fieldId = _self.data.uuid + "_" + fieldDefinition.name;
				_self.subform.removeField(fieldId);
			});
		},
		each : function(fields, callback, fieldProperty) {
			var _self = this;
			var data = _self.data;
			if($.isFunction(fields)){
				fieldProperty = callback;
				callback = fields;
				fields = null;
			}else if(!$.isArray(fields)){
				fields = [fields];
			}
			if(fields == null || fields.length <= 0){
				fields = fields || [];
				$.each(_self.subform.definition.fields, function(name, field){
					fields.push(field.name);
				})
			}
			$.each(fields, function(i, fieldName) {
				if(data.hasOwnProperty(fieldName) || fieldProperty !== false) {
					var fieldId = data.uuid + "_" + fieldName;
					var field = _self.subform.getField(fieldId);
					var value = data[fieldName];
					return callback(field, value, data);
					// field.setValue(value, true);
				}
			});
		}
	});
	
	commons.inherit(wSubForm, DyformField, {
		// 构造函数初始化时调用
		_options : function(options) {
			return options;
		},
		// 初始化，构造函数执行后自动调用
		init : function() {
			var _self = this;
			_self.formFields = [];
			_self.formFieldMap = {};
			_self.addedDataUuids = [];
			_self.updateData = {};
			_self.deletedDataUuids = [];
		},
		// 获取字段ID(dyfs_{主表/从表数据UUID}_{字段编号})
		getId : function() {
			return this.id;
		},
		// 获取字段所在定义UUID
		getFormUuid : function() {
			return this.formScope.getFormUuid();
		},
		// 获取字段所在数据UUID
		getDataUuid : function() {
			return this.formScope.getDataUuid();
		},
		// 获取字段名
		getName : function() {
			return this.definition.name;
		},
		// 是否从表字段
		isSubform : function() {
			return true;
		},
		// 获取字段所在定义UUID
		getSubFormUuid : function() {
			return this.definition.uuid;
		},
		getFields : function() {
			return this.formFields;
		},
		// 展开从表信息
		collapseSubFormIfPossible : function(collapse){
			var self = this;
			var wrapperSelector = "#div_" + self.getId();
			var wrapper = self.$placeHolder[0].querySelector(wrapperSelector);
			$("ul.mui-subform-view>li.mui-table-view-cell", wrapper).each(function(i, elem) {
				var action = "toggle";
				if(collapse === true){
					action = "add";
				}else if(collapse === false){
					action = "remove";
				}
				this.classList[action]("mui-active");
			});
		},
		// 根据字段ID获取字段实例
		getField : function(fieldId) {
			return this.formFieldMap[fieldId];
		},
		// 根据字段ID删除字段实例
		removeField : function(fieldId) {
			var _self = this;
			for(var index = 0; index < _self.formFields.length; index++) {
				if(_self.formFields[index].id == fieldId) {
					_self.formFields.splice(index, 1);
					break;
				}
			}
			delete this.formFieldMap[fieldId];
		},
		// 渲染
		render : function() {
			var _self = this;
			var dataList = _self.value;
			dataList = dataList.sort(function(d1, d2) {
				return d1['sort_order'] - d2['sort_order'];
			});
			var displayName = _self.definition.displayName;
			var fields = _self.definition.fields;
			var sb = new StringBuilder();
			// 从表ID规则 dysf_{从表定义UUID}
			var subformDomId = "dysf_" + _self.definition.uuid;
			var subformDomContentId = "dysf_" + _self.definition.uuid + "_content";
			sb.appendFormat('<div class="mui-card card-subform" >', displayName);
			sb.appendFormat('	<div class="mui-card-header" >');
			var subformHeader = '		<label class="mui-ellipsis">{0}</label><span class="mui-icon mui-badge mui-icon-plus icon-add-row"></span>';
			// 隐藏操作按钮或表单显示为文本时不可添加
			if(_self.definition.hideButtons === true || _self.formScope.isDisplayAsLabel()) {
				subformHeader = '		<label class="mui-ellipsis">{0}</label>';
			}
			// 切换横屏按钮
			if(($.os.android || $.os.ios) && (_self.definition.hideButtons === true || _self.formScope.isDisplayAsLabel()) 
					&& dataList && dataList.length != 0) {
				subformHeader += '<span class="mui-icon mui-icon-paperplane view-as-table"></span>';
			}
			sb.appendFormat(subformHeader, displayName);
			sb.appendFormat('	</div>');
			sb.appendFormat('	<div class="mui-card-content">');
			sb.appendFormat('<ul class="mui-table-view mui-subform-view">');
			sb.appendFormat('</ul>');
			sb.appendFormat('	</div>');
			sb.appendFormat('</div>');
			var wrapper = document.createElement("div");
			var id = "div_" + _self.getId();
			_self.wrapper = wrapper;
			wrapper.id = id;
			wrapper.innerHTML = sb.toString();
			_self.$placeHolder[0].appendChild(wrapper);
			// 从表编辑元素
			_self.$editableElem = $(wrapper);
			// 以表格形式展示
			var displayAsTable = Boolean(_self.definition.displayAsTable);
			if(_self.formScope.isDisplayAsLabel() && displayAsTable) {
				var headerContainer = wrapper.querySelector(".mui-subform-view");
				var tableId = "mui-slider_view-as-table_" + _self.getId();
				headerContainer.innerHTML = _self._buildTable(tableId);
			} else {
				// 生成从表字段及数据
				$.each(dataList, function(i, formData) {
					_self._addRow(i + 1, wrapper, formData);
				});
			}
			$("#" + id).on('tap', '.icon-delete-row', function() {
				var liNode = this.parentNode.parentNode;
				var uuid = liNode.getAttribute("uuid");
				_self.deleteRowData({uuid: uuid});
				event.stopPropagation();
			});
			$("#" + id).on('tap', '.icon-add-row', function(event) {
				var formData = {};
				_self.addRowData(formData);
			});
			if (_self.definition.displayStyle != '1') {
				_self._bindPanelEvent(id);
			}
			// 表格显示
			$("#" + id).on('tap', '.view-as-table', function() {
				_self.trigger("webview.titleBar.hide", {});
				_self.trigger("rotateUI", {orientation: 'landscape'});
				var wrapper = document.createElement("div");
				var viewAsTableId = "div_view-as-table_" + _self.getId();
				wrapper.id = viewAsTableId;
				wrapper.classList.add("mui-fullscreen");
				wrapper.innerHTML = "<div></div>";
				wrapper.setAttribute("triggerBackEvent", "rotateUI");
				wrapper.setAttribute("triggerBackEventData", "{orientation: 'portrait'}");
				var pageContainer = appContext.getPageContainer();
				var renderPlaceholder = pageContainer.getRenderPlaceholder();
				renderPlaceholder[0].appendChild(wrapper);
				var popupTableId = "mui-slider_view-as-table_" + _self.getId();
				formBuilder.buildPanel({
					title : displayName,
					content : _self._buildTable(popupTableId, true),
					contentClass : "mui-content mui-popup-table",
					container : "#" + viewAsTableId
				});
				$.ui.loadContent("#" + viewAsTableId);
				setTimeout(function() {
					var headerTable = wrapper.querySelector(".mui-header-table");
					var thread = headerTable.querySelector("tr");
					// 计算总宽度
					var columnWidth = 0;
					$.each(fields, function(i, field) {
						var isShow = field.hidden != 2;
						if(!isShow) {
							return;
						}
						columnWidth += field.width;
					});
//					console.log(thread.children.length);
//					console.log($.offset(headerTable).top);
//					console.log(headerTable.clientHeight);
					headerTable.classList.add("mui-fixed");
					var contentTable = wrapper.querySelector(".mui-content-table");
					var floatBtn = wrapper.querySelector(".filepreview-close");
					var hideFloatBtn = function() {
						setTimeout(function() {
							floatBtn.classList.add("mui-hidden");
						}, 3000);
					};
					hideFloatBtn();
					var availHeight = $.screen.availHeight || 800;
					if(columnWidth > availHeight) {
						headerTable.style.minWidth = columnWidth + "px";
						contentTable.style.minWidth = columnWidth + "px";
					}
					// 46为隐藏的标题栏高度
					contentTable.style.marginTop = (headerTable.clientHeight) + "px";
					contentTable.addEventListener($.EVENT_START, function(e) {
						headerTable.style.left = $.offset(contentTable).left + "px";
						floatBtn.classList.remove("mui-hidden");
					});
					contentTable.addEventListener($.EVENT_MOVE, function(e) {
						headerTable.style.left = $.offset(contentTable).left + "px";
					});
					contentTable.addEventListener($.EVENT_END, function(e) {
						headerTable.style.left = $.offset(contentTable).left + "px";
						setTimeout(function() {
							headerTable.style.left = $.offset(contentTable).left + "px";
						}, 500);
						hideFloatBtn();
					});
					contentTable.addEventListener("dragend", function(e) {
						headerTable.style.left = $.offset(contentTable).left + "px";
					});
					$(wrapper).on("tap", ".filepreview-close", function(event) {
						$.ui.goBack();
					});
				}, 500);
			});
		},
		_buildTable : function(tableId, popup) {
			var _self = this;
			var sb = new StringBuilder();
			var fields = _self.definition.fields;
			var fieldArray = [];
			$.each(fields, function(i, fieldDefinition) {
				fieldArray.push(fieldDefinition);
			});
			fieldArray = fieldArray.sort(function(o1,o2){
				return o1.order-o2.order;
			});
			var dataList = _self.value;
			sb.append('<div class="mui-table-view mui-display-as-table">');
			sb.append('<div class="mui-table-view-cell">');
			sb.append('<table class="mui-table mui-header-table">');
			sb.append('<thead>');
			sb.append('<tr>');
			sb.append('<th class="column-index">');
			sb.append('序号');
			sb.append('</th>');
			$.each(fieldArray,function(i, field) {
				var isShow = field.hidden != 2;
				if(!isShow) {
					return;
				}
				var displayName = field.displayName;
				var colspan = _self._getFieldNameColspan(displayName);
				field.colspan = colspan;
				field.width = colspan * 10;
				sb.appendFormat('<th class="column-{0}">', colspan);
				sb.append(displayName);
				sb.append('</th>');
			});
			sb.append('</tr>');
			sb.append('</thead>');
			sb.append('</table>');
			sb.append('<table class="mui-table mui-content-table">');
			sb.append('<tbody>');
			$.each(dataList,function(i, rowData) {
				sb.append('<tr>');
				sb.append('<td class="column-index">');
				sb.append(i + 1);
				sb.append('</td>');
				$.each(fieldArray,function(j, field) {
					var isShow = field.hidden != 2;
					if(!isShow) {
						return;
					}
					sb.appendFormat('<td class="column-{0}">', field.colspan);
					var fieldValue = rowData[field.name];
					if(fieldValue != null && fieldValue.charAt && fieldValue.charAt(0) === "{" && fieldValue.charAt(fieldValue.length - 1) === "}") {
						fieldValue = DyformCommons.getDisplayValue(fieldValue);
					}
					sb.append(fieldValue);
					sb.append('</td>');
				});
				sb.append('</tr>');
			});
			sb.append('</tbody>');
			sb.append('</table>');
			sb.append('</div>');
			sb.append('</div>');
			if(popup) {
				sb.append('<div class="filepreview-close"><i class="icon mui-icon mui-icon-close pt-info"></i></div>');
			}
//			sb.append('<div id="' + sliderId + '" class="mui-slider">');
//			sb.append('<div class="mui-slider-group">');
//			var groupCount = fieldArray.length / 4;
//			var groupMap = {};
//			var columnCount = 0;
//			for (var i = 0; i < fieldArray.length; i++) {
//				var field = fieldArray[i];
//				var currentGroup = parseInt(i / 4) + 1;
//				if(!groupMap[currentGroup]) {
//					sb.append('<div class="mui-slider-item">');
//					sb.append('<ul class="mui-table-view mui-grid-view mui-grid-9">');
//					groupMap[currentGroup] = currentGroup;
//					columnCount = 0;
//				}
//				var li = '<li class="mui-table-view-cell mui-media mui-col-xs-3">';
//				sb.append(li);
//				sb.append('<ul class="mui-table-view">');
//				sb.append('<li class="mui-table-view-cell">' + field.displayName + '</li>');
//				sb.append('</ul>');
//				sb.append('</li>');
//				columnCount++;
//				if(columnCount == 4) {
//					sb.append('</ul>');
//					sb.append('</div>');
//				}
//			}
//			if(columnCount != 4) {
//				sb.append('</ul>');
//				sb.append('</div>');
//			}
//			sb.append('</div>');
//			sb.append('</div>');
			return sb.toString();
		},
		_getFieldNameColspan : function(displayName) {
			var colspan = 12;
			var fieldName = displayName;
			if(StringUtils.isNotBlank(displayName)) {
				var columnLength = displayName.length;
				if(StringUtils.contains(fieldName, "<br/>")) {
					columnLength = columnLength / 2;
				}
				if(columnLength >= 10 && columnLength < 15) {
					colspan = 15;
				} else if(columnLength >= 15 && columnLength < 25) {
					colspan = 20;
				} else if(columnLength >= 25 && columnLength < 35) {
					colspan = 25;
				}
			}
			return colspan;
		},
		// 渲染后处理
		afterRender : function() {
			var _self = this;
			var isShow = _self.definition.isShow;
			if (!isShow) {
				_self.setHidden(true);
			}
			
			// 注册挂起的事件监听
			_self._registerPendingListeners();
		},
		_bindPanelEvent : function(id) {
			var _self = this;
			$("#" + id).on('tap', '.li-row-title', function(event) {
				var uuid = this.getAttribute("uuid");
				var rownum = 0;
				$.each(_self.value, function(index, formData) {
					if (formData.uuid == uuid) {
						rownum = index + 1;
					}
				});
				var subformDomRowDataId = "dysf_" + uuid + "_data_container_" + rownum;
				var subformDomDataId = "dysf_" + uuid + "_data_container";
				var wrapper = document.createElement("div");
				wrapper.id = subformDomRowDataId
				wrapper.classList.add("mui-fullscreen");
				var pageContainer = appContext.getPageContainer();
				if (pageContainer) {
					var renderPlaceholder = pageContainer.getRenderPlaceholder();
					renderPlaceholder[0].appendChild(wrapper);
				} else {
					document.body.appendChild(wrapper);
				}
				var html = new commons.StringBuilder();
				html.appendFormat('<div >');
				// 表单显示为文本时不显示按钮
				if(!_self.formScope.isDisplayAsLabel()) {
					html.appendFormat('	<nav class="mui-bar mui-bar-tab" uuid="{0}">', uuid);
					html.appendFormat('		<a class="mui-tab-item mui-active field_value_save" name="save" href="#save">');
					html.appendFormat('			<span class="mui-tab-label mui-label">确定</span>');
					html.appendFormat('		</a>');
					html.appendFormat('		<a class="mui-tab-item mui-active field_value_cancel" name="cancel" href="#cancel">');
					html.appendFormat('			<span class="mui-tab-label mui-label">取消</span>');
					html.appendFormat('		</a>');
					html.appendFormat('	</nav>');
				}
				html.appendFormat('	</div>');
				html.appendFormat('<div class="mui-card card-subform" >');
				html.appendFormat('	<div class="mui-card-content">');
				html.appendFormat('	</div>');
				html.appendFormat('</div>');
				formBuilder.buildPanel({
					title : "从表明细",
					content : html.toString(),
					contentClass : "mui-content mui-dyform",
					container : "#" + subformDomRowDataId
				})
				$.ui.loadContent("#" + subformDomRowDataId);
				_self._addRowBody2(rownum, wrapper, _self.value[rownum - 1]);

				$(wrapper).on("tap", ".field_value_save", function() {
					var uuid = this.parentNode.getAttribute("uuid");
					var rownum = 0;
					var formData = {};
					$.each(_self.value, function(index, fData) {
						if (fData.uuid == uuid) {
							rownum = index + 1;
							formData = fData;
						}
					});
					var updateFields = [];
					var fields = _self.definition.fields;
					$.each(fields, function(i, fieldDefinition) {
						var fieldId = formData.uuid + "_" + fieldDefinition.name;
						var field = _self.getField(fieldId);
						formData[fieldDefinition.name] = DyformCommons.getFieldValueForSave(field);
						if (field.isValueChanged() || _self.addedDataUuids.indexOf(uuid) != -1) {
							updateFields.push(fieldDefinition.name);
						}
					});
					if (updateFields.length) {
						_self.updateData[uuid] = updateFields;
					}
					$.ui.goBack();
				});
				$(wrapper).on("tap", ".field_value_cancel", function() {
					$.ui.goBack();
				});
				return false;
			});

		},
		_addRow : function(rownum, wrapper, formData) {
			this._addRowTitle(rownum, wrapper, formData);
			if (this.definition.displayStyle == '1') {
				this._addRowBody(rownum, wrapper, formData);
			}
		},
		_addRowTitle : function(rownum, wrapper, formData) {
			var _self = this;
			var expendAll = Boolean(_self.definition.expandAllWhileReadonly);
			var expendAllClass = "";
			if (_self.definition.displayStyle !== '1') {
				expendAll = false;
			}
			var dyform = _self.formScope.getDyform();
			if(dyform.dyformCreateComplete === true) {
				expendAll = false;
			}
			if(expendAll === true) {
				expendAllClass = "mui-active";
			}
			var sb = new StringBuilder();
			var subformDomDataId = "dysf_" + formData.uuid + "_data_container";
			sb.appendFormat('<li id="{0}" uuid="{1}" class="mui-table-view-cell mui-collapse li-row-title {2}">', subformDomDataId, formData.uuid, expendAllClass);
			sb.appendFormat('<a class="mui-navigate-right {0}" href="#">', expendAllClass);
			var rowTitleField = _self.definition.rowTitle;
			var rowTitle = "第" + rownum + "行" + _self._getFieldRowTitle(formData, rowTitleField);
			sb.appendFormat('	<span class="span-row-title">{0}</span>', rowTitle);
			// 表单显示为文本时不可删除
			if(!(_self.definition.hideButtons === true || _self.formScope.isDisplayAsLabel())) {
				sb.appendFormat('	<span class="mui-icon mui-badge mui-icon-close icon-delete-row"></span>');
			}
			sb.appendFormat('</a>');
			sb.appendFormat('</li>');
			var headerContainer = wrapper.querySelector(".mui-subform-view");
			headerContainer.appendChild($.dom(sb.toString())[0])
		},
		_getFieldRowTitle : function(formData, rowTitleField) {
			if(formData == null) {
				return "";
			}
			var fieldRowTitle = formData[rowTitleField];
			if(StringUtils.isBlank(fieldRowTitle) && StringUtils.isBlank(formData[fieldRowTitle])) {
				return "";
			}
			if(fieldRowTitle.charAt(0) === "{" && fieldRowTitle.charAt(fieldRowTitle.length - 1) === "}") {
				fieldRowTitle = DyformCommons.getDisplayValue(fieldRowTitle);
				if(StringUtils.isNotBlank(fieldRowTitle)) {
					fieldRowTitle = " (" + fieldRowTitle + ")";
				}
			}
			return fieldRowTitle;
		},
		_addRowBody2 : function(rowNum, wrapper, formData) {
			var _self = this;
			// 5、从表数据区 dysf_{从表数据UUID}_data_container
			var subformDomDataId = "dysf_" + formData.uuid + "_data_container";
			var subformDomRowDataId = "dysf_" + formData.uuid + "_data_container_" + rowNum;
			var rowContainer = wrapper.querySelector(".mui-content .mui-card-content");
			// var rowContainer = wrapper.querySelector(".mui-content");
			// rowContainer.classList.add("mui-input-group");
			var fields = _self.definition.fields;
			
			var formScope = _self.dyform.createDyformScope(_self.definition, _self.formFieldMap, formData);
			var callback = function() {
				var fieldArray = [];
				$.each(fields, function(i, fieldDefinition) {
					fieldArray.push(fieldDefinition);
				});
				fieldArray = fieldArray.sort(function(o1,o2){
					return o1.order-o2.order;
				});
				$.each(fieldArray, function(i, fieldDefinition) {
					_self._renderField(formData, formScope, fieldDefinition, rowContainer);
				});
				var rowRecord = new RowRecord(_self, formData);
				_self.trigger("afterInsertRow", {record : rowRecord});
			};
			setTimeout(callback, 1);
		},
		_renderField : function(formData, formScope, fieldDefinition, rowContainer) {
			var _self = this;
			fieldDefinition.isShow = (fieldDefinition.hidden != 2);
			//剔除换行标识
			fieldDefinition.displayName = fieldDefinition.displayName.replace(/<br>/g, " ").replace(/<br\/>/g, " ");
			var fieldId = formData.uuid + "_" + fieldDefinition.name;
			var option = {
				formScope : formScope,
				fieldDefinition : fieldDefinition,
				fieldValue : formData[fieldDefinition.name]
			};
			var inputMode = fieldDefinition.inputMode;
			if(inputMode == null || typeof inputMode === "undefined" || inputMode == ""){
				return console.log("unknown inputMode:" + inputMode);
			}
			// 按字段类型模块初始化
			var fieldModuleName = DyformConstant.muiFieldModule[inputMode];
			var fieldModule = require(fieldModuleName);
			var formField = new fieldModule($(rowContainer), option);
			if (fieldModule === null || typeof fieldModule === "undefined") {
				return console.log("can't not find controlModule：" + fieldModuleName);
			}
			formField.init();
			_self.formFields.push(formField);
			// 存储字段映射
			_self.formFieldMap[formField.getId()] = formField;
			// 渲染
			formField.render();
			// 标记字段初始化完成
			formField.initComplete = true;
		},
		_addRowBody : function(rowNum, wrapper, formData) {
			var _self = this;
			// 5、从表数据区 dysf_{从表数据UUID}_data_container
			var subformDomDataId = "dysf_" + formData.uuid + "_data_container";
			var subformDomRowDataId = "dysf_" + formData.uuid + "_data_container_" + rowNum;
			var fieldContainer = wrapper.querySelector("#" + subformDomDataId);
			var fields = _self.definition.fields;
			var rowSb = new StringBuilder();
			rowSb.appendFormat('<ul class="mui-table-view mui-subform-view">');
			rowSb.appendFormat('<li id="{0}" class="mui-table-view-cell">', subformDomRowDataId);
			rowSb.appendFormat('</ul>');
			var rowWrapper = document.createElement("div");
			// rowWrapper.innerHTML = rowSb.toString();
			rowWrapper.classList.add("mui-collapse-content");
			fieldContainer.appendChild(rowWrapper);

			var rowContainer = rowWrapper;
			// var rowContainer = rowWrapper.querySelector("#" +
			// subformDomRowDataId);

			var formScope = _self.dyform.createDyformScope(_self.definition, _self.formFieldMap, formData);
			var callback = function() {
				var fieldArray = [];
				$.each(fields, function(i, fieldDefinition) {
					fieldArray.push(fieldDefinition);
				});
				fieldArray = fieldArray.sort(function(o1,o2){
					return o1.order-o2.order;
				});
				$.each(fieldArray, function(i, fieldDefinition) {
					_self._renderField(formData, formScope, fieldDefinition, rowContainer);
				});
				var rowRecord = new RowRecord(_self, formData);
				_self.trigger("afterInsertRow", {record : rowRecord});
			};
			setTimeout(callback, 1);
		},
		// 新增的数据
		getAddedData : function() {
			return this.addedDataUuids;
		},
		// 获取更新的数据字段
		getUpdatedData : function() {
			var _self = this;
			if (_self.definition.displayStyle != '1') {
				return _self.updateData;
			}
			var fields = _self.definition.fields;
			var dataList = _self.value;
			var updateData = {};
			// TODO
			$.each(dataList, function(i, formData) {
				var updateFields = [];
				$.each(fields, function(i, fieldDefinition) {
					var fieldId = formData.uuid + "_" + fieldDefinition.name;
					var field = _self.getField(fieldId);
					if(!field || !field.isValueChanged) {
						// FIXME 从表初始化异步,主表初始化完成时,从表可能还没有初始化成功
						console.log("error fieldId:" + fieldId);
						return ;
					}
					if (field.isValueChanged() || _self.addedDataUuids.indexOf(formData.uuid) != -1) {
						updateFields.push(fieldDefinition.name);
					}
				});
				if (updateFields.length) {
					updateData[formData.uuid] = updateFields;
				}
			});
			return updateData;
		},
		// 删除的数据
		getDeletedData : function() {
			return this.deletedDataUuids;
		},
		// 获取字段值
		getValue : function() {
			// 重写
			var _self = this;
			if (_self.definition.displayStyle != '1') {
				return _self.value;
			}
			var fields = _self.definition.fields;
			var dataList = _self.value;
			$.each(dataList, function(i, formData) {
				formData['sort_order'] = i + "";
				$.each(fields, function(i, fieldDefinition) {
					var fieldId = formData.uuid + "_" + fieldDefinition.name;
					var field = _self.getField(fieldId);
					if (field) {
						formData[fieldDefinition.name] = DyformCommons.getFieldValueForSave(field);
					} else if (!(fieldDefinition.name in formData)) {
						formData[fieldDefinition.name] = "";
					}
				});
			});
			return dataList;
		},
		// 设置字段值
		setValue : function(value) {
			console.log("不支持的操作，setValue: " + value);
		},
		// 设置字段值后调用，自动调用
		afterSetValue : function(value) {
			console.log("afterSetValue: " + value);
		},
		// 设置字段显示为文本
		setDisplayAsLabel : function() {
		},
		// 判断字段是否必填
		isRequired : function() {
		},
		// 设置字段是否必填
		setRequired : function(required) {
		},
		// 判断字段是否可编辑
		isEditable : function() {
		},
		// 设置字段是否可编辑
		setEditable : function(editable) {
		},
		// 判断字段是否只读
		isReadonly : function() {
		},
		// 设置字段是否只读
		setReadonly : function(readonly) {
		},
		// 判断字段是否隐藏
		isHidden : function() {
			var cardSubform = this.$placeHolder[0].querySelector(".card-subform");
			var result = false;
			$.each(cardSubform.classList, function(i, clz) {
				if (StringUtils.trim(clz) == CLS_HIDDEN) {
					result = true;
				}
			});
			return result;
		},
		// 设置字段是否隐藏
		setHidden : function(hidden) {
			var cardSubform = this.$placeHolder[0].querySelector(".card-subform");
			if (hidden) {
				addClass($(cardSubform), CLS_HIDDEN);
			} else {
				removeClass($(cardSubform), CLS_HIDDEN);
			}
		},
		// 获取从表数据
		getData : function() {
			return this.getValue();
		},
		// 获取从表记录rowid/rowdata
		getRecord : function(rowid) {
			var _self = this;
			var id = rowid;
			if(typeof rowid === "object") {
				id = rowid.uuid;
			}
			var dataList = _self.value;
			for(var i = 0; i < dataList.length; i++) {
				if(id == dataList[i].uuid) {
					return new RowRecord(_self, dataList[i]);
				}
			}
			return null;
		},
		// 添加从表数据
		addRowData : function(rowData) {
			var _self = this;
			if(StringUtils.isBlank(rowData.uuid)) {
				rowData.uuid = commons.UUID.createUUID();
			}
			_self.value.push(rowData);
			_self.addedDataUuids.push(rowData.uuid);
			// 重新标记排序
			$.each(_self.value, function(i, data) {
				data["sort_order"] = i + "";
			});
			var wrapperSelector = "#div_" + _self.getId();
			var wrapper = _self.$placeHolder[0].querySelector(wrapperSelector);
			_self._addRow(_self.value.length, wrapper, rowData);
		},
		// 更新从表数据
		updateRowData : function(rowData) {
			var self = this;
			var record = self.getRecord(rowData.uuid);
			if(record == null || typeof record === "undefined"){
				console.log("updateRowData record not found:" + rowData.uuid);
				return false;
			}
			var data = record.getData();
			for(var p in rowData) {
				data[p] = rowData[p];
			}
			record.update();
		},
		// 删除从表数据
		deleteRowData : function(rowData) {
			var _self = this;
			var uuid = rowData.uuid;
			var liSelector = "li[uuid=" + rowData.uuid + "]";
			var liNode = _self.$placeHolder[0].querySelector(liSelector);
			var index = _self.addedDataUuids.indexOf(uuid);
			if (index != -1) {
				_self.addedDataUuids.splice(index, 1);
			} else {
				_self.deletedDataUuids.push(uuid);
			}
			var rownum = 0;
			$.each(_self.value, function(index, formData) {
				if (formData.uuid == uuid) {
					rownum = index + 1;
				}
			});
			_self.value.splice(rownum - 1, 1);
			liNode.parentNode.removeChild(liNode);

			var wrapperSelector = "#div_" + _self.getId();
			var wrapper = _self.$placeHolder[0].querySelector(wrapperSelector);
			$.each(_self.value, function(i, formData) {
				var subformDomDataId = "dysf_" + formData.uuid + "_data_container";
				var titleSpan = wrapper.querySelector("#" + subformDomDataId + " .span-row-title");
				titleSpan.innerHTML = "第" + (i + 1) + "行";
			});
			// 删除记录
			var rowRecord = new RowRecord(_self, rowData);
			rowRecord.remove();
			_self.trigger("afterDeleteRow", {rowId : uuid});
		},
		each : function(fields, callback, fieldProperty) {
			var _self = this;
			var data = _self.getData() || [];
			$.each(data, function(i, row) {
				var rowRecord = new RowRecord(_self, row);
				return rowRecord.each(fields, callback, fieldProperty);
			});
		},
		clearSubform : function(){
			var _self = this;
			var data = _self.getData() || [];
			$.each(data, function(i, row) {
				_self.deleteRowData(row);
			});
		},
		hide : function(){
			var _self = this;
			_self.$placeHolder[0].classList.add("mui-hidden");
		},
		show : function(){
			var _self = this;
			_self.$placeHolder[0].classList.remove("mui-hidden");
		}
	});
	return wSubForm;
});