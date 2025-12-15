import WidgetFormDevelopment from '@develop/WidgetFormDevelopment';

class BizOrgRoleTemplateFormDevelopment extends WidgetFormDevelopment {


  mounted() {
    if (this.$widget.form.roleTemplate) {
      let rows = this.getVueWidgetById('CRJllZWayEsxvjxpKHEqcGgHZMDJeYnq').rows;
      rows.push(...JSON.parse(this.$widget.form.roleTemplate));
    }
  }

  save() {
    this.validateForm().then(({ success, msg }) => {
      if (success) {
        this.getPageContext().emitEvent('CRJllZWayEsxvjxpKHEqcGgHZMDJeYnq:qYbteKtBpfHYopYgIXAVSkRTETBlvCgc', this.$widget.form);
        // 关闭弹窗
        this.getPageContext().emitEvent('esnKUBEDnpuuOZTJnLMUPtBhTnfzlhgP:closeModal');

      }
    })
  }


  get META() {
    return {
      name: '业务组织角色模板表单二开',
      hook: {
        save: '保存'
      }
    };
  }
}

export default BizOrgRoleTemplateFormDevelopment;
