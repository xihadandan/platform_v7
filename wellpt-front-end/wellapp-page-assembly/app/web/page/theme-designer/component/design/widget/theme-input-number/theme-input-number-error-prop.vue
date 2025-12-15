<template>
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputNumberErrorBorderColor"
              label="边框颜色"
              codeLabel="--w-input-number-error-border-color"
            />

            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-input-number-error-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wInputNumberErrorBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-input-number-error-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wInputNumberErrorBorderWidth"
            />
          </a-collapse-panel>

          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputNumberErrorWordColor"
              label="提示文字颜色"
              codeLabel="--w-input-number-error-word-color"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-input-number-error-word-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wInputNumberErrorWordFontSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-input-number-error-word-font-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wInputNumberErrorWordFontWeight"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="hoverBorder" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="hoverBorder" header="边框">
            <StyleColorTreeSelectFormItem
              v-model="item.wInputNumberErrorBorderColorHover"
              label="边框颜色"
              codeLabel="--w-input-number-error-border-color-hover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="hoverBoxShadow" header="阴影">
            <StyleBoxShadowTreeSelectFormItem
              label="阴影"
              codeLabel="--w-input-number-error-box-shadow-hover"
              v-model="item.wInputNumberErrorBoxShadowHover"
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
              v-model="item.wInputNumberErrorBorderColorFocus"
              label="边框颜色"
              codeLabel="--w-input-number-error-border-color-focus"
            />
          </a-collapse-panel>
          <a-collapse-panel key="focusBoxShadow" header="阴影">
            <StyleBoxShadowTreeSelectFormItem
              label="阴影"
              codeLabel="--w-input-number-error-box-shadow-focus"
              v-model="item.wInputNumberErrorBoxShadowFocus"
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
  name: 'ThemeInputNumberErrorProp',
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
