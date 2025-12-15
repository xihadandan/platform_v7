var NumberOperator = function(){
	this.plus = false; //加号
	this.plusUnit  = 1; //加多少
	this.minus = false; //减号
	this.minusUnit  = 1;//减多少
	this.formula = "";
};


var  WNumberInputClass = function(){
	this.decimal = 2; 		//浮点数的计算结果保留位数
	/*
	 * 操作符,值为json对象
		{
		“plus”:true, //加号
		“plusUnit”:1, //加多少
		“minus”:true, //减号
		“minusUnit”:1//减多少
		} 
	 */
	this.operator = new NumberOperator();
	this.toJSON = toJSON;
   };

   WNumberInputClass.prototype=new MainFormFieldClass();	
