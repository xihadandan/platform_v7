<template>
  <!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="颜色"
              codeLabel="--w-radio-button-solid-border-color"
              v-model="item.wRadioButtonSolidBorderColor"
            />
            <StyleVarSelectFormItem
              label="样式"
              codeLabel="--w-radio-button-solid-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wRadioButtonSolidBorderStyle"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角"
              codeLabel="--w-radio-button-solid-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wRadioButtonSolidBorderRadius"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-radio-button-solid-color" v-model="item.wRadioButtonSolidColor" />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem v-model="item.wRadioButtonSolidBg" label="背景颜色" codeLabel="--w-radio-button-solid-bg" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="颜色"
              codeLabel="--w-radio-button-solid-border-color-hover"
              v-model="item.wRadioButtonSolidBorderColorHover"
            />
            <StyleVarSelectFormItem
              label="样式"
              codeLabel="--w-radio-button-solid-border-style-hover"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wRadioButtonSolidBorderStyleHover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-radio-button-solid-text-color-hover"
              v-model="item.wRadioButtonSolidTextColorHover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem
              v-model="item.wRadioButtonSolidBackgroundHover"
              label="背景颜色"
              codeLabel="--w-radio-button-solid-background-hover"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="5" tab="点击">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="颜色"
              codeLabel="--w-radio-button-solid-border-color-active"
              v-model="item.wRadioButtonSolidBorderColorActive"
            />
            <StyleVarSelectFormItem
              label="样式"
              codeLabel="--w-radio-button-solid-border-style-active"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wRadioButtonSolidBorderStyleActive"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-radio-button-solid-text-color-active"
              v-model="item.wRadioButtonSolidTextColorActive"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem
              v-model="item.wRadioButtonSolidBackgroundActive"
              label="背景颜色"
              codeLabel="--w-radio-button-solid-background-active"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="选中">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="颜色"
              codeLabel="--w-radio-button-solid-border-color-checked"
              v-model="item.wRadioButtonSolidBorderColorChecked"
            />
            <StyleVarSelectFormItem
              label="样式"
              codeLabel="--w-radio-button-solid-border-style-checked"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wRadioButtonSolidBorderStyleChecked"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-radio-button-solid-text-color-checked"
              v-model="item.wRadioButtonSolidTextColorChecked"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem
              v-model="item.wRadioButtonSolidBackgroundChecked"
              label="背景颜色"
              codeLabel="--w-radio-button-solid-background-checked"
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
              label="颜色"
              codeLabel="--w-radio-button-solid-border-color-disabled"
              v-model="item.wRadioButtonSolidBorderColorDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-radio-button-solid-text-color-disabled"
              v-model="item.wRadioButtonSolidTextColorDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem
              v-model="item.wRadioButtonSolidBackgroundDisabled"
              label="背景颜色"
              codeLabel="--w-radio-button-solid-background-disabled"
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
  name: 'ThemeRadioButtonSolidProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
