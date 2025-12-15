<template>
  <a-card size="small" :bordered="false">
    <template slot="title">
      {{ assemble.processDefinitionJson.name }}
      <label style="color: rgba(0, 0, 0, 0.45)">v{{ assemble.processDefinitionJson.version }}</label>
    </template>
    <template slot="extra">
      <a-button @click="openBizProcessDesigner" size="small" type="link">
        <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
        编辑事项流
      </a-button>
    </template>
    <div id="item-flow-desinger-container">item-flow-desinger-container</div>
  </a-card>
</template>

<script>
export default {
  props: {
    designer: Object
  },
  inject: ['assemble'],
  mounted() {
    this.designer.init(this.assemble.processDefinitionJson);
    this.$el.querySelector('#item-flow-desinger-container').style.height = 'calc(100vh - 110px)';
  },
  methods: {
    openBizProcessDesigner() {
      const _this = this;
      let processDefUuid = _this.assemble.processDefinition.uuid;
      let url = `/web/app/pt-mgr/mod_biz_mgr/app_biz_process_def.html?pageUuid=5064e358-b070-4edb-8a39-e2359050bae5&processDefUuid=${processDefUuid}&designer=itemFlow`;
      window.open(url);
    }
  }
};
</script>

<style lang="less" scoped>
#item-flow-desinger-container {
  height: 100%;
}
</style>
