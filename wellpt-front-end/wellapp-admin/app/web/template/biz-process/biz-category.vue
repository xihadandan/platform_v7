<template>
  <div>
    <div v-if="displayState == 'edit'">
      <a-button @click="onAddCategoryClick">
        <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
        分类
      </a-button>
      <a-button @click="onAddBusinessClick">
        <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
        业务
      </a-button>
    </div>
    <CategoryModel
      ref="categoryModel"
      name="业务分类"
      :allowCreate="false"
      :showIcon="false"
      :displayState="displayState"
      :modelData="model"
      :modalTypeNames="modalTypeNames"
      :dataSource="dataSource"
      :rules="rules"
      :save="saveCategory"
      :delete="deleteCategory"
      @select="onSelect"
      @modalAdd="onModalAdd"
      @modalEdit="onModalEdit"
    ></CategoryModel>
  </div>
</template>

<script>
import CategoryModel from '../common/category-model.vue';
export default {
  components: {
    CategoryModel
  },
  inject: ['pageContext', 'namespace'],
  data() {
    return {
      model: {
        name: '',
        id: '',
        code: '',
        remark: ''
      },
      dataSource: [],
      rules: {
        name: { required: true, message: '不能为空', trigger: 'blur' },
        id: { required: true, message: '不能为空', trigger: 'blur' }
      },
      modalTypeNames: { category: '分类', business: '业务' },
      modalType: 'category'
    };
  },
  computed: {
    displayState() {
      return this.$parent.widget.configuration.code == 'process' ? 'edit' : 'label';
    }
  },
  created() {
    this.loadCategories();
  },
  methods: {
    loadCategories(keyword = '', moduleId = '') {
      $axios.get(`/proxy/api/biz/category/getCategoryAndBusinessTree`, { name: keyword, moduleId }).then(({ data: result }) => {
        let treeNode = result.data || [];
        if (treeNode) {
          this.dataSource = this.treeDataAsList(treeNode.children);
        }
      });
    },
    treeDataAsList(treeNodes = []) {
      let dataList = [];
      let extractTreeData = (children, parent) => {
        children.forEach(node => {
          let treeData = node.data;
          treeData.type = node.type;
          if (node.type == 'business' && parent) {
            treeData.parentUuid = parent.id;
          }
          dataList.push(treeData);
          if (node.children) {
            extractTreeData(node.children, node);
          }
        });
      };
      extractTreeData(treeNodes, null);
      return dataList;
    },
    saveCategory(formData, close) {
      const _this = this;
      $axios
        .post(`/proxy/api/biz/${formData.type}/save`, formData)
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
        .post(`/proxy/api/biz/${formData.type}/deleteAll?uuids=${[formData.uuid]}`)
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
    onSelect(selectedKeys, selectedKeysIncludeChildren, option) {
      // console.log('selectedKeys', selectedKeys, selectedKeysIncludeChildren);
      let categoryUuid = '';
      let businessId = '';
      if (option.selected) {
        categoryUuid = option.node.dataRef.type == 'category' ? option.node.dataRef.uuid : '';
        businessId = option.node.dataRef.type == 'business' ? option.node.dataRef.id : '';
        this.selectedData = option.node.dataRef;
      } else {
        this.selectedData = null;
      }
      this.pageContext.emitEvent('categorySelect_biz', selectedKeys, selectedKeysIncludeChildren, categoryUuid, businessId);
    },
    onAddCategoryClick() {
      const _this = this;
      _this.modalType = 'category';
      _this.$refs.categoryModel.onAddClick();
    },
    onAddBusinessClick() {
      const _this = this;
      if (_this.selectedData == null || _this.selectedData.type != 'category') {
        _this.$message.error('请选择业务分类');
        return;
      }
      _this.modalType = 'business';
      _this.$refs.categoryModel.onAddClick();
    },
    onModalAdd(modal, formData) {
      const _this = this;
      if (_this.modalType == 'category') {
        modal.title = '新增分类';
        formData.parentUuid = _this.selectedData && _this.selectedData.uuid;
      } else {
        modal.title = '新增业务';
        formData.categoryUuid = _this.selectedData.uuid;
      }
      formData.type = _this.modalType;
    },
    onModalEdit(modal, formData) {
      if (formData.type == 'business') {
        modal.title = formData.name;
      }
    }
  }
};
</script>

<style></style>
