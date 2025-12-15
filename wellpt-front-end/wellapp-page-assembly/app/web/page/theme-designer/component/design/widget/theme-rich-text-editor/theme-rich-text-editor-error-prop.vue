<template>
  <!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem label="边框颜色" codeLabel="--w-rich-error-border-color" v-model="item.wRichErrorBorderColor" />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-rich-error-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wRichErrorBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-rich-error-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wRichErrorBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem v-model="item.wRichErrorWordColor" label="提示文字颜色" codeLabel="--w-rich-error-word-color" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-rich-error-word-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wRichErrorWordFontSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-rich-error-word-font-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wRichErrorWordFontWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem label="背景颜色" codeLabel="--w-rich-error-bg-color" v-model="item.wRichErrorBgColor" />
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
              codeLabel="--w-rich-error-border-color-hover"
              v-model="item.wRichErrorBorderColorHover"
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
  name: 'ThemeRichTextEditorErrorProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
