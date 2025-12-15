<template>
  <PerfectScrollbar :style="{ height: '350px' }">
    <a-spin :tip="$t('WorkflowWork.loading', '加载中')" :spinning="loading">
      <a-input-search
        v-if="showSearch"
        :placeholder="$t('WorkflowWork.searchPlaceholder', '搜索')"
        style="width: 100%"
        @search="onSearch"
      />
      <a-tree
        :class="['ant-tree-directory']"
        :load-data="onLoadData"
        :tree-data="treeData"
        :replaceFields="replaceFields"
        :defaultExpandedKeys="defaultExpandedKeys"
        :expandedKeys.sync="expandedKeys"
        :blockNode="true"
        :showIcon="true"
        :multiple="multiple"
        @select="onFolderTreeSelect"
      >
        <a-icon slot="switcherIcon" type="down" />
        <template slot="title" slot-scope="node">
          <a-icon v-if="node.expanded && !node.isLeaf" type="folder-open"></a-icon>
          <a-icon v-else type="folder"></a-icon>
          <span>{{ node.name }}</span>
        </template>
      </a-tree>
      <a-empty v-if="searchValue && !treeData.length" :description="$t('WorkflowWork.noData', '暂无数据')"></a-empty>
    </a-spin>
  </PerfectScrollbar>
</template>

<script>
import { deepClone } from '@framework/vue/utils/util';
import { isEmpty, trim } from 'lodash';
export default {
  name: 'FolderTreeSelect',
  props: {
    folderUuid: String,
    excludeFolderUuids: {
      type: Array,
      default: []
    },
    chooseFolderTip: String,
    mode: {
      type: String,
      default: 'default' // default,multiple
    },
    showSearch: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      loading: false,
      treeData: [],
      replaceFields: { title: 'name', key: 'id', value: 'id' },
      defaultExpandedKeys: [this.folderUuid],
      expandedKeys: [],
      selectedFolder: {},
      selectedFolders: [],
      searchValue: ''
    };
  },
  computed: {
    multiple() {
      return this.mode == 'multiple';
    }
  },
  beforeMount() {
    const _this = this;
    if (this.chooseFolderTip === undefined) {
      this.chooseFolderTip = this.$t('WorkflowWork.message.pleaseSelectFolder', '请选择夹！');
    }
    if (_this.folderUuid) {
      _this.initFolderTree();
    }
  },
  methods: {
    initFolderTree() {
      this.asyncLoadTree().then(({ data: result }) => {
        if (result.data) {
          this.treeData = this.convertAsyncTreeData(result.data);
          // 默认展开第一个节点
          if (this.treeData.length) {
            this.expandedKeys = [this.treeData[0].id];
          }
        }
        this.selectedFolder = {};
      });
    },
    asyncLoadTree(parentFolderUuid, keyword) {
      return $axios({
        method: 'POST',
        url: '/proxy/api/dms/file/manager/load/folder/tree',
        params: {
          belongToFolderUuid: this.folderUuid,
          parentFolderUuid: parentFolderUuid,
          checkIsParent: true, // 异步加载，检测是否父节点
          keyword
        },
        headers: { 'content-type': 'application/x-www-form-urlencoded' }
      });
    },
    convertAsyncTreeData(treeData) {
      const _this = this;
      let traverseTree = nodes => {
        if (!nodes || !nodes.length) {
          return;
        }
        nodes.forEach(node => {
          // 不可选择的节点
          if (_this.excludeFolderUuids.includes(node.id)) {
            node.selectable = false;
            node.isLeaf = true;
          } else {
            node.isLeaf = !node.isParent;
          }

          if (node.isParent && node.children && !node.children.length) {
            delete node.children;
          }
          traverseTree(node.children);
        });
      };

      traverseTree(treeData);
      return treeData;
    },
    onLoadData(treeNode) {
      return new Promise(resolve => {
        if (treeNode.dataRef.children) {
          resolve();
          return;
        }

        this.asyncLoadTree(treeNode.dataRef.id).then(({ data: result }) => {
          if (result.data) {
            treeNode.dataRef.children = this.convertAsyncTreeData(result.data);
          }
          this.treeData = [...this.treeData];
          resolve();
        });
      });
    },
    onFolderTreeSelect(selectedKeys, e) {
      const _this = this;
      if (_this.multiple) {
        _this.selectedFolders = e.selectedNodes.map(node => ({ uuid: node.data.props.id, name: node.data.props.name }));
      } else {
        _this.selectedFolder = {
          uuid: e.node.dataRef.id,
          name: e.node.dataRef.name
        };
      }
    },
    onSearch(value, event) {
      const _this = this;
      let keyword = trim(value || '');
      _this.searchValue = keyword;
      _this.selectedFolder = {};
      if (isEmpty(keyword)) {
        if (_this.keepTreeData) {
          _this.treeData = [..._this.keepTreeData];
          delete _this.keepTreeData;
        }
      } else {
        if (!_this.keepTreeData) {
          _this.keepTreeData = deepClone(_this.treeData);
        }
        _this.loading = true;
        _this
          .asyncLoadTree(null, keyword)
          .then(({ data: result }) => {
            if (result.data) {
              _this.treeData = _this.convertAsyncTreeData(result.data);
            }
          })
          .finally(() => {
            _this.loading = false;
          });
      }
    },
    collect() {
      const _this = this;
      if (_this.multiple) {
        if (isEmpty(_this.selectedFolders)) {
          _this.$message.info(_this.chooseFolderTip);
          return Promise.reject(_this.selectedFolders);
        }
        return Promise.resolve(_this.selectedFolders);
      } else {
        if (isEmpty(_this.selectedFolder)) {
          _this.$message.info(_this.chooseFolderTip);
          return Promise.reject(_this.selectedFolder);
        }
        return Promise.resolve(_this.selectedFolder);
      }
    }
  }
};
</script>

<style></style>
