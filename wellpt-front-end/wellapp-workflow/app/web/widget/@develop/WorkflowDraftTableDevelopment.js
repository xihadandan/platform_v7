import WorkflowTableDevelopmentBase from './WorkflowTableDevelopmentBase';

class WorkflowDraftTableDevelopment extends WorkflowTableDevelopmentBase {
  get META() {
    return {
      name: '工作流程草稿表格二开',
      hook: {
        viewDraft: '查看草稿详情',
        deleteDraft: '删除'
      }
    };
  }

  /**
   * 查看草稿详情
   *
   * @param {*} evt
   */
  viewDraft(evt) {
    let meta = evt.meta || {};
    let flowInstUuid = meta.flowInstUuid ? meta.flowInstUuid : meta.uuid;
    let url = `/workflow/work/view/draft?flowInstUuid=${flowInstUuid}`;
    window.open(this.addSystemPrefix(url), '_blank');
  }

  /**
   * 删除草稿
   *
   * @param {*} evt
   */
  deleteDraft(evt) {
    let _this = this;
    let flowInstUuids = _this.getSelectedUuids(evt);
    if (flowInstUuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    evt.$evtWidget.$confirm({
      title: '确认框',
      content: '确定删除工作草稿？',
      onOk() {
        _this.showMask('删除中...');
        $axios
          .post(`/api/workflow/work/deleteDraft`, { flowInstUuids })
          .then(({ data: result }) => {
            _this.hideMask();
            if (result.code == 0) {
              // 刷新表格
              _this.$widget.refetch();
              _this.$widget.$message.success('删除成功！');
            } else {
              if (result.data && result.data.data && result.data.data.msg) {
                _this.$widget.$message.error(result.data.data.msg);
              } else {
                _this.$widget.$message.error(result.msg || '删除失败！');
              }
            }
          })
          .catch(error => {
            _this.hideMask();
            _this.$widget.$message.error('删除失败！');
          });
      }
    });
  }
}

export default WorkflowDraftTableDevelopment;
