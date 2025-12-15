/**
 * 全局弹框事件
 * 扩展 vue 的实例特性
 * @param {*} Vue
 */
export const install = (Vue) => {
  // 从底部向上弹出操作菜单
  Vue.prototype.$ptActionSheet = {
    show(options) {
      let pointPageUrl = getCurrentPages()[getCurrentPages().length - 1].route;
      if (pointPageUrl == "/packages/_/pages/popup/action-sheet") return;
      uni.navigateTo({
        url: "/packages/_/pages/popup/action-sheet",
        success: function (res) {
          // 利用事件 通知 目标页面
          res.eventChannel.emit("ptActionSheet", options);
        },
      });
    },
  };
  // 附件列表弹框
  Vue.prototype.$ptPopup = {
    show(options) {
      let pointPageUrl = getCurrentPages()[getCurrentPages().length - 1].route;
      if (pointPageUrl == "/packages/_/pages/popup/global-popup") return;
      uni.navigateTo({
        url: "/packages/_/pages/popup/global-popup",
        success: function (res) {
          // 利用事件 通知 目标页面
          res.eventChannel.emit("ptPopup", options);
        },
      });
    },
  };
};
