//加载全局国际化资源
I18nLoader.load("/resources/pt/js/global");		
//加载exchange模块国际化资源
I18nLoader.load("/resources/exchange/js/exchange");

$(function() {
	
	// 辅助类
	ExchangeHelper = {
		hanlderError : function(jqXHR) {
			if(!jqXHR) return;
			var msg = JSON.parse(jqXHR.responseText);
			if(!msg.success){
				alert(msg.msg);
			}
		},
		// with jQuery
		isEmptyValue : function(value) {
		    var type;
		    if(value == null) { // 等同于 value === undefined || value === null
		        return true;
		    }
		    type = Object.prototype.toString.call(value).slice(8, -1);
		    switch(type) {
		        case 'String':
		            return !$.trim(value);
		        case 'Array':
		            return !value.length;
		        case 'Object':
		            return $.isEmptyObject(value); // 普通对象使用 for...in 判断，有 key 即为 false
		        default:
		            return false; // 其他对象均视作非空
		    }
		},
		// cms 列,是否多选,返回逗号分隔值
		getCheckedUUID: function(single){
			var checkedNodes = $("input[class=checkeds]:checked");
			if(checkedNodes.length < 1){
				alert(global.selectARecord);
				return false;
			}
			if(single && checkedNodes.length > 1){
				alert(exchange_base.checkbox_single);
				return false;
			}
			var checkedUUID = new Array();
			$("input[class=checkeds]:checked").each(function() {
				//checkedUUID.push($(this).val());
				checkedUUID.push($(this).parent().next().html());
			});
			return checkedUUID.join(",");
		},
		objectToForm : function(data, form) {
			// iterate over all of the inputs for the form
			// element that was passed in
			$(':input', form)
					.each(
							function() {
								if (data.hasOwnProperty(this.id)) {
									var type = this.type;
									var tag = this.tagName.toLowerCase(); // normalize
									// case
									// it's ok to reset the value attr of text
									// inputs,
									// password inputs, and textareas
									if (type == 'hidden' || type == 'text' || type == 'password' || tag == 'textarea') {
										this.value = data[this.id] == null ? "" : data[this.id];
									}
									// checkboxes and radios need to have their
									// checked state
									// cleared
									// but should *not* have their 'value'
									// changed
									else if (type == 'checkbox' || type == 'radio') {
										//this.checked = (data[this.id] == true || data[this.id] == "true" || data[this.id] == "TRUE");
										var checkedState = (data[this.id] == true || data[this.id] == "true" || data[this.id] == "TRUE");
										if(checkedState){
											$(this).prop('checked', true);									    
											$(this).parents('span').addClass('checked');	
										}										
										//$(this).parents('label').toggleClass('active');
									}
									// select elements need to have their
									// 'selectedIndex' property
									// set
									// to -1
									// (this works for both single and multiple
									// select elements)
									else if (tag == 'select') {
										// this.selectedIndex = -1;
										this.value = data[this.id];
									}
								}
							});
		},
		formToObject : function(form, object) {
			// iterate over all of the inputs for the form
			// element that was passed in
			$(':input', form)
					.each(
							function() {
								if (object.hasOwnProperty(this.id)) {
									var type = this.type;
									var tag = this.tagName.toLowerCase(); // normalize
									// case
									// it's ok to reset the value attr of text
									// inputs,
									// password inputs, and textareas
									if (type == 'hidden' || type == 'text' || type == 'password' || tag == 'textarea') {
										object[this.id] = this.value;
									}
									// checkboxes and radios need to have their
									// checked state
									// cleared
									// but should *not* have their 'value'
									// changed
									else if (type == 'checkbox' || type == 'radio') {
										object[this.id] = this.checked;
									}
									// select elements need to have their
									// 'selectedIndex' property
									// set
									// to -1
									// (this works for both single and multiple
									// select elements)
									else if (tag == 'select') {
										// this.selectedIndex = -1;
										object[this.id] = this.value;
									}
								}
							});
		}
	};
	
	
	// 定义类
	ExchangeDefine = {
		exchange_dialog_width : 950,
		exchange_dialog_height : 500,
		exchange_box_width : 500,
		exchange_box_height : 500,
		jqgrid_striking_row: {"background":"red","color":"yellow","font-weight":"bold"},
		FormDocumentBean : {
			"documentUUID" : null,
			"formUUID" : null,
			"dataUUID" : null,			
			"dytableData" : null,
			"opinion" : null,
			"otype" : null,
			"ouuids" : null
		},
		log_list_op : {
			gid : "#log_list",
			url : ctx + '/exchange/log/jqgrid',
			colNames : [ "docID", exchange_base.name, exchange_base.time, exchange_base.operate_user, exchange_base.operate_obj,
					exchange_base.content, exchange_base.attachment, exchange_base.sign_limit_timeout, exchange_base.feedback_limit_timeout ],
			colModel : [ {
				"name" : "docID",
				"index" : "docID",
				"hidden" : true,
				"key" : true,
			}, {
				"name" : "logType",
				"index" : "logType",
				"width" : "180"
			}, {
				"name" : "operateTime",
				"index" : "operateTime",
				"width" : "180"
			}, {
				"name" : "operateUser",
				"index" : "operateUser",
				"width" : "180"
			}, {
				"name" : "operateTo",
				"index" : "operateTo",
				"width" : "180"
			}, {
				"name" : "content",
				"index" : "content",
				"width" : "180"
			}, {
				"name" : "attachment",
				"index" : "attachment",
				"width" : "180"
			}, {
				"name" : "signTimeout",
				"index" : "signTimeout",
				"width" : "180"
			}, {
				"name" : "feedbackTimeout",
				"index" : "feedbackTimeout",
				"width" : "180"
			} ],
			sortname : "operateTime",
			gridComplete: function(){
	        	if (!exconfig.allowLimitTime) {
					$("#log_list").jqGrid("hideCol", "signTimeout");
					$("#log_list").jqGrid("hideCol", "feedbackTimeout");
				}
	        	$('#log_list').setGridWidth($(".tab-content").width());	        	
	        }
		},		
		track_list_op : {
			gid : "#track_list",
			url : ctx + '/exchange/readtrack/jqgrid',
			colNames : [ "senderUUID", exchange_base.time, exchange_base.sender, exchange_base.cate ],
			colModel : [ {
				"name" : "senderUUID",
				"index" : "senderUUID",
				"hidden" : true,
				"key" : true,
			}, {
				"name" : "senderTime",
				"index" : "senderTime",
				"width" : "180"
			}, {
				"name" : "senderUserName",
				"index" : "senderUserName",
				"width" : "180"
			}, {
				"name" : "sendType",
				"index" : "sendType",
				"width" : "180"
			} ],
	        gridComplete: function(){
	        	$('#track_list').setGridWidth($(".tab-content").width());
	        },
			sortname : "senderTime",
			subGrid : true, // (1)开启子表格支持
			subGridRowExpanded : function(subgrid_id, row_id) { // (2)子表格容器的id和需要展开子表格的行id，将传入此事件函数
				var subgrid_table_id = subgrid_id + "_t"; // (3)根据subgrid_id定义对应的子表格的table的id
				// (5)动态添加子报表的table和pager
				$("#" + subgrid_id).html("<table id='" + subgrid_table_id + "' class='scroll'></table>");
				// (6)创建jqGrid对象
				$("#" + subgrid_table_id).jqGrid(
						{
							url : ctx + '/exchange/readtrack/jqgrid', // (7)子表格数据对应的url，注意传入的contact.id参数
							postData : {
								logaction : "sendeelist",
								senderID : row_id
							},
							mtype : "post",
							datatype : "json",
							colNames : [ exchange_base.sendee, exchange_base.readed_time, exchange_base.feedback_limit
							             , exchange_base.feedback_opinion, exchange_base.attachment
							             , exchange_base.sign_limit_timeout, exchange_base.feedback_limit_timeout ],
							colModel : [ {
								name : "sendeeUserName",
								index : "sendeeUserName",
								width : 80,
								key : true
							}, {
								name : "readTime",
								index : "readTime",
								width : "180"
							}, {
								name : "feedBackTime",
								index : "feedBackTime",
								width : "180"
							}, {
								name : "feedBackOpinion",
								index : "feedBackOpinion",
								width : "180"
							}, {
								name : "attachment",
								index : "attachment",
								width : "180"
							}, {
								"name" : "signTimeout",
								"index" : "signTimeout",
								"width" : "180"
							}, {
								"name" : "feedbackTimeout",
								"index" : "feedbackTimeout",
								"width" : "180"
							} ],
							jsonReader : { // (8)针对子表格的jsonReader设置
								"root" : "dataList",// json中代表实际模型数据的入口
								"page" : "currentPage", // json中代表当前页码的数据
								"total" : "totalPages", // json中代表页码总数的数据
								"records" : "totalRows", // json中代表数据行总数的数据
								"repeatitems" : false
							},
							viewrecords : true,
							height : "100%",
							width : "100%",
							rowNum : 10,
							autowidth : true,
							gridComplete: function(){
					        	if (!exconfig.allowLimitTime) {
					        		$("#" + subgrid_table_id).jqGrid("hideCol", "signTimeout");
					        		$("#" + subgrid_table_id).jqGrid("hideCol", "feedbackTimeout");
								}
					        }
						});
			}
		}
	};
});