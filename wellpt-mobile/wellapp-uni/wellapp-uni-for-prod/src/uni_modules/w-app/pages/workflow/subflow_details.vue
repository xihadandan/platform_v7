<template>
  <view :style="theme" class="subflow-details">
    <uni-nav-bar
      :statusBar="true"
      :fixed="true"
      left-icon="left"
      :title="navBarTitle"
      @clickLeft="goBack"
    ></uni-nav-bar>
    <scroll-view style="height: 100%" scroll-y="true">
      <uni-list>
        <uni-list-item title="承办部门" :note="item.todoName"></uni-list-item>
        <uni-list-item title="当前环节" :note="item.currentTaskName"></uni-list-item>
        <uni-list-item title="当前环节办理人" :note="item.currentTodoUserName"></uni-list-item>
        <uni-list-item title="剩余时限" :note="item.dueTime"></uni-list-item>
        <uni-list-item title="办理时限" :note="item.remainingTime"></uni-list-item>
        <uni-list-item title="办理过程">
          <template slot="body">
            <view class="uni-list-item__content">
              <text class="uni-list-item__content-title">办理过程</text>
              <view class="uni-list-item__content-note">
                <view class="work-process-item" v-for="(item, index) in workProcess" :key="index">
                  <view class="task-name">
                    <text>{{ item.taskName }}</text>
                    <text>(</text>
                    <text>{{ item.assignee }}</text>
                    <text>{{ item.endTime }}</text>
                    <text>)</text>
                  </view>
                  <view class="opinion">{{ item.opinion }}</view>
                </view>
              </view>
            </view>
          </template>
        </uni-list-item>
        <uni-list-item title="附件">
          <template slot="body">
            <view class="uni-list-item__content">
              <text class="uni-list-item__content-title">附件</text>
              <view class="uni-list-item__content-note">
                <view v-if="item.resultFiles">
                  <view v-for="file in item.resultFiles" :key="file.fileID">
                    <view>{{ file.fileName }}</view>
                  </view>
                </view>
                <view v-else>无</view>
              </view>
            </view>
          </template>
        </uni-list-item>
      </uni-list>
    </scroll-view>

    <view class="footer">
      <view class="uni-flex uni-row">
        <view v-for="(button, index) in buttons" :key="index" class="text" style="-webkit-flex: 1; flex: 1">
          <button type="primary" plain="true" @tap="onButtonTap($event, button)">{{ button.name }}</button>
        </view>
      </view>
    </view>

    <!-- 催办 -->
    <uni-popup ref="remindPopup" background-color="#fff">
      <view class="popup-content" style="height: 300px">
        <w-section title="催办" type="line">
          <view style="height: 200px">
            <uni-forms-item label="催办内容" labelPosition="top">
              <uni-easyinput type="textarea" v-model="remindContent" placeholder="请输入" />
            </uni-forms-item>
          </view>
        </w-section>
        <view class="uni-flex uni-row">
          <view class="text" style="-webkit-flex: 1; flex: 1">
            <button type="default" plain="true" @tap="$refs.remindPopup.close()">取消</button>
          </view>
          <view class="text" style="-webkit-flex: 1; flex: 1">
            <button type="primary" plain="true" @tap="onRemindConfirmOk">确定</button>
          </view>
        </view>
      </view>
    </uni-popup>

    <!-- 协办时限 -->
    <uni-popup ref="limitTimePopup" background-color="#fff">
      <view class="popup-content" style="height: 300px">
        <w-section title="协办时限" type="line">
          <view style="height: 200px">
            <uni-forms-item label="反馈时限" labelPosition="top">
              <uni-datetime-picker
                type="date"
                :start="new Date().getTime()"
                v-model="subflowDueTime"
                placeholder="请选择"
              />
            </uni-forms-item>
          </view>
        </w-section>
        <view class="uni-flex uni-row">
          <view class="text" style="-webkit-flex: 1; flex: 1">
            <button type="default" plain="true" @tap="$refs.limitTimePopup.close()">取消</button>
          </view>
          <view class="text" style="-webkit-flex: 1; flex: 1">
            <button type="primary" plain="true" @tap="onLimitTimeConfirmOk">确定</button>
          </view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { each as forEach, findIndex, isEmpty, trim as stringTrim } from "lodash";
const btnIds = ["remind", "limit-time", "redo", "stop"];
export default {
  data() {
    return {
      navBarTitle: "",
      item: null,
      workProcess: [], // 办理过程
      buttons: [], // 操作按钮
      remindContent: "", // 催办内容
      subflowDueTime: new Date(), // 反馈时限
    };
  },
  onLoad() {
    const _self = this;
    _self.workView = _self.getPageParameter("workView");
    _self.subTaskData = _self.getPageParameter("subTaskData");
    _self.shareData = _self.getPageParameter("shareData");
    _self.item = _self.getPageParameter("item");
    _self.navBarTitle = _self.item.todoName;
    // 操作按钮
    let btns = [];
    forEach(_self.shareData.buttons, function (button) {
      let btnIdIndex = findIndex(btnIds, function (btnId) {
        return btnId == button.id;
      });
      if (btnIdIndex != -1) {
        btns.push(button);
      }
    });
    _self.buttons = btns;
    // 加载办理过程
    _self.loadWorkProcess(_self.item.flowInstUuid);
  },
  methods: {
    loadWorkProcess(flowInstUuid) {
      const _self = this;
      uni.request({
        url: `/api/workflow/work/getWorkProcess`,
        method: "GET",
        data: {
          flowInstUuid,
        },
        success: function (result) {
          _self.workProcess = result.data.data;
        },
      });
    },
    goBack() {
      uni.navigateBack({
        delta: 1,
      });
    },
    onButtonTap(event, button) {
      const _self = this;
      let btnId = button.id;
      switch (btnId) {
        case "remind":
          _self.onRemind();
          break;
        case "limit-time":
          _self.onLimitTimeTap();
          break;
        case "redo":
          _self.onRedoTap();
          break;
        case "stop":
          _self.onStopTap();
          break;
      }
    },
    // 催办
    onRemind() {
      this.$refs.remindPopup.open("bottom");
    },
    onRemindConfirmOk() {
      const _self = this;
      if (isEmpty(stringTrim(_self.remindContent))) {
        uni.showToast({ title: "请填写催办内容！", icon: "error" });
        return;
      }

      let taskInstUuids = [_self.item.taskInstUuid];
      let msgContent = stringTrim(_self.remindContent);
      uni.request({
        url: "/api/workflow/work/remind",
        method: "POST",
        data: { taskInstUuids, opinionLabel: "", opinionValue: "", opinionText: msgContent },
        success: function (result) {
          if (result.data.code == -5002) {
            _self.workView.handlerError.call(_self.workView, result, () => {});
          } else {
            uni.$emit("subflowActionSuccess", { action: "subflowRemind", result, message: "催办成功！" });
          }
        },
        fail: function (error) {
          _self.workView.handlerError.call(_self.workView, error, () => {});
        },
      });
    },
    // 协办时限
    onLimitTimeTap() {
      this.$refs.limitTimePopup.open("bottom");
    },
    getChangeLimitTimeUrl() {
      return "/api/workflow/work/changeFlowDueTime";
    },
    onLimitTimeConfirmOk() {
      const _self = this;
      if (isEmpty(_self.subflowDueTime)) {
        uni.showToast({ title: "请选择反馈时限", icon: "error" });
        return;
      }
      let flowInstUuids = [_self.item.flowInstUuid];
      uni.request({
        url: _self.getChangeLimitTimeUrl(),
        method: "POST",
        data: {
          flowInstUuids,
          subflowDueTime: _self.subflowDueTime,
        },
        success(result) {
          if (result.data.code == -5002) {
            _self.workView.handlerError.call(_self.workView, result, () => {});
          } else {
            uni.$emit("subflowActionSuccess", {
              action: "subflowLimitTime",
              result,
              message: "操作成功！",
            });
          }
        },
        fail(error) {
          _self.workView.handlerError.call(_self.workView, error, () => {});
        },
      });
    },
    // 重做
    onRedoTap() {
      const _self = this;
      uni.showModal({
        title: "提示",
        content: "确认是否重办？",
        cancelText: "否",
        confirmText: "是",
        success: function (result) {
          if (result.confirm === true) {
            _self.onRedoConfirmOk();
          }
        },
      });
    },
    getRedoUrl() {
      return "/api/workflow/work/redoFlow";
    },
    onRedoConfirmOk() {
      const _self = this;
      let taskInstUuids = [_self.item.taskInstUuid];
      // 子流程重办
      let redoUrl = _self.getRedoUrl();
      uni.request({
        url: redoUrl,
        method: "POST",
        data: {
          taskInstUuids,
        },
        header: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
        success: function (result) {
          if (result.data.code == -5002) {
            _self.workView.handlerError.call(_self.workView, result, () => {});
          } else {
            uni.$emit("subflowActionSuccess", { action: "subflowRedo", result, message: "重办成功！" });
          }
        },
        fail: function (error) {
          _self.workView.handlerError.call(_self.workView, error, () => {});
        },
      });
    },
    // 终止
    onStopTap() {
      const _self = this;
      uni.showModal({
        title: "提示",
        content: "确认是否终止？",
        cancelText: "否",
        confirmText: "是",
        success: function (result) {
          if (result.confirm === true) {
            _self.onStopConfirmOk();
          }
        },
      });
    },
    getStopUrl() {
      return "/api/workflow/work/stopFlow";
    },
    onStopConfirmOk() {
      const _self = this;
      let workData = _self.workView.getWorkData();
      let currentTaskInstUuid = workData.taskInstUuid;
      let taskInstUuids = [_self.item.taskInstUuid];
      // 是否终止自身
      let stopSelf = currentTaskInstUuid == _self.item.taskInstUuid;
      let stopFunction = function (taskInstUuids, interactionTaskData) {
        let stopUrl = _self.getStopUrl();
        uni.request({
          url: stopUrl,
          method: "POST",
          data: {
            taskInstUuids: taskInstUuids,
            interactionTaskData: interactionTaskData,
          },
          success: function (result) {
            if (result.data.code == -5002) {
              _self.workView.handlerError.call(_self.workView, result, () => {});
            } else {
              uni.$emit("subflowActionSuccess", {
                action: "subflowStop",
                result,
                message: "终止成功！",
                stopSelf,
              });
            }
          },
          fail: function (error) {
            var callback = function () {
              stopFunction.call(_self, taskInstUuids, _self.workView.workFlow.getInteractionTaskData());
            };
            _self.workView.handlerError.call(_self.workView, error, callback);
          },
        });
      };
      // 关闭确认框
      _self.stopConfirmModalVisible = false;
      stopFunction.call(_self, taskInstUuids, {});
    },
  },
};
</script>

<style lang="scss" scoped>
.subflow-details {
  width: 100%;
}
.work-process-item {
  margin: 5px 0;
}
.work-process-item .task-name {
  text-decoration: underline;
}
.work-process-item .opinion {
  margin-left: 2.4em;
}
.footer {
  width: 100%;
  position: fixed;
  bottom: var(--window-bottom, 0);
  z-index: 10;
  background: #fff;
}

.uni-list-item__content {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  padding-right: 8px;
  flex: 1;
  color: #3b4144;
  // overflow: hidden;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}

.uni-list-item__content--center {
  justify-content: center;
}

.uni-list-item__content-title {
  font-size: $uni-font-size-base;
  color: #3b4144;
  overflow: hidden;
}

.uni-list-item__content-subtitle {
  font-size: $uni-font-size-sm;
  margin-top: 10rpx;
  color: #3b4144;
  overflow: hidden;
}

.uni-list-item__content-note {
  margin-top: 6rpx;
  color: $uni-text-color-grey;
  font-size: $uni-font-size-sm;
  overflow: hidden;
}
</style>
