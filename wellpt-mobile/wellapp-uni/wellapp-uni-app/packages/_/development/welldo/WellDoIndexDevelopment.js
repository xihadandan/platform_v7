import VueWidgetDevelopment from "@develop/VueWidgetDevelopment";

class WellDoIndexDevelopment extends VueWidgetDevelopment {
  get META() {
    return {
      name: "welldo工作台二开",
      hook: {
        loginOut: "退出登录",
      },
    };
  }

  loginOut() {
    uni.removeStorageSync("login_type");
    uni.reLaunch({
      url: LOGIN_PAGE_PATH,
    });
  }

  get ROOT_CLASS() {
    return "WellDoIndexDevelopment";
  }
}

export default WellDoIndexDevelopment;
