<template>
  <div>
    <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
      <a-form-model-item label="名称">
        <a-input v-model="widget.title" @change="evt => onChangeName(evt)" />
      </a-form-model-item>
      <a-form-model-item label="启用内容标签页">
        <a-switch v-model="widget.configuration.contentAsTabs" />
      </a-form-model-item>
      <div v-show="widget.configuration.contentAsTabs">
        <a-form-model-item label="默认内容页签名称">
          <a-input v-model="widget.configuration.defaultTabTitle" />
        </a-form-model-item>
        <a-form-model-item label="默认内容页签可关闭">
          <a-switch v-model="widget.configuration.defaultTabClosable" />
        </a-form-model-item>
      </div>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'WidgetLayoutContentConfiguration',
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
