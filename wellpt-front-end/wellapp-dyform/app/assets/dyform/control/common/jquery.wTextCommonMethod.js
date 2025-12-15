/**
 * input文本类型属性的公共方法 (主要是ReadOnly及Edtiable的控制)
 */
(function ($) {
  $.wTextCommonMethod = {
    get$InputElem: function () {
      if (this.$editableElem == null) {
        return $([]);
      } else {
        return this.$editableElem;
      }
    },

    /* 设值到标签中 */
    setValue2LabelElem: function () {
      var self = this;
      if (self.$labelElem == null) {
        return;
      }
      var value = self.value;
      if (self.isValueMap()) {
        value = self.getDisplayValue();
      }

      //'2'->富文本框 '20'->多行文本框
      if ($.inArray(self.options.commonProperty.inputMode, ['2', '20']) < 0) {
        self.$labelElem.text(value);
      } else if (self.options.columnProperty.htmlCodec === 'html2Escape') {
        var pre = '<pre></pre>';
        self.$labelElem.html(pre);
        self.$labelElem.find('pre').text(value);
        if (self.isInSubform()) {
          var editable = self.$labelElem.closest('td').first().find('a.editable');
          editable.attr('title', value).html(pre);
          editable.find('pre').text(value);
        }
      } else {
        if (StringUtils.isNotBlank(value)) {
          var newVal = '<pre>' + value + '</pre>';
          self.$labelElem.html(newVal);
          if (self.isInSubform()) {
            self.$labelElem.closest('td').first().find('a.editable').html(newVal).attr('title', value);
          }
        } else {
          self.$labelElem.html(value);
        }
      }

      //富文本框label去除title
      if (self.options.commonProperty.inputMode === '2') {
        self.$labelElem.addClass('richText').removeAttr('title');
      }

      // 单行输入框为密码时,默认显示为*
      if (self.options.columnProperty.isPasswdInput === 'true') {
        if (value) {
          self.$labelElem.text('******').attr('title', '******').data('value', value);
        } else {
          self.$labelElem.text('').attr('title', '').data('value', '');
        }
        //显示为文本时且单行输入框为密码时，判断是否要显示眼睛（显示/隐藏）按钮
        if (self.options.isShowAsLabel && self.options.columnProperty.showPasswordEye) {
          self.$textEye.show();
        }
      }
    },

    /* 设置到可编辑元素中 */
    setValue2EditableElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        return;
      }
      //有额外元素且已经解析完成
      if (self.options.columnProperty.realDisplay.display && self.value) {
        var display = self.options.columnProperty.realDisplay.display;
        var control = $.ControlManager.getCtl(self.getContronId(display));
        var _addonValue = [];
        var _Val = self.value;
        setTimeout(function () {
          _addonValue = control.value.split('-');
          $.each(_addonValue, function (i, v) {
            var _value = v.split(':')[1];
            if (!_value) return true;
            if (i === 0) {
              _Val = _Val.substr(_value.length);
              var addonFrontValueLen = self.options.columnProperty.addonFrontValue.split(',').length;
              if (addonFrontValueLen > 1) {
                self.$input.parent().prev().find('.addon-value').text(_value);
              }
            } else {
              var addonEndValueLen = self.options.columnProperty.addonEndValue.split(',').length;
              if (addonEndValueLen > 1) {
                self.$input.parent().next().find('.addon-value').text(_value);
              }
            }
          });
          self.$input.val(_Val);
        }, 0);
      } else {
        if (self.$input) {
          self.$input.val(self.value);
        } else {
          self.$editableElem.val(self.value);
        }
      }
    },

    // set............................................................//

    // 设值,值为真实值
    setValue: function (value, isRender) {
      if (value == null) {
        return;
      }

      if (typeof value == 'string' && value.indexOf('\u0000') == 0 && this.getPos() == dyControlPos.subForm) {
        value = value.replace('\u0000', '');
      }
      this.value = value;
      if (this.isRender()) {
        // 该控件被隐藏时则不进行渲染
        this.render(isRender); // 将值渲染到页面元素上
      }
      // this.set
      if (this.culateByFormula) this.culateByFormula(); // 根据运算公式计算
      this.invoke('afterSetValue', this.value);
    },

    //给额外元素赋值到隐藏字段中
    setToAddonDisplayColumn: function (value) {
      var self = this;
      var display = self.options.columnProperty.realDisplay.display;
      if (typeof display === 'string' && display.length > 0) {
        var control = $.ControlManager.getCtl(self.getContronId(display));
        if (typeof control == 'undefined' || control == null) {
          return;
        }
        control.setValue(value);
      }
    },

    isValueMap: function () {
      return false;
    }
  };
})(jQuery);
