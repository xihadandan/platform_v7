<template>
  <!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem label="边框颜色" codeLabel="--w-date-border-color" v-model="item.wDateBorderColor" />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-date-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wDateBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-date-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wDateBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-date-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wDateBorderRadius"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-date-text-color" v-model="item.wDateTextColor" />
            <StyleColorTreeSelectFormItem
              label="占位文字颜色"
              codeLabel="--w-date-placeholder-color"
              v-model="item.wDatePlaceholderColor"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-date-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wDateTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-date-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wDateTextWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-date-picker-icon-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wDatePickerIconSize"
            />
            <StyleColorTreeSelectFormItem label="图标颜色" codeLabel="--w-date-picker-icon-color" v-model="item.wDatePickerIconColor" />
          </a-collapse-panel>
          <a-collapse-panel key="5" header="背景">
            <StyleColorTreeSelectFormItem label="背景颜色" codeLabel="--w-date-picker-input-bg" v-model="item.wDatePickerInputBg" />
          </a-collapse-panel>
          <a-collapse-panel key="6" header="边距">
            <StyleVarSelectFormItem
              label="右外边距"
              codeLabel="--w-date-picker-icon-right"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wDatePickerIconRight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="7" header="高度">
            <StyleVarSelectFormItem
              label="高度"
              codeLabel="--w-date-picker-input-height"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wDatePickerInputHeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="8" header="阴影">
            <StyleBoxShadowTreeSelectFormItem
              label="阴影"
              codeLabel="--w-date-picker-input-box-shadow"
              v-model="item.wDatePickerInputBoxShadow"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem label="边框颜色" codeLabel="--w-date-border-color-hover" v-model="item.wDateBorderColorHover" />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-date-picker-icon-color-hover"
              v-model="item.wDatePickerIconColorHover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="8" header="阴影">
            <StyleBoxShadowTreeSelectFormItem
              label="阴影"
              codeLabel="--w-date-picker-input-box-shadow-hover"
              v-model="item.wDatePickerInputBoxShadowHover"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="焦点">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem label="边框颜色" codeLabel="--w-date-border-color-focus" v-model="item.wDateBorderColorFocus" />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-date-picker-icon-color-focus"
              v-model="item.wDatePickerIconColorFocus"
            />
          </a-collapse-panel>
          <a-collapse-panel key="8" header="阴影">
            <StyleBoxShadowTreeSelectFormItem
              label="阴影"
              codeLabel="--w-date-picker-input-box-shadow-focus"
              v-model="item.wDatePickerInputBoxShadowFocus"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="4" tab="禁用">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-date-border-color-disabled"
              v-model="item.wDateBorderColorDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-date-text-color-disabled" v-model="item.wDateTextColorDisabled" />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="图标">
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-date-picker-icon-color-disabled"
              v-model="item.wDatPickerIconColorDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem label="背景颜色" codeLabel="--w-date-picker-bg-disabled" v-model="item.wDatePickerBgDisabled" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
  </a-tabs>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';

export default {
  name: 'ThemeDatePickerProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
