(function () {
  var arrEntities1 = {
    '<': '&lt;',
    '>': '&gt;',
    '&': '&amp;',
    '"': '&quot;',
    "'": '&apos;'
  };

  function html2Escape(sHtml) {
    if (typeof sHtml === 'string') {
      return sHtml.replace(/[<>&"']/g, function (c) {
        return arrEntities1[c];
      });
    }
  }
  var controlConfig = {};
  $.extend(controlConfig, $.ControlConfigUtil, {
    /**
     * 初始化属性窗口
     */
    initPropertyDialog: function (editor) {
      var _this = this;
      _this.$('#tabs').tabs();
      this.editor = editor;
      var focusedDom = editor.focusedDom; // 当前editor被双击的对象(CKEDITOR.dom.node类)
      var focusedFormUuid = null;
      var subformDefinition = null;
      _this.$("input[name='isGroupShowTitle']").change(function () {
        var checkvalue = _this.$("input[name='isGroupShowTitle']:checked").val();
        if (checkvalue == '1') {
          $('#tr_groupShowTitle').show();
          $('#tr_isGroupColumnShow').show();
          $('#tr_supportSubRow').hide();
          $('#td_stackSeqNo').show();
        } else {
          $('#tr_supportSubRow').show();
          $('#tr_groupShowTitle').hide();
          $('#tr_isGroupColumnShow').hide();
          $('#td_stackSeqNo').hide();
        }
      });
      _this.$('input[name=layoutType]').on('change', function (event) {
        var paginationTab = $('[aria-controls=pagination]');
        if (this.value == '1') {
          // 内联模式只能非弹框编辑
          // _this.$("input[name='editMode'][value='1']").prop('checked', true).trigger('change');
          _this.$('.hs_layoutType_1').hide();
          _this.$('.hs_layoutType_0').show();
          $('#dysubformconfigcontainer').attr('layout', 1);
          $('[aria-controls=pagination]').hide();
        } else {
          _this.$('.hs_layoutType_0').hide();
          _this.$('.hs_layoutType_1').show();
          $('#dysubformconfigcontainer').attr('layout', 0);
          _this.$("input[name='editMode']:checked").trigger('change');
          paginationTab.show();
        }
      });

      _this
        .$('#lineOperate')
        .off()
        .on('click', function () {
          var status = $(this).find('.switch-radio').data('status');
          $(this)[status == '1' ? 'removeClass' : 'addClass']('active');
          _this.$('.tr_choose_type')[status == '1' ? 'hide' : 'show']();
          $(this)
            .find('.switch-radio')
            .data('status', status == '1' ? '0' : '1');
        });

      _this
        .$('#showType')
        .off()
        .on('click', function () {
          var $radio = $(this);
          if ($radio.data('waschecked') == true) {
            $radio.prop('checked', false);
            $radio.data('waschecked', false);
          } else {
            $radio.prop('checked', true);
            $radio.data('waschecked', true);
          }
        });

      if (focusedDom != null && typeof focusedDom != 'undefined') {
        // 初始化在editor设计器中被双击的对象的属性
        focusedFormUuid = $(focusedDom).attr('formuuid');
      }

      if (StringUtils.isNotBlank(focusedFormUuid)) {
        console.log('存在从表---');
        _this.$('#formTree').val(focusedFormUuid).trigger('change');
        // 初始化设置对话框中的各域
        window.setTimeout(function () {
          subformDefinition = formDefinition.getSubform(focusedFormUuid); // 表单中从表的信息
          console.log('subformDefinition----', subformDefinition);
          if (!subformDefinition) {
            alert('找不到对应的配置[' + focusedFormUuid + ']');
            return;
          }
          setPagination(subformDefinition.pagination || {});

          var formDefinitionDetail = loadFormDefinitionAndInitProperty.call(_this, focusedFormUuid, subformDefinition.tableButtonInfo); // 取得表单的各字段

          _this.$("input[name='tableOpen']").each(function () {
            if ($(this).val() == subformDefinition.tableOpen) {
              this.checked = true;
            }
          });

          _this.$("input[name='editMode']").each(function () {
            if ($(this).val() == subformDefinition.editMode) {
              this.checked = true;
            }
          });

          _this.$("input[name='hideButtons']").each(function () {
            if ($(this).val() == subformDefinition.hideButtons) {
              this.checked = true;
            }
          });

          _this.$("input[name='hideButtons']").each(function () {
            if ($(this).val() == subformDefinition.hideButtons) {
              this.checked = true;
            }
          });

          _this.$("input[name='hideButtons']").each(function () {
            if ($(this).val() == subformDefinition.hideButtons) {
              this.checked = true;
            }
          });

          _this.$("input[name='isGroupShowTitle']").each(function () {
            if ($(this).val() == subformDefinition.isGroupShowTitle) {
              this.checked = true;
            }
          });

          _this.$("input[name='isGroupColumnShow']").each(function () {
            if ($(this).val() == subformDefinition.isGroupColumnShow) {
              this.checked = true;
            }
          });

          // add by wujx 20160921 begin
          _this.$("input[name='isAllowBlankRow']").each(function () {
            if ($(this).val() == subformDefinition.isAllowBlankRow) {
              this.checked = true;
            }
          });
          // add by wujx 20160921 end

          _this
            .$('input[name=layoutType]')
            .filter('[value=' + subformDefinition.layoutType + ']')
            .prop('checked', true)
            .trigger('change');
          _this.$('select[name=rowTitle]').val(subformDefinition.rowTitle);

          _this.$('#groupShowTitle').attr('value', subformDefinition.groupShowTitle);

          _this.$('#openFormTree').val(subformDefinition.openFormTree);
          _this.$('input[name=winSize][value=' + subformDefinition.winSize + ']').prop('checked', true);
          loadDrawiFormTree(); // 加载所有的表单，并形成表单树==从表弹窗单据
          _this.$('#displayName').val(subformDefinition.displayName);
          if (subformDefinition.editMode == '2') {
            // 是否显示
            _this.$('.td_editableMode').hide();
            _this.$('#openFormTreeLabel').show();
          } else {
            _this.$('.td_editableMode').show();
            _this.$('#openFormTreeLabel').hide();
          }
          // 多选回填

          _this.$('#editableMode').val(subformDefinition.editableMode);

          var $tableHeader = _this.$("textarea[name='table-header-json']");
          $tableHeader.val(JSON.stringify(subformDefinition.tableHeader, null, 4));
          $.get(ctx + '/static/dyform/definition/ckeditor/plugins/dysubform/dialogs/example.json', function (data) {
            $tableHeader.attr('placeholder', JSON.stringify(data, null, 4));
          });

          _this
            .$('#formFieldsTbl')
            .find("input[name='inputField']")
            .each(function () {
              $(this).parent().parent().remove();
            });
          // console.log(JSON.cStringify(subformDefinition.fields));
          var fieldsOfSubform = subformDefinition.fields;
          var fieldsOfSubformBesored = formDefinition.sortFieldsOfSubform(focusedFormUuid, false);

          showFieldsOfForm(fieldsOfSubformBesored, focusedFormUuid);

          // 私有属性
          if (subformDefinition.isGroupShowTitle == '1') {
            $('#tr_groupShowTitle').show();
            $('#tr_isGroupColumnShow').show();
            $('#tr_supportSubRow').hide();
            $('#td_stackSeqNo').show();
          } else if (subformDefinition.isGroupShowTitle == '2') {
            $('#tr_groupShowTitle').hide();
            $('#tr_isGroupColumnShow').hide();
            $('#tr_supportSubRow').show();
            $('#td_stackSeqNo').hide();
          }
          setSubFormHeight(subformDefinition.subFormHeight);
          setAutoWidth(subformDefinition.autoWidth);
          setFixedHeader(subformDefinition.fixedHeader);
          setSupportSubRow(subformDefinition.supportSubRow);
          setShowImpButton(subformDefinition.showImpButton);
          setShowExpButton(subformDefinition.showExpButton);
          setShowExpButton(subformDefinition.showExpButton);
          setDefaultRowCount(subformDefinition.defaultRowCount);
          setShowRowCount(subformDefinition.showRowCount);
          setShowClearButton(subformDefinition.showClearButton);

          setMultiSelect(subformDefinition.multiselect);
          var lineSelection = subformDefinition.lineSelection == '0' ? '0' : '1';
          if (lineSelection != '1') {
            _this.$('#lineOperate').trigger('click');
          }

          _this.$('#hideSeqNo').prop('checked', !!subformDefinition.hideSeqNo);
          _this.$('#stackSeqNo').prop('checked', !!subformDefinition.stackSeqNo);
          _this.$('#horizontalScroll').prop('checked', !!subformDefinition.horizontalScroll);
          _this.$('#validateOnHidden').prop('checked', !!subformDefinition.validateOnHidden);
          var showType = subformDefinition.showType == '5' ? true : false;
          _this.$('#showType').prop('checked', showType).data('waschecked', showType);

          $('#showRowCount').on('change', function () {
            var val = $(this).val();
            if (val <= 0) {
              $(this).val('');
            }
          });

          initEventProperty(subformDefinition);
          // //初始化按钮事件表格
          // initButtonTableRow(subformDefinition.tableButtonInfo);
          // initButtionTableEvent(subformDefinition.tableButtonInfo);
        }, 100);
      } else {
        _this.$('#formTree').val('点击这里选择从表');
        setSubFormHeight(270);
        setAutoWidth(true); // 默认宽度自适应
        setSupportSubRow(false); // 默认不支持子行
        setShowImpButton(false); //
        setShowExpButton(false); //
        setShowClearButton(false);
        setPagination({});
      }

      loadFormTree.call(_this, focusedFormUuid); // 加载所有的表单，并形成表单树
      initEditModeEvent(); // 初始化编辑模式事件

      $('#subFormHeightTip').popover({
        html: true,
        placement: 'bottom',
        container: 'body',
        trigger: 'hover',
        template:
          '<div class="popover" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
        content: function () {
          return (
            '<span>1、超出最大高度时，从表数据区域显示垂直滚动条</span><br>' +
            '<span>2、为空时表示高度不限制，从表数据全部展示，无滚动条</span><br>' +
            '<span>3、只能输入正整数</span>'
          );
        }
      });

      $('#horizontalScrollTip').popover({
        html: true,
        placement: 'top',
        container: 'body',
        trigger: 'hover',
        template:
          '<div class="popover" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
        content: function () {
          return (
            '<span>支持横向滚动时，列宽遵循以下规则：</span><br>' +
            '<span>1、列属性中已设置列宽的：列宽即按照设置的宽度 </span><br>' +
            '<span>2、列属性中未设置列宽的：【展示为控件】未选中的列，限制列宽最大300px；</span><br>' +
            '<span>【展示为控件】选中的列，限制列宽固定为300px</span>'
          );
        }
      });
      $('#btnShowTypeTip').popover({
        html: true,
        placement: 'bottom',
        container: 'body',
        trigger: 'hover',
        template:
          '<div class="popover" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
        content: function () {
          return (
            '<span>在操作按钮选择时会区分显示，如流程环节-表单设置</span><br><span>显示类操作：不会影响数据存储的操作</span><br>' +
            '<span>编辑类操作：会影响数据存储的操作</span>'
          );
        }
      });
    },
    // 提交确认
    collectFormAndFillCkeditor: function (editor) {
      var that = this;
      var subform = new SubFormClass();
      // 分页
      var _pagination = getPagination();
      if (_pagination.isShow && _pagination.onSizes && _pagination.pageList === '') {
        return alert('请选择“切换分页数”');
      }
      subform.pagination = _pagination;
      subform.formUuid = that.$('#formTree').attr('formuuid');
      subform.outerId = that.$('#formTree').attr('formOuterId');
      subform.name = that.$('#formTree').attr('formname');
      subform.displayName = that.$('#displayName').val();

      subform.events = collectEvents(subform);

      // that.$("#scriptContent")

      subform.openFormTree = that.$('#openFormTree').val();
      subform.winSize = that.$('input[name=winSize]:checked').val();
      subform.tableOpen = that.$("input[name='tableOpen']:checked").val();
      subform.editMode = that.$("input[name='editMode']:checked").val(); // 用户要对从表的某条记录进行编辑时，是在当前的单元格里直接编辑，或者弹出新窗口让用户在窗口中编辑
      subform.hideButtons = that.$("input[name='hideButtons']:checked").val(); // 是否展示操作按钮
      if (subform.editMode == '2' && $.trim(subform.openFormTree).length <= 0) {
        return alert('请选择弹窗表单');
      }
      subform.showImpButton = getShowImpButton();
      subform.showExpButton = getShowExpButton();
      subform.showClearButton = getShowClearButton();
      subform.lineSelection = that.$('#lineOperate').find('.switch-radio').data('status');
      subform.multiselect = getMultiSelect();

      subform.subFormHeight = that.$("input[name='subFormHeight']").val();
      if (subform.subFormHeight && !/(^[1-9]\d*$)/.test(subform.subFormHeight)) {
        return alert('从表数据最大高度只能输入正整数！');
      }
      subform.hasSetHeight = true;

      subform.hideSeqNo = that.$('#hideSeqNo').prop('checked');
      subform.stackSeqNo = that.$('#stackSeqNo').prop('checked');
      subform.validateOnHidden = that.$('#validateOnHidden').prop('checked');
      subform.showType = that.$('#showType').prop('checked') ? '5' : '1';
      subform.horizontalScroll = that.$('#horizontalScroll').prop('checked');
      subform.editableMode = that.$('#editableMode').val();
      subform.isGroupShowTitle = that.$("input[name='isGroupShowTitle']:checked").val(); // 是否分组展示
      subform.groupShowTitle = that.$('#groupShowTitle').find('option:selected').val();

      try {
        var tableHeader = that.$("textarea[name='table-header-json']").val();
        subform.tableHeader = tableHeader && JSON.parse(tableHeader);
      } catch (ex) {
        // console.log(ex);
      }

      subform.isGroupColumnShow = that.$("input[name='isGroupColumnShow']:checked").val(); // 分组字段是否显示
      subform.isAllowBlankRow = that.$("input[name='isAllowBlankRow']:checked").val(); // 允许提交空行
      subform.layoutType = that.$('input[name=layoutType]:checked').val();
      // 内联模式只能非弹框编辑
      subform.layoutType === '1' && (subform.editMode = '1');
      subform.rowTitle = that.$('select[name=rowTitle]').val();
      subform.autoWidth = getAutoWidth();
      subform.fixedHeader = getFixedHeader();
      subform.supportSubRow = getSupportSubRow();
      subform.defaultRowCount = getDefaultRowCount();
      subform.showRowCount = getShowRowCount();
      var ckTable =
        '<table style="border-collapse:collapse;"   border="1"  formuuid="' +
        that.$('#formTree').attr('formuuid') +
        '" title="' +
        that.$('#displayName').val() +
        '">';
      editor = editor || that.editor;

      var $tab = that.$("table[id='formFieldsTbl']");
      var inputArr = $($tab.get($tab.length - 1)).find("input[name='inputField']");

      if (inputArr.length == 0) {
        // 用户没有选定任何字段
        return alert('请选择有“列属性”的表单');
      }

      // tableButtonInfo
      var $tableButton = that.$("table[id='formButtonTbl']");
      var inputButtons = $($tableButton.get($tableButton.length - 1)).find("input[name='btnName']");
      var tableButtonInfo = [];
      var btnCode = [];
      var hasRepeatCode = false;
      for (var i = 0; i < inputButtons.length; i++) {
        var button = new TableButtonClass();
        var $tr = $(inputButtons[i]).parents('tr').first();
        button.code = $tr.find("div[name='btnCode']").text() || $tr.find("input[name='btnCode']").val(); // 编码
        button.text = $tr.find("input[name='btnName']").val(); // 标题
        button.type = $tr.find("div[name='btnType']").text(); // 标题
        button.operate = $tr.find("div[name='btnOperate']").text() || $tr.find("select[name='btnOperate']").val(); // 标题
        button.uuid = $tr.find("input[name='uuid']").val(); // uuid
        button.cssClass = $tr.find("input[name='btnClass']").val(); // 样式类
        button.buttonEvents = $tr.find("input[name='btnEvent']").data('defineEventJs'); //
        button.position = $.map($tr.find('.checklist input[type=checkbox]:checked'), function (element) {
          return $(element).val();
        });
        button.displayText = $tr.find("input[name='btnEvent']").val();
        if (btnCode.indexOf(button.code) > -1) {
          hasRepeatCode = true;
          break;
        }
        btnCode.push(button.code);
        tableButtonInfo.push(button);
      }
      if (hasRepeatCode) {
        appModal.error('按钮编码不能重复！');
        return false;
      }
      subform.tableButtonInfo = tableButtonInfo;

      var $table = $('table[formuuid="' + subform.formUuid + '"]', editor.document && editor.document.$).not(
        editor.focusedDom && editor.focusedDom.$
      );
      if ($table.length > 0) {
        return alert('从表已添加过，请选择其他表单');
      }
      var ckTr = '<tr>';
      var appendHtml = '';
      var fields = {};
      for (var i = 0; i < inputArr.length; i++) {
        var field = new SubFormFieldClass();
        field.name = $(inputArr[i]).attr('fieldName');
        /*
        $.each(that._fields[field.name].fieldCheckRules, function (i, item) {
          if (item.value === '1') {
            field.isRequired = true;
          }
        });
         */
        var $tr = $(inputArr[i]).parents('tr').first();
        field.displayName = $(inputArr[i]).val();
        field.order = i; // 列排序
        field.sort = $tr.find("select[name='sort']").val(); // 行排序
        console.log(field.name + '=' + field.sort);
        var fieldWidth = $tr.find("input[name='widthField']").val();
        field.width = fieldWidth;
        field.formula = $tr.find("textarea[name='formula']").val();
        if (field.formula === 'function formatter(value, row, index){}') {
          delete field.formula; // 未实现函数功能
        }
        var $HideField = $tr.find("input[name='hideField']:checked");
        if ($HideField.size() > 0) {
          // 列隐藏
          field.hidden = dySubFormFieldShow.notShow;
        } else {
          // 列展示
          field.hidden = dySubFormFieldShow.show;
        }
        console.log(field.displayName + ':' + field.name + ':' + field.hidden);

        var $editField = $tr.find("input[name='editField']:checked");
        if ($editField.size() > 0) {
          // 不可编辑
          field.editable = dySubFormFieldEdit.notEdit;
        } else {
          // 可编辑
          field.editable = dySubFormFieldEdit.edit;
        }

        var $ctlField = $tr.find("input[name='ctlField']:checked");
        if ($ctlField.size() > 0) {
          //
          field.controlable = dySubFormFieldCtl.control;
        } else {
          //
          field.controlable = dySubFormFieldCtl.label;
        }

        field.frozen = that.$("input[name='frozenField'][fieldName='" + field.name + "']").prop('checked');
        field.textAlign = $tr.find('input.alignField:checked').val();

        subform.fields[field.name] = field;
        // modify time 2015-12-23 yuyq
        // 在从表字段中加入fieldName='"+field.name+"'字段名称
        appendHtml += "<th fieldName='" + field.name + "' style='text-align: " + field.textAlign + "'>" + $(inputArr[i]).val() + '</th>';
        // end time 2015-12-23
      }

      ckTr += appendHtml + '</tr>';

      ckTable += ckTr + '</table>';

      if ($.isArray(subform.tableHeader) && subform.tableHeader.length > 0) {
        var $ckTable = $(ckTable).empty();
        $.each(subform.tableHeader, function (i, columns) {
          var $tr = $('<tr>').appendTo($ckTable);
          $.each(columns, function (j, column) {
            column.colspan = column.colspan || 1;
            column.rowspan = column.rowspan || 1;
            var $td = $('<td>')
              .attr({
                colspan: column.colspan,
                rowspan: column.rowspan
              })
              .css({
                'text-align': column.align,
                'vertical-align': column.valign
              })
              .text(column.title)
              .appendTo($tr);
            var field = subform.fields[column.field];
            if (field && field.displayName) {
              $td.attr('fieldName', column.field).css('text-align', field.textAlign).text(field.displayName);
            }
          });
        });
        ckTable = $ckTable.prop('outerHTML');
      }

      var element = this.createControlPlaceHolder(this, '', subform, ckTable);
      if (element && element.unselectable) {
        element.unselectable();
      }

      // formDefinition.subforms[subform.formUuid] = subform;
      formDefinition.addSubform(subform.formUuid, subform);
      return true;
    },
    // 关闭
    exitDialog: function (editor) {
      editor = editor || this.editor;
      $('#container_dysubform').empty(); // 防止缓存 ，出现多个不同的属性弹出框由于缓存，出现重复元素
      if (editor && editor.focusedDom) {
        editor.focusedDom = null;
      }
    }
  });

  var $ace;

  /**
   * 加载表单定义及将各字段显示出来
   */
  function loadFormDefinitionAndInitProperty(formUuid, tableButtonInfo) {
    var that = this;
    var formDefinition = null; // 被选中的从表
    // 加载表单定义
    formDefinition = loadFormDefinition(formUuid);
    // 初始化从表按钮事件
    if (_.isEmpty(tableButtonInfo)) {
      // 清空旧配置
      var $table = that.$('#formButtonTbl tbody').empty();
      var tableButtonInfo = []; // "btn_edit"
      var initButtonsCode = [
        'btn_add',
        'btn_edit',
        'btn_copy',
        'btn_del',
        'btn_clear',
        'btn_up',
        'btn_down',
        'btn_add_sub',
        'btn_exp_subform',
        'btn_imp_subform'
      ];
      var initButtonsText = ['添加', '编辑', '复制行', '删除', '清除空行', '上移', '下移', '添加子行', '导出', '导入'];
      var initButtonsPosition = [['1'], ['2'], ['2'], ['2'], ['1'], ['2'], ['2'], ['2'], ['1'], ['1']];
      var initButtonEvents = [
        {
          click: "DyformFacade.addRowData(DyformFacade.get$dyform().getFormId(),data.formUuid,{id:'', newRow: true}, dysubform);"
        },
        {
          click: 'DyformFacade.addRowData(DyformFacade.get$dyform().getFormId(),data.formUuid, data, dysubform);'
        },
        {
          click: 'DyformFacade.addRowData(DyformFacade.get$dyform().getFormId(),data.formUuid, data, dysubform, true);'
        },
        {
          click: 'DyformFacade.deleteRowData(DyformFacade.get$dyform().getFormId(),data.formUuid,selectedRowIds);'
        },
        {
          click: 'DyformFacade.clearSubFormBlankRow(DyformFacade.get$dyform().getFormId(),data.formUuid);'
        },
        {
          click: 'DyformFacade.upSubFormRowData(DyformFacade.get$dyform().getFormId(),data.formUuid,selectedRowId);'
        },
        {
          click: 'DyformFacade.downSubFormRowData(DyformFacade.get$dyform().getFormId(),data.formUuid,selectedRowId);'
        },
        {
          click: 'DyformFacade.addSubformRowDataInJqGrid(DyformFacade.get$dyform().getFormId(),data.formUuid,selectedRowId);'
        },
        {
          click: "DyformFacade.subFormExcelExp4MainFormEx('abc',DyformFacade.get$dyform().getFormId());"
        },
        {
          click: "DyformFacade.subFormExcelImp4MainForm('abc')"
        }
      ];

      for (var i = 0; i < initButtonsCode.length; i++) {
        var button = new TableButtonClass();
        button.code = initButtonsCode[i]; // 编码
        button.text = initButtonsText[i]; // 标题
        button.position = initButtonsPosition[i]; // 位置
        button.uuid = createUUID(); // uuid
        button.buttonEvents = initButtonEvents[i]; //
        button.displayText = '鼠标点击事件;';
        tableButtonInfo.push(button);
      }
    }
    that.$('#formTree').attr('formuuid', formUuid);
    that.$('#formTree').attr('formname', formDefinition.tableName);
    that.$('#formTree').attr('formOuterId', formDefinition.id);
    that.$('#displayName').val(formDefinition.name);

    initButtonTableRow(tableButtonInfo);
    initButtionTableEvent(tableButtonInfo);
    //表格全选
    that
      .$('#formButtonTbl #selectAll')
      .die()
      .live('click', function () {
        var selectAll = $(this).prop('checked');
        $('#formButtonTbl .tr-td').each(function () {
          var t = $(this);
          if (t.find('input[name="isCheck"]').attr('disabled') != 'disabled') {
            t.find('input[name="isCheck"]').prop('checked', selectAll);
          }
        });
      });

    var fields = {};

    function unWrapSrc(ohtml) {
      var html = "<span id='_htmldyform_'>" + ohtml + '</span>';
      var srcpattern = /src="\/resources[^\"]+ckeditor[^\"]+plugins[^\"]+images[^\"]+jpg\"/;
      var srcpattern2 = /src="resources[^\"]+ckeditor[^\"]+plugins[^\"]+images[^\"]+jpg\"/;
      html = html.replace(srcpattern, '');
      html = html.replace(srcpattern2, '');
      while (srcpattern.test(html) || srcpattern2.test(html)) {
        html = html.replace(srcpattern, '');
        html = html.replace(srcpattern2, '');
      }

      var placeHolderImgPattern1 = /<img[^>]+class=[\"value\"|\'value\'][^>]+name=[\'|\"]([^\'|^\"]*)[\'|\"][^>]+>/i;
      var placeHolderImgPattern2 = /<img[^>]+name=[\'|\"](.*)[\'|\"][^>]+class=[\"value\"|\'value\'][^>]+>/i;

      while (placeHolderImgPattern1.test(html) || placeHolderImgPattern2.test(html)) {
        var r = html.match(placeHolderImgPattern1);
        if (r != null) {
          html = html.replace(r[0], "<span class='value' name='" + r[1] + "'></span>");
        }

        var r = html.match(placeHolderImgPattern2);
        if (r != null) {
          html = html.replace(r[0], "<span class='value' name='" + r[1] + "'></span>");
        }
      }
      return $(html).html();
    }

    $(unWrapSrc(formDefinition.html))
      .find('.value[name]')
      .each(function () {
        var name = $(this).attr('name');
        if (formDefinition.getField(name)) {
          fields[name] = formDefinition.getField(name);
        }
      });
    // 显示该表单的所有域
    that._fields = fields;
    showFieldsOfForm(fields, formUuid);
    // 初始化分组字段
    initFieldSelect(formDefinition);
    return formDefinition;
  }

  /**
   * 加载所有的表单
   */
  function loadFormTree(focusedFormUuid) {
    console.log('加载所有的表单');
    var _this = this;
    var $formTree = $('#formTree');

    // 表单字段 从表类型 表单下拉选择框 报错修改
    var currentUserUnitId = undefined;
    try {
      currentUserUnitId = server.SpringSecurityUtils.getCurrentUserUnitId();
    } catch (e) {
      currentUserUnitId = require('server').SpringSecurityUtils.getCurrentUserUnitId();
    }

    var wSelectParams = {
      serviceName: 'dyFormFacade',
      queryMethod: 'queryAllPforms',
      selectionMethod: 'getSelectedFormDefinition',
      labelField: 'displayName',
      valueField: 'formTree',
      defaultBlank: true,
      width: 400,
      params: {
        includeSuperAdmin: true, //是否包含超管的模块
        systemUnitId: currentUserUnitId
      }
    };
    var sFormDefinition = null;
    if (
      window.formDefinition &&
      formDefinition.formType === 'V' &&
      formDefinition.subforms &&
      (sFormDefinition = formDefinition.subforms[focusedFormUuid])
    ) {
      wSelectParams.queryMethod = 'queryAllForms';
      wSelectParams.defaultBlank = false;
      $.extend(wSelectParams.params, {
        tableName: sFormDefinition.tableName || sFormDefinition.name
      });
    }
    $formTree.wSelect2(wSelectParams).trigger('change');

    $formTree.on('select2-open', function () {
      $('#select2-drop-mask').css('z-index', '10011');
      $('#select2-drop').css('z-index', '10011');
    });
    $formTree.on('select2-close', function () {
      $('#select2-drop-mask').css('z-index', '10001');
      $('#select2-drop').css('z-index', '10001');
    });
    $formTree.on('change', function () {
      if ($('#formTree').select2('data').id) {
        loadFormDefinitionAndInitProperty.call(_this, $('#formTree').select2('data').id, '');
        loadDrawiFormTree();
      }
    });
  }

  var paginationShow, paginationContainer;
  // 获取分页配置
  function getPagination() {
    var _pagination = {};
    var _isShow = paginationShow.prop('checked');
    _pagination.isShow = _isShow;
    _pagination.defPageSize = $('#defPageSize').val();
    _pagination.pageList = $('#pageList').val();
    _pagination.jumper = $('#paginationJumper').prop('checked');
    _pagination.showTotal = $('#paginationShowTotal').prop('checked');
    _pagination.oneHide = $('#paginationOneHide').prop('checked');
    _pagination.onSizes = $('#paginationOnSizes').prop('checked');
    var _layoutType = $('input[name=layoutType]:checked').val();
    if (_layoutType === '1') {
      _pagination.isShow = false;
    }
    return _pagination;
  }
  // 设置分页配置
  function setPagination(pagination) {
    console.log('设置分页配置---', pagination);
    bindPagination();
    bindDefPageSize();
    var isShow = !!pagination.isShow;
    changePagination(isShow);
    paginationShow.prop('checked', isShow);
    $('#defPageSize').val(pagination.defPageSize || 10);

    $('#paginationJumper').prop('checked', pagination.jumper === undefined ? true : pagination.jumper);
    $('#paginationShowTotal').prop('checked', pagination.showTotal === undefined ? true : pagination.showTotal);
    $('#paginationOneHide').prop('checked', pagination.oneHide === undefined ? true : pagination.oneHide);

    $('#pageList').val(pagination.pageList || '10,20,50,100,200');
    var onSizes = pagination.onSizes === undefined ? true : pagination.onSizes;
    $('#paginationOnSizes').prop('checked', onSizes);
    setPageList();
    changeOnSizes(onSizes);
    bindOnSizes();
  }
  // 设置切换分页数
  function setPageList() {
    var dysubformContainer = $('#dysubformconfigcontainer');
    $('#pageList', dysubformContainer).wellSelect({
      separator: ',',
      multiple: true,
      chooseAll: true,
      searchable: false,
      data: [
        { id: '5', text: '5' },
        { id: '10', text: '10' },
        { id: '15', text: '15' },
        { id: '20', text: '20' },
        { id: '50', text: '50' },
        { id: '100', text: '100' },
        { id: '200', text: '200' },
        { id: '500', text: '500' },
        { id: '1000', text: '1000' }
      ]
    });
  }

  // 开启分页/关闭分页
  function changePagination(isShow) {
    if (isShow) {
      paginationContainer.show();
    } else {
      paginationContainer.hide();
    }
  }

  // 绑定分页开关change事件
  function bindPagination() {
    paginationContainer = $('#paginationCfg');
    paginationShow = $('#paginationShow');
    paginationShow.change(function () {
      changePagination($(this).prop('checked'));
    });
  }

  // 设置默认分页数值
  function bindDefPageSize() {
    var defPageSize = $('#defPageSize');
    var reducePageSize = $('#reducePageSize');
    var addPageSize = $('#addPageSize');
    addPageSize.on('click', function () {
      var oldVal = defPageSize.val();
      oldVal++;
      defPageSize.val(oldVal);
    });
    reducePageSize.on('click', function () {
      var oldVal = defPageSize.val();
      if (oldVal > 5) {
        oldVal--;
        defPageSize.val(oldVal);
      }
    });
  }

  // 开启/关闭 切换分页数
  function changeOnSizes(isShow) {
    var wMultiple = $('.well-select-multiple');
    if (isShow) {
      wMultiple.show();
    } else {
      wMultiple.hide();
    }
  }

  // 绑定切换分页数开关
  function bindOnSizes() {
    var onSizes = $('#paginationOnSizes');
    onSizes.change(function () {
      changeOnSizes($(this).prop('checked'));
    });
  }

  window.fieldChange = function (select) {
    var selectedOption = $(select).find('option:selected');
    var input = $(select).parent().parent().find("input[name='inputField']");
    input.attr('fieldName', $(selectedOption).val());
    input.attr('fieldTitle', $(selectedOption).attr('name'));
    input.val($(selectedOption).attr('name'));
    $($(select).parents('td')[0]).siblings('td').first().html($(selectedOption).val());
  };

  /**
   * 初始化分组字段
   */
  function initFieldSelect(data) {
    // 显示被选中表单的各字段
    var fields = data.fields;

    var selectHTML = '';
    for (var i in fields) {
      var field = fields[i];
      if (field.sysType != dyFieldSysType.custom) {
        // 系统性字段不提供给外部使用
        continue;
      }
      selectHTML += "<option value='" + field.name + "' name='" + field.displayName + "' >" + field.displayName + '</option>';
    }
    $('#groupShowTitle').html('<option value="" >--请选择--</option>' + selectHTML);
    $('select[name=rowTitle]').html('<option value="" >--请选择--</option>' + selectHTML);
  }

  /**
   * 显示被选中的表单的字段
   */
  function showFieldsOfForm(fields, formUuid) {
    var selectHTML = "<select name='fieldSelect' class='formfileSelect' onchange='fieldChange(this)'>";
    var $table = $('#formFieldsTbl tbody');
    $('#formFieldsTbl .tr-td').remove();
    // modify time 2015-12-28 yuyq 从表新增行时动态获取最新的从表字段信息
    var fieldsNewSubform = loadFormDefinition(formUuid),
      newfields = {};
    if (fieldsNewSubform) {
      newfields = fieldsNewSubform.fields;
      for (var i in newfields) {
        var field = newfields[i];
        selectHTML += "<option value='" + field.name + "' name='" + field.displayName + "' >" + field.displayName + '</option>';
      }
    }
    // 显示被选中表单的各字段
    // console.log(JSON.cStringify(fields));
    console.log(fields);
    for (var i in fields) {
      var field = fields[i],
        newfield = newfields[i];
      if (window.require) {
        commons = require('commons');
      }
      var UUID = commons.UUID;
      var uuid = UUID.createUUID();
      if (field.sysType && field.sysType != dyFieldSysType.custom) {
        // 只显示用户自定义字段
        continue;
      }
      var inputMode = field['inputMode'] || (newfield && newfield['inputMode']);
      var sortType = ['asc nulls last', 'asc nulls first', 'desc nulls last', 'desc nulls first'];
      var sortHtml = '<select  name="sort"  fieldName= "' + field.name + '">';
      sortHtml += '<option value="">无</option>';
      for (var i = 0; i < sortType.length; i++) {
        sortHtml +=
          '<option value="' + sortType[i] + '" ' + (sortType[i] == field.sort ? 'selected' : '') + '>' + sortType[i] + '</option>';
        // console.log(sortHtml);
      }

      sortHtml += '</select>';

      var alignField = 'alignField-' + $.guid++;
      // console.log(field.editable + ":" + dySubFormFieldEdit.notEdit +(
      // field.editable == dySubFormFieldEdit.notEdit));
      // selectHTML += "<option value='"+field.name+"'
      // name='"+field.displayName+"' >" + field.displayName +
      // "</option>";
      var vTr = '<tr name="tr-td" class="tr-td" >';
      vTr +=
        '<td style="min-width:60px;">' +
        field.name +
        '</td><td style="height:30px;min-width:60px"><input   type="text" name="inputField" value="' +
        html2Escape(field.displayName) +
        '" fieldName= "' +
        field.name +
        '" fieldTitle="' +
        html2Escape(field.displayName) +
        '"/></td>';
      vTr += '<td  class=fieldMapping name=fieldMapping   ></td>';
      vTr += '<td >' + sortHtml + '</td>';
      vTr +=
        '<td style="width:30px;"><input   type="text" name="widthField" value="' +
        (typeof field.width == 'undefined' ? '' : field.width) +
        '" fieldName= "' +
        field.name +
        '"/></td>';
      vTr +=
        '<td><input   type="checkbox" name="hideField" value="' +
        dySubFormFieldShow.notShow +
        '" fieldName= "' +
        field.name +
        '"' +
        ' id="' +
        'hideField_' +
        uuid +
        '"' +
        (typeof field.hidden == 'undefined'
          ? field.showType == '5'
            ? 'checked'
            : ''
          : field.hidden == dySubFormFieldShow.notShow
          ? 'checked'
          : '') +
        '/>' +
        '<label for="' +
        'hideField_' +
        uuid +
        '"></label>' +
        '</td>';
      vTr +=
        '<td><input   type="checkbox" name="editField" value="' +
        dySubFormFieldEdit.notEdit +
        '" fieldName= "' +
        field.name +
        '"' +
        ' id="' +
        'editField_' +
        uuid +
        '"' +
        (typeof field.editable == 'undefined'
          ? field.showType != '1'
            ? 'checked'
            : ''
          : field.editable == dySubFormFieldEdit.notEdit
          ? 'checked'
          : '') +
        '/>' +
        '<label for="' +
        'editField_' +
        uuid +
        '"></label>' +
        '</td>';
      vTr +=
        '<td><input   type="checkbox" name="ctlField" value="' +
        dySubFormFieldCtl.control +
        '" fieldName= "' +
        field.name +
        '"' +
        ' id="' +
        'ctlField_' +
        uuid +
        '"' +
        (typeof field.controlable == 'undefined' ? '' : field.controlable == dySubFormFieldCtl.control ? 'checked' : '') +
        '/>' +
        '<label for="' +
        'ctlField_' +
        uuid +
        '"></label>' +
        '</td>';
      vTr +=
        '<td class="column-frozen"><input   type="checkbox" name="frozenField" fieldName= "' +
        field.name +
        '"' +
        ' id="' +
        'frozenField_' +
        uuid +
        (typeof field.frozen == 'undefined' ? '' : field.frozen ? 'checked' : '') +
        '/>' +
        '<label for="' +
        'frozenField_' +
        uuid +
        '"></label>' +
        '</td>';
      vTr +=
        '<td style="min-width:80px;"><input  type="radio" name="' +
        alignField +
        '" fieldName= "' +
        field.name +
        '"' +
        ' id="' +
        'alignField_' +
        uuid +
        '_left"' +
        (field.textAlign === 'left' ? 'checked' : '') +
        ' value="left" class="alignField"/>' +
        '<label for="' +
        'alignField_' +
        uuid +
        '_left' +
        '"/label></label>';
      vTr +=
        '<input  type="radio" name="' +
        alignField +
        '" fieldName= "' +
        field.name +
        '"' +
        ' id="' +
        'alignField_' +
        uuid +
        '_center"' +
        (field.textAlign === 'center' ? 'checked' : '') +
        ' value="center" class="alignField"/>' +
        '<label for="' +
        'alignField_' +
        uuid +
        '_center' +
        '"/label></label>';
      vTr +=
        '<input  type="radio" name="' +
        alignField +
        '" fieldName= "' +
        field.name +
        '"' +
        ' id="' +
        'alignField_' +
        uuid +
        '_right"' +
        (field.textAlign === 'right' ? 'checked' : '') +
        ' value="right" class="alignField"/>' +
        '<label for="' +
        'alignField_' +
        uuid +
        '_right' +
        '"/label></label>' +
        '</td>';
      vTr +=
        '<td ><textarea name="formula" fieldName= "' +
        field.name +
        '" placeholder="function formatter(value, row, index){}" ' +
        (formDefinitionMethod.isAttach(inputMode) ? ' disabled="disabled"' : '') +
        '>' +
        (formDefinitionMethod.isAttach(inputMode)
          ? ''
          : $.trim(field.formula) <= 0
          ? 'function formatter(value, row, index){}'
          : field.formula) +
        '</textarea></td>';

      vTr += '</tr>';

      $table.append(vTr);
    }
    // end tiem 2015-12-28 yuqy
    selectHTML += '</select>';
    $('.fieldMapping').each(function () {
      $(this).html(selectHTML);
      $(this).find('select').val($(this).parent().find('input').attr('fieldname')); // 设置被选中项
    });

    // $("#formTree").comboTree("hide");

    // 设置各字段行被选中时的颜色
    $('#formFieldsTbl .tr-td').unbind('click');
    $('#formFieldsTbl .tr-td')
      .die()
      .live('click', function () {
        $('#formFieldsTbl .tr-td').removeClass('item-active-background');
        $(this).addClass('item-active-background');
      });
  }

  function getInputValue($elem) {
    var valObj = {};
    valObj['inputField'] = $("input[name='inputField']", $elem).val();
    valObj['fieldSelect'] = $("select[name='fieldSelect']", $elem).val();
    valObj['widthField'] = $("input[name='widthField']", $elem).val();
    valObj['formula'] = $("textarea[name='formula']", $elem).val();
    valObj['hideField'] = $("input[name='hideField']:checked", $elem).size() == 1;
    valObj['editField'] = $("input[name='editField']:checked", $elem).size() == 1;
    valObj['ctlField'] = $("input[name='ctlField']:checked", $elem).size() == 1;
    valObj['frozen'] = $("input[name='frozenField']", $elem).prop('checked');
    valObj['textAlign'] = $('input.alignField:checked', $elem).val();
    return valObj;
  }

  function setInputValue($elem, valObj) {
    $("input[name='inputField']", $elem).val(valObj['inputField']);
    $("input[name='widthField']", $elem).val(valObj['widthField']);
    $("textarea[name='formula']", $elem).val(valObj['formula']);
    $("select[name='fieldSelect']", $elem).val(valObj['fieldSelect']);
    $("input[name='hideField']", $elem).attr('checked', valObj['hideField']);
    $("input[name='editField']", $elem).attr('checked', valObj['editField']);
    $("input[name='ctlField']", $elem).attr('checked', valObj['ctlField']);
    $("input[name='frozenField']", $elem).prop('checked', valObj['frozen']);
    $('input.alignField[value=' + valObj.textAlign + ']', $elem).prop('checked', true);
  }

  function setSubFormHeight(subFormHeight) {
    $('#subFormHeight').val(subFormHeight);
  }

  function setAutoWidth(autoWidth) {
    if (typeof autoWidth == 'undefined' || autoWidth == true || autoWidth == 'true') {
      $('#autoWidth').prop('checked', true);
    } else {
      $('#autoWidth').prop('checked', false);
    }
  }

  function getAutoWidth() {
    if ($('#autoWidth').prop('checked')) {
      return true;
    } else {
      return false;
    }
  }

  function setSupportSubRow(supportSubRow) {
    if (supportSubRow == true || supportSubRow == 'true') {
      $('#supportSubRow').prop('checked', true);
    } else {
      $('#supportSubRow').prop('checked', false);
    }
  }

  function setFixedHeader(fixedHeader) {
    if (fixedHeader == true || fixedHeader == 'true') {
      $('#fixedHeader').prop('checked', true);
    } else {
      $('#fixedHeader').prop('checked', false);
    }
  }

  function setShowImpButton(showImpButton) {
    if (showImpButton == true || supportSubRow == 'true') {
      $('#showImpButton').prop('checked', true);
    } else {
      $('#showImpButton').prop('checked', false);
    }
  }

  function setShowExpButton(showExpButton) {
    if (showExpButton == true || supportSubRow == 'true') {
      $('#showExpButton').prop('checked', true);
    } else {
      $('#showExpButton').prop('checked', false);
    }
  }

  function setShowClearButton(showClearButton) {
    if (showClearButton == true || showClearButton == 'true') {
      $('#showClearButton').prop('checked', true);
    } else {
      $('#showClearButton').prop('checked', false);
    }
  }

  function getSupportSubRow() {
    if ($('#supportSubRow').prop('checked')) {
      return true;
    } else {
      return false;
    }
  }

  function getFixedHeader() {
    if ($('#fixedHeader').prop('checked')) {
      return true;
    } else {
      return false;
    }
  }

  function getShowImpButton() {
    if ($('#showImpButton').prop('checked')) {
      return true;
    } else {
      return false;
    }
  }

  function getShowExpButton() {
    if ($('#showExpButton').prop('checked')) {
      return true;
    } else {
      return false;
    }
  }

  function getShowClearButton() {
    if ($('#showClearButton').prop('checked')) {
      return true;
    } else {
      return false;
    }
  }

  function setDefaultRowCount(count) {
    if (typeof count == 'undefined') {
      $('#defaultRowCount').val('0');
    } else {
      $('#defaultRowCount').val(count);
    }
  }

  function getDefaultRowCount() {
    var defaultRowCount = $('#defaultRowCount').val();
    if (defaultRowCount == '') {
      return '0';
    }
    return defaultRowCount;
  }

  function setShowRowCount(count) {
    $('#showRowCount').val(count);
  }

  function getShowRowCount() {
    return $('#showRowCount').val();
  }

  function setMultiSelect(MultiSelect) {
    if (MultiSelect == true || MultiSelect == 'true') {
      $("input[name='multiSelect'][value='1']").prop('checked', true);
    } else {
      $("input[name='multiSelect'][value='0']").prop('checked', true);
    }
  }

  function getMultiSelect() {
    if ($('#lineOperate').find('.switch-radio').data('status') == '1') {
      var val = $("input[name='multiSelect']:checked").val();
      if (val == '1') {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  window.upRow = function () {
    $('#formFieldsTbl .tr-td').each(function () {
      if ($(this).hasClass('item-active-background')) {
        var prevStr = $(this).prev().html();
        if ($(this).prev().attr('class') != 'tr-td') {
          // alert("已经是第一行！");
          return;
        } else {
          var prevInputValue = getInputValue($(this).prev());
          $(this).prev().remove();
          prevStr = "<tr class='tr-td' name='tr-td'>" + prevStr + '</tr>';
          $(this).after(prevStr);
          setInputValue($(this).next(), prevInputValue);
        }
      }
    });
  };

  window.downRow = function () {
    $('#formFieldsTbl .tr-td').each(function () {
      if ($(this).hasClass('item-active-background')) {
        var prevStr = $(this).next().html();
        if (prevStr == null) {
          // alert("已经是最后一行！");
          return;
        } else {
          var prevInputValue = getInputValue($(this).next());
          $(this).next().remove();
          prevStr = "<tr class='tr-td' name='tr-td'>" + prevStr + '</tr>';
          $(this).before(prevStr);
          setInputValue($(this).prev(), prevInputValue);
        }
      }
    });
  };

  window.deleteRow = function () {
    if ($('#formFieldsTbl .tr-td').length == 1) {
      alert('最后一行不得删除');
      return;
    }
    $('#formFieldsTbl .tr-td').each(function () {
      var t = $(this);
      if (t.hasClass('item-active-background')) {
        t.remove();
      }
    });
  };

  window.addRow = function () {
    // $('#formFieldsTbl .tr-td').css('background-color', '');
    $('#formFieldsTbl .tr-td').removeClass('item-active-background');

    var $table = $('#formFieldsTbl tbody');
    // modify time 2015-1-4 yuyq 默认选中新增行,新增行无默认数据
    // var vTr2 = $('#formFieldsTbl .tr-td').eq(0).clone(true).css('background-color', 'rgb(75, 154, 210)');
    var vTr2 = $('#formFieldsTbl .tr-td').eq(0).clone(true).addClass('item-active-background');

    $table.append(vTr2);
    var i = 0;
    $('#formFieldsTbl .tr-td').each(function () {
      var t = $(this);
      i = i + 1;
      // if (t.css('background-color') == 'rgb(75, 154, 210)') {
      //   var val = document.getElementById('formFieldsTbl').rows[i].cells[0];
      //   val.innerHTML = '';
      //   t.find("input[name='inputField']").val('');
      //   t.find("input[name='inputField']").focus();
      // }
      if (t.hasClass('item-active-background')) {
        var val = document.getElementById('formFieldsTbl').rows[i].cells[0];
        val.innerHTML = '';
        t.find("input[name='inputField']").val('');
        t.find("input[name='inputField']").focus();
      }
    });
    // end time 2015-1-4 yuyq
  };

  // 14、UUID
  function UUID() {}

  // 14.1生成UUID
  createUUID = function () {
    var dg = new Date(1582, 10, 15, 0, 0, 0, 0);
    var dc = new Date();
    var t = dc.getTime() - dg.getTime();
    var tl = this.getIntegerBits(t, 0, 31);
    var tm = this.getIntegerBits(t, 32, 47);
    var thv = this.getIntegerBits(t, 48, 59) + '1';
    var csar = this.getIntegerBits(this.rand(4095), 0, 7);
    var csl = this.getIntegerBits(this.rand(4095), 0, 7);
    var n =
      this.getIntegerBits(this.rand(8191), 0, 7) +
      this.getIntegerBits(this.rand(8191), 8, 15) +
      this.getIntegerBits(this.rand(8191), 0, 7) +
      this.getIntegerBits(this.rand(8191), 8, 15) +
      this.getIntegerBits(this.rand(8191), 0, 15);
    return tl + tm + thv + csar + csl + n;
  };
  // 14.2
  getIntegerBits = function (val, start, end) {
    var base16 = this.returnBase(val, 16);
    var quadArray = new Array();
    var quadString = '';
    var i = 0;
    for (i = 0; i < base16.length; i++) {
      quadArray.push(base16.substring(i, i + 1));
    }
    for (i = Math.floor(start / 4); i <= Math.floor(end / 4); i++) {
      if (!quadArray[i] || quadArray[i] == '') quadString += '0';
      else quadString += quadArray[i];
    }
    return quadString;
  };
  // 14.3
  returnBase = function (number, base) {
    return number.toString(base).toUpperCase();
  };
  // 14.4
  // 生成一个大于等于0小于等于max的随机数
  rand = function (max) {
    return Math.floor(Math.random() * (max + 1));
  };

  window.deleteBtnRow = function () {
    if ($('#formButtonTbl .tr-td').length == 1) {
      alert('最后一行不得删除');
      return;
    }
    $('#formButtonTbl .tr-td').each(function () {
      var t = $(this);
      if (t.find('input[name="isCheck"]').prop('checked')) {
        t.remove();
      }
    });
  };

  var positionTpl =
    '<div class="checklist"><label><input type="checkbox" value="1" id="position1"><label for="position1" style="margin:0;">头部</label></label><label><input type="checkbox" value="2" id="position2"><label for="position2" style="margin:0;">行末</label></label><label><input type="checkbox" value="3" id="position3"><label for="position3" style="margin:0;">尾部</label></label></div>';
  var defaultButtonsCode = [
    'btn_add',
    'btn_edit',
    'btn_copy',
    'btn_del',
    'btn_clear',
    'btn_up',
    'btn_down',
    'btn_add_sub',
    'btn_exp_subform',
    'btn_imp_subform'
  ];
  function initButtonTableRow(tableButtonInfo) {
    console.log(tableButtonInfo);
    var addTduuid = createUUID();
    tableButtonInfo.forEach(function (tableButtonClass) {
      var tableButtonUUID = tableButtonClass.uuid;
      $('#formButtonTbl .tr-td').css('background-color', '');
      var $table = $('#formButtonTbl tbody');
      var $tr = $('<tr>', {
        class: 'tr-td',
        style: 'height:30px;'
      });

      var $position = $(positionTpl);
      // 设置label  和 id
      $($position.find("input[type='checkbox']")[0])
        .attr('id', 'position1_' + tableButtonUUID)
        .next('label')
        .attr('for', 'position1_' + tableButtonUUID);

      $($position.find("input[type='checkbox']")[1])
        .attr('id', 'position2_' + tableButtonUUID)
        .next('label')
        .attr('for', 'position2_' + tableButtonUUID);

      $($position.find("input[type='checkbox']")[2])
        .attr('id', 'position3_' + tableButtonUUID)
        .next('label')
        .attr('for', 'position3_' + tableButtonUUID);

      $tr.append(
        $('<td>', {
          class: 'th'
        }).append(
          $('<input>', {
            name: 'isCheck',
            type: 'checkbox',
            disabled: defaultButtonsCode.indexOf(tableButtonClass.code) > -1 ? true : false,
            id: addTduuid
          }),
          $('<label>', {
            for: addTduuid
          })
        ),
        $('<td>', {
          class: 'th',
          style: 'min-width:60px'
        }).append(
          $('<input>', {
            name: 'btnName',
            type: 'text',
            value: tableButtonClass.text
          })
        ),
        $('<td>', {
          class: 'th'
        }).append(
          defaultButtonsCode.indexOf(tableButtonClass.code) > -1
            ? $('<div>', {
                name: 'btnCode',
                text: tableButtonClass.code
              })
            : $('<input>', {
                name: 'btnCode',
                type: 'text',
                value: tableButtonClass.code
              })
        ),
        $('<td>', {
          class: 'th'
        }).append(
          $('<div>', {
            name: 'btnType',
            text: defaultButtonsCode.indexOf(tableButtonClass.code) > -1 ? '内置按钮' : '扩展按钮'
          })
        ),
        $('<td>', {
          class: 'th'
        }).append(
          defaultButtonsCode.indexOf(tableButtonClass.code) > -1
            ? $('<div>', {
                name: 'btnOperate',
                text: tableButtonClass.code == 'btn_exp_subform' ? '显示类操作' : '编辑类操作'
              })
            : $('<select>', {
                name: 'btnOperate'
              })
                .append(
                  $('<option>', {
                    text: '编辑类操作',
                    value: 'edit',
                    selected: tableButtonClass.operate == 'edit' ? 'true' : false
                  })
                )
                .append(
                  $('<option>', {
                    text: '显示类操作',
                    value: 'show',
                    selected: tableButtonClass.operate == 'show' ? 'true' : false
                  })
                )
        ),
        $('<td>', {
          class: 'th'
        }).append($position),
        $('<td>', {
          class: 'th event-position',
          style: 'min-width:70px'
        })
          .append(
            $('<input>', {
              name: 'uuid',
              type: 'hidden',

              value: tableButtonClass.uuid
            })
          )
          .append(
            $('<input>', {
              name: 'btnEvent',
              type: 'text',
              'data-code': tableButtonClass.code,
              id: tableButtonClass.uuid,
              value: tableButtonClass.displayText
            })
          ),
        $('<td>', {
          class: 'th',
          style: 'min-width:80px'
        }).append(
          $('<input>', {
            name: 'btnClass',
            type: 'text',
            value: tableButtonClass.cssClass
          })
        )
      );
      $table.append($tr);
      $.each(tableButtonClass.position || [], function (idx, value) {
        $position.find("input[value='" + value + "']").prop('checked', true);
      });
      $('#' + tableButtonClass.uuid).data('defineEventJs', tableButtonClass.buttonEvents);
    });
  }

  function initButtionTableEvent(tableButtonInfo) {
    var $buttonInfo = $('#formButtonTbl');
    tableButtonInfo.forEach(function (tableButtonClass) {
      var uuid = tableButtonClass.uuid;
      $('#' + uuid, $buttonInfo)
        .off('click')
        .on('click', function () {
          var $container = $('#btnEventContainer');
          $container.empty();
          var defineEventJs = $(this).data('defineEventJs');
          var data = {
            defineEventJs: defineEventJs,
            uuid: uuid
          };
          var code = $(this).data('code');
          getBtnEventDialogHtml($container, defineEventJs, defaultButtonsCode.indexOf(code) > -1);
          $container.data('data', data).dialog({
            autoOpen: true,
            height: 600,
            width: 860,
            modal: true,
            title: '按钮事件',
            zIndex: 20001,
            beforeClose: function () {
              $container.empty();
            },
            open: function () {
              if (defaultButtonsCode.indexOf(code) > -1) {
                $container.siblings('.ui-dialog-buttonpane').remove();
              }

              $('#defineEventJs button').each(function () {
                $(this)
                  .off('click')
                  .on('click', function () {
                    $('.eventCodeInputDiv').find('.codeEditDiv').hide();
                    $('.eventCodeInputDiv')
                      .find('#' + $(this).attr('_target'))
                      .show();
                    $('#eventBtnGroup .btn-primary').removeClass('btn-primary');
                    $(this).addClass('btn-primary');
                  });
              });
            },
            buttons: {
              确定: function () {
                var buttonEvent = {};
                var displayNames = '';
                var buttonName = {
                  mouseover: '鼠标移入事件',
                  mouseout: '鼠标移出事件',
                  init: '初始化事件',
                  click: '鼠标点击事件'
                };
                $('#defineEventJs')
                  .find('pre')
                  .each(function (i, v) {
                    var id = $(this).attr('id');
                    var text = $(this).data('editor').getValue();
                    var displayName = buttonName[id];
                    buttonEvent[id] = text;
                    if (!_.isEmpty(text)) {
                      displayNames += displayName + ';';
                    }
                  });
                $('#' + uuid).data('defineEventJs', buttonEvent);
                $('#' + uuid).val(displayNames);
                $(this).dialog('close');
              },
              取消: function () {
                $(this).dialog('close');
              }
            }
          });
        });
    });
  }

  window.addBtnRow = function () {
    // var $buttonInfo = $("#formButtonTbl");
    var uuid = createUUID();
    var tableButtonClass = new TableButtonClass();
    tableButtonClass.uuid = uuid;
    var tableButtonInfo = [];
    tableButtonInfo.push(tableButtonClass);
    initButtonTableRow(tableButtonInfo);
    initButtionTableEvent(tableButtonInfo);
  };

  function getBtnEventDialogHtml($container, defineEventJs, bDefaultButtons) {
    // 自定义事件脚本
    var $div = $('<div>', {
      class: 'form-group formbuilder clear',
      id: 'defineEventJs'
    });
    $div.append(
      $('<div>', {
        class: 'col-xs-3 controls',
        style: 'float: left;width: 20%;'
      }),
      $('<div>', {
        class: 'col-xs-9 controls eventCodeInputDiv',
        style: 'float: left;width: 70%;'
      })
    );
    $container.append($div);
    $container.data('aceCoderPlugin', {});
    $('#defineEventJs')
      .find('div:eq(0)')
      .append(
        $('<div>', {
          class: 'btn-group-vertical',
          role: 'group',
          id: 'eventBtnGroup'
        }).append(
          $('<button>', {
            type: 'button',
            class: 'btn btn-primary btn-sm',
            style: 'width:100px;margin-top:1px',
            _target: 'initEvent'
          }).text('初始化事件'),
          $('<button>', {
            type: 'button',
            class: 'btn btn-sm',
            style: 'width:100px;margin-top:1px',
            _target: 'clickEvent'
          }).text('鼠标点击事件'),
          $('<button>', {
            type: 'button',
            class: 'btn btn-sm',
            style: 'width:100px;margin-top:1px',
            _target: 'mouseOverEvent'
          }).text('鼠标移入事件'),
          $('<button>', {
            type: 'button',
            class: 'btn btn-sm',
            style: 'width:100px;margin-top:1px',
            _target: 'mouseOutEvent'
          }).text('鼠标移出事件')
        )
      );
    $('#defineEventJs')
      .find('.eventCodeInputDiv')
      .append(
        $('<div>', {
          id: 'initEvent',
          class: 'codeEditDiv'
        }).append(
          $('<pre>', {
            style: 'min-height:200px',
            id: 'init'
          })
        ),
        $('<div>', {
          id: 'clickEvent',
          style: 'display:none',
          class: 'codeEditDiv'
        }).append(
          $('<pre>', {
            style: 'min-height:200px',
            id: 'click'
          })
        ),
        $('<div>', {
          id: 'mouseOverEvent',
          style: 'display:none',
          class: 'codeEditDiv'
        }).append(
          $('<pre>', {
            style: 'min-height:200px',
            id: 'mouseover'
          })
        ),
        $('<div>', {
          id: 'mouseOutEvent',
          style: 'display:none',
          class: 'codeEditDiv'
        }).append(
          $('<pre>', {
            style: 'min-height:200px',
            id: 'mouseout'
          })
        )
      );
    // 代码输入框初始化
    $('#defineEventJs')
      .find('pre')
      .each(function () {
        var ID = $(this).attr('id');
        var text = '';
        if (_.isObject(defineEventJs)) {
          text = defineEventJs[ID];
        }
        var $ace = $.fn.aceBinder({
          id: 'aceCodeEditor' + ID,
          container: '#' + ID,
          iframeId: 'scriptContentIframe' + ID,
          value: text,
          codeHis: {
            enable: true,
            relaBusizUuid: $('#tableId').val() + '.' + $('#formTree').attr('formOuterId'), // 主表单+从表单
            codeType: 'dysubform.' + ID
          },
          readOnly: bDefaultButtons
        });
        $(this).data('editor', $ace);
      });
  }

  $("input[name='hideButtons']").click(function () {
    // alert($(this).val());
    if ($(this).val() == '1' || $(this).val() == 1) {
      $('#showExpButton').attr('disabled', false);
      $('#showImpButton').attr('disabled', false);
    }
    if ($(this).val() == '2' || $(this).val() == 2) {
      setShowImpButton(false);
      setShowExpButton(false);
      $('#showExpButton').attr('disabled', true);
      $('#showImpButton').attr('disabled', true);
    }
  });

  /**
   * 初始化事件属性值
   *
   * @param {Object}
   *            field
   */
  function initEventProperty(subformDefinition) {
    initEventTree(subformDefinition);
  }

  /**
   * 初始化事件树
   */
  function initEventTree(subformDefinition) {
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

    var topNodeIdOfControlEvents = 'control_events'; // 控件事件
    var topNodeIdOfCustomEvents = 'custom_events'; // 自定义事件

    var eventCallback = function (param) {
      var script = param.value; // $(this).val();
      var nodes = treeObj.getSelectedNodes();
      for (var i = 0; i < nodes.length; i++) {
        var treeNode = nodes[i];
        if ($.trim(script).length > 0) {
          checkEventNode(treeObj, treeNode.id, true);
          $('#scriptContent').data(treeNode.id, script);
        } else {
          checkEventNode(treeObj, treeNode.id, false);
          $('#scriptContent').data(treeNode.id, null);
        }
      }
    };
    this.aceCoderPlugin = {};

    var createAceCodeEditor = function (node) {
      $('#scriptContent').append(
        $('<div>', {
          id: 'dysubformEventCodeContainer_' + node.id
        })
      );

      _this.aceCoderPlugin[node.id] = $.fn.aceBinder({
        container: '#dysubformEventCodeContainer_' + node.id,
        id: node.id,
        iframeId: 'dysubformScriptContentIframe_' + node.id,
        value: subformDefinition.events[node.id],
        codeHis: {
          enable: true,
          relaBusizUuid: $('#tableId').val() + '.' + $('#formTree').attr('formOuterId'), // 主表单+从表单
          codeType: 'dysubform.' + node.id
        },
        valueChange: function (v) {
          eventCallback.call(_this, {
            value: v,
            $ace: this
          });
        }
      });

      _this.aceCoderPlugin[node.id].getContainer().hide();
    };

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
        id: topNodeIdOfCustomEvents,
        pId: 0,
        name: '自定义事件',
        open: true,
        // chkDisabled : true,
        isParent: true,
        nocheck: true
      }
    ];

    var zControlEventNodes = getControlEvents();
    for (var i = 0; i < zControlEventNodes.length; i++) {
      zControlEventNodes[i].pId = topNodeIdOfControlEvents;
      createAceCodeEditor(zControlEventNodes[i]);
    }

    var treeObj = $.fn.zTree.init($('#eventTree'), setting, [].concat(zRootNodes).concat(zControlEventNodes));

    // 缓存已有事件信息
    var events = subformDefinition.events;
    if (events) {
      for (var i = 0; i < zControlEventNodes.length; i++) {
        var controlEvent = zControlEventNodes[i];
        var id = controlEvent.id;
        if (StringUtils.isNotBlank(events[controlEvent.id])) {
          $('#scriptContent').data(id, events[id]);
          checkEventNode(treeObj, id, true);
        }
      }
    }

    function zTreeOnDblClick(event, treeId, treeNode) {
      // 双击后，从缓存里读取事件信息
      $("div[id^='dysubformEventCodeContainer_']").hide();
      if (!treeNode || treeNode.isParent) {
        return;
      }
      var id = treeNode.id;
      _this.aceCoderPlugin[id].getContainer().show();
      var script = $('#scriptContent').data(id);
      if (StringUtils.isNotBlank(script)) {
        checkEventNode(treeObj, id, true);
      } else {
        checkEventNode(treeObj, treeNode.id, false);
      }
    }
  }

  function checkEventNode(treeObj, eventNodeId, flag) {
    var treeNode = treeObj.getNodeByParam('id', eventNodeId, null);
    treeObj.setChkDisabled(treeNode, false);
    treeObj.checkNode(treeNode, flag, false, false);
    treeObj.setChkDisabled(treeNode, true);
  }

  function getControlEvents() {
    return [
      { id: 'afterFillFormData', chkDisabled: true, name: 'afterFillFormData' },
      { id: 'beforeInsertRow', chkDisabled: true, name: 'beforeInsertRow' },
      { id: 'afterInsertRow', chkDisabled: true, name: 'afterInsertRow' },
      { id: 'beforeDeleteRow', chkDisabled: true, name: 'beforeDeleteRow' },
      { id: 'afterDeleteRow', chkDisabled: true, name: 'afterDeleteRow' },
      { id: 'afterEditCell', chkDisabled: true, name: 'afterEditCell' },
      { id: 'onSelectRow', chkDisabled: true, name: 'onSelectRow' },
      { id: 'onCellSelect', chkDisabled: true, name: 'onCellSelect' },
      { id: 'onClickRow', chkDisabled: true, name: 'onClickRow' },
      { id: 'onDblClickRow', chkDisabled: true, name: 'onDblClickRow' },
      { id: 'onClickCell', chkDisabled: true, name: 'onClickCell' }
    ];
  }

  function collectEvents(subform) {
    // var treeObj = $.fn.zTree.getZTreeObj("eventTree");
    var zControlEventNodes = getControlEvents();
    var events = {};
    for (var i = 0; i < zControlEventNodes.length; i++) {
      var controlEvent = zControlEventNodes[i];
      var script = $('#scriptContent').data(controlEvent.id);
      if (StringUtils.isNotBlank(script)) {
        events[controlEvent.id] = script;
      }
    }
    return events;
  }

  function loadDrawiFormTree() {
    var $formTree = $('#openFormTree');
    $formTree.wSelect2({
      serviceName: 'dyFormFacade',
      queryMethod: 'queryAllForms',
      selectionMethod: 'getSelectedFormDefinition',
      labelField: 'displayName',
      valueField: 'openFormTree',
      defaultBlank: false,

      params: {
        tableName: $('#formTree').attr('formname')
      }
    });

    $formTree.on('select2-open', function () {
      $('#select2-drop-mask').css('z-index', '10011');
      $('#select2-drop').css('z-index', '10011');
    });
    $formTree.on('select2-close', function () {
      $('#select2-drop-mask').css('z-index', '10001');
      $('#select2-drop').css('z-index', '10001');
    });
    $formTree.on('change', function () {});
  }

  function initEditModeEvent() {
    var $editMode = $("input[name='editMode']");
    $editMode
      .on('change', function () {
        if ($(this).is(':checked')) {
          if ($(this).val() == '2') {
            $('.td_editableMode').hide();
            $('#openFormTreeLabel').show();
          } else {
            $('.td_editableMode').show();
            $('#openFormTreeLabel').hide();
          }
        }
      })
      .trigger('change');
  }
  // 有两种方法可进入该对话框
  // 1:通过点击工具栏的插入从表,如果通过这种方式，需要先判断焦点的位置，不得在从表所在的位置再创建另一个从表
  // 2:通过双击ckeditor编辑框内的从表元素
  controlConfig.pluginName = typeof CkPlugin == 'undefined' ? 'dysubform' : CkPlugin.SUBFORM;
  addPlugin(controlConfig.pluginName, '插入从表', '设置从表', controlConfig);
})();
