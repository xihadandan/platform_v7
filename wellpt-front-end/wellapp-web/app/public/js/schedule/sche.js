I18nLoader.load("/resources/schedule/global");

// 获取cookie
window.flagWindow = 0;
var cld_num = 0;
var dialog_name = "";
var save_cancel_flag = true;
function getCookie(name) {
	var arr = document.cookie
			.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
	if (arr) {
		return decodeURI(arr[2]);
	} else {
		return null;
	}
}
// 确保contextPath有值
function getContextPath() {
	if (window.ctx == null || window.contextPath == null) {
		window.contextPath = getCookie("ctx");
		window.contextPath = window.contextPath == "\"\"" ? ""
				: window.contextPath;
		window.ctx = window.contextPath;
	}
	return window.ctx;
}
getContextPath();
// 日程是否完成标识
var isCompleted = "0";

// 查看个人日程
function openScheduleDialog(viewType, ctype, date, currentUserId, tagId, uuid,
		scheduleName, address, startDate, startTime, endDate, endTime,
		isComplete, respon, responIds, pleases, pleaseIds, color, tip, tipDate,
		tipTime, tipMethod, repeat, creators, creatorIds, inviteeNames,
		inviteeIds, acceptIds, acceptNames, refuseIds, refuseNames, tagName,
		content, sourceUuid, noticMethod) {

	var permission = false;

	// 权限判断
	if (creatorIds.indexOf(currentUserId) != -1
			|| responIds.indexOf(currentUserId) != -1) {
		permission = true;
	}
	var btns = {};
	if (permission && 1 != isComplete) {
		btns.邀请 = inviteeSchedule;
	}
	btns.转发 = copySchedule;

	if (permission && 1 != isComplete) {
		isCompleted = "1";
		btns.完成 = completeSchedule;
	}
	if (permission) {
		btns.删除 = deleteSchedule;
	}
	if (permission && 1 != isComplete) {
		btns.编辑 = invalid;
	}

	if (permission && 1 == isComplete) {
		isCompleted = "0";
		btns.重新启动 = completeSchedule;
	}
	btns.保存 = addSchedule;
	var scheduleJson = {
		"viewType" : viewType,
		"ctype" : ctype,
		"dateStr" : date,
		"tagId" : tagId,
		"uuid" : uuid,
		"scheduleName" : scheduleName,
		"address" : address,
		"startDate" : startDate,
		"startTime" : startTime,
		"endDate" : endDate,
		"endTime" : endTime,
		"respon" : respon,
		"isComplete" : isComplete,
		"responIds" : responIds,
		"pleases" : pleases,
		"pleaseIds" : pleaseIds,
		"color" : color,
		"tip" : tip,
		"tipDate" : tipDate,
		"tipTime" : tipTime,
		"tipMethod" : tipMethod,
		"repeat" : repeat,
		"creators" : creators,
		"creatorIds" : creatorIds,
		"inviteeNames" : inviteeNames,
		"inviteeIds" : inviteeIds,
		"acceptIds" : acceptIds,
		"acceptNames" : acceptNames,
		"refuseIds" : refuseIds,
		"refuseNames" : refuseNames,
		"tagName" : tagName,
		"content" : content,
		"permission" : permission,
		"sourceUuid" : sourceUuid,
		"noticMethod" : noticMethod
	};
	var dialogcontent = getScheduleHtml(scheduleJson);
	dialog_name = "";
	var title = urldecode(scheduleName);
	if (title.length > 20) {
		title = title.substring(0, 30) + "...";
	}
	var json = {
		title : title,
		/** ***标题***** */
		autoOpen : true,
		/** ***初始化之后，是否立即显示对话框***** */
		modal : true,
		/** ***是否模式对话框***** */
		closeOnEscape : true,
		/** *当按 Esc之后，是否应该关闭对话框*** */
		draggable : true,
		/** ***是否允许拖动***** */
		resizable : true,
		/** ***是否可以调整对话框的大小***** */
		stack : false,
		/** ***对话框是否叠在其他对话框之上***** */
		height : 610,
		/** ***弹出框高度***** */
		width : 780,
		/** ***弹出框宽度***** */
		defaultBtnName : '取消',
		content : dialogcontent,
		/** ***内容***** */
		// open：事件,
		buttons : btns
	};

	showDialog(json);
	initFileuploadSche2(uuid, true);

	$('#scheTag').val(tagId);
	$('#scheduleName').val(urldecode(scheduleName));
	$('#address').val(urldecode(address));
	$('#content').val(urldecode(content));
	$('#flag').val(1);
	// showScheduleDialog (title,content,300,600);
	unvalid();
}
// 消息调用的新建事件
function msgAddSchedule(schejson) {
            // alert(schejson.repeatType);
	// 开始日期及时间
	var startDateStr = schejson.scheduleDates;
	var startDate = getDateStr();
	var startTime = getTimeHHMM();
	if (startDateStr.length == 10) {
		startDate = startDateStr;
	} else if (startDateStr.length >= 16 && startDateStr.length <= 19) {
		startDate = startDateStr.substring(0, 10);
		startTime = startDateStr.substring(11, 16);
	}
	// 结束日期及时间
	var endDateStr = schejson.scheduleDatee;
	var endDate = '';
	var endTime = '';
	if (endDateStr.length == 10) {
		endDate = endDateStr;
	} else if (endDateStr.length >= 16 && endDateStr.length <= 19) {
		endDate = endDateStr.substring(0, 10);
		endTime = endDateStr.substring(11, 16);
	}
	// 提醒日期及时间
	var tipDateStr = schejson.reminderTime;
	var tip = '1';
	var tipDate = getDateStr();
	var tipTime = getTimeHHMM();
	if (tipDateStr.length == 16 || tipDateStr.length == 18) {
		tipDate = tipDateStr.substring(0, 10);
		tipTime = tipDateStr.substring(11, 16);
	}
	var scheduleJson = {
		"viewType" : "",
		"ctype" : "",
		"dateStr" : '',
		"uuid" : '',
		"tagId" : '',
		"tagName" : '',
		"scheduleName" : '',
		"address" : '',
		"startDate" : startDate,
		"startTime" : startTime,
		"endDate" : endDate,
		"endTime" : endTime,
		"respon" : schejson.userName,
		"responIds" : schejson.userid,
		"pleases" : '',
		"pleaseIds" : '',
		"color" : '',
		"tip" : tip,
		"tipDate" : tipDate,
		"tipTime" : tipTime,
		"tipMethod" : ',3,',
		"repeat" : schejson.repeatType,
		"creators" : schejson.userName,
		"creatorIds" : schejson.userid,
		"inviteeNames" : '',
		"inviteeIds" : '',
		"acceptIds" : '',
		"acceptNames" : '',
		"refuseIds" : '',
		"refuseNames" : '',
		"content" : '',
		"isComplete" : '',
		'sourceUuid' : schejson.dataUuid,
		'noticMethod' : ',3,'
	};
	cld_num = new UUID();
	var content = getScheduleHtml(scheduleJson);
	var odialogTag = ".odialog" + cld_num;
	var odialog = '<div class="odialog' + cld_num + '" style="display:none;">'
			+ content + '</div>';
	var json = {
		title : scheglobal.addSchedule,
		/** ***标题***** */
		autoOpen : true,
		/** ***初始化之后，是否立即显示对话框***** */
		modal : true,
		/** ***是否模式对话框***** */
		closeOnEscape : true,
		/** *当按 Esc之后，是否应该关闭对话框*** */
		draggable : true,
		/** ***是否允许拖动***** */
		resizable : true,
		/** ***是否可以调整对话框的大小***** */
		stack : false,
		/** ***对话框是否叠在其他对话框之上***** */
		height : 610,
		/** ***弹出框高度***** */
		width : 780,
		/** ***弹出框宽度***** */
		content : content,
		/** ***内容***** */
		// open：事件,
		buttons : {
			"保存" : addSchedule
		}
	};
	$("body").after(odialog);
	$(odialogTag).oDialog(json);
	save_cancel_flag = false;
	dialog_name = odialogTag + " ";
	$(dialog_name + '#scheduleName').val(schejson.scheduleTitle);
	$(dialog_name + '#address').val(schejson.scheduleAddress);
	$(dialog_name + '#content').val(schejson.scheduleBody);
	var ctlID = "fileupload_schedule" + cld_num;
	$("#uploadcld_h").val(ctlID);
	var fileupload = new WellFileUpload(ctlID);
	fileupload
			.initWithLoadFilesFromFileSystem(false, $(dialog_name
					+ "#annex_fileupload"), false, true, schejson.uuid,
					"messageAttach");
	// fileupload.init(false,$("#annex_fileupload"),false, true, []);
	// 初始化常用地点的选择树
	$(dialog_name + '#address').comboTree({
		labelField : 'moduleName',
		valueField : 'moduleId',
		width : 450,
		height : 250,
		treeSetting : setting3,
		initServiceParam : [ 'MODULE_ID' ]
	});
}

function openNewSchedule(viewType, ctype, date, tagId, tagName, tagUserId,
		tagUserName, userId, userName) {
	dialog_name = "";
	var scheduleJson = {
		"viewType" : viewType,
		"ctype" : ctype,
		"dateStr" : date,
		"uuid" : '',
		"tagId" : tagId,
		"tagName" : tagName,
		"scheduleName" : '',
		"address" : '',
		"startDate" : date,
		"startTime" : getTimeHHMM(),
		"endDate" : '',
		"endTime" : '',
		"respon" : userName,
		"responIds" : userId,
		"pleases" : '',
		"pleaseIds" : '',
		"color" : '',
		"tip" : '',
		"tipDate" : '',
		"tipTime" : '',
		"tipMethod" : ',3,',
		"repeat" : '',
		"creators" : userName,
		"creatorIds" : userId,
		"inviteeNames" : '',
		"inviteeIds" : '',
		"acceptIds" : '',
		"acceptNames" : '',
		"refuseIds" : '',
		"refuseNames" : '',
		"content" : '',
		"isComplete" : '',
		'sourceUuid' : '',
		'noticMethod' : ',3,',
	};
	if (ctype.indexOf("3") == 0) {
		scheduleJson.respon = tagUserName;
		scheduleJson.responIds = tagUserId;
	}

	var content = getScheduleHtml(scheduleJson);
	var json = {
		title : scheglobal.addSchedule,
		/** ***标题***** */
		autoOpen : true,
		/** ***初始化之后，是否立即显示对话框***** */
		modal : true,
		/** ***是否模式对话框***** */
		closeOnEscape : true,
		/** *当按 Esc之后，是否应该关闭对话框*** */
		draggable : true,
		/** ***是否允许拖动***** */
		resizable : true,
		/** ***是否可以调整对话框的大小***** */
		stack : false,
		/** ***对话框是否叠在其他对话框之上***** */
		height : 610,
		/** ***弹出框高度***** */
		width : 780,
		/** ***弹出框宽度***** */
		content : content,
		/** ***内容***** */
		// open：事件,
		buttons : {
			"保存" : addSchedule
		}
	};
	showDialog(json);

	$('#scheTag').val(tagId);
	// 初始化常用地点的选择树
	$('#address').comboTree({
		labelField : 'moduleName',
		valueField : 'moduleId',
		required : true,
		width : 450,
		height : 250,
		treeSetting : setting3,
		initServiceParam : [ 'MODULE_ID' ]
	});
	initFileuploadSche();
}

function addSchedule() {

	var ctype = $(dialog_name + '#ctype').val();
	var viewType = $(dialog_name + "#viewType").val();

	var scheduleName = $(dialog_name + '#scheduleName').val().replace(
			/(^\s*)|(\s*$)/g, '');
	var dateStr = $(dialog_name + '#dateStr').val();
	var tagId = $(dialog_name + "#scheTag").val();

	if (scheduleName == '') {
		oAlert2(scheglobal.subjectNotNull, function() {
			$(dialog_name + '#scheduleName').focus();
		});
		return false;
	}
	var address = $(dialog_name + '#address').val();
	var startTime = $().val();

	var startDate = $(dialog_name + '#startDate').val();
	if (startDate == '') {
		oAlert2(scheglobal.startDateNotNull, function() {
			$(dialog_name + '#startDate').focus();
		});
		return false;

	}

	/*
	 * if("0"==isCompleted){ var endtime1=$(dialog_name+'#endTime').val(); var
	 * startTime1=$(dialog_name+'#startTime').val(); var
	 * endDate1=$(dialog_name+'#endDate').val();
	 * if(startTime1==timeOld&&startDate==startDateOld&&endTimeOld==endtime1&&endDate1==endDateOld){
	 * oConfirm("您的时间未变化，是否需要改变？",function(){
	 * $(dialog_name+'#startDate').focus(); }); return false; } }
	 */
	var quan = $(dialog_name + "#quan").attr("checked") == 'checked' ? "1"
			: "0";
	var jieshu = $(dialog_name + "#jieshu").attr("checked") == 'checked' ? "1"
			: "0";
	var isNowTip = $(dialog_name + "#isNowTip").attr("checked") == 'checked' ? "1"
			: "0";
	var startTime = "", endDate = "", endTime = "";

	if (quan == 0) {
		startTime = $(dialog_name + '#startTime').val();
		if (startTime == '') {
			oAlert2(scheglobal.startTimeNotNull, function() {
				$(dialog_name + '#startTime').focus();
			});
			return false;
		}
	}
	if (jieshu == 1) {
		endDate = $(dialog_name + '#endDate').val();
		if (endDate == '') {
			oAlert2(scheglobal.endDateNotNull, function() {
				$(dialog_name + '#endDate').focus();
			});
			return false;
		}
	}
	if (quan == 0 && jieshu == 1) {
		endTime = $(dialog_name + '#endTime').val();
		if (endTime == '') {
			oAlert2(scheglobal.endTimeNotNull, function() {
				$(dialog_name + '#endTime').focus();
			});
			return false;
		}
	}
	if (tagId == '' || tagId == null) {
		oAlert2(scheglobal.checkAttentionCalendar);
		return false;
	}
	var color = $(dialog_name + "#mycolor").val();
	var isComplete = $(dialog_name + "#isComplete").val();
	var responIds = $(dialog_name + '#responIds').val();
	var respon = $(dialog_name + '#respon').val();
	if (responIds == '') {
		oAlert2(scheglobal.initiatorNotNull, function() {
			$(dialog_name + '#respon').focus();
		});
		return false;
	}
	var pleases = $(dialog_name + '#pleases').val();
	var pleaseIds = $(dialog_name + '#pleaseIds').val();
	var creatorIds = $(dialog_name + '#creatorIds').val();
	var creators = $(dialog_name + '#creators').val();
	var inviteeIds = $(dialog_name + "#inviteeIds").val();
	var inviteeNames = $(dialog_name + "#inviteeNames").val();
	var uuid = $(dialog_name + '#scheduleUuid').val();
	var tip = $(dialog_name + '#tip').val();
	var tipTime = $(dialog_name + '#tipTime').val();
	var tipDate = $(dialog_name + '#tipDate').val();
	var repeat = $(dialog_name + "#repeat").val();
	// 邀请自己的提醒方式
	var tipMethod = ",";
	$(dialog_name + ".tipMethod:checked").each(function() {
		tipMethod += $(this).val() + ",";
	});
	// 被邀请的人提醒方式
	var noticMethod = ",";
	$(dialog_name + ".noticMethod:checked").each(function() {
		noticMethod += $(this).val() + ",";
	});
	// alert(noticMethod);
	var content = $(dialog_name + "#content").val();
	var sourceUuid = $(dialog_name + "#sourceUuid").val();
	var cld = $("#uploadcld_h").val();
	var acheduleAttach = WellFileUpload.files[cld];
	var scheduleAttachId = "";

	for (i in acheduleAttach) {
		scheduleAttachId += "," + acheduleAttach[i].fileID;
	}
	scheduleAttachId = scheduleAttachId.substring(1);
	deletedAttachids = deletedAttachids.substring(1);
	pageLock("show");
	$.ajax({
		type : "POST",
		url : contextPath + "/schedule/schedule_save",
		data : {
			"tipMethod" : tipMethod,
			"creators" : creators,
			"creatorIds" : creatorIds,
			"tip" : tip,
			"tipTime" : tipTime,
			"tipDate" : tipDate,
			"repeat" : repeat,
			"color" : color,
			"uuid" : uuid,
			"scheduleName" : scheduleName,
			"address" : address,
			"dstartDate" : startDate,
			"startTime" : startTime,
			"dendDate" : endDate,
			"endTime" : endTime,
			"respon" : respon,
			"responIds" : responIds,
			"pleases" : pleases,
			"pleaseIds" : pleaseIds,
			"content" : content,
			"isNowTip" : isNowTip,
			"inviteeIds" : inviteeIds,
			"inviteeNames" : inviteeNames,
			"tag.uuid" : tagId,
			"scheduleAttach" : scheduleAttachId,
			"deletedAttachids" : deletedAttachids,
			"sourceUuid" : sourceUuid,
			"noticMethod" : noticMethod
		},
		dataType : "text",
		success : function(result) {
			if (viewType == '1' || viewType == '2') {
				oAlert2(scheglobal.saveSuccess);
				if (save_cancel_flag) {
					closeDialog();
					$.ajax({
						type : "post",
						async : false,
						data : {
							"ctype" : ctype,
							"dateStr" : dateStr,
							"viewType" : viewType,
							"tagId" : tagId
						},
						url : ctx + "/schedule/month/view",
						success : function(result) {
							pageLock("hide");

							refreshSchedule(result);
						}
					});
					save_cancel_flag = true;
				}
			} else {
				oAlert2("保存成功", function() {
					location.reload();
				});
			}

		},
		error : function(data, status, e) {
			pageLock("hide");
			oAlert2(e);
			
		}
	});
	

}

function refreshSchedule(rightContentHtml) {
	$(".ui-dialog").remove();
	$("#toolbar").parent().parent().html(rightContentHtml);
}

// 获得日程HTML
function getScheduleHtml(scheduleJson) {
	if (scheduleJson.color == '')
		scheduleJson.color = "black";
	scheduleJson.inviteeNames = scheduleJson.acceptNames
			+ scheduleJson.refuseNames + scheduleJson.inviteeNames;
	scheduleJson.inviteeIds = scheduleJson.acceptIds + scheduleJson.refuseIds
			+ scheduleJson.inviteeIds;
	if (scheduleJson.inviteeNames.indexOf(";") != -1) {
		scheduleJson.inviteeNames = scheduleJson.inviteeNames.substr(0,
				scheduleJson.inviteeNames.length - 1);
	}
	if (scheduleJson.inviteeIds.indexOf(";") != -1) {
		scheduleJson.inviteeIds = scheduleJson.inviteeIds.substr(0,
				scheduleJson.inviteeIds.length - 1);
	}
	var content = "<div id='test_con3'>"
			+ "<input type='hidden' id='scheduleUuid' name='scheduleUuid' value='"
			+ scheduleJson.uuid
			+ "' />"
			// +"<input type='hidden' id='scheUuid' name='scheUuid'
			// value='"+scheduleJson.uuid+"'/> "
			+ "<input type='hidden' id='ctype' name='ctype' value='"
			+ scheduleJson.ctype
			+ "'/>"
			+ "<input type='hidden' id='dateStr' name='dateStr' value='"
			+ scheduleJson.dateStr
			+ "'/>"
			+ "<input type='hidden' id='viewType' name='viewType' value='"
			+ scheduleJson.viewType
			+ "'/>"
			+ "<input type='hidden' id='sourceUuid' name='sourceUuid' value='"
			+ scheduleJson.sourceUuid
			+ "'/>"
			/*
			 * +"<input type='hidden' id='scheTag'
			 * value='"+scheduleJson.tagId+"'/>"
			 */
			+ "<input type='hidden' id='isComplete' value='"
			+ scheduleJson.isComplete
			+ "'/>"
			// 设置隐藏域 便于区别查看和编辑
			+ "<input type='hidden' id='flag' name='flag' />"

			+ "<table class=''>"

			+ "<tr class='dialog_tr tr_odd' >"
			+ "<td align='left' width='20%'  class='left_td'>"
			+ scheglobal.title
			+ "</td>"
			+ "<td class='td_align_left'><input  type='text' size='50' maxlength='50' id='scheduleName' name='scheduleName' value='' />";

	content += "<select id='scheTag' >";

	if (scheduleJson.ctype.indexOf("3") == 0) {
		content += "<option value='" + scheduleJson.tagId + "' >"
				+ scheduleJson.tagName + "</option>";
	} else {
		var userTag = getUserTag();
		for ( var key in userTag) {
			content += "<option value='" + userTag[key].uuid
					+ "' isLeaderView='" + userTag[key].isLeaderView
					+ "' isView='" + userTag[key].isView + "' viewNames='"
					+ userTag[key].viewNames + "' viewIds='"
					+ userTag[key].viewIds + "' style='background-color:"
					+ userTag[key].color + ";' >" + userTag[key].name
					+ "</option>";
		}
	}
	content += "</select>";

	content += "<div class='chosecolor'>"
			+ "<input type='hidden' id='mycolor' name='mycolor' value='"
			+ scheduleJson.color
			+ "'/>"
			+ "<span id='showcolor' style='background-color:"
			+ scheduleJson.color
			+ ";'></span>"
			+ "</div>"
			+ "<div class='colorbutton' onclick='choseColor(this)'>"
			+ "<div class='colors' style='display:none;'>"
			+ "<span class='selectcolor' style='background-color:black;' onclick=\"selColor('black');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:red ' onclick=\"selColor('red');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:blue ' onclick=\"selColor('blue');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:orange ' onclick=\"selColor('orange');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:green ' onclick=\"selColor('green');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:yellow ' onclick=\"selColor('yellow');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:purple ' onclick=\"selColor('purple');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:silver ' onclick=\"selColor('silver');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:tan ' onclick=\"selColor('tan');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:maroon;' onclick=\"selColor('maroon');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:olive;' onclick=\"selColor('olive');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:gray;' onclick=\"selColor('gray');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:lime;' onclick=\"selColor('lime');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:white;' onclick=\"selColor('white');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:aqua;' onclick=\"selColor('aqua');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:fuchsia;' onclick=\"selColor('fuchsia');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:navy;' onclick=\"selColor('navy');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:teal;' onclick=\"selColor('teal');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "</div>"
			+ "</div>"
			+ "</td></tr>"

			+ "<tr class='dialog_tr tr_even'>"
			+ "<td align='left' width='20%' class='left_td'>"
			+ scheglobal.time
			+ "</td>"
			+ "<td class='td_align_left'>"
			+ "<input type='text' size='10' id='startDate'  name='startDate' class='date_ipt' value='"
			+ scheduleJson.startDate + "'  onclick='WdatePicker();' />";
	if (scheduleJson.startTime != '') {
		content += "<input  type='text' size='10' id='startTime' class='time_ipt' name='startTime' value='"
				+ scheduleJson.startTime
				+ "'  onclick=\"WdatePicker({dateFmt:'HH:mm'});\" />";
	} else {
		content += "<input  type='text' size='10' id='startTime' class='time_ipt'  name='startTime' value='"
				+ getTimeHHMM()
				+ "'  onclick=\"WdatePicker({dateFmt:'HH:mm'});\" style='display:none;' />";
	}
	if (scheduleJson.endDate != '') {
		content += "<div id='startToEnd' style='padding-top:9px;float:left;'>-</div>"
				+ "<input  type='text' size='10' id='endDate' class='date_ipt' name='endDate' value='"
				+ scheduleJson.endDate + "'  onclick='WdatePicker();' />";
	} else {
		content += "<div id='startToEnd' style='padding-top:9px;float:left;display:none;'>-</div>"
				+ "<input  type='text' size='10' id='endDate' class='date_ipt' name='endDate' value='"
				+ scheduleJson.startDate
				+ "'  onclick='WdatePicker();' style='display:none;'/>";
	}
	if (scheduleJson.endTime != '') {
		content += "<input type='text' size='10'  id='endTime' class='time_ipt' name='endTime' value='"
				+ scheduleJson.endTime
				+ "'  onclick=\"WdatePicker({dateFmt:'HH:mm'});\" />";
	} else {
		content += "<input  type='text' size='10' id='endTime' class='time_ipt' name='endTime'   value='"
				+ getTimeHHMM()
				+ "'  onclick=\"WdatePicker({dateFmt:'HH:mm'});\" style='display:none'/>";
	}
	content += "<div style='float:left;'><select id='repeat' name='repeat' value='"
			+ scheduleJson.repeat + "' >";
	if (scheduleJson.repeat == '1') {
		content += "<option value='1' selected='selected'>"
				+ scheglobal.repeat1 + "</option>";
	} else {
		content += "<option value='1'>" + scheglobal.repeat1 + "</option>";
	}
	if (scheduleJson.repeat == '2') {
		content += "<option value='2' selected='selected'>"
				+ scheglobal.repeat2 + "</option>";
	} else {
		content += "<option value='2'>" + scheglobal.repeat2 + "</option>";
	}
	if (scheduleJson.repeat == '3') {
		content += "<option value='3' selected='selected'>"
				+ scheglobal.repeat3 + "</option>";
	} else {
		content += "<option value='3'>" + scheglobal.repeat3 + "</option>";
	}
	if (scheduleJson.repeat == '4') {
		content += "<option value='4' selected='selected'>"
				+ scheglobal.repeat4 + "</option>";
	} else {
		content += "<option value='4'>" + scheglobal.repeat4 + "</option>";
	}
	if (scheduleJson.repeat == '5') {
		content += "<option value='5' selected='selected'>"
				+ scheglobal.repeat5 + "</option>";
	} else {
		content += "<option value='5'>" + scheglobal.repeat5 + "</option>";
	}
	if (scheduleJson.repeat == '6') {
		content += "<option value='6' selected='selected'>"
				+ scheglobal.repeat6 + "</option>";
	} else {
		content += "<option value='6'>" + scheglobal.repeat6 + "</option>";
	}
	content += "</select><div style='float:left;margin-top:9px;'>";
	if (scheduleJson.startDate != '' && scheduleJson.startTime == '') {
		content += "<input type='checkbox' id='quan' name='quan' onclick='quantian();' checked='checked' />"
				+ scheglobal.quantian;
	} else {
		content += "<input type='checkbox' id='quan' name='quan' onclick='quantian();' />"
				+ scheglobal.quantian;
	}
	if (scheduleJson.endDate != '') {
		content += "<input type='checkbox' id='jieshu' name='jieshu' onclick='jieshu();' checked='checked'  />"
				+ scheglobal.jieshu;
	} else {
		content += "<input type='checkbox' id='jieshu' name='jieshu' onclick='jieshu();'  />"
				+ scheglobal.jieshu;
	}
	content += "</div></div></td>" + "</tr>"

	+ "<tr class='dialog_tr tr_odd'>"
			+ "<td align='left' width='20%' class='left_td'>"
			+ scheglobal.address
			+ "</td>"
			+ "<td class='td_align_left'><input type='text' id='address' name='address' maxlength='50' value='' size='50'/></td>"
			+ "</tr>"
			+ "<tr class='dialog_tr tr_even'>"
			+ "<td align='left' width='20%' class='left_td'>"
			+ scheglobal.initiator
			+ "</td>"
			+ "<td class='td_align_left'><input  type='text' name='respon'  id='respon' readonly='readonly' value='"
			+ scheduleJson.respon
			+ "' size='50' /><input  type='hidden' name='responIds' value='"
			+ scheduleJson.responIds
			+ "'  id='responIds'  /></td>"
			+ "</tr>"
			+ "<tr class='dialog_tr tr_odd' style='display:none;'>"
			+ "<td align='left' width='20%' class='left_td'>"
			+ scheglobal.pleases
			+ "</td>"
			+ "<td class='td_align_left'><input  type='text' name='pleases'  id='pleases' value='"
			+ scheduleJson.pleases
			+ "' size='50' onclick=\"openUser('pleases','pleaseIds');\"/><input  type='hidden' name='pleaseIds' value='"
			+ scheduleJson.pleaseIds
			+ "'  id='pleaseIds'  /></td>"
			+ "</tr>"

			+ "<tr class='dialog_tr tr_even'>"
			+ "<td align='left' width='20%' class='left_td'>"
			+ scheglobal.invitee
			+ "</td>"
			+ "<td class='td_align_left'><input  type='text' name='inviteeNames'  id='inviteeNames' onmouseover='displayInviteState()' onmouseout='displayInviteState()' value='"
			+ scheduleJson.inviteeNames
			+ "'  size='50' onclick=\"openUser('inviteeNames','inviteeIds');\"/><input  type='hidden' name='inviteeIds' value='"
			+ scheduleJson.inviteeIds
			+ "'  id='inviteeIds'  /><div id='inviteStateDiv' style='position: absolute;border:1px solid gray;background-color: white;display:none;'>&nbsp;&nbsp;&nbsp;"
			+ scheglobal.acceptInvite
			+ ":<span style='color: green;'>"
			+ scheduleJson.acceptNames
			+ "</span>&nbsp;&nbsp;&nbsp;"
			+ scheglobal.refuseInvite
			+ ":<span style='color: red;'>"
			+ scheduleJson.refuseNames
			+ "</span>&nbsp;&nbsp;&nbsp;</div></td>"

			+ "<tr class='dialog_tr tr_odd'>"
			+ "<td align='left' width='20%' class='left_td'>"
			+ scheglobal.content
			+ "</td>"
			+ "<td class='td_align_left'><textarea name='content'  id='content' rows='6' maxlength='400' onpropertychange='if(this.value.length>400) this.value=this.value.substring(0,400)'></textarea></td>"
			+ "</tr>"
			+ "<tr class='dialog_tr tr_even'>"
			+ "<td align='left' width='20%' class='left_td'>"
			+ scheglobal.annex
			+ "</td>"
			+ "<td class='td_align_left' id='annex_td'><div id='annex_fileupload'></div></td>"
			/*
			 * <input id='annex_ipt' type='button' value='附件'/><div
			 * id='annex_div'
			 * style='width:130px;height:30px;background-color:silver;'></div>
			 */
			+ "</tr>"
			+ "<tr class='dialog_tr tr_even' style='display:none;'>"
			+ "<td align='left' width='20%' class='left_td'>"
			+ scheglobal.creator
			+ "</td>"
			+ "<td class='td_align_left'><input  type='text' name='creators'  id='creators' value='"
			+ scheduleJson.creators
			+ "' size='50'/><input  type='hidden' value='"
			+ scheduleJson.creatorIds
			+ "' name='creatorIds'  id='creatorIds'  /></td>"
			+ "</tr>"

			+ "<tr class='dialog_tr tr_even'>"
			+ "<td align='left' width='20%'  class='left_td'>"
			+ scheglobal.tipMethod
			+ "&nbsp&nbsp&nbsp"
			+ "(自己)"
			+ "</td>"
			+ "<td class='td_align_left'><div style='float:left;margin-top:9px;'>";

	if (scheduleJson.tipMethod.indexOf(',1,') != -1) {
		content += '<input class="tipMethod" name="tipMethod" type="checkbox" value="1" checked="checked"/>'
				+ scheglobal.tipMethod1;
	} else {
		content += '<input class="tipMethod" name="tipMethod" type="checkbox" value="1"/>'
				+ scheglobal.tipMethod1;
	}
	if (scheduleJson.tipMethod.indexOf(',2,') != -1) {
		content += '<input  class="tipMethod" name="tipMethod" type="checkbox" value="2" checked="checked"/>'
				+ scheglobal.tipMethod2;
	} else {
		content += '<input class="tipMethod" name="tipMethod" type="checkbox" value="2"/>'
				+ scheglobal.tipMethod2;
	}
	if (scheduleJson.tipMethod.indexOf(',3,') != -1) {
		content += '<input class="tipMethod" name="tipMethod" type="checkbox" value="3" checked="checked"/>'
				+ scheglobal.tipMethod3;
	} else {
		content += '<input class="tipMethod" name="tipMethod" type="checkbox" value="3"/>'
				+ scheglobal.tipMethod3;
	}
	/*
	 * if(scheduleJson.tipMethod.indexOf(',4,')!=-1){ content+='<input
	 * class="tipMethod" name="tipMethod" type="checkbox" value="4"
	 * checked="checked"/>'+scheglobal.tipMethod4; }else{ content+='<input
	 * class="tipMethod" name="tipMethod" type="checkbox"
	 * value="4"/>'+scheglobal.tipMethod4; }
	 */

	content += "</div><select id='tip' name='tip' value='" + scheduleJson.tip
			+ "' onchange='tipChange();'>";
	if (scheduleJson.tip == '1') {
		content += "<option value='1' selected='selected' >" + scheglobal.tip1
				+ "</option>";
	} else {
		content += "<option value='1'>" + scheglobal.tip1 + "</option>";
	}
	if (scheduleJson.tip == '2') {
		content += "<option value='2' selected='selected' >" + scheglobal.tip2
				+ "</option>";
	} else {
		content += "<option value='2'>" + scheglobal.tip2 + "</option>";
	}
	if (scheduleJson.tip == '3') {
		content += "<option value='3' selected='selected' >" + scheglobal.tip3
				+ "</option>";
	} else {
		content += "<option value='3'>" + scheglobal.tip3 + "</option>";
	}
	if (scheduleJson.tip == '4') {
		content += "<option value='4' selected='selected' >" + scheglobal.tip4
				+ "</option>";
	} else {
		content += "<option value='4'>" + scheglobal.tip4 + "</option>";
	}
	if (scheduleJson.tip == '5') {
		content += "<option value='5' selected='selected' >" + scheglobal.tip5
				+ "</option>";
	} else {
		content += "<option value='5'>" + scheglobal.tip5 + "</option>";
	}
	if (scheduleJson.tip == '6') {
		content += "<option value='6' selected='selected' >" + scheglobal.tip6
				+ "</option>";
	} else {
		content += "<option value='6'>" + scheglobal.tip6 + "</option>";
	}
	if (scheduleJson.tip == '7') {
		content += "<option value='7' selected='selected'  >" + scheglobal.tip7
				+ "</option>";
	} else {
		content += "<option value='7'>" + scheglobal.tip7 + "</option>";
	}
	content += "</select>";

	if (scheduleJson.tipDate == '') {
		scheduleJson.tipDate = scheduleJson.startDate;
	}
	if (scheduleJson.tip == '7') {
		content += "<input type='text' size='10'  id='tipDate' name='tipDate' value='"
				+ scheduleJson.tipDate + "' onclick='WdatePicker();'/>";
		content += "<input  type='text' size='10' id='tipTime' name='tipTime' value='"
				+ scheduleJson.tipTime
				+ "'  onclick=\"WdatePicker({dateFmt:'HH:mm'});\" />";
	} else {
		content += "<input type='text' size='10'  id='tipDate' name='tipDate' value='"
				+ scheduleJson.tipDate
				+ "' style='display:none;'  onclick='WdatePicker();'/>";
		content += "<input  type='text' size='10' id='tipTime' name='tipTime' value='"
				+ getTimeHHMM()
				+ "' style='display:none;'  onclick=\"WdatePicker({dateFmt:'HH:mm'});\" />";
	}
	/*
	 * content += "<input type='checkbox' value='1' id='isNowTip'
	 * name='isNowTip' checked='checked' style='margin-left:30px;'/>"
	 * +scheglobal.nowTip +"</td></tr>";
	 */
	+"</tr>";
	content += "<tr class='dialog_tr tr_even'>"
			+ "<td align='left' width='20%'  class='left_td'>"
			+ scheglobal.tipMethod + "&nbsp&nbsp&nbsp" + "(被邀请人)" + "</td>"
			+ "<td class='td_align_left'>";

	if (scheduleJson.noticMethod.indexOf(',1,') != -1) {
		content += '<input class="noticMethod" name="noticMethod" type="checkbox" value="1" checked="checked"/>'
				+ scheglobal.tipMethod1;
	} else {
		content += '<input class="noticMethod" name="noticMethod" type="checkbox" value="1"/>'
				+ scheglobal.tipMethod1;
	}
	if (scheduleJson.noticMethod.indexOf(',2,') != -1) {
		content += '<input  class="noticMethod" name="noticMethod" type="checkbox" value="2" checked="checked"/>'
				+ scheglobal.tipMethod2;
	} else {
		content += '<input class="noticMethod" name="noticMethod" type="checkbox" value="2"/>'
				+ scheglobal.tipMethod2;
	}
	if (scheduleJson.noticMethod.indexOf(',3,') != -1) {
		content += '<input class="noticMethod" name="noticMethod" type="checkbox" value="3" checked="checked"/>'
				+ scheglobal.tipMethod3;
	} else {
		content += '<input class="noticMethod" name="noticMethod" type="checkbox" value="3"/>'
				+ scheglobal.tipMethod3;
	}
	/*
	 * if(scheduleJson.tipMethod.indexOf(',4,')!=-1){ content+='<input
	 * class="tipMethod" name="tipMethod" type="checkbox" value="4"
	 * checked="checked"/>'+scheglobal.tipMethod4; }else{ content+='<input
	 * class="tipMethod" name="tipMethod" type="checkbox"
	 * value="4"/>'+scheglobal.tipMethod4; }
	 */

	content += "</td></tr>";
	/*
	 * if(scheduleJson.uuid!=''){ content+="<tr class='dialog_tr tr_odd'>" +"<td align='left' width='20%'  class='left_td'>"+scheglobal.operate+"</td><td>";
	 * if('1'!=scheduleJson.isComplete){ content+="<a href='#'
	 * onclick='inviteeSchedule();' style='float:left;margin-right:10px;'><i
	 * class='inviteegroup'></i> "+scheglobal.inviteeBtn+"</a>" +"<a href='#'
	 * onclick='copySchedule();' style='float:left;margin-right:10px;'><i
	 * class='copygroup'></i> "+scheglobal.copyBtn+"</a>"; }
	 * if(scheduleJson.permission&&'1'!=scheduleJson.isComplete){ content+="<a
	 * href='#' onclick='completeSchedule();'
	 * style='float:left;margin-right:10px;'><i class='completegroup'></i>
	 * "+scheglobal.completeBtn+"</a>"; } if(scheduleJson.permission){
	 * content+="<a href='#' onclick='deleteSchedule();'
	 * style='float:left;margin-right:10px;'><i class='delgroup'></i>
	 * "+scheglobal.deleteBtn+"</a>"; } content+="</td></tr>"; }
	 */
	content += "</table></div>" + "<script type='text/javascript'>"
			+ "var setting3 = {" + "async : {" + "otherParam : {"
			+ "	'serviceName' : 'scheduleSettingService',"
			+ "'methodName' : 'getAlwaysAddrTree'," + "'data' : 'MODULE_ID'"
			+ "}" + "}," + "check : {" + "enable : false" + "},"
			+ "callback : {" + "onClick: treeNodeOnClick," + "}"

			+ "};"
			/*
			 * +"$('#address').comboTree({" +"labelField : 'moduleName',"
			 * +"valueField : 'moduleId'," +"width:450," +"height:250,"
			 * +"treeSetting : setting3," +"initServiceParam:['MODULE_ID']"
			 * +"});"
			 */

			+ "function treeNodeOnClick(event, treeId, treeNode) {"
			+ "$('#address').val(treeNode.name);$('#address').comboTree('hide');" + "}"
			/*
			 * +"var schedule_fileupload_id = 'fileupload_schedule_ipt'; " +"
			 * var schedule_fileupload = new
			 * WellFileUpload(schedule_fileupload_id);" +"
			 * schedule_fileupload.init(false, $('#annex_td'), false, false ,
			 * []);"
			 */
			+ "</script>";
	return content;
}

// 鼠标经过邀请人输入框时显示邀请人状态div
function displayInviteState() {
	$("#inviteStateDiv").toggle();
}
// 增加事件的邀请人
function inviteeSchedule() {
	$.unit.open({
		labelField : '',
		valueField : '',
		selectType : 4,
		afterSelect : function(person) {
			pageLock("show");
			$.ajax({
				type : "POST",
				data : {
					"uuid" : $("#scheduleUuid").val(),
					"personIds" : person["id"]
				},
				url : ctx + "/schedule/schedule_invitee",
				dataType : "text",
				success : function callback(result) {
					var ctype = $('#ctype').val();
					if (ctype == '1' || ctype == '2') {
						var viewType = $("#viewType").val();
						var tagId = $('#scheTag').val();
						var dateStr = $("#dateStr").val();
						$.ajax({
							type : "post",
							async : false,
							data : {
								"ctype" : ctype,
								"dateStr" : dateStr,
								"viewType" : viewType,
								"tagId" : tagId
							},
							url : ctx + "/schedule/month/view",
							success : function(res) {
								refreshSchedule(res);
							}
						});
						oAlert2("邀请成功！");
					} else {
						oAlert2("邀请成功！", function() {
							location.reload();
						});
					}
				}
			});
			pageLock("hide");
		}
	});
}

// 转发事件
function copySchedule() {
	invalid();
	$("#scheduleUuid").val("");
	$("#scheduleName").val("转发：" + $("#scheduleName").val());
	$(".ui-dialog-title").text($("#scheduleName").val());
	$("#respon").val($("#creators").val());
	$("#responIds").val($("#creatorIds").val());
	$("#inviteeNames").val("");
	$("#inviteeIds").val("");
	/*
	 * $.unit.open({ labelField : '', valueField : '', selectType : 4,
	 * afterSelect:function(person){ $.ajax({ type : "POST",
	 * data:{"uuid":$("#uuid").val(),"personIds":person["id"]}, url : ctx +
	 * "/schedule/schedule_copy", dataType : "text", success : function
	 * callback(result) { closeDialog();
	 *  } }); } });
	 */
}

// 删除事件
function deleteSchedule() {
	var uuid = $('#scheduleUuid').val();
	var ctype = $('#ctype').val();
	var viewType = $("#viewType").val();
	var tagId = $('#scheTag').val();
	var dateStr = $("#dateStr").val();
	oConfirm(scheglobal.deleteConfirm, function() {

		$.ajax({
			type : "POST",
			url : ctx + "/schedule/schedule_delete.action",
			data : {
				"uuid" : uuid
			},
			dataType : "text",
			success : function callback(result) {
				if (viewType == '1' || viewType == '2') {
					oAlert2("删除成功！");
					closeDialog();
					$.ajax({
						type : "post",
						async : false,
						data : {
							"ctype" : ctype,
							"dateStr" : dateStr,
							"viewType" : viewType,
							"tagId" : tagId
						},
						url : ctx + "/schedule/month/view",
						success : function(result) {
							refreshSchedule(result);
						}

					});
				} else {
					oAlert2("删除成功", function() {
						location.reload();
					});

				}
			},
			error : function(data, status, e) {
				oAlert2(e);
			}
		});
	});

}

// 完成日程
function completeSchedule() {
	var uuid = $('#scheduleUuid').val();
	var ctype = $('#ctype').val();
	var viewType = $("#viewType").val();
	var tagId = $('#scheTag').val();
	var dateStr = $("#dateStr").val();
	var oldtime = $("#startTime").val();
	oConfirm("确认更改日程状态吗？", function() {
		$.ajax({
			type : "POST",
			url : ctx + "/schedule/schedule_complete.action",
			data : {
				"uuid" : uuid,
				"isComplete" : isCompleted
			},
			dataType : "text",
			success : function callback(result) {
				if ("1" == isCompleted) {
					if (viewType == '1' || viewType == '2') {
						closeDialog();
						$.ajax({
							type : "post",
							async : false,
							data : {
								"ctype" : ctype,
								"dateStr" : dateStr,
								"viewType" : viewType,
								"tagId" : tagId
							},
							url : ctx + "/schedule/month/view",
							success : function(result) {
								refreshSchedule(result);
							}
						});
					} else {
						location.reload();
					}
				} else {
					invalid();
				}
			}
		});

	});

}

// 使事件处于查看状态
function unvalid() {
	$("#scheduleName").attr("readonly", "readonly");
	$("#scheTag").attr("disabled", true);
	$(".colorbutton").css("display", "none");
	$("#startDate").attr("disabled", true);
	$("#startTime").attr("disabled", true);
	$("#endDate").attr("disabled", true);
	$("#endTime").attr("disabled", true);
	$("#repeat").attr("disabled", true);
	$("#quan").attr("disabled", true);
	$("#jieshu").attr("disabled", true);
	$("#address").attr("readonly", "readonly");
	$("#pleases").attr("disabled", true);
	$("#inviteeNames").attr("disabled", true);
	$("#content").attr("readonly", "readonly");
	$("#tip").attr("disabled", true);
	$("#tipDate").attr("disabled", true);
	$("#tipTime").attr("disabled", true);
	$(".tipMethod").attr("disabled", true);
	$("#isNowTip").attr("disabled", true);
	$(".noticMethod").attr("disabled", true);
	// $("#uuid").val($("#scheUuid").val());
	showDialogBtn([ '邀请', '转发', '编辑', '完成', '删除', '重新启动', '取消' ]);
}

// 定义一个旧时间变量
var timeOld;
// 定义一个旧开始日期变量
var startDateOld;
var endTimeOld;
var endDateOld;

// 使事件处于可编辑状态
function invalid() {
	$("#scheduleName").removeAttr("readonly");
	$("#scheTag").removeAttr("disabled");
	$(".colorbutton").css("display", "block");
	$("#startDate").removeAttr("disabled");
	/*
	 * startDateOld=$("#startDate").val(); timeOld = $("#startTime").val();
	 * endTimeOld=$("#endTime").val(); endDateOld=$("#endDate").val();
	 */
	$("#startTime").removeAttr("disabled");
	$("#endDate").removeAttr("disabled");
	$("#endTime").removeAttr("disabled");
	$("#repeat").removeAttr("disabled");
	$("#quan").removeAttr("disabled");
	$("#jieshu").removeAttr("disabled");
	$("#address").removeAttr("readonly");
	$("#pleases").removeAttr("disabled");
	//$("#inviteeNames").removeAttr("disabled");
	$("#content").removeAttr("readonly");
	$("#tip").removeAttr("disabled");
	$("#tipDate").removeAttr("disabled");
	$("#tipTime").removeAttr("disabled");
	$(".tipMethod").removeAttr("disabled");
	$("#isNowTip").removeAttr("disabled");
	$(".noticMethod").removeAttr("disabled");
	// 初始化常用地点的选择树
	$('#address').comboTree({
		labelField : 'moduleName',
		valueField : 'moduleId',
		width : 450,
		height : 250,
		treeSetting : setting3,
		initServiceParam : [ 'MODULE_ID' ]
	});

	showDialogBtn([ '保存', '取消' ]);
	initFileuploadSche2($('#scheduleUuid').val(), false);

}

// 显示指定的按钮
function showDialogBtn(showBtns) {
	$(".ui-button-text").each(function() {
		$(this).parent().css("display", 'none');
		for ( var i = 0; i < showBtns.length; i++) {
			if ($(this).text() == showBtns[i]) {
				$(this).parent().css("display", '');
			}
			;
		}
	});
}
// 颜色选择
function choseColor(obj) {
	if ($(obj).find(".colors").css("display") == 'none') {
		$(obj).find(".colors").css("display", "block");
	} else {
		$(obj).find(".colors").css("display", "none");
	}
}

function selColor(c) {
	$("#mycolor").val(c);
	$("#showcolor").css("background-color", c);
}

function quantian() {
	if ($("#quan").attr("checked") == 'checked') {
		$("#startTime").hide();
		$("#endTime").hide();
	} else {
		$("#startTime").show();
		if ($("#jieshu").attr("checked") == 'checked') {
			$("#endTime").show();
		} else {
			$("#endTime").hide();
		}
	}
}

function jieshu() {
	if ($("#jieshu").attr("checked") == 'checked') {
		$("#endDate").show();
		$("#startToEnd").show();
		if ($("#quan").attr("checked") == 'checked') {
			$("#endTime").hide();
		} else {
			$("#endTime").show();
		}
	} else {
		$("#endDate").hide();
		$("#endTime").hide();
		$("#startToEnd").hide();
	}
}

function tipChange() {
	var tip = $("#tip").val();
	if (tip != '7') {
		$("#tipDate").hide();
		$("#tipTime").hide();
	} else {
		$("#tipDate").show();
		$("#tipTime").show();
		if ($("#tipTime").val() == '') {
			$("#tipTime").val(getTimeH());
		}
	}
}

function getDateStr() {
	var ERP_Date = new Date();
	var intYear, intMonth, intDay;
	var datestr = "";

	intYear = ERP_Date.getFullYear();
	intMonth = ERP_Date.getMonth() + 1;
	intDay = ERP_Date.getDate();
	if (intMonth < 10) {
		datestr = intYear + "-0" + intMonth;
	} else {
		datestr = intYear + "-" + intMonth;
	}
	if (intDay < 10) {
		datestr += "-0" + intDay;
	} else {
		datestr += "-" + intDay;
	}
	return datestr;
}

// 获得当前小时分钟
function getTimeHHMM() {
	var ERP_TIME = new Date();
	var intHours, intMinutes;
	var timestr = "";

	intHours = ERP_TIME.getHours();
	intMinutes = ERP_TIME.getMinutes();
	if (intHours < 10) {
		timestr = timestr + "0" + intHours + ":";
	} else {
		timestr = timestr + intHours + ":";
	}
	if (intMinutes < 10) {
		timestr = timestr + "0" + intMinutes + " ";
	} else {
		timestr = timestr + intMinutes + " ";
	}
	return timestr;
}

// 日程定时的弹出框提醒
function scheduleTip(uuid, scheduleName, startDate, address) {
	var content = "<div id='test_con3'>"
			+ "<input id='scheduleId' type='hidden' value='"
			+ uuid
			+ "'/>"
			+ "<table>"
			+ "<tr class='dialog_tr tr_odd' >"
			+ "<td class='left_td'>"
			+ scheglobal.title
			+ "：</td><td>"
			+ scheduleName
			+ "</td></tr>"
			+ "<tr class='dialog_tr tr_even'><td width='20%' class='left_td'>"
			+ scheglobal.time
			+ "：</td>"
			+ "<td>"
			+ startDate
			+ "</td></tr>"
			+ "<tr class='dialog_tr tr_odd' ><td class='left_td'>"
			+ scheglobal.address
			+ "：</td><td>"
			+ address
			+ "</td></tr>"
			+ "<tr class='dialog_tr tr_even' ><td class='left_td'>"
			+ scheglobal.delayTip
			+ "：</td><td><select id='delayTip' name='delayTip' >"
			+ "<option value='5'>五分钟</option>"
			+ "<option value='10'>十分钟</option>"
			+ "<option value='30'>半小时</option>"
			+ "<option value='60'>一小时</option>"
			+ "<option value='120'>两小时</option>"
			+ "</select>"
			+ "</td></tr>"
			+ "</table></div>";

	var buttons = new Object();
	buttons.确定 = function() {
		var scheduleId = $("#scheduleId").val();
		var delayMinute = $("#delayTip").val();
		$.ajax({
			type : "POST",
			url : ctx + "/schedule/delay_tip.action",
			data : {
				"scheduleId" : scheduleId,
				"minute" : delayMinute
			},
			dataType : "text",
			success : function callback(result) {
				oAlert2(scheglobal.saveSuccess);
				closeDialog();
			},
			error : function() {
				oAlert2("推延失败，请重试！");
			}
		});
	};
	buttons.取消 = function() {
		$(this).dialog("close");
	};
	var json = {
		title : "日程提醒",
		/** ***标题***** */
		autoOpen : true,
		/** ***初始化之后，是否立即显示对话框***** */
		modal : true,
		/** ***是否模式对话框***** */
		closeOnEscape : false, /* 当按 Esc之后，是否应该关闭对话框***** */
		resizable : false,
		/** ***是否可以调整对话框的大小***** */
		stack : true,
		/** ***对话框是否叠在其他对话框之上***** */
		draggable : true,
		/** ***是否允许拖动***** */
		height : 300,
		/** ***标题***** */
		width : 420,
		/** ***标题***** */
		// open：事件,
		buttons : buttons
	};
	var str = '<div id="dialogModule1" title="" style="padding:0;margin:0; display:none;"><div class="dialogcontent1">'
			+ content + '</div></div>';
	if ($("#dialogModule1 .dialogcontent1").html() == undefined) {
		$("body").after(str);
	}
	$("#dialogModule1 .dialogcontent1").oDialog(json);
}

// 选择用户
function openUser(lname, lid) {

	/**
	 * $.unit.open({ labelField : lname, valueField : lid, selectType : 4 });
	 */
	$.unit.open({
		labelField : lname,
		valueField : lid,
		selectType : 4,
		afterSelect : function(retVal) {
			$("input[name=" + lname + "]").val(retVal.name);
			$("input[name=" + lid + "]").val(retVal.id);
		}
	});

}
// 得到用户的日程列表
function getUserTag() {
	var userTag = [];
	$.ajax({
		type : "POST",
		async : false,
		url : ctx + "/schedule/tag/user_tag.action",
		success : function callback(result) {
			userTag = result;
		}
	});
	return userTag;
}

// 点击日历修改
function editTag(uuid, name, color, sort, participantIds, participantNames,
		secretaryIds, secretaryNames, userId, userName, isView, viewIds,
		viewNames) {
	var json = {
		"uuid" : uuid,
		"name" : urldecode(name),
		"color" : color,
		"sort" : sort,
		"participantIds" : participantIds,
		"participantNames" : participantNames,
		"secretaryIds" : secretaryIds,
		"secretaryNames" : secretaryNames,
		"userId" : userId,
		"userName" : userName,
		"isView" : isView,
		"viewIds" : viewIds,
		"viewNames" : viewNames
	};
	var title = scheglobal.editCalendar;
	if ('' == json.uuid || 'undefined' == json.uuid) {
		title = scheglobal.addCalendar;
	}
	var dialogJson = {
		title : title,
		/** ***标题***** */
		autoOpen : true,
		/** ***初始化之后，是否立即显示对话框***** */
		modal : true,
		/** ***是否模式对话框***** */
		closeOnEscape : true, /* 当按 Esc之后，是否应该关闭对话框***** */
		draggable : true,
		/** ***是否允许拖动***** */
		resizable : true,
		/** ***是否可以调整对话框的大小***** */
		stack : false,
		/** ***对话框是否叠在其他对话框之上***** */
		height : 300,
		/** ***标题***** */
		width : 600,
		/** ***标题***** */
		content : getTagDailogHtml(json),
		/** ***内容***** */
		// open：事件,
		buttons : {
			"保存" : saveTag
		// "删除":deleteGroup
		// "取消": 事件2
		}
	};
	showDialog(dialogJson);
}

// 日历组修改弹出框源码
function getTagDailogHtml(tagJson) {
	if ("" == tagJson.color) {
		tagJson.color = "white";
	}
	if ("" == tagJson.isView) {
		tagJson.isView = "2";
	}
	var content = "<div id='test_con3'>"
			+ "<input type='hidden' id='tagUuid' name='tagUuid' value='"
			+ tagJson.uuid
			+ "'/> "
			+ "<input type='hidden' id='userIdes' name='userIdes' value='"
			+ tagJson.userId
			+ "'/> "
			+ "<input type='hidden' id='userName' name='userName' value='"
			+ tagJson.userName
			+ "'/> "
			+ "<table>"
			+ "<tr class='dialog_tr tr_odd'>"
			+ "<td class='left_td' align='left' width='20%' class='left' style='text-indent: 15px;'>"
			+ scheglobal.scheduleName
			+ "</td>"
			+ "<td><input type='text' name='tagName' maxLength='50' value='"
			+ tagJson.name
			+ "' id='tagName' size='50' />"
			+ "<div class='chosecolor'>"
			+ "<input type='hidden' id='mycolor' name='mycolor' value='"
			+ tagJson.color
			+ "'/>"
			+ "<span id='showcolor' style='background-color:"
			+ tagJson.color
			+ ";'></span>"
			+ "</div>"
			+ "<div class='colorbutton' onclick='choseColor(this)'>"
			+ "<div class='colors' style='display:none;'>"
			+ "<span class='selectcolor' style='background-color:black;' onclick=\"selColor('black');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:red ' onclick=\"selColor('red');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:blue ' onclick=\"selColor('blue');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:orange ' onclick=\"selColor('orange');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:green ' onclick=\"selColor('green');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:yellow ' onclick=\"selColor('yellow');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:purple ' onclick=\"selColor('purple');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:silver ' onclick=\"selColor('silver');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:tan ' onclick=\"selColor('tan');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:maroon;' onclick=\"selColor('maroon');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:olive;' onclick=\"selColor('olive');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:gray;' onclick=\"selColor('gray');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:lime;' onclick=\"selColor('lime');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:white;' onclick=\"selColor('white');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:aqua;' onclick=\"selColor('aqua');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:fuchsia;' onclick=\"selColor('fuchsia');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:navy;' onclick=\"selColor('navy');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "<span class='selectcolor' style='background-color:teal;' onclick=\"selColor('teal');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
			+ "</div>"
			+ "</div>"
			+ "</td></tr>"
			+ "<tr class='dialog_tr tr_even' style='display:none;'>"
			+ "<td class='left_td' align='left' width='20%'  style='text-indent: 15px;'>"
			+ scheglobal.sort
			+ "</td>"
			+ "<td>"
			+ "<input  type='text' value='"
			+ tagJson.sort
			+ "' name='sort' id='sort' size='3'/>"
			+ "</td>"
			+ "</tr>"
			+ "<tr class='dialog_tr tr_even'>"
			+ "<td class='left_td' align='left' width='20%' style='text-indent: 15px;'>"
			+ scheglobal.projectParticipant
			+ "</td>"
			+ "<td class='td_align_left'><input  type='text' id='participantNames' name='participantNames' value='"
			+ tagJson.participantNames
			+ "'   size='50' onclick=\"openUser('participantNames','participantIds');\"/><input  type='hidden' value='"
			+ tagJson.participantIds
			+ "' id='participantIds' name='participantIds'  /></td>"
			+ "</tr>"
			+ "<tr class='dialog_tr tr_odd'>"
			+ "<td class='left_td' align='left' width='20%' style='text-indent: 15px;'>"
			+ scheglobal.sec
			+ "</td>"
			+ "<td class='td_align_left'><input  type='text' id='secretaryNames' name='secretaryNames' value='"
			+ tagJson.secretaryNames
			+ "'   size='50' onclick=\"openUser('secretaryNames','secretaryIds');\"/><input  type='hidden' value='"
			+ tagJson.secretaryIds
			+ "' id='secretaryIds' name='secretaryIds'  /></td>"
			+ "</tr>"
			+ "<tr class='dialog_tr tr_even'>"
			+ "<td class='left_td' align='left' width='20%'  style='text-indent: 15px;' >"
			+ scheglobal.isView + "</td><td>";

	content += "<input type='radio' size='5' name='isView' class='isView' value='2' onclick='showView(this);' ";
	if (tagJson.isView == '2') {
		content += " checked='checked' ";
	}
	content += "/>" + scheglobal.isView2;
	content += "<input type='radio' size='5'  name='isView' class='isView' value='3' onclick='showView(this);' ";
	if (tagJson.isView == '3') {
		content += " checked='checked' ";
	}
	content += "/>" + scheglobal.isView3;
	content += "<input type='radio' size='5' class='isView' name='isView' value='1' onclick='showView(this);' ";
	if (tagJson.isView == '1') {
		content += " checked='checked' ";
	}
	content += "/>" + scheglobal.isView1 + "</td></tr>";

	if (tagJson.viewIds == '') {
		content += "<tr id='showview' style='display: none' class='dialog_tr tr_even'>";
	} else {
		content += "<tr id='showview' class='dialog_tr tr_odd'>";
	}
	content += "<td class='left_td' align='left' width='20%' style='text-indent: 15px;'>"
			+ scheglobal.views
			+ "</td>"
			+ "<td class='td_align_left'><input  type='text' id='views' name='views' value='"
			+ tagJson.viewNames
			+ "'   size='50' onclick=\"openUser('views','viewIds');\"/><input  type='hidden' value='"
			+ tagJson.viewIds
			+ "' id='viewIds' name='viewIds'  /></td>"
			+ "</tr>" + "</table>" + "</div>";
	return content;
}

function showView(obj) {
	if ($(obj).val() == '1') {
		$("#showview").show();
	} else {
		$("#showview").hide();
		$("#viewIds").val("");
		$("#views").val("");
	}
}

// 修改日历组信息
function saveTag() {
	var reqData = {
		"uuid" : $("#tagUuid").val().replace(/(^\s*)|(\s*$)/g, ''),
		"name" : $("#tagName").val(),
		"sort" : $("#sort").val(),
		"color" : $("#mycolor").val(),
		"participantIds" : $("#participantIds").val(),
		"secretaryIds" : $("#secretaryIds").val(),
		"userId" : $("#userIdes").val(),
		"userName" : $("#userName").val(),
		"isView" : $(".isView:checked").val(),
		"viewIds" : $("#viewIds").val(),
		"viewNames" : $("#views").val()
	};
	if (reqData.name == '') {
		oAlert2(scheglobal.tagNameNotNull, function() {
			$('#tagName').select();

		});
		return false;
		/*
		 * alert(scheglobal.tagNameNotNull); $('#tagName').select(); return
		 * false;
		 */
	}
	var re = /^[0-9,]{0,8}$/;
	if (!re.test(reqData.sort)) {
		oAlert2(scheglobal.onlyNum, function() {
			$('#sort').select();
		});
		return false;
	}
	if (reqData.uuid == "") {
		var userTag = getUserTag();
		for ( var i = 0; i < userTag.length; i++) {
			if (userTag[i].name == reqData.name) {
				oAlert2("日历已存在，请重命名！");
				return false;
			}
		}
	}
	pageLock("show");
	$.ajax({
		type : "POST",
		url : ctx + "/schedule/tag/add",
		dataType : "text",
		data : reqData,
		success : function callback(result) {
			closeDialog();
			$.ajax({
				type : "post",
				async : false,
				data : {},
				url : ctx + "/schedule/calendar/setting",
				success : function(result) {
					// refreshSchedule(result);
					location.reload();
				}
			});
		}
	});
	pageLock("hide");
}

// 删除日历组
function deleteTag(tagUuid) {
	oConfirm(scheglobal.deleteConfirm, function() {
		var userTag = getUserTag();
		if (userTag.length < 2) {
			oAlert2("最后一个日历，删除失败！");
			return false;
		}
		$.ajax({
			type : "POST",
			url : ctx + "/schedule/tag/delete",
			dataType : "text",
			data : {
				"uuid" : tagUuid
			},
			success : function callback(result) {
				location.reload();
			}
		});

	});

}
/*
 * function changeTrColor(obj){ var _table=obj.parentNode; for (var i=0;i<_table.rows.length;i++){
 * _table.rows[i].style.color="red"; } obj.style.color="#ff7200"; }
 */
function initFileuploadSche() {
	cld_num = new UUID();
	var ctlID = "fileupload_schedule" + cld_num;
	$("#uploadcld_h").val(ctlID);
	var fileupload = new WellFileUpload(ctlID);
	fileupload.init(false, $("#annex_fileupload"), false, true, []);
}
var deletedAttachids = "";
function initFileuploadSche2(folderID, readonly) {
	cld_num = new UUID();
	var ctlID = "fileupload_schedule" + cld_num;
	$("#uploadcld_h").val(ctlID);
	var fileupload = new WellFileUpload(ctlID);
	fileupload.initWithLoadFilesFromFileSystem(readonly,
			$("#annex_fileupload"), false, true, folderID, "scheduleAttach");
	fileupload.deleteOkCallback = function(fileInfo) {
		deletedAttachids += "," + fileInfo.fileID;
	};

}