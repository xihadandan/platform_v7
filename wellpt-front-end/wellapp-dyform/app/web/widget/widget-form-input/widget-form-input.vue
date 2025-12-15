<template>
  <a-form-model-item :prop="formModelItemProp" :rules="rules" :ref="widget.configuration.code"
    :wrapper-col="{ style: wrapperColStyle }" :style="itemStyle" :colon="displayAsLabel" :label="itemLabel"
    :class="widgetClass">
    <!-- <span>{{ widget.configuration.type }}</span> -->
    <template v-if="!displayAsLabel">
      <span v-if="widget.configuration.type == 'textarea'"
        :class="['input-textarea', widget.configuration.allowResize ? '' : 'resizeOver']">
        <!-- 文本域 -->
        <a-textarea :disabled="disable" :readOnly="readonly" v-model="formData[widget.configuration.code]"
          :autoSize="!widget.configuration.allowResize" :allowClear="widget.configuration.clearBtnShow"
          :placeholder="$t('placeholder', widget.configuration.placeholder)" :maxLength="maxLengthCompute"
          @focus="onFocus" @blur="onBlur" @change="onChange"></a-textarea>
        <!-- 显示字符数 -->
        <template
          v-if="widget.configuration.length && widget.configuration.wordCountPrompt && !widget.configuration.dbDataType">
          <span class="textLengthShow">{{ formData[widget.configuration.code] | textLengthFilter }}/{{
            widget.configuration.length }}</span>
        </template>
      </span>
      <span v-else-if="widget.configuration.type == 'input-password'" class="input-password">
        <!-- 密码输入框 -->
        <a-input-password v-model="formData[widget.configuration.code]" :disabled="disable" :readOnly="readonly"
          :allowClear="widget.configuration.clearBtnShow"
          :placeholder="$t('placeholder', widget.configuration.placeholder)" :maxLength="maxLengthCompute"
          @focus="onFocus" @blur="onBlur" @change="onChange"></a-input-password>
        <!-- 显示字符数 -->
        <template v-if="widget.configuration.length && widget.configuration.wordCountPrompt">
          <span class="textLengthShow">{{ formData[widget.configuration.code] | textLengthFilter }}/{{
            widget.configuration.length }}</span>
        </template>
      </span>
      <!-- <template v-else-if="widget.configuration.type == 'input-number'">
        <widget-form-input-number :widget="widget"></widget-form-input-number>
      </template> -->
      <span v-else
        :class="[showPasswordEye ? 'showPasswordEye' : '', widget.configuration.addonAfterSlot ? 'hasAddonAfterOutside' : '']">
        <a-input v-if="disable || readonly" v-model="formatValue" :disabled="disable" :readOnly="readonly"
          :placeholder="$t('placeholder', widget.configuration.placeholder)" :maxLength="maxLengthCompute"
          @focus="onFocus" @blur="onBlur" @change="onChange">
          <!-- 显示脱敏按钮（满足：需要脱敏、有脱敏按钮、不可编辑和只读） -->
          <template slot="suffix" v-if="showPasswordEye">
            <a-icon class="ant-input-password-icon" :type="eyesOpen ? 'eye' : 'eye-invisible'" style=""
              @click="eyeClickHandle" />
          </template>
          <!-- 显示字符数 -->
          <template slot="suffix" v-if="widget.configuration.length && widget.configuration.wordCountPrompt">
            <span class="textLengthShow">
              {{ formData[widget.configuration.code] | textLengthFilter }}/{{ widget.configuration.length }}
            </span>
          </template>
          <!-- 显示额外元素 -->
          <template v-if="hasAddonSetting">
            <template slot="suffix" v-if="hasSuffix">
              <Icon v-if="widget.configuration.addonEndIconValue" :type="widget.configuration.addonEndIconValue" style=""
                @click.stop="addonEndIconEvent" />
            </template>
            <template slot="prefix" v-if="hasPrefix">
              <Icon v-if="widget.configuration.addonFrontIconValue" :type="widget.configuration.addonFrontIconValue"
                style="" @click.stop="addonFrontIconEvent" />
            </template>
            <template slot="addonBefore" v-if="hasAddonBefore">
              <span>{{ addonBeforeValue }}</span>
            </template>
            <template slot="addonAfter" v-if="hasAddonAfter">
              <span>{{ addonAfterValue }}</span>
            </template>
          </template>
        </a-input>
        <a-input v-else v-model="showValue" :allowClear="widget.configuration.clearBtnShow"
          :placeholder="$t('placeholder', widget.configuration.placeholder)" :maxLength="maxLengthCompute"
          @focus="onFocus" @blur="onBlur" @change="onChange">
          <template slot="suffix" v-if="showPasswordEye">
            <a-icon class="ant-input-password-icon" :type="eyesOpen ? 'eye' : 'eye-invisible'" style=""
              @click="eyeClickHandle" />
          </template>
          <!-- 显示字符数 -->
          <template slot="suffix" v-if="widget.configuration.length && widget.configuration.wordCountPrompt">
            <span class="textLengthShow">
              {{ formData[widget.configuration.code] | textLengthFilter }}/{{ widget.configuration.length }}
            </span>
          </template>
          <!-- 显示额外元素 -->
          <template v-if="hasAddonSetting">
            <template slot="suffix" v-if="hasSuffix">
              <Icon :type="widget.configuration.addonEndIconValue" style="" @click.stop="addonEndIconEvent" />
            </template>
            <template slot="prefix" v-if="hasPrefix">
              <Icon :type="widget.configuration.addonFrontIconValue" style="" @click.stop="addonFrontIconValue" />
            </template>
            <template slot="addonBefore" v-if="hasAddonBefore">
              <template v-if="addonBeforeOptions.length > 1">
                <a-select v-model="addonBeforeValue" @change="onChangeAddonBeforeSelect" ref="addonBeforeSelectRef">
                  <a-select-option v-for="item in addonBeforeOptions" :key="item" :title="item">
                    {{ item }}
                  </a-select-option>
                </a-select>
              </template>
              <template v-else>{{ addonBeforeValue }}</template>
            </template>
            <template slot="addonAfter" v-if="hasAddonAfter">
              <template v-if="addonAfterOptions.length > 1">
                <a-select v-model="addonAfterValue" @change="onChangeAddonAfterSelect" ref="addonAfterSelectRef">
                  <a-select-option v-for="item in addonAfterOptions" :key="item" :title="item">
                    {{ item }}
                  </a-select-option>
                </a-select>
              </template>
              <template v-else>{{ addonAfterValue }}</template>
            </template>
          </template>
        </a-input>
        <div class="outside-addonAfter" v-if="widget.configuration.addonAfterSlot">
          <slot name="designAddonAfterSlot" v-if="designMode" />
          <template v-else>
            <template v-for="(wgt, windex) in widget.configuration.addonAfterSlotWidgets">
              <component :key="wgt.id" :is="wgt.wtype" :widget="wgt" :index="windex"
                :widgetsOfParent="widget.configuration.widgets" :parent="widget" @mounted="onAddonAfterSlotWidgetMounted">
              </component>
            </template>
          </template>
        </div>

      </span>
    </template>
    <span v-else :class="['textonly', widget.configuration.addonAfterSlot ? 'hasAddonAfterOutside' : '']"
      :title="displayTitleValue()">
      <template v-if="widget.configuration.type == 'input'">
        <span v-if="hasAddonSetting && hasAddonBefore">{{ addonBeforeValue }}</span>
        <span>{{ displayValue() }}</span>
        <span v-if="hasAddonSetting && hasAddonAfter">{{ addonAfterValue }}</span>
        <div class="outside-addonAfter" v-if="widget.configuration.addonAfterSlot">
          <template v-for="(wgt, windex) in widget.configuration.addonAfterSlotWidgets">
            <component :key="wgt.id" :is="wgt.wtype" :widget="wgt" :index="windex"
              :widgetsOfParent="widget.configuration.widgets" :parent="widget" @mounted="onAddonAfterSlotWidgetMounted">
            </component>
          </template>
        </div>
      </template>
      <template v-else-if="widget.configuration.type == 'textarea'">
        <span v-html="displayValue()"></span>
      </template>
      <span v-else>{{ displayValue() }}</span>
    </span>
  </a-form-model-item>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formCommonMixin from '../mixin/form-common.mixin';
import './css/index.less';
// import widgetFormInputNumber from './widget-form-input-number.vue';

export default {
  extends: FormElement,
  name: 'WidgetFormInput',
  mixins: [widgetMixin, formCommonMixin],
  data() {
    return {
      showValue: '',
      visible: true,
      eyesOpen: false,
      autoCompleteOptions: [],
      hasSuffixIcon: false,
      hasPrefixIcon: false,
      addonAfterValue: '',
      addonBeforeValue: '',
      addonAfterOptions: [], //额外元素,后置标签下拉框选项
      addonBeforeOptions: [], //额外元素,前置标签下拉框选项
      arrEntities: {
        //普通字符转换成转意符
        '<': '&lt;',
        '>': '&gt;',
        '&': '&amp;',
        '"': '&quot;',
        "'": '&apos;'
      }
    };
  },
  beforeCreate() { },
  components: {},
  computed: {
    formatValue: {
      get() {
        return this.getFormatValue();
      },
      set(val) { }
    },
    hasAddonSetting() {
      //文本类型(普通、email、电话)
      var iscontentFormat = ['text', 'email', 'telephoneNumber'].indexOf(this.widget.configuration.contentFormat) > -1;
      return (
        iscontentFormat &&
        this.widget.configuration.addonSetting &&
        this.widget.configuration.addonAfterSlot !== true &&
        this.widget.configuration.addonAfterSlotPosition !== 'inside'
      );
    },
    isPtIcon() {
      return this.widget.configuration.addonFrontIconValue && this.widget.configuration.addonFrontIconValue.indexOf('pticon') > -1;
    },
    hasSuffix() {
      this.hasSuffixIcon = false;
      //后置图标
      if (this.widget.configuration.addonValue) {
        var hasAddonEndIcon = this.widget.configuration.addonValue.indexOf('addonEndIcon');
        this.hasSuffixIcon = hasAddonEndIcon > -1 && this.widget.configuration.addonEndIconValue;
      }
      return this.hasSuffixIcon || this.widget.configuration.wordCountPrompt;
    },
    hasPrefix() {
      this.hasPrefixIcon = false;
      //前置图标
      if (this.widget.configuration.addonValue) {
        var hasAddonFrontIcon = this.widget.configuration.addonValue.indexOf('addonFrontIcon');
        this.hasPrefixIcon = hasAddonFrontIcon > -1 && this.widget.configuration.addonFrontIconValue;
      }
      return this.hasPrefixIcon;
    },
    hasAddonAfter() {
      // 后标签
      if (this.widget.configuration.addonValue) {
        var hasAddonEnd = this.widget.configuration.addonValue.indexOf('addonEnd');
        this.addonAfterOptions = this.widget.configuration.addonEndValue || [];
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
        var hasAddonFront = this.widget.configuration.addonValue.indexOf('addonFront');
        this.addonBeforeOptions = this.widget.configuration.addonFrontValue || [];
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
      var iscontentFormat = ['text', 'postCode'].indexOf(this.widget.configuration.contentFormat) == -1;
      return this.widget.configuration.type == 'input' && iscontentFormat && this.configuration.showPasswordEye;
    },
    //脱敏
    hasTextDesensitization() {
      //文本类型(手机、email、电话、身份证)，普通和邮编不需要
      var iscontentFormat = ['text', 'postCode'].indexOf(this.widget.configuration.contentFormat) == -1;
      return this.widget.configuration.type == 'input' && iscontentFormat && this.widget.configuration.istextDesensitization;
    },
    //字段长度
    maxLengthCompute() {
      var maxLength = this.widget.configuration.length;
      var length = maxLength;
      // 多行文本框，大字段时不限制输入字数
      if (this.widget.configuration.type == 'textarea' && this.widget.configuration.dbDataType) {
        length = null;
      }
      return length || null;
    },
    wrapperColStyle() {
      var style = {};
      if (this.widget.configuration.style) {
        if (this.widget.configuration.style.textAlign) {
          style.textAlign = this.widget.configuration.style.textAlign;
        }
      }
      return style;
    }
  },
  beforeMount() {
    let _this = this;
  },
  created() { },
  mounted() {
    this.eyesOpen = !this.configuration.istextDesensitization;
    if (this.eyesOpen) {
      this.showValue = this.formData[this.fieldCode];
    } else if (this.formData[this.fieldCode]) {
      this.setValue(this.formData[this.fieldCode]);
    }
    this.$nextTick(() => {
      this.inputStyleSetting();
    });
  },
  updated() { },
  filters: {
    //字符长度
    textLengthFilter(text) {
      return text ? text.length : 0;
    }
  },
  methods: {
    setValue(val) {
      this.formData[this.fieldCode] = val;
      if (this.configuration.type == 'input') {
        if (this.disable || this.readonly) {
          this.formData[this.fieldCode] = val;
        } else {
          this.showValue = val;
          if (this.hasTextDesensitization || !this.eyesOpen) {
            this.showValue = this.getFormatValue();
          }
        }
      }
      if (this.configuration.type == 'textarea') {
        this.textareaSrcoll();
      }
      this.clearValidate();
      this.emitChange();
    },
    //输入框动态脱敏
    desenInputText(e) {
      const ind = e.target.selectionStart - 1;
      let value = this.formData[this.fieldCode] || '';
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
          range.moveStart('character', ind + 1);
          range.moveEnd('character', ind + 1);
          range.select();
        }
      });
    },
    onBlur(e) {
      if (this.configuration.type == 'input') {
        if (this.hasTextDesensitization || !this.eyesOpen) {
          this.setValue(e.target._value);
        }
      }
      this.emitBlur();
    },
    onChange(e) {
      if (this.configuration.type == 'input') {
        if (this.hasTextDesensitization || !this.eyesOpen) {
          // this.desenInputText(e);
        } else {
          this.formData[this.fieldCode] = this.showValue;
        }
      }
      if (this.configuration.type == 'textarea') {
        this.textareaSrcoll();
      }
      this.emitChange();
    },
    onFocus() {
      if (this.configuration.type == 'input') {
        if (this.hasTextDesensitization || !this.eyesOpen) {
          this.showValue = this.formData[this.fieldCode];
        }
      }
    },
    // 显示值
    displayValue() {
      var value = this.formData[this.fieldCode];
      if (this.widget.configuration.type == 'input' && !this.eyesOpen) {
        value = this.textDesensitizationHandle(this.formData[this.fieldCode] || '');
      }
      if (value && this.widget.configuration.type == 'input-password') {
        var _value = '';
        for (var i = 0; i < value.length; i++) {
          _value += '*';
        }
        value = _value;
      }
      if (this.widget.configuration.type == 'textarea') {
        value = this.html2EscapeHandle(value);
      }
      if (this.widget.configuration.type == 'input-number') {
        value = this.numberFormatter(value);
      }
      return value;
    },
    displayTitleValue() {
      var value = this.formData[this.fieldCode];
      if (this.widget.configuration.type == 'input' && !this.eyesOpen) {
        value = this.textDesensitizationHandle(this.formData[this.fieldCode] || '');
      }
      if (value && this.widget.configuration.type == 'input-password') {
        value = '';
      }
      if (this.widget.configuration.type == 'textarea') {
        value = value && value.replace(/<[^>]*>/g, '');
      }
      if (this.widget.configuration.type == 'input-number') {
        value = this.numberFormatter(value);
      }
      return value;
    },
    // HTML转义
    html2EscapeHandle(value) {
      if (this.widget.configuration.htmlCodec) {
        return typeof value === 'string'
          ? value.replace(/[<>&"']/g, c => {
            return this.arrEntities[c];
          })
          : value;
      }
      return value;
    },
    //数字格式化，千位分隔符
    numberFormatter(value) {
      var val = value ? value + '' : '';
      if (val && this.widget.configuration.formatNumber) {
        val = val.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
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
      return this.textDesensitizationHandle(this.formData[this.fieldCode] || '');
    },
    //脱敏处理
    textDesensitizationHandle(value) {
      var formatValue = '';
      //只有单行文本框有脱敏处理
      if (!this.eyesOpen || (this.hasTextDesensitization && !this.eyesOpen)) {
        //根据文本类型进行脱敏处理
        switch (this.widget.configuration.contentFormat) {
          case 'email': //EMAIL
            formatValue = this.emailFormat(value);
            break;
          case 'identity': //身份证
            formatValue = this.identityFormat(value);
            break;
          case 'cellphoneNumber': //手机
            formatValue = this.mobilePhoneFormat(value);
            break;
          case 'telephoneNumber': //电话
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
      var _val = '';
      if (val && val.indexOf('@') > -1) {
        var valList = val.split('@');
        if (valList[0].length <= 3) {
          _val = valList[0].substring(0, 1) + '***';
        } else {
          _val = valList[0].slice(0, valList[0].length - 3) + '***';
        }
        _val += '@' + valList[1];
      }
      return _val || val;
    },
    //身份证号：保留前6后4
    identityFormat(val) {
      var _val = '';
      if (val && val.length > 10) {
        var length = val.length - 10;
        _val = val.slice(0, 6);
        for (var i = 0; i < length; i++) {
          _val += '*';
        }
        _val += val.slice(val.length - 4, val.length);
      }
      return _val || val;
    },
    //手机号，中间4位脱敏
    mobilePhoneFormat(val) {
      var _val = '';
      if (val && val.length > 7) {
        var length = val.length - 7;
        _val = val.slice(0, 3);
        for (var i = 0; i < length; i++) {
          _val += '*';
        }
        _val += val.slice(val.length - 4, val.length);
      }
      return _val;
    },
    //电话，后四位脱敏
    telephoneNumberFormat(val) {
      var _val = '';
      if (val) {
        _val = val.slice(0, val.length - 4) + '****';
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
          this.addonBeforeValue = this.addonBeforeOptions[0];
          // this.$set(this.formData, this.widget.configuration.realDisplayAddonFront, this.addonBeforeOptions[0]);
          this.onChangeAddonBeforeSelect(this.addonBeforeValue);
        }
      } else {
        this.addonBeforeValue = this.addonBeforeOptions[0];
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
          this.addonAfterValue = this.addonAfterOptions[0];
          this.onChangeAddonAfterSelect(this.addonAfterValue);
        }
      } else {
        this.addonAfterValue = this.addonAfterOptions[0];
      }
    },
    inputStyleSetting() {
      let inputStyle = [],
        allStyle = [];
      //设置input样式
      if (this.$el && this.$el.querySelector('.ant-input-suffix')) {
        var paddingRight = this.$el.querySelector('.ant-input-suffix').offsetWidth + 16;
        if (this.widget.configuration.clearBtnShow && !(this.disable || this.readonly)) {
          paddingRight += 24;
        }
        inputStyle.push('padding-right:' + paddingRight + 'px');
      }
      if (this.$el && this.$el.querySelector('.ant-input')) {
        if (this.widget.configuration.style) {
          if (this.widget.configuration.style.textAlign) {
            inputStyle.push('text-align:' + this.widget.configuration.style.textAlign);
          }
        }
      }

      if (this.widget.configuration.style.height && this.widget.configuration.style.height != 'auto') {
        const height = this.widget.configuration.style.height;
        allStyle.push('--w-input-height:' + height);
      }
      if (this.$el && this.$el.querySelector('.ant-input')) {
        if (this.$el.querySelector('.ant-input-group-wrapper')) {
          this.$el.querySelector('.ant-input-group-wrapper').setAttribute('style', allStyle.join(';'));
          this.$el.querySelector('.ant-input').setAttribute('style', inputStyle.join(';'));
        } else if (this.$el.querySelector('.ant-input-affix-wrapper')) {
          this.$el.querySelector('.ant-input-affix-wrapper').setAttribute('style', allStyle.join(';'));
          this.$el.querySelector('.ant-input').setAttribute('style', inputStyle.join(';'));
        } else {
          this.$el.querySelector('.ant-input').setAttribute('style', [...inputStyle, ...allStyle].join(';'));
        }
      }
    },
    // 设置多行文本的滚动
    textareaSrcoll() {
      let $textarea = this.$el.getElementsByTagName('textarea');
      if ($textarea && $textarea.length) {
        setTimeout(() => {
          if ($textarea[0].scrollHeight > $textarea[0].clientHeight) {
            if (!$textarea[0].classList.contains('ps__child--consume')) {
              $textarea[0].classList.add('ps__child--consume');
            }
          } else {
            if ($textarea[0].classList.contains('ps__child--consume')) {
              $textarea[0].classList.remove('ps__child--consume');
            }
          }
        });
      }
    },
    addonEndIconEvent() { },
    addonFrontIconEvent() { }
  }
};
</script>

<style lang="less"></style>
