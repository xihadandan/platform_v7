var  WRadioClass = function(){  
	this.optionDataSource = "1"; 		 	//备选项来源1:常量,2:字典
	//字典代码
	this.dictCode = ""; 
	//Select,radio, checkbox选项
	//以map的形式保存key为值，value为备注 
	/*Checkbox/radio/select都有option,在optionDataSource属性为1的情况下,option的值则来源于该字段.
	该字段的值以map的形式保存key为值，value为备注*/ 
	this.optionSet=[];
    this.radioStatus='1';
	this.selectDirection='0';
	this.radioShape='0';
	this.radioCancel = '0';
	this.selectAlign = '0';
	this.toJSON = toJSON;
   };

   WRadioClass.prototype=new MainFormFieldClass();	
