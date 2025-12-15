<template>
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="高度">
            <StyleVarSelectFormItem
              label="高度"
              codeLabel="--w-input-textarea-height"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wInputTextareaHeight"
            />
            <StyleVarSelectFormItem
              label="最小高度"
              codeLabel="--w-input-textarea-min-height"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wInputTextareaMinHeight"
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
  name: 'ThemeInputTextareaProp',
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
