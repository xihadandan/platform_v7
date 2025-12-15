import { Graph, Shape } from '@antv/x6'
import { Selection } from '@antv/x6-plugin-selection'
import { Snapline } from '@antv/x6-plugin-snapline'
import { Keyboard } from '@antv/x6-plugin-keyboard'
import { Clipboard } from '@antv/x6-plugin-clipboard'
import { History } from '@antv/x6-plugin-history'
import { Export } from '@antv/x6-plugin-export' // 导出
import { createShape } from './shape'
import NodeTask from '../designer/NodeTask'
import EdgeDirection from "../designer/EdgeDirection"
import NodeSubflow from '../designer/NodeSubflow'
import { sGetNewTaskID, sGetNewName, sGetNewDirectionID, sGetNewID, sGetTaskCurveWay, oCreateTask, aTaskCurvePoint, aGetCurvePoint } from '../designer/utils';
import constant, { flowTaskRights } from '../designer/constant';
import { NumberExt } from '@antv/x6-common';


class FlowGraph {
  constructor({ id = 'graph-container', designer = undefined, themeClass = '' } = {}) {
    this.graph = undefined
    this.designer = designer
    this.selectedTool = ''
    this.selectedId = ''
    this.tasks = []
    this.tasksData = []
    this.subflows = []
    this.subflowsData = []
    this.directions = []
    this.directionsData = []
    this.previousCell = undefined
    this.newTasks = [] // 新增环节
    this.newTasksUnEdited = [] // 未编辑过的新增环节
    this.newTasksEdited = [] // 编辑过的新增环节,点击打开配置认为编辑过，再次进入不能修改环节id
    this.newSubflows = []
    this.newSubflowsUnEdited = []
    this.newSubflowsEdited = []
    this.dragInfo = { startX: 0, startY: 0, x: 0, y: 0 }
    this.currentInfo = {
      x: 0,
      y: 0,
      sourceWay: '',
      targetWay: '',
      edge: undefined,
      enterNode: undefined,
      leaveNode: undefined,
      targetNode: undefined,
      sourceNode: undefined,
      isNewEdge: true
    }
    createShape(designer, themeClass)
    this.registerRouter()
    this.init(id)
  }
  init(id) {
    const that = this
    const container = document.getElementById(id)
    const graph = new Graph({
      container,
      autoResize: true,
      grid: {
        type: 'mesh',
        visible: true,
        size: 28,
        args: {
          color: '#F2F2F2',
          thickness: 1,
        },
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
        minScale: 0.5,
        maxScale: 3
      },
      // 交互连线
      connecting: {
        router: 'manhattan',
        connector: { // 连接器将起点、路由返回的点、终点加工为 <path> 元素的 d 属性
          name: 'rounded',
          args: {
            radius: 8
          }
        },
        anchor: 'center',
        //connectionPoint: 'anchor',
        snap: { // 自动吸附
          radius: 20
        },
        allowBlank: false,
        allowLoop: false, // 边的起始点和终止点不能同一节点
        allowMulti: false, // 是否允许在相同的起始节点和终止之间创建多条边
        // 点击 magnet=true 的元素时
        validateMagnet(params) {
          console.log('validateMagnet', params)
          const { e } = params
          const portPoint = this.pageToLocal(e.pageX, e.pageY)
          that.currentInfo.x = portPoint.x
          that.currentInfo.y = portPoint.y
          return true
        },
        createEdge(params) {
          console.log('createEdge', params)
          const { sourceCell, sourceMagnet } = params
          const sourceWay = sourceMagnet.getAttribute('port-group')

          // that.currentInfo.sourceWay = sourceWay
          // const edgeId = sGetNewDirectionID({
          //   edges: this.getEdges()
          // })
          // const edge = this.createEdge({
          //   id: edgeId,
          //   router: {
          //     name: 'curve',
          //     args: {
          //       way: sourceWay,
          //     }
          //   },
          //   shape: 'EdgeDirection',
          //   data: {}
          // })

          const edge = that.createEdgeDirection(sourceCell, sourceWay)
          return edge
        },
        // 在移动边的时候判断连接是否有效
        validateConnection(params) {
          console.log('validateConnection', params)
          const { edge, sourceCell, targetCell, sourceMagnet, targetMagnet } = params

          if (!sourceCell) {
            return false;
          }
          if (targetCell) {
            that.showNodePorts(targetCell)
          } else {
            return false;
          }

          let targetWay = ''
          if (targetMagnet) {
            targetWay = targetMagnet.getAttribute('port-group')
          }
          that.currentInfo.targetWay = targetWay
          edge.router.args.way = `${that.currentInfo.sourceWay};${targetWay}`
          // 目标元素可以被链接
          return !!targetMagnet
        },
        // 当停止拖动边的时候
        validateEdge(params) {
          console.log('validateEdge', params)
          return true
        }
      },
      // // 交互限制
      // interacting(cellView) {
      //   return true
      // },
      // // 移动范围
      // translating: {
      //   restrict(cellView) {
      //     return true
      //   }
      // }
    })
    graph
      .use(
        new Selection({
          modifiers: 'shift',
          rubberband: true,
          // showNodeSelectionBox: true
        })
      )
      .use(new Snapline())
      .use(new Keyboard())
      .use(new Clipboard())
      .use(new Export())
      .use(new History({
        enabled: true,
        beforeAddCommand(event, args) {
          // 连接桩不加入历史记录
          if (args && args.key === 'ports') {
            return false
          }
          return true
        }
      }))

    window.__x6_instances__ = []
    window.__x6_instances__.push(graph)
    this.graph = graph
    this.initEvent()
  }
  initEvent() {
    const { graph } = this
    graph.bindKey(['meta+c', 'ctrl+c'], () => {
      let cells = graph.getSelectedCells()
      cells = cells.filter(cell => cell.shape !== 'EdgeDirection') // 边不复制
      if (cells.length) {
        graph.copy(cells)
      }
      return false
    })
    graph.bindKey(['meta+x', 'ctrl+x'], () => {
      const cells = graph.getSelectedCells()
      if (cells.length) {
        graph.cut(cells)
      }
      return false
    })
    graph.bindKey(['meta+v', 'ctrl+v'], () => {
      if (!graph.isClipboardEmpty()) {
        const cells = graph.paste({ offset: 32 })
        this.batchUpdateCellAndDataId(cells)
        graph.cleanSelection()
      }
      return false
    })

    // undo redo
    graph.bindKey(['meta+z', 'ctrl+z'], () => {
      if (graph.canUndo()) {
        graph.undo()
      }
      return false
    })
    graph.bindKey(['meta+shift+z', 'ctrl+shift+z'], () => {
      if (graph.canRedo()) {
        graph.redo()
      }
      return false
    })

    graph.bindKey(['meta+a', 'ctrl+a'], () => {
      const nodes = graph.getNodes()
      if (nodes) {
        graph.select(nodes)
      }
    })

    graph.bindKey(['delete'], () => {
      const cells = graph.getSelectedCells()
      if (cells.length) {
        this.removeCells(cells)
      }
    })

    // zoom
    graph.bindKey(['ctrl+1', 'meta+1'], () => {
      const zoom = graph.zoom()
      if (zoom < 1.5) {
        graph.zoom(0.1)
      }
    })
    graph.bindKey(['ctrl+2', 'meta+2'], () => {
      const zoom = graph.zoom()
      if (zoom > 0.5) {
        graph.zoom(-0.1)
      }
    })

    // 控制连接桩显示/隐藏
    const showPorts = (view, show) => {
      const ports = view.container.querySelectorAll('.x6-port-body')
      for (let i = 0, len = ports.length; i < len; i += 1) {
        ports[i].style.visibility = show ? 'visible' : 'hidden'
      }
    }

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
      const cells = graph.getCells()
      if (cells && cells.length) {
        graph.centerContent()
      }
    })
    graph.on('cell:added', (params) => {
      console.log('cell:added', params)
    })
    graph.on('cell:click', (params) => {
      console.log('cell:click', params)
      const { cell } = params
    });
    graph.on('blank:click', ({ e, x, y }) => {
      const type = this.designer.getSelectedTool()
      this.checkAddCellType({ x, y, type })
    });
    graph.on('cell:mousemove', (params) => {
      console.log('cell:mousemove', params)
      const { x, y } = params
      this.currentInfo.x = x
      this.currentInfo.y = y
    })
    graph.on('cell:removed', (params) => {
      console.log('cell:removed', params)
      const { cell } = params
      if (cell.shape === 'NodeTask') {
        this.removeTask(cell)
      } else if (cell.shape === 'NodeSubflow') {
        this.removeSubflow(cell)
      } else if (cell.shape === 'EdgeDirection') {
        this.removeDirection(cell)
      }
    })
    graph.on('cell:selected', (params) => {
      console.log('cell:selected', params)
      const { cell, options } = params
      this.selectedCell(cell)
    })
    graph.on('node:mouseenter', (args) => {
      console.log('node:mouseenter', args)
      const { cell, node, view, e } = args
      this.currentInfo.enterNode = cell
      // getDir(e, e.target)

      // const rect = cell.getBBox()
      // rect.x = rect.x + this.graph.options.x + 224; // 224左侧选择面板
      // rect.y = rect.y + this.graph.options.y + 108
      const clientRect = e.target.getBoundingClientRect()
      /*
       rect.x + this.graph.options.x + 224 === clientRect.x
       rect.y + this.graph.options.y + 108 === clientRect.y
      */
      const way = sGetTaskCurveWay({
        w: clientRect.width, // e.target.offsetWidth,
        h: clientRect.height, // e.target.offsetHeight,
        x: clientRect.x,
        y: clientRect.y
      }, e.pageX, e.pageY)

      this.showNodePorts(cell)
    })
    graph.on('node:mouseleave', (params) => {
      console.log('node:mouseleave', params)
      const { cell, node, view, e } = params
      this.currentInfo.leaveNode = cell

      const type = this.designer.getSelectedTool()
      if (type === 'CURVE' || type === 'BEELINE') {
        const sourceNode = this.currentInfo.sourceNode
        const currentEdge = this.currentInfo.edge
        if (sourceNode && !currentEdge) {
          const clientRect = e.target.getBoundingClientRect()
          const way = sGetTaskCurveWay({
            w: clientRect.width,
            h: clientRect.height,
            x: clientRect.x,
            y: clientRect.y
          }, e.pageX, e.pageY)

          const edge = this.createEdgeDirection(sourceNode, way)
          this.currentInfo.edge = edge
          graph.addEdge(edge)
        }
      }

      this.hideNodePorts(cell)
    })
    graph.on('edge:contextmenu', (params) => {
      const type = this.designer.getSelectedTool()
      if (type === 'CURVE' || type === 'BEELINE') {
        const sourceNode = this.currentInfo.sourceNode
        const currentEdge = this.currentInfo.edge
        if (sourceNode && currentEdge) {
          this.removeUnselectCell(currentEdge, sourceNode)
        }
      }
    });
    graph.on('edge:connected', (params) => {
      console.log('edge:connected', params)
      const { isNew, edge, currentCell } = params
      if (isNew) {
        this.setEdgeData(edge)
      }
    })
  }
  createEdgeDirection(sourceCell, sourceWay) {
    const { graph } = this
    const routerName = this.getSelectedRouterName()
    const edgeId = sGetNewDirectionID({
      edges: this.getEdges()
    })
    const edge = graph.createEdge({
      id: edgeId,
      shape: 'EdgeDirection',
      source: {
        cell: sourceCell.id,
        anchor: {
          name: 'center',
          args: {
            // dy
          }
        },
      },
      router: {
        name: routerName,
        args: {
          way: sourceWay,
        }
      },
      data: {}
    })

    this.currentInfo.sourceWay = sourceWay
    return edge
  }
  selectedCell(cell) {
    const { graph } = this
    const type = this.designer.getSelectedTool()

    const sourceNode = this.currentInfo.sourceNode
    if (cell.isNode()) {
      if (type === 'CURVE' || type === 'BEELINE') {
        this.currentInfo.sourceNode = cell
      } else if (type === 'DEFAULT') {
        this.setSelectedNode(cell)
      }
    } else if (cell.isEdge()) {
      if (type === 'CURVE' || type === 'BEELINE') {
        const currentEdge = this.currentInfo.edge
        if (currentEdge) {
          let targetCell
          const targetPoint = currentEdge.getTargetPoint()
          const targetPointNode = graph.getNodesFromPoint(targetPoint)[0]
          if (
            targetPointNode &&
            sourceNode &&
            targetPointNode.id !== sourceNode.id
          ) {
            targetCell = targetPointNode
          }
          if (
            sourceNode &&
            targetCell &&
            sourceNode.id !==
            targetCell.id
          ) {
            this.removeUnselectCell(currentEdge, currentEdge)

            const way = `${this.currentInfo.sourceWay};${this.currentInfo.targetWay}`
            const newEdge = this.createEdgeDirection(sourceNode, way)
            newEdge.setTarget(targetCell)
            graph.addEdge(newEdge)
            this.setEdgeData(newEdge)
            this.currentInfo.sourceNode = undefined
          } else {
            this.removeUnselectCell(currentEdge, currentEdge)
          }
        }
      } else if (type === 'DEFAULT') {
        this.setSelectedEdge(cell)
      }
    }
  }
  removeUnselectCell(removeCell, unselectCell) {
    const { graph } = this
    graph.removeCell(removeCell) // graph.removeEdge
    graph.unselect(unselectCell)
    this.currentInfo.sourceNode = undefined
    this.currentInfo.edge = undefined
  }
  setEdgeData(edge) {
    let name = ''
    let type = '1'
    let terminalType = ''
    let terminalName = ''
    const sourceCell = edge.getSourceCell()
    const sourceData = sourceCell.getData()
    const targetCell = edge.getTargetCell()
    const targetData = targetCell.getData()

    let fromID = sourceData.id
    if (sourceCell.shape === 'NodeCondition') {
      type = '2'
      // 前续节点
      const predecessors = this.getPredecessors(sourceCell, {
        distance: 1
      })
      if (predecessors.length) {
        const preData = predecessors[0].getData()
        fromID = preData.id
      }
    }
    if (sourceData.id === constant.StartFlowId) {
      terminalType = constant.BEGIN
      terminalName = sourceData.name
      targetCell.setData({ canEditForm: '1' })
    } else {
      targetData.startRights = []
    }

    const toID = targetData.id
    if (targetCell.shape === 'NodeCondition') {
      sourceCell.setData(targetData)
    } else {
      name = `送${targetData.name}`
      edge.setLabels([name])
    }
    if (targetData.id === constant.EndFlowId) {
      terminalType = constant.END
      terminalName = targetData.name
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
      terminalName
    })
    edge.setData(edgeData)
    this.addDirection(edge)
  }
  registerRouter() {
    const that = this
    Graph.registerRouter(
      'curve',
      (vertices, options, edgeView) => that.curveRouter(vertices, options, edgeView),
      true,
    )
    Graph.registerRouter(
      'oneSideNew',
      (vertices, options, edgeView) => that.oneSideNew(vertices, options, edgeView),
      true,
    )
  }
  curveRouter(vertices, options, edgeView) {
    const { sourceView, targetView, cell, targetMagnet } = edgeView

    const sourceCell = sourceView.cell
    let targetCell
    if (targetView) {
      targetCell = targetView.cell
    }

    const sourceBBox = edgeView.sourceBBox;
    const targetBBox = edgeView.targetBBox;

    let sourceImgObject = {}
    sourceImgObject.x = sourceBBox.x
    sourceImgObject.y = sourceBBox.y
    sourceImgObject.w = sourceBBox.width
    sourceImgObject.h = sourceBBox.height

    let targetImgObject = {}
    targetImgObject.x = targetBBox.x
    targetImgObject.y = targetBBox.y
    targetImgObject.w = targetBBox.width
    targetImgObject.h = targetBBox.height

    let fromWay, toWay = null, { way } = options
    way = way.split(';')
    // console.log('---------->', way)
    fromWay = way[0]
    if (way[1]) {
      toWay = way[1]
    }
    // if (way.length > 1) {
    //   toWay = way[1]
    // } else {
    //   if (targetMagnet) {
    //     toWay = targetMagnet.getAttribute('port-group')
    //     cell.router.args.way = `${fromWay};${toWay}`
    //   }
    // }

    const getWayCurves = (edges) => {
      let wayCurves = {}
      const ways = ['up', 'down', 'left', 'right']
      for (let index = 0; index < ways.length; index++) {
        const way = ways[index];
        const edgesWay = []
        edges.map(item => {
          const args = item.router.args
          if (args.way.indexOf(way) > -1) {
            edgesWay.push({
              name: item.id
            })
          }
        })
        wayCurves[`${way}Curves`] = edgesWay
      }
      return wayCurves
    }

    const sourceConnectedEdges = this.getConnectedEdges(sourceCell.id)
    const sourceCurves = getWayCurves(sourceConnectedEdges)
    const sourceTask = oCreateTask({ name: sourceCell.id, imgObject: sourceImgObject, ...sourceCurves })

    // 从右到左  开始指向结束
    let sourceXY, targetXY, piMaxIndex, fromIndex, toIndex, laX_Y
    if (targetCell) {
      const isNewEdge = this.currentInfo.isNewEdge
      sourceXY = aTaskCurvePoint(sourceTask, fromWay, { name: cell.id }, isNewEdge)

      const targetConnectedEdges = this.getConnectedEdges(targetCell.id)
      const targetCurves = getWayCurves(targetConnectedEdges)
      const targetTask = oCreateTask({ name: targetCell.id, imgObject: targetImgObject, ...targetCurves })
      targetXY = aTaskCurvePoint(targetTask, toWay, { name: cell.id }, isNewEdge)
      fromIndex = sourceXY[2]
      toIndex = targetXY[2]
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

    const points = []
    laX_Y[0].forEach((x, index) => {
      points.push({
        x,
        y: laX_Y[1][index]
      })
    });
    return [...points.slice(1, points.length - 1)];
  }
  oneSideNew(vertices, options, edgeView) {
    console.log(options)
    const way = options.way || 'bottom';
    const padding = NumberExt.normalizeSides(options.padding || 20);
    const sourceBBox = edgeView.sourceBBox;
    const targetBBox = edgeView.targetBBox;

    // const sourcePoint = sourceBBox.getCenter();
    // const targetPoint = targetBBox.getCenter();

    const sourceAnchor = edgeView.sourceAnchor
    const sourcePoint = sourceAnchor.clone()
    const targetAnchor = edgeView.targetAnchor
    const targetPoint = targetAnchor.clone()

    if (options.dy > 0) {
      // sourcePoint.y = sourceAnchor.y
      sourcePoint.x = sourcePoint.x + options.dy
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
    }
    else {
      sourcePoint[coord] = targetPoint[coord];
    }
    console.log(sourcePoint)
    return [sourcePoint.toJSON(), ...vertices, targetPoint.toJSON()];
  }
  setContainerMoveEvent(event) {
    // console.log(event.target)
    const { graph } = this
    const { target } = event
    if (this.currentInfo.edge) {
      const movePoint = graph.pageToLocal(event.pageX, event.pageY)
      this.currentInfo.x = movePoint.x
      this.currentInfo.y = movePoint.y
      // const className = target.getAttribute('class')
      // if (className.indexOf('node-') === 0) {
      const cellDom = target.closest('g.x6-cell.x6-node')
      if (cellDom) {
        const clientRect = cellDom.getBoundingClientRect()
        const way = sGetTaskCurveWay({
          w: clientRect.width,
          h: clientRect.height,
          x: clientRect.x,
          y: clientRect.y
        }, event.pageX, event.pageY)

        this.currentInfo.targetWay = way
        const routerName = this.getSelectedRouterName()
        this.currentInfo.edge.setRouter({
          name: routerName,
          args: {
            way: `${this.currentInfo.sourceWay};${this.currentInfo.targetWay}`
          }
        })
      }
      this.currentInfo.edge.setTarget(movePoint)
    }
  }
  getSelectedRouterName() {
    const type = this.designer.getSelectedTool()
    const routerName = type === 'CURVE' ? 'curve' : 'normal'
    return routerName
  }
  updateDragInfo({
    startX = this.dragInfo.startX,
    startY = this.dragInfo.startY,
    x = this.dragInfo.x,
    y = this.dragInfo.y
  }) {
    this.dragInfo.startX = startX;
    this.dragInfo.startY = startY;
    this.dragInfo.x = x - this.graph.options.x;
    this.dragInfo.y = y - this.graph.options.y;
  }
  addDragNode({ type }) {
    let x = this.dragInfo.x
    let y = this.dragInfo.y
    this.checkAddCellType({ x, y, type })
  }
  checkAddCellType({ x, y, type }) {
    if (type === 'BEGIN' || type === 'END') {
      x = x - constant.NodeCircleWidth / 2
      y = y - constant.NodeCircleHeight / 2
    } else {
      x = x - constant.NodeDefaultWidth / 2
      y = y - constant.NodeDefaultHeight / 2
    }
    if (type === 'TASK') {
      this.addNodeTask({ x, y, type })
    } else if (type === 'SUBFLOW') {
      this.addNodeSubFlow({ x, y, type })
    } else if (type === 'BEGIN') {
      this.addNodeBegin({ x, y, type })
    } else if (type === 'END') {
      this.addNodeEnd({ x, y, type })
    } else if (type === 'CONDITION') {
      this.addNodeCondition({ x, y, type })
    }
  }
  addNodeBegin({ x, y }) {
    const { graph } = this
    const nodeId = sGetNewID({
      prefix: 'Start',
      cells: graph.getCells()
    })
    const dataId = constant.StartFlowId
    graph.addNode({
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
    const { graph } = this
    const nodeId = sGetNewID({
      prefix: 'End',
      cells: graph.getCells()
    })
    const dataId = constant.EndFlowId
    graph.addNode({
      id: nodeId,
      shape: 'NodeCircle',
      x,
      y,
      data: {
        id: dataId,
        name: constant.ENDNAME,
      }
    });
  }
  addNodeCondition({ x, y, type }) {
    const { graph } = this
    const conditionName = sGetNewName({
      type,
      cells: graph.getCells()
    })
    const nodeId = sGetNewID({
      prefix: 'Condition',
      cells: graph.getCells()
    })
    graph.addNode({
      id: nodeId,
      shape: 'NodeCondition',
      x,
      y,
      data: {
        conditionName,
        conditionBody: ''
      },
      label: conditionName
    });
  }
  addNodeSubFlow({ x, y, type }) {
    const { graph } = this
    const id = sGetNewTaskID({
      type,
      nodes: graph.getNodes()
    })
    const name = sGetNewName({
      type,
      cells: graph.getCells()
    })
    const nodeSubflowData = new NodeSubflow({
      id,
      name
    })
    const nodeSubflow = graph.addNode({
      id,
      shape: 'NodeSubflow',
      x,
      y,
      label: name,
      data: {}
    });
    nodeSubflow.setData(nodeSubflowData)
    this.addSubflow(nodeSubflow)
  }
  addNodeTask({ x, y, type }) {
    const { graph } = this
    const id = sGetNewTaskID({
      type,
      nodes: graph.getNodes()
    })
    const name = sGetNewName({
      type,
      cells: graph.getCells()
    })

    let defRights = {}
    let defRightConfigs = {}
    for (const key in flowTaskRights) {
      const settingKey = flowTaskRights[key]['settingKey']
      defRights[key] = this.designer.getRightOptions(settingKey)
      const configKey = flowTaskRights[key]['configKey']
      defRightConfigs[configKey] = this.designer.initRightConfig(defRights[key])
    }

    const nodeTaskData = new NodeTask({
      id,
      name,
      ...defRights,
      ...defRightConfigs
    });
    const nodeTask = graph.addNode({
      id,
      shape: 'NodeTask',
      x,
      y,
      label: name,
      data: {}
    });
    nodeTask.setData(nodeTaskData)
    this.addTask(nodeTask)
  }
  /* 环节节点 */
  setTasks(cells) {
    let tasks = []
    let datas = []
    cells.forEach(item => {
      if (item.shape === 'NodeTask') {
        tasks.push(item)
        datas.push(item.data)
      }
    });
    this.tasks = tasks
    this.setTasksData(datas)
  }
  addTask(node) {
    this.tasks.push(node)
    this.addTaskData(node.data)
    this.newTasks.push(node)
    this.newTasksUnEdited.push(node)
  }
  removeTask(node) {
    const curIndex = this.tasks.findIndex(item => item.id === node.id)
    this.tasks.splice(curIndex, 1)
    this.removeTaskData(node, curIndex)
  }
  /* 环节数据 */
  setTasksData(datas) {
    this.tasksData = datas
  }
  addTaskData(data) {
    this.tasksData.push(data)
  }
  removeTaskData(node, curIndex) {
    this.tasksData.splice(curIndex, 1)
  }
  /* 子流程节点 */
  setSubflows(cells) {
    let subflows = []
    let datas = []
    cells.forEach(item => {
      if (item.shape === 'NodeSubflow') {
        subflows.push(item)
        datas.push(item.data)
      }
    });
    this.subflows = subflows
    this.setSubflowsData(datas)
  }
  addSubflow(node) {
    this.subflows.push(node)
    this.addSubflowDat(node.data)
    this.newSubflows.push(node)
    this.newSubflowsUnEdited.push(node)
  }
  removeSubflow(node) {
    const curIndex = this.subflows.findIndex(item => item.id === node.id)
    this.subflows.splice(curIndex, 1)
    this.removeSubflowData(node, curIndex)
  }
  /* 子流程数据 */
  setSubflowsData(datas) {
    this.subflowsData = datas
  }
  addSubflowDat(data) {
    this.subflowsData.push(data)
  }
  removeSubflowData(node, curIndex) {
    this.subflowsData.splice(curIndex, 1)
  }
  /* 流向边 */
  setDirections(cells) {
    let directions = []
    let datas = []
    cells.forEach(item => {
      if (item.shape === 'EdgeDirection') {
        if (item.data.toID) {
          directions.push(item)
          datas.push(item.data)
        }
      }
    });
    this.directions = directions
    this.setDirectionsData(datas)
  }
  addDirection(edge) {
    this.directions.push(edge)
    this.addDirectionData(edge.data)
  }
  removeDirection(edge) {
    const curIndex = this.directions.findIndex(item => item.id === edge.id)
    this.directions.splice(curIndex, 1)
    this.removeDirectionData(edge, curIndex)
  }
  /* 流程数据 */
  setDirectionsData(datas) {
    this.directionsData = datas
  }
  addDirectionData(data) {
    this.directionsData.push(data)
  }
  removeDirectionData(edge, curIndex) {
    this.directionsData.splice(curIndex, 1)
  }
  // 重置新增节点
  resetNewNode() {
    this.newTasks = []
    this.newTasksUnEdited = []
    this.newTasksEdited = []
    this.newSubflows = []
    this.newSubflowsUnEdited = []
    this.newSubflowsEdited = []
  }
  // 批量更新元素和业务id
  batchUpdateCellAndDataId(updataCells) {
    const { graph } = this
    for (let index = 0; index < updataCells.length; index++) {
      const cell = updataCells[index];
      const cellData = cell.data;
      const shape = cell.shape;
      const cells = graph.getCells();

      let type = 'TASK', id = '', name = '';
      if (shape === 'NodeTask') {
        id = sGetNewTaskID({
          type,
          nodes: graph.getNodes()
        })
        name = sGetNewName({
          type,
          cells
        })
        cell.setData({ id, name })
      } else if (shape === 'NodeSubflow') {
        type = 'SUBFLOW'
        id = sGetNewTaskID({
          type,
          nodes: graph.getNodes()
        })
        name = sGetNewName({
          type,
          cells
        })
        cell.setData({ id, name })
      } else if (shape === 'NodeCondition') {
        type = 'CONDITION'
        id = sGetNewID({
          prefix: 'Condition',
          cells
        })
        name = sGetNewName({
          type,
          cells
        })
        cell.prop('label', name)  // prop()获取所有的属性
        cell.setData({ conditionName: name })
      } else if (cellData.id === constant.StartFlowId) {
        id = sGetNewID({
          prefix: 'Start',
          cells
        })
      } else if (cellData.id === constant.EndFlowId) {
        id = sGetNewID({
          prefix: 'End',
          cells
        })
      }
      if (id) {
        const newCell = graph.updateCellId(cell, id)
        if (shape === 'NodeTask') {
          this.newTasks.push(newCell)
          this.newTasksUnEdited.push(newCell)
        } else if (shape === 'NodeSubflow') {
          this.newSubflows.push(newCell)
          this.newSubflowsUnEdited.push(newCell)
        }
      }
    }
  }
  // 前续节点
  getPredecessors(cell, options) {
    return this.graph.getPredecessors(cell, options)
  }
  // 后续节点
  getSuccessors(cell, options) {
    return this.graph.getSuccessors(cell, options)
  }
  // 获取节点/边的输入边
  getIncomingEdges(cell) {
    // this.graph.model.incomings 元素输入的键值对
    return this.graph.getIncomingEdges(cell)
  }
  // 获取节点/边的输出边
  getOutgoingEdges(cell) {
    // this.graph.model.outgoings 元素输出的键值对
    return this.graph.getOutgoingEdges(cell)
  }
  // 获取与节点/边相连接的边
  getConnectedEdges(cell, options) {
    return this.graph.getConnectedEdges(cell, options)
  }
  // 显示连接桩
  showNodePorts(cell) {
    const ports = cell.getPorts();
    ports.forEach(port => {
      cell.setPortProp(port.id || port, 'attrs/circle/style', { visibility: 'visible' })
    });
  }
  // 隐藏连接桩
  hideNodePorts(cell) {
    const ports = cell.getPorts();
    ports.forEach(port => {
      cell.setPortProp(port.id || port, 'attrs/circle/style', { visibility: 'hidden' })
    });
  }
  // 先清空选区，然后选中提供的节点/边。
  resetSelection(id) {
    this.graph.resetSelection(id)
  }
  // 选中指定的节点/边。需要注意的是，该方法不会取消选中当前选中的节点/边
  select(id) {
    this.graph.select(id)
  }
  getCells() {
    return this.graph.getCells()
  }
  getNodes() {
    return this.graph.getNodes()
  }
  getEdges() {
    return this.graph.getEdges()
  }
  getSelectedCells() {
    return this.graph.getSelectedCells()
  }
  getSelectedCell() {
    return this.getCellById(this.selectedId)
  }
  getCellById(id) {
    return this.graph.getCellById(id)
  }
  // 设置选中节点
  setSelectedNode(cell) {
    const cellId = cell.id;
    const dataId = cell.data.id
    if (dataId === constant.StartFlowId || dataId === constant.EndFlowId) {
      return
    }
    this.setSelectedId(cellId);
  }
  // 设置选中边
  setSelectedEdge(cell) {
    const { graph } = this
    const cells = graph.getCells()
    if (cells && cells.length) {
      cells.forEach(item => {
        if (item.id !== cell.id) {
          item.attr('line/targetMarker/stroke', 'var(--workflow-edge-stroke)')
          item.attr('line/targetMarker/strokeWidth', 'var(--workflow-edge-stroke-width)')
        }
      });
    }

    cell.attr('line/targetMarker/stroke', 'var(--workflow-edge-selected-stroke)')
    cell.attr('line/targetMarker/strokeWidth', 'var(--workflow-edge-selected-width)')
    if (cell.data.toID) {
      const cellId = cell.id;
      this.setSelectedId(cellId);
    }
  }
  // 更新节点id
  updateNodeId({ value, oldValue }) {
    console.log(value, oldValue)
  }
  setSelectedId(id = '') {
    this.selectedId = id
    this.designer.setSelectedCellId(id);
  }
  setSelectedEdgeLabels(labels) {
    const edge = this.getSelectedCell()
    this.setEdgeLabels(edge, labels)
  }
  setEdgeLabels(edge, labels) {
    edge.setLabels([labels])
  }
  getDataById(id) {
    const node = this.getCellById(id)
    if (!node) {
      return null
    }
    return node.getData()
  }
  removeCells(cells) {
    this.graph.removeCells(cells)
    this.setSelectedId();
  }
  canUndo() {
    return this.graph.canUndo()
  }
  undo() {
    this.graph.undo()
  }
  exportPNG(fileName, options = {}) {
    this.graph.exportPNG(fileName, options)
  }
  fromJSON(flowJson) {
    this.graph.fromJSON(flowJson)
    this.graph.centerContent()
    const cells = this.getCells()
    this.setTasks(cells)
    this.setSubflows(cells)
    this.setDirections(cells)
  }
  toJSON() {
    return this.graph.toJSON()
  }
  destroy() {
    this.graph.dispose()
  }
}

export default FlowGraph
