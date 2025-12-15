<template>
  <!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-checkbox-text-color" v-model="item.wCheckboxTextColor" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-checkbox-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wCheckboxTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-checkbox-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wCheckboxTextWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem label="颜色" codeLabel="--w-checkbox-border-color" v-model="item.wCheckboxBorderColor" />
            <StyleVarSelectFormItem
              label="尺寸"
              codeLabel="--w-checkbox-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wCheckboxSize"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="边距">
            <StyleVarSelectFormItem
              label="文本左右内边距"
              codeLabel="--w-checkbox-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wCheckboxLrPadding"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-checkbox-text-color-hover"
              v-model="item.wCheckboxTextColorHover"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-checkbox-text-size-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wCheckboxTextSizeHover"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-checkbox-text-weight-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wCheckboxTextWeightHover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem label="颜色" codeLabel="--w-checkbox-color-hover" v-model="item.wCheckboxColorHover" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="选中">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-checkbox-text-color-checked"
              v-model="item.wCheckboxTextColorChecked"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-checkbox-text-size-checked"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wCheckboxTextSizeChecked"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-checkbox-text-width-checked"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wCheckboxTextWeightChecked"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem label="颜色" codeLabel="--w-checkbox-color" v-model="item.wCheckboxColor" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="4" tab="禁用">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-checkbox-disabled-color" v-model="item.wCheckboxDisabledColor" />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem label="填充颜色" codeLabel="--w-checkbox-disabled-bg" v-model="item.wCheckboxDisabledBg" />
            <StyleColorTreeSelectFormItem
              label="选中填充颜色"
              codeLabel="--w-checkbox-disabled-bg-checked"
              v-model="item.wCheckboxDisabledBgChecked"
            />
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-checkbox-disabled-border-color"
              v-model="item.wCheckboxDisabledBorderColor"
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
  name: 'ThemeCheckboxProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
