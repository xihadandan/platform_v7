import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';

class SecurityRoleTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '权限管理_角色表格二开',
      hook: {
        delete: '删除',
        defExport: '定义导出',
        defImport: '定义导入'
      }
    };
  }

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
      content: `确定要删除角色[${selectedRows.map(row => row.name)}]吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk() {
        $axios
          .post('/proxy/api/security/role/removeAll', uuids)
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新角色列表
              _this.$widget.refetch(true);
              _this.$widget.$message.success('删除成功');
              _this.$widget.pageContext.emitEvent("role:deleted");
            } else {
              _this.$widget.$message.error(result.msg);
            }
          }).catch(({ response }) => {
            if (response.data && response.data.msg) {
              _this.$widget.$message.error(response.data.msg);
            } else {
              _this.$widget.$message.error('删除失败');
            }
          });
      }
    });
  }


  /**
   * 定义导出
   *
   * @param {*} evt
   */
  defExport(evt) {
    const _this = this;
    let selectedRows = evt.meta.selectedRows || [evt.meta];
    let uuids = selectedRows.map(row => row.uuid);
    if (uuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    let DefDataExportComponent = Vue.extend(ExportDef);
    let defDataExport = new DefDataExportComponent({
      propsData: { title: '定义导出', uuid: uuids, type: 'role' }
    });
    let exportDiv = document.createElement('div');
    exportDiv.classList.add('export-container');
    document.body.appendChild(exportDiv);
    defDataExport.$mount(exportDiv);
    defDataExport.show();
  }

  /**
   * 定义导入
   *
   * @param {*} evt
   */
  defImport(evt) {
    let DefDataImportComponent = Vue.extend(ImportDef);
    let defDataImport = new DefDataImportComponent({ propsData: { title: '定义导入' } });
    let importDiv = document.createElement('div');
    importDiv.classList.add('import-container');
    document.body.appendChild(importDiv);
    defDataImport.$mount(importDiv);
    defDataImport.show();
  }

}

export default SecurityRoleTableDevelopment;
