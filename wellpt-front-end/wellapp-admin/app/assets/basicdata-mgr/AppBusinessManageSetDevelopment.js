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

  //  平台应用_公共资源_业务管理编辑二开
  var AppBusinessManageSetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppBusinessManageSetDevelopment, HtmlWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {},
    // 创建后回调
    create: function () {},
    // 初始化回调
    init: function () {
      var _self = this;
      var _uuid;
      var _rowData;
      if (GetRequestParam().uuid) {
        _rowData = GetRequestParam();
        _rowData.manageDeptValue = decodeURI(escape(_rowData.manageDeptValue));
        _rowData.manageUserValue = decodeURI(escape(_rowData.manageUserValue));
        _rowData.name = decodeURI(escape(_rowData.name));
      }
      // 表单选择器
      var form_selector = '#business_manage_form';

      var oldRoles = [];
      var bean = {
        uuid: null
      };

      // var validator = $.common.validation.validate(form_selector, "businessCategoryEntity", function (options) {
      //     options.ignore = "";
      // });

      $(form_selector).json2form(bean);

      // 获取数据
      function getBusinessData() {
        bean.uuid = _rowData.uuid;
        $(form_selector).json2form(_rowData);
        JDS.call({
          service: 'businessRoleService.findByCategoryUuid',
          async: false,
          data: [_rowData.businessCategoryUuid],
          success: function (result) {
            bean.datas = result.data;
          }
        });

        JDS.call({
          service: 'businessRoleOrgUserService.findByOrgUuid',
          data: [_rowData.uuid],
          success: function (result) {
            initTable(result.data);
          }
        });
      }

      var $business_category_table = $('#business_manage_table', _self.element);

      function initTable(data) {
        var _data = data || [];
        console.log(_data);
        $.each(_data, function (i, item) {
          item.jUuid = commons.UUID.createUUID();
        });

        var rolesList = $.map(bean.datas, function (item) {
          return {
            id: item.uuid,
            text: item.name
          };
        });

        var _bean = {
          checked: false,
          jUuid: '',
          uuid: '',
          businessCategoryOrgUuid: '',
          usersValue: '',
          businessRoleUuid: '',
          users: '',
          roleName: ''
        };
        // 定义添加，删除，上移，下移4按钮事件
        formBuilder.bootstrapTable.initTableTopButtonToolbar('business_manage_table', 'business_manage', _self.element, _bean, 'jUuid');

        $business_category_table.bootstrapTable('destroy').bootstrapTable({
          data: _data,
          idField: 'jUuid',
          striped: true,
          width: 500,
          onEditableHidden: function (field, row, $el, reason) {
            $el.closest('table').bootstrapTable('resetView');
          },
          onEditableSave: function (field, row, oldValue, $el) {
            if (field === 'attrType') {
              var allData = $business_category_table.bootstrapTable('getData');
              $.each(allData, function (i, item) {
                if (item.uuid === row.uuid) {
                  row.attrDisplay = row.attrValue = '';
                  $business_category_table.bootstrapTable('updateRow', i, row);
                }
              });
            }
          },
          onClickCell: function (field, value, rowData, $el) {
            console.log(field);
            if (field === 'roleName') {
              var container = $('<div>').html(_self.dlg_roleName_Html());
              appModal.dialog({
                title: '选择业务角色',
                size: 'small',
                message: container,
                shown: function () {
                  $("input[name='roleName']", container).val(rowData.roleName);
                  $("input[name='businessRoleUuid']", container).val(rowData.businessRoleUuid);

                  $('#businessManage_roleName', container).wSelect2({
                    labelField: 'businessManage_roleName',
                    valueField: 'businessManage_businessRoleUuid',
                    data: rolesList
                  });
                },
                callback: function () {
                  var allData = $business_category_table.bootstrapTable('getData');
                  var newRowData = {};
                  newRowData.roleName = $("input[name='roleName']", container).val();
                  newRowData.businessRoleUuid = $("input[name='businessRoleUuid']", container).val();
                  $.each(allData, function (i, item) {
                    if (item.jUuid === rowData.jUuid) {
                      $business_category_table.bootstrapTable('updateRow', i, $.extend(rowData, newRowData));
                      return false;
                    }
                  });
                }
              });
            } else if (field === 'usersValue') {
              var self = this;
              var unit = SpringSecurityUtils.getCurrentUserUnitId();
              if (unit === 'S0000000000') {
                unit = '';
              }

              $.unit2.open({
                valueField: '',
                labelField: '',
                title: '请选择成员',
                type: 'all',
                multiple: true,
                selectTypes: 'U',
                valueFormat: 'justId',
                initValues: rowData.users,
                initLabels: rowData.usersValue,
                unitId: unit,
                orgVersionId: '',
                callback: function (values, labels) {
                  var allData = $business_category_table.bootstrapTable('getData');
                  var newRowData = {};
                  newRowData.users = values.join(';');
                  newRowData.usersValue = labels.join(';');
                  $.each(allData, function (i, item) {
                    if (item.jUuid === rowData.jUuid) {
                      $business_category_table.bootstrapTable('updateRow', i, $.extend(rowData, newRowData));
                      return false;
                    }
                  });
                }
              });
            }
          },
          toolbar: $('#div_business_manage_toolbar', _self.element),
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
              field: 'jUuid',
              title: 'jUuid',
              visible: false
            },
            {
              field: 'uuid',
              title: 'UUID',
              visible: false
            },
            {
              field: 'roleName',
              title: '业务角色',
              width: '40%',
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
                    return '请选择业务角色!';
                  }
                }
              }
            },
            {
              field: 'usersValue',
              title: '成员',
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
                    return '请选择成员!';
                  }
                }
              }
            }
          ]
        });
      }

      if (_rowData) {
        getBusinessData();
      } else {
        initTable();
      }

      // 保存脚本定义信息
      $('#business_manage_btn_save').click(function () {
        // 收集表单数据
        var allData = $business_category_table.bootstrapTable('getData');
        var uuids = [],
          users = [],
          usersValues = [];
        var verify = false;
        $.each(allData, function (i, item) {
          if (!item.businessRoleUuid || !item.users || !item.usersValue) {
            appModal.alert('业务角色或成员不能为空！');
            verify = true;
            return false;
          }
          uuids.push(item.businessRoleUuid);
          users.push(item.users);
          usersValues.push(item.usersValue);
        });

        if (verify) {
          return;
        }
        JDS.call({
          service: 'businessRoleOrgUserService.batchSave',
          data: [bean.uuid, uuids, users, usersValues],
          async: false,
          validate: true,
          success: function (result) {
            // 保存成功刷新列表
            appModal.success('保存成功！');
          }
        });
      });
    },
    refresh: function () {
      var _self = this;
      _self.init();
    },
    dlg_roleName_Html: function () {
      return (
        '<div class="well-form well-dialog-form form-horizontal">' +
        '        <div class="form-group" data-type="2">' +
        '            <label class="well-form-label control-label">业务角色</label>' +
        '            <div class="well-form-control">' +
        '                <input type="hidden" class="form-control" id="businessManage_roleName" name="roleName">' +
        '                <input type="hidden" class="form-control" id="businessManage_businessRoleUuid" name="businessRoleUuid">' +
        '            </div>' +
        '        </div>' +
        '    </div>'
      );
    }
  });
  return AppBusinessManageSetDevelopment;
});
