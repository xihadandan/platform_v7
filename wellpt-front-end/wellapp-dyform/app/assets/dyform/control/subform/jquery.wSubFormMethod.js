/**
 * ] * 从表操作的一些方法 增删行、更新行操作等
 */
define(['uuid', 'commons', 'bootstrapTable'], function (uuidUtils, commons, bt) {
  (function ($) {
    var BootstrapTable = $.fn.bootstrapTable.Constructor;
    // 删除行
    BootstrapTable.prototype.remove = function (params) {
      this.showLoading();
      var _subTimer = setTimeout(() => {
        var len = this.options.data.length,
          i,
          row;
        if (!params.hasOwnProperty('field') || !params.hasOwnProperty('values')) {
          return;
        }

        for (i = len - 1; i >= 0; i--) {
          row = this.options.data[i];

          if (!row.hasOwnProperty(params.field)) {
            continue;
          }
          if ($.inArray(row[params.field], params.values) !== -1) {
            this.options.data.splice(i, 1);
          }
        }

        if (len === this.options.data.length) {
          return;
        }

        this.initSearch();
        this.initPagination();
        this.initSort();
        this.initBody(true);
        $.wSubFormMethod.setPagination();
        clearTimeout(_subTimer);
      }, 10);
    };

    // 切换分页数
    BootstrapTable.prototype.onPageListChange = function (event) {
      this.showLoading();
      var _subTimer = setTimeout(() => {
        var $this = $(event.currentTarget);

        $this.parent().addClass('active').siblings().removeClass('active');
        this.options.pageSize =
          $this.text().toUpperCase() === this.options.formatAllRows().toUpperCase() ? this.options.formatAllRows() : +$this.text();
        this.$toolbar.find('.page-size').text(this.options.pageSize);

        this.updatePagination(event);
        this.hideLoading();
        // $.wSubFormMethod.setPagination(this.options.pageSize, this.data);
        clearTimeout(_subTimer);
      }, 10);
    };

    // 翻页
    BootstrapTable.prototype.onPageNumber = function (event) {
      this.showLoading();
      var $this = $(event.currentTarget);
      $this.addClass('active').siblings().removeClass('active');
      var _subPageNumberTimer = setTimeout(() => {
        if (this.options.pageNumber === +$(event.currentTarget).text()) {
          return;
        }
        this.options.pageNumber = +$(event.currentTarget).text();
        this.updatePagination(event);
        this.hideLoading();
        clearTimeout(_subPageNumberTimer);
      }, 10);
    };

    // 跳页
    BootstrapTable.prototype.onPageJump = function (event) {
      var pagenum = $.trim(this.$pagination.find('.page-jump-number').val());
      if (/^[1-9]\d*$/.test(pagenum)) {
        this.options.pageNumber = parseInt(pagenum);
        this.updatePagination(event);
      } else {
        return false;
      }
    };
    $.fn.bootstrapTable.locales['zh-CN'] = {
      formatLoadingMessage: function () {
        return '数据加载中，请稍候……';
      },
      formatRecordsPerPage: function (pageNumber) {
        return pageNumber;
      },
      formatShowingRows: function (pageFrom, pageTo, totalRows, totalPages) {
        totalPages = totalPages ? totalPages : 0;
        return '共' + totalPages + '页/' + totalRows + '条记录';
      },
      formatSearch: function () {
        return '搜索';
      },
      formatNoMatches: function () {
        return '没有找到匹配的记录';
      },
      formatPaginationSwitch: function () {
        return '隐藏/显示分页';
      },
      formatRefresh: function () {
        return '刷新';
      },
      formatToggle: function () {
        return '切换';
      },
      formatColumns: function () {
        return '列';
      },
      formatExport: function () {
        return '导出数据';
      },
      formatClearFilters: function () {
        return '清空过滤';
      },
      formatePageSize: function (pageSize) {
        return pageSize + '条/页';
      }
    };
    $.extend($.fn.bootstrapTable.defaults, $.fn.bootstrapTable.locales['zh-CN']);
  })(jQuery);

  var rowCheckItem = 'rowCheckItem';
  var _subformCfg = {};
  $.fn.bootstrapTable.methods.push('updateRows');
  $.wSubFormMethod = {
    init: function () {
      var _this = this;
      var _paranentthis = _this.options.$paranentelement;
      var _tableelement = _this.get$PlaceHolder().empty();
      var ns = _paranentthis.attr('data-ns');
      if ($.trim(ns).length) {
        _this.options.namespace = ns;
      }
      // 初始化
      var formUuid = _this.options.formUuid;
      // console.log(this.options.formUuid,this.options.formDefinition.subforms[formUuid];)
      // 获取从表定义
      var subform = _this.getSubformConfig();
      _subformCfg[subform.formUuid] = subform;
      var subFormDefinition = _this.options.subformDefinition; // 加载从表对应的那张表的完整定义
      if (subform == null || subFormDefinition == null) {
        // 在定义中找不到该表单
        _tableelement.remove(); // 找不到定义就直接删除
        return true;
      }

      // 从表的table的id属性值为formId,
      _tableelement
        .attr({
          formId: subform.outerId
        })
        .css({
          'user-select': ''
        })
        .removeAttr('title');
      // 行紧凑显示
      _tableelement.addClass('table-condensed dyform-subtable');

      // 收集从表列信息
      var colModels = []; // 各列的配置信息

      // 序号
      // 提取为变量,后面更新frozen属性（有固定列,则序号也固定）
      var seqNOColModel = {
        field: 'seqNO',
        index: 'seqNO',
        title: '序号',
        width: '45px',
        sortable: false,
        fixed: true,
        align: 'center',
        valign: 'middle',
        class: 'bs-seqno',
        editable: false,
        visible: !subform.hideSeqNo,
        formatter: function (value, row, index) {
          return index + 1;
        }
      };
      var multiselect = subform.multiselect === true;
      if ($.isArray(subform.editableOperateBtns) && subform.editableOperateBtns.length > 0) {
        subform.hideButtons = dySubFormHideButtons.show;
      }
      if (subform.lineSelection != '0') {
        colModels.push({
          width: '36px',
          field: rowCheckItem,
          radio: !multiselect,
          checkbox: multiselect,
          visible: subform.hideButtons !== dySubFormHideButtons.hide
        });
      }

      colModels.push(seqNOColModel);

      var fields = subform.fields;

      for (var i in fields) {
        var field = fields[i];
        var fieldDef = subFormDefinition.fields[field.name];
        if (fieldDef == undefined) {
          throw new Error('从表[' + subform.displayName + '(' + subFormDefinition.name + ')]中不存在[字段' + field.name + ']的定义');
        }
        var fieldCheckRules = fieldDef.fieldCheckRules;
        if (fieldDef.inputMode == '6' || fieldDef.inputMode == '4' || fieldDef.inputMode == '33') {
          field.editable = '1';
        }
        field.isRequired =
          $.isArray(fieldCheckRules) &&
          fieldCheckRules.some(function (item) {
            return item.value == '1';
          });
        var model = _this.getColModel(fields[field.name], subform, ns);
        if ((model.sortable = !!field.sort)) {
          model.sortorder = field.sort.indexOf('asc') < 0 ? 'desc' : 'asc'; // 是否支持点击排序
        }
        model.visible = _this.isFieldHidden(field) === false;
        model.controlable = field.controlable && field.controlable == dySubFormFieldCtl.control ? true : false;

        if ((model.frozen = field.frozen) === true) {
          seqNOColModel.frozen = true;
        }
        model.inputMode = fieldDef.inputMode;

        _this.setModelWidth(model, field.width);
        _this.setEditableMode(model, field, subform, ns);
        if (false === model.visible) {
          // fieldDef.showType = "5";
        } else if (false === model.editable.editable) {
          fieldDef.showType = fieldDef.showType === '2' ? '2' : '3';
        } else if (model.controlable || model.editable.editable) {
          fieldDef.showType = '1';
        } else if (fieldDef.showType == '5') {
          // 控件隐藏设置为可编辑
          fieldDef.showType = '1';
        }

        // 附件控件 flowSecDevBtnIdStr
        if (field.flowSecDevBtnIdStr) {
          fieldDef.flowSecDevBtnIdStr = field.flowSecDevBtnIdStr;
        }

        model.editable.control = {
          controlable: model.controlable,
          subformConfig: subform,
          fieldDefinition: fieldDef,
          formDefinition: subFormDefinition,
          $currentForm: _this.$thisFormContext()
        };
        colModels.push(model);
        // 添加运算公式
        var formula = $.trim(field.formula);
        if (typeof formula === 'string' && formula.indexOf('function') === 0) {
          try {
            model.editable.subform = _this;
            model.editable.formatter = new Function('return ' + formula)();
            if (_this.options.groupField === field.name) {
              _this.options.groupByFormatter = model.editable.formatter;
            }
            formula = null;
          } catch (ex) {
            // 兼容旧方式
          }
        }
        if (typeof formula != 'undefined' && $.trim(formula).length > 0) {
          _this.addFormula(field.name, formula);
        }
      }

      var headerBtns = [],
        footerBtns = [],
        inlineBtns = [],
        codeButtonEvents = {};
      // 从表的标题
      var caption = _this.genDisplayNameHtml();
      headerBtns.push(caption);
      var lineButtonShow = subform.hideButtons !== dySubFormHideButtons.hide;
      $.each(subform.tableButtonInfo || [], function (idx, button) {
        if (false === _this.isGroupForm() && button.code === 'btn_add_sub') {
          // 非分组忽略添加子行
          return;
        } else if (subform.editMode !== '2' && button.code === 'btn_edit') {
          // 编辑模式不适合“当前单元格”（适合“新窗口”编辑）
          return;
        }
        var positions = button.position;
        if (positions == null || typeof positions === 'undefined') {
          positions = ['1']; // 兼容旧配置
        }
        var buttonHtml = new commons.StringBuilder();
        var btnClass = StringUtils.isBlank(button.cssClass) ? 'btn-primary' : button.cssClass + ' ' + button.cssStr;
        if (button.icon && button.icon.className) {
          btnClass += ' ' + button.icon.className;
        }
        btnClass += ' btn-customs';
        var style = '';
        if (_this.defaultOperateButtonConfig[button.code]) {
          if (subform.editableOperateBtns && subform.editableOperateBtns.indexOf(button.code) == -1) {
            // 非从表设置的操作按钮,不可编辑的情况下
            style = 'display:none;';
          }
        } else {
          if (subform.editableOperateBtns && subform.editableOperateBtns.indexOf(button.code) > 0) {
          } else {
            // 非从表默认操作按钮,不可编辑的情况下
            style = 'display:none;';
          }
        }
        buttonHtml.appendFormat(
          "<button type='button' class='btn {0} {1}' action='{1}' title='{2}' style='{4}'>{3}</button>",
          btnClass,
          button.code,
          button.text,
          button.text,
          style
        );
        // 头部按钮
        if ($.inArray('1', positions) != -1) {
          headerBtns.push(buttonHtml.toString());
        }
        // 行内按钮
        if ($.inArray('2', positions) != -1 && subform.editableOperateBtns && subform.editableOperateBtns.indexOf(button.code) > -1) {
          inlineBtns.push(buttonHtml.toString());
          if (!lineButtonShow && subform.editableOperateBtns && subform.editableOperateBtns.indexOf(button.code) != -1) {
            lineButtonShow = true;
          }
        }
        // 底部按钮
        if ($.inArray('3', positions) != -1) {
          footerBtns.push(buttonHtml.toString());
        }
        codeButtonEvents[button.code] = button.buttonEvents;
      });
      subform.codeButtonEvents = codeButtonEvents;
      if (inlineBtns.length > 0) {
        var buttonWidth = 0;
        var $inlineBtn = $('<div>').append(inlineBtns.join(''));
        $inlineBtn.find('button.btn').each(function () {
          var $this = $(this);
          $this.addClass('btn-sm');
          var words = $.trim($this.text()).length;
          buttonWidth += words * 14 + 10 * 2 + 2 + 8;
          // words*fontsize+panding*2+border*2+marginright
        });
        var inlineBtnHtml = $inlineBtn.html();
        colModels.push({
          field: '_inline_operate',
          index: '_inline_operate',
          title: '操作',
          sortable: false,
          fixed: true,
          width: buttonWidth,
          class: 'btn-inline',
          visible: lineButtonShow,
          formatter: function (value, row, index) {
            var buttonToolbar = new commons.StringBuilder();
            buttonToolbar.appendFormat(
              "<div class='btn-inline bt-inline-toolbar' style='display:inline-block;' index='{0}' data-rowid={2}>{1}</div>",
              index,
              inlineBtnHtml,
              row['id']
            );
            // 行按钮格式化
            return buttonToolbar.toString();
          }
        });
      }

      // 行ID,dataUuid
      colModels.push({
        field: 'id',
        index: 'id',
        title: 'id',
        visible: false
      });
      var tableOuterTdWidth = _this.$element.width();
      if (subform.horizontalScroll) {
        var tableOuterTdWidth = _this.countGridWidth(_this.$element);
      }
      if (!subform.pagination) {
        subform.pagination = {};
      }
      var bootstrapOptions = {
        groupBy: _this.isGroupForm(),
        groupByField: _this.options.groupField,
        groupByFormatter: function (name, id, data) {
          if (subform.isGroupColumnShow == '1') {
            //分组字段是否显示:是
            return name;
          } else if (subform.isGroupColumnShow == '2') {
            //分组字段是否显示:否
            return '';
          }
          return $.isFunction(_this.options.groupByFormatter) ? _this.options.groupByFormatter.apply(this, arguments) : name;
        },
        updateById: true,
        uniqueId: 'id',
        idField: 'id',
        singleSelect: !subform.multiselect,
        maintainSelected: subform.multiselect,
        toolbar: headerBtns.join(''),
        sidePagination: 'client',
        smartDisplay: false,
        pagination: !!subform.pagination.isShow,
        pageSize: subform.pagination.defPageSize,
        // pageList: [5,10,15,20,50,100],
        pageList: subform.pagination.pageList,
        jumpPage: subform.pagination.jumper,
        hideTotalPageText: '',
        parentIdField: '',
        height: subform.subFormHeight ? subform.subFormHeight : 'auto',
        maxHeight: subform.subFormHeight ? subform.subFormHeight : '',
        stackSeqNo: !!subform.stackSeqNo,
        columns: colModels,
        escape: true,
        resizable: false,
        showHeader: true,
        fixInitBody: true,
        hidePageList: false,
        hideInvisible: true, // 隐藏域创建控件
        syncHeaderWidth: 'async',
        // fixedColumns: true,
        // fixedNumber: 1,
        tableOuterTdWidth: tableOuterTdWidth,
        horizontalScroll: subform.horizontalScroll,
        onClickRow: function (row, $element, field, origEvent) {
          var item = row;
          if (field === '_inline_operate' || field === rowCheckItem) {
            return;
          } else if (origEvent.isTrigger) {
            return;
          }
          _this.invoke('onClickRow', item, $element);
          _this.runJsCode('onClickRow', {
            row: row,
            item: item,
            $element: $element
          });
        },
        onDblClickRow: function (row, $element, field, origEvent) {
          var item = row;
          if (field === '_inline_operate' || field === rowCheckItem) {
            return;
          } else if (origEvent.isTrigger) {
            return;
          }

          _this.invoke('onDblClickRow', item, $element);
          _this.runJsCode('onDblClickRow', {
            row: row,
            item: item,
            $element: $element
          });
        },
        onClickCell: function (field, value, row, $element, origEvent) {
          if ($element && ($element.is('.bt-inline') || $element.is('.bs-checkbox'))) {
            return;
          } else if (origEvent.isTrigger) {
            return;
          }
          _this.invoke('onClickCell', field, value, row, $element);
          _this.runJsCode('onClickCell', {
            row: row,
            field: field,
            value: value,
            $element: $element
          });
        },
        onSort: function (name, order) {},
        onPostBody: function (data) {},
        rowStyle: function (row, index) {
          var style = {};
          style.classes = (_this.options.rowClass || []).join(' ');
          return style;
        },
        onEditableSave: function (field, row, oldValue, $el) {
          if (oldValue !== undefined && oldValue != row[field]) {
            // bug#53285
            setTimeout(function () {
              var _value = row[field];
              _this.invoke('afterEditCell', row, field, _value, oldValue, $el);
              _this.runJsCode('afterEditCell', {
                row: row,
                field: field,
                value: _value,
                oldValue: oldValue,
                $element: $el
              });
            }, 1);
          }
        }
      };
      if (_this.isGroupForm()) {
        colModels.push({
          field: 'parent_uuid',
          index: 'parent_uuid',
          title: 'parent_uuid',
          visible: false
        });
        // bootstrapOptions.treeView = true;
        bootstrapOptions.parentIdField = 'parent_uuid';
        bootstrapOptions.treeNodeDisplayField = _this.options.groupField;
      }
      // 替换组合表头
      if ($.isArray(subform.tableHeader) && subform.tableHeader.length > 0) {
        function getField(fieldName) {
          for (var i = 0, len = colModels.length; i < len; i++) {
            var colModel = colModels[i];
            if (colModel.field === fieldName) {
              return colModel;
            }
          }
        }
        $.each(subform.tableHeader, function (i, columns) {
          $.each(columns, function (j, column, field) {
            if (typeof column.field === 'string' && (field = getField(column.field))) {
              // 重新赋值数组，减少拷贝次数
              columns[j] = $.extend(field, column);
            }
          });
        });
        bootstrapOptions.columns = subform.tableHeader;
      }
      // 展示
      _tableelement
        .on('mousedown.editable', function (event) {
          // 触发cell-click。不用onClickCell，ps：click在mousedown后面，click时无法出发到td元素
          var $target = $(event.target);
          var $td = $target.closest('td'),
            $a;
          if ($td.length > 0 && ($a = $td.find('>a[data-pk]')).length > 0) {
            // 只有从表控件会触发
            if ($target.is($td) || $target.is($a.find('pre')) || $target.is($a) || $target.closest('.Validform_checktip').length) {
              $a.trigger('cell-click');
              event.stopPropagation();
            }
          }
        })
        .on('resetGridWidth.editable', function () {
          _this.resetGridWidth();
        })
        .bootstrapTable(
          $.extend(
            {
              onPageChange: function (number) {
                if (_this.isReadOnly()) {
                  _this.setDisplayAsLabel();
                }
                _this.setPagination();
              }
            },
            bootstrapOptions
          )
        );

      if (subform.horizontalScroll) {
        _this.$element
          .closest('.fixed-table-container')
          .find('table')
          .css({
            width: 'auto',
            'min-width': tableOuterTdWidth + 'px'
          });
        _this.$element.closest('.fixed-table-container').css('width', tableOuterTdWidth + 'px');
      }
      _this.$container = _tableelement.closest('.bootstrap-table');
      if (footerBtns.length > 0 && _this.$container.length) {
        _this.$container.append($('<div class="fixed-table-footbar"/>').append(footerBtns.join('')));
      }
      if (_this.isGroupForm() && _this.$container.length) {
        _this.$container.addClass('bootstrap-group-table');
      }
      _this._initEvent(subform);
      _this.setPagination();
      // 隐藏从表
      if (subform.showType == '5') {
        _this.hide(); //
      }
      _this.$element.closest('.fixed-table-container').find('table').css({
        'table-layout': 'initial!important'
      });
    },
    _initEvent: function (subform) {
      var self = this;
      var $container = self.$container;
      var codeButtonEvents = subform.codeButtonEvents;
      var formId = subform.outerId,
        formUuid = subform.formUuid;
      // 默认展示
      if (subform.hideButtons === dySubFormHideButtons.show || subform.hideButtons === dySubFormHideButtons.hide) {
      } else {
        subform.hideButtons = dySubFormHideButtons.show;
      }
      // 标准按钮添加权限控制
      var operationClazz = [];
      if (subform.editableOperateBtns != undefined) {
        // 新版流程定义的从表可编辑按钮
        for (var k in this.defaultOperateButtonConfig) {
          if (subform.editableOperateBtns.indexOf(k) != -1) {
            operationClazz.push(this.defaultOperateButtonConfig[k].visibleClass);
          }
        }
      } else {
        if (subform.hideButtons == dySubFormHideButtons.show || subform.hideButtons2 == dySubFormHideButtons.showAdd) {
          operationClazz.push(this.defaultOperateButtonConfig.btn_add.visibleClass);
          operationClazz.push(this.defaultOperateButtonConfig.btn_edit.visibleClass);
        }
        if (subform.hideButtons == dySubFormHideButtons.show || subform.hideButtons2 == dySubFormHideButtons.showDel) {
          operationClazz.push(this.defaultOperateButtonConfig.btn_del.visibleClass);
          operationClazz.push(this.defaultOperateButtonConfig.btn_clear.visibleClass);
        }
        // bug#52582
        if (subform.tableButtonInfo) {
          $.each(subform.tableButtonInfo, function (i, buttonInfo) {
            if (buttonInfo.code == 'btn_exp_subform') {
              operationClazz.push(self.defaultOperateButtonConfig.btn_exp_subform.visibleClass);
            } else if (buttonInfo.code == 'btn_imp_subform') {
              operationClazz.push(self.defaultOperateButtonConfig.btn_imp_subform.visibleClass);
            }
          });
        } else {
          if (subform.hideButtons == dySubFormHideButtons.show || subform.showExpButton2 == 'true' || subform.showExpButton2 == true) {
            operationClazz.push(this.defaultOperateButtonConfig.btn_exp_subform.visibleClass);
          }
          if (subform.hideButtons == dySubFormHideButtons.show || subform.showImpButton2 == 'true' || subform.showImpButton2 == true) {
            operationClazz.push(this.defaultOperateButtonConfig.btn_imp_subform.visibleClass);
          }
        }
      }
      operationClazz = operationClazz.join(' ');

      $container.addClass((self.operationClazz = operationClazz));
      // 监听事件并立即触发初始化事件
      $container
        .on('click init mouseover mouseout', 'button[action]', function (event) {
          var evalCode;
          var $button = $(this);
          var action = $button.attr('action');
          var buttonEvents = codeButtonEvents[action];
          if (!buttonEvents || $.trim((evalCode = buttonEvents[event.type])).length <= 0) {
            return;
          }
          var data = {};
          var evalData = {
            $this: $button,
            event: event,
            dysubform: self,
            data: data
          };
          var isLineEndButton = $button.closest('.btn-inline').length;
          var selectRows = self.getSelectedRows();
          if (isLineEndButton) {
            // 记录选中的ID，执行完函数后删除
            $container.attr('selectedRowId', (evalData.selectedRowId = $button.closest('.btn-inline').attr('data-rowid')));
            evalData.selectedRowIds = [evalData.selectedRowId];
          } else {
            evalData.selectedRowId = self.getSelectedRowId();
            // 支持多选
            evalData.selectedRowIds = self.getSelectedRowIds();
          }
          if (evalData.selectedRowId && $.trim(evalData.selectedRowId).length > 0) {
            $.extend(data, self.getRowData(formUuid, evalData.selectedRowId));
          } else if (action === 'btn_edit') {
            return appModal.error(dymsg.selectRecordMod);
          }

          if (action === 'btn_del' && $('.widget-main').length > 0) {
            setTimeout(function () {
              $('.widget-main').length && $('.widget-main').getNiceScroll().resize();
            }, 20);
          }
          if (action === 'btn_copy') {
            if (!isLineEndButton) {
              var selectIndex = self.$element.find('tr.selected').attr('data-index');
              if (!selectRows.length) {
                appModal.error('请先选择记录');
                return;
              } else if (selectRows.length > 1) {
                appModal.error('只能选择1条记录！');
                return;
              }
            }
            var index = isLineEndButton ? $button.closest('tr').attr('data-index') : selectIndex;
            evalData.data.index = ++index;
            evalData.data.rowCheckItem = false;
            var timerCopy = setTimeout(function () {
              $('.widget-main').length && $('.widget-main').getNiceScroll().resize();
              clearTimeout(timerCopy);
            }, 20);
          } else if (action === 'btn_edit') {
            var isLineEndButton = $button.closest('.btn-inline').length;
            if (!isLineEndButton && selectRows.length > 1) {
              appModal.error('只能选择1条记录！');
              return;
            }
          }

          data.formUuid = formUuid;
          event.preventDefault();
          event.stopPropagation();
          // evalCode = evalCode.replace(".get$dyform().",
          // ".get$dyform(\""+formId+"\").");
          // 设置evalCode执行的命名空间，执行完后及时清理
          DyformFacade.setNamespace(self.options.namespace);
          try {
            var defineFunc = appContext.eval(evalCode, $button, evalData);
          } catch (e) {
            console.log(e);
          } finally {
            DyformFacade.removeNamespace();
            $container.removeAttr('selectedRowId');
          }
          var _subformOptions = self.get$PlaceHolder().bootstrapTable('getOptions');
          self.setPagination(_subformOptions.pageSize);
          return false;
        })
        .find('button[action]:visible')
        .trigger('init');
    },
    // 默认的从表操作按钮配置
    defaultOperateButtonConfig: {
      btn_add: {
        visibleClass: 'bt-enable-add'
      },
      btn_del: {
        visibleClass: 'bt-enable-del'
      },
      btn_edit: {
        visibleClass: 'bt-enable-edit'
      },
      btn_exp_subform: {
        visibleClass: 'bt-enable-exp'
      },
      btn_imp_subform: {
        visibleClass: 'bt-enable-imp'
      },
      btn_clear: {
        visibleClass: 'bt-enable-clear'
      },
      btn_add_sub: {
        visibleClass: 'bt-enable-add-sub'
      },
      btn_down: {
        visibleClass: 'bt-enable-down'
      },
      btn_up: {
        visibleClass: 'bt-enable-up'
      }
    },
    get$Placeholder: function () {
      return this.$element;
    },
    /**
     * 从表行数据对话框
     *
     * @param subform
     * @param data
     * @param parentData
     */
    editSubformRowDataInDialog: function (subform, data, parentData) {
      var _this = this;
      var winSize = subform.winSize;
      var ns = _this.options.namespace;
      var formUuid = subform.openFormTree;
      var _parentthis = this.options.$paranentelement;
      var formDefinition = this.options.subformDefinition;
      var isAdd = false,
        isEdit = false,
        uuid = data['uuid'];
      if ($.trim(uuid).length <= 0) {
        isAdd = true; // 只有id和newRow字段
        delete data.newRow;
      }
      isEdit = _this.isReadOnly() == false;
      uuid = uuid || data['id'] || this.createUuid(); // 重新赋值
      var uniqueId = 'subForm_' + uuid;
      var url = ctx + '/pt/dyform/app_dyform_data_viewer?';
      var minHeight = winSize === 'large' ? '500px' : winSize === 'middle' ? '360px' : '100%';
      var $iframe = $('<iframe>', {
        src: url + 'formUuid=' + formUuid + '&dataUuid=' + uuid + '&isEdit=' + (isAdd || isEdit) + '&isSubForm=true',
        style: 'width:100%; overflow-x: hidden;border: 0px;',
        height: minHeight,
        id: uniqueId,
        name: uniqueId
      });
      window.editableSubFormData = window.editableSubFormData || {};
      window.editableSubFormData[uuid] = oldData = data;
      // 定义窗口内容
      $('#dialog' + uuid).remove();
      _parentthis.append("<div id='dialog" + uuid + "' title=" + subform.displayName + '(' + dybtn.add + ')></div>');
      _parentthis.$('#dialog' + uuid).append($iframe);
      // var _this = this;
      var buttons = {};
      buttons[dybtn.cancel] = function () {
        $(this).modal('hide');
      };

      function cb_save(event) {
        var $$ = document.getElementById(uniqueId).contentWindow.$;
        var $dyform = $$('.dyform');
        $dyform.trigger('mousedown.editable');
        var validateForm = undefined;
        validateForm = $dyform.dyform('validateForm', true);
        if (validateForm) {
          var formData = '';
          var errorInfo = '';
          $dyform.dyform(
            'collectFormData',
            function (formDatas) {
              formData = formDatas;
            },
            function (errorInfo) {
              console.log(errorInfo);
            }
          );
          var data = formData.formDatas[formUuid][0];
          if (_this.invoke('beforeSaveSubformData', data, $dyform, isAdd, event) === false) {
            return false;
          }
          // $.each(data, function (key, value) {
          //   $.trim(value).length || delete data[key]; // 删除空属性
          // });
          delete formData.formDatas[formUuid]; // 删除主表数据
          var nestformDatas = formData;
          // 深度拷贝，如果有，合并上一次确认的addedFormDatas等
          if (oldData && oldData.nestformDatas) {
            var oNestformDatas = JSON.parse(oldData.nestformDatas);

            function mergeArrayInObject(nData, oData) {
              nData = nData || {};
              for (var key in oData) {
                if ($.isPlainObject(oData[key])) {
                  nData[key] = mergeArrayInObject(nData[key], oData[key]);
                } else if ($.isArray(oData[key])) {
                  nData[key] = $.merge(nData[key] || [], oData[key]);
                }
              }
              return nData;
            }
            if (oNestformDatas.addedFormDatas) {
              nestformDatas.addedFormDatas = mergeArrayInObject(nestformDatas.addedFormDatas, oNestformDatas.addedFormDatas);
            }
            if (oNestformDatas.deletedFormDatas) {
              nestformDatas.deletedFormDatas = mergeArrayInObject(nestformDatas.deletedFormDatas, oNestformDatas.deletedFormDatas);
            }
            if (oNestformDatas.updatedFormDatas) {
              nestformDatas.updatedFormDatas = mergeArrayInObject(nestformDatas.updatedFormDatas, oNestformDatas.updatedFormDatas);
            }
          }
          data.nestformDatas = JSON.stringify(nestformDatas); // 嵌套从表数据，保留更新和删除记录
          if (isAdd) {
            _this.addRowData(formUuid, data);
          } else {
            _this.updateRowData(formUuid, data);
          }
          $(this).modal('hide');
        } else {
          return false;
        }
      }
      if (isAdd || isEdit) {
        buttons[dybtn.save] = cb_save;
      } else if (isEdit) {
        buttons['编辑'] = function (event) {
          $iframe.attr('src', url + 'formUuid=' + formUuid + '&dataUuid=' + uuid + '&isEdit=true&isSubForm=true');
          var buttons = {};
          buttons[dybtn.save] = cb_save;
          buttons[dybtn.cancel] = function () {
            $(this).modal('hide');
          };
          appModal.dialog({
            modal: true,
            size: winSize,
            message: $iframe,
            buttons: buttons,
            title: subform.displayName + '(' + dybtn.edit + ')'
          });
        };
      }

      appModal.dialog({
        modal: true,
        size: winSize,
        message: $iframe,
        buttons: buttons,
        title: subform.displayName // + (isAdd ? ("(" + dybtn.add+ ")")
        // : "")
      });
    },

    /**
     * 根据从表定义，对从表数据进行重新定义
     */
    sortRowData: function (formdatas) {
      var subform = this.getSubformConfig();
      for (var i in subform.fields) {
        if (!subform.fields.hasOwnProperty(i)) {
          continue;
        }
        var sort = subform.fields[i].sort;
        if (sort) {
          formdatas.sort(function (obj1, obj2) {
            var val1 = obj1[i];
            var val2 = obj2[i];
            if (sort.indexOf('asc') != -1) {
              if (sort.indexOf('nulls first') != -1) {
                if (val1 == null || val1 == undefined) {
                  return -1;
                }
                if (val2 == null || val2 == undefined) {
                  return 1;
                }
              }
              if (sort.indexOf('nulls last') != -1) {
                if (val1 == null || val1 == undefined) {
                  return 1;
                }
                if (val2 == null || val2 == undefined) {
                  return -1;
                }
              }
              if (val1 < val2) {
                return -1;
              } else if (val1 > val2) {
                return 1;
              } else {
                return 0;
              }
            } else {
              if (sort.indexOf('nulls first') != -1) {
                if (val1 == null || val1 == undefined) {
                  return 1;
                }
                if (val2 == null || val2 == undefined) {
                  return -1;
                }
              }

              if (sort.indexOf('nulls last') != -1) {
                if (val1 == null || val1 == undefined) {
                  return -1;
                }
                if (val2 == null || val2 == undefined) {
                  return 1;
                }
              }
              if (val1 > val2) {
                return -1;
              } else if (val1 < val2) {
                return 1;
              } else {
                return 0;
              }
            }
          });
        }
      }
    },

    /**
     * 根据fieldDefinition获得gridmodel
     *
     * @param fieldDefinition
     */
    getColModel: function (field, subform, ns) {
      var _this = this;
      var model = {};
      model.field = field.name;
      model.index = field.name;
      model.align = field.textAlign;
      model.isRequired = field.isRequired;
      model.title = field.isRequired ? '<span style="color: #e30033">*</span>' + field.displayName : field.displayName;
      model.editable = {
        highlight: false,
        onblur: 'submit',
        editable: _this.isFieldEditable(field),
        emptytext: '',
        title: model.title,
        autotext: 'always',
        savenochange: true, // 没有变更也触发保存，调用handleEmpty
        showbuttons: false, // 不显示提交按钮
        type: 'control',
        unsavedclass: null,
        mode: 'inline', // inline、popover、modal
        toggle: 'cell-click'
      };
      model.editableOriginalTitle = $('<span>').html(model.title).text();
      model.formatter = function (cellvalue, row, index) {
        if (cellvalue == null || cellvalue == undefined) {
          cellvalue = '';
        }
        return cellvalue;
      };
      return model;
    },
    setEditableMode: function (model, field, subform, ns) {
      var allowModes = ['inline', 'popover', 'modal'];
      // 域优先级最高，从表配置第二，控件默认最低
      if ($.inArray(field.editableMode, allowModes) > -1) {
        return (model.editable.mode = field.editableMode);
      } else if ($.inArray(subform.editableMode, allowModes) > -1) {
        return (model.editable.mode = subform.editableMode);
      }
      // 根据控件类型，生成控件最友好的编辑模式，比如附件和富文本使用弹出框
      var inputMode = model.inputMode;
    },
    // 可以生成裸体控件(裸体控件，是指在初始化时不生成对应的labelelement及editableelement)
    isControlAllowNaked: function (model) {
      return (
        (!model.editable || !model.visible) &&
        !(
          formDefinitionMethod.isInputModeAsValueMap(model.inputMode) ||
          model.controlable ||
          formDefinitionMethod.isAttach(model.inputMode)
        )
      );
    },

    // bind函数，桥接
    bind: function (eventname, event) {
      if (this.options[eventname]) {
        var fun = this.options[eventname];
        this.options[eventname] = function () {
          fun.apply(this, $.makeArray(arguments));
          event.apply(this, $.makeArray(arguments));
        };
      } else {
        this.options[eventname] = event;
      }

      return this;
    },

    /**
     * 初始化控件事件，绑定到控件
     */
    initControlEvents: function () {
      var _this = this;
      var events = this.getSubformConfig().events;
      if (events) {
        for (var i in events) {
          if (events.hasOwnProperty(i)) {
            (function (i, events, _this) {
              _this.bind(i, function () {
                eval(events[i]);
              });
            })(i, events, _this);
          }
        }
      }
    },

    /* 在设置之后角发该事件 */
    invoke: function (method) {
      if (this.options[method]) {
        return this.options[method].apply(this, $.makeArray(arguments).slice(1));
      }
    },

    /**
     * 添加行控件的url事件.
     */
    addControlUrl: function (cellControl, rowId) {
      var _this = this;
      var urlClickEvent = function (event) {
        var reg = new RegExp(/\$\{([^\}]*)\}/g);
        var url = event.data.a;
        var tempValueArray = url.match(reg);
        for (var k = 0; k < tempValueArray.length; k++) {
          var tempvariable = tempValueArray[k].replace('${', '').replace('}', '');
          var control = _this.getControl(tempvariable, rowId); //
          if (control != undefined) {
            url = url.replace(tempValueArray[k], control.getValue());
          } else if (tempvariable == 'datarowid') {
            url = url.replace(tempValueArray[k], rowId);
          }
        }
        window.open(ctx + url);
      };
      // 添加url点击事件
      cellControl.addUrlClickEvent(urlClickEvent);
    },

    /**
     * 置于中，从表的各cell的id
     */
    getCellId: function (rowId, fieldName) {
      var cellInfo = this.getCellInfo(rowId, fieldName);
      var cellInfos = this.getCellInfos();
      var cellInfos2 = this.getCellInfos2();
      if (cellInfo == null || cellInfo == undefined) {
        var controlId = this.getCellInfoKey(rowId, fieldName); //
        cellInfo = {
          controlId: controlId,
          rowId: rowId,
          fieldName: fieldName
        };
        cellInfos[this.getCellInfoKey(rowId, fieldName)] = cellInfo;
        cellInfos2[controlId] = cellInfo;
        return controlId;
      } else {
        return cellInfo.controlId;
      }
    },

    /**
     * 单元信息以rowId,和fieldName组成key
     */
    getCellInfoKey: function (rowId, fieldName) {
      return rowId + '___' + fieldName;
    },

    /**
     * 返回所有的单元信息
     */
    getCellInfos: (function (cellInfos) {
      return function () {
        return cellInfos;
      };
    })({}),

    getCellInfos2: (function (cellInfos2) {
      return function () {
        return cellInfos2;
      };
    })({}),

    /**
     * 单元信息以rowId,和fieldName组成key
     */
    getCellInfo: function (rowId, fieldName) {
      var id = this.getCellInfoKey(rowId, fieldName);
      var cellInfos = this.getCellInfos();
      var cellInfo = cellInfos[id];
      return cellInfo;
    },

    getCellInfoByControlId: function (controlId) {
      var cellInfos2 = this.getCellInfos2();
      return cellInfos2[controlId];
    },

    /**
     * 根据表单定义Id从表单定义Json中获取从表定义
     */
    getSubformDefinitionByFormUuid: function (formDefinition, formUuid) {
      var subforms = formDefinition.subforms;
      var thisSubform = null;
      for (var i in subforms) {
        var subform = subforms[i];
        if (subform.formUuid == formUuid) {
          thisSubform = subform;
          break;
        }
      }
      return thisSubform;
    },

    /**
     * 在从表中的字段是不是控件字段
     *
     * @param fieldName
     */
    isControlFieldInSubform: function (fieldName) {
      if (
        fieldName == rowCheckItem ||
        fieldName == '_inline_operate' ||
        fieldName == 'seqNO' ||
        fieldName == 'cb' ||
        fieldName == 'id' ||
        fieldName == 'uuid' ||
        fieldName == 'parent_uuid' ||
        fieldName == 'level' ||
        fieldName == 'isLeaf' ||
        fieldName == 'expanded' ||
        fieldName == 'loaded' ||
        fieldName == 'parent' ||
        fieldName == 'icon' ||
        fieldName == 'linked'
      ) {
        return false;
      } else {
        return true;
      }
    },

    /**
     * 行数据增加
     *
     * @param formUuid
     * @param parentData
     */
    addSubformRowDataIn: function (formUuid, parentData) {
      // 点击添加从表行数据的按钮事件
      var _this = this;
      var colModels = _this.get$PlaceHolder().bootstrapTable('getVisibleColumns');
      var uuid = _this.createUuid();
      var data = {};
      data['id'] = uuid;
      data['newRow'] = true; // 新增的行
      if (typeof parentData != 'undefined') {
        data['parent_uuid'] = parentData['uuid'];
      }
      _this.addRowData(formUuid, data);
    },

    /**
     * 行数据增加
     *
     * @param formUuid
     * @param parentData
     */
    addSubformRowDataInJqGrid: function (formUuid, parentData) {
      // 点击添加从表行数据的按钮事件
      var _this = this;
      var ns = _this.options.namespace;
      var uuid = this.createUuid();
      var data = {};
      data['id'] = uuid;
      data['newRow'] = true; // 新增的行
      if (typeof parentData != 'undefined') {
        // data["parent_uuid"] = parentData["uuid"];
        if (_this.isGroupForm()) {
          data[_this.options.groupField] = parentData[_this.options.groupField];
        }
      }
      _this.addRowData(formUuid, data);
    },
    /**
     * 为从表添加行数据
     *
     * @param formUuid
     * @param data
     *            数据格式：{name1:value1,name2: value2…} name为在colModel中指定的名称
     * @param controlable
     *            是否对新添加的行控件化
     * @param seqNOable
     *            是否刷新序号
     * @param insertRowIndex
     *            插入序号
     */
    addRowData: function (formUuid, data, controlable, seqNOable, insertRowIndex) {
      var self = this;
      var flag = true;
      var jsCode = self.getJsCodeByMethod('beforeInsertRow');
      if (!_.isEmpty(jsCode)) {
        appContext.eval(
          jsCode,
          $(this),
          {
            data: data
          },
          function (e) {
            flag = e;
          }
        );
      }
      if (flag === false) {
        return;
      }
      var t = formUuid,
        field,
        fields;
      if (arguments.length == 1) {
        formUuid = self.options.formUuid;
        data = t;
      }
      if ($.trim(data['uuid']).length <= 0) {
        // 调用方没有生成行id,这里也为dataUuid
        data['uuid'] = data['id'] || self.createUuid();
        var defaultFormData = self.options.subformDefinition.defaultFormData;
        if ((fields = self.options.subformDefinition.fields)) {
          for (var fieldName in fields) {
            var defaultValue = defaultFormData[fieldName] || ((field = fields[fieldName]) && field.defaultValue);
            if (data[fieldName] == null && $.trim(defaultValue).length > 0) {
              data[fieldName] = defaultValue;
            }
          }
        }
      }
      if ($.trim(data['id']).length <= 0) {
        data['id'] = data['uuid'];
      }

      $.each(this.getSubformValueFormatSetMap(), function (i, item) {
        if (data[i]) {
          if (data[i].indexOf('至')) {
            data[i] = data[i].split(' 至 ')[0];
          }
          data[i] = commons.DateUtils.format(new Date(data[i]), item);
          if (data[i].indexOf('NaN') > -1) {
            delete data[i]; // 无法清除空行
            console.log(i + ':格式不正确！');
          }
        }
      });

      // 的行ID与Id值一样
      // self.get$PlaceHolder().trigger("click.editable");// 触发更新
      if (typeof insertRowIndex === 'number') {
        self.get$PlaceHolder().bootstrapTable('insertRow', {
          index: insertRowIndex,
          row: data
        });
      } else {
        self.get$PlaceHolder().bootstrapTable('append', data);
      }
      var subTableOptions = self.get$PlaceHolder().bootstrapTable('getOptions');
      // subTableOptions.pageNumber = subTableOptions.totalPages
      self.get$PlaceHolder().bootstrapTable('selectPage', subTableOptions.totalPages);
      self.setPagination();
      if (data['afterInsertRow'] === false) {
      } else {
        self.invoke('afterInsertRow', data);
        self.runJsCode('afterInsertRow', data);
      }

      self
        .get$PlaceHolder()
        .off('editable-shown.bs.table')
        .on('editable-shown.bs.table', function () {
          self.resetGridWidth();
        });

      return data['id'];
    },

    copyRowData: function (formUuid, data, controlable, seqNOable) {
      var self = this;
      var flag = true;
      var jsCode = self.getJsCodeByMethod('beforeInsertRow');
      if (!_.isEmpty(jsCode)) {
        appContext.eval(
          jsCode,
          $(this),
          {
            data: data
          },
          function (e) {
            flag = e;
          }
        );
      }
      if (flag === false) {
        return;
      }
      var t = formUuid,
        field,
        fields;
      if (arguments.length == 1) {
        formUuid = self.options.formUuid;
        data = t;
      }
      data['uuid'] = data['id'] || self.createUuid();
      var defaultFormData = self.options.subformDefinition.defaultFormData;
      if ((fields = self.options.subformDefinition.fields)) {
        for (var fieldName in fields) {
          var defaultValue = defaultFormData[fieldName] || ((field = fields[fieldName]) && field.defaultValue);
          if (data[fieldName] == null && $.trim(defaultValue).length > 0) {
            data[fieldName] = defaultValue;
          }
        }
      }
      if ($.trim(data['id']).length <= 0) {
        data['id'] = data['uuid'];
      }

      $.each(this.getSubformValueFormatSetMap(), function (i, item) {
        if (data[i] && data[i].indexOf('至')) {
          data[i] = data[i].split(' 至 ')[0];
        }
        data[i] = commons.DateUtils.format(new Date(data[i]), item);
        if (data[i].indexOf('NaN') > -1) {
          delete data[i]; // 无法清除空行
          console.log(i + ':格式不正确！');
        }
      });
      // 的行ID与Id值一样
      // self.get$PlaceHolder().trigger("click.editable");// 触发更新
      delete data[rowCheckItem];
      self.get$PlaceHolder().bootstrapTable('insertRow', {
        index: data.index,
        row: data
      });
      if (data['afterInsertRow'] === false) {
      } else {
        self.invoke('afterInsertRow', data);
        self.runJsCode('afterInsertRow', data);
      }
      self.resetGridWidth();
      return data['id'];
    },

    batchAddRowData: function (formUuid, dataList, controlable, seqNOable) {
      var self = this;
      var waitDatas = [];
      var t = formUuid,
        fields,
        field;
      if (arguments.length == 1) {
        formUuid = this.options.formUuid;
        dataList = t;
      }
      for (var k in dataList) {
        var data = dataList[k];
        var flag = true;
        var jsCode = self.getJsCodeByMethod('beforeInsertRow');
        if (!_.isEmpty(jsCode)) {
          appContext.eval(
            jsCode,
            $(this),
            {
              data: data
            },
            function (e) {
              flag = e;
            }
          );
        }
        if (flag === false) {
          continue;
        }
        if ($.trim(data['uuid']).length <= 0) {
          // 调用方没有生成行id,这里也为dataUuid
          data['uuid'] = data['id'] || self.createUuid();
          var defaultFormData = self.options.subformDefinition.defaultFormData;
          if ((fields = self.options.subformDefinition.fields)) {
            for (var fieldName in fields) {
              var defaultValue = defaultFormData[fieldName] || ((field = fields[fieldName]) && field.defaultValue);
              if (data[fieldName] == null && $.trim(defaultValue).length > 0) {
                data[fieldName] = defaultValue;
              }
            }
          }
        }
        if ($.trim(data['id']).length <= 0) {
          data['id'] = data['uuid'];
        }
        waitDatas.push(data);
      }
      // 的行ID与Id值一样
      self.get$PlaceHolder().bootstrapTable('append', waitDatas);
      for (var k in dataList) {
        var data = dataList[k];
        if (data['afterInsertRow'] === false) {
        } else {
          self.invoke('afterInsertRow', data);
          self.runJsCode('afterInsertRow', data);
        }
      }
    },

    /**
     * 获得行数据
     *
     * @param formUuid
     * @param rowId
     */
    getRowData: function (formUuid, rowId, fnSuccess, fnError) {
      var _this = this;
      var t = formUuid;
      if (arguments.length == 1) {
        formUuid = this.options.formUuid;
        rowId = t;
      }
      if (rowId == null || typeof rowId === 'undefined') {
        return fnError && fnError.call(_this, 'rowId不允许为空');
      }

      var subformData = {};
      subformData['uuid'] = rowId;
      var data = _this.get$PlaceHolder().bootstrapTable('getRowByUniqueId', rowId);
      $.extend(subformData, data);
      fnSuccess && fnSuccess.call(_this, subformData);
      return subformData;
    },
    /**
     * 获取从表所有的控件的值键对象
     */
    getSubformValueKeySetMap: function () {
      var keyMap = {};
      var fields = this.options.subformDefinition.fields;
      for (var field in fields) {
        // radio表单元素
        // valuemap,树形下拉框valuemap,checkbox表单元素valuemap,下拉单选框valuemap
        var inputMode = fields[field].inputMode;
        if (
          inputMode == dyFormInputMode.treeSelect ||
          inputMode == dyFormInputMode.radio ||
          inputMode == dyFormInputMode.checkbox ||
          inputMode == dyFormInputMode.selectMutilFase
        ) {
          var optionSet = $.wRadioCheckBoxCommonMethod.getOptionSet(fields[field]);
          // fields[field].optionSet;
          if (typeof optionSet == 'undefined' || optionSet == null || optionSet.length == 0) {
            console.log('field[' + field + '] optionSet undefined');
            continue;
          }
          var obj = {};
          for (var key in optionSet) {
            var value = optionSet[key]; // 键值对换
            obj[value] = key;
          }
          keyMap[field] = obj;
        }
      }
      return keyMap;
    },

    getSubformValueFormatSetMap: function () {
      var keyMap = {};
      var fields = this.options.subformDefinition.fields;
      for (var field in fields) {
        var inputMode = fields[field].inputMode;
        if (inputMode == dyFormInputMode.date) {
          var fmt = fields[field].contentFormat;
          if (typeof fmt == 'undefined' || fmt == null) {
            console.log('field[' + field + '] contentFormat undefined');
            continue;
          }
          var format = '';
          if (fmt == dyDateFmt.yearMonthDate) {
            format = 'yyyy-MM-dd';
          } else if (fmt == dyDateFmt.dateTimeHour) {
            format = 'yyyy-MM-dd HH';
          } else if (fmt == dyDateFmt.dateTimeMin) {
            format = 'yyyy-MM-dd HH:mm';
          } else if (fmt == dyDateFmt.dateTimeSec) {
            format = 'yyyy-MM-dd HH:mm:ss';
          } else if (fmt == dyDateFmt.timeHour) {
            format = 'HH';
          } else if (fmt == dyDateFmt.timeMin) {
            format = 'HH:mm';
          } else if (fmt == dyDateFmt.timeSec) {
            format = 'HH:mm:ss';
          } else if (fmt == dyDateFmt.yearMonthDateCn) {
            format = 'yyyy年MM月dd日';
          } else if (fmt == dyDateFmt.yearCn) {
            format = 'yyyy年';
          } else if (fmt == dyDateFmt.yearMonthCn) {
            format = 'yyyy年MM月';
          } else if (fmt == dyDateFmt.monthDateCn) {
            format = 'MM月dd日';
          } else if (fmt == dyDateFmt.year) {
            format = 'yyyy';
          }
          keyMap[field] = format;
        }
      }
      return keyMap;
    },
    /**
     * 获得行数据
     *
     * @param formUuid
     * @param rowId
     */
    getRowDisplayData: function (formUuid, rowId) {
      var t = formUuid;
      var _this = this;
      if (arguments.length == 1) {
        formUuid = this.options.formUuid;
        rowId = t;
      }

      var subformData = {};
      subformData['uuid'] = rowId;
      var data = _this.get$PlaceHolder().bootstrapTable('getRowByUniqueId', rowId);
      for (var i in data) {
        // 获取不是从数据加载而来的字段的值
        if (!this.isControlFieldInSubform(i)) {
          subformData[i] = data[i];
        }
      }
      var colModels = _this.get$PlaceHolder().bootstrapTable('getVisibleColumns');
      var formDefinition = _this.options.subformDefinition;
      for (var j = 0; j < colModels.length; j++) {
        var model = colModels[j];
        var fieldName = model.field;
        if (!this.isControlFieldInSubform(fieldName)) {
          continue;
        }
        var $input = $("a[data-pk='" + rowId + "'][data-name='" + fieldName + "']>input");
        subformData[fieldName] = $input.val();
      }
      return subformData;
    },

    /**
     * 删除行事件
     *
     * @param formUuid
     * @param selectedRowId
     */
    deleteSubformRowDataEvent: function (formUuid, slectedIds) {
      // 点击删除从表行数据的按钮事件
      var _this = this;
      var ids;
      var subform = this.getSubformConfig();
      if (subform.multiselect) {
        ids = slectedIds;
      } else {
        ids = [];
        ids.push(slectedIds);
      }
      // var ids = $dg.bootstrapTable('getGridParam','selarrrow');
      if (ids.length > 0) {
        appModal.confirm(dymsg.delConfirm, function (result) {
          if (result) {
            for (var i = ids.length - 1; i >= 0; i--) {
              _this.delRowData(formUuid, ids[i]);
            }
          }
        });
      } else {
        appModal.info(dymsg.selectRecordDel, dymsg.tipTitle);
      }
    },

    // 上移
    upSubformRowDataEvent: function (formUuid, selectRowId) {
      var self = this;
      var data = self.getData();
      for (var i = 0, len = data.length; i < len; i++) {
        var row = data[i];
        if (row['id'] === selectRowId && i > 0) {
          data[i] = data[i - 1];
          data[i - 1] = row;
          var r = self.get$Placeholder().bootstrapTable('load', data);
          // var r = self.get$Placeholder().bootstrapTable("moveRowUp", i);
          self.invoke('upSubformRow', selectRowId, row);
          self.resetGridWidth();
          return r;
        }
      }
    },

    // 下移
    downSubformRowDataEvent: function (formUuid, selectRowId) {
      var self = this;
      var data = self.getData();
      for (var i = 0, len = data.length; i < len; i++) {
        var row = data[i];
        if (row['id'] === selectRowId && i < len - 1) {
          data[i] = data[i + 1];
          data[i + 1] = row;
          var r = self.get$Placeholder().bootstrapTable('load', data);
          // var r = self.get$Placeholder().bootstrapTable("moveRowDown", i);
          self.invoke('downSubformRow', selectRowId, row);
          self.resetGridWidth();
          return r;
        }
      }
    },

    /**
     * 更新从表的行数据
     *
     * @param formUuid
     * @param data
     */
    updateRowData: function (formUuid, data) {
      var _this = this;
      var t = formUuid;
      if (arguments.length == 1) {
        formUuid = _this.options.formUuid;
        data = t;
      }
      var rowId = data['id'] || data['uuid'];
      if (typeof rowId == 'undefined') {
        throw new Error('id is not defined');
      }
      var rows = _this.getData();
      for (var i = 0, len = rows.length; i < len; i++) {
        var row = rows[i];
        if (row['id'] === rowId) {
          // 更新显示
          _this.eachControlByRowId(rowId, function (fieldName, ctl) {
            ctl.setValue(data[fieldName]);
          });
          _this.get$Placeholder().bootstrapTable('updateRow', {
            index: i,
            row: data
          });
          return _this.resetGridWidth();
        }
      }
    },
    eachControlByRowId: function (rowId, callback) {
      var self = this;
      self
        .get$PlaceHolder()
        .find('tr[data-uniqueid="' + rowId + '"]>td>a[data-name]')
        .each(function (idx, element) {
          var $element = $(element);
          var fieldName = $element.attr('data-name');
          var ctl = self.getControl(fieldName, rowId);
          ctl && callback(fieldName, ctl);
        });
    },
    /**
     * 删除从表的某行数据
     *
     * @param formUuid
     * @param data
     *            数据格式：{name1:value1,name2: value2…} name为在colModel中指定的名称
     */
    delRowData: function (formUuid, rowId) {
      var _this = this;
      var t = formUuid;
      if (arguments.length == 1) {
        formUuid = _this.options.formUuid;
        rowId = t;
      }
      var rowData = {};
      rowData.rowId = rowId;
      rowData.formUuid = formUuid;
      var flag = _this.invoke('beforeDeleteRow', rowData);
      // 执行自定义JS脚本
      _this.runJsCode('beforeDeleteRow', rowData, function (e) {
        flag = e;
      });

      if (flag === false) {
        return;
      }

      // bug#53112
      setTimeout(function () {
        _this.eachControlByRowId(rowId, function (fieldName, ctl) {
          ctl.clean();
        });
      }, 1);

      _this.get$PlaceHolder().bootstrapTable('remove', {
        field: 'id',
        values: [rowId]
      });

      _this.removeValidateBlankRow();

      _this.cacheDeleteRow({
        dataUuid: rowId,
        formUuid: formUuid
      });
      // 删除后，清空被选中ID值 add by wujx 20160816 end

      _this.invoke('afterDeleteRow', rowId);
      _this.runJsCode('afterDeleteRow', rowData);
      _this.resetGridWidth();
      _this.setPagination();
    },

    createUuid: function () {
      return new UUID().id.toLowerCase();
    },

    /**
     * 加载form数据
     *
     * @param formUuid
     * @returns {___anonymous12548_12549}
     */
    loadDefaultFormData: function (formUuid) {
      var formData = {};
      var url = ctx + '/pt/dyform/data/getDefaultFormData';
      $.ajax({
        url: url,
        cache: false,
        async: false, // 同步完成
        type: 'POST',
        data: {
          formUuid: formUuid
        },
        dataType: 'json',
        success: function (result) {
          if (result.success == 'true' || result.success == true) {
            formData = result.data;
          } else {
          }
        },
        error: function (data) {
          console.log(JSON.cStringify(data));
        }
      });
      return formData;
    },

    /**
     * 获取从表的定义id
     */
    getFormUuid: function () {
      return this.options.subformDefinition.uuid;
    },

    setMainformDataUuid: function (datauuid) {
      this.options.mainformDataUuid = datauuid;
    },

    /**
     * 获取主表Uuid
     *
     * @param dataUuid
     */
    getDataUuid4Mainform: function () {
      return this.options.mainformDataUuid;
    },

    /**
     * 加载子节点
     *
     * @param formUuid
     * @param data
     */
    loadAndAddChildRowData: function (formUuid, data) {
      var formUuidOfSubform = formUuid;
      var formUuidOfMainform = this.getFormUuid();
      var dataUuidOfMainform = this.getDataUuid4Mainform();
      if (dataUuidOfMainform == undefined) {
        return;
      }
      var dataUuidOfParentNode = data['id'];
      var _this = this;
      var url = ctx + '/pt/dyform/data/getFormDataOfChildNode4ParentNode';
      $.ajax({
        url: url,
        cache: false,
        async: true, // 异步完成即可
        type: 'POST',
        data: {
          formUuidOfSubform: formUuidOfSubform,
          formUuidOfMainform: formUuidOfMainform,
          dataUuidOfMainform: dataUuidOfMainform,
          dataUuidOfParentNode: dataUuidOfParentNode
        },
        dataType: 'json',
        success: function (result) {
          if (result.success == 'true' || result.success == true) {
            var formData = {};
            formData = result.data;
            if (formData == null) return; // 没有子节点

            for (var i = 0; i < formData.length; i++) {
              var data = formData[i];
              data['id'] = data['uuid'];
              _this.addRowData(formUuidOfSubform, data);
            }
          } else {
            console.log(JSON.cStringify(result));
          }
        },
        error: function (data) {
          console.log(JSON.cStringify(data));
        }
      });
      // return formData;
    },

    /**
     * 设置控件的值
     *
     * @param control
     * @param cellValue
     * @param dataUuid
     */
    setValue: function (control, cellValue, dataUuid) {
      if (!(typeof control === 'string')) {
        console.error('control must be string');
        return;
      }
      var self = this;
      var ns = self.options.namespace;
      var rows = self.getData();
      for (var i = 0, len = rows.length; i < len; i++) {
        var row = rows[i];
        if (row['id'] === dataUuid) {
          // 更新显示
          self.eachControlByRowId(dataUuid, function (fieldName, ctl) {
            if (control === fieldName) {
              ctl.setValue(cellValue);
            }
          });
          //					self.get$Placeholder().bootstrapTable("updateCell", {
          //						index : i,
          //						field : control,
          //						value : cellValue
          //					});
          return self.resetGridWidth();
        }
      }
    },
    /**
     * 获取从表的数据
     *
     * @param formUuid
     *            从表定义Uuid
     */
    collectSubformData: function (fnSuccess, fnError) {
      var subformDatas = [];
      var _this = this;
      // 触发一下bootstrap-table-editable数据回填
      // _this.get$PlaceHolder().trigger("click.editable");
      var datas = _this.getData();
      var promiseFuns = [];
      for (var i = 0; i < datas.length; i++) {
        (function (j) {
          var subformData = {};
          var data = datas[j];
          var rowId = data['id'];
          promiseFuns.push(function () {
            var def = $.Deferred();
            _this.getRowData(
              _this.options.formUuid,
              rowId,
              function (subformData) {
                subformData.seqNO = j + 1 + ''; //
                def.resolve(subformData);
              },
              function (errorInfo) {
                def.reject(errorInfo);
              }
            );
            return def.promise();
          });
        })(i);
      }

      var fnThen = function (index) {
        if (promiseFuns.length == 0) {
          fnSuccess.call(_this, subformDatas);
        } else {
          promiseFuns[index]().then(
            function (subformData) {
              subformDatas.push(subformData);
              if (index == promiseFuns.length - 1) {
                fnSuccess.call(_this, subformDatas);
              } else {
                fnThen(index + 1);
              }
            },
            function (errorInfo) {
              // 附件上传失败
              fnError.call(_this, errorInfo);
            }
          );
        }
      };
      fnThen(0);
    },
    collectSubformDisplayData: function (fnSuccess, fnError) {
      var self = this;
      var datas = self.getData();
      for (var i = 0; i < datas.length; i++) {
        var data = datas[i];
        var rowId = data['id'];
        self.eachControlByRowId(rowId, function (fieldName, ctl) {
          if (ctl.isValueMap() && ctl.getDisplayValue) {
            data[fieldName] = ctl.getDisplayValue();
          }
        });
      }
      fnSuccess && fnSuccess(datas);
      return datas;
      // return this.collectSubformData(fnSuccess, fnError);
    },
    getData: function () {
      var _this = this;
      return _this.get$Placeholder().bootstrapTable('getData');
    },
    /**
     * 获取行数据id
     */
    getRowIds: function () {
      var _this = this;
      var datas = _this.getData();
      var rowIds = [];
      for (var i = 0; i < datas.length; i++) {
        var data = datas[i];
        var rowId = data['id'];
        rowIds.push(rowId);
      }
      return rowIds;
    },

    /**
     * 获得被删除的行.
     */
    getDeleteRows: function () {
      data = this.$element.data(cacheType.deletedFormDataOfSubform);
      if (typeof data == 'undefined') {
        data = [];
      }
      return data;
    },

    /**
     * 清空被删除的行.
     */
    clearDeleteRows: function () {
      this.$element.data(cacheType.deletedFormDataOfSubform, []);
    },

    cacheDeleteRow: function (data) {
      var fdata = this.$element.data(cacheType.deletedFormDataOfSubform);
      if (typeof fdata == 'undefined') {
        fdata = [];
      }
      fdata.push(data.dataUuid);
      this.$element.data(cacheType.deletedFormDataOfSubform, fdata);
    },

    /**
     * 从表控件校验
     *
     * @returns {Boolean}
     */
    validate: function () {
      var _this = this;
      var ns = _this.options.namespace;
      var formId = this.getFormId();
      var valid = true;
      var datas = this.getData();
      var colModels = this.getColModels();

      for (var j = 0; j < datas.length; j++) {
        // 遍历从表的各行
        var data = datas[j];
        var rowId = data['id'];
        for (var k = 0; k < colModels.length; k++) {
          var model = colModels[k];
          var fieldName = model.field;
          if (this.isValidateIgnore(fieldName)) {
            // 不需要验证
            continue;
          }

          if (!this.isControlFieldInSubform(fieldName)) {
            // 不获取序号
            continue;
          }
          //从表编辑模式为单元格时且列定义不可编辑时不做校验
          if (!this.isControlEditable(rowId, fieldName) && this.getSubformConfig().editMode === '1') {
            continue;
          }

          // var cellId = this.getCellId(rowId, fieldName);

          var control = _this.getControl(fieldName, rowId);
          if (typeof control == 'undefined') {
            continue;
          }
          var v = true;
          v = control.validate();
          valid = valid && v;
        }
      }
      return valid;
    },

    /**
     * add by wujx 20160922 从表校验
     */
    validateSubform: function () {
      return this.validateBlankRow();
    },

    /**
     * add by wujx 20160922 是否有空行
     */
    validateBlankRow: function () {
      if (this.isValidationNeeded()) {
        var subformConfig = this.getSubformConfig();
        if (subformConfig.isAllowBlankRow == '2') {
          var blankRow = this.getSubformBlankRowIds();
          if (blankRow.length > 0) {
            return false;
          }
        }
      }
      return true;
    },

    // 移除校验空行 add by wujx 20160927
    removeValidateBlankRow: function () {
      if (this.validateBlankRow()) {
        var subformConfig = this.getSubformConfig();
        if (typeof bubble != 'undefined' && bubble != null) {
          bubble.removeErrorItem(subformConfig.formUuid + '-blankrow');
        }
      }
    },

    // 从表配置
    getSubformConfig: function () {
      var formUuid = this.options.formUuid;
      // 获取从表定义
      var subform = this.options.formDefinition.subforms[formUuid];
      return subform;
    },

    // 判断列是否可编辑
    isControlEditable: function (rowId, fieldName) {
      var subform = this.getSubformConfig();
      if (
        this.options.readOnly || // 调用者要求只读
        subform.editMode == dySubFormEdittype.newWin // 编辑直接在窗口中
      ) {
        return false;
      }

      var _this = this;
      var colModels = this.getColModels();
      var colModel = this.getColModelByFieldName(fieldName);
      if (colModel == null) {
        return false;
      }
      // var validateOnHidden = !!subform.validateOnHidden;
      // if (!colModel.visible && !validateOnHidden) {
      //   return false;
      // }

      if (!colModel.visible) {
        return false;
      }
      var control = _this.getControl(fieldName, rowId);

      if (control == undefined) {
        return false;
      }
      if (control.isReadOnly && control.isReadOnly()) {
        return false;
      }
      if (control.isShowAsLabel() || !control.isEditable()) {
        return false;
      }
      return true;
    },

    // 获取各列定义
    getColModels: function () {
      var _this = this;
      var ns = _this.options.namespace;
      if (typeof _this.options['colModels'] == 'undefined') {
        _this.options['colModels'] = _this.get$Placeholder().bootstrapTable('getVisibleColumns');
      }
      return _this.options['colModels'];
    },

    getColModelByFieldName: function (fieldName) {
      var colModels = this.getColModels();
      for (var j = 0; j < colModels.length; j++) {
        var model = colModels[j];
        if (model.field == fieldName) {
          return model;
        }
      }
      return null;
    },

    getFillFormData: function (formDatas) {},
    // 设置分页配置
    setPagination: function (pageSize, formDatas, subformCfg) {
      $.each(_subformCfg, function (key, item) {
        var _btOption = $('#' + key).bootstrapTable('getOptions');
        pageSize = _btOption.pageSize;
        formDatas = $('#' + key).bootstrapTable('getData');
        var tableContainer = $('#' + key).closest('.fixed-table-container');

        var pagination = item.pagination;
        if (pagination.isShow) {
          // this.$container.find('.fixed-table-body').css({
          //   'height': subformCfg.subFormHeight
          // });
          if (!formDatas.length) {
            tableContainer.find('.fixed-table-pagination').hide();
            return true;
          }
          if (!pagination.showTotal) {
            tableContainer.find('.pagination-detail').hide();
          }
          if (pagination.oneHide && formDatas.length <= Number(pageSize)) {
            tableContainer.find('.fixed-table-pagination').hide();
          }
          if (!pagination.onSizes) {
            tableContainer.find('.page-list').hide();
          }
        }
      });
    },
    /**
     * 填充从表数据.
     *
     * @param formUuid
     * @param formDatas
     */
    fillFormData: function (formDatas, optional) {
      var _self = this;
      var doFillFormData = function (formDatas, optional) {
        var _this = this;
        var ns = _this.options.namespace;
        if (typeof formDatas != 'undefined' && formDatas.length > 0) {
          _this.clearSubform(); // 清空从表数据
        }
        // 拷贝数据，原始数据用于收集时对比
        var localFormDatas = [];
        for (var i = 0, len = formDatas.length; i < len; i++) {
          var formData = formDatas[i];
          var localFormData = $.extend(
            {
              id: formData.uuid
            },
            formData
          );
          localFormDatas.push(localFormData);
        }
        _this.sortRowData(localFormDatas);
        // var $bt = _this.get$Placeholder();
        // $bt.bootstrapTable("load", localFormDatas);
        var formUuid = _this.options.formUuid;
        // 批量插入
        //			 for(var i =0;i<5;i++){
        //				 $.each(localFormDatas.concat(),function(){localFormDatas.push($.extend({},this,{uuid:"",id:""}))})
        //			 }
        var start = new Date().getTime();
        console.log('--------------------fillFormData start:', localFormDatas.length);

        //		 	$.each(localFormDatas, function(idx, localFormData){
        //		 		_this.addRowData(formUuid, localFormData, false, false);
        //		 	});
        _this.batchAddRowData(formUuid, localFormDatas, false, false);
        _this.invoke('afterFillFormData', localFormDatas);
        _this.runJsCode('afterFillFormData', localFormDatas);
        _this.setPagination();
        console.log('--------------------fillFormData end:', new Date().getTime() - start);
        if (optional && optional.callback) {
          optional.callback();
          _this.setPagination();
        }
      };
      var _waitForSubforms = appContext._waitForSubforms || {};
      if (optional && optional.asyncFillData) {
        _waitForSubforms[_self.getFormId()] = _self.getFormId();
        setTimeout(function () {
          try {
            doFillFormData.call(_self, formDatas, optional);
          } catch (e) {
            console.error(e);
          }
          delete _waitForSubforms[_self.getFormId()];
        }, 100);
      } else {
        doFillFormData.call(_self, formDatas, optional);
      }
      appContext._waitForSubforms = _waitForSubforms;
    },
    fillFormDataAsAsyn: function (formDatas, optional) {},
    /**
     * 填充从表数据.
     *
     * @param formUuid
     * @param formDatas
     */
    fillFormDataNC: function (formDatas) {
      var _this = this;
      var ns = _this.options.namespace;
      for (var i = 0; i < formDatas.length; i++) {
        var formData = formDatas[i];
        formData['id'] = formData.uuid;
      }
      _this.get$Placeholder().bootstrapTable('load', formDatas);
      _this.resetGridWidth();
    },
    /** 清空整个从表 */
    clearSubform: function () {
      var _this = this;
      // 标识所有数据为删除
      var dataUuids = _this.getRowIds();
      var formUuid = _this.options.formUuid;
      for (var i = 0; i < dataUuids.length; i++) {
        var rowId = dataUuids[i];
        _this.cacheDeleteRow({
          dataUuid: rowId,
          formUuid: formUuid
        });
      }
      _this.get$Placeholder().bootstrapTable('removeAll');
      _this.resetGridWidth();
    },

    /**
     * add time 2016-01-06 yuyq 清空从表的所有空行
     */
    clearSubformBlankRow: function () {
      var _this = this;
      var formUuid = _this.options.formUuid;
      appModal.confirm(dymsg.clearConfirm, function (result) {
        if (result) {
          var blankRowIds = _this.getSubformBlankRowIds();
          if (blankRowIds && blankRowIds.length > 0) {
            for (var i = 0; i < blankRowIds.length; i++) {
              _this.delRowData(formUuid, blankRowIds[i]);
            }
          }
        }
      });
    },

    /**
     * add by wujx 20160922 begin 获取空行ID
     */
    getSubformBlankRowIds: function () {
      var _this = this;
      var blackRowIds = [];
      var formUuid = _this.options.formUuid;
      var rows = _this.getData(); // 获取所有的数据
      var colModels = _this.getColModels();
      if (rows && rows.length > 0) {
        for (var i = 0; i < rows.length; i++) {
          var isNull = true,
            row = rows[i];
          for (var k = 0; k < colModels.length; k++) {
            var model = colModels[k];
            var fieldName = model.field;
            if (!(fieldName === 'seqNO' || fieldName === rowCheckItem) && row.hasOwnProperty(fieldName)) {
              if (null == row[fieldName] || row[fieldName].length <= 0) {
                continue; // 数组为空
              }
              isNull = false;
              break;
            }
          }

          if (isNull) {
            blackRowIds.push(row['id']);
          }
        }
      }
      return blackRowIds;
    },

    setReadOnly: function () {
      this.hideOperateBtn();
      this.options.readOnly = true;
      this.invokeCommonMethod('setReadOnly', true);
    },

    isReadOnly: function () {
      return this.options.readOnly == true;
    },

    isOperateBtnHide: function () {
      var subform = this.getSubformConfig();
      if (!subform.btnStatus) {
        return subform.hideButtons == dySubFormHideButtons.hide;
      } else {
        return subform.btnStatus == dySubFormHideButtons.hide;
      }
    },

    isOperateBtnShow: function () {
      var subform = this.getSubformConfig();
      if (!subform.btnStatus) {
        return subform.hideButtons == dySubFormHideButtons.show;
      } else {
        return subform.btnStatus == dySubFormHideButtons.show;
      }
    },

    hideOperateBtn: function () {
      var self = this;
      var subform = this.getSubformConfig();
      var tableBtn = subform.tableButtonInfo;
      var className = self.operationClazz.split(' ');

      var $container = self.$container;
      if ($container && self.operationClazz) {
        // $container.removeClass(self.operationClazz);
        $.each(tableBtn, function (index, item) {
          if (item.operate == '编辑类操作' || item.operate == 'edit') {
            if (self.defaultOperateButtonConfig[item.code]) {
              var i = className.indexOf(self.defaultOperateButtonConfig[item.code].visibleClass);
              if (i > -1) {
                $container.removeClass(className[i]);
              }
            } else {
              $container.find('.' + item.code).hide();
            }
          }
        });
      }
      self.hideColumn('rowCheckItem');
      self.hideColumn('_inline_operate');
      // var subform = this.getSubformConfig();
      // subform.btnStatus = dySubFormHideButtons.hide;
      // $container.find('.btn-customs').hide();
    },

    showOperateBtn: function () {
      var self = this;
      if (self.isReadOnly()) {
        // 只读不显示按钮
        return;
      }
      var $container = self.$container;
      if ($container && self.operationClazz) {
        $container.addClass(self.operationClazz);
      }
      self.showColumn('rowCheckItem');
      self.showColumn('_inline_operate');
      var subform = this.getSubformConfig();
      subform.btnStatus = dySubFormHideButtons.show;
      $container.find('.btn-customs').show();
    },

    setTextFile2SWF: function () {
      var _this = this;
      var datas = _this.get$PlaceHolder().bootstrapTable('getRowData');
      var colModels = _this.get$PlaceHolder().bootstrapTable('getVisibleColumns');
      for (var i = 0; i < datas.length; i++) {
        var data = datas[i];
        var rowId = data['id'];
        for (var j = 0; j < colModels.length; j++) {
          var fieldName = colModels[j].name;
          if (!subFormDefinition.isInputModeAsAttach(fieldName)) {
            continue;
          }
          // var ctlId = this.getCellId(rowId, fieldName);
          var control = _this.getControl(fieldName, rowId);
          if (typeof paramters == 'undefined') {
            control['setTextFile2SWF']();
          } else {
            control['setTextFile2SWF'](paramters);
          }
        }
      }
    },
    enableSignature: function (enable) {
      var _this = this;
      var subFormDefinition = _this.options.subformDefinition;
      var datas = _this.get$PlaceHolder().bootstrapTable('getRowData');
      var colModels = _this.get$PlaceHolder().bootstrapTable('getVisibleColumns');
      for (var i = 0; i < datas.length; i++) {
        var data = datas[i];
        var rowId = data['id'];
        for (var j = 0; j < colModels.length; j++) {
          var fieldName = colModels[j].name;
          if (!subFormDefinition.isInputModeAsAttach(fieldName)) {
            continue;
          }
          // var ctlId = this.getCellId(rowId, fieldName);
          var control = _this.getControl(fieldName, rowId);
          if (typeof paramters == 'undefined') {
            control['enableSignature']();
          } else {
            control['enableSignature'](paramters);
          }
        }
      }
    },
    setEditable: function () {
      var self = this;
      self.options.readOnly = false;

      var formUuid = self.options.formUuid;
      var $context = self.options.$paranentelement;
      if ($context.$('#gbox_' + formUuid + ' .collapsesubform ').attr('id') == 'notOpenDiv') {
        // 折叠状态时不展示操作按钮
        // $("#" + operateBtn).hide();
        self.hideOperateBtn();
      } else {
        self.showOperateBtn();
      }
      var allControls = self.getAllControls();
      $.each(allControls, function (i, ctl) {
        ctl.setEditable();
      });
    },
    setDisplayAsLabel: function () {
      // this.setReadOnly();
      var self = this;
      self.options.readOnly = true;
      self.hideOperateBtn();
      self.invokeCommonMethod('setDisplayAsLabel');
    },
    invokeCommonMethod: function (methodName, paramters) {
      var _this = this;
      var datas = _this.get$PlaceHolder().bootstrapTable('getData');
      var colModels = _this.get$PlaceHolder().bootstrapTable('getVisibleColumns');
      for (var i = 0; i < datas.length; i++) {
        var data = datas[i];
        var rowId = data['id'];
        for (var j = 0; j < colModels.length; j++) {
          var colModel = colModels[j];
          var fieldName = colModel.field;
          if (!this.isControlFieldInSubform(fieldName)) {
            continue;
          }
          // var ctlId = this.getCellId(rowId, fieldName);

          var control = _this.getControl(fieldName, rowId);
          if (typeof control == 'undefined' || control == null) {
            continue;
          }

          typeof paramters == 'undefined' ? control[methodName]() : control[methodName](paramters);
        }
      }
    },

    get$Layout: function () {
      if (this.options.$layout) {
        return this.options.$layout;
      }

      var formUuid = this.options.formUuid;
      var ns = this.options.namespace || formUuid;
      var subformTree = window.reverseSubformTree[ns] || {};
      var layoutNode = subformTree[formUuid] && subformTree[formUuid].parent;
      while (layoutNode && !layoutNode.isLayout) {
        layoutNode = layoutNode.parent;
      }

      if (layoutNode) {
        this.options.$layout = $.ContainerManager.getLayout(layoutNode.symbol);
      } else {
        this.options.$layout = null;
      }

      return this.options.$layout;
    },

    isValidationNeeded: function () {
      return this.isValidateOnHidden() || !this.isHide();
    },

    isValidateOnHidden: function () {
      return !!this.options.formDefinition.subforms[this.options.formUuid].validateOnHidden;
    },

    /**
     * 隐藏从表
     */
    hide: function () {
      var $container = this.$container;
      this.options.visible = false;
      $container && $container.hide();
    },

    isHide: function () {
      // 从表本身隐藏
      if (this.options.visible === false) {
        return true;
      }

      // 控件父级隐藏
      var $layout = this.get$Layout();
      while ($layout) {
        if ($layout.isHide()) {
          return true;
        }
        $layout = $layout.get$Layout();
      }
      return false;
    },

    /**
     * 显示从表
     */
    show: function () {
      var $container = this.$container;
      console.log('显示从表', this.$container);
      $container && $container.show();
      this.options.visible = true;
    },
    $thisContext: function () {
      return this.$container;
    },
    thisContext: function () {
      return this.$thisContext()[0];
    },

    $thisFormContext: function () {
      // 表单上下文
      return this.options.$paranentelement;
    },
    /**
     * 从表占位符
     */
    get$PlaceHolder: function () {
      return this.$element;
    },

    // 获取选中的行
    getSelectedRows: function () {
      var self = this;
      var $placeHolder = self.get$PlaceHolder();
      $placeHolder.bootstrapTable('updateRows');
      var rows = $placeHolder.bootstrapTable('getSelections');
      return rows;
    },
    // 获取选中的行
    getSelectedRowOnly: function () {
      var rows = this.getSelectedRows();
      return rows ? rows[0] : null;
    },
    // 获取被选中行的行ID
    getSelectedRowId: function () {
      var rows = this.getSelectedRows();
      if (rows && rows.length > 0) {
        return rows[0].id;
      }
    },

    // 获取被选中行的行ID(Array)
    getSelectedRowIds: function () {
      var rows = this.getSelectedRows();
      var ids = [];
      if (rows && rows.length > 0) {
        for (var i = 0; i < rows.length; i++) {
          ids.push(rows[i].id);
        }
      }
      return ids;
    },

    group: function (fnSuccess, fnFail) {
      var _this = this;
      var ns = _this.options.namespace;
      if (!this.options.groupField) {
        return;
      }
      // 分组刷新..
      var formUuid = _this.options.formUuid;
      // 获得表单数据
      this.collectSubformData(
        function (formdatas) {
          $($.dyform.nss(ns, '#' + formUuid)).bootstrapTable('clearGridData');
          // 重新填充
          _this.fillFormData(formdatas);
          if (fnSuccess) {
            fnSuccess.call(this);
          }
        },
        function (errorInfo) {
          console.error(errorInfo);
          if (fnFail) {
            fnFail.call(this);
          }
        }
      );
    },
    isFieldHidden: function (field) {
      var hidden = typeof field.hidden == 'undefined' ? false : dySubFormFieldShow.notShow == field.hidden; // 是否隐藏
      return hidden;
    },
    isFieldEditable: function (field) {
      var editable = typeof field.editable == 'undefined' ? true : dySubFormFieldEdit.notEdit != field.editable; // 是否隐藏
      return editable;
    },

    /**
     * 定义从表折叠事件
     *
     * @param collapse
     */
    definiteCollapse: function (collapse) {
      var _this = this;
      var $context = _this.$thisContext();
      $context.find('.collapsesubform').unbind('click');
      $context.find('.collapsesubform').bind('click', function (event) {
        // 设置自定义的collapse/expand按钮
        var $siblings = $(this).parent().parent().siblings();
        var id = $(this).attr('id');
        if (id == 'openDiv') {
          $siblings.hide();
          if (!_this.isOperateBtnHide()) {
            _this.hideOperateBtn();
          }
          $(this).attr('id', 'notOpenDiv');
        } else {
          $siblings.show();
          if (!_this.isOperateBtnShow()) {
            _this.showOperateBtn();
          }
          $(this).attr('id', 'openDiv');
        }
        $(window).trigger('resize'); // 触发重新设置从表的宽度
        event.preventDefault();
        event.stopPropagation();
        return false;
      });
      if (collapse == dySubFormTableOpen.notOpen) {
        _this.collapse();
      } else {
        _this.expand();
      }
    },

    /**
     * 从表折叠接口
     */
    collapse: function () {
      var _this = this;
      _this.get$Placeholder().bootstrapTable('collapseAllRows');
    },

    /**
     * 从表展开接口
     */
    expand: function () {
      var _this = this;
      _this.get$Placeholder().bootstrapTable('expandAllRows');
    },

    /**
     * 隐藏列
     *
     * @param fieldName
     */
    hideColumn: function (fieldName) {
      var _this = this;
      _this.get$Placeholder().bootstrapTable('hideColumn', fieldName);
    },

    /**
     * 展示列
     *
     * @param fieldName
     */
    showColumn: function (fieldName) {
      var _this = this;
      _this.get$Placeholder().bootstrapTable('showColumn', fieldName);
    },

    /**
     * 隐藏序号
     *
     * @param
     */
    hideSeqNo: function () {
      return this.hideColumn('seqNO');
    },

    /**
     * 展示序号
     *
     * @param
     */
    showSeqNo: function () {
      return this.showColumn('seqNO');
    },
    /**
     * 隐藏单选
     *
     * @param
     */
    hideCheckColumn: function () {
      return this.hideColumn(rowCheckItem);
    },

    /**
     * 展示单选
     *
     * @param
     */
    showCheckColumn: function () {
      return this.showColumn(rowCheckItem);
    },

    /**
     * 隐藏操作列
     *
     * @param
     */
    hideOperateColumn: function () {
      return this.hideColumn('_inline_operate');
    },

    /**
     * 展示操作列
     *
     * @param
     */
    showOperateColumn: function () {
      return this.showColumn('_inline_operate');
    },

    /**
     * 设置整列显示为标签
     *
     * @param fieldName
     */
    setColumnAsLabel: function (fieldName) {
      var _this = this;
      var colModels = _this.get$PlaceHolder().bootstrapTable('getVisibleColumns');
      for (var j = 0; j < colModels.length; j++) {
        if (colModels[j].name == fieldName || colModels[j].field == fieldName) {
          colModels[j].editable.editable = false;
          colModels[j].editable.control.fieldDefinition.showType = '3';
          // 数据控件设置为只读
          var dataList = _this.getData();
          $.each(dataList, function (i, data) {
            var rowId = data.id || data.uuid;
            var control = _this.getControl(fieldName, rowId);
            if (control != null) {
              control.setReadOnly(true);
              control.setEditable(false);
            }
          });
          break;
        }
      }
    },

    /**
     * 获取控件 add by wujx 20161109
     *
     * @param fieldName
     * @param dataUuid
     * @returns
     */
    getControl: function (fieldName, dataUuidOfSubform) {
      var self = this;
      var $dyform = self.$thisFormContext();
      return $dyform.getControl(fieldName, dataUuidOfSubform);
    },

    /**
     * 获取所有从表控件
     *
     * @param rowId 获取指定行所有控件
     */
    getAllControls: function (rowId) {
      var self = this;
      var rowIds = rowId ? [rowId] : self.getRowIds();
      var allControls = [];
      var colFields = [];
      $.each(self.getColModels(), function (m, item) {
        if ($.inArray(item.field, ['seqNO', 'rowCheckItem', '_inline_operate']) < 0) {
          colFields.push(item.field);
        }
      });
      $.each(rowIds, function (i, item) {
        $.each(colFields, function (k, field) {
          allControls.push(self.getControl(field, item));
        });
      });
      return allControls;
    },

    /**
     * 设置整列可编辑
     *
     * @param fieldName
     */
    setColumnEditable: function (fieldName) {
      var _this = this;
      var colModels = _this.get$PlaceHolder().bootstrapTable('getVisibleColumns');
      for (var j = 0; j < colModels.length; j++) {
        if (colModels[j].name == fieldName || colModels[j].field == fieldName) {
          colModels[j].editable.editable = true;
          colModels[j].editable.control.fieldDefinition.showType = '1';
          // 数据控件设置为可编辑
          var dataList = _this.getData();
          $.each(dataList, function (i, data) {
            var rowId = data.id || data.uuid;
            var control = _this.getControl(fieldName, rowId);
            if (control != null) {
              control.setReadOnly(false);
              control.setEditable(true);
            }
          });
          break;
        }
      }
    },
    countGridWidth: function ($ele) {
      if ($ele.parents('.tab-content').length) {
        var tabs = $ele.parents('.tab-content');
        var outermostTab = $(tabs[tabs.length - 1]);
        var tableOuterTdWidth = outermostTab.width() - 30 * (tabs.length - 1);
        if ($ele.parent().prev().hasClass('label-td')) {
          var labelTd = $ele.parent().prev();
          var labelTdWidth;
          if (labelTd.is(':visible')) {
            labelTdWidth = labelTd.outerWidth();
          } else {
            labelTdWidth = 120;
          }
          tableOuterTdWidth -= labelTdWidth + 30;
        }
      } else {
        var dyformWidth = $ele.closest('.dyform').width();
        var labelTdWidth = $ele.parent().prev().outerWidth();
        var tableOuterTdWidth = dyformWidth - labelTdWidth - 30;
        if (!$ele.parent().prev().is('td')) {
          tableOuterTdWidth = dyformWidth;
        }
      }
      return tableOuterTdWidth;
    },
    resetGridWidth: function () {
      var self = this;
      var subform = self.getSubformConfig();
      if (subform.horizontalScroll) {
        var tableOuterTdWidth = self.countGridWidth(self.$container);
        self.$element
          .closest('.fixed-table-container')
          .find('table')
          .css({
            width: 'auto',
            'min-width': tableOuterTdWidth + 'px'
          });
        self.$element.closest('.fixed-table-container').css('width', tableOuterTdWidth + 'px');
      } else {
        self.$element.closest('.fixed-table-container').find('table').css({
          width: ''
        });
      }

      self.resetWidthTid && clearTimeout(self.resetWidthTid);
      self.resetWidthTid = setTimeout(function () {
        self.get$PlaceHolder().bootstrapTable('resetWidth');
        delete self.resetWidthTid;
      }, 1000 / 12);
    },
    // 将运算公式及公式依附的列放到缓存中
    addFormula: function (fieldName, formula) {
      $.ControlManager.registFormula(formula);
    },
    isColumnDisplayAsCtl: function (model) {
      return !this.options.readOnly && model.editable && model.controlable;
    },

    /* 将运算公式注册到控件中 */
    registFormula: function (control) {
      var parentThis = this.options.$paranentelement;
      var allformulas = parentThis.cache.get.call(parentThis, cacheType.formula);

      var formId = this.options.subformDefinition.outerId || this.options.subformDefinition.id;
      var dataUuid = control.getDataUuid();
      var fieldName = control.getFieldName();

      // console.log(control.getCtlName() + ":" + fieldName);
      var formulaPlaceHolder1 = '${' + formId + ':' + fieldName + '}'; // 指定到列,该公式对所有列有效
      // console.log(formulaPlaceHolder1);
      var formulaPlaceHolder2 = control.getCtlName(); // 指定到行,该公式对行有效

      // 指定到列
      var fieldFormulas = allformulas[formulaPlaceHolder1];
      if (typeof fieldFormulas != 'undefined') {
        // 找到运算公式
        if (control.addAllFormulas) {
          control.addAllFormulas(formulaPlaceHolder1, fieldFormulas);
        }
      }

      // 指定到行
      var fieldFormulas = allformulas[formulaPlaceHolder2];
      if (typeof fieldFormulas != 'undefined') {
        // 找到运算公式
        if (control.addAllFormulas) {
          control.addAllFormulas(formulaPlaceHolder2, fieldFormulas);
        }
      }
    },
    /* 设置列宽度 */
    setModelWidth: function (model, width) {
      if ($.trim(width).length > 0) {
        // 列宽度
        model.width = width;
      }
    },
    /* 宽度自适应 */
    getAutoWidth: function () {
      if (typeof this.options.autoWidth == 'undefined' || this.options.autoWidth == 'true' || this.options.autoWidth == true) {
        return true;
      } else {
        return false;
      }
    },
    getFormId: function () {
      return this.options.subformDefinition.outerId || this.options.subformDefinition.id;
    },
    getDisplayName: function () {
      return this.options.subformDefinition.name;
    },
    isValidateIgnore: function (fieldName, formId) {
      return $.dyform.isValidateIgnore(fieldName, formId);
    },
    // 刷新数据
    refreshFormDatas: function (formDatas, callback) {
      var _this = this;
      this.collectSubformData(
        function (currentFormDatas) {
          if (typeof currentFormDatas == undefined || currentFormDatas == null || currentFormDatas.length == 0) {
            _this.fillFormData(formDatas);
          } else {
            for (var i = 0; i < formDatas.length; i++) {
              var formData = formDatas[i];
              var rowId = formData['uuid'];
              for (var fieldName in formData) {
                if (fieldName == 'uuid' || fieldName == undefined) {
                  continue;
                }
                _this.setFieldValue(fieldName, formData[fieldName], rowId);
              }
            }
          }
          if (callback) {
            callback();
          }
        },
        function (errorInfo) {
          console.error(errorInfo);
        }
      );
    },
    setFieldValue: function (fieldName, value, rowId) {
      var _this = this;
      var control = _this.getControl(fieldName, rowId);
      if (control != undefined && control != null) {
        if ($.dyform.getValue(control) != value) {
          $.dyform.setValue(control, value, rowId);
        }
      }
    },
    /**
     * 生成标题html
     */
    genDisplayNameHtml: function () {
      var displayName = this.getSubformConfig().displayName;
      if ($.trim(displayName).length) {
        return "<div class='subform-title'>" + this.getSubformConfig().displayName + '</div>';
      }
      return '';
    },

    get$displayNameElem: function () {
      return this.$thisContext().find('.subform-title');
    },

    /**
     * 设置从表标题
     */
    setDisplayName: function (displayName) {
      this.get$displayNameElem().html(displayName);
    },

    /**
     * 设置从表最大高度
     * @param height
     */
    setSubformTableMaxHeight: function (height) {
      var _this = this;
      _this.$element.bootstrapTable('resetView', {
        height: height,
        maxHeight: height
      });
    },

    hideRow: function (rowId) {
      var _this = this;
      _this.get$PlaceHolder().bootstrapTable('hideRow', {
        uniqueId: rowId
      });
    },

    showRow: function (rowId) {
      var _this = this;
      _this.get$PlaceHolder().bootstrapTable('showRow', {
        uniqueId: rowId
      });
    },

    isRowHidden: function (rowId) {
      var _this = this;
      _this.get$PlaceHolder().bootstrapTable('getRowsHidden');
      for (var i = 0, len = rowsHidden.length; i < len; i++) {
        if (rowsHidden[i].id === rowId) {
          return true;
        }
      }
      return false;
    },
    getJsCodeByMethod: function (method) {
      var _this = this;
      var events = _this.getSubformConfig().events;
      var jsCode = '';
      if (events) {
        for (var i in events) {
          if (events.hasOwnProperty(i)) {
            if (i == method) {
              jsCode = events[i];
            }
          }
        }
      }
      return jsCode;
    },

    runJsCode: function (methd, data, callBack) {
      var jsCode = this.getJsCodeByMethod(methd);
      if (!_.isEmpty(jsCode)) {
        console.debug(methd + ' 执行自定义脚本: ' + jsCode);
        this.runCode(jsCode, $(this), data, callBack);
      }
    },
    runCode: function (jsCode, $this, data, callBack) {
      var self = this;
      if (!_.isEmpty(jsCode)) {
        console.debug(' 执行自定义脚本: ' + jsCode);
        appContext.eval(
          jsCode,
          $(this),
          {
            data: data,
            dysubform: self,
            selectedRowId: null,
            selectedRowIds: null
          },
          callBack
        );
      }
    },
    fixedHeader: function (scrollContainerSelector, topContainerSelector) {},
    isGroupForm: function () {
      var self = this;
      var groupField = self.options.groupField;
      return (groupField == null || typeof groupField === 'undefined') === false;
    }
  };

  /**
   * 表单内敛展示
   */
  $.wSubFormMethod2 = {
    init: function () {
      var _this = this;
      var _paranentthis = _this.options.$paranentelement;
      var _tableelement = _this.get$Placeholder().empty();
      var ns = _paranentthis.attr('data-ns');
      if ($.trim(ns).length) {
        _this.options.namespace = ns;
      }
      // 初始化
      var formUuid = _this.options.formUuid;
      // 获取从表定义
      var subform = _this.getSubformConfig();
      var subFormDefinition = _this.options.subformDefinition; // 加载从表对应的那张表的完整定义
      if (subform == null || subFormDefinition == null) {
        // 在定义中找不到该表单
        _tableelement.remove(); // 找不到定义就直接删除
        return true;
      }

      // 从表的table的id属性值为formId,
      _this.$container = _tableelement;
      _tableelement
        .attr({
          formId: subform.outerId
        })
        .removeAttr('title');
      _tableelement.addClass('table-form');
      subform.codeButtonEvents = codeButtonEvents;
      // 收集从表列信息
      var colModels = (_this.colModels = []); // 各列的配置信息
      // 序号
      // 提取为变量,后面更新frozen属性（有固定列,则序号也固定）
      var seqNOColModel = {
        field: 'seqNO',
        index: 'seqNO',
        title: '序号',
        width: '45px',
        visible: !subform.hideSeqNo
      };
      var fields = subform.fields;
      for (var i in fields) {
        var field = fields[i];
        var fieldDef = subFormDefinition.fields[field.name];
        if (fieldDef == undefined) {
          throw new Error('从表[' + subform.displayName + '(' + subFormDefinition.name + ')]中不存在[字段' + field.name + ']的定义');
        }
        if (fieldDef.inputMode == '6' || fieldDef.inputMode == '4' || fieldDef.inputMode == '33') {
          field.editable = '1';
        }
        var model = _this.getColModel(fields[field.name], subform, ns);
        if ((model.sortable = !!field.sort)) {
          model.sortorder = field.sort.indexOf('asc') < 0 ? 'desc' : 'asc'; // 是否支持点击排序
        }
        model.visible = _this.isFieldHidden(field) === false;
        model.editable = _this.isFieldEditable(field);
        model.controlable = field.controlable && field.controlable == dySubFormFieldCtl.control ? true : false;

        var fieldDef = subFormDefinition.fields[field.name];
        if (fieldDef == undefined) {
          throw new Error('从表[' + subform.displayName + '(' + subFormDefinition.name + ')]中不存在[字段' + field.name + ']的定义');
        }
        if ((model.frozen = field.frozen) === true) {
          seqNOColModel.frozen = true;
        }
        model.inputMode = fieldDef.inputMode;
        // 从表配置应用到表单
        _this.setFieldModel(fieldDef, model);
        colModels.push(model);
        // 添加运算公式
        var formula = $.trim(field.formula);
        if (typeof formula === 'string' && formula.indexOf('function') === 0) {
          try {
            model.editable.subform = _this;
            model.editable.formatter = new Function('return ' + formula)();
            formula = null;
          } catch (ex) {
            // 兼容旧方式
          }
        }
        if (typeof formula != 'undefined' && $.trim(formula).length > 0) {
          _this.addFormula(field.name, formula);
        }
      }
      var headerBtns = [],
        inlineBtns = [],
        codeButtonEvents = {},
        ignoreCodes = ['btn_add_sub', 'btn_edit'];
      $.each(subform.tableButtonInfo || [], function (idx, button) {
        // 一些固化按钮位置写死
        if ($.inArray(button.code, ignoreCodes) > -1) {
          return;
        } else if (button.code === 'btn_add') {
          button.position = ['1', '2'];
        } else if (button.code === 'btn_del' || button.code === 'btn_edit') {
          button.position = ['2'];
        } else if (button.code === 'btn_clear' || button.code === 'btn_exp_subform' || button.code === 'btn_imp_subform') {
          button.position = ['1'];
        }
        var positions = button.position;
        var buttonHtml = new commons.StringBuilder();
        var btnClass = StringUtils.isBlank(button.cssClass) ? 'btn-primary' : button.cssClass + ' ' + button.cssStr;
        if (button.icon && button.icon.className) {
          btnClass += ' ' + button.icon.className;
        }
        buttonHtml.appendFormat(
          "<button type='button' class='btn {0} {1}' action='{1}' title='{2}'>{3}</button>",
          btnClass,
          button.code,
          button.text,
          button.text
        );
        // 头部按钮
        if ($.inArray('1', positions) != -1 && subform.layoutType != '1') {
          headerBtns.push(buttonHtml.toString());
        } else if ($.inArray('1', positions) != -1 && subform.layoutType == '1' && button.code === 'btn_add') {
          headerBtns.push(buttonHtml.toString());
        }
        // 行内按钮
        if ($.inArray('2', positions) != -1) {
          inlineBtns.push(buttonHtml.toString());
        }
        codeButtonEvents[button.code] = button.buttonEvents;
      });
      subform.headerBtnTemplate = headerBtns.join('');
      subform.inlineBtnTemplate = inlineBtns.join('');
      subform.codeButtonEvents = codeButtonEvents;
      _this._initEvent(subform);
      _this.clearSubform();
      if (subform.showType == '5') {
        _this.hide();
      }
    },
    /**
     * 根据fieldDefinition获得gridmodel
     *
     * @param fieldDefinition
     */
    getColModel: function (field, subform, ns) {
      var _this = this;
      var model = {};
      model.field = field.name;
      model.index = field.name;
      model.title = field.displayName;
      model.align = field.textAlign;
      return model;
    },
    setFieldModel: function (fieldDef, model) {
      fieldDef.textAlign = model.align;
      fieldDef.width = model.width || fieldDef.width;
      // 优先级：隐藏》只读》可编辑
      if (false === model.visible) {
        fieldDef.showType = '5';
      } else if (false === model.editable) {
        fieldDef.showType = fieldDef.showType === '2' ? '2' : '3';
      } else if (model.controlable) {
        fieldDef.showType = '1';
      }
    },
    getSubDyforms: function () {
      var self = this;
      return self.$container.find('.table-form-item>.dyform');
    },
    /**
     * 获取控件 add by wujx 20161109
     *
     * @param fieldName
     * @param dataUuid
     * @returns
     */
    getControl: function (fieldName, dataUuidOfSubform) {
      var self = this;
      var $dyform = self.$container.find($.dyform.nss(dataUuidOfSubform, '.table-form-item>.dyform'));
      var ns = self.options.namespace;
      var ctl = $dyform.getControl(fieldName, dataUuidOfSubform);
      if (null == ctl && $.trim(ns).length) {
        ctl = $dyform.getControl(fieldName, ns + '___' + dataUuidOfSubform);
      }
      return ctl;
    },
    // 获取被选中行的行ID
    getSelectedRowId: function () {
      var self = this;
      return self.$container.attr('selectedRowId');
    },
    // 获取被选中行的行ID(Array)
    getSelectedRowIds: function () {
      var self = this;
      var selectedRowId = self.getSelectedRowId();
      return $.trim(selectedRowId).length <= 0 ? [] : [selectedRowId];
    },
    /**
     * 删除从表的某行数据
     *
     * @param formUuid
     * @param data
     *            数据格式：{name1:value1,name2: value2…} name为在colModel中指定的名称
     */
    delRowData: function (formUuid, rowId) {
      var _this = this;
      var ns = _this.options.namespace;
      var t = formUuid;
      if (arguments.length == 1) {
        formUuid = _this.options.formUuid;
        rowId = t;
      }
      var rowData = {};
      rowData.rowId = rowId;
      rowData.formUuid = formUuid;
      var flag = _this.invoke('beforeDeleteRow', rowData);
      // 执行自定义JS脚本
      _this.runJsCode('beforeDeleteRow', rowData, function (e) {
        flag = e;
      });
      if (flag === false) {
        return;
      }
      _this.$container.find('tr[data-rowid=' + rowId + ']').remove();
      delete _this.dyformExplains[rowId];
      _this.removeValidateBlankRow();

      _this.cacheDeleteRow({
        dataUuid: rowId,
        formUuid: formUuid
      });
      _this.invoke('afterDeleteRow', rowId);
      _this.runJsCode('afterDeleteRow', rowData);
    },
    // 上移
    upSubformRowDataEvent: function (formUuid, selectRowId) {
      var self = this;
      var $selRow = self.$container.find('tr[data-rowid=' + selectRowId + ']');
      var $prevRow = $selRow.prev();
      if ($prevRow && $prevRow.length > 0) {
        $selRow.after($prevRow);
      }
    },
    // 下移
    downSubformRowDataEvent: function (formUuid, selectRowId) {
      var self = this;
      var $selRow = self.$container.find('tr[data-rowid=' + selectRowId + ']');
      var $nextRow = $selRow.next();
      if (!$nextRow || $nextRow.length <= 0 || $nextRow.is('tr.table-form-th')) {
        return;
      }
      $selRow.before($nextRow);
    },
    addRowData: function (formUuid, data, controlable, seqNOable, insertRowIndex) {
      var self = this;
      var t = formUuid,
        beforeRowId;
      if (arguments.length == 1) {
        formUuid = self.options.formUuid;
        data = t;
      }
      if (typeof insertRowIndex === 'number') {
        beforeRowId = self.getData()[insertRowIndex].id;
      } else {
        beforeRowId = self.getSelectedRowId();
      }
      return self.beforeRowData(beforeRowId, formUuid, data, controlable, seqNOable);
    },
    beforeRowData: function (beforeRowId, formUuid, data, controlable, seqNOable) {
      var self = this;
      var flag = true,
        fields;
      var jsCode = self.getJsCodeByMethod('beforeInsertRow');
      if (!_.isEmpty(jsCode)) {
        appContext.eval(
          jsCode,
          $(self),
          {
            data: data
          },
          function (e) {
            flag = e;
          }
        );
      }
      if (flag === false) {
        return;
      } else if ($.trim(data['uuid']).length <= 0) {
        // 调用方没有生成行id,这里也为dataUuid
        data['uuid'] = data['id'] || self.createUuid();
        if ((fields = self.options.subformDefinition.fields)) {
          for (var fieldName in fields) {
            var field = fields[fieldName] || {};
            if (data[fieldName] == null && field.defaultValue != null) {
              data[fieldName] = field.defaultValue;
            }
          }
        }
      } else if ($.trim(data['id']).length <= 0) {
        data['id'] = data['uuid'];
      }
      var options = self.options;
      var ns = options.namespace;
      var subforn = self.getSubformConfig();
      var subFormDefinition = options.subformDefinition;
      // 是否从表嵌套从表
      var isNestSubform = $.isEmptyObject(subFormDefinition.subforms) === false;
      // 表单数据处理(加载从表数据和从表数据字典项)
      var DyformFunction = requirejs('DyformFunction'),
        formData,
        nestformDatas;
      if (false === isNestSubform || data.nestformDatas) {
        // 从表嵌套从表，加载从表数据
        formData = {
          formDatas: {},
          addedFormDatas: {},
          deletedFormDatas: {},
          updatedFormDatas: {},
          formId: subFormDefinition.name,
          formUuid: subFormDefinition.uuid,
          formDefinition: subFormDefinition
        };
        formData.formDatas[formUuid] = [data];
        if ((nestformDatas = data.nestformDatas)) {
          if (typeof nestformDatas === 'string') {
            nestformDatas = JSON.parse(nestformDatas);
          }
          $.extend(formData.formDatas, nestformDatas.formDatas);
          formData.formDefinition = nestformDatas.formDefinition || formData.formDefinition;
          delete data.nestformDatas;
        }
      } else {
        formData = DyformFunction.loadFormDefinitionData(formUuid, data['id']);
      }
      // 从表保持namespace与dataUuid相同
      if (null == formData.dataUuid) {
        formData.dataUuid = data['id'];
      } else if (formData.dataUuid != data['id']) {
        data['id'] = formData.dataUuid;
      }
      // 定义
      if (null == self.fromDefinitionMerged && isNestSubform) {
        // 字段串类型
        var formDefinition = formData.formDefinition;
        if (typeof formDefinition === 'string') {
          formDefinition = eval('(' + formDefinition + ')');
        }
        formDefinition.fields = subFormDefinition.fields; // 拷贝从表字段配置，保留嵌套从表数据字典
        subFormDefinition = options.subformDefinition = formDefinition;
        self.fromDefinitionMerged = true;
      }
      formData.formDefinition = subFormDefinition;

      var $rowTemplate = $(
        '<tr class="btn-inline"><td><div class="table-form-item"><div class="table-form-header"></div><div class="table-form-body"></div><div class="table-form-footer"></div></div></td></tr>'
      );
      var $rowHeader = $rowTemplate.find('.table-form-header').append(subforn.inlineBtnTemplate);
      var $rowBody = $rowTemplate.find('.table-form-body');
      $rowTemplate.attr('data-rowid', data['id']);
      if (beforeRowId == null) {
        self.$container.find('tr.table-form-th').before($rowTemplate);
      } else {
        self.$container.find('tr[data-rowid=' + beforeRowId + ']').before($rowTemplate);
      }
      var DyformExplain = requirejs('DyformExplain');
      var dyformExplain = new DyformExplain($rowBody, {
        renderTo: $rowBody,
        namespace: ($.trim(ns).length ? ns + '___' : '') + data['id'],
        formData: formData,
        displayAsLabel: this.options.$paranentelement.isDisplayAsLabel(), //从表展示状态与父表单相同
        optional: {
          isFirst: data['newRow']
        }
      });
      self.dyformExplains = self.dyformExplains || {};
      self.dyformExplains[data['id']] = dyformExplain;
      if (data['afterInsertRow'] === false) {
      } else {
        self.invoke('afterInsertRow', data);
        self.runJsCode('afterInsertRow', data);
      }

      self.resetGridWidth();
      return data['id'];
    },
    /**
     * 更新从表的行数据
     *
     * @param formUuid
     * @param data
     */
    updateRowData: function (formUuid, data) {
      var _this = this;
      var t = formUuid,
        dyformExplain;
      if (arguments.length == 1) {
        formUuid = _this.options.formUuid;
        data = t;
      }
      var rowId = data['id'] || data['uuid'];
      if (typeof rowId == 'undefined') {
        throw new Error('id is not defined');
      } else if ((dyformExplain = _this.dyformExplains[rowId])) {
        dyformExplain.fillFormDataOfMainform(data);
      }
    },
    fillFormData: function (formDatas, optional) {
      if (optional && optional.async) {
      }
      var _this = this;
      var ns = _this.options.namespace;
      if (typeof formDatas != 'undefined' && formDatas.length > 0) {
        _this.clearSubform(); // 清空从表数据
      }
      // 拷贝数据，原始数据用于收集时对比
      var localFormDatas = [];
      for (var i = 0, len = formDatas.length; i < len; i++) {
        var formData = formDatas[i];
        var localFormData = $.extend(
          {
            id: formData.uuid
          },
          formData
        );
        localFormDatas.push(localFormData);
      }
      _this.sortRowData(localFormDatas);
      var formUuid = _this.options.formUuid;
      $.each(localFormDatas, function (idx, localFormData) {
        _this.addRowData(formUuid, localFormData, false, false);
      });
      _this.invoke('afterFillFormData', localFormDatas);
      _this.runJsCode('afterFillFormData', localFormDatas);
      if (optional && optional.callback) {
        optional.callback();
      }
    },
    getData: function () {
      var _this = this;
      var result = [];
      var rowIds = _this.getRowIds();
      var formUuid = _this.getFormUuid();
      for (var j = 0; j < rowIds.length; j++) {
        // 遍历从表的各行
        var rowData = _this.getRowData(formUuid, rowIds[j]);
        rowData['seqNO'] = rowData['seqNO'] || j + '';
        result.push(rowData);
      }
      return result;
    },
    /**
     * 获得行数据
     *
     * @param formUuid
     * @param rowId
     */
    getRowData: function (formUuid, rowId, fnSuccess, fnError) {
      var _this = this;
      var t = formUuid;
      if (arguments.length == 1) {
        formUuid = this.options.formUuid;
        rowId = t;
      }
      if (rowId == null || typeof rowId === 'undefined') {
        return fnError && fnError.call(_this, 'rowId不允许为空');
      }
      var subformData = {},
        dyformExplain;
      subformData['uuid'] = rowId;
      if ((dyformExplain = _this.dyformExplains[rowId])) {
        dyformExplain.collectFormData(
          function (formDatas) {
            var formData = formDatas;
            var data = formData.formDatas[formUuid][0];
            data['id'] = data['id'] || data['uuid'];
            delete formData.formDatas[formUuid]; // 删除主表数据
            if (false == $.isEmptyObject(formData.formDatas)) {
              // 还有嵌套从表数据
              data.nestformDatas = JSON.stringify(formData);
            }
            $.extend(subformData, data);
          },
          function (errorInfo) {
            console.log(errorInfo);
          }
        );
      }
      fnSuccess && fnSuccess.call(_this, subformData);
      return subformData;
    },
    /**
     * 获得行数据
     *
     * @param formUuid
     * @param rowId
     */
    getRowDisplayData: function (formUuid, rowId) {
      var t = formUuid;
      var _this = this;
      if (arguments.length == 1) {
        formUuid = this.options.formUuid;
        rowId = t;
      }
      return _this.getRowData();
    },
    /**
     * 获取从表的数据
     *
     * @param formUuid
     *            从表定义Uuid
     */
    collectSubformData: function (fnSuccess, fnError) {
      var _this = this;
      var datas = _this.getData();
      fnSuccess.call(_this, datas);
    },
    /**
     * 设置控件的值
     *
     * @param control
     * @param cellValue
     * @param dataUuid
     */
    setValue: function (control, cellValue, dataUuid) {
      if (!(typeof control === 'string')) {
        console.error('control must be string');
        return;
      }
      var rowData = {
        id: dataUuid
      };
      rowData[control] = cellValue;
      return this.updateRowData(rowData);
    },
    setFieldValue: function (fieldName, value, rowId) {
      var _this = this;
      return _this.setValue(fieldName, value, rowId);
    },
    getRowIds: function () {
      var self = this;
      return self.$container.find('tr[data-rowid]').map(function (idx, element) {
        return $(element).attr('data-rowid');
      });
    },
    getColModels: function () {
      return this.colModels;
    },
    getColModelByFieldName: function (fieldName) {
      return $.grep(this.getColModels(), function (field, idx) {
        return field.field === fieldName;
      })[0];
    },
    clearSubform: function () {
      var _this = this;
      var ns = _this.options.namespace;
      // 标识所有数据为删除
      var dataUuids = _this.getRowIds();
      var formUuid = _this.options.formUuid;
      for (var i = 0; i < dataUuids.length; i++) {
        var rowId = dataUuids[i];
        _this.cacheDeleteRow({
          dataUuid: rowId,
          formUuid: formUuid
        });
      }
      self.dyformExplains = {};
      var rowTemplate = '<tr class="btn-inline table-form-th"><td><div class="table-form-item"><div class="table-form-header">';
      rowTemplate += _this.getSubformConfig().headerBtnTemplate;
      rowTemplate += '</div></div></td></tr>';
      _this.$container.html(rowTemplate);
    },
    /**
     * 从表控件校验
     *
     * @returns {Boolean}
     */
    validate: function () {
      var _this = this;
      var valid = true;
      var rowIds = _this.getRowIds();
      for (var j = 0; j < rowIds.length; j++) {
        // 遍历从表的各行
        var dyformExplain = _this.dyformExplains[rowIds[j]];
        if (null == dyformExplain) {
          continue;
        }
        // 全部校验，不快速失败
        valid = dyformExplain.validateForm() && valid;
      }
      return valid;
    },
    invokeCommonMethod: function (methodName, paramters) {
      var _this = this;
      var rowIds = _this.getRowIds();
      for (var j = 0; j < rowIds.length; j++) {
        // 遍历从表的各行
        var dyformExplain = _this.dyformExplains[rowIds[j]];
        if (null == dyformExplain) {
          continue;
        } else if (methodName === 'setReadOnly') {
          if (paramters === true) {
            dyformExplain.setReadOnly();
          } else {
            dyformExplain.setEditable();
          }
        } else if (methodName === 'setDisplayAsLabel') {
          dyformExplain.showAsLabel();
        } else if (dyformExplain[methodName]) {
          dyformExplain[methodName](paramters);
        }
      }
    },

    setDisplayAsLabel: function () {
      var self = this;
      self.options.readOnly = true;
      self.hideOperateBtn();
      self.options.displayAsLabel = true;
    },

    /**
     * 从表折叠接口
     */
    collapse: function () {
      var _this = this;
      _this.$container.find('.table-form-item>.dyform').hide();
    },

    /**
     * 从表展开接口
     */
    expand: function () {
      var _this = this;
      _this.$container.find('.table-form-item>.dyform').show();
    },

    /**
     * 隐藏列
     *
     * @param fieldName
     */
    hideColumn: function (fieldName) {
      var _this = this;
      _this.eachDyformExplain(function (dyformExplain, rowid) {
        dyformExplain.setFieldAsHiddenByFieldName(fieldName, rowid);
      });
    },

    /**
     * 展示列
     *
     * @param fieldName
     */
    showColumn: function (fieldName) {
      var _this = this;
      _this.eachDyformExplain(function (dyformExplain, rowid) {
        dyformExplain.setFieldAsShowByFieldName(fieldName, rowid);
      });
    },

    /**
     * 设置整列显示为标签
     *
     * @param fieldName
     */
    setColumnAsLabel: function (fieldName) {
      var _this = this;
      _this.eachDyformExplain(function (dyformExplain, rowid) {
        dyformExplain.setFieldAsLabel(fieldName, rowid);
      });
    },
    /**
     * 设置整列可编辑
     *
     * @param fieldName
     */
    setColumnEditable: function (fieldName) {
      var _this = this;
      _this.eachDyformExplain(function (dyformExplain, rowid) {
        dyformExplain.setFieldEditable(fieldName, rowid);
      });
    },
    eachDyformExplain: function (callback) {
      var _this = this;
      var rowIds = _this.getRowIds();
      for (var j = 0; j < rowIds.length; j++) {
        // 遍历从表的各行
        var dyformExplain = _this.dyformExplains[rowIds[j]];
        if (null == dyformExplain) {
          continue;
        }
        callback(dyformExplain, rowIds[j]);
      }
    },
    hideOperateBtn: function () {
      var self = this;
      var $container = self.$container;
      if ($container && self.operationClazz) {
        $container.removeClass(self.operationClazz);
      }
      var subform = this.getSubformConfig();
      subform.btnStatus = dySubFormHideButtons.hide;
    },

    showOperateBtn: function () {
      var self = this;
      if (self.isReadOnly()) {
        // 只读不显示按钮
        return;
      }
      var $container = self.$container;
      if ($container && self.operationClazz) {
        $container.addClass(self.operationClazz);
      }
      var subform = this.getSubformConfig();
      subform.btnStatus = dySubFormHideButtons.show;
    },
    resetGridWidth: function () {},
    hideRow: function (rowId) {
      var _this = this;
      _this.$container('tr[data-rowid=' + rowId + ']').hide();
    },
    showRow: function (rowId) {
      var _this = this;
      _this.$container('tr[data-rowid=' + rowId + ']').show();
    },
    isRowHidden: function (rowId) {
      var _this = this;
      return _this.$container('tr[data-rowid=' + rowId + ']').is(':visible');
    }
  };
});
