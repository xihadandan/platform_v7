<template>
<!-- 选项面板 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-date-panel-border-color"
              v-model="item.wDatePanelBorderColor"
            />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-date-panel-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wDatePanelBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-date-panel-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wDatePanelBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-date-panel-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wDatePanelBorderRadius"
            />
           </a-collapse-panel>
          <a-collapse-panel key="3" header="背景">
            <StyleColorTreeSelectFormItem
              label="背景颜色"
              codeLabel="--w-date-picker-panel-bg"
              v-model="item.wDatePickerPanelBg"
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
  name: 'ThemeDatePickerPanel',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
