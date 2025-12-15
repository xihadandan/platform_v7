<template>
  <!-- 错误提示 -->
    <a-tabs default-active-key="1" class="style-prop-tab" size="small">
      <a-tab-pane key="1" tab="默认">
        <a-form-model layout="vertical" :colon="false">
          <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-upload-error-border-color"
              v-model="item.wUploadErrorBorderColor"
            />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-upload-error-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wUploadErrorBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-upload-error-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wUploadErrorBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="提示文字颜色"
              codeLabel="--w-upload-error-text-color"
              v-model="item.wUploadErrorTextColor"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-upload-error-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wUploadErrorTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-upload-error-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wUploadErrorTextWeight"
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
    name: 'ThemeUploadError',
    mixins: [themePropMixin],
    props: {
      item: Object
    },
    data() {
      return {};
    }
  };
  </script>
  