/**
 * 扩展 vue 的实例特性
 * @param {*} Vue
 */
export const install = (Vue) => {
  Vue.prototype.$orgSelectPopup = {
    show(params, options) {
      let pointPageUrl = getCurrentPages()[getCurrentPages().length - 1].route;
      if (pointPageUrl == "/packages/_/pages/org/unit/org_select_popup") return;
      uni.navigateTo({
        url: "/packages/_/pages/org/unit/org_select_popup",
        success: function (res) {
          // 利用事件 通知 目标页面
          res.eventChannel.emit("orgSelectPopup", params, options);
        },
      });
    },
  };
};
