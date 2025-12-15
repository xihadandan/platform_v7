<template>
  <div class="data-dictionary-nav">
    <a-row type="flex" style="margin-bottom: 12px" class="f_nowrap">
      <a-col flex="auto" v-if="title">
        <div class="data-dictionary-nav">{{ title }}</div>
      </a-col>
      <a-col flex="auto" v-if="showSearch">
        <a-input-search placeholder="请输入关键字" v-model="searchCategoryText" @search="onSearchCategory" allow-clear />
      </a-col>
      <a-col flex="44px" v-if="allowCreate" style="text-align: right">
        <Modal
          title="新建字典分类"
          :destroyOnClose="true"
          :ok="e => confirmCreateCategory(e)"
          :width="600"
          :maxHeight="500"
          dialogClass="pt-modal"
        >
          <a-button class="icon-only" title="新建字典分类"><Icon type="pticon iconfont icon-ptkj-jiahao"></Icon></a-button>
          <template slot="content">
            <DataDictionaryCategoryForm ref="newData" :formData="categoryFormData"></DataDictionaryCategoryForm>
          </template>
        </Modal>
      </a-col>
    </a-row>
    <a-spin :spinning="categoryLoading" style="min-height: 150px">
      <PerfectScrollbar :style="{ height: height }">
        <div class="category-container">
          <a-menu
            v-if="categories && categories.length > 0"
            :defaultOpenKeys="['all']"
            mode="inline"
            v-model="selectedCategoryUuid"
            @click="onCategoryClick"
            @openChange="onCategoryRootClick"
          >
            <a-sub-menu key="all">
              <template slot="title">
                <span>全部</span>
              </template>
              <a-menu-item
                v-for="category in categories"
                :key="category.uuid"
                :class="['btn-ghost-container', selectedCategoryUuid == category.uuid ? 'btn-hover' : '']"
              >
                <a-row type="flex">
                  <a-col flex="auto">{{ category.name }}</a-col>
                  <a-col flex="32px">
                    <a-dropdown :trigger="['click']">
                      <a-button ghost type="link" @click.stop="() => {}" class="icon-only" size="small" title="更多操作">
                        <Icon type="pticon iconfont icon-ptkj-gengduocaozuo"></Icon>
                      </a-button>
                      <a-menu slot="overlay">
                        <a-menu-item key="edit">
                          <Modal title="编辑字典分类" :ok="e => confirmEditCategory(e, category)" :width="600" :maxHeight="500">
                            编辑
                            <template slot="content">
                              <DataDictionaryCategoryForm
                                :formData="category"
                                :ref="'editData_' + category.uuid"
                              ></DataDictionaryCategoryForm>
                            </template>
                          </Modal>
                        </a-menu-item>
                        <a-menu-item key="delete" @click="onDeleteCategoryClick(category)">删除</a-menu-item>
                      </a-menu>
                    </a-dropdown>
                  </a-col>
                </a-row>
              </a-menu-item>
            </a-sub-menu>
          </a-menu>
          <div v-else>
            <a-empty description="无分类" />
          </div>
        </div>
      </PerfectScrollbar>
    </a-spin>
  </div>
</template>

<script>
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import DataDictionaryCategoryForm from './data-dictionary-category-form.vue';
import { isEmpty, trim } from 'lodash';
export default {
  props: {
    type: {
      String,
      default: 'module' // 字典分类类型，buildIn、module
    },
    showSearch: {
      type: Boolean,
      default: true
    },
    title: {
      type: String,
      default: ''
    },
    // 是否允许创建分类
    allowCreate: {
      type: Boolean,
      default: true
    },
    height: {
      type: String,
      default: 'calc(100vh - 260px)'
    }
  },
  components: { Modal, DataDictionaryCategoryForm },
  inject: ['currentModule'],
  data() {
    return {
      categoryLoading: true,
      searchCategoryText: '',
      categories: [],
      categoryFormData: { type: 1 },
      selectedCategoryUuid: null
    };
  },
  created() {
    this.initCategories = [];
    this.loadCategories();
  },
  methods: {
    loadCategories() {
      let url = '';
      if (this.type == 'buildIn') {
        url = `/proxy/api/datadict/category/listByType/buildIn`;
      } else {
        url = `/proxy/api/datadict/category/list?moduleId=${this.currentModule.id}`;
      }
      $axios.get(url).then(({ data }) => {
        if (data.data) {
          this.categories = data.data;
          this.initCategories = data.data;
          this.$emit('load', data.data);
        }
        this.categoryLoading = false;
      });
    },
    onCategoryClick({ key }) {
      if (this.selectedCategoryUuid != key) {
        this.selectedCategoryUuid = key;
      } else {
        this.selectedCategoryUuid = null;
      }
      this.$emit('click', this.selectedCategoryUuid);
    },
    onCategoryRootClick() {
      this.selectedCategoryUuid = null;
      this.$emit('click', null);
    },
    onSearchCategory() {
      let searchText = trim(this.searchCategoryText);
      if (isEmpty(searchText)) {
        this.categories = this.initCategories;
      } else {
        this.categories = this.initCategories.filter(item => item.name && item.name.indexOf(searchText) != -1);
      }
    },
    onDeleteCategoryClick(category) {
      const _this = this;
      _this.$confirm({
        title: '提示',
        content: `确认删除字典分类——${category.name}？`,
        onOk() {
          $axios
            .post(`/proxy/api/datadict/category/delete?uuid=${category.uuid}`)
            .then(({ data }) => {
              if (data.code == 0) {
                // 刷新表格
                _this.loadCategories();
                _this.onCategoryRootClick();
                _this.$message.success('删除成功！');
              }
            })
            .catch(({ response }) => {
              if (response.data && response.data.msg) {
                _this.$message.error(response.data.msg);
              } else {
                _this.$message.error('删除失败！');
              }
            });
        }
      });
    },
    confirmCreateCategory(e) {
      if (!this.categoryFormData.moduleId) {
        this.categoryFormData.moduleId = this.currentModule.id;
      }
      this.$refs['newData'].validate().then(() => {
        this.saveCategory(e, this.categoryFormData);
      });
    },
    confirmEditCategory(e, category) {
      this.$refs[`editData_${category.uuid}`][0].validate().then(() => {
        this.saveCategory(e, category);
      });
    },
    saveCategory(e, category) {
      $axios.post('/proxy/api/datadict/category/save', category).then(({ data }) => {
        if (data.data) {
          e(true);
          this.categoryFormData = { type: 1 };
          this.$message.success('保存成功！');
          this.loadCategories();
        }
      });
    }
  }
};
</script>

<style lang="less">
.data-dictionary-nav {
  .data-dictionary-nav {
    font-size: var(--w-font-size-base);
    color: var(--w-text-color-dark);
    font-weight: bold;
    line-height: 32px;
  }

  .category-container {
    .ant-menu-item {
      padding-right: 0;
      height: 40px;
      line-height: 40px;
    }
    .ant-menu-submenu-arrow {
      left: 5px;
    }
  }
}
</style>
