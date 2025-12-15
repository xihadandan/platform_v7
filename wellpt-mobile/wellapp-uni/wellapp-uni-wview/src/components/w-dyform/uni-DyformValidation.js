const _ = require("lodash");
// 约束条件
// var fieldCheckRule = {
//   notBlank: '1', // "非空"
//   unique: '5', // "唯一"
//   common: '10', // 文本样式，校验规则
//   url: '11', // "url",
//   email: '12', // "email",
//   idCardNumber: '13', // "身份证",
//   telephone: '14', // "固定电话",
//   mobilePhone: '15' // "手机"
// };
// 输入样式
var dyFormInputMode = {
  text: "1", // 普通表单输入
  ckedit: "2", // 富文本编辑
  // accessory:'3',//附件
  accessory1: "4", // 图标显示
  // accessory2:'5',//图标显示（含正文）
  accessory3: "6", // 列表显示（不含正文）
  accessoryImg: "33", // 图片附件
  serialNumber: "7", // 可编辑流水号
  unEditSerialNumber: "29", // 不可编辑流水号

  orgSelect2: "43",

  timeEmploy: "12", // 资源选择
  timeEmployForMeet: "13", // 资源选择（会议）
  timeEmployForCar: "14", // 资源选择（车辆）
  timeEmployForDriver: "15", // 资源选择（司机）
  treeSelect: "16", // 树形下拉框 valuemap
  radio: "17", // radio表单元素 valuemap
  checkbox: "18", // checkbox表单元素 valuemap
  selectMutilFase: "19", // 下拉单选框 valuemap
  comboSelect: "191", // 下拉选项框 valuemap
  select: "199", // select2普通下拉框（新版）
  textArea: "20", // 文本域输入
  // fileUpload:'21',//附件上传
  textBody: "22", // 正文
  dialog: "26", // 弹出框
  xml: "27", // XML

  date: "30", // 日期
  number: "31", // 数字控件
  viewdisplay: "32", // 视图展示
  embedded: "40", // url嵌入页面
  job: "41", // 职位控件
  relevant: "42", // 相关数据控件
  template: "44", // 母版
  chained: "61", // 级联
  taggroup: "126", // 标签组
  colors: "127", // 颜色
  switchs: "128", // 开关按钮
  progress: "129", // 进度条
  placeholder: "130", // 真实值占位符
};
// 校验规则
var dyCheckRule = {
  // 约束条件
  notNull: "1", // 非空
  unique: "5", // 全局唯一校验
  // 文本样式，校验规则
  common: "10", //
  url: "11",
  email: "12",
  idCard: "13",
  tel: "14", // 固定电话
  mobilePhone: "15", // 手机
  customizeRegular: "16", // 自定义规则
  postcode: "17", // 自定义规则
  // 数字控件校验
  num_int: "n13", // 整数
  num_int_positive: "n131", // 正整数
  num_int_negtive: "n132", // 负整数
  num_long: "n14", // 长整数
  num_float: "n15", // 浮点数
  num_double: "n12", // 双精度浮点数
};

// 验证结果
var ValidationResult = function (dyform, errors) {
  this.dyform = dyform;
  this.errors = errors;
};
_.extend(ValidationResult.prototype, {
  // 是否存在错误
  hasErrors: function () {
    return this.errors != null && this.errors.length != 0;
  },
  // 返回错误信息列表
  getErrors: function () {
    return this.errors;
  },
});

// 检验器
var Validator = function (rule, fn) {
  this.rule = rule;
  this.fn = fn;
};
_.extend(Validator.prototype, {
  // 验证字段
  validate: function (field) {
    var result = this.fn.call(this, field.getValue(), field);
    result.checkedRule = this.rule;
    return result;
  },
  // 判断是否必填规则
  isRequired: function () {
    return this.rule === dyCheckRule.notNull;
  },
});
/* eslint-disable */
var regUrl =
  /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i;
var regMail =
  /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i;
var regMobilePhone = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(147)|(170)|(176)|(177)|(178))+\d{8})$/;
/* eslint-enable */
// 表单验证
var DyformValidation = function (dyform) {
  var self = this;
  self.dyform = dyform;
  self.methods = {};
  self.init();
};
_.extend(DyformValidation.prototype, {
  // 初始化
  init: function () {
    // 注册内置验证器
    // 1、非空
    this.methods[dyCheckRule.notNull] = function (value, field) {
      var notBlankTip = {
        valid: false, // false
        // 表示校验不通过，true表示校验通过
        tipMsg: "非空", // 当校验不通过时，提示的信息
      };
      // console.log(field);
      // if (field.isValueMap() && _.isPlainObject(value) && JSON.stringify(value) === "{}") {
      //	return notBlankTip;
      // }
      if (typeof value == "number" && value != NaN) {
        return {
          valid: true,
        };
      }
      if ((_.isArray(value) && value.length <= 0) || _.isEmpty(value) || value == "{}") {
        return notBlankTip;
      }
      return {
        valid: true,
      };
    };
    // 5、唯一
    this.methods[dyCheckRule.unique] = function (value, field) {
      var fieldName = field.getName();
      var result = {
        valid: true,
      };
      _.each(field.formScope.getDyform().formFields, function (ofield) {
        // 从表数据
        if (ofield.isSubform()) {
          console.log("subform");
        } else if (fieldName !== ofield.getName() && value === ofield.getValue()) {
          // 主表数据
          result = {
            valid: false, // false
            // 表示校验不通过，true表示校验通过
            tipMsg: "唯一", // 当校验不通过时，提示的信息
          };
          return false;
        }
      });
      return result;
    };
    // 10、文本样式，校验规则, 检查不要超过最大字段长度的要求
    this.methods[dyCheckRule.common] = function (value, field) {
      // 大文本、三种附件不检查字段长度
      var inputMode = field.definition.inputMode;
      var dbDataType = field.definition.dbDataType;
      if (
        (inputMode == dyFormInputMode.textArea && dbDataType == "16") ||
        inputMode == dyFormInputMode.accessoryImg ||
        inputMode == dyFormInputMode.accessory3 ||
        inputMode == dyFormInputMode.accessory1
      ) {
        return {
          valid: true,
        };
      }
      var maxLength = parseInt(field.definition.length);
      // 有定义最大字段要求则检查
      if (maxLength > 0 && value) {
        var v = value;
        if (typeof value == "object") {
          v = JSON.stringify(value);
        }
        if (v.length > maxLength) {
          return {
            valid: false,
            tipMsg: "长度不能大于" + maxLength + "个字符",
          };
        }
      }
      // 其他情况均返回成功
      return {
        valid: true,
      };
    };
    // 11、url
    this.methods[dyCheckRule.url] = function (value) {
      if (_.isEmpty(_.trim(value)) || regUrl.test(value)) {
        return {
          valid: true,
        };
      }
      return {
        valid: false, // false
        // 表示校验不通过，true表示校验通过
        tipMsg: "非有效的URL地址", // 当校验不通过时，提示的信息
      };
    };
    // 12、email
    this.methods[dyCheckRule.email] = function (value) {
      if (_.isEmpty(_.trim(value)) || regMail.test(value)) {
        return {
          valid: true,
        };
      }
      return {
        valid: false, // false
        // 表示校验不通过，true表示校验通过
        tipMsg: "非有效的email地址", // 当校验不通过时，提示的信息
      };
    };
    // 13、身份证
    this.methods[dyCheckRule.idCard] = function (value) {
      // 身份证号码的验证规则
      function isIdCardNo(num) {
        // if (isNaN(num))
        // {alert("输入的不是数字！"); return
        // false;}
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
            var D = new Date("19" + a[3] + "/" + a[4] + "/" + a[5]);
            var B = D.getYear() == a[3] && D.getMonth() + 1 == a[4] && D.getDate() == a[5];
          } else {
            // var D = new Date(a[3] + '/' + a[4] + '/' + a[5]);
            // var B = D.getFullYear() == a[3] && D.getMonth() + 1 == a[4] && D.getDate() == a[5];
          }
          if (!B) {
            // alert("输入的身份证号 "+ a[0] +"
            // 里出生日期不对。");
            return false;
          }
        }
        if (!re.test(num)) {
          // alert("身份证最后一位只能是数字和字母。");
          return false;
        }
        return true;
      }
      if (_.isEmpty(_.trim(value)) || isIdCardNo(value)) {
        return {
          valid: true,
        };
      }
      return {
        valid: false, // false
        // 表示校验不通过，true表示校验通过
        tipMsg: "非有效的身份证", // 当校验不通过时，提示的信息
      };
    };
    // 14、固定电话
    this.methods[dyCheckRule.tel] = function (value) {
      var tel = /^(\d{3,4}-?)?\d{7,9}$/g;
      if (_.isEmpty(_.trim(value)) || tel.test(value)) {
        return {
          valid: true,
        };
      }
      return {
        valid: false, // false
        // 表示校验不通过，true表示校验通过
        tipMsg: "非有效的电话号", // 当校验不通过时，提示的信息
      };
    };
    // 15、手机
    this.methods[dyCheckRule.mobilePhone] = function (value) {
      var length = value.length;
      if (_.isEmpty(_.trim(value)) || (length == 11 && regMobilePhone.test(value))) {
        return {
          valid: true,
        };
      }
      return {
        valid: false, // false
        // 表示校验不通过，true表示校验通过
        tipMsg: "非有效的手机号", // 当校验不通过时，提示的信息
      };
    };
    // 16、整数
    this.methods[dyCheckRule.num_int] = function (value) {
      value = value + "";
      var n13 = /^[-\+]?\d+$/;
      if (_.isEmpty(_.trim(value)) || (n13.test(value) && value.length <= 9)) {
        return {
          valid: true,
        };
      }
      return {
        valid: false, // false
        tipMsg: "整数", // 当校验不通过时，提示的信息
      };
    };
    // 17、正整数
    this.methods[dyCheckRule.num_int_positive] = function (value) {
      value = value + "";
      var n13 = /^[-\+]?\d+$/;
      if (_.isEmpty(_.trim(value)) || (n13.test(value) && parseInt(value) >= 0)) {
        return {
          valid: true,
        };
      }
      return {
        valid: false, // false
        tipMsg: "正整数", // 当校验不通过时，提示的信息
      };
    };
    // 18、负整数
    this.methods[dyCheckRule.num_int_negtive] = function (value) {
      value = value + "";
      var n13 = /^[-\+]?\d+$/;
      if (_.isEmpty(_.trim(value)) || (n13.test(value) && parseInt(value) <= 0)) {
        return {
          valid: true,
        };
      }
      return {
        valid: false, // false
        tipMsg: "负整数", // 当校验不通过时，提示的信息
      };
    };
    // 19、长整数
    this.methods[dyCheckRule.num_long] = function (value) {
      value = value + "";
      var n13 = /^[-\+]?\d+$/;
      if (_.isEmpty(_.trim(value)) || (n13.test(value) && value.length <= 16)) {
        return {
          valid: true,
        };
      }
      return {
        valid: false, // false
        tipMsg: "长整数", // 当校验不通过时，提示的信息
      };
    };
    // 20、浮点数
    this.methods[dyCheckRule.num_float] = function (value) {
      value = value + "";
      var n15 = /^[-\+]?\d+(\.\d+)?$/;
      if (_.isEmpty(_.trim(value)) || (n15.test(value) && value.length <= 12)) {
        return {
          valid: true,
        };
      }
      return {
        valid: false, // false
        tipMsg: "浮点数", // 当校验不通过时，提示的信息
      };
    };
    // 21、双精度浮点数
    this.methods[dyCheckRule.num_double] = function (value) {
      value = value + "";
      var n15 = /^[-\+]?\d+(\.\d+)?$/;
      if (_.isEmpty(_.trim(value)) || (n15.test(value) && value.length <= 18)) {
        return {
          valid: true,
        };
      }
      return {
        valid: false, // false
        tipMsg: "双精度浮点数", // 当校验不通过时，提示的信息
      };
    };
  },
  // 根据字段检验规则创建验证器
  createRequiredValidator: function (checkRule) {
    return new Validator(1, this.methods[checkRule.value]);
  },
  // 根据字段检验规则创建验证器
  createValidator: function (checkRule) {
    return new Validator(checkRule.value, this.methods[checkRule.value]);
  },
  // 创建自定义的验证器
  createCustomValidator: function (fn) {
    return new Validator("custom", fn);
  },
  // 验证处理
  validate: function (bubble) {
    console.log("validate==============", bubble);
    var _self = this;
    var errors = [];
    var formFields = _self.dyform.getFields();
    _.each(formFields, function (formField) {
      if (formField.isSubform()) {
        var subformFieldErrors = _self._validateSubform(formField);
        errors = errors.concat(subformFieldErrors);
      } else {
        var validResults = formField.validate();
        errors = errors.concat(validResults);
      }
    });
    var validationResult = new ValidationResult(this.dyform, errors);
    return validationResult;
  },
  // 验证从表
  _validateSubform: function (subform) {
    // var errors = [];
    // var formFields = subform.getFields();
    // _.each(formFields, function (formField, i) {
    //   var validResults = formField.validate();
    //   errors = errors.concat(validResults);
    // });
    // return errors;
    return subform.validate();
  },
});

module.exports = DyformValidation;
