import WorkflowTableDevelopmentBase from './WorkflowTableDevelopmentBase';
import { addDbUrl } from '@framework/vue/utils/function';
class WorkflowReadTableDevelopment extends WorkflowTableDevelopmentBase {
  get META() {
    return {
      name: '工作流程已阅表格二开',
      hook: {
        viewRead: '查看已阅详情',
        markUnread: '标记未阅'
      }
    };
  }

  /**
   * 查看关注详情
   *
   * @param {*} evt
   */
  viewRead(evt) {
    let meta = evt.meta || {};
    let taskInstUuid = meta.taskInstUuid ? meta.taskInstUuid : meta.uuid;
    let flowInstUuid = meta.flowInstUuid;
    let url = `/workflow/work/view/read?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`;
    url = addDbUrl(evt, url);
    window.open(this.addSystemPrefix(url), '_blank');
  }

  /**
   * 标记未阅
   *
   * @param {*} evt
   */
  markUnread(evt) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    _this.showMask('标记未阅中...');
    $axios
      .post('/api/workflow/work/markUnread', { taskInstUuids })
      .then(({ data }) => {
        _this.hideMask();
        if (data.code == 0) {
          _this.$widget.$message.success('标记未阅成功!');
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

export default WorkflowReadTableDevelopment;
