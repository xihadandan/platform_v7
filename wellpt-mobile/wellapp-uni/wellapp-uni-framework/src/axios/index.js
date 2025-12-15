import axios from "axios";
import storage from "../storage";
import { lowerCase, replace } from "lodash";

export const install = function (Vue) {
  axios.defaults.adapter = function (config) {
    return new Promise((resolve, reject) => {
      var settle = require("axios/lib/core/settle");
      var buildURL = require("axios/lib/helpers/buildURL");
      uni.request({
        method: config.method.toUpperCase(),
        url: buildURL(config.url, config.params, config.paramsSerializer),
        header: config.headers,
        data: config.data,
        dataType: config.dataType,
        responseType: config.responseType,
        sslVerify: config.sslVerify,
        complete: function complete(response) {
          response = {
            data: response.data,
            status: response.statusCode,
            errMsg: response.errMsg,
            header: response.header,
            config: config,
          };

          settle(resolve, reject, response);
        },
      });
    });
  };
  axios.interceptors.request.use((config) => {
    let token = storage.getAccessToken();
    if (!config.headers) {
      config.headers = {};
    }
    if (token) {
      config.headers["Authorization-JWT"] = "Bearer " + token;
    }
    let locale = uni.getStorageSync("locale") || "zh_CN";
    locale = locale.replace("_", "-");
    locale = locale.toLowerCase();
    config.headers["Accept-Language"] = locale;
    config.headers["is_mobile_app"] = "true";
    let system = storage.getSystem();
    if (system) {
      config.headers["System_id"] = system;
    }
    return config;
  });
  Vue.prototype.$axios = axios;
  uni.$axios = axios;
};
