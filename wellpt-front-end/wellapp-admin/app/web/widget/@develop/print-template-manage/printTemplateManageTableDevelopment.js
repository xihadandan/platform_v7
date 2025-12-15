import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';

class printTemplateManageTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '打印模板管理表格二开',
      hook: {
        delete: "删除选中项",
        defExport: '定义导出',
        defImport: '定义导入'
      }
    };
  }

  mounted() {
    let dataSourceProvider = this.getWidget().getDataSourceProvider();
    let widget = this.getWidget();
    let _this = this;
    // 监听分类点击事件变化
    widget.pageContext.handleEvent('refetchPrintTemplateManageTable', ({ classifyUuid }) => {
      let criterions = [];
      if (classifyUuid) {
        criterions.push({
          columnIndex: 'category',
          value: classifyUuid,
          type: 'eq'
        });
      }
      let params = {
        criterions: criterions
      };
      widget.refetch(params);
    });
  }


  delete() {
    const ids = _.map(this.$widget.selectedRows, "uuid");
    if (ids.length == 0) {
      this.getWidget().$message.error('请选择记录！')
      return false;
    }
    this.deleteReq(ids);
  }

  deleteItem($event) {
    this.deleteReq([$event.meta.uuid]);
  }

  deleteReq(ids) {
    let _this = this;
    let widget = this.getWidget();
    widget.$confirm({
      title: '确定要删除所选记录吗？',
      onOk() {
        $axios
          .post('/json/data/services', {
            argTypes: [],
            serviceName: 'printTemplateService',
            methodName: 'deleteByUuids',
            args: JSON.stringify([ids]),
            validate: false,
            version: ""
          })
          .then(({ data }) => {
            if (data.code == 0) {
              widget.$message.success('删除成功');
              let options = widget.getDataSourceProvider().options;
              widget.refetch && widget.refetch(options);
            }
          })
      },
      onCancel() { },
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
      propsData: { title: '定义导出', visible: true, uuid: uuids, type: 'printTemplate' }
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

export default printTemplateManageTableDevelopment;
