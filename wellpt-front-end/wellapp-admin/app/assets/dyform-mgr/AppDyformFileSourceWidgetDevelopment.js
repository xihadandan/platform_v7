define(['constant', 'commons', 'server', 'appContext', 'appModal', 'multiOrg', 'design_commons', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  multiOrg,
  designCommons,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppDyformFileListSourceConfigWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppDyformFileListSourceConfigWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var $sourceConfigList = $('#source_config_list', this.widget.element);

      showTable();
      initData();
      var beans = [];

      $('.btn-add-rows', this.widget.element)
        .off()
        .on('click', function () {
          var bean = {
            uuid: null,
            sourceName: null,
            code: null,
            icon: null,
            jsModule: null,
            defaultFlag: null,
            _id: new Date().getTime()
          };
          var id = $(this).attr('id');
          var newId = getId(id);
          addTableRow($('#' + newId), bean, true);
        });

      $('.btn-del-rows', this.widget.element)
        .off()
        .on('click', function () {
          delTableRow($sourceConfigList);
        });

      $('.btn_source_config_move_up', this.widget.element)
        .off('click')
        .on('click', function (event) {
          var allData = $sourceConfigList.bootstrapTable('getData');
          var size = allData.length;
          var selections = $sourceConfigList.bootstrapTable('getSelections');
          if (selections.length === 0) {
            appModal.error('请选择记录!');
            return;
          }

          for (var i = 0; i < selections.length; i++) {
            var row = selections[i];
            $.each(allData, function (j, row2) {
              if (row['_id'] === row2['_id'] && j > 0) {
                allData[j] = allData[j - 1];
                allData[j - 1] = row;
                return $sourceConfigList.bootstrapTable('load', allData);
              }
            });
          }

          afterLoadTableData();
        });

      $('.btn_source_config_move_down', this.widget.element)
        .off('click')
        .on('click', function (event) {
          var allData = $sourceConfigList.bootstrapTable('getData');
          var size = allData.length;
          var selections = $sourceConfigList.bootstrapTable('getSelections');
          if (selections.length === 0) {
            appModal.error('请选择记录!');
            return;
          }

          for (var i = selections.length - 1; i >= 0; i--) {
            var row = selections[i];
            for (var j = 0; j < allData.length; j++) {
              var row2 = allData[j];
              if (row['_id'] === row2['_id'] && j < size - 1) {
                allData[j] = allData[j + 1];
                allData[j + 1] = row;
                $sourceConfigList.bootstrapTable('load', allData);
                break;
              }
            }
          }

          afterLoadTableData();
        });

      // 保存用户信息
      $('#file_list_source_config_btn_save', this.widget.element)
        .off()
        .on('click', function () {
          var $editableErrorBlocks = $sourceConfigList.find('.editable-error-block');
          if ($editableErrorBlocks.length > 0) {
            $editableErrorBlocks[0].scrollIntoView({ block: 'center' });
            return;
          }

          var data1 = $sourceConfigList.bootstrapTable('getData');

          var error = '';
          var codeObj = {};
          var sourceNameObj = {};
          var repeatSourceNameArr = [];
          var repeatCodeArr = [];
          $.each(data1, function (i, datum) {
            var rowError = '';
            if (sourceNameObj[datum.sourceName]) {
              repeatSourceNameArr.push(datum.sourceName);
            } else {
              sourceNameObj[datum.sourceName] = '1';
            }
            if (codeObj[datum.code]) {
              repeatCodeArr.push(datum.code);
            } else {
              codeObj[datum.code] = '1';
            }

            if (!datum.sourceName) {
              rowError += '名称不能为空；';
            }
            if (!datum.code) {
              rowError += '编码不能为空；';
            }

            if (datum.code === 'local_upload') {
            } else if ((datum.jsModule ? JSON.stringify(datum.jsModule) : '{}') === '{}') {
              rowError += 'JS模块不能为空；';
            }

            if (rowError) {
              error += '第' + (i + 1) + '行：' + rowError + '<br/>';
            }
          });

          if (error) {
            appModal.error(error);
            return;
          }
          if (repeatSourceNameArr.length > 0) {
            appModal.error('名称不允许重复！以下名称重复：' + repeatSourceNameArr.join('、') + '！');
            return;
          }
          if (repeatCodeArr.length > 0) {
            appModal.error('编码不允许重复！以下编码重复：' + repeatCodeArr.join('、') + '！');
            return;
          }

          data1 = data1.concat(beans);

          var data1Json = JSON.stringify(data1);
          var saveDataArr = JSON.parse(data1Json);

          for (var i = 0; i < saveDataArr.length; i++) {
            var data1Element = saveDataArr[i];
            if (data1Element.icon) {
              data1Element.icon = data1Element.icon.className;
            } else {
              data1Element.icon = '';
            }
            data1Element.jsModule = data1Element.jsModule ? JSON.stringify(data1Element.jsModule) : '{}';
          }

          JDS.call({
            service: 'dyformFileListSourceConfigService.saveAllBean',
            data: [saveDataArr],
            version: '',
            success: function (result) {
              appModal.success('保存成功！', function () {
                location.reload();
              });
            }
          });
        });

      function getId(id) {
        var newId = id.substring(4, id.length - 3) + 'list';
        return newId;
      }

      function showTable() {
        var datas = [
          {
            column: [
              {
                checkbox: true
              },
              {
                field: 'uuid',
                title: 'uuid',
                width: 40,
                visible: false
              },
              {
                field: 'no',
                title: '序号',
                align: 'center',
                width: 50,
                formatter: function (value, row, index) {
                  return index + 1;
                }
              },
              {
                field: 'sourceName',
                title: '名称<b style="color: red;">*</b>',
                editableOriginalTitle: '名称',
                width: '200',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline',
                  validate: function (value) {
                    //字段验证
                    if (!$.trim(value)) {
                      return '不能为空';
                    }
                    if (value.length > 1000) {
                      return '长度不能超过1000';
                    }
                  }
                }
              },
              {
                field: 'code',
                title: '编码<b style="color: red;">*</b>',
                editableOriginalTitle: '编码',
                width: '200',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline',
                  validate: function (value) {
                    //字段验证
                    if (!$.trim(value)) {
                      return '不能为空';
                    }
                    if (value.length > 1000) {
                      return '长度不能超过1000';
                    }
                  }
                }
              },
              {
                field: 'icon',
                title: '图标',
                width: '100',
                editable: {
                  onblur: 'cancel',
                  type: 'wCustomForm',
                  placement: 'bottom',
                  savenochange: true,
                  iconSelectTypes: [3],
                  value2input: designCommons.bootstrapTable.icon.value2input,
                  input2value: designCommons.bootstrapTable.icon.input2value,
                  value2display: designCommons.bootstrapTable.icon.value2display,
                  value2html: designCommons.bootstrapTable.icon.value2html
                }
              },
              {
                field: 'jsModule',
                title: 'JS模块',
                editable: {
                  onblur: 'cancel',
                  type: 'wCustomForm',
                  placement: 'bottom',
                  savenochange: true,
                  value2input: function (value) {
                    var $input = this.$input;
                    $input.closest('form').removeClass('form-inline');
                    $input.css('width', '400');
                    $input.empty();
                    value = value || {};
                    formBuilder.buildSelect2({
                      container: $input,
                      label: 'JS模块',
                      name: 'jsModule',
                      value: value.jsModule,
                      display: 'jsModuleName',
                      displayValue: value.jsModuleName,
                      inputClass: 'w-custom-collect',
                      labelColSpan: '3',
                      controlColSpan: '9',
                      select2: {
                        serviceName: 'appJavaScriptModuleMgr',
                        params: {
                          dependencyFilter: 'FileSourceDevelopment'
                        },
                        labelField: 'jsModuleName',
                        valueField: 'jsModule',
                        remoteSearch: false,
                        multiple: false
                      },
                      events: {}
                    });
                  },
                  value2display: function (value) {
                    return value.jsModuleName;
                  },
                  inputCompleted: function (value) {
                    if (value) {
                      value.refreshIfExists = Boolean(value.refreshIfExists);
                    }
                  }
                }
              },
              {
                field: 'defaultFlag',
                title: '默认选中<span class="defaultFlagTip"><i class="iconfont icon-ptkj-tishishuoming"></i></span>',
                editableOriginalTitle: '默认选中',
                width: '90',
                formatter: function (value, row, index) {
                  var formatterHtml = designCommons.bootstrapTable.checkbox.formatter(value, 'defaultFlag');
                  var $formatterHtml = $(formatterHtml);
                  if (row.code === 'local_upload') {
                    $formatterHtml.attr('disabled', 'disabled');
                  }
                  return $formatterHtml.prop('outerHTML');
                },
                events: designCommons.bootstrapTable.checkbox.events
              }
            ],
            ele: '#source_config_list'
          }
        ];
        for (var i = 0; i < datas.length; i++) {
          initTable(datas[i].ele, datas[i].column);
        }

        // 添加* 星号必填(查询元素添加红色星号，在columns中配置，弹出框会显示星号html)
        $($sourceConfigList.find('th')[5])
          .find('.th-inner')
          .append('<b style="color: red;">*</b><span class="jsModuleTip"><i class="iconfont icon-ptkj-tishishuoming"></i></span>');

        $sourceConfigList.find('.defaultFlagTip').popover({
          html: true,
          placement: 'bottom',
          container: 'body',
          trigger: 'hover',
          template:
            '<div class="popover" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
          content: function () {
            return '<span>关联附件控件中，附件来源的默认选中状态</span>';
          }
        });

        $sourceConfigList.find('.jsModuleTip').popover({
          html: true,
          placement: 'bottom',
          container: 'body',
          trigger: 'hover',
          template:
            '<div class="popover" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
          content: function () {
            return '<span>附件来源的JS模块，需要继承FileSourceDevelopment.js，如DemoFileSourceDevelopment.js</span>';
          }
        });
      }

      function initTable(ele, columns) {
        $(ele).bootstrapTable({
          data: [],
          idField: 'uuid',
          striped: false,
          showColumns: false,
          uniqueId: 'uuid',
          width: 500,
          undefinedText: '',
          columns: columns
        });
        $(ele).parents('.fixed-table-body').css({
          overflow: 'initial'
        });
      }

      function addTableRow(ele, data, scrollIntoView) {
        // 插入行数据
        var count = $sourceConfigList.bootstrapTable('getData').length;
        ele.bootstrapTable('insertRow', { index: count, row: data });
        afterLoadTableData();
        if (scrollIntoView) {
          var $tr = $('tr[id="' + data._id + '"]');
          if ($tr.length > 0) {
            $tr[0].scrollIntoView({ block: 'center' });
          }
        }
      }

      function delTableRow(ele) {
        // 删除行数据
        var selectionRows = ele.bootstrapTable('getSelections');
        if (selectionRows.length == 0) {
          appModal.error('请选择记录!');
          return;
        }
        for (var i = 0; i < selectionRows.length; i++) {
          var selectionRow = selectionRows[i];
          if (selectionRow.code === 'local_upload') {
            appModal.error('以下附件来源不允许删除：本地文件，请重新选择！');
            return;
          }
        }

        appModal.confirm('确定删除选择的附件来源吗？<br>' + '删除后，在附件控件和已上传附件中，<br>将删除附件来源，请谨慎操作！', function (
          result
        ) {
          if (result) {
            var fieldsVal = [];
            for (var i = 0; i < selectionRows.length; i++) {
              selectionRows[i].rowStatus = 'deleted';
              fieldsVal.push(selectionRows[i]['_id']);
              beans.push(selectionRows[i]);
            }

            ele.bootstrapTable('remove', {
              field: '_id',
              values: fieldsVal
            });

            afterLoadTableData();
          }
        });
      }

      function afterLoadTableData() {
        function setNotEditable(editable, columnValue) {
          var htmlValue = editable.$element.html();
          if ((!columnValue && htmlValue === 'Empty') || (JSON.stringify(columnValue) === '{}' && htmlValue === 'Empty')) {
            editable.$element.parent().html('');
          } else {
            editable.$element.parent().html(htmlValue);
          }
          editable.input.isDisabled = true;
        }

        var data1 = $sourceConfigList.bootstrapTable('getData');
        $.each($sourceConfigList, function (index, ele) {
          var bootstrapTable = $(ele).data('bootstrap.table');
          $.each(data1, function (i, datum) {
            $.each(bootstrapTable.columns, function (i, column) {
              var editable = bootstrapTable.$body.find('tr[id="' + datum._id + '"] a[data-name="' + column.field + '"]').data('editable');
              if (editable) {
                if (column.field === 'sourceName' || column.field === 'code') {
                  editable.$element.parent().css({
                    // 'overflow': 'hidden',
                    // 'white-space': 'nowrap',
                    // 'text-overflow': 'ellipsis',
                    // 'display': 'block'
                    'word-break': 'break-all'
                  });
                }
              }
            });

            if (datum.code === 'local_upload') {
              $.each(bootstrapTable.columns, function (i, column) {
                var editable = bootstrapTable.$body.find('tr[id="' + datum._id + '"] a[data-name="' + column.field + '"]').data('editable');
                if (editable) {
                  if (column.field === 'code' || column.field === 'jsModule') {
                    setNotEditable(editable, datum[column.field]);
                  }
                }
              });
            }
          });
        });
      }

      function initData() {
        JDS.call({
          service: 'dyformFileListSourceConfigService.getAllBean',
          data: [],
          version: '',
          success: function (result) {
            $(result.data).each(function () {
              this.jsModule = this.jsModule ? this.jsModule : '{}';
              try {
                JSON.parse(this.jsModule);
              } catch (e) {
                this.jsModule = '{}';
              }

              var data = {
                uuid: this.uuid,
                sourceName: this.sourceName,
                code: this.code,
                icon: this.icon ? { className: this.icon } : '',
                jsModule: JSON.parse(this.jsModule),
                defaultFlag: this.defaultFlag,
                _id: this.uuid
              };

              addTableRow($sourceConfigList, data);
            });

            afterLoadTableData();
          }
        });
      }
    }
  });
  return AppDyformFileListSourceConfigWidgetDevelopment;
});
