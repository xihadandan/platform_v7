define('global', [], function () {
  window.Browser = window.Browser || {};
  Browser.getAllQueryString = function () {
    var parts = location.search.substr(1).split('&');
    var data = {};
    for (var i = 0; i < parts.length; i++) {
      var kv = parts[i].split('=');
      if (!data[kv[0]]) {
        data[kv[0]] = unescape(kv[1]);
      } else {
        if (Array.isArray(data[kv[0]])) {
          data[kv[0]].push(unescape(kv[1]));
        } else {
          data[kv[0]] = [data[kv[0]]].concat(unescape(kv[1]));
        }
      }
    }
    return data;
  };
  Browser.getQueryString = function (name, defaultValue) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
    var values = window.location.search.substr(1).match(reg);
    if (values != null) {
      return unescape(values[2]);
    }
    if (defaultValue != null) {
      return defaultValue;
    }
    return null;
  };
  window.StringUtils = window.StringUtils || {};
  // 判断字符串不为undefined、null、空串、空格串
  StringUtils.isNotBlank = function (string) {
    return typeof string != 'undefined' && string != null && $.trim(string) != '';
  };
  // 判断字符串为undefined、null、空串、空格串
  StringUtils.isBlank = function (string) {
    return typeof string == 'undefined' || string == null || $.trim(string) == '';
  };

  StringUtils.nvl = function (str, out) {
    return str == undefined || str == null ? (out == undefined ? '' : out) : str;
  };
  window.getCookie = function (name) {
    var cookieStr = window.plus ? window.plus.navigator.getCookie(window.location.origin) : document.cookie;
    var arr = cookieStr.match(new RegExp('(^| )' + name + '=([^;]*)(;|$)'));
    if (arr) {
      return arr[2];
    } else {
      return null;
    }
  };
  // 获取后端服务地址
  window.getBackendUrl = function () {
    return getCookie('backend.url');
  };
  return {
    Browser: Browser,
    StringUtils: StringUtils,
    getCookie: getCookie,
    getBackendUrl: getBackendUrl
  };
});

// 搬运自 wellpt-front-end/wellapp-web/app/public/js/pt/js/global.js

var SpringSecurityUtils = {};
SpringSecurityUtils.getCurrentUserId = function () {
  return getCookie('cookie.current.userId');
};

SpringSecurityUtils.getCurrentUserName = function () {
  var user = SpringSecurityUtils.getUserDetails();
  if (user) {
    return user['userName'];
  }
  return null;
};

SpringSecurityUtils.getCurrentUserUnitId = function () {
  var user = SpringSecurityUtils.getUserDetails();
  if (user) {
    return user['systemUnitId'] || user['unitId'];
  }
  return null;
};

SpringSecurityUtils.getCurrentUserUnitName = function () {
  var unitId = SpringSecurityUtils.getCurrentUserUnitId();
  if (unitId) {
    return SystemUnit.getNameById(unitId);
  }
  return null;
};

SpringSecurityUtils.getUserDetails = function (userId) {
  if (StringUtils.isBlank(userId)) {
    userId = SpringSecurityUtils.getCurrentUserId();
  }
  var key = 'userDetails_' + userId;
  var userDetail = null;
  // 本地有缓存用本地数据
  if (window.opener != null && window.opener.WebApp && window.opener.WebApp.userDetails) {
    userDetail = window.opener.WebApp.userDetails[key];
  } else {
    userDetail = $(window).data(key);
  }
  if (userDetail) {
    return userDetail;
  } else {
    var date = new Date();
    var t = date.getTime();
    if (userId) {
      $.ajax({
        url: ctx + '/api/org/user/getUserById',
        type: 'get',
        data: {
          userId: userId
        },
        async: false,
        success: function (result) {
          userDetail = result.data;
          if (window.opener != null && window.opener.WebApp) {
            var details = window.opener.WebApp.userDetails || {};
            details[key] = userDetail;
            window.opener.WebApp.userDetails = details;
          } else {
            $(window).data(key, userDetail);
          }
          if (userDetail == null) {
            $.ajax({
              type: 'GET',
              url: '/security/user/details/?t=' + t,
              contentType: 'application/json',
              dataType: 'json',
              async: false,
              success: function (success, statusText, jqXHR) {
                userDetail = success;
              }
            });
          }
        }
      });
    } else {
      $.ajax({
        type: 'GET',
        url: '/security/user/details/?t=' + t,
        contentType: 'application/json',
        dataType: 'json',
        async: false,
        success: function (success, statusText, jqXHR) {
          userDetail = success;
        }
      });
    }
    return userDetail;
  }
};

SpringSecurityUtils.hasRole = function (roleId) {
  var userDetails = SpringSecurityUtils.getUserDetails();
  if (userDetails == null || userDetails['authorities'] == null) {
    return false;
  }
  for (var i = 0; i < userDetails['authorities'].length; i++) {
    var authority = userDetails['authorities'][i];
    if (authority['authority'] === roleId) {
      return true;
    }
  }
  return false;
};

var SystemParams = {};
SystemParams.data = {};
SystemParams.getValue = function (key, defaultValue, localCache) {
  if (SystemParams.data[key] != null) {
    return SystemParams.data[key];
  }
  var storage = (window.opener && window.opener.sessionStorage) || window.sessionStorage;
  if (storage != null) {
    var val = storage.getItem('sys_param_' + key);
    if (val != null) {
      return val;
    }
  }
  var getTimestamp = new Date().getTime();
  $.ajax({
    type: 'GET',
    url: '/basicdata/system/param/get?key=' + key + '&timestamp=' + getTimestamp,
    contentType: 'application/json',
    dataType: 'json',
    async: false,
    success: function (success, statusText, jqXHR) {
      SystemParams.data[key] = success.data;
      // 指定浏览器本地缓存，默认true
      if (localCache !== false) {
        if (storage != null) {
          storage.setItem('sys_param_' + key, success.data);
        }
      }
    }
  });
  var dv = defaultValue == null ? '' : defaultValue;
  return SystemParams.data[key] == null ? dv : SystemParams.data[key];
};
