<template>
  <!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem label="边框颜色" codeLabel="--w-switch-border-color" v-model="item.wSwitchBorderColor" />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-switch-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wSwitchBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-switch-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wSwitchBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-switch-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wSwitchBorderRadius"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-switch-text-color" v-model="item.wSwitchTextColor" />
            <StyleColorTreeSelectFormItem
              label="文字颜色(选中)"
              codeLabel="--w-switch-text-color-checked"
              v-model="item.wSwitchTextColorChecked"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-switch-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wSwitchTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-switch-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wSwitchTextWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem label="背景颜色" codeLabel="--w-switch-checked-bg" v-model="item.wSwitchCheckedBg" />
          </a-collapse-panel>
          <a-collapse-panel key="5" header="高度">
            <StyleVarSelectFormItem
              label="高度"
              codeLabel="--w-switch-height"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wSwitchHeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="6" header="圆点">
            <StyleVarSelectFormItem
              label="尺寸"
              codeLabel="--w-select-circle-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wSwitchCircleSize"
            />
            <StyleColorTreeSelectFormItem label="颜色" codeLabel="--w-switch-circle-bg" v-model="item.wSwitchCircleBg" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="禁用">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-switch-border-color-disabled"
              v-model="item.wSwitchBorderColorDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-switch-text-color-disabled"
              v-model="item.wSwitchTextColorDisabled"
            />
            <StyleColorTreeSelectFormItem
              label="文字颜色（选中）"
              codeLabel="--w-switch-text-color-checked-disabled"
              v-model="item.wSwitchTextColorCheckedDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="背景">
            <StyleColorTreeSelectFormItem
              label="背景颜色"
              codeLabel="--w-switch-checked-bg-disabled"
              v-model="item.wSwitchCheckedBgDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="圆点">
            <StyleColorTreeSelectFormItem label="颜色" codeLabel="--w-switch-circle-bg-disabled" v-model="item.wSwitchCircleBgDisabled" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
  </a-tabs>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';

export default {
  name: 'ThemeSwitchProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
