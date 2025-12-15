import { Graph, Shape } from '@antv/x6';
import { Selection } from '@antv/x6-plugin-selection';
import { Snapline } from '@antv/x6-plugin-snapline';
import { Keyboard } from '@antv/x6-plugin-keyboard';
import { Clipboard } from '@antv/x6-plugin-clipboard';
import { History } from '@antv/x6-plugin-history';
import { Export } from '@antv/x6-plugin-export';
import { MiniMap } from '@antv/x6-plugin-minimap';
import { createShape, edgeTargetMarkerSelected } from './shape';
import NodeTask from '../designer/NodeTask';
import EdgeDirection from '../designer/EdgeDirection';
import NodeSubflow from '../designer/NodeSubflow';
import {
  sGetNewTaskID,
  sGetNewName,
  sGetNewDirectionID,
  sGetNewID,
  sGetTaskCurveWay,
  oCreateTask,
  aTaskCurvePoint,
  aGetCurvePoint
} from '../designer/utils';
import constant, {
  flowTaskRights,
  subflowDefTitleExpression,
  multiJobFlowTypeConfig,
  MinimapWidth,
  MinimapHeight
} from '../designer/constant';
import { generateId } from '@framework/vue/utils/util';
import { NumberExt } from '@antv/x6-common';
import { Rectangle } from '@antv/x6-geometry';
import { EventObject } from '@antv/x6-common/es/dom/event/object';

class FlowGraph {
  constructor({ id = 'graph-container', designer = undefined, vueInstance = undefined, themeClass = '', centerContent = false } = {}) {
    this.graph = undefined;
    this.container = null;
    this.designer = designer;
    this.vueInstance = vueInstance;
    this.selectedTool = '';
    this.selectedId = '';
    this.selectedLabelId = '';
    this.tasks = [];
    this.tasksData = [];
    this.subflows = [];
    this.subflowsData = [];
    this.directions = [];
    this.directionsData = [];
    this.previousCell = undefined;
    this.newTasks = []; // 新增环节
    this.newTasksUnEdited = []; // 未编辑过的新增环节
    this.newTasksEdited = []; // 编辑过的新增环节,点击打开配置认为编辑过，再次进入不能修改环节id
    this.newSubflows = [];
    this.newSubflowsUnEdited = [];
    this.newSubflowsEdited = [];
    this.dragInfo = { startX: 0, startY: 0, x: 0, y: 0, offsetX: 0, offsetY: 0 };
    this.currentInfo = this.createCurrentInfo();
    this.guideInfo = this.createGuideItem();
    this.swimlanePositions = []; // 泳道坐标
    this.addNodeEnum = {
      TASK: 'addNodeTask',
      SUBFLOW: 'addNodeSubFlow',
      BEGIN: 'addNodeBegin',
      END: 'addNodeEnd',
      CONDITION: 'addNodeCondition',
      LABEL: 'addNodeLabel',
      SWIMLANE: 'addNodeSwimlane',
      GUIDE: 'addNodeGuide',
      ROBOT: 'addNodeRobot',
      COLLAB: 'addNodeCollab'
    };
    this.moveTarget = undefined;
    this.cellsEmpty = true;
    this.mountedAmount = 0;
    this.renderAmount = 0;
    this.renderDone = false;
    this.centerContent = centerContent
    createShape(designer, themeClass, this);
    this.registerRouter();
    this.init(id);
  }
  init(id) {
    const that = this;
    const container = document.getElementById(id);
    this.container = container;
    const graph = new Graph({
      container,
      autoResize: true,
      grid: {
        type: 'mesh',
        visible: true,
        size: 28,
        args: {
          color: '#E6E6E6',
          thickness: 1
        }
      },
      background: {
        color: '#FFFFFF'
      },
      panning: {
        enabled: true,
        eventTypes: ['leftMouseDown']
      },
      mousewheel: {
        enabled: true,
        zoomAtMousePosition: true,
        modifiers: 'ctrl',
        minScale: 0.2,
        maxScale: 3
      },
      // 交互连线
      connecting: {
        router: 'manhattan',
        connector: {
          // 连接器将起点、路由返回的点、终点加工为 <path> 元素的 d 属性
          name: 'rounded',
          args: {
            radius: 8
          }
        },
        anchor: {
          name: 'center'
        },
        //connectionPoint: 'anchor',
        snap: {
          // 自动吸附
          radius: 20
        },
        allowBlank: false,
        allowLoop: false, // 边的起始点和终止点不能同一节点
        allowMulti: false, // 是否允许在相同的起始节点和终止之间创建多条边
        // 点击 magnet=true 的元素时
        validateMagnet(params) {
          console.log('validateMagnet', params);
          const { e } = params;
          const portPoint = this.pageToLocal(e.pageX, e.pageY);
          that.currentInfo.x = portPoint.x;
          that.currentInfo.y = portPoint.y;
          return true;
        },
        createEdge(params) {
          console.log('createEdge', params);
          const { sourceCell, sourceMagnet } = params;
          const sourceWay = sourceMagnet.getAttribute('port-group');
          this.startBatch('custom-add-edge');

          const currentEdge = that.currentInfo.edge;
          if (currentEdge) {
            that.removeUnselectCell(currentEdge);
          }

          that.currentInfo.sourceWay = sourceWay;
          const edge = that.createEdgeDirection({
            source: sourceCell,
            edgeWay: sourceWay
          });
          return edge;
        },
        // 在移动边的时候判断连接是否有效
        validateConnection(params) {
          console.log('validateConnection', params);
          const { edge, sourceCell, targetCell, sourceMagnet, targetMagnet } = params;

          if (!sourceCell) {
            return false;
          }
          if (targetCell) {
            that.showNodePorts(targetCell);
          } else {
            return false;
          }

          if (targetMagnet) {
            const targetWay = targetMagnet.getAttribute('port-group');
            that.currentInfo.targetWay = targetWay;
            const routerName = that.getSelectedRouterName();
            edge.setRouter(routerName, { way: `${that.currentInfo.sourceWay};${that.currentInfo.targetWay}` });
          }

          // 目标元素可以被链接
          return !!targetMagnet;
        },
        // 当停止拖动边的时候
        validateEdge(params) {
          console.log('validateEdge', params);
          // const { pass } = that.checkAddEdge({
          //   cell: params.edge,
          //   options: {}
          // })
          // return pass
          return true;
        }
      },
      // 交互限制
      interacting: {
        magnetConnectable(cellView) {
          // 先于 validateMagnet 执行
          // const type = that.selectedTool
          // if (type === 'CURVE' || type === 'BEELINE') {
          //   return true
          // } else {
          //   return false
          // }
          return true;
        },
        edgeLabelMovable(cellView) {
          const type = that.selectedTool;
          if (type === 'DEFAULT') {
            return true;
          } else {
            return false;
          }
        },
        nodeMovable(cellView) {
          return true;
        }
      }
      // // 移动范围
      // translating: {
      //   restrict(cellView) {
      //     return true
      //   }
      // }
    });
    graph
      .use(
        new Selection({
          rubberband: true, // 启用框选节点
          modifiers: 'shift',
          // showNodeSelectionBox: true,
          filter(cell) {
            if (cell.shape === 'EdgeDirectionDash') {
              return false;
            } else if (cell.shape === 'NodeGuide') {
              const currentEdge = that.currentInfo.edge;
              if (currentEdge) {
                this.removeUnselectCell(currentEdge);
              }
              return false;
            }
            return true;
          }
        })
      )
      .use(new Snapline())
      .use(new Keyboard())
      .use(new Clipboard())
      .use(new Export())
      .use(
        new History({
          enabled: true,
          beforeAddCommand(event, args) {
            // console.log('before', event, args)
            // 连接桩不加入历史记录
            if (args) {
              if (args.key === 'ports' || args.key === 'attrs' || args.key === 'target') {
                return false;
              } else if (args.cell && (args.cell.shape === 'NodeGuide' || args.cell.shape === 'EdgeDirectionDash')) {
                return false;
              } else if (args.options && args.options.temp) {
                return false;
              }
            }
            return true;
          }
        })
      );
    graph.defineMarker(edgeTargetMarkerSelected());
    window.__x6_instances__ = [];
    window.__x6_instances__.push(graph);
    this.graph = graph;
    this.initEvent();
  }
  initEvent() {
    const { graph } = this;
    graph.bindKey(['meta+c', 'ctrl+c'], () => {
      let cells = this.getSelectedCells();
      cells = cells.filter(cell => cell.shape !== 'EdgeDirection'); // 边不复制
      if (cells.length) {
        graph.copy(cells);
      }
      return false;
    });
    graph.bindKey(['meta+x', 'ctrl+x'], () => {
      const cells = this.getSelectedCells();
      if (cells.length) {
        graph.cut(cells);
      }
      return false;
    });
    graph.bindKey(['meta+v', 'ctrl+v'], () => {
      if (!graph.isClipboardEmpty()) {
        const cells = graph.paste({ offset: 32 });
        this.batchUpdateCellAndDataId(cells);
        graph.cleanSelection();
      }
      return false;
    });

    // undo redo
    graph.bindKey(['meta+z', 'ctrl+z'], () => {
      if (graph.canUndo()) {
        graph.undo();
      }
      return false;
    });
    graph.bindKey(['meta+shift+z', 'ctrl+shift+z'], () => {
      if (graph.canRedo()) {
        graph.redo();
      }
      return false;
    });

    graph.bindKey(['meta+a', 'ctrl+a'], () => {
      const nodes = this.getNodes();
      if (nodes) {
        graph.select(nodes);
      }
    });

    graph.bindKey(['delete'], () => {
      const cells = this.getSelectedCells();
      if (cells.length) {
        this.removeCells(cells);
      }
    });

    // zoom
    graph.bindKey(['ctrl+1', 'meta+1'], () => {
      const zoom = graph.zoom();
      if (zoom < 1.5) {
        graph.zoom(0.1);
      }
    });
    graph.bindKey(['ctrl+2', 'meta+2'], () => {
      const zoom = graph.zoom();
      if (zoom > 0.5) {
        graph.zoom(-0.1);
      }
    });

    // 控制连接桩显示/隐藏
    const showPorts = (view, show) => {
      const ports = view.container.querySelectorAll('.x6-port-body');
      for (let i = 0, len = ports.length; i < len; i += 1) {
        ports[i].style.visibility = show ? 'visible' : 'hidden';
      }
    };

    // 鼠标进入边时添加工具（用于修改边的起点或终点）
    // graph.on('edge:mouseenter', ({ cell }) => {
    //   cell.addTools([
    //     'source-arrowhead',
    //     {
    //       name: 'target-arrowhead',
    //       args: {
    //         attrs: {
    //           fill: 'red',
    //         },
    //       },
    //     },
    //   ])
    // })

    // graph.on('edge:mouseleave', ({ cell }) => {
    //   cell.removeTools()
    // })
    graph.on('resize', () => {
      const cells = this.getCells();
      if (cells && cells.length && this.centerContent) {
        graph.centerContent()
      }
    });
    graph.on('blank:click', ({ e, x, y }) => {
      const type = this.selectedTool;
      this.checkAddCellType({ x, y, type });
    });
    graph.on('blank:contextmenu', params => {
      this.removeUnselectCell();
      this.setSelectedTool();
    });
    graph.on('cell:click', params => {
      // console.log('cell:click', params)
      const { cell } = params;
    });
    graph.on('cell:mousemove', params => {
      // console.log('cell:mousemove', params)
      const { e, x, y, edge } = params;
      if (edge) {
        this.currentInfo.x = x;
        this.currentInfo.y = y;
      }
    });
    graph.on('cell:mouseup', params => {
      const { e, x, y, edge } = params;
      if (edge) {
        const nodes = graph.getNodesFromPoint(x, y);
        if (nodes.length) {
          const targetCell = nodes[0];
          const targetData = targetCell.data;
          if (
            targetData.id === constant.StartFlowId ||
            targetCell.shape === 'NodeLabel' ||
            targetCell.shape === 'NodeSwimlane' ||
            targetCell.shape === 'NodeGuide'
          ) {
            return;
          }
          const sourceCell = edge.getSourceCell();
          const sourceId = edge.getSourceCellId();
          if (!sourceCell && sourceId !== targetCell.id) {
            const pagePoint = graph.localToPage(x, y);
            const targetContainer = document.querySelector(`[data-cell-id='${targetCell.id}']`);
            const clientRect = targetContainer.getBoundingClientRect();

            const way = sGetTaskCurveWay(
              {
                w: clientRect.width,
                h: clientRect.height,
                x: clientRect.x,
                y: clientRect.y
              },
              pagePoint.x,
              pagePoint.y
            );
            this.currentInfo.targetWay = way;

            const targetPortDom = targetContainer.querySelector(`[port-group='${way}']`);
            const targetPortId = targetPortDom.getAttribute('port');
            // let targetPortPosition = targetCell.getPortsPosition(way)
            // targetPortPosition = targetPortPosition[targetPortId]['position']
            // graph.view.onMouseUp
            // graph.findViewByCell(sourceId).onMouseUp(e, targetPoint.x, targetPoint.y)

            if (!this.currentInfo.sourceWay) {
              this.currentInfo.sourceWay = this.getPortGroupById({
                view: graph,
                port: edge.source.port
              });
            }
            const edgeWay = `${this.currentInfo.sourceWay};${this.currentInfo.targetWay}`;
            const newEdge = this.createEdgeDirection({
              source: edge.source,
              target: targetCell,
              edgeWay
            });
            this.currentInfo.newEdgeId = newEdge.id;

            graph.addEdge(newEdge);
            this.setEdgeData(newEdge);
            graph.stopBatch('custom-add-edge');
          }
        } else {
          if (edge.shape === 'EdgeDirection') {
            graph.stopBatch('custom-add-edge');
          }
        }
      }
    });
    graph.on('cell:added', params => {
      console.log('cell:added', params);
      const { cell, options } = params;
      this.cellsEmpty = false;
      // const { pass } = this.checkAddEdge(params)
      // if (!pass) {
      //   graph.removeCells([cell.id, cell.target.cell])
      // }
    });
    graph.on('cell:removed', params => {
      console.log('cell:removed', params);
      this.resetGuideInfo();
      const { cell, options } = params;

      if (cell.shape !== 'EdgeDirectionDash' && cell.shape !== 'NodeGuide') {
        this.resetCurrentInfo();
      }

      if (cell.shape === 'NodeTask') {
        this.removeTask(cell);
      } else if (cell.shape === 'NodeSubflow') {
        this.removeSubflow(cell);
      } else if (cell.shape === 'EdgeDirection') {
        this.updateEdgesCurve(cell);
        if (!options.temp) {
          this.removeDirection(cell);
        }
      } else if (cell.shape === 'NodeSwimlane') {
        this.removeSwimlanePosition(cell.position());
      }
      const cells = this.getCells();
      if (!cells.length) {
        this.cellsEmpty = true;
      }
    });
    graph.on('cell:selected', params => {
      console.log('cell:selected', params);
      const { cell, options } = params;
      this.selectedCell(cell, options);
    });
    graph.on('node:mouseenter', args => {
      // console.log('node:mouseenter', args)
      const { cell, node, view, e } = args;
      this.currentInfo.enterNode = cell;
      // getDir(e, e.target)

      // const rect = cell.getBBox()
      // rect.x = rect.x + this.graph.options.x + 224; // 224左侧选择面板
      // rect.y = rect.y + this.graph.options.y + 108
      const clientRect = e.target.getBoundingClientRect();
      /*
       rect.x + this.graph.options.x + 224 === clientRect.x
       rect.y + this.graph.options.y + 108 === clientRect.y
      */
      const way = sGetTaskCurveWay(
        {
          w: clientRect.width, // e.target.offsetWidth,
          h: clientRect.height, // e.target.offsetHeight,
          x: clientRect.x,
          y: clientRect.y
        },
        e.pageX,
        e.pageY
      );

      this.showNodePorts(cell);
    });
    graph.on('node:mouseleave', params => {
      // console.log('node:mouseleave', params)
      const { cell, node, view, e } = params;
      this.currentInfo.leaveNode = cell;

      const type = this.selectedTool;
      if (type === 'CURVE' || type === 'BEELINE') {
        const sourceNode = this.currentInfo.sourceNode;
        const currentEdge = this.currentInfo.edge;
        if (sourceNode && !currentEdge && cell.id === sourceNode.id) {
          const clientRect = e.target.getBoundingClientRect();
          const sourceWay = sGetTaskCurveWay(
            {
              w: clientRect.width,
              h: clientRect.height,
              x: clientRect.x,
              y: clientRect.y
            },
            e.pageX,
            e.pageY
          );
          this.currentInfo.sourceWay = sourceWay;
          const edge = this.createEdgeDirection({
            source: sourceNode,
            edgeWay: sourceWay,
            zIndex: 0
          });
          this.currentInfo.edge = edge;
          graph.addEdge(edge, { temp: true });
        }
      }

      this.hideNodePorts(cell);
    });
    graph.on('node:port:mouseenter', params => {
      const { node, view, port } = params;
      if (node.data.id === constant.EndFlowId) {
        return;
      }
      if (this.guideInfo.port === port) {
        // 连接桩有引导节点的不允许再新增
        return;
      }
      if (node.data.id === constant.StartFlowId) {
        // 开始节点已经流向的不允许有引导节点
        let outgoings = this.getOutgoingEdges(node.id);
        if (outgoings) {
          outgoings = outgoings.filter(item => item.shape !== 'EdgeDirectionDash');
          if (outgoings.length) {
            return;
          }
        }
      }
      const { hasNodes } = this.hasNodesInArea({ node, view, port });
      if (hasNodes) {
        // 拟引导区域有节点的不允许有引导节点
        return;
      }
      const outEdges = this.getOutEdges({ node, view, port });
      const inEdges = this.getInEdges({ node, view, port });
      if (outEdges.length || inEdges.length) {
        // 连接桩已经连接节点 || 连接桩被节点连接  不允许有引导节点
        let edges = outEdges.concat(inEdges);
        edges = edges.filter(item => item.shape !== 'EdgeDirectionDash');
        if (edges.length) {
          return;
        }
      }
      if (this.guideInfo.port !== port) {
        // 移入了新的连接桩，删除上一个
        graph.removeCell(this.guideInfo.id);
      }

      const timer = setTimeout(() => {
        clearTimeout(timer);
        if (this.moveTarget.classList.contains('x6-port-body')) {
          const portId = this.moveTarget.getAttribute('port');
          if (portId === port) {
            this.addGuideNodeAndEdge({ node, view, port });
          }
        }
      }, 500);
    });
    graph.on('edge:contextmenu', params => {
      this.removeUnselectCell();
      this.setSelectedTool();
    });
    graph.on('edge:connected', params => {
      console.log('edge:connected', params);
      const { isNew, edge, currentCell } = params;
      if (isNew) {
        this.setEdgeData(edge);
        graph.stopBatch('custom-add-edge');
      }
    });
    graph.on('view:mounted', args => {
      if (!this.renderDone) {
        this.mountedAmount++;
      }
      if (this.renderAmount && this.mountedAmount >= this.renderAmount) {
        this.renderDone = true;
      }
      const { view } = args;
      const cell = view.cell;
      if (cell.isEdge() && cell.id === this.currentInfo.newEdgeId) {
        console.log('view:mounted', view);
        this.updateEdgesCurve(cell);
      }
    });
  }
  get graphOptions() {
    return this.graph.options;
  }
  useMiniMap({ id = 'minimap', width = MinimapWidth, height = MinimapHeight } = {}) {
    this.graph.use(
      new MiniMap({
        container: document.getElementById(id),
        width,
        height
      })
    );
  }
  createGuideItem() {
    return {
      id: '',
      port: '',
      sourceWay: '',
      sourceNode: undefined,
      edgeDash: undefined,
      targetWay: ''
    };
  }
  resetGuideInfo() {
    this.guideInfo = this.createGuideItem();
  }
  createCurrentInfo() {
    return {
      x: 0,
      y: 0,
      sourceWay: '',
      targetWay: '',
      edge: undefined,
      enterNode: undefined,
      leaveNode: undefined,
      targetNode: undefined,
      sourceNode: undefined,
      isNewEdge: true,
      newEdgeId: '' // 和edge.id不同
    };
  }
  resetCurrentInfo() {
    const newEdgeId = this.currentInfo.newEdgeId;
    this.currentInfo = this.createCurrentInfo();
    this.currentInfo.newEdgeId = newEdgeId;
  }
  checkAddEdge({ cell, options }) {
    let pass = true,
      edgeTaskToCondition;
    if (cell.shape === 'EdgeDirection' && !options.temp) {
      const sourceId = cell.getSourceCellId();
      let outgoings = this.getOutgoingEdges(sourceId);
      if (outgoings) {
        for (let index = 0; index < outgoings.length; index++) {
          const item = outgoings[index];
          if (item.id === cell.id || item.shape === 'EdgeDirectionDash') {
            continue;
          }

          const sourceCell = item.getSourceCell();
          const targetCell = item.getTargetCell();
          if (sourceCell.shape === 'NodeTask' && targetCell && targetCell.shape === 'NodeCondition') {
            pass = false;
            edgeTaskToCondition = item;
            break;
          }
        }
      }
    }
    return { pass, edgeTaskToCondition };
  }
  updateEdgesCurve(cell) {
    let sourceWay, targetWay, routerName;
    if (cell.router) {
      routerName = cell.router.name;
    }

    if (routerName === 'curve') {
      let argsWay = cell.router.args.way;
      argsWay = argsWay.split(';');
      sourceWay = argsWay[0];
      targetWay = argsWay[1];

      const sourceId = cell.getSourceCellId();
      const targetId = cell.getTargetCellId();

      const sourceEdgesWay = this.getEdgesCurveByWay(sourceId, sourceWay);
      sourceEdgesWay.forEach(item => {
        const cellView = this.graph.findViewByCell(item.id);
        cellView.update();
      });
      if (targetId) {
        const targetEdgesWay = this.getEdgesCurveByWay(targetId, targetWay);
        targetEdgesWay.forEach(item => {
          const cellView = this.graph.findViewByCell(item.id);
          cellView.update();
        });
        this.currentInfo.newEdgeId = '';
      }
    }
  }
  hasNodesInArea({ node, view, port }) {
    let hasNodes = false;
    const portWay = this.getPortGroupById({ view, port });
    const { point } = this.getOffsetPointByWay({
      sourceBBox: node.getBBox(),
      sourceWay: portWay,
      targetSize: {
        width: constant.NodeDefaultWidth,
        height: constant.NodeDefaultHeight
      },
      isStartPoint: true
    });
    // 默认获取与区域相交的节点
    let nodes = this.graph.getNodesInArea(point.x, point.y, constant.NodeDefaultWidth, constant.NodeDefaultHeight);
    nodes = nodes.filter(item => item.shape !== 'NodeGuide');
    if (nodes.length) {
      hasNodes = true;
    }
    return { hasNodes, nodes };
  }
  getOutEdges({ node, view, port }) {
    let outEdges = [];
    let outgoings = this.getOutgoingEdges(node.id);
    if (outgoings) {
      for (let index = 0; index < outgoings.length; index++) {
        const edge = outgoings[index];

        let sourceWay, targetWay, routerName;
        if (edge.router) {
          routerName = edge.router.name;
          let argsWay = edge.router.args.way;
          argsWay = argsWay.split(';');
          sourceWay = argsWay[0];
          targetWay = argsWay[1];
        }

        if (edge.source.port === port) {
          outEdges.push(edge);
        } else if (routerName === 'normal') {
          const sourcePoint = this.graph.findViewByCell(edge.id).sourcePoint;
          const portPoint = this.getPortPointByGroup({ node, view, port });
          if (sourcePoint.x === portPoint.x && sourcePoint.y === portPoint.y) {
            outEdges.push(edge);
          }
        } else {
          const portWay = this.getPortGroupById({ view, port });
          if (portWay === sourceWay) {
            outEdges.push(edge);
          }
        }
      }
    }
    return outEdges;
  }
  getInEdges({ node, view, port }) {
    let inEdges = [];
    let incomings = this.getIncomingEdges(node.id);
    if (incomings) {
      for (let index = 0; index < incomings.length; index++) {
        const edge = incomings[index];

        let sourceWay, targetWay, routerName;
        if (edge.router) {
          routerName = edge.router.name;
          let argsWay = edge.router.args.way;
          argsWay = argsWay.split(';');
          sourceWay = argsWay[0];
          targetWay = argsWay[1];
        }

        if (edge.target.port === port) {
          inEdges.push(edge);
        } else if (routerName === 'normal') {
          const targetPoint = this.graph.findViewByCell(edge.id).targetPoint;
          const portPoint = this.getPortPointByGroup({ node, view, port });
          if (targetPoint.x === portPoint.x && targetPoint.y === portPoint.y) {
            inEdges.push(edge);
          }
        } else {
          const portWay = this.getPortGroupById({ view, port });
          if (portWay === targetWay) {
            inEdges.push(edge);
          }
        }
      }
    }
    return inEdges;
  }
  getPortPointByGroup({ node, view, port }) {
    let point;
    const portWay = this.getPortGroupById({ view, port });
    const nodeBBox = node.getBBox();

    switch (portWay) {
      case 'up':
        point = nodeBBox['topCenter'];
        break;
      case 'left':
        point = nodeBBox['leftMiddle'];
        break;
      case 'right':
        point = nodeBBox['rightMiddle'];
        break;
      case 'down':
      default:
        point = nodeBBox['bottomCenter'];
        break;
    }
    return point;
  }
  getPortGroupById({ view, port }) {
    let portGroup = '';
    const portDom = view.container.querySelector(`[port='${port}']`);
    if (portDom) {
      portGroup = portDom.getAttribute('port-group');
    }
    return portGroup;
  }
  addGuideNodeAndEdge({ node, view, port }) {
    const portWay = this.getPortGroupById({ view, port });
    const nodeBBox = node.getBBox();

    // const clientRect = portDom.getBoundingClientRect()
    // let portPoint = this.graph.pageToLocal(clientRect.x, clientRect.y)

    const { point, targetWay } = this.getOffsetPointByWay({
      sourceBBox: nodeBBox,
      sourceWay: portWay,
      targetSize: {
        width: constant.NodeGuideWidth,
        height: constant.NodeGuideHeight
      },
      offsetCount: 2
    });
    this.checkAddCellType({
      x: point.x,
      y: point.y,
      type: 'GUIDE'
    });

    const edgeWay = `${portWay};${targetWay}`;
    const newEdge = this.createEdgeDirection({
      source: node,
      target: { id: this.guideInfo.id },
      shape: 'EdgeDirectionDash',
      edgeWay
    });
    this.graph.addEdge(newEdge);

    this.guideInfo.port = port;
    this.guideInfo.sourceWay = portWay;
    this.guideInfo.sourceNode = node;
    this.guideInfo.edgeDash = newEdge;
    this.guideInfo.targetWay = targetWay;

    const nodeGuide = this.getCellById(this.guideInfo.id);
    nodeGuide.setData(this.guideInfo);
    // node.addChild(nodeGuide)
  }
  getEdgesCurveByWay(cellId, way) {
    const incomings = this.getIncomingEdges(cellId);
    const outgoings = this.getOutgoingEdges(cellId);

    let edgesWay = [];
    if (incomings) {
      incomings.map(item => {
        if (item.router) {
          let routerName = item.router.name;
          if (routerName === 'curve') {
            let argsWay = item.router.args.way;
            argsWay = argsWay.split(';');
            if (argsWay[1] && argsWay[1] === way) {
              edgesWay.push(item);
            }
          }
        }
      });
    }
    if (outgoings) {
      outgoings.map(item => {
        if (item.router) {
          let routerName = item.router.name;
          if (routerName === 'curve') {
            let argsWay = item.router.args.way;
            argsWay = argsWay.split(';');
            if (argsWay[0] && argsWay[0] === way) {
              edgesWay.push(item);
            }
          }
        }
      });
    }

    edgesWay.sort((a, b) => {
      return a.zIndex - b.zIndex;
    });
    return edgesWay;
  }
  createEdgeDirection({ source, edgeWay, zIndex, target, shape = 'EdgeDirection' }) {
    const routerName = this.getSelectedRouterName();
    const edgeId = sGetNewDirectionID({
      edges: this.getEdges()
    });
    let options = {
      id: edgeId,
      shape,
      source: {
        cell: source.id || source.cell
      },
      router: {
        name: routerName,
        args: {
          way: edgeWay
        }
      },
      data: {}
    };
    if (zIndex != undefined) {
      options.zIndex = zIndex;
    }
    if (target) {
      options.target = {
        cell: target.id || target.cell
      };
    }
    if (routerName === 'normal') {
      options.source.anchor = 'midSide';
      if (options.target) {
        options.target.anchor = 'midSide';
      }
    }
    const edge = this.graph.createEdge(options);
    return edge;
  }
  // 删除旧边并新增边
  removeOldNewEdge(sourceNode, targetNode, removeCell, unselectCell) {
    if (sourceNode && targetNode && sourceNode.id !== targetNode.id) {
      const edgeWay = `${this.currentInfo.sourceWay};${this.currentInfo.targetWay}`;
      const newEdge = this.createEdgeDirection({
        source: sourceNode,
        target: targetNode,
        edgeWay
      });
      this.currentInfo.newEdgeId = newEdge.id;
      this.graph.addEdge(newEdge);
      this.setEdgeData(newEdge);

      this.removeUnselectCell(removeCell, unselectCell);
      this.graph.stopBatch('custom-add-edge');
    } else {
      this.removeUnselectCell(removeCell, unselectCell);
    }
  }
  selectedCell(cell, options) {
    const type = this.selectedTool;

    const sourceNode = this.currentInfo.sourceNode;
    const currentEdge = this.currentInfo.edge;
    if (options.save) {
      this.setSelectedEdge(cell);
    } else if (cell.isNode()) {
      if (type === 'CURVE' || type === 'BEELINE') {
        if (currentEdge) {
          this.removeOldNewEdge(sourceNode, cell, currentEdge, cell);
        } else {
          this.graph.startBatch('custom-add-edge');
          this.currentInfo.sourceNode = cell;
        }
      } else {
        this.setSelectedNode(cell);
      }
    } else if (cell.isEdge()) {
      if (type === 'CURVE' || type === 'BEELINE' || currentEdge) {
        if (currentEdge) {
          let targetNode;
          const targetPoint = currentEdge.getTargetPoint();
          const targetPointNode = this.graph.getNodesFromPoint(targetPoint)[0];
          if (targetPointNode && sourceNode && targetPointNode.id !== sourceNode.id) {
            targetNode = targetPointNode;
          }

          this.removeOldNewEdge(sourceNode, targetNode, currentEdge, targetNode);
        } else {
          const cells = this.getSelectedCells();
          if (cells.length) {
            this.graph.unselect(cells);
          }
        }
      } else {
        this.setSelectedEdge(cell);
      }
    }
  }
  removeUnselectCell(removeCell, unselectCell) {
    const currentEdge = this.currentInfo.edge;
    if (!removeCell && currentEdge) {
      removeCell = currentEdge;
    }
    if (removeCell) {
      this.graph.removeCell(removeCell, { temp: true }); // this.graph.removeEdge
    }
    if (!unselectCell) {
      const cells = this.getSelectedCells();
      if (cells.length) {
        unselectCell = cells;
      }
    }
    if (unselectCell) {
      this.graph.unselect(unselectCell);
    }
  }
  setEdgeData(edge) {
    let name = '';
    let type = '1';
    let terminalType = '';
    let terminalName = '';
    const sourceCell = edge.getSourceCell();
    const targetCell = edge.getTargetCell();
    if (!sourceCell || !targetCell) {
      return;
    }
    const sourceData = sourceCell.getData();
    const targetData = targetCell.getData();
    if (
      targetData.id === constant.StartFlowId ||
      sourceCell.shape === 'NodeLabel' ||
      targetCell.shape === 'NodeLabel' ||
      sourceCell.shape === 'NodeSwimlane' ||
      targetCell.shape === 'NodeSwimlane' ||
      targetCell.shape === 'NodeGuide'
    ) {
      this.graph.removeCell(edge.id);
      return;
    }

    let fromID = sourceData.id;
    if (sourceCell.shape === 'NodeCondition') {
      type = '2';
    }
    if (sourceData.id === constant.StartFlowId) {
      terminalType = constant.BEGIN;
      terminalName = sourceData.name;
      targetData.canEditForm = '1';
      // targetCell.setData({ canEditForm: '1' })
      targetData.enabledJobFlowType = true;
      targetData.multiJobFlowType = multiJobFlowTypeConfig[2]['id'];
    }

    if (targetData.canEditForm !== '1' && targetData.startRights) {
      targetData.startRights = [];
    }

    const toID = targetData.id;
    if (targetCell.shape === 'NodeCondition') {
      type = '3';
      // sourceCell.setData(targetData)
    }
    if (sourceCell.shape !== 'NodeTask' || targetCell.shape !== 'NodeCondition') {
      name = `送${targetData.name || targetData.conditionName}`;
      edge.setLabels([name]);
    }

    if (targetData.id === constant.EndFlowId) {
      terminalType = constant.END;
      terminalName = targetData.name;
    }

    const edgeRouter = edge.getRouter();
    let line = edgeRouter.name;
    if (line === 'curve') {
      line = `${line};${edgeRouter.args.way}`;
    }
    // if(edge.data.name && edge.data.toID) {增加到directions}
    // toID = '' 目标节点是判断点
    const edgeData = new EdgeDirection({
      id: edge.id,
      name,
      type,
      fromID,
      toID,
      terminalType,
      terminalName,
      line
    });
    edge.setData(edgeData);
    this.addDirection(edge);
  }
  registerRouter() {
    const that = this;
    Graph.registerRouter('curve', (vertices, options, edgeView) => that.curveRouter(vertices, options, edgeView), true);
    Graph.registerRouter('beeline', (vertices, options, edgeView) => that.beelineRouter(vertices, options, edgeView), true);
    Graph.registerRouter('oneSideNew', (vertices, options, edgeView) => that.oneSideNew(vertices, options, edgeView), true);
  }
  beelineRouter(vertices, options, edgeView) {
    const points = this.routerCommon(vertices, options, edgeView);

    edgeView.sourceAnchor.x = points[0].x;
    edgeView.sourceAnchor.y = points[0].y;
    edgeView.targetAnchor.x = points[points.length - 1].x;
    edgeView.targetAnchor.y = points[points.length - 1].y;
    return [points[0]];
  }
  curveRouter(vertices, options, edgeView) {
    const points = this.routerCommon(vertices, options, edgeView);

    const routePoints = points.slice(1, points.length - 1);
    edgeView.sourceAnchor.x = points[0].x;
    edgeView.sourceAnchor.y = points[0].y;
    edgeView.targetAnchor.x = points[points.length - 1].x;
    edgeView.targetAnchor.y = points[points.length - 1].y;

    return [...routePoints];
  }
  routerCommon(vertices, options, edgeView) {
    let sourceCell,
      targetCell,
      { cell, sourceView, targetView, sourceBBox, targetBBox } = edgeView;

    if (sourceView) {
      sourceCell = sourceView.cell;
    } else {
      console.log('=========>', cell.id);
      sourceCell = this.getCellById(cell.source.cell);
    }
    if (!sourceBBox) {
      sourceBBox = sourceCell.getBBox();
    }

    let sourceImgObject = {};
    sourceImgObject.x = sourceBBox.x;
    sourceImgObject.y = sourceBBox.y;
    sourceImgObject.w = sourceBBox.width;
    sourceImgObject.h = sourceBBox.height;

    let fromWay,
      toWay = null,
      { way } = options;
    way = way.split(';');
    fromWay = way[0];
    if (way[1]) {
      toWay = way[1];
    }

    const getWayCurves = cellId => {
      let wayCurves = {};
      const ways = ['up', 'down', 'left', 'right'];

      for (let index = 0; index < ways.length; index++) {
        const way = ways[index];
        const edgesWay = this.getEdgesCurveByWay(cellId, way);
        const formatEdgesWay = edgesWay.map(item => {
          return {
            name: item.id,
            fromTask: {
              name: item.source.cell
            }
          };
        });

        wayCurves[`${way}Curves`] = formatEdgesWay;
      }
      return wayCurves;
    };

    const sourceCurves = getWayCurves(sourceCell.id);
    const sourceTask = oCreateTask({ name: sourceCell.id, imgObject: sourceImgObject, ...sourceCurves });

    // 从右到左  开始指向结束
    let sourceXY, targetXY, piMaxIndex, fromIndex, toIndex, laX_Y;
    if (targetView) {
      targetCell = targetView.cell;

      let targetImgObject = {};
      targetImgObject.x = targetBBox.x;
      targetImgObject.y = targetBBox.y;
      targetImgObject.w = targetBBox.width;
      targetImgObject.h = targetBBox.height;

      const isNewEdge = this.currentInfo.isNewEdge;
      sourceXY = aTaskCurvePoint(sourceTask, fromWay, { name: cell.id }, isNewEdge);

      const targetCurves = getWayCurves(targetCell.id);
      const targetTask = oCreateTask({ name: targetCell.id, imgObject: targetImgObject, ...targetCurves });
      targetXY = aTaskCurvePoint(targetTask, toWay, { name: cell.id }, isNewEdge);
      fromIndex = sourceXY[2];
      toIndex = targetXY[2];
      if (isNewEdge) {
        piMaxIndex = fromIndex > toIndex ? fromIndex : toIndex;
      }

      // var laX_Y = aGetCurvePoint(loObject.x1, loObject.y1, loObject.x2, loObject.y2, loObject.fromWay, loObject.toWay, loObject.maxIndex);
      laX_Y = aGetCurvePoint(sourceXY[0], sourceXY[1], targetXY[0], targetXY[1], fromWay, toWay, piMaxIndex);
    } else {
      // var laXY = aTaskCurvePoint(goWorkFlow.fromObject, lsFromWay, null);
      // var laX_Y = aGetCurvePoint(laXY[0], laXY[1], goWorkFlow.cursorX, goWorkFlow.cursorY, lsFromWay, lsToWay);
      sourceXY = aTaskCurvePoint(sourceTask, fromWay, null);
      laX_Y = aGetCurvePoint(sourceXY[0], sourceXY[1], this.currentInfo.x, this.currentInfo.y, fromWay, toWay);
    }

    const points = [];
    laX_Y[0].forEach((x, index) => {
      points.push({
        x,
        y: laX_Y[1][index]
      });
    });

    return points;
  }
  oneSideNew(vertices, options, edgeView) {
    console.log(options);
    const way = options.way || 'bottom';
    const padding = NumberExt.normalizeSides(options.padding || 20);
    const sourceBBox = edgeView.sourceBBox;
    const targetBBox = edgeView.targetBBox;

    // const sourcePoint = sourceBBox.getCenter();
    // const targetPoint = targetBBox.getCenter();

    const sourceAnchor = edgeView.sourceAnchor;
    const sourcePoint = sourceAnchor.clone();
    const targetAnchor = edgeView.targetAnchor;
    const targetPoint = targetAnchor.clone();

    if (options.dy > 0) {
      // sourcePoint.y = sourceAnchor.y
      sourcePoint.x = sourcePoint.x + options.dy;
    }

    let coord;
    let dim;
    let factor;
    switch (way) {
      case 'top':
        factor = -1;
        coord = 'y';
        dim = 'height';
        break;
      case 'left':
        factor = -1;
        coord = 'x';
        dim = 'width';
        break;
      case 'right':
        factor = 1;
        coord = 'x';
        dim = 'width';
        break;
      case 'bottom':
      default:
        factor = 1;
        coord = 'y';
        dim = 'height';
        break;
    }

    sourcePoint[coord] += factor * (sourceBBox[dim] / 2 + padding[way]);
    targetPoint[coord] += factor * (targetBBox[dim] / 2 + padding[way]);

    if (factor * (sourcePoint[coord] - targetPoint[coord]) > 0) {
      targetPoint[coord] = sourcePoint[coord];
    } else {
      sourcePoint[coord] = targetPoint[coord];
    }
    console.log(sourcePoint);
    return [sourcePoint.toJSON(), ...vertices, targetPoint.toJSON()];
  }
  setContainerMoveEvent(event) {
    const { target } = event;
    this.moveTarget = target;
    const guideId = this.guideInfo.id;
    if (guideId) {
      const guideEl = target.closest(`[data-cell-id='${guideId}']`);
      const edgeDashEl = target.closest(`[data-cell-id='${this.guideInfo.edgeDash.id}']`);
      const edgeDashSourceEl = target.closest(`[data-cell-id='${this.guideInfo.sourceNode.id}']`);
      if (!guideEl && !edgeDashEl && !edgeDashSourceEl) {
        this.graph.removeCell(guideId);
      }
    } else {
      // 防止鼠标移开了还有引导节点
      const guideEl = this.graph.container.querySelector("[data-shape='NodeGuide']");
      if (guideEl) {
        this.graph.removeCell(guideEl.getAttribute('data-cell-id'));
      }
    }
    if (this.currentInfo.edge) {
      const movePoint = this.graph.pageToLocal(event.pageX, event.pageY);
      this.currentInfo.x = movePoint.x;
      this.currentInfo.y = movePoint.y;
      // const className = target.getAttribute('class')
      // if (className.indexOf('node-') === 0) {
      const cellDom = target.closest('g.x6-cell.x6-node');
      if (cellDom) {
        const clientRect = cellDom.getBoundingClientRect();
        const way = sGetTaskCurveWay(
          {
            w: clientRect.width,
            h: clientRect.height,
            x: clientRect.x,
            y: clientRect.y
          },
          event.pageX,
          event.pageY
        );

        this.currentInfo.targetWay = way;
        const routerName = this.getSelectedRouterName();
        this.currentInfo.edge.setRouter(
          routerName,
          {
            way: `${this.currentInfo.sourceWay};${this.currentInfo.targetWay}`
          },
          { temp: true }
        );
      }
      // event = EventObject.create(event)
      // this.graph.view.onMouseMove(event)
      this.currentInfo.edge.setTarget(movePoint);
    }
  }
  getSelectedRouterName() {
    const type = this.selectedTool;
    const routerName = type === 'CURVE' ? 'curve' : 'normal';
    return routerName;
  }
  setSelectedTool(type = '') {
    const selectedCell = this.getSelectedCell();
    if ((type === 'CURVE' || type === 'BEELINE') && selectedCell) {
      this.currentInfo.sourceNode = selectedCell;
    }
    this.selectedTool = type;
  }
  getSelectedTool() {
    return this.selectedTool;
  }
  // 默认偏移3个网格
  getOffsetPointByWay({ sourceBBox, sourceWay, targetSize, isStartPoint, offsetCount = 3 }) {
    const gridSize = this.graph.getGridSize();
    let coord, dim, factor, point, targetWay;
    switch (sourceWay) {
      case 'up':
        factor = -1;
        coord = 'y';
        dim = 'height';
        point = sourceBBox['topCenter'];
        targetWay = 'down';
        break;
      case 'left':
        factor = -1;
        coord = 'x';
        dim = 'width';
        point = sourceBBox['leftMiddle'];
        targetWay = 'right';
        break;
      case 'right':
        factor = 1;
        coord = 'x';
        dim = 'width';
        point = sourceBBox['rightMiddle'];
        targetWay = 'left';
        break;
      case 'down':
      default:
        factor = 1;
        coord = 'y';
        dim = 'height';
        point = sourceBBox['bottomCenter'];
        targetWay = 'up';
        break;
    }
    if (isStartPoint) {
      point = sourceBBox.clone();
      point[coord] += factor * (targetSize[dim] + gridSize * offsetCount);
    } else {
      point[coord] += factor * (targetSize[dim] / 2 + gridSize * offsetCount);
    }

    return {
      point,
      targetWay
    };
  }
  updateDragInfo({
    startX = this.dragInfo.startX,
    startY = this.dragInfo.startY,
    x = this.dragInfo.x,
    y = this.dragInfo.y,
    offsetX = this.dragInfo.offsetX,
    offsetY = this.dragInfo.offsetY
  }) {
    this.dragInfo.startX = startX;
    this.dragInfo.startY = startY;
    this.dragInfo.x = x;
    this.dragInfo.y = y;
    this.dragInfo.offsetX = offsetX;
    this.dragInfo.offsetY = offsetY;
  }
  addDragNode({ type }) {
    this.dragInfo.offsetX = this.dragInfo.offsetX - this.graph.options.x;
    this.dragInfo.offsetY = this.dragInfo.offsetY - this.graph.options.y;

    const point = this.graph.snapToGrid(this.dragInfo.x, this.dragInfo.y);
    this.dragInfo.x = point.x;
    this.dragInfo.y = point.y;

    this.checkAddCellType({ x: point.x, y: point.y, type });
  }
  checkAddCellType({ x, y, type, id }) {
    if (type === 'GUIDE') {
      x = x - constant.NodeGuideWidth / 2;
      y = y - constant.NodeGuideHeight / 2;
    } else if (type === 'SWIMLANE') {
      x = x - constant.NodeSwimlaneWidth / 2;
      y = y - constant.NodeSwimlaneHeight / 2;
      const curIndex = this.getSwimlanePositionIndex({ x, y });
      if (curIndex > -1) {
        return;
      }
    } else if (type === 'BEGIN' || type === 'END') {
      x = x - constant.NodeCircleWidth / 2;
      y = y - constant.NodeCircleHeight / 2;
    } else {
      x = x - constant.NodeDefaultWidth / 2;
      y = y - constant.NodeDefaultHeight / 2;
    }
    if (this.addNodeEnum[type] && this[this.addNodeEnum[type]]) {
      this.graph.startBatch('custom-add-node');
      this[this.addNodeEnum[type]]({ x, y, type, id });
      this.graph.stopBatch('custom-add-node');
    }
  }
  addNodeGuide({ x, y }) {
    const nodeId = sGetNewID({
      prefix: 'Guide',
      cells: this.getCells()
    });

    this.guideInfo.id = nodeId;
    this.graph.addNode({
      id: nodeId,
      shape: 'NodeGuide',
      x,
      y,
      data: {
        id: nodeId
      }
    });
  }
  addNodeBegin({ x, y }) {
    const nodeId = sGetNewID({
      prefix: 'Start',
      cells: this.getCells()
    });
    const dataId = constant.StartFlowId;
    this.graph.addNode({
      id: nodeId,
      shape: 'NodeCircle',
      x,
      y,
      data: {
        id: dataId,
        name: constant.BEGINNAME
      }
    });
  }
  addNodeEnd({ x, y }) {
    const nodeId = sGetNewID({
      prefix: 'End',
      cells: this.getCells()
    });
    const dataId = constant.EndFlowId;
    this.graph.addNode({
      id: nodeId,
      shape: 'NodeCircle',
      x,
      y,
      data: {
        id: dataId,
        name: constant.ENDNAME
      }
    });
  }
  addNodeCondition({ x, y, type, id }) {
    const conditionName = sGetNewName({
      type,
      cells: this.getCells()
    });
    if (!id) {
      id = sGetNewID({
        prefix: 'Condition',
        cells: this.getCells()
      });
    }
    this.graph.addNode({
      id,
      shape: 'NodeCondition',
      x,
      y,
      data: {
        id,
        name: conditionName,
        remark: '',
        conditionName,
        conditionBody: ''
      },
      label: conditionName
    });
  }
  addNodeSubFlow({ x, y, type, id }) {
    if (!id) {
      id = sGetNewTaskID({
        type,
        nodes: this.getNodes()
      });
    }
    const name = sGetNewName({
      type,
      cells: this.getCells()
    });
    const nodeSubflowData = new NodeSubflow({
      id,
      name
    });
    nodeSubflowData.titleExpression = subflowDefTitleExpression;
    const nodeSubflow = this.graph.addNode({
      id,
      shape: 'NodeSubflow',
      x,
      y,
      label: name,
      data: {}
    });
    nodeSubflow.setData(nodeSubflowData);
    this.addSubflow(nodeSubflow);
  }
  initTaskData(id, name, type) {
    let defRights = {};
    let defRightConfigs = {};
    for (const key in flowTaskRights) {
      const settingKey = flowTaskRights[key]['settingKey'];
      defRights[key] = this.designer.getRightOptions(settingKey);
      const configKey = flowTaskRights[key]['configKey'];
      defRightConfigs[configKey] = this.designer.initRightConfig(defRights[key]);
    }
    let args = {
      id,
      name,
      ...defRights,
      ...defRightConfigs
    };
    if (type) {
      args.type = type;
    }
    const nodeTaskData = new NodeTask(args);
    return nodeTaskData;
  }
  addNodeTask({ x, y, type, id }) {
    if (!id) {
      id = sGetNewTaskID({
        type,
        nodes: this.getNodes()
      });
    }
    const name = sGetNewName({
      type,
      cells: this.getCells()
    });

    const nodeTaskData = this.initTaskData(id, name);
    const nodeTask = this.graph.addNode({
      id,
      shape: 'NodeTask',
      x,
      y,
      label: name,
      data: {}
    });
    nodeTask.setData(nodeTaskData);
    this.addTask(nodeTask);
  }
  addNodeLabel({ x, y }) {
    const nodeId = sGetNewID({
      prefix: 'label',
      cells: this.getCells()
    });
    this.graph.addNode({
      id: nodeId,
      shape: 'NodeLabel',
      x,
      y,
      data: {
        id: nodeId,
        label: ''
      }
    });
  }
  addNodeSwimlane({ x, y }) {
    const nodeId = sGetNewID({
      prefix: 'swimlane',
      cells: this.getCells()
    });
    this.graph.addNode({
      id: nodeId,
      shape: 'NodeSwimlane',
      x,
      y,
      data: {
        id: nodeId,
        name: '',
        layout: ['column'],
        columnHeight: constant.SwimlaneRowHeight,
        columns: [
          { title: '', dataIndex: 'selection', width: constant.SwimlaneSelectionWidth },
          { title: '泳道1', dataIndex: 'title', width: constant.SwimlaneColumnWidth }
        ],
        rowWidth: constant.SwimlaneColumnWidth,
        rows: [{ id: Date.now(), selection: '泳道1', title: '', height: constant.SwimlaneRowHeight }]
      },
      zIndex: 0
    });
    this.addSwimlanePosition({ x, y });
  }
  addNodeRobot({ x, y, type, id }) {
    if (!id) {
      id = sGetNewID({
        prefix: 'Robot',
        cells: this.getCells()
      });
    }
    const name = sGetNewName({
      type,
      cells: this.getCells()
    });
    const nodeData = new NodeTask({
      id,
      name,
      type: '4'
    });
    const nodeRobot = this.graph.addNode({
      id,
      shape: 'NodeRobot',
      x,
      y,
      label: name,
      data: {}
    });
    nodeRobot.setData(nodeData);
  }
  addNodeCollab({ x, y, type, id }) {
    if (!id) {
      id = sGetNewID({
        prefix: 'Collab',
        cells: this.getCells()
      });
    }
    const name = sGetNewName({
      type,
      cells: this.getCells()
    });

    const nodeData = this.initTaskData(id, name, '3');
    const nodeCollab = this.graph.addNode({
      id,
      shape: 'NodeCollab',
      x,
      y,
      label: name,
      data: {}
    });
    nodeCollab.setData(nodeData);
  }
  setSwimlanePositions(cells) {
    let positions = [];
    cells.forEach(item => {
      if (item.shape === 'NodeSwimlane') {
        positions.push(item.position);
      }
    });
    this.swimlanePositions = positions;
  }
  addSwimlanePosition(position) {
    this.swimlanePositions.push(position);
  }
  removeSwimlanePosition(position) {
    const curIndex = this.getSwimlanePositionIndex(position);
    this.swimlanePositions.splice(curIndex, 1);
  }
  getSwimlanePositionIndex(position) {
    const hasIndex = this.swimlanePositions.findIndex(item => {
      return item.x === position.x && item.y === position.y;
    });
    return hasIndex;
  }
  /* 环节节点 */
  setTasks(cells) {
    let tasks = [];
    let datas = [];
    cells.forEach(item => {
      if (item.shape === 'NodeTask' || item.shape === 'NodeCollab') {
        tasks.push(item);
        datas.push(item.data);
      }
    });
    this.tasks = tasks;
    this.setTasksData(datas);
  }
  addTask(node) {
    this.tasks.push(node);
    this.addTaskData(node.data);
    this.newTasks.push(node);
    this.newTasksUnEdited.push(node);
  }
  removeTask(node) {
    const curIndex = this.tasks.findIndex(item => item.id === node.id);
    this.tasks.splice(curIndex, 1);
    this.removeTaskData(node, curIndex);
  }
  /* 环节数据 */
  setTasksData(datas) {
    this.tasksData = datas;
  }
  addTaskData(data) {
    this.tasksData.push(data);
  }
  removeTaskData(node, curIndex) {
    this.tasksData.splice(curIndex, 1);
  }
  /* 子流程节点 */
  setSubflows(cells) {
    let subflows = [];
    let datas = [];
    cells.forEach(item => {
      if (item.shape === 'NodeSubflow') {
        subflows.push(item);
        datas.push(item.data);
      }
    });
    this.subflows = subflows;
    this.setSubflowsData(datas);
  }
  addSubflow(node) {
    this.subflows.push(node);
    this.addSubflowDat(node.data);
    this.newSubflows.push(node);
    this.newSubflowsUnEdited.push(node);
  }
  removeSubflow(node) {
    const curIndex = this.subflows.findIndex(item => item.id === node.id);
    this.subflows.splice(curIndex, 1);
    this.removeSubflowData(node, curIndex);
  }
  /* 子流程数据 */
  setSubflowsData(datas) {
    this.subflowsData = datas;
  }
  addSubflowDat(data) {
    this.subflowsData.push(data);
  }
  removeSubflowData(node, curIndex) {
    this.subflowsData.splice(curIndex, 1);
  }
  /* 流向边 */
  setDirections(cells) {
    let directions = [];
    let datas = [];
    cells.forEach(item => {
      if (item.shape === 'EdgeDirection') {
        if (item.data.toID) {
          directions.push(item);
          datas.push(item.data);
        }
      }
    });
    this.directions = directions;
    this.setDirectionsData(datas);
  }
  addDirection(edge) {
    this.directions.push(edge);
    this.addDirectionData(edge.data);
  }
  removeDirection(edge) {
    const curIndex = this.directions.findIndex(item => item.id === edge.id);
    this.directions.splice(curIndex, 1);
    this.removeDirectionData(edge, curIndex);
  }
  /* 流程数据 */
  setDirectionsData(datas) {
    this.directionsData = datas;
  }
  addDirectionData(data) {
    this.directionsData.push(data);
  }
  removeDirectionData(edge, curIndex) {
    this.directionsData.splice(curIndex, 1);
  }
  // 重置新增节点
  resetNewNode() {
    this.newTasks = [];
    this.newTasksUnEdited = [];
    this.newTasksEdited = [];
    this.newSubflows = [];
    this.newSubflowsUnEdited = [];
    this.newSubflowsEdited = [];
  }
  // 批量更新元素和业务id
  batchUpdateCellAndDataId(updataCells) {
    for (let index = 0; index < updataCells.length; index++) {
      const cell = updataCells[index];
      const cellData = cell.data;
      const shape = cell.shape;
      const cells = this.getCells();

      let type = 'TASK',
        id = '',
        name = '';
      if (shape === 'NodeTask') {
        id = sGetNewTaskID({
          type,
          nodes: this.getNodes()
        });
        name = sGetNewName({
          type,
          cells
        });

        if (cell.data.i18n) {
          for (const lan in cell.data.i18n) {
            for (const key in cell.data.i18n[lan]) {
              let nameI18n,
                resetName = false;
              if (key === `${cell.data.id}.taskName`) {
                resetName = true;
                // nameI18n = cell.data.i18n[lan][key]
                delete cell.data.i18n[lan][key];
              }
              if (lan === 'zh_CN') {
                nameI18n = name;
              }
              if (resetName) {
                cell.data.i18n[lan][`${id}.taskName`] = nameI18n;
              }
            }
          }
        }
        cell.setData({ id, name });
      } else if (shape === 'NodeSubflow') {
        type = 'SUBFLOW';
        id = sGetNewTaskID({
          type,
          nodes: this.getNodes()
        });
        name = sGetNewName({
          type,
          cells
        });
        cell.setData({ id, name });
      } else if (shape === 'NodeCondition') {
        type = 'CONDITION';
        id = sGetNewID({
          prefix: 'Condition',
          cells
        });
        name = sGetNewName({
          type,
          cells
        });
        cell.prop('label', name); // prop()获取所有的属性
        cell.setData({ name });
      } else if (cellData.id === constant.StartFlowId) {
        id = sGetNewID({
          prefix: 'Start',
          cells
        });
      } else if (cellData.id === constant.EndFlowId) {
        id = sGetNewID({
          prefix: 'End',
          cells
        });
      }
      if (id) {
        const newCell = this.graph.updateCellId(cell, id);
        if (shape === 'NodeTask') {
          this.newTasks.push(newCell);
          this.newTasksUnEdited.push(newCell);
        } else if (shape === 'NodeSubflow') {
          this.newSubflows.push(newCell);
          this.newSubflowsUnEdited.push(newCell);
        }
      }
    }
  }
  // 前续节点
  getPredecessors(cell, options) {
    return this.graph.getPredecessors(cell, options);
  }
  // 后续节点
  getSuccessors(cell, options) {
    return this.graph.getSuccessors(cell, options);
  }
  // 获取节点/边的输入边
  getIncomingEdges(cell) {
    // this.graph.model.incomings 元素输入的键值对
    return this.graph.getIncomingEdges(cell);
  }
  // 获取节点/边的输出边
  getOutgoingEdges(cell) {
    // this.graph.model.outgoings 元素输出的键值对
    return this.graph.getOutgoingEdges(cell);
  }
  // 获取与节点/边相连接的边
  getConnectedEdges(cell, options) {
    return this.graph.getConnectedEdges(cell, options);
  }
  // 显示连接桩
  showNodePorts(cell) {
    const ports = cell.getPorts();
    ports.forEach(port => {
      cell.setPortProp(port.id || port, 'attrs/circle/style', { visibility: 'visible' });
    });
  }
  // 隐藏连接桩
  hideNodePorts(cell) {
    const ports = cell.getPorts();
    ports.forEach(port => {
      cell.setPortProp(port.id || port, 'attrs/circle/style', { visibility: 'hidden' });
    });
  }
  // 设置选中节点
  setSelectedNode(cell) {
    const cellId = cell.id;
    const shape = cell.shape;
    const dataId = cell.data.id;
    if (shape === 'NodeCircle') {
      return;
    }
    if (shape === 'NodeLabel') {
      this.selectedLabelId = cellId;
      return;
    }
    this.setSelectedId(cellId);
  }
  // 设置选中边
  setSelectedEdge(edge) {
    let canSelect = true;
    // const cells = this.getCells()
    // if (cells && cells.length) {
    //   cells.forEach(item => {
    //     if (item.id !== cell.id) {
    //       item.attr('line/targetMarker/stroke', 'var(--workflow-edge-stroke)')
    //       item.attr('line/targetMarker/strokeWidth', 'var(--workflow-edge-stroke-width)')
    //     }
    //   });
    // }

    // cell.attr('line/targetMarker/stroke', 'var(--workflow-edge-selected-stroke)')
    // cell.attr('line/targetMarker/strokeWidth', 'var(--workflow-edge-selected-width)')
    const edgeData = edge.data;
    if (!Object.keys(edgeData).length) {
      canSelect = false;
    }
    if (edgeData.type === '3') {
      const sourceCell = edge.getSourceCell();
      const targetCell = edge.getTargetCell();
      // 环节指向判断点没有配置
      if (sourceCell.shape === 'NodeTask' && targetCell.shape === 'NodeCondition') {
        canSelect = false;
      }
    }
    if (canSelect) {
      const cellId = edge.id;
      this.setSelectedId(cellId);
    }
    if (this.designer.i18nEl) {
      this.designer.i18nEl.workflowName.visible = false;
    }
  }
  setTargetMarkerNormal() {
    const edges = this.getEdges();
    if (edges && edges.length) {
      edges.forEach(item => {
        item.attr('line/targetMarker/stroke', 'var(--workflow-edge-stroke)');
        item.attr('line/targetMarker/strokeWidth', 'var(--workflow-edge-stroke-width)');
      });
    }
  }
  // 更新节点数据id
  updateNodeDataId({ nodeDataId, oldNodeDataId }) {
    console.log(nodeDataId, oldNodeDataId);

    const incomings = this.getIncomingEdges(this.selectedId);
    if (incomings) {
      incomings.map(item => {
        item.data.toID = nodeDataId;
      });
    }
    const outgoings = this.getOutgoingEdges(this.selectedId);
    if (outgoings) {
      outgoings.map(item => {
        item.data.fromID = nodeDataId;
      });
    }
  }
  setSelectedId(id = '') {
    this.selectedId = id;
    this.designer.setSelectedCellId(id);
  }
  // 修改路由
  modifyRoute(routerName) {
    const edge = this.getSelectedCell();
    const edgeRouter = edge.getRouter();
    const edgeRouterArgs = edgeRouter.args;

    const sourceCell = edge.getSourceCell();
    const targetCell = edge.getTargetCell();
    edge.data.line = `${routerName};${edgeRouterArgs.way}`;
    if (routerName === 'normal') {
      edge.setSource(sourceCell, { anchor: 'midSide' });
      edge.setTarget(targetCell, { anchor: 'midSide' });
    } else {
      edge.setSource(sourceCell);
      edge.setTarget(targetCell);
    }
    edge.setRouter(routerName, { way: edgeRouterArgs.way });
  }
  // 流向反向
  reverseEdge() {
    const edge = this.getSelectedCell();
    const sourceCell = edge.getSourceCell();
    const targetCell = edge.getTargetCell();

    const edgeRouter = edge.getRouter();
    const way = edgeRouter.args.way.split(';');
    const newWay = way.reverse().join(';');

    let edgeData = edge.getData();
    const fromID = edgeData.fromID;
    const toID = edgeData.toID;
    edgeData.fromID = toID;
    edgeData.toID = fromID;

    this.graph.removeCell(edge);

    const newEdge = edge.clone({ keepId: true }); // 克隆元素并保留原有id
    newEdge.setRouter(edgeRouter.name, { way: newWay });
    newEdge.setSource(targetCell);
    newEdge.setTarget(sourceCell);
    newEdge.setData(edgeData);
    this.graph.addEdge(newEdge);
    this.resetSelection(edge.id);
  }
  setEdgesLablesByName(name, cellId = this.selectedId) {
    let selectedCell, edgeDatas = []
    if (!name && cellId) {
      selectedCell = this.getCellById(cellId);
      name = selectedCell.data.name
    }
    if (name) {
      name = `送${name}`;
    }

    if (cellId) {
      if (!selectedCell) {
        selectedCell = this.getCellById(cellId);
      }
      if (selectedCell && selectedCell.isNode()) {
        const incomings = this.getIncomingEdges(selectedCell);
        incomings.map(edge => {
          let canSelect = true;
          const edgeData = edge.data;
          if (edgeData.type === '3') {
            const sourceCell = edge.getSourceCell();
            const targetCell = edge.getTargetCell();
            // 环节指向判断点没有配置
            if (sourceCell.shape === 'NodeTask' && targetCell.shape === 'NodeCondition') {
              canSelect = false;
            }
          }

          if (edgeData.autoUpdateName && canSelect) {
            edgeData.name = name;
            if (edgeData.i18n && edgeData.i18n['zh_CN']) {
              edgeData.i18n['zh_CN'][`${edgeData.id}.directionName`] = name;
            }
            this.setEdgeLabels(edge, name);
            this.designer.languageOptions.map(item => {
              if (item.value !== 'zh_CN' &&
                edgeData.i18n &&
                edgeData.i18n[item.value] &&
                edgeData.i18n[item.value][`${edgeData.id}.directionName`] !== undefined
              ) {
                edgeDatas.push(edgeData)
              }
            })
          }
        });
      }
    }

    return new Promise((resolve, reject) => {
      if (edgeDatas.length) {
        this.autoUpdateEdgeName(edgeDatas, name).then(
          () => {
            resolve()
          },
          err => {
            reject(err)
          }
        )
      } else {
        resolve()
      }
    })
  }
  autoUpdateEdgeName(edgeDatas, name) {
    return new Promise((resolve, reject) => {
      this.designer.translateI18nAll(name).then(i18n => {
        edgeDatas.map(item => {
          if (item.i18n) {
            for (const key in i18n) {
              item.i18n[key][`${item.id}.directionName`] = i18n[key]
            }
          }
        })
        resolve()
      }, err => {
        reject(err)
      })
    })
  }
  setSelectedEdgeLabels(labels) {
    const edge = this.getSelectedCell();
    this.setEdgeLabels(edge, labels);
  }
  setEdgeLabels(edge, labels) {
    edge.setLabels([labels]);
  }
  positionContent(pos, options = {}) {
    if (typeof options.useCellGeometry === 'undefined') {
      options.useCellGeometry = false;
    }
    const padding = NumberExt.normalizeSides(options.padding || 20);
    const rect = this.graph.getContentArea(options);
    // const bbox = Rectangle.create(rect);
    const bbox = rect.clone().moveAndExpand({
      x: -padding.left,
      y: -padding.top,
      width: padding.left + padding.right,
      height: padding.top + padding.bottom
    });
    this.graph.positionRect(bbox, pos);
  }
  positionPoint(point, x, y) {
    this.graph.positionPoint(point, x, y);
  }
  getNodesTree({ nodes, level = 0, maxLevel = 7, parentKey = null, expandedKeys = [] }) {
    // const { nodes, incomings } = this.graph.model
    // let rootNodeIds = []
    // for (const key in nodes) {
    //   if (!incomings[key]) {
    //     rootNodeIds.push(key)
    //   }
    // }

    let nodesTree = [];
    let nodesList = [];
    const createTree = (data, level, maxLevel, parentKey) => {
      if (level > maxLevel) {
        return [];
      }
      return data.map((item, index) => {
        const key = `${parentKey ? parentKey + '-' : ''}${index}`;
        let className = item.shape;
        if (item.data.id === constant.EndFlowId) {
          className += '-END';
        }
        let node = {
          key,
          title: item.data.name,
          children: [],
          class: className,
          scopedSlots: {
            icon: 'nodeIcon',
            title: 'nodeTitle'
          },
          isLeaf: false,
          sourceData: item
        };
        const successors = this.getSuccessors(item, { distance: 1 });
        if (!successors.length) {
          node.isLeaf = true;
        }
        const outgoings = this.getOutgoingEdges(item);
        if (outgoings) {
          const targetCell = outgoings.map(edge => edge.getTargetCell());
          node.children = createTree(targetCell, level + 1, maxLevel, key);
        }
        if (!node.isLeaf && node.children.length) {
          expandedKeys.push(node.key);
        }
        nodesList.push(node);
        return node;
      });
    };
    if (nodes && nodes.length) {
      nodesTree = createTree(nodes, level, maxLevel, parentKey);
    }

    return { nodesTree, expandedKeys, nodesList };
  }
  centerCell(cell, options) {
    if (!cell) {
      return;
    }
    if (typeof cell === 'string') {
      cell = this.getCellById(cell);
    }
    this.graph.centerCell(cell, options);
  }
  getDataById(id) {
    const node = this.getCellById(id);
    if (!node) {
      return null;
    }
    return node.getData();
  }
  getComputedSize() {
    let w = 0,
      h = 0;
    if (this.container) {
      w = this.container.parentElement.clientWidth;
      h = this.container.parentElement.clientHeight;
    }
    return { width: w, height: h };
  }
  setStartNodePosition(point, x = '50%', y = 70) {
    const clientSize = this.getComputedSize();
    x = NumberExt.normalizePercentage(x, Math.max(0, clientSize.width));
    if (x < 0) {
      x = clientSize.width + x;
    }
    y = NumberExt.normalizePercentage(y, Math.max(0, clientSize.height));
    if (y < 0) {
      y = clientSize.height + y;
    }
    const scale = { sx: 1, sy: 1 };
    const dx = x - point.x * scale.sx;
    const dy = y - point.y * scale.sy;
    this.graph.translate(dx, dy);
  }
  removeCells(cells) {
    this.graph.removeCells(cells);
    this.setSelectedId();
  }
  exportPNG(fileName, options = {}) {
    this.graph.exportPNG(fileName, options);
  }
  fromJSON(flowJson) {
    this.renderAmount = flowJson.cells.length;
    if (flowJson.cells && this.renderAmount) {
      this.cellsEmpty = false;
    }
    if (!this.renderAmount) {
      this.renderDone = true;
    }
    this.graph.fromJSON(flowJson);
    // this.graph.centerContent()
    const cells = this.getCells();
    this.setTasks(cells);
    this.setSubflows(cells);
    this.setDirections(cells);
    this.setSwimlanePositions(flowJson.cells);
  }
  toJSON() {
    return this.graph.toJSON();
  }
  // 先清空选区，然后选中提供的节点/边。
  resetSelection(id, options) {
    this.graph.resetSelection(id, options);
  }
  // 选中指定的节点/边。需要注意的是，该方法不会取消选中当前选中的节点/边
  select(id) {
    this.graph.select(id);
  }
  getCells() {
    return this.graph.getCells();
  }
  getNodes() {
    return this.graph.getNodes();
  }
  getEdges() {
    return this.graph.getEdges();
  }
  getSelectedCells() {
    return this.graph.getSelectedCells();
  }
  getSelectedCell() {
    return this.getCellById(this.selectedId);
  }
  getCellById(id) {
    return this.graph.getCellById(id);
  }
  zoom(factor) {
    if (typeof factor === 'undefined') {
      return this.graph.zoom();
    }
    this.graph.zoom(factor);
  }
  zoomToFit(options) {
    this.graph.zoomToFit(options);
  }
  zoomTo(factor) {
    this.graph.zoomTo(factor);
  }
  getPlugin(pluginName) {
    // 查找 installedPlugins 是个Set
    return this.graph.getPlugin(pluginName);
  }
  canUndo() {
    return this.graph.canUndo();
  }
  undo() {
    this.graph.undo();
  }
  clearCells() {
    this.graph.clearCells();
    this.setSelectedId();
  }
  destroy() {
    this.graph.dispose();
  }
}

export default FlowGraph;
