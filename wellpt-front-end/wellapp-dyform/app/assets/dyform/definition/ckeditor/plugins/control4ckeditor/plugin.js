
var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.initProperty = function(field){
	if(field == null ||  (typeof field) == "undefined"){
		field = new WCkeditorClass();
		field.dbDataType = dyFormInputType._clob;
		field.valueCreateMethod = "1";
	}
	console.log(field.showMode);
    if(field.showMode) {
        $('#showMode' + field.showMode).attr('checked',true);
    }
    $("input[name=htmlCodec]").filter("[value="+field.htmlCodec+"]").prop("checked", true);
    $("#unitUnique").val(field.unitUnique)
    if(field.unitUnique == "true"){
        $('#checkRule_6').attr("checked",true);
        $('#checkRule_5').attr("checked",false);
    }else if(field.unitUnique == "false"){
        $('#checkRule_5').attr("checked",true);
        $('#checkRule_6').attr("checked",false);
    }

	//控件属性初始化公共设置.
	this.ctlPropertyComInitSet(field);
};


controlConfig.collectFormAndFillCkeditor = function(){
	var field = new WCkeditorClass();
	field.fieldCheckRules = [];
	field.inputMode =  dyFormInputMode.ckedit;
	field.dbDataType = dyFormDataType.clob;
	field.noNullValidateReminder = $("#noNullValidateReminder").val();
	field.uniqueValidateReminder = $("#uniqueValidateReminder").val();
	field.showMode = $('input[name="showMode"]:checked').val();
    field.unitUnique = $("#unitUnique").val()
    field.htmlCodec = $("input[name=htmlCodec]:checked").val();
	//控件公共属性收集
	var checkpass=this.collectFormCtlComProperty(field);
	if(!checkpass){
		return false;
	}
	
	
	//创建控件占位符
	this.createControlPlaceHolder(this,this.editor.placeHolderImage,field);
	//formDefinition.fields[field.name] = field; 
	formDefinition.addField(field.name, field);
	return true;
}

controlConfig.getInputEvents = function(){
	return [
		{
			id : "focus",
			chkDisabled : true,
			name : "focus"
		},
		{
			id : "blur",
			chkDisabled : true,
			name : "blur"
		},
		{
			id : "change",
			chkDisabled : true,
			name : "change"
		},
		{
			id : "keypress",
			chkDisabled : true,
			name : "keypress"
		},
		{
			id : "keyup",
			chkDisabled : true,
			name : "keyup"
		},
		{
			id : "keydown",
			chkDisabled : true,
			name : "keydown"
		}
		]	
}



controlConfig.pluginName = CkPlugin.CKEDITORCTL;
addPlugin(controlConfig.pluginName, "富文本控件", "富文本控件属性设置", controlConfig);

