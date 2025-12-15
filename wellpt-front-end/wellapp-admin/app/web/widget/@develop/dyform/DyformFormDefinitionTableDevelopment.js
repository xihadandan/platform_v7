import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';

class DyformFormDefinitionTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '表单管理_表单定义表格二开',
      hook: {
        addPc: '新增PC单据',
        addMobile: '新增移动单据',
        edit: '编辑表单定义',
        preview: '表单预览',
        copy: '复制表单定义',
        delete: '删除表单定义',
        defExport: '定义导出',
        defImport: '定义导入'
      }
    };
  }

  mounted() {
    this.$widget.pageContext.handleCrossTabEvent(`dyform:design:create`, metadata => {
      this.$widget.refetch(true);
    });

    this.$widget.pageContext.handleEvent('categorySelect_dyform', (selectedKeys, selectedKeysIncludeChildren) => {
      let dataSource = this.$widget.getDataSourceProvider();
      if (selectedKeysIncludeChildren && selectedKeysIncludeChildren.length > 0) {
        dataSource.removeParam('categoryUuids');
        dataSource.addParam('categoryUuids', selectedKeysIncludeChildren);
      } else {
        dataSource.removeParam('categoryUuids');
      }
      this.$widget.refetch(true);
    });
  }

  /**
   * 新增PC单据
   *
   * @param {*} evt
   */
  addPc(evt) {
    window.open(this.addSystemPrefix('/dyform-designer/index'));
  }

  /**
   * 新增移动单据
   *
   * @param {*} evt
   */
  addMobile(evt) {
    window.open(this.addSystemPrefix('/uni-dyform-designer/index'));
  }

  /**
   * 编辑表单定义
   *
   * @param {*} evt
   */
  edit(evt) {
    let url = '/dyform-designer/index?uuid=' + evt.meta.uuid;
    if (evt.meta.formType == 'M') {
      url = '/uni-dyform-designer/index?uuid=' + evt.meta.uuid;
    }
    window.open(this.addSystemPrefix(url));
  }

  /**
   * 表单预览
   *
   * @param {*} evt
   */
  preview(evt) {
    const _this = this;
    let selectedRows = evt.meta.selectedRows || [evt.meta];
    let uuids = selectedRows.map(row => row.uuid);
    if (uuids.length != 1) {
      _this.$widget.$message.error('请选择一条记录！');
      return;
    }

    if (selectedRows[0].formType == 'M') {
      window.open(_this.addSystemPrefix(`/uni-dyform-designer/preview?uuid=${selectedRows[0].uuid}`));
    } else {
      window.open(_this.addSystemPrefix(`/dyform-designer/preview/${selectedRows[0].id}?uuid=${selectedRows[0].uuid}`));
    }
  }

  addSystemPrefix(url) {
    const _this = this;
    if (_this.$widget._$SYSTEM_ID && url && !url.startsWith('/sys/')) {
      url = `/sys/${_this.$widget._$SYSTEM_ID}/_${url}`;
    }
    return url;
  }

  /**
   * 复制表单定义
   *
   * @param {*} evt
   */
  copy(evt) {
    const _this = this;
    let selectedRows = evt.meta.selectedRows || [evt.meta];
    let uuids = selectedRows.map(row => row.uuid);
    if (uuids.length != 1) {
      _this.$widget.$message.error('请选择一条记录！');
      return;
    }

    if (selectedRows[0].formType == 'M') {
      window.open(_this.addSystemPrefix(`/uni-dyform-designer/index?uuid=${selectedRows[0].uuid}&copy=true`));
    } else {
      window.open(_this.addSystemPrefix(`/dyform-designer/index?uuid=${selectedRows[0].uuid}&copy=true`));
    }
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
      content: `确定要删除表单定义[${selectedRows.map(row => row.name)}]吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk() {
        $axios
          .post('/json/data/services', {
            serviceName: 'dyFormFacade',
            methodName: 'dropForm',
            args: JSON.stringify([selectedRows[0].uuid])
          })
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新表单定义列表
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
      propsData: { title: '定义导出', visible: true, uuid: uuids, type: 'formDefinition' }
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

export default DyformFormDefinitionTableDevelopment;
