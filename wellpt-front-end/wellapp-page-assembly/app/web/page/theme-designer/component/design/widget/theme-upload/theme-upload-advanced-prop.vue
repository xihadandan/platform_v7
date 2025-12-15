<template>
<!-- 高级视图-框体样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-upload-advanced-color"
              v-model="item.wUploadAdvancedColor"
            />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-upload-advanced-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wUploadAdvancedStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-upload-advanced-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wUploadAdvancedWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-upload-advanced-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wUploadAdvancedRadius"
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
  name: 'ThemeUploadAdvancedProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
