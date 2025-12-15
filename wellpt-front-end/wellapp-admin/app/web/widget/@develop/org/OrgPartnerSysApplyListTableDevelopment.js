import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class OrgPartnerSysApplyListTableDevelopment extends WidgetTableDevelopment {

  mounted() {
    let dataSourceProvider = this.getWidget().getDataSourceProvider();
    let widget = this.getWidget();
    let _this = this;

    // 监听组织版本UUID变化
    widget.pageContext.handleEvent('refetchOrgPartnerSysApplyTable', ({ categoryUuid }) => {
      dataSourceProvider.clearParams();
      if (categoryUuid != '-1') {
        dataSourceProvider.addParam('categoryUuid', categoryUuid);
      }
      widget.refetch();
    });
  }

  stopRelation(evt) {
    this.updateState(evt.meta.uuid, 'TEMINATED');
  }

  reAdd(evt) { }


  agree(evt) {
    this.updateState(evt.meta.uuid, 'PASSED');
  }

  disagree(evt) {
    this.updateState(evt.meta.uuid, 'REJECTED');
  }

  updateState(uuid, state) {
    let _this = this;
    $axios
      .get('/proxy/api/org/organization/orgPartnerSysApply/updateState', { params: { uuid, state } })
      .then(({ data }) => {
        if (data.code == 0) {
          // 刷新表格
          _this.$widget.refetch();
          _this.$widget.$message.success('操作成功');
        }
      })
      .catch(error => {
        _this.$widget.$message.error('操作失败');
      });
  }

  get META() {
    return {
      name: '协作系统申请管理列表二开',
      hook: {
        disagree: '驳回',
        agree: '同意',
        reAdd: '重新添加',
        stopRelation: '终止协作'
      }
    };
  }
}


export default OrgPartnerSysApplyListTableDevelopment;
