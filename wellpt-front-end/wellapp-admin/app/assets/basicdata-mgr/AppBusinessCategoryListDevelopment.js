define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  ListViewWidgetDevelopment
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var JDS = server.JDS;

  // 平台应用_公共资源_业务类别列表二开
  var AppBusinessCategoryListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppBusinessCategoryListDevelopment, ListViewWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {},
    // 创建后回调
    create: function () {},
    // 初始化回调
    init: function () {},

    //删除
    btn_del: function () {
      var _self = this;
      var ids = _self.getSelectionUuids();
      if (!ids.length) {
        appModal.error('请选择记录!');
        return false;
      }
      appModal.confirm('确定要删除所选记录吗?', function (res) {
        if (res) {
          JDS.call({
            service: 'businessCategoryService.deleteByIds',
            data: [ids],
            success: function (result) {
              appModal.success('删除成功!');
              _self.refresh(true);
            }
          });
        }
      });
    }
  });
  return AppBusinessCategoryListDevelopment;
});
