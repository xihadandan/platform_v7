<template>
  <HtmlWrapper :title="title">
    <WorkView
      ref="workView"
      v-if="workBean"
      :workData="workBean"
      :settings="settings"
      @actionSuccess="onActionSuccess"
      @setTitle="onSetTitle"
    ></WorkView>
    <a-empty v-else description="流程单据加载失败！"></a-empty>
  </HtmlWrapper>
</template>
<script type="text/babel">
import '@dyform/app/web/framework/vue/install';
import '@installPageWidget';
import '@workflow/app/web/framework/vue/install';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件
export default {
  provide() {
    return {
      designMode: false,
      continuousMode: false
    };
  },
  data() {
    return {
      title: ''
    };
  },
  methods: {
    onSetTitle(title) {
      this.title = title;
    },
    // 刷新父窗口
    refreshParent(options) {
      try {
        // if (window.opener && window.opener.location && window.opener.location.reload) {
        //   window.opener.location.reload();
        // }
        if (window.opener == null) {
          options && options.message && this.$message.success(options && options.message);
        } else {
          this.pageContext.emitCrossTabEvent('workflow:detail:change', options || {});
        }
      } catch (e) {
        console.error(e);
      }
    },
    // 刷新父窗口并关闭当前窗口
    refreshParentWithMessageAndClose(options) {
      const _self = this;
      // 刷新父窗口，父窗口提交成功信息
      _self.refreshParent(options);
      // _self.$message.success(options.message);
      // 关闭当前窗口
      // setTimeout(() => {
      _self.close();
      // }, 2000);
    },
    showResultMessage(options) {
      this.$message.success(options.message);
    },
    showResultMessageAndReload(options) {
      // 提示信息，2秒后重新加载
      this.$message.success(options.message, 2, () => {
        window.location.reload();
      });
    },
    // 关闭当前窗口
    close() {
      try {
        if (window.self !== window.top) {
          const timer = setTimeout(() => {
            this.$refs.workView.workView.refreshCurrentWork();
            clearTimeout(timer);
          }, 1000);
          return;
        }
        if (window.opener == null) {
          setTimeout(() => {
            if (navigator.userAgent.indexOf('Firefox') != -1 || navigator.userAgent.indexOf('Chrome') != -1) {
              window.location.href = 'about:blank';
            } else {
              window.open('', '_self');
            }
            window.close();
          }, 2000);
        } else {
          window.close();
        }
      } catch (e) {
        console.error(e);
      }
    },
    refreshParentAndIframe(options) {
      this.refreshParent(options);

      if (window.self !== window.top) {
        let { workView } = options;
        if (!workView) {
          workView = this.$refs.workView.workView;
        }
        const timer = setTimeout(() => {
          workView.refreshCurrentWork();
          clearTimeout(timer);
        }, 2000);
      }
    },
    // 操作成功
    onActionSuccess(options) {
      const _self = this;
      let action = options.action;
      switch (action) {
        case 'save': // 保存
          _self.onSaveSuccess(options);
          break;
        case 'submit': // 提交
          _self.onSubmitSuccess(options);
          break;
        case 'cancel': // 撤回
          _self.refreshParent();
          break;
        case 'rollback': // 退回
          _self.onRollbackSuccess(options);
          break;
        case 'directRollback': // 直接退回
        case 'rollbackToMainFlow': // 退回主流程
        case 'transfer': // 转办
        case 'counterSign': // 会签
        case 'addSign': // 加签
        case 'remove': // 删除
        case 'recover': // 恢复
          if (action === 'counterSign' || action === 'transfer') {
            _self.refreshParentAndIframe(options);
          } else {
            _self.refreshParentWithMessageAndClose(options);
          }
          break;
        case 'copyTo': // 抄送
        case 'print': // 套打
        case 'remind': // 催办
          _self.showResultMessage(options);
          break;
        case 'attention': // 关注
        case 'unfollow': // 取消关注
        case 'handOver': // 特送个人
          _self.showResultMessageAndReload(options);
          break;
        case 'gotoTask': // 特送环节
          _self.refreshParentWithMessageAndClose(options);
          break;
        case 'suspend': // 挂起
        case 'resume': // 恢复
          _self.showResultMessageAndReload(options);
          break;
        case 'subflowAddSubflow': // 子流程添加承办
        case 'subflowAddMajorFlow': // 子流程添加主办
        case 'subflowAddMinorFlow': // 子流程添加协办
        case 'subflowRemind': // 子流程催办
        case 'subflowSendMessage': // 子流程信息分发
        case 'subflowLimitTime': // 子流程协办时限
        case 'subflowRedo': // 子流程重办
          _self.showResultMessageAndReload(options);
          break;
        case 'subflowStop': // 子流程终止
          _self.onSubflowStopSuccess(options);
          break;
        default:
          _self.showResultMessage(options);
      }
    },
    // 保存成功
    onSaveSuccess(options) {
      const _self = this;
      // 提示信息
      _self.$message.success(options.message, 2, () => {
        _self.$refs.workView.workView.reload();
      });
      this.refreshParent();
    },
    // 提交成功
    onSubmitSuccess(options) {
      const _self = this;
      let result = options.result;
      // 与前环节相同自动提交
      let sameUserSubmitInfo = _self.$refs.workView.getSameUserSubmitInfo(result);
      if (sameUserSubmitInfo != null) {
        // 刷新父窗口
        _self.refreshParent();
        _self.handleSameUserSubmitInfo(sameUserSubmitInfo);
        // 提示信息
        _self.$message.success(options.message);
      } else {
        // 父窗口提交成功信息
        _self.refreshParent(options);
        // _self.$message.success(options.message);
        // 关闭当前窗口
        if (result && result.data && result.data.downloadFile) {
          setTimeout(() => {
            _self.close();
          }, 1000);
        } else {
          _self.close();
        }
      }
    },
    handleSameUserSubmitInfo(sameUserSubmitInfo) {
      const _self = this;
      let sameUserSubmitType = sameUserSubmitInfo.sameUserSubmitType ? sameUserSubmitInfo.sameUserSubmitType : '0';
      let refreshUrl = sameUserSubmitInfo.refreshUrl;
      let keepOnCurrentPage = false;
      switch (sameUserSubmitType) {
        case '0': // 自动提交，让办理人确认是否继承上一环节意见
        case '1': // 自动提交，且自动继承意见
          keepOnCurrentPage = true;
          break;
        case '2': // 不自动提交并关闭页面
          break;
        case '3': // 不自动提交并刷新页面
          keepOnCurrentPage = true;
          break;
        case '4': // 不自动提交并刷新页面，且不自动继承意见
          keepOnCurrentPage = true;
          refreshUrl = refreshUrl.replace('&auto_submit=true', '').replace('&sameUserSubmitTaskOperationUuid=', '&optUuid=');
          break;
      }

      // 与前环节相同自动提交
      setTimeout(() => {
        if (keepOnCurrentPage) {
          if (
            _self.settings &&
            _self.settings.GENERAL &&
            _self.settings.GENERAL.enabledContinuousWorkView &&
            _self.settings.GENERAL.defaultContinuousWorkView &&
            refreshUrl &&
            refreshUrl.indexOf('continuousMode=') == -1
          ) {
            window.location.href = refreshUrl + '&continuousMode=0';
          } else {
            window.location.href = refreshUrl;
          }
        } else {
          _self.close();
        }
      }, 2000);

      return true;
    },
    // 退回成功
    onRollbackSuccess(options) {
      const _self = this;
      let result = options.result;
      // 刷新父窗口
      _self.refreshParent();
      // 退回到自己
      let rollbackToSelfInfo = _self.$refs.workView.getRollbackToSelfInfo(result);
      if (rollbackToSelfInfo && rollbackToSelfInfo.rollbackToSelf) {
        _self.rollbackToSelf(rollbackToSelfInfo);
        // 提示信息
        _self.$message.success(options.message);
      } else {
        // 父窗口提交成功信息
        _self.refreshParent(options);
        // _self.$message.success(options.message);
        // 关闭当前窗口
        // setTimeout(() => {
        _self.close();
        // }, 2000);
      }
    },
    rollbackToSelf(rollbackToSelfInfo) {
      let refreshUrl = `/workflow/work/view/todo?taskInstUuid=${rollbackToSelfInfo.taskInstUuid}&flowInstUuid=${rollbackToSelfInfo.flowInstUuid}`;
      setTimeout(() => {
        window.location.href = this.$refs.workView.workView.addSystemPrefix(refreshUrl);
      }, 2000);
    },
    // 子流程终止成功处理
    onSubflowStopSuccess(options) {
      const _self = this;
      let stopSelf = options.stopSelf;
      if (stopSelf === true) {
        _self.refreshParentWithMessageAndClose(options);
      } else {
        _self.showResultMessageAndReload(options);
      }
    }
  }
};
</script>
<style scoped></style>
