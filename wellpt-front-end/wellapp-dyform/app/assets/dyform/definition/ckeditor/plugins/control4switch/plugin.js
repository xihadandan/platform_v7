var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.initProperty = function(field){
	
	if(field == null ||  (typeof field) == undefined){
		field = new WSwitchsClass();
		field.inputMode =  dyFormInputMode.switchs;
		field.valueCreateMethod = "1";
		field.switchsVal = "0"
	}
	
	//控件属性初始化公共设置.
	this.ctlPropertyComInitSet(field);
	$("#openText").val(field.openText)
	$("#closeText").val(field.closeText)
	if(field.switchsVal == "0"){
		$("input[name='switchsVal'][value='"+0+"']").attr("checked",true);
	}else{
		$("input[name='switchsVal'][value='"+1+"']").attr("checked",true);
	}
}

controlConfig.exitDialog = function(){
	this.editor.focusedDom = null;
}

controlConfig.collectFormAndFillCkeditor = function(){ 
	var field = new WSwitchsClass();
	field.inputMode =  dyFormInputMode.switchs;
	field.fieldCheckRules = [];

	field.openText = $("#openText").val();
	field.closeText = $("#closeText").val();
	field.switchsVal = $("input[name='switchsVal']:checked").val();
	
	field.noNullValidateReminder = $("#noNullValidateReminder").val();
	//end
	//控件公共属性收集
	var checkpass=this.collectFormCtlComProperty(field);
	
	if(!checkpass){
		return false;
	}
	
	//创建控件占位符
	this.createControlPlaceHolder(this,this.editor.placeHolderImage,field);
	formDefinition.addField(field.name, field);
	return true;
}

controlConfig.getInputEvents = function(){
	return [
		{
			id : "change",
			chkDisabled : true,
			name : "change"
		}
	]	
}

if ($.fn.placeholder.input && $.fn.placeholder.textarea) {
}else{$('input, textarea').placeholder();}

controlConfig.pluginName = CkPlugin.SWITCHS;
addPlugin(controlConfig.pluginName, "按钮开关", "按钮开关控件属性设置", controlConfig);

