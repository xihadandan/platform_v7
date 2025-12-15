import storage from "../storage";
import appContext from "../app-context";
import pageContext from "../page-context";
import pageParams from "../page-param";

/**
 * 扩展 vue 的实例特性
 * @param {*} Vue
 * @param {*} config
 */
export const install = function (Vue, config = {}) {
  // 页面参数数据
  Vue.prototype.setPageParameter = function (name, value) {
    pageParams.setPageParameter(name, value);
  };
  Vue.prototype.getPageParameter = function (name) {
    return pageParams.getPageParameter(name);
  };

  Vue.prototype.pageContext = pageContext;
  Vue.prototype.appContext = appContext;

  const userDetails = storage.getUserDetails();
  if (userDetails) {
    Vue.prototype._$USER = userDetails;
  }
  const system = storage.getSystem();
  if (system) {
    Vue.prototype._$SYSTEM_ID = system;
  }

  Vue.prototype.$loading = function (obj) {
    if (obj === false) {
      uni.hideLoading();
    } else {
      uni.showLoading(typeof obj == "string" ? { title: obj } : obj || {});
    }
  };

  // 是否存在权限
  Vue.prototype._hasRole = function (role) {
    return role && this._$USER != undefined && this._$USER.roles != undefined && this._$USER.roles.includes(role);
  };
  // 是否存在任一权限
  Vue.prototype._hasAnyRole = function (roles) {
    let myRoles = this._$USER != undefined && this._$USER.roles != undefined ? this._$USER.roles  : [];
    if (roles && myRoles.length > 0) {
      let _roles = Array.isArray(roles) ? roles : [roles];
      for (let i = 0, len = _roles.length; i < len; i++) {
        if (myRoles.includes(_roles[i])) {
          return true;
        }
      }
    }
    return false;
  };



  // 时间格式化
  Date.prototype.format = function (fmt) {
    var o = {
      "M+": this.getMonth() + 1, //月份
      "DD+": this.getDate(), //日
      "HH+": this.getHours(), //小时
      "m+": this.getMinutes(), //分
      "s+": this.getSeconds(), //秒
      "q+": Math.floor((this.getMonth() + 3) / 3), //季度
      S: this.getMilliseconds(), //毫秒
    };

    if (/(y+)/.test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }

    for (var k in o) {
      if (new RegExp("(" + k + ")").test(fmt)) {
        fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
      }
    }

    return fmt;
  };

  Date.prototype.getQuarter = function () {
    return Math.floor(this.getMonth() / 3) + 1;
  };


};
