<template>
<!-- 错误提示 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-radio-error-border-color"
              v-model="item.wRadioErrorBorderColor"
            />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-radio-error-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wRadioErrorBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框尺寸"
              codeLabel="--w-radio-error-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wRadioErrorBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="提示文字颜色"
              codeLabel="--w-radio-error-word-color"
              v-model="item.wRadioErrorWordColor"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-radio-error-word-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wRadioErrorWordFontSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-radio-error-word-font-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wRadioErrorWordFontWeight"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
  </a-tabs>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';

export default {
  name: 'ThemeRadioErrorProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
