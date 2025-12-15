import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';

class TimerConfigTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '基础数据管理_计时服务配置表格二开',
      hook: {
        delete: '删除',
        defExport: '定义导出',
        defImport: '定义导入'
      }
    };
  }

  mounted() {
    this.$widget.pageContext.handleEvent('categorySelect_timer', (selectedKeys, selectedKeysIncludeChildren) => {
      let params = { criterions: [] };
      if (selectedKeys && selectedKeys.length > 0) {
        params.criterions.push({ columnIndex: 'categoryUuid', type: 'in', value: selectedKeys });
      }
      this.$widget.refetch(params);
    });
  }

  /**
   * 删除计时服务配置
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
      content: `确定要删除计时服务配置[${selectedRows.map(row => row.name)}]吗？`,
      onOk() {
        $axios
          .post(`/proxy/api/ts/timer/config/deleteAll?uuids=${uuids}`)
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新计时服务配置列表
              _this.$widget.refetch(true);
              _this.$widget.$message.success('删除成功');
            } else {
              _this.$widget.$message.error(result.msg);
            }
          })
          .catch(({ response }) => {
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
      propsData: { title: '定义导出', visible: true, uuid: uuids, type: 'tsTimerConfig' }
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

export default TimerConfigTableDevelopment;
