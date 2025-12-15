// 获取cookie
function getCookie(name) {
  var arr = document.cookie.match(new RegExp('(^| )' + name + '=([^;]*)(;|$)'));
  if (arr) {
    return urldecode(arr[2]);
  } else {
    return null;
  }
}

(function () {
  var cache;

  window.getUserTheme = function () {
    if (cache) {
      return cache;
    }

    $.ajax({
      type: 'GET',
      url: '/api/getUserTheme',
      async: false,
      dataType: 'json',
      success: function (result) {
        cache = result;
      }
    });

    return cache;
  };
})();

// 确保contextPath有值
function getContextPath() {
  if (window.ctx === '') {
    window.contextPath = window.ctx;
    return window.ctx;
  }
  if (window.ctx == null || window.contextPath == null) {
    window.contextPath = getCookie('ctx');
    window.contextPath = window.contextPath == '""' ? '' : window.contextPath;
    window.ctx = window.contextPath;
  }
  return window.ctx;
}

// 获取后端服务地址
function getBackendUrl() {
  return getCookie('backend.url');
}

// 获取平台版本信息
function getSystemVersion() {
  if (window.ptVersion == null || window.ptVersion == undefined) {
    window.ptVersion = getCookie('cookie.current.version');
  }
  return window.ptVersion;
}

window.getFrontCache =
  window.getFrontCache ||
  (function (win) {
    var frontCache = getCookie('frontCache') || {
      default: new Date().getTime()
    };
    while (typeof frontCache === 'string') {
      // 两个双引号转义
      frontCache = JSON.parse(frontCache);
    }
    // 是否支持多模块，单模块忽略module参数
    var supportMultiModule = 'app' in frontCache;
    var getFrontCache = supportMultiModule ?
      function (module) {
        return module ? frontCache[module] : frontCache['default'];
      } :
      function (module) {
        return frontCache['default'];
      };
    win.appendUrlCache = function (url, module) {
      if (url.indexOf('?') > 0) {
        url += '&_t=' + getFrontCache(module); // 多模块模式，单模块可以不传参
      } else {
        url += '?_t=' + getFrontCache(module); // 多模块模式，单模块可以不传参
      }
      return url;
    };
    win['_ts'] = '?_t=' + getFrontCache();
    return getFrontCache;
  })(window);

getSystemVersion();
getContextPath();
// getFrontCache();
// 格式化消息参数工具类
MsgUtils = {};
// 格式化消息，填充参数
MsgUtils.format = function (msg) {
  for (var i = 1; i < arguments.length; i++) {
    var param = '{' + (i - 1) + '}';
    msg = msg.replace(param, arguments[i]);
  }
  return msg;
};

// 国际化资源加载器
I18nLoader = {
  scrips: {}
};
// 根据当前cookie的语言环境加载相应的js国际化信息文件
I18nLoader.load = function (prefixPath) {
  if (this.scrips[prefixPath] != null) {
    return;
  }
  var language = getCookie('language');
  try {
    $.ajaxSettings.async = false;
    $.ajaxSettings.cache = true;
    var v = '6.0';
    try {
      if (WebApp && WebApp.environment && WebApp.environment.app_version) {
        v = WebApp.environment.app_version;
      }
    } catch (e) {
    }

    if (language == 'zh_CN' || language == 'zh-CN') {
      $.getScript(staticPrefix + prefixPath + '_zh_CN.js?v=' + v);
    } else {
      $.getScript(staticPrefix + prefixPath + '_en.js?v=' + v);
    }
    this.scrips[prefixPath] = prefixPath;
  } catch (e) {
  } finally {
    $.ajaxSettings.async = true;
    $.ajaxSettings.cache = false;
  }
};

var StringUtils = StringUtils || {};
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

// Tab Utils
TabUtils = {};
TabUtils.openTab = function (tabid, text, url, icon) {
  if (top.addTab) {
    top.addTab(tabid, text, url, icon);
  } else {
    window.open(url);
  }
};
TabUtils.removeTab = function (tabid) {
  if (returnWindow) {
    if (window.opener != null) {
      returnWindow();
      window.close();
    } else {
      if (StringUtils.isBlank(ctx)) {
        window.location.href = '/';
      } else {
        window.location.href = ctx;
      }
    }
  } else if (top.navtab) {
    top.navtab.removeTabItem(tabid);
  } else {
    window.opener = null;
    if (StringUtils.isBlank(ctx)) {
      window.location.href = '/';
    } else {
      window.location.href = ctx;
    }
  }
};
TabUtils.reloadAndRemoveTab = function (tabid1, tabid2) {
  if (returnWindow) {
    if (window.opener != null) {
      returnWindow();
      window.close();
    } else {
      if (StringUtils.isBlank(ctx)) {
        window.location.href = '/';
      } else {
        window.location.href = ctx;
      }
    }
  } else if (top.navtab) {
    top.navtab.selectTabItem(tabid1);
    top.navtab.reload(tabid1);
    top.navtab.removeTabItem(tabid2);
  } else {
    window.opener = null;
    if (StringUtils.isBlank(ctx)) {
      window.location.href = '/';
    } else {
      window.location.href = ctx;
    }
  }
};
// JSON Data Services
var JDS = {};
JDS.call = function (options) {
  options = $.extend({
    data: [],
    ignoreLoginTenantId: null,
    ignoreLoginUserId: null,
    validate: false,
    authenticate: false,
    async: true,
    mask: false
  },
    options
  );
  var jsonData = {};
  var service = options.service;
  var splitIndex = service.indexOf('.');
  jsonData.tenantId = options.tenantId;
  jsonData.userId = options.userId;
  jsonData.serviceName = service.substring(0, splitIndex);
  jsonData.methodName = service.substring(splitIndex + 1, service.length);
  jsonData.validate = options.validate;
  var data = options.data;
  var appModal = window.appModal ? window.appModal : top.appModal;
  if ($.isArray(data) === true) {
    jsonData.args = JSON.stringify(data);
  } else {
    jsonData.args = JSON.stringify([data]);
  }
  if (appModal && options.mask === true) {
    appModal.showMask();
  }
  $.ajax({
    type: 'POST',
    url: '/json/data/services',
    data: JSON.stringify(jsonData),
    contentType: 'application/json',
    dataType: 'json',
    async: options.async,
    success: function (success, statusText, jqXHR) {
      if (appModal && options.mask === true) {
        appModal.hideMask();
      }
      if (options.success) {
        options.success.apply(this, [success, statusText, jqXHR]);
      }
    },
    error: function (jqXHR, statusText, error) {
      try {
        if (appModal && options.mask === true) {
          appModal.hideMask();
        }
        var faultData = JSON.parse(jqXHR.responseText);
        // 登录超时
        if (faultData != null && 'SessionExpired' == faultData.errorCode) {
          var logout = function () {
            if (top) {
              top.location.href = faultData.data;
            } else {
              location.href = faultData.data;
            }
          };
          appModal.alert(faultData.msg, logout);
        } else if (faultData != null && 'StaleObjectState' == faultData.errorCode) {
          // Hibernate乐观锁
          appModal.alert(faultData.msg);
        } else if (faultData != null && 'FieldValidation' == faultData.errorCode) {
          var fvMsg = faultData.msg;
          if (faultData.data.length > 0) {
            fvMsg = '';
            $.each(faultData.data, function () {
              this.field;
              var label = $("label[for='" + this.field + "']")
                .filter("label[class!='error']")
                .text();
              fvMsg += label + ': ' + this.msg + '\n\r';
            });
          }
          appModal.alert(fvMsg);
        } else if (options.error) {
          // 原生错误处理
          options.error.apply(this, [jqXHR, statusText, error]);
        } else {
          // 默认错误处理
          if (faultData != null && faultData.data != null && faultData.data.msg != null && $.trim(faultData.data.msg) != '') {
            appModal.alert(faultData.data.msg);
          } else if (faultData != null && faultData.msg != null && $.trim(faultData.msg) != '') {
            appModal.alert(faultData.msg);
          } else {
            alert(jqXHR.responseText);
          }
        }
      } catch (e) {
      }
    }
  });
};
var bsTime = null;
JDS.getServerDate = function () {
  // 记录服务器时间差异
  if (null == bsTime) {
    $.ajax({
      type: 'GET',
      async: false,
      cache: false,
      url: ctx + '/security/user/details',
      success: function (success, statusText, jqXHR) {
        // 记录服务器时间差异
        bsTime = new Date(jqXHR.getResponseHeader('Date')).getTime() - new Date().getTime();
      },
      error: function (jqXHR, statusText, error) {
        // 记录服务器时间差异
        bsTime = new Date(jqXHR.getResponseHeader('Date')).getTime() - new Date().getTime();
      }
    });
  }
  return new Date(new Date().getTime() + bsTime);
};
var Browser = {};
// 检测是否是IE浏览器
Browser.isIE = function () {
  var _uaMatch = $.uaMatch(navigator.userAgent);
  var _browser = _uaMatch.browser;
  if (_browser == 'msie') {
    return true;
  } else {
    return 'ActiveXObject' in window;
  }
};
// 检测是否是chrome浏览器
Browser.isChrome = function () {
  var _uaMatch = $.uaMatch(navigator.userAgent);
  var _browser = _uaMatch.browser;
  if (_browser == 'chrome' || _browser == 'webkit') {
    return true;
  } else {
    return false;
  }
};
// 检测是否是Firefox浏览器
Browser.isMozila = function () {
  var _uaMatch = $.uaMatch(navigator.userAgent);
  var _browser = _uaMatch.browser;
  if (_browser == 'mozilla') {
    return true;
  } else {
    return false;
  }
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

Browser.replaceParamVal = function (params) {
  var url = window.location.href;
  $.each(params, function (i, item) {
    re = eval('/(' + item.key + '=)([^&]*)/gi');
    url = url.replace(re, item.key + '=' + item.value);
  });
  return url;
};

// HashMap
var HashMap = function () {
  this.data = {};
};
HashMap.prototype = {
  constructor: HashMap,
  get: function (key) {
    return this.data[key];
  },
  put: function (key, value) {
    this.data[key] = value;
    return this.data[key];
  },
  remove: function (key) {
    var v = this.data[key];
    delete this.data[key];
    return v;
  },
  values: function () {
    var a = [];
    for (var key in this.data) {
      a.push(this.data[key]);
    }
    return a;
  },
  containsKey: function (key) {
    return this.data.hasOwnProperty(key);
  }
};
(function ($) {
  // 将JSON对象的数据设置到表单中
  $.fn.json2form = function (object, checkboxesAsString) {
    // 如果对象不存在属性直接忽略
    for (var property in object) {
      $(':input[name=' + property + ']', $(this)).each(function () {
        var type = this.type;
        var name = this.name;
        var data = object[name];
        switch (type) {
          case undefined:
          case 'text':
          case 'number':
          case 'password':
          case 'hidden':
          case 'button':
          case 'reset':
          case 'textarea':
          case 'submit': {
            $(this).val(data);
            break;
          }
          case 'checkbox':
          case 'radio': {
            this.checked = false;
            if ($.isArray(data) === true) {
              // checkbox
              // multiple value is Array
              for (var el in data) {
                if (data[el] == $(this).val()) {
                  this.checked = true;
                }
              }
            } else {
              // radio is a string single value
              if (type == 'radio') {
                if (data == $(this).val()) {
                  this.checked = true;
                }
              } else {
                if (checkboxesAsString && checkboxesAsString === true) {
                  data = data == null ? '' : data;
                  var a = data.split(',');
                  for (var el in a) {
                    if (a[el] == $(this).val()) {
                      this.checked = true;
                    }
                  }
                } else {
                  // 当个checkbox只有true、false
                  this.checked = data == true || data == 'true';
                }
              }
            }
            break;
          }
          case 'select':
          case 'select-one':
          case 'select-multiple': {
            $(this).find('option:selected').attr('selected', false);
            if ($.isArray(data) === true) {
              for (var el in data) {
                $(this)
                  .find("option[value='" + data[el] + "']")
                  .attr('selected', true);
              }
            } else {
              $(this)
                .find("option[value='" + data + "']")
                .attr('selected', true);
            }
            break;
          }
        }
      });
    }
  };

  // 将表单中的数据收集到指定的JSON对象中
  $.fn.form2json = function (object, checkboxesAsString) {
    // 如果表单元素不存在直接忽略
    for (var property in object) {
      var elements = $(':input[name=' + property + ']', $(this));
      if (elements.length == 0) {
        continue;
      }
      // 单个元素
      if (elements.length == 1) {
        var element = elements[0];
        var v = fieldValue(element);
        if (v && v.constructor == Array) {
          var a = [];
          for (var i = 0, max = v.length; i < max; i++) {
            a.push(v[i]);
          }
          object[property] = a;
        } else {
          if (v !== null && typeof v != 'undefined') {
            object[property] = v;
          }
          // checkbox只有一个值(true、false)
          if (element.type == 'checkbox') {
            if (element.checked === true) {
              object[property] = true;
            } else {
              object[property] = false;
            }
          }
        }
      }
      // 多个元素
      if (elements.length > 1) {
        var a = [];
        for (var i = 0; i < elements.length; i++) {
          var element = elements[i];
          var v = fieldValue(element);
          if (v && v.constructor == Array) {
            for (var i = 0, max = v.length; i < max; i++) {
              a.push(v[i]);
            }
          } else {
            if (v !== null && typeof v != 'undefined') {
              a.push(v);
            }
          }
        }
        // 单选框只有一个值
        if (elements[0].type == 'radio') {
          object[property] = a.join();
        } else {
          if (elements[0].type == 'checkbox' && checkboxesAsString && checkboxesAsString === true) {
            object[property] = a.join();
          } else {
            object[property] = a;
          }
        }
      }
    }
  };

  /**
   * Returns the value of the field element.
   */
  function fieldValue(el, successful) {
    var n = el.name,
      t = el.type,
      tag = el.tagName.toLowerCase();
    if (successful === undefined) {
      successful = true;
    }

    if (
      successful &&
      (!n ||
        el.disabled ||
        t == 'reset' ||
        t == 'button' ||
        ((t == 'checkbox' || t == 'radio') && !el.checked) ||
        ((t == 'submit' || t == 'image') && el.form && el.form.clk != el) ||
        (tag == 'select' && el.selectedIndex == -1))
    ) {
      return null;
    }

    if (tag == 'select') {
      var index = el.selectedIndex;
      if (index < 0) {
        return null;
      }
      var a = [],
        ops = el.options;
      var one = t == 'select-one' || t == 'text';
      var max = one ? index + 1 : ops.length;
      for (var i = one ? index : 0; i < max; i++) {
        var op = ops[i];
        if (op.selected) {
          var v = op.value;
          if (!v) {
            // extra pain for IE...
            v = op.attributes && op.attributes['value'] && !op.attributes['value'].specified ? op.text : op.value;
          }
          if (one) {
            return v;
          }
          a.push(v);
        }
      }
      return a;
    }
    return $(el).val();
  }

  // 公共区
  $.common = $.common || {};
  $.common.json = $.common.json || {};
  // 清空JSON对象的属性
  $.common.json.clearJson = function (json, emptyToNull, ignore) {
    for (var p in json) {
      var v = json[p];
      if (ignore && ignore.indexOf && ignore.indexOf(p) > -1) {
        continue;
      }
      if (typeof p == 'object') {
        $.common.json.clearJson(json[p], emptyToNull);
      } else if (v && v.constructor == Array) {
        json[p] = [];
      } else {
        if (emptyToNull && emptyToNull === true) {
          json[p] = null;
        } else {
          json[p] = '';
        }
      }
    }
  };
  $.common.html = $.common.html || {};
  // 创建DIV元素
  $.common.html.createDiv = function (selector) {
    var id = selector;
    if (selector.indexOf('#') == 0) {
      id = selector.substring(1);
    }
    // 放置
    if ($(selector).length == 0) {
      $('body').append("<div id='" + id + "' />");
    }
  };

  $.objectLength = function (obj) {
    var count = 0;
    for (var i in obj) {
      count++;
    }
    return count;
  };
})(jQuery);

// 解析url 返回json数组
function readSearch() {
  var search = window.location.search;
  var s = new Object();
  var searchArray = search.replace('?', '').split('&');
  for (var i = 0; i < searchArray.length; i++) {
    var paraArray = searchArray[i].split('=');
    var key = paraArray[0];
    var value = paraArray[1];
    s[key] = value;
  }
  return s;
}

function refreshPage() {
  location.href = location.href;
}

var OrgUtils = {};
OrgUtils.getUserBeanById = function (userId) {
  var storage = (window.opener && window.opener.sessionStorage) || window.sessionStorage;
  if (storage != null) {
    var val = storage.getItem('user_id_' + userId);
    try {
      if (val != null) {
        return JSON.parse(val);
      }
    } catch (e) {
    }
  }
  var data = {};
  $.ajax({
    url: ctx + '/api/org/user/getUserById',
    type: 'get',
    data: {
      userId: userId
    },
    async: false,
    success: function (result) {
      data = result.data;
      if (storage != null) {
        storage.setItem('user_id_' + userId, JSON.stringify(data));
      }
    }
  });
  return data;
};

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
    var val = storage.getItem("sys_param_" + key);
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
          storage.setItem("sys_param_" + key, success.data)
        }
      }
    }
  });
  var dv = defaultValue == null ? '' : defaultValue;
  return SystemParams.data[key] == null ? dv : SystemParams.data[key];
};
var SelectiveDatas = {};
SelectiveDatas.data = {};
SelectiveDatas.init = function (configKeys) {
  $.ajax({
    type: 'POST',
    url: '/basicdata/selective/data/init',
    data: JSON.stringify(configKeys),
    contentType: 'application/json',
    dataType: 'json',
    async: false,
    success: function (success, statusText, jqXHR) {
      $.each(success, function () {
        SelectiveDatas.data[this.configKey] = this;
      });
    }
  });
};
SelectiveDatas.get = function (configKey) {
  if (SelectiveDatas.data[configKey] != null) {
    return SelectiveDatas.data[configKey];
  }
  $.ajax({
    type: 'GET',
    url: '/basicdata/selective/data/get?configKey=' + configKey,
    contentType: 'application/json',
    dataType: 'json',
    async: false,
    success: function (success, statusText, jqXHR) {
      SelectiveDatas.data[configKey] = success;
    }
  });
  return SelectiveDatas.data[configKey] == null ? {} : SelectiveDatas.data[configKey];
};
SelectiveDatas.getItems = function (configKey) {
  var data = SelectiveDatas.get(configKey);
  if (data == null || data['items'] == null) {
    return {};
  }
  return data['items'];
};

var SystemUnit = {
  data: [],
  PT_ID: 'S0000000000'
};
SystemUnit.init = function () {
  if (SystemUnit.data.length == 0) {
    $.ajax({
      url: ctx + '/api/org/multi/queryAllSystemUnitList',
      type: 'get',
      data: {},
      async: false,
      success: function (result) {
        SystemUnit.data = result.data;
        var pt = {
          id: SystemUnit.PT_ID,
          name: '基础平台',
          code: '001'
        };
        SystemUnit.data.push(pt);
      }
    });
  }
};
SystemUnit.getNameById = function (id) {
  if (SystemUnit.data.length > 0) {
    for (var i = 0; i < SystemUnit.data.length; i++) {
      if (SystemUnit.data[i].id == id) {
        return SystemUnit.data[i].name;
      }
    }
  } else {
    SystemUnit.init();
    return SystemUnit.getNameById(id);
  }
};
SystemUnit.getUnitById = function (id) {
  if (SystemUnit.data.length > 0) {
    for (var i = 0; i < SystemUnit.data.length; i++) {
      if (SystemUnit.data[i].id == id) {
        return SystemUnit.data[i];
      }
    }
  } else {
    SystemUnit.init();
    return SystemUnit.getUnitById(id);
  }
};

/** ******************************** 验证CAKey开始 ***************************** */
function checkCAKey() {
  var caStatus = false; // 判断客户端是否插key
  if (caStatus) {
    // 这里检测是否安装客户端版本
    var b = false;
    try {
      b = fjcaWs.OpenFJCAUSBKey();
      if (!b) {
        try {
          fjcaWs.CloseUSBKey();
        } catch (e) {
        }
        return false;
      }
    } catch (e) {
      oAlert('打开证书失败');
      try {
        fjcaWs.CloseUSBKey();
      } catch (e) {
      }
      return false;
    }

    // 读取证书
    if (!fjcaWs.ReadCertFromKey()) {
      oAlert('读取证书失败');
      try {
        fjcaWs.CloseUSBKey();
      } catch (e) {
      }
      return false;
    }
    var testCert = fjcaWs.GetCertData();
    var flag = false;
    JDS.call({
      service: 'FJCAAppsService.checkCurrentCertificate',
      data: [testCert],
      async: false,
      success: function (result) {
        flag = true;
      },
      error: function (jqXHR) {
        var faultData = JSON.parse(jqXHR.responseText);
        oAlert(faultData.msg);
        flag = false;
      }
    });
    fjcaWs.CloseUSBKey();
    return flag;
  } else {
    return true;
  }
}

/** ******************************** 验证CAKey结束 ***************************** */

if (typeof window.console == 'undefined') {
  window.console = {};
}
if (!window.console.log) {
  window.console.log = function () {
  };
}

if (!window.console.error) {
  window.console.error = function () {
  };
}

if (!window.console.debug) {
  window.console.debug = function () {
  };
}

if (!window.console.warn) {
  window.console.warn = function () {
  };
}

function urldecode(str) {
  // discuss at: http://phpjs.org/functions/urldecode/
  // original by: Philip Peterson
  // improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
  // improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
  // improved by: Brett Zamir (http://brett-zamir.me)
  // improved by: Lars Fischer
  // improved by: Orlando
  // improved by: Brett Zamir (http://brett-zamir.me)
  // improved by: Brett Zamir (http://brett-zamir.me)
  // input by: AJ
  // input by: travc
  // input by: Brett Zamir (http://brett-zamir.me)
  // input by: Ratheous
  // input by: e-mike
  // input by: lovio
  // bugfixed by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
  // bugfixed by: Rob
  // reimplemented by: Brett Zamir (http://brett-zamir.me)
  // note: info on what encoding functions to use from:
  // http://xkr.us/articles/javascript/encode-compare/
  // note: Please be aware that this function expects to decode from UTF-8
  // encoded strings, as found on
  // note: pages served as UTF-8
  // example 1: urldecode('Kevin+van+Zonneveld%21');
  // returns 1: 'Kevin van Zonneveld!'
  // example 2: urldecode('http%3A%2F%2Fkevin.vanzonneveld.net%2F');
  // returns 2: 'http://kevin.vanzonneveld.net/'
  // example 3:
  // urldecode('http%3A%2F%2Fwww.google.nl%2Fsearch%3Fq%3Dphp.js%26ie%3Dutf-8%26oe%3Dutf-8%26aq%3Dt%26rls%3Dcom.ubuntu%3Aen-US%3Aunofficial%26client%3Dfirefox-a');
  // returns 3:
  // 'http://www.google.nl/search?q=php.js&ie=utf-8&oe=utf-8&aq=t&rls=com.ubuntu:en-US:unofficial&client=firefox-a'
  // example 4: urldecode('%E5%A5%BD%3_4');
  // returns 4: '\u597d%3_4'

  return decodeURIComponent(
    (str + '')
      .replace(/%(?![\da-f]{2})/gi, function () {
        // PHP tolerates poorly formed escape sequences
        return '%25';
      })
      .replace(/\+/g, '%20')
  );
}

function urlencode(str) {
  // discuss at: http://phpjs.org/functions/urlencode/
  // original by: Philip Peterson
  // improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
  // improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
  // improved by: Brett Zamir (http://brett-zamir.me)
  // improved by: Lars Fischer
  // input by: AJ
  // input by: travc
  // input by: Brett Zamir (http://brett-zamir.me)
  // input by: Ratheous
  // bugfixed by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
  // bugfixed by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
  // bugfixed by: Joris
  // reimplemented by: Brett Zamir (http://brett-zamir.me)
  // reimplemented by: Brett Zamir (http://brett-zamir.me)
  // note: This reflects PHP 5.3/6.0+ behavior
  // note: Please be aware that this function expects to encode into UTF-8
  // encoded strings, as found on
  // note: pages served as UTF-8
  // example 1: urlencode('Kevin van Zonneveld!');
  // returns 1: 'Kevin+van+Zonneveld%21'
  // example 2: urlencode('http://kevin.vanzonneveld.net/');
  // returns 2: 'http%3A%2F%2Fkevin.vanzonneveld.net%2F'
  // example 3:
  // urlencode('http://www.google.nl/search?q=php.js&ie=utf-8&oe=utf-8&aq=t&rls=com.ubuntu:en-US:unofficial&client=firefox-a');
  // returns 3:
  // 'http%3A%2F%2Fwww.google.nl%2Fsearch%3Fq%3Dphp.js%26ie%3Dutf-8%26oe%3Dutf-8%26aq%3Dt%26rls%3Dcom.ubuntu%3Aen-US%3Aunofficial%26client%3Dfirefox-a'

  str = (str + '').toString();

  // Tilde should be allowed unescaped in future versions of PHP (as reflected
  // below), but if you want to reflect current
  // PHP behavior, you would need to add ".replace(/~/g, '%7E');" to the
  // following.
  return encodeURIComponent(str)
    .replace(/!/g, '%21')
    .replace(/'/g, '%27')
    .replace(/\(/g, '%28')
    .replace(/\)/g, '%29')
    .replace(/\*/g, '%2A')
    .replace(/%20/g, '+');
}

// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
Date.prototype.format = function (fmt) {
  // author: meizz
  var o = {
    'M+': this.getMonth() + 1, // 月份
    'd+': this.getDate(), // 日
    'H+': this.getHours(), // 小时
    'm+': this.getMinutes(), // 分
    's+': this.getSeconds(), // 秒
    'q+': Math.floor((this.getMonth() + 3) / 3), // 季度
    S: this.getMilliseconds()
    // 毫秒
  };
  if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
  for (var k in o)
    if (new RegExp('(' + k + ')').test(fmt))
      fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
  return fmt;
};
// 后台ztree保存、删除后局部刷新树方法
// 参数说明：1、ztreeName：ztree的id
// 2、doType：做局部刷新的动作（保存传save，删除传delete）3、saveUuid：保存该数据生成的uuid
// 4、beanUuid：收集到的数据bean的uuid 5、showTitle：选中节点的名字
function refreshZtree(ztreeName, doType, saveUuid, beanUuid, showTitle) {
  // alert("saveUuid " + saveUuid + " beanUuid " + beanUuid + " showTitle " +
  // showTitle);
  var treeObj = $.fn.zTree.getZTreeObj(ztreeName);
  var nodes = treeObj.getSelectedNodes();
  if (doType == 'delete') {
    for (var i = 0, l = nodes.length; i < l; i++) {
      treeObj.removeNode(nodes[i]);
    }
  }
  if (doType == 'save') {
    if (beanUuid != saveUuid) {
      var newNode = [{
        name: showTitle,
        id: saveUuid
      }];
      if (nodes.length != 0) {
        // 在选择好的父节点下面新增节点
        newNode = treeObj.addNodes(nodes[0], newNode);
      } else {
        newNode = treeObj.addNodes(null, newNode);
      }
    } else {
      if (nodes.length > 0) {
        nodes[0].name = showTitle;
        treeObj.updateNode(nodes[0]);
        // treeObj.reAsyncChildNodes(nodes[0], "refresh");
      }
    }
  }
}

// ztree查询方法
function searchZtreeNode(count, ztreeName, lastName, functionObject) {
  if (lastName == null || lastName == '') {
    return 0;
  }
  var treeObj = $.fn.zTree.getZTreeObj(ztreeName);
  // 通过名称模糊搜索，也可通过Id查找
  var nodes = treeObj.getNodesByParamFuzzy('name', lastName, null);
  if (count == nodes.length) {
    count = 0;
  }
  for (i = 0; i < nodes.length; i++) {
    if (i == count) {
      treeObj.selectNode(nodes[i]);
      if (functionObject != '') {
        functionObject(nodes[i].id);
      }
    }
    // 实现自动打开
    treeObj.expandNode(nodes[i], true, false);
  }
  return count;
}

// 控制必须为数字
function onlyNumber(IdName) {
  var idnam = null;
  var re = /^\d+(?=\.{0,1}\d+$|$)/;
  if (IdName != '' && IdName != null) {
    if (!re.test(IdName)) {
      alert('只能输入数字！');
    } else {
      return IdName;
    }
  }
}

// 控制为必须为字母
function onlyLetter(IdName) {
  var re = /^[A-Za-z_]+$/;
  if (IdName != '' && IdName != null) {
    if (!re.test(IdName)) {
      alert('只能输入字母！');
    } else {
      return IdName;
    }
  }
}

// 控制长度需要在指定长度
function LengthControl(LengthNum, values) {
  if (values.length > LengthNum) {
    alert('长度大于指定长度!');
  } else {
    alert(values);
    return values;
  }
}

// 控制字段符合变量名规则
function onlyVariableName(IdName) {
  var re = /^[_a-zA-Z][_a-zA-Z0-9]+$/;
  if (IdName != '' && IdName != null) {
    if (!re.test(IdName)) {
      return false;
    } else {
      return true;
    }
  }
}

function isOnlyNumber(IdName) {
  var idnam = null;
  var re = /^\d+(?=\.{0,1}\d+$|$)/;
  if (IdName != '' && IdName != null) {
    if (!re.test(IdName)) {
      return false;
    } else {
      return true;
    }
  }
}

/**
 * @param idClassSelector
 *            保存枚举的objectClassName
 * @param nameClassSelector
 *            保存备注
 */
function initEnums(idClassSelector, nameClassSelector, clickEvent) {
  var relationSettingTwo = {
    async: {
      otherParam: {
        serviceName: 'enumManageService',
        methodName: 'getAllEnums'
      }
    },
    check: {
      enable: false
    },
    callback: {
      onClick: clickEvent
    }
  };

  $('#' + nameClassSelector).comboTree({
    labelField: nameClassSelector,
    valueField: idClassSelector,
    treeSetting: relationSettingTwo,
    width: 220,
    height: 220
  });
}

/**
 * 空校验
 */
function isEmpty(value) {
  value = $.trim(value);
  if (value == '' || value == null) return true;
  return false;
}

/**
 * 非空校验
 *
 * @param value
 * @returns {Boolean}
 */
function isNoEmpty(value) {
  value = $.trim(value);
  if (value != '' && value != null) return true;
  return false;
}

function enumClass(jsonData) {
  var class_ = {};
  for (var key in jsonData) {
    class_[key] = jsonData[key];
  }

  class_.values = function () {
    var elementArray = new Array();
    for (var key in class_) {
      if (key != 'values') {
        elementArray.push(class_[key]);
      }
    }
    return elementArray;
  };

  return class_;
}

var Enum_ = function (enumJson) {
  var element_class = enumClass(enumJson);
  for (var child_member in element_class) {
    if (child_member != 'values') {
      var child_element = element_class[child_member];
      var child_class = new enumClass(child_element);
      element_class[child_member] = child_class;
    }
  }
  return element_class;
};

function getEnum() {
  var userDetails = SpringSecurityUtils.getUserDetails();
  var currentUserID = userDetails && userDetails.userId;
  if (
    currentUserID &&
    currentUserID.length > 0 &&
    'anonymousUser' != currentUserID &&
    (typeof window.Enum == 'undefined' || $.isEmptyObject(window.Enum))
  ) {
    JDS.call({
      service: 'enumManageService.getEnums',
      data: [],
      async: false,
      success: function (result) {
        if (result.success === true) {
          window.Enum = Enum_(eval('(' + result.data + ')'));
        }
      },
      error: function (jqXHR) {
        var faultData = JSON.parse(jqXHR.responseText);
        oAlert(faultData.msg);
        flag = false;
      }
    });
  } else {
    // 用户还没有登录
    if (typeof window.Enum == 'undefined') {
      window.Enum = {};
    }
  }
}

$(function () {
  // getEnum();
  window.Enum = undefined;
  window.environment = getCookie('cookie.product.environment');
});

String.prototype.gblen = function () {
  var len = 0;
  for (var i = 0; i < this.length; i++) {
    if (this.charCodeAt(i) > 127 || this.charCodeAt(i) == 94) {
      len += 2;
    } else {
      len++;
    }
  }
  return len;
};

var browserUtils = (function () {
  var UserAgent = navigator.userAgent.toLowerCase();
  console.log(UserAgent);
  return {
    isUc: /ucweb/.test(UserAgent), // UC浏览器
    isChrome: /chrome/.test(UserAgent.substr(-33, 6)), // Chrome浏览器
    isChromium: /chrome/.test(UserAgent.substr(-32, 7)), // Chromium浏览器
    isFirefox: /firefox/.test(UserAgent), // 火狐浏览器
    isOpera: /opera/.test(UserAgent), // Opera浏览器
    isSafire: /safari/.test(UserAgent) && !/chrome/.test(UserAgent), // safire浏览器
    is360: /360se/.test(UserAgent), // 360浏览器
    isBaidu: /bidubrowser/.test(UserAgent), // 百度浏览器
    isSougou: /metasr/.test(UserAgent), // 搜狗浏览器
    isIE6: /msie 6.0/.test(UserAgent), // IE6
    isIE7: /msie 7.0/.test(UserAgent), // IE7
    isIE8: /msie 8.0/.test(UserAgent), // IE8
    isIE9: /msie 9.0/.test(UserAgent), // IE9
    isIE10: /msie 10.0/.test(UserAgent), // IE10
    isIE11: /msie 11.0/.test(UserAgent), // IE11
    isLB: /lbbrowser/.test(UserAgent), // 猎豹浏览器
    isWX: /micromessenger/.test(UserAgent), // 微信内置浏览器
    isQQ: /qqbrowser/.test(UserAgent), // QQ浏览器
    isEdng: /windows nt 6.3/.test(UserAgent) // edng
  };
})();

/**
 * 获取URL地址中的传参， add by wujx 2015-09-17 使用方法 var Request = new Object(); Request =
 * GetRequestParam(); var 参数1,参数2,参数3,参数N; 参数1 = Request['参数1']; 参数2 =
 * Request['参数2']; 参数3 = Request['参数3']; 参数N = Request['参数N'];
 *
 * @returns {Object}
 *
 */
function GetRequestParam() {
  var url = location.search; // 获取url中"?"符后的字串
  var theRequest = new Object();
  if (url.indexOf('?') != -1) {
    var str = url.substr(1);
    strs = str.split('&');
    for (var i = 0; i < strs.length; i++) {
      theRequest[strs[i].split('=')[0]] = unescape(strs[i].split('=')[1]);
    }
  }
  return theRequest;
}

/**
 * 以POST表单方式打开新窗口
 *
 * @param:url 需要打开的URL
 * @param:args URL的参数，数据类型为object
 * @param:name 打开URL窗口的名字，如果同一按钮需要重复地打开新窗口，而不是在第一次打开的窗口做刷新，此参数应每次不同
 */
function openWindowByPost(url, args, name) {
  // 创建表单对象
  var _form = $('<form></form>', {
    id: 'tempForm',
    method: 'post',
    action: url,
    target: name,
    style: 'display:none'
  }).appendTo($('body'));

  // 将隐藏域加入表单
  if (args) {
    for (var i in args) {
      _form.append(
        $('<input>', {
          type: 'hidden',
          name: i,
          value: args[i]
        })
      );
    }
  }
  _form.append(
    $('<input>', {
      type: 'hidden',
      name: '_csrf',
      value: getCookie('_csrfToken')
    })
  );

  // 绑定提交触发事件
  /*_form.bind('submit',function(){
      window.open("about:blank",name);
  });*/

  // 触发提交事件
  _form.trigger('submit');
  // 表单删除
  _form.remove();
}

String.prototype.equalsIgnoreCase = function (arg) {
  return new String(this.toLowerCase()) == new String(arg).toLowerCase();
};

function jsonDataServices(data, method) {
  var o = '';
  $.ajax({
    data: data,
    type: 'POST',
    async: false,
    // dataType : "json",
    // contentType : "application/json",
    url: '/json/data/services/' + method,
    success: function (result, statusText, jqXHR) {
      o = result;
    }
  });
  return o;
}

/**
 * 要生成拼音的汉字
 * @param {String} chString
 */
function pinYin(chString) {
  return jsonDataServices({
    chString: chString
  },
    'pinYin'
  );
}

/**
 * 要生成拼音的汉字
 * @param {String} chString
 */

function pinYinHeadChar(chString) {
  return jsonDataServices({
    chString: chString
  },
    'pinYinHeadChar'
  );
}

var zTreeUtils = zTreeUtils || {};
var isContain = (zTreeUtils.isContain = function (names, name) {
  for (var i = 0; i < names.length; i++) {
    if (name != null && name.indexOf(names[i]) >= 0) {
      return true;
    }
  }
  return false;
});

var ergodicTree = (zTreeUtils.ergodicTree = function (ztree, node, names) {
  if (node.isParent) {
    var nodeschild = node.children;
    var isShow = false; // 是否隐藏node的父节点
    for (var i = 0; i < nodeschild.length; i++) {
      isShow = ergodicTree(ztree, nodeschild[i], names) || isShow;
    }
    isShow = isShow || isContain(names, node.name);

    if (!isShow) {
      ztree.hideNode(node);
    } else {
      ztree.showNode(node);
    }
    return isShow;
  } else {
    if (!isContain(names, node.name)) {
      ztree.hideNode(node);
      return false;
    } else {
      ztree.showNode(node);
      return true;
    }
  }
});

if (typeof String.prototype.startsWith != 'function') {
  String.prototype.startsWith = function (prefix) {
    return this.slice(0, prefix.length) === prefix;
  };
}

if (typeof String.prototype.endsWith != 'function') {
  String.prototype.endsWith = function (suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
  };
}
//全局设置csrf-token
$ &&
  $.ajaxSetup({
    beforeSend: function (xhr, settings) {
      if (!/^(GET|HEAD|OPTIONS|TRACE)$/.test(settings.type) && !this.crossDomain) {
        xhr.setRequestHeader('x-csrf-token', getCookie('_csrfToken'));
      }
      var _auth = getCookie('_auth');
      // 设置前端访问后端服务接口的token验证
      xhr.setRequestHeader('Authorization' + ('jwt' === _auth ? '-JWT' : ''), 'Bearer ' + getCookie(_auth));
      // 系统ID
      var systemId = null;
      if (location.pathname && location.pathname.startsWith("/sys/")) {
        let parts = location.pathname.split('/');
        systemId = parts[2];
      }
      if (systemId) {
        sessionStorage.setItem("system_id", systemId);
      } else {
        systemId = sessionStorage.getItem("system_id");
      }
      if (systemId) {
        xhr.setRequestHeader('system_id', systemId);
      }
    }
  });

//判断2个对象是否完全相等
function deepCompare(x, y) {
  var i, l, leftChain, rightChain;

  function compare2Objects(x, y) {
    var p;

    // remember that NaN === NaN returns false
    // and isNaN(undefined) returns true
    if (isNaN(x) && isNaN(y) && typeof x === 'number' && typeof y === 'number') {
      return true;
    }

    // Compare primitives and functions.
    // Check if both arguments link to the same object.
    // Especially useful on the step where we compare prototypes
    if (x === y) {
      return true;
    }

    // Works in case when functions are created in constructor.
    // Comparing dates is a common scenario. Another built-ins?
    // We can even handle functions passed across iframes
    if (
      (typeof x === 'function' && typeof y === 'function') ||
      (x instanceof Date && y instanceof Date) ||
      (x instanceof RegExp && y instanceof RegExp) ||
      (x instanceof String && y instanceof String) ||
      (x instanceof Number && y instanceof Number)
    ) {
      return x.toString() === y.toString();
    }

    // At last checking prototypes as good as we can
    if (!(x instanceof Object && y instanceof Object)) {
      return false;
    }

    if (x.isPrototypeOf(y) || y.isPrototypeOf(x)) {
      return false;
    }

    if (x.constructor !== y.constructor) {
      return false;
    }

    if (x.prototype !== y.prototype) {
      return false;
    }

    // Check for infinitive linking loops
    if (leftChain.indexOf(x) > -1 || rightChain.indexOf(y) > -1) {
      return false;
    }

    // Quick checking of one object being a subset of another.
    // todo: cache the structure of arguments[0] for performance
    for (p in y) {
      if (y.hasOwnProperty(p) !== x.hasOwnProperty(p)) {
        return false;
      } else if (typeof y[p] !== typeof x[p]) {
        return false;
      }
    }

    for (p in x) {
      if (y.hasOwnProperty(p) !== x.hasOwnProperty(p)) {
        return false;
      } else if (typeof y[p] !== typeof x[p]) {
        return false;
      }

      switch (typeof x[p]) {
        case 'object':
        case 'function':
          leftChain.push(x);
          rightChain.push(y);

          if (!compare2Objects(x[p], y[p])) {
            return false;
          }

          leftChain.pop();
          rightChain.pop();
          break;

        default:
          if (x[p] !== y[p]) {
            return false;
          }
          break;
      }
    }

    return true;
  }

  if (arguments.length < 1) {
    return true; //Die silently? Don't know how to handle such case, please help...
    // throw "Need two or more arguments to compare";
  }

  for (i = 1, l = arguments.length; i < l; i++) {
    leftChain = []; //Todo: this can be cached
    rightChain = [];

    if (!compare2Objects(arguments[0], arguments[i])) {
      return false;
    }
  }

  return true;
}

//用于给其他项目添加全局额外的js
addProjectScript();

function addProjectScript() {
  var urlStr = localStorage.getItem("import-other-project-globalJs") || "[]";
  var url = JSON.parse(urlStr);
  for (var i = 0; i < url.length; i++) {
    var script = document.createElement('script');
    script.setAttribute('type', 'text/javascript');
    script.setAttribute('src', url[i]);
    document.getElementsByTagName('head')[0].appendChild(script);
  }
}

// 处理 ckeditor 富文本粘贴事件
function handleCkeditorPaste(evt) {
  // evt.data.dataValue 粘贴的内容（字符串）
  // evt.editor.document 富文本内容
  evt.data.dataValue = evt.data.dataValue.replace(/<img[\s\S]*?(?:>|\/>)/gi, ''); // 去掉图片
  evt.data.dataValue = evt.data.dataValue.replace(/<a[\s\S]*name="_Toc.*?(?:<\/a>)/gi, ''); // 去掉数字序号中产生的“小红旗”
}
