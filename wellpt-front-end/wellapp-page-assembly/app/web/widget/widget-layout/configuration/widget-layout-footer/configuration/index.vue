<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-form-model-item label="名称">
      <a-input v-model="widget.title" @change="evt => onChangeName(evt)" />
    </a-form-model-item>
    <a-form-model-item label="固定底部">
      <a-switch v-model="widget.configuration.stickyOnBottom" />
    </a-form-model-item>
  </a-form-model>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'WidgetLayoutFooterConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    configuration: Object
  },
  data() {
    return { defaultTitle: this.widget.title };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    onChangeName(evt) {
      let v = evt.target.value || defaultTitle;
      this.designer.widgetTreeMap[this.widget.id].title = v.trim();
    }
  },
  mounted() {}
};
</script>
