<template>
  <HtmlWrapper :title="title">
    <DataModelDetail :metadata="metadata" v-if="!loading" />
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import DataModelDetail from './component/data-model/data-model-detail.vue';
export default {
  name: 'DataModelDesign',
  props: {},
  components: { DataModelDetail },
  computed: {},
  data() {
    return { title: '数据模型', loading: true, metadata: {}, currentModule: { id: undefined } };
  },
  provide() {
    return {
      currentModule: this.currentModule
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this.uuid != undefined || this.id != undefined) {
      this.getDataModelDetail(this.uuid, this.id).then(d => {
        this.loading = false;
        if (d != undefined) {
          this.metadata = d;
          this.currentModule.id = d.module;
        }
      });
    }
  },
  mounted() {},
  methods: {
    getDataModelDetail(uuid, id) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/dm/getDetails`, { params: { uuid, id } })
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {
            resolve(undefined);
          });
      });
    }
  }
};
</script>
