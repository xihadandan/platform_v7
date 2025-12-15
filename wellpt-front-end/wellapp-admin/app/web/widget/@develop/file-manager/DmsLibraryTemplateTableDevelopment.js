import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class DmsLibraryTemplateTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '数据管理_文件库_库模板表格二开',
      hook: {
        delete: '删除库模板',
      }
    };
  }

  /**
   * 删除库模板
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
    }

    _this.$widget.$confirm({
      title: '确认框',
      content: `确定删除库模板[${selectedRows.map(row => row.name)}]吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk() {
        _this.$widget.$loading();
        $axios
          .post(`/proxy/api/dms/library/template/deleteAll?uuids=${uuids}`)
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新库模板列表
              evt.$evtWidget && evt.$evtWidget.refetch && evt.$evtWidget.refetch(true);
              _this.$widget.$message.success('删除成功');
            } else {
              _this.$widget.$message.error(result.msg);
            }
            _this.$widget.$loading(false);
          })
          .catch(({ response }) => {
            _this.$widget.$loading(false);
            _this.$widget.$message.error(
              (response && response.data && response.data.msg) || '删除失败'
            );
          });
      }
    });
  }

}

export default DmsLibraryTemplateTableDevelopment;
