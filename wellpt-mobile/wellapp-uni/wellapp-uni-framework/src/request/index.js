let uniRequest = uni.request;
import { isArray } from "lodash";
import storage from "../storage";
import errorHandler from "../error-handler";

uni.request = function (options) {
  // 请求token
  let token = storage.getAccessToken(); // uni.getStorageSync("uni_id_token");
  if (!options.header) {
    options.header = {};
  }
  if (token != null && token != "" && options.url != "/login/1") {
    options.header["Authorization-JWT"] = "Bearer " + token;
  }
  let locale = uni.getStorageSync("locale") || "zh_CN";
  locale = locale.replace("_", "-");
  locale = locale.toLowerCase();
  if (options.header) {
    options.header["Accept-Language"] = locale;
  }
  let system = storage.getSystem();
  if (system && options.header) {
    options.header["System_id"] = system;
  }
  // jds调用
  if (options.service) {
    options.url = "/json/data/services?isMobileApp=true";
    var jsonData = {};
    var service = options.service;
    var splitIndex = service.indexOf(".");
    jsonData.serviceName = service.substring(0, splitIndex);
    jsonData.methodName = service.substring(splitIndex + 1, service.length);
    var data = options.data;
    if (isArray(data) === true) {
      jsonData.args = JSON.stringify(data);
    } else {
      jsonData.args = JSON.stringify([data]);
    }
    options.method = "POST";
    options.data = JSON.stringify(jsonData);
    options.contentType = "application/json";
    options.dataType = "json";
  } else {
    var url = options.url;
    if (url.indexOf("isMobileApp") == -1) {
      if (url.indexOf("?") == "-1") {
        url += "?isMobileApp=true";
      } else {
        url += "&isMobileApp=true";
      }
      options.url = url;
    }
  }

  let success = options.success || function () {};
  options.success = (res) => {
    // 会话过期,data.code不一定是5002
    if (res.data && res.data.errorCode == "SessionExpired") {
      errorHandler.getInstance(true).handle(res);
      return;
    }
    success.call(this, res);
  };
  // 默认的异常处理
  if (!options.fail) {
    options.fail = (e) => {
      errorHandler.getInstance(true).handle(e);
    };
  }
  // 非h5
  if (process.env.UNI_PLATFORM !== "h5") {
    let url = options.url;
    if (!url.startsWith("http")) {
      // 从用户登录设置读取后端服务地址
      let wellappBackendUrl = storage.getWellappBackendUrl();
      options.url = wellappBackendUrl + url;
    }
  } else if (!options.url.startsWith("http") && !options.url.startsWith("/server-api")) {
    // h5 后端请求增加 api 请求前缀
    options.url = "/server-api" + options.url;
  }
  uniRequest.apply(this, arguments);
};

export default null;
