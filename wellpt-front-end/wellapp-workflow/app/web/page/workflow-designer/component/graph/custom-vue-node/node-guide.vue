<template>
  <div class="node-guide-container">
    <svg width="100%" height="100%" class="node-guide-svg">
      <rect
        width="100%"
        height="100%"
        fill="none"
        stroke="var(--workflow-edge-dash-stroke)"
        stroke-width="var(--workflow-edge-stroke-width)"
        stroke-dasharray="var(--workflow-edge-stroke-dash-array)"
        rx="7"
      />
    </svg>
    <div class="node-task-container" :style="taskStyle" @click="handleAddTask">环节</div>
    <div class="node-condition-subflow">
      <div class="node-robot-container" :style="taskStyle" @click="handleAddRobot">
        <div class="node-robot-left-rect"><i class="iconfont icon-jiqi-01"></i></div>
        <div class="node-robot-name">机器</div>
      </div>
      <div class="node-collab-container" :style="taskStyle" @click="handleAddCollab">
        <div class="node-collab-left-rect"><i class="iconfont icon-xiezuo-01"></i></div>
        <div class="node-collab-name">协作</div>
      </div>
    </div>
    <div class="node-condition-subflow">
      <div class="node-condition-container" :style="conditionStyle" @click="handleAddCondition">
        <div class="node-diamond-container"></div>
        <div class="node-condition-name">判断点</div>
      </div>
      <div class="node-subflow-container" :style="subflowStyle" @click="handleAddSubflow">
        <div class="node-subflow-circle"><i class="iconfont icon-oa-banliguocheng"></i></div>
        <div class="node-subflow-name">子流程</div>
      </div>
    </div>
    <!-- <a-icon type="close-circle" class="node-guide-close" @click="removeCells" /> -->
  </div>
</template>

<script>
import { sGetNewTaskID, sGetNewID } from '../../designer/utils';
import constant from '../../designer/constant';
export default {
  name: 'NodeGuide',
  inject: ['getNode', 'getGraph', 'graphInstance'],
  data() {
    let nodeData = {};
    const node = this.getNode();
    const graph = this.getGraph();
    if (node) {
      nodeData = node.getData();
    }
    return {
      taskStyle: {
        width: constant.NodeTaskWidth + 'px',
        height: constant.NodeTaskHeight + 'px'
      },
      conditionStyle: {
        width: constant.NodeConditionWidth + 'px',
        height: constant.NodeConditionHeight + 'px'
      },
      subflowStyle: {
        width: constant.NodeSubflowWidth + 'px',
        height: constant.NodeSubflowHeight + 'px'
      },
      graph,
      node,
      nodeData
    };
  },
  methods: {
    // 增加环节
    handleAddTask(event) {
      const nodeType = 'TASK';
      const nodeId = sGetNewTaskID({
        type: nodeType,
        nodes: this.graph.getNodes()
      });

      this.addCellCommon({ nodeType, nodeId });

      this.removeCells();
    },
    // 增加判断点
    handleAddCondition() {
      this.removeCells();

      const nodeType = 'CONDITION';
      const nodeId = sGetNewID({
        prefix: 'Condition',
        cells: this.graph.getCells()
      });

      this.addCellCommon({ nodeType, nodeId });
    },
    // 增加子流程
    handleAddSubflow() {
      this.removeCells();

      const nodeType = 'SUBFLOW';
      const nodeId = sGetNewTaskID({
        type: nodeType,
        nodes: this.graph.getNodes()
      });

      this.addCellCommon({ nodeType, nodeId });
    },
    // 添加机器节点
    handleAddRobot() {
      const nodeType = 'ROBOT';
      const nodeId = sGetNewID({
        prefix: 'Robot',
        cells: this.graph.getCells()
      });

      this.addCellCommon({ nodeType, nodeId });
      this.removeCells();
    },
    // 添加协作节点
    handleAddCollab() {
      const nodeType = 'COLLAB';
      const nodeId = sGetNewID({
        prefix: 'Collab',
        cells: this.graph.getCells()
      });

      this.addCellCommon({ nodeType, nodeId });
      this.removeCells();
    },
    addCellCommon({ nodeType, nodeId }) {
      const { sourceWay, targetWay, sourceNode } = this.nodeData;
      const nodeBBox = sourceNode.getBBox();
      const { point } = this.graphInstance.getOffsetPointByWay({
        sourceBBox: nodeBBox,
        sourceWay,
        targetSize: {
          width: constant.NodeDefaultWidth,
          height: constant.NodeDefaultHeight
        }
      });

      this.graphInstance.checkAddCellType({
        x: point.x,
        y: point.y,
        type: nodeType,
        id: nodeId
      });

      this.graph.startBatch('custom-add-edge');
      const newEdge = this.graphInstance.createEdgeDirection({
        source: { id: sourceNode.id },
        target: { id: nodeId },
        edgeWay: `${sourceWay};${targetWay}`
      });
      this.graph.addEdge(newEdge);
      this.graphInstance.setEdgeData(newEdge);
      this.graph.stopBatch('custom-add-edge');
    },
    getNodePoint(event) {
      const clientRect = event.target.getBoundingClientRect();
      const nodePoint = this.graph.pageToLocal(clientRect.x, clientRect.y);
      return nodePoint;
    },
    removeCells() {
      const { port, edgeDash } = this.nodeData;
      this.graph.removeCells([this.node]);
      this.graphInstance.resetGuideInfo();
    }
  }
};
</script>
