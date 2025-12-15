"use strict";

const { appContext } = require("wellapp-uni-framework");

module.exports = {
  id: "btn_list_view_add",
  performed: function (options) {
    // 设置页面参数使用的数据为选择的
    let pageParameters = options.pageParameters || {};
    let extraParams = pageParameters.extraParams || {};
    // extraParams["ep_view_mode"] = "1";

    pageParameters.extraParams = extraParams;
    options.pageParameters = pageParameters;
    options.pageUrl = "/uni_modules/w-app/pages/dms/dms_dyform_view";
    appContext.startApp(options);
  },
};
