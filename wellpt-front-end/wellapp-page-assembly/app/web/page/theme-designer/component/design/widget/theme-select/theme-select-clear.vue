<template>
<!-- 允许清空 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-select-clear-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wSelectClearSize"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-select-clear-color"
              v-model="item.wSelectClearColor"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="边距">
            <StyleVarSelectFormItem
              label="右外边距"
              codeLabel="--w-select-clear-right"
              :options="getStyleDeriveOptions(packJson.spaceConfig.marginConfig)"
              v-model="item.wSelectClearRight"
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
              codeLabel="--w-select-clear-size-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wSelectClearSizeHover"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-select-clear-color-hover"
              v-model="item.wSelectClearColorHover"
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
              codeLabel="--w-select-clear-size-active"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wSelectClearSizeActive"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-select-clear-color-active"
              v-model="item.wSelectClearColorActive"
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
  name: 'ThemeSelectClear',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
