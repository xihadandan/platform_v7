<template>
  <view :theme="theme" class="workflow-process-viewer">
    <view class="scroll-view">
      <scroll-view class="scroll-view-box" scroll-y="true">
        <view class="process-timeline">
          <view
            v-for="(item, index) in items"
            :key="index"
            :class="{ 'process-timeline-item': true, 'current-task': isCurrentTask(item) }"
          >
            <!-- <w-icon icon="pticon iconfont icon-ptkj-jiedian" :size="8"></w-icon> -->
            <uni-row :class="{ 'process-item-head': true, 'current-task': isCurrentTask(item) }">
              <uni-col class="task-name" :span="10">{{ displayTaskName(item) }}</uni-col>
              <uni-col class="arrive-time" :span="14">{{
                item.arriveTime +
                (item.skipSubmit
                  ? " " + $t("WidgetWorkProcess.skipTask", "环节跳过")
                  : " " + $t("WidgetWorkProcess.arrived", "到达"))
              }}</uni-col>
            </uni-row>
            <view
              class="process-item-content"
              v-for="(detail, detailIndex) in item.handleDetail"
              :key="detailIndex"
              v-show="detailIndex < 3 || (item.showMore && detailIndex >= 3)"
            >
              <view class="uni-flex uni-column detail-item">
                <template v-if="detail.endTime">
                  <view class="user-avatar dot-icon">
                    <image
                      class="user-avatar-img"
                      v-if="!detail.iconShow"
                      :src="'/server-api/org/user/view/photo/' + detail.assigneeId"
                      @error="(e) => imageError(e, index, detailIndex)"
                    />
                    <text v-else>{{ getDisplayUsername(detail).slice(0, 1) }}</text>
                  </view>
                  <!-- <w-icon icon="pticon iconfont icon-ptkj-jiedian" :size="6" class="dot-icon"></w-icon> -->
                  <!-- <w-icon icon="pticon iconfont icon-ptkj-shixinjiantou-zuo" :size="14" class="arrow-icon"></w-icon> -->
                  <view>
                    <view
                      :class="{
                        'user-item': true,
                        'current-user': isCurrentUser(detail.assigneeId),
                        'has-option-position': isOptionPosition(item, detail),
                      }"
                    >
                      <view>
                        <uni-tag
                          v-if="detail.endTime && isCurrentUser(detail.assigneeId)"
                          type="primary"
                          size="mini"
                          class="current-user-tag"
                          :circle="true"
                          :text="$t('WidgetWorkProcess.Me', '我')"
                        >
                        </uni-tag
                      ></view>
                      <view class="username">
                        <rich-text
                          :nodes="`<div class=''>${getDisplayUsername(detail)}<div>`"
                          :class="{ todo: !detail.endTime }"
                        ></rich-text>
                      </view>
                      <view class="jobname-content" v-if="detail.endTime">
                        <view
                          class="jobname"
                          v-for="(job, jidx) in detail.jobNames"
                          :key="index + '' + jidx"
                          v-show="jidx == 0"
                        >
                          {{ job }}
                        </view>
                        <view class="jobname" v-if="detail.jobNames.length > 1" @click="showUserInfoPopup(detail)">
                          {{ $t("WidgetWorkProcess.more", "更多") }}
                        </view>
                      </view>
                    </view>
                  </view>
                </template>
                <template v-else>
                  <view class="user-avatar dot-icon">
                    <w-icon isPc icon="team" :size="20"></w-icon>
                  </view>
                  <view class="username" style="font-weight: normal; color: var(--w-text-color-light)">
                    {{ $t("WidgetWorkProcess.toDoUser", "待办人员") }}
                  </view>
                </template>
                <view class="opinion-content" v-if="detail.endTime != null">
                  <view
                    :class="['opinion-text', { 'empty-opinion': isEmpty(detail.opinion) }]"
                    v-if="detail.endTime != null"
                  >
                    <rich-text :nodes="getDetailOpinion(detail, item)"></rich-text>
                  </view>

                  <view
                    class="opinion-files"
                    v-if="!detail.canceled && detail.opinionFiles && detail.opinionFiles.length > 0"
                    @click="showFilesListPopup(detail.opinionFiles)"
                  >
                    {{
                      $t(
                        "WidgetFormFileUpload.totalFilesSize",
                        { count: detail.opinionFiles.length },
                        "附件(共" + detail.opinionFiles.length + "个)"
                      )
                    }}
                  </view>
                  <view class="opinion-content-hr"></view>
                  <view v-if="detail.endTime != null" class="action-time">
                    <rich-text :nodes="getActionInfo(detail)" class=""></rich-text>
                  </view>
                  <view v-if="detail.endTime != null && getActionInfoUser(detail)" class="action-user">
                    <rich-text :nodes="getActionInfoUser(detail)"></rich-text>
                  </view>
                </view>
                <view v-if="!detail.endTime" class="todo-user-name">
                  <!-- 待办 -->
                  <rich-text :nodes="getDisplayUsername(detail)"></rich-text>
                </view>
              </view>
            </view>

            <view
              v-if="item.handleDetail && item.handleDetail.length > 3"
              @click="showMoreHandle(index)"
              class="show-more-btn"
            >
              <template v-if="!item.showMore">
                <w-icon
                  icon="pticon iconfont icon-ptkj-xianmiaojiantou-xia"
                  :size="14"
                  style="padding-right: 4px"
                ></w-icon>
                {{ $t("WidgetWorkProcess.viewMore", "查看更多") }}
              </template>
              <template v-else>
                <w-icon
                  icon="pticon iconfont icon-ptkj-xianmiaojiantou-shang"
                  :size="14"
                  style="padding-right: 4px"
                ></w-icon>
                {{ $t("WidgetWorkProcess.collapse", "收起") }}
              </template>
            </view>
            <!-- 意见立场统计 -->
            <view class="flex" v-if="item.positionStatistics" style="margin-bottom: 20px; margin-left: 40px">
              <view
                v-for="(statistic, index) in item.positionStatistics"
                :key="index"
                :class="'position-statistic position-statistic-' + statistic.value"
              >
                <text class="label">{{ statistic.label }}</text>
                <text class="count">{{ statistic.count }}</text>
              </view>
            </view>
          </view>
          <view
            v-if="items.length"
            :class="{ 'process-timeline-item': true, 'task-end': true, 'current-task': isOver }"
          >
            <!-- <w-icon icon="pticon iconfont icon-ptkj-jiedian" :size="8"></w-icon> -->
            <uni-row :class="{ 'process-item-head': true, 'current-task': isOver }">
              <uni-col class="task-name" :span="10">{{ $t("WidgetWorkProcess.endTask", "结束") }}</uni-col>
            </uni-row>
          </view>
          <uni-w-empty v-else-if="!loading" :text="$t('global.noData', '暂无数据')"></uni-w-empty>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
import { isEmpty, trim, each, findIndex } from "lodash";
import { utils, storage } from "wellapp-uni-framework";
// #ifndef APP-PLUS
import "./css/workflow-process-viewer.scss";
// #endif
export default {
  props: {
    options: {
      type: Object,
      required: true,
    },
  },
  data() {
    const wellappBackendUrl = storage.getWellappBackendUrl();
    return {
      wellappBackendUrl,
      items: this.options.items || [],
      emptyData: { opinionPositionConfig: {}, workProcesses: [], items: [] },
      data: {},
      loading: false,
      filePreviewServer: undefined,
    };
  },
  computed: {
    isOver() {
      if (this.options && this.options.workView) {
        return this.options.workView.isOver();
      }
      let flowIsOver = true;
      this.initItems.forEach((item) => {
        if (item.hasOwnProperty("endTime") && item.endTime == null) {
          flowIsOver = false;
        } else if (item.handleDetail) {
          item.handleDetail.forEach((detail) => {
            if (detail.hasOwnProperty("endTime") && detail.endTime == null) {
              flowIsOver = false;
            }
          });
        }
      });
      return flowIsOver;
    },
  },
  created() {
    var _self = this;
    var options = _self.options;
    if (options.items) {
      return;
    }
    var workView = options.workView;
    var workData = workView.getWorkData();
    var flowInstUuid = workData.flowInstUuid;
    this.loading = true;
    uni.showLoading();
    uni.request({
      url: "/api/workflow/work/getWorkProcessAndOpinionPositionConfigs?flowInstUuid=" + flowInstUuid,
      method: "GET",
      dataType: "json",
      success: function (result) {
        var data = result.data.data;
        // 拆分数据
        if (data) {
          let opinionPositionConfig = data.opinionPositionConfig;
          let workProcesses = data.workProcesses;
          let items = _self.splitData(workProcesses);
          _self.statisticsOpinionPosition(items, opinionPositionConfig);
          data.items = items;
          _self.items = items;
          options.items = _self.items;
          _self.initItems = utils.deepClone(items);
        }
        this.loading = false;
        uni.hideLoading();
        _self.data = data || _self.emptyData;
        return _self.data;
      },
    });
  },
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    displayTaskName(item) {
      if (item.taskName.includes("补审补办")) {
        if (item.supplemented) {
          return this.$t("WorkflowView.supplementTaskName");
        }
      } else {
        if (item.supplemented) {
          // 增加“补审补办”字样
          return (
            this.$t("WorkflowView." + item.taskId + ".taskName", item.taskName) +
            " (" +
            this.$t("WorkflowWork.extraApproveExtraTask", "补审补办") +
            ")"
          );
        }
      }
      return this.$t("WorkflowView." + item.taskId + ".taskName", item.taskName);
    },
    isEmpty,
    extractJobNames(detail) {
      let identityNamePath = trim(detail.identityNamePath);
      if (isEmpty(identityNamePath)) {
        return [];
      }
      let jobNamePaths = identityNamePath.split(";");
      let jobNames = [];
      jobNamePaths.forEach((jobNamePath) => {
        let paths = jobNamePath.split("/");
        if (paths.length > 1) {
          jobNames.push(paths[paths.length - 2] + " - " + paths[paths.length - 1]);
        } else if (trim(jobNamePath)) {
          jobNames.push(jobNamePath);
        }
      });
      return jobNames;
    },
    splitData(dataList) {
      if (isEmpty(dataList)) {
        return [];
      }

      let groups = [];
      let groupMap = {};
      let toUsersMap = {};
      let pretaskInstUuid = null;
      dataList.forEach((item, index) => {
        if (!item.id) {
          item.id = utils.generateId();
        }
        let taskInstUuid = item.taskInstUuid;
        let group = groupMap[taskInstUuid];
        if (!group) {
          toUsersMap[taskInstUuid] = item.toUser ? item.toUser.split(";") : [];
          group = {
            id: utils.generateId(),
            taskName: item.taskName,
            arriveTime: item.submitTime,
            taskStatus: item.status,
            handleDetail: [item],
            taskId: item.taskId,
            taskInstUuid,
          };
          groupMap[taskInstUuid] = group;
          groups.push(group);
        } else {
          group.handleDetail.push(item);
        }

        pretaskInstUuid = groups.length > 1 ? groups[groups.length - 2].taskInstUuid : null;
        // 移交的办理人计算
        if (pretaskInstUuid && pretaskInstUuid != taskInstUuid) {
          let toUsers = toUsersMap[pretaskInstUuid];
          if (item.actionType == "HandOver") {
            item.formerHandler = toUsers.join(";");
            toUsers = item.toUser ? item.toUser.split(";") : [];
            toUsersMap[pretaskInstUuid] = toUsers;
          } else if (!["CopyTo", "Remind"].includes(item.actionType) && item.status != "未完成") {
            let userIndex = toUsers.indexOf(item.assignee);
            if (userIndex != -1) {
              toUsers.splice(userIndex, 1);
            }

            // 提交到下一环节
            if (
              item.actionType == "Submit" &&
              item.toUser &&
              dataList[index + 1] &&
              dataList[index + 1].taskInstUuid != item.taskInstUuid
            ) {
              if (!toUsersMap[item.taskInstUuid].includes(item.toUser)) {
                toUsersMap[item.taskInstUuid].push(item.toUser);
              }
            } else if (["Transfer", "CounterSign", "AddSign"].includes(item.actionType)) {
              let appendUsers = item.toUser ? item.toUser.split(";") : [];
              appendUsers.forEach((appendUser) => {
                if (!toUsers.includes(appendUser)) {
                  toUsers.push(appendUser);
                }
              });
            } else if (item.actionType == "Cancel") {
              if (item.assignee && !toUsers.includes(item.assignee)) {
                toUsers.push(item.assignee);
              }
            } else if (item.actionType == "Delegation") {
              if (item.toUser && !toUsers.includes(item.toUser)) {
                toUsers.push(item.toUser);
              }
            }
          }
        }

        item.expandFiles = "fileList";
        // 跳转环节名称
        if (item.gotoTaskId) {
          if (item.gotoTaskId == "<EndFlow>") {
            item.gotoTaskName = "结束";
          } else {
            let operation = dataList.find((operation) => operation.taskId == item.gotoTaskId);
            if (operation) {
              item.gotoTaskName = operation.taskName;
            }
          }
        }
        // 办理人身份名称
        item.jobNames = this.extractJobNames(item);

        item.opinion = this.options.workView.getMsgI18ns("", item.opinion, "WorkflowWork.opinionManager");
        if (item.opinionValue) {
          item.opinionLabel = this.options.workView.$widget.$t(
            "WorkflowView." + item.taskId + ".opinionPosition." + item.opinionValue,
            item.opinionLabel
          );
        }
      });
      return groups;
    },
    isCurrentUser(userId) {
      return this._$USER && this._$USER.userId == userId;
    },
    imageError: function (e, index, detailIndex) {
      this.$set(this.items[index].handleDetail[detailIndex], "iconShow", true);
    },
    getDetailOpinion: function (detail, item) {
      let opinion = '<div class="flex f_wrap ">';
      if (this.isOptionPosition(item, detail)) {
        opinion +=
          '<div class="opinion-stance-view opinion-stance-' +
          detail.opinionValue +
          '" >' +
          detail.opinionLabel +
          "</div>";
      }
      let ispass = false;
      if (this.options.workView) {
        let _opinion = this.options.workView.getMsgI18ns(null, detail.opinion, "WorkflowWork.opinionManager");
        if (_opinion && _opinion !== detail.opinion) {
          opinion += _opinion;
          ispass = true;
        }
      }
      if (!ispass) {
        // if (detail && detail.actionCode === 12) {
        //   opinion += this.$t("WidgetWorkProcess.pleaseHurryUp", detail.opinion || "请抓紧办理!");
        // } else {
        opinion += isEmpty(detail.opinion)
          ? detail.actionCode != 36
            ? this.$t("WidgetWorkProcess.noOpinionText", "未填写办理意见")
            : ""
          : detail.opinion;
        // }
      }
      opinion += "</div>";
      return opinion;
    },
    getDisplayUsername: function (detail) {
      let username = trim(detail.assignee);
      if (isEmpty(username)) {
        return username;
      }
      return username.replace(/\;/g, "，");
    },
    getDisplayJobName: function (detail) {
      let jobArr = [];
      let deptName = trim(detail.deptName);
      let mainJobName = trim(detail.mainJobName);
      if (isEmpty(deptName) && isEmpty(mainJobName)) {
        return jobArr;
      }
      jobArr.push(deptName + "-" + mainJobName);
      jobArr.push(deptName + "-" + mainJobName);
      jobArr.push(deptName + "-" + mainJobName);
      jobArr.push(deptName + "-" + mainJobName);
      return jobArr;
    },
    isCurrentTask: function (item) {
      if (item.handleDetail && item.handleDetail.length) {
        let lastItem = item.handleDetail[item.handleDetail.length - 1];
        return lastItem.taskStatus === "未完成" || lastItem.status === "未完成";
      }
      return item.taskStatus === "未完成" || item.status === "未完成";
    },
    isOptionPosition(item, detail) {
      return !detail.canceled && item.showUserOpinionPosition && detail.opinionLabel;
    },
    getActionInfo(detail, isall) {
      let actionInfo = null;
      let actionTime = detail.endTime || "";
      let actionName = detail.actionName || this.$t("WorkflowWork.operation.Submit", "提交");
      let _actionName = this.$t("WorkflowWork.operation." + detail.actionType, null);
      if (_actionName) {
        actionName = _actionName;
      }
      if (detail.actionCode == 35) {
        actionName += " (" + this.$t("WidgetWorkProcess.approveAuto", "自动审批") + ")";
      } else if (detail.actionCode == 36) {
        actionName = this.$t("WidgetWorkProcess.autoJumpRepeatTask", "重复自动跳过");
      }
      let actionType = detail.actionType || "Submit";
      let actionObjects = "";
      if (actionType == "GotoTask") {
        let fromTaskName = detail.suspensionState == 2 ? this.$t("WidgetWorkProcess.endTask", "结束") : detail.taskName;
        let toTaskName = detail.gotoTaskName || "";
        actionInfo = `<span class="action-goto-task-info">
            <span class="action-user-name">${detail.assignee}</span>
            ${this.$t(
              "WidgetWorkProcess.gotoTaskFromText",
              "将流程从"
            )} <span class="action-task-name">&lt;${fromTaskName}&gt;</span>
            ${this.$t(
              "WidgetWorkProcess.gotoTaskToText",
              "跳转至"
            )} <span class="action-task-name"><${toTaskName}></span>
          </span>`;
      } else if (actionType == "HandOver") {
        let formerHandler = detail.formerHandler || "";
        let toUser = detail.toUser || "";
        actionInfo = `<div class="action-hand-over-info">
            <span class="action-user-name">${detail.assignee}</span>
            ${this.$t(
              "WidgetWorkProcess.gotoTaskFromText",
              "将流程从"
            )}<span class="action-task-name"><${formerHandler}></span>
            ${this.$t("WidgetWorkProcess.handoverToText", "移交至")}<span class="action-task-name"><${toUser}></div>
          </span>`;
      } else if (
        detail.toUser &&
        (actionType == "Transfer" ||
          actionType == "CounterSign" ||
          actionType == "AddSign" ||
          actionType == "Delegation")
      ) {
        actionObjects = '<span class="action-objects">' + detail.toUser + "</span>";
        actionName += this.$t("WidgetWorkProcess.To", "给");
      } else if (detail.copyUser && actionType == "CopyTo") {
        if (isall) {
          actionObjects = '<span class="action-objects">' + detail.copyUser + "</span>";
          actionName += this.$t("WidgetWorkProcess.To", "给");
        } else {
          actionInfo = actionTime + " " + actionName;
        }
      } else if (detail.toUser && detail.copyUser) {
        actionInfo = actionTime + " " + actionName + " " + actionObjects;
        if (isall) {
          actionInfo += `<div class="action-with-copyto">${this.$t(
            "WidgetWorkProcess.copyToMeanwhile",
            "同时抄送"
          )} <span class="action-objects">${detail.copyUser}</span></div>`;
        }
      }
      return actionInfo || actionTime + " " + actionName + actionObjects;
    },
    getActionInfoUser(detail) {
      let actionName = detail.actionName || this.$t("WorkflowWork.operation.Submit", "提交");
      let _actionName = this.$t("WorkflowWork.operation." + detail.actionType, null);
      if (_actionName) {
        actionName = _actionName;
      }
      let actionType = detail.actionType || "Submit";
      let actionObjects = "";
      if (
        detail.toUser &&
        (actionType == "Transfer" ||
          actionType == "CounterSign" ||
          actionType == "AddSign" ||
          actionType == "Delegation")
      ) {
        // actionObjects = '<span class="action-objects">' + detail.toUser + '</span>';
        // actionName += this.$t('WidgetWorkProcess.Target', '对象');
      } else if (detail.copyUser && actionType == "CopyTo") {
        actionObjects = '<span class="action-objects">' + detail.copyUser + "</span>";
        actionName += this.$t("WidgetWorkProcess.Target", "对象");
      } else if (detail.toUser && detail.copyUser) {
        actionName = this.$t("WidgetWorkProcess.copyToMeanwhile", "同时抄送");
        actionObjects = `<span class="action-objects">${detail.copyUser}</span>`;
      }
      return actionObjects ? actionName + actionObjects : "";
    },
    showMoreHandle(index) {
      let item = this.items[index];
      this.$set(this.items[index], "showMore", !item.showMore);
    },
    statisticsOpinionPosition(items, opinionPositionConfig) {
      const _this = this;
      if (isEmpty(opinionPositionConfig)) {
        return;
      }
      let filterCancel = (details) => {
        let retDetails = [];
        let cancelAssigneeIdMap = {};
        for (let index = details.length - 1; index >= 0; index--) {
          let item = details[index];
          if (cancelAssigneeIdMap[item.assigneeId]) {
            delete cancelAssigneeIdMap[item.assigneeId];
            continue;
          }

          if (item.actionType == "Cancel" || item.canceled) {
            cancelAssigneeIdMap[item.assigneeId] = item.assigneeId;
            continue;
          }

          retDetails.push(details[index]);
        }
        return retDetails;
      };
      each(items, function (item) {
        let config = _this.getOpinionPositionConfigByTaskId(item.taskId, opinionPositionConfig);
        item.showUserOpinionPosition = (config && config.showUserOpinionPosition) || false;
        if (item.handleDetail) {
          item.handleDetail.forEach((detail) => {
            if (detail.opinionLabel) {
              detail.showUserOpinionPosition = item.showUserOpinionPosition;
            }
          });
        }

        if (config == null || !config.showOpinionPositionStatistics || isEmpty(item.handleDetail)) {
          return;
        }

        let positionStatistics = [];
        let details = filterCancel(item.handleDetail);
        for (let index = 0; index < details.length; index++) {
          let detail = details[index];
          if (isEmpty(detail.opinionLabel)) {
            continue;
          }
          let statisticIndex = findIndex(positionStatistics, function (o) {
            return o.value == detail.opinionValue;
          });
          if (statisticIndex == -1) {
            positionStatistics.push({
              label: detail.opinionLabel,
              value: detail.opinionValue,
              count: 1,
            });
          } else {
            positionStatistics[statisticIndex].count++;
          }
        }
        if (!isEmpty(positionStatistics)) {
          item.positionStatistics = positionStatistics;
        }
      });
    },
    getOpinionPositionConfigByTaskId(taskId, opinionPositionConfig) {
      return opinionPositionConfig.find((config) => config.taskId == taskId);
    },
    showFilesListPopup(files) {
      uni.$emit("ptPopupShow", {
        componentName: "popup-file-list", //弹框内部组件
        title: this.$t(
          "WidgetFormFileUpload.totalFilesSize",
          { count: files.length },
          "附件(共" + files.length + "个)"
        ),
        options: {
          files: files,
        },
      });
    },
    showUserInfoPopup(detail) {
      let userName = this.getDisplayUsername(detail);
      let jobList = detail.jobNames;
      uni.$emit("ptPopupShow", {
        componentName: "popup-user-info", //弹框内部组件
        title: "",
        options: {
          userId: detail.assigneeId,
          userName,
          jobList,
        },
      });
    },
  },
};
</script>

<style lang="scss" scoped>
/* #ifdef APP-PLUS */
@import "./css/workflow-process-viewer.scss";
/* #endif */
.scroll-view {
  /* #ifndef APP-NVUE */
  width: 100%;
  height: 100%;
  /* #endif */
  flex: 1;
}
// 处理抽屉内容滚动
.scroll-view-box {
  // padding-bottom: 10px;
  flex: 1;
  position: absolute;
  top: 45px;
  right: 0;
  bottom: 35px;
  left: 0;
}
</style>
