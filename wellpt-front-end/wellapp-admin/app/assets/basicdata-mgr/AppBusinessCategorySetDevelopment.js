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

  //  平台应用_公共资源_业务类别编辑二开
  var AppBusinessCategorySetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppBusinessCategorySetDevelopment, HtmlWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {},
    // 创建后回调
    create: function () {},
    // 初始化回调
    init: function () {
      var _self = this;
      var _uuid;
      if (GetRequestParam().uuid) {
        _uuid = GetRequestParam().uuid;
      }
      // 表单选择器
      var form_selector = '#business_category_form';

      var oldRoles = [];
      var bean = {
        uuid: null,
        code: null,
        name: null,
        manageDept: null,
        manageDeptValue: null,
        addRoles: [],
        delRoleIds: []
      };

      var validator = $.common.validation.validate(form_selector, 'businessCategoryEntity', function (options) {
        options.ignore = '';
      });

      $(form_selector).json2form(bean);
      $('#business_category_manageDeptValue').wSelect2({
        serviceName: 'businessCategoryService',
        queryMethod: 'querySelectDataFromMultiOrgSystemUnit',
        selectionMethod: 'loadSelectDataFromMultiOrgSystemUnit',
        labelField: 'business_category_manageDeptValue',
        valueField: 'business_category_manageDept',
        defaultBlank: true
      });

      // 获取数据
      function getBusinessCategory() {
        JDS.call({
          service: 'businessCategoryService.getBeanByUuid',
          data: [_uuid],
          success: function (result) {
            bean = result.data;
            $(form_selector).json2form(bean);
            $('#business_category_manageDeptValue').trigger('change');
            $('#business_category_id').prop('readonly', 'readonly');
            initTable(bean);
            oldRoles = JSON.parse(JSON.stringify(bean.addRoles));
            validator.form();
          }
        });
      }

      var $business_category_table = $('#business_category_table', _self.element);

      function initTable(bean) {
        var _data = bean ? bean.addRoles : [];
        $.each(_data, function (i, item) {
          item.jUuid = commons.UUID.createUUID();
        });
        var _bean = {
          checked: false,
          jUuid: '',
          uuid: '',
          name: ''
        };
        // 定义添加，删除，上移，下移4按钮事件
        formBuilder.bootstrapTable.initTableTopButtonToolbar('business_category_table', 'business_category', _self.element, _bean, 'jUuid');

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
          toolbar: $('#div_business_category_toolbar', _self.element),
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
              field: 'name',
              title: '业务角色',
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
                    return '请输入业务角色!';
                  }
                }
              }
            }
          ]
        });
      }

      if (_uuid) {
        getBusinessCategory();
      } else {
        initTable();
      }

      // 保存脚本定义信息
      $('#business_category_btn_save').click(function () {
        if (!validator.form()) {
          return false;
        }
        // 收集表单数据
        $('#business_category_form').form2json(bean);
        bean['id'] = $('#business_category_id').val();
        var addRoles = $business_category_table.bootstrapTable('getData');
        bean.addRoles = addRoles;
        $.each(oldRoles, function (i, item) {
          var isDel = true;
          $.each(addRoles, function (j, item2) {
            if (item2.uuid === item.uuid) {
              isDel = false;
              return false;
            }
          });
          if (isDel) {
            bean['delRoleIds'].push(item.uuid);
          }
        });

        JDS.call({
          service: 'businessCategoryService.save',
          data: [bean],
          async: false,
          validate: true,
          success: function (result) {
            if (result.data === 'success') {
              top.appModal.success('保存成功！');
              appContext.getNavTabWidget().refreshParentTabAndCloseTab();
            } else {
              top.appModal.error(result.data);
            }
          }
        });
      });
    },

    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppBusinessCategorySetDevelopment;
});
