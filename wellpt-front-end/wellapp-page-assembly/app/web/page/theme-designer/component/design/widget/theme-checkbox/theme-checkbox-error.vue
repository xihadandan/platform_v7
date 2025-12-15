<template>
<!-- 错误提示 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem
              label="提示文字颜色"
              codeLabel="--w-checkbox-error-text-color"
              v-model="item.wCheckboxErrorTextColor"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-checkbox-error-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wCheckboxErrorTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-checkbox-error-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wCheckboxErrorTextWeight"
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
  name: 'ThemeCheckboxError',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
