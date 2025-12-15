/**
 * 设计器
 * @param {vue 实例}} vueInstance
 * @returns
 */
export function createDesigner(vueInstance) {
  return {
    showDrawer: false,
    selectedNode: { id: undefined, component: undefined, data: {}, title: undefined },
    graph: undefined,
    selectId: undefined,
    namedParameter: {}, // 命名参数
    select(node) {
      this.selectedNode.id = node.id;
      this.selectId = node.id;
      this.selectedNode.component = node.component;
      this.selectedNode.data = node.data;
      this.selectedNode.title = node.title;
    },
    getSelectCell() {
      return this.graph.getCellById(this.selectId);
    }
  };
}
