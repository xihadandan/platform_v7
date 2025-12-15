 
$(function() {
	// 初使化
	JDS.call({
		service : 'workTaskResolveService.getData',
		data : [$("#formUuid").val(), $("#dataUuid").val()],
		success : function(result) {
			// init('abc', data, 'save', submit);
			$("#abc").dytable({
				data : result.data.formAndDataBean,
				btnSubmit : 'save',
				beforeSubmit : submit,   
				buttons:[{"text": "选择完成任务","method":choseMission,"subtableMapping":"dy_form_id_task_resolve_assign"},
				         {"text": "分解","method":selectOne,"subtableMapping":"dy_form_id_task_resolve_detail"}
				         ],

				open:function(){
					 
					 $('#sharer').click(function(){
						 $.unit.open({ 
								valueField : "sharer",
								selectType : 4 
						});

					 }); 
				}
			});
		},
		error : function(xhr, textStatus, errorThrown) {
		}
	});
	$('#closebtn').click(function(){
		if(!confirm('是否要保存编辑内容，是则保存并关闭，否则不保存并关闭')){
			window.close();
		}else{
			$('#save').click();
		}
	});
	 $('#submitbtn').click(function(){
		 
		
	 });

	function submit() { 
		var rootFormData = $("#abc").dytable("formData");
		 var workFormBean = {}; 
			workFormBean.formUuid=$("#formUuid").val(); 
			workFormBean.rootFormDataBean = rootFormData;
			if(rootFormData.formDatas[1].recordList.length==0){
				 $.jBox.info('请添加或者选择明细', dymsg.tipTitle);
				 return;
			}
			JDS.call({
				service : 'workTaskResolveService.saveData',
				data : workFormBean,
				success : function(result) {
					  
					 try{
						 window.opener.location.reload();
						 window.close();
					 }catch (e) { 
					}
				},
				error : function(xhr, textStatus, errorThrown) {
				}
			});
	}
});
var task_name1; 
var target1;
var begin_time1;
var end_time1;
var count1;
var type1;
var i=0;
function choseMission(){
	var subid=$("#abc" ).dytable("getIdInfo","dy_form_id_task_resolve_assign");
	var n1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_assign_name"}); 
	var t1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_assign_target"});
	var b1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_assign_begin"});
	var e1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_assign_end"});
	var c1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_assign_planworkcount"});
	var ty1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_assign_type"});
	task_name1=n1[0]; 
	target1=t1[0];
	begin_time1=b1[0];
	end_time1=e1[0];
	count1=c1[0];
	type1=ty1[0]; 
	var taskarray= window.showModalDialog(ctx+"/worktask/task/list_unfinished.action?fromtype=resolve",null,"dialogWidth=800px;dialogHeight=600px");
	for(var i=0;i<taskarray.length;i++){  
		var str="{"+type1+":'"+taskarray[i].task_type+"',"+task_name1+":'"+taskarray[i].task_name+"',"+target1
		+":'"+taskarray[i].target+"',"+begin_time1+":'"+taskarray[i].plan_begin_time+"',"+end_time1+":'"+taskarray[i].plan_end_time+"',"+count1+":'"+taskarray[i].plan_work_count+"'}";
	 
		 var obj=eval('('+str+')'); 
		
	 jQuery("#"+subid).jqGrid('addRowData',i++,obj);
		 
	}
}


function selectOne(){
	var subid=$("#abc" ).dytable("getIdInfo","dy_form_id_task_resolve_assign");
	var subid2=$("#abc" ).dytable("getIdInfo","dy_form_id_task_resolve_detail");
	var n1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_assign_name"}); 
	var t1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_assign_target"});
	var b1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_assign_begin"});
	var e1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_assign_end"});
	var c1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_assign_planworkcount"});
	var ty1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_assign_type"});
	task_name1=n1[0]; 
	target1=t1[0];
	begin_time1=b1[0];
	end_time1=e1[0];
	count1=c1[0];
	type1=ty1[0]; 
	var rowid = jQuery("#"+subid).jqGrid('getGridParam','selrow');
	if(rowid){
		var rowdata = jQuery("#"+subid).jqGrid('getRowData',rowid); 
		
		var stn=eval("rowdata."+task_name1);
		 
		
		var stype=eval("rowdata."+type1);
		var starget=eval("rowdata."+target1);
		var sbe=eval("rowdata."+begin_time1);
		var sed=eval("rowdata."+end_time1);
		var sc=eval("rowdata."+count1);
		
		var n2=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_detail_name"}); 
		var t2=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_detail_target"});
		var b2=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_detail_begintime"});
		var e2=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_detail_endtime"});
		var c2=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_detail_count"});
		var ty2=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_task_resolve_detail_type"});
		task_name2=n2[0]; 
		target2=t2[0];
		begin_time2=b2[0];
		end_time2=e2[0];
		count2=c2[0];
		type2=ty2[0]; 
		var str="{"+type2+":'"+stype+"',"+task_name2+":'"+stn+"',"+target2
		+":'"+starget+"',"+begin_time2+":'"+sbe+"',"+end_time2+":'"+sed+"',"+count2+":'"+sc+"'}";
		 var obj=eval('('+str+')'); 
		 jQuery("#"+subid2).jqGrid('addRowData',i++,obj);
	} else{
		alert('请选择一条上级分配的任务');
	}
	//$("#grid").jqGrid('setRowData',行号,{'列ID':'值'});
}

 