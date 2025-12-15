import { Graph, Shape } from '@antv/x6';
import { History } from '@antv/x6-plugin-history';
import { Snapline } from '@antv/x6-plugin-snapline';
import ItemFlowLayout from './item-flow-layout.js';
import { itemFlowPorts, deleteToolMarkup } from './item-flow-ports.js';
import { deepClone, invokeVueMethod } from './utils.js';
const edgeDefaultStroke = '#999999'; // 边默认颜色
let edgeAttrs = {
  attrs: {
    line: {
      stroke: edgeDefaultStroke,
      strokeWidth: 2,
      targetMarker: {
        // 箭头
        tagName: 'path',
        stroke: edgeDefaultStroke,
        strokeWidth: 2,
        fill: 'none',
        d: 'M 10 -10 0 0 10 10  0 0 Z',
        radius: 2
      }
    }
  },
  router: {
    name: 'manhattan',
    args: {}
  },
  connector: {
    name: 'rounded',
    args: {
      radius: 8
    }
  }
};
let globalOptions = {};
class ItemFlowDesigner {
  constructor(elId, processDesigner, editable = true) {
    const _this = this;
    _this.elId = elId;
    _this.processDesigner = processDesigner;
    _this.itemFlows = {};
    _this.dragInfo = { startX: 0, startY: 0, x: 0, y: 0 };
    _this.currentItemFlow = null;
    _this.currentItemDefinition = null;
    _this.layout = new ItemFlowLayout();
    // 选择的节点
    _this.selectedNodeId = '';
    _this.selectedNode = null;
    // 恢复撤销
    _this.canRedo = false;
    _this.canUndo = false;
    _this.editable = editable;
  }

  get graph() {
    const _this = this;
    if (this.x6Graph) {
      return this.x6Graph;
    }

    const x6Graph = new Graph({
      panning: true,
      snapline: true,
      history: {
        // 启用历史需安装@antv/x6-plugin-history
        enabled: true
      },
      selecting: {
        // 启用选择需安装@antv/x6-plugin-selection
        enabled: true
      },
      // interacting: {
      //   nodeMovable: false,
      //   magnetConnectable: false,
      //   edgeMovable: false
      // },
      connecting: {
        allowBlank: false,
        allowMulti: false,
        allowLoop: false,
        createEdge() {
          return new Shape.Edge({ ...edgeAttrs });
        },
        validateConnection({ targetMagnet, sourceCell, targetCell }) {
          // console.log("targetMagnet", targetMagnet);
          // console.log("targetCell", targetCell);
          if (!sourceCell) {
            return false;
          }
          if (!targetCell) {
            return false;
          }

          // 组合节点内的节点不可连接到外部节点，外部节点不可连接到组合节点内部
          if (sourceCell.getParent() != targetCell.getParent()) {
            return false;
          }

          let sourceData = sourceCell.getData();
          let targetData = targetCell.getData();
          if (!sourceData || !targetData) {
            return false;
          }

          // 开始节点不可直接连到结束节点
          if (sourceData.type == 'start' && targetData.type == 'end') {
            return false;
          }

          // 不可以连接到开始节点
          if (targetData.type == 'start') {
            return false;
          }

          // 连接到节点
          if (targetData.type == 'gateway' || targetData.type == 'item' || targetData.type == 'end') {
            _this.showNodePorts(targetCell);
            return true;
          }

          // 目标元素可以被链接
          return !!targetMagnet;
        }
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
        enabled: true,
        beforeAddCommand(event, args) {
          // 工具类对象不加入历史记录
          if (args && args.key == 'tools') {
            return false;
          }
          // 设置对象数据不加入历史
          if (args && args.key == 'data') {
            return false;
          }
          // 变更对象属性不加入历史
          if (args && args.key == 'attrs') {
            return false;
          }
          // console.log("beforeAddCommand", event, args);
          return true;
        }
      })
    );
    x6Graph.use(
      new Snapline({
        enabled: true
      })
    );
    this.x6Graph = x6Graph;
    globalOptions.graph = x6Graph;
    globalOptions.designer = this;
    return x6Graph;
  }

  get processDefinition() {
    return this.processDesigner.processDefinition;
  }

  init(processDefinition) {
    const _this = this;
    let itemFlows = processDefinition.itemFlows;
    if (itemFlows == null) {
      itemFlows = {};
    } else if (typeof itemFlows == 'string') {
      itemFlows = JSON.parse(itemFlows);
    }
    processDefinition.itemFlows = itemFlows;
    _this.itemFlows = itemFlows;

    console.log('itemFlows', itemFlows);
    _this.initEvents();
  }

  toProcessDefinition() {
    const _this = this;
    if (_this.currentItemFlow) {
      _this.unselectNode();
      _this.currentItemFlow.graphData = _this.graph.toJSON();
    }
    let processDefinition = _this.processDesigner.getProcessDefinition();
    processDefinition.itemFlows = JSON.stringify(_this.itemFlows || {});
  }

  // 初始化事件
  initEvents() {
    const _this = this;
    _this.graph.on('node:change:size', ({ node, options }) => {
      if (_this.layout.isLayouting()) {
        return;
      }
      // console.log('node:change:size');
      if (options.skipParentHandler) {
        return;
      }

      const children = node.getChildren();
      if (children && children.length) {
        node.prop('originSize', node.getSize());
      }
    });

    _this.graph.on('node:change:position', ({ node, options }) => {
      if (_this.layout.isLayouting()) {
        return;
      }

      // console.log('node:change:position');
      if (options.skipParentHandler) {
        return;
      }

      const children = node.getChildren();
      if (children && children.length) {
        node.prop('originPosition', node.getPosition());
      }

      const parent = node.getParent();
      if (parent && parent.isNode()) {
        let originSize = parent.prop('originSize');
        if (originSize == null) {
          parent.prop('originSize', parent.getSize());
        }
        originSize = parent.prop('originSize');

        let originPosition = parent.prop('originPosition');
        if (originPosition == null) {
          parent.prop('originPosition', parent.getPosition());
        }
        originPosition = parent.prop('originPosition');

        let x = originPosition.x;
        let y = originPosition.y;
        let cornerX = originPosition.x + originSize.width;
        let cornerY = originPosition.y + originSize.height;
        let hasChange = false;
        let changeX = false;
        let changeY = false;
        let changeCornerX = false;
        let changeCornerY = false;
        let paddingLength = 20;

        const children = parent.getChildren();
        if (children) {
          children.forEach(child => {
            const bbox = child.getBBox(); //.inflate(20);
            const corner = bbox.getCorner();

            if (bbox.x < x + paddingLength) {
              x = bbox.x;
              changeX = true;
              hasChange = true;
            }

            if (bbox.y < y + paddingLength) {
              y = bbox.y;
              changeY = true;
              hasChange = true;
            }

            if (corner.x > cornerX - paddingLength) {
              cornerX = corner.x;
              changeCornerX = true;
              hasChange = true;
            }

            if (corner.y > cornerY - paddingLength) {
              cornerY = corner.y;
              changeCornerY = true;
              hasChange = true;
            }
          });
        }

        if (hasChange) {
          // console.log("hasChange", hasChange);
          // console.log("originSize", originSize);
          // console.log("changeX", changeX);
          // console.log("changeY", changeY);
          // console.log("changeCornerX", changeCornerX);
          // console.log("changeCornerY", changeCornerY);
          let newX = changeX ? x - 20 : x;
          let newY = changeY ? y - 20 : y;
          let width = changeCornerX ? cornerX - x + (changeX ? 40 : 20) : cornerX - x + (changeX ? 20 : 0);
          let height = changeCornerY ? cornerY - y + (changeY ? 40 : 20) : cornerY - y + (changeY ? 20 : 0);

          // console.log("changed x y", x, y);
          // console.log("originSize", originSize);
          // console.log("originPosition", originPosition);
          // console.log("latestSize", { width, height });
          // console.log("latestPosition", { newX, newY });
          parent.prop(
            {
              position: { x: newX, y: newY },
              size: { width, height }
            },
            // Note that we also pass a flag so that we know we shouldn't
            // adjust the `originPosition` and `originSize` in our handlers.
            { skipParentHandler: true }
          );
        } else {
          parent.prop({
            position: { x, y },
            size: { width: cornerX - x, height: cornerY - y }
          });
        }
      }
    });
    // 包围盒渲染、删除工具
    _this.graph.on('cell:mouseenter', ({ cell }) => {
      if (cell.isNode()) {
        _this.showItemNodeInfo(cell);
        _this.showNodePorts(cell);
      }
      // 不可删除的直接返回
      if (cell.getProp('deletable') === false || !_this.editable) {
        return;
      }

      // let onClick = ({ view, btn }) => {
      //   if (_this.isSelectedNode(view.cell)) {
      //     // 记住最后选择的已删除节点
      //     _this.selectedNodeIdOfDeleted = _this.selectedNodeId;
      //   }
      //   console.log("btn.parent", btn.parent);
      //   console.log("view.cell", view.cell);
      //   btn.parent.remove();
      //   view.cell.remove({ ui: true, toolId: btn.cid });
      // }

      if (cell.isNode()) {
        // cell.getPorts().forEach(port => {
        //   cell.setPortProp(port.id || port, 'attrs/circle', { r: 4 });
        // });
        cell.addTools([
          {
            name: 'button-remove',
            args: {
              x: '100%',
              y: 0,
              offset: { x: -5, y: 5 }
              // onClick
            }
          }
        ]);
        // cell.addTools([
        //   {
        //     name: 'button',
        //     args: {
        //       x: '100%',
        //       y: '0',
        //       offset: { x: -8, y: -8 },
        //       ...deleteToolMarkup,
        //       onClick
        //     }
        //   }
        // ]);
      } else if (cell.isEdge()) {
        console.log('cell isEdge', cell);
        cell.addTools([
          {
            name: 'button-remove'
            // args: { onClick }
          }
        ]);
        // cell.addTools([
        //   {
        //     name: 'button',
        //     args: {
        //       ...deleteToolMarkup,
        //       onClick
        //     }
        //   }
        // ]);
        cell.setAttrs({ line: { stroke: 'var(--w-primary-color)', targetMarker: { stroke: 'var(--w-primary-color)' } } });
      }
    });
    _this.graph.on('cell:mouseleave', ({ cell }) => {
      if (cell.isNode()) {
        if (!_this.isSelectedNode(cell)) {
          _this.hideItemNodeInfo(cell);
        }
        _this.hideNodePorts(cell);
        // cell.getPorts().forEach(port => {
        //   cell.setPortProp(port.id || port, 'attrs/circle', { r: 0 });
        // });
      } else if (cell.isEdge() && !cell.hasTool('segments')) {
        // 边不选中时，离开改变颜色
        cell.setAttrs({ line: { stroke: edgeDefaultStroke, targetMarker: { stroke: edgeDefaultStroke } } });
      }
      cell.removeTools();
    });

    // 节点/边点击选择
    _this.graph.on('cell:click', ({ e, x, y, cell, view }) => {
      _this.selectNode(cell.id);
    });

    // 画布空白区域点击取消选择
    _this.graph.on('blank:click', ({ e, x, y, cell, view }) => {
      _this.unselectNode(_this.selectedNodeId);
    });

    // 边/节点删除
    _this.graph.on('cell:removed', ({ cell }) => {
      if (_this.isSelectedNode(cell)) {
        _this.unselectNode();
      }
      _this.update();
    });

    // 历史记录
    _this.graph.cleanHistory();
    _this.graph.on('history:change', () => {
      this.canRedo = _this.graph.canRedo();
      this.canUndo = _this.graph.canUndo();
    });
  }

  /**
   * 重置
   */
  reset() {
    const _this = this;
    if (_this.currentItemDefinition && _this.currentItemFlow) {
      delete _this.itemFlows[_this.currentItemFlow.itemId];
      let itemNode = _this.getItemNodeByItemId(_this.currentItemDefinition.id);
      if (itemNode) {
        let itemDefinition = itemNode.getData().configuration || {};
        _this.showItemFlowByItemDefinition(itemDefinition, false);
      } else {
        _this.showItemFlowByItemDefinition(_this.currentItemDefinition, false);
      }
    }
  }

  /**
   * 撤销
   */
  undo() {
    this.graph.undo();
  }

  /**
   * 恢复
   */
  redo() {
    this.graph.redo();
  }

  /**
   * 清空绘图
   */
  clear() {
    this.cleaning = true;
    this.unselectNode(this.selectedNodeId);
    this.graph.clearCells();
    this.graph.cleanHistory();
    this.cleaning = false;
  }

  /**
   * 更新
   */
  update() {
    const _this = this;
    if (_this.cleaning) {
      return;
    }

    if (_this.currentItemFlow) {
      _this.currentItemFlow.graphData = _this.graph.toJSON();
    }
  }

  // 删除
  removeNode(node) {
    const _this = this;
    if (node == null) {
      return;
    }
  }

  /**
   * 显示事项流
   *
   * @param {*} itemDefinition
   */
  showItemFlowByItemDefinition(itemDefinition, merge = true) {
    const _this = this;
    console.log('showItemFlow', itemDefinition);
    _this.currentItemDefinition = itemDefinition;
    let itemFlow = _this.getItemFlowByItemId(itemDefinition.id);
    if (_this.currentItemFlow) {
      _this.unselectNode();
      _this.currentItemFlow.graphData = _this.graph.toJSON();
    }

    _this.clear();

    if (itemFlow == null) {
      _this.currentItemFlow = _this.initItemFlowByItemDefinition(itemDefinition);
    } else {
      _this.showItemFlowByItemFlow(itemFlow);
      _this.currentItemFlow = itemFlow;
    }

    // 合并更新事项流
    if (merge) {
      _this.mergeItemFlowIfRequired();
    }

    _this.graph.cleanHistory();
  }

  /**
   * 显示事项流
   *
   * @param {*} itemFlow
   */
  showItemFlowByItemFlow(itemFlow) {
    this.graph.fromJSON(itemFlow.graphData);
  }

  /**
   * 合并更新事项流
   */
  mergeItemFlowIfRequired() {
    const _this = this;
    let itemDefinition = _this.currentItemDefinition;
    let itemFlow = _this.currentItemFlow;
    if (!itemDefinition || !itemFlow) {
      return;
    }

    let itemNodes = _this.graph.getNodes().filter(node => node.getData() && node.getData().type == 'item');
    itemNodes.forEach(itemNode => {
      let itemData = itemNode.getData();
      let itemType = itemData.itemType;
      let itemId = itemData.itemId;
      let sourceNode = _this.processDesigner.getItemNodeByItemId(itemId);
      // 组合事项下的事项
      if (itemData.belongItemId) {
        return;
      }
      if (itemType == '10') {
        // 单个事项
        if (sourceNode == null) {
          _this.markItemNode(itemNode, 'delete');
        }
      } else if (itemType == '20') {
        // 组合事项
        if (sourceNode == null) {
          _this.markItemNode(itemNode, 'delete');
        } else {
          let sourceItemDefinition = sourceNode.getData().configuration;
          _this.mergeCombinedItemIfRequired(sourceItemDefinition, itemNodes);
        }
      }
    });
  }

  mergeCombinedItemIfRequired(itemDefinition, itemNodes) {
    const _this = this;
    let parentItemId = itemDefinition.id;
    let includeItems = itemDefinition.includeItems || [];
    let childNodes = itemNodes.filter(item => parentItemId == item.getData().belongItemId);
    let addedItems = [];
    // 新增的事项
    includeItems.forEach(childItem => {
      let childIndex = childNodes.findIndex(childNode => childNode.getData().itemId == childItem.id);
      // 新增的事项配置
      if (childIndex == -1) {
        addedItems.push(childItem);
      }
    });
    // 删除的事项
    childNodes.forEach(childNode => {
      let childIndex = includeItems.findIndex(childItem => childNode.getData().itemId == childItem.id);
      // 删除的事项配置
      if (childIndex == -1) {
        _this.markItemNode(childNode, 'delete');
      }
    });

    // 新增的事项配置
    if (addedItems.length > 0) {
      let parentNode = itemNodes.find(item => item.getData().itemId == itemDefinition.id);
      if (parentNode) {
        // 标记为群组节点
        if (!parentNode.getProp('isGroup')) {
          parentNode.setProp('isGroup', true);
        }
        _this.appendChildItems(parentNode, addedItems);
      }
    }

    // 子事项递归处理
    includeItems.forEach(childItem => _this.mergeCombinedItemIfRequired(childItem, itemNodes));
  }

  /**
   * 标签事项节点
   *
   * @param {*} itemNode
   * @param {*} state
   */
  markItemNode(itemNode, state) {
    // 标记节点已删除
    if (state == 'delete') {
      let label = itemNode.label + '(已删除)';
      itemNode.setAttrs({ label: { text: label, fill: 'red' } });
      if (itemNode.getProp('deletable') === false) {
        itemNode.setProp('deletable', true);
      }
    } else {
    }
  }

  /**
   * 根据事项ID获取事项流
   *
   * @param {*} itemId
   * @returns
   */
  getItemFlowByItemId(itemId) {
    return this.itemFlows[itemId];
  }

  getStartNodeJson(nodeOptions = {}) {
    const _this = this;
    let startNodeSize = _this.layout.startNodeSize;
    if (nodeOptions.deletable !== false) {
      nodeOptions.ports = { ...itemFlowPorts };
    }
    return {
      shape: 'path',
      // x: 200,
      // y: 200,
      ...startNodeSize,
      data: { type: 'start', configuration: { name: '开始' } },
      // attrs: {
      //   circle: { fill: '#ffffff', strokeWidth: 3, stroke: '#8f8f8f', magnet: true },
      //   text: { text: '开始', fill: '#8f8f8f' }
      // },
      markup: [
        {
          tagName: 'circle',
          selector: 'circle',
          attrs: {
            cx: 28,
            cy: 28,
            r: 27.5,
            fill: '#ffffff'
          }
        },
        {
          tagName: 'path',
          selector: 'path1',
          groupSelector: 'path',
          attrs: {
            d: 'M28,3c13.785,0,25,11.215,25,25S41.785,53,28,53S3,41.785,3,28S14.215,3,28,3 M28,0C12.536,0,0,12.536,0,28s12.536,28,28,28  s28-12.536,28-28S43.464,0,28,0L28,0z'
          }
        },
        {
          tagName: 'path',
          selector: 'path2',
          groupSelector: 'path',
          attrs: {
            d: 'M36.172,26.401l-11.97-8.995C22.883,16.415,21,17.356,21,19.005v17.991c0,1.649,1.883,2.589,3.202,1.599l11.97-8.995  C37.236,28.799,37.236,27.201,36.172,26.401z'
          }
        }
      ],
      attrs: {
        path: {
          fill: 'var(--w-primary-color)'
        }
      },
      ...nodeOptions
    };
  }

  getGatewayNodeJson() {
    const _this = this;
    let getewayNodeSize = _this.layout.getewayNodeSize;
    return {
      ...getewayNodeSize,
      data: { type: 'gateway', configuration: { name: '网关' } },
      markup: [
        {
          tagName: 'rect',
          selector: 'rect',
          attrs: {
            transform: 'matrix(0.7071 0.7071 -0.7071 0.7071 14 -5.799)',
            x: '4.808',
            y: '4.808',
            fill: '#FFFFFF',
            width: '18.385',
            height: '18.385'
          }
        },
        {
          tagName: 'path',
          selector: 'path1',
          groupSelector: 'path',
          attrs: {
            d: 'M14,2.828L25.172,14L14,25.172L2.828,14L14,2.828 M14,0L0,14l14,14l14-14L14,0L14,0z'
          }
        },
        {
          tagName: 'circle',
          selector: 'path2',
          groupSelector: 'path',
          attrs: {
            cx: 14,
            cy: 14,
            r: 4
          }
        }
      ],
      attrs: {
        path: {
          fill: 'var(--w-primary-color)'
        }
      },
      ports: itemFlowPorts
    };
  }

  getItemNodeJson(itemDefinition, nodeOptions = {}, dataOptions = {}) {
    const _this = this;
    let itemSize = _this.layout.itemSize;

    let combiledItemText = {};
    if (itemDefinition.itemType == '20') {
      combiledItemText = { refX: 5, refY: 8, textAnchor: 'start', textVerticalAnchor: 'top' };
    } else {
      return _this.getItemSingleNodeJson(itemDefinition, nodeOptions, dataOptions);
    }
    let itemData = {
      type: 'item',
      itemId: itemDefinition.id,
      itemType: itemDefinition.itemType,
      ...dataOptions
    };
    let itemNodeJson = {
      shape: 'rect',
      // x: 300,
      // y: 200 - (70 - 40) / 2,
      // width: 130,
      // height: 170,
      ...itemSize,
      // label: itemDefinition.itemName,
      data: itemData,
      attrs: {
        body: {
          fill: '#ffffff',
          stroke: 'var(--w-primary-color)',
          strokeWidth: 2,
          rx: 4,
          ry: 4
        },
        label: {
          fill: '#ffffff',
          text: itemDefinition.itemName,
          ...combiledItemText,
          title: itemDefinition.itemName,
          fontSize: 14,
          padding: 10,
          textWrap: {
            ellipsis: true
          }
        },
        rect1: {
          fill: 'var(--w-primary-color)',
          ref: 'label',
          refX: 0,
          refY: -1,
          refWidth: '100%',
          refHeight: '100%',
          stroke: 'var(--w-primary-color)',
          strokeWidth: 10,
          rx: 2
        }
      },
      markup: [
        {
          tagName: 'rect',
          selector: 'body'
        },
        {
          tagName: 'rect',
          selector: 'rect1'
        },
        {
          tagName: 'text',
          selector: 'label'
        }
      ],
      ports: itemFlowPorts,
      ...nodeOptions
    };
    return itemNodeJson;
  }

  getItemSingleNodeJson(itemDefinition, nodeOptions = {}, dataOptions = {}) {
    const _this = this;
    let itemSize = _this.layout.itemSize;

    let itemData = {
      type: 'item',
      itemId: itemDefinition.id,
      itemType: itemDefinition.itemType,
      configuration: itemDefinition,
      ...dataOptions
    };
    let itemNodeJson = {
      shape: 'item-flow-node',
      id: itemDefinition.id,
      ...itemSize,
      data: itemData,
      label: itemDefinition.name,
      ports: itemFlowPorts,
      ...nodeOptions
    };
    return itemNodeJson;
  }

  getEndNodeJson() {
    const _this = this;
    let endNodeSize = _this.layout.endNodeSize;
    return {
      shape: 'path',
      // x: 430 + 60,
      // y: 200,
      // width: 40,
      // height: 40,
      ...endNodeSize,
      data: { type: 'end', configuration: { name: '结束' } },
      // attrs: {
      //   circle: { fill: '#ffffff', strokeWidth: 6, stroke: '#8f8f8f' },
      //   text: { text: '结束', fill: '#8f8f8f' }
      // }
      markup: [
        {
          tagName: 'circle',
          selector: 'circle',
          attrs: {
            cx: 28,
            cy: 28,
            r: 27.5,
            fill: '#FFFFFF'
          }
        },
        {
          tagName: 'path',
          selector: 'path1',
          groupSelector: 'path',
          attrs: {
            d: 'M28,3c13.785,0,25,11.215,25,25S41.785,53,28,53S3,41.785,3,28S14.215,3,28,3 M28,0C12.536,0,0,12.536,0,28s12.536,28,28,28  s28-12.536,28-28S43.464,0,28,0L28,0z'
          }
        },
        {
          tagName: 'path',
          selector: 'path2',
          groupSelector: 'path',
          attrs: {
            d: 'M36,38H20c-1.105,0-2-0.895-2-2V20c0-1.105,0.895-2,2-2h16c1.105,0,2,0.895,2,2v16C38,37.105,37.105,38,36,38z'
          }
        }
      ],
      attrs: {
        path: {
          fill: 'var(--w-primary-color)'
        }
      }
    };
  }

  /**
   * 根据事项定义初始化事项流
   *
   * @param {*} itemDefinition
   */
  initItemFlowByItemDefinition(itemDefinition) {
    const _this = this;
    if (itemDefinition.itemType == '10') {
      return _this.initSingleItemFlowByItemDefinition(itemDefinition);
    } else {
      return _this.initCombinedItemFlowByItemDefinition(itemDefinition);
    }
  }

  /**
   * 初始化单个事项流
   *
   * @param {*} itemDefinition
   * @returns
   */
  initSingleItemFlowByItemDefinition(itemDefinition) {
    const _this = this;
    // 事项流初始的开始节点不可删除
    let startNode = _this.getStartNodeJson({ deletable: false });
    // 事项流初始节点本身不可删除
    let itemNode = _this.getItemNodeJson(itemDefinition, { deletable: false });
    let endNode = _this.getEndNodeJson();

    startNode = _this.graph.addNode(startNode);
    itemNode = _this.graph.addNode(itemNode);
    endNode = _this.graph.addNode(endNode);
    // 事项流初始的开始流向不可删除
    _this.graph.addEdge({
      deletable: false,
      source: startNode,
      target: { cell: itemNode, port: itemNode.getPortsByGroup('left')[0].id },
      ...edgeAttrs
    });
    _this.graph.addEdge({
      source: { cell: itemNode, port: itemNode.getPortsByGroup('right')[0].id },
      target: endNode,
      ...edgeAttrs
    });

    _this.layout.layoutItemFlow(startNode, itemNode, endNode);

    _this.itemFlows[itemDefinition.id] = {
      itemId: itemDefinition.id,
      graphData: _this.graph.toJSON()
    };
    return _this.itemFlows[itemDefinition.id];
  }

  /**
   * 初始化组合事项流
   *
   * @param {*} itemDefinition
   */
  initCombinedItemFlowByItemDefinition(itemDefinition) {
    const _this = this;
    // 事项流初始的开始节点不可删除
    let startNode = _this.getStartNodeJson({ deletable: false });
    let endNode = _this.getEndNodeJson();

    startNode = _this.graph.addNode(startNode);
    endNode = _this.graph.addNode(endNode);

    // 添加组合事项
    // 事项流初始的开始流向不可删除
    let itemNode = _this.addCombinedItemByFlowDefinition(itemDefinition, { deletable: false });

    // 事项流初始的开始流向不可删除
    _this.graph.addEdge({
      deletable: false,
      source: startNode,
      target: { cell: itemNode, port: itemNode.getPortsByGroup('left')[0].id },
      ...edgeAttrs
    });
    _this.graph.addEdge({
      source: { cell: itemNode, port: itemNode.getPortsByGroup('right')[0].id },
      target: endNode,
      ...edgeAttrs
    });

    _this.layout.layoutItemFlow(startNode, itemNode, endNode);

    _this.itemFlows[itemDefinition.id] = {
      itemId: itemDefinition.id,
      graphData: _this.graph.toJSON()
    };
    return _this.itemFlows[itemDefinition.id];
  }

  addCombinedItemByFlowDefinition(itemDefinition, nodeJson = {}) {
    const _this = this;
    let itemNode = Object.assign(_this.getItemNodeJson(itemDefinition), nodeJson);

    itemNode = _this.graph.addNode(itemNode);
    // 标记为群组节点
    itemNode.setProp('isGroup', true);

    if (itemDefinition.includeItems && itemDefinition.includeItems.length > 0) {
      _this.addChildItems(itemNode, itemDefinition.includeItems);
    }
    return itemNode;
  }

  /**
   *
   */
  getChildItemOptions() {
    return {
      deletable: false,
      markup: [
        { tagName: 'rect', selector: 'body' },
        { tagName: 'text', selector: 'label' },
        {
          tagName: 'text',
          selector: 'itemInfo'
        }
      ],
      attrs: {
        itemInfo: {
          text: '',
          refX: 5,
          fontSize: '11px',
          refY: '100%',
          textAnchor: 'start',
          textVerticalAnchor: 'bottom',
          textWrap: {
            height: 20,
            ellipsis: true,
            breakWord: true
          },
          fill: 'blue' //'rgba(0, 0, 0, 0.25)'
        }
      }
    };
  }

  /**
   * 添加子事项
   *
   * @param {*} parentNode
   * @param {*} includeItems
   */
  addChildItems(parentNode, includeItems) {
    const _this = this;
    let startNode = _this.getStartNodeJson();
    startNode = _this.graph.addNode(startNode);
    parentNode.addChild(startNode);

    let endNode = _this.getEndNodeJson();
    endNode = _this.graph.addNode(endNode);
    parentNode.addChild(endNode);

    // 组合事项中的前后置事项信息
    let nodeOptions = _this.getChildItemOptions();
    let parentData = parentNode.getData();
    // 根事项ID
    let rootItemId = parentData.rootItemId || parentData.belongItemId;
    let parentItemId = parentData.itemId;
    includeItems.forEach(item => {
      // 组合事项中的事项不可删除
      let childNode = _this.getItemNodeJson(item, { deletable: false }, { belongItemId: parentItemId, rootItemId });
      // childNode.markup = deepClone(nodeOptions.markup);
      // childNode.attrs.itemInfo = deepClone(nodeOptions.attrs.itemInfo);
      childNode = _this.graph.addNode(childNode);
      // 子项数据配置是独立的数据
      // let childData = childNode.getData();
      // childData.configuration = { ...item };
      // childData.belongItemId = parentItemId;
      // childData.configuration = { itemChangable: false, ...item };
      // 子节点是组合事项，初始化子事项流
      if (item.itemType == '20' && item.includeItems && item.includeItems.length > 0) {
        _this.initChildCombinedItemByItemNode(childNode, item);
      }

      _this.graph.addEdge({
        source: startNode,
        target: { cell: childNode, port: childNode.getPortsByGroup('left')[0].id },
        ...edgeAttrs
      });
      _this.graph.addEdge({
        source: { cell: childNode, port: childNode.getPortsByGroup('right')[0].id },
        target: endNode,
        ...edgeAttrs
      });
      parentNode.addChild(childNode);
    });
  }

  /**
   * 添加组合节点事项流
   *
   * @param {*} itemNode
   * @param {*} itemDefinition
   */
  initChildCombinedItemByItemNode(itemNode, itemDefinition) {
    const _this = this;
    // 标记为群组节点
    itemNode.setProp('isGroup', true);

    if (itemDefinition.includeItems && itemDefinition.includeItems.length > 0) {
      _this.addChildItems(itemNode, itemDefinition.includeItems);
    }

    // 调整布局
    _this.layout.layoutCombinedItem(itemNode);
  }

  /**
   * 附加子事项
   *
   * @param {*} parentNode
   * @param {*} includeItems
   */
  appendChildItems(parentNode, includeItems) {
    const _this = this;
    // 组合事项中的前后置事项信息
    let nodeOptions = _this.getChildItemOptions();
    let parentData = parentNode.getData();
    // 根事项ID
    let rootItemId = parentData.rootItemId || parentData.belongItemId;
    let parentItemId = parentData.itemId;
    let children = [];
    includeItems.forEach(item => {
      // 组合事项中的事项不可删除
      let childNode = _this.getItemNodeJson(item, { deletable: false }, { belongItemId: parentItemId, rootItemId });
      // childNode.markup = deepClone(nodeOptions.markup);
      // childNode.attrs.itemInfo = deepClone(nodeOptions.attrs.itemInfo);
      childNode = _this.graph.addNode(childNode);
      // 子项数据配置是独立的数据
      // let childData = childNode.getData();
      // childData.configuration = { ...item };
      // childData.belongItemId = parentItemId;
      // childData.rootItemId = rootItemId;
      // childData.configuration = { itemChangable: false, ...item };

      childNode.setAttrs({ itemInfo: { text: `新增事项`, fill: 'green' } });
      children.push(childNode);
      // _this.graph.addEdge({ source: startNode, target: { cell: childNode, port: childNode.getPortsByGroup('left')[0].id } });
      // _this.graph.addEdge({ source: { cell: childNode, port: childNode.getPortsByGroup('right')[0].id }, target: endNode });
      parentNode.addChild(childNode);
    });
    _this.layout.layoutAppendChildren(parentNode, children);
  }

  /**
   * 显示前后置事项信息
   *
   * @param {*} cell
   */
  showItemNodeInfo(cell) {
    const _this = this;
    if (cell == null || !cell.isNode()) {
      return;
    }
    let nodeData = cell.getData();
    if (!nodeData || nodeData.type != 'item' || !nodeData.belongItemId) {
      return;
    }
    let itemId = nodeData.itemId;
    let rootItemId = nodeData.rootItemId;
    let belongItemId = nodeData.belongItemId;
    let itemDefinition = _this.getItemDefinitionByItemData({ itemId: belongItemId, rootItemId });
    // let parentItemNode = _this.processDesigner.getItemNodeByItemId(belongItemId);
    // if (!parentItemNode) {
    //   return;
    // }
    // let parentData = parentItemNode.getData();
    // let includeItems = parentData.configuration.includeItems || [];
    let includeItems = itemDefinition.includeItems || [];
    let frontItemCodes = includeItems.filter(item => item.id == itemId && item.frontItemCode).map(item => item.frontItemCode);
    let frontItemNames = includeItems.filter(item => frontItemCodes.indexOf(item.itemCode) != -1).map(item => item.itemName);

    if (frontItemNames.length > 0) {
      cell.setAttrs({ itemInfo: { text: `前置: ${frontItemNames.join('、')}`, fill: 'blue' } });
    }
  }

  /**
   * 显示节点端口
   */
  showNodePorts(cell) {
    const _this = this;
    let ports = cell.getPorts();
    cell.getPorts().forEach(port => {
      cell.setPortProp(port.id || port, 'attrs/circle', { r: 4, opacity: 1 });
    });
  }

  /**
   * 隐藏节点端口
   */
  hideNodePorts(cell) {
    const _this = this;
    let ports = cell.getPorts();
    cell.getPorts().forEach(port => {
      cell.setPortProp(port.id || port, 'attrs/circle', { r: 0 });
    });
  }

  /**
   * 隐藏前后置事项信息
   *
   * @param {*} cell
   */
  hideItemNodeInfo(cell) {
    if (cell == null || !cell.isNode()) {
      return;
    }
    let nodeData = cell.getData();
    if (!nodeData || nodeData.type != 'item' || !nodeData.belongItemId) {
      return;
    }
    // 在生产环境设置空白串才生效
    cell.setAttrs({ itemInfo: { text: ' ' } });
  }

  /**
   * 更新拖动对象信息
   *
   * @param {*} info
   */
  updateDragInfo({
    startX = this.dragInfo.startX,
    startY = this.dragInfo.startY,
    x = this.dragInfo.x,
    y = this.dragInfo.y,
    dragoverCellId
  }) {
    this.dragInfo.startX = startX;
    this.dragInfo.startY = startY;
    this.dragInfo.x = x - this.graph.options.x;
    this.dragInfo.y = y - this.graph.options.y;
    this.dragInfo.dragoverCellId = dragoverCellId;
  }

  /**
   * 添加拖动的节点
   *
   * @param {*} nodeOptions
   */
  addDragNode(nodeOptions = {}) {
    const _this = this;
    let nodeType = nodeOptions.type;
    let x = _this.dragInfo.x - _this.dragInfo.startX;
    let y = _this.dragInfo.y - _this.dragInfo.startY;
    let nodeJson = { x, y };
    let combinedItem = false;
    let itemDefinition = nodeOptions.configuration;
    switch (nodeType) {
      case 'start':
        nodeJson = Object.assign(_this.getStartNodeJson(), nodeJson);
        break;
      case 'gateway':
        nodeJson = Object.assign(_this.getGatewayNodeJson(), nodeJson);
        break;
      case 'item':
        // 取最新的事项定义
        let itemNode = _this.getItemNodeByItemId(itemDefinition.id);
        if (itemNode) {
          itemDefinition = itemNode.getData().configuration || itemDefinition;
        }
        combinedItem = itemDefinition && itemDefinition.itemType != '10';
        nodeJson = Object.assign(_this.getItemNodeJson(itemDefinition), nodeJson);
        break;
      case 'end':
        nodeJson = Object.assign(_this.getEndNodeJson(), nodeJson);
        break;
    }

    let addedNode = null;
    // 添加组合事项
    if (combinedItem) {
      addedNode = _this.addCombinedItemByFlowDefinition(itemDefinition, nodeJson);
      _this.layout.layoutCombinedItem(addedNode);
    } else {
      addedNode = _this.graph.addNode(nodeJson);
    }
    _this.update();

    // 开始、网关、结束节点可拖入组合节点
    if (nodeType != 'item') {
      let dragoverCellId = _this.dragInfo.dragoverCellId;
      let parentNode = _this.findParentNodeById(dragoverCellId);
      if (parentNode != null) {
        parentNode.addChild(addedNode);
      }
    }

    console.log(' _this.graph', _this.graph.toJSON());
  }

  findParentNodeById(cellId) {
    const _this = this;
    let cell = _this.graph.getCellById(cellId);
    if (cell == null) {
      return;
    }
    let isGroup = cell.getProp('isGroup');
    if (isGroup) {
      return cell;
    }
    return cell.getParent();
  }

  // 选择边/节点
  selectNode(id) {
    const _this = this;
    // 取消选择
    if (_this.selectedNodeId) {
      _this.unselectNode(_this.selectedNodeId);
    }

    let cell = null;
    if (id) {
      cell = _this.graph.getCellById(id); // 选择

      // 选择
      if (cell) {
        _this.selectedNodeId = id;
        if (cell.vm) {
          invokeVueMethod(cell.vm, 'select');
        }
      }
    }

    // 选择效果
    if (cell) {
      if (cell.isEdge()) {
        cell.addTools([
          {
            name: 'segments',
            args: {
              snapRadius: 8,
              attrs: {
                y: 0,
                height: 1,
                fill: 'transparent',
                stroke: 'transparent'
              }
            }
          }
        ]);
        cell.setAttrs({
          line: { stroke: 'var(--w-primary-color)', strokeWidth: 3, targetMarker: { stroke: 'var(--w-primary-color)', strokeWidth: 3 } }
        });
      }
    }

    // 显示前后置事项信息
    _this.showItemNodeInfo(cell);
  }

  isSelectedNode(cell) {
    return cell.id == this.selectedNodeId;
  }

  // 取消选择边/节点
  unselectNode(id = this.selectedNodeId) {
    const _this = this;

    _this.selectedNodeId = '';

    let cell = null;
    if (id) {
      cell = _this.graph.getCellById(id);
    }

    // 取消选中效果
    if (cell) {
      cell.removeTools(['boundary', 'segments']);
      invokeVueMethod(cell.vm, 'unselect');

      if (cell.isEdge()) {
        cell.setAttrs({ line: { stroke: edgeDefaultStroke, strokeWidth: 2, targetMarker: { stroke: edgeDefaultStroke, strokeWidth: 2 } } });
      }
    }

    // 隐藏前后置事项信息
    _this.hideItemNodeInfo(cell);
  }

  // 模块选择的节点
  getSelectedNode() {
    const _this = this;
    let selectedNode = null;
    if (_this.selectedNodeId) {
      selectedNode = this.graph.getCellById(this.selectedNodeId);
    }
    if (!selectedNode) {
      return null;
    }

    console.log('selectedNode', selectedNode);

    let id = _this.selectedNodeId;
    let nodeData = selectedNode.getData();
    // 事项节点获取业务流程中的事项定义
    if (nodeData && nodeData.type == 'item') {
      let itemDefinition = null;
      id = nodeData.itemId;
      itemDefinition = _this.getItemDefinitionByItemData(nodeData);
      console.log('itemDefinition', itemDefinition);
      // 定义为空不显示配置面板
      if (itemDefinition == null) {
        return null;
      }
      return {
        id,
        setAttrs() {
          //console.log("setAttrs", selectedNode.getAttrs());
          selectedNode.setAttrs(...arguments);
        },
        getData() {
          return { configuration: itemDefinition, ...nodeData };
        }
      };
    } else {
      return selectedNode;
    }
  }

  getItemNodeByItemId(itemId) {
    return this.processDesigner.getItemNodeByItemId(itemId);
  }

  getItemDefinitionByItemData(itemData) {
    const _this = this;
    let itemDefinition = {};
    // 组合事项中的事项定义在事项包含的子事项中
    if (itemData.rootItemId || itemData.belongItemId) {
      let itemId = itemData.itemId;
      let rootItemId = itemData.rootItemId || itemData.belongItemId;

      let findItemDefinition = (includeItems = []) => {
        if (itemDefinition.id) {
          return;
        }
        includeItems.forEach(childItem => {
          if (childItem.id == itemId) {
            itemDefinition = childItem;
          } else {
            findItemDefinition(childItem.includeItems);
          }
        });
      };
      let belongItemNode = _this.getItemNodeByItemId(rootItemId);
      if (belongItemNode && belongItemNode.data && belongItemNode.data.configuration && belongItemNode.data.configuration.includeItems) {
        findItemDefinition(belongItemNode.data.configuration.includeItems);
        if (itemDefinition) {
          itemDefinition.itemChangable = false;
        }
      }
    } else {
      // let treeNode = _this.processDesigner.processTree.getTreeNodeByKey(itemData.itemId);
      let sourceNode = _this.getItemNodeByItemId(itemData.itemId); // _this.processDesigner.graph.getCellById(itemData.itemId);
      if (sourceNode && sourceNode.data) {
        itemDefinition = sourceNode.data.configuration;
      }
    }
    return itemDefinition;
  }

  // 判断节点是否为模板
  isTemplateNode(nodeId, type) {
    return this.processDesigner.isTemplateNode(nodeId, type);
  }

  // 判断节点是否为引用
  isRefNode(nodeId, type) {
    return this.processDesigner.isRefNode(nodeId, type);
  }

  // 获取节点引用信息
  getNodeRefInfo(node) {
    return this.processDesigner.getNodeRefInfo(node);
  }

  // 事项流验证
  validate($widget) {
    const _this = this;
    if (_this.currentItemFlow) {
      _this.currentItemFlow.graphData = _this.graph.toJSON();
    }
    return new Promise((resolve, reject) => {
      if (_this.validateItemFlow($widget)) {
        resolve(true);
      } else {
        reject(false);
      }
    });
  }

  validateItemFlow($widget) {
    let isItemChild = (cellId, itemNodes) => {
      let isChild = false;
      itemNodes.forEach(item => {
        if (isChild) {
          return;
        }
        isChild = item.children && item.children.indexOf(cellId) != -1;
      });
      return isChild;
    };
    let hasEdge = (cellId, edges, outgoing = true, incoming = true) => {
      for (let index = 0; index < edges.length; index++) {
        let edge = edges[index];
        if ((outgoing && edge.source && edge.source.cell == cellId) || (incoming && edge.target && edge.target.cell == cellId)) {
          return true;
        }
      }
      return false;
    };

    for (let itemId in this.itemFlows) {
      let itemFlow = this.itemFlows[itemId];
      if (!itemFlow || !itemFlow.graphData || !itemFlow.graphData.cells) {
        return;
      }
      // console.log("itemFlow", itemFlow);
      let cells = itemFlow.graphData.cells;
      let nodes = cells.filter(cell => cell.shape != 'edge');
      let itemNodes = nodes.filter(cell => cell.data && cell.data.type == 'item');
      let startNodes = nodes.filter(cell => cell.data && cell.data.type == 'start' && !isItemChild(cell.id, itemNodes));
      if (startNodes.length > 1) {
        $widget.$message.error(`事项流[${itemFlow.itemName}]只能有开始节点！`);
        return false;
      }

      let edges = cells.filter(cell => cell.shape == 'edge');
      for (let index = 0; index < nodes.length; index++) {
        let node = nodes[index];
        if (node.data.type == 'start') {
          if (!hasEdge(node.id, edges)) {
            $widget.$message.error(`事项流[${itemFlow.itemName}]开始节点缺少输出边！`);
            return false;
          }
        } else if (node.data.type == 'end') {
          if (!hasEdge(node.id, edges)) {
            $widget.$message.error(`事项流[${itemFlow.itemName}]结束节点缺少输入边！`);
            return false;
          }
        } else if (node.data.type == 'gateway') {
          if (!hasEdge(node.id, edges, true, false) && hasEdge(node.id, edges, false, true)) {
            $widget.$message.error(`事项流[${itemFlow.itemName}]网关节点缺少边！`);
            return false;
          }
        } else if (node.data.type == 'item') {
          if (!(hasEdge(node.id, edges, true, false) && hasEdge(node.id, edges, false, true))) {
            let itemName = (node.attrs && node.attrs.text && node.attrs.text.text) || (node.attrs && node.attrs.label && node.attrs.label.text)
              || node.data && node.data.configuration && node.data.configuration.itemName;
            $widget.$message.error(`事项流[${itemFlow.itemName}]事项节点[${itemName}]缺少边！`);
            return false;
          }
        }
      }
    }
    return true;
  }
}

export default ItemFlowDesigner;
