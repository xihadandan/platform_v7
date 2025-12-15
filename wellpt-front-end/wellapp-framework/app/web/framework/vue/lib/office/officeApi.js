import xhr from 'xhr';
import { getCookie, generateId } from '../../utils/util';
import createClientCommonApi from '@pageAssembly/app/web/framework/vue/clientCommonApi';
const clientCommonApi = createClientCommonApi();

function getBackendUrl() {
  return getCookie('backend.url');
}

function serverlUrl(url) {
  url = url.indexOf('/') == 0 ? url.substr(1) : url;
  return `${getCookie('backend.url')}/${url}&jwt=${getCookie('jwt')}`;
}

function dateFormat(date, fmt) {
  // author: meizz
  var o = {
    'M+': date.getMonth() + 1, // 月份
    'd+': date.getDate(), // 日
    'H+': date.getHours(), // 小时
    'm+': date.getMinutes(), // 分
    's+': date.getSeconds(), // 秒
    'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
    S: date.getMilliseconds()
    // 毫秒
  };
  if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
  for (var k in o)
    if (new RegExp('(' + k + ')').test(fmt))
      fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
  return fmt;
}

const WellOffice = function () {
  this.ocxeventmap = {};
  this.tempLocalPath = 'c:\\temp4attachocx\\';
  this.inited = false;
  this.localPaths = {};
  this.url = serverlUrl('/ocxFileupload?');
  try {
    this.initWellOfficeDLL();
    if (this.attachOCX.init === undefined) {
      //FIXME：不知原因的初始化失败
      //初始化失败
      console.error('application/weboffice-plugin 初始化失败，重试');
      this.initWellOfficeDLL(true);
      return;
    }
    this.inited = true;
  } catch (error) {
    console.error('wellOffice.dll控件初始化失败: ', error);
    if (
      navigator.mimeTypes['application/weboffice-plugin'] == null &&
      this.attachOCX.init == null &&
      confirm(
        '点击“确定”下载welloffice，安装后，仅在支持的浏览器中可以使用。\n支持浏览器列表：\n1、360浏览器：360极速浏览器（推荐使用）、360安全浏览器\n2、IE浏览器：IE11（32位）\n3、chromium浏览器：版本号小于45，操作系统不支持windows10\n安装注意事项：\n1、安装前，先关闭浏览器，并退出安全防护软件。\n2、将本网站加入到您的信任站点中。\n3、对于IE8浏览器，需要在浏览器上方允许运行脚本。\n4、对于IE8以上版本的IE浏览器，请根据浏览器下方的提示安装控件。'
      )
    ) {
      window.open('/static/js/pt/js/fileupload/ocx/welloffice.exe', '_blank');
    }
  }
};

WellOffice.prototype.initWellOfficeDLL = function (renew) {
  if (renew) {
    document.querySelector('#attachOCX').remove();
  }
  // well office 动态库模式
  let attachOCX = document.createElement('object');
  attachOCX.setAttribute('type', 'application/weboffice-plugin');
  attachOCX.setAttribute('codebase', '/static/body/npWebOffice.dll?v=' + new Date().getTime());
  attachOCX.setAttribute('id', 'attachOCX');
  attachOCX.setAttribute('width', '100%');
  attachOCX.setAttribute('height', '0');
  document.body.appendChild(attachOCX);
  attachOCX.init('admin', this.url, this.tempLocalPath, '');
  this.attachOCX = attachOCX;
  if (this.attachOCX.init !== undefined) {
    this.inited = true;
    return;
  }
  if (renew && this.attachOCX.init == undefined) {
    console.error('application/weboffice-plugin 初始化失败，重试');
    this.initWellOfficeDLL(true);
  }
};

// 根据文件ID加载文件
WellOffice.prototype.loadFileByFileID = function (fileID, callback) {
  xhr.post('/repository/file/mongo/getNonioFiles', { body: { fileID }, json: true }, function (err, resp) {
    if (typeof callback === 'function') {
      callback(resp.body);
    }
  });
};

WellOffice.prototype.openFile = function (options, callback) {
  var bDown,
    bNew,
    _this = this;
  var psFileModified = null;
  var docId = options.docId;
  var origId = options.origId;
  var fileExt = options.fileExt;
  var fileName = options.newFileName;
  var attachOCX = this.attachOCX;
  // 未刷新页面，使用已经下载的文件重新打开
  var localPath = options.localPath || this.localPaths[docId];
  if (null == localPath) {
    localPath = this.localPaths[docId] = this.tempLocalPath + (origId || docId) + '\\' + new Date().getTime() + '\\' + fileName;
  }
  if (attachOCX.isLocalFileExist(localPath)) {
    bDown = 1;
  } else if (options.fileName) {
    psFileModified = dateFormat(new Date(), 'yyyy-MM-dd HH:mm:ss');
    bDown = attachOCX.download(serverlUrl(`/repository/file/mongo/download4ocx?fileID=${docId}`), psFileModified, localPath, -1, false);
  } else {
    bNew = true;
    psFileModified = dateFormat(new Date(), 'yyyy-MM-dd HH:mm:ss');
    var durl = window.location.origin + '/static/wps/newfile.';
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
  }
  var openType = options['openType'] || options['_openType'],
    protectTypes = [0, 1, 2, 3];
  var isReadonly = !!(openType && protectTypes.indexOf(openType.protectType) > -1);
  if (!bNew && 1 == attachOCX.isLocalFileOpen(localPath)) {
    // 编辑，且文件已打开，那就不需要再进行打开的操作了
    throw new Error('已打开相同的OA文件，请关闭之前的文件，再次打开。');
  }
  var bOpen = attachOCX.openLocalFile(localPath, isReadonly === true ? 0 : 1, -1);
  if (!bOpen || !fileName) {
    throw new Error('附件打开失败');
  } else if (isReadonly) {
    return; // 只读打开
  }
  // attachOCX.focusLocalFile(localPath);
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
  function uploadFile(tipText) {
    $app.$confirm({
      content: tipText || '编辑完成并关闭Office后返回该页面。点击“确定”上传OA，是否上传？',
      onOk() {
        var bFIleOpen = attachOCX.isLocalFileOpen(localPath);
        var needUpload = true;
        if (needUpload && bFIleOpen) {
          uploadFile('请先关闭Office。关闭后点击“确定”，“取消”则将忽略上传');
          return;
        }

        if (needUpload && !bFIleOpen) {
          // 上传
          setTimeout(function () {
            try {
              var source = '编辑';
              if (typeof options.uploadPath === 'string') {
                var reg = new RegExp('(^|&|\\?)source=([^&]*)(&|$)');
                var values = options.uploadPath.match(reg);
                if (values != null && values[2]) {
                  source = decodeURIComponent(values[2]);
                }
              }
              var fileParams = {
                source: source,
                fileId: docId,
                bsMode: options.bsMode,
                fileName: encodeURI(fileName)
              };
              var msg = attachOCX.uploadFile(localPath, 'fileParams://' + JSON.stringify(fileParams), 1000000000, false);
              // console.log(msg);// {success:1, fileID:"aed5db9b20194c62b1dbad2cf623353c"}
              if (msg && msg.indexOf('{') > -1) {
                msg = eval('(' + msg + ')');
              }
              if (msg.fileID) {
                _this.loadFileByFileID(msg.fileID, function (result) {
                  delete _this.localPaths[docId];
                  if (typeof options.success === 'function') {
                    options.success(result);
                  }
                });
              } else {
                if (typeof options.error === 'function') {
                  options.error('文件上传失败');
                }
              }
            } catch (ex) {
              console.error(ex);
              if (typeof options.error === 'function') {
                options.error(ex.message || '附件保存失败');
              }
            } finally {
            }
          }, 0);
        } else if (bNew && !bFIleOpen) {
          // 新增的，就删除掉
          attachOCX.deleteLocalFile(localPath);
        }
      },

      onCancel() {
        // 取消，还未关闭的话，提示重新编辑
        var bFIleOpen = attachOCX.isLocalFileOpen(localPath);
        if (bFIleOpen) {
          $app.$message.info('点击“编辑”会重新提示上传');
        }
      }
    });
  }
  // 异步执行（Office获取焦点）
  setTimeout(uploadFile, 2000);
};

WellOffice.prototype.pasteFile = function (options) {
  var attachOCX = this.attachOCX;
  var paths = attachOCX.getLocalCopyFiles(),
    localFiles = [];
  // console.log(paths);// C:\Users\admin\Desktop\正文.doc|C:\Users\admin\Desktop\谷物.doc|
  if (!paths) {
    alert('没有粘贴附件，请先复制附件');
    return;
  }
  paths = paths.split('|');
  for (var i = 0; i < paths.length; i++) {
    var path = paths[i].trim();
    if (!path) {
      continue;
    }
    var fileName = path.substr(path.lastIndexOf('\\') + 1);
    if (fileName.length > 80) {
      alert('附件文件名称超长(不得超过80个字)[' + fileName + ']');
      return;
    } else if (/[\/\\:\*\?"%<>\|\n]/gi.test(fileName)) {
      alert('文件名不得含有字符:"/ \\ : * ? " % < > |"及"换行符"');
      return;
    } else if (attachOCX.isLocalFileOpen(path)) {
      alert('文件[' + fileName + ']占用，请先解除占用后重试');
      return;
    }
    localFiles.push({
      path: path,
      name: fileName,
      size: attachOCX.getLocalFileSize(path)
    });
  }
  var _this = this,
    continuePaste = function () {
      for (var i = 0; i < localFiles.length; i++) {
        var fileObj = localFiles[i];
        var path = fileObj.path;
        var fileParams = {
          source: '粘贴附件',
          fileName: encodeURI(fileObj.name)
        };
        var msg = attachOCX.uploadFile(path, 'fileParams://' + JSON.stringify(fileParams), 1000000000, false);
        // console.log(msg);// {success:1, fileID:"aed5db9b20194c62b1dbad2cf623353c"}
        if (msg && msg.indexOf('{') > -1) {
          msg = eval('(' + msg + ')');
        }
        if (msg.fileID) {
          _this.loadFileByFileID(msg.fileID, function (fileResult) {
            if (typeof options.success === 'function') {
              options.success(fileResult);
            }
          });
        }
      }
    };
  if (options.validate) {
    if (typeof options.validate == 'function') {
      let result = options.validate(localFiles);
      if (false === result) {
        return;
      } else if (result != undefined && typeof result.then == 'function') {
        result.then(function () { continuePaste() }).catch(function (e) {
        })
        return;
      }
    }
  }
  continuePaste();


};

WellOffice.prototype.saveAsFile = function (options) {
  var attachOCX = this.attachOCX;
  var _this = this;
  function downloadFromRemoteForSaveAs(localPath, options) {
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
    const loadingKey = 'saveAsFile';
    $app.$message.loading({ content: '正在下载中', key: loadingKey, duration: 0 });
    _this.addEvent(attachOCX, 'downloadFile', function (msg) {
      var isDownloadOk = msg != '下载成功' ? false : true;
      try {
        if (isDownloadOk) {
          $app.$message.success({ content: '下载成功!', key: loadingKey });
          options.success && options.success(msg);
        } else {
          $app.$message.error({ content: '下载失败!', key: loadingKey });
          options.error && options.error(msg);
        }
      } finally {
        options.complete && options.complete(isDownloadOk, msg);
      }
    });
    var psFileModified = dateFormat(new Date(), 'yyyy-MM-dd HH:mm:ss');
    options.url = serverlUrl(options.url);
    attachOCX.downloadFile(options.url, psFileModified, localPath, -1, true);
  }
  this.addEvent(attachOCX, 'selectLocalSavePath', function (path) {
    if (!path) {
      return;
    }
    downloadFromRemoteForSaveAs(path, options);
  });
  attachOCX.selectLocalSavePath(options.filename);
};

WellOffice.prototype.queryLocalFileHistory = function (options) {
  var result = [],
    docId,
    subDirs,
    attachOCX = this.attachOCX;
  if ((docId = options.origId || options.docId)) {
    var localFolder = this.tempLocalPath + docId;
    var subDirStr = attachOCX.getLocalDir(localFolder);
    if (subDirStr && (subDirs = subDirStr.split('|')).length) {
      for (var i = 0; i < subDirs.length; i++) {
        var fileName = attachOCX.getLocalDir(subDirs[i]);
        if (fileName) {
          var fileName = fileName.split('|')[0];
          if (typeof fileName === 'string' && fileName.substr(fileName.lastIndexOf('.')) === '.tmp') {
            continue;
          }
          var filePath = subDirs[i] + fileName;
          var timestampFolder = subDirs[i].replace(localFolder, '').replace(/\\/g, '');
          var timestamp = dateFormat(new Date(parseInt(timestampFolder, 10)), 'yyyy-MM-dd HH:mm:ss');
          result.push({
            source: 'local',
            url: filePath,
            name: fileName,
            size: attachOCX.getLocalFileSize(filePath),
            timestamp: timestamp
          });
        }
      }
    }
  }
  result.sort(function (a, b) {
    return a.timestamp > b.timestamp ? -1 : 1;
  });
  options.success && options.success(result);
  return result;
};

WellOffice.prototype.openDoc = function (options) {
  options.fileExt = 'doc';
  this.openFile(options);
};

WellOffice.prototype.openET = function (options) {
  options.fileExt = 'xls';
  this.openFile(options);
};

WellOffice.prototype.openWpp = function (options) {
  options.fileExt = 'ppt';
  this.openFile(options);
};

WellOffice.prototype.openOfd = function (options) {
  options.fileExt = 'ofd';
  this.openFile(options);
};

WellOffice.prototype.addEvent = function (obj, name, func) {
  if (this.ocxeventmap[name]) {
    this.removeEvent(this.ocxeventmap[name].obj, name, this.ocxeventmap[name].func);
    delete this.ocxeventmap[name];
  }

  this.ocxeventmap[name] = {};
  this.ocxeventmap[name].func = func;
  this.ocxeventmap[name].obj = obj;
  if (obj.attachEvent) {
    obj.attachEvent('on' + name, func);
  } else {
    obj.addEventListener(name, func, false);
  }
};
WellOffice.prototype.removeEvent = function (obj, name, func) {
  if (obj.detachEvent) {
    obj.detachEvent('on' + name, func);
  } else {
    obj.removeEventListener(name, func, false);
  }
};

const WpsOffice = function (options) {
  require('./wps_sdk');
  this.WpsInvoke = window.WpsInvoke;
  if (this.WpsInvoke == undefined) {
    throw new Error('无法识别的WPS版本API');
  }
  this.params = {};
  this.plugins = [];
  this.options = options;
  this.proxyWpsApi();
  this.init();
};

WpsOffice.prototype.init = function () {
  this.checkWPSProtocolByHttp();
};

WpsOffice.prototype.proxyWpsApi = function () {
  var _this = this;
  var jsApis = {};
  jsApis[this.WpsInvoke.ClientType.et] = ['NewDoc', 'OpenDoc', 'OnlineEditDoc'];
  jsApis[this.WpsInvoke.ClientType.wpp] = ['NewDoc', 'OpenDoc', 'OnlineEditDoc'];
  jsApis[this.WpsInvoke.ClientType.wps] = [
    'NewDoc',
    'OpenDoc',
    'OnlineEditDoc',
    'UseTemplate',
    'InsertRedHead',
    'ExitWPS',
    'GetDocStatus',
    'NewOfficialDocument'
  ];

  function getPluginNameByType(type) {
    var pluginName;
    if (type === this.WpsInvoke.ClientType.wps) {
      pluginName = 'WpsOAAssist';
    } else if (type === this.WpsInvoke.ClientType.et) {
      pluginName = 'EtOAAssist';
    } else if (type === this.WpsInvoke.ClientType.wpp) {
      pluginName = 'WppOAAssist';
    }
    return pluginName;
  }

  function buildJsApi(name, type) {
    return function (params, funcs) {
      var funcName = name;
      var info = { funcs: [{}] };
      info.funcs[0][funcName] = params;
      if (Array.isArray(funcs) && funcs.length > 0) {
        info.funcs = info.funcs.concat(funcs);
      }

      return _this.invokeWpsApiAsHttp(type, getPluginNameByType.call(_this, type), 'dispatcher', info);
    };
  }
  for (var type in jsApis) {
    if (_this[type] == undefined) {
      _this[type] = {};
    }
    for (var i = 0, len = jsApis[type].length; i < len; i++) {
      _this[type][jsApis[type][i]] = buildJsApi(jsApis[type][i], type);
    }
  }

  // 兼容旧jsapi处理
  this.newDoc = this.wps.NewDoc;
  this.openDoc = this.wps.OpenDoc;
  this.fillTemplate = this.wps.UseTemplate;
  this.onlineEditDoc = this.wps.OnlineEditDoc;
  this.openET = this.et.OpenDoc;
  this.openWpp = this.wpp.OpenDoc;
  this.insertRedHeadDocFromWeb = function (params, templateURL, replaceBookMark) {
    params.insertFileUrl = templateURL;
    params.bkInsertFile = replaceBookMark;
    return _this.wps.OpenDoc.call(this, params);
  };
};

WpsOffice.prototype.invokeWpsApiAsHttp = function (clientType, jsApiName, func, info) {
  let args = arguments,
    _this = this;
  this.ws(function (sid) {
    for (let i = 0, len = info.funcs.length; i < len; i++) {
      var name, params;
      let internalFuncObj = info.funcs[i];
      console.log('invoke wps api : ', internalFuncObj);
      for (name in internalFuncObj) {
        // 空值处理，不允许传空值
        params = internalFuncObj[name] || (internalFuncObj[name] = {});
        params.docId = params.docId || generateId();
        _this.params[params.docId] = params; // 保存回调
        if (params.fileName && params.fileName.indexOf('/') === 0) {
          params.fileName += (params.fileName.indexOf('?') > -1 ? '&' : '?') + 'sId=' + sid;
          params.fileName = serverlUrl(params.fileName);
        }
        // 上传设置会话
        if (params.uploadPath) {
          params.uploadPath += (params.uploadPath.indexOf('?') > -1 ? '&' : '?') + 'sId=' + sid;
          params.uploadPath += params.uploadPath.indexOf('fileID=') > -1 ? '' : '&fileID=' + params.docId;
          params.uploadPath += params.uploadPath.indexOf('bsMode=') > -1 ? '' : '&bsMode=' + params.bsMode || '';
          params.uploadPath = serverlUrl(params.uploadPath);
        }
        // 套红设置会话
        if (params.insertFileUrl) {
          params.insertFileUrl = params.insertFileUrl.replace('/repository/file/mongo', '/office/wps');
          params.insertFileUrl = params.insertFileUrl + (params.insertFileUrl.indexOf('?') > -1 ? '&' : '?') + 'sId=' + sid;
          params.insertFileUrl = serverlUrl(params.insertFileUrl);
        }
        var openType = params.openType,
          protectTypes = [0, 1, 2, 3];
        if (openType && openType.password == null && protectTypes.indexOf(openType.protectType) != -1) {
          // 123456 保护模式需要设置密码
          openType.password = '________________';
        }
      }
    }
    this.WpsInvoke.InvokeAsHttp.apply(this, args);
  });
};

WpsOffice.prototype.ws = function (callback) {
  var _this = this;
  if (this.wsTransport == null || typeof this.wsTransport === 'undefined') {
    // 建立webSocket连接
    if ('WebSocket' in window) {
      var wsUrl = null,
        backendUrl = getBackendUrl();
      if (backendUrl) {
        wsUrl = serverlUrl('newFileFromWPS?');
        wsUrl = wsUrl.replace('https://', 'wss://').replace('http://', 'ws://');
      }
      var transport = (this.wsTransport = new WebSocket(wsUrl));
      transport.onopen = function (event) {
        console.log('webSocket连接建立');
      };
      transport.onmessage = function (event) {
        var result = JSON.parse(event.data);
        if (result.code === 0 && result.msg) {
          var params = _this.params[result.msg] || {};
          if (result.success && params.success) {
            params.success.apply(_this, [result]);
          } else if (result.success === false || params.error) {
            params.error.apply(_this, [result]);
          }
        } else {
          _this.wsConnected = true;
          _this.sessionid = result.data;
          callback.call(_this, result.data);
        }
      };
      transport.onerror = function (event) {
        console.log('webSocket连接出错');
      };
      transport.onclose = function (event) {
        _this.wsTransport = null;
        console.log('webSocket连接关闭');
      };

      setInterval(function () {
        _this.wsTransport.send('ping');
      }, 30000);
    }
  } else {
    callback.call(this, _this.sessionid);
  }
};

WpsOffice.prototype.pluginInstalled = function (plugin) {
  for (let i = 0, len = this.plugins.length; i < len; i++) {
    if (plugin.url === this.plugins[i].url && this.plugins[i].success) {
      return true;
    }
  }
  return false;
};

WpsOffice.prototype.instanceWPSPluginIfNeed = function (callback) {
  let _this = this;
  xhr.get(
    '/static/wps/oaassist/jsplugins.xml',
    {
      headers: {
        'Content-Type': 'application/xml'
      }
    },
    function (err, xmlResp) {
      let xmldoc = xmlResp.rawRequest.responseXML;
      xhr.get('http://127.0.0.1:58890/publishlist', { json: true }, function (err, resp) {
        let published = {};
        if (resp.body) {
          for (let i = 0, len = resp.body.length; i < len; i++) {
            published[resp.body[i].name + resp.body[i].tagname] = resp.body[i].enable;
          }
        }
        let jspluginsNode = xmldoc.querySelector('jsplugins');
        let jspluginonlineNodes = jspluginsNode.querySelectorAll('jspluginonline');
        let jspluginNodes = jspluginsNode.querySelectorAll('jsplugin');
        let nodes = [].concat(jspluginonlineNodes, jspluginNodes);
        let toalPluginNums = 0,
          installedNums = 0;
        for (let i = 0, len = nodes.length; i < len; i++) {
          for (let j = 0, jlen = nodes[i].length; j < jlen; j++) {
            let p = {
              name: nodes[i][j].getAttribute('name'),
              addonType: nodes[i][j].getAttribute('type'),
              online: nodes[i][j].nodeName === 'jspluginonline',
              url: nodes[i][j].getAttribute('url').replace('${requestURL}', window.location.origin)
            };
            toalPluginNums++;
            if (published[p.name + nodes[i][j].nodeName] !== 'true' && !_this.pluginInstalled(p)) {
              deployPlugins(p);
            } else {
              installedNums++;
            }
          }
        }
        if (toalPluginNums === installedNums && toalPluginNums != 0) {
          callback.call(this);
        }
        function deployPlugins(plugin) {
          var cmd = Object.assign({ cmd: 'enable' }, plugin);
          xhr.post('http://127.0.0.1:58890/deployaddons/runParams', {
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded'
            },
            beforeSend: function (xhr) { },
            // json: true,
            body: WpsInvoke.encode(JSON.stringify(cmd))
          }, function (err, resp) {
            if (err) {
              console.error('wps deploy plugin fail: ', err);
            } else {
              _this.plugins.push(plugin);
              plugin.success = true;
              installedNums++;
              console.log('wps deploy plugin success', plugin);
            }
            if (toalPluginNums === installedNums) {
              callback.call(this);
            }
          });
        }
      });
    }
  );
};

//检测本地是否支持WPS协议
WpsOffice.prototype.checkWPSProtocolByHttp = function (retry) {
  // var xhrreq = new XMLHttpRequest();
  // xhrreq.open('GET', 'http://127.0.0.1:58890/kso/protocolcheck?protocol=KsoWebStartupWPS', true);
  // xhrreq.onreadystatechange = function () {
  //   if (xhrreq.readyState == 4) {
  //     console.log(xhrreq.responseText);
  //   }
  // };
  // xhrreq.send('');
  var _this = this;
  xhr.get('http://127.0.0.1:58890/kso/protocolcheck?protocol=KsoWebStartupWPS', { json: true }, function (err, resp) {
    console.log('检测本地是否支持WPS协议，返回: ', resp.body);
    if (resp.body && resp.body.result === 'true' && !_this.inited) {
      _this.instanceWPSPluginIfNeed(function () {
        for (let i = 0, len = _this.plugins.length; i < len; i++) {
          if (!_this.plugins[i].success) {
            return;
          }
        }

        _this.inited = true;
      });
    } else if (err) {
      console.error('检测本地是否支持WPS协议，返回: ', err);
      if (retry == undefined) {
        window.location.href = 'ksowpscloudsvr://start=RelayHttpServer';
        setTimeout(function () {
          _this.checkWPSProtocolByHttp(true);
        }, 3000);
      } else {
        setTimeout(function () {
          _this.checkWPSProtocolByHttp(true);
        }, 1000);
      }
    }
  });
};

const OfficeApiFactory = function (options) {
  let _this = this;
  _this.api = {};
  _this.options = options;
  clientCommonApi.getSystemParamValue('app.fileupload.welloffice.enable', function (wellofficeEnable) {
    if (wellofficeEnable === 'true' || wellofficeEnable === 'auto') {
      _this.api = new WellOffice();
    } else {
      // wps-sdk
      _this.api = new WpsOffice();
    }
    if (options.callback && typeof options.callback == 'function') {
      options.callback();
    }
  });
};

OfficeApiFactory.prototype.openET = function (options) {
  this.ready(function () {
    this.api.openET(options);
    if (this.options.prepared && typeof this.options.prepared == 'function') {
      this.options.prepared();
    }
  });
};
OfficeApiFactory.prototype.openDoc = function (options) {
  this.ready(function () {
    this.api.openDoc(options);
    if (this.options.prepared && typeof this.options.prepared == 'function') {
      this.options.prepared();
    }
  });
};

OfficeApiFactory.prototype.ready = function (callback) {
  var _this = this;
  if (this.api.inited) {
    callback.call(this);
  } else {
    setTimeout(function () {
      _this.ready(callback);
    }, 1000);
  }
};

export const OfficeApi = OfficeApiFactory;
