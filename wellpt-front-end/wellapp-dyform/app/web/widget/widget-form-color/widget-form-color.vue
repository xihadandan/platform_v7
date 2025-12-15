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
    <template v-if="!displayAsLabel">
      <ColorPicker
        v-model="formData[widget.configuration.code]"
        @ok="setValue"
        :disabled="disable || readonly"
        :picker="picker"
        :palette="widget.configuration.defaultColors"
        :pickerChange="pickerChange"
        displayType="custom"
        @onPickerChange="onPickerChange"
      >
        <div
          :class="['color-widget-container flex', 'color-widget-container-' + this.widget.configuration.size]"
          :style="{ width: containerWidth }"
        >
          <div
            :class="['color-widget-block', 'color-widget-block-' + this.widget.configuration.size]"
            :style="{ background: designMode ? this.widget.configuration.defaultValue : formData[widget.configuration.code] }"
          ></div>
          <div v-if="widget.configuration.showText && formData[widget.configuration.code]" class="color-widget-text">
            {{ formData[widget.configuration.code] }}
          </div>
        </div>
      </ColorPicker>
    </template>

    <span v-if="displayAsLabel" class="textonly" :title="displayValue()">
      {{ displayValue() }}
    </span>
  </a-form-model-item>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formMixin from '../mixin/form-common.mixin';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
export default {
  extends: FormElement,
  name: 'WidgetFormColor',
  mixins: [widgetMixin, formMixin],
  components: {
    ColorPicker
  },
  data() {
    let containerWidth = this.widget.configuration.showText ? '' : 'fit-content';
    let pickerChange = this.widget.configuration.picker == 'Twitter' && this.widget.configuration.pickerChange;
    return {
      picker: this.widget.configuration.picker,
      containerWidth,
      pickerChange
    };
  },
  computed: {},
  mounted() {},
  methods: {
    setValue(val) {
      this.$set(this.formData, this.widget.configuration.code, val);
      this.emitChange();
    },
    displayValue() {
      return this.formData[this.widget.configuration.code];
    },
    onPickerChange() {
      if (this.picker == 'Sketch') {
        this.picker = this.widget.configuration.picker == 'Sketch' ? 'Twitter' : this.widget.configuration.picker;
      } else {
        this.picker = 'Sketch';
      }
    }
  }
};
</script>
<style lang="less">
.color-widget-container {
  border: 1px solid var(--w-border-color-base);
  border-radius: var(--w-border-radius-2);
  font-size: var(--w-font-size-base);
  line-height: 32px;
  height: 32px;
  &&-lg {
    font-size: var(--w-font-size-lg);
    line-height: 40px;
    height: 40px;
  }
  &&-sm {
    line-height: 24px;
    height: 24px;
  }

  .color-widget-text {
    margin-right: var(--w-margin-2xs);
  }
}
// 色块样式
.color-widget-block {
  border-radius: var(--w-border-radius-2);
  display: inline-block;
  cursor: pointer;
  width: 22px;
  height: 22px;
  margin: var(--w-margin-3xs);

  &&-sm {
    width: 14px;
    height: 14px;
  }
  &&-lg {
    width: 30px;
    height: 30px;
  }
}
</style>
