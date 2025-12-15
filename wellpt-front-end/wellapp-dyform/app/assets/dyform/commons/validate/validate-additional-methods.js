/*******************************************************************************
 * jQuery Validate扩展验证方法 (linjq)
 ******************************************************************************/
// 加载全局国际化资源
if (I18nLoader) {
  I18nLoader.load('/js/pt/js/global');
}
// 加载动态表单定义模块国际化资源
if (I18nLoader) {
  I18nLoader.load('/js/validate/js/messages');
}

$(function () {
  // 验证上次文件的扩展名
  $.wValidator.addMethod(
    'extension',
    function (value, ctl, param) {
      param = typeof param === 'string' ? param.replace(/,/g, '|') : 'png|jpe?g|gif';
      return this.optional(ctl) || value.match(new RegExp('\\.(' + param + ')$', 'i'));
    },
    '文件格式错误'
  );

  // 判断整数value是否等于0
  jQuery.wValidator.addMethod(
    'isIntEqZero',
    function (value, ctl) {
      value = parseInt(value);
      return this.optional(ctl) || value == 0;
    },
    '整数必须为0'
  );

  jQuery.wValidator.addMethod(
    'isCustomizeRegularText',
    function (value, ctl, param) {
      return this.optional(ctl) || value.match(new RegExp(param, 'i'));
    },
    '值不符合条件'
  );
  jQuery.wValidator.addMethod(
    'validateEvent',
    function (value, ctl, script) {
      var result;
      var defineFunc = appContext.eval(script, $(this), { data: value }, function (v) {
        result = v;
      });
      if (result != undefined) {
        return result;
      } else {
        return true;
      }
    },
    '值不符合条件'
  );

  // 判断整数value是否大于0
  jQuery.wValidator.addMethod(
    'isIntGtZero',
    function (value, ctl) {
      value = parseInt(value);
      return this.optional(ctl) || value > 0;
    },
    '整数必须大于0'
  );

  // 判断整数value是否大于或等于0
  jQuery.wValidator.addMethod(
    'isIntGteZero',
    function (value, ctl) {
      value = parseInt(value);
      return this.optional(ctl) || value >= 0;
    },
    '整数必须大于或等于0'
  );

  // 判断整数value是否不等于0
  jQuery.wValidator.addMethod(
    'isIntNEqZero',
    function (value, ctl) {
      value = parseInt(value);
      return this.optional(ctl) || value != 0;
    },
    '整数必须不等于0'
  );

  // 判断整数value是否小于0
  jQuery.wValidator.addMethod(
    'isIntLtZero',
    function (value, ctl) {
      value = parseInt(value);
      return this.optional(ctl) || value < 0;
    },
    '整数必须小于0'
  );

  // 判断整数value是否小于或等于0
  jQuery.wValidator.addMethod(
    'isIntLteZero',
    function (value, ctl) {
      value = parseInt(value);
      return this.optional(ctl) || value <= 0;
    },
    '整数必须小于或等于0'
  );

  // 判断浮点数value是否等于0
  jQuery.wValidator.addMethod(
    'isFloatEqZero',
    function (value, ctl) {
      value = parseFloat(value);
      return this.optional(ctl) || value == 0;
    },
    '浮点数必须为0'
  );

  // 判断浮点数value是否大于0
  jQuery.wValidator.addMethod(
    'isFloatGtZero',
    function (value, ctl) {
      value = parseFloat(value);
      return this.optional(ctl) || value > 0;
    },
    '浮点数必须大于0'
  );

  // 判断浮点数value是否大于或等于0
  jQuery.wValidator.addMethod(
    'isFloatGteZero',
    function (value, ctl) {
      value = parseFloat(value);
      return this.optional(ctl) || value >= 0;
    },
    '浮点数必须大于或等于0'
  );

  // 判断浮点数value是否不等于0
  jQuery.wValidator.addMethod(
    'isFloatNEqZero',
    function (value, ctl) {
      value = parseFloat(value);
      return this.optional(ctl) || value != 0;
    },
    '浮点数必须不等于0'
  );

  // 判断浮点数value是否小于0
  jQuery.wValidator.addMethod(
    'isFloatLtZero',
    function (value, ctl) {
      value = parseFloat(value);
      return this.optional(ctl) || value < 0;
    },
    '浮点数必须小于0'
  );

  // 判断浮点数value是否小于或等于0
  jQuery.wValidator.addMethod(
    'isFloatLteZero',
    function (value, ctl) {
      value = parseFloat(value);
      return this.optional(ctl) || value <= 0;
    },
    '浮点数必须小于或等于0'
  );

  // 判断浮点型
  jQuery.wValidator.addMethod(
    'isFloat',
    function (value, ctl, nd) {
      var rtn = this.optional(ctl) || /^[-\+]?\d+(\.\d+)?$/.test(value);
      if (rtn && nd && nd.decimal > 0) {
        value = '' + value;
        var scaleIdx = value.indexOf('.');
        if (scaleIdx > -1) {
          return value.length - scaleIdx - 1 <= nd.decimal;
        }
      }
      return rtn;
    },
    '只能包含数字、小数点等字符'
  );

  // 判断正浮点型
  jQuery.wValidator.addMethod(
    'isPositiveFloat',
    function (value, ctl, nd) {
      var rtn = this.optional(ctl) || /^[+]?\d+(\.\d+)?$/.test(value);
      if (rtn && nd && nd.decimal > 0) {
        value = '' + value;
        var scaleIdx = value.indexOf('.');
        if (scaleIdx > -1) {
          return value.length - scaleIdx - 1 <= nd.decimal;
        }
      }
      return rtn;
    },
    '只能包含数字、小数点的正浮点数'
  );

  // 匹配integer
  jQuery.wValidator.addMethod(
    'isInteger',
    function (value, ctl) {
      return this.optional(ctl) || /^[-\+]?[Ee\d]+$/.test(value);
    },
    '匹配integer'
  );

  // 匹配负integer
  jQuery.wValidator.addMethod(
    'isNegative',
    function (value, ctl) {
      console.log(value);
      return this.optional(ctl) || (/^[-\+]?[Ee\d]+$/.test(value) && parseInt(value) < 0);
    },
    '匹配integer'
  );

  // 匹配正integer
  jQuery.wValidator.addMethod(
    'isPositive',
    function (value, ctl) {
      console.log(value);
      return this.optional(ctl) || (/^[-\+]?[Ee\d]+$/.test(value) && parseInt(value) >= 0);
    },
    '匹配integer'
  );

  jQuery.wValidator.addMethod(
    'isURL',
    function (value, ctl) {
      var parse_url = '[a-zA-z]+://[^s]*';
      return this.optional(ctl) || parse_url.test(value);
    },
    'url格式错误'
  );

  // 判断数值类型，包括整数和浮点数
  jQuery.wValidator.addMethod(
    'isNumber',
    function (value, ctl, nd) {
      var didx,
        rtn = this.optional(ctl) || /^[-\+]?[Ee\d]+$/.test(value) || /^[-\+]?\d+(\.\d+)?$/.test(value);
      if (rtn && jQuery.isPlainObject(nd) && nd.datatype === '17' && (didx = nd.precision) > 0) {
        value = '' + value;
        // 有效数位 ：从左边第一个不为 0 的数算起，到末位数字为止的所有数字，小数点和负号不计入有效位数。精确到小数点右边
        // s位，并四舍五入
        var scaleIdx = value.indexOf('.');
        nd.scale = parseInt(nd.scale) || 0;
        scaleIdx > -1 && nd.scale > 0 && (rtn = rtn && value.length - scaleIdx - 1 <= nd.scale);
        scaleIdx > -1 && didx++;
        (value.indexOf('-') > -1 || value.indexOf('+') > -1) && didx++;
        var val = Number(value).toPrecision();
        var sc = 0;
        scaleIdx > -1 && (sc = val.length - scaleIdx - 1);
        return val.length <= didx && sc <= nd.scale;
      }
      return rtn;
    },
    '匹配数值类型，包括整数和浮点数'
  );

  // 只能输入[0-9]数字
  jQuery.wValidator.addMethod(
    'isDigits',
    function (value, ctl) {
      return this.optional(ctl) || /^\d+$/.test(value);
    },
    '只能输入0-9数字'
  );

  // 判断中文字符
  jQuery.wValidator.addMethod(
    'isChinese',
    function (value, ctl) {
      return this.optional(ctl) || /^[\u0391-\uFFE5]+$/.test(value);
    },
    '只能包含中文字符。'
  );

  // 判断英文字符
  jQuery.wValidator.addMethod(
    'isEnglish',
    function (value, ctl) {
      return this.optional(ctl) || /^[A-Za-z]+$/.test(value);
    },
    '只能包含英文字符。'
  );

  // 手机号码验证
  jQuery.wValidator.addMethod(
    'isMobile',
    function (value, ctl) {
      value = value.toString();
      var length = value.length;
      var mobile = /^(1\d{10})$/; //手机号目前只能确保到11位,及以1开头
      return this.optional(ctl) || (length == 11 && mobile.test(value));
    },
    '请正确填写您的手机号码。'
  );

  // 电话号码验证
  jQuery.wValidator.addMethod(
    'isPhone',
    function (value, ctl) {
      var tel = /^(\d{3,4}-?)?\d{7,9}$/g;
      return this.optional(ctl) || tel.test(value);
    },
    '请正确填写您的电话号码。'
  );

  // 联系电话(手机/电话皆可)验证
  jQuery.wValidator.addMethod(
    'isTel',
    function (value, ctl) {
      var length = value.length;
      var mobile = /^(1\d{10})$/; //手机号目前只能确保到11位,及以1开头
      var tel = /^(\d{3,4}-?)?\d{7,9}$/g;
      return this.optional(ctl) || tel.test(value) || (length == 11 && mobile.test(value));
    },
    '请正确填写您的联系方式'
  );

  // 匹配qq
  jQuery.wValidator.addMethod(
    'isQq',
    function (value, ctl) {
      return this.optional(ctl) || /^[1-9]\d{4,12}$/;
    },
    '匹配QQ'
  );

  // 邮政编码验证
  jQuery.wValidator.addMethod(
    'isZipCode',
    function (value, ctl) {
      var zip = /^[0-9]{6}$/;
      return this.optional(ctl) || zip.test(value);
    },
    '请正确填写您的邮政编码。'
  );

  // 匹配密码，以字母开头，长度在6-12之间，只能包含字符、数字和下划线。
  jQuery.wValidator.addMethod(
    'isPwd',
    function (value, ctl) {
      return this.optional(ctl) || /^[a-zA-Z]\\w{6,12}$/.test(value);
    },
    '以字母开头，长度在6-12之间，只能包含字符、数字和下划线。'
  );

  // 身份证号码验证
  jQuery.wValidator.addMethod(
    'isIdCardNo',
    function (value, ctl) {
      // var idCard = /^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/;
      return this.optional(ctl) || isIdCardNo(value);
    },
    '请输入正确的身份证号码。'
  );

  // 邮编验证
  jQuery.wValidator.addMethod(
    'isPostcode',
    function (value, ctl) {
      return this.optional(ctl) || /^[0-9]{6}$/.test(value);
    },
    '请输入正确的邮编。'
  );

  // IP地址验证
  jQuery.wValidator.addMethod(
    'ip',
    function (value, ctl) {
      return (
        this.optional(ctl) ||
        /^(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))$/.test(
          value
        )
      );
    },
    '请填写正确的IP地址。'
  );

  // 字符验证，只能包含中文、英文、数字、下划线等字符。
  jQuery.wValidator.addMethod(
    'stringCheck',
    function (value, ctl) {
      return this.optional(ctl) || /^[a-zA-Z0-9\u4e00-\u9fa5-_]+$/.test(value);
    },
    '只能包含中文、英文、数字、下划线等字符'
  );

  // 匹配english
  jQuery.wValidator.addMethod(
    'isEnglish',
    function (value, ctl) {
      return this.optional(ctl) || /^[A-Za-z]+$/.test(value);
    },
    '匹配english'
  );

  // 匹配汉字
  jQuery.wValidator.addMethod(
    'isChinese',
    function (value, ctl) {
      return this.optional(ctl) || /^[\u4e00-\u9fa5]+$/.test(value);
    },
    '匹配汉字'
  );

  // 匹配中文(包括汉字和字符)
  jQuery.wValidator.addMethod(
    'isChineseChar',
    function (value, ctl) {
      return this.optional(ctl) || /^[\u0391-\uFFE5]+$/.test(value);
    },
    '匹配中文(包括汉字和字符) '
  );

  // 判断是否为合法字符(a-zA-Z0-9-_)
  jQuery.wValidator.addMethod(
    'isRightfulString',
    function (value, ctl) {
      return this.optional(ctl) || /^[A-Za-z0-9_-]+$/.test(value);
    },
    '判断是否为合法字符(a-zA-Z0-9-_)'
  );

  // 判断是否包含中英文特殊字符，除英文"-_"字符外
  jQuery.wValidator.addMethod(
    'isContainsSpecialChar',
    function (value, ctl) {
      var reg = RegExp(
        /[(\ )(\`)(\~)(\!)(\@)(\#)(\$)(\%)(\^)(\&)(\*)(\()(\))(\+)(\=)(\|)(\{)(\})(\')(\:)(\;)(\')(',)(\[)(\])(\.)(\<)(\>)(\/)(\?)(\~)(\！)(\@)(\#)(\￥)(\%)(\…)(\&)(\*)(\（)(\）)(\—)(\+)(\|)(\{)(\})(\【)(\】)(\‘)(\；)(\：)(\”)(\“)(\’)(\。)(\，)(\、)(\？)]+/
      );
      return this.optional(ctl) || !reg.test(value);
    },
    '含有中英文特殊字符'
  );

  // 身份证号码的验证规则
  function isIdCardNo(num) {
    // if (isNaN(num)) {alert("输入的不是数字！"); return false;}
    var len = num.length,
      re;
    if (len == 15) re = new RegExp(/^(\d{6})()?(\d{2})(\d{2})(\d{2})(\d{2})(\w)$/);
    else if (len == 18) re = new RegExp(/^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/);
    else {
      // alert("输入的数字位数不对。");
      return false;
    }
    var a = num.match(re);
    if (a != null) {
      if (len == 15) {
        var D = new Date('19' + a[3] + '/' + a[4] + '/' + a[5]);
        var B = D.getYear() == a[3] && D.getMonth() + 1 == a[4] && D.getDate() == a[5];
      } else {
        var D = new Date(a[3] + '/' + a[4] + '/' + a[5]);
        var B = D.getFullYear() == a[3] && D.getMonth() + 1 == a[4] && D.getDate() == a[5];
      }
      if (!B) {
        // alert("输入的身份证号 "+ a[0] +" 里出生日期不对。");
        return false;
      }
    }
    if (!re.test(num)) {
      // alert("身份证最后一位只能是数字和字母。");
      return false;
    }
    return true;
  }

  jQuery.wValidator.addMethod(
    'minNum',
    function (value, ctl, parameters) {
      return this.optional(ctl) || parseFloat(value) >= parameters;
    },
    '不能小于最小值'
  );
  jQuery.wValidator.addMethod(
    'maxNum',
    function (value, ctl, parameters) {
      return this.optional(ctl) || parseFloat(value) <= parameters;
    },
    '不能大于最大值'
  );
  jQuery.wValidator.addMethod('numberRange', function (value, ctl, parameters) {
    var validateTrue = false;

    if (parameters.length == 1 && parameters[0].minNum == '' && parameters[0].maxNum == '') {
      validateTrue = true;
    } else {
      for (var i = 0; i < parameters.length; i++) {
        var param = parameters[i];
        var minNum = param.minNum;
        var maxNum = param.maxNum;
        var minOperator = param.minOperator;
        var maxOperator = param.maxOperator;
        if (minNum != '' && maxNum != '') {
          if (minOperator == '1' && maxOperator == '1' && parseFloat(value) - minNum >= 0 && maxNum - parseFloat(value) >= 0) {
            validateTrue = true;
            break;
          } else if (minOperator == '1' && maxOperator == '2' && parseFloat(value) - minNum >= 0 && maxNum - parseFloat(value) > 0) {
            validateTrue = true;
            break;
          } else if (minOperator == '2' && maxOperator == '1' && parseFloat(value) - minNum > 0 && maxNum - parseFloat(value) >= 0) {
            validateTrue = true;
            break;
          } else if (minOperator == '2' && maxOperator == '2' && parseFloat(value) - minNum > 0 && maxNum - parseFloat(value) > 0) {
            validateTrue = true;
            break;
          }
        } else if (minNum != '') {
          if ((minOperator == '1' && parseFloat(value) - minNum >= 0) || (minOperator == '2' && parseFloat(value) - minNum > 0)) {
            validateTrue = true;
            break;
          }
        } else if (maxNum != '') {
          if ((maxOperator == '1' && maxNum - parseFloat(value) >= 0) || (maxOperator == '2' && maxNum - parseFloat(value) > 0)) {
            validateTrue = true;
            break;
          }
        }
      }
    }

    return this.optional(ctl) || validateTrue;
  });
  jQuery.wValidator.addMethod(
    // 复选框控件
    'checkboxMin',
    function (value, ctl, parameters) {
      var len = value.split(';').length;
      return this.optional(ctl) || len >= parameters;
    },
    '选中数不能小于最小值'
  );
  jQuery.wValidator.addMethod(
    'checkboxMax',
    function (value, ctl, parameters) {
      var len = value.split(';').length;
      return this.optional(ctl) || len <= parameters;
    },
    '选中数不能大于最大值'
  );

  jQuery.wValidator.addMethod(
    // 标签组控件
    'selectMinContent',
    function (value, ctl, parameters) {
      var len = value.split(',').length;
      return this.optional(ctl) || len >= parameters;
    },
    '选中数不能小于最小值'
  );
  jQuery.wValidator.addMethod(
    'selectMaxContent',
    function (value, ctl, parameters) {
      var len = value.split(',').length;
      return this.optional(ctl) || len <= parameters;
    },
    '选中数不能大于最大值'
  );
  jQuery.wValidator.addMethod(
    'validFiles',
    function (value, ctl, parameters) {
      var files = WellFileUpload.files[ctl.getFielctlID()];
      var result = true;
      if (files && $.isArray(files)) {
        $.each(files, function (index, file) {
          if (!file.fileID) {
            // 没有fileId的数据 -> 文件上传中或上传失败
            result = false;
          }
        });
      }
      return result;
    },
    '存在上传中或上传失败的附件，请在所有附件上传完成后，重新操作'
  );
});
