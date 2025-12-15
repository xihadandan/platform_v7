"use strict";

module.exports = {
  id: "app-workflow-start-new-work",
  performed: function (options) {
    uni.navigateTo({
      url: "/uni_modules/w-app/pages/workflow/start_new_work",
    });
  },
};
