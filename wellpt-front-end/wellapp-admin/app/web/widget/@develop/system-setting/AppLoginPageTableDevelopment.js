import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
// import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
// import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';

class AppLoginPageTableDevelopment extends WidgetTableDevelopment {
  enableLoginPage(e) {
    return new Promise((resolve, reject) => {
      $axios
        .get(`/proxy/api/system/enableLoginPage`, {
          params: {
            uuid: e.UUID,
            enabled: e.ENABLED != 1
          }
        })
        .then(({ data }) => {
          this.clearLoginPageCache();
          resolve(true);
        })
        .catch(error => {});
    });
  }

  clearLoginPageCache() {
    $axios
      .get(`/api/cache/deleteByKey`, {
        params: {
          key: `${this.getSystemID()}:${this.getTenantID()}:systemLoginPagePolicy`
        }
      })
      .then(({ data }) => {})
      .catch(error => {});
  }

  deleteRows(e) {
    console.log(arguments);
    let _this = this,
      selectedRowKeys = e.meta.selectedRowKeys;
    if (selectedRowKeys.length > 0) {
      this.$widget.$confirm({
        title: '确定要删除吗?',
        onOk() {
          _this.$widget.$loading();
          $axios
            .post(`/proxy/api/system/deleteLoginPages`, selectedRowKeys)
            .then(({ data }) => {
              _this.clearLoginPageCache();
              _this.$widget.$loading(false);
              _this.refetch();
            })
            .catch(error => {});
        }
      });
    }
  }

  exportData(e) {
    let _this = this,
      selectedRowKeys = e.meta.selectedRowKeys;
    if (selectedRowKeys.length > 0) {
      import('@pageAssembly/app/web/lib/eximport-def/export-def.vue').then(m => {
        m.default.propsData = {
          uuid: selectedRowKeys,
          type: 'appSystemLoginPageDef',
          title: '导出'
        };
        let inst = _this.getPageContext().newVueComponent(m.default);
        let mounted = _this.getPageContext().mountAsChild(inst, _this.$widget.$el, false);
        mounted.show();
      });
    }
  }

  importData(e) {
    let _this = this;
    import('@pageAssembly/app/web/lib/eximport-def/import-def.vue').then(m => {
      m.default.propsData = {
        filterType: ['appSystemLoginPageDef', 'logicFileInfo'],
        title: '导入'
      };
      let inst = _this.getPageContext().newVueComponent(m.default);
      inst.visible = true;
      _this.getPageContext().mountAsChild(inst, _this.$widget.$el, false);
    });
  }

  copy(e) {
    this.$widget.$loading('复制中...');
    $axios
      .get(`/proxy/api/system/copyLoginPage`, { params: { uuid: e.meta.UUID } })
      .then(({ data }) => {
        this.$widget.$loading(false);
        this.$widget.$confirm({
          title: '复制成功，是否进入配置?',
          onOk() {
            window.open(`${location.origin}/login-design/${data.data}`, '_blank');
          }
        });
        this.refetch();
      })
      .catch(error => {
        this.$widget.$loading(false);
        this.$widget.$message.error('复制失败');
      });
  }

  get META() {
    return {
      name: '登录页表格二开',
      hook: {
        deleteRows: '删除登录页',
        importData: '导入数据',
        exportData: '导出数据',
        copy: '复制'
      }
    };
  }
}
export default AppLoginPageTableDevelopment;
