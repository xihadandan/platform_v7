<template>
  <a-form-model-item
    class="widget-form-input-number"
    :style="itemStyle"
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="widgetClass"
  >
    <template v-if="!displayAsLabel">
      <!-- 数字输入框 -->
      <!-- precision，保留小数点后几位 -->
      <a-input-group compact :class="focusClass">
        <a-button @click="numberStepHandle('down')" icon="minus" :disabled="disable || readonly" v-if="stepBtnTypeLR"></a-button>
        <a-input-number
          :ref="'inputNumberRef_' + widget.configuration.code"
          :disabled="disable || readonly"
          v-model="formData[widget.configuration.code]"
          :formatter="numberFormatter"
          :parser="numberParser"
          :max="maxNum"
          :min="minNum"
          :precision="precisionNum"
          :step="widget.configuration.step || 1"
          :placeholder="$t('placeholder', widget.configuration.placeholder)"
          @focus="onFocus"
          @blur="onBlur"
          @change="onChange"
          :style="inputStyle"
          :class="[!widget.configuration.stepBtnShow || stepBtnTypeLR ? 'hiddenStep' : '']"
        ></a-input-number>
        <a-button @click="numberStepHandle('up')" icon="plus" :disabled="disable || readonly" v-if="stepBtnTypeLR"></a-button>
      </a-input-group>
      <template v-if="configuration.isCapital && configuration.capitalPosition === 'outside'">
        <div style="line-height: 18px">{{ capitalAmount }}</div>
      </template>
    </template>
    <template v-else>
      <span class="textonly" :title="displayValue()">{{ displayValue() }}</span>
    </template>
  </a-form-model-item>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import { deepClone } from '@framework/vue/utils/util';
import './css/index.less';

export default {
  extends: FormElement,
  name: 'WidgetFormInputNumber',
  mixins: [widgetMixin],
  data() {
    return {
      numberRange: {
        MAX_INT_VALUE: new Number(2147483647),
        MIN_INT_VALUE: new Number(-2147483648),
        MAX_LONG_VALUE: new Number(9223372036854775807),
        MIN_LONG_VALUE: new Number(-9223372036854775808),
        MAX_DOUBLE_VALUE: new Number(1.7976931348623157e308),
        MIN_DOUBLE_VALUE: new Number(4.9e-324)
      },
      focusClass: ''
    };
  },
  components: {},
  computed: {
    // 设置小数位个数
    precisionNum() {
      var num = null;
      var dbDataType = this.widget.configuration.dbDataType;
      if (dbDataType == '12' || dbDataType == '15') {
        //双精度浮点数
        num = this.widget.configuration.decimalPlacesNumber || null;
      } else if (dbDataType == '17') {
        num = this.widget.configuration.scale || null;
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
    inputStyle() {
      let style = { width: '100%' };
      if (this.stepBtnTypeLR) {
        style.width = 'calc(100% - 64px)';
      }
      return style;
    },
    // 移动端时只有左右按钮
    stepBtnTypeLR() {
      return (
        this.widget.configuration.stepBtnShow &&
        (this.widget.configuration.stepBtnType == 'LR' || (this.designMode && this.designer && this.designer.terminalType == 'mobile'))
      );
    },
    capitalAmount() {
      let amount = '';
      if (this.configuration.isCapital) {
        amount = this.getCapitalAmount(this.formData[this.fieldCode]);
      }
      return amount;
    }
  },
  created() {
    if (!this.widget.configuration.widgetStyle) {
      this.widget.configuration.widgetStyle = {};
    }
  },
  beforeCreate() {},
  beforeMount() {
    var _self = this;
    if (this.rules) {
      let trigger = 'blur';
      if (this.configuration.validateRule && this.configuration.validateRule.trigger) {
        trigger = this.configuration.validateRule.trigger;
      }
      this.rules.splice(1, 0, {
        trigger,
        validator: function (rule, value, callback) {
          if (value == undefined || value === '') {
            callback();
            return;
          }
          var numberRule = _self.getNumberRule(_self.widget.configuration.dbDataType);
          var errorRule = '';
          for (var key in numberRule.rule) {
            if (_self.formData[_self.widget.configuration.code] != undefined) {
              errorRule = _self.otherValidate(key, numberRule.rule[key], _self.formData[_self.widget.configuration.code]) ? false : key;
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
        }
      });
    }
  },
  mounted() {
    this.formatFormDataValueIfNotNull();
  },
  beforeUpdate() {
    this.formatFormDataValueIfNotNull();
  },
  updated() {},
  filters: {},
  methods: {
    onBlur() {
      this.emitBlur();
      this.$refs[this.configuration.code].onFieldBlur();
      this.focusClass = '';
      if (this.configuration.isCapital && this.configuration.capitalPosition === 'inside') {
        const numberEl = this.$el.querySelector('.ant-input-number-input');
        let val = numberEl.value;
        if (val) {
          val = val.replace(/,/g, '');
          val = this.getCapitalAmount(val);
          const timer = setTimeout(() => {
            clearTimeout(timer);
            numberEl.value = val;
          }, 100);
        }
      }
    },
    onChange() {
      this.emitChange();
      this.$refs[this.configuration.code].onFieldChange();

      if (this.configuration.isCapital && this.configuration.capitalPosition === 'other') {
        if (this.configuration.capitalPositionOther) {
          const amount = this.getCapitalAmount(this.formData[this.fieldCode]);
          this.form.setFieldValue(this.configuration.capitalPositionOther, amount);
        }
      }
    },
    onFocus() {
      this.focusClass = 'theme-input-number-focus';
    },
    setValue(val) {
      val = this.numberParser(val);
      this.formData[this.widget.configuration.code] = val && typeof val === 'string' ? Number(val) : val;
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
    //数字输入框-左右-步长处理
    numberStepHandle(type) {
      var $inputNumber = this.$refs['inputNumberRef_' + this.widget.configuration.code];
      $inputNumber.$refs.inputNumberRef.stepFn(type);
      $inputNumber.$refs.inputNumberRef.stop();
    },
    // 获取大写金额
    getCapitalAmount(input) {
      const amount = Number(input);
      if (isNaN(amount)) {
        return '';
      }

      // 保留两位小数
      const fixedAmount = amount.toFixed(2);

      // 中文数字
      const cnNums = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
      const cnIntRadice = ['', '拾', '佰', '仟'];
      const cnIntUnits = ['', '万', '亿', '兆'];
      const cnDecUnits = ['角', '分'];
      const cnInteger = '整';
      const cnIntLast = '元';

      let [integerNum, decimalNum] = fixedAmount.split('.');
      let chineseStr = '';

      // 处理整数部分
      if (parseInt(integerNum, 10) > 0) {
        let zeroCount = 0;
        const intLen = integerNum.length;
        for (let i = 0; i < intLen; i++) {
          const n = integerNum.charAt(i);
          const p = intLen - i - 1;
          const q = Math.floor(p / 4);
          const m = p % 4;

          if (n === '0') {
            zeroCount++;
          } else {
            if (zeroCount > 0) {
              chineseStr += cnNums[0];
            }
            zeroCount = 0;
            chineseStr += cnNums[parseInt(n)] + cnIntRadice[m];
          }
          if (m === 0 && zeroCount < 4) {
            chineseStr += cnIntUnits[q];
          }
        }
        chineseStr += cnIntLast;
      } else {
        chineseStr = '零' + cnIntLast;
      }

      // 处理小数部分
      if (decimalNum !== '00') {
        for (let i = 0; i < decimalNum.length; i++) {
          const n = decimalNum.charAt(i);
          if (n !== '0') {
            chineseStr += cnNums[parseInt(n)] + cnDecUnits[i];
          }
        }
      } else {
        // chineseStr += cnInteger;
      }

      return chineseStr;
    },
    numberParser(value) {
      let val = value != undefined ? value + '' : '';
      if (this.configuration.dbDataType == '15' || this.configuration.dbDataType == '12' || this.configuration.dbDataType == '17') {
        val = this.convertNumber(val, value);
      }
      if (val && this.widget.configuration.formatNumber) {
        val = val.replace(/\$\s?|(,*)/g, '');
      }
      return val;
    },
    //数字格式化，千位分隔符
    numberFormatter(value) {
      let val = value != undefined ? value + '' : '';
      if (this.configuration.dbDataType == '15' || this.configuration.dbDataType == '12' || this.configuration.dbDataType == '17') {
        val = this.convertNumber(val, value);
      }
      if (val && this.widget.configuration.formatNumber) {
        val = val.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
      }
      return val;
    },
    convertNumber(val, value) {
      const numberMaxLength = 16; // number 最大长度16 = 整数长度 + 小数长度
      let integralLength = this.configuration.length; // 整数长度
      let decimalLength = this.configuration.decimalPlacesNumber; // 小数长度
      if (this.configuration.dbDataType == '17') {
        integralLength = this.configuration.precision;
        decimalLength = this.configuration.scale;
      }
      value = value != undefined ? value + '' : '';
      if (value.indexOf('.') != -1) {
        let integralNumber = value.split('.')[0]; // 整数
        let decimalNumber = value.split('.')[1]; // 小数
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
      var result = '';
      value = this.numberParser(value);
      if (value || value == 0) {
        if (type == 'isInteger') {
          //整数
          result = /^[-\+]?[Ee\d]+$/.test(value);
        } else if (type == 'isIntLtZero') {
          //负整数
          result = value < 0;
        } else if (type == 'isIntGtZero') {
          //正整数
          result = value > 0;
        } else if (type == 'numberRangeMax') {
          //最大数限制
          result = value <= rule;
        } else if (type == 'numberRangeMin') {
          //最小数限制
          result = value >= rule;
        } else if (type == 'maxlength') {
          //字段长度
          result = (value + '').length <= rule;
        } else if (type == 'length') {
          //字段长度
          value = value + '';
          if (value.indexOf('.') != -1) {
            result = value.length - 1 <= rule;
          } else {
            result = value.length <= rule;
          }
        } else if (type == 'isFloat') {
          //浮点数/双精度浮点数
          result = this.isFloatValidate(value, rule);
        } else if (type == 'isNumber') {
          //Number
          result = this.isNumberValidate(value, {
            scale: this.widget.configuration.scale,
            precision: this.widget.configuration.precision
          });
        } else if (type == 'minNum') {
          result = value >= rule;
        } else if (type == 'maxNum') {
          result = value <= rule;
        }
      }
      return result;
    },
    // 判断数值类型，包括整数和浮点数
    isNumberValidate(value, nd) {
      var didx,
        rtn = /^[-\+]?[Ee\d]+$/.test(value) || /^[-\+]?\d+(\.\d+)?$/.test(value) || /^[+-]?[\d]+([\.][\d]+)?([Ee][+-]?[\d]+)?$/.test(value);
      if (rtn && nd && (didx = nd.precision) > 0) {
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
        value = '' + value;
        var scaleIdx = value.indexOf('.');
        if (scaleIdx > -1) {
          return value.length - scaleIdx - 1 <= nd.decimal;
        }
      }
      return rtn;
    },
    getNumberRule: function (dbDataType) {
      var rule = {},
        msg = {};
      if (dbDataType == '12') {
        // 双精度浮点数
        // @see maxlength修改 bug:49960
        var decimal = parseInt(this.widget.configuration.decimalPlacesNumber, 10) || 0;
        rule = {
          isFloat: {
            decimal: decimal
          },
          maxlength: 18
        };
        msg = {
          isFloat: this.$t(
            'WidgetFormInputNumber.messages.doublePrecisionFloatingPointNumberDecimal',
            { decimal },
            '双精度浮点数,小数位不超过' + decimal + '位'
          ),
          maxlength: this.$t('WidgetFormInputNumber.messages.doublePrecisionFloatingPointNumberMaxDecimal', '双精度浮点数,不得超过18个字符')
        };
      } else if (dbDataType == '15') {
        // 浮点数
        // @see maxlength修改 bug:49960
        var decimal = parseInt(this.widget.configuration.decimalPlacesNumber, 10) || 0;
        rule = {
          isFloat: {
            decimal: decimal
          },
          maxlength: 12
        };
        msg = {
          isFloat: this.$t(
            'WidgetFormInputNumber.messages.floatingPointNumberDecimal',
            { decimal },
            '浮点数,小数位不超过' + decimal + '位'
          ),
          maxlength: this.$t('WidgetFormInputNumber.messages.floatingPointNumberMaxDecimal', '浮点数,不得超过12个字符')
        };
      } else if (dbDataType == '13') {
        // 整数
        rule = {
          isInteger: true,
          maxlength: 10,
          numberRangeMax: this.numberRange.MAX_INT_VALUE,
          numberRangeMin: this.numberRange.MIN_INT_VALUE
        };
        msg = {
          isInteger: this.$t('WidgetFormInputNumber.messages.integer', '整数'),
          maxlength: this.$t('WidgetFormInputNumber.messages.integerMaxDecimal', '整数,不得超过10个字符'),
          numberRangeMax: this.$t(
            'WidgetFormInputNumber.messages.integerMax',
            { max: this.numberRange.MAX_INT_VALUE },
            '整数，不得大于' + this.numberRange.MAX_INT_VALUE
          ),
          numberRangeMix: this.$t(
            'WidgetFormInputNumber.messages.integerMin',
            { min: this.numberRange.MIN_INT_VALUE },
            '整数，不得小于' + this.numberRange.MIN_INT_VALUE
          )
        };
      } else if (dbDataType == '131') {
        // 正整数
        rule = {
          isInteger: true,
          isIntGtZero: true,
          maxlength: 9,
          numberRangeMax: this.numberRange.MAX_INT_VALUE
        };
        msg = {
          isInteger: this.$t('WidgetFormInputNumber.messages.noInteger', '非整数'),
          isIntGtZero: this.$t('WidgetFormInputNumber.messages.intGtZero', '正整数'),
          maxlength: this.$t('WidgetFormInputNumber.messages.intGtZeroMaxDecimal', '正整数,不得超过9个字符'),
          numberRangeMax: this.$t(
            'WidgetFormInputNumber.messages.integerMax',
            { max: this.numberRange.MAX_INT_VALUE },
            '整数，不得大于' + this.numberRange.MAX_INT_VALUE
          )
        };
      } else if (dbDataType == '132') {
        // 负整数
        rule = {
          isInteger: true,
          isIntLtZero: true,
          // maxlength: 10,
          numberRangeMin: this.numberRange.MIN_INT_VALUE
        };
        msg = {
          isInteger: this.$t('WidgetFormInputNumber.messages.noInteger', '非整数'),
          isIntLtZero: this.$t('WidgetFormInputNumber.messages.intLtZero', '负整数'),
          maxlength: this.$t('WidgetFormInputNumber.messages.intLtZeroMaxDecimal', '负整数,不得超过10个字符'),
          numberRangeMix: this.$t(
            'WidgetFormInputNumber.messages.integerMin',
            { min: this.numberRange.MIN_INT_VALUE },
            '整数，不得小于' + this.numberRange.MIN_INT_VALUE
          )
        };
      } else if (dbDataType == '14') {
        // 长整数
        rule = {
          isInteger: true,
          maxlength: 16,
          numberRangeMax: this.numberRange.MAX_LONG_VALUE,
          numberRangeMin: this.numberRange.MIN_LONG_VALUE
        };
        msg = {
          isInteger: this.$t('WidgetFormInputNumber.messages.longInteger', '长整数'),
          maxlength: this.$t('WidgetFormInputNumber.messages.longIntegerMaxDecimal', '长整数,不得超过16个字符'),
          numberRangeMax: this.$t(
            'WidgetFormInputNumber.messages.integerMax',
            { max: this.numberRange.MAX_LONG_VALUE },
            '整数，不得大于' + this.numberRange.MAX_LONG_VALUE
          ),
          numberRangeMin: this.$t(
            'WidgetFormInputNumber.messages.integerMin',
            { min: this.numberRange.MIN_LONG_VALUE },
            '整数，不得小于' + this.numberRange.MIN_LONG_VALUE
          )
        };
      } else if (dbDataType == '17') {
        // Number
        var precision = parseInt(this.widget.configuration.precision, 10);
        var scale = parseInt(this.widget.configuration.scale, 10);
        rule = {
          isNumber: {
            precision: precision,
            scale: scale
          }
        };
        if (scale) {
          msg = {
            isNumber: this.$t(
              'WidgetFormInputNumber.messages.nonSignificantNumber',
              { precision, scale },
              '非有效数字:精度位' + precision + ',小数位' + scale
            )
          };
        } else {
          msg = {
            isNumber: this.$t(
              'WidgetFormInputNumber.messages.nonSignificantNumberPrecision',
              { precision },
              '非有效数字:精度位' + precision
            )
          };
        }
      }
      if (this.widget.configuration.length) {
        rule.length = this.widget.configuration.length;
        msg.length = this.$t(
          'WidgetFormInputNumber.messages.maxLength',
          { length: this.widget.configuration.length },
          '不能超过字段长度' + this.widget.configuration.length
        );
      }
      if (this.widget.configuration.min) {
        rule.minNum = parseFloat(this.widget.configuration.min);
        msg.minNum = this.$t(
          'WidgetFormInputNumber.messages.minValue',
          { min: this.widget.configuration.min },
          '不能小于最小值' + this.widget.configuration.min
        );
      }
      if (this.widget.configuration.max) {
        rule.maxNum = parseFloat(this.widget.configuration.max);
        msg.maxNum = this.$t(
          'WidgetFormInputNumber.messages.maxValue',
          { max: this.widget.configuration.max },
          '不能大于最大值' + this.widget.configuration.max
        );
      }

      return {
        rule: rule,
        msg: msg
      };
    },
    onFilter({ searchValue, comparator, source }) {
      if (source != undefined) {
        if (comparator == '>=') {
          return Number(source) >= Number(searchValue);
        } else if (comparator == '<=') {
          return Number(source) <= Number(searchValue);
        } else if (comparator == '<') {
          return Number(source) < Number(searchValue);
        } else if (comparator == '>') {
          return Number(source) > Number(searchValue);
        } else if (comparator == '!=') {
          return Number(searchValue) != Number(source);
        } else if (comparator == 'like') {
          return (source + '').indexOf(searchValue + '') > -1;
        } else if (comparator == 'between') {
          let isAfter = searchValue[0] != undefined ? Number(source) >= Number(searchValue[0]) : true,
            isBefore = searchValue[1] != undefined ? Number(source) <= Number(searchValue[1]) : true;
          return isAfter && isBefore;
        }
        return searchValue == source;
      }
      //TODO: 判断本组件值是否匹配
      return false;
    }
  }
};
</script>

<style lang="less"></style>
