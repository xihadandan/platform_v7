var wpsSupport = {
  npdebug: null,
  requireJsName: 'js-wps',
  os: (function (userAgent) {
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
  })(navigator.userAgent.toLowerCase()),
  browser: (function (userAgent) {
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
  })(navigator.userAgent.toLowerCase())
};
var _auth = window.getCookie && getCookie('_auth');
var _backendUrl = window.getBackendUrl && getBackendUrl();
_backendUrl = _backendUrl && _backendUrl.replace(/\/$/, '');
var getCtx = function getCtx() {
  return window.ctx || '';
};
function appendBackendUrl(url) {
  var orig = window.location.origin + getCtx();
  if ($.trim(_backendUrl).length > 0) {
    if (url.indexOf(orig) === 0) {
      return url.replace(orig, _backendUrl);
    } else {
      return _backendUrl + url;
    }
  }
  if (url.indexOf(orig) === 0) {
    return url;
  }
  return orig + url;
}
function appendBackendJwt(url) {
  if (_auth) {
    return url + (url.indexOf('?') > -1 ? '&' : '?') + _auth + '=' + getCookie(_auth);
  }
  return url;
}
function appendBackendParams(url) {
  return appendBackendUrl(appendBackendJwt(url));
}
// location.origin polyfill
if (window.location.origin == null) {
  window.location.origin =
    window.location.protocol + '//' + window.location.hostname + (window.location.port ? ':' + window.location.port : '');
}

function checkWPSProtocolDelay(uri, successcb, failcb) {
  jQuery.support.cors = true;
  window.location.href = uri;
  setTimeout(function () {
    checkWPSProtocolInner(successcb, failcb, 16);
  }, 1000);
}
// 检测本地是否支持wps协议
function checkWPSProtocolInner(successcb, failcb, i) {
  jQuery.support.cors = true;
  $.ajax({
    type: 'get',
    url: 'http://localhost:58890/kso/protocolcheck?protocol=KsoWebStartupWPS',
    cache: false,
    async: true,
    timeout: 1000,
    beforeSend: null,
    dataType: 'json',
    success: function (data) {
      try {
        if (data.result == 'true') {
          successcb();
        } else {
          failcb();
        }
      } catch (e) {
        failcb();
      }
    },
    error: function (xhr, status, error) {
      i--;
      if (i > 0) {
        checkWPSProtocolInner(successcb, failcb, i);
      } else {
        failcb();
      }
    }
  });
}

/**
 * 自定义扩增支持数科OFD
 */
(function (root, factory) {
  'use strict';
  var requireJsName = wpsSupport.requireJsName;
  if (typeof define === 'function' && define.amd) {
    define((wpsSupport.requireJsName = requireJsName + '-custom'), [requireJsName], factory); //
  } else if (typeof exports === 'object') {
    module.exports = factory(require(requireJsName));
  } else {
    root._wps = factory(root._wps);
  }
})(this, function initOFD(_wps) {
  // TODO 实现[2.4　数科URL 协议调用]
  // 调用格式：suwellofd:[openurl]&saveurl=[saveurl]&comopisteinvisble=[id1|id2|...]
  _wps.openOfd =
    _wps.openOfd ||
    function openOfd(params) {
      _runParams('suwell', [{ OpenDoc: params }]);
    };
  return _wps;
});

/**
 * np-api实现，与js-api同一层调用
 *
 */
(function (root, factory) {
  'use strict';
  if (wpsSupport.os.isLinux || wpsSupport.npdebug) {
    // CX操作系统，直接返回
    var requireJsName = wpsSupport.requireJsName;
    if (typeof define === 'function' && define.amd) {
      define((wpsSupport.requireJsName = 'np-wps'), [requireJsName], factory); //
    } else if (typeof exports === 'object') {
      module.exports = factory(require(requireJsName));
    } else {
      root._wps = factory(root._wps);
    }
  }
})(this, function init(_wps) {
  var winObjs = (wpsSupport.winObjs = {});
  var getCtx = function getCtx() {
    return window.ctx || '';
  };
  var getNormal = function getNormal(type) {
    if (type === 'wpp') {
      type = 'dps';
    } else if (type === 'suwell' || type === 'ofd') {
      type = 'ofd';
    }
    return window.location.origin + getCtx() + '/static/wps/newfile.' + type;
  };
  var availWidth = window.screen.availWidth - 10;
  var availHeight = window.screen.availHeight - 30;
  var winFuture =
    'width=' + availWidth + ',height=' + availHeight + ',top=0,left=0,toolbar=0,menubar=0,scrollbars=0,resizable=0,location=0,status=0';
  function _buildWpsApi(name, wpsFunction) {
    wpsFunction = wpsFunction.toString();
    var matchs = wpsFunction.match(/_runParams\s*\(\s*"(wps|wpp|et|suwell)"/);
    var type = (matchs && matchs[1]) || 'wps'; // 默认为wps
    return function _runParams(params, funcs) {
      var docId, codes, url, wpsCallback, title;
      var args = Array.prototype.slice.call(arguments);
      // 获取WPS窗体
      var winObj = winObjs[(docId = params.docId || '_blank')];
      function doSendMessage() {
        return winObj.postMessage(
          {
            fName: name,
            fArgs: JSON.stringify(args)
          },
          location.origin
        );
      }
      // 文件已近打开，直接发送
      if (winObj && winObj.postMessage) {
        return doSendMessage();
      }
      winObj = window.open('about:blank', docId, winFuture);
      // 在WPS实例中注册回调
      wpsCallback = winObj.wpsCallback = {};
      wpsCallback.onload = function (event) {
        // 初始化成功
        // console.log(event);
        return doSendMessage(event);
      };
      wpsCallback.onerror = function (event) {
        delete winObjs[docId];
        return alert('初始化失败');
      };
      wpsCallback.onbeforeunload = function (event) {
        // WPS退出
        delete winObjs[docId];
      };
      // 组织WPS展示HTML的内容
      codes = [];
      title = params.newFileName || '新建';
      url = window.location.origin + getCtx() + '/static/body/np-api.js';
      codes.push(
        '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">'
      );
      // https://developer.mozilla.org/en-US/docs/Web/API/Window/open#Note_on_security_issues_of_the_status_bar_presence
      codes.push('<html style="width:100%;height:calc(100% - 20px);padding:0px;margin:0px;border-width:0px;">');
      codes.push('<head>');
      codes.push('<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />');
      codes.push('<title>' + title + '</title>');
      if (type === 'suwell' || type === 'ofd') {
        url = window.location.origin + getCtx() + '/static/ofd/ofd-api.js';
        codes.push(
          '<script type="text/javascript" src="' + window.location.origin + getCtx() + '/static/ofd/suwell_ofdReader.js"></script>'
        );
      }
      codes.push(
        '<script type="text/javascript" src="' +
          url +
          '" onload="wpsCallback.onload(event);" onerror="wpsCallback.onerror(event);"></script>'
      );
      codes.push('</head>');
      if (type === 'suwell' || type === 'ofd') {
        codes.push('<body id="webwps_id" style="width:100%;height:100%;padding:0px;margin:0px;border-width:0px;">');
      } else {
        codes.push('<body style="width:100%;height:100%;padding:0px;margin:0px;border-width:0px;">');
        codes.push(
          "<object type='application/x-" +
            type +
            "' data-newfile='" +
            getNormal(type) +
            "' name='webwps' id='webwps_id' width='100%' height='100%' wpsshieldbutton=\"false\" >"
        );
        codes.push("<param name='Enabled' value='1' />");
        codes.push('<param name="quality" value="high" />');
        codes.push('<param name="bgcolor" value="#ffffff" />');
        codes.push('<param name="allowFullScreen" value="true" />');
        codes.push('</object>');
      }
      codes.push('</body>');
      codes.push('</html>');
      winObj.document.write(codes.join('\n'));
      // win.document.write('<h1>HelloWorld</h1>');
      winObjs[docId] = winObj;
    };
  }
  var exports = {};
  function checkWPSProtocolInner(successcb, failcb, i) {
    // https://developer.mozilla.org/en-US/docs/Archive/Plugins/Reference/NP_GetMIMEDescription
    // application/x-wps、application/x-wpp、application/x-et
    var mimeType = navigator.mimeTypes['application/x-wps'] || wpsSupport.npdebug;
    return mimeType == null ? failcb() : successcb();
  }
  window.checkWPSProtocol = window.checkWPSProtocolInner = checkWPSProtocolInner;
  for (var name in _wps) {
    var attr = _wps[name];
    exports[name] = typeof attr === 'function' ? _buildWpsApi(name, attr) : attr;
  }
  Object.defineProperty(exports, '__esModule', {
    value: true
  });
  return exports;
});

/**
 * wps-api实现
 *
 */
(function (root, factory, factory2) {
  'use strict';
  var ocxUrl = ctx + '/static/body/npWebOffice.dll';
  var $attachOCX = $(
    '<object type="application/weboffice-plugin" codebase="' + ocxUrl + '"  id="attachOCX" width="100%" height="0" ></OBJECT>'
  );
  $attachOCX.appendTo(document.body);
  var attachOCX = $attachOCX[0],
    welloffice;
  if (window.SystemParams && SystemParams.getValue) {
    welloffice = SystemParams.getValue('app.fileupload.welloffice.enable');
  }
  if (welloffice == 'true') {
    return (root.bodyApi = factory2($, attachOCX)); // welloffice开发模式
  }
  var disableWellOffice = navigator.mimeTypes['application/weboffice-plugin'] == null && attachOCX.init == null;
  if (welloffice == 'auto' && false == disableWellOffice) {
    root.bodyApi = factory2($, attachOCX);
  } else {
    // 默认或则不支持weboffice
    $attachOCX.remove();
    if (typeof define === 'function' && define.amd) {
      define(['jquery', 'commons', 'server', 'js-base64', wpsSupport.requireJsName], factory);
    } else if (typeof exports === 'object') {
      module.exports = factory(
        require('jquery'),
        require('commons'),
        require('server'),
        require('js-base64'),
        require(wpsSupport.requireJsName)
      );
    } else {
      root.bodyApi = factory(root.jQuery, root.commons, root.server, root.base64, root._wps);
    }
  }
})(
  this,
  function init($, commons, server, base64, _wps) {
    var bodyApi = {};
    $.extend(bodyApi, {
      params: {},
      guid: function () {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
          var r = (Math.random() * 16) | 0,
            v = c == 'x' ? r : (r & 0x3) | 0x8;
          return v.toString(16);
        });
      },
      checkWPSProtocol: function (once) {
        var self = this;
        var def = $.Deferred();
        function scb() {
          def.resolveWith(self, ['Support WPS']);
        }
        function fcb() {
          def.rejectWith(self, ['您的WPS版本未集成OA助手']);
        }
        // 直接调用AJAX检测，防止弹出打开WPS确认框，提升体验
        checkWPSProtocolInner(
          scb,
          once
            ? fcb
            : function () {
                checkWPSProtocol(scb, fcb); // 二层检测,防止手动退出WPS守护进程
              },
          1
        );
        return def.promise();
      },
      getSid: function () {
        var self = this;
        var sid = self.sid;
        if (sid == null || typeof sid === 'undefined') {
          sid = self.sid = $.Deferred();
          // 建立webSocket连接
          if ('WebSocket' in window) {
            var ctx = getCtx(),
              wsUrl = null;
            if ($.trim(_backendUrl).length) {
              wsUrl = appendBackendJwt('ws://' + _backendUrl.replace('https://', '').replace('http://', '') + '/newFileFromWPS');
            } else {
              wsUrl = 'ws://' + window.location.host + ctx + '/newFileFromWPS';
            }
            var transport = (self.transport = new WebSocket(wsUrl));
            transport.onopen = function (event) {
              console.log('webSocket连接建立');
            };
            transport.onmessage = function (event) {
              var result = $.parseJSON(event.data);
              if (sid.state() === 'pending') {
                // 第一个消息为会话消息
                sid.resolveWith(self, [result.data]);
              } else if (result.code === 0 && result.msg) {
                self.retMessage(result);
              }
              // console.log("webSocket返回消息：" + event.data);
            };
            transport.onerror = function (event) {
              if (sid.state() === 'pending') {
                sid.rejectWith(self, [event]);
              }
              console.log('webSocket连接出错');
            };
            transport.onclose = function (event) {
              if (sid.state() === 'pending') {
                sid.rejectWith(self, [event]);
              } else {
                self.sid = null; // 服务器主动关闭时，重新连接
              }
              console.log('webSocket连接关闭');
            };
          } else {
            // TODO DWR的ScriptSession实现，支持IE8+
            sid.resolveWith(self, [-1]); // 传统模式，"不支持回调"
          }
        }
        return sid.promise();
      },
      retMessage: function (result) {
        var self = this;
        var params = self.params[result.msg] || {};
        if (result.success && params.success) {
          params.success.apply(self, [result]);
        } else if (result.success === false || params.error) {
          params.error.apply(self, [result]);
        }
        // delete self.params[result.msg]; // 删除回调?多次保存回写
      }
    });
    function _hookWpsApi(name, wpsFunction) {
      return function (params) {
        var self = this;
        var args = arguments;
        params.docId = params.docId || self.guid();
        self
          .checkWPSProtocol()
          .then(function (msg) {
            // 检测WPS协议
            self
              .getSid()
              .then(function (sid) {
                // 建立会话
                self.params[params.docId] = params; // 保存回调
                if (params.fileName && params.fileName.indexOf('http') === 0) {
                  params.fileName += (params.fileName.indexOf('?') > -1 ? '&' : '?') + 'sId=' + sid;
                  params.fileName = appendBackendParams(params.fileName);
                }
                // 上传设置会话
                if (params.uploadPath && params.uploadPath.indexOf('http') === 0) {
                  params.uploadPath += (params.uploadPath.indexOf('?') > -1 ? '&' : '?') + 'sId=' + sid;
                  params.uploadPath += params.uploadPath.indexOf('fileID=') > -1 ? '' : '&fileID=' + params.docId;
                  params.uploadPath += params.uploadPath.indexOf('newVer=') > -1 ? '' : '&newVer=' + params.newVer || '';
                  params.uploadPath = appendBackendParams(params.uploadPath);
                }
                // 套红设置会话
                if (name === 'insertRedHeadDocFromWeb' && args[1]) {
                  var templateURL = args[1];
                  templateURL = templateURL.replace('/repository/file/mongo', '/office/wps');
                  args[1] = templateURL + (templateURL.indexOf('?') > -1 ? '&' : '?') + 'sId=' + sid;
                  params.insertFileUrl = appendBackendParams(params.insertFileUrl);
                }
                // 删除表格的版本控制
                if (name == 'openET' && (params['openType'] || params['revisionCtrl'])) {
                  params['_openType'] = params['openType'];
                  params['_revisionCtrl'] = params['revisionCtrl'];
                  delete params['openType']; //
                  delete params['revisionCtrl']; //
                }
                var openType = params.openType,
                  protectTypes = [0, 1, 2, 3];
                if (openType && openType.password == null && $.inArray(openType.protectType, protectTypes) > -1) {
                  // 123456 保护模式需要设置密码
                  openType.password = '________________';
                }
                // console.log(args);
                return wpsFunction.apply(this, args); // 发起请求
              })
              .fail(function (data) {
                alert('WPS会话建立失败');
                console.log(data);
              });
          })
          .fail(function (msg) {
            if (wpsSupport.os.isWindows && confirm('WPS版本未集成OA助手,点击“确认”下载')) {
              return window.open('ftp://wellsoft:anonymous@ftp.well-soft.com/office/wps/WPSOffice2019.exe', '_blank');
            } else if (wpsSupport.os.isLinux && confirm('未安装WPS专业版,请联系供应商提供支持')) {
              // return
              // window.open("ftp://wellsoft:anonymous@ftp.well-soft.com/office/wps",
              // "_blank");
            }
          });
      };
    }
    for (var name in _wps) {
      var attr = _wps[name];
      bodyApi[name] = $.isFunction(attr) ? _hookWpsApi(name, attr) : attr;
    }
    window.base64 = window.base64 || base64;
    window.launchKsoWebStartup = function (uri, appname, data) {
      jQuery.support.cors = true;
      // window.location.href = uri; // 只启动一次，checkWPSProtocol会启动一次
      if (data.indexOf(getWPSWebStartupHead(appname)) == -1) data = getWPSWebStartupHead(appname) + data;
      setTimeout(function () {
        postData(getWPSRunUrl(appname), data, 5);
      }, 2000);
    };
    // 导出全局getBodyApi
    window.getWpsApi = function () {
      console.error('getWpsApi废弃，请使用getBodyApi');
      return bodyApi;
    };
    window.getBodyApi = function () {
      return bodyApi;
    };
    return bodyApi;
  },
  function initWellOffice($, attachOCX) {
    var bodyApi = {
      checkWPSProtocol: function () {
        var def = $.Deferred();
        def.rejectWith(this, ['welloffice模式']);
        return def.promise();
      }
    };
    var ocxeventmap = {};
    function loadFileByFileID(fileID) {
      var fileResult = null;
      $.ajax({
        async: false,
        url: ctx + '/repository/file/mongo/getNonioFiles',
        type: 'POST',
        data: { fileID: fileID },
        dataType: 'json',
        success: function (result) {
          fileResult = result;
          $.each(result.data || [], function (idx, file) {
            if (file.filename) {
              file.fileName = file.filename;
            } else if (file.fileName) {
              file.filename = file.fileName;
            }
          });
        },
        error: function (data) {
          console.error(JSON.stringify(data));
        }
      });
      return fileResult;
    }
    function addEvent(obj, name, func) {
      if (ocxeventmap[name]) {
        removeEvent(ocxeventmap[name].obj, name, ocxeventmap[name].func);
        delete ocxeventmap[name];
      }

      ocxeventmap[name] = {};
      ocxeventmap[name].func = func;
      ocxeventmap[name].obj = obj;
      if (obj.attachEvent) {
        obj.attachEvent('on' + name, func);
      } else {
        obj.addEventListener(name, func, false);
      }
    }
    function removeEvent(obj, name, func) {
      if (obj.detachEvent) {
        obj.detachEvent('on' + name, func);
      } else {
        obj.removeEventListener(name, func, false);
      }
    }
    var bInit = false,
      iFail = 0;
    var noSupport = navigator.mimeTypes['application/weboffice-plugin'] == null && attachOCX.init == null;
    var defaultDownloadLocalPath = 'c:\\temp4attachocx\\';
    var url = appendBackendParams('/ocxFileupload');
    var cookie = JSON.stringify(document.cookie);
    function initOffice() {
      try {
        attachOCX.init('admin', url, defaultDownloadLocalPath, cookie);
        return (bInit = true);
      } catch (ex) {
        console.log('控件初始化失败', ex);
        if (
          noSupport &&
          confirm(
            '点击“确定”下载welloffice，安装后，仅在支持的浏览器中可以使用。\n支持浏览器列表：\n1、360浏览器：360极速浏览器（推荐使用）、360安全浏览器\n2、IE浏览器：IE11（32位）\n3、chromium浏览器：版本号小于45，操作系统不支持windows10\n安装注意事项：\n1、安装前，先关闭浏览器，并退出安全防护软件。\n2、将本网站加入到您的信任站点中。\n3、对于IE8浏览器，需要在浏览器上方允许运行脚本。\n4、对于IE8以上版本的IE浏览器，请根据浏览器下方的提示安装控件。'
          )
        ) {
          window.open(location.origin + ctx + '/static/js/pt/js/fileupload/ocx/welloffice.exe', '_blank');
        }
        return false;
      }
    }
    function openOffice(options) {
      if (!bInit && initOffice(options) === false) {
        return; // 初始化失败
      }
      var bDown, bNew;
      var docId = options.docId;
      var fileExt = options.fileExt;
      var localPath = defaultDownloadLocalPath + docId + '.' + fileExt;
      if (attachOCX.isLocalFileExist(localPath)) {
        bDown = 1;
      } else if (options.fileName) {
        var psFileModified = new Date().format('yyyy-MM-dd HH:mm:ss');
        var url = location.origin + ctx + fileServiceURL.downloadOcx + docId;
        bDown = attachOCX.download(url, psFileModified, localPath, -1, false);
      } else {
        bNew = true;
        var psFileModified = new Date().format('yyyy-MM-dd HH:mm:ss');
        var durl = location.origin + ctx + '/static/js/wps/newfile.';
        if (fileExt == 'xls') {
          durl += 'et';
        } else if (fileExt == 'doc') {
          durl += 'wps';
        } else if (fileExt == 'ppt') {
          durl += 'dps';
        } else if (fileExt == 'ofd') {
          durl += 'ofd';
        }
        bDown = attachOCX.download(durl, psFileModified, localPath, -1, false);
      }
      if (!bDown) {
        console.log('附件下载失败');
        return;
      } else if (attachOCX.isLocalFileOpen(localPath)) {
        attachOCX.focusLocalFile(localPath);
        return;
      }
      var openType = options['openType'] || options['_openType'],
        protectTypes = [0, 1, 2, 3];
      var isReadonly = !!(openType && protectTypes.indexOf(openType.protectType) > -1);
      var bOpen = attachOCX.openLocalFile(localPath, isReadonly === true ? 0 : 1, -1);
      options.userName && attachOCX.setEditUser(localPath, options.userName);
      var revisionCtrl = options.revisionCtrl || {};
      var bOpenRevision = !!revisionCtrl.bOpenRevision;
      var bShowRevision = !!revisionCtrl.bShowRevision;
      var bEnabledRevision = !!revisionCtrl.bEnabledRevision;
      // 先解除保护，在操作
      if (bEnabledRevision) {
        attachOCX.setRevisionEnabled(localPath, true);
      }
      attachOCX.setRevision(localPath, bOpenRevision, bShowRevision);
      if (typeof revisionCtrl.showRevisionsMode === 'number') {
        // JSAPI(WPS)支持showRevisionsMode
        console.error('welloffice不支持showRevisionsMode');
        // WPS Office支持，MS Office会奔溃
        // attachOCX.showRevisionsMode(localPath, revisionCtrl.showRevisionsMode || 1);
      }
      if (revisionCtrl.bAcceptRevision) {
        attachOCX.acceptRevision(localPath);
      }
      if (revisionCtrl.bRejectRevision) {
        attachOCX.rejectRevision(localPath);
      }
      // 操作后，保护起来
      if (false === bEnabledRevision) {
        attachOCX.setRevisionEnabled(localPath, false);
      }
      if (!bOpen) {
        console.log('附件打开失败');
        return;
      } else if (isReadonly) {
        return; // 只读打开
      }
      var tid;
      function uploadFile() {
        if (attachOCX.isLocalFileOpen(localPath)) {
          return (tid = setTimeout(uploadFile, 1000));
        }
        clearTimeout(tid);
        appModal.confirm("点击“确定”上传OA，是否上传？", function (result) {
          if (result) {
            try {
              var fileParams = {
                fileId: docId,
                fileName: encodeURI(options.newFileName)
              };
              var msg = attachOCX.uploadFile(localPath, 'fileParams://' + JSON.stringify(fileParams), 1000000000, false);
              // console.log(msg);// {success:1, fileID:"aed5db9b20194c62b1dbad2cf623353c"}
              if (msg && msg.indexOf('{') > -1) {
                msg = eval('(' + msg + ')');
              }
              var fileResult = null;
              if (msg.fileID && (fileResult = loadFileByFileID(msg.fileID))) {
                $.isFunction(options.success) && options.success(fileResult);
                attachOCX.deleteLocalFile(localPath);
              } else {
                $.isFunction(options.error) && options.error('文件上传失败');
              }
            } catch (ex) {
              console.error(ex);
              $.isFunction(options.error) && options.error(ex.message || '附件保存失败');
            }
          } else if (bNew) {
            attachOCX.deleteLocalFile(localPath);
          }
        });
      }
      tid = setTimeout(uploadFile, 5000);
    }
    bodyApi.openET = function (options) {
      options.fileExt = 'xls';
      openOffice.call(this, options);
    };
    bodyApi.openDoc = function (options) {
      options.fileExt = 'doc';
      openOffice.call(this, options);
    };
    bodyApi.openWpp = function (options) {
      options.fileExt = 'ppt';
      openOffice.call(this, options);
    };
    bodyApi.openOfd = function (options) {
      options.fileExt = 'ofd';
      openOffice.call(this, options);
    };
    bodyApi.pasteFile = function (options) {
      if (!bInit && initOffice(options) === false) {
        return; // 初始化失败
      }
      var paths = attachOCX.getLocalCopyFiles(),
        localFiles = [];
      // console.log(paths);// C:\Users\admin\Desktop\正文.doc|C:\Users\admin\Desktop\谷物.doc|
      if ($.trim(paths).length <= 0) {
        alert('没有粘贴附件，请先复制附件');
        return;
      }
      paths = paths.split('|');
      for (var i = 0; i < paths.length; i++) {
        var path = paths[i];
        if ($.trim(path).length <= 0) {
          continue;
        }
        var fileName = path.substr(path.lastIndexOf('\\') + 1);
        if (fileName.length > 80) {
          alert('附件文件名称超长(不得超过80个字)[' + fileName + ']');
          return;
        } else if (/[\/\\:\*\?"%<>\|\n]/gi.test(fileName)) {
          alert('文件名不得含有字符:"/ \\ : * ? " % < > |"及"换行符"');
          return;
        }
        localFiles.push({
          path: path,
          name: fileName,
          size: attachOCX.getLocalFileSize(path)
        });
      }
      if (options.validate && false === options.validate(localFiles)) {
        return;
      }
      for (var i = 0; i < localFiles.length; i++) {
        var fileObj = localFiles[i];
        var path = fileObj.path;
        var fileParams = {
          fileName: encodeURI(fileObj.name)
        };
        var msg = attachOCX.uploadFile(path, 'fileParams://' + JSON.stringify(fileParams), 1000000000, false);
        // console.log(msg);// {success:1, fileID:"aed5db9b20194c62b1dbad2cf623353c"}
        if (msg && msg.indexOf('{') > -1) {
          msg = eval('(' + msg + ')');
        }
        var fileResult = null;
        if (msg.fileID && (fileResult = loadFileByFileID(msg.fileID))) {
          $.isFunction(options.success) && options.success(fileResult);
        } else {
          $.isFunction(options.error) && options.error('文件[' + fileObj.name + ']上传失败');
        }
      }
    };
    bodyApi.saveAsFile = function (options) {
      if (!bInit && initOffice(options) === false) {
        return; // 初始化失败
      }
      var $attachOCX = $(attachOCX);
      function downloadFromRemoteForSaveAs(localPath, options) {
        var _this = this;
        // 删除临时文件
        var localPathTmp = localPath + '.tmp';
        if (attachOCX.isLocalFileExist(localPathTmp)) {
          attachOCX.deleteLocalFile(localPathTmp);
        }
        // 提示覆盖
        if (attachOCX.isLocalFileExist(localPath)) {
          // 用户选择下载时，需要提示已存在
          if (!confirm(options.filename + '已存在,是否覆盖?')) {
            return false;
          }
        }
        // 关闭打开文件
        if (attachOCX.isLocalFileOpen(localPath)) {
          attachOCX.closeLocalFile(localPath, false);
        }
        // 异步下载, 通过监听事件来回调通知调用者
        window.appModal && appModal.showMask('正在下载中', 'body', 86400 * 1000);
        addEvent(attachOCX, 'downloadFile', function (msg) {
          console.log('下载完成---->结果为:' + msg);
          var isDownloadOk = msg != '下载成功' ? false : true;
          window.appModal && appModal.hideMask();
          try {
            if (isDownloadOk) {
              options.success && options.success(msg);
            } else {
              options.error && options.error(msg);
            }
          } finally {
            options.complete && options.complete(isDownloadOk, msg);
          }
        });
        var psFileModified = new Date().format('yyyy-MM-dd HH:mm:ss');
        attachOCX.downloadFile(options.url, psFileModified, localPath, -1, true);
      }
      addEvent(attachOCX, 'selectLocalSavePath', function (path) {
        if ($.trim(path).length == 0) {
          return;
        }
        downloadFromRemoteForSaveAs(path, options);
      });
      attachOCX.selectLocalSavePath(options.filename);
    };
    // 导出全局getBodyApi
    window.getWpsApi = function () {
      console.error('getWpsApi废弃，请使用getBodyApi');
      return bodyApi;
    };
    window.getBodyApi = function () {
      return bodyApi;
    };
    return bodyApi;
  }
);
