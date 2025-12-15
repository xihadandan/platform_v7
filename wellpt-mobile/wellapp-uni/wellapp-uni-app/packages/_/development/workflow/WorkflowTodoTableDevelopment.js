import VueWidgetDevelopment from "@develop/VueWidgetDevelopment";
import { isEmpty, find } from "lodash";
import { utils, workFlowUtils } from "wellapp-uni-framework/src";

class WorkflowTodoTableDevelopment extends VueWidgetDevelopment {
  get META() {
    return {
      name: "工作流程待办表格二开",
      hook: {
        viewTodo: "查看待办详情",
      },
      // 将来可能支持的事件参数配置选项
      eventParams: {
        viewTodo: {
          key: "supportsContinuousWorkView",
          value: "false",
          remark: "是否支持进入连续签批true是false否，默认false",
          autoFill: false,
        },
      },
    };
  }

  /**
   * 查看待办详情
   *
   * @param {*} evt
   */
  viewTodo(evt) {
    const _this = this;
    if (!_this.$widget) {
      _this.$widget = evt.ui;
    }
    let meta = evt.meta || {};
    let taskInstUuid = meta.taskInstUuid ? meta.taskInstUuid : meta.uuid;
    let taskIdentityUuid = meta.taskIdentityUuid || "";
    let flowInstUuid = meta.flowInstUuid;
    let url = null;
    let requestCode = _this.setSupportsContinuousWorkViewParamsIfRequired(evt);
    if (!isEmpty(taskIdentityUuid)) {
      url =
        workFlowUtils.pageUrl +
        `?aclRole=TODO&taskInstUuid=${taskInstUuid}&taskIdentityUuid=${taskIdentityUuid}&flowInstUuid=${flowInstUuid}&_requestCode=${requestCode}`;
    } else {
      url =
        workFlowUtils.pageUrl +
        `?aclRole=TODO&taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}&_requestCode=${requestCode}`;
    }
    uni.navigateTo({
      url: url,
    });
  }

  /**
   * 设置支持连续签批的参数
   *
   * @param {*} evt
   */
  setSupportsContinuousWorkViewParamsIfRequired(evt) {
    const _this = this;
    let supportsContinuousWorkView = "false";
    // 是否支持连续签批参数
    if (evt.eventParams) {
      let supportsContinuousWorkViewParams = find(evt.eventParams, (item) => {
        return item.paramKey == "supportsContinuousWorkView";
      });
      if (supportsContinuousWorkViewParams) {
        supportsContinuousWorkView = supportsContinuousWorkViewParams.paramValue;
      }
    }
    let requestCode = utils.generateId();
    if (supportsContinuousWorkView == "true") {
      let dataProvider = _this.$widget.getDataSourceProvider();
      let cwvDataStoreParams = { ...((dataProvider && dataProvider.options) || {}) };
      delete cwvDataStoreParams["receiver"];
      delete cwvDataStoreParams["onDataChange"];
      uni.setStorageSync(`cwvDataStoreParams_${requestCode}`, JSON.stringify(cwvDataStoreParams));
    }
    return requestCode;
  }

  get ROOT_CLASS() {
    return "WorkflowTodoListDevelopment";
  }
}

export default WorkflowTodoTableDevelopment;
