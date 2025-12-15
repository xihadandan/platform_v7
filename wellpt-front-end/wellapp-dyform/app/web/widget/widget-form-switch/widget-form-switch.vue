<template>
  <a-form-model-item
    :style="itemStyle"
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="widgetClass"
  >
    <a-switch v-model="checked" @change="onChange" :disabled="disable || readonly" :readOnly="readonly" v-if="!displayAsLabel">
      <template v-if="switchStyle == 'label'">
        <label slot="checkedChildren">{{ checkedLabel }}</label>
        <label slot="unCheckedChildren">{{ uncheckedLabel }}</label>
      </template>
      <template v-else-if="switchStyle == 'icon'">
        <Icon slot="checkedChildren" :type="widget.configuration.checkedIcon" />
        <Icon slot="unCheckedChildren" :type="widget.configuration.uncheckedIcon" />
      </template>
    </a-switch>

    <span v-if="displayAsLabel" class="textonly" :title="displayValue()">
      {{ displayValue() }}
    </span>
  </a-form-model-item>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formMixin from '../mixin/form-common.mixin';
import './css/index.less';
export default {
  extends: FormElement,
  name: 'WidgetFormSwitch',
  mixins: [widgetMixin, formMixin],
  data() {
    return {
      checked: this.formData[this.widget.configuration.code] == this.widget.configuration.checkedValue,
      allowValues: [this.widget.configuration.checkedValue, this.widget.configuration.uncheckedValue]
    };
  },
  computed: {
    checkedLabel() {
      return this.$t('checkedLabel', this.widget.configuration.checkedLabel);
    },
    uncheckedLabel() {
      return this.$t('uncheckedLabel', this.widget.configuration.uncheckedLabel);
    },

    switchStyle() {
      // 设计器里面移动端显示效果
      if (this.designMode && this.designer && this.designer.terminalType == 'mobile') {
        return this.widget.configuration.uniConfiguration ? this.widget.configuration.uniConfiguration.switchStyle : '';
      }
      return this.widget.configuration.switchStyle;
    }
  },
  mounted() {
    this.emitChange();
  },
  methods: {
    resetField() {
      if (this.$refs[this.fieldCode]) {
        this.$refs[this.fieldCode].clearValidate();
        this.checked = false;
      }
    },
    onChange(checked) {
      this.formData[this.widget.configuration.code] = this.widget.configuration[checked ? 'checkedValue' : 'uncheckedValue'];
      this.emitChange();
    },
    setValue(val) {
      let i = -1;
      if (typeof val == 'boolean') {
        // 真/假值转换
        i = val ? 0 : 1;
      } else {
        if (typeof this.widget.configuration.checkedValue == 'number') {
          val = val ? Number(val) : null;
          i = this.allowValues.indexOf(val);
        } else {
          i = this.allowValues.indexOf(val);
        }
      }
      if (i != -1) {
        this.formData[this.widget.configuration.code] = this.allowValues[i];
        this.checked = i == 0;
      } else {
        console.error('开关设值的可选值为：', this.allowValues);
      }
      this.clearValidate();
    },

    isOpen() {},

    displayValue(v, template) {
      let checked = this.checked;
      if (v != undefined) {
        checked = this.widget.configuration.checkedValue == v;
      }
      if (this.configuration.uneditableDisplayState == 'label') {
        // 不可编辑模式 纯文本
        if (this.configuration.switchStyle != 'label') {
          return checked ? this.configuration.checkedValue : this.configuration.uncheckedValue;
        }
      }
      let label =
        this.widget.configuration.switchStyle == 'label' || (this.widget.configuration.switchStyle == 'icon' && template !== true);
      if (label) {
        return checked ? this.checkedLabel : this.uncheckedLabel;
      } else {
        return {
          label: checked ? this.checkedLabel : this.uncheckedLabel,
          template: checked
            ? `<a-icon type="${this.widget.configuration.checkedIcon}" />`
            : `<a-icon type="${this.widget.configuration.uncheckedIcon}" />`
        };
      }
    },

    onFilter({ searchValue, comparator, source, ignoreCase }) {
      if (source != undefined) {
        if (comparator == 'like') {
          if (this.widget.configuration.switchStyle == 'label') {
            let label = source == this.widget.configuration.checkedValue ? this.checkedLabel : this.uncheckedLabel;
            return ignoreCase ? label.toLowerCase().indexOf(searchValue.toLowerCase()) != -1 : label.indexOf(searchValue) != -1;
          } else {
            return false;
          }
        }
        return searchValue == source;
      }
      //TODO: 判断本组件值是否匹配
      return false;
    }
  }
};
</script>
