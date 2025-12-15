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

  // 平台应用_产品集成开发及管理_功能编辑二开
  var AppFunctionSetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppFunctionSetDevelopment, HtmlWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {},
    // 创建后回调
    create: function () {},
    // 初始化回调
    init: function () {
      var _self = this;
      var _uuid;
      if (_self.widget.options.containerDefinition.params && _self.widget.options.containerDefinition.params.uuid) {
        _uuid = _self.widget.options.containerDefinition.params.uuid;
      }
      var form_selector = '#app_function_form';
      var formBean = {
        uuid: null, // UUID
        recVer: null, // 版本号
        name: null, // 名称
        id: null, // ID
        code: null, // 编号
        type: null, // 类型
        remark: null
      };

      $(form_selector).json2form(formBean);

      // 功能类型
      var items = SelectiveDatas.getItems('PT_APP_FUNCTION_TYPE');
      var functionTypes = $.map(items, function (item) {
        return {
          aceId: item.aceId,
          id: item.value,
          text: item.label
        };
      });

      // 功能信息只读
      $('input, select, textarea', form_selector).prop('readonly', 'readonly');
      $('#type', form_selector)
        .wellSelect({
          valueField: 'type',
          searchable: false,
          data: functionTypes
        })
        .wellSelect('readonly', true);

      if (_uuid) {
        getAppFunction();
      }
      // 根据UUID获取组织选择项
      function getAppFunction() {
        // JDS.call({
        //     service : "appFunctionMgr.getBean",
        //     data : _uuid,
        //     success : function(result) {
        server.JDS.restfulGet({
          url: `/proxy/api/app/pagemanager/getFunctionBean/${_uuid}`,
          success: function (result) {
            var bean = result.data;
            $(form_selector).json2form(bean);
            $('#type', form_selector).wellSelect('val', bean.type);
            $('.jsonview', form_selector).JSONView(bean.definitionJson, {
              collapsed: true
            });
          }
        });
      }
    },
    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppFunctionSetDevelopment;
});
