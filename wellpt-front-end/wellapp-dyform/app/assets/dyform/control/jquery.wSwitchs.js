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

  var Switchs = function ($placeHolder /* 占位符 */, options) {
    // this.$element = $(element);
    this.options = $.extend({}, $.fn['wswitchs'].defaults, options);
    this.value = '';
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = $placeHolder;
  };

  Switchs.prototype = {
    constructor: Switchs
  };

  $.WSwitchs = {
    setDisplayAsCtl: function () {
      // 显示为可编辑框
      var options = this.options;
      options.isShowAsLabel = false;
      if (this.$editableElem == null) {
        this.createEditableElem();
        this.initInputEvents();
      }
      this.$editableElem.show();
    },
    setDisplayAsLabel: function () {
      var self = this;
      var options = self.options;
      var readStyle = options.columnProperty.readStyle;
      if (self.$editableElem == null) {
        self.createEditableElem();
        self.initInputEvents();
      }
      self.$editableElem.show().find('.switch-wrap').addClass('disabledEdit').css({
        cursor: 'not-allowed'
      });
    },
    setValue2LabelElem: function () {
      var self = this;
      //if (self.$labelElem == null) {
      //return;
      //}
      if (this.$editableElem == null) {
        this.createEditableElem();
        this.initInputEvents();
      }
      self.$labelElem && self.$labelElem.hide();
    },

    createEditableElem: function () {
      if (this.$editableElem != null) {
        // 创建可编辑框
        return;
      }

      var ctlName = this.getCtlName();
      var editableElem = document.createElement('span');
      editableElem.setAttribute('class', this.editableClass);
      editableElem.setAttribute('name', ctlName);

      this.$placeHolder.after($(editableElem));
      this.$editableElem = this.$placeHolder.next('.' + this.editableClass);
      this.drawOptionElem(this.options);
      this.elementEvent();
    },

    drawOptionElem: function (options) {
      var html = '<div class="switch-wrap ' + (options.switchsVal == '0' ? '' : 'active') + '">';
      if (options.openText != '') {
        html += '<span class="switch-text switch-open">' + options.openText + '</span>';
      }

      html += '<span class="switch-radio" data-status="' + options.switchsVal + '"></span>';
      if (options.closeText != '') {
        html += '<span class="switch-text switch-close">' + options.closeText + '</span>';
      }
      html += '</div>';

      this.$editableElem.html(html);
      this.setValue($('.switch-radio', this.$editableElem[0]).data('status').toString());
    },

    elementEvent: function () {
      var _this = this;

      $('.switch-wrap', this.$editableElem[0]).live('click', function () {
        if ($(this).hasClass('disabledEdit') || $(this).closest('span.editableClass').attr('disabled') === 'disabled') {
          return false;
        }
        if ($(this).hasClass('active')) {
          $(this).removeClass('active').find('.switch-radio').data('status', '0');
          $(this).find('.switch-close').show();
          $(this).find('.switch-open').hide();
        } else {
          $(this).addClass('active').find('.switch-radio').data('status', '1');
          $(this).find('.switch-open').show();
          $(this).find('.switch-close').hide();
        }
        _this.setValue($('.switch-radio', _this.$editableElem[0]).data('status').toString());
      });
    },
    isValueMap: function () {
      return true;
    },
    setValue2EditableElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        return;
      }
      var valueObj = self.value;
      var displayVal = self.getDisplayLabel(valueObj);
      if ($.isArray(valueObj)) {
      } else if (typeof valueObj === 'string') {
        valueObj = valueObj.split(self.getSeparator());
      } else {
        valueObj = [valueObj];
      }
      if (valueObj.length > 0) {
        $('.switch-radio', self.$editableElem[0]).data('status', valueObj[0]);
        if (valueObj[0] == '0') {
          $('.switch-wrap', this.$editableElem[0]).removeClass('active');
        } else {
          $('.switch-wrap', this.$editableElem[0]).addClass('active');
        }
      }
    },
    get$InputElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        return $([]); // 还没生成输入框时，先返回一个jquery对象
      } else {
        return $("span[name='" + self.getCtlName() + "']", self.$editableElem[0]);
      }
    }
  };

  $.fn.wswitchs = function (option) {
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
        data = $this.data('wswitchs');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wswitchs'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new Switchs($(this), options);
      $.extend(data, $.wControlInterface);
      $.extend(data, $.WSwitchs);
      data.init();
      $this.data('wswitchs', data);
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

  $.fn.wswitchs.Constructor = Switchs;

  $.fn.wswitchs.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性

    isShowAsLabel: false,
    // 私有属性
    isHide: false, // 是否隐藏
    checked: false,
    disabled: false,
    ctrlField: null,
    openText: '',
    closeText: '',
    switchsVal: '0'
  };
})(jQuery);
