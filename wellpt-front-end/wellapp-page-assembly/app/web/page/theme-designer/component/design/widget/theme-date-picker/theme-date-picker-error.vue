<template>
  <!-- 错误提示 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="颜色"
              codeLabel="--w-date-picker-error-border-color"
              v-model="item.wDatePickerErrorBorderColor"
            />
            <StyleVarSelectFormItem
              label="样式"
              codeLabel="--w-date-picker-error-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wDatePickerErrorBorderStyle"
            />
            <StyleVarSelectFormItem
              label="宽度"
              codeLabel="--w-date-picker-error-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wDatePickerErrorBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="提示文字颜色"
              codeLabel="--w-date-picker-error-text-color"
              v-model="item.wDatePickerErrorTextColor"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-date-picker-error-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wDatePickerErrorTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-date-picker-error-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wDatePickerErrorTextWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-date-picker-error-icon-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wDatePickerErrorIconSize"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-date-picker-error-icon-color"
              v-model="item.wDatePickerErrorIconColor"
            />
          </a-collapse-panel>
          <a-collapse-panel key="8" header="阴影">
            <StyleBoxShadowTreeSelectFormItem
              label="阴影"
              codeLabel="--w-date-picker-input-error-box-shadow"
              v-model="item.wDatePickerInputErrorBoxShadow"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-date-picker-error-border-color-hover"
              v-model="item.wDatePickerErrorBorderColorHover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="8" header="阴影">
            <StyleBoxShadowTreeSelectFormItem
              label="阴影"
              codeLabel="--w-date-picker-input-error-box-shadow-hover"
              v-model="item.wDatePickerInputErrorBoxShadowHover"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="焦点">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-date-picker-error-border-color-focus"
              v-model="item.wDatePickerErrorBorderColorFocus"
            />
          </a-collapse-panel>
          <a-collapse-panel key="8" header="阴影">
            <StyleBoxShadowTreeSelectFormItem
              label="阴影"
              codeLabel="--w-date-picker-input-error-box-shadow-focus"
              v-model="item.wDatePickerInputErrorBoxShadowFocus"
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
  name: 'ThemeDatePickerError',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
