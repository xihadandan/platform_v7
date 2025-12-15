define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  HtmlWidgetDevelopment
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var JDS = server.JDS;

  // 平台管理_系统单位_视图组件二开

  var AppSystemUnitSetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppSystemUnitSetDevelopment, HtmlWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {},
    // 创建后回调
    create: function () {},
    // 初始化回调
    init: function () {
      var _self = this;
      var _uuid = GetRequestParam().uuid;
      // if (_self.widget.options.containerDefinition.params && _self.widget.options.containerDefinition.params.uuid) {
      //   _uuid = _self.widget.options.containerDefinition.params.uuid;
      // }
      var form_selector = '#org_unit_form';
      var formBean = {
        uuid: null,
        id: null,
        name: null,
        shortName: null,
        code: null,
        sapCode: null,
        remark: null,
        isGroupUnit: 0,
        members: null
      };

      $('#org_unit_form').json2form(formBean);

      var validator = $.common.validation.validate(form_selector, 'orgSystemUnitVo');

      // $('#sys_unit_members').wSelect2({
      //   serviceName: 'multiOrgSystemUnitService',
      //   queryMethod: 'queryUnitListForSelect2',
      //   valueField: 'sys_unit_members',
      //   placeholder: '请选择',
      //   multiple: true,
      //   remoteSearch: false,
      //   width: '100%',
      //   height: 250
      // });

      // $('#sys_unit_isGroupUnit').on('change', function () {
      //   if ($(this).attr('checked')) {
      //     $('.isGroupUnit').show();
      //   } else {
      //     $('.isGroupUnit').hide();
      //   }
      // });

      var $sys_unit_custom_table = $('#sys_unit_custom_table', _self.element);

      function initTable(bean) {
        var _data = bean ? bean.orgElementAttrs : [];
        var $sys_unit_custom_bean = {
          checked: false,
          uuid: '',
          name: '',
          code: '',
          attrType: 'text',
          attrDisplay: ''
        };
        // 定义添加，删除，上移，下移4按钮事件
        formBuilder.bootstrapTable.initTableTopButtonToolbar(
          'sys_unit_custom_table',
          'sys_unit_custom',
          _self.element,
          $sys_unit_custom_bean
        );

        $sys_unit_custom_table.bootstrapTable('destroy').bootstrapTable({
          data: _data,
          idField: 'uuid',
          striped: true,
          width: 500,
          onEditableHidden: function (field, row, $el, reason) {
            $el.closest('table').bootstrapTable('resetView');
          },
          onEditableSave: function (field, row, oldValue, $el) {
            if (field === 'attrType') {
              var allData = $sys_unit_custom_table.bootstrapTable('getData');
              $.each(allData, function (i, item) {
                if (item.uuid === row.uuid) {
                  row.attrDisplay = row.attrValue = '';
                  $sys_unit_custom_table.bootstrapTable('updateRow', i, row);
                }
              });
            }
          },
          toolbar: $('#div_sys_unit_custom_toolbar', _self.element),
          undefinedText: 'empty',
          columns: [
            {
              field: 'checked',
              checkbox: true,
              formatter: function (value) {
                if (value) {
                  return true;
                }
                return false;
              }
            },
            {
              field: 'uuid',
              title: 'UUID',
              visible: false
            },
            {
              field: 'name',
              title: '属性名称',
              width: 200,
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline',
                formatter: function (value) {
                  return value || '';
                },
                validate: function (value) {
                  if (StringUtils.isBlank(value)) {
                    return '请输入属性名称!';
                  }
                }
              }
            },
            {
              field: 'code',
              title: '属性编码',
              width: 200,
              editable: {
                type: 'text',
                mode: 'inline',
                showbuttons: false,
                onblur: 'submit',
                emptytext: 'Empty',
                savenochange: true,
                validate: function (value) {
                  if (StringUtils.isBlank(value)) {
                    return '请输入属性编码!';
                  }
                }
              }
            },
            {
              field: 'attrType',
              title: '属性类型',
              editable: {
                type: 'select',
                mode: 'inline',
                showbuttons: false,
                onblur: 'submit',
                source: [
                  {
                    value: 'text',
                    text: '文本属性'
                  },
                  {
                    value: 'org',
                    text: '组织属性'
                  },
                  {
                    value: 'dict',
                    text: '字典属性'
                  }
                ]
              }
            },
            {
              field: 'attrValue',
              title: '属性值',
              editable: {
                onblur: 'save',
                type: 'wCustomForm',
                placement: 'bottom',
                savenochange: true,
                validate: function (value) {
                  if (StringUtils.isBlank(value)) {
                    return '请输入属性值!';
                  }
                },
                value2input: function () {
                  var $container = $(this.options.scope).next();
                  var _index = $container.closest('tr').data('index');
                  var allData = $sys_unit_custom_table.bootstrapTable('getData');
                  var currentData = allData[_index];
                  var attrType = currentData.attrType;
                  var $input = this.$input;
                  $input.closest('form').removeClass('form-inline');
                  $input.css('width', '400');
                  $input.empty();

                  var fieldHtml = new StringBuilder();
                  fieldHtml.append("<div class='formbuilder form-group' style='padding-left: 70px'>");
                  fieldHtml.append("<label class='col-xs-3 control-label label-formbuilder' style='width: 60px!important;'>属性值</label>");
                  fieldHtml.append("<div class='col-xs-9 controls'>");
                  fieldHtml.append("<div id='btn_container_div' style='display: inline;'>");
                  if (attrType === 'text') {
                    fieldHtml.append(
                      "<input id='" +
                        _index +
                        "_attr_value' class='form-control' style='width: 100%' value='" +
                        (currentData.attrValue || '') +
                        "'>"
                    );
                  } else {
                    fieldHtml.append(
                      "<input type='hidden' id='" +
                        _index +
                        "_attr_value' class='form-control' value='" +
                        (currentData.attrValue || '') +
                        "'>"
                    );
                    fieldHtml.append(
                      "<input id='" +
                        _index +
                        "_attr_label' class='form-control' style='width: 100%' value='" +
                        (currentData.attrDisplay || '') +
                        "'>"
                    );
                  }
                  fieldHtml.append('</div></div></div>');
                  $input.append(fieldHtml.toString());

                  $(document)
                    .off('click', '#' + _index + '_attr_label')
                    .on('click', '#' + _index + '_attr_label', function () {
                      if (attrType === 'org') {
                        $container.hide();
                        //组织弹出框选择
                        $.unit2.open({
                          valueField: _index + '_attr_value',
                          labelField: _index + '_attr_label',
                          title: '选择组织节点',
                          type: 'all',
                          unitId: $('#sys_unit_id').val(),
                          //multiple: false,
                          //selectTypes: "U",
                          valueFormat: 'justId',
                          callback: function (values) {
                            $container.show();
                          }
                        });
                      } else if (attrType === 'dict') {
                        $container.hide();
                        var id = new Date().getTime();
                        var $div = $('<div>').append(
                          $('<input>', {
                            type: 'text',
                            id: id,
                            style: 'width:100%',
                            placeholder: '请选择数据字典',
                            value: $('#' + _index + '_attr_value').val()
                          })
                        );
                        var $dialog = $div.dialog({
                          title: '选择字典数据',
                          autoOpen: true,
                          resizable: true,
                          stack: true,
                          width: 850,
                          height: 560,
                          modal: true,
                          overlay: {
                            background: '#000',
                            opacity: 0.5
                          },
                          buttons: {
                            确定: function () {
                              $('#' + _index + '_attr_label').val($('#' + id).val());
                              $('#' + _index + '_attr_value').val($('#' + id).data('v'));
                              $dialog.dialog('close');
                              $dialog.remove();
                            },
                            取消: function () {
                              $dialog.dialog('close');
                              $dialog.remove();
                            }
                          },
                          open: function (e) {
                            //下拉框选择数据字典
                            $('#' + id).wCommonComboTree({
                              service: 'dataDictionaryService.getAllAsTree',
                              serviceParams: [-1],
                              multiSelect: true, // 是否多选
                              parentSelect: true, // 父节点选择有效，默认无效
                              expandRootNode: true, // 展开根结点
                              onAfterSetValue: function (event, self, value) {
                                $('#' + id).data('v', value);
                              }
                            });
                          },
                          close: function () {
                            return true;
                          }
                        });
                      }
                    });
                },
                input2value: function () {
                  var $input = this.$input;
                  var $container = $(this.options.scope).next();
                  var _index = $container.closest('tr').data('index');
                  var allData = $sys_unit_custom_table.bootstrapTable('getData');
                  var curData = allData[_index];
                  var attrType = curData.attrType;
                  if (attrType === 'text') {
                    curData.attrDisplay = curData.attrValue = $input.find('#' + _index + '_attr_value').val();
                  } else {
                    curData.attrValue = $input.find('#' + _index + '_attr_value').val();
                    curData.attrDisplay = $input.find('#' + _index + '_attr_label').val();
                  }
                  $sys_unit_custom_table.bootstrapTable('updateRow', _index, curData);
                },
                value2display: function () {},
                value2html: function (value, element) {
                  var _index = $(element).closest('tr').data('index');
                  var allData = $sys_unit_custom_table.bootstrapTable('getData');
                  var tableData;
                  tableData = $.isArray(allData) ? allData : _data;
                  $(element).html(tableData[_index].attrDisplay || '');
                }
              }
            }
          ]
        });
      }

      if (_uuid) {
        showSystemUnitInfo();
      } else {
        initTable();
      }

      // 根据组织UUID获取信息
      function showSystemUnitInfo() {
        $.ajax({
          url: ctx + '/api/org/multi/getSystemUnitVo',
          type: 'get',
          data: {
            uuid: _uuid
          },
          success: function (result) {
            var bean = result.data;
            $('#org_unit_form').json2form(bean);
            // $('#sys_unit_members').trigger('change');
            // $('#sys_unit_isGroupUnit').trigger('change');
            initTable(bean);
          }
        });
      }

      // 定义保存按钮的事件
      $('#btn_save')
        .off()
        .on('click', function () {
          if (!validator.form()) {
            return false;
          }
          $('#org_unit_form').form2json(formBean);
          formBean.isGroupUnit = formBean.isGroupUnit ? '1' : '0';

          //保存扩展属性信息
          var allData = $sys_unit_custom_table.bootstrapTable('getData');
          formBean.orgElementAttrs = [];
          var attrCodes = [];

          $.each(allData, function (i, item) {
            if (attrCodes.indexOf(item.code) > -1) {
              appModal.alert('自定义属性的编码不能重复');
              return false;
            }
            attrCodes.push(item.code);
            formBean.orgElementAttrs.push({
              code: item.code,
              name: item.name,
              attrValue: item.attrValue,
              attrType: item.attrType,
              seq: i + 1,
              attrDisplay: item.attrDisplay
            });
          });

          var url = '/api/org/multi/' + (formBean.uuid ? 'modifySystemUnit' : 'addSystemUnit');
          $.ajax({
            type: 'POST',
            url: url,
            dataType: 'json',
            data: formBean,
            success: function (data) {
              appModal.success('保存成功！');
              appContext.getNavTabWidget().closeTab();
            }
          });
        });
    },
    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppSystemUnitSetDevelopment;
});
