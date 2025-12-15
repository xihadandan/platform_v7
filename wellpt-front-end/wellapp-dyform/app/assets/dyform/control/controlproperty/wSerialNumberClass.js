var  WSerialNumberClass = function(){  
	this.designatedId = ""; 			
	this.designatedType = ""; 		 	
	this.isOverride = ""; 		 	
	this.isSaveDb = "";
    this.serialNumberTips = ""; // 流水号重复提示
	this.toJSON = toJSON;
   };

   WSerialNumberClass.prototype=new MainFormFieldClass();	
