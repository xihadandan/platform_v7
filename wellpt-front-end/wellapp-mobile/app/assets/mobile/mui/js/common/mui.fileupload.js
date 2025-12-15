(function (factory) {
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['mui'], factory);
  } else {
    // Browser globals
    factory(mui);
  }
})(function ($) {
  /**
   * 事件处理类，可以独立使用，也可以扩展给对象使用。 webupload mediator
   */
  var slice = [].slice,
    separator = /\s+/;
  $.grep = function (elems, callback, inv) {
    var retVal,
      ret = [],
      i = 0,
      length = elems.length;
    inv = !!inv;

    // Go through the array, only saving the items
    // that pass the validator function
    for (; i < length; i++) {
      retVal = !!callback(elems[i], i);
      if (inv !== retVal) {
        ret.push(elems[i]);
      }
    }
    return ret;
  };
  // 根据条件过滤出事件handlers.
  function findHandlers(arr, name, callback, context) {
    return $.grep(arr, function (handler) {
      return (
        handler &&
        (!name || handler.e === name) &&
        (!callback || handler.cb === callback || handler.cb._cb === callback) &&
        (!context || handler.ctx === context)
      );
    });
  }

  function eachEvent(events, callback, iterator) {
    // 不支持对象，只支持多个event用空格隔开
    $.each((events || '').split(separator), function (_, key) {
      iterator(key, callback);
    });
  }

  function triggerHanders(events, args) {
    var stoped = false,
      i = -1,
      len = events.length,
      handler;

    while (++i < len) {
      handler = events[i];

      if (handler.cb.apply(handler.ctx2, args) === false) {
        stoped = true;
        break;
      }
    }

    return !stoped;
  }
  var EventClass = ($.EventClass = $.Class.extend({
    /**
     * 绑定事件。
     *
     * `callback`方法在执行时，arguments将会来源于trigger的时候携带的参数。如
     * ```javascript var obj = {}; // 使得obj有事件行为 Mediator.installTo(
     * obj );
     *
     * obj.on( 'testa', function( arg1, arg2 ) { console.log( arg1,
     * arg2 ); // => 'arg1', 'arg2' });
     *
     * obj.trigger( 'testa', 'arg1', 'arg2' ); ```
     *
     * 如果`callback`中，某一个方法`return false`了，则后续的其他`callback`都不会被执行到。
     * 切会影响到`trigger`方法的返回值，为`false`。
     *
     * `on`还可以用来添加一个特殊事件`all`,
     * 这样所有的事件触发都会响应到。同时此类`callback`中的arguments有一个不同处，
     * 就是第一个参数为`type`，记录当前是什么事件在触发。此类`callback`的优先级比脚低，会再正常`callback`执行完后触发。
     * ```javascript obj.on( 'all', function( type, arg1, arg2 ) {
     * console.log( type, arg1, arg2 ); // => 'testa', 'arg1',
     * 'arg2' }); ```
     *
     * @method on
     * @grammar on( name, callback[, context] ) => self
     * @param {String}
     *            name 事件名，支持多个事件用空格隔开
     * @param {Function}
     *            callback 事件处理器
     * @param {Object}
     *            [context] 事件处理器的上下文。
     * @return {self} 返回自身，方便链式
     * @chainable
     * @class Mediator
     */
    on: function (name, callback, context) {
      var me = this,
        set;

      if (!callback) {
        return this;
      }

      set = this._events || (this._events = []);

      eachEvent(name, callback, function (name, callback) {
        var handler = {
          e: name
        };

        handler.cb = callback;
        handler.ctx = context;
        handler.ctx2 = context || me;
        handler.id = set.length;

        set.push(handler);
      });

      return this;
    },

    /**
     * 绑定事件，且当handler执行完后，自动解除绑定。
     *
     * @method once
     * @grammar once( name, callback[, context] ) => self
     * @param {String}
     *            name 事件名
     * @param {Function}
     *            callback 事件处理器
     * @param {Object}
     *            [context] 事件处理器的上下文。
     * @return {self} 返回自身，方便链式
     * @chainable
     */
    once: function (name, callback, context) {
      var me = this;

      if (!callback) {
        return me;
      }

      eachEvent(name, callback, function (name, callback) {
        var once = function () {
          me.off(name, once);
          return callback.apply(context || me, arguments);
        };

        once._cb = callback;
        me.on(name, once, context);
      });

      return me;
    },

    /**
     * 解除事件绑定
     *
     * @method off
     * @grammar off( [name[, callback[, context] ] ] ) => self
     * @param {String}
     *            [name] 事件名
     * @param {Function}
     *            [callback] 事件处理器
     * @param {Object}
     *            [context] 事件处理器的上下文。
     * @return {self} 返回自身，方便链式
     * @chainable
     */
    off: function (name, cb, ctx) {
      var events = this._events;

      if (!events) {
        return this;
      }

      if (!name && !cb && !ctx) {
        this._events = [];
        return this;
      }

      eachEvent(name, cb, function (name, cb) {
        $.each(findHandlers(events, name, cb, ctx), function () {
          delete events[this.id];
        });
      });

      return this;
    },

    /**
     * 触发事件
     *
     * @method trigger
     * @grammar trigger( name[, args...] ) => self
     * @param {String}
     *            type 事件名
     * @param {*}
     *            [...] 任意参数
     * @return {Boolean} 如果handler中return false了，则返回false, 否则返回true
     */
    trigger: function (type) {
      var args, events, allEvents;

      if (!this._events || !type) {
        return this;
      }
      args = slice.call(arguments, 1);
      events = findHandlers(this._events, type);
      allEvents = findHandlers(this._events, 'all');

      return triggerHanders(events, args) && triggerHanders(allEvents, arguments);
    }
  }));

  // 创建 DOM
  $.dom = function (str) {
    if (typeof str !== 'string') {
      if (str instanceof Array || (str[0] && str.length)) {
        return [].slice.call(str);
      } else {
        return [str];
      }
    }
    if (!$.__create_dom_div__) {
      $.__create_dom_div__ = document.createElement('div');
    }
    $.__create_dom_div__.innerHTML = str;
    return [].slice.call($.__create_dom_div__.childNodes);
  };

  $.support = {
    xhrFormDataMuiFileUpload: !!window.FormData
  };
  var rExt = /\.\w+$/;
  var ATTACH_BTN_CLASS = 'attach-picker';
  var MONGO_REP = '/proxy-repository/repository/file/mongo';
  var SAVE_FILE = MONGO_REP + '/savefiles';
  var DELETE_FILE = MONGO_REP + '/deleteFile?fileID=';
  var DOWNLOAD_FILE = MONGO_REP + '/download?fileID=';
  var DOWNLOAD_ALL_FILES = MONGO_REP + '/downAllFiles?fileIDs=';
  var LOAD_FILE_FROM_FOLDER = MONGO_REP + '/getNonioFilesFromFolder';
  var endsWith = function (searchString, position) {
    var subjectString = this.toString();
    if (typeof position !== 'number' || !isFinite(position) || Math.floor(position) !== position || position > subjectString.length) {
      position = subjectString.length;
    }
    position -= searchString.length;
    var lastIndex = subjectString.lastIndexOf(searchString, position);
    return lastIndex !== -1 && lastIndex === position;
  };
  var MuiFileUpload = ($.MuiFileUpload = $.EventClass.extend({
    init: function (holder, options) {
      var self = this,
        option,
        label;
      self.holder = holder = $(holder)[0];
      if (!holder) {
        throw '构造 MuiFileUpload 时找不到元素';
      }
      self.options = options = options || {};
      self.setAccept(options.accept);
      self.setMultiple(options.multiple);
      self.setReadonly(options.readonly);
      self.setSignature(options.signature);
      self.setParamName(options.paramName);
      self.setAutoUpload(options.autoUpload);
      self.render();
      self.bindEvent();
      self.mask = $.createMask();
      if (!options.files && $.isPlainObject(options.initXHRData)) {
        options.files = self.loadFilesFormDb(undefined);
      } else if (!options.files && options.fileIds) {
        options.files = self.loadFilesByFileIds(undefined);
      }
      self.setItems(options.files, false);
      // 存储上传对象
      var id = 'MuiFileUpload_' + ++$.uuid;
      MuiFileUpload.MuiFileUploadObj[id] = self;
      self.holder.setAttribute('data-MuiFileUpload', id);
    },
    setAutoUpload: function (autoUpload) {
      this.options.autoUpload = !!autoUpload;
    },
    isAutoUpload: function () {
      return this.options.autoUpload;
    },
    setParamName: function (paramName) {
      var self = this;
      if (!paramName) {
        paramName = [self.holder.getAttribute('name') || 'files[]'];
      }
      self.options.paramName = $.isArray(paramName) ? paramName : [paramName];
    },
    getParamName: function () {
      return this.options.paramName;
    },
    setAccept: function (accept) {
      var self = this;
      self.options.accept = accept;
      if (typeof accept === 'string') {
        self.holder.setAttribute('accept', accept);
      } else {
        self.holder.removeAttribute('accept');
      }
    },
    getAccept: function () {
      return this.options.accept;
    },
    setMultiple: function (multiple) {
      var self = this;
      self.options.multiple = !!multiple;
      if (self.isMultiple()) {
        self.holder.setAttribute('multiple', 'multiple');
      } else {
        self.holder.removeAttribute('multiple');
      }
    },
    isMultiple: function () {
      return this.options.multiple;
    },
    setEditable: function (editable) {
      var self = this;
      self.options.editable = !!editable;
      if (self.isEditable()) {
        self.holder.removeAttribute('disabled');
      } else {
        self.holder.setAttribute('disabled', 'disabled');
      }
    },
    isEditable: function () {
      return this.options.editable;
    },
    setRequired: function (required) {
      this.options.required = !!required;
    },
    isRequried: function () {
      return this.options.required;
    },
    setReadonly: function (readonly) {
      var self = this;
      self.options.readonly = !!readonly;
      if (self.isReadonly()) {
        self.holder.setAttribute('readonly', 'readonly');
      } else {
        self.holder.removeAttribute('readonly');
      }
    },
    isReadonly: function () {
      return this.options.readonly;
    },
    setSignature: function (signature) {
      var self = this;
      self.options.signature = !!signature;
      if (self.isSignature()) {
        // TODO
      } else {
      }
    },
    isSignature: function () {
      return this.options.signature;
    },
    setItems: function (files, append, itemFormat) {
      var self = this;
      files = files || [];
      self.renderItems(files, append && self.isMultiple(), itemFormat);
      if (append && self.isMultiple()) {
        self.files = self.files.concat(files);
      } else {
        self.files = files;
      }
      self.trigger('afterSetValue', files, append, itemFormat);
    },
    getItems: function () {
      var self = this;
      var values = $.extend(true, [], self.files);
      $.each(values, function (i, value) {
        delete value.isNew;
      });
      return values;
    },
    render: function () {
      var self = this,
        label;
      label = self.label = document.createElement('label');
      if (self.holder.id) {
        label.setAttribute('for', self.holder.id);
      }
      var labelText = self.options.lableText;
      label.innerHTML = typeof labelText === 'undefined' || labelText == null ? '添加附件' : self.options.lableText;
      label.setAttribute('class', self.holder.getAttribute('class')); // ATTACH_BTN_CLASS
      var holderStyle = 'opacity: 0;position: absolute;z-index: 999'; // holderStyle
      self.holder.setAttribute('style', holderStyle);
      // self.holder.parentNode.appendChild(label);
      self.holder.parentNode.insertBefore(label, self.holder.nextSibling);
      label.insertBefore(self.holder, label.firstChild);
      // debugger
      // holderStyle += ";top:" + label.offsetTop + "px";
      // holderStyle += ";left:" + label.offsetLeft + "px";
      // holderStyle += ";width:" + label.offsetWidth + "px";
      // holderStyle += ";height:" + label.offsetHeight + "px";
      // self.holder.setAttribute("style", holderStyle);
    },
    renderItems: function (files, append, itemFormat) {
      var self = this;
      if (!append) {
        self.renderDelItems(self.files);
      }
      self.renderAddItems(files, itemFormat);
    },
    renderDelItems: function (files) {
      var self = this;
      console.log('移除文件：' + (files && files.join ? self.formatItems(files).join(',') : files));
      return files;
    },
    renderAddItems: function (files, itemFormat) {
      var self = this;
      var items = self.formatItems(files, itemFormat);
      console.log('添加文件：' + items.join(','));
      return items;
    },
    formatItems: function (files, itemFormat) {
      var self = this,
        items = [];
      $.each(files, function (i, file) {
        items.push(self.formatItem(file, itemFormat));
      });
      return items;
    },
    /**
     * 格式化文件大小, 输出成带单位的字符串
     *
     * @method formatSize
     * @grammar Base.formatSize( size ) => String
     * @grammar Base.formatSize( size, pointLength ) => String
     * @grammar Base.formatSize( size, pointLength, units ) =>
     *          String
     * @param {Number}
     *            size 文件大小
     * @param {Number}
     *            [pointLength=2] 精确到的小数点数。
     * @param {Array}
     *            [units=[ 'B', 'K', 'M', 'G', 'TB' ]]
     *            单位数组。从字节，到千字节，一直往上指定。如果单位数组里面只指定了到了K(千字节)，同时文件大小大于M,
     *            此方法的输出将还是显示成多少K.
     */
    formatSize: function (size, pointLength, units) {
      var unit;
      units = units || ['B', 'K', 'M', 'G', 'TB'];
      while ((unit = units.shift()) && size > 1024) {
        size = size / 1024;
      }
      return (unit === 'B' ? size : size.toFixed(pointLength || 2)) + unit;
    },
    imgext: 'png,bmp,jpg,tiff,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,jpeg'.split(','),
    isImage: function (file) {
      var self = this,
        fileName;
      if (file && file.fileName) {
        var imgexts = self.imgext;
        fileName = file.fileName.toLowerCase();
        for (var i = 0; i < imgexts.length; i++) {
          if (endsWith.call(fileName, imgexts[i])) {
            return true;
          }
        }
      }
      return false;
    },
    formatItem: function (file, itemFormat) {
      var self = this;
      itemFormat = itemFormat || self.options.itemFormat;
      if (typeof itemFormat === 'function') {
        return itemFormat.apply(this, arguments);
      }
      return file.toString();
    },
    bindEvent: function () {
      var self = this;
      self.holder.onchange = function (event) {
        var files = event.target.files || [];
        self._onchange(event, files);
        // TODO remove setTimeout
        setTimeout(function () {
          event.target.value = '';
        }, 1000);
      };
      self.on('success', function (result, xhr, settings) {
        var files = result.data;
        if (result.success === true && files != null) {
          var dbFiles = self._processDbFiles(files);
          if (self.isAutoUpload()) {
            self.setItems(dbFiles, true);
          }
        }
      });
    },
    _onchange: function (event, files) {
      var self = this,
        items = [];
      if (files && files.length > 0) {
        $.each(files, function (i, file) {
          file.isNew = true;
          file.fileName = file.name;
          file.fileSize = file.size;
          file.contentType = file.type;
          items.push(file);
        });
        self.options.files = items;
        if (self.isAutoUpload()) {
          self.upload(event);
        } else {
          self.setItems(items, true);
        }
      }
      self.trigger('change', event, items);
    },
    getFileExt: function (file) {
      return file.fileName && rExt.exec(file.fileName) ? RegExp.$1 : '';
    },
    getCtx: function () {
      var self = this;
      if (self.options.baseurl) {
        return self.options.baseurl;
      } else if (window.ctx) {
        return window.ctx;
      }
      return '';
    },
    getSaveFileUrl: function () {
      return SAVE_FILE;
    },
    getPreviewObj: function (file) {
      var self = this,
        fileObj;
      if (file && file.fileName && file.fileSize) {
        var fileExt = '';
        var fileId = file.fileID;
        var fileName = file.fileName;
        var image = self.isImage(file);
        var fileSize = file.fileSize || 0;
        var downloadUrl = self.getCtx() + DOWNLOAD_FILE + fileId;
        if (fileName.lastIndexOf('.') > 0) {
          fileExt = fileName.substring(fileName.lastIndexOf('.') + 1);
        }
        fileObj = {
          image: image,
          fileId: fileId,
          fileExt: fileExt,
          fileSize: fileSize,
          fileName: fileName,
          fileUrl: downloadUrl
        };
      }
      return fileObj;
    },
    _submitForm: function (url, params) {
      /*
       * var form = document.createElement("form");
       * form.setAttribute("style","display:none");
       * form.setAttribute("target","_black");
       * form.setAttribute("method","post");
       * form.setAttribute("action", url);
       * document.body.appendChild(form); form.submit(); // 提交表单
       * form.parentNode.removeChild(form);
       */
      var self = this,
        fileObj;
      if (window.WellAdapter && params && params.fileId) {
        var file = self.getFile(params.fileId);
        if (file && file.fileName && file.fileSize) {
          var fileExt = '';
          var fileId = file.fileID;
          var fileName = file.fileName;
          var image = self.isImage(file);
          var fileSize = file.fileSize || 0;
          var fileDownloadUrl = location.origin + url;
          if (fileName.lastIndexOf('.') > 0) {
            fileExt = fileName.substring(fileName.lastIndexOf('.') + 1);
          }
          fileObj = {
            image: image,
            fileId: fileId,
            fileExt: fileExt,
            fileSize: fileSize,
            fileName: fileName,
            fileUrl: fileDownloadUrl
          };
          // break
        }
        // continue;
      }
      return $.trigger(document.body, 'file.show', {
        fileUrl: url,
        fileObj: fileObj
      });
    },
    downFile: function (fileId, fileName) {
      var self = this;
      var downloadUrl = self.getCtx() + DOWNLOAD_FILE + fileId;
      if (fileName) {
        downloadUrl = downloadUrl + '&filename=' + fileName;
      }
      self._submitForm(downloadUrl, {
        fileId: fileId,
        fileName: fileName
      });
    },
    downAllFile: function (files) {
      var self = this;
      self._submitForm(self.getCtx() + DOWNLOAD_ALL_FILES + encodeURI(JSON.stringify(files)));
    },
    deleteFile: function (file) {
      var result,
        that = this;
      if (file.fileID && (that._isInstanceOf('File', file) || that._isInstanceOf('Blob', file))) {
        $.ajax({
          type: 'post',
          async: false,
          url: that.getCtx() + DELETE_FILE + file.fileID,
          success: function (data) {
            result = true;
          },
          error: function (data) {
            result = false;
          }
        });
      }
      return result;
    },
    deleteFileItem: function (file) {
      var _this = this;
      for (var index = 0; index < _this.files.length; index++) {
        if (_this.files[index].fileID == file.fileID) {
          _this.files.splice(index, 1);
        }
      }
      var fileInfo = {};
      $.extend(fileInfo, file);
      fileInfo.filename = file.fileName;
      _this.deleteFile(fileInfo);
      _this.trigger('deleteOkCallback', fileInfo); // 删除成功回调
    },
    getLoadFileUrl: function () {
      return this.getCtx() + LOAD_FILE_FROM_FOLDER;
    },
    loadFilesFormDb: function (append, itemFormat) {
      var self = this;
      var dbFiles = [];
      $.ajax({
        url: self.getLoadFileUrl(),
        async: false,
        dataType: 'json',
        data: self.options.initXHRData,
        success: function (result) {
          var files = result.data;
          if (result.success === true && files != null) {
            dbFiles = self._processDbFiles(files);
          }
        }
      });
      if (typeof append === 'boolean') {
        self.setItems(dbFiles, append, itemFormat);
      }
      return dbFiles;
    },
    loadFilesByFileIds: function (append, itemFormat) {
      var self = this;
      var dbFiles = [];
      var options = self.options;
      $.ajax({
        type: 'GET',
        async: false,
        url: ctx + '/repository/file/mongo/getNonioFiles?fileID=' + options.fileIds,
        contentType: 'application/json',
        dataType: 'json',
        success: function (result, statusText, jqXHR) {
          var files = result.data;
          if (result.success === true && files != null) {
            dbFiles = self._processDbFiles(files);
          }
        }
      });
      if (typeof append === 'boolean') {
        self.setItems(dbFiles, append, itemFormat);
      }
      return dbFiles;
    },
    getFile: function (fileId) {
      var self = this,
        file = null;
      for (var index = 0; index < self.files.length; index++) {
        if (self.files[index].fileID == fileId) {
          file = self.files[index];
        }
      }
      return file;
    },
    _processDbFiles: function (files) {
      var dbFiles = [];
      if (files) {
        for (var i in files) {
          var dbfile = files[i];
          var file = {};
          file.isNew = false;
          file.fileID = dbfile.fileID;
          // loadfile || savefile
          file.fileName = dbfile.fileName || dbfile.filename;
          file.fileSize = dbfile.fileSize;
          file.contentType = dbfile.contentType;
          file.digestAlgorithm = dbfile.digestAlgorithm;
          file.digestValue = dbfile.digestValue;
          file.certificate = dbfile.certificate;
          dbFiles.push(file);
        }
      }
      return dbFiles;
    },
    _isInstanceOf: function (type, obj) {
      // Cross-frame instanceof check
      return Object.prototype.toString.call(obj) === '[object ' + type + ']';
    },
    _initXHRData: function (options, files) {
      var that = this,
        formData,
        paramName = options.paramName[0];
      options.headers = options.headers || {};
      if (options.contentRange) {
        options.headers['Content-Range'] = options.contentRange;
      }
      if (that._isInstanceOf('FormData', options.formData)) {
        formData = options.formData;
      } else {
        formData = new FormData();
        $.each(that._getFormData(options), function (index, field) {
          formData.append(field.name, field.value);
        });
      }
      var paramNameCount = 0;
      $.each(files, function (index, file) {
        // This check allows the tests to run with dummy
        // objects:
        if (that._isInstanceOf('File', file) || that._isInstanceOf('Blob', file)) {
          formData.append(options.paramName[index] || paramName + paramNameCount++, file, file.name);
        }
      });
      return formData;
    },
    _getFormData: function (options) {
      var formData;
      if (typeof options.formData === 'function') {
        return options.formData(options.form);
      }
      if ($.isArray(options.formData)) {
        return options.formData;
      }
      if ($.type(options.formData) === 'object') {
        formData = [];
        $.each(options.formData, function (name, value) {
          formData.push({
            name: name,
            value: value
          });
        });
        return formData;
      }
      return [];
    },
    /**
     * 为控件添加一组文件
     *
     * @param files
     *            文件信息数组，数组中的成员的成员为类FileInfo对象
     * @param isNew
     *            如果为true，同时初始化时的signature参数也为true,那么将会为该附件生成签名信息。
     * @param isAppend
     *            如果为true,
     *            表示不清空原来夹下的文件，在原来的夹下面继续append参数files里面的文件到文件夹下，如果为false则表示将原来夹下面的文件清空，再把参数files里面的文件添加到文件夹下
     */
    addFiles: function (files, isAppend) {
      var self = this;
      self.setItems(files, isAppend);
    },
    _getUploadData: function (options) {
      var self = this,
        items = [];
      var files = options.files && options.files.length > 0 ? options.files : self.files;
      $.each(files, function (index, file) {
        if (self._isInstanceOf('File', file) || self._isInstanceOf('Blob', file)) {
          items.push(file);
        }
      });
      return items;
    },
    upload: function (event) {
      var self = this,
        items = self._getUploadData(self.options);
      if (self.trigger('submit', event, items) === false) {
        return false;
      }
      if (items.length <= 0) {
        return items;
      } else if (self.options.chunk === true && items.length > 1) {
        // 分片上传,一个文件一个请求
      }
      var xhrData = self._initXHRData(self.options, items);
      self.mask.show();

      var xhr = new XMLHttpRequest();
      xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
          if (xhr.responseText) {
            self.trigger('success', JSON.parse(xhr.responseText));
          }
        }
        self.mask.close();
      };
      xhr.open('POST', self.getSaveFileUrl());
      xhr.send(xhrData);

      return xhr;
    },
    destory: function () {
      var self = this;
      // TODO self.label
    },
    /**
     * 返回附件个数
     */
    size: function () {
      return this.files.length;
    }
  }));

  $.extend(MuiFileUpload, {
    guid: 1,
    MuiFileUploadObj: {},
    /**
     * 通过控件ID获取指定的控件实例,若该控件ID所指定的控件实例不存在则返回undefined
     *
     * @param ctlID
     *            控件实例的控件ID
     * @returns
     */
    get: function (ctlID) {
      var element;
      if ((element = $(ctlID)[0])) {
        return MuiFileUpload.MuiFileUploadObj(element.getAttribute('data-MuiFileUpload'));
      }
    },
    actionSheet: (function () {
      var cancelText = '取消';
      var sheetHtml = '<div class="mui-popover mui-popover-action mui-popover-bottom fileupload-actionsheet">';
      sheetHtml +=
        '<ul class="mui-table-view"></ul><ul class="mui-table-view"><li class="mui-table-view-cell"><a href="#"><b>' +
        cancelText +
        '</b></a></li></ul></div>';
      var actionSheet = $.dom(sheetHtml)[0],
        actionView = $('.mui-table-view', actionSheet)[0];
      document.body.appendChild(actionSheet);
      return function (options, callback) {
        actionView.innerHTML = ''; // 清空菜单
        $.each(options || {}, function (text, fn) {
          if (text && text != cancelText && $.isFunction(fn)) {
            actionView.appendChild($.dom('<li class="mui-table-view-cell"><a href="#">' + text + '</a></li>')[0]);
          }
        });
        $(actionSheet)
          .off('tap', 'li>a')
          .on('tap', 'li>a', function () {
            var self = this;
            var text = self.innerHTML;
            var command = options[text];
            if ($.isFunction(callback) && $.isFunction(command) && callback.call(self, command, text) === false) {
              return;
            }
            $(actionSheet).popover('hide');
          })
          .popover('show');
      };
    })(),
    /**
     * 根据folderID从数据库里面加载文件-
     */
    loadFilesFormDb: function (folderID, purpose) {
      var dbFiles = [];
      JDS.call({
        service: 'mongoFileService.getNonioFilesFromFolder',
        data: [folderID, purpose],
        async: false,
        success: function (result) {
          var files = result.data;
          if (files != null) {
            for (var i in files) {
              var file = files[i];
              var file2 = new FileInfo();
              file2.fileID = file.fileID;
              file2.fileName = file.fileName;
              file2.contentType = file.contentType;
              file2.digestAlgorithm = file.digestAlgorithm;
              file2.digestValue = file.digestValue;
              file2.certificate = file.certificate;
              file2.isNew = false;
              dbFiles.push(file2);
            }
          }
        },
        error: function (jqXHR) {}
      });
      return dbFiles;
    },
    /**
     * 签名
     *
     * @param fileID
     * @param digestValue
     * @param digestAlgorithm
     * @param certificate
     */
    sign: function (fileID, digestValue, digestAlgorithm, certificate) {
      var updateSignatureOK = true;
      return updateSignatureOK;
    }
  });

  var MuiBodyFileUpload = ($.MuiBodyFileUpload = $.MuiFileUpload.extend({
    init: function (holder, options) {
      var self = this,
        attachContainer;
      if (!holder) {
        throw '构造 MuiBodyFileUpload 时找不到元素';
      }
      options = options || {};
      self.attachContainer = attachContainer = holder.parentNode.querySelector('.attach-container');
      self.attachLabel = attachContainer.querySelector('.attach-label');
      self.attachTotal = attachContainer.querySelector('.attach-total');
      self.attachPicker = attachContainer.querySelector('.attach-picker');
      self.filedelall = attachContainer.querySelector('.btn-filedelall');
      self.filedownall = attachContainer.querySelector('.btn-filedownall');
      self.fileshowtype = attachContainer.querySelector('.btn-fileshowtype');
      self.attachTableView = attachContainer.querySelector('.attach-table-view');
      var attachtable = 'attach-body-view';
      if (options.type == '4' || options.type == 'icon') {
        attachtable = 'attach-icon-view';
      } else if (options.type == '33' || options.type == 'image') {
        attachtable = 'attach-image-view';
      }
      self.attachTableView.classList.add(attachtable);
      options.lableText = options.lableText || '添加';
      options.itemFormat =
        options.itemFormat ||
        function (file) {
          var self = this;
          var item =
            '<li class="mui-table-view-cell">\
									<div class="attach-handler"">' +
            (file && file.fileName) +
            '<span class="mui-badge">' +
            self.formatSize(file.fileSize) +
            '</span></div><a class="mui-btn mui-btn-blue btn-filedel">删除</a></li>';
          return item;
        };
      holder.style.display = 'none';
      attachContainer.style.display = 'block';
      self._super(self.attachPicker, options);
      // 初始化状态
      self.setAllowDelete(self.isAllowDelete());
      self.setAllowUpload(self.isAllowUpload());
      self.setAllowDownload(self.isAllowDownload());

      if (options.type == '4' || options.type == 'icon') {
        if (self.fileshowtype) {
          $.trigger(self.fileshowtype, 'tap', {});
        } else {
          self.attachTableView.classList.add('attach-icon-view');
        }
      }
      if (self.filedelall && self.disableDelAll()) {
        self.filedelall.style.display = 'none';
      }
      if (self.filedownall && self.disableDownAll()) {
        self.filedownall.style.display = 'none';
      }
    },
    disableDelAll: function () {
      var self = this;
      return self.options.filedelall === false;
    },
    disableDownAll: function () {
      var self = this;
      return self.options.filedownall === false;
    },
    renderDelItems: function (files) {
      var self = this;
      if (files == null || self.files === files) {
        self.attachTableView.innerHTML = '';
      } else if (files && files.length > 0) {
        self.attachTableView.each(function (i, element) {
          var fileID = element.getAttribute('data-fileid');
          var fileName = element.getAttribute('data-filename');
          for (i = 0; i < files.length; i++) {
            var file = files[i];
            if (fileID == file.fileID || fileName == file.fileName) {
              self.attachTableView.removeChild(element);
            }
          }
        });
      }
      return files;
    },
    renderAddItems: function (files, itemFormat) {
      var self = this,
        items = [];
      $.each(files, function (i, file) {
        file.createTime = file.createTime || new Date().format('yyyy-MM-dd HH:mm:ss');
        var item = self.formatItem(file, itemFormat);
        if (item) {
          items.push(item);
          // 设置ID和名称，如果存在
          var element = $.dom(item)[0];
          file.fileID && element.setAttribute('data-fileid', file.fileID);
          if (file.fileName) {
            element.setAttribute('title', file.fileName);
            element.setAttribute('data-filename', file.fileName);
          }
          self.attachTableView.appendChild(element);
        }
      });
      return items;
    },
    popAction: function () {},
    bindEvent: function () {
      var self = this;
      var options = self.options;
      self._super();
      self.on('afterSetValue', function () {
        self.updateAttachTotal(true);
        self.updateDelAll(!self.disableDelAll());
        self.updateDownAll(!self.disableDownAll());
        // 从验证框架中删除该控件的所对应的非法条目
        var editableElement;
        self.trigger('uploadOkCallback'); // 上传成功回调
        if ((editableElement = self.holder && typeof Theme != 'undefined')) {
          Theme.validationRules.unhighlight(editableElement);
          Theme.validationRules.success(null, editableElement);
        }
      });

      self.on('submit', function (event, files) {
        var self = this;
        if (self.isSignature() && self.trigger('validSignatureUSB') === false) {
          return false;
        }
        if (!self.isAllowFileNameRepeat()) {
          var isForbid = false;
          if (self.files && self.files.length > 0) {
            for (var i in files) {
              var curFile = files[i];
              for (var j in self.files) {
                var hadUploadFile = self.files[j];
                if (hadUploadFile.fileName === curFile.name) {
                  $.alert('已上传过文件名称为[' + curFile.name + ']的文件，不允许重复上传！');
                  isForbid = true;
                  break;
                }
              }
            }
          }
          if (isForbid) {
            return false;
          }
        }
        if (self.trigger('beforeMuiFileUpload', event, files) === false) {
          return false;
        }
        return true;
      });

      function delfn(file, cmd) {
        var self = this;
        file && self.deleteFileItem(file);
        self.setItems(self.files, false); // 刷新界面
      }

      // 删除功能
      function popAction() {
        var elem = this;
        for (; elem && elem !== self.attachTableView; elem = elem.parentNode) {
          if (elem.classList && elem.classList.contains('mui-table-view-cell')) {
            break;
          }
        }
        var fileID = elem.getAttribute('data-fileid');
        // delAction：false不允许删除,function允许删除
        var delAction = self.isAllowDelete() && delfn;
        options.action = $.extend(options.action, {
          删除: delAction
        });
        var file = self.getFile(fileID);
        MuiFileUpload.actionSheet(options.action, function (fn, cmd) {
          return fn.call(self, file, cmd);
        });
      }
      // 下载功能
      var downAction = function () {
        // 手机端所有文件都允许预览
        // if (!self.isAllowDownload()) {
        //   return false;
        // }
        var elem = this;
        for (; elem && elem !== self.attachTableView; elem = elem.parentNode) {
          if (elem.classList && elem.classList.contains('mui-table-view-cell')) {
            break;
          }
        }
        var fileID = elem.getAttribute('data-fileid');
        var fileName = elem.getAttribute('data-filename');
        // fileID存在才下载，
        fileID && self.downFile(fileID, fileName);
        $.swipeoutClose(elem);
      };
      // TableView事件代理
      var viewTap = function (event) {
        var target = event.target;
        var classList = target.classList;
        var parentClassList = target.parentNode.classList;
        if (classList == null || parentClassList == null) {
          return;
        } else if (classList.contains('btn-filedel') || parentClassList.contains('btn-filedel')) {
          popAction.call(target, event);
        } else if (classList.contains('attach-handler') || parentClassList.contains('attach-handler')) {
          downAction.call(target, event);
        }
      };
      self.attachTableView.addEventListener('tap', viewTap);

      // 清空
      if (self.filedelall) {
        self.filedelall.addEventListener('tap', function () {
          var btnArray = ['否', '是'];
          mui.confirm('是否清空附件', '', btnArray, function (e) {
            if (e.index == 1) {
              self.deleteAllFileItem();
              self.setItems(self.files, false); // 刷新界面
            }
          });
        });
      }
      // 全部下载
      if (self.filedownall) {
        self.filedownall.addEventListener('tap', function () {
          var files = [];
          for (var i in self.files) {
            var file = self.files[i];
            if (!file.isNEW) {
              files.push({
                fileID: file.fileID
              });
            }
          }
          if (files.length > 0) {
            if (!(self.options.downconfirm === false)) {
              var btnArray = ['否', '是'];
              mui.confirm('是否下载全部附件', '', btnArray, function (e) {
                if (e.index == 1) {
                  self.downAllFile(files);
                }
              });
            } else {
              self.downAllFile(files);
            }
          }
        });
      }
      // 显示类型
      if (self.fileshowtype) {
        self.fileshowtype.addEventListener('tap', function (event) {
          event.preventDefault();
          event.stopPropagation();
          self.attachTableView.classList.toggle('attach-body-view');
          var html = self.attachTableView.classList.toggle('attach-icon-view') ? '详情' : '图标';
          self.fileshowtype.innerHTML = html + '/';
        });
        if (self.options.showtype === true) {
          self.fileshowtype.style.display = 'inline-block';
        } else {
          self.fileshowtype.style.display = 'none';
        }
      }
    },
    /**
     * 设置是否可上传 下载 删除
     */
    initAllowUploadDeleteDownload: function (allowUpload, allowDelete, allowDownload) {
      var self = this;
      var options = self.options;
      options.allowUpload = allowUpload;
      options.allowDelete = allowDelete;
      options.allowDownload = allowDownload;
      self.setReadonly(allowUpload || allowDelete);
    },
    setAllowUpload: function (allow) {
      var self = this;
      if (allow) {
        return self.enableUploadFunction();
      }
      return self.disableUploadFunction();
    },
    isAllowUpload: function () {
      var self = this;
      var allowUpload = self.options.allowUpload;
      return allowUpload == undefined || allowUpload == true; // &&
      // !self.isReadonly();//
      // 默认可以上传
    },
    setAllowDelete: function (allow) {
      var self = this;
      if (allow) {
        return self.enableDeleteFunction();
      }
      return self.disableDeleteFunction();
    },
    isAllowDelete: function () {
      var self = this;
      var allowDelete = self.options.allowDelete;
      return allowDelete == undefined || allowDelete == true; // &&
      // !self.isReadonly();//
      // 默认可以删除
    },
    setAllowDownload: function (allow) {
      var self = this;
      if (allow) {
        return self.enableDownLoadFunction();
      }
      return self.disableDownLoadFunction();
    },
    isAllowDownload: function () {
      var allowDownload = this.options.allowDownload;
      return allowDownload == undefined || allowDownload == true; // 默认可以下载
    },
    /**
     * 设置是否允许上传重复名称的文件
     */
    initAllowFileNameRepeat: function (allowFileNameRepeat) {
      var options = this.options;
      options.allowFileNameRepeat = allowFileNameRepeat === true; // 默认不支持重名
    },

    isAllowFileNameRepeat: function () {
      return this.options.allowFileNameRepeat;
    },

    /**
     * 为控件添加单个附件 .
     *
     * @param filename
     * @param fileID
     * @param digestValue
     * @param digestAlgorithm
     * @param certificate
     * @param isNew
     *            如果为true,表示该文件还没与文件夹产生关系,false则反之
     */
    addFile: function (filename, fileID, contentType, digestValue, digestAlgorithm, certificate, isNew) {
      if (typeof isNew == 'undefined') {
        isNew = false;
      }
      var fileSize = 0;
      if (arguments.length == 8) {
        fileSize = arguments[7]; // 第8个参数为文件大小
      }

      var _this = this;

      if (this.signature && isNew) {
        // 更新文件签名
        var updateSignatureOK = MuiFileUpload.sign(fileID, digestValue, digestAlgorithm, certificate);
        if (!updateSignatureOK) {
          $.alert('文件签名上传失败,请重新上传');
          return;
        }
      }

      if (isNew && MuiBodyFileUpload.file2swf) {
        var ok = _this.createReplicaOfSWF(fileID); //
        if (!ok) {
          $.alert('副本生成失败,请重试');
          return;
        }
      }
      var file = _this.getFile(fileID);
      if (file == null) {
        var file = {};
        file.fileID = fileID;
        file.fileName = filename;
        file.digestAlgorithm = digestAlgorithm == undefined ? '' : digestAlgorithm;
        file.digestValue = digestValue == undefined ? '' : digestValue;
        file.certificate = certificate == undefined ? '' : certificate;
        file.signatureValue = '';
        file.contentType = contentType == undefined ? '' : contentType;
        file.isNew = isNew;
        file.$fileItemElement = $fileItemElement;
        file.fileSize = fileSize;
        _this.files.push(file);
      }

      var fileInfo = {};
      $.extend(fileInfo, file);
      fileInfo.filename = fileInfo.fileName;

      // 设置是否可下载
      if (_this.isAllowDownload()) {
        this.enableDownLoadFunction();
      } else {
        _this.disableDownLoadFunction();
      }
      // 设置是否可删除
      if (self.isAllowDelete()) {
        _this.enableDeleteFunction();
      } else {
        _this.disableDeleteFunction();
      }
      _this.setItems(fileInfo, true);
    },

    deleteAllFileItem: function () {
      var self = this;
      for (; self.size() > 0; ) {
        self.deleteFileItem(self.files[0]);
      }
    },

    enableDeleteFunction: function () {
      var self = this;
      self.updateDel(true);
      self.updateDelAll(true);
      var options = self.options;
      self.initAllowUploadDeleteDownload(options.allowUpload, true, options.allowDownload);
    },

    // 定义单个文件的删除功能
    disableDeleteFunction: function (file) {
      var self = this;
      self.updateDel(false);
      self.updateDelAll(false);
      var options = self.options;
      self.initAllowUploadDeleteDownload(options.allowUpload, false, options.allowDownload);
    },

    enableDownLoadFunction: function () {
      var self = this;
      self.updateDownAll(true);
      var options = self.options;
      self.initAllowUploadDeleteDownload(options.allowUpload, options.allowDelete, true);
    },

    disableDownLoadFunction: function () {
      var self = this;
      self.updateDownAll(false);
      var options = self.options;
      self.initAllowUploadDeleteDownload(options.allowUpload, options.allowDelete, false);
    },
    /**
     * 添加上传功能
     */
    enableUploadFunction: function () {
      var self = this;
      var options = self.options;
      self.updateUploadFunction(true);
      self.initAllowUploadDeleteDownload(true, options.allowDelete, options.allowDownload);
    },
    /**
     * 禁用上传功能
     */
    disableUploadFunction: function () {
      var self = this;
      var options = self.options;
      self.updateUploadFunction(false);
      self.initAllowUploadDeleteDownload(false, options.allowDelete, options.allowDownload);
    },
    updateDel: function (enable) {
      var self = this;
      // 全部下载按钮
      var display = enable ? 'inline-block' : 'none';
      $('.btn-filedel', self.attachTableView).each(function (i, element) {
        // element.style.display = display;
      });
    },
    updateDelAll: function (enable) {
      var self = this;
      // 全部下载按钮
      var display = self.size() > 0 && enable ? 'inline-block' : 'none';
      if (self.filedelall) {
        self.filedelall.style.display = display;
      }
    },
    updateDownAll: function (enable) {
      var self = this;
      // 全部下载按钮
      var display = self.size() > 0 && enable ? 'inline-block' : 'none';
      if (self.filedownall) {
        self.filedownall.style.display = display;
      }
    },
    updateUploadFunction: function (enable) {
      var self = this;
      var display = enable ? 'inline-block' : 'none';
      if (self.attachPicker) {
        self.attachPicker.parentNode.style.display = display;
      }
    },
    // update the element of load all file
    updateAttachTotal: function (enable) {
      var self = this;
      if (self.attachTotal) {
        self.attachTotal.innerHTML = self.size();
        self.attachTotal.classList.remove('mui-hidden');
      }
    },

    size: function () {
      return this.files == null ? 0 : this.files.length;
    },
    // 删除所有的文件
    clear: function () {
      this.deleteAllFileItem();
    },
    createReplicaOfSWF: function (fileID) {
      return true;
    },
    /**
     * 验证签名USB
     */
    validSignatureUSB: function () {
      return true;
    },

    /**
     * 获取指定的控件ID的文件列表信息数组,数组成员为FileInfo类对象 若指定的ctlID不存在则返回undefined
     *
     * @param ctlID
     * @returns 返回的文件信息列表，是一个由FileInfo类对象组成的数组
     */
    getFiles: function (ctlID) {
      return MuiFileUpload.get.call(this, ctlID);
    },

    /**
     * 产生文件夹ID
     */
    createFolderID: function () {
      // TODO
    },

    /**
     * 该方法仅提供给动态表单使用 通过表名，字段名，行索引组合成每个控件的id。
     *
     * @param tblName
     * @param fieldName
     * @param rowIndex
     *            行索引, 主表就直接使用0即可,从表则可以每行的行ID,该参数也可以是直接用dataUUID
     * @returns 返回控件ID
     */
    getCtlID4Dytable: function (tblName, fieldName, rowIndex) {
      var controlID = tblName + '___' + fieldName + '____' + rowIndex;
      return controlID;
    },

    /**
     * 判断value1和value2是否相等,相等返回true,不等返回false
     */
    isEqual: function (value1, value2) {
      if (this.isFileSetInAnotherFileSet(value1, value2) && this.isFileSetInAnotherFileSet(value2, value1)) {
        return true;
      } else {
        return false;
      }
    },
    /**
     * 判断value1的值集是否在value2的值集中
     */
    isFileSetInAnotherFileSet: function (value1, value2) {
      var noEqual = false;
      for (var i in value1) {
        var file = value1[i];
        var fileID = file.fileID;
        var yes = false;
        for (var j in value2) {
          var file2 = value2[j];
          var fileId2 = file2.fileID;
          if (fileID == fileId2) {
            yes = true;
            break;
          }
        }
        if (!yes) {
          noEqual = true;
          break;
        }
      }
      return !noEqual;
    },

    /**
     * 获取文件路径
     */
    getFilePath: function (data) {
      var fileInputs = data.fileInput;
      var paths = [];
      for (var i in fileInputs) {
        var fileInput = fileInputs[i];
        if (fileInput && fileInput.value) {
          paths.push(fileInput.value);
        }
      }
      return paths;
    }
  }));

  var MuiImageFileUpload = ($.MuiImageFileUpload = $.MuiBodyFileUpload.extend({
    init: function (holder, options) {
      var self = this;
      options.accept = 'image/*';
      options.lableText = '选择图片';
      options.itemFormat = function (file) {
        var self = this;
        var style = 'width: 100vw;height: 50vw;background-size: contain;background-position: center;background-repeat: no-repeat;';
        if (file.fileID && self.isImage(file)) {
          var burl = this.getCtx() + DOWNLOAD_FILE + file.fileID + '&preview=true';
          style += ';background-image:url(' + burl + ')';
        } else {
          style += ';background-image:url(' + this.getCtx() + '/mobile/mui/images/cbd.jpg)';
        }
        var item =
          '<li class="mui-table-view-cell">\
					<div class="mui-card">\
					<div class="mui-card-header mui-card-media" style="' +
          style +
          '"></div>\
					<div class="mui-card-footer">\
						<div class="attach-handler"><h4 class="mui-ellipsis">' +
          (file && file.fileName) +
          '</h4><h5>' +
          file.createTime +
          '<span class="filesize">' +
          self.formatSize(file.fileSize) +
          '</span></h5></div>\
						<a class="mui-btn btn-filedel mui-icon mui-icon-info"></a>\
					</div>\
				</div></li>';
        return item;
      };
      self._super(holder, options);
    }
  }));

  var FileUploadFactory = ($.FileUploadFactory = function (holder, options, type) {
    options = options || {};
    if (type) {
      options.type = type;
    }
    if (options.type == '4' || options.type == 'icon') {
      // 图标显示
      options.showtype = true;
      return new MuiBodyFileUpload(holder, options);
    } else if (options.type == '33' || options.type == 'image') {
      // 图片附件
      return new MuiImageFileUpload(holder, options);
    } else {
      // 6:列表显示
      return new MuiBodyFileUpload(holder, options);
    }
  });
  return FileUploadFactory;
});
