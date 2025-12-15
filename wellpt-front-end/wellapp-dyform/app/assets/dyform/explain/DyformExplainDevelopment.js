define(['jquery', 'server', 'commons', 'constant', 'appContext', 'DyformDevelopment'], function (
  $,
  server,
  commons,
  constant,
  appContext,
  DyformDevelopment
) {
  var DyformExplainDevelopment = function () {
    DyformDevelopment.apply(this, arguments);
  };

  commons.inherit(DyformExplainDevelopment, DyformDevelopment, {
    // 设值前
    beforeSetData: function () {
      // var btnOptions = [
      //   {
      //     inputMode: '6', // 列表附件
      //     haveEditAuth: {
      //       code: 'download_btn'
      //     },
      //     notHaveEditAuth: {
      //       code: 'copy_name_btn'
      //     }
      //   },
      //   {
      //     inputMode: '4', // 图标附件
      //     haveEditAuth: {
      //       code: 'btn-edit',
      //       readOnly: true
      //     },
      //     notHaveEditAuth: {
      //       code: 'btn-edit',
      //       readOnly: true
      //     }
      //   }
      // ];
      // this.dyform.setFileCtlBtnEventOptions(btnOptions);
    }
  });
  return DyformExplainDevelopment;
});
