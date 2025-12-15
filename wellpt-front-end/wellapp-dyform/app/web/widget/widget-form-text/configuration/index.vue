<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model-item label="ID" :label-col="{ span: 6 }" :wrapper-col="{ span: 15 }">
          <a-input v-model="widget.id" :readOnly="true" />
        </a-form-model-item>

        <a-form-model-item label="名称" :label-col="{ span: 6 }" :wrapper-col="{ span: 15 }">
          <a-input v-model="widget.configuration.name" />
        </a-form-model-item>

        <a-form-model-item label="内容" :label-col="{ span: 6 }" :wrapper-col="{ span: 15 }">
          <div style="display: flex">
            <a-textarea v-model="widget.configuration.content" />
            <WI18nInput
              style="padding: 0px 15px"
              :widget="widget"
              :designer="designer"
              :code="widget.id"
              v-model="widget.configuration.content"
            />
          </div>
        </a-form-model-item>
        <FieldFormulaInput :configuration="widget.configuration" :widget="widget" :designer="designer" />
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'WidgetFormTextConfiguration',
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
  computed: {},
  created() {},
  methods: {},
  mounted() {}
};
</script>
