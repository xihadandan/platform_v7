<template>
<!-- 高级视图-列表 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-upload-advanced-list-color"
              v-model="item.wUploadAdvancedListColor"
            />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-upload-advanced-list-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wUploadAdvancedListStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-upload-advanced-list-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wUploadAdvancedListWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-upload-advanced-list-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wUploadAdvancedListRadius"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="背景">
            <StyleColorTreeSelectFormItem
              label="背景颜色"
              codeLabel="--w-upload-advanced-list-bg"
              v-model="item.wUploadAdvancedListBg"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="边距">
            <StyleVarSelectFormItem
              label="内边距"
              codeLabel="--w-upload-advanced-list-margin"
              :options="getStyleDeriveOptions(packJson.spaceConfig.marginConfig)"
              v-model="item.wUploadAdvancedListMargin"
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
  name: 'ThemeUploadAdvancedList',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
