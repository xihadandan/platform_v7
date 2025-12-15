(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'constant', 'commons'], factory);
  } else {
    // Browser globals
    factory(jQuery, Constant, commons, true);
  }
})(function ($, constant, commons, isGlobal) {
  var Separator = constant.Separator;
  var UrlUtils = commons.UrlUtils;
  var StringUtils = commons.StringUtils;
  if (StringUtils.isBlank(window.ctx) && window.WebApp && WebApp.requestInfo) {
    window.ctx = WebApp.requestInfo.contextPath;
  }

  // 服务器交互相关对象
  var server = {};
  // 1、安全工具类
  var SecurityUtils = {};
  // 1.1、获取当前用户的UserDetails
  SecurityUtils.getUserDetails = function (personId) {
    var date = new Date();
    var t = date.getTime();
    var userDetails = {};
    if (personId) {
      $.ajax({
        url: ctx + '/api/org/user/getUserById',
        type: 'get',
        data: {
          userId: personId
        },
        async: false,
        success: function (result) {
          userDetails = result.data;
          // 保持orgUserVo 跟 DefaultUserDetails 格式尽量一样
          userDetails.userId = userDetails.id;
        }
      });
    } else {
      var wd = window.opener || window;
      if (wd && wd.userDetails) {
        return wd.userDetails;
      }
      $.ajax({
        type: 'GET',
        url: ctx + '/security/user/details?t=' + t,
        contentType: 'application/json',
        dataType: 'json',
        async: false,
        success: function (success, statusText, jqXHR) {
          userDetails = success;
        }
      });
      if (wd) {
        wd.userDetails = userDetails;
      }
    }
    return userDetails;
  };
  // 1.2、判断当前用户是否拥有指定的角色
  SecurityUtils.hasRole = function (roleId) {
    var userDetails = SecurityUtils.getUserDetails();
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
  SecurityUtils.getCurrentUserId = function () {
    return SecurityUtils.getUserDetails()['userId'];
  };
  SecurityUtils.getCurrentUserUnitId = function () {
    return SecurityUtils.getUserDetails()['systemUnitId'];
  };

  // 2、系统配置参数
  var SystemParams = {};
  SystemParams.data = {};
  // 2.1 根据key，获取值
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
      url: ctx + '/basicdata/system/param/get?key=' + key + '&timestamp=' + getTimestamp,
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

  // 3、选择性数据
  var SelectiveDatas = {};
  SelectiveDatas.data = {};
  // 3.1 初始化到本地
  SelectiveDatas.init = function (configKeys) {
    $.ajax({
      type: 'POST',
      url: ctx + '/basicdata/selective/data/init',
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
  // 3.2 根据configKey对应的选择性数据
  SelectiveDatas.get = function (configKey) {
    if (SelectiveDatas.data[configKey] != null) {
      return SelectiveDatas.data[configKey];
    }
    $.ajax({
      type: 'GET',
      url: ctx + '/basicdata/selective/data/get?configKey=' + configKey,
      contentType: 'application/json',
      dataType: 'json',
      async: false,
      success: function (success, statusText, jqXHR) {
        SelectiveDatas.data[configKey] = success;
      }
    });
    return SelectiveDatas.data[configKey] == null ? {} : SelectiveDatas.data[configKey];
  };
  // 3.3 根据configKey对应的数据列表
  SelectiveDatas.getItems = function (configKey) {
    var data = SelectiveDatas.get(configKey);
    if (data == null || data.items == null) {
      return {};
    }
    return data.items;
  };

  // 4、异常处理
  var _default = 'Default';
  var _sessionExpired = 'SessionExpired';
  var _system_error_msg = '系统忙，请稍后再试！';
  var ErrorHandler = function () {
    this._handlers = {};
  };
  // 4.1.1、注册
  ErrorHandler.prototype.register = function (errorCode, callback) {
    this._handlers[errorCode] = callback;
    return this;
  };
  // 4.1.2、注册默认
  ErrorHandler.prototype.registerDefault = function (callback) {
    this._handlers[_default] = callback;
    return this;
  };
  // 4.1.3、注册默认
  ErrorHandler.prototype.registerSessionExpired = function (callback) {
    this._handlers[_sessionExpired] = callback;
    return this;
  };
  // 4.2、注销
  ErrorHandler.prototype.unregister = function (errorCode) {
    delete this._handlers[errorCode];
    return this;
  };
  // 4.3、处理
  ErrorHandler.prototype.handle = function (jqXHR, statusText, error, options) {
    try {
      var faultData = {};
      try {
        if (jqXHR.responseText) {
          faultData = JSON.parse(jqXHR.responseText);
          // api接口返回错误处理
          if (faultData.code == -5002 && faultData.data) {
            faultData = faultData.data;
          }
        } else if (typeof jqXHR.data == 'object') {
          faultData = jqXHR.data;
        }
      } catch (e) {
        console.error(e);
      }
      if (this._handlers[faultData.errorCode] != null) {
        this._handlers[faultData.errorCode](faultData, options);
      } else {
        this._handlers[_default](faultData, options);
      }
    } catch (e) {
      if (window.appModal) {
        window.appModal.error(_system_error_msg);
      } else {
        alert(_system_error_msg);
      }
      console.error(jqXHR.responseText || statusText);
      console.error(e);
    } finally {
    }
  };
  // 4.5、内置异常处理
  // 4.5.1、会话过期处理
  var SessionExpiredHanlder = function (faultData, options) {
    var logout = function () {
      if (top) {
        top.location.href = ctx + faultData.data;
      } else {
        location.href = ctx + faultData.data;
      }
    };
    // if ($.alerts) {
    // oAlert2(faultData.msg);
    // } else {
    if (window.appModal) {
      window.appModal.error(faultData.msg);
    } else {
      alert(faultData.msg);
    }
    logout();
    // }
  };
  // 4.5.2、Hibernate乐观锁
  var StaleObjectStateHanlder = function (faultData, options) {
    // Hibernate乐观锁
    if (window.appModal) {
      window.appModal.error(_system_error_msg);
    } else {
      alert(_system_error_msg);
    }
  };
  // 4.5.3、字段验证处理
  var FieldValidationHanlder = function (faultData, options) {
    var fvMsg = faultData.msg;
    if (faultData.data.length > 0) {
      fvMsg = '';
      $.each(faultData.data, function () {
        var label = $("label[for='" + this.field + "']")
          .filter("label[class!='error']")
          .text();
        fvMsg += label + ': ' + this.msg + '\n\r';
      });
    }
    if (window.appModal) {
      window.appModal.error(fvMsg);
    } else {
      alert(fvMsg);
    }
  };
  // 4.5.4、业务异常处理
  var BusinessExceptionHanlder = function (faultData, options) {
    var errorData = faultData || {};
    if (errorData.data && typeof errorData.data == 'string') {
      if (appModal) {
        appModal.error(errorData.data);
      } else {
        alert(errorData.data);
      }
    } else {
      DefaultErrorHanlder(faultData, options);
    }
  };
  // 4.5.5、默认错误处理
  var DefaultErrorHanlder = function (faultData, options) {
    var errorData = faultData || {};
    var data = errorData.data || {};
    // 默认错误处理
    if (StringUtils.isNotBlank(data.msg)) {
      if (window.appModal) {
        appModal.error(data.msg);
      } else {
        alert(data.msg);
      }
    } else if (StringUtils.isNotBlank(errorData.msg)) {
      if (window.appModal) {
        appModal.error(errorData.msg);
      } else {
        alert(errorData.msg);
      }
    } else {
      console.error('unknow fault data: ' + JSON.stringify(faultData));
    }
  };
  // 4.6、返回默认的异常处理器
  var getDefaultErrorHandler = function (defaultErrorHandlerCallback) {
    var errorHandler = new ErrorHandler();
    errorHandler.register('StaleObjectState', StaleObjectStateHanlder);
    errorHandler.register('FieldValidation', FieldValidationHanlder);
    // 业务异常
    if ($.isFunction(defaultErrorHandlerCallback) === false) {
      defaultErrorHandlerCallback = DefaultErrorHanlder;
    }
    errorHandler.register('BusinessException', BusinessExceptionHanlder);
    errorHandler.register(_sessionExpired, SessionExpiredHanlder);
    errorHandler.register(_default, defaultErrorHandlerCallback);
    return errorHandler;
  };
  // 4.7、异常处理器实例化对外方法包装
  var errorHandler = {};
  errorHandler.getInstance = function (defaultErrorHandler) {
    if (defaultErrorHandler === false) {
      return new ErrorHandler();
    }
    return getDefaultErrorHandler(defaultErrorHandler);
  };

  // 5、JSON数据服务
  var JDS = {};
  JDS.handleSuccess = function (success, statusText, jqXHR, options) {
    localStorage.removeItem('errorAlertOnlyOnce');
    if (options.success) {
      options.success.apply(this, [success, statusText, jqXHR]);
    }
    if (options.mask === true) {
      if (appModal) {
        appModal.hideMask();
      }
    }
  };
  JDS.handleError = function (jqXHR, statusText, error, options) {
    if (options.mask === true) {
      if (appModal) {
        appModal.hideMask();
      }
    }
    var _errorcode = jqXHR.getResponseHeader('error');
    if (_errorcode) {
      appModal.error(_errorcode === 'ResponseTimeoutError' ? '服务响应超时' : '服务异常');
      appModal.hideMask();
      return;
    }
    if (error && error.NETWORK_ERR && error.NETWORK_ERR == error.code) {
      if (localStorage.errorAlertOnlyOnce) {
        return;
      }
      localStorage.errorAlertOnlyOnce = true;
      appModal.alert('网络异常');
      return;
    }
    var responseJSON = jqXHR.responseJSON,
      responseMsg;
    var responseText = jqXHR.responseText;
    var loginSessionOut = false;
    if (jqXHR.getResponseHeader('forbidLoginedSameTime')) {
      responseMsg = '您的账号已登录其他设备，请重新登录!';
      loginSessionOut = true;
    }

    if (
      jqXHR.getResponseHeader('tokenexpired') === 'true' ||
      (responseJSON && (responseMsg = responseJSON.msg) && responseMsg.indexOf('登录超时') > -1) ||
      (responseText && responseText.indexOf('/login/security_check') != -1)
    ) {
      loginSessionOut = true;
    }

    // //密码被锁定，多端已登录强制退出
    var logout = getCookie('logout');
    if (logout != null && logout == 'logout') {
      var lockedMsg_options = {
        title: '提示框', // 标题
        message: '因账号锁定，当前登录已被强制退出，请重新登录！', // 消息内容，不能为空
        type: 'warning', // 消息类型:success、info、warning、error、alert
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              window.top.location.href = ctx + '/j_spring_security_logout';
              return;
            }
          }
        }
      };
      appModal.dialog(lockedMsg_options);
      return;
    }

    if (loginSessionOut) {
      if (localStorage.errorAlertOnlyOnce) {
        return;
      }
      localStorage.errorAlertOnlyOnce = true;
      appModal.hide();
      appModal.alert(responseMsg || '登录超时，请重新登录', function () {
        window.top.location.href = ctx + '/logout';
        return;
      });
      throw new Error('登录超时，请重新登录'); // 抛异常，阻止脚本继续执行
    }

    // 原生错误处理
    if (options.error) {
      options.error.apply(this, [jqXHR, statusText, error]);
    } else if (options.errorHandler) {
      // 定制的异常处理器处理
      options.errorHandler(jqXHR, statusText, error, options);
    } else {
      // 默认的异常处理器处理
      getDefaultErrorHandler().handle(jqXHR, statusText, error, options);
    }
  };
  JDS.call = function (options) {
    options = $.extend(
      {
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
    var splitIndex = service.indexOf(Separator.Dot);
    jsonData.tenantId = options.tenantId;
    jsonData.userId = options.userId;
    jsonData.serviceName = service.substring(0, splitIndex);
    jsonData.methodName = service.substring(splitIndex + 1, service.length);
    jsonData.validate = options.validate;
    jsonData.version = options.version;
    var data = options.data;
    if ($.isArray(data) === true) {
      jsonData.args = JSON.stringify(data);
    } else {
      jsonData.args = JSON.stringify([data]);
    }
    var argTypes = options.argTypes || [];
    if ($.isArray(argTypes) === true) {
      jsonData.argTypes = argTypes;
    } else {
      jsonData.argTypes = [argTypes];
    }
    if (options.mask === true) {
      if (appModal) {
        appModal.showMask();
      }
    }
    var jdsUrl = ctx + '/json/data/services';
    options.urlParams = options.urlParams || {};
    var isMobileApp = options.urlParams['isMobileApp'];
    if (typeof appContext != 'undefined' && appContext.isMobileApp() && null == isMobileApp) {
      options.urlParams['isMobileApp'] = true;
    }
    jdsUrl = UrlUtils.appendUrlParams(jdsUrl, options.urlParams);

    $.ajax({
      type: 'POST',
      url: jdsUrl,
      data: JSON.stringify(jsonData),
      contentType: 'application/json',
      dataType: 'json',
      async: options.async,
      success: function (success, statusText, jqXHR) {
        if (success.success === false || success.errorCode) {
          // 业务异常
          JDS.handleError.apply(this, [jqXHR, statusText, {}, options]);
          return;
        }
        JDS.handleSuccess.apply(this, [success, statusText, jqXHR, options]);
      },
      error: function (jqXHR, statusText, error) {
        JDS.handleError.apply(this, [jqXHR, statusText, error, options]);
      }
    });
  };
  JDS.restfulGet = function (options) {
    options.type = 'GET';
    JDS.restfulRequest(options);
  };
  JDS.restfulPost = function (options) {
    options.type = 'POST';
    JDS.restfulRequest(options);
  };
  JDS.restfulRequest = function (options) {
    options = $.extend(
      {
        urlParams: {},
        async: true,
        mask: false,
        contentType: 'application/json',
        dataType: 'json'
      },
      options
    );
    if (options.mask === true) {
      if (appModal) {
        appModal.showMask();
      }
    }
    var type = options.type;
    var url = UrlUtils.appendUrlParams(options.url, options.urlParams);
    var data = null;
    if (typeof options.data == 'object' && options.contentType == 'application/json') {
      data = JSON.stringify(options.data);
    } else {
      data = options.data;
    }
    $.ajax({
      type: type,
      url: url,
      data: data,
      contentType: options.contentType,
      dataType: options.dataType,
      traditional: options.traditional,
      async: options.async,
      success: function (success, statusText, jqXHR) {
        // api接口返回错误处理
        if (success.code == -5002) {
          JDS.handleError.apply(this, [jqXHR, statusText, jqXHR, options]);
        } else {
          JDS.handleSuccess.apply(this, [success, statusText, jqXHR, options]);
        }
      },
      error: function (jqXHR, statusText, error) {
        JDS.handleError.apply(this, [jqXHR, statusText, error, options]);
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
  // 6、文件下载
  var FileDownloadUtils = {};
  /**
   * 下载资源目录(/resources/pt/)下的文件，filePath为相对资源目录的路径，error为导出异常时回到函数（IE下不支持）
   */
  FileDownloadUtils.downloadResource = function (filePath, error) {
    var urlPrefix = '/resfacade/';
    if (StringUtils.isBlank(filePath)) {
      return;
    }
    var pathBlock = filePath.split(/['/','\\']/);
    if (StringUtils.isBlank(pathBlock[0])) {
      pathBlock.shift();
    }
    if (pathBlock.length <= 1) {
      return;
    }
    var filename = pathBlock.pop();
    if (pathBlock[0] === 'cadriver') {
      urlPrefix = urlPrefix + 'share/cadriver';
      pathBlock.shift();
    } else if (pathBlock[0] === 'share') {
      urlPrefix = urlPrefix + 'share';
      pathBlock.shift();
    }
    var url = urlPrefix + pathBlock.join('/');
    FileDownloadUtils.downloadTools(
      url,
      {
        filename: filename
      },
      error
    );
  };
  /**
   * 下载芒果DB的文件，参数可以是folderId/fileId，error为导出异常时回到函数（IE下不支持）
   */
  FileDownloadUtils.downloadMongoFile = function (options) {
    var urlPrefix = ctx + '/repository/file/mongo/';
    options = $.extend(
      {
        folderId: '', // 文件夹ID 字符串
        purpose: '', // 用途
        fileId: '', // 文件ID 字符串、字符串数组
        batchCompression: false
        // 多个文件时，是否压缩
      },
      options
    );
    if (StringUtils.isNotBlank(options.folderId)) {
      $.ajax({
        type: 'POST',
        url: ctx + urlPrefix + 'downLoadFiles',
        data: {
          folderID: options.folderId,
          purpose: options.purpose
        },
        async: false,
        dataType: 'json',
        success: function (files) {
          options.fileId = $.map(files, function (file) {
            var fileStr = file.split(constant.Separator.Semicolon);
            return fileStr[1];
          });
        }
      });
    }
    if (typeof options.fileId === 'string') {
      if (StringUtils.isNotBlank(options.fileId)) {
        FileDownloadUtils.downloadTools(
          urlPrefix + 'download',
          {
            fileID: options.fileId
          },
          options.error
        );
      }
    } else if ($.isArray(options.fileId) && options.fileId.length > 0) {
      if (options.batchCompression) {
        var fileIDs = $.map(options.fileId, function (fileId) {
          return {
            fileID: fileId
          };
        });
        FileDownloadUtils.downloadTools(
          urlPrefix + 'downAllFiles',
          {
            fileIDs: fileIDs,
            fileName: options.fileName
          },
          options.error
        );
      } else {
        var dataList = $.map(options.fileId, function (file) {
          return {
            fileID: file
          };
        });
        FileDownloadUtils.downloadTools4Window(urlPrefix + 'download', dataList);
      }
    }
  };
  /**
   * 根据URL提交表单下载，data可以json，json数组。如果是数组将依次下载各个文件。get方式提交数据
   */
  FileDownloadUtils.downloadTools4Window = function (url, data, target) {
    if (StringUtils.isBlank(target)) {
      target = '_self';
    }
    if (url.indexOf('?') === -1) {
      url = url + '?';
    }
    if ($.isArray(data)) {
      $.each(data, function (i, da) {
        var url1 = url;
        $.each(da, function (key, value) {
          if ($.isPlainObject(value) || $.isArray(value)) {
            value = JSON.stringify(value);
          }
          if (url1.substring(url.length - 1) !== '&' && url1.substring(url.length - 1) !== '?') {
            url1 = url1 + '&';
          }
          url1 = url1 + key + '=' + value;
        });
        window.open(url1, target);
      });
    } else if ($.isPlainObject(data)) {
      var url1 = url;
      $.each(data, function (key, value) {
        if ($.isPlainObject(value) || $.isArray(value)) {
          value = JSON.stringify(value);
        }
        if (url1.substring(url.length - 1) !== '&' && url1.substring(url.length - 1) !== '?') {
          url1 = url1 + '&';
        }
        url1 = url1 + key + '=' + value;
      });
      window.open(url1, target);
    }
  };
  /**
   * 以内部框架下载文件，支持异常下回调（IE下不支持），post方式提交数据，在下载窗未弹出前，连续调用只会下载最后一次。data只能是json
   */
  FileDownloadUtils.downloadTools = function (url, data, error, success) {
    var _auth = getCookie('_auth');
    if (_auth) {
      url += '?' + _auth + '=' + getCookie(_auth);
    }
    var $iFrame = $('#download_iframe');
    if ($iFrame.length) {
      $iFrame.remove();
      $iFrame = $('#download_iframe');
    }
    if (!$iFrame[0]) {
      $iFrame = $("<iframe style='display:none' id='download_iframe' name='download_iframe'/>");
      $('body').append($iFrame);
    }
    // zyguo , iframe的one事件不支持错误回调，所以传进来的error 是作用的，移除掉
    $iFrame.off('load').on('load', data, function (event, callback) {
      var title = $iFrame[0].contentDocument ? $iFrame[0].contentDocument.title : null;
      if (title === '系统忙') {
        appModal.error('下载失败');
      }
      if (callback) {
        callback.call($(this));
      }
    });
    var method = url.indexOf('/repository/file/mongo/download') > -1 ? 'get' : 'post';
    var $form = $("<form style='display:none' method='" + method + "' target='_self' />").attr('action', url);
    $.each(data, function (key, value) {
      var $input = $("<input type='hidden' >").attr('name', key);
      if ($.isPlainObject(value) || $.isArray(value)) {
        value = JSON.stringify(value);
      }
      $input.val(value);
      $form.append($input);
    });
    $($iFrame[0].contentDocument.body).append($form);
    $form.submit();
    if (success) {
      success();
    }
    $form.remove();
  };
  /**
   * 下载方法，用法同JDS，无成功回调函数，服务器端对应的service方法必须返回对象必须实现javax.activation。DataSource接口
   */
  FileDownloadUtils.download = function (options) {
    options = $.extend(
      {
        data: [],
        ignoreLoginTenantId: null,
        ignoreLoginUserId: null,
        validate: false,
        mask: true
      },
      options
    );
    var jsonData = {};
    var service = options.service;
    var splitIndex = service.indexOf(Separator.Dot);
    jsonData.tenantId = options.tenantId;
    jsonData.userId = options.userId;
    jsonData.serviceName = service.substring(0, splitIndex);
    jsonData.methodName = service.substring(splitIndex + 1, service.length);
    jsonData.validate = options.validate;
    jsonData.version = constant.PT_VERSION;
    var data = options.data;
    var args = '';
    if ($.isArray(data) === true) {
      args = JSON.stringify(data);
    } else {
      args = JSON.stringify([data]);
    }
    var url = getBackendUrl() + '/file/download/services';
    FileDownloadUtils.downloadTools(
      url,
      {
        jsonData: jsonData,
        args: args
      },
      options.error,
      options.success
    );
  };

  // 7、WebUtils
  var WebUtils = {};

  // 8、验证
  var Validation = {};
  // 8.1 验证
  Validation.validate = function (options) {
    var _self = this;
    var callback = options.callback;
    var $form = $(options.form || options.container);
    if (!$form.is('form') && $form.find('form').length == 0 && options.wrapperForm === true) {
      var $wrapperForm = $("<form action=''></form>");
      $form.children().appendTo($wrapperForm);
      $form.append($wrapperForm);
      $form = $wrapperForm;
    }
    var beanName = options.beanName;
    var serverOptions = {};
    $.ajax({
      type: 'GET',
      url: ctx + '/common/validation/metadata?beanName=' + beanName,
      contentType: 'application/json',
      dataType: 'json',
      async: false,
      success: function (success, statusText, jqXHR) {
        serverOptions = success;
      },
      error: function (jqXHR, statusText, error) {
        getDefaultErrorHandler().handle(jqXHR, statusText, error, options);
      }
    });
    var defaultOptions = {
      ignore: ''
    };
    options = $.extend(true, serverOptions, defaultOptions, options);
    if (callback != null) {
      callback.call(this, options);
    }
    $.each(options.rules, function (p) {
      if (options.addRequiredMarker && this.required === true) {
        $("label[for='" + p + "']", $form).after('<font color="red">*</font>');
      }
      if (this.remote) {
        this.remote = '/common/validation/metadata?beanName=' + beanName;
      }
    });
    var validator = $form.validate(options);
    return validator;
  };
  // 1.2 添加验证方法
  Validation.addMethod = function () {
    jQuery.validator.addMethod.apply(jQuery.validator, arguments);
  };

  // 9、ajax请求
  var ajax = function (options) {
    // 支持进度条
    if (options && $.isFunction(options.process)) {
      options.async = true; // 异步执行
      options.interval < 1000 && (options.interval = 1000);
      options.processId = new Date().getTime();
      var appendUrl = '_processId=' + options.processId;
      options.url = options.url + (options.url.indexOf('?') > 0 ? '&' : '?') + appendUrl;

      function processQuery(appendUrl) {
        function cb() {
          options.processJqXHR = $.ajax({
            url: ctx + '/common/process/query?' + appendUrl,
            type: 'GET',
            cache: false,
            dataType: 'json',
            success: function (result) {
              result.rate = (100 * result.current) / result.total + '%';
              options.process.apply(this, arguments);
              options.tid = setTimeout(cb, options.interval);
            }
          });
        }

        options.tid = setTimeout(cb, options.interval);
      }

      processQuery(appendUrl);
      var complete = options.complete;
      options.complete = function (jqXHR, status) {
        options.tid && clearTimeout(options.tid);
        // 单例错误，支持续点
        if (jqXHR && jqXHR.processId) {
          processQuery('_processId=' + jqXHR.processId);
        }
        $.isFunction(complete) && complete.apply(this, arguments);
      };
    }
    return $.ajax.apply(this, arguments);
  };

  // 1、安全工具类
  server.SpringSecurityUtils = SecurityUtils;
  // 2、系统配置参数
  server.SystemParams = SystemParams;
  // 3、选择性数据
  server.SelectiveDatas = SelectiveDatas;
  // 4、异常处理
  server.ErrorHandler = errorHandler;
  // 5、JSON数据服务
  server.JDS = JDS;
  // 6、文件下载
  server.FileDownloadUtils = FileDownloadUtils;
  // 7、 WebUtils工具类
  server.WebUtils = WebUtils;
  // 8、验证
  server.Validation = Validation;
  // 9、ajax请求
  server.ajax = ajax;

  if (isGlobal === true) {
    window.server = server;
  }

  return server;
});
