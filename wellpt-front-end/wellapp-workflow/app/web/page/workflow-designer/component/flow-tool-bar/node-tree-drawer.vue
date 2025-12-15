<template>
  <div>
    <a-button
      type="link"
      size="small"
      :class="{
        'tool-node-tree-btn': true,
        'btn-tool-active': visible
      }"
      @click="openDrawer"
    >
      <Icon type="pticon iconfont icon-jiagou-01" />
    </a-button>
    <drawer
      v-if="containerCreated"
      v-model="visible"
      :width="240"
      :closable="false"
      :container="getContainer"
      :wrapStyle="{
        position: 'absolute'
      }"
      wrapClassName="tool-node-tree-drawer"
    >
      <node-search-select slot="title" v-model="searchKeyword" :graphItem="graphItem" :isTree="true" />
      <template slot="content">
        <a-tree
          v-show="!searchKeyword"
          class="tool-node-tree"
          :treeData="treeData"
          :loadData="onLoadTreeData"
          :showIcon="true"
          :expandedKeys="expandedKeys"
          @expand="onExpand"
          @select="onSelect"
        >
          <template v-slot:nodeIcon="node">
            <Icon :type="`pticon iconfont ${setNodeIcon(node)}`" />
          </template>
          <!-- <template v-slot:nodeTitle="{ title }">
            <span v-if="title.indexOf(searchKeyword) > -1">
              {{ title.substr(0, title.indexOf(searchKeyword)) }}
              <span style="color: var(--w-primary-color)">{{ searchKeyword }}</span>
              {{ title.substr(title.indexOf(searchKeyword) + searchKeyword.length) }}
            </span>
            <span v-else>{{ title }}</span>
          </template> -->
        </a-tree>
      </template>
    </drawer>
  </div>
</template>

<script>
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import NodeSearchSelect from './node-search-select.vue';
const drawerSelector = '.graph-wrapper';

export default {
  name: 'ToolNodeTreeDrawer',
  inject: ['pageContext', 'graph'],
  props: {
    graphItem: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    Drawer,
    NodeSearchSelect
  },
  data() {
    return {
      containerCreated: false,
      visible: false,
      expandedKeys: [],
      treeData: [],
      treeIconMap: {
        NodeTask: 'icon-liucheng-huanjieshu-huanjie',
        NodeCondition: 'icon-liucheng-huanjieshu-panduan-01',
        NodeSubflow: 'icon-liucheng-huanjieshu-ziliucheng-01',
        NodeCircle: 'icon-ptkj-danxuan-weixuan'
      },
      searchKeyword: '' // 搜索关键词
    };
  },
  mounted() {
    this.pageContext.handleEvent('closeAllDrawer', this.closeDrawer);
  },
  beforeDestroy() {
    this.pageContext.offEvent('closeAllDrawer');
  },
  methods: {
    closeDrawer() {
      this.visible = false;
    },
    openDrawer() {
      if (this.visible) {
        this.closeDrawer();
        return;
      }
      this.getNodesTree();
      this.pageContext.emitEvent('closeAllDrawer');
      if (!this.containerCreated) {
        this.containerCreated = true;
      }
      this.visible = true;
    },
    // 异步加载树
    onLoadTreeData(node) {
      const { key: nodeKey, children, sourceData, isLeaf } = node.dataRef;
      return new Promise((resolve, reject) => {
        if (!isLeaf && !children.length) {
          const outgoings = this.graphItem.getOutgoingEdges(sourceData.id);
          let nodes = [];
          if (outgoings) {
            nodes = outgoings.map(edge => edge.getTargetCell());
          }

          const { nodesTree, expandedKeys } = this.graphItem.getNodesTree({
            nodes,
            parentKey: nodeKey
          });
          node.dataRef.children = nodesTree;
          this.expandedKeys = this.expandedKeys.concat(expandedKeys);
        }
        resolve();
      });
    },
    // 点击树节点
    onSelect(selectedKeys, { event: eventName, selected, node, selectedNodes }) {
      const { sourceData } = node.dataRef;
      this.graphItem.centerCell(sourceData);
      this.graphItem.resetSelection(sourceData, { save: true });
    },
    // 展开/收起
    onExpand(expandedKeys, { expanded, node, nativeEvent }) {
      let { key: nodeKey } = node.dataRef;
      if (expanded) {
        this.expandedKeys.push(nodeKey);
      } else {
        this.expandedKeys = this.expandedKeys.filter(key => key !== nodeKey);
      }
    },
    // 设置节点图标
    setNodeIcon(node) {
      const { sourceData } = node;

      return this.treeIconMap[sourceData['shape']];
    },
    getNodesTree() {
      const rootNodes = this.graphItem.graph.getRootNodes();
      let nodes = [];
      rootNodes.map(node => {
        const outgoings = this.graphItem.getOutgoingEdges(node);
        if (outgoings) {
          outgoings.map(edge => {
            nodes.push(edge.getTargetCell());
          });
        } else {
          if (node.shape !== 'NodeCircle') {
            nodes.push(node);
          }
        }
      });
      const { nodesTree, expandedKeys } = this.graphItem.getNodesTree({ nodes });
      this.treeData = nodesTree;
      this.expandedKeys = this.expandedKeys.concat(expandedKeys);
    },
    getContainer() {
      return document.querySelector(drawerSelector);
    }
  }
};
</script>
