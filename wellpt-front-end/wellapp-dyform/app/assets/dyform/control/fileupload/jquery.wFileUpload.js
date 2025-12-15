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
   * FILEUPLOAD CLASS DEFINITION ======================
   */
  var FileUpload = function (element, options) {
    this.$element = $(element);
    this.options = $.extend({}, $.fn['wfileUpload'].defaults, options, this.$element.data());
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = this.$element;
    // console.log(this.$element.attr("display"));
  };
  var $tipSpan = $('<span class="tip" style="margin: 0"><i class="iconfont icon-ptkj-tishishuoming"></i></span>');
  FileUpload.prototype = {
    constructor: FileUpload,
    init: function () {
      var self = this;
      $.wControlInterface.initControlEvents.call(this);
      this.invoke('beforeInit', this.options);
      if (this.initReadOnly()) {
        this.options.allowUpload = this.options.allowDelete = false;
      }
      // 设置字段属性.根据不同的控件类型区分。
      this.$element.show();
      // $.ControlUtil.setCommonCtrAttr(this.$element,this.options);
      this.$element.attr('id', this.$element.attr('name'));
      var allowDownload = this.options.allowDownload;
      var allowDelete = this.options.allowDelete;
      var mutiselect = this.options.mutiselect;
      var btnShowType = this.options.btnShowType;

      // var setReadOnly=false;
      // setReadOnly=!this.options.allowUpload;
      // var showType =this.options.columnProperty.showType;
      // if(showType){
      // //showAsLabel:'2',//直接以文本的形式显示
      // // readonly:'3',//有输入框但只读
      // // disabled:'4',//有输入框但被disabled
      // if(showType == dyshowType.showAsLabel
      // || showType == dyshowType.readonly
      // || showType == dyshowType.disabled
      // ){
      // setReadOnly = true;
      // }else{
      // setReadOnly = false;
      // }
      // }
      // console.log(this.$element.is(":hidden"));
      // this.$element.hide();
      var id = this.$element.attr('id'); // 字段名称

      // var $uploaddiv="<div id='_fileupload"+id+"'></div>";

      // this.$element.after(uploaddivhtml);
      var $attachContainer = this.$element;
      // 创建上传控件
      // var elementID =
      // WellFileUpload.getCtlID4Dytable(this.options.mainTableName, id,
      // 0);
			self.fileuploadobj = new WellFileUpload(id);
			self.fileuploadobj.formUuid = self.getFormDefinition().uuid;
			self.fileuploadobj.dataUuid = self.getDataUuid() || self.getCurrentForm().getDataUuid();
			self.fileuploadobj.fieldName = self.getFieldName();
      this.fileuploadobj.initAllowUploadDeleteDownload(this.options.allowUpload, this.options.allowDelete, this.options.allowDownload);
      this.fileuploadobj.initFileUploadExtraParam(
        this.options.isShowFileFormatIcon,
        this.options.isShowFileSourceIcon,
        this.options.secDevBtnIdStr,
        this.options.fileSourceIdStr,
        this.options.flowSecDevBtnIdStr,
        this.options.downloadAllType,
        this.options.columnProperty.displayName,
        btnShowType,
        true
      );
      // add by wujx 20160606 begin
      this.fileuploadobj.initAllowFileNameRepeat(this.options.allowFileNameRepeat);
      this.fileuploadobj.setAfterChangeFilesCallBack(function () {
        //附件控件 值变更后，运行 运算公式
        self.culateByFormula && self.culateByFormula();
      });
      // add by wujx 20160606 end

      /*
       * if(this.getDataUuid()!=''&&this.getDataUuid()!=undefined &&
       * !this.options.columnProperty.data){ //初始化上传控件
       * this.fileuploadobj.initWithLoadFilesFromFileSystem(setReadOnly,
       * $attachContainer, this.options.enableSignature, mutiselect,
       * this.getDataUuid() , id); }else{
       */
      var files = [];
      if (this.options.columnProperty.data) {
        files = this.options.columnProperty.data;
      }

      this.fileuploadobj.init(this.initReadOnly(), $attachContainer, this.options.enableSignature, mutiselect, files);
      // }
      this.addMustMark();
      // add by wujx 20160614 begin
      this.displayByShowType();
      // add by wujx 20160614 end
      // this.setAllowDelete(this.options.allowDelete);
      // this.setAllowUpload(this.options.allowUpload);
      // this.setAllowDownload(this.options.allowDownload);
      var tip = [];
      if (this.options.fileExt) {
        tip.push('支持' + this.options.fileExt + '格式附件');
        this.fileuploadobj.setFileExt(this.options.fileExt);
      }
      if (this.options.fileMaxSize) {
        tip.push('附件大小不超过' + this.options.fileMaxSize + 'MB');
        this.fileuploadobj.setFileMaxSize(this.options.fileMaxSize);
      }
      if (this.options.fileMaxNum) {
        tip.push('最多上传' + this.options.fileMaxNum + '个');
        this.fileuploadobj.setMaxFilesLength(this.options.fileMaxNum);
      }
      var tipTxt = '';
      if (tip.length > 0) {
        tipTxt = '2、' + tip.join(';  ');
      }
      if (this.fileuploadobj.$uploadElement) {
        var $tipSpan2 = $tipSpan.clone();
        $tipSpan2.popover({
          html: true,
          placement: 'top',
          container: 'body',
          trigger: 'hover',
          template:
            '<div class="popover" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
          content: function () {
            return '<span>' + '1、表单提交或保存草稿时，只保存上传成功的附件<br/>' + tipTxt + '</span>';
          }
        });

        this.fileuploadobj.$uploadElement.append($tipSpan2);
      }
      if (!this.initReadOnly()) {
        this.initInputEvents();
      }
      $(self.$element).on('filechange', function () {
        self.invoke('afterSetValue', true);
      });

      this.invoke('afterInit', true);
      this.bindEvent();
    },

    getFieldName: function () {
      return this.options.columnProperty.columnName;
    },

    addFilesIcon: function (fileId, iconUrl) {
      this.fileuploadobj.addFileIcon(fileId, iconUrl);
    },
    createAccessoryContainer: function (position, accessoryId) {
      var self = this;
      var $accessoryContainer = null;
      var $placeHolder = self.$placeHolder;
      if (null == $placeHolder) {
        return;
      }
      var $elem = $placeHolder.find('#upload_div');
      if ($elem) {
        var containerId = 'accessory_container_' + accessoryId;
        var containerHtml = "<span id='" + containerId + "'></span>";
        var $accessoryContainer = $(containerHtml);
        if (position == -1) {
          $elem.before($accessoryContainer);
        } else {
          $elem.after($accessoryContainer);
        }
      }
      return $accessoryContainer;
    }
  };

  /*
   * FILEUPLOAD PLUGIN DEFINITION =========================
   */
  $.fn.wfileUpload = function (option) {
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
        var data = $this.data('wfileUpload');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wfileUpload'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new FileUpload($(this), options);
      // $.extend(data,$.wControlInterface);
      var data1 = $.extend(data, $.wFileUploadMethod);
      $.extend(data1, FileUpload.prototype);
      data1.init();
      $this.data('wfileUpload', data1);
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

  $.fn.wfileUpload.Constructor = FileUpload;

  $.fn.wfileUpload.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性
    isHide: false, // 是否隐藏
    mainTableName: '',
    formSign: '',
    enableSignature: false,
    allowUpload: true, // 允许上传
    allowDownload: true, // 允许下载
    allowDelete: true, // 允许删除
    mutiselect: true, // 是否多选
    isAppend: false
    // true 追加值，false 不追加，采用覆盖 add by wujx 20160615
  };
})(jQuery);
