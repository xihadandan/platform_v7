import WorkflowTableDevelopmentBase from './WorkflowTableDevelopmentBase';
import { addDbUrl } from '@framework/vue/utils/function';
class WorkflowUnreadTableDevelopment extends WorkflowTableDevelopmentBase {
  get META() {
    return {
      name: '工作流程未阅表格二开',
      hook: {
        viewUnread: '查看未阅详情',
        markRead: '标记已阅'
      }
    };
  }

  /**
   * 查看关注详情
   *
   * @param {*} evt
   */
  viewUnread(evt) {
    let meta = evt.meta || {};
    let taskInstUuid = meta.taskInstUuid ? meta.taskInstUuid : meta.uuid;
    let flowInstUuid = meta.flowInstUuid;
    let url = `/workflow/work/view/read?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`;
    url = addDbUrl(evt, url);
    window.open(this.addSystemPrefix(url), '_blank');
  }

  /**
   * 标记已阅
   *
   * @param {*} evt
   */
  markRead(evt) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    _this.showMask('标记已阅中...');
    $axios
      .post('/api/workflow/work/markRead', { taskInstUuids })
      .then(({ data }) => {
        _this.hideMask();
        if (data.code == 0) {
          _this.$widget.$message.success('标记已阅成功!');
          _this.refresh();
        } else {
          _this.handleError(data);
        }
      })
      .catch(err => {
        _this.hideMask();
        _this.handleError(err);
      });
  }
}

export default WorkflowUnreadTableDevelopment;
