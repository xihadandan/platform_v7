
$(function() {
	// 初使化
	JDS.call({
		service : 'workTaskAssignService.getData',
		data : [$("#formUuid").val(), $("#dataUuid").val()],
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

	function submit() {
		 var b=$("#abc").dytable("getFieldForFormData", {formuuid:$("#formUuid").val(),mappingname:"dy_work_task_assign_begintime"});
		 var e=$("#abc").dytable("getFieldForFormData", {formuuid:$("#formUuid").val(),mappingname:"dy_work_task_assign_endtime"}); 
		 if(b==null||b=='')
			 return false;
		 if(e==null||e==''){
			 return false;
		 }
		 var jiange=Computation(b,e);
		 $(".selector").dytable("setFieldValue", {mappingName: "dy_work_task_assign_count", value : jiange});
		 
		 var rootFormData = $("#abc").dytable("formData");
		 var workFormBean = {}; 
			workFormBean.formUuid=$("#formUuid").val(); 
			workFormBean.rootFormDataBean = rootFormData;
			JDS.call({
				service : 'workTaskAssignService.saveData',
				data : workFormBean,
				success : function(result) {
					 
					//$.jBox.info(dymsg.saveOk, dymsg.tipTitle);
					 try{
						 window.opener.location.reload();
						 window.close();
					 }catch (e) {
						// TODO: handle exception
					}
				},
				error : function(xhr, textStatus, errorThrown) {
				}
			});
		
	}
});
function Computation(sDate1, sDate2){   //sDate1和sDate2是2008-12-13格式    
	  var aDate, oDate1, oDate2, iDays    ;
	  aDate = sDate1.split("-")    ;
	  oDate1 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0])  ; //转换为12-13-2008格式    
	  aDate = sDate2.split("-")   ; 
	  oDate2 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0])    ;
	  iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 /24);   //把相差的毫秒数转换为天数    

	      return iDays  ;  
	} 