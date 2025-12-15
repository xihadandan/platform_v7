(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'constant'], factory);
  } else {
    // Browser globals
    factory(jQuery, Constant, true);
  }
})(function ($, Constant, isGlobal) {
  // 1、

  // 2、字符串
  var StringUtils = {};
  StringUtils.EMPTY = '';
  StringUtils.UNDEFINED = 'undefined';
  // 2.1 判断是否为空
  StringUtils.isBlank = function (string) {
    // 0 == "" : true
    return typeof string == StringUtils.UNDEFINED || string == null || StringUtils.trim(string) === StringUtils.EMPTY;
  };
  // 2.2 判断是否不为空
  StringUtils.isNotBlank = function (string) {
    return !StringUtils.isBlank(string);
  };
  // 2.3 如果为空，则默认某个值
  StringUtils.defaultIfBlank = function (string, defaultString) {
    return StringUtils.isBlank(string) ? defaultString : string;
  };
  // 2.4 判断是否包含指定字符串
  StringUtils.contains = function (string, searchValue, fromIndex) {
    return StringUtils.isBlank(string) ? string === searchValue : string.indexOf(searchValue, fromIndex) != -1;
  };
  // 2.5 首字母大写，如果string为空，直接返回
  StringUtils.capitalise = function (string) {
    return StringUtils.isBlank(string) ? string : string[0].toUpperCase() + string.slice(1);
  };
  // 2.6 首字母小写，如果string为空，直接返回
  StringUtils.uncapitalise = function (string) {
    return StringUtils.isBlank(string) ? string : string[0].toLowerCase() + string.slice(1);
  };
  // 2.7 删除左右两端的空格
  StringUtils.trim = function (string) {
    if (typeof string === 'string') {
      return string.replace(/(^\s*)|(\s*$)/g, '');
    }
    return string;
  };
  // 2.8 删除左边的空格
  StringUtils.ltrim = function (string) {
    return string.replace(/(^\s*)/g, '');
  };
  // 2.9 删除右边的空格
  StringUtils.rtrim = function (str) {
    return string.replace(/(\s*$)/g, '');
  };
  // 2.10 格式化字符串
  /**
   * 字符串格式化，支持深度获取。
   *
   * ```js
   *  StringUtils.format('这是 ${a.b.c} 的 ${x.y}。', { a: { b: { c: 'ssss' } }, x: { y: 'zzzz' } });
   * // => '这是 ssss 的 zzzz。'
   *
   * StringUtils.format('<a href="${href}" target="${target}"></a>', { href: 'www.baidu.com', target: '_target' });
   * // => '<a href="www.baidu.com" target="_target"></a>'
   * ```
   * @param {string} text 模板字符串
   * @param {object} obj 数据对象
   * @returns {string}
   */
  StringUtils.format = function (text, obj) {
    if (typeof text !== 'string' || typeof obj !== 'object') {
      return text;
    }

    return text.replace(/\${([^}]+)}/g, function (match, key) {
      return ObjectUtils.deepGet(obj, key.split('.'), '');
    });
  };

  // 是否手机格式
  StringUtils.isMobile = function (str) {
    var reg = /^1[3|4|5|6|7|8][0-9]{9}$/; // 验证规则
    return reg.test(str);
  };
  // 是否邮箱格式
  StringUtils.isEmail = function (str) {
    var reg = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
    return reg.test(str);
  };
  // 普通字符转换成转意符
  var arrEntities1 = {
    '<': '&lt;',
    '>': '&gt;',
    '&': '&amp;',
    '"': '&quot;',
    "'": '&apos;'
  };
  StringUtils.html2Escape = function (sHtml) {
    return sHtml.replace(/[<>&"']/g, function (c) {
      return arrEntities1[c];
    });
  };
  // 转意符换成普通字符
  var arrEntities2 = {
    lt: '<',
    gt: '>',
    nbsp: ' ',
    amp: '&',
    quot: '"',
    apos: "'"
  };
  StringUtils.escape2Html = function (str) {
    return str.replace(/&(lt|gt|nbsp|amp|quot|apos);/gi, function (all, t) {
      return arrEntities2[t];
    });
  };

  // 3、文件名
  var FilenameUtils = {};
  // 3.1、获取文件名，不带后缀
  FilenameUtils.getBaseName = function (fileName) {
    var baseName = fileName;
    var end = baseName.lastIndexOf(constant.Separator.Dot);
    if (end !== -1) {
      baseName = baseName.substring(0, end);
    }
    return baseName;
  };

  // 4、数字
  var NumberUtils = {};
  // 4.1、是否是整数
  NumberUtils.isInteger = function (object) {
    return Math.floor(object) === object;
  };

  // 5、随机数
  var RandomUtils = {};
  // 6、单词
  var WordUtils = {};
  // 7、数组
  var ArrayUtils = {};
  // 7.1、从数组中删除一个元素，并返回新的数组
  ArrayUtils.removeElement = function (array, element, equalCallback) {
    var tmpArray = [];
    $.each(array, function () {
      if (this == element || ($.isFunction(equalCallback) && equalCallback.call(ArrayUtils, this, element))) {
        return;
      }
      tmpArray.push(this);
    });
    return tmpArray;
  };

  // 8、布尔值
  var BooleanUtils = {};
  // 9、本地化
  var LocaleUtils = {};

  // 10、对象
  var ObjectUtils = {};
  // 10.1、null值转为空串
  ObjectUtils.nullToEmpty = function (object) {
    return object == null ? StringUtils.EMPTY : object;
  };
  // 10.2、判断是否为函数
  ObjectUtils.isFunction = function (func) {
    return $.isFunction(func);
  };
  // 10.3、判断是否为数组
  ObjectUtils.isArray = function (array) {
    return $.isArray(array);
  };
  // 10.4、获得obj对象值
  ObjectUtils.expValue = function (obj, expr) {
    var ret,
      p,
      prm = [],
      i;
    if (typeof expr === 'function') {
      return expr(obj);
    }
    ret = obj[expr];
    if (ret === undefined) {
      try {
        if (typeof expr === 'string') {
          prm = expr.split('.');
        }
        i = prm.length;
        if (i) {
          ret = obj;
          while (ret && i--) {
            p = prm.shift();
            ret = ret[p];
          }
        }
      } catch (e) {}
    }
    return ret;
  };
  // 10.2 按路径获取对象值

  /**
   * 按路径获取对象值，支持深度获取
   * 类似 `lodash.get()`，根据 `path` 获取对象值
   *
   * ``` js
   *  ObjectUtils.deepGet({ a: { b: { c: 'text' } } }, 'a.b.c');
   *  // => 'text'
   *
   *  ObjectUtils.deepGet({ a: { b: { c: 'text' } } }, 'x.y.z', 'default');
   *  // => 'default'
   * ```
   *
   * @param {object} obj 数据对象
   * @param {string | string[] | null | undefined} path 获取路径
   * @param {any=} defaultValue: 查询不到时的默认值
   * @returns {any}
   */
  ObjectUtils.deepGet = function (obj, path, defaultValue) {
    if (!obj || path == null || path.length === 0) {
      return defaultValue;
    }

    // 将 `path` 转为数组
    if (!$.isArray(path)) {
      path = ~path.indexOf('.') ? path.split('.') : [path];
    }

    if (path.length === 1) {
      var checkObj = obj[path[0]];
      return typeof checkObj === 'undefined' ? defaultValue : checkObj;
    }

    // 深度获取对象值
    var result = path.reduce(function (obj, key) {
      return (obj || {})[key];
    }, obj);

    return typeof result === 'undefined' ? defaultValue : result;
  };

  // 11、消息摘要
  var DigestUtils = {};

  // 12、日期
  var DateUtils = {};
  // 12.1、获取某年某月的天数
  DateUtils.getDaysInOneMonth = function (year, month) {
    return new Date(year, month, 0).getDate();
  };
  // 12.2、获取相差秒数
  DateUtils.escapeSecond = function (start, end) {
    return (end - start) / 1000.0 + 's';
  };
  // 12.3、日期格式化原型
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
    if (/(y+)/.test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
      if (new RegExp('(' + k + ')').test(fmt)) {
        fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
      }
    }
    return fmt;
  };
  // 12.4、日期格式化工具类，判空
  DateUtils.format = function (date, fmt) {
    return date && Date.prototype.format.call(date, fmt);
  };
  DateUtils.parseDate = function (str) {
    if (typeof str == 'string') {
      var results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) *$/);
      if (results && results.length > 3) return new Date(parseInt(results[1], 10), parseInt(results[2], 10) - 1, parseInt(results[3], 10));
      results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}) *$/);
      if (results && results.length > 5)
        return new Date(
          parseInt(results[1], 10),
          parseInt(results[2], 10) - 1,
          parseInt(results[3], 10),
          parseInt(results[4], 10),
          parseInt(results[5], 10)
        );
      results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}) *$/);
      if (results && results.length > 6)
        return new Date(
          parseInt(results[1], 10),
          parseInt(results[2], 10) - 1,
          parseInt(results[3], 10),
          parseInt(results[4], 10),
          parseInt(results[5], 10),
          parseInt(results[6], 10)
        );
      results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2})[\.| ](\d{1,9}) *$/);
      if (results && results.length > 7)
        return new Date(
          parseInt(results[1], 10),
          parseInt(results[2], 10) - 1,
          parseInt(results[3], 10),
          parseInt(results[4], 10),
          parseInt(results[5], 10),
          parseInt(results[6], 10),
          parseInt(results[7], 10)
        );
    }
    return null;
  };
  /**
   * 传递日期和天数，返回指定日期多少天前后的日期
   */
  DateUtils.getDateByDateDaysParam = function (date, days, param) {
    var newDate = new Date(date);
    if (param == 'before') {
      newDate.setDate(newDate.getDate() - days);
    } else if (param == 'after') {
      newDate.setDate(newDate.getDate() + days);
    }
    return newDate;
  };
  /**
   * 传递日期和周数，返回指定日期多少周前后的日期
   */
  DateUtils.getDateByDateWeeksParam = function (date, weeks, param) {
    var newDate = new Date(date);
    if (param == 'before') {
      newDate.setDate(newDate.getDate() - weeks * 7);
    } else if (param == 'after') {
      newDate.setDate(newDate.getDate() + weeks * 7);
    }
    return newDate;
  };
  /**
   * 传递日期和天数，返回指定日期多少个月前后的日期
   */
  DateUtils.getDateByDateMonthsParam = function (date, months, param) {
    var newDate = new Date(date);
    if (param == 'before') {
      newDate.setMonth(newDate.getMonth() - months);
    } else if (param == 'after') {
      newDate.setMonth(newDate.getMonth() + months);
    }
    return newDate;
  };
  /**
   * 传递日期和年数，返回指定日期多少年前后的日期
   */
  DateUtils.getDateByDateYearsParam = function (date, years, param) {
    var newDate = new Date(date);
    if (param == 'before') {
      newDate.setFullYear(newDate.getFullYear() - years);
    } else if (param == 'after') {
      newDate.setFullYear(newDate.getFullYear() + years);
    }
    return newDate;
  };
  /**
   * 传递日期和格式，返回指定格式日期
   */
  DateUtils.getDateStrByDateAndFormat = function (date, format) {
    var newDate = new Date(date);
    return newDate.format(format);
  };
  /**
   * 传递日期字符串和格式，返回指定日期
   */
  DateUtils.getSpecificDateByDateFormat = function (date, format) {
    var resultData = '';
    JDS.call({
      service: 'apiDateFacadeService.getSpecificDateByDateFormat',
      data: [date, format],
      async: false,
      success: function (result) {
        resultData = result.data;
      }
    });
    return new Date(resultData);
  };
  /**
   * 传递日期和工作日天数，返回指定日期多少个工作日前后的日期
   */
  DateUtils.getSpecificDateByDatewkhrParam = function (date, workHour, param) {
    var resultData = '';
    JDS.call({
      service: 'apiDateFacadeService.getSpecificDateByDatewkhrParam',
      data: [date, workHour, param],
      async: false,
      success: function (result) {
        resultData = result.data;
      }
    });
    return resultData;
  };
  // 13、格式化消息参数工具类
  var MsgUtils = {};

  // 14、UUID
  function UUID() {}

  // 14.1生成UUID
  UUID.prototype.createUUID = function () {
    var dg = new Date(1582, 10, 15, 0, 0, 0, 0);
    var dc = new Date();
    var t = dc.getTime() - dg.getTime();
    var tl = this.getIntegerBits(t, 0, 31);
    var tm = this.getIntegerBits(t, 32, 47);
    var thv = this.getIntegerBits(t, 48, 59) + '1';
    var csar = this.getIntegerBits(this.rand(4095), 0, 7);
    var csl = this.getIntegerBits(this.rand(4095), 0, 7);
    var n =
      this.getIntegerBits(this.rand(8191), 0, 7) +
      this.getIntegerBits(this.rand(8191), 8, 15) +
      this.getIntegerBits(this.rand(8191), 0, 7) +
      this.getIntegerBits(this.rand(8191), 8, 15) +
      this.getIntegerBits(this.rand(8191), 0, 15);
    return tl + tm + thv + csar + csl + n;
  };
  // 14.2
  // Pull out only certain bits from a very large integer, used to get the
  // time
  // code information for the first part of a UUID. Will return zero's if
  // there
  // aren't enough bits to shift where it needs to.
  UUID.prototype.getIntegerBits = function (val, start, end) {
    var base16 = this.returnBase(val, 16);
    var quadArray = new Array();
    var quadString = '';
    var i = 0;
    for (i = 0; i < base16.length; i++) {
      quadArray.push(base16.substring(i, i + 1));
    }
    for (i = Math.floor(start / 4); i <= Math.floor(end / 4); i++) {
      if (!quadArray[i] || quadArray[i] == '') quadString += '0';
      else quadString += quadArray[i];
    }
    return quadString;
  };
  // 14.3
  // Replaced from the original function to leverage the built in methods in
  // JavaScript. Thanks to Robert Kieffer for pointing this one out
  UUID.prototype.returnBase = function (number, base) {
    return number.toString(base).toUpperCase();
  };
  // 14.4
  // 生成一个大于等于0小于等于max的随机数
  UUID.prototype.rand = function (max) {
    return Math.floor(Math.random() * (max + 1));
  };

  // 15、控制台输出
  var Console = {};
  Console.log = $.noop;
  Console.trace = $.noop;
  Console.debug = $.noop;
  Console.info = $.noop;
  Console.warn = $.noop;
  Console.error = $.noop;

  // 16、StringBuilder
  // 16.1 对象定义（构造函数)
  // 参数：所有参数须为字符串形式.
  // 当参数长度为1时，参数值将是字符串之间连接的分隔符
  // 当参数长度大于1时，最后一位将是字符串之间的分隔符,其余的参数将是字符串值
  // 当不指定参数时，分隔符默认为空白
  // 也可以不指定分隔符，而在toString中显式指定分隔符
  // 如：var str = new StringBuilder(',')； 则在toString时，将使用,号作为分隔符连接字符串
  // var str = new StringBuilder('a','b','c',','); 则在toString时，将输出 'a,b,c'
  var StringBuilder = function () {
    this._buffers = [];
    this._length = 0;
    this._splitChar = arguments.length > 0 ? arguments[arguments.length - 1] : '';
    this.nullStr = '';

    if (arguments.length > 0) {
      for (var i = 0, iLen = arguments.length - 1; i < iLen; i++) {
        this.append(arguments[i]);
      }
    }
  };
  // 16.2向对象中添加字符串
  // 参数：一个字符串值
  StringBuilder.prototype.append = function (str) {
    var tmp = str;
    if (tmp == null) {
      tmp = this.nullStr;
    }
    this._length += tmp.length;
    this._buffers[this._buffers.length] = tmp;
  };
  // 16.3向对象附加格式化的字符串
  // 参数：参数一是预格式化的字符串，如：'{0} {1} {2}'
  // 格式参数可以是数组，或对应长度的arguments,
  // 参见示例
  StringBuilder.prototype.appendFormat = function () {
    if (arguments.length > 1) {
      var TString = arguments[0];
      if (arguments[1] instanceof Array) {
        for (var i = 0, iLen = arguments[1].length; i < iLen; i++) {
          var jIndex = i;
          var re = eval('/\\{' + jIndex + '\\}/g;');
          TString = TString.replace(re, arguments[1][i]);
        }
      } else {
        for (var i = 1, iLen = arguments.length; i < iLen; i++) {
          var jIndex = i - 1;
          var re = eval('/\\{' + jIndex + '\\}/g;');
          TString = TString.replace(re, arguments[i]);
        }
      }
      this.append(TString);
    } else if (arguments.length == 1) {
      this.append(arguments[0]);
    }
  };
  // 16.4字符串长度（相当于toString()后输出的字符串长度
  StringBuilder.prototype.length = function () {
    if (this._splitChar.length > 0 && !this.isEmpty()) {
      return this._length + this._splitChar.length * (this._buffers.length - 1);
    } else {
      return this._length;
    }
  };
  // 16.5字符串是否为空
  StringBuilder.prototype.isEmpty = function () {
    return this._buffers.length <= 0;
  };
  // 16.6 清空
  StringBuilder.prototype.clear = function () {
    this._buffers = [];
    this._length = 0;
  };
  // 16.17输出
  // 参数：可以指定一个字符串（或单个字符），作为字符串拼接的分隔符
  StringBuilder.prototype.toString = function () {
    if (arguments.length == 1) {
      return this._buffers.join(arguments[1]);
    } else {
      return this._buffers.join(this._splitChar);
    }
  };

  // 17、HashMap
  var HashMap = {};

  // 18、浏览器
  var Browser = {};
  // 18.1 获取浏览器访问地址的参数值
  Browser.getQueryString = function (name, defaultValue) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
    var values = window.location.search.substr(1).match(reg);
    if (values != null) {
      return decodeURIComponent(values[2]);
    }
    if (defaultValue != null) {
      return defaultValue;
    }
    return null;
  };
  // 18.2 判断是否IE8或更低版本
  Browser.isIE8OrLower = function () {
    if (navigator.appVersion && navigator.appName == 'Microsoft Internet Explorer') {
      var version = navigator.appVersion.split(';');
      if (version && version.length > 1) {
        var trimVersion = version[1].replace(/[ ]/g, '');
        if (trimVersion == 'MSIE8.0' || trimVersion == 'MSIE7.0' || trimVersion == 'MSIE6.0') {
          return true;
        }
      }
    }
    return false;
  };
  // 18.3 判断是否指定IE版本，参数version为版本号，如6、7、8、9
  Browser.isIEOfVersion = function (checkVersion) {
    if (navigator.appVersion && navigator.appName == 'Microsoft Internet Explorer') {
      var version = navigator.appVersion.split(';');
      if (version && version.length > 1) {
        var trimVersion = version[1].replace(/[ ]/g, '');
        if (trimVersion == 'MSIE' + checkVersion + '.0') {
          return true;
        }
      }
    }
    return false;
  };
  // 18.4 获取basePath
  Browser.getBasePath = function () {
    // 1、从WebApp中获取
    if (WebApp.requestInfo && StringUtils.isNotBlank(WebApp.requestInfo.basePath)) {
      return WebApp.requestInfo.basePath.indexOf('://') > -1
        ? WebApp.requestInfo.basePath
        : WebApp.requestInfo.scheme + '://' + WebApp.requestInfo.basePath;
    }
    // 2、从浏览器地址中获取
    // 获取当前网址，如： http://localhost:8080/ems/Pages/Basic/Person.jsp
    var requestPath = window.document.location.href;
    // 获取主机地址之后的目录，如： /ems/Pages/Basic/Person.jsp
    var pathName = window.document.location.pathname;
    var pos = requestPath.indexOf(pathName);
    // 获取主机地址，如： http://localhost:8080
    var serverPath = requestPath.substring(0, pos);
    // 获取带"/"的项目名，如：/ems
    var contextPath = ctx;
    // 获取项目的basePath http://localhost:8080/ems/
    var basePath = serverPath + contextPath;
    return basePath;
  };
  // 18.5 浏览器检测
  Browser.browser = (function (userAgent) {
    var isUc = /ucweb/.test(userAgent);
    var isChrome = /chrome/.test(userAgent.substr(-33, 6));
    var isFirefox = /firefox/.test(userAgent);
    var isOpera = /opera/.test(userAgent);
    var isSafire = /safari/.test(userAgent) && !/chrome/.test(userAgent);
    var is360 = /360se/.test(userAgent);
    var isBaidu = /bidubrowser/.test(userAgent);
    var isSougou = /metasr/.test(userAgent);
    var isIE = /msie /.test(userAgent);
    var isIE6 = /msie 6.0/.test(userAgent);
    var isIE7 = /msie 7.0/.test(userAgent);
    var isIE8 = /msie 8.0/.test(userAgent);
    var isIE9 = /msie 9.0/.test(userAgent);
    var isIE10 = /msie 10.0/.test(userAgent);
    var isIE11 = /msie 11.0/.test(userAgent);
    var isLB = /lbbrowser/.test(userAgent);
    var isWX = /micromessenger/.test(userAgent);
    var isQQ = /qqbrowser/.test(userAgent);
    return {
      isUc: isUc, // UC浏览器
      isChrome: isChrome, // Chrome浏览器
      isFirefox: isFirefox, // 火狐浏览器
      isOpera: isOpera, // Opera浏览器
      isSafire: isSafire, // safire浏览器
      is360: is360, // 360浏览器
      isBaidu: isBaidu, // 百度浏览器
      isSougou: isSougou, // 搜狗浏览器
      isIE: isIE, // IE
      isIE6: isIE6, // IE6
      isIE7: isIE7, // IE7
      isIE8: isIE8, // IE8
      isIE9: isIE9, // IE9
      isIE10: isIE10, // IE10
      isIE11: isIE11, // IE11
      isLB: isLB, // 猎豹浏览器
      isWX: isWX, // 微信内置浏览器
      isQQ: isQQ // QQ浏览器
    };
  })(navigator.userAgent.toLowerCase());
  // 18.6 浏览器系统
  Browser.os = (function (userAgent) {
    return {
      isIpad: /ipad/.test(userAgent),
      isIphone: /iphone os/.test(userAgent),
      isAndroid: /android/.test(userAgent),
      isWindows: /windows/.test(userAgent),
      isWindowsCe: /windows ce/.test(userAgent),
      isWindowsMobile: /windows mobile/.test(userAgent),
      isWin2K: /windows nt 5.0/.test(userAgent),
      isXP: /windows nt 5.1/.test(userAgent),
      isVista: /windows nt 6.0/.test(userAgent),
      isWin7: /windows nt 6.1/.test(userAgent),
      isWin8: /windows nt 6.2/.test(userAgent),
      isWin81: /windows nt 6.3/.test(userAgent),
      isWin81: /windows nt 10/.test(userAgent),
      isLinux: /linux/.test(userAgent),
      isMac: /mac os/.test(userAgent)
    };
  })(navigator.userAgent.toLowerCase());

  // 19、本地数据存储
  var _app_prefix = 'app_';
  // mode, 1 cookie, 2 sessionStorage, 3 localStorage
  // 19.1、应用本地数据存储
  var AppStorage = function (mode) {
    this._storage = null;
    switch (mode) {
      case 1:
        // 先使用sessionStorage
        this._storage = sessionStorage;
        break;
      case 2:
        this._storage = sessionStorage;
        break;
      case 3:
        this._storage = localStorage;
        break;
      default:
    }
    if (this._storage == null) {
      this._storage = sessionStorage;
    }
  };
  // 19.2、添加本地存储数据
  AppStorage.prototype.setItem = function (key, value) {
    return this._storage.setItem(_app_prefix + key, value);
  };
  // 19.3、通过key获取相应的Value
  AppStorage.prototype.getItem = function (key) {
    return this._storage.getItem(_app_prefix + key);
  };
  // 19.4、通过key删除本地数据
  AppStorage.prototype.removeItem = function (key) {
    return this._storage.removeItem(_app_prefix + key);
  };
  // 19.5、清空数据
  AppStorage.prototype.clear = function () {
    return this._storage.clear();
  };
  // 19.6、本地存储对象工具类
  var StorageUtils = function () {
    this._storages = {};
  };
  // 19.7、本地存储对象工具类对象接口
  StorageUtils.prototype.getStorage = function (mode) {
    var storyMode = mode;
    if (mode == null) {
      mode = 2;
    }
    if (this._storages[mode] == null) {
      this._storages[mode] = new AppStorage(mode);
    }
    return this._storages[mode];
  };

  // 20 、JS原型继承
  var inherit = function (Child, Parent, prototype) {
    var F = function () {};
    F.prototype = Parent.prototype;
    Child.prototype = new F();
    Child.prototype.constructor = Child;
    Child.superclass = Parent.prototype;

    // 对像代理继承
    return _proxiedPrototype(Child, prototype);
  };

  // JS对象代理继承
  function _proxiedPrototype(Child, prototype) {
    var superTest = (superApplyTest = /.*/);
    var patternMacher = /xyz/.test(function () {
      xyz;
    });
    if (patternMacher) {
      superTest = /\b_super\b/;
      superApplyTest = /\b_superApply\b/;
    }
    var proxiedPrototype = prototype || {};
    $.each(proxiedPrototype, function (prop, value) {
      if (!$.isFunction(value)) {
        proxiedPrototype[prop] = value;
        return;
      } else if (Child.superclass[prop] && $.isFunction(value) && (superTest.test(value) || superApplyTest.test(value))) {
        proxiedPrototype[prop] = (function () {
          var _super = function () {
            return Child.superclass[prop].apply(this, arguments);
          };
          var _superApply = function (args) {
            return Child.superclass[prop].apply(this, args);
          };
          return function () {
            var __super = this._super;
            var __superApply = this._superApply;
            var returnValue;

            this._super = _super;
            this._superApply = _superApply;

            returnValue = value.apply(this, arguments);

            this._super = __super;
            this._superApply = __superApply;

            return returnValue;
          };
        })();
      }
    });
    return $.extend(Child.prototype, proxiedPrototype);
  }

  // 21、JSON
  var JsonUtils = {};
  // 增强容错性,支持非标准的JSON,如：{1:''}
  JsonUtils.parse = function (json) {
    if (typeof json === 'string') {
      try {
        return $.parseJSON(json);
      } catch (e) {
        return new Function('return ' + json)();
      }
    }
    return json;
  };

  // 22、QueryItem
  var QueryItem = {};
  QueryItem.getKey = function (rawKey) {
    return QueryItem.dbNameToJavaName(rawKey, false);
  };
  QueryItem.dbNameToJavaName = function (dbName, firstCharUppered) {
    var name = dbName;
    if (name == null || !dbName.trim()) {
      return '';
    }
    var parts = [];
    if (name.indexOf('_') != -1) {
      parts = name.toLowerCase().split('_');
    } else {
      parts = name.split('_');
    }
    var sb = new StringBuilder();
    for (var i = 0; i < parts.length; i++) {
      var part = parts[i];
      if (part.length() == 0) {
        continue;
      }
      sb.append(part.substring(0, 1).toUpperCase());
      sb.append(part.substring(1));
    }
    if (firstCharUppered) {
      return sb.toString();
    } else {
      return sb.substring(0, 1).toLowerCase() + sb.substring(1);
    }
  };

  // 23、UrlUtils
  var UrlUtils = {};
  // 23.1、附加url参数
  UrlUtils.appendUrlParams = function (url, params) {
    if (params == null) {
      return url;
    }
    // URL参数处理
    var array = params;
    if ($.isArray(params) === false) {
      array = [];
      for (var p in params) {
        if (typeof params[p] === 'undefined') {
          console.log('url param[' + p + '] is undefined');
          continue;
        }
        array.push({
          name: p,
          value: params[p]
        });
      }
    }
    if (array.length == 0) {
      return url;
    }
    var sb = new StringBuilder();
    for (var i = 0; i < array.length; i++) {
      var param = array[i];
      sb.append(param.name + '=' + encodeURIComponent(param.value));
      if (i < array.length - 1) {
        sb.append('&');
      }
    }
    var hash = '';
    var newUrl = url;
    if (StringUtils.contains(newUrl, '#')) {
      var hashIndex = newUrl.lastIndexOf('#');
      hash = newUrl.substring(hashIndex);
      newUrl = newUrl.substring(0, hashIndex);
    }
    if (StringUtils.contains(newUrl, '?')) {
      newUrl += '&' + sb.toString();
    } else {
      newUrl += '?' + sb.toString();
    }
    return newUrl + hash;
  };

  var TreePath = {};
  // 获取离的最近的单位ID
  TreePath.getDeptId = function (path) {
    var p = path.split('/');
    for (var i = p.length - 1; i >= 0; i--) {
      if (p[i].substr(0, 1) == 'D') {
        return p[i];
      }
    }
    return null;
  };
  // 获取离的最近的职位ID
  TreePath.getJobId = function (path) {
    var p = path.split('/');
    for (var i = p.length - 2; i >= 0; i--) {
      if (p[i].substr(0, 1) == 'J') {
        return p[i];
      }
    }
    return null;
  };
  // 获取根节点ID
  TreePath.getRootNodeId = function (path) {
    var p = path.split('/');
    return p[0];
  };
  // 获取元素ID
  TreePath.getEleId = function (path) {
    var p = path.split('/');
    return p[p.length - 1];
  };

  // lists的工具类
  var Lists = {
    // 按指定字段，进行分组, 返回LIST
    toGroupList: function (list, field) {
      var groupNames = new Array();
      var objs = new Array();
      $.each(list, function (i, item) {
        var fieldValue = item[field];
        var pos = $.inArray(fieldValue, groupNames);
        if (pos == -1) {
          // 不存在
          groupNames.push(fieldValue);
          var obj = {
            groupName: fieldValue,
            data: [item]
          };
          objs.push(obj);
        } else {
          var obj = objs[pos];
          obj.data.push(item);
        }
      });
      return objs;
    },

    // 按指定字段，进行分组, 返回Map
    toGroupMap: function (list, field) {
      var objs = {};
      $.each(list, function (i, item) {
        var fieldValue = item[field];
        if (objs[fieldValue]) {
          objs[fieldValue].push(item);
        } else {
          objs[fieldValue] = [item];
        }
      });
      return objs;
    }
  };

  // 导出公共模块commons
  var commons = {};
  // 2、字符串
  commons.StringUtils = StringUtils;
  // 3、文件名
  commons.FilenameUtils = FilenameUtils;
  // 4、数字
  commons.NumberUtils = NumberUtils;
  // 7、数组
  commons.ArrayUtils = ArrayUtils;
  // 10、对象
  commons.ObjectUtils = ObjectUtils;
  // 12、日期
  commons.DateUtils = DateUtils;
  // 14、UUID生成工具类
  // 15、控制台输出
  commons.Console = Console;
  commons.UUID = new UUID();
  // 16、StringBuilder
  commons.StringBuilder = StringBuilder;
  // 18、浏览器
  commons.Browser = Browser;
  // 19、本地存储对象工具类对象
  commons.StorageUtils = new StorageUtils();
  // 20、继承
  commons.inherit = inherit;
  // 21、JSON
  commons.JSON = JsonUtils;
  // 23、UrlUtils
  commons.UrlUtils = UrlUtils;
  // treePath
  commons.treePath = TreePath;
  // lists
  commons.Lists = Lists;

  if (isGlobal === true) {
    window.commons = commons;
  }

  return commons;
});
