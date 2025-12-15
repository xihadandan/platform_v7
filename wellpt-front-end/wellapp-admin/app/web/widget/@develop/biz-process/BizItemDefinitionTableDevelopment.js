import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';

class BizItemDefinitionTableDevelopment extends WidgetTableDevelopment {

  get META() {
    return {
      name: '业务事项定义表格二开',
      hook: {
        deleteItemDefinition: '删除业务事项定义',
        defExport: '定义导出',
        defImport: '定义导入'
      }
    };
  }

  deleteItemDefinition(evt) {
    let _this = this;
    let uuids = _this.$widget.getSelectedRowKeys();
    if (uuids.length == 0) {
      _this.$widget.$message.error('请选择记录');
      return;
    }
    evt.$evtWidget.$confirm({
      title: "确认框",
      content: "确认删除业务事项定义？",
      onOk() {
        $axios
          .post(`/proxy/api/biz/item/definition/deleteAll?uuids=${uuids}`)
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
      }
    })
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
      propsData: { title: '定义导出', visible: true, uuid: uuids, type: 'bizItemDefinition' }
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
    let defDataImport = new DefDataImportComponent({ propsData: { title: '定义导入', visible: true } });
    let importDiv = document.createElement('div');
    importDiv.classList.add('import-container');
    document.body.appendChild(importDiv);
    defDataImport.$mount(importDiv);
    defDataImport.show();
  }

}

export default BizItemDefinitionTableDevelopment;
