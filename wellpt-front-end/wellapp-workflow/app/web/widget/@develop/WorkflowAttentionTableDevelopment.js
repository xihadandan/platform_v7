import WorkflowTableDevelopmentBase from './WorkflowTableDevelopmentBase';
import { addDbUrl } from '@framework/vue/utils/function';
class WorkflowAttentionTableDevelopment extends WorkflowTableDevelopmentBase {
  get META() {
    return {
      name: '工作流程关注表格二开',
      hook: {
        viewAttention: '查看关注详情',
        unfollow: '取消关注'
      }
    };
  }

  /**
   * 查看关注详情
   *
   * @param {*} evt
   */
  viewAttention(evt) {
    let meta = evt.meta || {};
    let taskInstUuid = meta.taskInstUuid ? meta.taskInstUuid : meta.uuid;
    let flowInstUuid = meta.flowInstUuid;
    let url = `/workflow/work/view/attention?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`;
    url = addDbUrl(evt, url);
    window.open(this.addSystemPrefix(url), '_blank');
  }

  /**
   * 取消关注
   *
   * @param {*} evt
   */
  unfollow(evt) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    _this.showMask('取消关注中...');
    $axios
      .post('/api/workflow/work/unfollow', { taskInstUuids })
      .then(({ data: result }) => {
        _this.hideMask();
        if (result.code == 0) {
          _this.$widget.$message.success('已取消关注!');
          _this.refresh();
        } else {
          _this.handleError(result);
        }
      })
      .catch(err => {
        _this.hideMask();
        _this.handleError(err);
      });
  }
}

export default WorkflowAttentionTableDevelopment;
