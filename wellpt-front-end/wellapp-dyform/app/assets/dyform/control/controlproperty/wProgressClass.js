var  WProgressClass = function(){  
	this.ctrlField = ""; 		 	//checkbox的属性
	
	this.defaultProgress ="0";//默认值
	this.progressMin ="";//最小值
	this.progressMax ="";//最大值
	this.progressColor ="";//自定义颜色
	this.progressUnit = "";
	this.editProgress = "0";
	this.progressHeight = "0";
	this.toJSON = toJSON;
   };

   WProgressClass.prototype=new MainFormFieldClass();	
