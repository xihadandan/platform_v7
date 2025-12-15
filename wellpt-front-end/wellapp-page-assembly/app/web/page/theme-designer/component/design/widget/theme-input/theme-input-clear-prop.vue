<template>
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-input-clear-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wInputClearFontSize"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-input-clear-font-color"
              v-model="item.wInputClearFontColor"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="边距">
            <StyleVarSelectFormItem
              label="右内边距"
              codeLabel="--w-input-clear-right-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wInputClearRightPadding"
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
              codeLabel="--w-input-clear-size-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wInputClearSizeHover"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-input-clear-color-hover"
              v-model="item.wInputClearColorHover"
            />
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
              codeLabel="--w-input-clear-size-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wInputClearSizeActive"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-input-clear-color-hover"
              v-model="item.wInputClearColorActive"
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
  name: 'ThemeInputClearProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
