define([ "mui", "constant", "commons", "server", "mui-DyformField", 'dataStoreBase' ], function($, constant, commons,
		server, DyformField, DataStore) {
	// 普通输入框
	var wViewdisplay = function($placeHolder, options) {
		DyformField.apply(this, arguments);
	};
	var placeHolderTplId = "mui-DyformField-placeHolder-viewdisplay";
	commons.inherit(wViewdisplay, DyformField, {
		render : function() {
			var self = this;
			var options = self.definition;
			var data = self._getTemplateData();
			var templateEngine = appContext.getJavaScriptTemplateEngine();
			var html = templateEngine.renderById(placeHolderTplId, data);
			self.$placeHolder[0].innerHTML = html;
			self.$displayView = $(".mui-viewdisplay-view", self.$placeHolder[0]);
			self.$placeHolder.on("tap", ".btn-collapse", function(event) {
				options.active = !options.active;
				$(".mui-viewdisplay-view>.mui-collapse", self.$placeHolder[0]).each(function() {
					this.classList[options.active ? "add" : "remove"]("mui-active");
				});
			})
			if (options.relationDataValue) {
				var addRowFn = function(rowId, rowData) {
					rowId = rowId + 1;
					var cellTpl = '<li class="mui-table-view-cell mui-collapse">'
					cellTpl += '<a class="mui-navigate-right"><span class="span-row-title">第' + rowId + '行</span>'
					cellTpl += '<span class="mui-icon mui-badge"></span></a>'
					cellTpl += '<div class="mui-collapse-content">';
					for ( var key in rowData) {
						cellTpl += '<div class="mui-input-row">';
						cellTpl += '<label class="mui-ellipsis">' + key + '</label>';
						cellTpl += '<label class="dyform-field-label">' + rowData[key] + '</label>';
						cellTpl += '</div>';
					}
					cellTpl += '</div></li>';
					var liCell = $.dom(cellTpl)[0];
					self.$displayView[0].appendChild(liCell);
				};
				options.type = 2;// 视图
				options.pageSize = 20;
				options.dataStoreId = options.relationDataValue;
				options.onDataChange = function(data, count, refresh, definitionJson) {
					data = data || [];
					var columns = definitionJson.columnDefinitionNews;
					for (var rowId = 0; rowId < data.length; rowId++) {
						var rowData = {};
						for (var colId = 0; colId < columns.length; colId++) {
							var column = columns[colId];
							if (column.hidden || !column.titleName) {
								continue;
							}
							var columnTitle = column.titleName;
							var columnAliase = column.columnAliase;
							var columnValue = data[rowId][columnAliase];
							rowData[columnTitle] = columnValue;
						}
						addRowFn(rowId, rowData);
					}
				};
				var dataStore = new DataStore(options);
				dataStore.load(); // 装载数据

			}
		},
		// 渲染编辑元素
		renderEditableElem : function($editableElem) {
		},
		// 渲染文本元素
		renderLabelElem : function($labelElem) {
		},
		// 渲染后处理
		afterRender : function() {

		},
		setValue2LabelElem : function(value) {
		},
		// 设置字段是否必填
		setRequired : function(required) {
		},
		// 设置字段是否可编辑
		setEditable : function(editable) {
		},
		// 设置字段是否只读
		setReadonly : function(readonly) {
		},
		// 设置字段是否隐藏
		setHidden : function(hidden) {
		},
		// 显示验证错误信息
		showErrors : function(errors) {
		},
		hideErrors : function() {
		},
		// 获取数据数据新增、修改、删除的状态
		getState : function() {
		},
		// 返回控件DOM事件的元素
		getEventElem : function() {
		},
		setDisplayAsLabel : function() {

		}
	});
	return wViewdisplay;
});