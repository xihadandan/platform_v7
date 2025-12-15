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
						 if($(this).attr('id')==type1_name){
							 
					    	 var val=$(this).val();
					    	 var s=$("select[name='"+type2_name+"']");
					    	  
					    	 if(val==1){
					    		 s.empty();
					    		 var w=parseInt(weekOfYear);
					    		 if(w<1){
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
					    		if(w<1){
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
					    			 if(parseInt(season)==i){
					    				 s.append("<option selected value='"+i+"'>"+i+"月</option>"); 
					    			 }else
					    			s.append("<option value='"+i+"'>"+i+"月</option>"); 
					    		 }
					    	 }else if(val==3){s.empty();
					    	 for(var i=1;i<5;i++){
				    			 if(parseInt(month)==i){
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
	 

	function submit() { 
		 var rootFormData = $("#abc").dytable("formData");
		 var type1=$("#abc").dytable("getFieldForFormData", {formuuid:$("#formUuid").val(),mappingname:"dy_work_plan_type1"});
		 var type2=$("#abc").dytable("getFieldForFormData", {formuuid:$("#formUuid").val(),mappingname:"dy_work_plan_type2"});
		  
		 var workFormBean = {}; 
			workFormBean.formUuid=$("#formUuid").val(); 
			workFormBean.rootFormDataBean = rootFormData;
			workFormBean.type1=type1;
			workFormBean.type2=type2;  
			workFormBean.userId=userId;
			if(rootFormData.formDatas[1].recordList.length==0){
				 $.jBox.info('请添加或者选择任务', dymsg.tipTitle);
				 return;
			}
			JDS.call({
				service : 'workPlanService.saveData',
				data : workFormBean,
				success : function(result) {
					 var msg=result.data.msg;
					 if(msg!=null&&msg!=''){
						 $.jBox.info(msg, dymsg.tipTitle);
						 return;
					 }
					//$.jBox.info(dymsg.saveOk, dymsg.tipTitle);
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