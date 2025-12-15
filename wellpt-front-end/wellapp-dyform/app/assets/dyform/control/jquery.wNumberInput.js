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
   * NUMBERINPUT CLASS DEFINITION ======================
   */
  var NumberInput = function ($placeHolder, options) {
    this.options = $.extend({}, $.fn['wnumberInput'].defaults, options);
    this.value = '';
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = $placeHolder;
  };

  var MAX_INT_VALUE = new Number(2147483647);
  var MIN_INT_VALUE = new Number(-2147483648);
  var MAX_LONG_VALUE = new Number(9223372036854775807);
  var MIN_LONG_VALUE = new Number(-9223372036854775808);
  var MAX_DOUBLE_VALUE = new Number(1.7976931348623157e308);
  var MIN_DOUBLE_VALUE = new Number(4.9e-324);
  var KEY_CODE_MAP = {
    48: '0',
    49: '1',
    50: '2',
    51: '3',
    52: '4',
    53: '5',
    54: '6',
    55: '7',
    56: '8',
    57: '9'
  };
  $.NumberInput = {
    createEditableElem: function () {
      var _self = this;
      if (_self.$editableElem != null) {
        // 创建可编辑框
        return;
      }
      var options = _self.options;
      var ctlName = _self.getCtlName();
      var editableElem = document.createElement('input');
      if (
        (options.columnProperty.dbDataType == '13' ||
          options.columnProperty.dbDataType == '131' ||
          options.columnProperty.dbDataType == '132') &&
        options.columnProperty.length > 9
      ) {
        options.columnProperty.length = '9';
      } else if (options.columnProperty.dbDataType == '14' && options.columnProperty.length > 16) {
        options.columnProperty.length = '16';
      } else if (options.columnProperty.dbDataType == '15' && options.columnProperty.length > 12) {
        options.columnProperty.length = '12';
      } else if (options.columnProperty.dbDataType == '12' && options.columnProperty.length > 18) {
        options.columnProperty.length = '18';
      }
      editableElem.setAttribute('class', _self.editableClass);
      editableElem.setAttribute('name', ctlName);
      editableElem.setAttribute('type', 'text');
      editableElem.setAttribute('maxlength', options.columnProperty.length);
      var _inputCss = _self.getTextInputCss();
      // _inputCss['text-align'] = 'center';
      // _inputCss['font-size'] = '14px';
      if (_inputCss['width']) {
        _self.cssWidth = _inputCss['width'];
      }

      var numberInput = '';
      numberInput +=
        '<div class="input-group well-number-input" style="width: 100%;">' +
        '<div class="well-number-input-control" style="height:calc(' +
        _inputCss['height'] +
        ' + 2px)">' +
        '<input type="text" class="form-control ' +
        _self.editableClass +
        '" name="' +
        ctlName +
        '" maxlength="' +
        options.columnProperty.length +
        '">' +
        '</div>' +
        '</div>';

      _inputCss['width'] = '100%';
      _inputCss['background'] = 'transparent';
      _self.$placeHolder.after(numberInput);

      if (_self.$placeHolder.next('.input-group').length) {
        _self.$numberInputWrap = _self.$placeHolder.next('.input-group');
        _self.$editableElem = _self.$placeHolder.next('.input-group').find('.' + _self.editableClass);
      } else {
        _self.$editableElem = _self.$placeHolder.next('.' + _self.editableClass);
      }
      // _self.$numberInputWrap.css(_inputCss);
      _self.$editableElem.css(_inputCss);
      if (options.columnProperty.minNum) {
        _self.$editableElem.attr('min', options.columnProperty.minNum);
      }
      if (options.columnProperty.maxNum) {
        _self.$editableElem.attr('max', options.columnProperty.maxNum);
      }
      _self.htmlelement = _self.$editableElem[0];

      var evtFun = function () {
        var val = _self.$editableElem.val();
        _self.setValue(val, false);
      };
      var formatNumber = _self.options.columnProperty.formatNumber;
      _self.$editableElem
        .on('paste', function () {
          window.setTimeout(function () {
            _self.setValue(_self.$editableElem.val(), false);
            formatNumber && _self.setValue2EditableElem();
          }, 100);
        })
        .on('blur', function () {
          var $this = $(this);
          var val = $this.val();
          var oldValue = $this.data('oldValue');
          if (isNaN(Number(val))) {
            val = oldValue;
            $this.val(oldValue);
          }
          enableClick(val);
          evtFun(); // 先设值，再展现千位分隔符
          formatNumber && _self.setValue2EditableElem();
        })
        .on('focus', function () {
          var val = $(this).val();
          $(this).data('oldValue', val);
          formatNumber && _self.setValue2EditableElem();
        })
        .on('keyup', evtFun);
      /*
       * this.$editableElem.bind('change',function(){
       * _this.setValue(_this.$editableElem.val(), false); });
       */

      // this.$editableElem.keydown($.proxy(this._preCheckInput, this));
      // this.$editableElem.keyup($.proxy(this._postCheckInput, this));
      var operator = _self.options.operator;
      var inputMode = _self.options.commonProperty.inputMode;
      if (operator) {
        function mul(a, b) {
          var c = 0,
            d = a.toString(),
            e = b.toString();
          try {
            c += d.split('.')[1].length;
          } catch (f) {}
          try {
            c += e.split('.')[1].length;
          } catch (f) {}
          return (Number(d.replace('.', '')) * Number(e.replace('.', ''))) / Math.pow(10, c);
        }

        function add(a, b) {
          var c, d, e;
          try {
            c = a.toString().split('.')[1].length;
          } catch (f) {
            c = 0;
          }
          try {
            d = b.toString().split('.')[1].length;
          } catch (f) {
            d = 0;
          }
          return (e = Math.pow(10, Math.max(c, d))), (mul(a, e) + mul(b, e)) / e;
        }

        function sub(a, b) {
          var c, d, e;
          try {
            c = a.toString().split('.')[1].length;
          } catch (f) {
            c = 0;
          }
          try {
            d = b.toString().split('.')[1].length;
          } catch (f) {
            d = 0;
          }
          return (e = Math.pow(10, Math.max(c, d))), (mul(a, e) - mul(b, e)) / e;
        }
        var dbDataType = _self.options.columnProperty.dbDataType;

        //判断是否可以继续加减
        function enableClick(val) {
          var plusBtn = _self.$editableElem.parent().next();
          var minusBtn = _self.$editableElem.parent().prev();
          var max = _self.options.columnProperty.maxNum;
          if (dbDataType === '132') {
            max = max && max < 0 ? max : 0;
          }
          var min = _self.options.columnProperty.minNum;
          if (dbDataType === '131') {
            min = min && min > 0 ? min : 0;
          }
          var newVal;
          if (dbDataType != dyFormDataType['float'] && dbDataType != dyFormDataType['double']) {
            newVal = parseInt(val) + operator.plusUnit;
          } else {
            newVal = add(parseFloat(val), operator.plusUnit);
          }
          if (max !== '' && newVal > max) {
            plusBtn.addClass('disabled');
          } else {
            plusBtn.removeClass('disabled');
          }
          if (dbDataType != dyFormDataType['float'] && dbDataType != dyFormDataType['double']) {
            newVal = parseInt(val) - operator.minusUnit;
          } else {
            newVal = sub(parseFloat(val), operator.minusUnit);
          }
          if (min !== '' && newVal < min) {
            minusBtn.addClass('disabled');
          } else {
            minusBtn.removeClass('disabled');
          }
        }

        // _self.$editableElem.after("<span class='plus' style='cursor:pointer' inputMode='" + inputMode
        // 	+ "'>+</span>");
        var addonNum = 0;
        if (operator.plus) {
          addonNum++;
          _self.$editableElem.parent().addClass('rightNoRadius');
          _self.$editableElem
            .parent()
            .after(
              '<span class="input-group-addon plus" style="width: 35px;cursor: pointer;height:calc(' +
                _inputCss['height'] +
                ' + 2px);line-height:' +
                _inputCss['height'] +
                '" inputMode="' +
                inputMode +
                '"><i class="iconfont icon-ptkj-jiahao"></i></span>'
            );
          _self.$editableElem
            .parent()
            .next()
            .click(function () {
              var $this = $(this);
              if ($this.hasClass('disabled')) {
                return;
              }
              var val = _self.value;

              if ($.trim(val).length == 0) {
                val = 0;
                // return;
              }
              if (
                dbDataType != dyFormDataType['float'] &&
                dbDataType != dyFormDataType['double'] &&
                dbDataType != dyFormDataType['number']
              ) {
                val = parseInt(val) + operator.plusUnit;
              } else {
                val = add(parseFloat(val), operator.plusUnit);
              }

              _self.setValue(val + '');
              enableClick(val);
            });
        } else {
          _self.$editableElem.parent().next().addClass('disabled');
        }
        // _self.$editableElem.before("<span class='minus' style='cursor:pointer' inputMode='" + inputMode
        // 	+ "'>-</span>");
        if (operator.minus) {
          addonNum++;
          _self.$editableElem.parent().addClass('leftNoRadius');
          _self.$editableElem
            .parent()
            .before(
              '<span class="input-group-addon minus" style="width: 35px;cursor: pointer;height:calc(' +
                _inputCss['height'] +
                ' + 2px);line-height:' +
                _inputCss['height'] +
                '" inputMode="' +
                inputMode +
                '"><i class="iconfont icon-ptkj-jianhao"></i></span>'
            );
          _self.$editableElem
            .parent()
            .prev()
            .click(function () {
              var $this = $(this);
              if ($this.hasClass('disabled')) {
                return;
              }
              var val = _self.value;
              if ($.trim(val).length == 0) {
                // return;
                val = 0;
              }
              if (
                dbDataType != dyFormDataType['float'] &&
                dbDataType != dyFormDataType['double'] &&
                dbDataType != dyFormDataType['number']
              ) {
                val = parseInt(val) - operator.minusUnit;
              } else {
                val = sub(parseFloat(val), operator.minusUnit);
              }
              _self.setValue(val + '');
              enableClick(val);
            });
        } else {
          _self.$editableElem.parent().prev().addClass('disabled');
        }

        var outerWidth = _self.$placeHolder.parents('td').width();
        if (_self.cssWidth && parseInt(_self.cssWidth) > outerWidth) {
          _self.$numberInputWrap.css('width', outerWidth);
        } else {
          _self.$numberInputWrap.css({
            width: _self.cssWidth
          });
        }

        // if (addonNum) {
        //     if (_self.width && _self.width !== '100%') {
        //         if (outerWidth < addonNum * 35 + parseInt(_inputCss.width) + 2) {
        //             _inputCss.width = outerWidth - 35 * addonNum - 2 + 'px';
        //             _self.$editableElem.css(_inputCss);
        //         } else {
        //             _self.$numberInputWrap.css('width', parseInt(_inputCss.width) + addonNum * 35 + 2 + 'px');
        //         }
        //     }
        // } else {
        //     if (_inputCss.width && _inputCss.width !== '100%') {
        //         if (outerWidth < parseInt(_inputCss.width)) {
        //             _inputCss.width = '100%';
        //             _self.$editableElem.css(_inputCss);
        //         } else {
        //             _self.$numberInputWrap.css('width', parseInt(_inputCss.width) + 'px');
        //         }
        //     }
        // }
      }
    },
    /* 设值到标签中 */
    setValue2LabelElem: function () {
      var self = this;
      if (self.$labelElem == null) {
        return;
      }
      var value = self.getDisplayValue();
      self.$labelElem.text(value);
    },
    /* 设置到可编辑元素中 */
    setValue2EditableElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        return;
      }
      var value = self.value;
      if (typeof value === 'string' && value.indexOf(',') > -1) {
        self.value = value = value.replace(/,/g, '');
      }
      if (false === self.$editableElem.is(':focus')) {
        value = self.getDisplayValue();
      }
      if (self.$editableElem.val() === value) {
        return;
      }
      self.$editableElem.val(value);
    },
    getDisplayValue: function () {
      var self = this;
      var value = self.value;
      var formatNumber = self.options.columnProperty.formatNumber;
      if (formatNumber && $.trim(value).length > 0) {
        value = value + '';
        var scale = 0;
        if (value.indexOf('.') > -1) {
          scale = value.length - value.indexOf('.') - 1;
        }
        value = new Number(value).toLocaleString('en', {
          minimumFractionDigits: scale
        });
      }
      return value;
    },
    // 二开传数字范围,参数示例：
    // var ranges = [{
    //   minNum: 1,
    //   maxNum: 10,
    //   minOperator: "2",     // 1代表≤，2代表＜
    //   maxOperator: "1"      // 1代表≤，2代表＜
    // }]
    setNumberRange: function (ranges) {
      if (ranges && $.isArray(ranges)) {
        this.options.numberRange = ranges;
      } else if (ranges) {
        appModal.error('数字范围必须是数组形式！');
      }
    },

    get$WrapElem: function () {
      return this.$numberInputWrap;
    },

    showEditableElem: function () {
      if (this.$editableElem == null) {
        this.createEditableElem();
        this.initInputEvents();
      }
      this.$editableElem
        .css({
          resize: 'vertical'
        })
        .show();
      this.$placeHolder.siblings('.well-number-input').show();
    },

    hideEditableElem: function () {
      if (this.$editableElem == null) {
        return;
      }
      this.$editableElem.hide();
      this.hideOperator();
      this.$editableElem.siblings('.Validform_checktip').hide();
      this.$placeHolder.siblings('.well-number-input').hide();
    },

    // 显示为可编辑框
    setDisplayAsCtl: function () {
      var options = this.options;
      options.isShowAsLabel = false;

      this.showEditableElem();
      this.showOperator();
      this.hideLabelElem();
      this.setValue2EditableElem();
    },

    hideOperator: function () {
      // alert(1);
      var operator = this.options.operator;
      if (operator) {
        if (operator.minus) {
          this.$editableElem.prev('.minus').hide();
        }
        if (operator.plus) {
          // console.log(this.$editableElem.siblings(".minus").size());
          this.$editableElem.siblings('.plus').hide();
        }
      }
    },
    showOperator: function () {
      var operator = this.options.operator;
      if (operator) {
        if (operator.minus) {
          this.$editableElem.prev('.minus').show();
        }
        if (operator.plus) {
          this.$editableElem.siblings('.plus').show();
        }
      }
    },
    _preCheckInput: function (event) {
      this.oldValue = this.$editableElem.val();
      // Allow: backspace, delete, tab and escape
      if (
        event.keyCode == 46 ||
        event.keyCode == 8 ||
        event.keyCode == 9 ||
        event.keyCode == 27 ||
        // Allow: Ctrl+A
        (event.keyCode == 65 && event.ctrlKey === true) ||
        // Allow: home, end, left, right
        (event.keyCode >= 35 && event.keyCode <= 39)
      ) {
        // let it happen, don't do anything
        return true;
      }
      // "-" 189
      // if ((event.keyCode == 189||event.keyCode == 173) &&
      // this.options.negative == true) {
      // return true;
      // }
      // "." 190/110
      if (
        this.oldValue.indexOf('.') > 0 &&
        (event.keyCode == 190 || event.keyCode == 110) &&
        this.options.columnProperty.dbDataType == '15'
      ) {
        return false;
      }
      // 火狐的"-"号是173的code?
      if (
        !(event.keyCode >= 48 && event.keyCode <= 57) &&
        !(event.keyCode >= 95 && event.keyCode <= 105) &&
        !((event.keyCode == 190 || event.keyCode == 173 || event.keyCode == 110) && this.options.negative == true)
      ) {
        return false;
      }
      // 如果是小数，则根据decimal设置输入小数位
      if (this.options.columnProperty.dbDataType == '15') {
        var value = this.oldValue;
        var decimal = this.options.decimal;
        if (
          (event.keyCode >= 48 && event.keyCode <= 57) ||
          (event.keyCode >= 95 && event.keyCode <= 105) ||
          event.keyCode == 190 ||
          event.keyCode == 110
        ) {
          var cursorpos = getCurPosition(this.htmlelement);
          var selText = getSelectedText(this.htmlelement);
          var dotPos = value.indexOf('.');
          if (dotPos > 0 && cursorpos > dotPos) {
            if (cursorpos > dotPos + decimal) return false;
            if (selText.length > 0 || value.substr(dotPos + 1).length < decimal) return true;
            else return false;
          }
          return true;
        }
        return true;
      } else if (
        this.options.columnProperty.dbDataType == '13' ||
        this.options.columnProperty.dbDataType == '131' ||
        this.options.columnProperty.dbDataType == '132'
      ) {
        var newValue = Number(this.$editableElem.val());
        var selText = getSelectedText(this.htmlelement);
        if ((selText.length == 0 && newValue > MAX_INT_VALUE) || newValue < MIN_INT_VALUE) {
          this.$editableElem.val(this.oldValue);
          return false;
        }
      } else if (this.options.columnProperty.dbDataType == '14') {
        var newValue = Number(this.$editableElem.val());
        var selText = getSelectedText(this.htmlelement);
        if ((selText.length == 0 && newValue > MAX_LONG_VALUE) || newValue < MIN_LONG_VALUE) {
          this.$editableElem.val(this.oldValue);
          return false;
        }
      } else {
        return true;
      }
    },
    _postCheckInput: function (event) {
      var value = this.$editableElem.val();
      if (this.options.negative == true && value == '-') {
        return true;
      }

      if (this.$editableElem.val() == '') {
        return true;
      }

      var newValue = Number(this.$editableElem.val());
      if (!isNumeric(newValue)) {
        this.$editableElem.val(this.oldValue);
        return false;
      }
    },

    setReadOnly: function (isReadOnly) {
      this.render();
      if (isReadOnly) {
        this.options.columnProperty.showType = dyshowType.readonly;
        this.get$InputElem().attr('readonly', 'readonly');
        this.hideOperator();
      } else {
        this.options.columnProperty.showType = dyshowType.edit;
        this.get$InputElem().removeAttr('readonly', '');
        this.showOperator();
      }
    },
    getRule: function () {
      var rule = $.ControlUtil.getCheckRuleAndMsg(this.options)['rule'];
      var numberRule = this.getNumberRule(this.options.columnProperty.dbDataType);
      if (rule == undefined) {
        rule = numberRule;
      } else {
        $.extend(rule, numberRule);
      }

      return JSON.cStringify(rule);
    },

    getNumberRule: function (dbDataType) {
      var rule = {};
      if (dbDataType == '12') {
        // 双精度浮点数
        // @see maxlength修改 bug:49960
        var decimal = parseInt(this.options.columnProperty.decimal, 10);
        rule = {
          isFloat: {
            decimal: decimal
          },
          maxlength: '18'
        };
      } else if (dbDataType == '15') {
        // 浮点数
        // @see maxlength修改 bug:49960
        var decimal = parseInt(this.options.columnProperty.decimal, 10);
        rule = {
          isFloat: {
            decimal: decimal
          },
          maxlength: '12'
        };
      } else if (dbDataType == '13') {
        // 整数
        rule = {
          isInteger: true,
          maxlength: '9'
        };
      } else if (dbDataType == '131') {
        // 正整数
        rule = {
          isInteger: true,
          isIntGtZero: true,
          maxlength: '9'
        };
      } else if (dbDataType == '132') {
        // 负整数
        rule = {
          isInteger: true,
          isIntLtZero: true,
          maxlength: '9'
        };
      } else if (dbDataType == '14') {
        // 长整数
        rule = {
          isInteger: true,
          maxlength: '16'
        };
      } else if (dbDataType == '17') {
        // Number
        var precision = parseInt(this.options.columnProperty.precision, 10);
        var scale = parseInt(this.options.columnProperty.scale, 10);
        rule = {
          isNumber: {
            datatype: '17',
            precision: precision,
            scale: scale
          }
        };
      }
      if (this.options.columnProperty.minNum) {
        rule.minNum = parseFloat(this.options.columnProperty.minNum);
      }
      if (this.options.columnProperty.maxNum) {
        rule.maxNum = parseFloat(this.options.columnProperty.maxNum);
      }

      if (this.options.numberRange && this.options.numberRange.length > 0) {
        rule.numberRange = this.options.numberRange;
      }

      return rule;
    },

    getMessage: function () {
      var msg = $.ControlUtil.getCheckRuleAndMsg(this.options)['msg'];
      var numberMsg = this.getNumberMessage(this.options.columnProperty.dbDataType);
      if (msg == undefined) {
        msg = numberMsg;
      } else {
        $.extend(msg, numberMsg);
      }
      return JSON.cStringify(msg);
    },

    getNumberMessage: function (dbDataType) {
      var msg = {};
      if (dbDataType == '12') {
        // 双精度浮点数
        var decimal = parseInt(this.options.columnProperty.decimal, 10) || 0;
        msg = {
          isFloat: '  双精度浮点数,小数位不超过' + decimal + '位',
          maxlength: '双精度浮点数,不得超过  {0} 个字符'
        };
      } else if (dbDataType == '15') {
        // 浮点数
        var decimal = parseInt(this.options.columnProperty.decimal, 10) || 0;
        msg = {
          isFloat: '  浮点数,小数位不超过' + decimal + '位',
          maxlength: '浮点数,不得超过  {0} 个字符'
        };
      } else if (dbDataType == '13') {
        // 整数
        msg = {
          isInteger: '  整数',
          maxlength: '整数,不得超过  {0} 个字符'
        };
      } else if (dbDataType == '131') {
        // 正整数
        msg = {
          isInteger: '非整数',
          isIntGtZero: '  正整数',
          maxlength: '正整数,不得超过  {0} 个字符'
        };
      } else if (dbDataType == '132') {
        // 负整数
        msg = {
          isInteger: '非整数',
          isIntLtZero: '  负整数',
          maxlength: '负整数,不得超过  {0} 个字符'
        };
      } else if (dbDataType == '14') {
        // 长整数
        msg = {
          isInteger: '  长整数',
          maxlength: '长整数,不得超过  {0} 个字符'
        };
      } else if (dbDataType == '17') {
        // Number
        var precision = this.options.columnProperty.precision;
        var scale = this.options.columnProperty.scale;
        msg = {
          isNumber: '  非有效数字:精度位' + precision + ',小数位' + scale
        };
      }
      if (this.options.columnProperty.minNum) {
        msg.minNum = '不能小于最小值' + this.options.columnProperty.minNum;
      }
      if (this.options.columnProperty.maxNum) {
        msg.maxNum = '不能大于最大值' + this.options.columnProperty.maxNum;
      }
      if (this.options.numberRange && this.options.numberRange.length > 0) {
        var rangeTips = [];
        $.each(this.options.numberRange, function (i, item) {
          var minOperator = item.minOperator == '1' ? '≤' : '＜';
          var maxOperator = item.maxOperator == '1' ? '≤' : '＜';
          if (item.minNum != '' && item.maxNum != '') {
            rangeTips.push(item.minNum + minOperator + '数字' + maxOperator + item.maxNum);
          } else if (item.minNum != '') {
            rangeTips.push(item.minNum + minOperator + '数字');
          } else if (item.maxNum != '') {
            rangeTips.push('数字' + maxOperator + item.maxNum);
          }
        });
        msg.numberRange = '不满足数字范围【' + rangeTips.join('，') + '】';
      }
      return msg;
    },
    setMinNum: function (minNum) {
      var self = this;
      self.options.columnProperty.minNum = minNum ? minNum.toString() : null;
      if (self.$editableElem) {
        if (null == minNum) {
          self.$editableElem.removeAttr('min');
        } else {
          self.$editableElem.attr('min', minNum);
        }
      }
      self.initValidate(true);
    },
    setMaxNum: function (maxNum) {
      var self = this;
      self.options.columnProperty.maxNum = maxNum ? maxNum.toString() : null;
      if (self.$editableElem) {
        if (null == maxNum) {
          self.$editableElem.removeAttr('max');
        } else {
          self.$editableElem.attr('max', maxNum);
        }
      }
      self.initValidate(true);
    }
  };

  // 获取当前光标在文本框的位置
  function getCurPosition(domObj) {
    var position = 0;
    if (domObj.selectionStart || domObj.selectionStart == '0') {
      position = domObj.selectionStart;
    }
    return position;
  }
  // 获取当前文本框选中的文本
  function getSelectedText(domObj) {
    if (domObj.selectionStart || domObj.selectionStart == '0') {
      return domObj.value.substring(domObj.selectionStart, domObj.selectionEnd);
    }
    return '';
  }

  function isNumeric(obj) {
    return !isNaN(parseFloat(obj)) && isFinite(obj);
  }
  /*
   * NUMBERINPUT PLUGIN DEFINITION =========================
   */
  $.fn.wnumberInput = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }

    if (typeof option == 'string') {
      if (option === 'getObject') {
        // 通过getObject来获取实例
        var $this = $(this),
          data = $this.data('wnumberInput');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wnumberInput'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new NumberInput($(this), options);
      $.extend(data, $.wControlInterface);
      $.extend(data, $.wTextCommonMethod);
      $.extend(data, $.NumberInput);
      $this.data('wnumberInput', data);
      if (options.columnProperty.uninit) {
        return data;
      }
      data.init();
    }
    if (typeof option == 'string') {
      return data[option]();
    } else {
      return data;
    }
  };

  $.fn.wnumberInput.Constructor = NumberInput;

  $.fn.wnumberInput.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性

    // 控件私有属性
    decimal: 2, // 默认两位小数
    negative: true,
    disabled: false,
    readOnly: false,
    isShowAsLabel: false,
    isHide: false, // 是否隐藏
    formulas: {}
  };
})(jQuery);
