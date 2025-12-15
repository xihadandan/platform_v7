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

  // 平台应用_公共资源_业务通讯录列表二开
  var AppBusinessBookListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppBusinessBookListDevelopment, ListViewWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {},
    // 创建后回调
    create: function () {},
    // 初始化回调
    init: function () {},

    btn_set: function () {
      var _self = this;
      var uuids = _self.getSelectionUuids();
      if (!uuids.length) {
        appModal.alert('请选择列!');
        return;
      }

      var unit = SpringSecurityUtils.getCurrentUserUnitId();
      if (unit == 'S0000000000') {
        unit = '';
      }
      var userIds = _self.getSelectionsFields('MANAGE_USER')[0];
      var userNames = _self.getSelectionsFields('MANAGE_USER_VALUE')[0];
      $.unit2.open({
        valueField: '',
        labelField: '',
        title: '请选择管理员',
        type: 'all',
        multiple: true,
        selectTypes: 'U',
        valueFormat: 'justId',
        initValues: userIds,
        initLabels: userNames,
        unitId: unit,
        callback: function (values, labels) {
          if (!values.length) {
            appModal.alert('请选择管理员!');
            return;
          }
          JDS.call({
            service: 'businessCategoryService.updateManageUser',
            data: [uuids, values.join(';'), labels.join(';')],
            success: function (result) {
              appModal.success('保存成功!');
              _self.refresh(true);
            }
          });
        }
      });
    }
  });
  return AppBusinessBookListDevelopment;
});
