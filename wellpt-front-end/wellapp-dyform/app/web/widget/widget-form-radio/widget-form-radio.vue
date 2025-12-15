<template>
  <a-form-model-item
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :style="itemStyle"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="['form-item-radio', widgetClass]"
  >
    <div class="loading-skeleton" v-if="loading">
      <div class="loading" v-for="(item, index) in 3">
        <div class="circle-skeleton-loading"></div>
        <div class="text-skeleton-loading"></div>
      </div>
    </div>
    <template v-else>
      <a-radio-group
        v-model="formData[widget.configuration.code]"
        :disabled="disable"
        :readOnly="readonly"
        v-show="!displayAsLabel"
        :buttonStyle="widget.configuration.buttonSelectedStyle"
        :ref="'radio_' + widget.configuration.code"
        :class="groupClass"
        @blur="onBlur"
        @change="onChange"
      >
        <template v-if="widget.configuration.styleType == 'button'">
          <a-radio-button
            v-for="(opt, i) in radioOptions"
            :key="'radio-' + widget.id + i"
            :value="opt.value"
            :ref="'radio' + i"
            :disabled="readonly && opt.value != formData[widget.configuration.code]"
            @click.stop="$evt => onClick($evt, 'radio' + i, opt.value)"
          >
            {{ opt.label }}
          </a-radio-button>
        </template>
        <template v-else>
          <a-radio
            v-for="(opt, i) in radioOptions"
            :key="'radio-' + widget.id + i"
            :style="radioStyle"
            :value="opt.value"
            :ref="'radio' + i"
            :disabled="readonly && opt.value != formData[widget.configuration.code]"
            @click.stop="$evt => onClick($evt, 'radio' + i, opt.value)"
          >
            {{ opt.label }}
          </a-radio>
        </template>
      </a-radio-group>
      <span v-show="displayAsLabel" class="textonly" :title="checkedLabel">{{ checkedLabel }}</span>
    </template>
  </a-form-model-item>
</template>

<script type="text/babel">
import { findIndex, each, debounce } from 'lodash';
import { deepClone } from '@framework/vue/utils/util';
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formCommonMixin from '../mixin/form-common.mixin';
import './css/index.less';
export default {
  extends: FormElement,
  name: 'WidgetFormRadio',
  inject: ['designMode'],
  mixins: [widgetMixin, formCommonMixin],
  props: {
    defaultRadioOptions: Array
  },
  data() {
    let radioOptions = this.defaultRadioOptions || [];
    return { initOptions: radioOptions, radioOptions, checkedLabel: null, loading: false };
  },
  computed: {
    groupClass() {
      if (this.widget.configuration.layout == 'vertical' && this.widget.configuration.styleType !== 'button') {
        return 'radio-group-vertical';
      }
      return '';
    },
    radioStyle() {
      return {
        display: this.widget.configuration.layout == 'vertical' ? 'block' : 'inline-block',
        width:
          this.widget.configuration.layout == 'horizontal' && this.widget.configuration.alignType == 'fixedWidth'
            ? this.widget.configuration.itemWidth + 'px'
            : 'auto'
      };
    },
    defaultEvents() {
      let eventParams = [];
      if (this.designMode) {
        if (this.optionType === 'selfDefine' || this.optionType === 'dataDictionary') {
          let eventParamKeyOptions = {
            paramKey: 'options',
            remark: '选项',
            valueSource: {
              inputType: 'multi-select',
              options: this.radioOptions
            }
          };
          eventParams.push(eventParamKeyOptions);
        }
        if (this.optionType == 'dataSource' || this.optionType == 'dataModel') {
          eventParams.push({
            paramKey: 'removeCache',
            remark: '在从表里，该值为true会清除缓存；主表里默认清除缓存,该参数无效',
            valueSource: {
              inputType: 'select',
              options: [
                { label: '是', value: true },
                { label: '否', value: false }
              ]
            }
          });
        }
      }
      return [
        {
          id: 'refreshOptions',
          title: '重新加载备选项',
          eventParams
        }
      ];
    },
    radioValueOptionMap() {
      let map = {};
      for (let i = 0, len = this.radioOptions.length; i < len; i++) {
        map[this.radioOptions[i].value] = this.radioOptions[i];
      }
      return map;
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      if (!this.designMode) {
        if (this.optionDataAutoSet && this.relateKey) {
          this.listenRelateChanged(() => {
            this.checkRelateValue({
              relateValue: this.relateValue,
              configuration: this.widget.configuration,
              callback: this.optionsChangeAfter
            });
          });
        }
      }
      if (!this.optionDataAutoSet || this.designMode || this.formData[this.fieldCode]) {
        this.fetchRadioOptions();
      }
    },

    //重新加载备选项
    refetchOption() {
      // 不是联动获取备选项
      if (!this.optionDataAutoSet || this.formData[this.fieldCode]) {
        this.fetchRadioOptions();
      }
    },
    // 通过事件配置的选项值，筛选常量和字典备选项
    getSelectOptionByValue(values) {
      this.radioOptions.splice(0, this.radioOptions.length);
      each(this.initOptions, opt => {
        if (values.indexOf(opt.value) > -1) {
          this.radioOptions.push(opt);
        }
      });
    },
    // 判断联动方式
    checkRelateValue(arg) {
      if (this.optionType === 'selfDefine') {
        // 常量
        this.relateSelfDefine(arg);
      }
      if (this.optionType === 'dataDictionary') {
        // 数据字典
        this.fetchRadioOptionByDataDic(this.relateValue);
      }
    },
    fetchRadioOptions(callback) {
      // 常量
      this.loading = true;
      if (this.widget.configuration.options.type == 'selfDefine' && this.widget.configuration.options.defineOptions.length) {
        this.loading = false;
        this.fetchedOptions = true;
        this.radioOptions = this.widget.configuration.options.defineOptions;
        for (let o of this.radioOptions) {
          o.label = this.$t(o.id, o.label);
        }
        this.initOptions = deepClone(this.radioOptions);
        this.setCheckdLabel();
        this.emitChange({}, false);
        if (typeof callback === 'function') {
          callback();
        }
      } else if (this.widget.configuration.options.type == 'dataSource') {
        this.fetchRadioOptionByDataSource(this.widget.configuration.options, () => {
          this.fetchedOptions = true;
          if (typeof callback === 'function') {
            callback();
          }
        });
      } else if (this.widget.configuration.options.type == 'dataDictionary') {
        this.fetchRadioOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid, () => {
          this.fetchedOptions = true;
          if (typeof callback === 'function') {
            callback();
          }
        });
      } else if (this.widget.configuration.options.type == 'dataModel') {
        this.fetchRadioOptionByDataModel(this.widget.configuration.options, () => {
          this.fetchedOptions = true;
          if (typeof callback === 'function') {
            callback();
          }
        });
      } else if (this.widget.configuration.options.type == 'apiLinkService') {
        this.fetchRadioOptionByApiLink(this.widget.configuration.options, () => {
          this.fetchedOptions = true;
          if (typeof callback === 'function') {
            callback();
          }
        });
      }
    },
    fetchRadioOptionByApiLink(options, callback) {
      let _this = this;
      _this.radioOptions.splice(0, _this.radioOptions.length);
      this.fetchDataByApiLinkInvocation(options.apiInvocationConfig).then(result => {
        _this.loading = false;
        if (Array.isArray(result)) {
          _this.radioOptions = result;
          _this.initOptions = deepClone(_this.radioOptions);
          _this.setCheckdLabel();
          _this.emitChange({}, false);
          if (typeof callback === 'function') {
            callback();
          }
        }
      });
    },
    fetchRadioOptionByDataModel(options, callback) {
      this.getLabelValueOptionByDataModel(options, result => {
        this.loading = false;
        this.radioOptions = result;
        this.initOptions = deepClone(this.radioOptions);
        this.setCheckdLabel();
        this.emitChange({}, false);
        this.loading = false;
        if (typeof callback === 'function') {
          callback();
        }
      });
    },

    fetchRadioOptionByDataSource(options, callback) {
      this.getLabelValueOptionByDataSource(
        {
          ...options,
          loading: l => {
            this.loading = l != undefined ? l === true : !this.loading;
          }
        },
        result => {
          this.radioOptions = result;
          this.initOptions = deepClone(this.radioOptions);
          this.setCheckdLabel();
          this.emitChange({}, false);
          this.loading = false;
          if (typeof callback === 'function') {
            callback();
          }
        }
      );
    },
    fetchRadioOptionByDataDic(dataDicUuid, callback) {
      this.getLabelValueOptionByDataDic(dataDicUuid, result => {
        this.loading = false;
        this.radioOptions = result;
        this.initOptions = deepClone(this.radioOptions);
        this.setCheckdLabel();
        this.emitChange({}, false);
        if (typeof callback === 'function') {
          callback();
        }
      });
    },

    onChange(evt) {
      this.setCheckdLabel(evt.target.value);
      this.emitChange();
    },

    setRelaFieldValue(value) {
      if (this.widget.configuration.displayValueField) {
        this.form.setFieldValue(this.widget.configuration.displayValueField, value);
      }
    },

    onClick(evt, ref, val) {
      if (val == this.getValue() && this.widget.configuration.cancleChecked) {
        //取消选中
        this.setValue(null);
        this.$refs[this.fieldCode].onFieldChange(); // 触发校验
        this.emitChange();
      }
    },

    setCheckdLabel(value) {
      if (value == undefined) {
        value = this.form.formData[this.fieldCode];
        this.checkedLabel = null;
      }
      for (let i = 0, len = this.radioOptions.length; i < len; i++) {
        if (value === this.radioOptions[i].value) {
          this.checkedLabel = this.radioOptions[i].label;
          break;
        }
      }
      // 设置关联显示值字段
      this.setRelaFieldValue(this.checkedLabel);
      return this.checkedLabel;
    },
    setValue(value) {
      this.formData[this.fieldCode] = value;
      if (value && !this.fetchedOptions) {
        this.fetchRadioOptions(() => {
          this.formData[this.fieldCode] = value;
          this.setCheckdLabel();
          this.clearValidate();
        });
      } else {
        this.setCheckdLabel();
        this.clearValidate();
      }
    },
    displayValue(value) {
      // 提供其他组件调用获取显示值的方法
      if (value != undefined) {
        for (let i = 0, len = this.radioOptions.length; i < len; i++) {
          if (value === this.radioOptions[i].value) {
            return this.radioOptions[i].label;
          }
        }
      }
      return this.checkedLabel;
    },
    // 选项变化后，当前值重置,避免选项被隐藏，当前值还在的问题
    optionsChangeAfter(radioOptions) {
      this.radioOptions = radioOptions;
      let val = this.form.formData[this.fieldCode];
      if (val) {
        let index = findIndex(this.radioOptions, { value: val });
        if (index == -1) {
          this.setValue(null);
        }
      }
    },

    onFilter({ searchValue, comparator, source, ignoreCase }) {
      return new Promise((resolve, reject) => {
        if (source != undefined) {
          if (comparator == 'like') {
            // 模糊匹配
            if (this.radioValueOptionMap[source] != undefined) {
              let label = this.radioValueOptionMap[source].label;
              if (ignoreCase ? label.toLowerCase().indexOf(searchValue.toLowerCase()) != -1 : label.indexOf(searchValue) != -1) {
                resolve(true);
                return;
              }
            }
          } else {
            if (source == searchValue) {
              resolve(true);
              return;
            }
          }
          resolve(false);
        }
        //TODO: 判断本组件值是否匹配
        resolve(false);
      });
    }
  }
};
</script>
