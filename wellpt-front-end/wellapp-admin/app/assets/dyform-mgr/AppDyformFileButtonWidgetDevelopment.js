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

  var AppDyformFileListButtonConfigWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppDyformFileListButtonConfigWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var $buttonConfigList = $('#button_config_list', this.widget.element);
      var btnCode = [
        'upload_btn',
        'cancel_upload_btn',
        're_upload_btn',
        'preview_btn',
        'delete_btn',
        'download_btn',
        'copy_name_btn',
        'download_all_btn',
        'look_up_btn',
        'seal_file_btn'
      ];

      showTable();
      initData();
      var beans = [];

      $('.btn-add-rows', this.widget.element)
        .off()
        .on('click', function () {
          var bean = {
            uuid: null,
            buttonName: null,
            code: null,
            btnType: null,
            btnShowType: null,
            // icon: null,
            btnLib: null,
            // buttonResource: null,
            eventManger: null,
            fileExtensions: null,
            defaultFlag: 0,
            _id: new Date().getTime()
          };
          var id = $(this).attr('id');
          var newId = getId(id);
          addTableRow($('#' + newId), bean, true);
        });

      $('.btn-del-rows', this.widget.element)
        .off()
        .on('click', function () {
          // var id = $(this).attr("id");
          // var newId = getId(id);
          delTableRow($buttonConfigList);
        });

      $('.btn_button_config_move_up', this.widget.element)
        .off('click')
        .on('click', function (event) {
          var allData = $buttonConfigList.bootstrapTable('getData');
          var size = allData.length;
          var selections = $buttonConfigList.bootstrapTable('getSelections');
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
                return $buttonConfigList.bootstrapTable('load', allData);
              }
            });
          }

          afterLoadTableData();
        });

      $('.btn_button_config_move_down', this.widget.element)
        .off('click')
        .on('click', function (event) {
          var allData = $buttonConfigList.bootstrapTable('getData');
          var size = allData.length;
          var selections = $buttonConfigList.bootstrapTable('getSelections');
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
                $buttonConfigList.bootstrapTable('load', allData);
                break;
              }
            }
          }

          afterLoadTableData();
        });

      // 保存用户信息
      $('#file_list_button_config_btn_save', this.widget.element)
        .off()
        .on('click', function () {
          var $editableErrorBlocks = $buttonConfigList.find('.editable-error-block');
          if ($editableErrorBlocks.length > 0) {
            $editableErrorBlocks[0].scrollIntoView({ block: 'center' });
            return;
          }

          var data1 = $buttonConfigList.bootstrapTable('getData');

          var error = '';
          var codeObj = {};
          var buttonNameObj = {};
          var repeatButtonNameArr = [];
          var repeatCodeArr = [];
          $.each(data1, function (i, datum) {
            var rowError = '';
            if (buttonNameObj[datum.buttonName]) {
              repeatButtonNameArr.push(datum.buttonName);
            } else {
              buttonNameObj[datum.buttonName] = '1';
            }
            if (codeObj[datum.code]) {
              repeatCodeArr.push(datum.code);
            } else {
              codeObj[datum.code] = '1';
            }

            if (!datum.buttonName) {
              rowError += '名称不能为空；';
            }
            if (!datum.code) {
              rowError += '编码不能为空；';
            }
            if ((datum.btnLib ? JSON.stringify(datum.btnLib) : '{}') === '{}') {
              rowError += '按钮库不能为空；';
            }

            if (
              datum.code === 'upload_btn' ||
              datum.code === 'cancel_upload_btn' ||
              datum.code === 're_upload_btn' ||
              datum.code === 'preview_btn' ||
              datum.code === 'delete_btn' ||
              datum.code === 'download_btn' ||
              datum.code === 'copy_name_btn' ||
              datum.code === 'download_all_btn' ||
              datum.code === 'look_up_btn' ||
              datum.code === 'seal_file_btn'
            ) {
            } else if ((datum.eventManger ? JSON.stringify(datum.eventManger) : '{}') === '{}') {
              rowError += '事件管理不能为空；';
            }

            if (rowError) {
              error += '第' + (i + 1) + '行：' + rowError + '<br/>';
            }
          });

          if (error) {
            appModal.error(error);
            return;
          }
          if (repeatButtonNameArr.length > 0) {
            appModal.error('名称不允许重复！以下名称重复：' + repeatButtonNameArr.join('、') + '！');
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
            // data1Element.icon = data1Element.icon.className;
            // data1Element.buttonResource = data1Element.buttonResource ? JSON.stringify(data1Element.buttonResource) : '{}';
            data1Element.btnType = btnCode.indexOf(data1Element.code) > -1 ? '1' : '0';
            data1Element.eventManger = data1Element.eventManger ? JSON.stringify(data1Element.eventManger) : '{}';
            data1Element.btnLib = data1Element.btnLib ? JSON.stringify(data1Element.btnLib) : '{}';
            if (btnCode.indexOf(data1Element.code) > -1) {
              data1Element.btnShowType =
                data1Element.code == 'preview_btn' ||
                data1Element.code == 'download_btn' ||
                data1Element.code == 'download_all_btn' ||
                data1Element.code == 'copy_name_btn' ||
                data1Element.code === 'look_up_btn'
                  ? 'show'
                  : 'edit';
            } else if (btnCode.indexOf(data1Element.code) == -1) {
              data1Element.btnShowType = $buttonConfigList
                .find('tbody tr:eq(' + i + ')')
                .find("td[data-field='btnShowType']")
                .find('option:selected')
                .val();
            }
          }

          JDS.call({
            service: 'dyformFileListButtonConfigService.saveAllBean',
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
                checkbox: true,
                width: 40
              },
              {
                field: 'uuid',
                title: 'uuid',

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
                field: 'buttonName',
                title: '名称',
                width: '80',
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
                title: '编码',
                width: '120',
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
                field: 'btnType',
                title: '内置/扩展按钮',
                width: '110',
                formatter: function (value, row, index) {
                  if (btnCode.indexOf(row.code) > -1) {
                    return '内置按钮';
                  } else {
                    return '扩展按钮';
                  }
                }
              },
              {
                field: 'btnShowType',
                title: '显示/编辑类操作',
                width: '140',
                formatter: function (value, row, index) {
                  if (btnCode.indexOf(row.code) > -1) {
                    return row.code == 'preview_btn' ||
                      row.code == 'download_btn' ||
                      row.code == 'copy_name_btn' ||
                      row.code == 'download_all_btn' ||
                      row.code == 'look_up_btn'
                      ? '显示类操作'
                      : '编辑类操作';
                  } else {
                    var isShow = value == 'show' ? 'selected' : '';
                    var isEdit = value != 'show' ? 'selected' : '';
                    var html =
                      '<select><option value="show" ' +
                      isShow +
                      '>显示类操作</option><option value="edit" ' +
                      isEdit +
                      '>编辑类操作</option></select>';
                    return html;
                  }
                }
              },
              {
                field: 'btnLib',
                title: '按钮库',
                width: 90,
                editable: {
                  onblur: 'save',
                  type: 'wCustomForm',
                  placement: 'bottom',
                  savenochange: true,
                  value2input: designCommons.bootstrapTable.btnLib.value2input,
                  input2value: designCommons.bootstrapTable.btnLib.input2value,
                  value2display: designCommons.bootstrapTable.btnLib.value2display,
                  value2html: designCommons.bootstrapTable.btnLib.value2html,
                  validate: function (value) {
                    //字段验证
                    if (!$.trim(value)) {
                      return '不能为空';
                    }
                  }
                }
              },
              /*{
                  field: "buttonResource",
                  title: "资源",
                  width: 100,
                  editable: {
                      mode: "modal",
                      type: "wCommonComboTree",
                      showbuttons: false,
                      onblur: "submit",
                      wCommonComboTree: {
                          inlineView: true,
                          service: "appProductIntegrationMgr.getTreeNodeByUuid",
                          serviceParams: [/!*piUuid*!/'cd08a458-8d40-4da3-a3e7-715f249f0e56', [], ['BUTTON']],
                          multiSelect: false
                          // 是否多选
                      }
                  }
              },*/
              {
                field: 'eventManger',
                title: '事件管理',
                editable: {
                  mode: 'modal',
                  onblur: 'ignore',
                  type: 'wCustomForm',
                  placement: 'top',
                  savenochange: true,
                  value2input: designCommons.bootstrapTable.eventManager.value2input,
                  input2value: designCommons.bootstrapTable.eventManager.input2value,
                  value2display: designCommons.bootstrapTable.eventManager.value2display,
                  validate: function (value) {
                    //字段验证
                    if (!$.trim(value)) {
                      return '不能为空';
                    }
                  }
                }
              },
              {
                field: 'fileExtensions',
                title: '支持的文件扩展名',
                width: '150',
                editable: {
                  type: 'text',
                  placeholder: 'doc;docx',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'defaultFlag',
                title: '默认选中',
                width: 90,
                formatter: function (value, row, index) {
                  var formatterHtml = designCommons.bootstrapTable.checkbox.formatter(value, 'defaultFlag');
                  var $formatterHtml = $(formatterHtml);
                  if (
                    row.code === 'upload_btn' ||
                    row.code === 'cancel_upload_btn' ||
                    row.code === 're_upload_btn' ||
                    row.code === 'preview_btn' ||
                    row.code === 'delete_btn' ||
                    row.code === 'download_btn' ||
                    row.code === 'download_all_btn' ||
                    row.code === 'copy_name_btn' ||
                    row.code === 'look_up_btn' ||
                    row.code === 'seal_file_btn'
                  ) {
                    $formatterHtml.attr('disabled', 'disabled');
                  }
                  return $formatterHtml.prop('outerHTML');
                },
                events: designCommons.bootstrapTable.checkbox.events
              }
            ],
            ele: '#button_config_list'
          }
        ];
        for (var i = 0; i < datas.length; i++) {
          initTable(datas[i].ele, datas[i].column);
        }

        // 添加* 星号必填(查询元素添加红色星号，在columns中配置，弹出框会显示星号html)
        $($buttonConfigList.find('th')[2]).find('.th-inner').append('<b style="color: red;">*</b>');
        $($buttonConfigList.find('th')[3]).find('.th-inner').append('<b style="color: red;">*</b>');
        $($buttonConfigList.find('th')[5])
          .find('.th-inner')
          .append('<span class="btnShowTypeTip"><i class="iconfont icon-ptkj-tishishuoming"></i></span>');
        $($buttonConfigList.find('th')[6]).find('.th-inner').append('<b style="color: red;">*</b>');
        $($buttonConfigList.find('th')[7]).find('.th-inner').append('<b style="color: red;">*</b>');
        $($buttonConfigList.find('th')[8])
          .find('.th-inner')
          .append('<span class="fileExtensionsTip"><i class="iconfont icon-ptkj-tishishuoming"></i></span>');
        $($buttonConfigList.find('th')[9])
          .find('.th-inner')
          .append('<span class="defaultFlagTip"><i class="iconfont icon-ptkj-tishishuoming"></i></span>');

        $buttonConfigList.find('.btnShowTypeTip').popover({
          html: true,
          placement: 'bottom',
          container: 'body',
          trigger: 'hover',
          template:
            '<div class="popover" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
          content: function () {
            return '<span>在操作按钮选择时会区分显示，如流程环节-表单设置</span><br><span>显示类操作：不会影响数据存储的操作</span><br><span>编辑类操作：会影响数据存储的操作</span>';
          }
        });
        $buttonConfigList.find('.defaultFlagTip').popover({
          html: true,
          placement: 'bottom',
          container: 'body',
          trigger: 'hover',
          template:
            '<div class="popover" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
          content: function () {
            return '<span>关联附件控件中，附件按钮的默认选中状态</span>';
          }
        });

        $buttonConfigList.find('.fileExtensionsTip').popover({
          html: true,
          placement: 'bottom',
          container: 'body',
          trigger: 'hover',
          template:
            '<div class="popover" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
          content: function () {
            return '<span>如果值为空，表示支持任意扩展名的附件<br/>值的格式必须符合JSON数组规范，除非允许所有格式时则为空</span>';
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
        var count = $buttonConfigList.bootstrapTable('getData').length;
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
          if (
            selectionRow.code === 'upload_btn' ||
            selectionRow.code === 'cancel_upload_btn' ||
            selectionRow.code === 're_upload_btn' ||
            selectionRow.code === 'preview_btn' ||
            selectionRow.code === 'delete_btn' ||
            selectionRow.code === 'download_btn' ||
            selectionRow.code === 'copy_name_btn' ||
            selectionRow.code === 'look_up_btn' ||
            selectionRow.code === 'seal_file_btn'
          ) {
            appModal.error('以下按钮不允许删除：上传、预览、下载、删除、取消上传、重新上传、复制名称，请重新选择！');
            return;
          }
        }

        appModal.confirm(
          '确定删除选择的附件按钮吗？<br>' + '删除后，在附件控件和已上传附件中，<br>将删除附件按钮，请谨慎操作！',
          function (result) {
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
          }
        );
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

        var data1 = $buttonConfigList.bootstrapTable('getData');
        $.each($buttonConfigList, function (index, ele) {
          var bootstrapTable = $(ele).data('bootstrap.table');
          $.each(data1, function (i, datum) {
            $.each(bootstrapTable.columns, function (i, column) {
              var editable = bootstrapTable.$body.find('tr[id="' + datum._id + '"] a[data-name="' + column.field + '"]').data('editable');
              if (editable) {
                if (column.field === 'buttonName' || column.field === 'code') {
                  editable.$element.parent().css({
                    'word-break': 'break-all'
                  });
                }
                if (column.field == 'btnType') {
                  setNotEditable(editable, datum[column.field]);
                }
                // if (column.field == 'btnShowType' && btnCode.indexOf(datum.code) > -1) {
                //   setNotEditable(editable, datum[column.field]);
                // }
              }
            });

            if (
              datum.code === 'upload_btn' ||
              datum.code === 'cancel_upload_btn' ||
              datum.code === 're_upload_btn' ||
              datum.code === 'preview_btn' ||
              datum.code === 'delete_btn' ||
              datum.code === 'download_btn' ||
              datum.code === 'copy_name_btn' ||
              datum.code === 'download_all_btn' ||
              datum.code === 'look_up_btn' ||
              datum.code === 'seal_file_btn'
            ) {
              $.each(bootstrapTable.columns, function (i, column) {
                var editable = bootstrapTable.$body.find('tr[id="' + datum._id + '"] a[data-name="' + column.field + '"]').data('editable');

                // 查阅 && 盖章 按钮 显示扩展名
                if (column.field === 'fileExtensions' && (datum.code === 'look_up_btn' || datum.code === 'seal_file_btn')) {
                  return true;
                }

                if (editable) {
                  if (column.field === 'code' || column.field === 'eventManger' || column.field === 'fileExtensions') {
                    setNotEditable(editable, datum[column.field]);
                  }

                  if (
                    (datum.code === 'upload_btn' ||
                      datum.code === 'cancel_upload_btn' ||
                      datum.code === 're_upload_btn' ||
                      datum.code === 'preview_btn' ||
                      datum.code === 'delete_btn' ||
                      datum.code === 'download_btn' ||
                      datum.code === 'download_all_btn' ||
                      datum.code === 'copy_name_btn' ||
                      datum.code === 'look_up_btn' ||
                      datum.code === 'seal_file_btn') &&
                    column.field === 'buttonName'
                  ) {
                    setNotEditable(editable, datum[column.field]);
                  }

                  // if (datum.code === 'upload_btn' && (column.field === 'btnLib' || column.field === 'buttonName')) {
                  //   setNotEditable(editable, datum[column.field]);
                  // }
                }
              });
            }
          });
        });
      }

      function initData() {
        JDS.call({
          service: 'dyformFileListButtonConfigService.getAllBean',
          data: [],
          version: '',
          success: function (result) {
            $(result.data).each(function () {
              // this.buttonResource = this.buttonResource ? this.buttonResource : "{}";
              this.eventManger = this.eventManger ? this.eventManger : '{}';
              this.btnLib = this.btnLib ? this.btnLib : '{}';
              var data = {
                uuid: this.uuid,
                buttonName: this.buttonName,
                code: this.code,
                btnType: this.btnType,
                btnShowType: this.btnShowType,
                // icon: this.icon ? {className: this.icon,} : '',
                btnLib: JSON.parse(this.btnLib),
                // buttonResource: JSON.parse(this.buttonResource),
                eventManger: JSON.parse(this.eventManger),
                fileExtensions: this.fileExtensions,
                defaultFlag: this.defaultFlag,
                _id: this.uuid
              };

              addTableRow($buttonConfigList, data);
            });

            afterLoadTableData();
          }
        });
      }
    }
  });
  return AppDyformFileListButtonConfigWidgetDevelopment;
});
