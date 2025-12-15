define(['constant', 'commons', 'server', 'appContext', 'appModal', 'wSelect2', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  wSelect2,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppDataTransWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppDataTransWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var bean = {
        uuid: null,
        name: null,
        id: null,
        code: null,
        sourceId: null,
        destinationId: null,
        xsl: null
      };
      $('#trans_module_form').json2form(bean);

      getExDataTypeList();
      function getExDataTypeList() {
        JDS.call({
          service: 'exchangeDataTypeService.getExDataTypeList',
          async: false,
          version: '',
          success: function (result) {
            var selectData = [];
            $.each(result.data, function (i) {
              selectData.push({
                id: result.data[i].id,
                text: result.data[i].name
              });
            });
            var filedList = ['sourceId', 'destinationId'];
            for (var i = 0; i < filedList.length; i++) {
              $('#' + filedList[i]).wSelect2({
                data: selectData,
                valueField: filedList[i],
                remoteSearch: false
              });
            }
          }
        });
      }

      var uuid = GetRequestParam().uuid;
      if (uuid) {
        getModuleById(uuid);
      }

      function getModuleById(uuid) {
        JDS.call({
          service: 'exchangeDataTransformService.getBeanByUuid',
          async: false,
          data: [uuid],
          version: '',
          success: function (result) {
            bean = result.data;
            $('#trans_module_form').json2form(bean);
            $('#sourceId').trigger('change');
            $('#destinationId').trigger('change');
          }
        });
      }

      $('#transform_btn_save')
        .off()
        .on('click', function () {
          $('#trans_module_form').form2json(bean);

          JDS.call({
            service: 'exchangeDataTransformService.save',
            data: [bean],
            async: false,
            validate: true,
            version: '',
            success: function (result) {
              if (result.success) {
                appModal.success('保存成功!', function () {
                  appContext.getNavTabWidget().closeTab();
                });
              }
            }
          });
        });
    },
    refresh: function () {
      this.init();
    }
  });
  return AppDataTransWidgetDevelopment;
});
