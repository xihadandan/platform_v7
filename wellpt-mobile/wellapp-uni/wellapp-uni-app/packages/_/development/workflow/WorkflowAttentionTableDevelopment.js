import WorkflowTableDevelopmentBase from "./WorkflowTableDevelopmentBase";
import { utils, workFlowUtils } from "wellapp-uni-framework/src";
// import { addDbUrl } from '@framework/vue/utils/function';
class WorkflowAttentionTableDevelopment extends WorkflowTableDevelopmentBase {
  get META() {
    return {
      name: "工作流程关注表格二开",
      hook: {
        viewAttention: "查看关注详情",
        unfollow: "取消关注",
      },
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
    let url = `/packages/_/pages/workflow/work_view?aclRole=ATTENTION&taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`;
    // url = addDbUrl(evt, url);
    uni.navigateTo({ url });
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
      uni.showToast({ title: "请选择记录！", icon: "error" });
      return;
    }

    _this.showMask("取消关注中...");
    uni.$axios
      .post("/api/workflow/work/unfollow", { taskInstUuids })
      .then(({ data: result }) => {
        _this.hideMask();
        if (result.code == 0) {
          uni.showToast({ title: "已取消关注!" });
          _this.refresh();
        } else {
          _this.handleError(result);
        }
      })
      .catch((err) => {
        _this.hideMask();
        _this.handleError(err);
      });
  }
}

export default WorkflowAttentionTableDevelopment;
