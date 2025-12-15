<template>
  <!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleVarSelectFormItem
              label="宽度"
              codeLabel="--w-radio-button-small-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wRadioButtonSmallBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-radio-button-small-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wRadioButtonSmallTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-radio-button-small-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wRadioButtonSmallTextWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-radio-button-small-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wRadioButtonSmallLrPadding"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="高度">
            <StyleVarSelectFormItem
              label="高度"
              codeLabel="--w-radio-button-small-height-base"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wRadioButtonSmallHeightBase"
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
  name: 'ThemeRadioButtonSmallProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
