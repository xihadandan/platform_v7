<template>
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="defaultBorder" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="defaultBorder" header="边框">
            <StyleColorTreeSelectFormItem v-model="item.wInputBorderColor" label="边框颜色" codeLabel="--w-input-border-color" />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-input-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wInputBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-input-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wInputBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角"
              codeLabel="--w-input-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wInputBorderRadius"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="文字">
            <StyleColorTreeSelectFormItem 
              v-model="item.wInputColor"
              label="文字颜色"
              codeLabel="--w-input-color"
            />
            <StyleColorTreeSelectFormItem
              v-model="item.wInputPlaceholderColor"
              label="占位文字颜色"
              codeLabel="--w-input-placeholder-color"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-input-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wInputFontSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-input-font-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wInputFontWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputBackgroundColor"
              label="背景颜色"
              codeLabel="--w-input-background-color"
            />
          </a-collapse-panel>
          <a-collapse-panel key="5" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-input-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wInputLrPadding"
            />
          </a-collapse-panel>
          <a-collapse-panel key="6" header="高度">
            <StyleVarSelectFormItem
              label="高度"
              codeLabel="--w-input-height"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wInputHeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="7" header="阴影">
            <StyleBoxShadowTreeSelectFormItem label="阴影" codeLabel="--w-input-box-shadow" v-model="item.wInputBoxShadow" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="hoverBorder" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="hoverBorder" header="边框">
            <StyleColorTreeSelectFormItem v-model="item.wInputBorderColorHover" label="边框颜色" codeLabel="--w-input-border-color-hover" />
          </a-collapse-panel>
          <a-collapse-panel key="hoverBoxShadow" header="阴影">
            <StyleBoxShadowTreeSelectFormItem label="阴影" codeLabel="--w-input-box-shadow-hover" v-model="item.wInputBoxShadowHover" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="焦点">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="focusBorder" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="focusBorder" header="边框">
            <StyleColorTreeSelectFormItem v-model="item.wInputBorderColorFocus" label="边框颜色" codeLabel="--w-input-border-color-focus" />
          </a-collapse-panel>
          <a-collapse-panel key="focusBoxShadow" header="阴影">
            <StyleBoxShadowTreeSelectFormItem label="阴影" codeLabel="--w-input-box-shadow-focus" v-model="item.wInputBoxShadowFocus" />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="4" tab="禁用">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="disabledBorder" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="disabledBorder" header="边框">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputBorderColorDisabled"
              label="边框颜色"
              codeLabel="--w-input-border-color-disabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="disabledColor" header="文字">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputColorDisabled"
              label="文字颜色"
              codeLabel="--w-input-color-disabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="disabledBg" header="背景">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputBackgroundColorDisabled"
              label="边框颜色"
              codeLabel="--w-input-background-color-disabled"
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
  name: 'ThemeInputProp',
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
