<template>
  <div>
    <a-form-model-item label="数据字典">
      <a-tree-select
        showSearch
        allowClear
        v-model="options.dataDic"
        treeNodeFilterProp="title"
        :replaceFields="{ title: 'name', value: 'id', key: 'id' }"
        :tree-data="dataDictionaryTreeData"
        style="width: 100%"
        :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
      ></a-tree-select>
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'dataDictionaryRendererConfig',
  mixins: [],
  props: {
    options: Object
  },
  data() {
    return {
      dataDictionaryTreeData: []
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    if (this.options.dataDic == undefined) {
      this.$set(this.options, 'dataDic', undefined);
    }
  },
  methods: {
    fetchDataDictionaryTreeData() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'cdDataDictionaryFacadeService',
          methodName: 'getAllDataDictionaryAsCategoryTree'
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.dataDictionaryTreeData = data.data.children;
            _this.dataDictionaryTreeData.forEach(node => {
              node.selectable = !node.nocheck;
              node.checkable = false;
              // if (node.children) {
              //   for (let c of node.children) {
              //     c.scopedSlots = { title: 'nodeTitleSlot' };
              //   }
              // }
            });
          }
        });
    }
  },
  mounted() {
    this.fetchDataDictionaryTreeData();
  }
};
</script>
