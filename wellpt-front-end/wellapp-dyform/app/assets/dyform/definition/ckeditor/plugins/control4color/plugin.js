var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);


controlConfig.initProperty = function(field){
	
	if(field == null ||  (typeof field) == undefined){
		field = new WColorClass();
		field.inputMode =  dyFormInputMode.colors;
		field.valueCreateMethod = "1";
		field.selectMode = "1";
	}
	
	//控件属性初始化公共设置.
	this.ctlPropertyComInitSet(field);
    if (formDefinition.isInputModeAsValueMap(field.inputMode)) {
        // 字段有真实值显示值
        for (var i in formDefinition.fields) {
            var fieldt = formDefinition.getField(i);

            if (!formDefinition.isCustomField(fieldt.name)
                || fieldt.name == field.name
                || !(formDefinition.isInputModeAsText(fieldt.inputMode) || formDefinition
                    .isInputModeAsTextArea(fieldt.inputMode))) {
                continue;
            }
            this.$("#relatedField").append("<option value=\"" + fieldt.name + "\">" + fieldt.displayName + "</option>");
        }

        var relatedField = field.relatedField;

        if (typeof relatedField != "undefined") {
            this.$("#relatedField").val(relatedField);
        }
    }
	
	if(field.selectMode=='2'){
		$("input[name='selectMode'][value='"+2+"']").attr("checked",true);
	}else{
		$("input[name='selectMode'][value='"+1+"']").attr("checked",true);
	}

    $("#unitUnique").val(field.unitUnique)
    if(field.unitUnique == "true"){
        $('#checkRule_6').attr("checked",true);
        $('#checkRule_5').attr("checked",false);
    }else if(field.unitUnique == "false"){
        $('#checkRule_5').attr("checked",true);
        $('#checkRule_6').attr("checked",false);
    }

}

controlConfig.collectFormAndFillCkeditor = function(){ 
	var field = new WColorClass();
	//field.ctrlField=$("#checkboxAttrCfgDiv_ctrlField").val();
	field.inputMode =  dyFormInputMode.colors;
	field.fieldCheckRules = [];

	field.selectMode=$("input[name='selectMode']:checked").val();
    field.relatedField = $("#relatedField").val();

	field.noNullValidateReminder = $("#noNullValidateReminder").val();
	field.uniqueValidateReminder = $("#uniqueValidateReminder").val();
    field.unitUnique = $("#unitUnique").val()
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

controlConfig.pluginName = CkPlugin.COLORS;
addPlugin(controlConfig.pluginName, "颜色控件", "颜色控件属性设置", controlConfig);

