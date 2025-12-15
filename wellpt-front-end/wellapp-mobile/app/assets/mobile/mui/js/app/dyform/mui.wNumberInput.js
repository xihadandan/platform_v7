define(['mui', 'constant', 'commons', 'server', 'mui-DyformField'], function ($, constant, commons, server, DyformField) {
  // 数字控件
  var wNumberInput = function ($placeHolder, options) {
    DyformField.apply(this, arguments);
  };
  var StringUtils = commons.StringUtils;
  var CLS_HIDDEN = 'mui-hidden';
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
  var placeHolderTplId_1 = 'mui-DyformField-placeHolder-number';
  var placeHolderTplId_2 = 'mui-DyformField-placeHolder-number-2';
  commons.inherit(wNumberInput, DyformField, {
    render: function () {
      var that = this,
        options = that.options.fieldDefinition;
      var displayStyle = parseInt(options.displayStyle);
      var placeHolderTplId = displayStyle == 2 ? placeHolderTplId_2 : placeHolderTplId_1;
      //console.log( "mui.wnumber, displayStyle=" + displayStyle );
      that._superApply([placeHolderTplId]);
      var ctlName = that.getName();
      var inputMode = options.inputMode;
      var _$editableElem = $('input[name=' + ctlName + ']', that.$editableElem[0]);
      that._$editableElem = _$editableElem;
      var editableElem = _$editableElem[0];
      editableElem.setAttribute('name', ctlName);
      editableElem.setAttribute('inputMode', inputMode);

      if (options.editableClass) {
        editableElem.classList.add(options.editableClass);
      }
      if ((options.dbDataType == '13' || options.dbDataType == '131' || options.dbDataType == '132') && options.length > 9) {
        options.length = '9';
      } else if (options.dbDataType == '14' && options.length > 16) {
        options.length = '16';
      } else if (options.dbDataType == '15' && options.length > 12) {
        options.length = '12';
      } else if (options.dbDataType == '12' && options.length > 18) {
        options.length = '18';
      }
      editableElem.setAttribute('maxlength', options.length);
      // $(editableElem).css(this.getTextInputCss());

      editableElem.onpaste = function (event) {
        window.setTimeout(function () {
          that.setValue(editableElem.value, false);
        }, 100);
      };

      var $plus = (options.$plus = $('.mui-btn-numbox-plus', that.$editableElem[0]));
      var $minus = (options.$minus = $('.mui-btn-numbox-minus', that.$editableElem[0]));
      var plusElem = $plus[0],
        minusElem = $minus[0];
      plusElem.style.display = 'none';
      that.$editableElem[0].style.paddingRight = '0px';
      minusElem.style.display = 'none';
      that.$editableElem[0].style.paddingLeft = '0px';
      var operator = options.operator;
      var dbDataType = options.dbDataType;
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
        if (operator.plus) {
          plusElem.style.display = 'inline-block';
          that.$editableElem[0].style.paddingRight = '30px';
          plusElem.addEventListener('tap', function () {
            var val = that.getValue();
            if (!val) {
              val = 0;
              // return;
            }
            if (dbDataType != dyFormDataType['float'] && dbDataType != dyFormDataType['double']) {
              that.setValue(parseInt(val) + operator.plusUnit);
            } else {
              that.setValue(add(parseFloat(val), operator.plusUnit));
            }
          });
        }
        if (operator.minus) {
          minusElem.style.display = 'inline-block';
          that.$editableElem[0].style.paddingLeft = '30px';
          minusElem.addEventListener('tap', function () {
            var val = that.getValue();
            if (!val) {
              val = 0;
              // return;
            }
            if (dbDataType != dyFormDataType['float'] && dbDataType != dyFormDataType['double']) {
              that.setValue(parseInt(val) - operator.minusUnit);
            } else {
              that.setValue(sub(parseFloat(val), operator.minusUnit));
            }
          });
        }
      }
    },
    // 渲染编辑元素
    renderEditableElem: function ($editableElem) {
      var self = this;
      if (self._$editableElem) {
        self._$editableElem[0].value = self.getDisplayValue();
      }
    },
    collectValue: function (event) {
      return this._$editableElem[0].value;
    },
    // 获取字段值
    getValue: function () {
      var self = this;
      return StringUtils.isBlank(self.value) ? '' : parseFloat(self.value);
    },
    hideEditableElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        return;
      }
      self.$editableElem[0].classList.add(CLS_HIDDEN);
      // TODO hide error
      // self.$editableElem.siblings(".Validform_checktip").hide();
    },
    hideLabelElem: function () {
      if (this.$editableElem == null) {
        return;
      }
      this.$labelElem.addClass(CLS_HIDDEN);
    },
    // 显示为可编辑框
    setDisplayAsCtl: function () {
      var self = this;
      var options = self.options.fieldDefinition;
      options.isShowAsLabel = false;
      if (self.$editableElem == null) {
        self.createEditableElem(); // TODO
      } else {
        self.$editableElem[0].classList.remove(CLS_HIDDEN);
      }
      this.showOperator();
      this.hideLabelElem();
      this.setValue2EditableElem();
    },
    hideOperator: function () {
      var self = this,
        operator;
      var options = self.options.fieldDefinition;
      if ((operator = options.operator)) {
        if (operator.plus) {
          options.$plus[0].style.display = 'none';
          that.$editableElem[0].style.paddingRight = '0px';
        }
        if (operator.minus) {
          options.$minus[0].style.display = 'none';
          that.$editableElem[0].style.paddingLeft = '0px';
        }
      }
    },
    showOperator: function () {
      var self = this,
        operator;
      var options = self.options.fieldDefinition;
      if ((operator = options.operator)) {
        if (operator.plus) {
          that.$editableElem[0].style.paddingRight = '30px';
          options.$plus[0].style.display = 'inline-block';
        }
        if (operator.minus) {
          that.$editableElem[0].style.paddingLeft = '30px';
          options.$minus[0].style.display = 'inline-block';
        }
      }
    },
    setReadOnly: function (isReadOnly) {
      var self = this;
      self._superApply([isReadOnly]);
      if (isReadOnly) {
        self.options.fieldDefinition.showType = dyshowType.readonly;
        self.hideOperator();
      } else {
        self.options.fieldDefinition.showType = dyshowType.edit;
        self.showOperator();
      }
    },

    getFieldCheckRules: function (options) {
      var self = this;
      var fieldCheckRules = self._superApply([options]) || [];
      var numberCheckRules = self.getNumberRule(options);
      return fieldCheckRules.concat(numberCheckRules);
    },
    getNumberRule: function (options) {
      var rules = [];
      var dbDataType = options.dbDataType;
      if (dbDataType == '12' || dbDataType == '17') {
        // 双精度浮点数
        rules = [
          {
            value: 'n12',
            label: '双精度浮点数'
          }
        ];
      } else if (dbDataType == '15') {
        // 浮点数
        rules = [
          {
            value: 'n15',
            label: '浮点数'
          }
        ];
      } else if (dbDataType == '13') {
        // 整数
        rules = [
          {
            value: 'n13',
            label: '整数'
          }
        ];
      } else if (dbDataType == '131') {
        // 正整数
        rules = [
          {
            value: 'n131',
            label: '正整数'
          }
        ];
      } else if (dbDataType == '132') {
        // 负整数
        rules = [
          {
            value: 'n132',
            label: '负整数'
          }
        ];
      } else if (dbDataType == '14') {
        // 长整数
        rules = [
          {
            value: 'n14',
            label: '长整数'
          }
        ];
      }
      return rules;
    },
    // 获取当前光标在文本框的位置
    getCurPosition: function (domObj) {
      var position = 0;
      if (domObj.selectionStart || domObj.selectionStart == '0') {
        position = domObj.selectionStart;
      }
      return position;
    },
    // 获取当前文本框选中的文本
    getSelectedText: function (domObj) {
      if (domObj.selectionStart || domObj.selectionStart == '0') {
        return domObj.value.substring(domObj.selectionStart, domObj.selectionEnd);
      }
      return '';
    },
    isNumeric: function (obj) {
      return !isNaN(parseFloat(obj)) && isFinite(obj);
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
    }
  });
  return wNumberInput;
});
