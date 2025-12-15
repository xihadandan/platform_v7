(function ($) {
  $.ControlConfigUtil = {
    editedFieldNameIncludeEverFieldName: [], // 被编辑过的字段名,包括曾用名(本次打开表彰定义后，被改名的)

    $: function (selector) {
      return $(selector, $('#' + getContainerId(this.pluginName))); //this.pluginName这个变量在各插件中定义
    },
    /**
     * 初始化属性窗口
     */
    initPropertyDialog: function (editor) {
      this.initTab(); //初始化页签

      // $(".cke_reset_all").removeClass("cke_reset_all");//删除ckeditor内部的局css
      this.editor = editor;
      // loadCommonJsFile();//加载自定义的ckeditor公共js
      var focusedDom = editor.focusedDom; // 当前editor被双击的对象(CKEDITOR.dom.node类)
      this.field = null;
      if (focusedDom != null && typeof focusedDom != 'undefined') {
        var fieldName = $(focusedDom).attr('name');
        console.log(formDefinition.getField(fieldName));
        this.field = formDefinition.getField(fieldName); // 表单中从表的信息
      }

      if (this.getFieldDefinition && (this.field == null || this.field == undefined)) {
        this.field = this.getFieldDefinition(fieldName);
      }

      if (this.field && this.isFieldInPform(this.field.name)) {
        // 来自存储单据，则名称/编码/长度设置为只读, 不可修改
        this.setInputOfNameAsReadonly();
        this.setInputOfDisplayNameAsReadonly();
        this.setInputOfLengthAsReadonly();
      }
      this.initProperty(this.field);

      //日期、大字段、附件不需要指定字段长度
      if (!this.isFieldNeedLength(this.fieldOfInit)) {
        this.hideLengthInput();
        this.hideDefaultValueInput();
      }

      if ($.inArray(this.pluginName, ['control4text', 'control4textarea', 'control4serialnumber']) < 0) {
        this.hidePlaceholderInput();
      }

      if (this.pluginName === 'control4placeholder') {
        this.$('.ui-tabs-nav li').hide();
        // this.$('#displayName').parents('tr').first().hide();
        this.$('#applyTo').parents('tr').first().hide();
        this.hideDefaultValueInput();
        this.hidePlaceholderInput();
        this.hideLengthInput();
        this.$('#valueCreateMethod').parents('tr').first().hide();
        this.$('.tr_validateOnHidden').hide();
        // this.$('input[name="showType"]').parents('tr').first().hide();
      }
    },

    isFieldNeedLength: function (field) {
      if (
        field.dbDataType == dyFormDataType.clob ||
        field.dbDataType == dyFormDataType.date ||
        this.isFileControl(field) ||
        this.pluginName === 'control4placeholder'
      ) {
        return false;
      } else {
        return true;
      }
    },

    isNumberControl: function (field) {
      if (field instanceof WNumberInputClass || field instanceof WNumberInputClass) {
        return true;
      } else if (field.inputMode && field.inputMode == dyFormInputMode.number) {
        return true;
      } else {
        return false;
      }
    },

    isFileControl: function (field) {
      if (field instanceof WFileUploadClass || field instanceof WFileUpload4IconClass) {
        return true;
      } else if (
        field.inputMode &&
        (field.inputMode == dyFormInputMode.accessory1 ||
          field.inputMode == dyFormInputMode.accessory3 ||
          field.inputMode == dyFormInputMode.accessoryImg ||
          field.inputMode == dyFormInputMode.textBody)
      ) {
        return true;
      } else {
        return false;
      }
    },

    hideLengthInput: function () {
      this.$('#length').parents('tr').first().hide();
    },

    hideDefaultValueInput: function () {
      this.$('#defaultValue').parents('tr').first().hide();
    },

    hidePlaceholderInput: function () {
      this.$('#placeholder').parents('tr').first().hide();
    },

    initTab: function () {
      var $tabs = this.$('#tabs');
      $tabs.tabs();
    },
    /**
     * 初始化事件树
     */
    initEventTree: function () {
      var _this = this;
      var setting = {
        check: {
          enable: true
          // chkDisabledInherit : true
        },
        data: {
          simpleData: {
            enable: true
          }
        },
        callback: {
          onClick: zTreeOnDblClick
        }
      };

      var topNodeIdOfControlEvents = 'control_events'; //控件事件
      var topNodeIdOfInputEvents = 'input_events'; //输入域事件
      var topNodeIdOfCustomEvents = 'custom_events'; //自定义事件

      var zRootNodes = [
        {
          id: topNodeIdOfControlEvents,
          pId: 0,
          name: '控件事件',
          open: true,
          // chkDisabled : true,
          isParent: true,
          nocheck: true
        },
        {
          id: topNodeIdOfInputEvents,
          pId: 0,
          name: 'html元素事件',
          open: true,
          // chkDisabled : true,
          isParent: true,
          nocheck: true
        },
        {
          id: topNodeIdOfCustomEvents,
          pId: 0,
          name: '自定义事件',
          open: true,
          // chkDisabled : true,
          isParent: true,
          nocheck: true
        }
      ];

      var eventCallback = function (param) {
        var script = param.value;
        var nodes = treeObj.getSelectedNodes();
        for (var i = 0; i < nodes.length; i++) {
          var treeNode = nodes[i];
          if ($.trim(script).length > 0) {
            if (treeNode.pId == topNodeIdOfControlEvents) {
              _this.events.controlEvents[treeNode.id] = script;
            } else if (treeNode.pId == topNodeIdOfInputEvents) {
              _this.events.inputEvents[treeNode.id] = script;
            }

            _this.checkEventNode(treeNode.id, true);
          } else {
            if (treeNode.pId == topNodeIdOfControlEvents) {
              delete _this.events.controlEvents[treeNode.id];
            } else if (treeNode.pId == topNodeIdOfInputEvents) {
              delete _this.events.inputEvents[treeNode.id];
            }

            _this.checkEventNode(treeNode.id, false);
          }
        }
      };
      this.aceCoderPlugin = {};

      var createAceCodeEditor = function (node) {
        $('#scriptContentContainer').append($('<div>', { id: 'propEventCodeContainer_' + node.id }));

        _this.aceCoderPlugin[node.id] = $.fn.aceBinder({
          container: '#propEventCodeContainer_' + node.id,
          id: node.id,
          iframeId: 'dyformPropScriptContentIframe_' + node.id,
          value: _this.events.controlEvents[node.id],
          varSnippets: 'dyform.controlEvent',
          codeHis: {
            enable: true,
            relaBusizUuid: $('#tableId').val(), //表单ID
            codeType:
              'dyform.' +
              $('#name').val() + //字段编码
              node.id
          },
          valueChange: function (v) {
            eventCallback.call(_this, { value: v, $ace: this });
          }
        });
        //if (i != 0)
        _this.aceCoderPlugin[node.id].getContainer().hide();
      };
      var zIntputEventNodes = this.getInputEvents();
      for (var i = 0; i < zIntputEventNodes.length; i++) {
        zIntputEventNodes[i].pId = topNodeIdOfInputEvents;
        createAceCodeEditor(zIntputEventNodes[i]);
      }

      var zControlEventNodes = this.getControlEvents();
      for (var i = 0; i < zControlEventNodes.length; i++) {
        zControlEventNodes[i].pId = topNodeIdOfControlEvents;
        createAceCodeEditor(zControlEventNodes[i]);
      }

      function zTreeOnDblClick(event, treeId, treeNode) {
        $("div[id^='propEventCodeContainer_']").hide();
        if (!treeNode || treeNode.isParent) {
          return;
        }
        var id = treeNode.id;
        _this.aceCoderPlugin[id].getContainer().show();
        if (treeNode.pId == topNodeIdOfControlEvents) {
          if (_this.events.controlEvents[id]) {
            _this.aceCoderPlugin[id].setValue(_this.events.controlEvents[id]);
          } else {
            _this.aceCoderPlugin[id].setValue('');
          }
        } else if (treeNode.pId == topNodeIdOfInputEvents) {
          if (_this.events.inputEvents[id]) {
            _this.aceCoderPlugin[id].setValue(_this.events.inputEvents[id]);
          } else {
            _this.aceCoderPlugin[id].setValue('');
          }
        }
      }

      var treeObj = (this.eventTreeObj = $.fn.zTree.init(
        $('#eventTree'),
        setting,
        [].concat(zRootNodes).concat(zControlEventNodes).concat(zIntputEventNodes)
      ));

      // window.setTimeout(function () {
      //     $("textarea", window['scriptContentIframe'].document.body).on('blur', eventCallback);
      //     $("textarea", window['scriptContentIframe'].document.body).on('paste', eventCallback);
      // }, 1500);
    },

    /**
     * 初始化事件属性值
     * @param {Object} field
     */
    initEventProperty: function (field) {
      var _this = this;
      this.events = { controlEvents: {}, inputEvents: {} };
      if (field.events && field.events.controlEvents) {
        this.events.controlEvents = field.events.controlEvents;
      }

      if (field.events && field.events.inputEvents) {
        this.events.inputEvents = field.events.inputEvents;
      }
      this.initEventTree();

      function initEventTreeNode(type, events) {
        for (var i in events) {
          if (events.hasOwnProperty(i)) {
            if (events[i].length > 0) {
              _this.checkEventNode(i, true);
            } else {
              _this.checkEventNode(i, false);
            }
          }
        }
      }

      var controlEvents = this.events.controlEvents;
      initEventTreeNode('controlEvent', controlEvents);

      var inputEvents = this.events.inputEvents;
      initEventTreeNode('inputEvent', inputEvents);
    },

    getControlEvents: function () {
      var controlEvents = [
        {
          id: 'beforeInit',
          chkDisabled: true,
          name: 'beforeInit'
        },
        {
          id: 'afterInit',
          chkDisabled: true,
          name: 'afterInit'
        },
        {
          id: 'afterSetValue',
          chkDisabled: true,
          name: 'afterSetValue'
        }
      ];
      if (this.pluginName == 'control4dialog') {
        controlEvents.push({
          id: 'beforeDialogShow',
          chkDisabled: true,
          name: 'beforeDialogShow'
        });
      }
      return controlEvents;
    },

    getInputEvents: function () {
      return [
        {
          id: 'click',
          chkDisabled: true,
          name: 'click'
        },
        {
          id: 'dblclick',
          chkDisabled: true,
          name: 'dblclick'
        },
        {
          id: 'change',
          chkDisabled: true,
          name: 'change'
        },
        {
          id: 'focus',
          chkDisabled: true,
          name: 'focus'
        },
        {
          id: 'blur',
          chkDisabled: true,
          name: 'blur'
        }
      ];
    },

    initProperty: function (field) {},

    ctlPropertyComInitSet: function (field) {
      var _this = this;
      this.fieldOfInit = field;
      this.initEventProperty(field);

      if (StringUtils.isNotBlank(field.name)) {
        $.ControlConfigUtil.editedFieldNameIncludeEverFieldName.push(field.name);
      } else {
        field.fieldCheckRules = [];
      }

      this.$('#applyTo').val(field.applyTo);
      this.initApplyToTree(this.$('#applyTo'), field);
      // console.log(JSON.cStringify(field.applyTo));
      // this.$("#applyTo").comboTree("initValue", field.applyTo);

      this.$('#name').val(field.name);
      if (formDefinition.hasDatabaseField(field.name)) {
        //已保存数据库的字段，不允许修改字段编码
        this.$('#name').prop('readonly', true); //字段编码不允许修改
      } else {
        var candidateFields = this.getCandidateFields(field.name);
        if (candidateFields.length > 0) {
          this.$('#name').wSelect2({
            data: $.map(candidateFields, function (field) {
              return { id: field.name, text: field.label };
            }),
            remoteSearch: false,
            width: '100%'
          });
        }
      }
      this.$('#name').attr('oldName', field.oldName);
      this.$('#customizeRegularText').val(field.customizedRegularText);
      this.$('#displayName').val(field.displayName);
      this.$('#dbDataType').val(field.dbDataType);
      this.$("input[name='valueCreateMethod'][value='" + field.valueCreateMethod + "']").attr('checked', true);
      selColor(field.fontColor);
      this.$('#fontSize').val(field.fontSize);
      this.$('#length').val(field.length);
      this.$('#placeholder').val(field.placeholder);
      this.$('#ctlWidth').val(field.ctlWidth);
      this.$('#ctlHight').val(field.ctlHight);
      this.$('#textAlign').val(field.textAlign);
      this.$('#defaultValue').val(field.defaultValue);
      this.$('#onlyreadUrl').val(field.onlyreadUrl);
      this.$('#validateEventManage').val(field.validateEventManage);
      this.$('#noNullValidateReminder').val(field.noNullValidateReminder);
      this.$('#uniqueValidateReminder').val(field.uniqueValidateReminder);
      this.$('#regularValidateReminder').val(field.regularValidateReminder);
      this.$('#eventValidateReminder').val(field.eventValidateReminder);

      if (field.validateOnHidden == true || field.validateOnHidden == 'true') {
        this.$('#validateOnHidden').prop('checked', true);
      } else {
        this.$('#validateOnHidden').prop('checked', false);
      }

      // add by zyguo 初始化备注属性 2017-03-17
      if ($.inArray(field.inputMode, ['4', '6', '33', '130']) < 0) {
        if ($.inArray(field.inputMode, ['127', '128']) > -1) {
          // 颜色、开关
          this.$('input[name=readStyle][value=2]').hide().next('label').hide();
          this.$('input[name=readStyle][value=2]').closest('tr').hide();
        } else if ($.inArray(field.inputMode, ['126', '40']) > -1) {
          // 标签组、嵌入页面
          this.$('input[name=readStyle][value=3]').hide().next('label').hide();
          this.$('input[name=readStyle][value=3]').closest('tr').hide();
        }
        var dReadStyle = $.inArray(field.inputMode, ['127', '128']) > -1 ? '3' : '2';
        this.$('input[name=readStyle]')
          .filter('[value=' + (field.readStyle || dReadStyle) + ']')
          .prop('checked', true)
          .trigger('change');
      } else {
        // 附件、真实值占位符控件
        this.$('input[name=readStyle]').closest('tr').hide();
      }
      this.$('#note').val(field.note);
      this.$('#realDisplay_display').empty().append('<option value="">--请选择--</option>');
      this.$('#realDisplay_real').empty().append('<option value="">--请选择--</option>');
      this.$('#realDisplay_span').empty().append('<option value="">--请选择--</option>');
      this.$('.mappingFields').empty().append('<option value="">--请选择--</option>');

      if (formDefinition.isInputModeAsValueMap(field.inputMode)) {
        // 字段有真实值显示值
        for (var i in formDefinition.fields) {
          var fieldt = formDefinition.getField(i);

          if (
            !formDefinition.isCustomField(fieldt.name) ||
            fieldt.name == field.name ||
            !(formDefinition.isInputModeAsText(fieldt.inputMode) || formDefinition.isInputModeAsTextArea(fieldt.inputMode))
          ) {
            continue;
          }
          this.$('#realDisplay_display').append('<option value="' + fieldt.name + '">' + fieldt.displayName + '</option>');
          this.$('#realDisplay_real').append('<option value="' + fieldt.name + '">' + fieldt.displayName + '</option>');
          this.$('.mappingFields').append('<option value="' + fieldt.name + '">' + fieldt.displayName + '</option>');
        }

        for (var i in formDefinition.placeholderCtr) {
          var placeholderCtr = formDefinition.placeholderCtr[i];
          this.$('#realDisplay_span').append('<option value="' + placeholderCtr.name + '">' + placeholderCtr.displayName + '</option>');
        }

        var realDisplay = field.realDisplay;

        if (typeof realDisplay != 'undefined') {
          var realFieldName = realDisplay.real;
          var displayFieldName = realDisplay.display;
          this.$('#realDisplay_display').val(displayFieldName);
          this.$('#realDisplay_real').val(realFieldName);
        }

        var realDisplayPlaceholder = field.realDisplay.placeholder;

        if (realDisplayPlaceholder) {
          this.$('#realDisplay_span').val(realDisplay.placeholder);
        }
      } else {
        // this.$("#realDisplay_display").hide();
        // this.$("#realDisplay_real").hide();
      }

      if (formDefinition.isDateControl(field.inputMode)) {
        for (var i in formDefinition.fields) {
          var fieldt = formDefinition.getField(i);

          if (!formDefinition.isDateControl(fieldt.inputMode) || fieldt.name == field.name) {
            continue;
          }
          this.$('#endTimeArr').append('<option value="' + fieldt.name + '">' + fieldt.displayName + '</option>');
        }
      }

      // 设置约束条件
      if (typeof field.fieldCheckRules != 'undefined' && field.fieldCheckRules != null) {
        var fieldCheckRules = field.fieldCheckRules;
        for (var i = 0; i < fieldCheckRules.length; i++) {
          var fieldCheckRule = fieldCheckRules[i];
          if (fieldCheckRule.value == '5' && fieldCheckRule.label == '全局唯一') {
            this.$("input[name='fieldCheckRules'][value='" + fieldCheckRule.value + "'][id='checkRule_5']").prop('checked', true);
          } else if (fieldCheckRule.value == '5' && fieldCheckRule.label == '单位内唯一') {
            this.$("input[name='fieldCheckRules'][value='" + fieldCheckRule.value + "'][id='checkRule_6']").prop('checked', true);
          } else {
            this.$("input[name='fieldCheckRules'][value='" + fieldCheckRule.value + "'][id='checkRule_1']").prop('checked', true);
          }
        }
      }

      // 禁用选项被删除，改为只读
      if (field.showType === '4') {
        field.showType = '3';
      }
      this.$("input[name='showType'][value='" + field.showType + "']").each(function () {
        this.checked = true;
      });
      this.$('input[name=fieldCheckRules][value=1]')
        .change(function (event) {
          var defaultValue = _this.$('#defaultValue').val();
          if (this.checked && StringUtils.isBlank(defaultValue)) {
            _this.$('input[name=showType][value=1]').attr('checked', 'checked');
            _this
              .$('input[name=showType][value=2],input[name=showType][value=3],input[name=showType][value=4],input[name=showType][value=5]')
              .attr('disabled', 'disabled');
          } else {
            _this.$('input[name=showType]').removeAttr('disabled');
          }
        })
        .trigger('change');
      // 如果field已存在，则name不可编辑
      if (field.name != '' && field.name != undefined) {
        // _this.$("#name").attr("readonly","readonly");
      } else {
        _this.$('#name').removeAttr('readonly');
      }

      this.setDbDataType(field.dbDataType);
    },

    getCandidateFields: function (currentFieldName) {
      var _self = this;
      var candidateFields = [];
      if (
        formDefinition == null ||
        $.trim(formDefinition.uuid) == null ||
        formDefinition.repository == null ||
        formDefinition.repository.mode == null ||
        formDefinition.repository.mode == '1'
      ) {
        return candidateFields;
      }
      var formFields = [];
      JDS.call({
        service: 'formDefinitionService.getCandidateFormFields',
        data: [formDefinition.uuid],
        async: false,
        version: '',
        success: function (result) {
          formFields = result.data;
        }
      });
      $.each(formFields, function (i, field) {
        if (_self.isFieldNameExistEqualsIgnoreCase(field.name) && field.name != currentFieldName) {
          return;
        }
        candidateFields.push(field);
      });
      return candidateFields;
    },

    setDbDataType: function (dbDataType) {
      var _this = this;

      if (this.$('#dbDataType').size() == 0) {
        return;
      }
      if (this.$('#dbDataType').find('option').length > 0) {
        this.$('#dbDataType').val(dbDataType);
        return;
      }
      for (var type in dyFormInputTypeObj) {
        var obj = dyFormInputTypeObj[type];
        this.$('#dbDataType').append("<option value='" + obj.code + "'>" + obj.name + '</option>');
      }
      this.$('#dbDataType').change(function () {
        if ($(this).val() == dyFormInputType._clob) {
          // 大字段,不需要设置长度
          _this.$('#length').parents('tr').first().hide();
        } else {
          _this.$('#length').parents('tr').first().show();
        }
      });
      if (typeof dbDataType == 'undefined') {
        return;
      }
      this.$('#dbDataType').val(dbDataType);
      this.$('#dbDataType').trigger('change');
    },
    getDbDataType: function () {
      if (this.$('#dbDataType').size() == 0) {
        return null;
      }
      return this.$('#dbDataType').val();
    },

    /**
     * 控件属性编辑对话框onshow
     *
     * @param _editor
     * @param _this
     * @param ctlname
     */
    ctlEditDialogonShow: function (_editor, _this, ctlname, pluginName) {
      if (_editor == undefined || !_editor.getSelection) {
        return;
      }

      // 判断光标焦点位置是不是在从表中，若在其他从表中则不允许插入新的从表
      var selection = _editor.getSelection();
      var selected_ranges = selection.getRanges(); // getting ranges
      var node = selected_ranges[0].startContainer; // selecting the
      // starting node

      var parents = node.getParents(true);
      var parentsLength = parents.length;
      if (parentsLength > 3) {
        var fieldClazzElement = null;
        for (var i = parentsLength - 1; i > -1; i--) {
          var parent = parents[i];
          if (parent.type != 3 && parent.getName() == 'tr') {
            fieldClazzElement = parent; // 获取包括焦点的最内层的tr,该tr的class将被设置成"field"
          } else if (parent.type != 3) {
            valueClassElement = parent;
          }
        }
        editor.fieldClazzElement = fieldClazzElement;
      } else if (pluginName !== CkPlugin.SUBFORM) {
        _this.hide();
        alert('在插件' + ctlname + '控件之前，请先添加布局表格!!');
      }
    },

    /**
     * 控件属性编辑对话框ok事件
     *
     * @param ctl
     * @param containerID
     * @returns {Boolean}
     */
    ctlEditDialogOnOk: function (ctl, containerID) {
      var checkpass = ctl.collectFormAndFillCkeditor();
      if (!checkpass) {
        return false;
      }
      ctl.exitDialog();
      window.setTimeout(function () {
        $('#' + containerID).empty(); // 重新初始化属性窗口
      }, 100);

      // 如果字段有重命名,则需要删除原来的字段字义
      var $oldField = $(ctl.editor && ctl.editor.getData()).find("img[name='" + ctl.fieldOfInit.name + "']");
      if (ctl.isRename() && $oldField.length <= 0) {
        formDefinition.deleteFieldDirectly(ctl.fieldOfInit.name);
      }

      return true;
    },

    /**
     * 控件属性编辑对话框退出事件
     *
     * @param ctl
     * @param containerID
     */
    ctlEditDialogOnCancel: function (ctl, containerID) {
      if (typeof ctl.exitDialog != 'undefined') {
        ctl.exitDialog();
        window.setTimeout(function () {
          $('#' + containerID).empty(); // 重新初始化属性窗口
        }, 100);
      }
    },

    /**
     * 初始化applytotree
     */
    initApplyToTree: function ($elment, field) {
      var id_applyToName = 'id_applyToName';
      var id_applyTo = $elment.attr('id');
      $elment.after("<input type='text' id='" + id_applyToName + "' >");
      var $element_applyToName = this.$('#' + id_applyToName);
      if (field.applyToName) {
        $element_applyToName.val(field.applyToName);
      }

      $elment.hide();
      var setting2 = {
        mutiSelect: true,
        view: {
          showLine: true
        },
        check: {
          enable: true,
          chkStyle: 'checkbox'
        },
        async: {
          otherParam: {
            serviceName: 'dyFormFacade',
            methodName: 'getFormFieldApplyToRootDicts'
          }
        }
      };

      setTimeout(function () {
        $element_applyToName.comboTree({
          labelField: id_applyToName,
          valueField: id_applyTo,
          width: 200,
          height: 250,
          mutiSelect: true,
          treeSetting: setting2
          // initServiceParam:["DY_FORM_ID_MAPPING"]
        });
        $element_applyToName.addClass('input-tier');
      }, 0);
    },

    setInputOfNameAsReadonly: function () {
      this.get$InputOfName().attr('readonly', 'readonly');
    },

    setInputOfLengthAsReadonly: function () {
      this.get$InputOfName().attr('readonly', 'readonly');
    },

    setInputOfDisplayNameAsReadonly: function () {
      this.get$InputOfLength().attr('readonly', 'readonly');
    },

    get$InputOfName: function () {
      return this.$('#name');
    },

    get$InputOfLength: function () {
      return this.$('#length');
    },

    get$InputOfPlaceholder: function () {
      return this.$('#placeholder');
    },

    get$InputOfDisplayName: function () {
      return this.$('#displayName');
    },

    getFieldNameFromInput: function () {
      return this.get$InputOfName().val();
    },
    getFieldOldNameFromInput: function () {
      return this.get$InputOfName().attr('oldName');
    },

    getFIeldDisplayNameFromInput: function () {
      return this.get$InputOfDisplayName().val();
    },

    /**
     * 判断字段是否为存储单据的字段
     *
     * @param fieldName
     */
    isFieldInPform: function (fieldName) {
      if (this.editor.pform && this.editor.pform.formDefinition) {
        var fields = this.editor.pform.formDefinition.fields;
        for (var i in fields) {
          if (fields.hasOwnProperty(i) && i.toLowerCase() == fieldName.toLowerCase()) {
            return true;
          }
        }
      }
      return false;
    },

    checkEventNode: function (eventNodeId, flag) {
      var treeNode = this.eventTreeObj.getNodeByParam('id', eventNodeId, null);
      if (treeNode == null) {
        return false;
      }
      this.eventTreeObj.setChkDisabled(treeNode, false);
      this.eventTreeObj.checkNode(treeNode, flag, false, false);
      this.eventTreeObj.setChkDisabled(treeNode, true);
    },

    /**
     * 收集控件一些公共属性
     *
     * @param field
     */
    collectFormCtlComProperty: function (field) {
      var _this = this;
      this.fieldAfterCollect = field;

      if (this.events) {
        field.events = this.events;
      }

      var checkpass = true;
      field.name = this.getFieldNameFromInput();
      field.oldName = this.getFieldOldNameFromInput();
      field.displayName = this.getFIeldDisplayNameFromInput();

      /**
       * 用户在点击属性弹出框的时候，关于字段名有几个种情况需要判定 1、new 2、unpersist---->字段名不变
       * 3、unpersist---->字段名重命名 4、persist------>字段名不变
       * 5、persist------>字段名重命名
       */
      if (this.isPropertyDialogFromToolBar()) {
        // 弹出框来自工具栏
        if (this.isFieldInPform(field.name)) {
          // 字段存在于存储单据中
          appModal.error('该字段在存储单据中已存在，请从左边的树形中选择对应的字段!!!');
          return false;
        } else if (this.isFieldNameExistEqualsIgnoreCase(field.name)) {
          // 判断字段编码是否已存在
          appModal.error('该字段编码已存在!!');
          return false;
        }
      } else if (this.isRename()) {
        // 弹出框来自双击设计区
        if (this.isFieldNameExistEqualsIgnoreCase(field.name)) {
          appModal.error('该字段编码已存在!!');
          return false;
        } else if (this.isFieldNameExistEqualsIgnoreCaseInEverFieldName(field.name)) {
          appModal.error('您可以先点"取消",然后将本次之前对表单的所有操作先保存到系统中,再来修改该字段名,因为该字段名正在被其他字段使用。');
          return false;
        }
      }

      if (field.name == '' || field.name == undefined) {
        appModal.error(dymsg.fieldNameNotEmpty);
        return false;
      }

      var re = /^([a-zA-Z_][a-zA-Z0-9_]*)$/;
      if (!re.test(field.name)) {
        appModal.error(dymsg.fieldNameError);
        return false;
      }

      if (field.displayName == '' || field.displayName == undefined) {
        appModal.error(dymsg.displayNameNotEmpty);
        return false;
      }
      if (preservedFields.is($.trim(field.name))) {
        // 字段名是否与预留字段重复
        appModal.error(dymsg.preservedField);
        return false;
      }

      // field.valueCreateMethod = "1";
      field.applyTo = _this.$('#applyTo').val();
      field.applyToName = _this.$('#id_applyToName').val();
      field.fontColor = _this.$('#fontColor').val();
      field.fontSize = _this.$('#fontSize').val();
      field.ctlWidth = _this.$('#ctlWidth').val();
      field.ctlHight = _this.$('#ctlHight').val();
      field.textAlign = _this.$('#textAlign').val();
      field.showType = _this.$('input[name="showType"]:checked').val();
      field.validateOnHidden = _this.$('input[name="validateOnHidden"]').prop('checked');
      field.defaultValue = _this.$('#defaultValue').val();
      field.valueCreateMethod = $('input[name="valueCreateMethod"]:checked').val();

      field.onlyreadUrl = _this.$('#onlyreadUrl').val();
      // add by zyguo 2017-03-17 所有基本控件添加备注信息
      if (_this.$('#note')) {
        // 有这个对象就加入这个属性，没有的话就不用加了
        field.note = _this.$('#note').val();
      }
      if ($.inArray(field.inputMode, ['4', '6', '33']) < 0) {
        field.readStyle = _this.$('input[name=readStyle]:checked').val();
      }

      if (_this.$('#length').size() > 0) {
        field.length = _this.$('#length').val();
      }

      if (_this.$('#placeholder').size() > 0) {
        field.placeholder = _this.$('#placeholder').val();
      }

      if (_this.$('#dbDataType').size() > 0) {
        field.dbDataType = _this.$('#dbDataType').val();
      }

      field.fieldCheckRules.length = 0;
      _this.$("input[name='fieldCheckRules']").each(function () {
        if (this.checked) {
          field.fieldCheckRules.push({
            value: $(this).val(),
            label: $(this).next().text()
          });
        }
      });

      if (formDefinition.isInputModeAsValueMap(field.inputMode)) {
        // 字段有真实值显示值
        field.realDisplay = {};

        field.realDisplay.real = _this.$('#realDisplay_real').val();
        field.realDisplay.display = _this.$('#realDisplay_display').val();
        field.realDisplay.placeholder = _this.$('#realDisplay_span').val();
        field.realDisplayPlaceholder = _this.$('#realDisplay_span').val();
        if (field.realDisplay.real && field.realDisplay.real == field.realDisplay.display) {
          alert('显示值字段与真实值字段一样');
        }
      }
      var dbDataType = this.getDbDataType();
      if (dbDataType != null) {
        field.dbDataType = dbDataType;
      }

      if (this.isFieldNeedLength(field)) {
        if (StringUtils.isBlank(field.length)) {
          alert('字段长度不得为空');
          return false;
        }
      }

      //收集内容格式
      //field.contentFormat=$("#contentFormat").val();
      //field.customValidates

      return checkpass;
    },

    isPropertyDialogFromToolBar: function () {
      var fieldName = this.fieldOfInit.name;
      if (StringUtils.isBlank(fieldName)) {
        return true;
      } else {
        return false;
      }
    },

    isPropertyDialogFromPformTree: function () {
      if (this.editor.pform && this.editor.pform.isCommandFromPform) {
        return true;
      } else {
        return false;
      }
    },

    /**
     * 如果有存在两个字段的字段名在忽略大写的情况下名字一样则返回true,或者返回false
     */
    isFieldNameExistEqualsIgnoreCase: function (fieldName) {
      var fields = formDefinition.fields;
      for (var fieldName1 in fields) {
        if (fieldName.equalsIgnoreCase(fieldName1)) {
          return true;
        }
      }
      return false;
    },

    isFieldNameExistEqualsIgnoreCaseInEverFieldName: function (fieldName) {
      for (var i = 0; i < $.ControlConfigUtil.editedFieldNameIncludeEverFieldName.length; i++) {
        var fieldName1 = $.ControlConfigUtil.editedFieldNameIncludeEverFieldName[i];
        if (fieldName.equalsIgnoreCase(fieldName1)) {
          return true;
        }
      }
      return false;
    },

    /**
     * 判断字段名是否做了修改,是返回true, 否返回false
     */
    isRename: function () {
      if (!this.fieldOfInit.name.equalsIgnoreCase($.trim(this.fieldAfterCollect.name))) {
        return true;
      } else {
        return false;
      }
    },

    /**
     * 初始化数据字典字树
     */
    initDictCode: function () {
      var ctrlFieldSetting = {
        async: {
          otherParam: {
            serviceName: 'dataDictionaryService',
            methodName: 'getAsTreeAsyncForControl'
          }
        },
        check: {
          enable: false
        },
        callback: {
          onClick: treeNodeOnClickForctrlFieldSetting
          //  bug#47738: 下拉框的数据来源选择数据字典时，无法选择没有选项的数据字典
          // beforeClick: zTreeBeforeClick
        }
      };
      setTimeout(function () {
        $('#dictName').comboTree({
          labelField: 'dictName',
          valueField: 'dictCode',
          treeSetting: ctrlFieldSetting,
          width: 220,
          height: 220
        });
      }, 0);

      function treeNodeOnClickForctrlFieldSetting(event, treeId, treeNode) {
        $('#dictName').val(getAbsolutePath(treeNode));
        $('#display_dictName').find('.well-select-selected-value').text(getAbsolutePath(treeNode)).show();
        $('#display_dictName').find('.well-select-placeholder').hide();
        $('#dictCode').val(treeNode.data + ':' + getAbsolutePath(treeNode));
        $('#dictUuid').val(treeNode.id);
      }

      function zTreeBeforeClick(treeId, treeNode, clickFlag) {
        return treeNode.isParent; // 当是父节点 返回false 不让选取
      }

      // 获取树结点的绝对路径
      function getAbsolutePath(treeNode) {
        var path = treeNode.name;
        var parentNode = treeNode.getParentNode();
        while (parentNode != null) {
          path = parentNode.name + '/' + path;
          parentNode = parentNode.getParentNode();
        }
        return path;
      }

      $('#dictName').addClass('input-tier');
    },

    /**
     * 初始化数据源
     */
    initDataSource: function () {
      var ctrlFieldSetting = {
        async: {
          otherParam: {
            serviceName: 'dyFormDefinitionService',
            methodName: 'getAsTreeAsyncForControl'
          }
        },
        check: {
          enable: false
        },
        callback: {
          onClick: treeNodeOnClickForctrlFieldSetting,
          beforeClick: zTreeBeforeClick
        }
      };

      setTimeout(function () {
        $('#dictName').comboTree({
          labelField: 'dictName',
          valueField: 'dictCode',
          treeSetting: ctrlFieldSetting,
          width: 220,
          height: 220
        });
      }, 0);

      function treeNodeOnClickForctrlFieldSetting(event, treeId, treeNode) {
        $('#dictName').val(getAbsolutePath(treeNode));
        $('#dictCode').val(treeNode.data + ':' + getAbsolutePath(treeNode));
      }

      function zTreeBeforeClick(treeId, treeNode, clickFlag) {
        return treeNode.isParent; // 当是父节点 返回false 不让选取
      }

      // 获取树结点的绝对路径
      function getAbsolutePath(treeNode) {
        var path = treeNode.name;
        var parentNode = treeNode.getParentNode();
        while (parentNode != null) {
          path = parentNode.name + '/' + path;
          parentNode = parentNode.getParentNode();
        }
        return path;
      }

      $('#dictName').addClass('input-tier');
    },

    initDataSource1: function (dataSourceId) {
      var _this = this;
      // 取回所有的数据源信息
      var setting = {
        async: {
          otherParam: {
            serviceName: 'dataSourceDefinitionService',
            methodName: 'getAllByTreeNode'
          }
        },
        check: {
          enable: false
        },
        callback: {
          onClick: treeNodeOnClick
        }
      };

      setTimeout(function () {
        _this.$('#dataSourceText').comboTree({
          labelField: 'dataSourceText',
          valueField: 'dataSourceId',
          treeSetting: setting,
          width: 220,
          height: 220
        });
      }, 0);

      // 选择数据源后点击的回调函数
      function treeNodeOnClick(event, treeId, treeNode) {
        // 将后台传回的json串转化为对象
        _this.$('#dataSourceText').val(treeNode.name);
        _this.$('#dataSourceId').val(treeNode.id);

        _this.$('#dataSourceText').comboTree('hide');

        var dataSourceId = treeNode.id;
        _this.loadDataSource1Fields(dataSourceId);
      }

      if (dataSourceId && dataSourceId.length > 0) _this.loadDataSource1Fields(dataSourceId);
    },

    initDataSource2: function (dataSourceId, dataSourceText, cb) {
      var _this = this;
      // 数据源

      setTimeout(function () {
        _this
          .$('#dataSourceId')
          .wSelect2({
            serviceName: 'viewComponentService',
            queryMethod: 'loadSelectData',
            labelField: 'dataSourceText',
            valueField: 'dataSourceId',
            remoteSearch: false,
            queryCallBack: cb,
            async: true
          })
          .change(function () {
            var id = _this.$('#dataSourceId').val() || dataSourceId;
            _this.loadDataSource2Fields(id);
          })
          .val(dataSourceId)
          .trigger('change');
      }, 200);
    },
    /**
     * 加载数据源对应的字段
     */
    loadDataSource1Fields: function (dataSourceId) {
      var _this = this;
      JDS.call({
        service: 'dataSourceDefinitionService.getDataSourceFieldsById',
        data: dataSourceId,
        async: false,
        success: function (result) {
          var data = result.data;
          var optionHtml = '<option></option>';
          for (var i = 0; i < data.length; i++) {
            if (data[i].fieldName == null) {
              console.log('ID为[' + dataSourceId + ']的数据源的列的fieldName属性为null,请确认数据源的配置是否有问题!');
              continue;
            }
            optionHtml += "<option value='" + data[i].fieldName + "'>" + data[i].titleName + '</option>';
          }
          _this.$('#dataSourceFieldName').html(optionHtml);
          _this.$('#dataSourceDisplayName').html(optionHtml);
          if (_this.$('#dataSourceGroup')) {
            _this.$('#dataSourceGroup').html(optionHtml);
          }
        },
        error: function (msg) {
          alert(JSON.stringify(msg));
        }
      });
    },

    loadDataSource2Fields: function (dataSourceId) {
      var _this = this;
      var dataStoreId = dataSourceId;
      var dataStoreIdKey = 'data-' + dataStoreId;
      var nameSource = _this.$('#dataSourceId').data(dataStoreIdKey);
      if (nameSource == null || typeof nameSource === 'undefined') {
        JDS.call({
          service: 'viewComponentService.getColumnsById',
          data: [dataStoreId],
          async: false,
          success: function (result) {
            if (result.msg == 'success') {
              nameSource = $.map(result.data, function (data) {
                return {
                  value: data.columnIndex,
                  text: data.title,
                  dataType: data.dataType,
                  title: data.title,
                  id: data.columnIndex
                };
              });
            }
          }
        });
        _this.$('#dataSourceId').data(dataStoreIdKey, nameSource);
      }
      var data = nameSource || [];
      var optionHtml = '<option></option>';
      for (var i = 0; i < data.length; i++) {
        if (data[i].title == null) {
          console.log('ID为[' + dataSourceId + ']的数据源的列的title属性为null,请确认数据源的配置是否有问题!');
          continue;
        }
        optionHtml += "<option value='" + data[i].id + "'>" + data[i].title + '</option>';
      }

      _this.$('#dataSourceFieldName').html(optionHtml);
      _this.$('#dataSourceDisplayName').html(optionHtml);
      if (_this.$('#dataSourceGroup')) {
        _this.$('#dataSourceGroup').html(optionHtml);
      }
      return nameSource;
    },
    deleteSameProperty: function (source, target, ignoreProperties) {
      if (source == null || target == null) {
        return;
      }
      for (var i in target) {
        if (ignoreProperties && $.inArray(i, ignoreProperties) > -1) {
          continue;
        }
        var v = source[i];
        var tv = target[i];
        if (v == tv || JSON.stringify(v) == JSON.stringify(tv)) {
          delete target[i];
        }
      }
    },
    /**
     * 确定时生成控件占位符
     *
     * @param _this
     * @param imgsrc
     * @param inputMode
     */
    createControlPlaceHolder: function (_this, imgSrc, field, domHtml) {
      var ctlHtml = null;
      if ($.trim(domHtml).length > 0) {
        ctlHtml = domHtml;
      } else {
        var inputMode = field.inputMode;
        var ctlHtml =
          "<img  class='value' inputMode='" +
          inputMode +
          "' name='" +
          field.name +
          "' title='" +
          field.displayName +
          "' src='" +
          imgSrc +
          "'>";
      }

      $(_this.editor.fieldClazzElement).attr('class', 'field');
      var element = CKEDITOR.dom.element.createFromHtml(ctlHtml);

      if (_this.editor.focusedDom == null || _this.editor.document.$.contains(_this.editor.focusedDom.$) === false /*游离元素*/) {
        _this.editor.focusedDom = _this.editor.document.findOne("img[name='" + field.name + "']"); // /查找占位符
      }

      if (_this.editor.focusedDom != null && _this.editor.focusedDom != undefined) {
        element.insertBefore(_this.editor.focusedDom);
        _this.editor.focusedDom.remove();
      } else {
        _this.editor.insertElement(element);
        var nextP = element.getNext();
        if (nextP && nextP.getName && $.trim(nextP.getName()).toLowerCase() === 'p') {
          nextP.remove();
        }
      }
      return element;
    },

    evalJSON: function (json, message) {
      try {
        return eval('(' + json + ')');
      } catch (e) {
        alert((message || '错误的JSON格式') + ':' + json);
        throw e;
      }
    },

    enableFocus: function (editor, dialog, containerID) {
      if (typeof dialog == 'undefined' || dialog == null) return;
      // TODO remove unreliable setTimeout
      for (var i = 0, d = dialog._.focusList; i < d.length; i++) {
        if (d[i].isFocusable() === false) {
          d.splice(i, 1);
          i--;
        } else {
          d[i].focusIndex = i;
        }
        // 从下标为i的元素开始，连续删除1个元素// 删除下标为i的元素后，该位置又被新的元素所占据，所以要重新检测该位置
      }
      // window.setTimeout(function () {
      var lcontinerId = '#' + containerID;
      dialog._.fi = 0;
      $(lcontinerId)
        .find('input,select,textarea,button')
        .each(function (tidx, ele) {
          var element = new CKEDITOR.dom.element(ele);
          element.focusIndex = tidx;
          dialog.addFocusable(element, element.focusIndex);
        });
      dialog._.currentFocusIndex = 0;
      // }, 800);
      /*
       * window.setTimeout(function () {
       * $(dialog).find("input,select,textarea,button").each(function(tidx,
       * ele){ if(!!this.getAttribute( 'disabled' )) return;
       * $(ele).prop("tabindex", ele.tabIndex ||
       * tidx).keydown(function(evn){ // debugger; evn.stopPropagation();
       * }); }) }, 800);
       */
    },
    exitDialog: function (editor1) {
      if (typeof editor1 == undefined && (this.editor == null || this.editor == undefined)) {
        return;
      } else if (!(this.editor == null || this.editor == undefined)) {
        editor1 = this.editor;
      }
      // 取消时删除拖拽临时添加的字段
      if (editor1.focusedDom && editor1.focusedDom.getAttribute('data-is-temp') === 'true') {
        var fieldName = editor1.focusedDom.getAttribute('name');
        if (formDefinition.fields[fieldName]) {
          formDefinition.deleteField(fieldName);
        }
      }
      editor1.focusedDom = null; // /查找占位符
    },

    reaplaceInitDefaultValue: function (initdefaultvalue) {
      var tempStr = initdefaultvalue;
      tempStr = tempStr.replace(dySysVariable.currentYearMonthDate, '{当前日期(2000-01-01)}');
      tempStr = tempStr.replace(dySysVariable.currentYearMonthDateCn, '{当前日期(2000年1月1日)}');
      tempStr = tempStr.replace(dySysVariable.currentYearCn, '{当前日期(2000年)}');
      tempStr = tempStr.replace(dySysVariable.currentYearMonthCn, '{当前日期(2000年1月)}');
      tempStr = tempStr.replace(dySysVariable.currentMonthDateCn, '{当前日期(1月1日)}');
      tempStr = tempStr.replace(dySysVariable.currentWeekCn, '{当前日期(星期一)}');
      tempStr = tempStr.replace(dySysVariable.currentYear, '{当前年份(2000)}');
      tempStr = tempStr.replace(dySysVariable.currentTimeMin, '{当前时间(12:00)}');
      tempStr = tempStr.replace(dySysVariable.currentTimeSec, '{当前时间(12:00:00)}');
      tempStr = tempStr.replace(dySysVariable.currentDateTimeMin, '{当前日期时间(2000-01-01 12:00)}');
      tempStr = tempStr.replace(dySysVariable.currentDateTimeSec, '{当前日期时间(2000-01-01 12:00:00)}');
      tempStr = tempStr.replace(dySysVariable.currentUser, '{当前用户}');
      tempStr = tempStr.replace(dySysVariable.currentUserId, '{当前用户ID}');
      tempStr = tempStr.replace(dySysVariable.currentUserName, '{当前用户姓名}');
      tempStr = tempStr.replace(dySysVariable.currentUserDepartment, '{当前用户部门}');
      tempStr = tempStr.replace(dySysVariable.currentUserDepartmentId, '{当前用户部门ID}');
      tempStr = tempStr.replace(dySysVariable.currentUserDepartmentName, '{当前用户部门(短名称)}');
      tempStr = tempStr.replace(dySysVariable.currentUserDepartmentPath, '{当前用户部门(长名称)}');
      tempStr = tempStr.replace(dySysVariable.currentUserMainJob, '{当前用户主职位}');
      tempStr = tempStr.replace(dySysVariable.currentUserMainJobId, '{当前用户主职位ID}');
      tempStr = tempStr.replace(dySysVariable.currentUserMainJobName, '{当前用户主职位(短名称)}');
      tempStr = tempStr.replace(dySysVariable.currentUserMainJobPath, '{当前用户主职位(长名称)}');
      tempStr = tempStr.replace(dySysVariable.currentUserBizUnit, '{当前用户业务单位}');
      tempStr = tempStr.replace(dySysVariable.currentUserBizUnitId, '{当前用户业务单位ID}');
      tempStr = tempStr.replace(dySysVariable.currentUserBizUnitName, '{当前用户业务单位名称}');
      tempStr = tempStr.replace(dySysVariable.currentUserSysUnit, '{当前用户系统单位}');
      tempStr = tempStr.replace(dySysVariable.currentUserSysUnitId, '{当前用户系统单位ID}');
      tempStr = tempStr.replace(dySysVariable.currentUserSysUnitName, '{当前用户系统单位名称}');
      tempStr = tempStr.replace(dySysVariable.currentCreatorId, '{创建人ID}');
      tempStr = tempStr.replace(dySysVariable.currentCreatorName, '{创建人姓名}');
      tempStr = tempStr.replace(dySysVariable.currentCreatorDepartmentId, '{创建人部门ID}');
      tempStr = tempStr.replace(dySysVariable.currentCreatorDepartmentPath, '{创建人部门(长名称)}');
      tempStr = tempStr.replace(dySysVariable.currentCreatorDepartmentName, '{创建人部门(短名称)}');
      tempStr = tempStr.replace(dySysVariable.currentCreatorMainJobName, '{创建人主职位}');
      return tempStr;
    },

    reaplaceDefaultValue: function (defaultvalue) {
      var tempStr = defaultvalue;
      tempStr = tempStr.replace('{当前日期(2000-01-01)}', dySysVariable.currentYearMonthDate);
      tempStr = tempStr.replace('{当前日期(2000年1月1日)}', dySysVariable.currentYearMonthDateCn);
      tempStr = tempStr.replace('{当前日期(2000年)}', dySysVariable.currentYearCn);
      tempStr = tempStr.replace('{当前日期(2000年1月)}', dySysVariable.currentYearMonthCn);
      tempStr = tempStr.replace('{当前日期(1月1日)}', dySysVariable.currentMonthDateCn);
      tempStr = tempStr.replace('{当前日期(星期一)}', dySysVariable.currentWeekCn);
      tempStr = tempStr.replace('{当前年份(2000)}', dySysVariable.currentYear);
      tempStr = tempStr.replace('{当前时间(12:00)}', dySysVariable.currentTimeMin);
      tempStr = tempStr.replace('{当前时间(12:00:00)}', dySysVariable.currentTimeSec);
      tempStr = tempStr.replace('{当前日期时间(2000-01-01 12:00)}', dySysVariable.currentDateTimeMin);
      tempStr = tempStr.replace('{当前日期时间(2000-01-01 12:00:00)}', dySysVariable.currentDateTimeSec);
      tempStr = tempStr.replace('{当前用户}', dySysVariable.currentUser);
      tempStr = tempStr.replace('{当前用户ID}', dySysVariable.currentUserId);
      tempStr = tempStr.replace('{当前用户姓名}', dySysVariable.currentUserName);
      tempStr = tempStr.replace('{当前用户部门}', dySysVariable.currentUserDepartment);
      tempStr = tempStr.replace('{当前用户部门ID}', dySysVariable.currentUserDepartmentId);
      tempStr = tempStr.replace('{当前用户部门(短名称)}', dySysVariable.currentUserDepartmentName);
      tempStr = tempStr.replace('{当前用户部门(长名称)}', dySysVariable.currentUserDepartmentPath);
      tempStr = tempStr.replace('{当前用户主职位}', dySysVariable.currentUserMainJob);
      tempStr = tempStr.replace('{当前用户主职位ID}', dySysVariable.currentUserMainJobId);
      tempStr = tempStr.replace('{当前用户主职位(短名称)}', dySysVariable.currentUserMainJobName);
      tempStr = tempStr.replace('{当前用户主职位(长名称)}', dySysVariable.currentUserMainJobPath);
      tempStr = tempStr.replace('{当前用户业务单位}', dySysVariable.currentUserBizUnit);
      tempStr = tempStr.replace('{当前用户业务单位ID}', dySysVariable.currentUserBizUnitId);
      tempStr = tempStr.replace('{当前用户业务单位名称}', dySysVariable.currentUserBizUnitName);
      tempStr = tempStr.replace('{当前用户系统单位}', dySysVariable.currentUserSysUnit);
      tempStr = tempStr.replace('{当前用户系统单位ID}', dySysVariable.currentUserSysUnitId);
      tempStr = tempStr.replace('{当前用户系统单位名称}', dySysVariable.currentUserSysUnitName);
      tempStr = tempStr.replace('{创建人ID}', dySysVariable.currentCreatorId);
      tempStr = tempStr.replace('{创建人姓名}', dySysVariable.currentCreatorName);
      tempStr = tempStr.replace('{创建人部门ID}', dySysVariable.currentCreatorDepartmentId);
      tempStr = tempStr.replace('{创建人部门(长名称)}', dySysVariable.currentCreatorDepartmentPath);
      tempStr = tempStr.replace('{创建人部门(短名称)}', dySysVariable.currentCreatorDepartmentName);
      tempStr = tempStr.replace('{创建人主职位}', dySysVariable.currentCreatorMainJobName);
      return tempStr;
    }
  };
})(jQuery);
