import { generateId, deepClone } from '@framework/vue/utils/util';
const createNodes = function (count, nodeStartIndex = 0) {
  let nodes = [];
  for (let i = 0; i < count; i++) {
    let code = generateId('SF');
    nodes.push({ id: 'node_' + code, code, name: `阶段名称${nodeStartIndex + i + 1}` });
  }
  return nodes;
};
const createItems = function (count, itemStartIndex = 0) {
  let items = [];
  for (let i = 0; i < count; i++) {
    items.push({ id: 'item_' + generateId('SF'), name: `事项名称${itemStartIndex + i + 1}` });
  }
  return items;
};
const createNodeTemplate = function ({ count, nodeType = '1', layout, nodeStartIndex = 0, itemStartIndex = 0 }) {
  let nodeTemplate = {
    id: 'group_' + generateId('SF'),
    layout
  };
  if (nodeType == '1') {
    nodeTemplate.nodes = createNodes(count, nodeStartIndex);
  } else {
    nodeTemplate.items = createItems(count, itemStartIndex);
  }
  return nodeTemplate;
};

const getNodeChildren = function (parentNode) {
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
};

const getNodeChildrenSize = function (node) {
  return getNodeChildren(node).length;
};

// 调用vue实现的方法
const invokeVueMethod = function (vm, methodName, ...args) {
  if (!vm) {
    return;
  }
  if (vm[methodName]) {
    vm[methodName].call(vm, ...args);
  } else if (vm.$children) {
    vm.$children.forEach(child => {
      invokeVueMethod(child, methodName, ...args);
    });
  }
};

// 将节点移入画布窗口中
const moveNodeToViewportIfRequired = function (node, graph) {
  let translate = graph.translate();
  let clientSize = graph.transform.getComputedSize();
  let nodeData = node.getData();
  let nodeLayout = nodeData.layout;
  let paddingSize = nodeLayout == 'horizontal' ? 200 : 80;
  let nodeCenter = node.getBBox().getCenter();
  if (nodeCenter.x + translate.tx < 0) {
    // graph.positionCell(node, "center");
    graph.translate(translate.tx - (nodeCenter.x + translate.tx) + paddingSize, translate.ty);
  } else if (nodeCenter.x + translate.tx > clientSize.width) {
    graph.translate(translate.tx - (nodeCenter.x + translate.tx - clientSize.width) - paddingSize, translate.ty);
  } else if (nodeCenter.y + translate.ty < 0) {
    graph.translate(translate.tx, translate.ty - (nodeCenter.y + translate.ty) + paddingSize + 40);
  } else if (nodeCenter.y + translate.ty > clientSize.height - 60) {
    graph.translate(translate.tx, translate.ty - (nodeCenter.y + translate.ty - clientSize.height) - paddingSize - 120);
  }
  // console.log('select', translate, clientSize, nodeCenter);
};

const filterSelectOption = function (inputValue, option) {
  return (
    (option.componentOptions.propsData.value &&
      option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
    option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
  );
};

let cache = {};
let cachePromise = {};
const getCacheData = function (key, promiseFunc) {
  let data = cache[key];
  if (data) {
    return Promise.resolve(data);
  }
  let promise = cachePromise[key];
  if (promise == null) {
    promise = new Promise(promiseFunc);
    cachePromise[key] = promise;
  }
  return promise.then(data => {
    cache[key] = data;
    delete cachePromise[key];
    return data;
  });
};

export {
  createNodeTemplate,
  createNodes,
  createItems,
  getNodeChildren,
  getNodeChildrenSize,
  generateId,
  deepClone,
  invokeVueMethod,
  moveNodeToViewportIfRequired,
  filterSelectOption,
  getCacheData
};
