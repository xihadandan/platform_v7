"use strict";
import md5 from "../utils/md5";

import { isEmpty } from "lodash";

export default {
  getStorageSync: function (key) {
    return uni.getStorageSync(key);
  },
  setStorageSync: function (key, value) {
    uni.setStorageSync(key, value);
  },
  removeStorageSync: function (key) {
    uni.removeStorageSync(key);
  },
  getWellappBackendUrl: function () {
    let wellappBackendUrl =
      uni.getStorageSync("wellapp_backend_url") || process.env.VUE_APP_WELLAPP_BACKEND_URL || "http://127.0.0.1:8180";
    return wellappBackendUrl;
  },
  setWellappBackendUrl: function (wellappBackendUrl) {
    uni.setStorageSync("wellapp_backend_url", wellappBackendUrl);
  },
  setWellappWebUrl: function (url) {
    uni.setStorageSync("wellapp_web_url", url);
  },
  getWellappWebUrl: function () {
    return uni.getStorageSync("wellapp_web_url") || process.env.VUE_APP_WELLAPP_WEB_URL || "http://127.0.0.1:7001";
  },
  getAccessToken: function () {
    return uni.getStorageSync("uni_id_token");
  },
  setAccessToken: function (accessToken) {
    uni.setStorageSync("uni_id_token", accessToken);
  },
  clearAccessToken: function () {
    uni.removeStorageSync("uni_id_token");
  },
  setUsername: function (username) {
    uni.setStorageSync("username", username);
  },
  getUsername: function () {
    return uni.getStorageSync("username");
  },
  setSystem: function (system) {
    uni.setStorageSync("system", system);
  },
  getSystem: function () {
    return uni.getStorageSync("system") || process.env.VUE_APP_DEFAULT_SYSTEM_ID;
  },
  setUserDetails: function (userDetails) {
    uni.setStorageSync("user_details", userDetails);
  },
  getUserDetails: function () {
    return uni.getStorageSync("user_details");
  },
  // 填充访问资源的URL地址
  fillAccessResourceUrl: function (uri) {
    const _self = this;
    let wellappBackendUrl = _self.getWellappBackendUrl();
    let accessToken = _self.getAccessToken();
    let url = wellappBackendUrl + uri.replace("/proxy-repository", "");
    if (!isEmpty(accessToken)) {
      if (url.indexOf("?") > 0) {
        url += "&jwt=" + accessToken;
      } else {
        url += "?jwt=" + accessToken;
      }
    }
    return url;
  },
  fillAppResourceUrl: function (uri) {
    const _self = this;
    let webUrl = _self.getWellappWebUrl();
    return webUrl + uri;
  },
  _queue: {},
  _queueCallback: {},
  // 获取缓存，如果缓存存在则返回，否则排队等待缓存请求设置
  getStorageCache: function (_key, fetchPromise, callback) {
    let _this = this;
    let key = _this.keyWrapper(_key);
    let getItem = (key) => {
      return new Promise((resolve, reject) => {
        let data = _this.getStorageSync(key);
        if (data) {
          resolve(data);
        } else {
          reject();
        }
      });
    };
    let setItem = (key, data) => {
      return new Promise((resolve, reject) => {
        _this.setStorageSync(key, data);
        resolve(data);
      });
    };

    getItem(key)
      .then((result) => {
        if (typeof callback == "function") {
          callback(result);
        }
      })
      .catch(() => {
        if (_this._queue[key] == undefined) {
          _this._queue[key] = true;
          _this._queueCallback[key] = [];
          let result = fetchPromise;
          if (typeof fetchPromise === "function") {
            result = fetchPromise();
          }
          if (result != undefined) {
            let _setItem = (key, o) => {
              setItem(key, o).then(() => {
                console.log(`设置缓存数据 key = [ ${key} ]`, o);
                callback(o);
                let invokeQueueCallback = () => {
                  let cb = _this._queueCallback[key].shift();
                  if (cb != undefined) {
                    console.log(`排队获取缓存数据 key = [ ${key} ]`, o);
                    cb(o);
                    if (_this._queueCallback[key].length > 0) {
                      setTimeout(() => {
                        invokeQueueCallback();
                      }, 0);
                    } else {
                      delete _this._queue[key];
                    }
                  } else {
                    delete _this._queue[key];
                  }
                };
                invokeQueueCallback();
              });
            };
            if (result instanceof Promise) {
              result.then((o) => {
                _setItem(key, o);
              });
            } else {
              _setItem(key, result);
            }
          }
        } else {
          // 放到队列里面等待回调
          _this._queueCallback[key].push((r) => {
            if (typeof callback == "function") {
              callback(r);
            }
          });
        }
      });
  },
  //缓存 key 包裹
  keyWrapper: function (key) {
    let user = this.getUserDetails();
    let userId = user ? user.userId : "";
    let systemId = this.getSystem();
    if (!(typeof key == "string")) {
      key = md5(JSON.stringify(key));
    }
    return (systemId ? `${systemId}:` : "") + (userId ? `${userId}:` : "") + key;
  },
  // 移除缓存
  removeStorageCache: function (_key) {
    let _this = this;
    // console.log(`缓存数据库 ${this._dbInfo.storeName} -> 删除缓存 key = [ ${_key} ]`);
    if (_key == undefined) {
      // 移除所有缓存
      return new Promise((resolve, reject) => {
        uni.clearStorageSync();
        resolve();
      });
    }
    let key = _this.keyWrapper(_key);
    let removeItem = (key) => {
      return new Promise((resolve, reject) => {
        let data = _this.getStorageSync(key);
        _this.removeStorageSync(key);
        resolve(data);
      });
    };
    return new Promise((resolve, reject) => {
      delete _this._queue[key];
      removeItem(key).then((data) => {
        resolve(data);
      });
    });
  },
};
