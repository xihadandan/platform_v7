import { Graph, Point } from '@antv/x6';
import { History } from '@antv/x6-plugin-history';
import { isEmpty, debounce, findIndex } from 'lodash';
import {
  processNodePorts, itemNodePorts, portLayout, showNodePort, showNodePortByGroups, hideNodePort, hideNodePorts, createRightLineGroup,
  createBottomLineGroup
} from './node-ports.js';
import ProcessTree from './process-tree.js';
import NodeLayout from './node-layout.js';
import { deepClone, invokeVueMethod } from './utils.js';
let globalOptions = {};
// 注册节点端口布局
Graph.registerPortLayout('node-port', portLayout(globalOptions), true);
// 注册事项节点路由
Graph.registerRouter(
  'item-route',
  (vertices, args, view) => {
    let points = vertices.map(p => Point.create(p));
    let graph = view.graph;
    let targetNode = graph.getCellById(view.targetView.cell.id);
    let parentData = targetNode.getParent().getData();
    let siblingGroupNode = null;
    if (parentData.parentId) {
      let parentNode = graph.getCellById(parentData.parentId);
      let childOfProcessGroupId = parentNode && parentNode.getData().childOfProcessGroupId;
      if (childOfProcessGroupId) {
        siblingGroupNode = graph.getCellById(childOfProcessGroupId);
      }
    }

    // 事项同级的过程结点
    if (siblingGroupNode) {
      // 阶段节点在左边
      if (args.startDirections && args.startDirections.indexOf('right') >= 0) {
        let sourceCenter = view.sourceBBox.getCenter();
        let targetLeftMiddle = view.targetBBox.getLeftMiddle();
        points.push({ x: sourceCenter.x, y: sourceCenter.y - 70 });
        points.push({ x: targetLeftMiddle.x - 35, y: sourceCenter.y - 70 });
        points.push({ x: targetLeftMiddle.x - 35, y: targetLeftMiddle.y });
      } else {
        // 阶段节点在上方
        let sourceCenter = view.sourceBBox.getCenter();
        let targetCenter = view.targetBBox.getCenter();
        points.push({ x: sourceCenter.x - 70, y: sourceCenter.y });
        points.push({ x: sourceCenter.x - 70, y: targetCenter.y });
      }
    } else {
      // 阶段节点在左边
      if (args.startDirections && args.startDirections.indexOf('right') >= 0) {
        let sourceRightMiddlePoint = view.sourceBBox.rightMiddle;
        let targetLeftMiddlePoint = view.targetBBox.leftMiddle;
        let xdiff = targetLeftMiddlePoint.x - sourceRightMiddlePoint.x;
        points.push({ x: sourceRightMiddlePoint.x + xdiff / 3, y: sourceRightMiddlePoint.y });
        points.push({ x: sourceRightMiddlePoint.x + xdiff / 3, y: targetLeftMiddlePoint.y });
      } else {
        // 阶段节点在上方
        let sourceBottomCenter = view.sourceBBox.getBottomCenter();
        let targetLeftMiddlePoint = view.targetBBox.leftMiddle;
        points.push({ x: sourceBottomCenter.x, y: sourceBottomCenter.y + 1 });
        points.push({ x: sourceBottomCenter.x, y: targetLeftMiddlePoint.y });
      }
    }
    return points;
  },
  true
);
Graph.registerRouter(
  'process-route',
  (vertices, args, view) => {
    let points = [];
    let graph = view.graph;

    const { sourceAnchor, targetAnchor, targetView } = view;
    let targetNode = targetView.cell;

    const directionBottomToTop = () => {
      let sourcePoint = sourceAnchor.clone();
      let targetPoint = targetAnchor.clone();
      let median = (targetPoint.y - sourcePoint.y) / 2;
      sourcePoint.y = sourcePoint.y + median;
      targetPoint.y = targetPoint.y - median;
      points = [sourcePoint, targetPoint];
    };
    const directionRightToLeft = () => {
      let sourcePoint = sourceAnchor.clone();
      let targetPoint = targetAnchor.clone();
      let xdiff = (targetPoint.x - sourcePoint.x) / 2;
      sourcePoint.x = sourcePoint.x + xdiff;
      targetPoint.x = targetPoint.x - xdiff;
      points = [sourcePoint, targetPoint];
    };
    // 阶段节点在左边
    if (args.startDirections && args.startDirections.indexOf('right') >= 0) {
      directionRightToLeft();
    } else {
      // 阶段节点在上方
      directionBottomToTop();
    }
    return points;
  },
  true
);
class ProcessDesigner {
  constructor(elId, editable = true) {
    this.elId = elId;
    this.layout = new NodeLayout({ designer: this });
    this.processTree = new ProcessTree();
    this.processDefinition = {};
    this.selectedNodeId = '';
    this.selectedNodeType = '';
    this.drawerVisibleKey = '';
    this.canRedo = false;
    this.canUndo = false;
    this.isEmpty = false;
    // 构建方式，process业务流程，itemFlow事项流
    this.buildWay = 'process';
    this.editable = editable;
    this.definitionTemplateMap = {};
    this.refInfoMap = {};
  }

  get graph() {
    if (this.x6Graph) {
      return this.x6Graph;
    }

    const x6Graph = new Graph({
      panning: true,
      history: {
        // 启用历史需安装@antv/x6-plugin-history
        enabled: true
      },
      selecting: {
        // 启用选择需安装@antv/x6-plugin-selection
        enabled: true
      },
      interacting: {
        nodeMovable: false,
        magnetConnectable: false,
        edgeMovable: false
      },
      scroller: {
        enabled: true
      },
      container: document.getElementById(this.elId),
      width: '100%',
      height: '100%',
      background: {
        color: '#fafafa'
      }
      // grid: {
      //   size: 10,
      //   visible: true
      // }
    });
    x6Graph.use(
      new History({
        enabled: true
      })
    );
    if (!window.__x6_instances__) {
      window.__x6_instances__ = {};
    }
    window.__x6_instances__.processFlow = x6Graph;
    this.x6Graph = x6Graph;
    globalOptions.graph = x6Graph;
    globalOptions.designer = this;
    return x6Graph;
  }

  setBuildWay(buildWay) {
    this.buildWay = buildWay;
  }

  getBuildWay() {
    return this.buildWay;
  }

  getNodeChildren(groupNode) {
    return this.layout.getNodeChildren(groupNode);
  }

  getItemNodeByItemId(itemId) {
    let itemNode = this.graph.getCellById(itemId);
    if (itemNode != null) {
      return itemNode;
    }

    let nodes = this.graph.getNodes();
    itemNode = nodes.find(node => {
      let data = node.getData();
      return data && data.type == 'item' && data.configuration && data.configuration.id == itemId;
    });
    return itemNode;
  }

  getStageNodeByNodeId(nodeId) {
    return this.graph.getCellById(nodeId);
  }

  removeNode(node) {
    const _this = this;
    if (node == null) {
      return;
    }

    _this.startBatch('removeNode');
    let groupNode = node.getParent();
    // 递归删除节点，包含子节点
    this.recursiveRemoveNode(node);
    // 上级节点是群组节点
    if (groupNode != null) {
      let children = groupNode.getChildren();
      if (!children || children.length == 0) {
        _this.removeNode(groupNode);
      } else {
        _this.layout.layoutGroup(groupNode);
        _this.layout.layoutSiblingGroup(groupNode);
      }
    } else {
      // 删除的是群组节点
      _this.layout.layoutSiblingGroup(node);
    }

    // 更新上级节点的配置提示
    let parentId = null;
    // 上级节点是群组节点
    if (groupNode != null) {
      parentId = groupNode.getData().parentId;
    } else {
      parentId = node.getData().parentId;
    }
    let parentNode = _this.graph.getCellById(parentId);
    if (parentNode) {
      invokeVueMethod(parentNode.vm, 'updateConfigTips');
    } else {
      // _this.clear();
    }

    _this.selectedNodeId = '';
    _this.setGraphIsEmpty();
    _this.stopBatch('removeNode');
  }

  recursiveRemoveNode(node) {
    const _this = this;
    if (node == null) {
      return;
    }
    let nodeData = node.getData();
    // 子阶段群组
    if (nodeData.childOfProcessGroupId) {
      let groupNode = _this.graph.getCellById(nodeData.childOfProcessGroupId);
      if (groupNode != null) {
        _this.layout.getNodeChildren(groupNode).forEach(child => {
          _this.removeNode(child);
        });
        // let children = _this.layout.getNodeChildren(groupNode);
        // if (!children || children.length == 0) {
        //   _this.removeNode(groupNode);
        // }
      }
    }
    // 子事项群组
    if (nodeData.childOfItemGroupId) {
      let groupNode = _this.graph.getCellById(nodeData.childOfItemGroupId);
      if (groupNode != null) {
        _this.layout.getNodeChildren(groupNode).forEach(child => {
          _this.removeNode(child);
        });
        // let children = _this.layout.getNodeChildren(groupNode);
        // if (!children || children.length == 0) {
        //   _this.removeNode(groupNode);
        // }
      }
    }

    // 删除关系数据的关系引用
    if ((nodeData.type == 'node-group' || nodeData.type == 'item-group') && nodeData.parentId != -1) {
      let parentNode = _this.graph.getCellById(nodeData.parentId);
      if (parentNode != null) {
        let parentData = parentNode.getData();
        if (parentData.childOfProcessGroupId == node.id) {
          delete parentData.childOfProcessGroupId;
        } else if (parentData.childOfItemGroupId == node.id) {
          delete parentData.childOfItemGroupId;
        }
      }
    }

    // 删除父子节点关系
    let parent = node.getParent();
    if (parent != null) {
      parent.removeChild(node);
      let children = parent.getChildren();
      if (!children || children.length == 0) {
        _this.removeNode(parent);
      }
    }

    // 删除节点
    _this.graph.removeNode(node);
  }

  startBatch(name) {
    if (!this.changing) {
      this.graph.startBatch(name || 'designer-change');
    }
    this.changing = true;
  }

  stopBatch(name) {
    if (this.changing) {
      this.graph.stopBatch(name || 'designer-change');
    }
    this.changing = false;
  }

  undo() {
    this.graph.undo();
    this.processTree.updateFromGraph(this.graph);
    let selectNode = this.getSelectedNode();
    if (selectNode) {
      this.selectedNodeType = selectNode.getData().type;
    } else {
      this.selectedNodeType = null;
    }

    this.setGraphIsEmpty();
  }

  redo() {
    this.graph.redo();
    this.processTree.updateFromGraph(this.graph);
    let selectNode = this.getSelectedNode();
    if (selectNode) {
      this.selectedNodeType = selectNode.getData().type;
    } else {
      this.selectedNodeType = null;
    }

    this.setGraphIsEmpty();
  }

  setGraphIsEmpty() {
    if (this.graph.getCellCount() == 0) {
      this.isEmpty = true;
    } else {
      this.isEmpty = false;
    }
  }

  clear() {
    this.layout = new NodeLayout({ designer: this });
    this.selectedNodeType = null;
    this.graph.clearCells();
    this.graph.cleanHistory();
    this.processTree.updateFromGraph(this.graph);
    this.setGraphIsEmpty();
  }

  updateTree() {
    this.processTree.updateFromGraph(this.graph);
  }

  switchLayout() {
    const _this = this;
    _this.toProcessDefinition();
    _this.processDefinition.layout = _this.processDefinition.layout == 'horizontal' ? 'vertical' : 'horizontal';
    delete _this.processDefinition.graphData;
    _this.layout = new NodeLayout({ designer: this });
    _this.graph.clearCells();
    _this.initFromProcessDefinition();
    let selectNode = _this.getSelectedNode();
    if (selectNode) {
      _this.layout.showNode(selectNode);
    } else {
      _this.layout.showRoot();
    }
    _this.graph.cleanHistory();
  }

  // 通过业务流程定义移动节点，目标节点为事项时，作为同级放入事项节点
  moveNodeWithProcessDefinition(fromId, toId, sibling, dropToBottom) {
    const _this = this;
    _this.toProcessDefinition();
    _this._moveNodeWithProcessDefinition(_this.processDefinition, fromId, toId, sibling, dropToBottom);
    delete _this.processDefinition.graphData;
    _this.layout = new NodeLayout({ designer: this });
    _this.graph.clearCells();
    _this.initFromProcessDefinition();
    _this.selectNode(fromId);
    setTimeout(() => _this.graph.cleanHistory(), 0);
  }

  _moveNodeWithProcessDefinition(processDefinition, fromId, toId, sibling, dropToBottom) {
    const _this = this;
    // 删除并返回源节点数据
    let deleteFromNode = nodes => {
      let deletedNode = null;
      if (nodes == null) {
        return deletedNode;
      }
      for (let index = 0; index < nodes.length; index++) {
        let node = nodes[index];
        if (node.id == fromId) {
          nodes.splice(index, 1);
          return node;
        }
        // 子阶段、事项
        deletedNode = deleteFromNode(node.nodes) || deleteFromNode(node.items);
        if (deletedNode) {
          return deletedNode;
        }
      }
      return deletedNode;
    };
    // 在目标节点插入源节点数据
    let appendFromNode = (fromNode, nodes, targetType) => {
      if (nodes == null) {
        return false;
      }
      for (let index = 0; index < nodes.length; index++) {
        let node = nodes[index];
        if (node.id == toId) {
          if (targetType == 'node') {
            node.nodes = node.nodes || [];
            node.items = node.items || [];
            let cell = _this.graph.getCellById(fromNode.id);
            let fromNodeType = cell && cell.getData().type == 'node';
            // 源节点是过程节点
            if (fromNodeType) {
              if (sibling) {
                nodes.splice(dropToBottom ? index + 1 : index, 0, fromNode);
              } else {
                node.nodes.push(fromNode);
              }
            } else {
              node.items.push(fromNode);
            }
          } else {
            nodes.splice(dropToBottom ? index + 1 : index, 0, fromNode);
          }
          return true;
        }
        // 子阶段、事项
        if (appendFromNode(fromNode, node.nodes, 'node')) {
          return true;
        }
        if (appendFromNode(fromNode, node.items, 'item')) {
          return true;
        }
      }
      return false;
    };
    let fromNode = deleteFromNode(processDefinition.nodes);
    if (fromNode) {
      appendFromNode(fromNode, [processDefinition], 'node');
    } else {
      console.error('from node not found', fromId);
    }
  }

  toJSON() {
    return this.graph.toJSON();
  }

  toProcessDefinition() {
    const _this = this;
    let graphData = _this.graph.toJSON();
    _this.processTree.updateFromGraphData(graphData);
    let treeData = _this.processTree.getTreeData();
    let traverseTree = function (treeNodes, definition) {
      treeNodes.forEach(child => {
        let node = _this.graph.getCellById(child.key);
        let nodeData = node.getData();
        definition.nodes = definition.nodes || [];
        definition.items = definition.items || [];
        let configuration = nodeData.configuration;
        if (nodeData.type == 'node' && configuration) {
          configuration.nodes = [];
          configuration.items = [];
          definition.nodes.push(configuration);

          // 子节点
          if (child.children && child.children.length > 0) {
            traverseTree(child.children, configuration);
          }
        } else if (configuration) {
          definition.items.push(configuration);
        }
      });
    };
    let definition = { nodes: [] };
    if (treeData.length > 0) {
      traverseTree(treeData[0].children, definition);
    }
    _this.processDefinition.nodes = definition.nodes;
    _this.processDefinition.layout = _this.layout.getRootLayout();
    _this.processDefinition.graphData = JSON.stringify(graphData);
    return _this.processDefinition;
  }

  getProcessDefinition() {
    return this.processDefinition || {};
  }

  getLatestProcessTree() {
    this.updateTree();
    return this.processTree;
  }

  init(processDefinition) {
    const _this = this;
    _this.processDefinition = processDefinition;

    _this.initFromProcessDefinition();

    _this.initDefinitionTemplate(_this.processDefinition.definitionTemplates);

    _this.processTree.updateFromGraph(_this.graph);

    _this.layout.showRoot();

    _this.initEvents();
  }

  // 初始化事件
  initEvents() {
    const _this = this;
    _this.graph.on('node:customevent', ({ name, node, view, e }) => {
      // 添加节点或事项
      if (name == 'node:right:addNodeOrItem') {
        invokeVueMethod(view.vm, 'onAddNodeOrItemHover', { e, node, view });
      } else if (name == 'node:bottom:addNodeOrItem') {
        invokeVueMethod(view.vm, 'onAddNodeOrItemHover', { e, node, view });
      }
      _this.selectNode(node.id);
      e.stopPropagation();
    });
    _this.graph.on('node:mouseenter', args => {
      const { cell, node, view, e: event } = args;
      // if (!cell.children || (cell.children && !cell.children.length)) {
      if (cell.shape === 'process-node') {
        const nodeEl = event.target.closest('.process-designer-node')
        if (nodeEl) {
          showNodePortByGroups(cell);
          // if (nodeEl.classList.contains('is-end')) {
          //   if (cell.data.layout === 'horizontal') {
          //     const rightLinePort = cell.getPortsByGroup('rightLine')[0];
          //     showNodePort(cell, rightLinePort, ['rightLine', 'rightLineContent']);
          //   } else {
          //     const bottomLinePort = cell.getPortsByGroup('bottomLine')[0];
          //     showNodePort(cell, bottomLinePort, ['bottomLine', 'bottomLineContent']);
          //   }
          // }
        }
      }
    })
    _this.graph.on('node:mouseleave', args => {
      const { cell, node, view, e: event } = args;
      if (cell.shape === 'process-node') {
        hideNodePorts(cell);
        if (cell.parent && cell.parent._children) {
          const findIndex = cell.parent._children.findIndex(f => f.id === cell.id)
          if (findIndex === cell.parent._children.length - 1) {
            if (cell.data.layout === 'horizontal') {
              const rightLinePort = cell.getPortsByGroup('rightLine')[0];
              hideNodePort(cell, rightLinePort, ['rightLine', 'rightLineContent']);
            } else {
              const bottomLinePort = cell.getPortsByGroup('bottomLine')[0];
              hideNodePort(cell, bottomLinePort, ['bottomLine', 'bottomLineContent']);
            }
          }
        }
      }
    })
    // 加号节点，鼠标移入移出样式调整
    _this.graph.on('node:port:mouseenter', args => {
      const { cell, node, view, e: event, port: portId } = args;
      if (cell.data.layout === 'horizontal') {
        if (event.target.closest('[port-group="rightLine"]')) {
          // if (event.target.getAttribute('port-group') === 'rightLine') {
          const rightPort = cell.getPortsByGroup('right')[0]
          showNodePort(cell, rightPort);
          // const bottomPort = cell.getPortsByGroup('bottom')[0]
          // hideNodePort(cell, bottomPort);
        }
      } else {
        if (event.target.closest('[port-group="bottomLine"]')) {
          const bottomPort = cell.getPortsByGroup('bottom')[0]
          showNodePort(cell, bottomPort);
        }
      }
      _this.setAddNodeOrItemBtnStyle('var(--w-primary-color)', event, cell, view);
    });
    _this.graph.on('node:port:mouseleave', ({ e: event, cell, view }) => {
      _this.setAddNodeOrItemBtnStyle('var(--w-primary-color-4)', event, cell, view);
    });

    // 节点点击选择
    _this.graph.on('node:click', ({ e, x, y, node, view }) => {
      _this.selectNode(node.id);
      this.layout.layoutGroup(node.getParent());
      if (node.shape === 'process-node' || node.shape === 'process-item') {
        this.setIncomingsHighlight(node)
      }
      console.log('selectNode', node);
    });

    // 节点添加、删除，更新业务流程树
    _this.graph.on(
      'node:added',
      debounce(({ node, index, options }) => {
        console.log('node:added', node);
        _this.processTree.updateFromGraph(_this.graph);
      }, 250)
    );
    _this.graph.on(
      'node:removed',
      debounce(({ node, index, options }) => {
        console.log('node:removed', node);
        _this.processTree.updateFromGraph(_this.graph);
      }, 250)
    );

    // 鼠标滚轮滚动时移动画布
    _this.graph.on('blank:mousewheel', options => {
      console.log('blank:mousewheel', options);
      let translate = _this.graph.translate();
      translate.ty = translate.ty + options.delta * 60;
      _this.graph.translate(translate.tx, translate.ty);
    });

    // 历史记录
    _this.graph.cleanHistory();
    _this.graph.on('history:change', () => {
      this.canRedo = _this.graph.canRedo();
      this.canUndo = _this.graph.canUndo();
    });

    // _this.graph.on("node:selected", (cell, node, options) => {
    //   console.log("node:selected", cell, node, options);
    // });
  }

  // 设置输入边、边起始节点高亮
  setIncomingsHighlight(node) {
    let highlightNodeIds = [], highlightEdgeIds = []
    const getHighlightId = (node) => {
      highlightNodeIds.push(node.id)
      let incomings
      if (node.shape === 'process-item') {
        incomings = this.graph.getIncomingEdges(node.parent)
      } else {
        incomings = this.graph.getIncomingEdges(node)
      }
      if (incomings) {
        const edge = incomings[0]
        highlightEdgeIds.push(edge.id)
        // edge.setAttrs({
        //   line: { stroke: "var(--w-primary-color)" }
        // })
        getHighlightId(edge.getSourceCell())
      }
    }
    getHighlightId(node)
    if (highlightEdgeIds.length) {
      this.graph.getEdges().map(item => {
        item.setZIndex(0)
      })
    }
    if (highlightNodeIds.length || highlightEdgeIds.length) {
      let allCellEl = this.graph.view.viewport.querySelectorAll('.x6-cell')
      for (let index = 0; index < allCellEl.length; index++) {
        const item = allCellEl[index];
        const cellId = item.getAttribute('data-cell-id')
        if (highlightNodeIds.includes(cellId)) {
          item.classList.add('highlight')
        } else if (highlightEdgeIds.includes(cellId)) {
          item.classList.add('highlight')
          this.graph.getCellById(cellId).setZIndex(1)
        } else {
          item.classList.remove('highlight')
        }
      }
    }
  }
  // 隐藏接接端口操作
  hideCellPorts(ports) {
    for (let key in (ports && ports.groups) || {}) {
      let group = ports.groups[key];
      if (group.attrs && group.attrs.circle) {
        group.attrs.circle.opacity = 0;
      }
      if (group.markup && group.markup[0] && group.markup[0].attrs) {
        group.markup[0].attrs.r = 1;
      }
    }
  }

  setAddNodeOrItemBtnStyle(color, e, cell, view) {
    const group = e.target.parentElement.getAttribute('port-group')
    if (group === 'right' || group === 'bottom') {
      let portId = e.target.parentElement.getAttribute('port');
      if (portId && color) {
        cell.portProp(portId, 'attrs/circle/style', { stroke: color });
        cell.portProp(portId, 'attrs/line1/style', { stroke: color });
        cell.portProp(portId, 'attrs/line2/style', { stroke: color });
      }
    }
  }

  // 初始化业务流程定义的模板
  initDefinitionTemplate(definitionTemplates = []) {
    const _this = this;
    definitionTemplates.forEach(template => {
      if (template.type == '50' && template.itemId) {
        _this.definitionTemplateMap[template.itemId] = template;
      } else if (template.type == '60' && template.nodeId) {
        _this.definitionTemplateMap[template.nodeId] = template;
      }
    });
  }

  // 从业务流程定义初始化
  initFromProcessDefinition() {
    const _this = this;
    if (_this.processDefinition.graphData && !_this.isRefNodeOrItemDefinition()) {
      let graphData = JSON.parse(_this.processDefinition.graphData);
      if (!_this.editable && graphData.cells && Array.isArray(graphData.cells)) {
        graphData.cells.forEach(cell => {
          cell.ports && _this.hideCellPorts(cell.ports);
        });
      }
      if (graphData.cells) {
        const createRightLinePort = (cell) => {
          let group = createRightLineGroup()
          group.position.args.cell = cell.id
          let port = {
            group,
            item: {
              group: "rightLine",
              id: `${cell.id}-right-line`,
            }
          }
          return port
        }
        const createBottomLinePort = (cell) => {
          let group = createBottomLineGroup()
          group.position.args.cell = cell.id
          let port = {
            group,
            item: {
              group: "bottomLine",
              id: `${cell.id}-bottom-line`,
            }
          }
          return port
        }
        // 查找在父节点的索引
        const findChildrenIndex = (node) => {
          let findIndex = 0
          for (let index = 0; index < graphData.cells.length; index++) {
            const cell = graphData.cells[index];
            if (cell.id === node.parent) {
              cell.children.map((item, i) => {
                if (item === node.id) {
                  findIndex = i
                }
              })
              break
            }
          }
          return findIndex
        }

        graphData.cells.forEach(cell => {
          if (cell.shape === 'edge' && cell.router) {
            if (cell.router.name === 'manhattan') {
              cell.router.name = 'process-route';
            }
            if (cell.source.anchor === undefined) {
              cell.zIndex = 0
              cell.connector.name = 'normal'
              cell.attrs.line.targetMarker = null
              cell.attrs.line.stroke = 'var(--w-primary-color-3)'
              cell.source.anchor = 'nodeCenter'
              cell.target.anchor = 'nodeCenter'
              delete cell.source.port
              delete cell.target.port
            }

          } else if (cell.shape === 'rect' && cell.children && cell.children.length) {
            if (cell.data.type === "node-group") {
              cell.attrs.body.fill = 'transparent'
              cell.attrs.body.strokeWidth = 0
            }
          } else if (cell.shape === 'process-node' && cell.parent) {
            if (cell.data.layout === 'horizontal') {
              if (cell.ports.groups.rightLine === undefined) {
                const { group, item } = createRightLinePort(cell)
                cell.ports.groups.rightLine = group
                const findIndex = cell.ports.items.findIndex(f => f.group === 'right')
                cell.ports.items.splice(findIndex, 0, item)
                cell.position.x = cell.position.x + 18 * findChildrenIndex(cell)
              }
            } else {
              if (cell.ports.groups.bottomLine === undefined) {
                const { group, item } = createBottomLinePort(cell)
                cell.ports.groups.bottomLine = group
                const findIndex = cell.ports.items.findIndex(f => f.group === 'bottom')
                cell.ports.items.splice(findIndex, 0, item)
                cell.position.y = cell.position.y + 18 * findChildrenIndex(cell)
              }
            }
          }
        });
      }
      _this.graph.fromJSON(graphData);
      return;
    }

    if (_this.processDefinition.nodes && _this.processDefinition.nodes.length > 0) {
      let rootGroup = _this.initFromNodeTemplate({
        id: _this.processDefinition.id,
        name: _this.processDefinition.name,
        layout: _this.processDefinition.layout || 'horizontal', // "horizontal"、"vertical"
        nodes: _this.processDefinition.nodes
      });
      _this.layout.getNodeChildren(rootGroup).forEach(childNode => {
        // 添加阶段下子节点或事项节点
        _this.addChildrenOfNodeIfRequired(childNode);
      });
    } else {
      _this.graph.fromJSON({ cells: [] });
    }
    _this.setGraphIsEmpty();
  }

  // 合并引用的阶段或事项节点
  isRefNodeOrItemDefinition() {
    const _this = this;
    let ref = false;
    let nodes = _this.processDefinition.nodes || [];
    let checkRef = nodes => {
      if (ref) {
        return;
      }
      nodes.forEach(node => {
        if (node.refProcessDefUuid) {
          ref = true;
          return;
        }
        if (node.nodes) {
          checkRef(node.nodes);
        }
        if (node.items) {
          checkRef(node.items);
        }
      });
    };
    checkRef(nodes);
    return ref;
  }

  // 判断节点是否为模板
  isTemplateNode(nodeId, type) {
    return !!this.definitionTemplateMap[nodeId];
  }

  // 删除节点模板
  deleteNodeTemplate(nodeId) {
    delete this.definitionTemplateMap[nodeId];
    this.updateTree();
  }

  // 判断节点是否为引用
  isRefNode(nodeId, type) {
    let refInfo = this.getNodeRefInfoById(nodeId);
    return refInfo.ref;
  }

  // 取消引用
  cancelRefByNodeId(nodeId) {
    let node = this.graph.getCellById(nodeId);
    if (!node) {
      return false;
    }
    let nodeData = node.getData();
    delete nodeData.configuration.refProcessDefUuid;
    this.getNodeRefInfoById(nodeId);
    this.updateTree();
    return true;
  }

  // 获取节点引用信息
  getNodeRefInfoById(nodeId) {
    return this.getNodeRefInfo(this.graph.getCellById(nodeId));
  }

  // 获取节点引用信息
  getNodeRefInfo(node) {
    const _this = this;
    let refInfo = _this.refInfoMap[node && node.id] || {
      ref: false,
      directRef: false
    };
    if (node == null) {
      return refInfo;
    }
    _this.refInfoMap[node && node.id] = refInfo;

    let nodeData = node.getData();
    if ((nodeData.type == 'node' || nodeData.type == 'item') && nodeData.configuration) {
      // 直接引用
      if (nodeData.configuration.refProcessDefUuid) {
        refInfo.ref = true;
        refInfo.directRef = true;
        refInfo.type == nodeData.type;
      } else {
        // 间接引用
        refInfo.ref = _this.isIndirectRef(node);
        refInfo.directRef = false;
        refInfo.type == nodeData.type;
      }
    }
    return refInfo;
  }

  isIndirectRef(node) {
    const _this = this;
    let indirectRef = false;
    let parentIds = _this.processTree.getParentNodeKeysByKey(node.id);
    parentIds.forEach(nodeId => {
      let node = _this.graph.getCellById(nodeId);
      if (node != null) {
        let configuration = node.getData().configuration;
        if (configuration && configuration.refProcessDefUuid) {
          indirectRef = true;
        }
      }
    });
    return indirectRef;
  }

  initFromNodeTemplate(nodeTemplate) {
    const _this = this;
    console.log('initFromNodeTemplate', nodeTemplate);
    _this.startBatch('initFromNodeTemplate');
    let layout = nodeTemplate.layout;
    let nodeDefinitions = nodeTemplate.nodes;
    let startPosition = _this.layout.startPosition;
    let groupNode = _this.addGroupNode(
      nodeTemplate.id,
      _this.processDefinition.name || nodeTemplate.name,
      startPosition,
      layout,
      'node-group'
    );

    let nodeSize = _this.layout.getNodeSize(layout);
    for (let index = 0; index < nodeDefinitions.length; index++) {
      let nodeDefinition = nodeDefinitions[index];
      _this.addProcessNode(groupNode, layout, nodeSize, nodeDefinition);
    }
    // 连接子节点
    // _this.connectChildrenOfNode(groupNode);
    // 节点布局
    _this.layout.layout(groupNode);
    _this.setGraphIsEmpty();
    _this.stopBatch('initFromNodeTemplate');
    return groupNode;
  }

  // 更新根节点名称
  updateRootNodeTitle(title) {
    let rootNode = this.layout.getRootNodeFromDesigner();
    if (rootNode) {
      rootNode.label = title;
      if (rootNode.data) {
        rootNode.data.name = title;
      }
      this.processTree.updateRootNodeTitle(title);
    }
  }

  // 获取或添加节点群组
  getOrAddGroupNode(id, name = '', position, layout, type = 'node-group', parentNode) {
    const _this = this;
    let data = parentNode.getData();
    let groupId = type == 'node-group' ? data.childOfProcessGroupId : data.childOfItemGroupId;
    let groupNode = null;
    if (groupId) {
      groupNode = _this.graph.getCellById(groupId);
    }
    if (groupNode == null) {
      groupNode = _this.addGroupNode(id, name, position, layout, type, parentNode.id);
      if (type == 'node-group') {
        data.childOfProcessGroupId = groupNode.id;
      } else {
        data.childOfItemGroupId = groupNode.id;
      }
    }
    return groupNode;
  }

  // 添加群组
  addGroupNode(id, name = '', position, layout, type, parentId = '-1') {
    const _this = this;
    let options = {
      x: position.x, // 40,
      y: position.y, // 40,
      id,
      label: name,
      // width: 240,
      // height: 180,
      data: { layout, type, parentId, name },
      attrs: {
        body: {
          stroke: 'var(--w-gray-color-8)', // 边框颜色
          strokeWidth: 1, // 边框宽度
          strokeDasharray: [6, 6], //虚线宽度
          rx: 4 //圆角
        },
        text: {
          opacity: 0
        }
      }
    };
    if (type == 'item-group') {
      let ports = deepClone(itemNodePorts);
      // 端口记录所在节点id
      for (let key in ports.groups) {
        if (key == 'top' && layout == 'vertical') {
          ports.groups[key].attrs.circle1.r = 0;
        }
        if (key == 'left' && layout == 'horizontal') {
          ports.groups[key].attrs.circle1.r = 0;
        }
      }
      options.ports = { ...ports };
    } else {
      options.attrs.body.fill = 'transparent'
      options.attrs.body.strokeWidth = 0
    }
    // 添加父节点
    let groupNode = _this.graph.addNode(options);
    return groupNode;
  }

  // 添加过程节点
  addProcessNode(parentNode, layout, nodeSize, nodeDefinition) {
    const _this = this;
    let child = _this.getProcessNodeJson(layout, nodeSize, nodeDefinition, parentNode);
    let childNode = _this.graph.addNode(child);
    parentNode.addChild(childNode);
    return childNode;
  }

  getProcessNodeJson(layout, nodeSize, nodeDefinition, parentNode) {
    let ports = deepClone(processNodePorts);
    if (!this.editable) {
      this.hideCellPorts(ports);
    }
    // 端口记录所在节点id
    let delIndex
    for (let key in ports.groups) {
      ports.groups[key].position.args.cell = nodeDefinition.id;
      // 删除items的项，就不会再渲染
      if (key == 'left' && (layout == 'horizontal' || !parentNode || parentNode.data.parentId == '-1')) {
        ports.groups[key].attrs.circle1.r = 0;
      }
      if (key == 'top' && (layout == 'vertical' || !parentNode || parentNode.data.parentId == '-1')) {
        ports.groups[key].attrs.circle1.r = 0;
      }
    }
    if (layout == 'horizontal') {
      delete ports.groups.bottomLine
    } else {
      delete ports.groups.rightLine
    }
    ports.items.map((item, index) => {
      if (layout == 'horizontal') {
        // 水平
        if (item.group === 'bottomLine') {
          delIndex = index
        }
      } else {
        if (item.group === 'rightLine') {
          delIndex = index
        }
      }
    })
    ports.items.splice(delIndex, 1)
    return {
      shape: 'process-node',
      id: nodeDefinition.id,
      // x: 80 + index * 220,
      // y: 80,
      data: { layout: layout, configuration: nodeDefinition, type: 'node' },
      width: nodeSize.width, // 160,
      height: nodeSize.height, // 80,
      label: nodeDefinition.name,
      ports,
      highlighting: {
        //  连线过程中，自动吸附到连接桩时被使用。
        magnetAdsorbed: {
          name: 'circle',
          args: {
            padding: 4,
            attrs: {
              'stroke-width': 2,
              stroke: 'red'
            }
          }
        }
      }
    };
  }

  // 添加业务事项
  addProcessItem(parentNode, layout, itemSize, itemDefinition) {
    const _this = this;
    let child = _this.getProcessItemJson(layout, itemSize, itemDefinition);
    let childNode = _this.graph.addNode(child);
    parentNode.addChild(childNode);
    return childNode;
  }

  getProcessItemJson(layout, itemSize, itemDefinition) {
    return {
      shape: 'process-item',
      id: itemDefinition.id,
      label: itemDefinition.name || itemDefinition.itemName,
      data: { configuration: itemDefinition, type: 'item' },
      width: itemSize.width,
      height: itemSize.height
      // ports: { ...itemNodePorts }
    };
  }

  // 从结点模板添加子节点
  addChildFromNodeTemplate(parentNode, nodeTemplate, addChildrenOfProcessNode = true) {
    console.log('addChildFromNodeTemplate', nodeTemplate);
    const _this = this;
    _this.startBatch('addChildFromNodeTemplate');
    let layout = nodeTemplate.layout;
    let position = parentNode.getPosition();
    let groupType = nodeTemplate.nodes ? 'node-group' : 'item-group';
    let groupNode = _this.getOrAddGroupNode(nodeTemplate.id, nodeTemplate.name, position, layout, groupType, parentNode);

    // 添加阶段节点
    if (nodeTemplate.nodes) {
      let nodeDefinitions = nodeTemplate.nodes;
      let nodeSize = _this.layout.getNodeSize(layout);
      for (let index = 0; index < nodeDefinitions.length; index++) {
        let nodeDefinition = nodeDefinitions[index];
        let childNode = _this.addProcessNode(groupNode, layout, nodeSize, nodeDefinition);
        // 添加阶段下子节点或事项节点
        if (addChildrenOfProcessNode) {
          _this.addChildrenOfNodeIfRequired(childNode);
        }
      }
      // 连接子节点
      // _this.connectChildrenOfNode(groupNode);
      // 连接分组节点
      _this.connectGroupNode(parentNode, groupNode);
      // 节点布局
      _this.layout.layoutProcessGroup(parentNode, groupNode);
      // 刷新兄弟事项群组
      let childOfItemGroupId = parentNode.getData().childOfItemGroupId;
      if (childOfItemGroupId) {
        _this.layout.layoutItemGroup(parentNode, _this.graph.getCellById(childOfItemGroupId));
      }
    } else {
      // 添加事项节点
      let itemDefinitions = nodeTemplate.items;
      let itemSize = _this.layout.getItemSize();
      for (let index = 0; index < itemDefinitions.length; index++) {
        let itemDefinition = itemDefinitions[index];
        _this.addProcessItem(groupNode, layout, itemSize, itemDefinition);
      }
      // 连接分组节点
      _this.connectGroupItem(parentNode, groupNode);
      // 事项布局
      _this.layout.layoutItemGroup(parentNode, groupNode);
    }
    _this.stopBatch('addChildFromNodeTemplate');
    return groupNode;
  }

  addSiblingFromNodeTemplate(node, nodeTemplate) {
    console.log('addSiblingFromNodeTemplate', nodeTemplate);
    const _this = this;
    _this.startBatch('addSiblingFromNodeTemplate');
    let groupNode = node.getParent();
    let nodeIndex = groupNode.getChildIndex(node);
    let layout = nodeTemplate.layout;
    let groupParentNode = null;
    // 添加阶段节点
    if (nodeTemplate.nodes) {
      let parentId = groupNode.getData().parentId;
      groupParentNode = _this.graph.getCellById(parentId);
      let nodeSize = _this.layout.getNodeSize(layout);
      let nodeDefinitions = nodeTemplate.nodes;
      for (let index = 0; index < nodeDefinitions.length; index++) {
        let nodeDefinition = nodeDefinitions[index];
        let childJson = _this.getProcessNodeJson(layout, nodeSize, nodeDefinition, groupParentNode);
        let childNode = _this.graph.addNode(childJson);
        groupNode.insertChild(childNode, nodeIndex + 1 + index);
        // 添加阶段下子节点或事项节点
        _this.addChildrenOfNodeIfRequired(childNode);
      }
      // 节点布局
      // _this.layout.resizeProcessGroup(groupNode);
      _this.layout.layoutProcessGroup(groupParentNode, groupNode);
      // 连接子结点
      // _this.connectChildrenOfNode(groupNode);
      // 只删除结点相关的边，再连接其他结点
      // _this.removeNodeEdgeAndConnectOthers(groupNode, node);
      // 刷新兄弟事项节点
      if (groupParentNode) {
        _this.connectGroupNode(groupParentNode, groupNode);
        let childOfItemGroupId = groupParentNode.getData().childOfItemGroupId;
        if (childOfItemGroupId) {
          _this.layout.layoutItemGroup(groupParentNode, _this.graph.getCellById(childOfItemGroupId));
        }
      }
    } else {
      // 添加事项节点
      let itemSize = _this.layout.getItemSize();
      let itemDefinitions = nodeTemplate.items;
      for (let index = 0; index < itemDefinitions.length; index++) {
        let itemDefinition = itemDefinitions[index];
        let childJson = _this.getProcessItemJson(layout, itemSize, itemDefinition);
        let childNode = _this.graph.addNode(childJson);
        groupNode.insertChild(childNode, nodeIndex + 1 + index);
      }
      // 连接分组节点
      let parentId = groupNode.getData().parentId;
      groupParentNode = _this.graph.getCellById(parentId);
      _this.connectGroupItem(groupParentNode, groupNode);
      // 节点布局
      _this.layout.layoutItemGroup(groupParentNode, groupNode);
    }

    // 更新上级节点的配置提示
    if (groupParentNode) {
      invokeVueMethod(groupParentNode.vm, 'updateConfigTips');
    }
    _this.stopBatch('addSiblingFromNodeTemplate');
    return groupNode;
  }

  addChildrenOfNodeIfRequired(node) {
    const _this = this;
    let nodeData = node.getData();
    let nodeDefinition = nodeData.configuration; // nodeData.nodeDefinition || nodeData.itemDefinition;
    let groupNode = null;
    if (!isEmpty(nodeDefinition.nodes)) {
      groupNode = _this.addChildNodesOfNode(node, nodeDefinition.nodes);
    }
    // 递归添加子节点
    if (groupNode) {
      _this.layout.getNodeChildren(groupNode).forEach(childNode => {
        // 添加阶段下子节点或事项节点
        _this.addChildrenOfNodeIfRequired(childNode);
      });
    }

    if (!isEmpty(nodeDefinition.items)) {
      groupNode = _this.addChildItemsOfNode(node, nodeDefinition.items);
    }
  }

  addChildNodesOfNode(node, nodeDefinitions) {
    const _this = this;
    let layout = node.getData().layout;
    let childrenLayout = layout == 'horizontal' ? 'vertical' : 'horizontal'; // 子节点布局与父节点相反，现不做该调整
    return _this.addChildFromNodeTemplate(node, { layout: layout, nodes: nodeDefinitions }, false);
  }

  addChildItemsOfNode(node, itemDefinitions) {
    const _this = this;
    let layout = node.getData().layout;
    let childrenLayout = layout == 'horizontal' ? 'vertical' : 'horizontal'; // 子节点布局与父节点相反，现不做该调整
    return _this.addChildFromNodeTemplate(node, { layout: layout, items: itemDefinitions });
  }

  // 连接子结点
  connectChildrenOfNode(parent) {
    // const _this = this;
    // let children = _this.layout.getNodeChildren(parent);
    // let previous = null;
    // let layout = parent.getData().layout;
    // children.forEach(child => {
    //   if (previous != null) {
    //     if (!_this.isEdgeExists(previous, child)) {
    //       let edgeJson = _this.getEdgeJson(layout, previous, child);
    //       _this.graph.addEdge(edgeJson);
    //     }
    //   }
    //   previous = child;
    // });
  }

  getEdgeJson(layout, sourceNode, targetNode) {
    let sourceGroup = 'right';
    let targetGroup = 'left';
    if (layout == 'vertical') {
      sourceGroup = 'bottom';
      targetGroup = 'top';
    }
    let edgeJson = {
      source: { cell: sourceNode, port: sourceNode.getPortsByGroup(sourceGroup)[0].id },
      target: { cell: targetNode, port: targetNode.getPortsByGroup(targetGroup)[0].id },
      visible: false, // 隐藏连接线
      attrs: {
        line: {
          stroke: 'var(--w-primary-color)',
          strokeWidth: 2,
          opacity: 0
        }
      }
    };
    return edgeJson;
  }

  // 只删除结点相关的边，再连接其他结点
  removeNodeEdgeAndConnectOthers(groupNode, node) {
    // const _this = this;
    // let nodes = [];
    // let edges = [];
    // let nodeIdMap = new Map();
    // let edgeSet = new Set();
    // let children = groupNode.getChildren();
    // children.forEach(child => {
    //   if (child.shape == 'edge') {
    //     edges.push(child);
    //     edgeSet.add(child.source.cell + "_" + child.target.cell);
    //   } else {
    //     nodes.push(child);
    //     nodeIdMap.set(child.id, child);
    //   }
    // });
    // // 删除节点相关边
    // edges.forEach(edge => {
    //   if (edge.source.cell == node.id && nodeIdMap.has(edge.target.cell)) {
    //     edgeSet.delete(edge.source.cell + "_" + edge.target.cell)
    //     this.graph.removeEdge(edge);
    //   }
    // });
    // // 连接剩余的边
    // let previous = null;
    // let layout = groupNode.getData().layout;
    // nodes.forEach(child => {
    //   if (previous != null) {
    //     let forward = child;
    //     // 节点边不存在时添加
    //     if (!edgeSet.has(previous.id + "_" + forward.id)) {
    //       let edgeJson = _this.getEdgeJson(layout, previous, forward);
    //       _this.graph.addEdge(edgeJson);
    //     }
    //   }
    //   previous = child;
    // });
  }

  // 连接分组节点
  connectGroupNode(parentNode, groupNode) {
    const _this = this;
    let sourceGroup = 'bottom';
    let targetGroup = 'top';
    let routerName = 'process-route';
    if (parentNode.getData().layout == 'vertical') {
      sourceGroup = 'right';
      targetGroup = 'left';
    }
    let children = _this.layout.getNodeChildren(groupNode);
    children.forEach(child => {
      // 已经存在边，忽略掉
      if (_this.isEdgeExists(parentNode, child)) {
        return;
      }
      _this.graph.addEdge({
        // source: { cell: parentNode, port: parentNode.getPortsByGroup(sourceGroup)[0].id },
        // target: { cell: child, port: child.getPortsByGroup(targetGroup)[0].id },
        source: { cell: parentNode, anchor: 'nodeCenter' },
        target: { cell: child, anchor: 'nodeCenter' },
        zIndex: 0,
        visible: true, // 隐藏连接线
        router: {
          name: routerName,
          args: {
            startDirections: [sourceGroup],
            endDirections: [targetGroup],
            padding: 20 // 分叉路的间距
          }
        },
        connector: {
          name: 'normal',
          args: {
            radius: 8
          }
        },
        attrs: {
          line: {
            stroke: 'var(--w-primary-color-3)',
            strokeWidth: 2,
            targetMarker: null
            // connection: true, // 使用分叉路
          }
        }
      });
    });
  }

  isEdgeExists(source, target) {
    const _this = this;
    let incomingEdges = _this.graph.getIncomingEdges(target);
    if (incomingEdges == null) {
      return false;
    }
    for (let index = 0; index < incomingEdges.length; index++) {
      let incomingEdge = incomingEdges[index];
      if (incomingEdge.source.cell == source.id) {
        return true;
      }
    }
    return false;
  }

  // 连接事项分组节点
  connectGroupItem(parentNode, groupNode) {
    const _this = this;

    let sourceGroup = 'bottom';
    let targetGroup = 'top';
    if (parentNode.getData().layout == 'vertical') {
      sourceGroup = 'right';
      targetGroup = 'left';
    }
    // 已经存在边，忽略掉
    if (_this.isEdgeExists(parentNode, groupNode)) {
      return;
    }
    _this.graph.addEdge({
      // source: { cell: parentNode, port: parentNode.getPortsByGroup(sourceGroup)[0].id },
      // target: { cell: groupNode, port: groupNode.getPortsByGroup(targetGroup)[0].id },
      source: { cell: parentNode, anchor: 'nodeCenter' },
      target: { cell: groupNode, anchor: 'nodeCenter' },
      zIndex: 0,
      visible: true, // 隐藏连接线
      router: {
        name: 'process-route',
        args: {
          startDirections: [sourceGroup],
          endDirections: [targetGroup],
          padding: 20 // 分叉路的间距
        }
      },
      connector: {
        name: 'normal',
        args: {
          radius: 8
        }
      },
      attrs: {
        line: {
          stroke: 'var(--w-primary-color)',
          strokeWidth: 2,
          targetMarker: null
          // connection: true, // 使用分叉路
        }
      }
    });
  }

  selectNode(id) {
    const _this = this;
    _this.startBatch('selectNode');
    let selectedNode = null;
    // 取消选择
    if (_this.selectedNodeId) {
      _this.unselectNode(_this.selectedNodeId);
    }

    // 选择
    if (id) {
      selectedNode = _this.graph.getCellById(id);
      if (selectedNode) {
        invokeVueMethod(selectedNode.vm, 'select');
        _this.selectedNodeType = selectedNode.getData().type;
      }
    }

    // 展开一级节点
    if (selectedNode) {
      _this.layout.showNode(selectedNode);
    }
    if (_this.selectedNodeId == id) {
      // 触发对selectedNodeId数据监控的变化
      _this.selectedNodeId = '';
      setTimeout(() => {
        _this.selectedNodeId = id;
        _this.stopBatch('selectNode');
      }, 0);
    } else {
      _this.selectedNodeId = id;
      _this.stopBatch('selectNode');
    }
  }

  isSelected(node) {
    return _this.selectedNodeId == node.id;
  }

  unselectNode(id) {
    const _this = this;
    let selectedNode = _this.graph.getCellById(id);
    if (selectedNode) {
      invokeVueMethod(selectedNode.vm, 'unselect');
      _this.selectedNodeType = '';
    }
  }

  getSelectedNode() {
    if (this.selectedNodeId) {
      return this.graph.getCellById(this.selectedNodeId);
    }
    return null;
  }
}

export default ProcessDesigner;
