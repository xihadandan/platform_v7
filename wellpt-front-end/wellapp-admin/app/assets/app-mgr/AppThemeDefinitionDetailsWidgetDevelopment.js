define(['constant', 'commons', 'server', 'appModal', 'AppPtMgrDetailsWidgetDevelopment', 'AppPtMgrCommons', 'formBuilder'], function (
  constant,
  commons,
  server,
  appModal,
  AppPtMgrDetailsWidgetDevelopment,
  AppPtMgrCommons,
  formBuilder
) {
  var JDS = server.JDS;
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;
  var UUID = commons.UUID;
  var Validation = server.Validation;
  var SecurityUtils = server.SpringSecurityUtils;

  // 应用视图列表
  var listView = null;
  // 表单验证器
  var validator = null;

  // 平台管理_产品集成_主题定义详情_HTML组件二开
  var AppThemeDefinitionDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
    // AppApplication的VO类
    this.bean = {
      uuid: null, // UUID
      recVer: null, // 版本号
      name: null, // 名称
      id: null, // ID
      code: null, // 编号
      sortOrder: null, // 排序号
      applyTo: null, // 应用于
      enabled: null, // 是否启用
      definitionJsonUuid: null, // 定义JSON UUID
      remark: null, // 备注
      systemUnitId: null // 归属系统单位ID
    };
  };

  // 接口方法
  commons.inherit(AppThemeDefinitionDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      // 获取传给组件的参数
      var params = _self.getWidgetParams();
      // 验证器
      validator = Validation.validate({
        beanName: 'appThemeEntity',
        container: widget.element,
        wrapperForm: true
      });

      // 应用于
      $('#applyTo').wSelect2({
        serviceName: 'dataDictionaryService',
        params: {
          type: 'APP_THEME_APPLY_TO'
        },
        remoteSearch: false,
        width: '100%',
        height: 250
      });

      // 隐藏配置按钮
      $('#btn_config', $container).hide();

      // 绑定事件
      _self._bindEvents();
    },
    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var parentWidget = _self.getParentWidget();
      // 监听应用列表事件
      // 新增
      parentWidget.on('AppThemeDefinitionListView.addRow', function (e, ui) {
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });
        listView = e.detail.ui;
        // 生成ID
        AppPtMgrCommons.idGenerator.generate($('#id', $container), 'theme_');
        $('#applyTo', $container).trigger('change');
        // ID可编辑
        $('#id', $container).prop('readonly', '');
        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');
        $('#btn_config', $container).hide();
      });
      // 删除行
      parentWidget.on('AppThemeDefinitionListView.deleteRow', function (e, _ui) {
        AppPtMgrCommons.clearForm({
          includeHidden: true,
          container: $container
        });
        $('#btn_config', $container).hide();
      });
      // 行点击
      parentWidget.on('AppThemeDefinitionListView.clickRow', function (e, ui) {
        _self.bean = $.extend(true, {}, e.detail.rowData);
        // 归属集成信息UUID
        AppPtMgrCommons.json2form({
          json: _self.bean,
          container: $container
        });
        listView = ui;
        $('#applyTo', $container).trigger('change');
        // ID只读
        $('#id', $container).prop('readonly', 'readonly');
        validator.form();
        $('#btn_config', $container).show();
      });

      // 保存
      $('#btn_save', $container).on('click', function () {
        if (!validator.form()) {
          return false;
        }
        AppPtMgrCommons.form2json({
          json: _self.bean,
          container: $container
        });
        JDS.restfulPost({
          url: `/proxy/api/app/theme/definition/save`,
          data: _self.bean,
          success: function (result) {
            appModal.success('保存成功！');
            // 保存成功刷新列表
            _self.refresh(listView);
          }
        });
      });

      // 配置
      $('#btn_config', $container).on('click', function () {
        _self.showThemeConfigDialog();
      });
    },
    showThemeConfigDialog: function () {
      var _self = this;
      var uuid = _self.bean.uuid;
      var title = '主题配置';
      var dlgId = UUID.createUUID();
      var dlgSelector = '#' + dlgId;
      var message = "<div id='" + dlgId + "'></div>";
      var dlgOptions = {
        title: title,
        size: 'large',
        message: message,
        shown: function () {
          _self.getDefinitionJson(uuid, function (definitionJson) {
            var objectJson = (definitionJson && JSON.parse(definitionJson)) || {};
            formBuilder.buildTextarea({
              label: '',
              labelColSpan: '0',
              name: 'definitionJson',
              controlColSpan: '12',
              value: objectJson.source || JSON.stringify(objectJson),
              placeholder: '',
              rows: 20,
              container: dlgSelector
            });
          });
        },
        buttons: {
          confirm: {
            label: '保存',
            className: 'btn-primary',
            callback: function () {
              var jsonObject = {};
              var definitionJson = $('#definitionJson', dlgSelector).val();
              try {
                if (!/^{.*}$/gs.test(definitionJson)) {
                  appModal.error('请输入正确的主题JSON定义！');
                  return false;
                }

                // 不是纯JSON格式，尝试js json对象定义
                if (!_self.isValidJsonString(definitionJson)) {
                  var expression = 'jsonObject = ' + definitionJson;
                  eval(expression);
                  jsonObject.source = definitionJson;
                  definitionJson = JSON.stringify(jsonObject);
                }
              } catch (e) {
                appModal.error('请输入正确的主题JSON定义！');
                return false;
              }
              _self.saveDefinitionJson(uuid, definitionJson);
              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {
              return true;
            }
          }
        }
      };
      appModal.dialog(dlgOptions);
    },
    isValidJsonString: function (jsonString) {
      try {
        JSON.parse(jsonString);
      } catch (e) {
        return false;
      }
      return true;
    },
    getDefinitionJson: function (uuid, callback) {
      var _self = this;
      JDS.restfulGet({
        url: `/proxy/api/app/theme/getDefinitionJson?uuid=${uuid}`,
        success: function (result) {
          callback.call(_self, result.data);
        }
      });
    },
    saveDefinitionJson: function (uuid, definitionJson) {
      var _self = this;
      JDS.restfulPost({
        url: `/proxy/api/app/theme/saveDefinitionJson?uuid=${uuid}`,
        data: definitionJson,
        success: function (result) {
          appModal.success('保存成功！');
        }
      });
    }
  });
  return AppThemeDefinitionDetailsWidgetDevelopment;
});
