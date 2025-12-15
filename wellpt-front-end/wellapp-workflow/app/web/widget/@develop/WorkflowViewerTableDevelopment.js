import WorkflowTableDevelopmentBase from './WorkflowTableDevelopmentBase';
import { addDbUrl } from '@framework/vue/utils/function';
class WorkflowViewerTableDevelopment extends WorkflowTableDevelopmentBase {
  get META() {
    return {
      name: '工作流程查看表格二开',
      hook: {
        viewWork: '查看流程详情'
      }
    };
  }

  /**
   * 查看工作详情
   *
   * @param {*} evt
   */
  viewWork(evt) {
    let meta = evt.meta || {};
    let taskInstUuid = meta.task_inst_uuid || meta.TASK_INST_UUID || meta.taskInstUuid || meta.uuid || '';
    let flowInstUuid = meta.flow_inst_uuid || meta.FLOW_INST_UUID || meta.flowInstUuid || '';
    let url = `/workflow/work/view/work?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`;
    url = addDbUrl(evt, url);
    window.open(this.addSystemPrefix(url), '_blank');
  }
}

export default WorkflowViewerTableDevelopment;
