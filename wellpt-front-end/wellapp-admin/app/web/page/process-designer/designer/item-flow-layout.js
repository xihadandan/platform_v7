class ItemFlowLayout {
  constructor() {
    this.layouting = false;
  }

  // 开始位置
  get startPosition() {
    return {
      x: 200,
      y: 200
    };
  }

  // 事项节点大小
  get itemSize() {
    return {
      width: 200,
      height: 40
    };
  }

  // 开始节点大小
  get startNodeSize() {
    return {
      width: 56,
      height: 56
    };
  }

  // 结束节点大小
  get endNodeSize() {
    return {
      width: 56,
      height: 56
    };
  }
  // 网关节点大小
  get getewayNodeSize() {
    return {
      width: 28,
      height: 28
    };
  }

  // 节点水平间隔
  get horizontalNodeSpace() {
    return 60;
  }

  // 节点垂直间隔
  get verticalNodeSpace() {
    return 35;
  }

  /**
   * 是否在布局中
   *
   * @returns
   */
  isLayouting() {
    return this.layouting;
  }

  /**
   * 事项流布局
   *
   * @param {*} startNode
   * @param {*} itemNode
   * @param {*} endNode
   */
  layoutItemFlow(startNode, itemNode, endNode) {
    const _this = this;
    _this.layouting = true;

    let startPosition = _this.startPosition;
    startNode.position(startPosition.x, startPosition.y);

    let children = itemNode.getChildren();
    // 调整父节点大小
    if (children && children.length > 0) {
      let itemNodes = children.filter(child => child.data && child.data.type == 'item');
      _this.resizeCombinedItem(itemNode, itemNodes);
    }

    _this.layoutNextNode(startNode, itemNode);

    // 事项节点y坐标为负数时，向下调整为正数
    let itemPosition = itemNode.position();
    if (itemPosition.y < 0) {
      let diffY = -itemPosition.y + 50;
      itemNode.position(itemPosition.x, itemPosition.y + diffY);
      let startNodePosition = startNode.position();
      startNode.position(startNodePosition.x, startNodePosition.y + diffY);
    }

    _this.layoutNextNode(itemNode, endNode);

    // 布局组合节点的子节点
    if (children && children.length > 0) {
      _this.layoutCombinedItemChildren(itemNode, children);
    }

    _this.layouting = false;
  }

  layoutCombinedItem(itemNode) {
    const _this = this;
    _this.layouting = true;
    let children = itemNode.getChildren();
    // 调整父节点大小
    if (children && children.length > 0) {
      let itemNodes = children.filter(child => child.data && child.data.type == 'item');
      _this.resizeCombinedItem(itemNode, itemNodes);

      _this.layoutCombinedItemChildren(itemNode, children);
    }
    _this.layouting = false;
  }

  layoutCombinedItemChildren(parentNode, children) {
    const _this = this;
    let startNode = children.find(child => child.data && child.data.type == 'start');
    let itemNodes = children.filter(child => child.data && child.data.type == 'item');
    let endNode = children.find(child => child.data && child.data.type == 'end');
    let bbox = parentNode.getBBox();
    console.log('parent bbox', bbox);

    let startX = bbox.x + _this.horizontalNodeSpace;
    let startY = bbox.center.y - _this.startNodeSize.height / 2;
    if (startNode) {
      startNode.position(startX, startY);
    }

    // let verticalSpace = (this.horizontalNodeSpace * itemNodes.length) / (itemNodes.length + 1);
    let preNode = null;
    let totalNodeHeight = 0;
    itemNodes.forEach((node, index) => {
      if (preNode) {
        totalNodeHeight += preNode.getSize().height;
      }
      preNode = node;
      let nodeX = bbox.x + _this.startNodeSize.width + _this.horizontalNodeSpace * 2;
      let nodeY = bbox.y + _this.verticalNodeSpace * (index + 1) + totalNodeHeight;
      if (node.getChildren()) {
        _this.positionCombinedNode(node, nodeX, nodeY);
      } else {
        node.position(nodeX, nodeY);
      }

      // 递归处理组合子节点
      if (node.getChildren()) {
        _this.layoutCombinedItemChildren(node, node.getChildren());
      }
    });

    let endX = bbox.right - _this.endNodeSize.width - _this.horizontalNodeSpace;
    let endY = bbox.center.y - _this.endNodeSize.height / 2;
    if (endNode) {
      endNode.position(endX, endY);
    }
  }

  positionCombinedNode(node, x, y) {
    let originPosition = node.position();
    node.position(x, y);
    let children = node.getChildren();
    if (children) {
      let diffX = x - originPosition.x;
      let diffY = y - originPosition.y;
      children.forEach(child => {
        let childPosition = child.position();
        // console.log('childPosition', childPosition, diffX, diffY);
        child.position(childPosition.x + diffX, childPosition.y + diffY);
      });
    }
  }

  layoutAppendChildren(parentNode, itemNodes) {
    const _this = this;
    let bbox = parentNode.getBBox();
    let width = bbox.width;
    let height = bbox.height;
    // console.log("bbox", bbox);
    let childrenCount = itemNodes.length;
    let newHeight = height + _this.itemSize.height * childrenCount + _this.verticalNodeSpace * childrenCount;
    parentNode.resize(width, newHeight);

    itemNodes.forEach((node, index) => {
      let nodeX = bbox.bottomCenter.x - _this.itemSize.width / 2;
      let nodeY = bbox.bottom + _this.verticalNodeSpace * index + _this.itemSize.height * index;
      if (node.getChildren()) {
        _this.positionCombinedNode(node, nodeX, nodeY);
      } else {
        node.position(nodeX, nodeY);
      }
    });
  }

  resizeCombinedItem(itemNode, children) {
    const _this = this;
    let childrenCount = children.length;
    let maxWidth = 0;
    let totalHeight = 0;
    children.forEach(child => {
      let size = child.getSize();
      if (size.width > maxWidth) {
        maxWidth = size.width;
      }
      totalHeight += size.height;
    });
    let width = _this.startNodeSize.width + maxWidth + _this.endNodeSize.width + _this.horizontalNodeSpace * 4;
    let height = totalHeight + _this.verticalNodeSpace * childrenCount + _this.verticalNodeSpace;
    itemNode.resize(width, height);
  }

  layoutNextNode(startNode, nextNode) {
    console.log('startNode', startNode.getBBox());
    let startBBox = startNode.getBBox();
    let nextNodeSize = nextNode.getSize();
    let nextNodeX = startBBox.x + startBBox.width + this.horizontalNodeSpace;
    let nextNodeY = startBBox.center.y - nextNodeSize.height / 2;
    nextNode.position(nextNodeX, nextNodeY);
  }
}

export default ItemFlowLayout;
