/**
 * unit类型属性的公共方法
 */
(function ($) {
  $.wUnitCommonMethod = {
    isValueMap: function () {
      return true;
    },
    /**
     * 设置默认值
     *
     * @param defaultValue
     */
    setDefaultValue: function (defaultValue) {
      // 加空判断
      if ($.trim(defaultValue).length == 0) {
        return;
      }
      var self = this;
      self.setValue(defaultValue);
    },
    setValue: function (value, isRender) {
      var self = this;
      self.value = value;
      self.setValue2LabelElem && self.setValue2LabelElem();
      self.setValue2EditableElem && self.setValue2EditableElem();
      try {
        if (self.isValueMap()) {
          self.setToRealDisplayColumn();
        }
      } finally {
      }
    },
    // 将值分别从labelfield和valuefield中取出放到内存中的value对象中
    setValue2Object: function () {
      var self = this;
      var realValue = self.$editableElem.attr('hiddenvalue');
      var displayValue = self.$editableElem.val();
      var values = realValue.split(';');
      var displayvalue = displayValue.split(';');
      if (values.length != displayvalue.length) {
        throw new Error('隐藏值和显示值长度不一致!');
      }
      var key = 'vcache_' + realValue;
      var v = [],
        opt = {};
      for (var i = 0; i < values.length; i++) {
        v.push(values[i]);
        opt[values[i]] = displayvalue[i];
      }
      self.options[key] = opt;
      self.setValue(realValue);
      if (self.culateByFormula) {
        self.culateByFormula(); // 根据运算公式计算
      }
    },
    getDisplayValue: function () {
      var self = this;
      var value = self.getValue(),
        dvalue;
      if ($.trim(value).length == 0) {
        return '';
      }
      var key = 'vcache_' + value;
      if (typeof value === 'string') {
        value = value.split(self.getSeparator());
      } else if (false === $.isArray(value)) {
        value = [value];
      }
      var nameDisplayMethod = self.options.nameDisplayMethod || '1';

      if (!self.options[key]) {
        var label = self.$editableElem && self.$editableElem.val().split(';');
        $.ajax({
          type: 'POST',
          url: ctx + '/proxy/api/org/tree/dialog/smartName',
          contentType: 'application/json',
          dataType: 'json',
          async: false,
          data: JSON.stringify({
            nameDisplayMethod: nameDisplayMethod,
            nodeNames: label,
            nodeIds: value
          }),
          success: function (result) {
            var res = {};
            for (var i in result.data) {
              var _data = result.data[i];
              if (i[0] === 'U') {
                res[i] = _data.name;
              } else {
                res[i] = (nameDisplayMethod === '2' ? _data.shortName : _data.smartNamePath) || _data.name;
              }
            }
            self.options[key] = res;
          }
        });
      }
      var displayValues = [];
      if (self.options[key]) {
        var opt = self.options[key];
        for (var i = 0; i < value.length; i++) {
          displayValues.push(opt[value[i]]);
        }
      }
      return displayValues.join(self.getSeparator());
    },
    /* 选择完组织之后 */
    afterUnitChoose: function (treeNodes) {
      // 控件自定义事件
      var _this = this;
      var inputMode = this.options.commonProperty.inputMode;
      _this.setToRealDisplayColumn();
      this.invoke('afterOrgInfo', treeNodes);
      this.invoke('afterSetValue', treeNodes);
    }
  };
})(jQuery);
