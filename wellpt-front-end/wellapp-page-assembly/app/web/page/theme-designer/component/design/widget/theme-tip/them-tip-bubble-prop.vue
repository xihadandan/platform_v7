<template>
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="defaultBorder" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="defaultBorder" header="边框">
            <StyleColorTreeSelectFormItem v-model="item.wTipBorderColor" label="边框颜色" codeLabel="--w-tip-border-color" />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-tip-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wTipBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-tip-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wTipBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-tip-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wTipBorderRadius"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="文字">
            <StyleColorTreeSelectFormItem v-model="item.wTipColor" label="文字颜色" codeLabel="--w-tip-color" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-tip-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wTipFontSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-tip-font-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wTipFontWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="填充">
            <StyleColorTreeSelectFormItem v-model="item.wTipBackgroundColor" label="填充颜色" codeLabel="--w-tip-background-color" />
          </a-collapse-panel>
          <a-collapse-panel key="5" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-tip-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wTipLrPadding"
            />
          </a-collapse-panel>
          <a-collapse-panel key="6" header="阴影">
            <StyleBoxShadowTreeSelectFormItem label="阴影" codeLabel="--w-tip-box-shadow" v-model="item.wTipBoxShadow" />
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
  name: 'ThemeTipBubbleProp',
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
