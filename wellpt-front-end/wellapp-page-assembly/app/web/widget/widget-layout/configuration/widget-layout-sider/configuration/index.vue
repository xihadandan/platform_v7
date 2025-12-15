<template>
  <div>
    <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
      <a-form-model-item label="名称">
        <a-input v-model="widget.title" @change="evt => onChangeName(evt)" />
      </a-form-model-item>
      <a-form-model-item label="可收起">
        <a-switch v-model="widget.configuration.collapsible" />
      </a-form-model-item>
      <a-form-model-item label="logo" v-if="parent.configuration.logoPosition == 'sider'">
        <ImageLibrary v-model="parent.configuration.header.configuration.logo" width="100%" :height="100" :tipVisible="true" />
      </a-form-model-item>
      <a-form-model-item label="背景色">
        <a-radio-group size="small" v-model="widget.configuration.backgroundColorType" button-style="solid">
          <a-radio-button value="primary-color">主题色</a-radio-button>
          <a-radio-button value="light">浅色</a-radio-button>
          <a-radio-button value="dark">深色</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';
export default {
  name: 'WidgetLayoutSiderConfiguration',
  mixins: [],
  props: {
    widget: Object,
    parent: Object,
    designer: Object,
    configuration: Object
  },

  data() {
    return { defaultTitle: this.widget.title };
  },

  beforeCreate() {},
  components: { ImageLibrary },
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
