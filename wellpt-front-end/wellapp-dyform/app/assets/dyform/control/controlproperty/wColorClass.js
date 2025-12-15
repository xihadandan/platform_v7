var  WColorClass = function(){  
	this.ctrlField = ""; 		 	//checkbox的属性
    this.selectMode ="2";//选择模式，单选1，多选2
    this.relatedField ="";//关联字段
	this.toJSON = toJSON;
   };

   WColorClass.prototype=new MainFormFieldClass();	
