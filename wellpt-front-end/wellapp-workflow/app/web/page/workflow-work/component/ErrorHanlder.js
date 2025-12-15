'use strict';
import { isEmpty, isFunction } from 'lodash';
// 异常处理
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
      } else if (jqXHR.data != null && jqXHR.data.errorCode) {
        faultData = jqXHR.data;
      } else if (jqXHR.data != null && jqXHR.data.data != null && typeof jqXHR.data.data == 'object') {
        faultData = jqXHR.data.data;
      } else if (jqXHR.data != null && typeof jqXHR.data == 'object') {
        faultData = jqXHR.data;
      } else {
        faultData = jqXHR;
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
    options.callbackContext.$widget.$message.error(options.callbackContext.$widget.$t('WorkflowWork.message.serverBusyError', _system_error_msg));
    console.error(jqXHR.responseText || statusText);
    console.error(e);
  }
};
// 4.5、内置异常处理
// 4.5.1、会话过期处理
var SessionExpiredHanlder = function (faultData, options) {
  console.log(options);
  options.callbackContext.$widget.$message.error(faultData.msg);
};
// 4.5.2、Hibernate乐观锁
var StaleObjectStateHanlder = function (faultData, options) {
  console.log(faultData);
  console.log(options);
  options.callbackContext.$widget.$message.error(_system_error_msg);
};
// 4.5.3、字段验证处理
var FieldValidationHanlder = function (faultData, options) {
  console.log(options);
  var fvMsg = faultData.msg;
  options.callbackContext.$widget.$message.error(fvMsg);
};
// 4.5.4、业务异常处理
var BusinessExceptionHanlder = function (faultData, options) {
  var errorData = faultData || {};
  if (errorData.data && typeof errorData.data == 'string') {
    options.callbackContext.$widget.$message.error(errorData.data);
  } else {
    DefaultErrorHanlder(faultData, options);
  }
};
// 4.5.5、默认错误处理
var DefaultErrorHanlder = function (faultData, options) {
  console.log(options);
  var errorData = faultData || {};
  var data = errorData.data || {};
  // 默认错误处理
  if (!isEmpty(data.msg)) {
    options.callbackContext.$widget.$message.error(data.msg);
  } else if (!isEmpty(errorData.msg)) {
    options.callbackContext.$widget.$message.error(errorData.msg);
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
  if (isFunction(defaultErrorHandlerCallback) === false) {
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
export default errorHandler;
