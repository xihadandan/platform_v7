<template>
  <div style="position: relative">
    <a-card :bordered="false" class="preview-card module-index-nav-content">
      <template slot="title">
        <span class="title">导航预览</span>
      </template>
      <template v-if="navData.definition.designable">
        <img src="/static/images/module_page_design.png" style="width: 100%; border-radius: 4px" />
      </template>
      <template v-else>
        <template slot="extra">
          <a-button type="link" size="small" @click="previewModulePage">
            <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
            预览页面
          </a-button>
        </template>
        <WidgetVpage
          v-if="!navData.loading && !isEmpty(navData.definitionJson)"
          :widget="navData.definitionJson"
          style="height: 100%"
          :key="navData.wkey"
        />
      </template>
      <div style="text-align: center; position: absolute; top: 40%; left: 49%">
        <a-spin :spinning="navData.loading"></a-spin>
      </div>
    </a-card>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import '@installPageWidget';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件
import { isEmpty } from 'lodash';

export default {
  name: 'ModuleIndexNavContent',
  inject: ['currentModule', 'pageContext', 'getPageResources'],
  props: {
    navData: Object
  },
  components: {},
  computed: {},
  provide() {
    return {};
  },
  data() {
    return {};
  },

  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    isEmpty,
    previewModulePage() {
      window.open(`/webpage/${this.navData.definition.id}`, '_blank');
    }
  },
  watch: {}
};
</script>
