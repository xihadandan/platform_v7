<template>
<!-- 水平布局-等宽 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边距">
            <StyleVarSelectFormItem
              label="选项右外边距"
              codeLabel="--w-radio-group-width-mr"
              :options="getStyleDeriveOptions(packJson.spaceConfig.marginConfig)"
              v-model="item.wRadioGroupWidthMr"
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
  name: 'ThemeGroupItemWidth',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
