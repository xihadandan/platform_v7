;
(function($) {
	/*
	 * POPUPWINDOW CLASS DEFINITION ======================
	 */
	var PopupWindow = function(element, options) {
		this.init("popupWindow", element, options);
	};

	PopupWindow.prototype = {
		constructor : PopupWindow,
		init : function(type, element, options) {
			this.$element = $(element);
			var $element = this.$element;
			this.options = this.getOptions(options);
			var options = this.options;
			var dialogId = $element.attr("id") + "_" + "dialog";
			var tableId = $element.attr("id") + "_" + "jqgrid";
			var tableDiv = "<div id='" + dialogId + "'>" + "<table id='" + tableId
					+ "'></table>" + "</div>";
			this.dialogId = dialogId;
			this.tableId = tableId;
			$(tableDiv).insertAfter($element);

			this.$element.click($.proxy(this.open, this));

			var $this = this;
			// 初始化窗口
			$("#" + this.dialogId)
					.dialog(
							{
								title : options.title,
								resizable : options.resizable,
								autoOpen : options.autoOpen,
								height : options.height,
								width : options.width,
								modal : options.modal,
								open : function() {
									$this._open();
								},
								close : function() {
									$this._close();
								},
								buttons : {
									"确定" : function() {
											var data = new Array();
											var rowids = $("#"+ $element.attr("id") + "_" + "jqgrid").jqGrid('getGridParam', 'selarrrow');
											if (rowids.length == 0) {
												alert("请选择备选项!");
												return;
											}else {
											for ( var i = (rowids.length - 1); i >= 0; i--) {
												var rowid = rowids[i];
												 a = $("#"+ $element.attr("id") + "_" + "jqgrid").getCell(rowid,"descValue");
												 b = $("#"+ $element.attr("id") + "_" + "jqgrid").getCell(rowid,"value");
												 var value = {"descName":a,"value":b};
												 	data.push(value);
											}
										
											if ($this.options.afterSelect) {
												$this.options.afterSelect.call(
														$this.$element[0],
														data);
											}
												$(this).dialog("close");
											}
										}
									},
									"取消" : function() {
										if ($this.options.afterCancel) {
											$this.options.afterCancel
													.call($this.$element[0]);
										}
										$(this).dialog("close");
									}
								}
							);

		},
		getOptions : function(options) {
			options = $.extend({}, $.fn.popupWindow.defaults, options,
					this.$element.data());
			return options;
		},
		open : function() {
			$("#" + this.dialogId).dialog("open");
		},
		_open : function() {
			var options = this.options;
			$("#"+ this.tableId).after("<div id='PAGE'></div>");
			$("#"+ this.tableId).jqGrid({
				url:contextPath + '/basicdata/dyview/getSelectData.action',
				postData:{
					tableType: this.options.initValues.tableType,
					formUuid: this.options.initValues.formUuid,
					fieldName: this.options.initValues.fieldName,
					page:this.options.initValues.currentPage,
					rows:10,
					defaultCondition:this.options.initValues.defaultCondition,
					value:this.options.initValues.value,
				},
				mtype:"POST",
				datatype:'json',
				colNames:['显示值','真实值'],
				colModel:[
					      {	   name:'descValue',
					           index:'descValue',
					        	   width:"100",
					        		   editable : true
				           },
				           {   name:'value',
					           index:'value',
					           
				           }, 
				         ],
				         	rowNum : 20,
							rownumbers : false,
							rowList : [ 10, 20, 50, 100, 200 ],
							rowId : "value",
							pager : "#PAGE",
							sortname : "descValue",
							viewrecords : true,
							sortable : true,
							sortorder : "asc",
							multiselect : true,
							autowidth : true,
							height : 200,
							scrollOffset : 0,
							cellEdit : true,// 表示表格可编辑
							cellsubmit : "clientArray", // 表示在本地进行修改
							autowidth : true,
							jsonReader : {
								root : "dataList",
								total : "totalPages",
								page : "currentPage",
								records : "totalRows",
								repeatitems : false
							},
							afterInsertRow : function(rowid, rowData)  {
								var values = options.initValues.value.split(";");//前台传递过来的真实值
								var labels = options.initValues.label.split(";");//前台传递过来的显示值
									for(var i=0;i<values.length;i++) {
										if(values[i] == rowData.value) {
											$(this).setRowData(rowid,{descValue:labels[i]});
											$(this).setSelection(rowid, true);
									}
								}
							}
			});
		},
		_close : function() {

		}
	};

	/*
	 * POPUPWINDOW PLUGIN DEFINITION =========================
	 */
	$.fn.popupWindow = function(option) {
		return this
				.each(function() {
					var $this = $(this), data = $this.data("popupWindow"), options = $
							.extend({}, typeof option == 'object'
									&& option);
					if (!data) {
						$this.data('popupWindow',
								(data = new PopupWindow(this, options)));
					}
					if (typeof option == 'string') {
						data[option]();
					}
				});
	};

	$.fn.popupWindow.Constructor = PopupWindow;

	$.fn.popupWindow.defaults = {
		title : "备选项",
		autoOpen : false,
		resizable : false,
		height : 400,
		width : 450,
		modal : true,
		initValues : null,
		enableTreeView : true
	};
})(jQuery);