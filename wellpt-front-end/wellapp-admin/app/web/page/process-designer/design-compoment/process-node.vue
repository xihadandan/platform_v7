<template>
  <div
    class="process-designer-node"
    :class="{
      selected: selected,
      horizontal: layout == 'horizontal',
      vertical: layout == 'vertical',
      'is-start': sort.isStart,
      'is-end': sort.isEnd
    }"
  >
    <!-- 背景 -->
    <div :class="layout">
      <template v-if="layout == 'horizontal'">
        <div class="horizontal-node-bg"></div>
      </template>
      <template v-if="layout == 'vertical'">
        <div class="vertical-node-bg"></div>
      </template>
    </div>
    <a-row :style="{ width: styles.width, height: styles.height, lineHeight: styles.height }">
      <a-col class="text" :class="layout">
        <div class="node-name" :title="nodeDefinition.name">
          {{ nodeDefinition.name }}
        </div>
        <div :class="layout + '-tag'">
          <a-tag color="pink" v-for="(item, index) in configTips" :key="'tips_' + index" :color="item.color">
            {{ item.text }}{{ layout == 'horizontal' && item.num ? '：' : '' }}
            <span class="tag-num">{{ item.num }}</span>
          </a-tag>
        </div>
      </a-col>
    </a-row>
    <ProcessAddNodeOrItemSelectPopover
      ref="processAddNodeOrItemSelectPopover"
      :designer="designer"
      :ok="handleSelectTypeOk"
      :getPopupContainer="getPopupContainer"
    ></ProcessAddNodeOrItemSelectPopover>
    <ProcessAddNodeOrItemModal
      ref="processAddNodeOrItemModal"
      :ok="handleAddNodeOrItemOk"
      :designer="designer"
      :type="modelType"
    ></ProcessAddNodeOrItemModal>
  </div>
</template>

<script>
import { createNodeTemplate, getNodeChildrenSize, moveNodeToViewportIfRequired } from '../designer/utils';
import ProcessAddNodeOrItemModal from '../component/process-add-node-or-item-modal.vue';
import ProcessAddNodeOrItemSelectPopover from '../component/process-add-node-or-item-select-popover.vue';
import { hideNodePort, showNodePort } from '../designer/node-ports';
import { findIndex } from 'lodash';
export default {
  name: 'ProcessNode',
  components: { ProcessAddNodeOrItemModal, ProcessAddNodeOrItemSelectPopover },
  inject: ['getNode', 'getGraph', 'designer', 'pageContext'],
  data() {
    return {
      configTips: [{ color: '', text: '无配置' }],
      nodeDefinition: {},
      styles: {
        width: 180 + 'px',
        height: 66 + 'px'
      },
      selected: false,
      nodeType: '',
      modelType: '',
      parentId: ''
    };
  },
  computed: {
    layout() {
      return this.nodeData.layout;
    },
    sort() {
      let sort = {};
      let node = this.getNode();
      let parentNodes = node.parent._children;
      sort.index = findIndex(parentNodes, { id: this.nodeData.configuration.id });
      sort.isEnd = sort.index == parentNodes.length - 1;
      sort.isStart = sort.index === 0;
      return sort;
    }
  },
  created() {
    const _this = this;
    let node = _this.getNode();
    if (!node.vm) {
      node.vm = _this;
    }
    _this.nodeData = node.getData();
    _this.nodeDefinition = _this.nodeData.configuration || _this.nodeData;
    let nodeSize = node.getSize();
    if (_this.layout == 'vertical') {
      _this.styles.width = (66 || nodeSize.width) + 'px';
      _this.styles.height = (180 || nodeSize.height) + 'px';
    }
    _this.updateConfigTips();
    this.setLineHideOrShow();
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
  beforeDestroy() {
    this.setHideLastNodeLine();
  },
  methods: {
    // 隐藏最后一个节点的线
    setHideLastNodeLine() {
      const parentNode = this.getGraph().getCellById(this.parentId);
      if (parentNode && parentNode.children) {
        const childrenLen = parentNode.children.length;
        parentNode.children.map((item, index) => {
          if (index === childrenLen - 1) {
            if (item.data.layout === 'horizontal') {
              const rightLinePort = item.getPortsByGroup('rightLine')[0];
              hideNodePort(item, rightLinePort, ['rightLine', 'rightLineContent']);
            } else {
              const bottomLinePort = item.getPortsByGroup('bottomLine')[0];
              hideNodePort(item, bottomLinePort, ['bottomLine', 'bottomLineContent']);
            }
          }
        });
      }
    },
    setLineHideOrShow() {
      const currentNode = this.getNode();
      if (currentNode.parent && currentNode.parent._children) {
        this.parentId = currentNode.parent.id;
        const childrenLen = currentNode.parent._children.length;
        currentNode.parent._children.map((item, index) => {
          if (index === childrenLen - 1) {
            // 最后一个
            if (item.data.layout === 'horizontal') {
              const rightLinePort = item.getPortsByGroup('rightLine')[0];
              hideNodePort(item, rightLinePort, ['rightLine', 'rightLineContent']);
            } else {
              const bottomLinePort = item.getPortsByGroup('bottomLine')[0];
              hideNodePort(item, bottomLinePort, ['bottomLine', 'bottomLineContent']);
            }
          } else {
            if (item.data.layout === 'horizontal') {
              const rightLinePort = item.getPortsByGroup('rightLine')[0];
              showNodePort(item, rightLinePort, ['rightLine', 'rightLineContent']);
            } else {
              const bottomLinePort = item.getPortsByGroup('bottomLine')[0];
              showNodePort(item, bottomLinePort, ['bottomLine', 'bottomLineContent']);
            }
          }
        });
      }
      // }
    },
    getPopupContainer(e) {
      return document.body.querySelector('#desinger-container');
    },
    updateConfigTips() {
      this.configTips = [];
      let node = this.getNode();
      let nodeData = node.getData();

      // 阶段群组
      if (nodeData.childOfProcessGroupId) {
        let groupNode = this.getGraph().getCellById(nodeData.childOfProcessGroupId);
        if (groupNode) {
          this.configTips.push({ color: 'green', text: '阶段', num: getNodeChildrenSize(groupNode) });
        }
      }

      // 事项群组
      if (nodeData.childOfItemGroupId) {
        let groupNode = this.getGraph().getCellById(nodeData.childOfItemGroupId);
        if (groupNode) {
          this.configTips.push({ color: 'orange', text: '事项', num: getNodeChildrenSize(groupNode) });
        }
      }

      if (this.configTips.length == 0) {
        this.configTips.push({ color: '', text: '无配置' });
      }
    },
    getReverseLayout() {
      if (this.layout == 'horizontal') {
        return 'vertical';
      }
      return 'horizontal';
    },
    // 鼠标悬停在水平布局下面的添加按钮和竖向布局右侧的添加按钮
    onAddNodeOrItemHover({ e, node, view }) {
      const _this = this;
      let portId = e.target.parentElement.getAttribute('port');
      if (portId) {
        let positionIndex = findIndex(node.port.ports, { id: portId });
        if (positionIndex > -1) {
          let position = node.port.ports[positionIndex].group;
          if ((_this.layout == 'horizontal' && position == 'bottom') || (_this.layout == 'vertical' && position == 'right')) {
            _this.$refs.processAddNodeOrItemSelectPopover.open(_this.layout, position);
          } else {
            this.handleSelectTypeOk(_this.layout, position, '1');
          }
        }
      }
    },
    // 选中节点类型
    handleSelectTypeOk(layout, position, type) {
      this.nodeType = type;
      this.onAddNodeOrItemClick(position, type);
    },
    onAddNodeOrItemClick(position, type) {
      const _this = this;
      let refInfo = _this.designer.getNodeRefInfo(_this.getNode());
      let addChildNode = _this.isAddChildNode(_this.layout, position);
      if (addChildNode) {
        if (refInfo.ref) {
          _this.$message.error('引用阶段不能添加子阶段/事项！');
          return;
        }
      } else {
        if (refInfo.ref && !refInfo.directRef) {
          _this.$message.error('引用阶段不能添加阶段！');
          return;
        }
      }
      this.modelType = type ? type : '';
      this.$nextTick(() => {
        _this.$refs.processAddNodeOrItemModal.open(_this.layout, position);
      });
    },
    isAddChildNode(layout, position) {
      let addChildNode = false;
      // 水平方向
      if (layout == 'horizontal') {
        // 添加同级节点
        if (position == 'right') {
          addChildNode = false;
        } else {
          // 添加子节点/事项
          addChildNode = true;
        }
      } else {
        // 垂直方向
        // 添加子节点/事项
        if (position == 'right') {
          addChildNode = true;
        } else {
          // 添加同级节点
          addChildNode = false;
        }
      }
      return addChildNode;
    },
    handleAddNodeOrItemOk(formData) {
      const _this = this;
      // console.log(formData);
      let designNode = {
        layout: _this.layout, //formData.childNode ? _this.getReverseLayout() : _this.layout, // 子节点布局与父节点一致
        ...formData
      };
      // 添加过程节点
      if (designNode.nodeType == '1') {
        let nodeLevelInfo = _this.getNodeLevelInfo();
        designNode.nodeLevelInfo = nodeLevelInfo;
        console.log('nodeLevelInfo', nodeLevelInfo);
        let nodeStartIndex = _this.designer.processTree.getTreeDataCount('node');
        designNode.nodeStartIndex = nodeStartIndex;
      } else {
        // 添加事项
        let itemStartIndex = _this.designer.processTree.getTreeDataCount('item');
        designNode.itemStartIndex = itemStartIndex;
      }

      let nodeTemplate = createNodeTemplate(designNode);
      if (designNode.childNode) {
        if (formData.refMode == 'node' && formData.nodeDefinition) {
          nodeTemplate.nodes = [formData.nodeDefinition];
        } else if (formData.refMode == 'item' && formData.itemDefinition) {
          nodeTemplate.items = [formData.itemDefinition];
        }
        _this.designer.addChildFromNodeTemplate(_this.getNode(), nodeTemplate);
      } else {
        if (formData.refMode == 'node' && formData.nodeDefinition) {
          nodeTemplate.nodes = [formData.nodeDefinition];
        } else if (formData.refMode == 'item' && formData.itemDefinition) {
          nodeTemplate.items = [formData.itemDefinition];
        }
        _this.designer.addSiblingFromNodeTemplate(_this.getNode(), nodeTemplate);
      }
      _this.updateConfigTips();
    },
    getNodeLevelInfo() {
      const _this = this;
      let node = _this.getNode();
      let treeData = _this.designer.processTree.getTreeData();
      let nodeLevelInfo = {
        level: -1
      };
      let indexes = [];
      let traverseTree = function (treeNode, nodeLevelInfo, level) {
        if (nodeLevelInfo.key) {
          return;
        }
        if (treeNode.children) {
          treeNode.children.forEach((child, index) => {
            let childLevel = level + 1;
            indexes.push(index);
            if (child.key == node.id) {
              nodeLevelInfo.key = child.key;
              nodeLevelInfo.level = childLevel;
              nodeLevelInfo.index = index;
              nodeLevelInfo.indexes = [...indexes];
              nodeLevelInfo.parentId = treeNode.key;
              nodeLevelInfo.parentChildrenSize = treeNode.children.length;
              nodeLevelInfo.childrenSize = child.children ? child.children.length : 0;
              return;
            }
            traverseTree(child, nodeLevelInfo, childLevel);
            indexes.pop();
          });
        }
      };

      if (treeData[0]) {
        traverseTree(treeData[0], nodeLevelInfo, 0);
      }
      return nodeLevelInfo;
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
._highlight() {
  --w-process-designer-node-background-color: var(--w-primary-color); // #0099ff;
  --w-process-designer-node-border-color: var(--w-primary-color);

  .text {
    color: #fff;
    background-color: var(--w-process-designer-node-background-color);
  }
}
.x6-node {
  &.highlight {
    .process-designer-node {
      ._highlight();
    }
  }
}
.process-designer-node {
  position: relative;
  --w-process-designer-node-background-color: var(--w-primary-color-1);
  --w-process-designer-node-border-color: var(--w-primary-color-4);
  cursor: pointer;
  border-radius: 4px;

  // .horizontal-node-bg,
  // .vertical-node-bg {
  //   position: absolute;
  //   top: 0;
  //   left: 0;
  //   width: 100%;
  //   height: 100%;
  // }

  &:hover {
    --w-process-designer-node-background-color: var(--w-primary-color-2); // #0099ff;
    --w-process-designer-node-border-color: var(--w-primary-color);
    .text {
      color: var(--w-primary-color);
    }
  }
  &.selected {
    ._highlight();
  }
  &.horizontal {
    .ant-row {
      position: relative;
      &::after {
        display: none;
        // content: '';
        position: absolute;
        right: -48px;
        top: 32px;
        width: 48px;
        height: 10px;
        background-color: var(--w-primary-color);
      }
    }
  }

  .text {
    color: var(--w-text-color-dark);
    font-size: var(--w-font-size-base);
    font-weight: bold;
    display: flex;
    flex-direction: column;
    line-height: 14px;
    border-radius: 4px;
    border: 1px solid var(--w-process-designer-node-border-color);
    background-color: var(--w-process-designer-node-background-color);

    .ant-tag {
      --w-tag-text-size: 10px;
      --w-tag-text-color: var(--w-gray-color-11);
      --w-tag-background: var(--w-gray-color-4);
      --w-tag-border-color: var(--w-gray-color-9);
    }

    &.horizontal {
      padding: 12px 7px 12px 7px;
      border-left: 4px solid var(--w-primary-color);
      border-top-left-radius: 4px;
      border-bottom-left-radius: 4px;

      .node-name {
        flex: 1;
        justify-content: center;
        align-items: center;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
      }

      .horizontal-tag {
        margin-top: 8px;
        .ant-tag {
          --w-tag-height: 18px;
          --w-tag-border-radius: 4px;
          --w-tag-lr-padding: 4px;
          --w-tag-edit-margin-right: 4px;
          cursor: pointer;
          &:last-child {
            --w-tag-edit-margin-right: 0;
          }
        }
      }
    }

    &.vertical {
      border-top: 4px solid var(--w-primary-color);
      border-top-left-radius: 4px;
      border-top-right-radius: 4px;
      padding: 7px 12px 7px 12px;
      flex-direction: row-reverse;
      height: 100%;
      justify-content: center;
      align-items: start;
      .node-name {
        width: 14px;
        height: 100%;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        flex-shrink: 0;
        margin-right: 4px;
        writing-mode: vertical-rl; /* 文字从上到下竖向排列 */
        text-align: left; /* 水平居中 */
        vertical-align: middle; /* 垂直居中 */
        direction: ltr; /* 显式设置文本方向为左至右 */
      }

      .vertical-tag {
        margin-right: 4px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        .ant-tag {
          --w-tag-height: auto;
          --w-tag-edit-margin-right: 0;
          padding: 4px 0;
          margin-bottom: 4px;
          writing-mode: vertical-rl; /* 文字从上到下竖向排列 */
          text-align: center; /* 水平居中 */
          vertical-align: middle; /* 垂直居中 */
          direction: ltr; /* 显式设置文本方向为左至右 */

          .tag-num {
            writing-mode: lr;
          }

          &:last-child {
            margin-bottom: 0;
          }
        }
      }
    }
  }
  @-moz-document url-prefix() {
    .text {
      &.vertical {
        .vertical-tag {
          .ant-tag {
            width: 18px;
            .tag-num {
              writing-mode: sideways-lr;
              transform: rotate(90deg);
            }
          }
        }
      }
    }
  }
}
</style>
