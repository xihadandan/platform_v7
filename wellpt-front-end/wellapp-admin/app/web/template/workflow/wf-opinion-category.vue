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
      replaceFields: { title: 'label', key: 'uuid', value: 'uuid' }
    };
  },
  created() {
    this.loadCategories();
  },
  methods: {
    loadCategories() {
      $axios.get('/proxy/api/datadict/getByCode/PT_BUSINESS_APP_CATEGORY').then(({ data: result }) => {
        if (result.data) {
          let items = result.data.items || [];
          this.dataSource = items;
        }
      });
    },
    onSelect(selectedKeys, selectedKeysIncludeChildren) {
      this.pageContext.emitEvent('categorySelect_wf_opinion_category', selectedKeys, selectedKeysIncludeChildren);
    }
  }
};
</script>

<style></style>
