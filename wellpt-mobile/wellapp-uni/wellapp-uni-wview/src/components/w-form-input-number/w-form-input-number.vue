<template>
  <uni-forms-item
    :ref="fieldCode"
    :label="itemLabel"
    :name="formModelItemProp"
    :label-position="widget.configuration.labelPosition"
    :class="widgetClass"
    :style="itemStyle"
  >
    <template v-if="!displayAsLabel">
      <uni-w-number-box
        v-model="formData[fieldCode]"
        :inputBorder="widget.configuration.uniConfiguration.inputBorder"
        :stepBtnShow="widget.configuration.stepBtnShow"
        :ref="'inputNumberRef_' + widget.configuration.code"
        :disabled="disable || readonly"
        :formatter="numberFormatter"
        :parser="numberParser"
        :max="maxNum"
        :min="minNum"
        :precision="precisionNum"
        :step="widget.configuration.step || 1"
        :placeholder="widget.configuration.placeholder"
        :formatNumber="widget.configuration.formatNumber"
        @focus="onFocus"
        @blur="onBlur"
        @change="onChange"
      />
    </template>
    <view v-else class="textonly">{{ displayValue() }}</view>
  </uni-forms-item>
</template>
<style lang="sass"></style>
<script type="text/babel">
import formElement from "../w-dyform/form-element.mixin";
import formCommonMixin from "../w-dyform/form-common.mixin";

export default {
  mixins: [formElement, formCommonMixin],
  props: {},
  components: {},
  computed: {
    // 设置小数位个数
    precisionNum() {
      var num = null;
      var dbDataType = this.widget.configuration.dbDataType;
      if (dbDataType == "12" || dbDataType == "15") {
        //双精度浮点数
        num = this.widget.configuration.decimalPlacesNumber || null;
      } else if (dbDataType == "17") {
        num = this.widget.configuration.scale !== null ? this.widget.configuration.scale : null;
      }
      return num;
    },
    maxNum() {
      var num = this.widget.configuration.max;
      return num || num == 0 ? num : Infinity;
    },
    minNum() {
      var num = this.widget.configuration.min;
      return num || num == 0 ? num : -Infinity;
    },
  },
  data() {
    return {
      configuration: this.widget.configuration,
      numberRange: {
        MAX_INT_VALUE: new Number(2147483647),
        MIN_INT_VALUE: new Number(-2147483648),
        MAX_LONG_VALUE: new Number(9223372036854775807),
        MIN_LONG_VALUE: new Number(-9223372036854775808),
        MAX_DOUBLE_VALUE: new Number(1.7976931348623157e308),
        MIN_DOUBLE_VALUE: new Number(4.9e-324),
      },
      focusClass: "",
    };
  },
  beforeMount() {
    var _self = this;
    _self.setCustomRules();
  },
  mounted() {
    this.formatFormDataValueIfNotNull();
  },
  beforeUpdate() {
    this.formatFormDataValueIfNotNull();
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", { inputBorder: false });
    }
  },
  methods: {
    onBlur() {
      this.emitBlur();
    },
    onChange() {
      this.emitChange();
    },
    onFocus() {},
    setCustomRules() {
      var _self = this;
      if (this.customRules) {
        let trigger = "blur";
        if (this.configuration.validateRule && this.configuration.validateRule.trigger) {
          trigger = this.configuration.validateRule.trigger;
        }
        this.customRules.splice(1, 0, {
          trigger,
          validateFunction: function (rule, value, data, callback) {
            var numberRule = _self.getNumberRule(_self.widget.configuration.dbDataType);
            var errorRule = "";
            for (var key in numberRule.rule) {
              if (_self.formData[_self.widget.configuration.code] != undefined) {
                errorRule = _self.otherValidate(
                  key,
                  numberRule.rule[key],
                  _self.formData[_self.widget.configuration.code]
                )
                  ? false
                  : key;
              }
              if (errorRule) {
                break;
              }
            }
            if (errorRule) {
              callback(numberRule.msg[errorRule]);
            } else {
              callback();
            }
          },
        });
      }
    },
    setValue(val) {
      this.formData[this.widget.configuration.code] = val && typeof val === "string" ? Number(val) : val;
      this.emitChange();
    },
    formatFormDataValueIfNotNull() {
      if (this.formData[this.widget.configuration.code] != undefined) {
        var val = this.formData[this.widget.configuration.code];
        this.setValue(val);
      }
    },
    // 显示值
    displayValue() {
      var value = this.formData[this.widget.configuration.code];
      value = this.numberFormatter(value);
      return value;
    },
    numberParser(value) {
      let val = value !== undefined && value !== null ? value + "" : "";
      if (val && this.widget.configuration.formatNumber) {
        val = val.replace(/\$\s?|(,*)/g, "");
      }
      if (
        this.configuration.dbDataType == "15" ||
        this.configuration.dbDataType == "12" ||
        this.configuration.dbDataType == "17"
      ) {
        val = this.convertNumber(val, value);
      }
      return val;
    },
    //数字格式化，千位分隔符
    numberFormatter(value) {
      let val = value !== undefined && value !== null ? value + "" : "";
      if (val && this.widget.configuration.formatNumber) {
        val = val.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
      }
      if (
        this.configuration.dbDataType == "15" ||
        this.configuration.dbDataType == "12" ||
        this.configuration.dbDataType == "17"
      ) {
        val = this.convertNumber(val, value);
      }
      return val;
    },
    convertNumber(val, value) {
      const numberMaxLength = 16; // number 最大长度16 = 整数长度 + 小数长度
      let integralLength = this.configuration.length; // 整数长度
      let decimalLength = this.configuration.decimalPlacesNumber; // 小数长度
      if (this.configuration.dbDataType == "17") {
        integralLength = this.configuration.precision;
        decimalLength = this.configuration.scale;
      }
      value = value != undefined ? value + "" : "";
      if (value.indexOf(".") != -1) {
        let integralNumber = value.split(".")[0]; // 整数
        let decimalNumber = value.split(".")[1]; // 小数
        if (decimalNumber !== 0) {
          decimalNumber = decimalNumber.substr(0, decimalLength);
        }
        if (integralNumber.length + decimalNumber.length > numberMaxLength) {
          integralLength = numberMaxLength - decimalNumber.length;
        }
        integralNumber = integralNumber.substr(0, integralLength);
        if (!decimalLength) {
          val = integralNumber;
        } else {
          val = `${integralNumber}.${decimalNumber}`;
        }
      } else {
        if (value.length > integralLength) {
          val = value.substr(0, integralLength);
        }
      }
      return val;
    },
    otherValidate(type, rule, value) {
      var result = "";
      if (value || value == 0) {
        if (type == "isInteger") {
          //整数
          result = /^[-\+]?[Ee\d]+$/.test(value);
        } else if (type == "isIntLtZero") {
          //负整数
          result = value < 0;
        } else if (type == "isIntGtZero") {
          //正整数
          result = value > 0;
        } else if (type == "numberRangeMax") {
          //最大数限制
          result = value <= rule;
        } else if (type == "numberRangeMin") {
          //最小数限制
          result = value >= rule;
        } else if (type == "maxlength") {
          //字段长度
          result = (value + "").length <= rule;
        } else if (type == "length") {
          //字段长度
          value = value + "";
          if (value.indexOf(".") != -1) {
            result = value.length - 1 <= rule;
          } else {
            result = value.length <= rule;
          }
        } else if (type == "isFloat") {
          //浮点数/双精度浮点数
          result = this.isFloatValidate(value, rule);
        } else if (type == "isNumber") {
          //Number
          result = this.isNumberValidate(value, {
            scale: this.widget.configuration.scale,
            precision: this.widget.configuration.precision,
          });
        } else if (type == "minNum") {
          result = value >= rule;
        } else if (type == "maxNum") {
          result = value <= rule;
        }
      }
      return result;
    },
    // 判断数值类型，包括整数和浮点数
    isNumberValidate(value, nd) {
      var didx,
        rtn =
          /^[-\+]?[Ee\d]+$/.test(value) ||
          /^[-\+]?\d+(\.\d+)?$/.test(value) ||
          /^[+-]?[\d]+([\.][\d]+)?([Ee][+-]?[\d]+)?$/.test(value);
      if (rtn && nd && (didx = nd.precision) > 0) {
        value = "" + value;
        // 有效数位 ：从左边第一个不为 0 的数算起，到末位数字为止的所有数字，小数点和负号不计入有效位数。精确到小数点右边
        // s位，并四舍五入
        var scaleIdx = value.indexOf(".");
        nd.scale = parseInt(nd.scale) || 0;
        scaleIdx > -1 && nd.scale > 0 && (rtn = rtn && value.length - scaleIdx - 1 <= nd.scale);
        scaleIdx > -1 && didx++;
        (value.indexOf("-") > -1 || value.indexOf("+") > -1) && didx++;
        var val = Number(value).toPrecision();
        var sc = 0;
        scaleIdx > -1 && (sc = val.length - scaleIdx - 1);
        if (nd.scale) {
          return val.length <= didx && sc <= nd.scale;
        }
        return val.length <= didx;
      }
      return rtn;
    },
    isFloatValidate(value, nd) {
      var rtn = false;
      if (value != null || value != undefined) {
        var rtn = /^[-\+]?\d+(\.\d+)?$/.test(value);
      }
      if (rtn && nd && nd.decimal > 0) {
        value = "" + value;
        var scaleIdx = value.indexOf(".");
        if (scaleIdx > -1) {
          return value.length - scaleIdx - 1 <= nd.decimal;
        }
      }
      return rtn;
    },
    getNumberRule: function (dbDataType) {
      var rule = {},
        msg = {};
      if (dbDataType == "12") {
        // 双精度浮点数
        // @see maxlength修改 bug:49960
        var decimal = parseInt(this.widget.configuration.decimalPlacesNumber, 10) || 0;
        rule = {
          isFloat: {
            decimal: decimal,
          },
          maxlength: 18,
        };
        msg = {
          isFloat: "双精度浮点数,小数位不超过" + decimal + "位",
          maxlength: "双精度浮点数,不得超过18个字符",
        };
      } else if (dbDataType == "15") {
        // 浮点数
        // @see maxlength修改 bug:49960
        var decimal = parseInt(this.widget.configuration.decimalPlacesNumber, 10) || 0;
        rule = {
          isFloat: {
            decimal: decimal,
          },
          maxlength: 12,
        };
        msg = {
          isFloat: "  浮点数,小数位不超过" + decimal + "位",
          maxlength: "浮点数,不得超过12个字符",
        };
      } else if (dbDataType == "13") {
        // 整数
        rule = {
          isInteger: true,
          maxlength: 10,
          numberRangeMax: this.numberRange.MAX_INT_VALUE,
          numberRangeMin: this.numberRange.MIN_INT_VALUE,
        };
        msg = {
          isInteger: "整数",
          maxlength: "整数,不得超过10个字符",
          numberRangeMax: "整数，不得大于" + this.numberRange.MAX_INT_VALUE,
          numberRangeMix: "整数，不得小于" + this.numberRange.MIN_INT_VALUE,
        };
      } else if (dbDataType == "131") {
        // 正整数
        rule = {
          isInteger: true,
          isIntGtZero: true,
          maxlength: 9,
          numberRangeMax: this.numberRange.MAX_INT_VALUE,
        };
        msg = {
          isInteger: "非整数",
          isIntGtZero: "正整数",
          maxlength: "正整数,不得超过9个字符",
          numberRangeMax: "整数，不得大于" + this.numberRange.MAX_INT_VALUE,
        };
      } else if (dbDataType == "132") {
        // 负整数
        rule = {
          isInteger: true,
          isIntLtZero: true,
          // maxlength: 10,
          numberRangeMin: this.numberRange.MIN_INT_VALUE,
        };
        msg = {
          isInteger: "非整数",
          isIntLtZero: "负整数",
          maxlength: "负整数,不得超过10个字符",
          numberRangeMin: "整数，不得小于" + this.numberRange.MIN_INT_VALUE,
        };
      } else if (dbDataType == "14") {
        // 长整数
        rule = {
          isInteger: true,
          maxlength: 16,
          numberRangeMax: this.numberRange.MAX_LONG_VALUE,
          numberRangeMin: this.numberRange.MIN_LONG_VALUE,
        };
        msg = {
          isInteger: "长整数",
          maxlength: "长整数,不得超过16个字符",
          numberRangeMax: "整数，不得大于" + this.numberRange.MAX_LONG_VALUE,
          numberRangeMin: "整数，不得小于" + this.numberRange.MIN_LONG_VALUE,
        };
      } else if (dbDataType == "17") {
        // Number
        var precision = parseInt(this.widget.configuration.precision, 10);
        var scale = parseInt(this.widget.configuration.scale, 10);
        rule = {
          isNumber: {
            precision: precision,
            scale: scale,
          },
        };
        if (scale) {
          msg = {
            isNumber: "非有效数字:精度位" + precision + ",小数位" + scale,
          };
        } else {
          msg = {
            isNumber: "非有效数字:精度位" + precision,
          };
        }
      }
      if (this.widget.configuration.length) {
        rule.length = this.widget.configuration.length;
        msg.length = "不能超过字段长度" + this.widget.configuration.length;
      }
      if (this.widget.configuration.min) {
        rule.minNum = parseFloat(this.widget.configuration.min);
        msg.minNum = "不能小于最小值" + this.widget.configuration.min;
      }
      if (this.widget.configuration.max) {
        rule.maxNum = parseFloat(this.widget.configuration.max);
        msg.maxNum = "不能大于最大值" + this.widget.configuration.max;
      }

      return {
        rule: rule,
        msg: msg,
      };
    },
  },
};
</script>
