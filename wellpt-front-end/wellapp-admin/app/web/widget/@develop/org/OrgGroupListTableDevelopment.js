import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class OrgGroupListTableDevelopment extends WidgetTableDevelopment {
  deleteOrgGroup(evt) {
    let _this = this,
      selectedRowKeys = evt.meta.selectedRowKeys;
    if (selectedRowKeys && selectedRowKeys.length != 0) {
      _this.$widget.$confirm({
        title: '提示',
        content: '确认要删除群组吗?',
        onOk() {
          let uuid = selectedRowKeys[0];
          $axios
            .get('/proxy/api/org/organization/orgGroup/delete', { params: { uuid } })
            .then(({ data }) => {
              if (data.code == 0) {
                // 刷新表格
                _this.$widget.refetch();
                _this.$widget.$message.success('删除成功');
              }
            })
            .catch(error => {
              _this.$widget.$message.error('删除失败');
            });
        },
        onCancel() { }
      });
    }
  }

  get META() {
    return {
      name: '组织群组列表二开',
      hook: { deleteOrgGroup: '删除组织群组' }
    }
  }
}



export default OrgGroupListTableDevelopment;
