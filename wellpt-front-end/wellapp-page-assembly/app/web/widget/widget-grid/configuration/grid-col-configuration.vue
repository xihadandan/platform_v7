<template>
  <div>
    <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
      <a-tabs default-active-key="1" v-if="!rowDisplay">
        <a-tab-pane key="1" tab="设置">
          <a-form-model-item label="列占位格数" v-show="vRowType === 'default'">
            <a-input-number v-model="widget.configuration.span" />
          </a-form-model-item>
          <a-form-model-item :label="designer.terminalType == 'pc' ? '' : '移动端' + '列宽度'" v-show="vRowType === 'flex'">
            <a-input-number v-model="widget.configuration.flex" v-if="designer.terminalType == 'pc'" />
            <a-input-number v-model="widget.configuration.uniflex" v-else />
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane key="3" tab="样式设置">
          <StyleCodeConfiguration :configuration="widget.configuration" />
        </a-tab-pane>
      </a-tabs>
      <a-empty description="暂无配置" v-else style="margin-top: 50%" />
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'GridColConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {
    vRowType() {
      let p = this.designer.widgetTreeMap[this.widget.id].parentKey,
        parentWidget = this.designer.widgetIdMap[p],
        rowType = parentWidget.configuration.rowType;
      return rowType;
    },
    // 移动端,行展示
    rowDisplay() {
      let p = this.designer.widgetTreeMap[this.widget.id].parentKey,
        parentWidget = this.designer.widgetIdMap[p];
      return this.designer.terminalType == 'mobile' && parentWidget.configuration.uniConfiguration.rowDisplay;
    }
  },
  created() {},
  methods: {},
  mounted() {}
};
</script>
