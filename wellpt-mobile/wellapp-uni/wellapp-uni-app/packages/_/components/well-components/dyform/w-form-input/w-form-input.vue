<template>
  <uni-forms-item
    v-if="!hidden"
    :label="itemLabel"
    :name="formModelItemProp"
    :label-position="widget.configuration.labelPosition"
    :class="widgetClass"
    :style="itemStyle"
    :ref="fieldCode"
  >
    <template v-if="!displayAsLabel">
      <view v-if="widget.configuration.type == 'textarea'" :class="['input-textarea']">
        <uni-w-easyinput
          type="textarea"
          v-model="formData[fieldCode]"
          :class="inputClass"
          :inputBorder="
            widget.configuration.uniConfiguration ? widget.configuration.uniConfiguration.inputBorder : false
          "
          :placeholder="$t('placeholder', widget.configuration.placeholder)"
          :autoHeight="widget.configuration.uniConfiguration ? widget.configuration.uniConfiguration.autoHeight : false"
          :clearable="widget.configuration.clearBtnShow || false"
          :disabled="disable || readonly"
          :maxlength="maxLengthCompute"
          :textLengthShow="
            widget.configuration.length && widget.configuration.wordCountPrompt && !widget.configuration.dbDataType
          "
          @focus="onFocus"
          @blur="onBlur"
          @change="onChange"
        />
      </view>
      <template v-else-if="widget.configuration.type == 'input-password'">
        <uni-w-easyinput
          type="password"
          v-model="formData[fieldCode]"
          :class="inputClass"
          :inputBorder="
            widget.configuration.uniConfiguration ? widget.configuration.uniConfiguration.inputBorder : false
          "
          :clearable="widget.configuration.clearBtnShow || false"
          :placeholder="$t('placeholder', widget.configuration.placeholder)"
          :disabled="disable || readonly"
          :maxlength="maxLengthCompute"
          passwordIcon
          :textLengthShow="widget.configuration.length && widget.configuration.wordCountPrompt"
          @focus="onFocus"
          @blur="onBlur"
          @change="onChange"
          @iconClick="iconClick"
        />
      </template>
      <template v-else>
        <template v-if="disable || readonly">
          <uni-w-easyinput
            v-model="formatValue"
            :inputBorder="
              widget.configuration.uniConfiguration ? widget.configuration.uniConfiguration.inputBorder : false
            "
            :clearable="false"
            :class="inputClass"
            :placeholder="$t('placeholder', widget.configuration.placeholder)"
            :disabled="disable || readonly"
            :maxlength="maxLengthCompute"
            :textLengthShow="widget.configuration.length && widget.configuration.wordCountPrompt"
            :passwordIcon="showPasswordEye || false"
            :eyesOpen="eyesOpen"
            @focus="onFocus"
            @blur="onBlur"
            @change="onChange"
            @eyeClick="eyeClickHandle"
          >
            <template v-if="hasAddonSetting">
              <template slot="prefix" v-if="hasPrefix">
                <w-icon
                  :icon="widget.configuration.addonFrontIconValue"
                  class="content-clear-icon slot-icon"
                  isPc
                  :size="iconSize"
                ></w-icon>
              </template>
              <template slot="suffix" v-if="hasSuffix">
                <w-icon
                  :icon="widget.configuration.addonEndIconValue"
                  class="content-clear-icon slot-icon"
                  isPc
                  :size="iconSize"
                ></w-icon>
              </template>
              <view class="addon-before" slot="addonBefore" v-if="hasAddonBefore">
                <text class="text">{{ addonBeforeValue }}</text>
              </view>
              <view class="addon-after" slot="addonAfter" v-if="hasAddonAfter">
                <text class="text">{{ addonAfterValue }}</text>
              </view>
            </template>
          </uni-w-easyinput>
        </template>
        <uni-w-easyinput
          v-else
          v-model="showValue"
          :class="inputClass"
          :inputBorder="
            widget.configuration.uniConfiguration ? widget.configuration.uniConfiguration.inputBorder : false
          "
          :clearable="widget.configuration.clearBtnShow || false"
          :placeholder="$t('placeholder', widget.configuration.placeholder)"
          :disabled="disable || readonly"
          :maxlength="maxLengthCompute"
          :textLengthShow="widget.configuration.length && widget.configuration.wordCountPrompt"
          :passwordIcon="showPasswordEye || false"
          :eyesOpen="eyesOpen"
          @focus="onFocus"
          @blur="onBlur"
          @change="onChange"
          @iconClick="iconClick"
          @eyeClick="eyeClickHandle"
          @clear="onClear"
        >
          <template v-if="hasAddonSetting">
            <template slot="prefix" v-if="hasPrefix">
              <w-icon
                :icon="widget.configuration.addonFrontIconValue"
                class="content-clear-icon slot-icon"
                isPc
                :size="iconSize"
              ></w-icon>
            </template>
            <template slot="suffix" v-if="hasSuffix">
              <w-icon
                :icon="widget.configuration.addonEndIconValue"
                class="content-clear-icon slot-icon"
                isPc
                :size="iconSize"
              ></w-icon>
            </template>
            <view class="addon-before" slot="addonBefore" v-if="hasAddonBefore">
              <template v-if="addonBeforeOptions.length > 1">
                <uni-w-data-select
                  v-model="addonBeforeValue"
                  :localdata="addonBeforeOptions"
                  @change="onChangeAddonBeforeSelect"
                  ref="addonBeforeSelectRef"
                  :clear="false"
                ></uni-w-data-select>
              </template>
              <text class="text" v-else>{{ addonBeforeValue }}</text>
            </view>
            <view class="addon-after" slot="addonAfter" v-if="hasAddonAfter">
              <template v-if="addonAfterOptions.length > 1">
                <uni-w-data-select
                  v-model="addonAfterValue"
                  :localdata="addonAfterOptions"
                  @change="onChangeAddonAfterSelect"
                  ref="addonAfterSelectRef"
                  :clear="false"
                ></uni-w-data-select>
              </template>
              <text class="text" v-else>{{ addonAfterValue }}</text>
            </view>
          </template>
        </uni-w-easyinput>
      </template>
    </template>
    <view v-else :class="['textonly', inputClass]" :title="displayTitleValue()">
      <template v-if="widget.configuration.type == 'input'">
        <text v-if="hasAddonSetting && hasAddonBefore">{{ addonBeforeValue }}</text>
        <text>{{ displayValue() }}</text>
        <text v-if="hasAddonSetting && hasAddonAfter">{{ addonAfterValue }}</text>
      </template>
      <template v-else-if="widget.configuration.type == 'textarea'">
        <text v-html="displayValue()"></text>
      </template>
      <text v-else>{{ displayValue() }}</text>
    </view>
  </uni-forms-item>
</template>
<style lang="scss">
/* #ifdef APP-PLUS */
@import "./css/index.scss";
/* #endif */
</style>
<script type="text/babel">
import { kebabCase, map } from "lodash";
import formElement from "../w-dyform/form-element.mixin";
import formCommonMixin from "../w-dyform/form-common.mixin";
// #ifndef APP-PLUS
import "./css/index.scss";
// #endif
export default {
  mixins: [formElement, formCommonMixin],
  props: {},
  components: {},
  computed: {
    formatValue: {
      get() {
        return this.getFormatValue();
      },
      set(val) {},
    },
    hasAddonSetting() {
      //文本类型(普通、email、电话)
      var iscontentFormat = ["text", "email", "telephoneNumber"].indexOf(this.widget.configuration.contentFormat) > -1;
      return iscontentFormat && this.widget.configuration.addonSetting;
    },
    hasSuffix() {
      this.hasSuffixIcon = false;
      //后置图标
      if (this.widget.configuration.addonValue) {
        var hasAddonEndIcon = this.widget.configuration.addonValue.indexOf("addonEndIcon");
        this.hasSuffixIcon = hasAddonEndIcon > -1 && this.widget.configuration.addonEndIconValue;
      }
      return this.hasSuffixIcon || this.widget.configuration.wordCountPrompt;
    },
    hasPrefix() {
      this.hasPrefixIcon = false;
      //前置图标
      if (this.widget.configuration.addonValue) {
        var hasAddonFrontIcon = this.widget.configuration.addonValue.indexOf("addonFrontIcon");
        this.hasPrefixIcon = hasAddonFrontIcon > -1 && this.widget.configuration.addonFrontIconValue;
      }
      return this.hasPrefixIcon;
    },
    hasAddonAfter() {
      // 后标签
      if (this.widget.configuration.addonValue) {
        var hasAddonEnd = this.widget.configuration.addonValue.indexOf("addonEnd");
        this.addonAfterOptions = map(this.widget.configuration.addonEndValue || [], (item) => {
          return {
            text: item,
            value: item,
          };
        });
        if (this.addonAfterOptions.length > 0) {
          this.setAddonAfterSelectVal();
        }
        return hasAddonEnd > -1 && this.addonAfterOptions.length > 0;
      }
      return false;
    },
    hasAddonBefore() {
      // 前标签
      if (this.widget.configuration.addonValue) {
        var hasAddonFront = this.widget.configuration.addonValue.indexOf("addonFront");
        this.addonBeforeOptions = map(this.widget.configuration.addonFrontValue || [], (item) => {
          return {
            text: item,
            value: item,
          };
        });
        if (this.addonBeforeOptions.length > 0) {
          this.setAddonBeforeSelectVal();
        }
        return hasAddonFront > -1 && this.addonBeforeOptions.length > 0;
      }
      return false;
    },
    // 显示脱敏按钮
    showPasswordEye() {
      //文本类型(手机、email、电话、身份证)，普通和邮编不需要
      var iscontentFormat = ["text", "postCode"].indexOf(this.widget.configuration.contentFormat) == -1;
      return this.widget.configuration.type == "input" && iscontentFormat && this.configuration.showPasswordEye;
    },
    //脱敏
    hasTextDesensitization() {
      //文本类型(手机、email、电话、身份证)，普通和邮编不需要
      var iscontentFormat = ["text", "postCode"].indexOf(this.widget.configuration.contentFormat) == -1;
      return (
        this.widget.configuration.type == "input" && iscontentFormat && this.widget.configuration.istextDesensitization
      );
    },
    //字段长度
    maxLengthCompute() {
      var maxLength = this.widget.configuration.length;
      var length = maxLength;
      // 多行文本框，大字段时不限制输入字数,最大输入长度，设置为 -1 的时候不限制最大长度
      if (this.widget.configuration.type == "textarea" && this.widget.configuration.dbDataType) {
        length = -1;
      }
      return length || -1;
    },
    inputClass() {
      var className = [];
      if (this.widget.configuration.style) {
        // if (this.widget.configuration.style.textAlign) {
        //   className.push("align-" + this.widget.configuration.style.textAlign);
        // }
      }
      return className.join(" ");
    },
  },
  data() {
    let configuration = this.widget.configuration;
    return {
      configuration,
      showValue: "",
      visible: true,
      eyesOpen: false,
      autoCompleteOptions: [],
      hasSuffixIcon: false,
      hasPrefixIcon: false,
      addonAfterValue: "",
      addonBeforeValue: "",
      addonAfterOptions: [], //额外元素,后置标签下拉框选项
      addonBeforeOptions: [], //额外元素,前置标签下拉框选项
      arrEntities: {
        //普通字符转换成转意符
        "<": "&lt;",
        ">": "&gt;",
        "&": "&amp;",
        '"': "&quot;",
        "'": "&apos;",
      },
      iconSize: 16,
    };
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", { inputBorder: false, autoHeight: false });
    }
  },
  beforeMount() {},
  mounted() {
    this.eyesOpen = !this.widget.configuration.istextDesensitization;
    if (this.eyesOpen) {
      this.showValue = this.formData[this.fieldCode];
    } else if (this.formData[this.fieldCode]) {
      this.setValue(this.formData[this.fieldCode]);
    }
    // this.$nextTick(() => {
    //   this.inputStyleSetting();
    // });
  },
  methods: {
    setValue(val) {
      this.formData[this.fieldCode] = val;
      if (this.configuration.type == "input") {
        if (this.disable || this.readonly) {
          this.formData[this.fieldCode] = val;
        } else {
          this.showValue = val;
          if (this.hasTextDesensitization || !this.eyesOpen) {
            this.showValue = this.getFormatValue();
          }
        }
      }
      if (this.configuration.type == "textarea") {
        this.textareaSrcoll();
      }
      this.clearValidate();
      this.emitChange();
    },
    //输入框动态脱敏
    desenInputText(e) {
      const ind = e.target.selectionStart - 1;
      let value = this.formData[this.fieldCode] || "";
      const showValue = this.showValue;
      const isAdd = showValue.length > value.length;
      const num = Math.abs(value.length - showValue.length);
      if (isAdd) {
        value = value.slice(0, ind - num + 1) + showValue.slice(ind - num + 1, ind + 1) + value.slice(ind - num + 1);
      } else {
        value = value.slice(0, ind + 1) + value.slice(ind + num + 1);
      }
      this.formData[this.fieldCode] = value;
      this.showValue = this.getFormatValue();
      this.$nextTick(() => {
        const elem = e.target;
        if (elem.setSelectionRange) {
          elem.setSelectionRange(ind + 1, ind + 1);
        } else {
          const range = elem.createTextRange();
          range.moveStart("character", ind + 1);
          range.moveEnd("character", ind + 1);
          range.select();
        }
      });
    },
    onBlur(e) {
      if (this.configuration.type == "input") {
        if (this.hasTextDesensitization || !this.eyesOpen) {
          this.setValue(e.target.value);
        }
      }
      this.emitBlur();
    },
    onChange(value) {
      if (this.configuration.type == "input") {
        if (this.hasTextDesensitization || !this.eyesOpen) {
          // this.desenInputText(e);
          this.formData[this.fieldCode] = value;
        } else {
          this.formData[this.fieldCode] = this.showValue;
        }
      } else {
        this.formData[this.fieldCode] = value;
      }
      console.log(this.formData[this.fieldCode]);
      if (this.configuration.type == "textarea") {
        this.textareaSrcoll();
      }
      this.emitChange();
    },
    onFocus() {
      if (this.configuration.type == "input") {
        if (this.hasTextDesensitization || !this.eyesOpen) {
          this.showValue = this.formData[this.fieldCode];
        }
      }
    },
    onClear() {
      this.setValue("");
    },
    // 显示值
    displayValue(value = this.formData[this.fieldCode]) {
      if (this.widget.configuration.type == "input" && !this.eyesOpen) {
        value = this.textDesensitizationHandle(value || "");
      }
      if (value && this.widget.configuration.type == "input-password") {
        var _value = "";
        for (var i = 0; i < value.length; i++) {
          _value += "*";
        }
        value = _value;
      }
      if (this.widget.configuration.type == "textarea") {
        value = this.html2EscapeHandle(value);
      }
      if (this.widget.configuration.type == "input-number") {
        value = this.numberFormatter(value);
      }
      return value;
    },
    displayTitleValue() {
      var value = this.formData[this.fieldCode];
      if (this.widget.configuration.type == "input" && !this.eyesOpen) {
        value = this.textDesensitizationHandle(this.formData[this.fieldCode] || "");
      }
      if (value && this.widget.configuration.type == "input-password") {
        value = "";
      }
      if (this.widget.configuration.type == "textarea") {
        value = value && value.replace(/<[^>]*>/g, "");
      }
      if (this.widget.configuration.type == "input-number") {
        value = this.numberFormatter(value);
      }
      return value;
    },
    // HTML转义
    html2EscapeHandle(value) {
      if (this.widget.configuration.htmlCodec) {
        return typeof value === "string"
          ? value.replace(/[<>&"']/g, (c) => {
              return this.arrEntities[c];
            })
          : value;
      }
      return value;
    },
    //数字格式化，千位分隔符
    numberFormatter(value) {
      var val = value ? value + "" : "";
      if (val && this.widget.configuration.formatNumber) {
        val = val.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
      }
      return val;
    },
    afterDisplayStateChanged() {
      if (this.hasTextDesensitization) {
        this.formatValue = this.getFormatValue();
      }
    },
    eyeClickHandle() {
      this.eyesOpen = !this.eyesOpen;
      this.showValue = this.getFormatValue();
    },
    getFormatValue() {
      return this.textDesensitizationHandle(this.formData[this.fieldCode] || "");
    },
    //脱敏处理
    textDesensitizationHandle(value) {
      var formatValue = "";
      //只有单行文本框有脱敏处理
      if (!this.eyesOpen || (this.hasTextDesensitization && !this.eyesOpen)) {
        //根据文本类型进行脱敏处理
        switch (this.widget.configuration.contentFormat) {
          case "email": //EMAIL
            formatValue = this.emailFormat(value);
            break;
          case "identity": //身份证
            formatValue = this.identityFormat(value);
            break;
          case "cellphoneNumber": //手机
            formatValue = this.mobilePhoneFormat(value);
            break;
          case "telephoneNumber": //电话
            formatValue = this.telephoneNumberFormat(value);
            break;
          default:
            break;
        }
      }
      return formatValue || value;
    },
    //email: @ 符号的前3位脱敏
    emailFormat(val) {
      var _val = "";
      if (val && val.indexOf("@") > -1) {
        var valList = val.split("@");
        if (valList[0].length <= 3) {
          _val = valList[0].substring(0, 1) + "***";
        } else {
          _val = valList[0].slice(0, valList[0].length - 3) + "***";
        }
        _val += "@" + valList[1];
      }
      return _val || val;
    },
    //身份证号：保留前6后4
    identityFormat(val) {
      var _val = "";
      if (val && val.length > 10) {
        var length = val.length - 10;
        _val = val.slice(0, 6);
        for (var i = 0; i < length; i++) {
          _val += "*";
        }
        _val += val.slice(val.length - 4, val.length);
      }
      return _val || val;
    },
    //手机号，中间4位脱敏
    mobilePhoneFormat(val) {
      var _val = "";
      if (val && val.length > 7) {
        var length = val.length - 7;
        _val = val.slice(0, 3);
        for (var i = 0; i < length; i++) {
          _val += "*";
        }
        _val += val.slice(val.length - 4, val.length);
      }
      return _val;
    },
    //电话，后四位脱敏
    telephoneNumberFormat(val) {
      var _val = "";
      if (val) {
        _val = val.slice(0, val.length - 4) + "****";
      }
      return _val;
    },
    // 前置元素选择事件
    onChangeAddonBeforeSelect(val, opt) {
      if (this.widget.configuration.realDisplayAddonFront) {
        this.$set(this.formData, this.widget.configuration.realDisplayAddonFront, val);
        this.form.setFieldValue(this.widget.configuration.realDisplayAddonFront, val);
      }
      // this.emitChange();
    },
    // 后置元素选择事件
    onChangeAddonAfterSelect(val, opt) {
      if (this.widget.configuration.realDisplayAddonEnd) {
        this.$set(this.formData, this.widget.configuration.realDisplayAddonEnd, val);
        this.form.setFieldValue(this.widget.configuration.realDisplayAddonEnd, val);
      }
      // this.emitChange();
    },
    // 前置元素选择设值
    setAddonBeforeSelectVal() {
      if (this.widget.configuration.realDisplayAddonFront) {
        if (this.formData[this.widget.configuration.realDisplayAddonFront]) {
          // 前置元素未设值时
          // if (!this.addonBeforeValue) {
          this.addonBeforeValue = this.formData[this.widget.configuration.realDisplayAddonFront];
          // }
        } else {
          this.addonBeforeValue = this.addonBeforeOptions[0].value;
          // this.$set(this.formData, this.widget.configuration.realDisplayAddonFront, this.addonBeforeOptions[0]);
          this.onChangeAddonBeforeSelect(this.addonBeforeValue);
        }
      } else {
        this.addonBeforeValue = this.addonBeforeOptions[0].value;
      }
    },
    // 后置元素选择设值
    setAddonAfterSelectVal() {
      if (this.widget.configuration.realDisplayAddonEnd) {
        if (this.formData[this.widget.configuration.realDisplayAddonEnd]) {
          // 前置元素未设值时
          // if (!this.addonAfterValue) {
          this.addonAfterValue = this.formData[this.widget.configuration.realDisplayAddonEnd];
          // }
        } else {
          this.addonAfterValue = this.addonAfterOptions[0].value;
          this.onChangeAddonAfterSelect(this.addonAfterValue);
        }
      } else {
        this.addonAfterValue = this.addonAfterOptions[0].value;
      }
    },
    inputStyleSetting() {
      let inputStyle = [],
        allStyle = [];
      //设置input样式
      if (this.$el && this.$el.querySelector(".ant-input-suffix")) {
        var paddingRight = this.$el.querySelector(".ant-input-suffix").offsetWidth + 16;
        if (this.widget.configuration.clearBtnShow && !(this.disable || this.readonly)) {
          paddingRight += 24;
        }
        inputStyle.push("padding-right:" + paddingRight + "px");
      }
      if (this.$el && this.$el.querySelector(".ant-input")) {
        if (this.widget.configuration.style) {
          if (this.widget.configuration.style.textAlign) {
            inputStyle.push("text-align:" + this.widget.configuration.style.textAlign);
          }
        }
      }

      if (this.widget.configuration.style.height && this.widget.configuration.style.height != "auto") {
        const height = this.widget.configuration.style.height;
        allStyle.push("--w-input-height:" + height);
      }
      if (this.$el && this.$el.querySelector(".ant-input")) {
        if (this.$el.querySelector(".ant-input-group-wrapper")) {
          this.$el.querySelector(".ant-input-group-wrapper").setAttribute("style", allStyle.join(";"));
          this.$el.querySelector(".ant-input").setAttribute("style", inputStyle.join(";"));
        } else if (this.$el.querySelector(".ant-input-affix-wrapper")) {
          this.$el.querySelector(".ant-input-affix-wrapper").setAttribute("style", allStyle.join(";"));
          this.$el.querySelector(".ant-input").setAttribute("style", inputStyle.join(";"));
        } else {
          this.$el.querySelector(".ant-input").setAttribute("style", [...inputStyle, ...allStyle].join(";"));
        }
      }
    },
    // 设置多行文本的滚动
    textareaSrcoll() {
      let $textarea = this.$el.getElementsByTagName("textarea");
      if ($textarea && $textarea.length) {
        setTimeout(() => {
          if ($textarea[0].scrollHeight > $textarea[0].clientHeight) {
            if (!$textarea[0].classList.contains("ps__child--consume")) {
              $textarea[0].classList.add("ps__child--consume");
            }
          } else {
            if ($textarea[0].classList.contains("ps__child--consume")) {
              $textarea[0].classList.remove("ps__child--consume");
            }
          }
        });
      }
    },
    iconClick(type) {
      console.log(type);
    },
    addonEndIconEvent() {},
    addonFrontIconEvent() {},
  },
};
</script>
