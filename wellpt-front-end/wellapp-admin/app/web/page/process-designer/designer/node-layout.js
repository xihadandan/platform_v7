let nodePaddingSize = 15;
let itemPaddingSize = 12;
let itemPaddingSizeTopBottom = 7;

let enableCollapse = true;
class NodeLayout {
  // 开始位置
  get startPosition() {
    return {
      x: 20,
      y: 20
    };
  }

  // 水平过程节点大小
  get horizontalNodeSize() {
    return {
      width: 180,
      height: 65
    };
  }
  // 过程节点三角箭头及添加图标大小
  get nodeMarkerSize() {
    // return 30;
    return 48
  }
  // 垂直过程节点大小
  get verticalNodeSize() {
    return {
      width: 65,
      height: 180
    };
  }

  // 事项节点大小
  get itemSize() {
    return {
      width: 200,
      // height: 32
      height: 24
    };
  }
  // 事项节点添加图标大小
  get itemMarkerSize() {
    return 20;
  }

  constructor(options) {
    this.direction = 'horizontal'; // 默认水平方向
    if (options && options.rootNode) {
      this.layout(rootNode);
    }
    this.designer = options.designer;
  }

  layout(rootNode) {
    const _this = this;
    _this.rootNode = rootNode;
    let groupSize = _this.getGroupSize(rootNode);
    rootNode.resize(groupSize.width, groupSize.height);
    _this.layoutChildren(rootNode);
  }

  layoutGroup(groupNode) {
    if (!groupNode) {
      return
    }
    const _this = this;
    let groupData = groupNode.getData();
    let parentId = groupData.parentId;
    // 根节点
    if (parentId == -1) {
      _this.layout(groupNode);
    } else {
      let parentNode = _this.designer.graph.getCellById(parentId);
      if (groupData.type == 'node-group') {
        _this.layoutProcessGroup(parentNode, groupNode);
      } else {
        _this.layoutItemGroup(parentNode, groupNode);
      }
    }
  }

  // 布局群组的同级群组
  layoutSiblingGroup(groupNode) {
    const _this = this;
    let groupData = groupNode.getData();
    let parentId = groupData.parentId;
    // 根节点没有同级群组
    if (parentId == -1 || parentId == '-1') {
      return;
    }
    let graph = _this.designer.graph;
    let parentNode = graph.getCellById(parentId);
    if (parentNode != null) {
      let parentData = parentNode.getData();
      if (parentData.childOfProcessGroupId && parentData.childOfProcessGroupId != groupNode.id) {
        let siblingGroup = graph.getCellById(parentData.childOfProcessGroupId);
        if (siblingGroup != null) {
          _this.layoutProcessGroup(parentNode, siblingGroup);
        }
      } else if (parentData.childOfItemGroupId && parentData.childOfItemGroupId != groupNode.id) {
        let siblingGroup = graph.getCellById(parentData.childOfItemGroupId);
        if (siblingGroup != null) {
          _this.layoutItemGroup(parentNode, siblingGroup);
        }
      }
    }
  }

  // 布局节点群组
  layoutProcessGroup(parentNode, groupNode) {
    const _this = this;
    if (parentNode == null) {
      return _this.layout(groupNode);
    }
    // 调整大小
    let groupSize = _this.getGroupSize(groupNode);
    groupNode.resize(groupSize.width, groupSize.height);

    // 调整位置
    let parentPosition = parentNode.getPosition();
    let parentSize = parentNode.getSize();
    let parentDirection = parentNode.getData().layout;
    let x = _this.startPosition.x; //起点对齐
    let y = _this.startPosition.y;
    // 上级阶段是水平方向
    if (parentDirection == 'horizontal') {
      // x = parentPosition.x; //parentPosition.x + parentSize.width / 2 - groupSize.width / 2;
      y = parentPosition.y + parentSize.height + nodePaddingSize * 4;
    } else {
      x = parentPosition.x + parentSize.width + nodePaddingSize * 4;
      // y = parentPosition.y;//parentPosition.y + parentSize.height / 2 - groupSize.height / 2;
    }
    groupNode.position(x, y);

    // 调整容器内的节点位置
    _this.layoutChildren(groupNode);
  }

  resizeProcessGroup(groupNode) {
    const _this = this;
    // 调整大小
    let groupSize = _this.getGroupSize(groupNode);
    groupNode.resize(groupSize.width, groupSize.height);
    // 调整容器内的节点位置
    _this.layoutChildren(groupNode);
  }

  // 布局事项群组节点
  layoutItemGroup(parentNode, groupNode) {
    const _this = this;
    // 调整大小
    let groupSize = _this.getGroupSize(groupNode);
    groupNode.resize(groupSize.width, groupSize.height);

    // 调整位置
    let parentPosition = parentNode.getPosition();
    let parentSize = parentNode.getSize();
    let parentData = parentNode.getData();
    let parentDirection = parentData.layout;
    let siblingGroupNode = null;
    let childOfProcessGroupId = parentData.childOfProcessGroupId;
    if (childOfProcessGroupId) {
      siblingGroupNode = _this.designer.graph.getCellById(childOfProcessGroupId);
    }
    let x = _this.startPosition.x + nodePaddingSize; //起点对齐
    let y = _this.startPosition.y + nodePaddingSize;
    // 上级阶段是水平方向
    if (parentDirection == 'horizontal') {
      y = parentPosition.y + parentSize.height + nodePaddingSize * 5;
      // 存在同级的阶段群组时调整x轴
      if (siblingGroupNode) {
        x += siblingGroupNode.getSize().width + nodePaddingSize;
      }
    } else {
      let childSize = _this.getNodeChildren(groupNode).length;
      x = parentPosition.x + parentSize.width + +nodePaddingSize * 5;
      // 存在同级的阶段群组时调整x轴
      if (siblingGroupNode) {
        y += siblingGroupNode.getSize().height + nodePaddingSize * 2;
      }
      // 上级阶段是垂直方向，确保y大于等于上级节点所在分组
      let parentGroup = parentNode.getParent();
      let parentGroupPosition = parentGroup.getPosition();
      if (y < parentGroupPosition.y) {
        y = parentGroupPosition.y;
      }
    }
    groupNode.position(x, y);

    // 调整容器内的节点位置
    _this.layoutChildren(groupNode);
  }

  resizeItemGroup(groupNode) {
    const _this = this;
    // 调整大小
    let groupSize = _this.getGroupSize(groupNode);
    groupNode.resize(groupSize.width, groupSize.height);
    // 调整容器内的节点位置
    _this.layoutChildren(groupNode);
  }

  layoutChildren(parentNode) {
    const _this = this;
    let groupData = parentNode.getData();
    let direction = groupData.layout;
    // 阶段节点
    if (groupData.type == 'node-group') {
      let children = _this.getNodeChildren(parentNode);
      let nodeSize = _this.getNodeSize(direction);
      for (let index = 0; index < children.length; index++) {
        let child = children[index];
        let x = nodePaddingSize;
        let y = nodePaddingSize;
        if (direction == 'horizontal') {
          x += (nodeSize.width + _this.nodeMarkerSize) * index;
        } else {
          y += (nodeSize.height + _this.nodeMarkerSize) * index;
        }
        child.position(x, y, { relative: true });

        // 节点的子节点及事项群组
        let childData = child.getData();
        if (childData.childOfProcessGroupId) {
          _this.layoutProcessGroup(child, _this.designer.graph.getCellById(childData.childOfProcessGroupId));
        }
        if (childData.childOfItemGroupId) {
          _this.layoutItemGroup(child, _this.designer.graph.getCellById(childData.childOfItemGroupId));
        }
      }
    } else {
      // 事项节点
      let children = _this.getNodeChildren(parentNode);
      let itemSize = _this.getItemSize();
      for (let index = 0; index < children.length; index++) {
        let child = children[index];
        let x = itemPaddingSize;
        let y = itemPaddingSize;
        if (direction == 'horizontal') {
          y = itemPaddingSizeTopBottom;
          // 一列2个
          if (index % 2 == 1) {
            //下面一个
            x += (itemSize.width + _this.itemMarkerSize) * Math.floor(index / 2);
            y += itemSize.height + 4;
          } else {
            //上面一个
            x += (itemSize.width + _this.itemMarkerSize) * Math.floor(index / 2);
          }
        } else {
          x = itemPaddingSizeTopBottom
          // 一行2个
          if (index % 2 == 1) {
            //右边一个
            y += (itemSize.width + _this.itemMarkerSize) * Math.floor(index / 2);
            x += itemSize.height + 4;
          } else {
            //左面一个
            y += (itemSize.width + _this.itemMarkerSize) * Math.floor(index / 2);
          }
        }
        child.position(x, y, { relative: true });
      }
    }
  }

  getNodeChildren(parentNode) {
    let nodes = [];
    let children = parentNode.getChildren();
    if (children == null) {
      return [];
    }
    children.forEach(child => {
      if (child.shape != 'edge') {
        nodes.push(child);
      }
    });
    return nodes;
  }

  getNodeSize(direction) {
    if (direction == 'horizontal') {
      return this.horizontalNodeSize;
    }
    return this.verticalNodeSize;
  }

  getItemSize() {
    return this.itemSize;
  }

  // 获取分组大小
  getGroupSize(groupNode) {
    const _this = this;
    let groupData = groupNode.getData();
    let children = _this.getNodeChildren(groupNode);
    let childSize = children.length;
    let direction = groupData.layout;
    // 阶段群组
    if (groupData.type == 'node-group') {
      let nodeSize = _this.getNodeSize(direction);
      if (childSize == 0) {
        return {
          width: nodeSize.width + nodePaddingSize * 2,
          height: nodeSize.height + nodePaddingSize * 2
        };
      } else if (direction == 'horizontal') {
        return {
          width: nodeSize.width * childSize + _this.nodeMarkerSize * childSize + nodePaddingSize * 1.5, // 横向时有右侧添加按钮
          height: nodeSize.height + nodePaddingSize * 2
        };
      } else {
        return {
          width: nodeSize.width + nodePaddingSize * 2,
          height: nodeSize.height * childSize + _this.nodeMarkerSize * childSize + nodePaddingSize * 1.5
        };
      }
    } else {
      // 事项群组
      let itemSize = _this.getItemSize();
      if (childSize == 0) {
        return {
          width: itemSize.width + itemPaddingSize * 2,
          height: itemSize.height + itemPaddingSizeTopBottom * 2
        };
      } else if (direction == 'horizontal') {
        return {
          width: (itemSize.width + _this.itemMarkerSize) * Math.ceil(childSize / 2) + itemPaddingSize, // 横向时有右侧添加按钮
          height: (itemSize.height + itemPaddingSizeTopBottom) * 2 + 4
        };
      } else {
        return {
          width: (itemSize.height + itemPaddingSizeTopBottom) * 2 + 4,
          height: (itemSize.width + _this.itemMarkerSize) * Math.ceil(childSize / 2) + itemPaddingSize
        };
      }
    }
  }

  // 获取根节点的布局
  getRootLayout() {
    const _this = this;
    if (_this.rootNode == null) {
      _this.rootNode = _this.getRootNodeFromDesigner();
    }
    if (_this.rootNode == null) {
      return 'horizontal';
    }
    let nodeData = _this.rootNode.getData();
    return (nodeData && nodeData.layout) || 'horizontal';
  }

  // 从设计器中获取根节点
  getRootNodeFromDesigner() {
    let nodes = this.designer.graph.getNodes();
    for (let index = 0; index < nodes.length; index) {
      let node = nodes[index];
      let nodeData = node.getData();
      if (nodeData && nodeData.parentId == -1) {
        return node;
      }
    }
    return null;
  }

  // 显示根群组
  showRoot() {
    if (!enableCollapse) {
      return;
    }
    const _this = this;
    if (_this.rootNode == null) {
      _this.rootNode = _this.getRootNodeFromDesigner();
    }
    if (_this.rootNode == null) {
      return;
    }

    let rootGroup = _this.rootNode;
    let children = _this.getNodeChildren(rootGroup);
    children.forEach(child => {
      _this.collapseChildOfNode(child);
    });
  }

  // 折叠群组
  collapseGroup(group) {
    const _this = this;
    group.hide();
    let children = _this.getNodeChildren(group);
    children.forEach(child => {
      _this.collapseNode(child);
    });
  }

  // 折叠节点
  collapseNode(node) {
    node.hide();
    this.collapseChildOfNode(node);
  }

  // 折叠节点
  collapseChildOfNode(node) {
    const _this = this;
    let nodeData = node.getData();
    let groupNode = null;
    if (nodeData.childOfProcessGroupId) {
      groupNode = _this.designer.graph.getCellById(nodeData.childOfProcessGroupId);
      if (groupNode) {
        _this.collapseGroup(groupNode);
      }
    }
    if (nodeData.childOfItemGroupId) {
      groupNode = _this.designer.graph.getCellById(nodeData.childOfItemGroupId);
      if (groupNode) {
        _this.collapseGroup(groupNode);
      }
    }
  }

  // 显示节点
  showNode(node) {
    if (!enableCollapse) {
      return;
    }

    const _this = this;
    if (node == null) {
      return;
    }
    let nodeData = node.getData();
    // 节点为群组
    if (nodeData.type == 'node-group' || nodeData.type == 'item-group') {
      _this.showGroup(node);
      return;
    }

    // 显示第一级
    _this.showRoot();

    // 显示节点所在群组的节点
    let groupNode = node.getParent();
    if (groupNode != null) {
      _this.showGroup(groupNode);
    }

    // 显示节点关联的子群组
    if (nodeData.childOfProcessGroupId) {
      groupNode = _this.designer.graph.getCellById(nodeData.childOfProcessGroupId);
      if (groupNode) {
        _this.showGroup(groupNode);
        // _this.layoutProcessGroup(node, groupNode);
      }
    }
    if (nodeData.childOfItemGroupId) {
      groupNode = _this.designer.graph.getCellById(nodeData.childOfItemGroupId);
      if (groupNode) {
        _this.showGroup(groupNode);
        // _this.layoutItemGroup(node, groupNode);
      }
    }

    // 显示父节点
    _this.showParentNode(node);

    // 显示父节点的兄第节点
    let parentGroup = node.getParent();
    if (parentGroup != null) {
      let groupData = parentGroup.getData();
      let parentNode = _this.designer.graph.getCellById(groupData.parentId);
      if (parentNode != null) {
        let parentData = parentNode.getData();
        let siblingGroup;
        if (parentData.childOfProcessGroupId && parentData.childOfProcessGroupId != parentGroup.id) {
          siblingGroup = _this.designer.graph.getCellById(parentData.childOfProcessGroupId);
        } else if (parentData.childOfItemGroupId && parentData.childOfItemGroupId != parentGroup.id) {
          siblingGroup = _this.designer.graph.getCellById(parentData.childOfItemGroupId);
        }
        let hideSiblingItem = true; // 隐藏同级事项
        if (siblingGroup && hideSiblingItem && node.shape === 'process-item') {
          _this.showGroup(siblingGroup);
        }
      }
    }
  }

  // 显示上级节点
  showParentNode(node) {
    const _this = this;
    // 上级节点是群组
    let parentNode = node.getParent();
    if (parentNode != null) {
      _this.showGroup(parentNode);
      _this.showParentNode(parentNode);
    }

    // 上级节点是节点
    let nodeData = node.getData();
    if (nodeData.parentId) {
      parentNode = _this.designer.graph.getCellById(nodeData.parentId);
      if (parentNode != null) {
        _this.showParentNode(parentNode);
      }
    }
  }

  // 显示群组
  showGroup(group) {
    const _this = this;
    group.show();
    let children = _this.getNodeChildren(group);
    children.forEach(child => {
      child.show();
    });
  }
}

export default NodeLayout;
