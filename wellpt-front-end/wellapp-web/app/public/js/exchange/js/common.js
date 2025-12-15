//加载全局国际化资源
I18nLoader.load("/resources/pt/js/global");		
//加载exchange模块国际化资源
I18nLoader.load("/resources/exchange/js/exchange");
I18nLoader.load("/resources/pt/js/dytable/dytable");

$(function() {
	
	alert("s " + dymsg.fileUploadFailure);

	
	
	ExchangeUI = {
		initDocumentViewGrid : function(documentID) {
			// 绑定列表			
			var log_list_op = $.extend({}, ExchangeDefine.log_list_op);
			log_list_op.postData = { logaction : "log_list", docID : documentID };
			$(log_list_op.gid).GridUnload();
			ExchangeJqgrid.open(log_list_op);

			var track_list_op = $.extend({}, ExchangeDefine.track_list_op);
			track_list_op.postData = { logaction : "senderlist", docID : documentID };
			$(track_list_op.gid).GridUnload();
			ExchangeJqgrid.open(track_list_op);
			
		},
		bindDocumentViewForm : function(rowData, reloadGrid) {
			//var rowData = { documentUUID:'', senderUUID:'' };
			JDS.call({
				service : "documentService.getDocumentView",
				data : [ rowData.senderUUID, rowData.sendeeUUID ],
				success : function(result) {
					if (result.success) {
						$("#docView_docID").val(rowData.documentUUID);
						$("#docView_senderUUID").val(rowData.senderUUID);
						$("#docView_senderUser").val(result.data.senderUser);
						$("#docView_sendeeUUID").val(rowData.sendeeUUID);
						$("#docView_sendeeUser").val(result.data.sendeeUser);
						$("#docView_senderUserName").html(result.data.senderUserName);						
						$("#docView_sendeeUserName").html(result.data.sendeeUserName);
						$("#docView_senderTime").html(result.data.senderTime);
						$("#docView_docTitle").html(result.data.docTitle);
						$("#docView_docOption").html(result.data.docOption);
						$("#docView_docContent").html(result.data.docContent);
						
						$("#document_view_dialog").dialog("option", "title", result.data.docTitle );
						$("#document_view_dialog").dialog("open");
						ExchangeUI.initDocumentViewGrid(rowData.documentUUID);
						//权限控制移除
						if (!exconfig.showTraceUser) {
							$('#document_view_tab a[href="#tab2"]').remove();
						}
						if (!exconfig.showLogs) {
							$('#document_view_tab a[href="#tab3"]').remove();
						}
						//签收按钮控制
						if(jQuery.inArray(result.data.currentUser, result.data.jsSignUser.split(';')) > -1){
							$("#document_view_dialog").nextAll(".ui-dialog-buttonpane").find("button").filter(function() {
								return $(this).text() == exchange_button.sign;
							}).attr("disabled", true);
						}
						//办结按钮控制
						$("#document_view_dialog").nextAll(".ui-dialog-buttonpane").find("button").each(function() {
							if($(this).text() != exchange_button.close){
								
								if(result.data.jsFinish == "1"){
									$(this).attr("disabled", true);
								}else{
									$(this).attr("disabled", false);
								}
							}
						});
						
						
						//刷新Grid,因为设置已读了						
						if (reloadGrid) {
							$(reloadGrid).trigger("reloadGrid");
						}
					} else {
						alert(result.msg);
					}
				},
				error : function(result) {
					ExchangeHelper.hanlderError(result);
				}
			});
		},
		dialogButtonEnable : function(buttonText, enable){
			var dlgButton = $("#document_view_dialog").next(".ui-dialog-buttonpane button:contains('" + buttonText + "')");
			if (enable) {
				dlgButton.attr('disabled', '');
				dlgButton.removeClass('ui-state-disabled');
		    } else {
		    	dlgButton.attr('disabled', 'disabled');
		    	dlgButton.addClass('ui-state-disabled');
		    }
		},
		reloadDocumentViewGrid : function() {
			// 刷新表单
			var rowData = { documentUUID: $("#docView_docID").val()
					, senderUUID: $("#docView_senderUUID").val()
					, sendeeUUID: $("#docView_sendeeUUID").val() };
			ExchangeUI.bindDocumentViewForm(rowData);
			// 刷新Grid
			//$("#dispatch_list").trigger("reloadGrid");
			//$("#all_opinion_list").trigger("reloadGrid");
			//$("#feedback_opinion_list").trigger("reloadGrid");
			$("#log_list").trigger("reloadGrid");
			$("#track_list").trigger("reloadGrid");
			// 刷新列表
			$('#outbox_list').length ? $("#outbox_list").trigger("reloadGrid") : undefined;
			$('#inbox_list').length ? $("#inbox_list").trigger("reloadGrid") : undefined;
		},
		treeLoadSenders : function(uuid, hasSendee) {
			var treeSetting = {
				check : {
					enable : true
				},
				callback : {
					onCheck : ExchangeUI.treeSetSelectedRole
				}
			};

			JDS.call({
				service : "documentService.getTreeByDocumentID",
				data : [ uuid, hasSendee ],
				success : function(result) {
					var zTree = $.fn.zTree.init($("#sender_tree"), treeSetting,
							result.data);
					zTree.expandAll(true);
				}
			});
		},
		treeSetSelectedRole : function(e, treeId, treeNode) {
			var zTree = $.fn.zTree.getZTreeObj("sender_tree");
			var checkedNodes = zTree.getCheckedNodes(true);
			// 设置选中值
			var checkedSendees = new Array();
			var checkedSendeeNames = new Array();
			$.each(checkedNodes, function(i, n) {
				if (n.isParent)
					return true;// 排除父节点 |return false;//相当于break;
				checkedSendees.push(n.id);
				checkedSendeeNames.push(n.name);
			});
			$("#operate_sendee").val(checkedSendees.join(";"));
			$("#operate_sendeeName").val(checkedSendeeNames.join(";"));
		},
		treeLoadOutunit : function() {
			var treeSetting = {
				check : {
					enable : true
				},
				callback : {
					onCheck : ExchangeHelper.treeSetOutunit
				}
			};

			JDS.call({
				service : "outunitService.getTreeByOutunit",
				data : [],
				success : function(result) {
					var zTree = $.fn.zTree.init($("#outunit_tree"),
							treeSetting, result.data);
					zTree.expandAll(true);
				}
			});
		},
		treeSetOutunit : function(e, treeId, treeNode) {
			var zTree = $.fn.zTree.getZTreeObj("outunit_tree");
			var checkedNodes = zTree.getCheckedNodes(true);
			// 设置选中值
			var checkedSendees = new Array();
			var checkedSendeeNames = new Array();
			$.each(checkedNodes, function(i, n) {
				if (n.isParent)
					return true;// 排除父节点 |return false;//相当于break;
				checkedSendees.push(n.id);
				checkedSendeeNames.push(n.name);
			});
			$("#operate_sendee").val(checkedSendees.join(";"));
			$("#operate_sendeeName").val(checkedSendeeNames.join(";"));
		}
	};

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

	// 公交交换弹出操作框
	ExchangeDialog = {
		dlgID: null, //对话框ID
		dlgHandle: null, //对话框句柄
		afterEventHandle: null, //操作后事件
		extendData: null,
		buttons: [ {
			"text" : exchange_button.save,
			"class" : "btn",
			"click" : function() {
				
				//检查空值
				if (exconfig.allowLimitTime && ExchangeDialog.option.limit) {
					 if(ExchangeHelper.isEmptyValue($("#operate_form #operate_signlimit").val())){
						 alert(exchange_base.sign_limit + exchange_base.no_empty);
						 return;
					 }
					 if(ExchangeHelper.isEmptyValue($("#operate_form #operate_feedBacklimit").val())){
						 alert(exchange_base.feedback_limit + exchange_base.no_empty);
						 return;
					 }
				}
				//不能为空人员
				var sendeeArray = new Array("SENDADD","FORWARD","WITHDRAW","FINISH","REMIND");
				//不能为空意见
				var opinionArray = new Array("FEEDBACK","FORWARD");
				
				if($.inArray(ExchangeDialog.option.cmtype, opinionArray) > -1){
					if(ExchangeHelper.isEmptyValue($("#operate_form #operate_opinion").val())){
						alert(ExchangeDialog.option.cmopinion + exchange_base.no_empty);
						return;
					}
				}
				if($.inArray(ExchangeDialog.option.cmtype, sendeeArray) > -1){
					if(ExchangeHelper.isEmptyValue($("#operate_form #operate_sendee").val())){
						alert(ExchangeDialog.option.cmsendee + exchange_base.no_empty);
						return;
					}
				}
				
				
				//bean处理
				var dialogBean = {
					"documentUUID" : $("#docView_docID").val(),//$("#operate_form #operate_uuid").val(), // 文档uuid
					"senderUUID" : $("#docView_senderUUID").val(),//$("#operate_form #operate_senderuuid").val(), //
					"sendeeUUID" : $("#docView_sendeeUUID").val(),//$("#operate_form #operate_sendeeuuid").val(),
					"sendeeUser" : $("#operate_form #operate_sendee").val(), // 接收人id
					"opinion" : $("#operate_form #operate_opinion").val(), // 意见
					"type" : $("#operate_form #operate_type").val(), // 类型
					"signLimit" : $("#operate_form #operate_signlimit").val(),
					"feedBackLimit" : $("#operate_form #operate_feedBacklimit").val(),
					"attachment": $("#operate_form #operate_file_attach").val()
				};
				$.extend(dialogBean, ExchangeDialog.extendData);
				
				JDS.call({
					service : "documentService.saveOperate",
					data : [ dialogBean ],
					success : function(result) {
						if (result.success) {
							if(ExchangeDialog.afterEventHandle){
								ExchangeDialog.afterEventHandle();
							}
							//$("#operate_form").clearForm(true);
							//this.dlgHandle.dialog("close");
							ExchangeDialog.dlgHandle.remove();
							alert(exchange_base.success);
						} else {
							alert(result.msg);
						}
					},
					error : function(result) {
						ExchangeHelper.hanlderError(result);
					}
				});
			}
		}, {
			"text" : exchange_button.close,
			"class" : "btn",
			"click" : function() {
				//$("#operate_form").clearForm(true);
				//this.dlgHandle.dialog("close");
				ExchangeDialog.dlgHandle.remove();
				
			}
		} ],
		option : {
			title : "dialog",
			height : ExchangeDefine.exchange_box_height,
			width : ExchangeDefine.exchange_box_width,
			modal : true,
			resizable : false,
			external : true,
			draggable : true,
			limit : false,
			close: function(){
				ExchangeDialog.dlgHandle.remove();
			}
		// 办理时限
		},
		init: function(dlgid, options, afterEventHandle){
			this.dlgID = dlgid;
			this.afterEventHandle = afterEventHandle;
			
			var tempDialog = document.getElementById(dlgid);
			if (tempDialog != undefined && tempDialog != null) {
				this.dlgHandle = $(tempDialog);//如果有外部对象,则引用外部对象
			}else{
				//构建Dialog
				var boxContent = $("<div class='box-content'><form class='form-horizontal' id='operate_form'><fieldset></fieldset></form></div>");
				var boxField = boxContent.find("fieldset");
				boxField.append("<input type='hidden' id='operate_uuid' value='" + options.cmuuid + "' />");
				boxField.append("<input type='hidden' id='operate_type' value='"+ options.cmtype + "' />");
				// 意见框
				if (options.cmopinion) {
					boxField.append(
						"<div class='control-group'><label class='control-label' for='operate_opinion'> "
						+ options.cmopinion
						+ " </label><div class='controls'><textarea id='operate_opinion' rows='3' class='span12'></textarea></div></div>");
				}
				// 接收人
				if (options.cmsendee) {
					boxField.append(
						"<div class='control-group'><label class='control-label' for='operate_sendee'> "
						+ options.cmsendee
						+ " </label><div class='controls'><textarea id='operate_sendeeName' rows='3' class='span12'></textarea><input type='hidden' id='operate_sendee' name='operate_sendee' /></div></div>");
				}
				// 接收人树
				if (options.cmsendeetree) {
					boxField.append(
						"<div class='control-group' style='width: 200px; margin-left: 50px; float: left'><ul id='sender_tree' class='ztree'></ul></div>");
				}
				// 时限
				if (options.limit) {
					boxField.append(
						"<div class='control-group'><label class='control-label' for='operate_signlimit'>"
						+ exchange_base.sign_limit
						+ "</label><div class='controls'><input type='text' id='operate_signlimit'></div></div>");
					boxField.append(
						"<div class='control-group'><label class='control-label' for='operate_feedBacklimit'>"
						+ exchange_base.feedback_limit
						+ "</label><div class='controls'><input type='text' id='operate_feedBacklimit'></div></div>");
				}
				//附件
				if(options.fileupload){
					boxField.append(
							"<div class='control-group'><label class='control-label' for='operate_file'>"
							+ exchange_base.fileupload
							+ "</label><div class='controls'>"
							+ "<div id='operate_file' class='btn btn-success'><class='icon-upload icon-white'></i>选择文件</div>"
							+ "<div id='operate_file_messages'></div> <input type='hidden' id='operate_file_attach' value=''>"
							+ " </div></div>");
				}
				
				//加入页面
				$body = $("body");
				$div = $("<div class='row-fluid sortable'><div class='box span12'></div></div>");
				$div.attr("id", dlgid);
				$div.children().append(boxContent);
				$body.append($div);
				this.dlgHandle = $("#" + dlgid);
				
				// 相关处理
				// 接收人
				if (options.cmsendee) {
					$("#operate_sendeeName").click(function() {						 						
						$.unit.open({
							title : options.cmsendee,
							labelField : "operate_sendeeName",
							valueField : "operate_sendee",							
							excludeValues: options.cmexcute
						});
					});
				}
				if (options.cmsendeetree) {
					$('#operate_sendeeName').unbind("click"); // 移除事件
					$('#operate_sendeeName').attr("readonly","readonly");
					ExchangeUI.treeLoadSenders($("#docView_docID").val(), options.cmtreehaschild);
//					var setting = {
//						async : {
//						otherParam : {
//							"serviceName" : "documentService",
//							"methodName" : "getTreeByDocumentID",
//							"data" : $("#docView_docID").val()
//						}}
//					};
//					$("#operate_sendeeName").comboTree({
//						labelField : "operate_sendeeName",
//						valueField : "operate_sendee",
//						treeSetting : setting
//					});
				}
				if(options.fileupload){
					fileupload($('#operate_file_attach'),$('#operate_file'),$('#operate_file_messages'),'${ctx}');
				}
			}
			
		},
		open: function(dlgid, options, afterEventHandle) {
			this.init(dlgid, options, afterEventHandle);
			var op = $.extend({}, this.option, options);			
			// 补加扩展传参
			if (op.postData) {
				$.extend(this.extendData, op.postData);
			}
			var button = { buttons: this.buttons};
			this.option = $.extend(op, button);//按钮
			
			this.dlgHandle.dialog(op);
		}
	};

	// jqgrid
	ExchangeJqgrid = {
		option : {
			gid : null,
			url : null,
			mtype : "post",
			postData : undefined,
			datatype : "json", // 默认json数据类型
			autowidth : true,
			shrinkToFit : true, // 自动调整列宽
			// altRows: true, //隔行变色
			// altclass: 'altclass', //隔行变色样式
			width : "100%",
			height : "100%",
			multiselect : false, // 可以多选
			colNames : null,
			colModel : null,
			jsonReader : { // 数据解读器
				root : "dataList", // json中代表实际模型数据的入口
				page : "currentPage", // json中代表当前页码的数据
				total : "totalPages", // json中代表页码总数的数据
				records : "totalRows", // json中代表数据行总数的数据
				repeatitems : false
			},
			rowNum : 10, // 默认行数
			rowList : [ 10, 20, 30 ], // 行数设置集合
			rownumbers : true, // 显示行号
			rownumWidth : 35,
			viewrecords : true, // 显示总的记录条数
			ajaxGridOptions: {cache: false},
			pager : null,
			sortable : true,
			sortorder : "desc",
			sortname : null,
			subGrid : false,
			subGridRowExpanded : null
		},
		open : function(options) {
			var op = $.extend({}, this.option, options);			
			$(op.gid).jqGrid(op);// 表格初始化
		}
	};

	//表单
	$("#draft_form_dialog").dialog({
		autoOpen : false,
		modal : true,
		width : ExchangeDefine.exchange_dialog_width,
		height : ExchangeDefine.exchange_dialog_height,
		buttons : [{
			"text": exchange_button.send,
			"class": "btn",
			"click": function() {
				var dytableFormData = $("#draft_dytable").dytable("formData");
				var bean = $.extend({}, ExchangeDefine.FormDocumentBean);
				ExchangeHelper.formToObject($("#draft_form"), bean);				
				bean.saveDytableBean = dytableFormData;
				
				JDS.call({
					service : "draftService.saveSend",
					data : [ bean ],
					success : function(result) {
						if (result.success) {
							$("#draft_form_dialog").dialog("close");
							alert(exchange_base.success);
							//刷新
							if($("#draft_list").length > 0) $("#draft_list").trigger("reloadGrid");
							if($("#outbox_list").length > 0) $("#outbox_list").trigger("reloadGrid");							
						} else {
							alert(result.msg);
						}
					},
					error : function(result) {
						ExchangeHelper.hanlderError(result);
					}
				});
			}
		},{
			"text": exchange_button.save,
			"class": "btn",
			"click": function() {
				var dytableFormData = $("#draft_dytable").dytable("formData");
				var bean = $.extend({}, ExchangeDefine.FormDocumentBean);
				ExchangeHelper.formToObject($("#draft_form"), bean);
				bean.saveDytableBean = dytableFormData;
				
				JDS.call({
					service : "draftService.saveDraft",
					data : [ bean ],
					success : function(result) {
						if (result.success) {
							$("#draft_form_dialog").dialog("close");
							alert(exchange_base.success);
							//刷新
							if($("#draft_list").length > 0) $("#draft_list").trigger("reloadGrid");
							if($("#outbox_list").length > 0) $("#outbox_list").trigger("reloadGrid");
						} else {
							alert(result.msg);
						}
					},
					error : function(result) {
						ExchangeHelper.hanlderError(result);
					}
				});
			}
		},{
			"text": exchange_button.close,
			"class": "btn",
			"click": function() {
				$(this).dialog("close");
			}
		}]
	});
});