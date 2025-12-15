<template>
  <BizProcessDesigner
    ref="bizProcessDesigner"
    v-if="designerLoaded"
    :initProcessDefUuid="processDefUuid"
    :initProcessDefinition="processDefinitionJson"
    :headerStyle="headerStyle"
    :showTitleIcon="false"
  ></BizProcessDesigner>
</template>

<script>
export default {
  inject: ['assemble'],
  data() {
    return {
      designerLoaded: false,
      processDefUuid: this.assemble.processDefinition.uuid,
      processDefinitionJson: this.assemble.processDefinitionJson,
      headerStyle: {
        // backgroundColor: '#ebeef3',
        // color: '#000'
        height: '52px',
        display: 'none'
      }
    };
  },
  created() {
    if (EASY_ENV_IS_BROWSER) {
      import('../../../process-designer').then(BizProcessDesigner => {
        Vue.component('BizProcessDesigner', BizProcessDesigner.default);
        this.designerLoaded = true;
      });
    }
  },
  methods: {
    setBuildWay(buildWay) {
      this.$refs.bizProcessDesigner.designer.setBuildWay(buildWay);
    },
    save() {
      this.$refs.bizProcessDesigner.saveProcessDefinition();
    },
    saveAsNewVersion() {
      this.$refs.bizProcessDesigner.saveAsNewVersion();
    },
    getProcessDesigner() {
      return this.$refs.bizProcessDesigner.designer;
    }
  }
};
</script>

<style lang="less" scoped></style>
