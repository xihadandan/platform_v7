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
    unitUnique: null,
    fontSize: null, // 字段的大小
    fontColor: null, // 字段的颜色
    ctlWidth: null, // 宽度
    ctlHight: null, // 高度
    textAlign: null // 对齐方式
  };

  /*
   * Ckeditor CLASS DEFINITION ======================
   */
  var Ckeditor = function ($placeHolder, options) {
    this.options = $.extend({}, $.fn['wckeditor'].defaults, options);
    this.value = '';
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = $placeHolder;
  };

  Ckeditor.prototype = {
    constructor: Ckeditor
  };

  $.Ckeditor = {
    createEditableElem: function () {
      if (this.$editableElem != null) {
        // 创建可编辑框
        return;
      }
      var options = this.options;
      var ctlName = this.getCtlName();
      var editableElem = document.createElement('textarea');
      editableElem.setAttribute('class', this.editableClass);
      editableElem.setAttribute('name', ctlName);
      editableElem.setAttribute('id', ctlName);
      editableElem.setAttribute('type', 'text');

      $(editableElem).css(this.getTextInputCss());

      this.$placeHolder.after($(editableElem));
      this.$editableElem = this.$placeHolder.next('.' + this.editableClass);
      var _this = this;
      _this.createInstance();
    },
    createInstance: function (force) {
      var _this = this;
      var name = _this.$editableElem.attr('id');
      // 清除编辑器
      var instance = CKEDITOR.instances[name];
      if (instance) {
        instance.destroy(); // 销毁
        CKEDITOR.remove(instance); // 删除缓存
      }

      var options = _this.options;
      var width = _this.options.commonProperty.ctlWidth;
      if ($.trim(width).length > 0) {
        if (width.indexOf('px') == -1 && width.indexOf('%') == -1) {
          width = width + 'px';
        }
      } else {
        width = '100%';
      }

      var height = _this.options.commonProperty.ctlHight;
      if ($.trim(height).length > 0) {
        if (height.indexOf('px') == -1 && height.indexOf('%') == -1) {
          height = height + 'px';
        }
      } else {
        height = '100%';
      }

      //ckeditor配置路径
      var customCkeditorPath = '/static/dyform/explain/ckeditor'; // 自定义ckeditor相关配置的路径
      CKEDITOR.plugins.basePath = customCkeditorPath + '/plugins/'; // 自定义ckeditor的插件路径

      // 初始化编辑器
      var editor = CKEDITOR.replace(name, {
        allowedContent: true,
        enterMode: CKEDITOR.ENTER_P,
        toolbarStartupExpanded: true,
        toolbarCanCollapse: true,
        height: height,
        width: width,
        // 工具栏
        customConfig: customCkeditorPath + '/dyform_config.js',
        // toolbar : [ [ 'Bold', 'Italic', 'Underline' ], [ 'Cut', 'Copy', 'Paste' ],
        // 		[ 'NumberedList', 'BulletedList', '-' ],
        // 		[ 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ,'Outdent', 'Indent'], [ 'Link', 'Unlink' ],
        // 		[ 'Format', 'Font', 'FontSize' ], [ 'TextColor', 'BGColor' ], [ 'Image', 'Table', 'Smiley' ],
        // 		[ 'Source', 'Maximize' ], [ 'formfile','control4expand','control4indent' ], ['Undo','Redo'] ],
        // toolbar :
        //    [
        // 	//加粗     斜体，     下划线      穿过线      下标字        上标字
        //    ['Bold','Italic','Underline','Strike','Subscript','Superscript'],
        // 	//数字列表          实体列表            减小缩进    增大缩进
        //    ['NumberedList','BulletedList','-','Outdent','Indent'],
        // 	//左对齐             居中对齐          右对齐          两端对齐
        //    ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
        // 	//超链接 取消超链接 锚点
        //    ['Link','Unlink','Anchor'],
        // 	//图片    flash    表格       水平线            表情       特殊字符        分页符
        //    ['Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak'],
        //    '/',
        // 	//样式       格式      字体    字体大小
        //    ['Styles','Format','Font','FontSize'],
        // 	//文本颜色     背景颜色
        //    ['TextColor','BGColor'],
        // 	//全屏           显示区块
        //    ['Maximize', 'ShowBlocks','-']
        // ],

        toolbar: [
          [
            'Bold',
            'Italic',
            'Underline',
            'NumberedList',
            'BulletedList',
            '-',
            'Outdent',
            'Indent',
            'JustifyLeft',
            'JustifyCenter',
            'JustifyRight',
            'JustifyBlock',
            'Undo',
            'Redo',
            'changeMode',
            'Maximize'
          ],
          '/',
          ['Font', 'FontSize', 'TextColor', 'BGColor', 'Blockquote', 'Link', 'Image', 'Html5video', 'Table', 'Smiley', 'Source']
        ],

        on: {
          paste: function (evt) {
            handleCkeditorPaste(evt);
          },
          instanceReady: function (ev) {
            this.dataProcessor.writer.setRules('p', {
              indent: options.indent,
              breakBeforeOpen: options.breakBeforeOpen,
              breakAfterOpen: options.breakAfterOpen,
              breakBeforeClose: options.breakBeforeClose,
              breakAfterClose: options.breakAfterClose
            });
            if (options.columnProperty.showMode !== '2') {
              var _path = _this.getCkeditor().plugins.changeMode.path;
              var _name = _this.getCkeditor().name;
              var iconDown = _path + 'images/iconDown.png';
              var cke_toolbar_lastChild = $('#cke_' + _name + ' .cke_toolbar:last-child');
              var cke_button__changemode_icon = $('#cke_' + _name + ' .cke_button__changemode_icon');
              cke_toolbar_lastChild.hide();
              cke_button__changemode_icon.css('backgroundImage', 'url(' + iconDown + ')');
            }
          },
          focus: function () {
            _this.$editableElem.trigger('focus');
          },
          blur: function (event) {
            _this.$editableElem.trigger('blur');
          },
          change: function () {
            _this.$editableElem.html(_this.value).trigger('change');
            var text = _this.getCkText();
            text = text.replace(/<table border="0"/g, '<table border="1" style="border-color:#333;"');
            _this.setValue(text);
            $(_this.$editableElem.next().find('.cke_wysiwyg_frame')[0].contentWindow.document)
              .find('.cke_editable_themed table')
              .attr('border', '1')
              .css({
                borderColor: '#333'
              });
          },
          key: function () {
            // _this.value = _this.getCkText();
            // console.log(_this.value + "---keydown");
          },
          loaded: function () {
            //alert("ok");
            _this.invoke('initOk');
            _this.$editableElem.trigger('loaded.cheditor');
            //初始化完成后，根据是否存在可编辑的div标签，如果是就重置富文本body的可编辑属性
            var contentstr = this._.data || '';
            if (contentstr.indexOf('textContainer') == -1) {
              _this.invoke('initOk');
              return;
            } else {
              _this.invoke('initOk');
              var editorBody = this.document.getBody();
              var timer = setTimeout(function () {
                editorBody.setAttribute('contenteditable', 'false');
                clearTimeout(timer);
              }, 100);
            }
          }
        }
      });
    },

    /**
     *
     * @param {String} eventname
     * @param {Function} event
     * @param {Boolean} custom
     */
    bind: function (eventname, event, custom) {
      if (custom) {
        $.wControlInterface.bind.call(this, eventname, event, custom);
      } else {
        _this.getCkeditor().on(eventname, event);
      }
    },

    getCkeditor: function () {
      var name = this.$editableElem.attr('id');
      var oEditor = CKEDITOR.instances[name];
      return oEditor;
    },

    getCkText: function () {
      return this.getCkeditor().getData();
    },

    getCkHtml: function () {
      return this.getCkeditor().document.getBody().getHtml();
    },

    // set方法............................................................//

    // 设置必输
    setRequired: function (isrequire) {
      $.ControlUtil.setRequired(isrequire, this.options);
    },

    setCkValue: function (value) {
      if (value != null) {
        this.getCkeditor().setData(value);
        this.getCkeditor().fire('change');
      }
    },

    // 显示为lablel
    setDisplayAsLabel: function () {
      // $.ControlUtil.setDisplayAsLabel(this.$element,this.options,false,this);
      var self = this;
      var options = self.options;
      var readStyle = options.columnProperty.readStyle;
      if (readStyle === dyshowType.readonly) {
        self.setDisplayAsCtl();
        self.setEnable(false);
      } else {
        if (self.$labelElem && self.$labelElem.children().length) {
          // return;
        }
        $.wControlInterface.setDisplayAsLabel.call(this);
        this.$placeHolder.parents('td').first().css('white-space', '');
        this.hideDiv();
        self.listeningMediaComplete();
      }
    },
    //监听富文本框媒体文件加载完成事件
    listeningMediaComplete: function () {
      var self = this;
      var video = self.$labelElem.find('.ckeditor-html5-video video')[0];
      if (!video) {
        return;
      }
      video.addEventListener('loadedmetadata', () => {
        $(document).trigger('widgetMainResize');
      });
    },

    hideDiv: function () {
      var _this = this;
      if (_this.$editableElem == null) {
        return;
      }
      if (
        _this.$editableElem.siblings("div[role='application']").size() == 0 && // ie外的浏览器
        _this.$editableElem.siblings("span[role='application']").size() == 0 // ie
      ) {
        // 由于ckeditor是异步的，所在这里需要等待

        window.setTimeout(function () {
          _this.hideDiv();
        }, 10);
      } else {
        var ckeditor = _this.getCkeditor();
        if (ckeditor.document && ckeditor.document.$) {
          // 暂停声音媒体
          setTimeout(function () {
            ckeditor.document.$.querySelectorAll('video,audio').forEach(function (media, idx) {
              media.pause && media.pause();
            });
          }, 1000);
        }
        _this.$editableElem.siblings("span[role='application']").hide();
        _this.$editableElem.siblings("div[role='application']").hide();
      }
    },

    showEditableElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        self.createEditableElem();
        self.initInputEvents();
      }
      self.$editableElem.hide();
      self.$editableElem.next().show();
    },

    // 显示为控件
    setDisplayAsCtl: function () {
      var self = this;
      // this.value = this.getValue();
      $.wControlInterface.setDisplayAsCtl.call(this);
      // $.ControlUtil.setDisplayAsCtl(this.$element,this.options);
      if (self.$labelElem) {
        self.$labelElem.find('video,audio').each(function (idx, media) {
          media.pause && media.pause();
        });
      }
      this.$editableElem.hide();
      this.$editableElem.next().show();
    },

    // get..........................................................//

    // 返回控件值
    getValue: function () {
      if (this.$editableElem == null || this.options.isShowAsLabel) {
        return this.value;
      }
      this.value = this.getCkText();
      if (this.value.indexOf('contenteditable') == -1) {
        return this.value;
      } else {
        this.value = this.value.replace(/true/, 'false');
        return this.value;
      }
    },

    // 一些其他method ---------------------
    getwckeditorInstance: function () {
      return CKEDITOR.instances[this.getCtlName()];
    },

    setReadOnly: function (isReadOnly) {
      //if (this.isShowAsLabel()) {
      //return;
      //}
      if (isReadOnly) {
        this.options.columnProperty.showType = dyshowType.readonly;
        this.setDisplayAsLabel();
      } else {
        this.options.columnProperty.showType = dyshowType.edit;
        this.setDisplayAsCtl();
      }
    },

    //富文本上传图片的url地址设置，该url对应的控制层接收图片,该控制层会返回一个url给富文本框显示
    setFilebrowserImageUploadUrl: function (url) {
      //CKEDITORConfig这个变量在\resources\ckeditor4.4.3\config.js里面有定义
      CKEDITORConfig.filebrowserImageUploadUrl = url;
    },
    //获取接收图片的服务器地址
    getFilebrowserImageUploadUrl: function () {
      //CKEDITORConfig这个变量在\resources\ckeditor4.4.3\config.js里面有定义
      return CKEDITORConfig.filebrowserImageUploadUrl;
    }
  };

  /*
   * wckeditor PLUGIN DEFINITION =========================
   */
  $.fn.wckeditor = function (option) {
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
        data = $this.data('wckeditor');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wckeditor'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new Ckeditor($this, options);
      $.extend(data, $.wControlInterface);

      $.extend(data, $.wTextCommonMethod);
      $.extend(data, $.Ckeditor);
      data.init();
      $this.data('wckeditor', data);
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

  $.fn.wckeditor.Constructor = Ckeditor;

  $.fn.wckeditor.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性
    isShowAsLabel: false,
    // 控件私有属性
    disabled: false,
    readOnly: false,
    isHide: false, // 是否隐藏
    indent: true,
    breakBeforeOpen: false,
    breakAfterOpen: false,
    breakBeforeClose: false,
    breakAfterClose: false,
    allowedContent: true
  };
})(jQuery);
