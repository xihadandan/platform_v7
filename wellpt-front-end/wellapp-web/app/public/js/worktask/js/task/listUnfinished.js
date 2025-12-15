$(function() {

	$('#submitbtn').click(function() {
		
		var arr=new Array();
		var i=0; 
		if(fromtype=='report'){
			$("[name='ids']").each(function() {
				if ($(this).attr("checked")) {
					var id=$(this).val();
					var state=$('#state_'+id).val();
					var name=$('#name_'+id).val();
					 
					arr[i++]={task_name:name,task_state:state,execute_memo:"",self_evaluate:"",judge_evaluate:"",judge_memo:""};
					 
				}
			});
		}else if(fromtype=='taskchange'||fromtype=='resolve'||fromtype=='delay'){  
			$("[name='ids']").each(function() { 
				if ($(this).attr("checked")) {
					var uuid=$(this).val();
					var id=$(this).val();
					var type=$('#type_'+id).val();
					var name=$('#name_'+id).val();
					var target=$('#target_'+id).val();
					var begin=$('#begin_'+id).val(); 
					var end=$('#end_'+id).val();
					var count=$('#count_'+id).val(); 
					arr[i++]={task_uuid:uuid,task_name:name,task_type:type,target:target,plan_begin_time:begin,plan_end_time:end,plan_work_count:count,memo:'',supervisior:'',sharer:'',duty_man:''};
					 
				}
			});
		}else if(fromtype=='cancel'){
			$("[name='ids']").each(function() {
				if ($(this).attr("checked")) {
					var uuid=$(this).val();
					var id=$(this).val();
					var type=$('#type_'+id).val();
					var name=$('#name_'+id).val();
					var target=$('#target_'+id).val();
					var begin=$('#begin_'+id).val(); 
					var end=$('#end_'+id).val();
					var count=$('#count_'+id).val(); 
					arr[i++]={task_uuid:uuid,task_name:name, task_type:type,target:target,plan_begin_time:begin,plan_end_time:end,plan_work_count:count,memo:''};
					 
				}
			});
		}
		
		
		window.returnValue=arr;
		window.close();
	});
	$('#cancelBtn').click(function() {
		window.close();
	});
});