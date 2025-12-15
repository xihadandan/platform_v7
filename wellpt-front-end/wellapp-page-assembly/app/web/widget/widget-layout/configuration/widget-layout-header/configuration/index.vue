<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-form-model-item label="名称">
      <a-input v-model="widget.title" @change="evt => onChangeName(evt)" />
    </a-form-model-item>
    <a-form-model-item label="标题">
      <a-input v-model="widget.configuration.title" />
    </a-form-model-item>
    <a-form-model-item label="背景色">
      <a-radio-group size="small" v-model="widget.configuration.backgroundColorType" button-style="solid">
        <a-radio-button value="primary-color">主题色</a-radio-button>
        <a-radio-button value="light">固定浅色</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="logo" v-if="parent.configuration.logoPosition == 'header'">
      <ImageLibrary v-model="parent.configuration.header.configuration.logo" width="100%" :height="100" :tipVisible="true" />
    </a-form-model-item>
    <a-form-model-item label="固定头部">
      <a-switch v-model="widget.configuration.stickyOnTop" />
    </a-form-model-item>
  </a-form-model>
</template>
<style></style>
<script type="text/babel">
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';
export default {
  name: 'WidgetLayoutHeaderConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    configuration: Object,
    parent: Object
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
    },

    getFunctionElements(wgt, EWidgets) {
      let elements = {};
      elements[wgt.id] = [];

      // 导出logo图片
      if (wgt.configuration.logo && wgt.configuration.logo.startsWith('/proxy-repository/repository/file/mongo/download')) {
        elements[wgt.id].push({
          ref: true,
          functionType: 'logicFileInfo',
          exportType: 'logicFileInfo',
          configType: '1',
          uuid: wgt.configuration.logo.split('/proxy-repository/repository/file/mongo/download?fileID=')[1],
          isProtected: false
        });
      }

      return elements;
    }
  },
  mounted() {}
};
</script>
