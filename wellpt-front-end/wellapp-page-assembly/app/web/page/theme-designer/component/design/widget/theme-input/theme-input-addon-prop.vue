<template>
  <!-- 前置后置 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-input-addon-font-color" v-model="item.wInputAddonFontColor" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-input-addon-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wInputAddonFontSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-input-addon-font-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wInputAddonFontWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-input-addon-arrow-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wInputAddonArrowSize"
            />
            <StyleColorTreeSelectFormItem label="图标颜色" codeLabel="--w-input-addon-arrow-color" v-model="item.wInputAddonArrowColor" />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="背景">
            <StyleColorTreeSelectFormItem label="背景颜色" codeLabel="--w-input-addon-bg-color" v-model="item.wInputAddonBgColor" />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="边距">
            <StyleVarSelectFormItem
              label="左内边距"
              codeLabel="--w-input-addon-left-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wInputAddonLeftPadding"
            />
            <StyleVarSelectFormItem
              label="右内边距"
              codeLabel="--w-input-addon-right-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wInputAddonRightPadding"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬浮">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-input-addon-font-color-hover"
              v-model="item.wInputAddonFontColorHover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-input-addon-arrow-color-hover"
              v-model="item.wInputAddonArrowColorHover"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="点击">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-input-addon-font-color-focus"
              v-model="item.wInputAddonFontColorFocus"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-input-addon-arrow-color-focus"
              v-model="item.wInputAddonArrowColorFocus"
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
  name: 'ThemeInputAddonProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
