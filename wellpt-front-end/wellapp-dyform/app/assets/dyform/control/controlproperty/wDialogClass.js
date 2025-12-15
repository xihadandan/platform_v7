var  WDialogClass = function(){  
	this.relationDataTextTwo = ""; 		 	
	this.relationDataValueTwo = ""; 		 	
	this.relationDataTwoSql = ""; 	
	this.relationDataDefiantion = ""; 		 	
	this.relationDataShowMethod = ""; 	
	this.relationDataShowType = ""; 	
	
	this.relativeMethod = dyRelativeMethod.DIALOG; //关联方式:默认为弹出框
	this.toJSON = toJSON;
   };

   WDialogClass.prototype=new MainFormFieldClass();	
