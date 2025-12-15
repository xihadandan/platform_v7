import WorkflowTableDevelopmentBase from './WorkflowTableDevelopmentBase';
import { addDbUrl } from '@framework/vue/utils/function';
class WorkflowSuperviseTableDevelopment extends WorkflowTableDevelopmentBase {
  get META() {
    return {
      name: '工作流程督办表格二开',
      hook: {
        viewSupervise: '查看督办详情',
        remind: '催办'
      }
    };
  }

  /**
   * 查看督办详情
   *
   * @param {*} evt
   */
  viewSupervise(evt) {
    let meta = evt.meta || {};
    let taskInstUuid = meta.taskInstUuid ? meta.taskInstUuid : meta.uuid;
    let flowInstUuid = meta.flowInstUuid;
    let url = `/workflow/work/view/supervise?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`;
    url = addDbUrl(evt, url);
    window.open(this.addSystemPrefix(url), '_blank');
  }
}

export default WorkflowSuperviseTableDevelopment;
