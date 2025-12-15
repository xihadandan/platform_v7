<template>
<!-- 编辑状态 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="删除图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-tag-edit-close-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wTagEditCloseSize"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-tag-edit-close-color"
              v-model="item.wTagEditCloseColor"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="边距">
            <StyleVarSelectFormItem
              label="外边距"
              codeLabel="--w-tag-edit-margin-right"
              :options="getStyleDeriveOptions(packJson.spaceConfig.marginConfig)"
              v-model="item.wTagEditMarginRight"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="删除图标">
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-tag-close-color-hover"
              v-model="item.wTagCloseColorHover"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="点击">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="删除图标">
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-tag-close-color-active"
              v-model="item.wTagCloseColorActive"
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
  name: 'ThemeTagEditor',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
