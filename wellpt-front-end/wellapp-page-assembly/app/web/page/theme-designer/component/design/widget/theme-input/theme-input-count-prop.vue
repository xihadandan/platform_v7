<template>
<!-- 字符数 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem
              label="计数文字颜色"
              codeLabel="--w-input-count-color"
              v-model="item.wInputCountColor"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-input-count-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wInputCountSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-input-count-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wInputCountWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="边距">
            <StyleVarSelectFormItem
              label="右内边距"
              codeLabel="--w-input-count-right-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wInputCountRightPadding"
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
  name: 'ThemeInputCountProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
