
var controlConfig = {};
 

$.extend(controlConfig, $.ControlConfigUtil);
 
$.extend(controlConfig, {
	 


	initProperty :function(field){
		if(field == null ||  (typeof field) == "undefined"){
			field = new WComboBoxClass();
			field.dbDataType = dyFormInputType._text;
			field.valueCreateMethod = dyFormInputValue.userImport;
			field.inputMode =  dyFormInputMode.job;
		}else{
		}

		//控件属性初始化公共设置.
		this.ctlPropertyComInitSet(field);

        $("#unitUnique").val(field.unitUnique)
        if(field.unitUnique == "true"){
            $('#checkRule_6').attr("checked",true);
            $('#checkRule_5').attr("checked",false);
        }else if(field.unitUnique == "false"){
            $('#checkRule_5').attr("checked",true);
            $('#checkRule_6').attr("checked",false);
        }
	},


	collectFormAndFillCkeditor : function(){
		var field = new WComboBoxClass();
		field.inputMode =  dyFormInputMode.job;
		//added by linxr
		field.fieldCheckRules = [];
		field.noNullValidateReminder = $("#noNullValidateReminder").val();
		field.uniqueValidateReminder = $("#uniqueValidateReminder").val();
        field.unitUnique = $("#unitUnique").val()
		
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
	
});

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
		}
		]	
}

controlConfig.pluginName = CkPlugin.JOBS;
addPlugin(controlConfig.pluginName, "职位控件", "职位控件属性设置", controlConfig);

