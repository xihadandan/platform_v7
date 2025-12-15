var  WCheckBoxClass = function(){  
	this.ctrlField = ""; 		 	//checkbox的属性
	this.optionDataSource = "1"; 		 	//备选项来源1:常量,2:字典
	//字典代码
	this.dictCode = ""; 
	//Select,radio, checkbox选项
	//以map的形式保存key为值，value为备注 
	/*Checkbox/radio/select都有option,在optionDataSource属性为1的情况下,option的值则来源于该字段.
	该字段的值以map的形式保存key为值，value为备注*/ 
	this.optionSet=[];	
	
	this.selectMode ="2";//选择模式，单选1，多选2
	this.singleCheckContent ="";//单选 选中内容
	this.singleUnCheckContent ="";//单选 取消选中内容
	this.selectDirection = "0";
	this.checkboxAll = "0";
    this.checkboxMin = "";
	this.checkboxMax = "";
	this.selectAlign = "0";
	this.toJSON = toJSON;
   };

   WCheckBoxClass.prototype=new MainFormFieldClass();	
