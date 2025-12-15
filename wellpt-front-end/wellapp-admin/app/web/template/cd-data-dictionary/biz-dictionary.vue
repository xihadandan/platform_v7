<template>
  <div class="biz-dictionary">
    <a-row type="flex" class="f_nowrap template-widget-row">
      <a-col class="sidebar widget-col">
        <div class="col-content">
          <div style="margin-bottom: 12px">
            <a-input-search placeholder="搜索" v-model="searchModuleText" allowClear @search="onSearchModuleTree" />
          </div>
          <div class="tree-content" style="margin-bottom: 8px">
            <div class="title">字典归属</div>
            <PerfectScrollbar style="height: calc(100% - 32px)">
              <a-tree
                ref="moduleTree"
                :tree-data="treeData"
                show-icon
                default-expand-all
                :replaceFields="{ title: 'name', key: 'id' }"
                @select="moduleTreeSelected"
                class="ant-tree-directory"
              ></a-tree>
              <a-empty v-if="searchModuleText && treeData.length == 0" class="pt-search-empty" description="无匹配数据" />
            </PerfectScrollbar>
          </div>
          <div class="tree-content">
            <DataDictionaryCategoryNav
              @click="onCategoryClick"
              @load="onCategoryLoad"
              title="字典分类"
              :height="'calc(100% - 32px)'"
              :showSearch="false"
              ref="dataDictionaryCategoryNavRef"
            ></DataDictionaryCategoryNav>
          </div>
        </div>
      </a-col>
      <a-col flex="auto" class="widget-col" style="width: 0">
        <DataDictionaryList
          ref="dataDictionaryList"
          moduleSource="all"
          :supportsGeneric="true"
          :categoryUuid="selectedCategoryUuid"
          :categories="categories"
          :queryParams="queryParams"
        ></DataDictionaryList>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import DataDictionaryCategoryNav from '@pageAssembly/app/web/page/module-center/component/metadata/data-dictionary/data-dictionary-category-nav.vue';
import DataDictionaryList from '@pageAssembly/app/web/page/module-center/component/metadata/data-dictionary/data-dictionary-list.vue';
import { trim, isEmpty } from 'lodash';
import { getElSpacingForTarget } from '@framework/vue/utils/util';
export default {
  components: { DataDictionaryCategoryNav, DataDictionaryList },
  provide() {
    return {
      currentModule: { id: '' }
    };
  },
  data() {
    return {
      searchModuleText: '',
      queryParams: {},
      treeData: [],
      categories: [],
      selectedCategoryUuid: ''
    };
  },
  created() {
    this.initTreeData = [];
    this.loadModuleTree();
  },
  mounted() {
    this.changeTableStyle();
  },
  methods: {
    loadModuleTree() {
      $axios
        .post('/json/data/services', {
          serviceName: 'appProductIntegrationMgr',
          methodName: 'getTreeByDataType',
          args: JSON.stringify([['0', '1', '2']])
        })
        .then(({ data }) => {
          if (data.data) {
            this.treeData = data.data.children || [];
            this.initTreeData = this.treeData;
          } else {
            reject(data);
          }
        })
        .catch(res => {
          reject(res);
        });
    },
    moduleTreeSelected(selectedKeys) {
      if (selectedKeys.length === 0) {
        this.selectedTreeNode = {};
      } else {
        this.selectedTreeNode = this.findTreeNodeByKey(selectedKeys[0]);
      }
      this.selectedCategoryUuid = '';
      this.queryParams = {};
      if (this.selectedTreeNode && this.selectedTreeNode.data) {
        let nodeData = this.selectedTreeNode.data;
        if (nodeData.type == 0) {
          this.queryParams.productUuid = nodeData.uuid;
          this.queryParams.productId = nodeData.id;
        } else if (nodeData.type == 1) {
          this.queryParams.systemUuid = nodeData.uuid;
          this.queryParams.systemId = nodeData.id;
        } else if (nodeData.type == 2) {
          this.queryParams.moduleUuid = nodeData.uuid;
          this.queryParams.moduleId = nodeData.id;
        }
      }
      this.$nextTick(() => {
        this.$refs.dataDictionaryList.refresh();
      });
    },
    findTreeNodeByKey(key) {
      let treeNode = null;
      let findTreeNode = nodes => {
        if (treeNode) {
          return;
        }
        nodes.forEach(node => {
          if (node.id == key) {
            treeNode = node;
            return;
          }
          findTreeNode(node.children || []);
        });
      };
      findTreeNode(this.treeData);

      return treeNode;
    },
    onCategoryClick(categoryUuid) {
      this.queryParams = {};
      this.selectedCategoryUuid = categoryUuid;
      this.$nextTick(() => {
        this.$refs.dataDictionaryList.refresh();
      });
    },
    onCategoryLoad(categories) {
      this.categories = categories;
    },
    onSearchModuleTree() {
      let searchText = trim(this.searchModuleText);
      if (isEmpty(searchText)) {
        this.treeData = this.initTreeData;
      } else {
        let searchNodes = [];
        let searchTreeNode = nodes => {
          nodes.forEach(node => {
            if (node.name && node.name.indexOf(searchText) != -1) {
              searchNodes.push(node);
            }
            if (node.children) {
              searchTreeNode(node.children);
            }
          });
        };
        searchTreeNode(this.initTreeData);
        this.treeData = searchNodes;
      }
      this.$refs.dataDictionaryCategoryNavRef.searchCategoryText = searchText;
      this.$refs.dataDictionaryCategoryNavRef.onSearchCategory();
    },
    changeTableStyle() {
      if (this.$el.closest('.grid-in-layout-widget')) {
        this.$root.pageContext.emitEvent(`SET_TEMPLATE_WIDGET-ROW-COL-HEIGHT`, {
          callback: () => {
            this.$nextTick(() => {
              setTimeout(() => {
                let $el = this.$refs.dataDictionaryList.$el;
                let { maxHeight } = getElSpacingForTarget($el, this.$el);
                if (maxHeight) {
                  maxHeight = maxHeight;
                  $el.style.cssText += `height:${maxHeight}px;`;
                }
              }, 200);
            });
          }
        });
      }
    }
  }
};
</script>

<style lang="less" scoped>
.biz-dictionary {
  .sidebar {
    width: 240px;
    .col-content {
      padding: 12px 20px;
      height: 100%;
    }
  }
  .tree-content {
    height: e('calc(50% - 28px)');
    .title {
      font-size: var(--w-font-size-base);
      color: var(--w-text-color-dark);
      font-weight: bold;
      line-height: 32px;
    }
    ::v-deep .ant-tree-directory {
      .ant-tree-title {
        display: -webkit-box;
        overflow: hidden;
        white-space: normal !important;
        text-overflow: ellipsis;
        word-wrap: break-word;
        -webkit-line-clamp: 1;
        -webkit-box-orient: vertical;
        word-break: break-all;
      }
    }
    ::v-deep .data-dictionary-nav {
      height: 100%;
      .ant-spin-nested-loading {
        height: e('calc(100% - 30px)');
        .ant-spin-container {
          height: 100%;
        }
      }
    }
  }
}
</style>
