<template>
  <HtmlWrapper :title="vTitle" ref="htmlWrapper" :style="htmlWrapperStyle">
    <a-layout class="widget-design-layout flow-designer-layout">
      <a-layout-header class="flow-designer-header" v-if="showHeader">
        <a-row>
          <a-col :span="12">
            <Icon type="pticon iconfont icon-logo_wellinfo" title="公司LOGO" class="logo" />
            <h1>{{ workflowName }}</h1>
          </a-col>
          <a-col :span="12" :style="{ textAlign: 'right' }">
            <a-button size="small" icon="download" :loading="downloadLoading" @click="downloadFlowChart">下载流程图</a-button>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout :hasSider="true" class="flow-designer-content">
        <a-layout-content id="design-main" class="flow-designer-main">
          <div class="graph-full" style="height: 100%">
            <div class="spin-center" v-if="spinning">
              <a-spin />
            </div>
            <div class="graph-wrapper">
              <div id="graph-container"></div>
            </div>
          </div>
        </a-layout-content>
      </a-layout>
    </a-layout>
  </HtmlWrapper>
</template>

<script>
import '@pageAssembly/app/web/assets/css/design.less';
import './component/style/designer.less';
import './component/style/graph.less';
import FlowDesigner from './component/designer/FlowDesigner';
import WorkFlow from './component/designer/WorkFlow';
import FlowProperty from './component/designer/FlowProperty';
import constant, { propertyAddKey } from './component/designer/constant';
import { transformGraphData } from './component/designer/utils';

export default {
  name: 'WorkflowViewer',
  props: {
    viewerProp: Object,
    htmlWrapperStyle: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    const designer = new FlowDesigner();
    let workFlow = new WorkFlow();
    let flowProperty = new FlowProperty();
    propertyAddKey.forEach(key => {
      flowProperty[key] = workFlow[key];
    });
    workFlow.property = flowProperty;
    return {
      designer,
      graphItem: undefined,
      workFlow,
      graph: {
        instance: undefined
      },
      themeClass: '',
      flowStateInfo: undefined, // 流程状态
      spinning: true,
      downloadLoading: false
    };
  },
  provide() {
    return {
      designer: this.designer,
      workFlowData: this.workFlow,
      graph: this.graph
    };
  },
  computed: {
    vTitle() {
      let title = '流程查阅';
      if (this.workFlow.property.name) {
        title += ' - ' + this.workFlow.property.name;
      }
      return title;
    },
    workflowName() {
      let title = '流程查阅';
      if (this.workFlow.property.name) {
        title = this.workFlow.property.name;
      }
      if (this.workFlow.property.version) {
        title = `${title} v${this.workFlow.property.version}`;
      } else {
        if (this.workFlow.property.name) {
          title = `${title} v1.0`;
        }
      }
      return title;
    }
  },
  created() {
    if (this.viewerProp) {
      this.workFlowData = this.viewerProp.workFlowData;
      this.flowInstUuid = this.viewerProp.flowInstUuid;
      this.showHeader = this.viewerProp.showHeader;
      //   workFlowData: Object,
      // flowInstUuid: String,
      // showHeader: {
      //   type: Boolean,
      //   default: true
      // },
    }
    if (this.workFlowData) {
      const data = JSON.parse(JSON.stringify(this.workFlowData));
      for (const key in data) {
        this.workFlow[key] = data[key];
        if (propertyAddKey.includes(key) && data[key]) {
          data.property[key] = data[key];
        }
      }
    }
  },
  mounted() {
    this.themeClass = this.$refs.htmlWrapper.baseClass;

    this.initGraph();
    window.addEventListener('resize', this.resizeGraph);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeGraph);
  },
  methods: {
    // 窗口改变重置画布
    resizeGraph() {
      const graphFull = this.$el.querySelector('.graph-full');
      const elH = this.$el.offsetHeight;
      let headerEl;
      if (this.showHeader) {
        headerEl = this.$el.querySelector('.flow-designer-header');
      }
      if (this.graphItem && graphFull) {
        const { graph } = this.graphItem;
        const width = graphFull.clientWidth;
        let height = elH;
        if (headerEl) {
          height = height - headerEl.offsetHeight;
        }
        graph.resize(width, height);
      }
    },
    initGraph() {
      import('./component/graph/index').then(res => {
        const FlowGraph = res.default;
        this.graphItem = new FlowGraph({
          designer: this.designer,
          themeClass: this.themeClass,
          centerContent: true
        });
        this.graph.instance = this.graphItem;
        this.graphItem.setSelectedEdge = function () {};
        this.graphItem.showNodePorts = function () {};
        const { graph } = this.graphItem;
        graph.options.interacting.nodeMovable = false;

        // if (!this.workFlow.graphData) {
        //   // 62旧数据转换
        //   const graphData = transformGraphData(this.workFlow, this.graphItem);
        //   this.workFlow.graphData = JSON.stringify(graphData);
        // }
        if (this.workFlow.graphData) {
          const graphJson = JSON.parse(this.workFlow.graphData);
          if (this.flowInstUuid) {
            this.setFlowState(graphJson).then(res => {
              this.graphItem.fromJSON(res);
              this.spinning = false;
            });
          } else {
            graphJson.cells.forEach(cell => {
              this.setCellI18n(cell);
            });
            this.graphItem.fromJSON(graphJson);
            this.spinning = false;
          }
        }
      });
    },
    // 根据流程实例获取办理状态信息
    fetchFlowStateInfo(flowInstUuid) {
      const params = {
        flowInstUuid
      };
      return new Promise((resolve, reject) => {
        $axios
          .get('/api/workflow/definition/getFlowHandingStateInfo', {
            params
          })
          .then(res => {
            if (res.status === 200) {
              if (res.data && res.data.code === 0) {
                const data = res.data.data;
                resolve(data);
              } else {
                reject(res.data);
              }
            } else {
              reject(res);
            }
          });
      });
    },
    // 设置流程状态
    setFlowState(graphJson) {
      return new Promise((resolve, reject) => {
        this.fetchFlowStateInfo(this.flowInstUuid).then(flowStateInfo => {
          this.flowStateInfo = flowStateInfo;

          const started = flowStateInfo['start']['started'];
          if (!started) {
            if (graphJson.cells) {
              graphJson.cells.forEach(cell => {
                this.setCellI18n(cell);
              });
            }
            resolve(graphJson);
            return;
          }
          // 创建未办边的样式
          const createEdgeUndoAttr = () => {
            return {
              line: {
                class: 'edge-direction-undo',
                stroke: '',
                strokeWidth: '',
                targetMarker: {
                  stroke: 'var(--workflow-edge-undo-stroke)',
                  strokeWidth: 'var(--workflow-edge-undo-width)'
                }
              }
            };
          };

          let cellsMap = {};
          let endCompletedFromId = ''; // 从那个环节结束
          let endCompletedId = '';
          let nodeTodoId = ''; // 待办的节点id
          let edgeHighlightIds = []; // 高亮的边id
          let nodeHighlightIds = [];

          const { cells } = graphJson;

          for (let index = 0; index < flowStateInfo.ends.length; index++) {
            const item = flowStateInfo.ends[index];
            if (item.completed) {
              endCompletedFromId = item.fromId;
              break;
            }
          }
          // 获取代办节点id
          const getTodoId = (type = 'tasks') => {
            let todoId = '';
            for (const key in flowStateInfo[type]) {
              const task = flowStateInfo[type][key];
              if (task && task.state === 'todo') {
                todoId = key;
              }
            }
            return todoId;
          };

          if (!endCompletedFromId) {
            let type = 'tasks';
            let todoId = getTodoId();
            if (!todoId) {
              type = 'subflows';
              todoId = getTodoId(type);
            }
            if (todoId) {
              nodeTodoId = todoId;
            }
          }
          // 设置高亮边
          const setEdgeHighlight = (stateId, stateItem) => {
            for (let index = 0; index < cells.length; index++) {
              const cell = cells[index];
              const id = cell.id;
              const shape = cell.shape;
              const data = cell.data;
              const dataId = data.id;
              if (shape !== 'EdgeDirection') {
                continue;
              }
              if (shape === 'EdgeDirection') {
                const sourceId = cell.source.cell;
                const targetId = cell.target.cell;
                const fromID = data.fromID;
                const toID = data.toID;

                let formState = flowStateInfo['tasks'][fromID];
                if (!formState) {
                  formState = flowStateInfo['subflows'][fromID];
                }
                let toState = flowStateInfo['tasks'][toID];
                if (!toState) {
                  toState = flowStateInfo['subflows'][toID];
                }

                if (data.type === '3') {
                  if (fromID === stateId && formState && formState['state'] === 'done') {
                    edgeHighlightIds.push(id);
                    nodeHighlightIds.push(...[sourceId, targetId]);
                  }

                  if (stateItem.preGatewayIds && stateItem.preGatewayIds.includes(fromID) && stateItem.preGatewayIds.includes(toID)) {
                    edgeHighlightIds.push(id);
                    nodeHighlightIds.push(...[sourceId, targetId]);
                  }
                }

                if (stateItem.preTaskIds && stateItem.preTaskIds.length) {
                  if (data.type === '2' && toID === stateId) {
                    edgeHighlightIds.push(id);
                    nodeHighlightIds.push(...[sourceId, targetId]);
                  }
                  if (stateItem.preTaskIds.includes(fromID) && toID === stateId) {
                    edgeHighlightIds.push(id);
                    nodeHighlightIds.push(...[sourceId, targetId]);
                  }
                }
              }
            }
          };

          for (let index = 0; index < cells.length; index++) {
            const cell = cells[index];
            const id = cell.id;
            const shape = cell.shape;
            const data = cell.data;
            const dataId = data.id;
            cellsMap[id] = cell;

            if (shape === 'NodeLabel' || shape === 'NodeSwimlane') {
              continue;
            }

            let stateItem = flowStateInfo['tasks'][dataId];
            if (!stateItem) {
              stateItem = flowStateInfo['subflows'][dataId];
            }
            if (stateItem) {
              setEdgeHighlight(dataId, stateItem);
            }

            if (shape === 'EdgeDirection') {
              const sourceId = cell.source.cell;
              const targetId = cell.target.cell;
              const fromID = data.fromID;
              const toID = data.toID;

              if (fromID === endCompletedFromId) {
                endCompletedId = targetId;
                edgeHighlightIds.push(id);
                nodeHighlightIds.push(targetId);
              } else {
                if (fromID === constant.StartFlowId) {
                  edgeHighlightIds.push(id);
                  nodeHighlightIds.push(sourceId);
                } else {
                  cell.attrs = createEdgeUndoAttr();
                }
              }
            } else {
              data['state'] = 'undo';
            }

            if (shape === 'NodeCircle') {
              if (dataId === constant.StartFlowId && started) {
                data['state'] = 'done';
              }
            } else if (shape === 'NodeSubflow') {
              data['state'] = flowStateInfo['subflows'][dataId]['state'];
            } else {
              if (flowStateInfo['tasks'][dataId]) {
                data['state'] = flowStateInfo['tasks'][dataId]['state'];
              }
            }
          }

          cells.forEach(cell => {
            const id = cell.id;
            const shape = cell.shape;
            const data = cell.data;
            const dataId = data.id;
            this.setCellI18n(cell);
            if (nodeHighlightIds.includes(id)) {
              data['state'] = 'done';
            }
            if (edgeHighlightIds.includes(id)) {
              delete cell.attrs;
            }
          });

          console.log(nodeHighlightIds, edgeHighlightIds);
          resolve(graphJson);
        });
      });
    },
    setCellI18n(cell) {
      const shape = cell.shape;
      const data = cell.data;
      if (shape === 'EdgeDirection') {
        const i18Name = this.$t(`WorkflowView.${data.id}.directionName`, data.name);
        data.name = i18Name;
        if (cell['labels'] && cell['labels'].length) {
          cell['labels'][0] = i18Name;
        }
      } else if (shape === 'NodeCondition') {
        const i18Name = this.$t(`WorkflowView.${data.id}.conditionName`, data.name || data.conditionName);
        data.name = i18Name;
        if (cell['label']) {
          cell['label'] = i18Name;
        }
      } else if (shape === 'NodeSwimlane' || shape === 'NodeLabel') {
      } else if (shape === 'NodeCircle') {
        let i18Name;
        if (data.id === constant.StartFlowId) {
          i18Name = this.$t(`WorkflowWork.startTaskName`, data.name);
        } else if (data.id === constant.EndFlowId) {
          i18Name = this.$t(`WorkflowWork.endTaskName`, data.name);
        }
        data.name = i18Name;
      } else {
        const i18Name = this.$t(`WorkflowView.${data.id}.taskName`, data.name);
        data.name = i18Name;
        if (cell['label']) {
          cell['label'] = i18Name;
        }
      }
    },
    // 设置流程状态
    setFlowStat2(graphJson) {
      return new Promise((resolve, reject) => {
        this.fetchFlowStateInfo(this.flowInstUuid).then(flowStateInfo => {
          this.flowStateInfo = flowStateInfo;

          let taskUndoIds = []; // 未办的环节id
          let taskTodoIds = []; // 待办的环节id
          let taskDoneIds = []; // 已办的环节id
          for (const key in flowStateInfo.tasks) {
            const state = flowStateInfo.tasks[key]['state'];
            if (state === 'undo') {
              taskUndoIds.push(key);
            } else if (state === 'todo') {
              taskTodoIds.push(key);
            } else if (state === 'done') {
              taskDoneIds.push(key);
            }
          }

          let subflowUndoIds = []; // 未办的子流程id
          let subflowTodoIds = [];
          for (const key in flowStateInfo.subflows) {
            const state = flowStateInfo.subflows[key]['state'];
            if (state === 'undo') {
              subflowUndoIds.push(key);
            } else if (state === 'todo') {
              subflowTodoIds.push(key);
            }
          }

          let endUndoToIds = []; // 未办的结束节点id
          let endUndoFromIds = []; // 未办的结束节点来源id
          let endCompletedFromIds = []; // 从哪个节点走完流程
          flowStateInfo.ends.forEach(node => {
            if (node.completed) {
              endCompletedFromIds.push(node.fromId);
            } else {
              endUndoFromIds.push(node.fromId);
            }
          });

          let todoToIds = []; // 待办指向的目标id (下一个环节id)
          let allUndoIds = [];
          const { cells } = graphJson;
          for (let index = 0; index < cells.length; index++) {
            const cell = cells[index];

            const id = cell.id;
            const shape = cell.shape;
            const data = cell.data;
            const dataId = data.id;
            if (shape === 'NodeCircle' || shape === 'NodeCondition' || shape === 'NodeSwimlane') {
              continue;
            }
            if (shape === 'EdgeDirection') {
              const fromID = data.fromID;
              const toID = data.toID;
              if (
                taskUndoIds.includes(toID) ||
                taskUndoIds.includes(fromID) ||
                taskTodoIds.includes(fromID) ||
                endUndoFromIds.includes(fromID) ||
                subflowUndoIds.includes(toID)
              ) {
                allUndoIds = [...allUndoIds, ...[cell.source.cell, cell.target.cell]];
                cell.attrs = {
                  line: {
                    class: 'edge-direction-undo',
                    stroke: '',
                    strokeWidth: '',
                    targetMarker: {
                      stroke: 'var(--workflow-edge-undo-stroke)',
                      strokeWidth: 'var(--workflow-edge-undo-width)'
                    }
                  }
                };
                if (endUndoFromIds.includes(fromID)) {
                  endUndoToIds.push(cell.target.cell);
                } else if (taskTodoIds.includes(fromID)) {
                  todoToIds.push(cell.target.cell);
                }
              }
            } else if (shape === 'NodeSubflow') {
              data['state'] = flowStateInfo['subflows'][dataId]['state'];
            } else {
              data['state'] = flowStateInfo['tasks'][dataId || id]['state'];
            }
          }

          // 未办的结束节点
          cells.forEach(node => {
            const id = node.id;
            const shape = node.shape;
            const data = node.data;
            const dataId = data.id;
            if (shape === 'NodeCondition') {
              if (allUndoIds.includes(id)) {
                data['state'] = 'undo';
              }
            } else if (dataId === constant.EndFlowId && endUndoToIds.includes(id)) {
              data['state'] = 'undo';
            } else if (todoToIds.includes(id)) {
              data['state'] = 'undo';
            }
          });

          resolve(graphJson);
        });
      });
    },
    // 下载流程图
    downloadFlowChart(args = {}) {
      const ele = document.querySelector('#graph-container');
      const copyEle = ele.cloneNode(true); // 包括子节点
      copyEle.id = 'graph-container-copy';
      ele.parentNode.insertBefore(copyEle, ele);
      this.graphItem.zoomToFit({ padding: 20 });

      this.downloadLoading = true;
      require('./component/designer/tools')
        .downloadFlow({ picName: args.picName || this.workflowName })
        .then(() => {
          this.graphItem.zoomTo(1);
          copyEle.remove();

          this.downloadLoading = false;

          if (args.callback && typeof args.callback === 'function') {
            args.callback();
          }
        });
    },
    // 删除多个连接桩
    removePorts() {
      const nodes = this.graphItem.getNodes();
      nodes.forEach(node => {
        const ports = node.getPorts();
        node.removePorts(ports);
      });
    }
  }
};
</script>
