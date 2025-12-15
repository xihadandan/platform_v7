
$(function() {
	
	$('#adminNames').click(function(){
		$.unit.open({
			labelField : "adminNames",
			valueField : "adminIds",
			selectType : 4,
			multiple : true
		});
		 
	});
	$('#readerNames').click(function(){
		$.unit.open({
			labelField : "readerNames",
			valueField : "readerIds",
			selectType : 4, 
			multiple : true
		});
		 
	});$('#yearUsemanNames').click(function(){
		$.unit.open({
			labelField : "yearUsemanNames",
			valueField : "yearUsemanIds",
			selectType : 4,
			multiple : true
		});
		 
	});
	$('#seasonUsemanNames').click(function(){
		$.unit.open({
			labelField : "seasonUsemanNames",
			valueField : "seasonUsemanIds",
			selectType : 4, 
			multiple : true
		});
		 
	});

	$('#monthUsemanNames').click(function(){
		$.unit.open({
			labelField : "monthUsemanNames",
			valueField : "monthUsemanIds",
			selectType : 4, 
			multiple : true
		});
		 
	}); 
	
	$('#weekUsemanNames').click(function(){
		$.unit.open({
			labelField : "weekUsemanNames",
			valueField : "weekUsemanIds",
			selectType : 4,
			multiple : true
		});
		 
	});
	$('#recordUsemanNames').click(function(){
		$.unit.open({
			labelField : "recordUsemanNames",
			valueField : "recordUsemanIds",
			selectType : 4, 
			multiple : true
		});
		 
	});
	
	$('#submitBtn').click(function(){
		var recordUsemanIds=('#recordUsemanIds').val();
		var recordUsemanIds=('#recordUsemanIds').val();
		var keepMonth=('#keepMonth').val();
	});
});