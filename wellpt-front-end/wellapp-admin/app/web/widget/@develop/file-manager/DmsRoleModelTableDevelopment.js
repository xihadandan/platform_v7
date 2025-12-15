import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class DmsRoleModelTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '数据管理_文件库_权限模型表格二开',
      hook: {
        delete: '删除权限模型',
      }
    };
  }

  /**
   * 删除权限模型
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
      content: `确定删除权限模型[${selectedRows.map(row => row.name)}]吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk() {
        $axios
          .post(`/proxy/api/dms/role/model/deleteAll?uuids=${uuids}`)
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新库分类
              evt.$evtWidget && evt.$evtWidget.refetch && evt.$evtWidget.refetch(true);
              _this.$widget.$message.success('删除成功');
            } else {
              _this.$widget.$message.error(result.msg);
            }
          })
          .catch(({ response }) => {
            _this.$widget.$message.error(
              (response && response.data && response.data.msg) || '删除失败'
            );
          });
      }
    });
  }

}

export default DmsRoleModelTableDevelopment;
