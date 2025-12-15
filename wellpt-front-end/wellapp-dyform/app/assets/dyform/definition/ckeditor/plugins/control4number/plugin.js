var controlConfig = {};
controlConfig.$ = function (selector) {
  return $(selector, $('#numberAttrCfgDiv'));
};

$.extend(controlConfig, $.ControlConfigUtil);

$.extend(controlConfig, {
  getFieldDefinition: function (fieldName) {
    var field = null;
    field = formDefinition.getField(fieldName); //表单中从表的信息
    if (field == null || typeof field == 'undefined') {
      field = new WNumberInputClass();
      field.inputMode = dyFormInputMode.number;
      field.dbDataType = dyFormDataType['int'];
      field.decimal = '0';
      field.valueCreateMethod = dyFormInputValue.userImport;
      field.length = 7;
      field.textAlign = 'center';
    } else {
      var fieldCopy = {};
      $.extend(true, fieldCopy, field); //这里只是做了一个副本,只有当用户点击确定完之后方可改变formDefinition对应的field对象
      field = fieldCopy;
    }
    return field;
  },

  initProperty: function (field) {
    var _this = this;
    //控件属性初始化公共设置.
    this.ctlPropertyComInitSet(field);
    //私有属性
    this.$('#formatNumber').prop('checked', field.formatNumber);
    this.$('#decimal').val(field.decimal);
    this.$('#precision').val(field.precision || 18);
    this.$('#length').attr('readonly', 'readonly'); //数值控件长度为固定
    this.$('#scale').val(field.scale);
    // this.$("#minNum").val(field.minNum);
    // this.$("#maxNum").val(field.maxNum);
    if (field.minNum && field.maxNum) {
      // 兼容旧数据
      field.numberRange = [];
      field.numberRange.push({
        minNum: field.minNum,
        maxNum: field.maxNum,
        minOperator: '1',
        maxOperator: '1'
      });
    }
    if (field.numberRange && field.numberRange.length > 0) {
      var html = '';
      $.each(field.numberRange, function (index, item) {
        html +=
          '<div class="number_range">' +
          '<input type="number" placeholder="最小值" class="minNum" value="' +
          item.minNum +
          '">' +
          '<select class="minOperator">' +
          '<option value="1" ' +
          (item.minOperator == '1' ? 'selected' : '') +
          '> ≤</option>' +
          '<option value="2" ' +
          (item.minOperator == '2' ? 'selected' : '') +
          '> ＜</option>' +
          '</select><span class="number_text">数字</span>' +
          '<select class="maxOperator">' +
          '<option value="1" ' +
          (item.maxOperator == '1' ? 'selected' : '') +
          '> ≤</option>' +
          '<option value="2" ' +
          (item.maxOperator == '2' ? 'selected' : '') +
          '> ＜</option>' +
          '</select>' +
          '<input type="number" placeholder="最大值" class="maxNum" value="' +
          item.maxNum +
          '">' +
          '<span class="iconfont icon-ptkj-jiahao"></span>' +
          '<span class="iconfont icon-ptkj-jianhao"></span>' +
          '</div>';
      });
      this.$('#number_range_content').html(html);
    }
    if (!field.numberRange || (field.numberRange && field.numberRange.length == 1)) {
      this.$('.number_range').find('.icon-ptkj-jianhao').hide();
    }
    this.$('#number_range_content')
      .off('click', '.icon-ptkj-jiahao')
      .on('click', '.icon-ptkj-jiahao', function () {
        var html =
          '<div class="number_range">' +
          '<input type="number" placeholder="最小值" class="minNum">' +
          '<select class="minOperator">' +
          '<option value="1" selected> ≤</option>' +
          '<option value="2"> ＜</option>' +
          '</select><span class="number_text">数字</span>' +
          '<select class="maxOperator">' +
          '<option value="1" selected> ≤</option>' +
          '<option value="2"> ＜</option>' +
          '</select>' +
          '<input type="number" placeholder="最大值" class="maxNum">' +
          '<span class="iconfont icon-ptkj-jiahao"></span>' +
          '<span class="iconfont icon-ptkj-jianhao"></span>' +
          '</div>';

        if ($(this).parent().find('.icon-ptkj-jianhao:visible').length == 0) {
          $(this).next().show();
        }
        $(this).parents('.number_range').after(html);
      });

    this.$('#number_range_content')
      .off('click', '.icon-ptkj-jianhao')
      .on('click', '.icon-ptkj-jianhao', function () {
        $(this).parent().remove();
        if (_this.$('#number_range_content').find('.number_range').length == 1) {
          _this.$('#number_range_content').find('.number_range').find('.icon-ptkj-jianhao').hide();
        }
      });
    this.$('#number_range_content').on('mousewheel', 'input[type="number"]', function () {
      return false;
    });

    this.$('#dbDataType').change(function () {
      var value = _this.$('#dbDataType').val();
      // @see length修改 bug:49960
      if (value == '13') {
        _this.$('#length').val('9');
        _this.$('#tr_decimal').hide();
        _this.$('#decimal').val('0');
        _this.$('#tr_number').hide();
      } else if (value == '14') {
        _this.$('#length').val('16');
        _this.$('#tr_decimal').hide();
        _this.$('#decimal').val('0');
        _this.$('#tr_number').hide();
      } else if (value == '15') {
        _this.$('#length').val('12');
        _this.$('#tr_decimal').show();
        _this.$('#decimal').val('2');
        _this.$('#tr_number').hide();
      } else if (value == '12') {
        _this.$('#length').val('18');
        _this.$('#tr_decimal').show();
        _this.$('#decimal').val('2');
        _this.$('#tr_number').hide();
      } else if (value == '17') {
        _this.$('#tr_decimal').hide();
        _this.$('#tr_number').show();
      } else if (value == '131' || value == '132') {
        _this.$('#tr_decimal').hide();
      }
    });

    if (field.dbDataType == '15' || field.dbDataType == '12') {
      this.$('#tr_decimal').show();
      this.$('#decimal').val(field.decimal);
    } else {
      this.$('#tr_decimal').hide();
    }
    if (field.dbDataType == '17') {
      _this.$('#tr_number').show();
    }
    if (field.formula) this.$('#formula').text(field.formula);
    this.setOperatorValue(field.operator);

    $('#unitUnique').val(field.unitUnique);
    if (field.unitUnique == 'true') {
      $('#checkRule_6').attr('checked', true);
      $('#checkRule_5').attr('checked', false);
    } else if (field.unitUnique == 'false') {
      $('#checkRule_5').attr('checked', true);
      $('#checkRule_6').attr('checked', false);
    }

    $('#precision').change(function () {
      var _val = $(this).val();
      _this.$('#length').val(_val);
    });
  },

  setOperatorValue: function (operator) {
    if (typeof operator == 'undefined') {
      operator = new NumberOperator();
    }
    this.$('#plusMark').attr('checked', operator.plus);
    this.$('#minusMark').attr('checked', operator.minus);
    this.$('#plusUnit').val(operator.plusUnit);
    this.$('#minusUnit').val(operator.minusUnit);
  },

  getOperatorValue: function (dbDataType) {
    var operator = new NumberOperator();
    operator.plus = this.$('#plusMark').attr('checked') == 'checked';
    operator.minus = this.$('#minusMark').attr('checked') == 'checked';
    operator.plusUnit = this.$('#plusUnit').val();
    operator.minusUnit = this.$('#minusUnit').val();

    //number小数位
    var scale = this.$('#scale').val();
    var precision = this.$('#precision').val();
    if (dbDataType == '17') {
      if (scale && isNaN(scale)) {
        alert('小数位须为数字');
        throw new Error('小数位须为数字');
      }
      if (parseInt(scale) > 127 || parseInt(scale) < -84) {
        alert('小数位须为-84-127');
        throw new Error('小数位须为-84-127');
      }
      if (precision && isNaN(precision)) {
        alert('精度位须为数字');
        throw new Error('整数位须为数字');
      }
      if (parseInt(precision) > 38 || parseInt(precision) < 0) {
        alert('精度位须为1-38');
        throw new Error('精度位须为1-38');
      }
    }
    if ((operator.minus && isNaN(operator.minusUnit)) || (operator.plus && isNaN(operator.plusUnit))) {
      alert('加量、减量须为数字');
      throw new Error('减量须为数字');
    }

    if (operator.plusUnit != '' && operator.plus) {
      if (dbDataType == dyFormDataType['float'] || dbDataType == dyFormDataType['double'] || dbDataType == dyFormDataType['number']) {
        try {
          operator.plusUnit = parseFloat(operator.plusUnit);
        } catch (e) {
          alert('加量须为小数');
          throw new Error('加量须为小数');
        }
      } else {
        try {
          operator.plusUnit = parseInt(operator.plusUnit);
        } catch (e) {
          alert('加量须为整数');
          throw new Error('加量须为整数');
        }
      }
    }

    if (operator.minusUnit != '' && operator.minus) {
      if (dbDataType == dyFormDataType['float'] || dbDataType == dyFormDataType['double'] || dbDataType == dyFormDataType['number']) {
        try {
          operator.minusUnit = parseFloat(operator.minusUnit);
        } catch (e) {
          alert('减量须为小数');
          throw new Error('减量须为小数');
        }
      } else {
        try {
          operator.minusUnit = parseInt(operator.minusUnit);
        } catch (e) {
          alert('减量须为整数');
          throw new Error('减量须为整数');
        }
      }
    }
    return operator;
  },

  collectFormAndFillCkeditor: function () {
    var self = this;
    var field = this.field;
    field.inputMode = dyFormInputMode.number;
    //added by linxr
    field.fieldCheckRules = [];
    field.noNullValidateReminder = $('#noNullValidateReminder').val();
    field.uniqueValidateReminder = $('#uniqueValidateReminder').val();
    field.unitUnique = $('#unitUnique').val();
    //控件公共属性收集
    var checkpass = this.collectFormCtlComProperty(field);
    field.operator = this.getOperatorValue(field.dbDataType);

    if (!checkpass) {
      return false;
    }
    field.formatNumber = this.$('#formatNumber').prop('checked');
    field.decimal = this.$('#decimal').val();
    field.formula = this.$('#formula').val();
    //number精度位
    field.precision = this.$('#precision').val();
    //number小数位
    field.scale = this.$('#scale').val();
    field.numberRange = [];
    var validateNum = true;
    var emptyRange = 0;

    $.each(this.$('.number_range'), function (index, item) {
      var minNum = $(item).find('input.minNum').val();
      var maxNum = $(item).find('input.maxNum').val();
      var minOperator = $(item).find('select.minOperator option:selected').val();
      var maxOperator = $(item).find('select.maxOperator option:selected').val();
      if (minNum != '' && maxNum != '' && maxNum - minNum < 0) {
        appModal.error('最大值必须≥最小值，请修改！');
        validateNum = false;
        return;
      }
      if (minNum == '' && maxNum == '') {
        emptyRange++;
      }
      var obj = {
        minNum: minNum,
        maxNum: maxNum,
        minOperator: minOperator,
        maxOperator: maxOperator
      };
      field.numberRange.push(obj);
    });

    if (!validateNum) {
      return validateNum;
    }
    var validateTips = [];
    if (field.numberRange.length > 1) {
      if (emptyRange > 0) {
        validateTips.push('不允许出现最大值和最小值都为空的区间');
      }

      var currNumRanger = $.extend(true, [], field.numberRange);
      for (var j = 0; j < currNumRanger.length; j++) {
        var data = currNumRanger[j];
        if (data.minNum == '') {
          currNumRanger.splice(j, 1);
          currNumRanger.unshift(data);
        } else if (data.maxNum == '') {
          currNumRanger.splice(j, 1);
          currNumRanger.push(data);
        }
      }

      for (var i = 0; i < currNumRanger.length; i++) {
        var range = currNumRanger[i];
        if (range.minNum == '' && range.maxNum == '') {
        } else if (range.minNum == '') {
          if (i != 0) {
            validateTips.push('不允许重叠');
            break;
          }
        } else if (range.maxNum == '') {
          if (i != currNumRanger.length - 1 || (i > 0 && range.minNum - currNumRanger[i - 1].maxNum < 0)) {
            validateTips.push('不允许重叠');
            break;
          }
        } else if (i > 0 && range.minNum - currNumRanger[i - 1].maxNum < 0) {
          validateTips.push('不允许重叠');
          break;
        }
      }
    }

    if (validateTips.length > 0) {
      appModal.error('数字范围无效，多区间时，' + validateTips.join('，') + '，请修改！');
      return false;
    }

    //创建控件占位符
    this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);
    formDefinition.addField(field.name, field);
    //添加校验规则
    //field.contentFormat=$("#dbDataType").val();
    //field.fieldCheckRules=new Array;
    //field.fieldCheckRules.push({value:'n'+field.contentFormat, label:$("#dbDataType").find("option:selected").text()});
    return true;
  }
});

controlConfig.getInputEvents = function () {
  return [
    {
      id: 'focus',
      chkDisabled: true,
      name: 'focus'
    },
    {
      id: 'blur',
      chkDisabled: true,
      name: 'blur'
    },
    {
      id: 'change',
      chkDisabled: true,
      name: 'change'
    },
    {
      id: 'keypress',
      chkDisabled: true,
      name: 'keypress'
    },
    {
      id: 'keyup',
      chkDisabled: true,
      name: 'keyup'
    },
    {
      id: 'keydown',
      chkDisabled: true,
      name: 'keydown'
    }
  ];
};

controlConfig.pluginName = CkPlugin.NUMBERCTL;
addPlugin(controlConfig.pluginName, '数字输入控件', '数字输入控件属性设置', controlConfig);
