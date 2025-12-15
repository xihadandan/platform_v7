<template>
  <div>
    <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" />
          </a-form-model-item>
          <a-form-model-item label="图片">
            <ImageLibrary v-model="widget.configuration.src" style="float: right" />
          </a-form-model-item>
          <a-form-model-item label="设置图片高度">
            <a-input-number v-model="widget.configuration.style.height" :min="1" />
          </a-form-model-item>
          <a-form-model-item label="图片比例适应" style="flex-direction: column" :wrapper-col="{ style: { 'text-align': 'left' } }">
            <a-radio-group v-model="widget.configuration.objectFit">
              <a-radio value="fill">不保证保持原有的比例, 内容拉伸填充整个内容容器</a-radio>
              <a-radio value="contain">保持原有尺寸比例, 内容被缩放</a-radio>
              <a-radio value="cover">保持原有尺寸比例, 但部分内容可能被剪切</a-radio>
              <a-radio value="none">保留原有元素内容的比例</a-radio>
            </a-radio-group>
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane key="2" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import { queryString } from '@framework/vue/utils/util';
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';

export default {
  name: 'WidgetImageConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: { ImageLibrary },
  computed: {},
  created() {},
  methods: {
    // 生成组件定义数据保存
    getWidgetDefinitionElements(widget) {
      return [
        {
          wtype: widget.wtype,
          id: widget.id,
          title: widget.title,
          definitionJson: JSON.stringify(widget)
        }
      ];
    },
    onUploadChange(data) {
      console.log(data);
      this.widget.configuration.fileID = data ? data.fileID : undefined;
      this.widget.configuration.srcName = data ? data.filename : undefined;
      this.widget.configuration.contentType = data ? data.contentType : undefined;
    },
    getFunctionElements(wgt) {
      let functionElements = {},
        elements = [];

      let configuration = wgt.configuration,
        { src } = configuration;
      if (src) {
        elements.push({
          ref: true,
          functionType: 'logicFileInfo',
          exportType: 'logicFileInfo',
          configType: '1',
          uuid: configuration.fileID,
          name: configuration.srcName,
          isProtected: false
        });
      }

      functionElements[wgt.id] = elements;
      return functionElements;
    }
  },
  mounted() {},
  configuration(widget) {
    let conf = {
      title: '图片',
      src: undefined,
      objectFit: 'none',
      style: {
        width: '100%',
        height: 150
      }
    };
    if (widget != undefined && widget.useScope === 'bigScreen') {
      // 大屏设计，需要默认相关配置
      conf.style.width = 150;
      conf.style.height = 150;
    }
    return conf;
  }
};
</script>
