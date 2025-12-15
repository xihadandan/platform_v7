<template>
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="color" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="color" header="图标">
            <StyleColorTreeSelectFormItem v-model="item.wTipIconColor" label="颜色" codeLabel="--w-tip-icon-color" />
            <StyleVarSelectFormItem
              label="尺寸"
              codeLabel="--w-tip-icon-font-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wTipIconFontSize"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="color" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="color" header="图标">
            <StyleColorTreeSelectFormItem v-model="item.wTipIconColorHover" label="颜色" codeLabel="--w-tip-icon-color" />
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
  name: 'ThemeTipProp',
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
