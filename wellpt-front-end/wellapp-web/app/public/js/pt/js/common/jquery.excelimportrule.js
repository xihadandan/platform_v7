$(function() {
	$(".excelSelect").each(function(){
		var temp = $(this);
		$.ajax({
			type : "POST",
			url : ctx + "/basicdata/excelimportrule/excelTempOption",
			contentType : "application/json",
			dataType : "text",
			success : function(result) {
				temp.html(result);
			},
			error : function(result) {
			}
		});
	});
});
