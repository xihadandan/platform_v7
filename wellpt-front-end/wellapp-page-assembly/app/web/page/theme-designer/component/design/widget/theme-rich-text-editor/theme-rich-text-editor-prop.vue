<template>
  <!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem label="边框颜色" codeLabel="--w-rich-border-color" v-model="item.wRichBorderColor" />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-rich-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wRichBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-rich-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wRichBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-rich-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wRichBorderRadius"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="文字">
            <StyleColorTreeSelectFormItem
              label="占位文字颜色"
              codeLabel="--w-rich-placeholder-color"
              v-model="item.wRichPlaceholderColor"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem label="背景颜色" codeLabel="--w-rich-bg-color" v-model="item.wRichBgColor" />
          </a-collapse-panel>
          <a-collapse-panel key="5" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-rich-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wRichLrPadding"
            />
          </a-collapse-panel>
          <!-- <a-collapse-panel key="6" header="高度">
            <StyleVarSelectFormItem
              label="默认高度"
              codeLabel="--w-rich-def-height"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wRichDefHeight"
            />
          </a-collapse-panel> -->
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem label="边框颜色" codeLabel="--w-rich-border-color-hover" v-model="item.wRichBorderColorHover" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="禁用">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-rich-border-color-disabled"
              v-model="item.wRichBorderColorDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="背景">
            <StyleColorTreeSelectFormItem label="背景颜色" codeLabel="--w-rich-bg-color-disabled" v-model="item.wRichBgColorDisabled" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
  </a-tabs>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';

export default {
  name: 'ThemeRichTextEditorProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
