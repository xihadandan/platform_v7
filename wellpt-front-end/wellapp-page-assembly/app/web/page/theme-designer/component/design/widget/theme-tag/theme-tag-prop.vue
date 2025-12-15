<template>
<!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-tag-border-color"
              v-model="item.wTagBorderColor"
            />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-tag-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wTagBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-tag-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wTagBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-tag-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wTagBorderRadius"
            />
           </a-collapse-panel>
          <a-collapse-panel key="3" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-tag-text-color"
              v-model="item.wTagTextColor"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-tag-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wTagTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-tag-text-weight"
             :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wTagTextWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem
              label="背景颜色"
              codeLabel="--w-tag-background"
              v-model="item.wTagBackground"
            />
          </a-collapse-panel>
          <a-collapse-panel key="5" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-tag-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wTagLrPadding"
            />
          </a-collapse-panel>
          <a-collapse-panel key="6" header="高度">
            <StyleVarSelectFormItem
              label="高度"
              codeLabel="--w-tag-height"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wTagHeight"
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
  name: 'ThemeTagProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
