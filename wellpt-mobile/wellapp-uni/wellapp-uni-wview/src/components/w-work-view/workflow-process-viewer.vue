<template>
  <view :theme="theme" class="workflow-process-viewer">
    <uni-nav-bar :statusBar="true" :border="false" :shadow="true" :fixed="true" title="办理过程"></uni-nav-bar>
    <view class="scroll-view">
      <scroll-view class="scroll-view-box" scroll-y="true">
        <view class="process-timeline">
          <view
            v-for="(item, index) in items"
            :key="index"
            :class="{ 'process-timeline-item': true, 'current-task': isCurrentTask(item) }"
          >
            <w-icon icon="pticon iconfont icon-ptkj-jiedian" :size="8"></w-icon>
            <uni-row :class="{ 'process-item-head': true, 'current-task': isCurrentTask(item) }">
              <uni-col class="task-name" :span="10">{{ item.taskName }}</uni-col>
              <uni-col class="arrive-time" :span="14">{{ item.arriveTime + " 到达" }}</uni-col>
            </uni-row>
            <view
              class="process-item-content"
              v-for="(detail, detailIndex) in item.handleDetail"
              :key="detailIndex"
              v-show="detailIndex < 3 || (item.showMore && detailIndex >= 3)"
            >
              <view class="uni-flex uni-column detail-item">
                <w-icon icon="pticon iconfont icon-ptkj-jiedian" :size="6" class="dot-icon"></w-icon>
                <w-icon icon="pticon iconfont icon-ptkj-shixinjiantou-zuo" :size="14" class="arrow-icon"></w-icon>
                <view>
                  <view
                    :class="{
                      'user-item': true,
                      'current-user': isCurrentUser(detail.assigneeId),
                      'has-option-position': isOptionPosition(item, detail),
                    }"
                  >
                    <view v-if="detail.endTime != null" class="user-avatar">
                      <image
                        class="user-avatar-img"
                        v-if="!detail.iconShow"
                        :src="'/org/user/view/photo/' + detail.assigneeId"
                        @error="(e) => imageError(e, index, detailIndex)"
                      />
                      <text v-else>{{ getDisplayUsername(detail).slice(0, 1) }}</text>
                    </view>
                    <view class="username">
                      <rich-text
                        :nodes="`<div class='w-ellipsis'>${getDisplayUsername(detail)}<div>`"
                        :class="{ todo: !detail.endTime }"
                      ></rich-text>
                    </view>
                    <view
                      class="jobname"
                      :title="detail.endTime ? getDisplayJobName(detail) : ''"
                      v-if="detail.endTime"
                    >
                      {{ detail.endTime ? getDisplayJobName(detail) : "" }}
                    </view>
                    <view>
                      <uni-tag
                        v-if="detail.endTime && isCurrentUser(detail.assigneeId)"
                        type="primary"
                        size="mini"
                        class="current-user-tag"
                        :circle="true"
                        text="我"
                      >
                      </uni-tag
                    ></view>
                  </view>
                  <view
                    :class="['opinion-text', { 'empty-opinion': isEmpty(detail.opinion) }]"
                    v-if="detail.endTime != null"
                  >
                    <rich-text :nodes="getDetailOpinion(detail)"></rich-text>
                  </view>
                </view>
                <view
                  v-if="isOptionPosition(item, detail)"
                  :class="'opinion-stance opinion-stance-' + detail.opinionValue"
                >
                  <rich-text :nodes="detail.opinionLabel" class="opinion-label"></rich-text>
                </view>
                <uni-collapse
                  v-if="!detail.canceled && detail.opinionFiles && detail.opinionFiles.length > 0"
                  v-model="detail.expandFiles"
                  class="file-list-collapse"
                >
                  <uni-collapse-item title="默认开启" class="file-list-collapse-panel">
                    <view slot="title">
                      <text class="file-title">附件（{{ detail.opinionFiles.length }}）</text>
                      <text class="file-action" @click.stop="downloadAll(detail.opinionFiles, detail)">
                        <w-icon icon="pticon iconfont icon-ptkj-xiazai" :size="14"></w-icon>
                        全部下载
                      </text>
                    </view>
                    <uni-list :bordered="false">
                      <uni-list-item v-for="(fitem, findex) in detail.opinionFiles" :key="findex" class="file-item">
                        <view class="flex f_y_c" style="width: 100%" slot="body" @tap="showFileAction(fitem)">
                          <!-- <w-icon  :icon="getFileIcon(item.fileName)" :size="14" style="margin-right: 8px"></w-icon> -->
                          <rich-text
                            :nodes="`<div class='w-ellipsis'>${fitem.fileName}<div>`"
                            class="file-title"
                          ></rich-text>
                          <view class="file-action">
                            <w-icon icon="pticon iconfont icon-ptkj-gengduocaozuo" :size="16"></w-icon>
                          </view>
                        </view>
                      </uni-list-item>
                    </uni-list>
                  </uni-collapse-item>
                </uni-collapse>
                <view v-if="detail.endTime != null" class="action-time">
                  <rich-text :nodes="getActionInfo(detail)" class="w-ellipsis"></rich-text>
                </view>
                <view v-if="detail.endTime != null && getActionInfoUser(detail)" class="action-user">
                  <rich-text :nodes="getActionInfoUser(detail)"></rich-text>
                </view>
              </view>
            </view>

            <view
              v-if="item.handleDetail && item.handleDetail.length > 3"
              @click="showMoreHandle(index)"
              class="show-more-btn"
            >
              <template v-if="!item.showMore">
                <w-icon icon="pticon iconfont icon-ptkj-xianmiaojiantou-xia" :size="14"></w-icon>
                查看更多
              </template>
              <template v-else>
                <w-icon icon="pticon iconfont icon-ptkj-xianmiaojiantou-shang" :size="14"></w-icon>
                收起
              </template>
            </view>
            <!-- 意见立场统计 -->
            <view class="flex" v-if="item.positionStatistics" style="margin-bottom: 12px; margin-left: 20px">
              <view
                v-for="(statistic, index) in item.positionStatistics"
                :key="index"
                :class="'position-statistic position-statistic-' + statistic.value"
              >
                <text>{{ statistic.label }}</text>
                <text class="count">{{ statistic.count }}</text>
              </view>
            </view>
          </view>
          <view
            v-if="items.length"
            :class="{ 'process-timeline-item': true, 'task-end': true, 'current-task': isOver }"
          >
            <w-icon icon="pticon iconfont icon-ptkj-jiedian" :size="8"></w-icon>
            <uni-row :class="{ 'process-item-head': true, 'current-task': isOver }">
              <uni-col class="task-name" :span="10">结束</uni-col>
            </uni-row>
          </view>
          <uni-w-empty v-else-if="!loading" text="暂无办理过程数据"></uni-w-empty>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
import { isEmpty, trim, each, findIndex } from "lodash";
import { utils, storage } from "wellapp-uni-framework";
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
    isEmpty,
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
      });
      return groups;
    },
    isCurrentUser(userId) {
      return this._$USER && this._$USER.userId == userId;
    },
    imageError: function (e, index, detailIndex) {
      this.$set(this.items[index].handleDetail[detailIndex], "iconShow", true);
    },
    getDetailOpinion: function (detail) {
      return isEmpty(detail.opinion) ? "未填写办理意见" : detail.opinion;
    },
    getDisplayUsername: function (detail) {
      let username = trim(detail.assignee);
      if (isEmpty(username)) {
        return username;
      }
      return username.replaceAll(";", "，");
    },
    getDisplayJobName: function (detail) {
      let deptName = trim(detail.deptName);
      let mainJobName = trim(detail.mainJobName);
      if (isEmpty(deptName) && isEmpty(mainJobName)) {
        return "";
      }
      return "(" + deptName + " " + mainJobName + ")";
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
      let actionName = detail.actionName || "提交";
      let actionType = detail.actionType || "Submit";
      let actionObjects = "";
      if (actionType == "GotoTask") {
        let fromTaskName = detail.suspensionState == 2 ? "结束" : detail.taskName;
        let toTaskName = detail.gotoTaskName || "";
        actionInfo = `<span class="action-goto-task-info">
            <span class="action-user-name">${detail.assignee}</span>
            将流程从<span class="action-task-name">${fromTaskName}</span>
            跳转至<span class="action-task-name">${toTaskName}</span>
          </span>`;
      } else if (actionType == "HandOver") {
        let formerHandler = detail.formerHandler || "";
        let toUser = detail.toUser || "";
        actionInfo = `<div class="action-hand-over-info">
            <span class="action-user-name">${detail.assignee}</span>
            将流程从<span class="action-task-name">${formerHandler}</span>
            移交至<span class="action-task-name">${toUser}</div>
          </span>`;
      } else if (
        detail.toUser &&
        (actionType == "Transfer" ||
          actionType == "CounterSign" ||
          actionType == "AddSign" ||
          actionType == "Delegation")
      ) {
        actionObjects = '<span class="action-objects">' + detail.toUser + "</span>";
        actionName += "给";
      } else if (detail.copyUser && actionType == "CopyTo") {
        if (isall) {
          actionObjects = '<span class="action-objects">' + detail.copyUser + "</span>";
          actionName += "给";
        } else {
          actionInfo = actionTime + " " + actionName;
        }
      } else if (detail.toUser && detail.copyUser) {
        actionInfo = actionTime + " " + actionName + " " + actionObjects;
        if (isall) {
          actionInfo += `<div class="action-with-copyto">同时抄送 <span class="action-objects">${detail.copyUser}</span></div>`;
        }
      }
      return actionInfo || actionTime + " " + actionName + actionObjects;
    },
    getActionInfoUser(detail) {
      let actionName = detail.actionName || "提交";
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
        // actionName += '对象';
      } else if (detail.copyUser && actionType == "CopyTo") {
        actionObjects = '<span class="action-objects">' + detail.copyUser + "</span>";
        actionName += "对象";
      } else if (detail.toUser && detail.copyUser) {
        actionName = "同时抄送";
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
        for (let index = details.length - 1; index >= 0; index--) {
          if (details[index].actionType == "Cancel" || details[index].canceled) {
            break;
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
    showFileAction(file) {
      const _self = this;
      let actions = ["预览", "下载"];
      uni.showActionSheet({
        itemList: ["预览", "下载"],
        success: (e) => {
          console.log(e.tapIndex);
          if (e.tapIndex == 0) {
            _self.previewFile(file);
          } else if (e.tapIndex == 1) {
            _self.downloadFile(file);
          }
        },
      });
    },
    previewFile(file) {
      let WOPISrc =
        this.wellappBackendUrl +
        "/wopi/files/" +
        file.fileID +
        "?access_token=xxx&wdMobileHost=2&wdTtime=" +
        new Date().getTime();
      const filePreviewServer = this.loadFilePreviewServer();
      const url = filePreviewServer + "/document/online/viewer?WOPISrc=" + encodeURIComponent(WOPISrc);
      uni.navigateTo({
        url: "/uni_modules/w-app/pages/web-view/web-view?url=" + url + "&title=" + file.fileName,
      });
    },
    // 加载文件预览服务器地址
    loadFilePreviewServer: function () {
      if (this.filePreviewServer) {
        return this.filePreviewServer;
      }
      this.filePreviewServer = uni.getStorageSync("filePreviewServer");
      if (this.filePreviewServer) {
        return this.filePreviewServer;
      }
      const key = "document.preview.path";
      const getTimestamp = new Date().getTime();
      uni.request({
        url: "/basicdata/system/param/get?key=" + key + "&timestamp=" + getTimestamp,
        method: "GET",
        contentType: "application/json",
        dataType: "json",
        success: (success) => {
          this.filePreviewServer = success.data.data;
          uni.setStorageSync("filePreviewServer", this.filePreviewServer);
        },
      });
    },
    downloadFile(file) {
      file.url = `/proxy-repository/repository/file/mongo/download?fileID=${file.fileID}`;
      // #ifdef H5
      this.downloadFileByWeb(file);
      // #endif
      // #ifndef H5
      this.downloadFileOther(file);
      // #endif
    },
    // 通过web方式下载
    /*
    	import { saveAs } from 'file-saver';
      //filePath 这里的地址是 uni.downloadFile 中的返回值里的地址
      //finName 下载文件名
      saveAs(filePath,finName)
    */
    downloadFileByWeb(file) {
      var _this = this;
      var hiddenIFrameID = "hiddenDownloader" + Date.now();
      var iframe = document.createElement("iframe");
      iframe.id = hiddenIFrameID;
      iframe.style.display = "none";
      document.body.appendChild(iframe);
      iframe.src = storage.fillAccessResourceUrl(file.url);
    },
    // 下载文件
    downloadFileOther(file, url) {
      uni.showLoading({
        title: `文件${file.fileName || file.name}下载中...`,
      });

      uni.downloadFile({
        url: url || `${storage.fillAccessResourceUrl(file.url)}`,
        success: (res) => {
          console.log("downloadFile success, res is", res);
          uni.hideLoading();
          uni.showToast({ title: "下载成功！" + res.tempFilePath });
          this.saveFileToLocal(res.tempFilePath);
        },
        fail: (err) => {
          console.log("downloadFile fail, err is:", err);
        },
      });
    },
    // 保存文件到本地
    saveFileToLocal(tempFilePath) {
      uni.saveFile({
        tempFilePath,
        success: function (res) {
          var savedFilePath = res.savedFilePath;
          console.log(res);
        },
      });
    },
    downloadAll(files, detail) {
      const _this = this;
      if (_this.options.workView) {
        _this.options.workView.getSettings().then((settings) => {
          let opinionFileSetting = settings.get("OPINION_FILE") || {};
          // 下载压缩包
          if (opinionFileSetting.downloadAllType == "1") {
            _this.downloadAllAsZip(files, detail);
          } else {
            // 下载源文件
            //bug：手机浏览器批量下载源文件有问题（未解决）
            files.forEach((file) => {
              // #ifdef H5
              _this.downloadFileByWeb(file);
              // #endif
              // #ifndef H5
              _this.downloadFileOther(file);
              // #endif
            });
          }
        });
      } else {
        _this.downloadAllAsZip(files, detail);
      }
    },
    downloadAllAsZip(files, detail) {
      let ids = [],
        fileName = `${detail.taskName}_${detail.assignee}_${new Date().format("yyyyMMDDHHmmss")}`;
      files.forEach((item) => {
        if (!fileName) {
          fileName = (item.name || item.filename || item.fileName) + "等文件";
        }
        ids.push({
          fileID: item.fileID,
        });
      });
      // #ifdef H5
      this.downloadFileByWeb({
        url: `/proxy-repository/repository/file/mongo/downAllFiles?fileIDs=${encodeURIComponent(
          JSON.stringify(ids)
        )}&includeFolder=false&fileName=${fileName}`,
      });
      // #endif
      // #ifndef H5
      this.downloadFileOther(
        { fileName: fileName },
        storage.fillAccessResourceUrl(
          `/proxy-repository/repository/file/mongo/downAllFiles?fileIDs=${encodeURIComponent(
            JSON.stringify(ids)
          )}&includeFolder=false&fileName=${fileName}`
        )
      );
      // #endif
    },
  },
};
</script>

<style lang="scss" scoped>
.scroll-view {
  /* #ifndef APP-NVUE */
  width: 100%;
  height: 100%;
  /* #endif */
  flex: 1;
}
// 处理抽屉内容滚动
.scroll-view-box {
  padding-top: 45px;
  // padding-bottom: 10px;
  flex: 1;
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}
</style>
