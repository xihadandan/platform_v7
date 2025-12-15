import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import { generateId } from '@framework/vue/utils/util';

class BizOrgRoleTemplateRoleTableDevelopment extends WidgetTableDevelopment {


  updateOrAppendRow(formData) {

    if (formData.uuid == undefined) {
      this.$widget.rows.push({
        name: formData.name,
        applyTo: formData.applyTo,
        remark: formData.remark,
        allowMemberType: formData.allowMemberType,
        multipleSelectMember: formData.multipleSelectMember,
        id: formData.id,
        uuid: generateId(),
        i18n: formData.i18n
      });
    } else {
      for (let i = 0, len = this.$widget.rows.length; i < len; i++) {
        if (this.$widget.rows[i].uuid === formData.uuid) {
          for (let key of ['name', 'applyTo', 'remark', 'allowMemberType', 'multipleSelectMember', 'id', 'i18n']) {
            this.$widget.rows[i][key] = formData[key];
          }
        }
      }
    }


    // 设置到表单的数据
    let formWidget = this.getVueWidgetById('gsXgMPsnsTdWzKMJisYKMlBiIZheavCV');
    formWidget.form.roleTemplate = JSON.stringify(this.$widget.rows);

  }


  mounted() {
  }



  get META() {
    return {
      name: '业务组织角色模板角色表格二开',
      hook: {
        updateOrAppendRow: '更新数据'
      }
    };
  }
}

export default BizOrgRoleTemplateRoleTableDevelopment;
