import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';

class BotRuleConfigTableDevelopment extends WidgetTableDevelopment {

  get META() {
    return {
      name: '单据转换_单据转换规则表格二开',
      hook: {
        delete: '删除单据转换规则',
        defExport: '定义导出',
        defImport: '定义导入'
      }
    };
  }



  /**
   * 删除表单定义
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
      content: `确定要删除单据转换规则[${selectedRows.map(row => row.ruleName)}]吗？`,
      onOk() {
        $axios
          .post('/json/data/services', {
            serviceName: 'botRuleConfFacadeService',
            methodName: 'deleteRuleConfig',
            args: JSON.stringify([uuids])
          })
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新表单定义列表
              _this.$widget.refetch(true);
              _this.$widget.$message.success('删除成功');
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
      propsData: { title: '定义导出', uuid: uuids, type: 'botRuleConf' }
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

export default BotRuleConfigTableDevelopment;
