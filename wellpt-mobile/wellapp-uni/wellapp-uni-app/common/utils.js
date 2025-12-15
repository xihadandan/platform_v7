import { univerifyLogin } from "@/common/univerify.js";
import store from "wellapp-uni-framework/src/store";
import { isEmpty, isString, isObject, each } from "lodash";

export function guideToLogin(loginPage) {
  let forcedLogin = store.state.forcedLogin;
  uni.showModal({
    title: "未登录",
    content: "您未登录，需要登录后才能继续",
    /**
     * 如果需要强制登录，不显示取消按钮
     */
    showCancel: !forcedLogin,
    success: (res) => {
      if (res.confirm) {
        univerifyLogin().catch((err) => {
          if (err === false) return;
          /**
           * 如果需要强制登录，使用reLaunch方式
           */
          store.dispatch("Logout").then(() => {
            if (forcedLogin) {
              uni.reLaunch({
                url: loginPage || "/pages/oa/user/login",
              });
            } else {
              uni.navigateTo({
                url: loginPage || "/pages/oa/user/login",
              });
            }
          });
        });
      }
    },
  });
}

/**
 * 获取页面配置
 * @param {*} appPiPath /{系统id}/{模块id}/{应用id}
 * @param {*} pageUuid
 */
export function getAppPage(params, callback) {
  var _self = this;
  let key = "app_page_" + params.appPiPath + "_" + params.pageUuid;
  if (isEmpty(params.uniApp)) {
    params.uniApp = "true"; //配置页面是否为uniapp
  }
  // 从服务器取页面定义信息
  uni.request({
    url: "/webapp/get",
    data: params,
    method: "GET",
    async: false,
    success: (res) => {
      if (res.data.code == 403) {
        uni.showModal({
          content: "您无权限访问该页面，请联系管理员！",
          showCancel: false,
          success: function (res) {
            if (res.confirm) {
              uni.navigateBack({ delta: 1 });
            }
          },
        });
      } else if (res.data.errorCode === "SessionExpired") {
        guideToLogin();
      } else {
        callback.call(_self, res.data.data);
        if (res.data.data) {
          uni.setStorage({
            key: key,
            data: JSON.stringify(res.data.data),
          });
        }
      }
    },
    fail: () => {},
    complete: () => {},
  });
}

export function requestFun(arg, callback) {
  var _self = this;
  uni.request({
    url: arg.url,
    data: arg.data || {},
    method: arg.method || "GET",
    contentType: arg.contentType || "application/json",
    dataType: arg.dataType || "json",
    async: arg.async || false,
    header: arg.header || {},
    success: (res) => {
      if (res.data.code == 403) {
        uni.showModal({
          content: "您无权限访问该页面，请联系管理员！",
          showCancel: false,
          success: function (res) {
            if (res.confirm) {
              uni.navigateBack({ delta: 1 });
            }
          },
        });
      } else if (res.data.errorCode === "SessionExpired") {
        guideToLogin();
      } else {
        callback.call(_self, res.data);
      }
    },
    fail: () => {},
    complete: () => {},
  });
}

/**
 * 列表按钮，根据样式名获取对应颜色值
 * @param {*} classname
 */
export function getColorByClassName(classname) {
  let color = "";
  switch (classname) {
    case "btn-inverse": //黑色
      color = "#000000";
      break;
    case "btn-default": //灰色
      color = "#d4d4d4";
      break;
    case "btn-primary": //蓝色
      color = "#007aff";
      break;
    case "btn-success": //绿色
      color = "#3aa322";
      break;
    case "btn-info": //浅蓝
      color = "#2aaedd";
      break;
    case "btn-warning": //橙色
      color = "#e99f00";
      break;
    case "btn-danger": //红色
      color = "#e33033";
      break;
    default:
      break;
  }
  return color;
}
/**
 * 根据模板匹配并替换{}对应值
 * @param {*} template
 * @param {*} data
 */
export function replaceTemplate(template, data) {
  var regex = /\{(.+?)\}/g; //获取只有花括号的内容
  each(template.match(regex), function (item) {
    var reg = /^\{/gi;
    var reg2 = /\}$/gi;
    let citem = item.replace(reg, "");
    citem = citem.replace(reg2, "");
    template = template.replace(item, data[citem] || "");
  });
  return template;
}

/**
 * 判断字符串是否是json格式
 * @param {Object} str
 */
export function isJSON(str) {
  if (isString(str)) {
    try {
      var obj = JSON.parse(str);
      if (isObject(obj) && obj) {
        return obj;
      } else {
        return false;
      }
    } catch (e) {
      return false;
    }
  }
}
