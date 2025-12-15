$(function() {
	// 初使化
	JDS.call({
		service : 'workPlanService.getData',
		data : [$("#formUuid").val(), $("#dataUuid").val()],
		success : function(result) {
			 
			$("#abc").dytable({
				data : result.data.formAndDataBean,
				btnSubmit : 'save', 
				open:function(){
					 
				}
			});
		},
		error : function(xhr, textStatus, errorThrown) {
		}
	});

	  

	function submit() { 
	}
});