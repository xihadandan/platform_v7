

var controlConfig = {};
$.extend(controlConfig, $.ControlConfigUtil);

$.extend(controlConfig, {

	
	getFieldDefinition:function(fieldName){
		var field = null;
		field = formDefinition.getField(fieldName);//表单中从表的信息
		if(field == null ||  (typeof field) == "undefined"){
			field = new WNumberInputClass();
			field.inputMode =  dyFormInputMode.number;
			field.dbDataType = dyFormDataType["int"];
			field.decimal = '0';
			field.valueCreateMethod = dyFormInputValue.userImport;
			field.length = 7;
		}else{
			var fieldCopy = {};
			$.extend(true, fieldCopy, field);//这里只是做了一个副本,只有当用户点击确定完之后方可改变formDefinition对应的field对象 
			field = fieldCopy;
		}
		return field; 
	},


	initProperty:function(field){
		  
		//控件属性初始化公共设置.
		this.ctlPropertyComInitSet(field);
		//私有属性
		this.$("#decimal").val(field.decimal);
		this.$("#precision").val(field.precision);
		this.$("#length").attr("readonly", "readonly");//数值控件长度为固定
		this.$("#scale").val(field.scale);
		var _this = this;
		this.$("#dbDataType").change(function(){
			var value=_this.$("#dbDataType").val();
			if(value=='13'){
				_this.$("#length").val('9');
				_this.$("#tr_decimal").hide();
				_this.$("#decimal").val('0'); 
				_this.$("#tr_number").hide();
			}else if(value=='14'){
				_this.$("#length").val('16');
				_this.$("#tr_decimal").hide();
				_this.$("#decimal").val('0'); 
				_this.$("#tr_number").hide();
			}else if(value=='15'){
				_this.$("#length").val('12');
				_this.$("#tr_decimal").show();
				_this.$("#decimal").val('2');
				_this.$("#tr_number").hide();
			}else if(value=='12'){
				_this.$("#length").val('18');
				_this.$("#tr_decimal").show();
				_this.$("#decimal").val('2');
				_this.$("#tr_number").hide();
			}else if(value=='17'){
				_this.$("#tr_decimal").hide();
				_this.$("#tr_number").show();
			}
		});
		
		
		if(field.dbDataType=='15' || field.dbDataType=='12'){
			this.$("#tr_decimal").show();
			this.$("#decimal").val(field.decimal);
		}else {
			this.$("#tr_decimal").hide();
		}
		if(field.dbDataType=='17'){
			_this.$("#tr_number").show();
		}
		if(field.formula)
		this.$("#formula").text(field.formula); 
		this.setOperatorValue(field.operator);

        $("#unitUnique").val(field.unitUnique)
        if(field.unitUnique == "true"){
            $('#checkRule_6').attr("checked",true);
            $('#checkRule_5').attr("checked",false);
        }else if(field.unitUnique == "false"){
            $('#checkRule_5').attr("checked",true);
            $('#checkRule_6').attr("checked",false);
        }
	},

	setOperatorValue:function(operator){
		if(typeof operator == "undefined"){
			operator = new NumberOperator(); 
		}
		this.$("#plusMark").attr("checked", operator.plus );
		this.$("#minusMark").attr("checked", operator.minus );
		this.$("#plusUnit").val(operator.plusUnit); 
		this.$("#minusUnit").val(operator.minusUnit);
	},


	getOperatorValue:function(dbDataType){
		var operator = new NumberOperator();
		operator.plus = this.$("#plusMark").attr("checked" ) == "checked";
		operator.minus = this.$("#minusMark").attr("checked") == "checked";
		operator.plusUnit = this.$("#plusUnit").val();
		operator.minusUnit = this.$("#minusUnit").val(); 
		
		//number小数位
		var scale = this.$("#scale").val();
		var precision = this.$("#precision").val();
		if(dbDataType == '17'){
			if(scale && isNaN(scale)){
				alert("小数位须为数字");
				throw new Error("小数位须为数字");
			}
			if(parseInt(scale)>127 || parseInt(scale)<-84){
				alert("小数位须为-84-127");
				throw new Error("小数位须为-84-127");
			}
			if(precision && isNaN(precision)){
				alert("精度位须为数字");
				throw new Error("整数位须为数字");
			}
			if(parseInt(precision)>38 || parseInt(precision)<0){
				alert("精度位须为1-38");
				throw new Error("精度位须为1-38");
			}
		}
		if((operator.minus && isNaN(operator.minusUnit)) ||(operator.plus && isNaN(operator.plusUnit))){
			alert("加量、减量须为数字");
			throw new Error("减量须为数字");
		}
		
		
		if(operator.plusUnit != "" && operator.plus  ){
			if(dbDataType == dyFormDataType["float"]){
				try{
					operator.plusUnit = parseFloat(operator.plusUnit);
				}catch (e) {
					alert("加量须为小数");
					throw new Error("加量须为小数");
				}
			}else{
				try{
					operator.plusUnit = parseInt(operator.plusUnit);
				}catch (e) {
					alert("加量须为整数");
					throw new Error("加量须为整数");
				}
			}
			
			
		}
		
		// alert(this.$("#plusMark").attr("checked" ) + ":" + ("checked" == this.$("#plusMark").attr("checked" )) + ":" + isNaN(operator.plusUnit) );
		 
		if(operator.minusUnit != "" && operator.minus  ){
			if(dbDataType == dyFormDataType["float"]){
				try{
					operator.minusUnit = parseFloat(operator.minusUnit);
				}catch (e) {
					alert("减量须为小数");
					throw new Error("减量须为小数");
				}
			}else{
				try{
					operator.minusUnit = parseInt(operator.minusUnit);
				}catch (e) {
					alert("减量须为整数");
					throw new Error("减量须为整数");
				}
			}
			
			
		}
		 
		 
		return operator;
	},

	collectFormAndFillCkeditor:function(){
		var field = this.field;
		field.inputMode =  dyFormInputMode.number;
		field.fieldCheckRules = [];
		//控件公共属性收集
		var checkpass=this.collectFormCtlComProperty(field);
		field.operator = this.getOperatorValue(field.dbDataType);
		//added by linxr
		field.noNullValidateReminder = $("#noNullValidateReminder").val();
		field.uniqueValidateReminder = $("#uniqueValidateReminder").val();
        field.unitUnique = $("#unitUnique").val()
		if(field.operator.plus || field.operator.minus){//如果有加减功能，则需要设置控件的宽度
			if($.trim(field.ctlWidth).length == 0){
				field.ctlWidth = "50";
			}
			if($.trim(field.ctlHight).length == 0){
				field.ctlHight = "13";
			}
		}
	 	
		if(!checkpass){
			return false;
		}
		field.decimal = this.$("#decimal").val();
		field.formula = this.$("#formula").val();
		//number精度位
		field.precision = this.$("#precision").val();
		//number小数位
		field.scale = this.$("#scale").val();
		//创建控件占位符
		this.createControlPlaceHolder(this,this.editor.placeHolderImage,field);
		formDefinition.addField(field.name, field);
		//添加校验规则
		//field.contentFormat=$("#dbDataType").val();
		//field.fieldCheckRules=new Array;
		//field.fieldCheckRules.push({value:'n'+field.contentFormat, label:$("#dbDataType").find("option:selected").text()});
		return true;
	}
});



controlConfig.pluginName = CkPlugin.TAGS;
addPlugin(controlConfig.pluginName, "标签控件", "标签控件属性设置", controlConfig);

