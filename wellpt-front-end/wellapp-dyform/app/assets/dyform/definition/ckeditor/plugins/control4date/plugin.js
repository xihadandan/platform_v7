var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.initProperty = function(field){

	if(field == null ||  (typeof field) == "undefined"){
		field = new WDatePickerClass();
		field.dbDataType = dyFormInputType._date;
		field.valueCreateMethod = dyFormInputValue.userImport;
	}else{
		//$("#name").attr("readonly", "readonly");//在编辑的状态下时则时字段名为只读
	}
	//控件属性初始化公共设置.
	this.ctlPropertyComInitSet(field);
	//私有属性
	$("input[name='valueCreateMethod'][value='" + field.valueCreateMethod + "']").each(function(){
		this.checked = true;
	});
	$("input[name=valueCreateMethod]").change(function(event){
		var value = this.value;
		if(dyFormInputValue.creatOperation === value){
			if($("input[name=showType][value=1]").attr("disabled", "disabled").prop("checked")){
				$("input[name=showType][value=3]").attr("checked", "checked");
			}
		}else {
			$("input[name=showType][value=1]").removeAttr("disabled");
		}
	})

	$("input[name=valueCreateMethod][value=3]:checked").trigger("change");
	//私有属性设置
  $("#dpStyle").val(field.dpStyle);
  field.setTimeStatus = field.setTimeStatus || 'defaultTime';
  $("input[type=radio][name='setTimeStatus'][value='" + field.setTimeStatus + "']").attr("checked",'checked');

  if(field.setTimeStatus === 'startTime') {
      $('.getEndTime').show();
      $('#endTimeArr').val(field.relevanceEndTime);
  }
  $('input[name="setTimeStatus"]').on('change',function () {
    var $this = $(this);
    var _val = $this.val();
    if(_val === 'startTime') {
      $('.getEndTime').show();
    } else {
      $('.getEndTime').hide();
    }
  })

  $("#unitUnique").val(field.unitUnique)
  if(field.unitUnique == "true"){
      $('#checkRule_6').attr("checked",true);
      $('#checkRule_5').attr("checked",false);
  }else if(field.unitUnique == "false"){
      $('#checkRule_5').attr("checked",true);
      $('#checkRule_6').attr("checked",false);
  }
  $("#contentFormat").val(field.contentFormat || '1').trigger('change');
  if (field.contentFormat === '15') {
    $('.time-status-tr').hide();
  }
  $("#contentFormat").on('change', function () {
    var _val = $(this).val();
    if (_val === '15') {
      $('.time-status-tr').hide();
      field.setTimeStatus = 'defaultTime';
      $("input[name='setTimeStatus'][value='" + field.setTimeStatus + "']").attr("checked", 'checked');
      $('#endTimeArr').val('');
      field.relevanceEndTime = '';
    } else {
      $('.time-status-tr').show();
      if(field.setTimeStatus === 'defaultTime') {
        $('.getEndTime').hide();
      }
    }
  })
	$("#minDate").val(field.minDate);
	$("#maxDate").val(field.maxDate);

	if(field.defaultTime) {
        $("input[name='defaultTime']").attr('checked','checked');
	}

};

controlConfig.collectFormAndFillCkeditor = function(){
	var field = new WDatePickerClass();
	field.fieldCheckRules = [];
	field.inputMode =  dyFormInputMode.date;
	field.dbDataType=dyFormInputType._date;
	field.noNullValidateReminder = $("#noNullValidateReminder").val();
	field.uniqueValidateReminder = $("#uniqueValidateReminder").val();
	field.minDate = $("#minDate").val();
	field.maxDate = $("#maxDate").val();
	//控件公共属性收集
	var checkpass=this.collectFormCtlComProperty(field);
	field.valueCreateMethod = $("input[name='valueCreateMethod']:checked").val();
	if(!checkpass){
		return false;
	}
	field.dpStyle = 'LayDate';
	field.contentFormat=$("#contentFormat").val();
  field.layDateFormat=$("#layDateFormat").val();
  field.unitUnique = $("#unitUnique").val()
	field.setTimeStatus = $("input[name='setTimeStatus']:checked").val();
  if (field.setTimeStatus === 'startTime') {
    var relevanceEndTime = $('#endTimeArr').val();
    if (!relevanceEndTime) {
      appModal.error('请选择关联结束时间！')
      return false;
    }
      field.relevanceEndTime = $('#endTimeArr').val();

	} else {
        field.relevanceEndTime && delete field.relevanceEndTime;
	}

	if($("input[name='defaultTime']").attr('checked')) {
		field.defaultTime = true;
	} else {
    	field.defaultTime = null;
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
			id : "click",
			chkDisabled : true,
			name : "click"
		}
		]
}


controlConfig.pluginName = CkPlugin.DATECTL;
addPlugin(controlConfig.pluginName, "日期控件", "日期控件属性设置", controlConfig);

