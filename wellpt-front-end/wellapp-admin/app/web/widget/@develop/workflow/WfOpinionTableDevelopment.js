import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class WfOpinionTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '流程管理_常用意见库表格二开',
      hook: {
        delete: '删除意见'
      }
    };
  }

  /**
   *
   */
  mounted() {
    const _this = this;
    this.$widget.pageContext.handleEvent('categorySelect_wf_opinion', (selectedKeys, selectedKeysIncludeChildren) => {
      // 表格数据过滤
      let tableWidget = _this.$widget;
      let dataSource = tableWidget.getDataSourceProvider();
      if (selectedKeys.length > 0) {
        dataSource.removeParam('opinionCategoryUuids');
        dataSource.addParam('opinionCategoryUuids', selectedKeysIncludeChildren);
      } else {
        dataSource.removeParam('opinionCategoryUuids');
      }
      tableWidget.refetch(true);
    });
  }

  /**
   * 删除意见校验规则
   *
   * @param {*} evt
   */
  delete(evt) {
    const _this = this;
    let selectedRows = evt.meta.selectedRows || [evt.meta];
    let uuids = selectedRows.map(row => row.uuid);
    if (uuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    } else if (uuids.length > 1) {
      _this.$widget.$message.error('请选择一条记录！');
      return;
    }

    _this.$widget.$confirm({
      title: '确认',
      content: `确定删除意见[${selectedRows.map(row => row.content)}]？`,
      okText: '确定',
      cancelText: '取消',
      onOk() {
        $axios
          .post(`/proxy/api/workflow/opinion/deleteAll?uuids=${uuids}`)
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新信息格式
              _this.$widget.refetch(true);
              _this.$widget.$message.success('删除成功！');
            } else {
              _this.$widget.$message.error(result.msg || '删除失败！');
            }
          })
          .catch(error => {
            _this.$widget.$message.error('删除失败！');
          });
      }
    });
  }
}

export default WfOpinionTableDevelopment;
