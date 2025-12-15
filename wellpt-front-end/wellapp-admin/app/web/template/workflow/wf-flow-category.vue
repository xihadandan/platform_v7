<template>
  <div>
    <CategoryModel
      name="流程分类"
      applyTo="flowCategory"
      :modelData="model"
      :dataSource="dataSource"
      :rules="rules"
      :displayState="displayState"
      :save="saveCategory"
      :delete="deleteCategory"
      @select="onSelect"
    ></CategoryModel>
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
      model: {
        name: '',
        code: '',
        icon: '',
        iconColor: '',
        remark: ''
      },
      dataSource: [],
      rules: {
        name: { required: true, message: '不能为空', trigger: 'blur' },
        code: { required: true, message: '不能为空', trigger: 'blur' }
      }
    };
  },
  computed: {
    displayState() {
      if (this.$parent && this.$parent.widget && 'dev_ops_category' == this.$parent.widget.configuration.code) {
        return 'label';
      }
      return 'edit';
    }
  },
  created() {
    this.loadCategories();
  },
  methods: {
    loadCategories(keyword = '', moduleId = '') {
      $axios.post(`/api/workflow/category/getAllBySystemUnitIdsLikeName`, { name: keyword, moduleId }).then(({ data: result }) => {
        if (result.data) {
          this.dataSource = result.data;
        }
      });
    },
    saveCategory(formData, close) {
      const _this = this;
      $axios
        .post(`/api/workflow/category/save`, formData)
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
        .post(`/proxy/api/workflow/category/deleteAll?uuids=${[formData.uuid]}`)
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
    onSelect(selectedKeys, selectedKeysIncludeChildren, e) {
      // console.log('selectedKeys', selectedKeys, selectedKeysIncludeChildren);
      let sourceCode = this.$parent.widget.configuration.code;
      if (sourceCode == 'dev_ops_category') {
        this.pageContext.emitEvent('devOpsCategorySelect', selectedKeys, selectedKeysIncludeChildren, sourceCode, e);
      } else {
        this.pageContext.emitEvent('categorySelect_workflow', selectedKeys, selectedKeysIncludeChildren, sourceCode, e);
      }
    }
  }
};
</script>

<style></style>
