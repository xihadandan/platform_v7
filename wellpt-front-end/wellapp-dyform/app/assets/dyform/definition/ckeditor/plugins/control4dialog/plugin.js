var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);
$.extend(controlConfig, {
  initProperty: function (field) {
    var _this = this;
    if (field == null || typeof field == 'undefined') {
      field = new WDialogClass();
      field.dbDataType = dyFormInputType._dialog;
      field.valueCreateMethod = '1';
    } else {
    }
    //公共属性
    this.ctlPropertyComInitSet(field);

    //定义关联方式发生改变的事件
    this.definiteRelativeMehtodChangeEvt();

    //定义数据源发生改变的事件
    this.definiteRelationDataTextTwo4DsChangeEvt();

    //定义关联流程事件
    this.definiteRelevantWorkClickEvt();

    //定义目标类型的事件
    this.definiteDestTypeClickEvent();

    this.initSubformList();

    this.definiteDestSubformChangeEvt();

    this.setPageSize(field.pageSize);

    $('#relationDataTextTwo').addClass('input-tier');

    this.setRelationDataTextTwo4Ds(field.relationDataValueTwo);

    //设置关联方式
    this.setRelativeMethod(field.relativeMethod);

    this.setIsRelevantWorkFlow(field.isRelevantWorkFlow);

    this.setWorkFlowTree(field.workflow);

    $('#relationDataTwoSql').val(field.relationDataTwoSql);

    $('#dialogTitle').val(field.dialogTitle);
    $("input[name='dialogSize'][value='" + field.dialogSize + "']").attr('checked', true);

    $('#allowValNotInDs').prop('checked', field.allowValNotInDs);

    if (typeof field.relativeMethod == 'undefined' || field.relativeMethod == dyRelativeMethod.DIALOG) {
      //弹出框
      $('#relationDataIdTwo').val(field.relationDataIdTwo);
      $('#relationDataTextTwo').val(field.relationDataTextTwo);
      $('#relationDataValueTwo').val(field.relationDataValueTwo);
      $('#relationDataSourceTwo').val(field.relationDataSourceTwo);
      $('#relationDsDefaultCondition').val(field.relationDsDefaultCondition);
      $('#queryDefaultCondition').val(field.queryDefaultCondition);
      this.setDestType(field.destType);
      this.setDestSubform(field.destSubform);

      this.setSelectType(field.selectType);
    }

    $('#relationDataTextTwo')
      .wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'relationDataTextTwo',
        valueField: 'relationDataValueTwo',
        params: {
          wtype: 'wBootstrapTable',
          appPageUuid: null,
          uniqueKey: 'uuid',
          includeWidgetRef: 'false'
        },
        remoteSearch: false
      })
      .change(function (event) {
        var widgetUuid = $('#relationDataValueTwo').val();
        treeNodeOnClick2(widgetUuid);
      })
      .trigger('change');

    $('.definitioncontentiteam').remove();
    $('#relationDataDefiantion').val(field.relationDataDefiantion);
    if (field.relationDataDefiantion != '') {
      var str = field.relationDataDefiantion;
      var tempArray = str.split('|');
      for (var j = 0; j < tempArray.length; j++) {
        var tempObj = JSON.parse(tempArray[j]);
        _this.addRelationDefinition(tempObj);
      }
    }

    $('#unitUnique').val(field.unitUnique);
    if (field.unitUnique == 'true') {
      $('#checkRule_6').attr('checked', true);
      $('#checkRule_5').attr('checked', false);
    } else if (field.unitUnique == 'false') {
      $('#checkRule_5').attr('checked', true);
      $('#checkRule_6').attr('checked', false);
    }

    this.definiteCountNumEvt(field);

    $('.addRelationField').click(function () {
      var sqlFieldText = $('#sqlField option:selected').text();
      var sqlFieldValue = $('#sqlField option:selected').val();
      var formFieldText = $('#formField option:selected').text();
      var formFieldValue = $('#formField option:selected').val();
      var searchCheck = $('#searchCheck ').val();
      if ($('#searchCheck:checked').val() == 'yes') {
        searchCheck = true;
      } else {
        searchCheck = false;
      }

      _this.addRelationDefinition({
        sqlTitle: sqlFieldText,
        sqlField: sqlFieldValue,
        formTitle: formFieldText,
        formField: formFieldValue,
        isSearch: searchCheck,
        isHide: false,
        width: ''
      });

      /* var relationDataDefiantion = '<tr class="definitioncontentiteam" name="' + formFieldValue + '">';
            relationDataDefiantion += '<td>'+sqlFieldText+'</td>';
            relationDataDefiantion += '<td>'+sqlFieldValue+'</td>';
            relationDataDefiantion += '<td>'+formFieldText+'</td>';
            relationDataDefiantion += '<td  style="display: none;">'+formFieldValue+'</td>';
            relationDataDefiantion += '<td><input type="checkbox" class="isSearch" /></td>';
            relationDataDefiantion += '<td><input type="checkbox" class="isHide"/></td>';
            relationDataDefiantion += '<td><input type="text" class="width"/></td>';
            relationDataDefiantion += '<td><button class="delField">删除</button></td>';
            relationDataDefiantion += '</tr>';
            $(".definitiontrtable").append(relationDataDefiantion); */
    });

    $('.addRelationUserField').click(function () {
      var sqlFieldText = $('#sqlField option:selected').text();
      var sqlFieldValue = $('#sqlField option:selected').val();
      var formFieldText = $('#userDefinedField').val();
      var formFieldValue = $('#userDefinedField').val();
      _this.addRelationDefinition({
        sqlTitle: sqlFieldText,
        sqlField: sqlFieldValue,
        formTitle: formFieldText,
        formField: formFieldValue,
        isSearch: false,
        isHide: false,
        width: ''
      });
    });

    $('.subDelField').live('click', function () {
      $(this).parent().parent().remove();
    });

    $('.delField').live('click', function () {
      $(this).parent().parent().remove();
    });

    //if(  field.relativeMethod ==  dyRelativeMethod.SEARCH){//搜索框时设置映射列
    this.setFormFieldOptionSet(formDefinition.fields);
    //}

    //初始化字段下拉
    /* var optionStr="";
        var data=formDefinition.fields;
          for(var key in data){
            optionStr += "<option value="+data[key].name+" title=" + data[key].displayName + ">"+data[key].displayName+"</option>";
        }
        $("#formField").html(optionStr); */
  },

  addRelationDefinition: function (def) {
    var relationDataDefiantion = '<tr class="definitioncontentiteam">';
    relationDataDefiantion += '<td>' + def.sqlTitle + '</td>';
    relationDataDefiantion += '<td>' + def.sqlField + '</td>';
    relationDataDefiantion += '<td>' + def.formTitle + '</td>';
    relationDataDefiantion += '<td  style="display: none;">' + def.formField + '</td>';
    relationDataDefiantion += '<td><input type="checkbox" class="isSearch" ' + (def.isSearch ? 'checked' : '') + '/></td>';
    relationDataDefiantion += '<td><input type="checkbox" class="isHide" ' + (def.isHide ? 'checked' : '') + '/></td>';
    var width = def.width;
    width = typeof width == 'undefined' ? '' : width;
    relationDataDefiantion += '<td><input type="text" class="width" style="width:50px;" value="' + width + '"/></td>';
    relationDataDefiantion += '<td><button class="well-btn w-btn-danger well-btn-sm my-del-btn delField">删除</button></td>';
    relationDataDefiantion += '</tr>';
    $('.definitiontrtable').append(relationDataDefiantion);
  },

  definiteCountNumEvt: function (field) {
    var _this = this;
    this.$('#countNum2Input').prop('checked', field.countNum2Input);
    this.$('#countNumHref').prop('checked', field.countNumHref);
    this.$('#countNumHrefText').val(field.countNumHrefText);

    this.$('#countNumHref')
      .on('change', function () {
        if ($(this).prop('checked')) {
          _this.$('#countNumHrefText').show();
        } else {
          _this.$('#countNumHrefText').hide();
        }
      })
      .trigger('change');

    this.$('#countNum2Input')
      .on('change', function () {
        if ($(this).prop('checked')) {
          _this.$('.countNumTr').show();
          _this.$('.relation').hide();
        } else {
          _this.$('.countNumTr').hide();
          _this.$('.relation').show();
        }
      })
      .trigger('change');
  },

  collectFormAndFillCkeditor: function () {
    var field = new WDialogClass();
    field.fieldCheckRules = [];
    //added by linxr
    field.noNullValidateReminder = $('#noNullValidateReminder').val();
    field.uniqueValidateReminder = $('#uniqueValidateReminder').val();
    field.unitUnique = $('#unitUnique').val();
    //end
    //控件公共属性收集
    var checkpass = this.collectFormCtlComProperty(field);
    if (!checkpass) {
      return false;
    }
    field.inputMode = dyFormInputMode.dialog;
    if ($('#dialogTitle').val() != '') {
      field.dialogTitle = $('#dialogTitle').val();
    }
    field.dialogSize = $("input[name='dialogSize']:checked").val();
    field.relativeMethod = this.getRelativeMethod();
    if (field.relativeMethod == dyRelativeMethod.DIALOG) {
      field.relationDataIdTwo = $('#relationDataIdTwo').val();
      field.relationDataTextTwo = $('#relationDataTextTwo').val();
      field.relationDataValueTwo = $('#relationDataValueTwo').val();
      field.relationDataSourceTwo = $('#relationDataSourceTwo').val();
      field.relationDsDefaultCondition = $('#relationDsDefaultCondition').val();
      field.queryDefaultCondition = $('#queryDefaultCondition').val();
      field.isRelevantWorkFlow = this.getIsRelevantWorkFlow();
      if (field.isRelevantWorkFlow == '1') {
        //流程视图
        field.workflow = this.getWorkFlowTree();
      }
    } else {
      field.relationDataTextTwo = this.getRelationDataTextTwo4Ds();
      field.relationDataValueTwo = this.getRelationDataTextTwo4Ds();
    }
    field.pageSize = this.getPageSize();
    field.selectType = this.getSelectType();
    field.destType = this.getDestType();
    field.destSubform = this.getDestSubform();
    field.countNum2Input = $('#countNum2Input').prop('checked');
    field.countNumHref = $('#countNumHref').prop('checked');
    field.countNumHrefText = $('#countNumHrefText').val();
    field.allowValNotInDs = $('#allowValNotInDs').prop('checked');

    field.relationDataTwoSql = $('#relationDataTwoSql').val();

    var definitioncontentContent = new Array();
    $('.definitioncontentiteam').each(function () {
      var objTemp = new Object();
      objTemp.sqlTitle = $(this).find('td').eq(0).html();
      objTemp.sqlField = $(this).find('td').eq(1).html();
      objTemp.formTitle = $(this).find('td').eq(2).html();
      objTemp.formField = $(this).find('td').eq(3).html();
      objTemp.isSearch = $(this).find('td').eq(4).find('input').attr('checked') == 'checked';
      // objTemp.isSearch = true;
      objTemp.search = 'no';
      objTemp.isHide = $(this).find('td').eq(5).find('input').attr('checked') == 'checked';
      objTemp.width = $(this).find('td').eq(6).find('input').val();
      definitioncontentContent.push(JSON.cStringify(objTemp));
    });
    field.relationDataDefiantion = definitioncontentContent.join('|');

    if (field.isRelevantWorkFlow == '1') {
      //流程视图
      //field.workflow = this.getWorkFlowTree();
      //field.relationDataDefiantion =
    }

    //创建控件占位符
    this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);

    formDefinition.addField(field.name, field);
    return true;
  },

  /*
    设置关联方式
    **/
  setRelativeMethod: function (method) {
    if (typeof method == 'undefined') {
      //console.error("method is undefined");
      return;
    }
    this.$('#relativeMethod').val(method);
    this.$('#relativeMethod').trigger('change');
  },
  /*
    获取关联方式
    **/
  getRelativeMethod: function () {
    return this.$('#relativeMethod').val();
  },
  /*定义关联方式更新事件**/
  definiteRelativeMehtodChangeEvt: function () {
    var _this = this;
    this.$('#relativeMethod')
      .die()
      .live('change', function () {
        _this.$('#dialogTitle').hide();
        _this.$('#dialogSize').hide();
        if ($(this).val() == dyRelativeMethod.DIALOG) {
          //弹出框
          _this.$('.dataSourceSetting').hide();
          _this.$('#contraint').hide();
          _this.$('.viewSetting').show();
          // _this.$('#isRelevantWorkFlow').show();
          _this.$('#dialogTitle').show();
          _this.$('#dialogSize').show();
          _this.$('.countViewNumTr').show();
          _this.$('#isRelevantWorkFlow').trigger('change');
          _this.$('.allowValNotInDs').hide();
        } else if ($(this).val() == dyRelativeMethod.SEARCH) {
          //搜索框
          _this.$('#contraint').show();
          _this.$('.dataSourceSetting').show();
          _this.$('.viewSetting').hide();
          _this.$('.countViewNumTr').hide();
          var $relationDataTextTwo4Ds = _this.$('#relationDataTextTwo4Ds');
          $relationDataTextTwo4Ds.trigger('change');
          _this.$('#isRelevantWorkFlow').hide();
          _this.$('.allowValNotInDs').show();
        } else {
          //弹出框--
          _this.$('#isRelevantWorkFlow').hide();
        }
      });
  },
  setIsRelevantWorkFlow: function (isRelevantWorkFlow) {
    if (isRelevantWorkFlow == undefined) {
      isRelevantWorkFlow = '0';
    }
    var _this = this;
    //isRelevantWorkFlow
    var $isRelevantWorkFlow = this.$('#isRelevantWorkFlow');
    if (isRelevantWorkFlow == '1') {
      $isRelevantWorkFlow.attr('checked', true);
      //this.initWorkFlowTree();
    } else {
      $isRelevantWorkFlow.attr('checked', false);
    }
    var checked = $isRelevantWorkFlow.attr('checked');
    this.showOrHideRelevantWorkFlowInfo(checked);
  },

  setWorkFlowTree: function (workFlow) {
    if (workFlow != undefined) {
      this.$('#workflowId').val(workFlow.workflowId);
      this.$('#workflowName').val(workFlow.workflowName);
      if (workFlow.isCompleteForm == 1) {
        this.$('#isCompleteForm').attr('checked', true);
      } else {
        this.$('#isCompleteForm').attr('checked', false);
      }
    }
    var setting = {
      async: {
        otherParam: {
          serviceName: 'dutyAgentService',
          methodName: 'getContentAsTreeAsync',
          data: 'workflow'
        }
      },
      check: {
        enable: true
        // chkStyle: "radio"
      },
      callback: {
        //onClick: treeNodeOnClick,
      }
    };

    $('#workflowName').comboTree({
      labelField: 'workflowName',
      valueField: 'workflowId',
      treeSetting: setting,
      initService: 'dutyAgentService.getKeyValuePair',
      initServiceParam: ['workflow']
    });
  },

  getWorkFlowTree: function () {
    var val = {};
    val['workflowId'] = this.$('#workflowId').val();
    val['workflowName'] = this.$('#workflowName').val();

    var $isCompleteForm = this.$('#isCompleteForm');
    var checked = $isCompleteForm.attr('checked');
    if (checked == true || checked == 'checked') {
      val['isCompleteForm'] = '1';
    } else {
      val['isCompleteForm'] = '0';
    }
    return val;
  },

  getIsRelevantWorkFlow: function () {
    var _this = this;
    //isRelevantWorkFlow
    var $isRelevantWorkFlow = this.$('#isRelevantWorkFlow');

    var checked = $isRelevantWorkFlow.attr('checked');
    if (checked == true || checked == 'checked') {
      return '1';
    } else {
      return '0';
    }
    // this.showOrHideRelevantWorkFlowInfo(checked);

    //$isRelevantWorkFlow.trigger("click");

    //$isRelevantWorkFlow.val(isRelevantWorkFlow);
  },

  showOrHideRelevantWorkFlowInfo: function (checked) {
    var _this = this;
    if (checked == true || checked == 'checked') {
      _this.$('.notWf').hide();
      _this.$('.isWf').show();
    } else {
      _this.$('.notWf').show();
      _this.$('.isWf').hide();
    }
  },
  definiteRelevantWorkClickEvt: function () {
    var _this = this;
    var $isRelevantWorkFlow = this.$('#isRelevantWorkFlow');
    $isRelevantWorkFlow.change(function () {
      var checked = $(this).attr('checked');
      _this.showOrHideRelevantWorkFlowInfo(checked);
    });
  },

  /*设置数据源id**/
  setRelationDataTextTwo4Ds: function (dsId) {
    var $relationDataTextTwo4Ds = this.$('#relationDataTextTwo4Ds');
    var $options = $relationDataTextTwo4Ds.find('option');

    if ($options.size() == 0) {
      //初始化数据源列表

      $.ajax({
        type: 'POST',
        url: ctx + '/common/select2/query',
        dataType: 'json',
        async: false,
        data: {
          serviceName: 'viewComponentService',
          queryMethod: 'loadSelectData',
          pageSize: 1000,
          pageNo: 1
        },
        success: function (data) {
          var results = data.results;
          for (var i = 0; i < results.length; i++) {
            $("<option value='" + results[i].id + "'>" + results[i].text + '</option>').appendTo($relationDataTextTwo4Ds);
          }
        },
        error: function (res) {
          console.log(JSON.cStringify(res));
          alert('数据源获取失败');
        }
      });
    }

    if (typeof dsId == 'undefined') {
      return;
    }
    $relationDataTextTwo4Ds.val(dsId);
    //alert($relationDataTextTwo4Ds.val() + "xxxxxxxxxx" + dsId);
    $('#relationDataTextTwo4Ds').select2({ width: '100%' });
  },
  getRelationDataTextTwo4Ds: function () {
    var $relationDataTextTwo4Ds = this.$('#relationDataTextTwo4Ds');
    return $relationDataTextTwo4Ds.val();
  },

  definiteRelationDataTextTwo4DsChangeEvt: function () {
    var _this = this;
    var $relationDataTextTwo4Ds = this.$('#relationDataTextTwo4Ds');
    $relationDataTextTwo4Ds.die().live('change', function () {
      _this.loadDataSouceColumnDefinition();
    });
  },
  loadDataSouceColumnDefinition: function () {
    var dataSourceId = this.getRelationDataTextTwo4Ds();
    var _this = this;

    JDS.call({
      service: 'viewComponentService.getColumnsById',
      data: [dataSourceId],
      async: false,
      success: function (result) {
        var data = result.data;
        var optionStr = '';
        for (var i = 0; i < data.length; i++) {
          var titleName = data[i].title;
          if (titleName == '') {
            titleName = data[i].columnName;
          }
          var fieldName = data[i].columnIndex;
          if (typeof fieldName == 'undefined' || fieldName.length == 0) {
            fieldName = data[i].columnName;
          }
          optionStr += "<option value='" + fieldName + "'>" + titleName + '</option>';
        }
        _this.$('#sqlField').html(optionStr);
      },
      error: function (res) {
        console.log(JSON.cStringify(res));
        alert('数据源列定义列表获取失败');
      }
    });
  },

  definiteDestTypeClickEvent: function () {
    //alert(3);
    var _this = this;
    this.$('#destType').click(function () {
      //alert($(this).attr("checked") == "checked");

      if ($(this).attr('checked') == 'checked') {
        _this.showDestSubform();
        _this.$('.selectType').show();
      } else {
        _this.$('.destSubform').hide();
        _this.$('.selectType').hide();
        _this.setFormFieldOptionSet(formDefinition.fields);
      }
    });
  },
  showDestSubform: function () {
    this.$('.destSubform').show();
    this.$('#destSubform').trigger('change');
  },

  initSubformList: function () {
    var data = formDefinition.subforms;
    var optionStr = '';
    optionStr += "<option value='' >--请选择--</option>";
    for (var key in data) {
      optionStr += '<option value=' + data[key].outerId + ' title=' + data[key].displayName + '>' + data[key].displayName + '</option>';
    }
    $('#destSubform').html(optionStr);
  },

  /**
   * 从表选项发生变化时的事件
   */
  definiteDestSubformChangeEvt: function () {
    var _this = this;
    this.$('#destSubform').change(function () {
      var formId = $(this).val();
      //alert(formId);
      if (formId.length == 0) {
        _this.$('#formField').html('');
      } else {
        //alert(_this.getDestType() ==   dyControlPos.subForm);
        if (_this.getDestType() == dyControlPos.subForm) {
          //从表
          var data = _this.getSubFormFields(formId);
          _this.setFormFieldOptionSet(data);
        } else {
          //主表
          // _this.$("#formField").html("");
        }
      }
    });
  },

  /**
   * 设置目标从表
   */
  setDestSubform: function (val) {
    if (typeof val == 'undefined') {
      val = '';
    }
    this.$('#destSubform').val(val);
    this.$('#destSubform').trigger('change');
  },

  /**
   * 获取目标从表
   */
  getDestSubform: function (val) {
    return this.$('#destSubform').val();
  },

  setFormFieldOptionSet: function (optionSet) {
    var optionStr = '';
    optionStr += "<option value='' >--请选择--</option>";
    for (var key in optionSet) {
      optionStr +=
        '<option value=' + optionSet[key].name + ' title=' + optionSet[key].displayName + '>' + optionSet[key].displayName + '</option>';
    }

    this.$('#formField').html(optionStr);
  },
  setPageSize: function (pageSize) {
    if (typeof pageSize == 'undefined' || pageSize == null) {
      pageSize = '';
    }
    this.$('#pageSize').val($.trim(pageSize));
  },
  getPageSize: function () {
    return this.$('#pageSize').val();
  },

  /*
   *获取从表的字段
   */
  getSubFormFields: function (formId) {
    var subforms = formDefinition.subforms;
    for (var i in subforms) {
      var subform = subforms[i];
      if (subform.outerId == formId) {
        return subform.fields;
      }
    }
    return [];
  },

  /**
   * 设置多选还是单选
   */
  setSelectType: function (selectType) {
    if (selectType == dySelectType.multiple) {
      this.$('#selectType').attr('checked', false);
      this.$('#selectType').trigger('click');
    } else {
      this.$('#selectType').attr('checked', false);
    }
  },

  /*
   * 设置目标类型
   */
  setDestType: function (destType) {
    if (destType == dyControlPos.subForm) {
      this.$('#destType').attr('checked', true);
      this.showDestSubform();
      this.$('.selectType').show();
    } else {
      this.$('#destType').attr('checked', false);
      this.setFormFieldOptionSet(formDefinition.fields);
      this.$('.selectType').hide();
    }
  },

  /**
   * 获取多选还是单选
   */
  getSelectType: function () {
    if (this.$('#selectType').attr('checked') == 'checked') {
      return dySelectType.multiple;
    } else {
      dySelectType.single;
    }
  },
  /*
   * 获取目标类型
   */
  getDestType: function () {
    if (this.$('#destType').attr('checked') == 'checked') {
      return dyControlPos.subForm;
    } else {
      return dyControlPos.mainForm;
    }
  }
});

function treeNodeOnClick2(widgetUuid) {
  if (typeof widgetUuid === 'undefined' || widgetUuid == null || widgetUuid == '') {
    return false;
  }
  JDS.call({
    service: 'appWidgetDefinitionMgr.getBean',
    data: [widgetUuid],
    async: false,
    success: function (result) {
      $('#relationDataIdTwo').val(result.data.id);
      var definitionJson = $.parseJSON(result.data.definitionJson);
      var data = definitionJson.configuration.columns;
      $('#relationDataSourceTwo').val(definitionJson.configuration.dataStoreId);
      debugger;
      $('#relationDsDefaultCondition').val(definitionJson.configuration.defaultCondition);
      var optionStr = '';
      for (var i = 0; i < data.length; i++) {
        //					if(data[i].hidden === "1" || data[i].hidden === true){
        //						continue;
        //					}
        var titleName = data[i].header || data[i].name;
        optionStr += "<option value='" + data[i].name + "'>" + titleName + '</option>';
      }
      $('#sqlField').html(optionStr);
    }
  });
}

controlConfig.getInputEvents = function () {
  return [
    {
      id: 'focus',
      chkDisabled: true,
      name: 'focus'
    },
    {
      id: 'blur',
      chkDisabled: true,
      name: 'blur'
    },
    {
      id: 'change',
      chkDisabled: true,
      name: 'change'
    }
  ];
};

controlConfig.pluginName = CkPlugin.DIALOGCTL;
addPlugin(controlConfig.pluginName, '弹出框控件', '弹出框控件属性设置', controlConfig);
