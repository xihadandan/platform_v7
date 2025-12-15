(function ($) {
  var columnProperty = {
    // 控件字段属性
    applyTo: null, // 应用于
    columnName: null, // 字段定义 fieldname
    displayName: null, // 描述名称 descname
    dbDataType: '', // 字段类型 datatype type
    indexed: null, // 是否索引
    showed: null, // 是否界面表格显示
    sorted: null, // 是否排序
    sysType: null, // 系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
    length: null, // 长度
    showType: '1', // 显示类型 1,2,3,4 datashow
    defaultValue: null, // 默认值
    valueCreateMethod: '1', // 默认值创建方式 1用户输入
    onlyreadUrl: null // 只读状态下设置跳转的url
  };

  // 控件公共属性
  var commonProperty = {
    inputMode: null, // 输入样式 控件类型 inputDataType
    fieldCheckRules: null,
    fontSize: null, // 字段的大小
    fontColor: null, // 字段的颜色
    ctlWidth: null, // 宽度
    ctlHight: null, // 高度
    textAlign: null // 对齐方式
  };

  /*
   * FILEUPLOAD4Body CLASS DEFINITION ======================
   */
  var FileUpload4Body = function (element, options) {
    this.$element = $(element);
    this.options = $.extend({}, $.fn['wfileUpload4Body'].defaults, options, this.$element.data());
    this.$placeHolder = $(element);
  };

  FileUpload4Body.prototype = {
    constructor: FileUpload4Body
  };
  $.FileUpload4Body = {};

  $.extend($.FileUpload4Body, $.FileUpload4Icon);

  $.extend($.FileUpload4Body, {
    init: function () {
      // 设置字段属性.根据不同的控件类型区分。
      var self = this;
      $.wControlInterface.initControlEvents.call(this);
      this.invoke('beforeInit', this.options);
      this.$element.show();

      if (this.initReadOnly()) {
        this.options.allowUpload = this.options.allowDelete = false;
      }

      // $.ControlUtil.setCommonCtrAttr(this.$element,this.options);
      this.$element.attr('id', this.$element.attr('name'));

      var mutiselect = this.options.mutiselect;
      var fileExt = this.options.fileExt;
      var fileMaxSize = this.options.fileMaxSize;
      var fileMaxNum = this.options.fileMaxNum;
      var defaultView = this.options.defaultView;
      var operateBtns = this.options.flowSecDevBtnIdStr ? this.options.flowSecDevBtnIdStr.split(';') : this.options.operateBtns;
      var createHistory = this.options.createHistory;
      var id = this.$element.attr('id'); // 字段名称

      this.options.allowUpload = operateBtns.indexOf('1') > -1 || operateBtns.indexOf('4') > -1 ? true : false;
      this.options.allowDownload = operateBtns.indexOf('2') > -1 ? true : false;
      this.options.allowDelete = operateBtns.indexOf('3') > -1 ? true : false;

      var $attachContainer = this.$element;
      // 创建上传控件

			this.fileuploadobj = new WellFileUpload4Body(id, iconFileControlStyle.OnlyIcon, fileExt,fileMaxSize,fileMaxNum,defaultView,operateBtns);
			self.fileuploadobj.formUuid = self.getFormDefinition().uuid;
			self.fileuploadobj.dataUuid = self.getDataUuid() || self.getCurrentForm().getDataUuid();
			self.fileuploadobj.fieldName = self.getFieldName();
      this.fileuploadobj.initAllowUploadDeleteDownload(this.options.allowUpload, this.options.allowDelete, this.options.allowDownload);
      this.fileuploadobj.initAllowFileNameRepeat(this.options.allowFileNameRepeat);
      var files = [];
      if (this.options.columnProperty.data) {
        files = this.options.columnProperty.data;
      }

      this.fileuploadobj.init(
        true, // 是否则有上传的权限
        this.options.allowDownload, // 是否具有下载的权限
        $attachContainer, // 存放该附件的容器
        this.options.enableSignature, // 是否签名
        files
      );

      this.addMustMark();
      this.displayByShowType();

      this.setAllowDelete(this.options.allowDelete);
      this.setAllowUpload(this.options.allowUpload);
      this.setAllowDownload(this.options.allowDownload);
      if (!this.initReadOnly()) {
        this.initInputEvents();
      }
      $(self.$element).on('filechange', function () {
        self.invoke('afterSetValue', true);
      });

      this.invoke('afterInit', true);
      this.bindEvent();
    },
    getValue: function (fnSuccess, fnError) {
      return $.FileUpload4Icon.getValue.call(this, fnSuccess, fnError);
    },
    setReadOnly: function () {
      return $.FileUpload4Icon.setReadOnly.call(this);
    },
    // 设置可编辑
    setEditable: function () {
      this.options.readOnly = false;
      this.fileuploadobj.readOnly = false;
      return $.FileUpload4Icon.setEditable.call(this);
    },
    // 设置是否保留痕迹 add by wujx 20161024
    setPbRecord: function (flag) {
      this.fileuploadobj.pbRecord = flag;
    },
    setRevisionCtrl: function (revisionCtrl) {
      this.fileuploadobj.revisionCtrl = revisionCtrl;
    },
    enableReplaceFile: function (callback) {
      var self = this;
      var fileuploadobj = self.fileuploadobj;
      if (callback) {
        fileuploadobj.enableBtn(WellFileUpload4Icon.btnCodes.replace);
        $.isFunction(callback) &&
          self.$element.on('file-replace', function (event, data) {
            callback.apply(this, [
              event,
              {
                new: data.aValue,
                old: data.bValue
              }
            ]);
          });
      } else {
        fileuploadobj.disableBtn(WellFileUpload4Icon.btnCodes.replace);
        self.$element.off('file-replace');
      }
    }
  });

  /*
   * FILEUPLOAD4Body PLUGIN DEFINITION =========================
   */
  $.fn.wfileUpload4Body = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }

    if (typeof option == 'string') {
      if (option === 'getObject') {
        // 通过getObject来获取实例
        var $this = $(this);
        var data = $this.data('wfileUpload4Body');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wfileUpload4Body'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new FileUpload4Body(this, options);
      var data1 = $.extend(data, $.wFileUploadMethod);
      $.extend(data1, $.FileUpload4Body);

      data1.init();

      $this.data('wfileUpload4Body', data1);
    }
    if (typeof option == 'string') {
      if (method == true && args != null) {
        return data[option](args);
      } else {
        return data[option]();
      }
    } else {
      return data;
    }
  };

  $.fn.wfileUpload4Body.Constructor = FileUpload4Body;

  $.fn.wfileUpload4Body.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性
    isHide: false, // 是否隐藏
    mainTableName: '',
    formSign: '',
    enableSignature: '',
    allowUpload: true, // 允许上传
    allowDownload: true, // 允许下载
    allowDelete: true, // 允许删除
    mutiselect: true, // 是否多选
    isAppend: false
    // true 追加值，false 不追加，采用覆盖 add by wujx 20160615
  };
})(jQuery);
