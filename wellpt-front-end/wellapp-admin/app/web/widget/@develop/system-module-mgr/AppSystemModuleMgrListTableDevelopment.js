import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class AppSystemModuleMgrListTableDevelopment extends WidgetTableDevelopment {


  deleteRows(e) {
    let _this = this;
    let uuid = e.meta.UUID != undefined ? [e.meta.UUID] : e.meta.selectedRowKeys;
    if (uuid.length > 0) {
      this.$widget.$confirm({
        title: '确定要删除吗?',
        onOk() {
          _this.$widget.$loading();
          $axios.post(`/proxy/api/app/module/delete`, uuid).then(({ data }) => {
            _this.$widget.$loading(false);
            _this.$widget.$message.success('删除成功');
            _this.refetch()
          });

        }
      })
    }

  }

  exportData(e) {
    let _this = this, selectedRowKeys = e.meta.selectedRowKeys;
    let uuid = e.meta.UUID != undefined ? [e.meta.UUID] : e.meta.selectedRowKeys;
    if (uuid.length > 0) {
      import('@pageAssembly/app/web/lib/eximport-def/export-def.vue').then(m => {
        m.default.propsData = {
          uuid,
          type: 'appModule',
          title: '导出'
        }
        let inst = _this.getPageContext().newVueComponent(m.default);
        let mounted = _this.getPageContext().mountAsChild(inst, _this.$widget.$el, false);
        mounted.show();
      })
    }
  }

  importData(e) {
    let _this = this;
    import('@pageAssembly/app/web/lib/eximport-def/import-def.vue').then(m => {
      m.default.propsData = {
        filterType: 'appSystemLoginPageDef',
        title: '导入'
      }
      let inst = _this.getPageContext().newVueComponent(m.default);
      inst.visible = true;
      _this.getPageContext().mountAsChild(inst, _this.$widget.$el, false)
    })

  }

  openCreateModuleModal() {
    let _this = this;
    import('@pageAssembly/app/web/page/module-center/component/module-quick-create-select.vue').then(m => {
      m.default.propsData = {
        visible: true,
        afterCreate: function () {
          _this.refetch();
        }
      }
      let inst = _this.getPageContext().newVueComponent(m.default);
      _this.getPageContext().mountAsChild(inst, _this.$widget.$el, false)
    })
  }

  editModuleDetail(e) {
    let _this = this;
    import('@pageAssembly/app/web/page/module-center/component/module-detail-modal.vue').then(m => {
      m.default.propsData = {
        uuid: e.meta.UUID,
        modalVisible: true,
        title: '编辑',
        afterSave: function (d) {
          console.log('保存返回', d);
          // 更新行数据
          let rows = _this.getRows(function (item) {
            return item.UUID == d.uuid
          });
          rows[0].NAME = d.name;
          rows[0].REMARK = d.remark;
          rows[0].ICON = d.icon;

        }
      }
      let inst = _this.getPageContext().newVueComponent(m.default);
      _this.getPageContext().mountAsChild(inst, _this.$widget.$el, false)
    })
  }

  get META() {
    return {
      name: '系统模块表格二开',
      hook: {
        openCreateModuleModal: '打开创建模块弹窗',
        editModuleDetail: '编辑模块基础属性',
        deleteRows: '删除模块',
        importData: '导入数据',
        exportData: '导出模块'
      }
    }
  }
}
export default AppSystemModuleMgrListTableDevelopment;
