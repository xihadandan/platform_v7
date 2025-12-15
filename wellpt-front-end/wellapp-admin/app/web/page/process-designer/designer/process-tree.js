import { debounce } from 'lodash';

class ProcessTree {
  constructor() {
    this.treeData = [];
  }

  getTreeData() {
    return this.treeData;
  }

  getTreeDataList(type = '') {
    let dataList = [];
    this.traverseTree(null, this.treeData, dataList);
    return type ? dataList.filter(item => item.data && item.data.type == type) : dataList;
  }

  getTreeDataCount(type = '') {
    return this.getTreeDataList(type).length;
  }

  getTreeNodeByKey(key) {
    let treeNode = null;
    let findTreeNode = nodes => {
      if (treeNode) {
        return;
      }
      nodes.forEach(node => {
        if (node.key == key) {
          treeNode = node;
          return;
        }
        findTreeNode(node.children || []);
      });
    };
    findTreeNode(this.treeData);
    return treeNode;
  }

  traverseTree(parentNode, children, dataList) {
    children.forEach(treeNode => {
      dataList.push({
        title: treeNode.title,
        key: treeNode.key,
        data: treeNode.data,
        parentId: parentNode == null ? '-1' : parentNode.id
      });
      if (treeNode.children) {
        this.traverseTree(treeNode, treeNode.children, dataList);
      }
    });
  }

  getParentNodeKeysByKey(key) {
    const _this = this;
    let parentKeys = [];
    let treeData = _this.treeData;
    let match = false;
    let extractParentKeys = nodes => {
      if (match) {
        return;
      }

      nodes.forEach(node => {
        if (match) {
          return;
        }

        if (node.key == key) {
          match = true;
          return;
        }

        parentKeys.push(node.key);
        if (node.children && node.children.length) {
          extractParentKeys(node.children);
        }

        if (match) {
          return;
        }
        parentKeys.pop();
      });
    };

    extractParentKeys(treeData);
    return parentKeys;
  }

  // 更新根节点名称
  updateRootNodeTitle(title) {
    if (this.treeData && this.treeData[0]) {
      this.treeData[0].title = title;
    }
  }

  // 从图形对象更新树
  updateFromGraph(graph) {
    this.updateFromGraphData(graph.toJSON());
  }

  // 从图形数据更新树
  updateFromGraphData(graphJson) {
    const _this = this;
    let cellMap = new Map();
    let cells = graphJson.cells || [];
    if (cells.length == 0) {
      _this.treeData = [];
      return;
    }

    let rootCell = null;
    cells.forEach(cell => {
      cellMap.set(cell.id, cell);
      if (cell.shape == 'rect' && cell.data && cell.data.parentId == '-1') {
        rootCell = cell;
      }
    });

    let rootNode = {
      title: rootCell.label || rootCell.data.name,
      key: rootCell.id,
      data: rootCell.data,
      children: [],
      selectable: false, //第一层节点不可选
    };
    _this.parseGroupCell(rootNode, rootCell, cellMap);
    _this.treeData = [rootNode];
    // console.log("updateFromGraphJson", this.tree, this.getTreeDataList());
  }

  // 解析群组节点
  parseGroupCell(rootNode, groupCell, cellMap) {
    const _this = this;
    if (groupCell.children) {
      groupCell.children.forEach(childId => {
        _this.parseChildCell(rootNode, childId, cellMap);
      });
    }
  }

  // 解析群组下的节点
  parseChildCell(parentNode, childId, cellMap) {
    const _this = this;
    let cell = cellMap.get(childId);
    if (cell == null || cell.shape == 'edge') {
      return;
    }

    let cellData = cell.data || {};
    let title = cell.label;
    if (cellData.type == 'item' && cellData.configuration) {
      title = cellData.configuration.itemName;
    } else if (cellData.type == 'node' && cellData.configuration) {
      title = cellData.configuration.name;
    }
    // 子节点
    let childNode = {
      title,
      key: cell.id,
      data: cellData,
      children: []
    };
    parentNode.children.push(childNode);

    // 子阶段
    if (cellData.childOfProcessGroupId) {
      let processGroupCell = cellMap.get(cellData.childOfProcessGroupId);
      if (processGroupCell) {
        _this.parseGroupCell(childNode, processGroupCell, cellMap);
      }
    }
    // 阶段下的事项
    if (cellData.childOfItemGroupId) {
      let itemGroupCell = cellMap.get(cellData.childOfItemGroupId);
      if (itemGroupCell) {
        _this.parseGroupCell(childNode, itemGroupCell, cellMap);
      }
    }
  }

  // 从业务流程定义更新树
  updateFromProcessDefinition(json) { }
}

export default ProcessTree;
