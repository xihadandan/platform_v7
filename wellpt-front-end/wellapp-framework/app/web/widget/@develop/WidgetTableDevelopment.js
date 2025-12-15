import VueWidgetDevelopment from './VueWidgetDevelopment';

class WidgetTableDevelopment extends VueWidgetDevelopment {
  constructor($widget) {
    super($widget);
  }

  showLoading(tip) {
    this.$widget.loading = {
      spinning: true,
      tip
    };
  }

  hideLoading() {
    this.$widget.loading = false;
  }

  /**
   * 重新加载数据
   * @param {加载选项} options
   *
   */
  refetch(options) {
    this.$widget.refetch(options);
  }

  /**
   * 根据主键值删除表格行
   * @param {主键值集合} keys
   */
  deleteRowsByKeys(keys) {
    this.$widget.deleteRowsByKeys(keys);
  }

  /**
   * 添加数据查询参数
   * @param {参数对象} params
   */
  addDataSourceParams(params) {
    this.$widget.addDataSourceParams(params);
  }

  /**
   * 清空数据查询参数对象
   */
  clearDataSourceParams() {
    this.$widget.clearDataSourceParams();
  }

  getRows(filter) {
    let _row = filter == undefined ? this.$widget.rows : [];
    if (typeof filter == 'function') {
      for (let i = 0, len = this.$widget.rows.length; i < len; i++) {
        if (filter(this.$widget.rows[i])) {
          _row.push(this.$widget.rows[i])
        }
      }
    }
    return _row;
  }

  /**
   * 根据参数键删除参数对象
   * @param  {...参数键} key
   */
  deleteDataSourceParams() {
    let keys = [];
    if (arguments.length == 0 && Array.isArray(arguments[0])) {
      keys = arguments[0];
    } else {
      keys = Array.from(arguments);
    }
    this.$widget.deleteDataSourceParams.apply(this.$widget, keys);
  }

  /**
   * 清空指定行标的行样式
   * @param {行标} index
   */
  clearRowStyle(index) {
    this.$widget.clearRowStyle(index);
  }
  /**
   * 设置指定行标的行样式
   * @param {航标} index
   * @param {样式} style
   */
  setRowStyle(index, style) {
    this.$widget.setRowStyle(index, style);
  }

  /**
   * 加载数据前回调方法
   *
   * @param {*} params
   */
  beforeLoadData(params) { }

  get ROOT_CLASS() {
    return 'WidgetTableDevelopment';
  }
}

export default WidgetTableDevelopment;
