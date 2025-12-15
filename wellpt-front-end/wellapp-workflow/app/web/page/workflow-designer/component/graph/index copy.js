import { Graph, Shape } from '@antv/x6'
import { Selection } from '@antv/x6-plugin-selection'
import { Snapline } from '@antv/x6-plugin-snapline'
import { Keyboard } from '@antv/x6-plugin-keyboard'
import { Clipboard } from '@antv/x6-plugin-clipboard'
import { History } from '@antv/x6-plugin-history'
import { createShape } from './shape'
import NodeTask from '../designer/NodeTask'
import EdgeDirection from "../designer/EdgeDirection"
import NodeSubflow from '../designer/NodeSubflow'
import { sGetNewTaskID, sGetNewName, sGetNewDirectionID, sGetNewID } from '../designer/utils';
import constant from '../designer/constant';


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
    createShape(designer, themeClass)
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
        validateMagnet() {
          console.log('validateMagnet', arguments)
          return true
        },
        createEdge(params) {
          console.log('createEdge', arguments)

          const { sourceCell, sourceMagnet } = params
          const cellPosition = sourceCell.getPosition() // 节点坐标
          const portId = sourceMagnet.getAttribute('port') // 连接桩id
          const groupName = sourceMagnet.getAttribute('port-group')
          const portsPosition = sourceCell.getPortsPosition(groupName)

          let portPosition //  连接桩坐标（matrix值）
          for (const key in portsPosition) {
            if (key === portId) {
              portPosition = portsPosition[key]['position']
            }
          }
          const curPortPosition = {
            x: portPosition.x + cellPosition.x,
            y: portPosition.y + cellPosition.y,
          }

          const outEdges = this.getOutgoingEdges(sourceCell)
          const hasIndex = outEdges.findIndex(edge => edge.source.port === portId);
          let args = {}
          let vertices = []
          console.log(hasIndex, curPortPosition)
          if (hasIndex > -1) {
            // args.dy = 10
            vertices.push({
              x: curPortPosition.x - 28 * 2,
              y: curPortPosition.y
            })
          }

          const edgeId = sGetNewDirectionID({
            edges: this.getEdges()
          })
          const edge = this.createEdge({
            id: edgeId,
            // source: {
            //   anchor: {
            //     name: 'center',
            //     args
            //   }
            // },
            vertices,
            shape: 'EdgeDirection',
            data: {}
          })

          return edge
        },
        // 在移动边的时候判断连接是否有效
        validateConnection({ sourceCell, targetCell, sourceMagnet, targetMagnet }) {
          console.log('validateConnection', arguments)
          if (!sourceCell) {
            return false;
          }
          if (!targetCell) {
            return false;
          }
          // if (targetCell) {
          //   return true
          // }

          // 目标元素可以被链接
          return !!targetMagnet
        },
        // 当停止拖动边的时候
        validateEdge() {
          console.log('validateEdge', arguments)
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
        // graph.select(cells)
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
    graph.on('node:mouseenter', ({ cell, node, view, e }) => {
      // showPorts(view, true)
      this.showNodePorts(cell)
    })
    graph.on('node:mouseleave', ({ cell, node, view, e }) => {
      // showPorts(view, false)
      this.hideNodePorts(cell)
    })
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

    graph.on('cell:selected', (params) => {
      console.log('cell:selected', params)
      const { cell, options } = params
      if (cell.isNode()) {
        this.setSelectedNode(cell)
      } else if (cell.isEdge()) {
        this.setSelectedEdge(cell)
      }
    })

    graph.on('node:click', (params) => {
      console.log('node:click', params)
      const { cell } = params
    });
    graph.on('edge:click', (params) => {
      console.log('edge:click', params)
      const { cell } = params
    });
    graph.on('blank:click', ({ e, x, y }) => {
      const type = this.designer.getSelectedTool()
      this.checkAddCellType({ x, y, type })
    });

    graph.on('resize', () => {
      const cells = graph.getCells()
      if (cells && cells.length) {
        graph.centerContent()
      }
    })

    graph.on('cell:added', (params) => {
      console.log('cell:added', params)
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

    graph.on('edge:connected', (params) => {
      console.log('edge:connected', params)
      const { isNew, edge, currentCell } = params
      if (isNew) {
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
    })

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
      x = x - 28
      y = y - 28
    } else {
      x = x - 85
      y = y - 28
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
    const startRights = this.designer.getDefaultRights('startRights')
    const rights = this.designer.getDefaultRights('todoRights')
    const doneRights = this.designer.getDefaultRights('doneRights')
    const monitorRights = this.designer.getDefaultRights('monitorRights')
    const adminRights = this.designer.getDefaultRights('adminRights')
    let nodeTaskData = new NodeTask({
      id,
      name,
      startRights,
      rights,
      doneRights,
      monitorRights,
      adminRights
    });
    nodeTaskData = JSON.parse(JSON.stringify(nodeTaskData))
    const task = graph.addNode({
      id,
      shape: 'NodeTask',
      x,
      y,
      label: name,
      data: nodeTaskData
    });
    this.addTask(task)
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
