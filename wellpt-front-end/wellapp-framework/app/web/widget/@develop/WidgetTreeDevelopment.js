import VueWidgetDevelopment from './VueWidgetDevelopment';

class WidgetTreeDevelopment extends VueWidgetDevelopment {
  /**
   * 设置选中的节点
   * @param {节点key值} keys
   * @param {停止传播节点选中事件} stopPropagation
   */
  setSelectedKeys(keys, stopPropagation = false) {
    this.$widget.setSelectedKeys(keys, stopPropagation);
  }

  /**
   * 设置展开的节点
   * @param {节点key值} keys
   */
  setExpandedTreeKeys(keys) {
    this.$widget.setExpandedTreeKeys(keys);
  }

  /**
   * 通过筛选函数返回 true 或者 false 选中节点
   * @param {筛选函数} predicate
   * @param {停止传播节点选中事件} stopPropagation
   */
  setSelectedByPredicate(predicate, stopPropagation = false) {
    this.$widget.setSelectedByPredicate(predicate, stopPropagation);
  }

  /**
   * 通过筛选函数返回 true 或者 false 展开节点
   * @param {筛选函数} predicate
   */
  setExpandedByPredicate(predicate) {
    this.$widget.setExpandedByPredicate(predicate);
  }

  refetch(options) {
    this.$widget.refetch(options);
  }

  /**
   * 加载数据前回调方法
   *
   * @param {*} options
   */
  beforeLoadData(options) {}

  get ROOT_CLASS() {
    return 'WidgetTreeDevelopment';
  }
}

// 扩展表格组件的二开方法
export default WidgetTreeDevelopment;
