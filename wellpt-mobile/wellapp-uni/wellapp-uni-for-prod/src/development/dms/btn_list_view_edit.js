"use strict";

const { appContext, dmsDataServices } = require("wellapp-uni-framework");

module.exports = {
  id: "btn_list_view_edit",
  performed: function (options) {
    // 验证
    if (!dmsDataServices.validate(options)) {
      return;
    }

    // 设置页面参数使用的数据为选择的
    let selection = options.data;
    let pageParameters = options.pageParameters || {};
    let extraParams = pageParameters.extraParams || {};
    pageParameters.item = selection[0];
    extraParams["ep_view_mode"] = "1";

    pageParameters.extraParams = extraParams;
    options.pageParameters = pageParameters;
    options.pageUrl = "/uni_modules/w-app/pages/dms/dms_dyform_view";
    appContext.startApp(options);
  },
};
