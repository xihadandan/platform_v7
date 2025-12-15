var did=$("#dataUuid").val();
$(function() {
	// 初使化
	JDS.call({
		service : 'workPlanService.getData',
		data : [$("#formUuid").val(), $("#dataUuid").val()],
		success : function(result) {
			// init('abc', data, 'save', submit);
			$("#abc").dytable({
				data : result.data.formAndDataBean,
				btnSubmit : 'save',
				beforeSubmit : submit,
				open:function(){
					
					  
					 $("select").each(function(){
						 if($(this).attr('id')==type1_name){//类型1
							 var val=$(this).val();
							 var s=$("select[name='"+type2_name+"']");
							 if(val==1){
								 weekOfYear=type2_value;
								 s.empty();
					    		 var w=parseInt(weekOfYear);
					    		 if(w<1){
					    			 w=w+3;
					    		 }else if(w<2){
					    			 w=w+2;
					    		 } 
					    		 for(var i=w-2;i<w+5;i++){ 
					    			s.append("<option value='"+i+"' "+(i==type2_value?"selected":"")+"  >"+i+"周</option>"); 
					    		 }
							 }else if(val==2){
								 s.empty();
					    		var w=parseInt(type2_value);
					    		if(w<1){
					    			 w=w+2;
					    		 } 
					    		 for(var i=w-1;i<w+2;i++){ 
					    			s.append("<option value='"+i+"'  "+(i==type2_value?"selected":"")+">"+i+"月</option>"); 
					    		 }
							 }else if(val==3){ s.empty();
							 	if(type2_value==1){
							 		 s.append("<option value='1'>1季度</option>"); 
							 	}else{
							 		 s.append("<option value='1' selected>1季度</option>"); 
							 	}
								if(type2_value==2){
							 		 s.append("<option value='2'>2季度</option>"); 
							 	}else{
							 		 s.append("<option value='2' selected>2季度</option>"); 
							 	}
								if(type2_value==3){
							 		 s.append("<option value='3'>3季度</option>"); 
							 	}else{
							 		 s.append("<option value='3' selected>3季度</option>"); 
							 	}
								if(type2_value==4){
							 		 s.append("<option value='4'>4季度</option>"); 
							 	}else{
							 		 s.append("<option value='4' selected>4季度</option>"); 
							 	}
								 
							 }else {s.empty();
							 	var w=parseInt(type2_value);
					    		 for(var i=w-1;i<w+2;i++){ 
					    			 s.append("<option value='"+i+"' "+(i==type2_value?"selected":"")+">"+i+"年</option>"); 
					    		 }
				    	 }
						 }
					 });
					 
				}
			});
		},
		error : function(xhr, textStatus, errorThrown) {
		}
	});

	 
	function submit() { 
	}
});