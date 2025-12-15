<template>
  <!-- 上下加减按钮 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="分割线颜色"
              codeLabel="--w-input-number-handler-border-color"
              v-model="item.wInputNumberHandlerBorderColor"
            />
            <StyleVarSelectFormItem
              label="分割线样式"
              codeLabel="--w-input-number-handler-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wInputNumberHandlerBorderStyle"
            />
            <StyleVarSelectFormItem
              label="分割线尺寸"
              codeLabel="--w-input-number-handler-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wInputNumberHandlerBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="背景">
            <StyleColorTreeSelectFormItem
              label="背景颜色"
              codeLabel="--w-input-number-handler-bg-color"
              v-model="item.wInputNumberHandlerBgColor"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="图标">
            <StyleColorTreeSelectFormItem
              label="颜色"
              codeLabel="--w-input-number-handler-font-color"
              v-model="item.wInputNumberHandlerFontColor"
            />
            <StyleVarSelectFormItem
              label="尺寸"
              codeLabel="--w-input-number-handler-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wInputNumberHandlerFontSize"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-input-number-handler-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wInputNumberHandlerLrPadding"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="背景">
            <StyleColorTreeSelectFormItem
              label="背景颜色"
              codeLabel="--w-input-number-handler-bg-color-hover"
              v-model="item.wInputNumberHandlerBgColorHover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem
              label="颜色"
              codeLabel="--w-input-number-handler-font-color-hover"
              v-model="item.wInputNumberHandlerFontColorHover"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="点击">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="背景">
            <StyleColorTreeSelectFormItem
              label="背景颜色"
              codeLabel="--w-input-number-handler-bg-color-active"
              v-model="item.wInputNumberHandlerBgColorActive"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem
              label="颜色"
              codeLabel="--w-input-number-handler-font-color-active"
              v-model="item.wInputNumberHandlerFontColorActive"
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
  name: 'ThemeInputNumberUpdown',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
