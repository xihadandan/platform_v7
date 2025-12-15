import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class WfOpinionCategoryTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '流程管理_常用意见分类表格二开',
      hook: {
        delete: '删除意见分类'
      }
    };
  }

  /**
   *
   */
  mounted() {
    const _this = this;
    this.$widget.pageContext.handleEvent('categorySelect_wf_opinion_category', (selectedKeys, selectedKeysIncludeChildren) => {
      // 表格数据过滤
      let tableWidget = _this.$widget;
      let dataSource = tableWidget.getDataSourceProvider();
      if (selectedKeys.length > 0) {
        dataSource.removeParam('businessFlag');
        dataSource.addParam('businessFlag', selectedKeysIncludeChildren);
      } else {
        dataSource.removeParam('businessFlag');
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

    $axios.get(`/proxy/api/workflow/opinion/getByOpinionCategory?categoryUuid=${uuids[0]}`).then(({ data: result }) => {
      let content = `确定删除意见分类[${selectedRows.map(row => row.name)}]？`;
      let deleteUrl = `/proxy/api/workflow/opinion/category/delete?categoryUuids=${uuids}`;
      if (result.data.length) {
        content = '分类下存在常用意见，将同时被删除，确认删除？';
        deleteUrl = `/proxy/api/workflow/opinion/categoryAndOpinion/delete?categoryUuid=${uuids[0]}`;
      }

      _this.$widget.$confirm({
        title: '确认',
        content,
        okText: '确定',
        cancelText: '取消',
        onOk() {
          $axios
            .post(deleteUrl)
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
    });
  }
}

export default WfOpinionCategoryTableDevelopment;
