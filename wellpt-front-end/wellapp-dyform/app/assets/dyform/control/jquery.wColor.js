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

  var Colors = function ($placeHolder /* 占位符 */, options) {
    // this.$element = $(element);
    this.options = $.extend({}, $.fn['wcolor'].defaults, options);
    this.value = '';
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = $placeHolder;
  };

  Colors.prototype = {
    constructor: Colors
  };

  $.Colors = {
    colorArr: [
      '#C00000',
      '#FF0000',
      '#FFC000',
      '#FFFF00',
      '#92D050',
      '#00B050',
      '#00B0F0',
      '#0070C0',
      '#002060',
      '#7030A0',
      '#FFFFFF',
      '#000000'
    ],
    setDisplayAsCtl: function () {
      // 显示为可编辑框
      var options = this.options;
      options.isShowAsLabel = false;
      if (this.$editableElem == null) {
        this.createEditableElem();
        this.initInputEvents();
      }
      this.$editableElem.show();
      this.$editableElem.removeAttr('disabled');
    },
    setDisplayAsLabel: function () {
      var self = this;
      var options = self.options;
      var readStyle = options.columnProperty.readStyle;
      if (this.$editableElem == null) {
        this.createEditableElem();
        this.initInputEvents();
      }
      self.$editableElem.show();
      self.$editableElem.attr('disabled', 'disabled');
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
    setStyleRelativeField: function () {
      var self = this;
      var relatedField = self.options.relatedField;
      if (relatedField == '') {
        return;
      }
      var value = self.getValue();

      var control = $.ControlManager.getCtl(self.getContronId(relatedField));
      if (typeof control == 'undefined' || control == null) {
        return;
      }
      var realetiveDom = control.options.isShowAsLabel ? $(control.$labelElem) : $(control.$editableElem);
      realetiveDom.css({
        color: value
      });
      if (!control.options.isShowAsLabel) {
        if ($(self.$editableElem[0]).parents('td').find(realetiveDom).size() <= 0) {
          realetiveDom.parents('td').append(self.$editableElem);
        }
        var optionLeft = self.options.selectMode == '2' ? '-123px' : -$('.color-options', self.$editableElem[0]).width() - 2 + 'px';
        var miniColorleft = self.options.selectMode == '2' ? '-53px' : '';
        $(self.$editableElem[0]).parents('td').css({
          position: 'relative'
        });
        $(self.$editableElem[0]).css({
          position: 'absolute',
          left: realetiveDom.width() - 2 + 'px'
        });
        $('.color-selected', self.$editableElem[0]).css({
          'border-radius': '0 3px 3px 0'
        });
        $('.color-options', self.$editableElem[0]).css({
          left: optionLeft
        });
        $('.minicolors .minicolors-panel', self.$editableElem[0]).css({
          left: miniColorleft
        });
      }
    },
    drawOptionElem: function (options) {
      var self = this;
      var html =
        "<div class='color-wrap'>" +
        "<ul class='color-selected' data-color='#0070C0'><li></li></ul>" +
        "<div class='color-options " +
        (options.selectMode == 1 ? 'color-inline' : 'color-box') +
        "'><ul class='color-list'>";
      $.each(this.colorArr, function (i, n) {
        html += "<li data-color='" + n + "' class='color-items' style='background:" + n + ";'></li>";
      });
      html += '</ul>';
      if (options.selectMode == 2) {
        html +=
          "<div class='color-more'>更多<i class='iconfont icon-ptkj-xianmiaojiantou-you'></i></div>" +
          "<input class='color-input' id='colorInput_" +
          self.getCtlName() +
          "' type='text' name='color'>";
      }
      html += '</div></div>';
      this.$editableElem.html(html);
      this.setValue($('.color-selected', this.$editableElem[0]).data('color'));
      self.setStyleRelativeField();
      setTimeout(function () {
        self.setStyleRelativeField();
      }, 0);
    },

    elementEvent: function () {
      var _this = this;
      $(document).bind('click', function (e) {
        // 点击关闭颜色选择框
        e.stopPropagation();
        var selectEle = $('.color-selected', _this.$editableElem[0]);
        if (!$(e.target).hasClass('minicolors-grid') && !$(e.target).hasClass('minicolors-slider') && selectEle.hasClass('options-open')) {
          selectEle.removeClass('options-open').next().hide();
        }
      });
      $('.color-selected', this.$editableElem[0]).live('click', function (e) {
        if ($(this).closest('span.editableClass').attr('disabled') === 'disabled') {
          return false;
        }
        // 开启或者关闭选色框
        e.stopPropagation();

        var selected = $(this).data('color');
        if ($(this).hasClass('options-open')) {
          $(this).removeClass('options-open').next().hide();
        } else {
          if ($('.more-tags-box:visible').size() > 0) {
            $('.more-tags-box:visible').hide();
          }
          if ($('.options-open').size() > 0) {
            $('.options-open').removeClass('options-open').next().hide();
          }
          $(this).addClass('options-open').next().show();
          var items = $('.color-items', _this.$editableElem[0]);

          $.each(items, function (index, item) {
            if ($(item).data('color') == selected) {
              var fontColor = selected == '#FFFFFF' ? 'hasWhite' : '';
              $(item).siblings().removeClass('iconfont icon-ptkj-dagou');
              $(item).addClass('iconfont icon-ptkj-dagou ' + fontColor);
            } else {
              $(item).removeClass('iconfont icon-ptkj-dagou');
            }
          });

          if ($(_this.$editableElem[0]).find('.minicolors').size() > 0) {
            $(_this.$editableElem[0]).find('.minicolors').hide().prev().show().prev().show();
          }
        }
      });

      $('.color-items', this.$editableElem[0]).live('click', function (e) {
        // 勾选或取消颜色
        e.stopPropagation();
        $(this)
          .parents('.color-wrap')
          .find('.color-selected li')
          .css({
            background: $(this).data('color')
          });

        var fontColor = $(this).data('color') == '#FFFFFF' ? 'hasWhite' : '';

        $(this).siblings().removeClass('iconfont icon-ptkj-dagou');
        $(this).addClass('iconfont icon-ptkj-dagou ' + fontColor);

        // $(".color-selected",_this.$editableElem[0]).trigger("click")
        $('.color-selected', _this.$editableElem[0]).removeClass('options-open').next().hide();

        _this.setValue($(this).data('color'));
      });

      if (_this.options.selectMode == '2') {
        $('.color-input', _this.$editableElem[0]).minicolors({
          control: 'hue',
          format: 'hex',
          color: '#0070C0',
          letterCase: 'lowercase',
          opacity: false,
          position: 'bottom left',
          theme: 'bootstrap',
          change: function (value, opacity) {
            var self = $(this);
            $('.color-input', _this.$editableElem[0]).focus();
            self.parents('.color-wrap').find('.color-selected').data('color', value).find(' li').css({
              background: value
            });
            _this.setValue(value);
          },
          hide: function () {
            var self = $(this);
            $('.color-input', _this.$editableElem[0]).hide();
            $(_this.$editableElem[0]).find('.minicolors').hide();
            self
              .parents('.color-options')
              .removeClass('show-more-color')
              .prev('.color-selected')
              .removeClass('options-open')
              .next()
              .hide()
              .removeClass('auto-open-close');
          },
          show: function () {
            $('.color-input', _this.$editableElem[0]).focus();
            _this.setStyleRelativeField();
          }
        });
      }

      $('.color-more', this.$editableElem[0]).live('click', function (e) {
        e.stopPropagation();
        $(this).parents('.color-options').addClass('show-more-color');
        $(this).hide().prev().hide();
        $('.color-input', _this.$editableElem[0]).show();
        if ($(_this.$editableElem[0]).find('.minicolors').size() > 0) {
          $(_this.$editableElem[0]).find('.minicolors').show();
          $('.color-input', _this.$editableElem[0]).focus();
        } else {
          $('.color-input', _this.$editableElem[0]).focus();
        }
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
        valueObj = '';
      }

      if (valueObj.length > 0) {
        $(self.$editableElem[0]).find('.color-selected').data('color', valueObj[0]).find('li').css({
          background: valueObj[0]
        });
        self.setStyleRelativeField();
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

  $.fn.wcolor = function (option) {
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
        data = $this.data('wcolor');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wcolor'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new Colors($(this), options);
      $.extend(data, $.wControlInterface);
      $.extend(data, $.Colors);
      data.init();
      $this.data('wcolor', data);
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

  $.fn.wcolor.Constructor = Colors;

  $.fn.wcolor.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性

    isShowAsLabel: false,
    // 私有属性
    isHide: false, // 是否隐藏
    checked: false,
    disabled: false,
    ctrlField: null,
    selectMode: '1', // 选择模式，单选1，多选2
    relatedField: '', // 关联字段
    value: '' // 控件的值,字符串形式
  };
})(jQuery);
