<template>
  <a-select
    v-model="selectedKey"
    @change="onChange"
    allow-clear
    showSearch
    :open="open"
    @dropdownVisibleChange="dropdownVisibleChange"
    :filterOption="filterOption"
    :class="['app-category-select', loading ? 'loading' : '']"
    :getPopupContainer="popupContainer"
    dropdownClassName="app-category-select-dropdown"
  >
    <div slot="dropdownRender" slot-scope="menu" class="app-category-dropdown-content">
      <div style="padding: 4px 8px; cursor: pointer; text-align: right" v-if="enableAdd">
        <a-button
          @mousedown="e => e.preventDefault()"
          size="small"
          type="link"
          icon="plus"
          @click.stop="addCategory"
          v-show="!newCategoryInputVisible"
        >
          新增分类
        </a-button>
        <a-input
          placeholder="请输入分类名称"
          :maxLength="64"
          size="small"
          style="width: 100%"
          v-model.trim="categoryName"
          v-show="newCategoryInputVisible"
          @focus="onFocusInput"
        >
          <template slot="suffix">
            <a-icon title="保存" :type="saveLoading ? 'loading' : 'save'" @click="saveCategory" style="color: var(--w-primary-color)" />
          </template>
        </a-input>
        <a-divider style="margin: 4px 0" />
      </div>
      <v-nodes :vnodes="menu" />
    </div>
    <a-select-option v-for="(item, i) in categories" :key="item.uuid" :value="item.uuid">
      {{ item.name }}
      <a-button
        v-if="enableAdd"
        v-show="open"
        class="select-option-button"
        size="small"
        type="link"
        :icon="item.deleteLoading ? 'loading' : 'delete'"
        title="删除分类"
        style="float: right"
        @click.stop="deleteCategory(item, i)"
      />
    </a-select-option>
  </a-select>
</template>
<style lang="less">
.app-category-select {
  .ant-select-selection-selected-value {
    .select-option-button {
      opacity: 0;
    }
  }
  &.loading {
    .ant-select-selection-selected-value {
      opacity: 0 !important;
    }
  }
}
.app-category-select-dropdown {
  .select-option-button {
    float: right;
  }
}
</style>
<script type="text/babel">
export default {
  name: 'AppCategorySelect',
  props: {
    value: String,
    applyTo: {
      type: String,
      required: true
    },
    enableAdd: Boolean,
    enableDelete: Boolean,
    popupContainer: Function
  },
  components: {
    VNodes: {
      functional: true,
      render: (h, ctx) => ctx.props.vnodes
    }
  },
  computed: {},
  data() {
    return {
      open: false,
      categoryName: undefined,
      categories: [],
      loading: this.value != undefined,
      saveLoading: false,
      newCategoryInputVisible: false,
      focusInput: false,
      selectedKey: this.value != undefined ? this.value : undefined
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchCategories();
  },
  mounted() {},
  methods: {
    onFocusInput() {
      this.focusInput = true;
    },
    deleteCategory(item, i) {
      let _this = this;
      this.$confirm({
        title: '提示',
        content: '确定要删除吗?',
        okText: '确定',
        cancelText: '取消',
        getContainer: this.popupContainer,
        onOk() {
          _this.$set(item, 'deleteLoading', true);
          $axios
            .get(`/proxy/api/app/category/delete/${item.uuid}`, { params: {} })
            .then(({ data }) => {
              if (data.code == 0) {
                if (_this.selectedKey == item.uuid) {
                  _this.selectedKey = undefined;
                }
                _this.categories.splice(i, 1);
              } else {
                _this.$set(item, 'deleteLoading', false);
              }
            })
            .catch(error => {
              _this.$set(item, 'deleteLoading', false);
            });
        },
        onCancel() {}
      });
    },
    saveCategory() {
      if (this.categoryName) {
        this.saveLoading = true;
        $axios.post(`/proxy/api/app/category/save`, { name: this.categoryName, applyTo: this.applyTo }).then(({ data }) => {
          this.saveLoading = false;
          if (data.code == 0) {
            this.categories.splice(0, 0, {
              uuid: data.data,
              name: this.categoryName
            });
            this.categoryName = undefined;
          }
        });
      }
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    dropdownVisibleChange(open) {
      if (!this.focusInput) {
        this.open = open;
      }
    },
    addCategory() {
      this.newCategoryInputVisible = true;
      this.open = true;
    },
    onChange() {
      this.open = false;
      this.focusInput = false;
      this.$emit('input', this.selectedKey);
    },

    fetchCategories() {
      $axios
        .get(`/proxy/api/app/category/getAll`, { params: { applyTo: this.applyTo } })
        .then(({ data }) => {
          this.loading = false;
          if (data.code == 0) {
            this.categories = data.data;
            if (this.selectedKey != undefined) {
              for (let c of this.categories) {
                if (c.uuid == this.selectedKey) {
                  return;
                }
              }
              this.selectedKey = undefined;
            }
          }
        })
        .catch(error => {});
    }
  }
};
</script>
