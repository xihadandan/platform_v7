<template>
  <div class="process-designer-item" :class="{ selected: selected, horizontal: layout == 'horizontal', vertical: layout == 'vertical' }">
    <a-row :style="{ width: styles.width, height: styles.height, lineHeight: styles.height }">
      <a-col class="text" :title="itemDefinition.itemName || itemDefinition.name">
        {{ itemDefinition.itemName || itemDefinition.name }}
      </a-col>
    </a-row>
    <div v-show="designer.editable" class="add-bottom">
      <Icon type="plus-circle" @click="onAddItemClick('bottom')" />
    </div>
    <ProcessAddNodeOrItemModal ref="processAddNodeOrItemModal" title="添加事项" :ok="handleAddItemOk" type="2"></ProcessAddNodeOrItemModal>
  </div>
</template>

<script>
import { createNodeTemplate, moveNodeToViewportIfRequired } from '../designer/utils';
import ProcessAddNodeOrItemModal from '../component/process-add-node-or-item-modal.vue';
export default {
  name: 'ProcessItem',
  components: { ProcessAddNodeOrItemModal },
  inject: ['getNode', 'getGraph', 'designer', 'pageContext'],
  data() {
    return {
      itemDefinition: {},
      styles: {
        width: 200 + 'px',
        height: 24 + 'px'
      },
      selected: false
    };
  },
  created() {
    const _this = this;
    let node = _this.getNode();
    if (!node.vm) {
      node.vm = _this;
    }
    _this.layout = (node.parent && node.parent.data.layout) || 'horizontal';
    _this.itemData = node.getData();
    let itemDefinition = _this.itemData.configuration || _this.itemData;
    itemDefinition.itemName = itemDefinition.itemName || itemDefinition.name;
    _this.itemDefinition = itemDefinition;
    let nodeSize = node.getSize();
    if (_this.layout == 'vertical') {
      _this.styles.width = (24 || nodeSize.width) + 'px';
      _this.styles.height = (200 || nodeSize.height) + 'px';
    }
    if (this.designer.selectedNodeId === node.id) {
      this.pageContext.emitEvent(`processConfigurationPanel:updateSelectedKey`);
    }
  },
  mounted() {
    if (this.designer.selectedNodeId == this.getNode().id) {
      const timer = setTimeout(() => {
        clearTimeout(timer);
        this.designer.setIncomingsHighlight(this.getNode());
      }, 300);
      this.select();
    }
  },
  methods: {
    onAddItemClick(position) {
      const _this = this;
      _this.$refs.processAddNodeOrItemModal.open('horizontal', position);
    },
    handleAddItemOk(formData) {
      let count = formData.count || 1;
      if (formData.itemDefinition) {
        let itemNode = this.designer.processTree.getTreeNodeByKey(formData.itemDefinition.id);
        if (itemNode) {
          this.$message.error(`事项[${formData.itemDefinition.itemName}]已存在，不能重复引用！`);
        } else {
          this.designer.addSiblingFromNodeTemplate(this.getNode(), { items: [formData.itemDefinition] });
        }
      } else {
        let itemStartIndex = this.designer.processTree.getTreeDataCount('item');
        for (let index = count - 1; index >= 0; index--) {
          this.designer.addSiblingFromNodeTemplate(
            this.getNode(),
            createNodeTemplate({ count: 1, nodeType: '2', itemStartIndex: itemStartIndex + index })
          );
        }
      }
    },
    select() {
      this.selected = true;
      moveNodeToViewportIfRequired(this.getNode(), this.getGraph());
    },
    unselect() {
      this.selected = false;
    }
  }
};
</script>

<style lang="less">
.process-designer-item {
  position: relative;
  --w-process-designer-item-border-color: var(--w-primary-color-3);
  --w-process-designer-item-text-color: var(--w-font-size-base);
  --w-process-designer-item-btn-color: var(--w-primary-color-3);

  border-radius: 4px;
  border: 1px solid var(--w-process-designer-item-border-color);
  cursor: pointer;

  &:hover {
    --w-process-designer-item-border-color: var(--w-primary-color);
  }
  &.selected {
    --w-process-designer-item-border-color: var(--w-primary-color);
    --w-process-designer-item-text-color: #ffffff;
    font-weight: bold;
    background-color: var(--w-primary-color);
  }

  .text {
    font-size: var(--w-font-size-base);
    color: var(--w-process-designer-item-text-color);
    line-height: 24px;
    padding-left: 12px;
    padding-right: 12px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    border-left: 4px solid var(--w-primary-color);
    border-top-left-radius: 4px;
    border-bottom-left-radius: 4px;
  }

  &.horizontal {
    .add-bottom {
      top: 0;
      margin-top: -1px;
      right: -11px;
    }
  }
  &.vertical {
    width: 24px;
    .text {
      height: 100%;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
      flex-shrink: 0;
      writing-mode: vertical-rl; /* 文字从上到下竖向排列 */
      vertical-align: middle; /* 垂直居中 */
      direction: ltr; /* 显式设置文本方向为左至右 */
      padding: 12px 0;
      line-height: 22px;

      border-left: 0;
      border-top: 4px solid var(--w-primary-color);
      border-top-right-radius: 4px;
    }

    .add-bottom {
      left: 50%;
      margin-left: -9px;
      bottom: -5px;
    }
  }
  .add-bottom {
    width: 18px;
    height: 18px;
    border-radius: 50%;
    color: var(--w-process-designer-item-btn-color);
    position: absolute;
    font-size: 18px;

    i {
      background: #ffffff;
      border-radius: 50%;
    }

    &:hover {
      --w-process-designer-item-btn-color: var(--w-primary-color);
    }
  }
}
</style>
