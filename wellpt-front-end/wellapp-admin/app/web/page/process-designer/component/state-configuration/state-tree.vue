<template>
  <div>
    <a-tree
      v-if="defaultExpandedKeys.length > 0"
      :defaultExpandedKeys="defaultExpandedKeys"
      :tree-data="treeData"
      :selectable="true"
      :showLine="true"
      :showIcon="false"
      :replaceFields="{ title: 'name', key: 'id' }"
      default-expand-all
      :expandedKeys.sync="expandedTreeKeys"
      :selectedKeys.sync="selectedTreeKeys"
      class="ant-tree-directory"
      style="--w-tree-title-offset-width: 80px; --w-tree-node-content-wrapper-display: inline"
      @select="selectTreeNode"
    >
      <template slot="title" slot-scope="scope">
        <span class="title" :title="scope.name">{{ scope.name }}</span>
        <a-button-group v-if="scope.data" :style="{ marginRight: '5px', float: 'right', color: scope.selected ? '#fff' : '#1890ff' }">
          <a-button v-if="!scope.data.deletable" type="link" size="small" title="添加状态" @click.stop="addState(scope.id)">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          </a-button>
          <a-popover v-if="scope.data.deletable" placement="rightBottom" trigger="click">
            <template slot="content">
              <p>
                状态名称
                <a-input v-model="scope.dataRef.name" placeholder="状态名称" />
              </p>
              <p>
                状态编码
                <a-input v-model="scope.dataRef.data.code" placeholder="状态编码" />
              </p>
            </template>
            <a-button type="link" size="small" title="编辑"><Icon type="pticon iconfont icon-ptkj-bianji"></Icon></a-button>
          </a-popover>
          <a-button v-if="scope.data.deletable" type="link" size="small" title="删除" @click.stop="removeState(scope.id)">
            <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          </a-button>
        </a-button-group>
      </template>
    </a-tree>
  </div>
</template>

<script>
import { generateId, deepClone } from '@framework/vue/utils/util';
export default {
  inject: ['designer'],
  data() {
    let processDefinition = this.designer.getProcessDefinition();
    let treeData = this.getInitTreeData(processDefinition);
    processDefinition.stateTree = treeData.length > 0 ? treeData[0] : null;
    let defaultExpandedKeys = treeData.length > 0 ? [treeData[0].id] : [];
    return { defaultExpandedKeys, treeData, expandedTreeKeys: [], selectedTreeKeys: [] };
  },
  mounted() {
    this.$nextTick(() => this.$emit('mounted', this.treeData));
  },
  methods: {
    getInitTreeData(processDefinition = this.designer.getProcessDefinition()) {
      let treeData = (processDefinition.stateTree && [processDefinition.stateTree]) || null;
      if (treeData == null) {
        treeData = this.replaceFieldAsNameId(deepClone(this.designer.processTree.treeData));
      } else {
        treeData = this.mergeTree(treeData, this.replaceFieldAsNameId(deepClone(this.designer.processTree.treeData)));
      }
      return treeData;
    },
    replaceFieldAsNameId(treeNodes) {
      treeNodes.forEach(node => {
        node.id = node.key;
        node.name = node.title;
        if (node.data) {
          let code =
            (node.data.configuration && node.data.configuration.code) || (node.data.configuration && node.data.configuration.itemCode);
          node.data = { code, deletable: false };
        }
        if (node.children) {
          this.replaceFieldAsNameId(node.children);
        }
      });
      return treeNodes;
    },
    // 合并状态树的状态到最新的业务流程树作为新的状态树，已删除的过程节点、事项节点下的状态不再显示
    mergeTree(treeData, processTreeData) {
      const _this = this;
      // 获取结点下的状态节点
      let getNodeStateChildren = node => {
        if (!node) {
          return [];
        }
        let children = (node.children || []).filter(child => child.data && child.data.deletable);
        return children;
      };
      // 遍历历
      let traverseTree = nodes => {
        nodes.forEach(node => {
          traverseTree(node.children || []);
          let stateChildren = getNodeStateChildren(_this.findTreeNodeById(node.id, treeData));
          node.children = (node.children || []).concat(stateChildren);
        });
      };
      traverseTree(processTreeData);
      return processTreeData;
    },
    findTreeNodeById(id, treeData) {
      let treeNode = null;
      let findTreeNode = nodes => {
        if (treeNode) {
          return;
        }
        nodes.forEach(node => {
          if (node.id == id) {
            treeNode = node;
            return;
          }
          findTreeNode(node.children || []);
        });
      };
      findTreeNode(treeData || this.treeData);

      return treeNode;
    },
    addState(key) {
      let treeNode = this.findTreeNodeById(key);
      let children = treeNode.children || [];
      treeNode.children = children;

      let childNode = {
        id: generateId('SF'),
        name: '状态名称',
        data: {
          code: '状态编码',
          deletable: true
        },
        children: []
      };
      children.push(childNode);
      if (!this.expandedTreeKeys.includes(key)) {
        this.expandedTreeKeys.push(key);
      }
      this.selectedTreeKeys = [childNode.id];
    },
    removeState(id) {
      let removeTreeNode = nodes => {
        nodes.forEach((node, index) => {
          if (node.id == id) {
            nodes.splice(index, 1);
            return;
          }
          removeTreeNode(node.children || []);
        });
      };
      removeTreeNode(this.treeData);
    },
    selectTreeNode(selectedKeys) {
      this.selectedTreeKeys = selectedKeys;
    }
  }
};
</script>

<style></style>
