import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
// import DefDataImport from '@admin/app/web/lib/def-data-import.vue';
// import DefDataExport from '@admin/app/web/lib/def-data-export.vue';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';

class WfFlowFormatTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '流程管理_信息格式表格二开',
      hook: {
        edit: '编辑信息格式',
        delete: '删除信息格式',
        defExport: '定义导出',
        defImport: '定义导入'
      }
    };
  }

  /**
   * 编辑信息格式
   *
   * @param {*} evt
   * @returns
   */
  edit(evt) {
    let _this = this;
    let formData = evt.meta || {};
    let title = (evt.eventParams && evt.eventParams.title) || '编辑信息格式';

    _this.getPageContext().emitEvent('lCXUuzgWVJQtIgesCmjDHVbQwwCpVWzG:showModal', null, null, title);
    _this.$widget.$nextTick(() => {
      let formWidget = _this.getVueWidgetById('JYxAgsglxoMlHVrwiMmDnYvzohWzPaOA');
      formWidget.setFormData(formData);
    });
  }

  /**
   * 删除信息格式
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
      content: `确定删除信息格式[${selectedRows.map(row => row.name)}]吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk() {
        $axios
          .post(`/api/workflow/format/deleteAll`, {
            uuids
          })
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新信息格式
              _this.$widget.pageContext.emitEvent('VvsNVGDGSDQfqhmiMoRLEKMbzRyRTGnz:refetch');
              _this.$widget.$message.success('删除成功');
            } else {
              _this.$widget.$message.error(result.msg);
            }
          })
          .catch(error => {
            _this.$widget.$message.error('删除失败');
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
      propsData: { title: '定义导出', visible: true, uuid: uuids, type: 'flowFormat' }
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

export default WfFlowFormatTableDevelopment;
