<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model-item label="名称">
          <a-input v-model="widget.title" />
        </a-form-model-item>
        <a-collapse :bordered="false" expandIconPosition="right" :accordion="true">
          <a-collapse-panel v-for="(wgt, i) in widget.configuration.widgets" :key="'pos_' + wgt.id">
            <template #header>{{ wgt.title }} - 样式设置</template>
            <template v-if="widget.configuration.widgetPosition[wgt.id] != undefined">
              <a-form-model-item>
                <template #label>
                  宽度使用
                  <a-checkbox v-model="widget.configuration.widgetPosition[wgt.id].wFixed" />
                </template>
                <a-input-group compact>
                  <a-input-number
                    v-model="widget.configuration.widgetPosition[wgt.id].w"
                    :disabled="!widget.configuration.widgetPosition[wgt.id].wFixed"
                  />
                  <a-button :disabled="!widget.configuration.widgetPosition[wgt.id].wFixed">px</a-button>
                </a-input-group>
              </a-form-model-item>
              <a-form-model-item>
                <template #label>
                  宽度使用
                  <a-checkbox
                    :checked="!widget.configuration.widgetPosition[wgt.id].wFixed"
                    @change="e => onChangeWidthUsePercent(e, widget.configuration.widgetPosition[wgt.id])"
                  />
                </template>
                <a-input
                  :value="formateValue(widget.configuration.widgetPosition[wgt.id].widthPercent)"
                  @change="e => onInputWidthPercentValue(e, widget.configuration.widgetPosition[wgt.id])"
                  :disabled="widget.configuration.widgetPosition[wgt.id].wFixed"
                  suffix="%"
                ></a-input>
              </a-form-model-item>
            </template>
          </a-collapse-panel>
        </a-collapse>
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'WidgetPositionLayoutConfiguration',
  props: {
    widget: Object,
    designer: Object
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
  methods: {
    onInputWidthPercentValue(e, item) {
      item.widthPercent = e.target.value.trim() + '%';
    },
    formateValue(v) {
      return v != undefined ? v.replace('%', '') : undefined;
    },
    onChangeWidthUsePercent(e, item) {
      item.wFixed = !e.target.checked;
    }
  }
};
</script>
