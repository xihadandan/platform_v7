<template>
  <!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-radio-text-color" v-model="item.wRadioTextColor" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-radio-text-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wRadioTextFontSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-radio-text-font-width"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wRadioTextFontWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem label="颜色" codeLabel="--w-radio-border-color" v-model="item.wRadioBorderColor" />
            <StyleVarSelectFormItem
              label="尺寸"
              codeLabel="--w-radio-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wRadioFontSize"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-radio-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wRadioLrPadding"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-radio-text-color-hover" v-model="item.wRadioTextColorHover" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-radio-text-font-size-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wRadioTextFontSizeHover"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-radio-text-font-width-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wRadioTextFontWeightHover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem label="颜色" codeLabel="--w-radio-border-color-hover" v-model="item.wRadioBorderColorHover" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="选中">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-radio-text-color-checked" v-model="item.wRadioTextColorChecked" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-radio-text-font-size-checked"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wRadioTextFontSizeChecked"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-radio-text-font-width-checked"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wRadioTextFontWeightChecked"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem label="颜色" codeLabel="--w-radio-border-color-checked" v-model="item.wRadioBorderColorChecked" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="4" tab="禁用">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-radio-text-color-disabled"
              v-model="item.wRadioTextColorDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem
              label="填充颜色"
              codeLabel="--w-radio-background-disabled"
              v-model="item.wRadioBackgroundDisabled"
            />
            <StyleColorTreeSelectFormItem
              label="选中填充颜色"
              codeLabel="--w-radio-background-disabled-checked"
              v-model="item.wRadioBackgroundDisabledChecked"
            />
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-radio-border-color-disabled"
              v-model="item.wRadioBorderColorDisabled"
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
  name: 'ThemeRadioProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
