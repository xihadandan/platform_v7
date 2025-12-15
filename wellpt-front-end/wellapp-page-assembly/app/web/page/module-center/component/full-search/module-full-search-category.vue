<template>
  <div class="module-full-search-category">
    <a-card class="pt-card" :bordered="false" style="--w-card-head-title-weight: bold">
      <template slot="title">
        <template v-if="showTitle">
          <label>全文检索</label>
          <a-tooltip placement="bottom" :arrowPointAtCenter="true">
            <div slot="title">设置模块中需支持全文检索的数据和分类，系统开启全文检索功能后，这些数据将自动创建索引</div>
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-tooltip>
        </template>
      </template>
      <div class="_header">
        <a-button type="link" size="small" @click="addItem">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          添加
        </a-button>
        <span>可拖动排序</span>
      </div>
      <a-tree
        v-if="treeData.length"
        ref="itemTree"
        class="ant-tree-directory tree-more-operations"
        :tree-data="treeData"
        show-icon
        default-expand-all
        :expandedKeys.sync="expandedItemTreeKeys"
        :selectedKeys.sync="selectedItemTreeKeys"
        :replaceFields="{ title: 'name', key: 'id' }"
        :draggable="true"
        @select="onSelect"
        @drop="onDrop"
      >
        <template slot="title" slot-scope="scope">
          <span class="title" :title="scope.name">{{ scope.name }}</span>
          <div class="widget-tree-operations">
            <a-button-group class="button-group">
              <a-button type="link" size="small" title="设置" @click.stop="setItem(scope)">
                <Icon type="pticon iconfont icon-ptkj-shezhi" />
              </a-button>
              <template v-if="scope.data && scope.data.parentUuid === '0'">
                <a-button type="link" size="small" title="添加子级" @click.stop="addChildItem(scope)">
                  <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                </a-button>
              </template>
              <a-button type="link" size="small" title="删除" @click.stop="delItem(scope.id)">
                <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
              </a-button>
            </a-button-group>
          </div>
        </template>
      </a-tree>
    </a-card>
    <Modal title="数据分类" :ok="saveItme" okText="保存" v-model="modalVisible">
      <template slot="content">
        <category-info :info="currentItem" v-if="modalVisible" ref="refItem" />
      </template>
    </Modal>
  </div>
</template>

<script>
import { fetchCategoryListAsTree, fetchSaveCategory, fetchDeleteCategory } from './api';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import CategoryInfo from './category-info.vue';

export default {
  name: 'ModuleFullSearchCategory',
  inject: ['pageContext'],
  props: {
    moduleDetail: {
      type: Object,
      default: () => {}
    },
    showTitle: {
      type: Boolean,
      default: true
    }
  },
  components: {
    Modal,
    CategoryInfo
  },
  data() {
    const moduleId = this.moduleDetail.id;
    return {
      modalVisible: false,
      currentItem: null,
      currentKey: '',
      currentParent: null,
      selectedItem: null,
      moduleId,
      createCategory: ({ parentUuid = '0', sortOrder = 0 } = {}) => {
        return {
          name: '',
          moduleId,
          parentUuid,
          sortOrder
        };
      },
      createTreeNode: data => {
        return {
          children: [],
          id: data.uuid,
          name: data.name,
          data
        };
      },
      treeData: [],
      expandedItemTreeKeys: [],
      selectedItemTreeKeys: []
    };
  },
  created() {
    this.getCategoryTree();
  },
  methods: {
    // 获取分类树
    getCategoryTree() {
      fetchCategoryListAsTree({
        moduleId: this.moduleId
      }).then(res => {
        if (res.length) {
          this.treeData = res;
          this.$emit('select', 'ModuleFullSearchContent');
        } else {
          this.saveDefaultCategory();
        }
      });
    },
    // 保存模块下的默认分类
    saveDefaultCategory() {
      let req = this.createCategory();
      req.name = this.moduleDetail.name;
      fetchSaveCategory(req).then(uuid => {
        req.uuid = uuid;
        const node = this.createTreeNode(req);
        this.treeData.push(node);
        this.$emit('select', 'ModuleFullSearchContent', null, node);
      });
    },
    addItem() {
      let parentUuid = '0';
      if (this.currentParent) {
        parentUuid = this.currentParent.id;
      }
      this.currentItem = this.createCategory({ parentUuid });
      this.modalVisible = true;
    },
    setItem(record) {
      this.currentKey = record.id || record.data.uuid;
      this.currentItem = record;
      this.modalVisible = true;
    },
    saveItme(callback) {
      this.$refs.refItem.validate(({ valid, error, data }) => {
        if (valid) {
          if (!this.currentKey) {
            // 新增
            let sortOrder = 0;
            if (this.currentParent) {
              if (this.currentParent.children) {
                sortOrder = this.currentParent.children.length;
              }
            } else {
              sortOrder = this.treeData.length;
            }
            data.sortOrder = sortOrder;
          }
          fetchSaveCategory(data).then(uuid => {
            data.uuid = uuid;
            let item;
            if (this.currentParent) {
              item = this.currentParent;
              this.pageContext.emitEvent('deleteModelByCategoryUuid', item.id);
              if (!item.children) {
                item.children = [];
              }
              const node = this.createTreeNode(data);
              item.children.push(node);

              const key = item.id;
              if (!this.expandedItemTreeKeys.includes(key)) {
                this.expandedItemTreeKeys.push(key);
              }
              // this.selectedItemTreeKeys = [data.uuid];
              this.currentParent = null;
            } else {
              if (this.currentKey) {
                // 编辑
                item = this.currentItem;
                item.dataRef.name = data.name;
                item.data.name = data.name;
              } else {
                const node = this.createTreeNode(data);
                this.treeData.push(node);
              }
            }
            this.currentKey = '';
            this.selectedItem = null;
            callback(true);
          });
        }
      });
    },
    addChildItem(record) {
      this.currentParent = record;

      this.currentItem = this.createCategory({ parentUuid: record.id });
      this.modalVisible = true;
    },
    delItem(key) {
      fetchDeleteCategory(key).then(() => {
        let removeTreeNode = nodes => {
          nodes.forEach((node, index) => {
            if (node.key == key || node.id === key) {
              nodes.splice(index, 1);
              return;
            }
            removeTreeNode(node.children || []);
          });
        };
        removeTreeNode(this.treeData);
        this.selectedItemTreeKeys = this.selectedItemTreeKeys.filter(selected => selected != key);
        this.$emit('select', 'ModuleFullSearchContent');
      });
    },

    // 点击树节点
    onSelect(selectedKeys, { event: eventName, selected, node, selectedNodes }) {
      let item;
      if (selected) {
        item = node.dataRef;
      }
      this.selectedItem = item;
      if (item && item.data && item.data.parentUuid !== '0') {
        this.currentParent = node.$parent.dataRef;
      }
      this.$emit('select', 'ModuleFullSearchContent', item);
      this.selectedItemTreeKeys = selectedKeys;
    },
    onDrop(info) {
      const dragKey = info.dragNode.eventKey; // 拖动的key fromKey
      const dropKey = info.node.eventKey; // toKey
      const dropPos = info.node.pos.split('-');
      const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);
      const dropToBottom = dropPosition == 1;

      const fromNode = info.dragNode;
      const fromNodeData = fromNode.dataRef.data;
      const toNode = info.node;
      const toNodeData = toNode.dataRef.data;
      if (dragKey == dropKey || (toNodeData && toNodeData.parentUuid !== '0')) {
        return;
      }
      if (fromNodeData && fromNodeData.children && fromNodeData.children.length) {
        return;
      }

      this.moveItemNode(dragKey, dropKey, info.dropToGap, dropToBottom);
    },
    // 更新分类
    updateCategory(data) {
      return new Promise((resolve, reject) => {
        fetchSaveCategory(data).then(res => {
          resolve();
        });
      });
    },
    /* 
      dropToBottom 同级排序从上往下
    */
    moveItemNode(fromId, toId, sibling, dropToBottom, fromPos, toPos) {
      const _this = this;
      let treeData = this.treeData;
      // 删除并返回源节点数据
      let deleteFromNode = nodes => {
        let deletedNode = null;
        if (nodes == null) {
          return deletedNode;
        }
        for (let index = 0; index < nodes.length; index++) {
          let node = nodes[index];
          if (node.key == fromId || node.id == fromId) {
            nodes.splice(index, 1);
            return node;
          }
          // 子节点
          deletedNode = deleteFromNode(node.children);
          if (deletedNode) {
            return deletedNode;
          }
        }
        return deletedNode;
      };
      // 在目标节点插入源节点数据
      let appendFromNode = (fromNode, nodes) => {
        if (nodes == null) {
          return false;
        }
        for (let index = 0; index < nodes.length; index++) {
          let node = nodes[index];
          if (node.key == toId || node.id == toId) {
            node.children = node.children || [];

            let toNode = node;
            let toNodeData = toNode.data;
            let fromNodeData = fromNode.data;

            if (sibling) {
              let _index = dropToBottom ? index + 1 : index;

              let childrenToParent = false;
              if (fromNodeData.parentUuid !== '0' && toNodeData.parentUuid === '0') {
                // 子移动到父
                fromNodeData.parentUuid = '0';
                childrenToParent = true;
                this.pageContext.emitEvent('deleteModelByCategoryUuid', fromNodeData.uuid);
              }

              // let formSortOrder = fromNodeData.sortOrder;
              // let sortOrder = toNodeData.sortOrder;
              // toNodeData.sortOrder = formSortOrder;
              // fromNodeData.sortOrder = sortOrder;

              this.updateCategory(fromNodeData);
              // this.updateCategory(toNodeData);

              nodes.splice(_index, 0, fromNode);
            } else {
              // 添加子类
              let sortOrder = node.children.length;
              fromNodeData.sortOrder = sortOrder;
              fromNodeData.parentUuid = toNodeData.uuid;
              this.updateCategory(fromNodeData);

              if (!toNodeData.children) {
                toNodeData.children = [];
              }
              toNodeData.children.push(fromNodeData);
              node.children.push(fromNode);
            }
            return true;
          }
          // 子节点
          if (appendFromNode(fromNode, node.children)) {
            return true;
          }
        }
        return false;
      };

      let fromNode = deleteFromNode(treeData);
      if (fromNode) {
        appendFromNode(fromNode, treeData);
      } else {
        console.error('from node not found', fromId);
      }
    },
    findDictionaryItemByKey(key) {
      let item = null;
      let findTreeNode = nodes => {
        if (item) {
          return;
        }
        nodes.forEach(node => {
          if (node.key == key || node.id == key) {
            item = node;
            return;
          }
          findTreeNode(node.children || []);
        });
      };
      findTreeNode(this.treeData);

      return item;
    }
  }
};
</script>

<style lang="less">
.module-full-search-category {
  .pt-card {
    ._header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .ant-tree-directory.tree-more-operations {
      --w-tree-title-offset-width: 60px;
      li .ant-tree-node-content-wrapper {
        --w-tree-node-content-wrapper-display: inline;
      }
      .widget-tree-operations {
        float: right;
        position: relative;

        > div {
          position: absolute;
          right: var(--w-padding-2xs);
        }
      }
    }
  }
}
</style>
