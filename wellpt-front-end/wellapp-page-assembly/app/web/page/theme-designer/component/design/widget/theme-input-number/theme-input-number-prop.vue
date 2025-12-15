<template>
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="defaultBorder" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="defaultBorder" header="边框">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputNumberBorderColor"
              label="边框颜色"
              codeLabel="--w-input-number-border-color"
            />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-input-number-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wInputNumberBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-input-number-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wInputNumberBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角"
              codeLabel="--w-input-number-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wInputNumberBorderRadius"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="文字">
            <StyleColorTreeSelectFormItem v-model="item.wInputNumberColor" label="文字颜色" codeLabel="--w-input-number-color" />
            <StyleColorTreeSelectFormItem
              v-model="item.wInputNumberPlaceholderColor"
              label="占位文字颜色"
              codeLabel="--w-input-number-placeholder-color"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-input-number-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wInputNumberFontSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-input-number-font-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wInputNumberFontWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputNumberBackgroundColor"
              label="背景颜色"
              codeLabel="--w-input-number-background-color"
            />
          </a-collapse-panel>
          <a-collapse-panel key="5" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-input-number-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wInputNumberLrPadding"
            />
          </a-collapse-panel>
          <a-collapse-panel key="6" header="高度">
            <StyleVarSelectFormItem
              label="高度"
              codeLabel="--w-input-number-height"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wInputNumberHeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="7" header="阴影">
            <StyleBoxShadowTreeSelectFormItem label="阴影" codeLabel="--w-input-number-box-shadow" v-model="item.wInputNumberBoxShadow" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="hoverBorder" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="hoverBorder" header="边框">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputNumberBorderColorHover"
              label="边框颜色"
              codeLabel="--w-input-number-border-color-hover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="hoverBoxShadow" header="阴影">
            <StyleBoxShadowTreeSelectFormItem
              label="阴影"
              codeLabel="--w-input-number-box-shadow-hover"
              v-model="item.wInputNumberBoxShadowHover"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="焦点">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="focusBorder" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="focusBorder" header="边框">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputNumberBorderColorFocus"
              label="边框颜色"
              codeLabel="--w-input-number-border-color-focus"
            />
          </a-collapse-panel>
          <a-collapse-panel key="focusBoxShadow" header="阴影">
            <StyleBoxShadowTreeSelectFormItem
              label="阴影"
              codeLabel="--w-input-number-box-shadow-focus"
              v-model="item.wInputNumberBoxShadowFocus"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="4" tab="禁用">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="disabledBorder" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="disabledBorder" header="边框">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputNumberBorderColorDisabled"
              label="边框颜色"
              codeLabel="--w-input-number-border-color-disabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="disabledColor" header="文字">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputNumberColorDisabled"
              label="文字颜色"
              codeLabel="--w-input-number-color-disabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="disabledBg" header="背景">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputNumberBackgroundColorDisabled"
              label="边框颜色"
              codeLabel="--w-input-number-background-color-disabled"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
  </a-tabs>
</template>
<style lang="less"></style>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';

export default {
  name: 'ThemeInputNumberProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
