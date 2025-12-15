<template>
  <!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem label="颜色" codeLabel="--w-upload-list-text-color" v-model="item.wUploadListTextColor" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-upload-list-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wUploadListTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-upload-list-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wUploadListTextWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="提示文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-upload-tips-color" v-model="item.wUploadTipsColor" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-upload-tips-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wUploadTipsSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-upload-tips-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wUploadTipsWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="背景">
            <StyleColorTreeSelectFormItem label="背景颜色" codeLabel="--w-upload-list-bg" v-model="item.wUploadListBg" />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-upload-list-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wUploadListPadding"
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
  name: 'ThemeUploadProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
