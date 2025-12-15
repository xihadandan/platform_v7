define([
  'constant',
  'commons',
  'server',
  'appModal',
  'AppPtMgrDetailsWidgetDevelopment',
  'AppPtMgrCommons',
  'dataStoreBase',
  'formBuilder',
  'design_commons'
], function (
  constant,
  commons,
  server,
  appModal,
  AppPtMgrDetailsWidgetDevelopment,
  AppPtMgrCommons,
  DataStore,
  formBuilder,
  designCommons
) {
  var validator;
  var listView;
  var orginalButtons = [];
  var orginalMethods = [];
  var applyToSelections = [];
  // 平台管理_产品集成_权限菜单详情_HTML组件二开
  var AppSecurityMenuDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppSecurityMenuDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      // 验证器
      validator = server.Validation.validate({
        beanName: 'resource',
        container: this.widget.element,
        wrapperForm: true
      });

      _self._initButtonInfoTable();
      _self._initMethodInfoTable();

      // 绑定事件
      _self._bindEvents();
    },

    _initButtonInfoTable: function () {
      var _self = this;
      var $container = $(this.widget.element);
      var $buttonInfoTable = $('#table_menu_button_info', $container);
      // 定义新增，删除按钮事件

      server.JDS.restfulGet({
        url: '/proxy/api/dict/getDataDictionariesByType/SECURITY_DYBTN',
        success: function (result) {
          if (result.success) {
            applyToSelections = [];
            for (var i = 0, len = result.data.length; i < len; i++) {
              applyToSelections.push({
                id: result.data[i].code,
                text: result.data[i].name,
                value: result.data[i].code
              });
            }
          }
        },
        async: false
      });

      formBuilder.bootstrapTable.initTableTopButtonToolbar(
        'table_menu_button_info',
        'menu_button_info',
        $container,
        {
          name: '',
          code: '',
          target: '',
          isDefault: false,
          url: '',
          remark: '',
          applyTo: '',
          className: ''
        },
        'GUUID'
      );
      // "uuid", "名称", "编号", "URL", "JS代码", "动态按钮", "默认显示", "应用于", "CLASS样式", "备注"
      // 列定义
      $buttonInfoTable.bootstrapTable('destroy').bootstrapTable({
        data: [],
        idField: 'GUUID',
        uniqueKey: 'GUUID',
        striped: false,
        showColumns: false,
        toolbar: $('#div_menu_button_info_toolbar', $container),
        width: 500,
        horizontalScroll: true,
        columns: [
          {
            field: 'checked',
            formatter: function (value, row, index) {
              if (value) {
                return true;
              }
              return false;
            },
            checkbox: true
          },
          {
            field: 'GUUID',
            title: 'GUUID',
            visible: false
          },
          {
            field: 'uuid',
            title: 'UUID',
            visible: false
          },
          {
            field: 'name',
            title: '名称',
            width: 100,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true,
              validate: function (value) {
                if (value == '') {
                  return '请输入名称!';
                }
              }
            }
          },
          {
            field: 'code',
            title: '编号',
            width: 100,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true,
              validate: function (value) {
                if (value == '') {
                  return '请输入编号!';
                }
                if (!_.startsWith(value, 'B')) {
                  return '按钮编号需要以B开头！';
                }
              }
            }
          },
          {
            field: 'url',
            title: 'URL',
            width: 200,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          },
          {
            field: 'target',
            title: 'JS代码',
            width: 220,
            editable: {
              type: 'textarea',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          },
          {
            field: 'isDefault',
            title: '默认显示',
            width: 80,
            formatter: function (value, row, index) {
              return designCommons.bootstrapTable.checkbox.formatter(value ? '1' : '0', 'isDefault');
            },
            events: {
              'change :checkbox': function (event, value, row, index) {
                row.isDefault = this.checked;
              }
            }
          },
          {
            field: 'applyTo',
            title: '应用于',
            width: 120,
            editable: {
              type: 'checklist',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true,
              separator: ';',
              source: applyToSelections
            }
          },
          {
            field: 'className',
            title: 'class样式',
            width: 120,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          },
          {
            field: 'remark',
            title: '备注',
            width: 220,
            editable: {
              type: 'textarea',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          }
        ]
      });
    },

    _initMethodInfoTable: function () {
      var _self = this;
      var $container = $(this.widget.element);
      var $methodInfoTable = $('#table_menu_method_info', $container);
      // 定义新增，删除按钮事件
      formBuilder.bootstrapTable.initTableTopButtonToolbar(
        'table_menu_method_info',
        'menu_method_info',
        $container,
        {
          name: '',
          code: '',
          target: '',
          remark: ''
        },
        'GUUID'
      );

      // 列定义
      $methodInfoTable.bootstrapTable('destroy').bootstrapTable({
        data: [],
        idField: 'GUUID',
        uniqueKey: 'GUUID',
        striped: false,
        showColumns: false,
        toolbar: $('#div_menu_method_info_toolbar', $container),
        width: 500,
        columns: [
          {
            field: 'checked',
            formatter: function (value, row, index) {
              if (value) {
                return true;
              }
              return false;
            },
            checkbox: true
          },
          {
            field: 'GUUID',
            title: 'GUUID',
            visible: false
          },
          {
            field: 'uuid',
            title: 'UUID',
            visible: false
          },
          {
            field: 'name',
            title: '名称',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true,
              validate: function (value) {
                if (value == '') {
                  return '请输入名称!';
                }
              }
            }
          },
          {
            field: 'code',
            title: '编号',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true,
              validate: function (value) {
                if (value == '') {
                  return '请输入编号!';
                }
                if (!_.startsWith(value, 'M')) {
                  return '方法编号需要以M开头！';
                }
              }
            }
          },
          {
            field: 'target',
            title: '服务调用',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          },
          {
            field: 'remark',
            title: '备注',
            editable: {
              type: 'textarea',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          }
        ]
      });
    },

    _refreshButtonInfoTable: function (rowData) {
      $('#table_menu_button_info', this.widget.element).bootstrapTable('load', rowData);
      $('#table_menu_button_info', this.widget.element).bootstrapTable('resetView');
    },

    _refreshMethodInfoTable: function (rowData) {
      $('#table_menu_method_info', this.widget.element).bootstrapTable('load', rowData);
      $('#table_menu_method_info', this.widget.element).bootstrapTable('resetView');
    },

    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var pageContainer = _self.getPageContainer();

      pageContainer.off('AppSecurityMenuTreeView.editTreeNode');
      pageContainer.on('AppSecurityMenuTreeView.editTreeNode', function (e, ui) {
        var param = e.detail;
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });

        _self._refreshButtonInfoTable([]);
        _self._refreshMethodInfoTable([]);
        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');

        //设置父节点UUID
        var treeObj = $.fn.zTree.getZTreeObj('security_menu_tree');
        if (treeObj) {
          var selected = treeObj.getSelectedNodes();
          if (selected.length == 1 && selected[0].id != -1) {
            $('#parentUuid', $container).val(selected[0].id);
          }
        }

        if (param.uuid) {
          //加载数据字典详情
          _self._loadSecurityMenuDetails(param.uuid);
        }
      });

      $('#btn_save_menu', $container).on('click', function () {
        _self._saveSecurityMenu();
        return false;
      });

      $('a[data-toggle="tab"]', $container).on('shown.bs.tab', function (e) {
        if ($(e.target).attr('aria-controls') == 'menu_method_info') {
          $('#table_menu_method_info', _self.widget.element).bootstrapTable('resetView');
        }
        if ($(e.target).attr('aria-controls') == 'menu_button_info') {
          $('#table_menu_button_info', _self.widget.element).bootstrapTable('resetView');
        }
        return true;
      });
    },

    _saveSecurityMenu: function () {
      var _self = this;
      var bean = this._bean();
      AppPtMgrCommons.form2json({
        json: bean,
        container: this.widget.element,
        ignorePropertys: ['moduleId']
      });

      //收集按钮
      var buttons = $('#table_menu_button_info', this.widget.element).bootstrapTable('getData');
      for (var i = 0, len = orginalButtons.length; i < len; i++) {
        if (orginalButtons[i].applyTo && Array.isArray(orginalButtons[i].applyTo)) {
          orginalButtons[i].applyTo = orginalButtons[i].applyTo.join(';');
        }
        if (
          _.findIndex(buttons, {
            uuid: orginalButtons[i].uuid
          }) == -1
        ) {
          bean.deletedButtons.push(orginalButtons[i]);
        }
      }
      for (var i = 0, len = buttons.length; i < len; i++) {
        buttons[i].moduleId = bean.moduleId;
        if (buttons[i].applyTo && Array.isArray(buttons[i].applyTo)) {
          buttons[i].applyTo = buttons[i].applyTo.join(';');
        }
      }
      bean.changedButtons = buttons;

      var methods = $('#table_menu_method_info', this.widget.element).bootstrapTable('getData');
      for (var i = 0, len = orginalMethods.length; i < len; i++) {
        if (
          _.findIndex(methods, {
            uuid: orginalMethods[i].uuid
          }) == -1
        ) {
          bean.deletedMethods.push(orginalMethods[i]);
        }
      }
      for (var i = 0, len = methods.length; i < len; i++) {
        methods[i].moduleId = bean.moduleId;
      }
      bean.changedMethods = methods;

      var treeObj = $.fn.zTree.getZTreeObj('security_menu_tree');
      if (!bean.parentUuid && !bean.uuid) {
        //没有选择父节点的情况下，默认是根节点
        treeObj.selectNode(treeObj.getNodes()[0]);
      }

      if (!validator.form()) {
        return false;
      }

      server.JDS.restfulPost({
        url: '/proxy/api/security/resource/saveBean',
        data: bean,
        success: function (result) {
          appModal.success('保存成功！');
          // 保存成功刷新树
          var saveUuid = result.data;
          _self.widget.trigger('AppSecurityMenuTreeView.refreshDataDicZtree', {
            action: 'save',
            saveUuid: saveUuid,
            uuid: bean.uuid,
            name: bean.name
          });
        }
      });
    },

    _loadSecurityMenuDetails: function (uuid) {
      var _self = this;
      var $container = this.widget.element;
      server.JDS.restfulGet({
        url: `/proxy/api/security/resource/getBean/${uuid}`,
        success: function (result) {
          var bean = _self._bean();
          $.extend(bean, result.data);
          AppPtMgrCommons.json2form({
            json: bean,
            container: $container
          });
          for (var i = 0, len = bean.buttons.length; i < len; i++) {
            bean.buttons[i].GUUID = bean.buttons[i].uuid;
          }
          for (var i = 0, len = bean.methods.length; i < len; i++) {
            bean.methods[i].GUUID = bean.methods[i].uuid;
          }
          _self._refreshButtonInfoTable(bean.buttons);
          _self._refreshMethodInfoTable(bean.methods);
          orginalButtons = $.extend(true, [], bean.buttons);
          orginalMethods = $.extend(true, [], bean.methods);
          validator.form();
        }
      });
    },

    _bean: function () {
      return {
        uuid: null,
        name: null,
        code: null,
        type: null,
        target: null,
        url: null,
        enabled: null,
        dynamic: null,
        isDefault: null,
        issys: null,
        remark: null,
        parent: null,
        children: [],
        privileges: [],
        parentName: null,
        parentUuid: null,
        buttons: [],
        changedButtons: [],
        deletedButtons: [],
        methods: [],
        changedMethods: [],
        deletedMethods: [],
        moduleId: this._moduleId()
      };
    },
    _moduleId: function () {
      return this.getWidgetParams().moduleId;
    }
  });
  return AppSecurityMenuDetailsWidgetDevelopment;
});
