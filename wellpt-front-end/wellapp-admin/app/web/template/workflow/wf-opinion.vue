<template>
  <CategoryModel
    name="行业应用分类"
    :dataSource="dataSource"
    :replaceFields="replaceFields"
    @select="onSelect"
    displayState="label"
    :showIcon="false"
  ></CategoryModel>
</template>

<script>
import CategoryModel from '../common/category-model.vue';
export default {
  components: { CategoryModel },
  inject: ['pageContext'],
  data() {
    return {
      dataSource: [],
      replaceFields: { title: 'name', key: 'id', value: 'id' }
    };
  },
  created() {
    this.loadCategories();
  },
  methods: {
    loadCategories() {
      $axios
        .post('/json/data/services', {
          serviceName: 'flowOpinionCategoryService',
          methodName: 'getFlowOpinionCategoryTreeByBusinessAppDataDic',
          args: JSON.stringify(['true'])
        })
        .then(({ data: result }) => {
          if (result.data) {
            this.dataSource = result.data.children || [];
          }
        });
    },
    onSelect(selectedKeys, selectedKeysIncludeChildren) {
      this.pageContext.emitEvent('categorySelect_wf_opinion', selectedKeys, selectedKeysIncludeChildren);
    }
  }
};
</script>

<style></style>
