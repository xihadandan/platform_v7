import Vue from "vue";
import Vuex from "vuex";
import storage from "../storage";

Vue.use(Vuex);

const store = new Vuex.Store({
  state: {
    /**
     * 是否需要强制登录
     */
    forcedLogin: true,
    hasLogin: false,
    loginPageUrl: LOGIN_PAGE_PATH,
    system: undefined,
    userName: "",
    univerifyErrorMsg: "",
    hideUniverify: true,
    currentAppPage: "", // 当前页面
    customNavBar: false, // 是否自定义标题栏
    navBarTitle: "", // 当前页面标题栏标题
    customTabBar: false, // 是否自定义底部选项卡
    appPageNavBarTitles: {}, // <页面，标题栏>
    themeName: uni.getStorageSync("currentTheme") || "uni_app_light", // 当前主题
    themeList: [], // 主题列表
    themeStyle: {
      // 主题样式
      uni_app_light: "",
    },
    workbenchAppPiPath: "", // 工作台的产品集成路径
    workbenchPageUuid: "", // 工作台的页面UUID
    pageOverflow: "visible", //page-meta 组件配合阻止滚动穿透
    pageOptions: undefined,
    returnData: null, // 返回数据
    returnRefresh: false, // 是否刷新页面
  },
  getters: {
    theme(state) {
      let themeStyle =
        state.themeStyle[state.themeName] || (state.themeList.length > 0 && state.themeStyle[state.themeList[0].value]);
      return themeStyle;
    },
  },
  mutations: {
    loginPageUrl(state, loginPageUrl) {
      state.loginPageUrl = loginPageUrl;
    },
    login(state, userName) {
      state.userName = userName || "新用户";
      state.hasLogin = true;
    },
    loginByUserDetails(state, userDetails) {
      state.userName = userDetails.userName || "新用户";
      state.hasLogin = true;
      storage.setAccessToken(userDetails.token);
      storage.setUsername(userDetails.userName);
      if (userDetails.roles == undefined && userDetails.authorities && userDetails.authorities.length > 0) {
        userDetails.roles = userDetails.authorities.map((item) => item.authority);
      }
      storage.setUserDetails(userDetails);
      Vue.prototype._$USER = userDetails;
      const system = storage.getSystem();
      if (system) {
        Vue.prototype._$SYSTEM_ID = system;
      }
    },
    logout(state) {
      state.userName = "";
      state.hasLogin = false;
      storage.setSystem("");
      Vue.prototype._$SYSTEM_ID = "";
      storage.clearAccessToken();
      storage.setUsername("");
      storage.setUserDetails({});
    },
    setHasLogin(state, hasLogin = false) {
      state.hasLogin = hasLogin;
    },
    setUniverifyErrorMsg(state, payload = "") {
      state.univerifyErrorMsg = payload;
    },
    setHideUniverify(state, payload = false) {
      state.hideUniverify = payload;
    },
    setCurrentAppPage(state, appPage) {
      state.currentAppPage = appPage;
    },
    setCustomNavBar(state, payload = false) {
      state.customNavBar = payload;
    },
    setNavBarTitle(state, navBarTitle) {
      state.navBarTitle = navBarTitle;
      state.appPageNavBarTitles[state.currentAppPage] = navBarTitle;
    },
    updateNavBarTitleByAppPage(state, appPage) {
      state.navBarTitle = state.appPageNavBarTitles[appPage];
    },
    setCustomTabBar(state, payload = false) {
      state.customTabBar = payload;
    },
    setTheme(state, themeName = "light") {
      state.themeName = themeName;
      uni.setStorageSync("currentTheme", themeName);
    },
    setThemeList(state, themeList = []) {
      state.themeList = themeList;
    },
    setThemeStyle(state, themeStyle = {}) {
      state.themeStyle = themeStyle;
    },
    setWorkbenchAppPiPath(state, appPiPath = "") {
      state.workbenchAppPiPath = appPiPath;
    },
    setWorkbenchPageUuid(state, pageUuid = "") {
      state.workbenchPageUuid = pageUuid;
    },
    setPageOverFlow(state, overflow = "") {
      state.pageOverflow = overflow;
    },
    setPageOptions(state, options) {
      state.pageOptions = options;
    },
    setReturnData(state, payload) {
      state.returnData = payload;
    },
    setRefreshFlag(state, flag) {
      state.returnRefresh = flag;
    },
  },
  actions: {
    // 页面返回操作
    handlePageReturn({ commit }, data) {
      commit("setReturnData", data);
      commit("setRefreshFlag", true);
    },
    // 登录
    // Login ({ commit }, userInfo) {
    //   return new Promise((resolve, reject) => {
    //     login(userInfo).then(response => {
    //       const result = response.data
    //       storage.set(ACCESS_TOKEN, result.token, 7 * 24 * 60 * 60 * 1000)
    //       commit('SET_TOKEN', result.token)
    //       commit('SET_NAME', { name: result.loginName, welcome: welcome() })
    //       resolve()
    //     }).catch(error => {
    //       reject(error)
    //     })
    //   })
    // },

    // 登出
    logout({ commit, state }) {
      return new Promise((resolve, reject) => {
        uni.request({
          url: "/security_logout",
          method: "GET",
          data: {},
          success: (res) => {
            commit("logout", state);
            resolve(res);
          },
          fail: (error) => {
            reject(error);
          },
        });
      });
    },
  },
});

export default store;
