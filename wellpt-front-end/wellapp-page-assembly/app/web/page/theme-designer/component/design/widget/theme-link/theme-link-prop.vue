<template>
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="text" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="text" header="文字">
            <StyleColorTreeSelectFormItem v-model="item.wLinkTextColor" label="文字颜色" codeLabel="--w-link-text-color" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-link-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wLinkFontSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-link-font-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wLinkFontWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="背景">
            <StyleColorTreeSelectFormItem v-model="item.wLinkBackgroundColor" label="背景颜色" codeLabel="--w-link-background-color" />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-link-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wLinkLrPadding"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="hoverText" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="hoverText" header="文字">
            <StyleColorTreeSelectFormItem v-model="item.wLinkColorHover" label="文字颜色" codeLabel="--w-link-color-hover" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-link-font-size-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wLinkFontSizeHover"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-link-font-weight-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wLinkFontWeightHover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="hoverBackgroundColor" header="背景">
            <StyleColorTreeSelectFormItem
              v-model="item.wLinkBackgroundColorHover"
              label="背景颜色"
              codeLabel="--w-link-background-color-hover"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="点击">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="activeText" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="activeText" header="文字">
            <StyleColorTreeSelectFormItem v-model="item.wLinkColorActive" label="文字颜色" codeLabel="--w-link-color-active" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-link-font-size-active"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wLinkFontSizeActive"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-link-font-weight-active"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wLinkFontWeightActive"
            />
          </a-collapse-panel>
          <a-collapse-panel key="activeBackgroundColor" header="背景">
            <StyleColorTreeSelectFormItem
              v-model="item.wLinkBackgroundColorActive"
              label="背景颜色"
              codeLabel="--w-link-background-color-active"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="4" tab="禁用">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="disabledText" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="disabledText" header="文字">
            <StyleColorTreeSelectFormItem v-model="item.wLinkColorDisabled" label="文字颜色" codeLabel="--w-link-color-disabled" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-link-font-size-disabled"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wLinkFontSizeDisabled"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-link-font-weight-disabled"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wLinkFontWeightDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="disabledBackgroundColor" header="背景">
            <StyleColorTreeSelectFormItem
              v-model="item.wLinkBackgroundColorDisabled"
              label="背景颜色"
              codeLabel="--w-link-background-color-disabled"
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
  name: 'ThemeLinkProp',
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
