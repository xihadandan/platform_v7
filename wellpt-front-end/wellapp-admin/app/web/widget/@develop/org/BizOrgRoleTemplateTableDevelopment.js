import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class BizOrgRoleTemplateTableDevelopment extends WidgetTableDevelopment {




  deleteRoleTemplate(e) {
    let uuid = e.meta.UUID;
    $axios.post(`/proxy/api/org/biz/deleteBizOrgRoleTemplate`, [uuid]).then(({ data }) => {
      if (data.code == 0) {
        this.$message.success('删除成功')
        this.refetch();
      } else {
        this.$message.error('删除失败')
      }
    }).catch(error => {
      this.$message.error('删除失败')
    })
  }


  get META() {
    return {

      name: '业务组织角色模板表格二开',
      hook: {
        deleteRoleTemplate: '删除模板'
      }
    };
  }
}

export default BizOrgRoleTemplateTableDevelopment;
