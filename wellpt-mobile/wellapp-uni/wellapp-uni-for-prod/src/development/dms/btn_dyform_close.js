"use strict";

module.exports = {
  id: "btn_dyform_close",
  performed: function (options) {
    uni.navigateBack({
      delta: 1,
    });
  },
};
