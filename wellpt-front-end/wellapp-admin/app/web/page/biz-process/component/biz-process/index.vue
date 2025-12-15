<template>
  <a-card size="small" title="业务流程" :bordered="false">
    <PerfectScrollbar style="height: calc(100vh - 120px)">
      <a-tabs default-active-key="process" @change="tabChange">
        <a-tab-pane key="process" tab="业务结构">
          <a-tree
            v-if="defaultExpandedKeys.length > 0"
            :defaultExpandedKeys="defaultExpandedKeys"
            :tree-data="designer.processTree.treeData"
            :selectable="true"
            :showLine="true"
            @select="selectTreeNode"
          ></a-tree>
        </a-tab-pane>
        <a-tab-pane key="itemFlow" tab="事项流">
          <a-menu @click="onItemMenuClick">
            <a-menu-item v-for="item in itemDefinitions" :key="item.id">
              {{ item.itemName }}
            </a-menu-item>
          </a-menu>
        </a-tab-pane>
      </a-tabs>
    </PerfectScrollbar>
  </a-card>
</template>

<script>
import BizProcessDesignerPreview from './biz-process-designer-preview.vue';
import BizItemFlowPreview from './biz-item-flow-preview.vue';
import ProcessDesigner from '../../../process-designer/designer/process-designer';
import ItemFlowDesigner from '../../../process-designer/designer/item-flow-designer';
export default {
  components: {},
  inject: ['assemble'],
  data() {
    let designer = new ProcessDesigner('desinger-container', false);
    let itemFlowDesigner = new ItemFlowDesigner('item-flow-desinger-container', designer, false);
    return {
      designer,
      itemFlowDesigner,
      currentDesigner: 'process'
    };
  },
  computed: {
    defaultExpandedKeys() {
      let treeData = this.designer.processTree.treeData;
      return treeData.length > 0 ? [treeData[0].key] : [];
    },
    itemDefinitions() {
      return this.assemble.getAllItems();
    }
  },
  mounted() {
    const _this = this;
    _this.show();
  },
  methods: {
    show() {
      const _this = this;
      if (_this.currentDesigner == 'process') {
        _this.assemble.showContent({
          component: BizProcessDesignerPreview,
          metadata: {
            designer: _this.designer
          },
          keepAlive: true,
          key: 'biz-process-designer'
        });
      } else {
        _this.assemble.showContent({
          component: BizItemFlowPreview,
          metadata: {
            designer: _this.itemFlowDesigner
          },
          keepAlive: true,
          key: 'biz-item-flow-designer'
        });
      }
    },
    tabChange(activeKey) {
      this.currentDesigner = activeKey;
      this.show();
    },
    selectTreeNode(selectedKeys) {
      this.designer.selectNode(selectedKeys[0]);
    },
    onItemMenuClick({ key }) {
      const _this = this;
      let itemDefinition = _this.itemDefinitions.find(item => item.id == key);
      _this.itemFlowDesigner.showItemFlowByItemDefinition(itemDefinition);
    }
  }
};
</script>

<style></style>
