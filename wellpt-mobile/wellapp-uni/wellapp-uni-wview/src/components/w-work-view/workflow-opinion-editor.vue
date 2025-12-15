<template>
  <view class="workflow-opinion-editor">
    <view class="tabs" :style="enableOpinionPosition ? 'height: 175px' : 'height: 200px'">
      <scroll-view class="scroll-h" :scroll-x="true" :show-scrollbar="false" :scroll-into-view="scrollIntoView">
        <view
          v-for="(tab, index) in tabs"
          :key="index"
          class="uni-tab-item"
          :id="tab.id"
          :data-current="index"
          v-if="tabDatas[tab.id].length"
          @click="onTabTap"
        >
          <text class="uni-tab-item-title" :class="tabIndex == index ? 'uni-tab-item-title-active' : ''">{{
            tab.name
          }}</text>
        </view>
      </scroll-view>
      <view class="line-h"></view>
      <swiper :current="tabIndex" class="swiper-box" :duration="300" @change="onTabChange">
        <swiper-item class="swiper-item" v-for="(tab, index1) in tabs" :key="index1">
          <scroll-view style="height: 100%" scroll-y="true">
            <uni-list>
              <uni-list-item
                class="opinion-list-item"
                v-for="(item, dataIndex) in tabDatas[tab.id]"
                :key="dataIndex"
                clickable
                @click="onItemClick($event, item)"
                :title="item.content"
              ></uni-list-item>
            </uni-list>
          </scroll-view>
        </swiper-item>
      </swiper>
    </view>
    <view class="opinion-position-container" v-if="enableOpinionPosition">
      <radio-group @change="onOpinionPositionChange">
        <label v-for="item in opinions" :key="item.code" class="radio">
          <radio :value="item.code" :checked="item.code == opinion.value" />
          {{ item.content }}
        </label>
      </radio-group>
    </view>
    <view class="opinion-textarea-container">
      <uni-easyinput
        class="opinion-textarea"
        type="textarea"
        v-model="opinion.text"
        placeholder="签署意见"
      ></uni-easyinput>
    </view>
    <view v-if="setting.enabled" style="margin: 5px">
      <uni-w-simple-list-upload
        :fileIds="opinion.files"
        ref="filePicker"
        :limitNum="setting.numLimit"
        :sizeLimit="setting.sizeLimit"
        :sizeLimitUnit="setting.sizeLimitUnit"
        :fileAccept="accept.replaceAll('.', '')"
        :downloadAllType="'2' || setting.downloadAllType"
        @change="onFileChange"
      >
        <button type="primary" plain="true" size="mini">
          <w-icon icon="iconfont icon-ptkj-shangchuan" :size="14"></w-icon>添加附件
        </button>
      </uni-w-simple-list-upload>
    </view>
    <view class="dialog-button-group">
      <view class="dialog-button uni-border-left" @tap="onSignOpinionOk">
        <text class="dialog-button-text button-color">确定</text>
      </view>
      <view
        v-for="(button, index) in buttons"
        :key="index"
        class="dialog-button uni-border-left"
        @tap="onOpinionButtonTap($event, button)"
      >
        <text class="dialog-button-text button-color">{{ button.label }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { isEmpty, isArray, debounce, isFunction } from "lodash";
export default {
  props: {
    options: {
      type: Object,
      required: true,
    },
    setting: {
      type: Object,
      default: {},
    },
    initOpinionText: {
      type: String,
      default: "",
    },
  },
  data() {
    const workData = this.options.workView.getWorkData();
    let text = workData.opinionText || ""; // 自动提交带入的办理意见
    let label = workData.opinionLabel || ""; // 自动提交带入的办理意见
    let value = workData.opinionValue || ""; // 自动提交带入的办理意见
    let files = workData.opinionFiles || []; // 自动提交带入的办理意见
    return {
      tabs: [
        {
          name: "最近使用",
          id: "recent_use",
        },
        {
          name: "常用意见",
          id: "public_opinion",
        },
      ],
      scrollIntoView: "",
      tabIndex: 0,
      tabDatas: {
        recent_use: [],
        public_opinion: [],
      },
      buttons: this.options.buttons,
      enableOpinionPosition: false,
      opinions: [],
      opinion: {
        text,
        label,
        value,
        files,
      },
      fileList: files,
      enableOpinionTextWysiwyg: false, // 意见即时显示
      allowRestore: isEmpty(text), // 是否允许还原本地意见
      records: [], // 信息记录
    };
  },
  computed: {
    accept() {
      if (isArray(this.setting.accept)) {
        return this.setting.accept.join(",");
      }
      return this.setting.accept;
    },
  },
  created() {
    var _self = this;
    var workView = _self.options.workView;
    _self.workView = workView;
    var workData = workView.getWorkData();
    var flowDefUuid = workData.flowDefUuid;
    var taskId = workData.taskId;
    var callback = function (data) {
      workView.setRequiredOpinionPosition(data.enableOpinionPosition && data.requiredOpinionPosition);
      _self.enableOpinionPosition = data.enableOpinionPosition;
      _self.opinions = data.opinions;
      _self.tabDatas["recent_use"] = data.recents || [];
      if (data.recents.length == 0) {
        _self.tabIndex = 1;
      }
      _self.tabDatas["public_opinion"] = data.publicOpinionCategory.opinions || [];
    };
    if (workView.currentUserOpinion) {
      callback(workView.currentUserOpinion);
    } else {
      uni.request({
        url: "/api/workflow/work/getCurrentUserOpinion2Sign/" + flowDefUuid + "/" + taskId,
        method: "GET",
        dataType: "json",
        success: function (result) {
          var data = result.data.data;
          workView.currentUserOpinion = data;
          callback(data);
        },
      });
    }
  },
  mounted() {
    const _self = this;
    if (_self.workView.getOpinionEditor()) {
      return false;
    }
    _self.workView.setOpinionEditor(_self);
    // 撤回时要能带出之前提交填写的意见
    if (_self.workView.isDraft() || _self.workView.isTodo()) {
      let workData = _self.workView.getWorkData();
      _self.records = workData.records || [];
      _self.enableOpinionTextWysiwyg = _self.records.find((item) => item.enableWysiwyg) != null;
      if (_self.workView.isTodo()) {
        this.$axios
          .get(`/api/task/operation/getLastestCancelAfterByFlowInstUuid/${workData.flowInstUuid}`)
          .then(({ data: result }) => {
            let cancelData = result.data;
            if (cancelData) {
              _self.opinion.text = cancelData.opinionText || _self.opinion.text;
              _self.opinion.label = cancelData.opinionLabel || _self.opinion.label;
              _self.opinion.value = cancelData.opinionValue || _self.opinion.value;
              if (cancelData.opinionFileIds) {
                _self.initOpinionFiles(cancelData.opinionFileIds, this.onSignOpinionOk);
              }
              this.onSignOpinionOk();
            } else {
              this.onSignOpinionOk();
            }
          });
      }
    }
  },
  methods: {
    initOpinionFiles(fileIds, callback) {
      this.$axios.get(`/repository/file/mongo/getNonioFiles?fileID=${fileIds}`).then(({ data: result }) => {
        if (result.data) {
          this.fileList = result.data.map((file) => ({
            uuid: file.fileID,
            fileID: file.fileID,
            name: file.fileName,
            status: "done",
          }));
          this.onFileChange({ tempFiles: this.fileList }, callback);
        }
      });
    },
    onTabTap(e) {
      let index = e.target.dataset.current || e.currentTarget.dataset.current;
      this.switchTab(index);
    },
    onTabChange(e) {
      let index = e.target.current || e.detail.current;
      this.switchTab(index);
    },
    switchTab: function (index) {
      this.tabIndex = index;
      this.scrollIntoView = this.tabs[index].id;
    },
    onItemClick: function (event, item) {
      this.opinion.text += item.content;
    },
    // 意见立场变更，设置意见立场名称
    onOpinionPositionChange: function (e) {
      const _self = this;
      let label = "";
      let value = e.detail.value;
      for (let index = 0; index < _self.opinions.length; index++) {
        if (_self.opinions[index].code == value) {
          label = _self.opinions[index].content;
          break;
        }
      }
      _self.opinion.label = label;
      _self.opinion.value = value;
    },
    onSignOpinionOk: function () {
      this.$emit("signOpinionOk", this.opinion);
    },
    getOpinion: function () {
      return this.opinion;
    },
    onOpinionButtonTap: function (event, button) {
      var _self = this;
      if (isEmpty(_self.opinion.text)) {
        uni.showToast({ title: "请先签署意见！" });
        return;
      }

      if (_self.enableOpinionPosition && isEmpty(_self.opinion.value)) {
        uni.showToast({ title: "请选择意见立场！" });
        return;
      }

      _self.onSignOpinionOk();
      setTimeout(() => {
        button.callback.call(button.callbackScope);
      }, 100);
    },
    // 办理意见是否变更
    isOpinionChanged: function () {
      return this.initOpinionText != this.opinion.text || this.opinion.value != "";
    },
    // 本地存储签署意见
    store() {
      uni.setStorageSync(this.getStorageKey(), JSON.stringify(this.opinion));
    },
    // 本地还原签署意见
    restore(customOpinion) {
      try {
        let opinion = customOpinion || uni.getStorageSync(this.getStorageKey());
        if (opinion) {
          if (/^{.*}$/gs.test(opinion)) {
            this.opinion = JSON.parse(opinion);
          } else {
            this.opinion.text = opinion;
          }
          this.fileList = this.opinion.files || [];
        }
      } catch (error) {
        console.error(error);
      }
    },
    // 重置签署意见
    reset(callback) {
      this.opinion = {
        text: "",
        label: "",
        value: "",
        files: [],
      };
      this.fileList = [];
      this.store();
      if (isFunction(callback)) {
        callback(this.opinion);
      }
    },
    // 获取签署意见存储的key
    getStorageKey() {
      let workView = this.workView;
      let workData = workView.getWorkData();
      let flowDefUuid = workData.flowDefUuid;
      let flowInstUuid = workData.flowInstUuid;
      let taskInstUuid = workData.taskInstUuid;
      let userId = workData.userId || "";
      if (workView.isDraft()) {
        return flowDefUuid + userId;
      }
      return flowInstUuid + userId;
    },
    onFileChange({ tempFiles }, callback) {
      this.fileList = tempFiles;
      this.opinion.files = tempFiles
        .filter((file) => file.status == "success" || file.status == "done")
        .map((file) => ({
          uuid: file.fileID,
          name: file.name,
          fileName: file.name,
          fileID: file.fileID,
          status: "done",
        }));
      if (isFunction(callback)) {
        callback(this.opinion);
      }
    },
    // 意见即时显示
    opinionTextWysiwyg: debounce(function (newVal) {
      const _this = this;
      if (!_this.enableOpinionTextWysiwyg) {
        return;
      }
      let records = _this.records;
      records.forEach((record) => {
        if (!(record.enableWysiwyg && record.includeOpinionTextVariable)) {
          return;
        }
        _this.previewOpinionTextRecord(record, newVal);
      });
    }, 250),
    previewOpinionTextRecord(record, newVal) {
      const _this = this;
      // 前置条件
      if (record.enablePreCondition && !isEmpty(record.recordConditions)) {
        let opinion = _this.opinion;
        let workData = _this.workView.getWorkData();
        let dyFormData = _this.workView.collectFormData().dyFormData;
        let data = {
          dyFormData: dyFormData,
          flowInstUuid: workData.flowInstUuid,
          opinionLabel: opinion.label,
          opinionText: opinion.text,
          opinionValue: opinion.value,
          record,
          taskInstUuid: workData.taskInstUuid,
          flowDefUuid: workData.flowDefUuid,
        };
        this.$axios.post("/api/workflow/work/checkRecordPreCondition", data).then(({ data: result }) => {
          if (result.data) {
            _this.changeDyformRecordFieldValue(record, newVal);
          }
        });
      } else {
        _this.changeDyformRecordFieldValue(record, newVal);
      }
    },
    changeDyformRecordFieldValue(record, newValue) {
      const _this = this;
      let fieldName = record.field;
      let dyformWidget = _this.workView.dyform;
      // 记录初始的表单字段值记录
      if (!record.keepInitFieldValue) {
        let fieldValue = dyformWidget.getFieldValue(fieldName);
        record.keepInitFieldValue = true;
        record.initFieldValue = fieldValue;
      }
      let previewValue = record.initFieldValue || "";
      let previewText = `<div class="opinion-preview">
          <div class='opinion-user'>${_this._$USER.userName}（我）</div>
          <div class='opinion-text'>${newValue}</div>
        </div>`;
      if (record.assembler == "descTaskFormOpinionAssembler") {
        previewValue = previewText + previewValue;
      } else {
        previewValue += previewText;
      }
      dyformWidget.setFieldValue(fieldName, previewValue);
    },
  },
};
</script>

<style lang="scss" scoped>
/* #ifndef APP-PLUS */
page {
  width: 100%;
  min-height: 100%;
  display: flex;
}
/* #endif */

.workflow-opinion-editor {
  position: relative;
  height: 350px;
  overflow-y: auto;
  color: $uni-text-color;
  background-color: $uni-bg-secondary-color;
  padding-bottom: 45px;

  .tabs {
    flex: 1;
    flex-direction: column;
    overflow: hidden;
    background-color: $uni-bg-color;
    /* #ifndef APP-PLUS */
    height: 100vh;
    /* #endif */
  }

  .scroll-h {
    width: 750rpx;
    /* #ifdef H5 */
    width: 100%;
    /* #endif */
    height: 80rpx;
    white-space: nowrap;
  }

  .line-h {
    height: 1rpx;
    background-color: #cccccc;
  }

  .uni-tab-item {
    display: inline-block;
    flex-wrap: nowrap;
    padding-left: 34rpx;
    padding-right: 34rpx;
  }

  .uni-tab-item-title {
    color: #555;
    font-size: 30rpx;
    height: 80rpx;
    line-height: 80rpx;
    flex-wrap: nowrap;
    /* #ifndef APP-PLUS */
    white-space: nowrap;
    /* #endif */
  }

  .uni-tab-item-title-active {
    color: $uni-color-primary; //#007aff;
  }

  .swiper-box {
    flex: 1;
    height: "500px";
  }

  .swiper-item {
    flex: 1;
    flex-direction: row;
  }

  .opinion-list-item {
    background-color: $uni-bg-secondary-color;

    ::v-deep .uni-list-item__content-title {
      color: $uni-text-color;
    }
  }

  .opinion-position-container {
    height: 25px;
    margin-left: 15rpx;
  }

  .opinion-textarea-container {
    height: 100px;
    margin: 5px;

    .opinion-textarea {
      color: $uni-text-color !important;
    }
  }

  .dialog-button-group {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    border-top-color: #f5f5f5;
    border-top-style: solid;
    border-top-width: 1px;
    position: fixed;
    bottom: 0;
    width: 100%;
    background-color: #ffffff;

    .dialog-button {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */

      flex: 1;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      height: 40px;
    }

    .border-left {
      border-left-color: #f0f0f0;
      border-left-style: solid;
      border-left-width: 1px;
    }

    .dialog-button-text {
      font-size: 16px;
      color: #333;
    }

    .button-color {
      color: #007aff;
    }
  }
}
</style>
