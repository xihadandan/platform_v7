<template>
  <a-card size="small" :bordered="false">
    <template slot="title">
      {{ assemble.processDefinitionJson.name }}
      <label style="color: rgba(0, 0, 0, 0.45)">v{{ assemble.processDefinitionJson.version }}</label>
    </template>
    <template slot="extra">
      <a-button @click="openBizProcessDesigner" size="small" type="link">
        <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
        编辑业务流程
      </a-button>
    </template>
    <div id="desinger-container">desinger-container</div>
  </a-card>
</template>

<script>
import { register } from '@antv/x6-vue-shape';
import ProcessNode from '../../../process-designer/design-compoment/process-node.vue';
import ProcessItem from '../../../process-designer/design-compoment/process-item.vue';
export default {
  props: {
    designer: Object
  },
  inject: ['assemble'],
  created() {
    this.registerVueCompoment();
  },
  mounted() {
    this.designer.init(this.assemble.processDefinitionJson);
    this.$el.querySelector('#desinger-container').style.height = 'calc(100vh - 110px)';
  },
  methods: {
    registerVueCompoment() {
      const _this = this;
      register({
        shape: 'process-node',
        width: 100,
        height: 100,
        component: {
          provide() {
            return { designer: _this.designer };
          },
          render: h => h(ProcessNode)
        }
      });
      register({
        shape: 'process-item',
        width: 100,
        height: 100,
        component: {
          provide() {
            return { designer: _this.designer };
          },
          render: h => h(ProcessItem)
        }
      });
    },
    openBizProcessDesigner() {
      const _this = this;
      let processDefUuid = _this.assemble.processDefinition.uuid;
      let url = `/web/app/pt-mgr/mod_biz_mgr/app_biz_process_def.html?pageUuid=5064e358-b070-4edb-8a39-e2359050bae5&processDefUuid=${processDefUuid}&designer=process`;
      window.open(url);
    }
  }
};
</script>

<style lang="less" scoped>
#desinger-container {
  height: 100%;
}
</style>
