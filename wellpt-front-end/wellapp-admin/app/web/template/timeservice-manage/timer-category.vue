<template>
  <div>
    <CategoryModel
      name="计时服务分类"
      :modelData="model"
      :dataSource="dataSource"
      :rules="rules"
      :save="saveCategory"
      :delete="deleteCategory"
      @select="onSelect"
    >
      <template slot="formModelItemOfId" slot-scope="{ formData }">
        <a-form-model-item label="ID" prop="id">
          <a-input v-model="formData.id" :maxLength="50" :disabled="!!formData.uuid"></a-input>
        </a-form-model-item>
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
      model: {
        name: '',
        id: '',
        code: '',
        icon: '',
        iconColor: '',
        remark: ''
      },
      dataSource: [],
      rules: {
        name: { required: true, message: '不能为空', trigger: 'blur' },
        id: { required: true, message: '不能为空', trigger: 'blur' }
      }
    };
  },
  created() {
    this.loadCategories();
  },
  methods: {
    loadCategories(keyword = '', moduleId = '') {
      $axios.post(`/api/ts/timer/category/getAllBySystemUnitIdsLikeName`, { name: keyword, moduleId }).then(({ data: result }) => {
        if (result.data) {
          this.dataSource = result.data;
        }
      });
    },
    saveCategory(formData, close) {
      const _this = this;
      $axios
        .post(`/api/ts/timer/category/save`, formData)
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
        .post(`/proxy/api/ts/timer/category/deleteAll?uuids=${[formData.uuid]}`)
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
      this.pageContext.emitEvent('categorySelect_timer', selectedKeys, selectedKeysIncludeChildren);
    }
  }
};
</script>

<style></style>
