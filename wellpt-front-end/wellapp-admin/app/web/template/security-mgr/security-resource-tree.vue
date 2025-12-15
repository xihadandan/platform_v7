<template>
  <div style="padding: var(--w-padding-xs) var(--w-padding-md)">
    <div class="flex">
      <div class="f_g_1">
        <a-input-search v-model="searchValue" @change="onSearchTreeNode" allowClear />
      </div>
      <div class="f_s_0" style="padding-left: 8px">
        <a-space>
          <a-button type="primary" class="icon-only" @click="handleAdd" title="新增">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          </a-button>
          <a-button type="danger" class="icon-only" @click="handleDelete" title="删除">
            <Icon type="pticon iconfont icon-ptkj-shanchu" style="color: white"></Icon>
          </a-button>
          <a-dropdown>
            <a-button class="icon-only">
              <Icon type="iconfont icon-ptkj-gengduocaozuo"></Icon>
            </a-button>
            <a-menu slot="overlay">
              <a-menu-item @click="handleDefExport">定义导出</a-menu-item>
              <a-menu-item @click="handleDefImport">定义导入</a-menu-item>
            </a-menu>
          </a-dropdown>
        </a-space>
      </div>
    </div>
    <PerfectScrollbar style="height: calc(100% - 32px); margin-right: -20px; padding-right: 12px">
      <a-tree
        ref="resourceTree"
        :tree-data="treeData"
        :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
        :expandedKeys="expandedKeys"
        :selectedKeys="selectedKeys"
        @expand="expandTree"
        @select="clickTree"
        class="ant-tree-directory"
      >
        <template slot="title" slot-scope="node">
          <div class="title">
            <span v-if="searchValue && node.name.indexOf(searchValue) > -1">
              {{ node.name.substr(0, node.name.indexOf(searchValue)) }}
              <span style="color: #f50">{{ searchValue }}</span>
              {{ node.name.substr(node.name.indexOf(searchValue) + searchValue.length) }}
            </span>
            <span v-else>{{ node.name }}</span>
          </div>
        </template>
      </a-tree>
    </PerfectScrollbar>
    <ExportDef ref="defExport" :uuid="exportUuids" type="resource"></ExportDef>
    <ImportDef ref="defImport"></ImportDef>
  </div>
</template>

<script>
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';
export default {
  components: { ExportDef, ImportDef },
  inject: ['pageContext'],
  data() {
    return {
      searchValue: '',
      treeData: [],
      expandedKeys: [],
      selectedKeys: [],
      exportUuids: []
    };
  },
  computed: {
    treeNodeAsList() {
      let list = [];
      let travelTree = (nodes, parentNode, parentKeys) => {
        nodes.forEach(node => {
          if (parentNode) {
            parentKeys.push(parentNode.id);
          }
          node.parentKeys = [...parentKeys];
          list.push(node);
          if (node.children) {
            travelTree(node.children, node, parentKeys);
          }
          if (parentNode) {
            parentKeys.pop();
          }
        });
      };
      travelTree(this.treeData, null, []);
      return list;
    }
  },
  created() {
    this.loadResourceTree();
  },
  mounted() {
    this.pageContext.handleEvent('resource-tree:reload', () => {
      this.loadResourceTree();
    });
  },
  methods: {
    loadResourceTree() {
      this.selectedNodeData = null;
      $axios
        .post(`/proxy/api/security/resource/getModuleMenuAsTree?uuid=-1&moduleId=`)
        .then(({ data: result }) => {
          if (result.data) {
            this.treeData = [result.data];
            this.expandedKeys = this.expandedKeys.length > 0 ? this.expandedKeys : [result.data.id];
          } else {
            this.$message.error(result.msg || '加载失败！');
          }
        })
        .catch(({ response }) => {
          this.$message.error((response.data && response.data.msg) || '服务异常！');
        });
    },
    handleAdd() {
      this.pageContext.emitEvent('resource-tree:add', this.selectedKeys.length > 0 ? this.selectedKeys[0] : null);
    },
    handleDelete() {
      const _this = this;
      if (!_this.selectedNodeData) {
        return;
      }

      _this.$confirm({
        title: '确认框',
        content: `确认删除资源[${_this.selectedNodeData.name}]吗?`,
        okText: '确定',
        cancelText: '取消',
        onOk() {
          $axios
            .delete(`/proxy/api/security/resource/remove/${_this.selectedNodeData.id}`)
            .then(({ data: result }) => {
              if (result.code == 0) {
                _this.$message.success('删除成功！');
                _this.loadResourceTree();
                _this.handleAdd();
              } else {
                _this.$message.error(result.msg || '删除失败！');
              }
            })
            .catch(({ response }) => {
              this.$message.error((response.data && response.data.msg) || '服务异常！');
            });
        }
      });
    },
    handleDefExport() {
      if (this.selectedKeys.length > 0) {
        this.exportUuids = this.selectedKeys;
      } else if (this.selectedNodeData) {
        this.exportUuids = [this.selectedNodeData.id];
      }
      if (this.exportUuids.length == 0) {
        this.$message.info('请选择资源！');
      }
      this.$refs.defExport.show();
    },
    handleDefImport() {
      this.$refs.defImport.show();
    },
    onSearchTreeNode() {
      const _this = this;
      let dataList = _this.treeNodeAsList;
      if (!_this.searchValue) {
        _this.expandedKeys = ['-1'];
        return false;
      }

      let matchNodes = dataList.filter(item => item.name && item.name.indexOf(_this.searchValue) != -1);
      matchNodes.forEach(item => {
        let parentKeys = item.parentKeys || [];
        parentKeys.forEach(key => {
          if (!_this.expandedKeys.includes(key)) {
            _this.expandedKeys.push(key);
          }
        });
      });

      // 第一个匹配的节点移入可见视图内
      if (matchNodes.length > 0) {
        _this.$nextTick(() => {
          let $node = _this.getVueTreeNodeByNodeId(matchNodes[0].id);
          if ($node && $node.$el && $node.$el.scrollIntoView) {
            $node.$el.scrollIntoView();
          }
        });
      }
    },
    getVueTreeNodeByNodeId(nodeId) {
      const _this = this;
      let resourceTree = _this.$refs.resourceTree;
      let node = null;
      let fileVueTreeNode = nodes => {
        if (node) {
          return;
        }
        nodes.forEach(child => {
          if (child.dataRef && child.dataRef.id == nodeId) {
            node = child;
          } else {
            fileVueTreeNode(child.$children || []);
          }
        });
      };
      fileVueTreeNode(resourceTree.$children || []);
      return node;
    },
    clickTree(selectedKeys, e) {
      if (e.node.dataRef.id == '-1') {
        this.selectedKeys = [];
        this.selectedNodeData = null;
        return;
      }
      this.selectedKeys = selectedKeys;
      this.selectedNodeData = e.node.dataRef;
      this.pageContext.emitEvent('resource-tree:click', e.node.dataRef);
    },
    expandTree(expandedKeys) {
      this.expandedKeys = expandedKeys;
    }
  }
};
</script>

<style></style>
