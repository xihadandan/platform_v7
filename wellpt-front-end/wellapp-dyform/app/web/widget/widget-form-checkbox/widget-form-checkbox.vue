<template>
  <a-form-model-item
    :style="itemStyle"
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="['form-item-checkbox', widgetClass]"
  >
    <div class="loading-skeleton" v-if="loading">
      <div class="loading" v-for="(item, index) in 3">
        <div class="cube-skeleton-loading"></div>
        <div class="text-skeleton-loading"></div>
      </div>
    </div>
    <template v-else>
      <a-checkbox-group v-model="value" :disabled="disable || readonly" v-show="!displayAsLabel" @change="onChange" :class="groupClass">
        <a-checkbox
          v-for="(opt, i) in checkboxOptions"
          :key="'checkbox-' + widget.id + i"
          :style="checkboxStyle"
          :disabled="opt.disabled"
          :value="opt.value"
        >
          {{ opt.label }}
        </a-checkbox>
      </a-checkbox-group>
      <div v-if="!displayAsLabel && this.widget.configuration.checkboxAll" class="checkAll-container">
        <a-checkbox :disabled="disable || readonly" :indeterminate="indeterminate" :checked="checkAll" @change="onCheckAllChange">
          全选
        </a-checkbox>
      </div>
      <span v-show="displayAsLabel" class="textonly" :title="checkedLabel">{{ checkedLabel }}</span>
    </template>
  </a-form-model-item>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formCommonMixin from '../mixin/form-common.mixin';
import { findIndex, each, debounce } from 'lodash';
import { deepClone } from '@framework/vue/utils/util';
import './css/index.less';
export default {
  extends: FormElement,
  name: 'WidgetFormCheckbox',
  inject: ['designMode'],
  mixins: [widgetMixin, formCommonMixin],
  props: {
    defaultCheckOptions: Array
  },
  data() {
    let checkboxOptions = this.defaultCheckOptions || [];
    return {
      initOptions: checkboxOptions,
      checkboxOptions,
      checkedLabels: [],
      value: null,
      indeterminate: false,
      checkAll: false,
      loading: false
    };
  },
  computed: {
    checkedLabel() {
      return this.checkedLabels.join(this.widget.configuration.tokenSeparators);
    },
    groupClass() {
      if (this.widget.configuration.layout == 'vertical') {
        return 'ant-checkbox-group-vertical';
      }
      return '';
    },
    checkboxStyle() {
      let style = {
        display: this.widget.configuration.layout == 'vertical' ? 'block' : 'inline-block',
        width:
          this.widget.configuration.layout == 'horizontal' && this.widget.configuration.alignType == 'fixedWidth'
            ? this.widget.configuration.itemWidth + 'px'
            : 'auto'
      };
      if (style.display == 'inline-block' && style.width != 'auto') {
        style.margin = '0px';
      }
      return style;
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
              options: this.checkboxOptions
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
    checkValueOptionMap() {
      let map = {};
      for (let i = 0, len = this.checkboxOptions.length; i < len; i++) {
        map[this.checkboxOptions[i].value] = this.checkboxOptions[i];
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
      this.addSelectedNumRules({
        min: this.widget.configuration.minCount,
        max: this.widget.configuration.maxCount
      });
      this.setValue(this.formData[this.fieldCode]); // 设初始值
      // 不是联动获取备选项
      if (!this.optionDataAutoSet || this.designMode || this.formData[this.fieldCode]) {
        this.fetchCheckboxOptions();
      }
    },

    //重新加载备选项
    refetchOption() {
      // 不是联动获取备选项
      if (!this.optionDataAutoSet || this.formData[this.fieldCode]) {
        this.fetchCheckboxOptions();
      }
    },

    // 通过事件配置的选项值，筛选常量和字典备选项
    getSelectOptionByValue(values) {
      this.checkboxOptions.splice(0, this.checkboxOptions.length);
      each(this.initOptions, opt => {
        if (values.indexOf(opt.value) > -1) {
          this.checkboxOptions.push(opt);
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
        this.fetchCheckboxOptionByDataDic(this.relateValue);
      }
    },
    fetchCheckboxOptions() {
      // 常量
      this.loading = true;
      if (this.widget.configuration.options.type == 'selfDefine' && this.widget.configuration.options.defineOptions.length) {
        this.loading = false;
        this.checkboxOptions = this.widget.configuration.options.defineOptions;
        for (let o of this.checkboxOptions) {
          o.label = this.$t(o.id, o.label);
        }
        this.initOptions = deepClone(this.checkboxOptions);
        this.setCheckedLabels();
        if (this.formData[this.fieldCode] != undefined) {
          this.emitChange({}, false);
        }
      } else if (this.widget.configuration.options.type == 'dataSource') {
        this.fetchCheckboxOptionByDataSource(this.widget.configuration.options);
      } else if (this.widget.configuration.options.type == 'dataDictionary') {
        this.fetchCheckboxOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
      } else if (this.widget.configuration.options.type == 'dataModel') {
        this.fetchCheckboxOptionByDataModel(this.widget.configuration.options);
      } else if (this.widget.configuration.options.type == 'apiLinkService') {
        this.fetchCheckboxOptionByApiLink(this.widget.configuration.options);
      }
    },
    fetchCheckboxOptionByApiLink(options) {
      let _this = this;
      _this.checkboxOptions.splice(0, _this.checkboxOptions.length);
      this.fetchDataByApiLinkInvocation(options.apiInvocationConfig).then(result => {
        _this.loading = false;
        if (Array.isArray(result)) {
          _this.checkboxOptions = result;
          _this.initOptions = deepClone(_this.checkboxOptions);
          _this.setCheckedLabels();
          _this.emitChange({}, false);
        }
      });
    },
    fetchCheckboxOptionByDataModel(options) {
      let _this = this;
      this.getLabelValueOptionByDataModel(
        {
          ...options,
          loading: l => {
            this.loading = l != undefined ? l === true : !this.loading;
          }
        },
        function (result) {
          _this.checkboxOptions = result;
          _this.initOptions = deepClone(_this.checkboxOptions);
          _this.setCheckedLabels();
          _this.emitChange({}, false);
          _this.loading = false;
        }
      );
    },

    fetchCheckboxOptionByDataSource(options) {
      let _this = this;
      this.getLabelValueOptionByDataSource(
        {
          ...options,
          loading: l => {
            this.loading = l != undefined ? l === true : !this.loading;
          }
        },
        function (result) {
          _this.checkboxOptions = result;
          _this.initOptions = deepClone(_this.checkboxOptions);
          _this.setCheckedLabels();
          _this.emitChange({}, false);
          _this.loading = false;
        }
      );
    },
    fetchCheckboxOptionByDataDic(dataDicUuid) {
      let _this = this;
      this.getLabelValueOptionByDataDic(dataDicUuid, function (result) {
        _this.loading = false;
        _this.checkboxOptions = result;
        _this.initOptions = deepClone(_this.checkboxOptions);
        _this.setCheckedLabels();
        _this.emitChange({}, false);
      });
    },

    onChange(values) {
      this.setCheckedLabels(values);
      this.formData[this.fieldCode] = values ? values.join(this.widget.configuration.tokenSeparators) : null;
      this.indeterminate = !!values.length && values.length < this.checkboxOptions.length;
      this.checkAll = values.length === this.checkboxOptions.length;
      // this.setOptionsDisabled();
      this.emitChange();
    },
    setRelaFieldValue(value) {
      if (this.widget.configuration.displayValueField) {
        // 设置关联显示值字段
        this.form.setFieldValue(this.widget.configuration.displayValueField, value, false);
      }
    },
    setCheckedLabels(values) {
      this.checkedLabels.splice(0, this.checkedLabels.length);
      if (values == undefined) {
        values = this.value;
      }
      if (values == undefined) {
        return [];
      }
      for (let i = 0, len = this.checkboxOptions.length; i < len; i++) {
        if (values.indexOf(this.checkboxOptions[i].value) != -1) {
          this.checkedLabels.push(this.checkboxOptions[i].label);
        }
      }
      this.setRelaFieldValue(this.checkedLabels.join(this.widget.configuration.tokenSeparators));
      return this.checkedLabels;
    },
    displayValue(value) {
      // 提供其他组件调用获取显示值的方法
      if (value != undefined) {
        let labels = [];
        let values = value;
        if (typeof value == 'string') {
          values = value.split(this.widget.configuration.tokenSeparators);
        }
        for (let i = 0, len = this.checkboxOptions.length; i < len; i++) {
          if (values.indexOf(this.checkboxOptions[i].value) != -1) {
            labels.push(this.checkboxOptions[i].label);
          }
        }
        return labels.join(this.widget.configuration.tokenSeparators);
      }

      return this.checkedLabel;
    },

    setValue(v) {
      this.value = v;
      if (typeof v === 'string') {
        this.value = v.split(this.widget.configuration.tokenSeparators);
      }
      if (v == undefined && !this.designMode) {
        this.value = this.widget.configuration.defaultValue
          ? this.widget.configuration.defaultValue.split(this.widget.configuration.tokenSeparators)
          : [];
      }
      this.formData[this.fieldCode] = Array.isArray(v) ? v.join(this.widget.configuration.tokenSeparators) : v;
      this.setCheckedLabels();
      this.clearValidate();
    },
    // 全选
    onCheckAllChange(e) {
      this.checkAll = e.target.checked;
      this.formData[this.fieldCode] = '';
      if (this.checkAll) {
        var checkedList = [];
        for (let i = 0, len = this.checkboxOptions.length; i < len; i++) {
          checkedList.push(this.checkboxOptions[i].value);
        }
        this.onChange(checkedList);
        this.setValue(checkedList.join(';'));
      } else {
        this.onChange([]);
        this.setValue('');
      }
      this.indeterminate = false;
      this.$refs[this.fieldCode].onFieldChange(); // 触发校验
    },
    // 设置选项不可选
    setOptionsDisabled() {
      // 最多可选个数
      let max = this.widget.configuration.checkboxMax;
      if (this.widget.configuration.checkboxMax) {
        let size = this.value.length;
        if (size < max) {
          // 个数小于最大值时，全部选项都可操作
          for (let i = 0, len = this.checkboxOptions.length; i < len; i++) {
            this.checkboxOptions[i].disabled = false;
          }
        } else {
          // 个数大于最大值时，全部选项都可操作
          for (let i = 0, len = this.checkboxOptions.length; i < len; i++) {
            if (this.value.indexOf(this.checkboxOptions[i].value) != -1) {
              this.checkboxOptions[i].disabled = false;
            } else {
              this.checkboxOptions[i].disabled = true;
            }
          }
        }
      }
    },
    // 选项变化后，当前值重置,避免选项被隐藏，当前值还在的问题
    optionsChangeAfter(checkboxOptions) {
      this.checkboxOptions = checkboxOptions;
      let checkedList = this.value || [];
      for (let i = 0, len = checkedList.length; i < len; i++) {
        var index = findIndex(this.checkboxOptions, { value: checkedList[i] });
        if (index == -1) {
          checkedList.splice(index, 1);
        }
      }
      this.onChange(checkedList);
      this.setValue(checkedList.join(';'));
    },
    onFilter({ searchValue, comparator, source, ignoreCase }) {
      return new Promise((resolve, reject) => {
        if (source != undefined) {
          // 由外部提供数据源进行判断
          let sources = source.split(';');
          let searchValues = searchValue.split(';');
          for (let i = 0, len = searchValues.length; i < len; i++) {
            if (comparator == 'like') {
              // 模糊匹配
              for (let j = 0, jlen = sources.length; j < jlen; j++) {
                let s = sources[j];
                if (this.checkValueOptionMap[s] != undefined) {
                  let label = this.checkValueOptionMap[s].label;
                  if (
                    ignoreCase ? label.toLowerCase().indexOf(searchValues[i].toLowerCase()) != -1 : label.indexOf(searchValues[i]) != -1
                  ) {
                    resolve(true);
                    return;
                  }
                }
              }
            } else {
              for (let j = 0, jlen = sources.length; j < jlen; j++) {
                if (sources.includes(searchValues[i])) {
                  resolve(true);
                  return;
                }
              }
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
