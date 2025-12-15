import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class OrgListTableDevelopment extends WidgetTableDevelopment {
  beforeMount() {
    this.$widget.addDataSourceParams({
      system: this.$widget._$SYSTEM_ID
    });
  }
  deleteOrg(evt) {
    let _this = this,
      uuid = evt.meta.uuid;
    if (uuid) {
      if (evt.meta.isDefault) {
        _this.$widget.$message.error('默认组织无法删除');
        return false;
      }
      this.$widget.$confirm({
        title: '提示',
        content: '确认要删除吗?',
        onOk() {
          _this.$widget.$loading('删除中');
          $axios
            .get('/proxy/api/org/organization/delete', { params: { uuid } })
            .then(({ data }) => {
              _this.$widget.$loading(false);
              if (data.code == 0) {
                // 刷新表格
                _this.$widget.refetch();
                _this.$widget.$message.success('删除成功');
              }
            })
            .catch(error => {
              _this.$widget.$loading(false);
              _this.$widget.$message.error('删除失败');
            });
        },
        onCancel() {}
      });
    }
  }

  deleteOrgVersion(evt) {
    let _this = this,
      uuid = evt.meta.uuid;
    this.$widget.$confirm({
      title: '确认要删除设计版吗?',
      content: undefined,
      onOk() {
        _this.$widget.$loading('删除中');
        $axios
          .get('/proxy/api/org/organization/version/delete', { params: { uuid } })
          .then(({ data }) => {
            _this.$widget.$loading(false);
            if (data.code == 0) {
              evt.$evtWidget.refetch();
              _this.$widget.$message.success('删除成功');
              _this.$widget.pageContext.emitEvent('OrgVersionChange', undefined);
            }
          })
          .catch(error => {
            _this.$widget.$loading(false);
            _this.$widget.$message.error('删除失败');
          });
      },
      onCancel() {}
    });
  }

  enable(row, checked, success) {
    let _this = this,
      enable = checked;
    if (row.isDefault && !enable) {
      row.enable = !enable;
      _this.$widget.$message.error('默认组织无法停用！');
      return;
    }

    $axios
      .get('/proxy/api/org/organization/enable', { params: { uuid: row.uuid, enable } })
      .then(({ data }) => {
        success(data.code === 0);
      })
      .catch(error => {
        success(false);
      });
  }

  setDefault(evt, success) {
    let _this = this,
      row = evt.meta;
    $axios
      .get('/proxy/api/org/organization/setDefault', { params: { uuid: row.uuid } })
      .then(({ data }) => {
        if (data.code == 0) {
          // 刷新表格
          _this.$widget.refetch();
          _this.$widget.$message.success('设为默认成功');
        }
      })
      .catch(error => {
        _this.$widget.$message.error('设为默认失败');
      });
  }

  get META() {
    return {
      name: '组织列表二开',
      hook: {
        deleteOrg: '删除组织',
        deleteOrgVersion: '删除组织版本',
        enable: '启用或停用组织',
        setDefault: '设置未默认组织'
      }
    };
  }
}

export default OrgListTableDevelopment;
