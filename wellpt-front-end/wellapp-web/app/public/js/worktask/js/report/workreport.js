
$(function() {
	// 初使化
	JDS.call({
		service : 'workReportService.getData',
		data : [$("#formUuid").val(), $("#dataUuid").val()],
		success : function(result) {
			// init('abc', data, 'save', submit);
			$("#abc").dytable({
				data : result.data.formAndDataBean,
				btnSubmit : 'save',
				beforeSubmit : submit,
				buttons:{text:"选择未完成任务",method:choseMission ,subtableMapping:"dy_form_id_report_evaluate"},
				open:function(){
					initplan(); 
					inittype (); 
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
	 function checkRate(input)
	 {
	      var re = /^[1-9]+[0-9]*]*$/;    
	      if (!re.test(input.rate.value))
	     {
	         alert("请输入数字(例:0.02)");
	         input.rate.focus();
	         return false;
	      }
	 }
	function submit() { 
		var rootFormData = $("#abc").dytable("formData");
		 var workFormBean = {}; 
		 var allcount=$("#abc").dytable("getFieldForFormData", {formuuid:$("#formUuid").val(),mappingname:"dy_work_report_allcount"});
		 var delay=$("#abc").dytable("getFieldForFormData", {formuuid:$("#formUuid").val(),mappingname:"dy_work_report_delay"});
		 var good=$("#abc").dytable("getFieldForFormData", {formuuid:$("#formUuid").val(),mappingname:"dy_work_report_good"});
		 var hege=$("#abc").dytable("getFieldForFormData", {formuuid:$("#formUuid").val(),mappingname:"dy_work_report_hege"});
		 var plancount=$("#abc").dytable("getFieldForFormData", {formuuid:$("#formUuid").val(),mappingname:"dy_work_report_plan"});
		 var unpass=$("#abc").dytable("getFieldForFormData", {formuuid:$("#formUuid").val(),mappingname:"dy_work_report_unpass"});
		 allcount=parseInt(delay)+parseInt(good)+parseInt(hege)+parseInt(plancount)+parseInt(unpass);
		 $(".selector").dytable("setFieldValue", {mappingName: "dy_work_report_allcount", value : allcount});
		 
			workFormBean.formUuid=$("#formUuid").val(); 
			workFormBean.rootFormDataBean = rootFormData;
			if(rootFormData.formDatas[2].recordList.length==0){
				 $.jBox.info('请添加或者选择任务', dymsg.tipTitle);
				 return;
			}
			JDS.call({
				service : 'workReportService.saveData',
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

function inittype(){
	 $("select").each(function(){
		 if($(this).attr('id')==type1_name){
			 
	    	 var val=$(this).val();
	    	 var s=$("select[name='"+type2_name+"']");
	    	 
	    	 if(val==1){
	    		 s.empty();
	    		 var w=parseInt(weekOfYear);
	    		 if(weekOfYear<1){
	    			 w=w+3;
	    		 }else if(weekOfYear<2){
	    			 w=w+2;
	    		 } 
	    		 for(var i=w-2;i<w+5;i++){ 
	    			 if(parseInt(weekOfYear)==i){
	    				 s.append("<option selected value='"+i+"'>"+i+"周</option>"); 
	    				 
	    			 }else
	    			s.append("<option value='"+i+"'>"+i+"周</option>"); 
	    		 }
	    	 }else if(val==2){ s.empty();
	    		var w=new Date().getMonth() + 1;
	    		if(weekOfYear<1){
	    			 w=w+2;
	    		 } 
	    		 for(var i=w-1;i<w+2;i++){ 
	    			 if(parseInt(month)==i){
	    				 s.append("<option selected value='"+i+"'>"+i+"月</option>"); 
	    			 }else
	    			s.append("<option value='"+i+"'>"+i+"月</option>"); 
	    		 }
	    	 }else if(val==3){s.empty();
	    	 for(var i=1;i<5;i++){
    			 if(parseInt(season)==i){
    				 s.append("<option selected value='"+i+"'>"+i+"季度</option>"); 
    			 }else
    			 s.append("<option value='"+i+"'>"+i+"季度</option>"); 
    		 }
	    	 }else {s.empty();
	    		 var w=new Date().getFullYear();
	    		 for(var i=w-1;i<w+2;i++){
	    			 if(parseInt(year)==i){
	    				 s.append("<option selected value='"+i+"'>"+i+"年</option>"); 
	    			 }else
	    			 s.append("<option value='"+i+"'>"+i+"年</option>"); 
	    		 }
	    	 }
	     }
	 });
	 $("select").change(function(){
	     if($(this).attr('id')==type1_name){
	    	 var val=$(this).val();
	    	 var s=$("select[name='"+type2_name+"']");
	    	 
	    	 if(val==1){
	    		 s.empty();
	    		 var w=parseInt(weekOfYear);
	    		 if(weekOfYear<1){
	    			 w=w+3;
	    		 }else if(weekOfYear<2){
	    			 w=w+2;
	    		 } 
	    		 for(var i=w-2;i<w+5;i++){ 
	    			 if(parseInt(weekOfYear)==i){
	    				 s.append("<option selected value='"+i+"'>"+i+"周</option>"); 
	    				 
	    			 }else
	    			s.append("<option value='"+i+"'>"+i+"周</option>"); 
	    		 }
	    	 }else if(val==2){ s.empty();
	    		var w=new Date().getMonth() + 1;
	    		if(weekOfYear<1){
	    			 w=w+2;
	    		 } 
	    		 for(var i=w-1;i<w+2;i++){ 
	    			 if(parseInt(month)==i){
	    				 s.append("<option selected value='"+i+"'>"+i+"月</option>"); 
	    			 }else
	    			s.append("<option value='"+i+"'>"+i+"月</option>"); 
	    		 }
	    	 }else if(val==3){s.empty();
	    	 for(var i=1;i<5;i++){
    			 if(parseInt(season)==i){
    				 s.append("<option selected value='"+i+"'>"+i+"季度</option>"); 
    			 }else
    			 s.append("<option value='"+i+"'>"+i+"季度</option>"); 
    		 }
	    	 }else {s.empty();
	    		 var w=new Date().getFullYear();
	    		 for(var i=w-1;i<w+2;i++){
	    			 if(parseInt(year)==i){
	    				 s.append("<option selected value='"+i+"'>"+i+"年</option>"); 
	    			 }else
	    			 s.append("<option value='"+i+"'>"+i+"年</option>"); 
	    		 }
	    	 }
	     }
	    });
}
var task_name1;
var task_state1;
var target1;
var begin_time1;
var end_time1;
var count1;
var type1;
function initplan(){ 
	var subid=$("#abc" ).dytable("getIdInfo","dy_form_id_report_plan"); 
	var n1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_report_detail_name"});
	var s1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_report_detail_state"});
	var t1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_report_detail_target"});
	var b1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_report_detail_begin_time"});
	var e1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_report_detail_end_time"});
	var c1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_report_detail_count"});
	var ty1=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_report_detail_type"});
	task_name1=n1[0];
	task_state1=s1[0];
	target1=t1[0];
	begin_time1=b1[0];
	end_time1=e1[0];
	count1=c1[0];
	type1=ty1[0];
	 for(var i=0;i<taskarray.length;i++){ 

			var str="{"+type1+":'"+taskarray[i].task_type+"',"+task_name1+":'"+taskarray[i].task_name+"',"+task_state1+":'"+taskarray[i].task_state+"',"+target1
			+":'"+taskarray[i].target+"',"+begin_time1+":'"+taskarray[i].plan_begin_time+"',"+end_time1+":'"+taskarray[i].plan_end_time+"',"+count1+":'"+taskarray[i].plan_work_count+"'}";
		 
			 var obj=eval('('+str+')'); 
			
		 jQuery("#"+subid).jqGrid('addRowData',99,obj);

	 }  

}
var name2;
var state2;
function choseMission(){
	var ret= window.showModalDialog(ctx+"/worktask/task/list_unfinished.action?fromtype=report",null,"dialogWidth=800px;dialogHeight=600px");
	var subid=$("#abc" ).dytable("getIdInfo","dy_form_id_report_evaluate"); 
	var n2=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_report_evaluate_name"});
	var s2=$("#abc").dytable("getSubFieldInfo",{uuid:$("#formUuid").val(),fieldMappingName:"dy_work_report_evaluate_state"});
	name2=n2[0];
	state2=s2[0];
	for(var i=0;i<ret.length;i++){  
		var str="{"+name2+":'"+ret[i].task_name+"',"+state2+":'"+ret[i].task_state+"',execute_memo:'',self_evaluate:'',judge_evaluate:'',judge_memo:''}";
		 jQuery("#"+subid).jqGrid('addRowData',99,eval('('+str+')'));
		 
	}
}