<template>
  <!-- 气泡显示 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem label="边框颜色" codeLabel="--w-upload-popover-color" v-model="item.wUploadPopoverColor" />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-upload-popover-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wUploadPopoverStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-upload-popover-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wUploadPopoverWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-upload-popover-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wUploadPopoverRadius"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="文字">
            <StyleColorTreeSelectFormItem
              label="标题文字颜色"
              codeLabel="--w-upload-popover-title-color"
              v-model="item.wUploadPopoverTitleColor"
            />
            <StyleVarSelectFormItem
              label="标题字号"
              codeLabel="--w-upload-popover-title-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wUploadPopoverTitleSize"
            />
            <StyleVarSelectFormItem
              label="标题字重"
              codeLabel="--w-upload-popover-title-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wUploadPopoverTitleWeight"
            />
            <StyleColorTreeSelectFormItem
              label="文件描述文字颜色"
              codeLabel="--w-upload-popover-desc-color"
              v-model="item.wUploadPopoverDescColor"
            />
            <StyleVarSelectFormItem
              label="文件描述字号"
              codeLabel="--w-upload-popover-desc-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wUploadPopoverDescSize"
            />
            <StyleVarSelectFormItem
              label="文件描述字重"
              codeLabel="--w-upload-popover-desc-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wUploadPopoverDescWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="填充">
            <StyleColorTreeSelectFormItem
              label="背景颜色"
              codeLabel="--w-upload-popover-background"
              v-model="item.wUploadPopoverBackground"
            />
          </a-collapse-panel>
          <a-collapse-panel key="5" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-upload-popover-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wUploadPopoverLrPadding"
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
  name: 'ThemeUploadPopover',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
