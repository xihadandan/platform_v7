I18nLoader.load("/resources/schedule/global");

//获取cookie
window.flagWindow = 0;
function getCookie(name) {
	var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
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
		window.contextPath = window.contextPath == "\"\"" ? "" : window.contextPath;
		window.ctx = window.contextPath;
	}
	return window.ctx;
}
getContextPath();
	

//查看个人日程 
function openScheduleDialog(lleaderNames ,lleaderIds,mtype,ldate,userno,lstatus,
		rel,ctype,groupid,tipMethod,isComplete,sdate,uuid ,scheduleName ,address ,
		startDate ,startTime ,endDate,endTime   ,isView ,status ,leaderNames ,
		leaderIds ,pleases ,pleaseIds ,views ,viewIds,color,tip,tipDate,tipTime,
		repeat,startTime2,endTime2,tipTime2,creators,creatorIds,isLeaderView,
		inviteeNames,inviteeIds,acceptIds,acceptNames,refuseIds,refuseNames,tagId){
	var title=scheduleName;
	var delsche=false,editsche=false,compsche=false;
	var ress=leaderIds.split(";");
	for(var i=0;i<ress.length;i++){
		if(ress[i]==userno){
			delsche=editsche=compsche=true;
			break;
		}
	}
	var ress=creatorIds.split(";");
	for(var i=0;i<ress.length;i++){
		if(ress[i]==userno){
			delsche=editsche=compsche=true;
			break;
		}
	}
	var content=getScheduleHtml(lleaderNames ,lleaderIds,lstatus,rel,ctype,groupid,
			isComplete,uuid,scheduleName,address,startDate,startTime,endDate,endTime,
			color,leaderNames,leaderIds,pleases,pleaseIds,creators,creatorIds,isView,
			views,viewIds,tip,tipDate,tipTime,tipMethod,repeat,ldate,mtype,delsche,
			compsche,editsche,startTime2,isLeaderView,inviteeNames,inviteeIds,
			acceptIds,acceptNames,refuseIds,refuseNames);
	var json = 
	{
		title:title,  /*****标题******/
		autoOpen: true,  /*****初始化之后，是否立即显示对话框******/
		modal: true,     /*****是否模式对话框******/
		closeOnEscape: true, /*当按 Esc之后，是否应该关闭对话框******/
		draggable: true, /*****是否允许拖动******/  
		resizable: true, /*****是否可以调整对话框的大小******/  
		stack : false,   /*****对话框是否叠在其他对话框之上******/
		height: 580, /*****弹出框高度******/
		width: 660,   /*****弹出框宽度******/
		content: content,/*****内容******/
		//open：事件,
		buttons: {
			"编辑": invalid,
			"确定":addSchedule,
			"删除":deleteSchedule,
			"完成":completeSaveSchedule
			//"取消": 事件2
		}
	};
	showDialog(json);
	$('#flag').val(1);
	$('#scheTag').val(tagId);
	//showScheduleDialog (title,content,300,600);
	unvalid(compsche,editsche,delsche,isComplete);
	
}


//完成领导日程
function completeSaveSchedule(){
	var status=$('#status').val();
	var ldate=$('#ldate').val();
	var rel=$('#rel').val();
	var mtype=$('#mtype').val();
	var ctype=$('#ctype').val();
	var groupid=$('#groupid').val();
	var scheduleName=$('#scheduleName').val();
	if(scheduleName==''){
		alert(global.subjectNotNull);
		$('#scheduleName').focus();
		return false;
	}
	var address=$('#address').val();
	if(address==''){
		alert(global.addressNotNull);
		$('#address').focus();
		return false;
	}
	var startDate=$('#startDate').val();
	if(startDate==''){
		alert(global.startDateNotNull);
		$('#startDate').focus();
		return false;
	}
	var quan=$("#quan").attr("checked")=='checked'?"1":"0";
	var jieshu=$("#jieshu").attr("checked")=='checked'?"1":"0";
	var isLeaderView=$("#isLeaderView").attr("checked")=='checked'?"1":"0";
	var startTime,startTime2,endDate,endTime,endTime2;
	if(quan==0){
	startTime=$('#startTime').val();
	startTime2=$('#startTime2').val();
	if(startTime==''||startTime2==''){
		alert(global.startTimeNotNull);
		$('#startTime').focus();
		return false;
	}
	}
	if(jieshu==1){
	endDate=$('#endDate').val();
	if(endDate==''){
		alert(global.endDateNotNull);
		$('#endDate').focus();
		return false;
	}
	}
	if(quan==0&&jieshu==1){
	endTime=$('#endTime').val();
	endTime2=$('#endTime2').val();
	if(endTime==''||endTime2==''){
		alert(global.endTimeNotNull);
		$('#endTime').focus();
		return false;
	}
	}
	
	var color="";
	
	color=$("#mycolor").val();
	
	var leaderNames=$('#leaderNames').val();
	if(leaderNames==''){
		if(status=='1'){
			alert(global.leaderNotNull);
			}else{
				alert(global.zerenNotNull);
			}
		$('#leaderNames').focus();
		return false;
	}
	var leaderIds=$('#leaderIds').val();
	var isView="";
	$(".isView").each(function (){
			if($(this).attr("checked")=="checked"){
				isView=$(this).val();
			}
		});
	var pleases=$('#pleases').val();
	var views=$('#views').val();
	var pleaseIds=$('#pleaseIds').val();
	var viewIds=$('#viewIds').val();
	var creatorIds=$('#creatorIds').val();
	var creators=$('#creators').val();
	var inviteeIds=$("#inviteeIds").val();
	var inviteeNames=$("#inviteeNames").val();
	var status=$('#status').val();
	var uuid=$('#uuid').val();
	var tip=$('#tip').val();
	var tipDate=$('#tipDate').val();
	var tipTime=$('#tipTime').val();
	var tipTime2=$('#tipTime2').val();
	var repeat=$("#repeat").val();
	var tipMethod = ",";
	$(".tipMethod:checked").each(function (){
		tipMethod += $(this).val()+",";
	});
//	var tipMethod=$("#tipMethod").val();
	//alert("color="+color+"&uuid="+uuid+"&status="+status+"&scheduleName="+scheduleName+"&address="+address+"&dstartDate="+startDate+"&startTime="+startTime+"&dendDate="+endDate+"&endTime="+endTime+"&scheduleType="+scheduleType+"&isHint="+isHint+"&hintCreator="+hintCreator+"&hintPlease="+hintPlease+"&hintView="+hintView+"&onlineMessage="+onlineMessage+"&mail="+mail+"&mobil="+mobil+"&hintDay="+hintDay+"&hintHour="+hintHour+"&hintMinute="+hintMinute+"&isView="+isView+"&leaderNames="+leaderNames+"&leaderIds="+leaderIds+"&pleases="+pleases+"&views="+views+"&pleaseIds="+pleaseIds+"&viewIds="+viewIds);
	$.ajax({
		type : "POST",
		url : contextPath+"/schedule/schedule_add.action",
		data : "tipMethod="+tipMethod+"&creators="+creators+"&creatorIds="+creatorIds+"&tip="+tip
		+"&tipTime="+tipTime+"&tipDate="+tipDate+"&repeat="+repeat+"&color="+color+"&uuid="+uuid
		+"&status="+status+"&scheduleName="+scheduleName+"&address="+address+"&dstartDate="+startDate
		+"&startTime="+startTime+"&dendDate="+endDate+"&endTime="+endTime+"&isView="+isView
		+"&leaderNames="+leaderNames+"&leaderIds="+leaderIds+"&pleases="+pleases+"&views="+views
		+"&pleaseIds="+pleaseIds+"&viewIds="+viewIds+"&isLeaderView="+isLeaderView
		+"&inviteeIds="+inviteeIds+"&inviteeNames="+inviteeNames,
		dataType : "text",
		success : function callback(result) {
			completeSchedule(status,ldate,rel,mtype,ctype,groupid);
		},
		error: function (data, status, e)
		{
			alert(e);
		}
	});
}
	
//完成日程
function completeSchedule(status,ldate,rel,mtype,ctype,groupid){
	var uuid=$('#uuid').val();
	if(confirm(global.completeConfirm)) {
		$.ajax({
			type : "POST",
			url : contextPath+"/schedule/schedule_complete.action",
			data : "uuid="+uuid,
			dataType : "text",
			success : function callback(result) {
				closeDialog();
				var uu;
				if(rel==1){
					//window.location.href=contextPath+"/schedule/leader_schedule.action?ldate="+ldate+"&mtype="+mtype;
					uu = contextPath+"/schedule/leader_schedule.action";
				}
			
				if(rel==2){
					//window.location.href=contextPath+"/schedule/leader_schedule.action?ldate="+ldate+"&ctype=1&mtype="+mtype;
					uu =contextPath+"/schedule/leader_schedule.action";
					ctype = 1;
					
					}
				if(rel==3){
					//window.location.href=contextPath+"/schedule/person_schedule.action?ldate="+ldate+"&mtype="+mtype;
					uu = contextPath+"/schedule/person_schedule.action";
					
					}
				if(rel==4){
					//window.location.href=contextPath+"/schedule/person_schedule2.action?ldate="+ldate+"&mtype="+mtype;
					uu=contextPath+"/schedule/person_schedule2.action";
					
				}
				if(rel==5){
					//window.location.href=contextPath+"/schedule/group_schedule.action?ldate="+ldate+"&ctype="+ctype+"&groupid="+groupid;
					uu=contextPath+"/schedule/group_schedule.action";
					
				}
				
				$.ajax({
					type:"post",
					async:false,
					data : {"stype":0,"ldate":ldate,"mtype":mtype,"requestType":"cms","&ctype=":ctype,"&groupid=":groupid},
					url: uu,
					success:function(result){
						$(".schedule_person_list").parent().html(result);
//										$(".schedule_person_list").parent().hide();
//										$(".schedule_person_list").parent().html(result);
////						 				$(".schedule_person_list").parent().children().not(".schedule_person_list").remove();
//										$(".schedule_person_list").parent().show();
					}
				});
			}
		});
	}
	
}
//删除领导日程
function deleteSchedule(){
	var status=$('#status').val();
	var ldate=$('#ldate').val();
	var rel=$('#rel').val();
	var mtype=$('#mtype').val();
	var ctype=$('#ctype').val();
	var groupid=$('#groupid').val();
	var uuid=$('#uuid').val();
		if(confirm(global.deleteConfirm)) {
				$.ajax({
					type : "POST",
					url : contextPath+"/schedule/schedule_delete.action",
					data : "uuid="+uuid,
					dataType : "text",
					success : function callback(result) {
						closeDialog();
						var uu;
						if(rel==1){
							uu = contextPath+"/schedule/leader_schedule.action";
							}
							if(rel==2){
								uu =contextPath+"/schedule/leader_schedule.action";
								ctype = 1;
								}
							if(rel==3){
								//window.location.href=contextPath+"/schedule/person_schedule.action?ldate="+ldate+"&mtype="+mtype;
								  uu = contextPath+"/schedule/person_schedule.action";
								}
							if(rel==4){
								uu=contextPath+"/schedule/person_schedule2.action";
							}
							if(rel==5){
								uu=contextPath+"/schedule/group_schedule.action";
							}
							
							$.ajax({
								type:"post",
								async:false,
								data : {"stype":0,"ldate":ldate,"mtype":mtype,"requestType":"cms","&ctype=":ctype,"&groupid=":groupid},
								url: uu,
								success:function(result){
									$(".schedule_person_list").parent().html(result);
//										$(".schedule_person_list").parent().hide();
//										$(".schedule_person_list").parent().html(result);
////						 				$(".schedule_person_list").parent().children().not(".schedule_person_list").remove();
//										$(".schedule_person_list").parent().show();
								}
							});
							
					},
					error: function (data, status, e)
				{
					alert(e);
				}
				});
		}
	
}
//添加领导日程
//ctype=0 群组日程1 ctype=1 群组日程2
function addSchedule(){
	var status=$('#status').val();
	var ldate=$('#ldate').val();
	var rel=$('#rel').val();
	var mtype=$('#mtype').val();
	var ctype=$('#ctype').val();
	var groupid=$('#groupid').val();
	var scheduleName=$('#scheduleName').val();
	var tagId = $('#scheTag').val();
	
	if(scheduleName==''){
		alert(global.subjectNotNull);
		$('#scheduleName').focus();
		return false;
	}
	var address=$('#address').val();
	if(address==''){
		alert(global.addressNotNull);
		$('#address').focus();
		return false;
	}
	var startDate=$('#startDate').val();
	if(startDate==''){
		alert(global.startDateNotNull);
		$('#startDate').focus();
		return false;
	}
	var quan=$("#quan").attr("checked")=='checked'?"1":"0";
	var jieshu=$("#jieshu").attr("checked")=='checked'?"1":"0";
	var isLeaderView=$("#isLeaderView").attr("checked")=='checked'?"1":"0";
	var isNowTip=$("#isNowTip").attr("checked")=='checked'?"1":"0";
	var startTime,startTime2,endDate,endTime,endTime2;
	if(quan==0){
		startTime=$('#startTime').val();
		//startTime2=$('#startTime2').val();
		if(startTime==''||startTime2==''){
			alert(global.startTimeNotNull);
			$('#startTime').focus();
			return false;
		}
	}
	if(jieshu==1){
		endDate=$('#endDate').val();
		if(endDate==''){
			alert(global.endDateNotNull);
			$('#endDate').focus();
			return false;
		}
	}
	if(quan==0&&jieshu==1){
		endTime=$('#endTime').val();
		if(endTime==''||endTime2==''){
			alert(global.endTimeNotNull);
			$('#endTime').focus();
			return false;
		}
	}
	var color="";
	color=$("#mycolor").val();
	var leaderNames=$('#leaderNames').val();
	if(leaderNames==''){
		if(status=='1'){
		alert(global.leaderNotNull);
		}else{
			alert(global.zerenNotNull);
		}
		$('#leaderNames').focus();
		return false;
	}
	var leaderIds=$('#leaderIds').val();
	var isView="";
	$(".isView").each(function (){
		if($(this).attr("checked")=="checked"){
			isView=$(this).val();
		}
	});
	var pleases=$('#pleases').val();
	var views=$('#views').val();
	var pleaseIds=$('#pleaseIds').val();
	var viewIds=$('#viewIds').val();
	var creatorIds=$('#creatorIds').val();
	var creators=$('#creators').val();
	var inviteeIds=$("#inviteeIds").val();
	var inviteeNames=$("#inviteeNames").val();
	var status=$('#status').val();
	var uuid=$('#uuid').val();
	var tip=$('#tip').val();
	var tipTime=$('#tipTime').val();
	var tipDate=$('#tipDate').val();
	//var tipTime2=$('#tipTime2').val();
	var repeat=$("#repeat").val();
	var tipMethod=",";
	$(".tipMethod:checked").each(function (){
		tipMethod+=$(this).val()+",";
	});
	$.ajax({
		type : "POST",
		url : contextPath+"/schedule/schedule_add.action",
		data : "tipMethod="+tipMethod+"&creators="+creators+"&creatorIds="+creatorIds
			+"&tip="+tip+"&tipTime="+tipTime+"&tipDate="+tipDate+"&repeat="+repeat+"&color="
			+color+"&uuid="+uuid+"&status="+status+"&scheduleName="+scheduleName+"&address="
			+address+"&dstartDate="+startDate+"&startTime="+startTime+"&dendDate="+endDate+"&endTime="
			+endTime+"&isView="+isView+"&leaderNames="+leaderNames+"&leaderIds="+leaderIds+"&pleases="
			+pleases+"&views="+views+"&pleaseIds="+pleaseIds+"&viewIds="+viewIds+"&isLeaderView="+isLeaderView
			+"&isNowTip="+isNowTip+"&inviteeIds="+inviteeIds+"&inviteeNames="+inviteeNames+"&tag.uuid="+tagId,
		dataType : "text",
		success : function(result) {
			alert("保存成功！");
			closeDialog();
			window.flagWindow = 1;
			var uu="";
			if(rel==1){
			//window.location.href=contextPath+"/schedule/leader_schedule.action?ldate="+ldate+"&mtype="+mtype;
				uu = contextPath+"/schedule/leader_schedule.action";
			}
			if(rel==2){
				//window.location.href=contextPath+"/schedule/leader_schedule.action?ldate="+ldate+"&ctype=1&mtype="+mtype;
				uu =contextPath+"/schedule/leader_schedule.action";
				ctype = 1;
				}
			if(rel==3){ 
				//alert("/schedule/person_schedule.action?ldate="+ldate+"&mtype="+mtype);
				//window.location.href=contextPath+"/schedule/person_schedule.action?ldate="+ldate+"&mtype="+mtype;
				uu = contextPath+"/schedule/person_schedule.action";
				}
			if(rel==4){
				//window.location.href=contextPath+"/schedule/person_schedule2.action?ldate="+ldate+"&mtype="+mtype;
				uu=contextPath+"/schedule/person_schedule2.action";
			}
			if(rel==5){
				//window.location.href=contextPath+"/schedule/group_schedule.action?ldate="+ldate+"&ctype="+ctype+"&groupid="+groupid;
				uu=contextPath+"/schedule/group_schedule.action";
			}
			$.ajax({
				type:"post",
				async:false,
				data : {"stype":0,"ldate":ldate,"mtype":mtype,"requestType":"cms","&ctype=":ctype,"&groupid=":groupid},
				url: uu,
				success:function(result){
					$(".schedule_person_list").parent().html(result);
//										$(".schedule_person_list").parent().hide();
//										$(".schedule_person_list").parent().html(result);
////								 	$(".schedule_person_list").parent().children().not(".schedule_person_list").remove();
//										$(".schedule_person_list").parent().show();
				}
			});
		},
		error: function (data, status, e)
		{
			alert(e);
		}
	});
	
	//刷新窗口
	setTimeout(function(){searchWindow();},300);
}
	
////弹出DIALOG窗口
//	function showScheduleDialog (title,content,height,width){
//		$("#dialogModule").dialog({
//			title:title,
//			autoOpen: true,
//			height: height,
//			width: width,
//			modal: true,
//			open:function() {
//				$(".ui-widget-overlay").css("background", "#000");
//				$(".ui-widget-overlay").css("opacity", "0.5");
//				if(content!=""){
//					$(".dialogcontent").html(content);
//				}
//			}
//		});	
//	}
//	//关闭DIALOG窗口
//	function closeDialog(){
//		$("#dialogModule").dialog("close");
//	}	
	
	
//新建日程
function openScheduleNewDialog(relLeader,relLeaderId,lleaderNames ,lleaderIds,status,rel,ctype,groupid,
		creators,creatorIds,sdate,ldate,mtype,now,isLeaderView,isView,views,viewIds){
	var title=global.addSchedule;
	var endDate='',startDate=sdate,editsche=false,delsche=false,compsche=false,isComplete='0',
	uuid='',scheduleName='',address='',startTime=getTimeHHMM(),startTime2=sdate,endTime='',mycolor='black',
	leaderNames=creators,leaderIds=creatorIds,pleases='',pleaseIds='',isView='3',views='',viewIds='',tip='1',
	tipDate='',tipTime='',tipMethod=',',repeat='1',inviteeNames='',inviteeIds='',
	acceptIds='',acceptNames='',refuseIds='',refuseNames='';
	
	if(tipDate==''){
		tipDate=sdate;
	}
	if(status=='1'){
		leaderNames=relLeader+";"+relLeaderId;
		leaderIds=relLeaderId;
	}
	var content=getScheduleHtml(lleaderNames ,lleaderIds,status,rel,ctype,groupid,
			isComplete,uuid,scheduleName,address,startDate,startTime,
			endDate,endTime,mycolor,leaderNames,leaderIds,pleases,pleaseIds,
			creators,creatorIds,isView,views,viewIds,tip,tipDate,tipTime,tipMethod,
			repeat,ldate,mtype,delsche,compsche,editsche,startTime2,isLeaderView,
			inviteeNames,inviteeIds,acceptIds,acceptNames,refuseIds,refuseNames);
	var json = 
	{
		title:title,  /*****标题******/ 
		autoOpen: true,  /*****初始化之后，是否立即显示对话框******/ 
		modal: true,     /*****是否模式对话框******/ 
		closeOnEscape: true, /*当按 Esc之后，是否应该关闭对话框******/ 
		draggable: true, /*****是否允许拖动******/  
		resizable: true, /*****是否可以调整对话框的大小******/  
		stack : false,   /*****对话框是否叠在其他对话框之上******/ 
		height: 580, /*****标题******/ 
		width: 660,   /*****标题******/ 
		content: content,/*****内容******/ 
		//open：事件,
		buttons: {
			"确定":addSchedule
			//"取消": 事件2
		}
	};

	showDialog(json);
	openNewSche();
	$('#flag').val(0);//代表新增
	
	/**使用日程的样例，请使用dialog的按钮（统一规范），按钮不能写在content里**/
//		showScheduleDialog (title,content,300,600);
}

//获得当前小时分钟
function getTimeHHMM()
{
	var ERP_TIME = new Date();
	var intHours, intMinutes;
	var timestr="";
	
	intHours = ERP_TIME.getHours();
	intMinutes = ERP_TIME.getMinutes();
	if (intHours < 10) {
		timestr =timestr+ "0"+intHours+":";
	} else {
		timestr =timestr+ intHours + ":";
	}
	if (intMinutes < 10) {
		timestr =timestr+ "0"+intMinutes+" ";
	} else {
		timestr =timestr+ intMinutes+" ";
	}
	return timestr;
}

//获得当前小时分钟
function getTimeHHMM2()
{
	var ERP_TIME = new Date();
	var intHours, intMinutes;
	var timestr="";
	
	intHours = ERP_TIME.getHours();
	intMinutes = ERP_TIME.getMinutes();
	if (intHours < 10) {
		timestr =timestr+ "0"+intHours+":";
	} else {
		timestr =timestr+ intHours + ":";
	}
	if (intMinutes < 10) {
		timestr =timestr+ "0"+intMinutes;
	} else {
		timestr =timestr+ intMinutes;
	}
	
	return timestr;
}

//获得日程HTML
function getScheduleHtml(lleaderNames ,lleaderIds,status,rel,ctype,groupid,isComplete,
		uuid,scheduleName,address,startDate,startTime,endDate,endTime,mycolor,
		leaderNames,leaderIds,pleases,pleaseIds,creators,creatorIds,isView,views,
		viewIds,tip,tipDate,tipTime,tipMethod,repeat,ldate,mtype,delsche,compsche,
		editsche,startTime2,isLeaderView,inviteeNames,inviteeIds,acceptIds,acceptNames,
		refuseIds,refuseNames){
	var scheContent="schedule content";
	var userTag = getUserTag();
	var inviteeNames = acceptNames + refuseNames + inviteeNames;
	var inviteeIds = acceptIds +refuseIds +inviteeIds;
	inviteeIds = inviteeIds.substr(0,inviteeIds.length-1);
	inviteeNames = inviteeNames.substr(0,inviteeNames.length-1);
	var content="<div class='addschedulediv'>"
		+"<input type='hidden' id='status' name='status' value='"+status+"' />"
		+"<input type='hidden' id='uuid' name='uuid' value='"+uuid+"' />"
		
		+"<input type='hidden' id='rel' name='rel' value='"+rel+"'/>"
		+"<input type='hidden' id='ctype' name='ctype' value='"+ctype+"'/>"
		+"<input type='hidden' id='mtype' name='mtype' value='"+mtype+"'/>"
		+"<input type='hidden' id='ldate' name='ldate' value='"+ldate+"'/>"
		+"<input type='hidden' id='groupid' name='groupid' value='"+groupid+"'/>"
		//设置隐藏域 便于区别查看和编辑
		+"<input type='hidden' id='flag' name='flag' />"

		+"<table class='add_schedule_table'>"
		
		+"<tr class='add_schedule_table_tr add_schedule_table_odd' >"
		+"<td align='left' width='20%'  class='td_align_right'>"+global.title+"</td>"
		+"<td class='td_align_left'><input  type='text' size='50' id='scheduleName' name='scheduleName' value='"+scheduleName+"' />" ;
		
		content +="<select id='scheTag' onchange='selTag(this)'><option value=''>标记为...</option>" ;
		for(var key in userTag){
			content +="<option value='"+userTag[key].uuid+"' isLeaderView='"+userTag[key].isLeaderView
			+"' isView='"+userTag[key].isView+"' viewNames='"+userTag[key].viewNames
			+"' viewIds='"+userTag[key].viewIds+"' style='color:"+ userTag[key].color +";' >"+ userTag[key].name +"</option>";
		}
		content += "</select>";
		
		content += "<div class='chosecolor'>" 
		+"<input type='hidden' id='mycolor' name='mycolor' value='"+mycolor+"'/>"
		+"<span id='showcolor' style='background-color:"+mycolor+";'></span>"
		+"</div>"
		+"<div class='colorbutton' onclick='choseColor1(this)'>" 
		+"<div class='colors' style='display:none;'>" 
		+"<span class='selectcolor' style='background-color:black;' onclick=\"selColor('black');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:red ' onclick=\"selColor('red');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:blue ' onclick=\"selColor('blue');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:orange ' onclick=\"selColor('orange');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:green ' onclick=\"selColor('green');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:yellow ' onclick=\"selColor('yellow');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:purple ' onclick=\"selColor('purple');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:silver ' onclick=\"selColor('silver');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:tan ' onclick=\"selColor('tan');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:maroon;' onclick=\"selColor('maroon');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:olive;' onclick=\"selColor('olive');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:gray;' onclick=\"selColor('gray');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:lime;' onclick=\"selColor('lime');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"</div>"
		+"</div>"
		+"</td></tr>"
		
		+"<tr class='add_schedule_table_tr add_schedule_table_even'>"
		+"<td align='left' width='20%' class='td_align_right'>时间</td>"
		+"<td class='td_align_left'>"
		+"<div class='trkaishi'>"
		+"<div class='trkaishi_data'>"
		+"<input type='text' size='10' id='startDate'  name='startDate' value='"+startDate+"'  onclick='WdatePicker();' />" 
		+"</div>";
		if(startTime!=''){
			content+="<div class='trkaishi_time' id='dstartTime' style='display:block;'>";
			content+="<input  type='text' size='10' id='startTime'  name='startTime' value='"+startTime+"'  onclick=\"WdatePicker({dateFmt:'HH:mm'});\" />";
		}else{
			content+="<div class='trkaishi_time' id='dstartTime'  style='display:none;'>";
			content+="<input  type='text' size='10' id='startTime'  name='startTime' value='"+getTimeHHMM()+"'  onclick=\"WdatePicker({dateFmt:'HH:mm'});\" />";
		}
		content+="</div>";
		content+="</div>";
		if(endDate!=''){
			content+="<div style='display:block' id='trjieshu' class='trjieshu'>";
		}else{
			content+="<div style='display:none' id='trjieshu' class='trjieshu'>";
		}
		content+="<div class='datetimeto'>-</div><div  class='trjieshu_data' >"
		+"<input  type='text' size='10' id='endDate'  name='endDate' value='"+endDate+"'  onclick='WdatePicker();' />"
		+"</div>";
		if(endTime!=''){
			content+="<div class='trjieshu_time' id='dendTime'    style='display:block;'>";
			content+="<input  type='text' size='10'  id='endTime' name='endTime' value='"+endTime+"'  onclick=\"WdatePicker({dateFmt:'HH:mm'});\" />";
		}else{
			content+="<div class='trjieshu_time' id='dendTime'    style='display:none;'>";
			content+="<input  type='text' size='10' id='endTime' name='endTime'   value='"+getTimeHHMM()+"'  onclick=\"WdatePicker({dateFmt:'HH:mm'});\" />";
		}
		content+=" </div>"
		+"</div>"
		+"<div class='datatimeset'>";
		if(startDate!='' &&startTime==''){
			content+="<input type='checkbox' id='quan' name='quan' onclick='quantian();' checked='checked' />"+global.quantian;
		}else{
			content+="<input type='checkbox' id='quan' name='quan' onclick='quantian();' />"+global.quantian;
		}
		if(endDate!=''){
			content+="<input type='checkbox' id='jieshu' name='jieshu' onclick='jieshu();' checked='checked'  />"+global.jieshu;
		}else{
			content+="<input type='checkbox' id='jieshu' name='jieshu' onclick='jieshu();'  />"+global.jieshu;
		}
		content+="<select id='repeat' name='repeat' value='"+repeat+"' >";
		if(repeat=='1'){
			content+="<option value='1' selected='selected'>"+global.repeat1+"</option>";
		}else{
			content+="<option value='1'>"+global.repeat1+"</option>";
		}
		if(repeat=='2'){
			content+="<option value='2' selected='selected'>"+global.repeat2+"</option>";
		}else{
			content+="<option value='2'>"+global.repeat2+"</option>";
		}
		if(repeat=='3'){
			content+="<option value='3' selected='selected'>"+global.repeat3+"</option>";
		}else{
			content+="<option value='3'>"+global.repeat3+"</option>";
		}
		if(repeat=='4'){
			content+="<option value='4' selected='selected'>"+global.repeat4+"</option>";
		}else{
			content+="<option value='4'>"+global.repeat4+"</option>";
		}
		if(repeat=='5'){
			content+="<option value='5' selected='selected'>"+global.repeat5+"</option>";
		}else{
			content+="<option value='5'>"+global.repeat5+"</option>";
		}
		if(repeat=='6'){
			content+="<option value='6' selected='selected'>"+global.repeat6+"</option>";
		}else{
			content+="<option value='6'>"+global.repeat6+"</option>";
		}
		content+="</select></div>"
		+"</td>"
		+"</tr>"
		
		+"<tr class='add_schedule_table_tr add_schedule_table_odd'>"
		+"<td align='left' width='20%' class='td_align_right'>"+global.address+"</td>"
		+"<td class='td_align_left'><input type='text' id='address' name='address' value='"+address+"' size='50'/></td>"
		+"</tr>"
		
		
		+"<tr class='add_schedule_table_tr add_schedule_table_even'>";
		//leaderNames ,leaderIds,
		if(status=='1'){
			content+="<td align='left' width='20%' class='td_align_right'>"+global.leader+"</td>"
				+"<td class='td_align_left'><select id='leaderNames' name='leaderNames' value='"+leaderNames+"'>";
			var lnames=lleaderNames.split(";");
			var lids=lleaderIds.split(";");
			for(var i=0;i<lnames.length;i++){
				if(lnames[i]!=''){
					var lvalue=lnames[i]+";"+lids[i];
					if(lids[i]==leaderIds){
						content+="<option value='"+lvalue+"' selected='selected'>"+lnames[i]+"</option>";
					}else{
						content+="<option value='"+lvalue+"'>"+lnames[i]+"</option>";
					}
				}
			}
			content+="</select><input  type='hidden' name='leaderIds' value='"+leaderIds+"' id='leaderIds'/>";
		}else{
			content+="<td align='left' width='20%' class='td_align_right'>"+global.zeren+"</td>"
			+"<td class='td_align_left'><input type='text' name='leaderNames' value='"+leaderNames+"' readonly='readonly' id='leaderNames' size='50' onclick=\"openUser('leaderNames','leaderIds');\" /><input  type='hidden' name='leaderIds' value='"+leaderIds+"' id='leaderIds'/></td>";
		}
		content+="</tr>"
		+"<tr class='add_schedule_table_tr add_schedule_table_odd'>"
		+"<td align='left' width='20%' class='td_align_right'>"+global.pleases+"</td>"
		+"<td class='td_align_left'><input  type='text' name='pleases'  id='pleases' value='"+pleases+"' readonly='readonly'  size='50' onclick=\"openUser('pleases','pleaseIds');\"/><input  type='hidden' name='pleaseIds' value='"+pleaseIds+"'  id='pleaseIds'  /></td>"
		+"</tr>"

		+"<tr class='add_schedule_table_tr add_schedule_table_even'>"
		+"<td align='left' width='20%' class='td_align_right'>"+global.invitee+"</td>"
		+"<td class='td_align_left'><input  type='text' name='inviteeNames'  id='inviteeNames' onmouseover='displayInviteState()' onmouseout='displayInviteState()' value='"
		+inviteeNames+"' readonly='readonly'  size='50' onclick=\"openUser('inviteeNames','inviteeIds');\"/><input  type='hidden' name='inviteeIds' value='"
		+inviteeIds+"'  id='inviteeIds'  /><div id='inviteStateDiv' style='position: absolute;border:1px solid gray;background-color: white;display:none;'>&nbsp;&nbsp;&nbsp;"
		+global.acceptInvite+":<span style='color: green;'>"+acceptNames+"</span>&nbsp;&nbsp;&nbsp;"+global.refuseInvite+":<span style='color: red;'>"+refuseNames+"</span>&nbsp;&nbsp;&nbsp;</div></td>"

		+"<tr class='add_schedule_table_tr add_schedule_table_odd'>"
		+"<td align='left' width='20%' class='td_align_right'>"+global.content+"</td>"
		+"<td class='td_align_left'><textarea name='content'  id='content' rows='3' >"+scheContent+"</textarea></td>"
		+"</tr>"
		+"<tr class='add_schedule_table_tr add_schedule_table_even'>"
		+"<td align='left' width='20%' class='td_align_right'>"+global.annex+"</td>"
		+"<td class='td_align_left'><input id='annex' type='file'/></td>"
		+"</tr>"
		+"<tr class='add_schedule_table_tr add_schedule_table_odd'>"
		+"<td align='left' width='20%' class='td_align_right'>"+global.creator+"</td>"
		+"<td class='td_align_left'><input  type='text' name='creators'  id='creators' value='"+creators+"' readonly='readonly' disabled='true' size='50'/><input  type='hidden' value='"+creatorIds+"' name='creatorIds'  id='creatorIds'  /></td>"
		+"</tr>"
		+"<tr class='add_schedule_table_tr add_schedule_table_even'>"
		+"<td align='left' width='20%' class='td_align_right'>"+global.isView+"</td>"
		+"<td class='td_align_left'><table><tr><td>";
		if(isView=='3'){
			content+="<input  type='radio' size='5'  name='isView' class='isView' value='3' checked='checked' onclick='showView(this);'/>"+global.isView3;
			content+="</td><td><input type='radio' size='5' name='isView' class='isView' value='2'  onclick='showView(this);'/>"+global.isView2;
			content+="</td><td><input type='radio' size='5' class='isView' name='isView' value='1' onclick='showView(this);' />"+global.isView1;
		}else if(isView=='2'){
			content+="<input  type='radio' size='5' name='isView' class='isView' value='3'  onclick='showView(this);'/>"+global.isView3;
			content+="</td><td><input type='radio' size='5'  name='isView' class='isView' value='2' checked='checked' onclick='showView(this);'/>"+global.isView2;
			content+="</td><td><input type='radio' size='5'  class='isView' name='isView' value='1' onclick='showView(this);' />"+global.isView1;
		}else if(isView=='1'){
			content+="<input  type='radio' size='5' name='isView' class='isView' value='3'  onclick='showView(this);'/>"+global.isView3;
			content+="</td><td><input type='radio' size='5' name='isView' class='isView' value='2'  onclick='showView(this);'/>"+global.isView2;
			content+="</td><td><input type='radio' size='5'  class='isView' name='isView' value='1' checked='checked' onclick='showView(this);' />"+global.isView1;
		}
		if(isLeaderView=='1'){
			content+="</td><td><input type='checkbox' value='1'  id='isLeaderView' name='isLeaderView' checked='checked' style='margin-left:30px;'/>"+global.isView4;
		}else{
			content+="</td><td><input type='checkbox' value='1'  id='isLeaderView' name='isLeaderView' style='margin-left:30px;'/>"+global.isView4;
		}
		
		content+="</td></tr></table></td>"
		+"</tr>";
		if(views==''){
			content+="<tr id='showview' style='display: none' class='add_schedule_table_tr add_schedule_table_even'>";
		}else{
			content+="<tr id='showview' class='add_schedule_table_tr add_schedule_table_even'>";
		}
		content+="<td align='left' width='20%' class='td_align_right'>"+global.views+"</td>"
		+"<td class='td_align_left'><input  type='text' id='views' name='views' value='"+views+"'   readonly='readonly' size='50' onclick=\"openUser('views','viewIds');\"/><input  type='hidden' value='"+viewIds+"' id='viewIds' name='viewIds'  /></td>"
		+"</tr>"
		+"<tr class='add_schedule_table_tr add_schedule_table_odd'>"
		+"<td align='left' width='20%' class='td_align_right'>"+global.tip+"</td>"
		+"<td class='td_align_left'>"
		+"<select id='tip' name='tip' value='"+tip+"' onchange='tipChange();'>";
		if(tip=='1'){
			content+="<option value='1' selected='selected' >"+global.tip1+"</option>";
		}else{
			content+="<option value='1'>"+global.tip1+"</option>";
		}
		if(tip=='2'){
			content+="<option value='2' selected='selected' >"+global.tip2+"</option>";
		}else{
			content+="<option value='2'>"+global.tip2+"</option>";
		}
		if(tip=='3'){
			content+="<option value='3' selected='selected' >"+global.tip3+"</option>";
		}else{
			content+="<option value='3'>"+global.tip3+"</option>";
		}
		if(tip=='4'){
			content+="<option value='4' selected='selected' >"+global.tip4+"</option>";
		}else{
			content+="<option value='4'>"+global.tip4+"</option>";
		}
		if(tip=='5'){
			content+="<option value='5' selected='selected' >"+global.tip5+"</option>";
		}else{
			content+="<option value='5'>"+global.tip5+"</option>";
		}
		if(tip=='6'){
			content+="<option value='6' selected='selected' >"+global.tip6+"</option>";
		}else{
			content+="<option value='6'>"+global.tip6+"</option>";
		}
		if(tip=='7'){
			content+="<option value='7' selected='selected'  >"+global.tip7+"</option>";
		}else{
			content+="<option value='7'>"+global.tip7+"</option>";
		}
		content+="</select>";

		if(tipDate==''){
			tipDate=startTime2;
		}
		if(tip=='4'){
			content+="<input type='text' size='10'  id='tipDate' name='tipDate' value='"+tipDate+"' onclick='WdatePicker();'/>";
		}else{
			content+="<input type='text' size='10'  id='tipDate' name='tipDate' value='"+tipDate+"' style='display:none;'  onclick='WdatePicker();'/>";
		}
		if(tip!='1'){
			content+="<input  type='text' size='10' id='tipTime' name='tipTime' value='"+tipTime+"'  onclick=\"WdatePicker({dateFmt:'HH:mm'});\" />";
		}else{
			content+="<input  type='text' size='10' id='tipTime' name='tipTime' value='"+getTimeHHMM2()+"' style='display:none;'  onclick=\"WdatePicker({dateFmt:'HH:mm'});\" />";
		}
		content+="</td>"
		+"</tr>"
		+"<tr class='add_schedule_table_tr add_schedule_table_even'>"
		+"<td align='left' width='20%'  class='td_align_right'>"+global.tipMethod+"</td>"
		+"<td class='td_align_left'>";
		
		if(tipMethod.indexOf(',1,')!=-1){
			content+='<input class="tipMethod" name="tipMethod" type="checkbox" value="1" checked="checked"/>'+global.tipMethod1;
		}else{
			content+='<input class="tipMethod" name="tipMethod" type="checkbox" value="1"/>'+global.tipMethod1;
		}
		if(tipMethod.indexOf(',2,')!=-1){
			content+='<input  class="tipMethod" name="tipMethod" type="checkbox" value="2" checked="checked"/>'+global.tipMethod2;
		}else{
			content+='<input class="tipMethod" name="tipMethod" type="checkbox" value="2"/>'+global.tipMethod2;
		}
		if(tipMethod.indexOf(',3,')!=-1){
			content+='<input class="tipMethod" name="tipMethod" type="checkbox" value="3" checked="checked"/>'+global.tipMethod3;
		}else{
			content+='<input class="tipMethod" name="tipMethod" type="checkbox" value="3"/>'+global.tipMethod3;
		}
		if(tipMethod.indexOf(',4,')!=-1){
			content+='<input class="tipMethod" name="tipMethod" type="checkbox" value="4" checked="checked"/>'+global.tipMethod4;
		}else{
			content+='<input class="tipMethod" name="tipMethod" type="checkbox" value="4"/>'+global.tipMethod4;
		}
			
		content += "<input type='checkbox' value='1'  id='isNowTip' name='isNowTip' style='margin-left:30px;'/>" +global.nowTip
		+"</td></tr>"
		content+="</table>"
		+"</div>";
		return content;
}


//日程无法修改
function unvalid(compsche,editsche,delsche,isComplete){
	$('#scheduleName').attr("readonly","readonly");
	$('#address').attr("disabled",true);
	$('#startDate').attr("disabled",true);
	$("#quan").attr("disabled",true);
	$("#jieshu").attr("disabled",true);
	$('#startTime').attr("disabled",true);
	$('#endDate').attr("disabled",true);
	$('#endTime').attr("disabled",true);
	$('#content').attr("readonly","readonly");
	$('#creators').attr("disabled",true);
	$(".isView").attr("disabled",true);
	$("#isLeaderView").attr("disabled",true);
	$("#isNowTip").attr("disabled",true);
	$(".tipMethod").attr("disabled",true);
	$(".selectcolor").attr("disabled",true);
	$('#status').attr("disabled",true);
	$('#leaderNames').attr("disabled",true);
	$('#pleases').attr("disabled",true);
	$('#inviteeNames').attr("disabled",true);
	$('#views').attr("disabled",true);
	$("#tip").attr("disabled",true);
	$("#tipDate").attr("disabled",true);
	$("#tipTime").attr("disabled",true);
	if(editsche==false||isComplete=='1'){
		$(".ui-dialog-buttonset button").each(function(){
			if($(this).text()=='编辑'){
				$(this).hide();
			}
		});
	}
	if(editsche==false||isComplete=='1'){
		$(".ui-dialog-buttonset button").each(function(){
			if($(this).text()=="确定"){
				$(this).hide();
			}
		});
	}
	if(delsche==false){
		$(".ui-dialog-buttonset button").each(function(){
			if($(this).text()=="删除"){
				$(this).hide();
			}
		});
	}
	if(compsche==false||isComplete=='1'){
		$(".ui-dialog-buttonset button").each(function(){
			if($(this).text()=="完成"){
				$(this).hide();
			}
		});
	}
	//$("#addsche").attr("disabled",true);
	$("#repeat").attr("disabled",true);
	
}
//日程开启修改
function invalid(){
	var isComplete=$("#isComplete").val();
	$('#scheduleName').removeAttr("readonly");
	$('#address').attr("disabled",false);
	$('#startDate').attr("disabled",false);
	$("#quan").attr("disabled",false);
	$("#jieshu").attr("disabled",false);
	$('#startTime').attr("disabled",false);
	$('#startTime2').attr("disabled",false);
	$('#endDate').attr("disabled",false);
	$('#endTime').attr("disabled",false);
	$('#endTime2').attr("disabled",false);
	
	$(".isView").attr("disabled",false);
	$(".tipMethod").attr("disabled",false);
	$("#isLeaderView").attr("disabled",false);
	$("#isNowTip").attr("disabled",false);
	$(".selectcolor").attr("disabled",false);
	
	$("#flag").val(2);
	
	$('#content').removeAttr("readonly");
	$('#status').attr("disabled",false);
	
	$('#leaderNames').attr("disabled",false);
	$('#leaderNames').removeAttr("readonly");
	$('#leaderNames').css("cursor","pointer");
	//$('#leaderIds').val(leaderIds);
	
	$('#pleases').attr("disabled",false);
	$('#pleases').removeAttr("readonly");
	$('#pleases').css("cursor","pointer");
	//$('#pleaseIds').val(pleaseIds);
	$('#inviteeNames').attr("disabled",false);
	$('#inviteeNames').removeAttr("readonly");
	$('#inviteeNames').css("cursor","pointer");
	$('#views').attr("disabled",false);
	$('#views').removeAttr("readonly");
	$('#views').css("cursor","pointer");
	//$('#viewIds').val(viewIds);
	$("#tip").attr("disabled",false);
	
	$("#tipDate").attr("disabled",false);
	$("#tipTime").attr("disabled",false);
	$("#tipTime2").attr("disabled",false);
	//$("#addsche").attr("disabled",false);
	$("#repeat").attr("disabled",false);
	if(isComplete!='1'){
	$(".ui-dialog-buttonset button").each(function(){
		if($(this).text()=="确定"){
			$(this).attr("display",'inline');
		}
	});
	}
}

function openNewSche() {
	$('#creators').removeAttr("readonly");
	$('#creators').css("cursor","pointer");
	
	$('#leaderNames').removeAttr("readonly");
	$('#leaderNames').css("cursor","pointer");
	
	$('#pleases').removeAttr("readonly");
	$('#pleases').css("cursor","pointer");

	$('#inviteeNames').removeAttr("readonly");
	$('#inviteeNames').css("cursor","pointer");
	
	$('#views').removeAttr("readonly");
	$('#views').css("cursor","pointer");
}

//跳转至按日日程显示 
function to(stype,mtype) {
	var addCss = $("#addCss").val();
	var ldate = $("#ldate").val();
	alert(contextPath+'/schedule/person_schedule2.action?ldate='
			+ ldate + '&stype=' + stype+"&mtype="+mtype+"&requestType=cms");
	if(addCss=='yes'){
		window.location.href = contextPath+'/schedule/person_schedule2.action?ldate='
		+ ldate + '&stype=' + stype+"&mtype="+mtype+"&requestType=cms";
	}else{
		window.location.href = contextPath+'/schedule/person_schedule2.action?ldate='
		+ ldate + '&stype=' + stype+"&mtype="+mtype;
	}
	
}
//
function to3(stype) {
	$("#mytb tr").each(function() {

	});
}

//鼠标经过显示新建
function showorno(userno,cday,ctype){
	if(ctype==1){
		$("#"+userno+cday).show();
	}
	if(ctype==2){
		$("#"+userno+cday).hide();
	}
}


// 鼠标经过邀请人输入框时显示邀请人状态div
function displayInviteState(){
	$("#inviteStateDiv").toggle();
}

//日程时间范围变更
function changeTime(ctype){
	if(ctype==1){
		$('#startTime').val("08:00");
		$('#endTime').val("17:30");
	}
	if(ctype==2){
		$('#startTime').val("08:00");
		$('#endTime').val("11:30");
	}
	if(ctype==3){
		$('#startTime').val("13:30");
		$('#endTime').val("17:30");
	}
	if(ctype==4){
		$('#startTime').val("18:00");
		$('#endTime').val("21:00");
	}
	
}

//弹出秘书领导窗口
function openLayer33(objId,conId,secUuid ,secUserNo ,secUserName ,leaderUserNos ,leaderUserNames){
	openLayer3(objId,conId);
	$("#secUuid").val(secUuid);
	$("#secUserNo").val(secUserNo);
	$("#secUserName").val(secUserName);
	$("#leaderUserNames").val(leaderUserNames);
	$("#leaderUserNos").val(leaderUserNos);
	$("#delssec").show();
}
	
//选择用户
function openUser(lname,lid){

	$.unit.open({
		 	labelField : lname,
			valueField : lid,
			selectType : 4
	});

}

//获得当前小时分钟
function getTime()
{
	var ERP_TIME = new Date();
	var intHours, intMinutes;
	var timestr="";
	
	intHours = ERP_TIME.getHours();
	intMinutes = ERP_TIME.getMinutes();
	
	if (intHours < 12) {
		timestr =timestr+ intHours+":";
	} else {
		intHours = intHours - 12;
		timestr =timestr+ intHours + ":";
	}
	if (intMinutes < 10) {
		timestr =timestr+ "0"+intMinutes;
	} else {
		timestr =timestr+ intMinutes;
	}
	return timestr;
}
	
	
	//获得当前小时分钟
	function getTimeH()
	{
		var ERP_TIME = new Date();
		var intHours;
		
		intHours = ERP_TIME.getHours();
		
		
		return intHours;
	}
	
	
	//获得当前小时分钟
	function getTimeM()
	{
		var ERP_TIME = new Date();
		var  intMinutes;
		var timestr="";
		

		intMinutes = ERP_TIME.getMinutes();
		if(intMinutes<15&&intMinutes>=5){
			intMinutes=10;
		}
		if(intMinutes<25&&intMinutes>=15){
			intMinutes=20;
		}
		if(intMinutes<35&&intMinutes>=25){
			intMinutes=30;
		}
		if(intMinutes<45&&intMinutes>=35){
			intMinutes=40;
		}
		if(intMinutes<55&&intMinutes>=45){
			intMinutes=50;
		}
		if((intMinutes<=60&&intMinutes>=55)||(intMinutes<5&&intMinutes>=0)){
			intMinutes=0;
		}
		if (intMinutes < 10) {
			timestr =timestr+ "0"+intMinutes;
		} else {
			timestr =timestr+ intMinutes;
		}
		return timestr;
	}
	
	
	
	function tipChange(){
		var tip=$("#tip").val();
		if(tip!='7'){
			$("#tipDate").hide();
			$("#tipTime").hide();
		}else{
			$("#tipDate").show();
			$("#tipTime").show();
			if($("#tipTime").val()==''){
				$("#tipTime").val(getTimeH());
				$("#tipTime2").val(getTimeM());
			}
		}
	}

	function quantian(){
		if($("#quan").attr("checked")=='checked'){
			$("#dstartTime").hide();
			$("#dendTime").hide();
		}else{
			$("#dstartTime").show();
			$("#dendTime").show();
			if($("#startTime").val()==''){
			$("#startTime").val(getTimeH());
			$("#startTime2").val(getTimeM());
			}
			if($("#endTime").val()==''){
				$("#endTime").val(getTimeH());
				$("#endTime2").val(getTimeM());
				}
		}
	}
	function jieshu(){
		if($("#jieshu").attr("checked")=='checked'){
			$("#trjieshu").show();
			if($("#endDate").val()==''){
				$("#endDate").val($("#startDate").val());
			}
			quantian();
		}else{
			$("#trjieshu").hide();
			quantian();
		}
	}
	
	function selColor(c){
		$("#mycolor").val(c);
		$("#showcolor").css("background-color",c);
	}
	
	
	function showView(obj){
		if($(obj).val()=='1'){
			$("#showview").show();
		}else{
			$("#showview").hide();
			$("#viewIds").val("");
			$("#views").val("");
		}
	}

//群组
	function openLayerGroupSet(ldate,groupid,stype){
		var content=getGroupHtml('','','','',ldate,groupid,stype,false);
//				var title=global.addGroup;
		var title="群组-名称";
		var json = 
		{
			title:title,  /*****标题******/
			autoOpen: true,  /*****初始化之后，是否立即显示对话框******/
			modal: true,     /*****是否模式对话框******/
			closeOnEscape: true, /*当按 Esc之后，是否应该关闭对话框******/
			draggable: true, /*****是否允许拖动******/  
			resizable: true, /*****是否可以调整对话框的大小******/  
			stack : false,   /*****对话框是否叠在其他对话框之上******/
			height: 250, /*****标题******/
			width: 600,   /*****标题******/
			content: content,/*****内容******/
			//open：事件,
			buttons: {
				"确定":addGroup
				//"取消": 事件2
			}
		};

		showDialog(json);
		//showScheduleDialog(global.addGroup,content,300,500);
	}
	function openLayerGroupSet2(uuid,groupName,userNames,userIds,ldate,groupid,stype){
		var content=getGroupHtml(uuid,groupName,userNames,userIds,ldate,groupid,stype,true);
//				var title=global.addGroup;
		var title="群组-名称";
		var json = 
		{
			title:title,  /*****标题******/
			autoOpen: true,  /*****初始化之后，是否立即显示对话框******/
			modal: true,     /*****是否模式对话框******/
			closeOnEscape: true, /*当按 Esc之后，是否应该关闭对话框******/
			draggable: true, /*****是否允许拖动******/  
			resizable: true, /*****是否可以调整对话框的大小******/  
			stack : false,   /*****对话框是否叠在其他对话框之上******/
			height: 250, /*****标题******/
			width: 600,   /*****标题******/
			content: content,/*****内容******/
			//open：事件,
			buttons: {
				"确定":addGroup
				//"删除":deleteGroup
				//"取消": 事件2
			}
		};

		showDialog(json);
		//showScheduleDialog(global.viewGroup,content,300,500);
	}
	function getGroupHtml(uuid,groupName,userNames,userIds,ldate,groupid,stype,delsche){
		var content="<div id='test_con3'>"
			+"<input type='hidden' id='uuid' name='uuid' value='"+uuid+"'/>"
			+"<input type='hidden' id='stype' name='stype' value='"+stype+"'/>"
			+"<input type='hidden' id='ldate' name='ldate' value='"+ldate+"'/>"
			+"<input type='hidden' id='groupid' name='groupid' value='"+groupid+"'/>"
			+"<table>"
			+"<tr class='tr_odd'>"
			+"<td align='left' width='20%' class='left' style='text-indent: 15px;'>"+global.gourpName+"</td>"
			+"<td><input type='text' name='groupName' value='"+groupName+"' id='groupName' size='50' /></td>"
			+"</tr>"
			+"<tr class='tr_even'>"
			+"<td align='left' width='20%'  style='text-indent: 15px;'>"+global.chengyuan+"</td>"
			+"<td>"
			+"<input  type='hidden' value='"+userIds+"' name='userIds' id='userIds'/>"
			+"<textarea style='cursor: pointer;background:#FFFFFF;margin: 5px; width: 90%;'  readonly='readonly' id='userNames' name='userNames' onclick=\"openUser('userNames','userIds');\">"+userNames+"</textarea>";
//					+"<input type='text' style='cursor: pointer;' value='"+userNames+"' readonly='readonly' name='userNames' id='userNames' size='50'  onclick=\"openUser('userNames','userIds');\"/>"
			+"</td>"
			+"</tr>"
			+"</table>"
//					+"<div align='center'>"
//					+"<table><tr><td class='group_set_btn'>"
//					+"<button onclick='addGroup();'>"+global.confirm+"</button>";
//					if(delsche==true){
//					content+="<button id='delsche' onclick='deleteGroup();'>"+global.deleteBtn+"</button>";
//					}else{
//					content+="<button id='delsche' style='display:none' onclick='deleteGroup();'>"+global.deleteBtn+"</button>";
//					}
//					content+="<button onclick='closeDialog();'>"+global.cancel+"</button></td><td>"
//					+"</td></tr></table>"
//					+"</div>"
			+"</div>";
			return content;
	}
	
	//删除群组
	function deleteGroup(){
		var uuid=$("#uuid").val();
			if(confirm(global.deleteConfirm)) {
					$.ajax({
						type : "POST",
						url : contextPath+"/schedule/group_delete.action",
						data : "uuid="+uuid,
						dataType : "text",
						success : function callback(result) {
							closeDialog();
							window.location.href=contextPath+"/schedule/schedule_setlist.action";
						}
					});
			}
		
	}
	//删除群组
	function deleteGroup2(uuid,ldate,groupid,type){
		if(confirm(global.deleteConfirm)) {
			$.ajax({
				type : "POST",
				url : contextPath+"/schedule/group_delete.action",
				data : "uuid="+uuid,
				dataType : "text",
				success : function callback(result) {
					var addCss = $("#addCss").val();
					if(addCss=="yes"){
						setTimeout(function(){searchWindow();},300);
					}else{
						if(type==1){
							window.location.href=contextPath+'/schedule/group_schedule.action?ldate='+ldate+'&ctype=0&groupid='+groupid;
						}else{
							window.location.href=contextPath+'/schedule/group_schedule.action?ldate='+ldate+'&ctype=1&groupid='+groupid;
						}
					}
					
				}
			});
		}
		
	}

	
	//添加群组
	function addGroup(){
		var uuid=$("#uuid").val();
		var stype=$("#stype").val();
		var ldate=$("#ldate").val();
		var groupid=$("#groupid").val();
		var groupName=$("#groupName").val();
		var userNames=$("#userNames").val();
		var userIds=$("#userIds").val();
		var addCss = $("#addCss").val();
		if(groupName==''){
			alert(global.groupNameNotNull);
			$("#groupName").focus();
			return;
		}
		if(userNames==''){
			alert(global.chengyuanNotNull);
			$("#userNames").focus();
			return;
		}
		//alert("userNo="+userNo+"&userName="+userName+"&groupUserNames="+groupUserNames+"&groupUserNos="+groupUserNos);
					$.ajax({
						type : "POST",
						url : contextPath+"/schedule/group_add.action",
						data : "uuid="+uuid+"&groupName="+groupName+"&userNames="+userNames+"&userIds="+userIds,
						dataType : "text",
						success : function callback(result) {
							closeDialog();
							//刷新窗口
							
							if(addCss=="yes"){
								setTimeout(function(){searchWindow();},300);
//										window.location.href=contextPath+"/pt/cms/index.jsp?uuid=c645ba94-a0d0-48f3-9ef4-a04624b548e8&treeName=SCHEDULE_CATE&mid=20136199117335&moduleid=fc5d811f-8bd1-40cc-a3eb-641498ac320a";
							}else{
								if(stype==1){
									window.location.href=contextPath+'/schedule/group_schedule.action?ldate='+ldate+'&ctype=0&groupid='+groupid;
								}else{
									window.location.href=contextPath+'/schedule/group_schedule.action?ldate='+ldate+'&ctype=1&groupid='+groupid;
								}
								//window.location.href=contextPath+"/schedule/schedule_setlist.action";
							}
						}
					});
		
	}
	
	//秘书
	function openSecNew(){
		var content=getSecHtml('','','','','',false);
		var title=global.addSec;
		var json = 
		{
			title:title,  /*****标题******/
			autoOpen: true,  /*****初始化之后，是否立即显示对话框******/
			modal: true,     /*****是否模式对话框******/
			closeOnEscape: true, /*当按 Esc之后，是否应该关闭对话框******/
			draggable: true, /*****是否允许拖动******/  
			resizable: true, /*****是否可以调整对话框的大小******/  
			stack : false,   /*****对话框是否叠在其他对话框之上******/
			height: 600, /*****标题******/
			width: 850,   /*****标题******/
			content: content,/*****内容******/
			//open：事件,
			buttons: {
				"确定":addSec
				//"取消": 事件2
			}
		};

		showDialog(json);
	}
	function openSec(secUuid ,secUserNo ,secUserName,leaderUserNos ,leaderUserNames){
		var content=getSecHtml(secUuid ,secUserName,secUserNo ,leaderUserNames,leaderUserNos ,true);
		var title=global.addSec;
		var json = 
		{
			title:title,  /*****标题******/
			autoOpen: true,  /*****初始化之后，是否立即显示对话框******/
			modal: true,     /*****是否模式对话框******/
			closeOnEscape: true, /*当按 Esc之后，是否应该关闭对话框******/
			draggable: true, /*****是否允许拖动******/  
			resizable: true, /*****是否可以调整对话框的大小******/  
			stack : false,   /*****对话框是否叠在其他对话框之上******/
			height: 600, /*****标题******/
			width: 850,   /*****标题******/
			content: content,/*****内容******/
			//open：事件,
			buttons: {
				"确定":addSec,
				"删除":deleteSec
				//"取消": 事件2
			}
		};

		showDialog(json);
	}
		function getSecHtml(uuid,secUserName,secUserNo,leaderUserNames,leaderUserNos,delssec){
			var content="<div id='test_con33' >"
				+"<input type='hidden' id='secUuid' value='"+uuid+"' />"
				+"<table>"
				+"<tr>"
				+"<td align='left' width='20%'>"+global.sec+":</td>"
				+"<td><input type='text' style='cursor: pointer;' name='secUserName' value='"+secUserName+"' id='secUserName' readonly='readonly' size='50' onclick=\"openUser('secUserName','secUserNo');\" /><input  type='hidden' value='"+secUserNo+"' name='secUserNo' id='secUserNo'/></td>"
				+"</tr>"
				+"<tr>"
				+"<td align='left' width='20%'>"+global.leader+":</td>"
				+"<td><input type='text' style='cursor: pointer;' name='leaderUserNames' value='"+leaderUserNames+"' id='leaderUserNames' readonly='readonly' size='50' onclick=\"openUser('leaderUserNames','leaderUserNos');\" /><input  type='hidden' value='"+leaderUserNos+"' name='leaderUserNos' id='leaderUserNos'/></td>"
				+"</tr>"
				+"</table>"
//						+"<div align='center'>"
//						+"<table><tr><td>"
//						+"<button onclick='addSec();'>"+global.confirm+"</button></td><td>";
//						if(delssec==true){
//						content+="<button id='delssec' onclick='deleteSec();'>"+global.deleteBtn+"</button></td>";
//						}else{
//						content+="<button id='delssec' style='display:none' onclick='deleteSec();'>"+global.deleteBtn+"</button></td>";
//						}
//						content+="<td>"
//						+"<button onclick='closeDialog();'>"+global.cancel+"</button></td><td>"
//						+"</td></tr></table>"
//						+"</div>"
				+"</div>";
				return content;
		}

//删除秘书
function deleteSec(){
	var secUuid=$("#secUuid").val();
	var secUserNo=$("#secUserNo").val();
	var secUserName=$("#secUserName").val();
	var groupUserNames=$("#leaderUserNames").val();
	var groupUserNos=$("#leaderUserNos").val();
		if(confirm(global.deleteConfirm)) {
				$.ajax({
					type : "POST",
					url : contextPath+"/schedule/leader_delete.action",
					data : "secUuid="+secUuid+"&secUserNo="+secUserNo+"&secUserName="+secUserName+"&leaderUserNames="+groupUserNames+"&leaderUserNos="+groupUserNos,
					dataType : "text",
					success : function callback(result) {
						closeDialog();
						window.location.href=contextPath+"/schedule/schedule_secsetlist.action";
					}
				});
		}
	
}
//添加秘书
function addSec(){
	var secUuid=$("#secUuid").val();
	var secUserNo=$("#secUserNo").val();
	var secUserName=$("#secUserName").val();
	var groupUserNames=$("#leaderUserNames").val();
	var groupUserNos=$("#leaderUserNos").val();
	if(secUserName==''){
		alert(global.secNotNull);
		return false;
	}
	if(groupUserNames==''){
		alert(global.secNotNull);
		return false;
	}
		$.ajax({
			type : "POST",
			url : contextPath+"/schedule/leader_add.action",
			data : "secUuid="+secUuid+"&secUserNo="+secUserNo+"&secUserName="+secUserName+"&leaderUserNames="+groupUserNames+"&leaderUserNos="+groupUserNos,
			dataType : "text",
			success : function callback(result) {
				closeDialog();
				window.location.href=contextPath+"/schedule/schedule_secsetlist.action";
			}
		});
}

function choseColor(obj){
	if($(obj).find(".colors").css("display")=='none'){
		$(obj).find(".colors").css("display","block");
	}else{
		$(obj).find(".colors").css("display","none");
	}
}
//新增方法
function choseColor1(obj){
	var value= $('#flag').val();
	if(value ==2 || value ==0) {   //当前为编辑(2)或新增(0)日程，需要展示颜色栏
		if($(obj).find(".colors").css("display")=='none'){
			$(obj).find(".colors").css("display","block");
		}else{
			$(obj).find(".colors").css("display","none");
		}
	}
	else if(value ==1)  //当前为查看日程，不要展示 颜色栏
		$(".colors").hide();
	
}

function selTag(obj){
	var scheduleId = $("#uuid").val();
	var tagId = $("#scheTag").val();
	if(scheduleId!=''&&$("#scheduleName").attr("disabled")=='disabled'){
		// 若处在不可编辑状态，则立即为此日程标记
		$.ajax({
			type : "POST",
			url : contextPath+"/schedule/tag/make_tag.action",
			data : {"scheduleId":scheduleId,"tagId":tagId},
			dataType : "text",
			sucess:function callback(){
				alert("sucess");
			}
		});
	}else if(scheduleId!=''&&$("#scheduleName").attr("disabled")!='disabled'){
		// 若处在修改日程且可编辑状态，则立即为此日程标记，并将共享设置成所选日程组的共享
		$.ajax({
			type : "POST",
			url : contextPath+"/schedule/tag/make_tag.action",
			data : {"scheduleId":scheduleId,"tagId":tagId},
			dataType : "text",
			sucess:function callback(){
				alert("sucess");
			}
		});
		var isView = $(obj).find(":checked").attr("isView");
		var isLeaderView = $(obj).find(":checked").attr("isLeaderView");
		var viewIds = $(obj).find(":checked").attr("viewIds");
		var viewNames = $(obj).find(":checked").attr("viewNames");
		$(".isView[value='"+ isView +"']").click();
		$("#viewIds").val(viewIds);
		$("#views").val(viewNames);
		if(isLeaderView=='1'){
			$("#isLeaderView").attr("checked","checked");
		}else{
			$("#isLeaderView").removeAttr("checked","");
		}
	}else if(scheduleId==''){
		// 若处新建日程状态，则将共享设置成所选日程组的共享，由后台标记
		var isView = $(obj).find(":checked").attr("isView");
		var isLeaderView = $(obj).find(":checked").attr("isLeaderView");
		var viewIds = $(obj).find(":checked").attr("viewIds");
		var viewNames = $(obj).find(":checked").attr("viewNames");
		$(".isView[value='"+ isView +"']").click();
		$("#viewIds").val(viewIds);
		$("#views").val(viewNames);
		if(isLeaderView=='1'){
			$("#isLeaderView").attr("checked","checked");
		}else{
			$("#isLeaderView").removeAttr("checked","");
		}
	}
	/**/
}

// 日程定时的弹出框提醒 
function scheduleTip(uuid,scheduleName,startDate,address){
	var content = "<div>"
		+"<input id='scheduleId' type='hidden' value='"+uuid+"'/>"
		+"<table class='add_schedule_table'>"
		+"<tr class='add_schedule_table_tr' >"
		+"<td align='left' width='30%'  class='td_align_right'>"+global.title+"：</td><td>" + scheduleName + "</td></tr>"
		+"<tr class='add_schedule_table_tr'><td align='left' width='20%' class='td_align_right'>"+global.time+"：</td>"
		+"<td>"+startDate
		+ "</td></tr>"
		+ "<tr class='add_schedule_table_tr' ><td align='left' width='20%'  class='td_align_right'>" 
		+ global.address +"：</td><td>" + address + "</td></tr>"
		+ "<tr class='add_schedule_table_tr' ><td align='left' width='20%'  class='td_align_right'>" 
		+ global.delayTip +"：</td><td><select id='delayTip' name='delayTip' >" 
		+ "<option value='5'>五分钟</option>"
		+ "<option value='10'>十分钟</option>"
		+ "<option value='30'>半小时</option>"
		+ "<option value='60'>一小时</option>"
		+ "<option value='120'>两小时</option>"
		+ "</select>"
		+ "</td></tr>"
		+"</table></div>";
	
	var buttons = new Object(); 
		buttons.确定 = function() {
			var scheduleId=$("#scheduleId").val();
			var delayMinute=$("#delayTip").val();
			$.ajax({
				type : "POST",
				url : contextPath+"/schedule/delay_tip.action",
				data : {"scheduleId":scheduleId,"minute":delayMinute},
				dataType : "text",
				success : function callback(result) {
					alert(result);
				},
				error:function(){
					alert("推延失败，请重试！");
				}
			});
		};
        buttons.取消 = function() { $(this).dialog("close"); };
	var json = 
	{
		title:"日程提醒",  /*****标题******/
		autoOpen: true,  /*****初始化之后，是否立即显示对话框******/
		modal: true,     /*****是否模式对话框******/
		closeOnEscape: false, /*当按 Esc之后，是否应该关闭对话框******/
		resizable: false, /*****是否可以调整对话框的大小******/  
		stack : true,   /*****对话框是否叠在其他对话框之上******/
		draggable: true, /*****是否允许拖动******/  
		height: 300, /*****标题******/
		width: 420,   /*****标题******/
		//open：事件,
		buttons:buttons
	};
	var str = '<div id="dialogModule1" title="" style="padding:0;margin:0; display:none;"><div class="dialogcontent1">'+content+'</div></div>';
	if ($("#dialogModule1 .dialogcontent1").html() == undefined) {
		$("body").after(str);
	}
	$("#dialogModule1 .dialogcontent1").oDialog(json);
}

//编辑后刷新窗口
function searchWindow(){
	var moduleid = "";
	if($(".page_index").length>0){
		moduleid = $(".rc").parents(".dnrw").attr("moduleid");
		alert(moduleid);
	}else{
		$(".openchild").each(function(){
			if($(this).attr("class").indexOf("activite")>-1){
				moduleid = $(this).attr("moduleid");
			}
		});
		if(moduleid==""){
			var json_ = readSearch();
			moduleid = json_.moduleid;
		}
	}
	pageLock("show");
	$.ajax({
		type:"post",
		async:false,
		data : {"uuid":moduleid},
		url:contextPath+"/cms/cmspage/viewcontent",
		success:function(result){
			$(".schedule_css").parent().html(result);
			pageLock("hide");
		}
	});
}

// 得到用户的日程列表
function getUserTag(){
	var userTag = [];
	$.ajax({
		type : "POST",
		async:false,
		url : contextPath+"/schedule/tag/user_tag.action",
		success : function callback(result) {
			userTag = result;
		}
	});
	return userTag;
}


// 点击日历修改 
function editTag(uuid,name,color,sort,participantIds,participantNames,secretaryIds,secretaryNames,
		isView,viewIds,viewNames){
	var json ={"uuid":uuid,"name":name,"color":color,"sort":sort ,
			"participantIds":participantIds,"participantNames":participantNames,"secretaryIds":secretaryIds,"secretaryNames":secretaryNames,
			"isView":isView,"viewIds":viewIds,"viewNames":viewNames};
	var title = global.editCalendar;
	if(''==json.uuid||'undefined'==json.uuid){
		title = global.addCalendar;
	}
	var dialogJson = 
	{
		title:title,  /*****标题******/
		autoOpen: true,  /*****初始化之后，是否立即显示对话框******/
		modal: true,     /*****是否模式对话框******/
		closeOnEscape: true, /*当按 Esc之后，是否应该关闭对话框******/
		draggable: true, /*****是否允许拖动******/  
		resizable: true, /*****是否可以调整对话框的大小******/  
		stack : false,   /*****对话框是否叠在其他对话框之上******/
		height: 360, /*****标题******/
		width: 600,   /*****标题******/
		content: getTagDailogHtml(json),/*****内容******/
		//open：事件,
		buttons: {
			"确定":saveTag
			//"删除":deleteGroup
			//"取消": 事件2
		}
	};
	showDialog(dialogJson);
}

// 日历组修改弹出框源码   
function getTagDailogHtml(tagJson){
	if(""==tagJson.color){
		tagJson.color="black";
	}
	if(""==tagJson.isView){
		tagJson.isView="3";
	}
	var content="<div id='test_con3'>"
		+"<input type='hidden' id='uuid' name='uuid' value='"+tagJson.uuid+"'/>"
		+"<table>"
		+"<tr class='dialog_tr tr_odd'>"
		+"<td class='left_td' align='left' width='20%' class='left' style='text-indent: 15px;'>"+global.scheduleName+"</td>"
		+"<td><input type='text' name='tagName' value='"+tagJson.name+"' id='tagName' size='50' />"
		+"<div class='chosecolor'>" 
		+"<input type='hidden' id='mycolor' name='mycolor' value='"+tagJson.color+"'/>"
		+"<span id='showcolor' style='background-color:"+tagJson.color+";'></span>"
		+"</div>"
		+"<div class='colorbutton' onclick='choseColor(this)'>" 
		+"<div class='colors' style='display:none;'>" 
		+"<span class='selectcolor' style='background-color:black;' onclick=\"selColor('black');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:red ' onclick=\"selColor('red');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:blue ' onclick=\"selColor('blue');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:orange ' onclick=\"selColor('orange');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:green ' onclick=\"selColor('green');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:yellow ' onclick=\"selColor('yellow');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:purple ' onclick=\"selColor('purple');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:silver ' onclick=\"selColor('silver');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:tan ' onclick=\"selColor('tan');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:maroon;' onclick=\"selColor('maroon');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:olive;' onclick=\"selColor('olive');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:gray;' onclick=\"selColor('gray');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"<span class='selectcolor' style='background-color:lime;' onclick=\"selColor('lime');\">&nbsp;&nbsp;&nbsp;&nbsp;</span>"
		+"</div>"
		+"</div>"
		+"</td></tr>"
		+"<tr class='dialog_tr tr_even'>"
		+"<td class='left_td' align='left' width='20%'  style='text-indent: 15px;'>"+global.sort+"</td>"
		+"<td>"
		+"<input  type='text' value='"+tagJson.sort+"' name='sort' id='sort' size='3'/>"
		+"</td>"
		+"</tr>"
		+"<tr class='dialog_tr tr_odd'>"
		+"<td class='left_td' align='left' width='20%' style='text-indent: 15px;'>"+global.pleases+"</td>"
		+"<td class='td_align_left'><input  type='text' id='participantNames' name='participantNames' value='"+tagJson.participantNames+"'   size='50' onclick=\"openUser('participantNames','participantIds');\"/><input  type='hidden' value='"+tagJson.participantIds+"' id='participantIds' name='participantIds'  /></td>"
		+"</tr>"
		+"<tr class='dialog_tr tr_even'>"
		+"<td class='left_td' align='left' width='20%' style='text-indent: 15px;'>"+global.sec+"</td>"
		+"<td class='td_align_left'><input  type='text' id='secretaryNames' name='secretaryNames' value='"+tagJson.secretaryNames+"'   size='50' onclick=\"openUser('secretaryNames','secretaryIds');\"/><input  type='hidden' value='"+tagJson.secretaryIds+"' id='secretaryIds' name='secretaryIds'  /></td>"
		+"</tr>"
		+"<tr class='dialog_tr tr_odd'>"
		+"<td class='left_td' align='left' width='20%'  style='text-indent: 15px;' >"+global.isView+"</td><td>";

		content+="<input type='radio' size='5'  name='isView' class='isView' value='3' onclick='showView(this);' ";
		 if(tagJson.isView=='3'){
			 content+=" checked='checked' ";
		 }
		content+="/>"+global.isView3;
		content+="<input type='radio' size='5' name='isView' class='isView' value='2' onclick='showView(this);' ";
		 if(tagJson.isView=='2'){
			 content+=" checked='checked' ";
		 }
		content+="/>"+global.isView2;
		content+="<input type='radio' size='5' class='isView' name='isView' value='1' onclick='showView(this);' ";
		 if(tagJson.isView=='1'){
			 content+=" checked='checked' ";
		 }
		content+="/>"+global.isView1+"</td></tr>";

		if(tagJson.viewIds==''){
			content+="<tr id='showview' style='display: none' class='dialog_tr tr_odd'>";
		}else{
			content+="<tr id='showview' class='dialog_tr tr_odd'>";
		}
		content+="<td class='left_td' align='left' width='20%' style='text-indent: 15px;'>"+global.views+"</td>"
		+"<td class='td_align_left'><input  type='text' id='views' name='views' value='"+tagJson.viewNames+"'   size='50' onclick=\"openUser('views','viewIds');\"/><input  type='hidden' value='"+tagJson.viewIds+"' id='viewIds' name='viewIds'  /></td>"
		+"</tr>"
		+"</table>"
		+"</div>";
		return content;
}

// 修改日历组信息 
function saveTag(){
	var reqData = {
			"uuid":$("#uuid").val(),
			"name": $("#tagName").val(),
			"sort":$("#sort").val(),
			"color":$("#mycolor").val() ,
			"participantIds":$("#participantIds").val() ,
			"secretaryIds":$("#secretaryIds").val() ,
			"isView":$(".isView:checked").val() ,
			"viewIds":$("#viewIds").val() ,
			"viewNames":$("#views").val() 
		};
	if(reqData.name==''){
		alert(global.tagNameNotNull);
		$('#tagName').select();
		return false;
	}
	var re = /^[0-9,]{0,8}$/;
	if(!re.test(reqData.sort)){
		alert(global.onlyNum);
		$('#sort').select();
		return false;
	}
	$.ajax({
		type : "POST",
		url : contextPath+"/schedule/tag/add",
		dataType : "text",
		data:reqData,
		success : function callback(result) {
			closeDialog();
			$.ajax({
				type:"post",
				async:false,
				data : {"stype":0,"requestType":"cms"},
				url: contextPath+"/schedule/person_schedule",
				success:function(result){
					$(".schedule_person_list").parent().html(result);
				}
			});
		}
	});
}

// 删除日历组 
function deleteTag(tagUuid){
	if(confirm(global.deleteConfirm)) {
		$.ajax({
			type : "POST",
			url : contextPath+"/schedule/tag/delete",
			dataType : "text",
			data:{"uuid":tagUuid},
			success : function callback(result) {
				$(".schedule_person_list").parent.html(result);
			}
		});
	}
}


