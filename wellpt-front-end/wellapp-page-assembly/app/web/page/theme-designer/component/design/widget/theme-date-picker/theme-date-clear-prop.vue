<template>
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-date-clear-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wDateClearFontSize"
            />
            <StyleColorTreeSelectFormItem label="图标颜色" codeLabel="--w-date-clear-font-color" v-model="item.wDateClearFontColor" />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="边距">
            <StyleVarSelectFormItem
              label="右内边距"
              codeLabel="--w-date-clear-right-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wDateClearRightPadding"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-date-clear-size-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wDateClearSizeHover"
            />
            <StyleColorTreeSelectFormItem label="图标颜色" codeLabel="--w-date-clear-color-hover" v-model="item.wDateClearColorHover" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="点击">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-date-clear-size-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wDateClearSizeActive"
            />
            <StyleColorTreeSelectFormItem label="图标颜色" codeLabel="--w-date-clear-color-hover" v-model="item.wDateClearColorActive" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
  </a-tabs>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';

export default {
  name: 'ThemeDateClearProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
