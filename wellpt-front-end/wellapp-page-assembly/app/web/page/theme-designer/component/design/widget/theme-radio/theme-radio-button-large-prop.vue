<template>
  <!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleVarSelectFormItem
              label="宽度"
              codeLabel="--w-radio-button-large-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wRadioButtonLargeBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-radio-button-large-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wRadioButtonLargeTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-radio-button-large-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wRadioButtonLargeTextWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-radio-button-large-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wRadioButtonLargeLrPadding"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="高度">
            <StyleVarSelectFormItem
              label="高度"
              codeLabel="--w-radio-button-large-height-base"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wRadioButtonLargeHeightBase"
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
  name: 'ThemeRadioButtonLargeProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
