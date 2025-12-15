<template>
<!-- 错误提示 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-org-error-border-color"
              v-model="item.wOrgErrorBorderColor"
            />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-org-error-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wOrgErrorBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-org-error-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wOrgErrorBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="提示文字颜色"
              codeLabel="--w-org-error-text-color"
              v-model="item.wOrgErrorTextColor"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-org-error-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wOrgErrorTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-org-error-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wOrgErrorTextWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-org-error-icon-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wOrgErrorIconSize"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-org-error-icon-color"
              v-model="item.wOrgErrorIconColor"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-org-error-border-color-hover"
              v-model="item.wOrgErrorBorderColorHover"
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
  name: 'ThemeOrgSelectError',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
