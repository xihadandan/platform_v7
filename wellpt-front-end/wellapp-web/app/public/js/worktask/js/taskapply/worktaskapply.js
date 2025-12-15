
$(function() {
	// 初使化
	JDS.call({
		service : 'workTaskApplyService.getData',
		data : [formUid, dataUid],
		success : function(result) {
			// init('abc', data, 'save', submit);
			$("#abc").dytable({
				data : result.data.formAndDataBean,
				btnSubmit : 'save',
				beforeSubmit : submit,
				open:function(){
					 
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
	 
	  
	   
});
function gentdate(str){
	var date=new Date();
	var   arr = str.split('-');
    date.setFullYear(arr[0],arr[1]-1,arr[2]);
    return date;
}
function Computation(sDate1, sDate2){   //sDate1和sDate2是2008-12-13格式    
	  var aDate, oDate1, oDate2, iDays    ;
	  aDate = sDate1.split("-")    ;
	  oDate1 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0])  ; //转换为12-13-2008格式    
	  aDate = sDate2.split("-")   ; 
	  oDate2 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0])    ;
	  iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 /24);   //把相差的毫秒数转换为天数    
 
	      return iDays  ;  
	}  
function submit() {
	 var rootFormData = $("#abc").dytable("formData");
	 var workFormBean = {}; 
	 
	 
	 var b=$("#abc").dytable("getFieldForFormData", {formuuid:$("#formUuid").val(),mappingname:"dy_work_task_apply_begin_time"});
	 var e=$("#abc").dytable("getFieldForFormData", {formuuid:$("#formUuid").val(),mappingname:"dy_work_task_apply_end_time"}); 
	 if(b==null||b=='')
		 return false;
	 if(e==null||e==''){
		 return false;
	 }
	 var jiange=Computation(b,e);
	 $(".selector").dytable("setFieldValue", {mappingName: "dy_work_task_apply_count", value : jiange});

		workFormBean.formUuid=formUid; 
		workFormBean.rootFormDataBean = rootFormData;
		 
		JDS.call({
			service : 'workTaskApplyService.saveData',
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
 
function afterselect(laReturn){ 
	 var rootFormData = $("#abc").dytable("formData");
	 var workFormBean = {};
		workFormBean.type1 = $("#type1").val();
		workFormBean.type2 = $("#type2").val();
		workFormBean.auditManUuid = laReturn['id'];
		workFormBean.auditManName = laReturn['name'];
		workFormBean.formUuid=formUuid; 
		workFormBean.rootFormDataBean = rootFormData;
		JDS.call({
			service : 'workPlanService.saveData',
			data : workFormBean,
			success : function(result) {
				//设置数据UUID
				$("#dataUuid").val(result.data.dataUuid);
				var url = $("#current_form_url_prefix").val() + "?formUid=" + $("#formUuid").val() + "&dataUid=" + $("#dataUuid").val();
				$("#current_form_url").attr("href", url).show();
				$.jBox.info(dymsg.saveOk, dymsg.tipTitle);
				 
			},
			error : function(xhr, textStatus, errorThrown) {
			}
		});
}
