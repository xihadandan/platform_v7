<template>
  <!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem label="颜色" codeLabel="--w-radio-button-border-color" v-model="item.wRadioButtonBorderColor" />
            <StyleVarSelectFormItem
              label="样式"
              codeLabel="--w-radio-button-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wRadioButtonBorderStyle"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角"
              codeLabel="--w-radio-button-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wRadioButtonBorderRadius"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-radio-button-color" v-model="item.wRadioButtonColor" />
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
              codeLabel="--w-radio-button-border-color-hover"
              v-model="item.wRadioButtonBorderColorHover"
            />
            <StyleVarSelectFormItem
              label="样式"
              codeLabel="--w-radio-button-border-style-hover"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wRadioButtonBorderStyleHover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-radio-button-text-color-hover"
              v-model="item.wRadioButtonTextColorHover"
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
              codeLabel="--w-radio-button-border-color-active"
              v-model="item.wRadioButtonBorderColorActive"
            />
            <StyleVarSelectFormItem
              label="样式"
              codeLabel="--w-radio-button-border-style-active"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wRadioButtonBorderStyleActive"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-radio-button-text-color-active"
              v-model="item.wRadioButtonTextColorActive"
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
              codeLabel="--w-radio-button-border-color-checked"
              v-model="item.wRadioButtonBorderColorChecked"
            />
            <StyleVarSelectFormItem
              label="样式"
              codeLabel="--w-radio-button-border-style-checked"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wRadioButtonBorderStyleChecked"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-radio-button-text-color-checked"
              v-model="item.wRadioButtonTextColorChecked"
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
              codeLabel="--w-radio-button-border-color-disabled"
              v-model="item.wRadioButtonBorderColorDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-radio-button-text-color-disabled"
              v-model="item.wRadioButtonTextColorDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem
              v-model="item.wRadioButtonBackgroundDisabled"
              label="背景颜色"
              codeLabel="--w-radio-button-background-disabled"
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
  name: 'ThemeRadioButtonProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
