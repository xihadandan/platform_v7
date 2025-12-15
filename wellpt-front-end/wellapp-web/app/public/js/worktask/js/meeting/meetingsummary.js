

$(function() {
	// 初使化
	JDS.call({
		service : 'meetingSummaryService.getData',
		data : [$("#formUuid").val(), $("#dataUuid").val()],
		success : function(result) {
			// init('abc', data, 'save', submit);
			$("#abc").dytable({
				data : result.data.formAndDataBean,
				btnSubmit : 'save',
				beforeSubmit : submit,
				open:function(){
					 $('#supervisior').click(function(){
						 $.unit.open({ 
								valueField : "supervisior",
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
				service : 'meetingSummaryService.saveData',
				data : workFormBean,
				success : function(result) {
					  
					 try{
						 window.opener.location.reload();
						 window.close();
					 }catch (e) {
						alert(e);
					}
				},
				error : function(xhr, textStatus, errorThrown) {
				}
			});
	}
});
