<template>
  <div>
    <CategoryModel
      applyTo="snSerialNumberCategory"
      name="流水号分类"
      :dataSource="dataSource"
      :save="saveCategory"
      :delete="deleteCategory"
      @select="onSelect"
    >
      <template slot="formModelItemOfParentUuid">
        <span></span>
      </template>
    </CategoryModel>
  </div>
</template>

<script>
import CategoryModel from '../common/category-model.vue';
export default {
  components: {
    CategoryModel
  },
  inject: ['pageContext'],
  data() {
    return {
      dataSource: []
    };
  },
  created() {
    this.loadCategories();
  },
  methods: {
    loadCategories(keyword = '', moduleId = '') {
      $axios.post(`/proxy/api/sn/serial/number/category/getAllBySystemUnitIdsLikeName`, { name: keyword }).then(({ data: result }) => {
        if (result.data) {
          this.dataSource = result.data;
        }
      });
    },
    saveCategory(formData, close) {
      const _this = this;
      $axios
        .post(`/proxy/api/sn/serial/number/category/save`, formData)
        .then(({ data: result }) => {
          if (result.success) {
            close(true);
            _this.loadCategories();
            _this.$message.success('保存成功');
          } else {
            _this.$message.error(result.msg || '保存失败');
          }
        })
        .catch(({ response }) => {
          _this.$message.error((response && response.data && response.data.msg) || '保存失败');
        });
    },
    deleteCategory(formData) {
      const _this = this;
      $axios
        .post(`/proxy/api/sn/serial/number/category/deleteWhenNotUsed?uuid=${formData.uuid}`)
        .then(({ data: result }) => {
          if (result.code == 0) {
            this.loadCategories();
            _this.$message.success('删除成功');
          } else {
            _this.$message.error(result.msg || '删除失败');
          }
        })
        .catch(({ response }) => {
          _this.$message.error((response && response.data && response.data.msg) || '删除失败');
        });
    },
    onSelect(selectedKeys, selectedKeysIncludeChildren) {
      // console.log('selectedKeys', selectedKeys, selectedKeysIncludeChildren);
      this.pageContext.emitEvent('categorySelect_serial_number', selectedKeys, selectedKeysIncludeChildren);
    }
  }
};
</script>

<style></style>
