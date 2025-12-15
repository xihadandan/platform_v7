<template>
  <a-card :bordered="false" class="preview-card">
    <template slot="title">
      <span class="title">{{ title }}</span>
    </template>
    <div class="spin-center" v-if="loading">
      <a-spin />
    </div>
    <a-empty :image="emptyImg" v-if="emptyDesign" style="padding-top: 240px"></a-empty>
    <PerfectScrollbar style="height: calc(100vh - 124px); z-index: 1" v-else>
      <div style="width: 100%; height: 100%; display: block; position: fixed; z-index: 1"></div>
    </PerfectScrollbar>
  </a-card>
</template>
<style lang="less"></style>
<script type="text/babel">
import { orderBy, debounce } from 'lodash';
import { Empty } from 'ant-design-vue';
export default {
  name: 'MetadataPreview',
  inject: ['pageContext', 'currentModule'],
  props: {
    metadata: Object
  },
  provide() {
    return {};
  },
  computed: {},
  data() {
    return {
      emptyImg: Empty.PRESENTED_IMAGE_SIMPLE,
      title: this.metadata.title,
      emptyDesign: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  beforeDestroy() {},
  methods: {}
};
</script>
