<template>
  <div class="simulation-workflow-viewer">
    <!-- <WorkflowViewer
      ref="workflowViewer"
      :htmlWrapperStyle="{ height: '100%' }"
      :viewerProp="{
        showHeader: false,
        workFlowData: flowDefinition,
        flowInstUuid: flowInstUuid
      }"
    /> -->
    <div style="width: 100%; height: 100%">
      <div id="graph-container"></div>
    </div>
    <div class="simulation-state" v-if="simulation.state">
      <span>仿真状态:</span>
      <span v-if="simulation.state.code == 'inited'">未开始</span>
      <span v-if="simulation.state.code == 'start' || simulation.state.code == 'running'">{{ simulation.state.tip }}</span>
      <span v-if="simulation.state.code == 'pause'">已暂停({{ simulation.simulationData.taskName }})</span>
      <span v-if="simulation.state.code == 'success'">仿真成功</span>
      <span v-if="simulation.state.code == 'error'">仿真失败</span>
    </div>
  </div>
</template>

<script>
// import WorkflowViewer from '../../workflow-designer/viewer.vue';
import '@pageAssembly/app/web/assets/css/design.less';
import '../../workflow-designer/component/style/designer.less';
import '../../workflow-designer/component/style/graph.less';
import FlowDesigner from '../../workflow-designer/component/designer/FlowDesigner';
// import { register } from '@antv/x6-vue-shape';
import SimulationNodeTask from './graph-task-node/simulation-node-task.vue';
import SimulationNodeCollab from './graph-task-node/simulation-node-collab.vue';
import SimulationNodeSubflow from './graph-task-node/simulation-node-subflow.vue';
import { ports } from '../../workflow-designer/component/graph/ports';
import constant from '../../workflow-designer/component/designer/constant';
import { isEmpty } from 'lodash';
export default {
  props: {
    flowDefinition: Object,
    flowInstUuid: String
  },
  components: {},
  inject: ['simulation'],
  data() {
    const designer = new FlowDesigner();
    return {
      designer,
      themeClass: ''
    };
  },
  watch: {
    'simulation.simulationData': {
      deep: true,
      handler(newVal, oldVal) {
        if (newVal.handingStateInfo) {
          this.updateHandingState();
        }
      }
    }
  },
  mounted() {
    this.initGraph();
  },
  methods: {
    initGraph() {
      const _this = this;
      Promise.all([import('../../workflow-designer/component/graph/index'), import('@antv/x6-vue-shape')]).then(results => {
        let res = results[0];
        let register = results[1].register;
        const FlowGraph = res.default;
        _this.flowGraph = new FlowGraph({
          designer: _this.designer,
          themeClass: _this.themeClass
        });
        _this.registerNode(_this.designer, _this.themeClass, _this.flowGraph, register);

        _this.flowGraph.setSelectedEdge = function () {};
        _this.flowGraph.showNodePorts = function () {};
        const { graph } = _this.flowGraph;
        graph.options.interacting.nodeMovable = false;
        const graphJson = JSON.parse(_this.flowDefinition.graphData);
        let startNode = null;
        graphJson.cells.forEach(cell => {
          if (cell.shape == 'NodeTask') {
            cell.shape = 'SimulationNodeTask';
          } else if (cell.shape == 'NodeCollab') {
            cell.shape = 'SimulationNodeCollab';
          } else if (cell.shape == 'NodeSubflow') {
            cell.shape = 'SimulationNodeSubflow';
          } else if (cell.shape === 'NodeCircle') {
            if (cell.data && cell.data.id === constant.StartFlowId) {
              startNode = cell;
            }
          }

          if (cell.shape === 'EdgeDirection') {
            // cell.attrs = {
            //   line: {
            //     class: 'edge-direction-undo',
            //     stroke: '',
            //     strokeWidth: '',
            //     targetMarker: {
            //       stroke: 'var(--workflow-edge-undo-stroke)',
            //       strokeWidth: 'var(--workflow-edge-undo-width)'
            //     }
            //   }
            // };
          } else if (cell.data) {
            cell.data.state = 'done';
          } else {
            // cell.data.state = 'todo';
          }
        });
        if (startNode) {
          _this.flowGraph.setStartNodePosition(startNode.position);
        }
        _this.graphJson = graphJson;
        _this.flowGraph.fromJSON(graphJson);
        _this.updateHandingState();
        // this.loadHandingState().then(handingStateInfo => {
        //   this.initStartState();
        //   this.simulation.simulationData.handingStateInfo = handingStateInfo;
        //   this.updateHandingState();
        // });
      });
    },
    registerNode(designer, themeClass, graphInstance, register) {
      let simulation = this.simulation;
      register({
        shape: 'SimulationNodeTask',
        width: constant.NodeTaskWidth,
        height: constant.NodeTaskHeight,
        attrs: {
          body: {
            class: themeClass
          }
        },
        component: {
          provide() {
            return {
              designer: designer,
              simulation
            };
          },
          render: h => h(SimulationNodeTask)
        },
        ports: { ...ports }
      });
      // 协作节点
      register({
        shape: 'SimulationNodeCollab',
        width: constant.NodeDefaultWidth,
        height: constant.NodeDefaultHeight,
        attrs: {
          body: {
            class: themeClass
          }
        },
        component: {
          provide() {
            return {
              graphInstance,
              simulation
            };
          },
          render: h => h(SimulationNodeCollab)
        },
        ports: { ...ports }
      });
      // 子流程
      register({
        shape: 'SimulationNodeSubflow',
        width: constant.NodeSubflowWidth,
        height: constant.NodeSubflowHeight,
        attrs: {
          body: {
            class: themeClass
          }
        },
        component: {
          provide() {
            return {
              designer: designer,
              simulation
            };
          },
          render: h => h(SimulationNodeSubflow)
        },
        ports: { ...ports }
      });
    },
    initReadyState() {
      const _this = this;
      let cells = _this.graphJson.cells || [];
      cells.forEach(cellJson => {
        let cell = _this.flowGraph.graph.getCellById(cellJson.id);
        if (cell && cell.shape === 'EdgeDirection') {
          cell.attr('line/class', 'edge-direction');
          cell.attr('line/targetMarker', {
            stroke: 'var(--workflow-edge-stroke)',
            strokeWidth: 'var(--workflow-edge-width)'
          });
        } else {
          cell && (cell.data.state = 'done');
        }
      });
    },
    initStartState(startTaskId = '<StartFlow>') {
      const _this = this;
      let cells = _this.graphJson.cells || [];
      cells.forEach(cellJson => {
        let cell = _this.flowGraph.graph.getCellById(cellJson.id);
        if (cell && cell.shape === 'EdgeDirection') {
          cell.attr('line/class', 'edge-direction-undo');
          cell.attr('line/targetMarker', {
            stroke: 'var(--workflow-edge-undo-stroke)',
            strokeWidth: 'var(--workflow-edge-undo-width)'
          });
        } else {
          cell && (cell.data.state = 'undo');
        }
      });

      let startCellJson = cells.find(cell => cell.data && cell.data.id == startTaskId);
      if (startCellJson) {
        if (startTaskId == '<StartFlow>') {
          let startCell = _this.flowGraph.graph.getCellById(startCellJson.id);
          if (startCell) {
            startCell.data.state = 'done';
            let fromEdges = cells.filter(cell => {
              return cell.shape == 'EdgeDirection' && cell.data && cell.data.fromID == startTaskId;
            });
            _this.highlightEdges(fromEdges);
            fromEdges.forEach(edge => {
              let targetCell = _this.flowGraph.graph.getCellById(edge.target.cell);
              targetCell && (targetCell.data.state = 'todo');
            });
          }
        } else {
          let startCell = _this.flowGraph.graph.getCellById(startCellJson.id);
          if (startCell) {
            startCell.data.state = 'todo';
          }
        }
      }
    },
    updateHandingState() {
      const _this = this;
      let handingStateInfo = _this.simulation.simulationData && _this.simulation.simulationData.handingStateInfo;
      if (handingStateInfo) {
        let todoTaskIds = _this.getTodoTaskIds(handingStateInfo);
        _this.todoTaskIds = todoTaskIds;

        if (_this.hasEnd(handingStateInfo)) {
          let ends = handingStateInfo.ends.filter(end => end.completed);
          ends.forEach(end => {
            _this.highlightHandingState({ fromTaskId: end.fromId, directionId: end.toDirectionId, toId: '<EndFlow>', handingStateInfo });
          });
        }

        if (todoTaskIds.length) {
          todoTaskIds.forEach(taskId => {
            let taskInfo = handingStateInfo.tasks[taskId] || handingStateInfo.subflows[taskId];
            let preTaskIds = taskInfo.preTaskIds || [];
            preTaskIds.forEach(preTaskId => {
              _this.highlightHandingState({ fromTaskId: preTaskId, gatewayIds: taskInfo.preGatewayIds, toId: taskId, handingStateInfo });
            });
            let cell = _this.flowGraph.graph.getCellById(taskId);
            if (!cell) {
              cell = _this.flowGraph.graph.getCells().find(cell => {
                if (cell.shape != 'SimulationNodeTask') {
                  return false;
                }
                return cell.data.id == taskId;
              });
            }
            cell && cell.data && (cell.data.state = 'todo');
          });
        } else if (!_this.hasEnd(handingStateInfo)) {
          // 运行到指定环节结束
          let latestDoneTasks = {};
          let preTaskIds = [];
          for (let taskId in handingStateInfo.tasks) {
            let task = handingStateInfo.tasks[taskId];
            if (task.state == 'done') {
              latestDoneTasks[taskId] = task;
              preTaskIds = [...preTaskIds, ...task.preTaskIds];
            }
          }
          for (let taskId in handingStateInfo.subflows) {
            let task = handingStateInfo.subflows[taskId];
            if (task.state == 'done') {
              latestDoneTasks[taskId] = task;
              preTaskIds = [...preTaskIds, ...task.preTaskIds];
            }
          }
          preTaskIds.forEach(preTaskId => delete latestDoneTasks[preTaskId]);
          for (let taskId in latestDoneTasks) {
            let taskInfo = latestDoneTasks[taskId];
            let preTaskIds = taskInfo.preTaskIds || [];
            preTaskIds.forEach(preTaskId => {
              _this.highlightHandingState({ fromTaskId: preTaskId, gatewayIds: taskInfo.preGatewayIds, toId: taskId, handingStateInfo });
            });
          }

          let cells = _this.graphJson.cells.filter(cell => cell.shape != 'EdgeDirection');
          cells.forEach(cellJson => {
            let cell = _this.flowGraph.graph.getCellById(cellJson.id);
            if (cell && cell.data && cell.data.state == 'todo') {
              cell.data.state = 'done';
            }
          });
        }
      }
    },
    loadHandingState() {
      // return $axios
      //   .get('/proxy/api/workflow/definition/getFlowHandingStateInfo?flowInstUuid=235337017691471872')
      //   .then(({ data: result }) => {
      //     return result.data;
      //   });
    },
    hasEnd(handingStateInfo) {
      let ends = handingStateInfo.ends;
      if (isEmpty(ends)) {
        return false;
      }
      return ends.filter(end => end.completed == true).length;
    },
    getTodoTaskIds(handingStateInfo) {
      let todoTaskIds = [];
      let tasks = handingStateInfo.tasks;
      let subflows = handingStateInfo.subflows;
      for (let taskId in tasks) {
        if (tasks[taskId].state == 'todo') {
          todoTaskIds.push(taskId);
        }
      }
      for (let taskId in subflows) {
        if (subflows[taskId].state == 'todo') {
          todoTaskIds.push(taskId);
        }
      }
      return todoTaskIds;
    },
    highlightHandingState({ fromTaskId, gatewayIds = [], directionId, toId, handingStateInfo, checkedInfo = {} }) {
      const _this = this;
      let taskId = fromTaskId;
      if (checkedInfo[`${taskId}_${gatewayIds}_${toId}`]) {
        return;
      }
      checkedInfo[`${taskId}_${gatewayIds}_${toId}`] = true;

      let cellInfo = _this.getToEdgeAndNode(taskId, gatewayIds, directionId, toId);
      if (cellInfo.edges) {
        _this.highlightEdges(cellInfo.edges);
      }
      if (cellInfo.nodes) {
        _this.highlightNodes(cellInfo.nodes);
      }

      // 当前节点
      let node = _this.getNodeById(taskId);
      if (node) {
        _this.highlightNodes([node]);
      }

      // 前一节点
      let preTaskIds = [];
      let preGatewayIds = [];
      let taskInfo = handingStateInfo.tasks[taskId] || handingStateInfo.subflows[taskId];
      if (taskInfo && taskInfo.preTaskIds && taskInfo.preTaskIds.length) {
        preTaskIds = taskInfo.preTaskIds;
        preGatewayIds = taskInfo.preGatewayIds || [];
      } else if (taskId != '<StartFlow>') {
        preTaskIds = ['<StartFlow>'];
        preGatewayIds = [];
      }
      if (preTaskIds.length) {
        preTaskIds.forEach(preTaskId => {
          _this.highlightHandingState({
            fromTaskId: preTaskId,
            gatewayIds: preGatewayIds,
            toId: taskId,
            handingStateInfo,
            checkedInfo
          });
        });
      }
    },
    getNodeById(id) {
      return this.graphJson.cells.find(cell => cell.shape != 'EdgeDirection' && cell.data && cell.data.id == id);
    },
    getToEdgeAndNode(fromId, gatewayIds = [], directionId, toId) {
      const _this = this;
      let cells = _this.graphJson.cells || [];
      let fromEdges = cells.filter(cell => {
        return cell.shape == 'EdgeDirection' && cell.data && cell.data.fromID == fromId;
      });
      let edgeAndNodes = {
        edges: [],
        nodes: []
      };

      // 存在网关的边，取经过网关的边
      let gatewayEdge = fromEdges.filter(edge => gatewayIds.includes(edge.target.cell));
      if (gatewayEdge.length) {
        fromEdges = gatewayEdge;
      }

      // 存在指定的流向ID
      if (directionId && fromEdges.length) {
        fromEdges = fromEdges.filter(edge => edge.id == directionId);
      }

      fromEdges.forEach(edge => {
        let cellInfo = {
          edges: [],
          nodes: [],
          match: false,
          checkEdgeIds: []
        };
        _this.findEdgeAndNode(edge, gatewayIds, toId, cellInfo, cells);
        if (cellInfo.match) {
          edgeAndNodes.edges = [...edgeAndNodes.edges, ...cellInfo.edges];
          edgeAndNodes.nodes = [...edgeAndNodes.nodes, ...cellInfo.nodes];
        }
      });
      return edgeAndNodes;
    },
    findEdgeAndNode(edge, gatewayIds = [], toId, cellInfo, cells) {
      const _this = this;
      if (cellInfo.match) {
        return;
      }
      if (cellInfo.checkEdgeIds.includes(edge.id)) {
        return;
      }
      cellInfo.checkEdgeIds.push(edge.id);

      let targetCellId = edge.target.cell;
      let targetCell = cells.find(cell => cell.id == targetCellId);
      if (
        toId == targetCellId ||
        (targetCellId != edge.data.toID && toId == edge.data.toID) ||
        (toId == '<EndFlow>' && toId == edge.data.toID)
      ) {
        cellInfo.edges.push(edge);
        cellInfo.nodes.push(targetCell);
        cellInfo.match = true;
      } else if (targetCell.shape == 'NodeCondition') {
        cellInfo.edges.push(edge);
        cellInfo.nodes.push(targetCell);
        let fromId = targetCell.data.id;
        let fromEdges = cells.filter(cell => {
          return cell.shape == 'EdgeDirection' && cell.data && cell.data.fromID == fromId;
        });

        // 存在网关的边，取经过网关的边
        let gatewayEdge = fromEdges.filter(edge => gatewayIds.includes(edge.target.cell));
        if (gatewayEdge.length) {
          fromEdges = gatewayEdge;
        }

        fromEdges.forEach(edge => {
          _this.findEdgeAndNode(edge, gatewayIds, toId, cellInfo, cells);
        });
      } else {
        cellInfo.match = false;
      }
    },
    highlightEdges(edges = []) {
      const _this = this;
      edges.forEach(edge => {
        let cell = _this.flowGraph.graph.getCellById(edge.id);
        cell && cell.attr('line/class', 'edge-direction');
        cell && cell.attr('line/targetMarker', { stroke: 'var(--workflow-edge-stroke)', strokeWidth: 'var(--workflow-edge-stroke-width)' });
      });
    },
    highlightNodes(nodes = []) {
      const _this = this;
      nodes.forEach(node => {
        let cell = _this.flowGraph.graph.getCellById(node.id);
        if (_this.todoTaskIds && _this.todoTaskIds.includes(node.id) && cell.data.state == 'todo') {
          return;
        }
        cell && (cell.data.state = 'done');
      });
    }
  }
};
</script>

<style lang="less" scoped>
.simulation-workflow-viewer {
  height: e('calc(100vh - 90px)');
  position: relative;

  .simulation-state {
    height: 90px;
    padding: 8px;
  }
}
</style>
