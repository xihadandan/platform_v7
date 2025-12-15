<template>
  <view class="workflow-opinion-editor">
    <view style="padding: 0 var(--w-padding-xs)">
      <view class="workflow-opinion-editor__container">
        <uni-w-tabs
          :list="tabs"
          @change="onTabTap"
          keyName="title"
          name="title"
          :current="tabIndex"
          equalWidth
          :isFirstItemLeftPadding="false"
          bgColor="transparent"
          style="--uni-w-tabs-box-border-color: transparent"
        ></uni-w-tabs>
        <!-- itemMaxWidth="120" -->
        <swiper :current="tabIndex" class="swiper-box" :duration="300" @change="onTabChange">
          <swiper-item class="swiper-item" v-for="(tab, index1) in tabs" :key="index1">
            <scroll-view style="height: 100%" scroll-y="true">
              <uni-list :border="false">
                <uni-list-item
                  :border="false"
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
      <view class="workflow-opinion-editor__container opinion-textarea-container">
        <view class="workflow-opinion-editor__title">{{
          $t("WorkflowWork.opinionManager.signOpinionPanel.title", "签署意见")
        }}</view>
        <view class="opinion-textarea">
          <uni-w-easyinput
            type="textarea"
            :inputBorder="false"
            v-model="opinion.text"
            :placeholder="$t('WorkflowWork.opinionManager.pleaseSignOpinionContent', '请输入签署内容...')"
          ></uni-w-easyinput>
          <view class="opinion-position-container" v-if="enableOpinionPosition && workView.isTodo()">
            <uni-w-data-checkbox
              :multiple="false"
              mode="default"
              v-model="opinion.value"
              :localdata="opinions"
              :map="{ text: 'content', value: 'code' }"
              @change="onOpinionPositionChange"
            />
            <!-- <radio-group @change="onOpinionPositionChange">
              <label v-for="item in opinions" :key="item.code" class="radio">
                <radio :value="item.code" :checked="item.code == opinion.value" />
                {{ item.content }}
              </label>
            </radio-group> -->
          </view>
        </view>
      </view>
      <view v-if="setting.enabled" class="workflow-opinion-editor__container">
        <uni-w-simple-list-upload
          v-model="opinion.files"
          ref="filePicker"
          title=""
          :limitNum="setting.numLimit"
          :sizeLimit="setting.sizeLimit"
          :sizeLimitUnit="setting.sizeLimitUnit"
          :fileAccept="accept.replace(/\./g, '')"
          :downloadAllType="setting.downloadAllType"
          :fileNameRepeat="setting.allowNameRepeat"
          @change="onFileChange"
        >
          <view class="flex f_x_s workflow-opinion-editor__title">
            <view @click.stop>
              <text>{{ $t("WorkflowWork.opinionManager.attachment", "附件") }}</text>
              <text style="font-size: var(--w-font-size-sm); font-weight: normal" v-if="setting.numLimit"
                >({{ opinion.files.length }}/{{ setting.numLimit }})
              </text>
            </view>
            <uni-w-button
              type="link"
              size="small"
              icon="iconfont icon-ptkj-shangchuan"
              :text="$t('WorkflowWork.opinionManager.operation.addAttachment', '上传附件')"
            ></uni-w-button>
          </view>
        </uni-w-simple-list-upload>
      </view>
    </view>
    <view class="dialog-button-group">
      <uni-w-button @click="onSignOpinionOk" type="primary" block>
        {{ $t("WorkflowWork.opinionManager.operation.ok", "确定") }}
      </uni-w-button>
      <uni-w-button v-if="buttons.length" @click="onOpinionButtonTap($event, buttons[0])" :type="buttons[0].type" block>
        {{ buttons[0].title }}
      </uni-w-button>
    </view>
  </view>
</template>

<script>
import { isEmpty, isArray, debounce, isFunction, isEqual, map } from "lodash";
export default {
  inject: ["workviewContext"],
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
    initOpinionFileIds: {
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
          name: this.$t("WorkflowWork.opinionManager.recentTabTitle", "最近使用"),
          id: "recent_use",
        },
        {
          name: this.$t("WorkflowWork.opinionManager.commonTabTitle", "常用意见"),
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
    this.getStorageKey();
    var callback = function (data) {
      workView.setRequiredOpinionPosition(data.enableOpinionPosition && data.requiredOpinionPosition);
      _self.enableOpinionPosition = data.enableOpinionPosition;
      _self.opinions = map(data.opinions, (item) => {
        item.content = taskId
          ? _self.$t("WorkflowView." + taskId + ".opinionPosition." + item.code, item.content)
          : item.content;
        return item;
      });
      _self.tabDatas["recent_use"] = data.recents || [];
      if (data.recents.length == 0) {
        _self.tabIndex = 1;
      }
      _self.tabDatas["public_opinion"] = data.publicOpinionCategory.opinions || [];

      if (!_self.enableOpinionPosition && _self.opinion) {
        _self.opinion.label = null;
        _self.opinion.value = null;
      }
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
  watch: {
    "opinion.text": function (newVal, oldVal) {
      this.opinionTextWysiwyg(newVal);
    },
  },
  mounted() {
    const _self = this;
    if (_self.workView.getOpinionEditor()) {
      let self = _self.workView.getOpinionEditor();
      if (!isEqual(_self.opinion, self.opinion)) {
        _self.opinion = self.opinion;
        if (_self.opinion.files) {
          let _files = "";
          if (typeof _self.opinion.files == "object") {
            _self.fileList = _self.opinion.files;
          } else if (typeof _self.opinion.files == "string") {
            _self.initOpinionFiles(_self.opinion.files);
          }
        }
      }
      _self.workView.setOpinionEditor(_self);
      if (_self.workView.isDraft() || _self.workView.isTodo()) {
        let workData = _self.workView.getWorkData();
        _self.records = workData.records || [];
        _self.enableOpinionTextWysiwyg = _self.records.find((item) => item.enableWysiwyg) != null;
      }
      return false;
    }
    _self.workView.setOpinionEditor(_self);
    // 还原本地意见
    if (_self.allowRestore) {
      _self.restore();
    }
    if (!isEmpty(_self.initOpinionFileIds)) {
      _self.initOpinionFiles(_self.initOpinionFileIds);
    }
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
    // this.store();
  },
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    initOpinionFiles(fileIds, callback) {
      this.$axios.get(`/repository/file/mongo/getNonioFiles?fileID=${fileIds}`).then(({ data: result }) => {
        if (result.data) {
          this.fileList = result.data.map((file) => ({
            uuid: file.fileID,
            fileID: file.fileID,
            name: file.fileName,
            status: "done",
            size: file.fileSize,
          }));
          this.onFileChange({ tempFiles: this.fileList }, callback);
        }
      });
    },
    onTabTap(index) {
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
      _self.opinion.label = e.detail.data.content;
    },
    onSignOpinionOk: function () {
      if (this.$refs.filePicker) {
        let ispass = this.$refs.filePicker.filesStatusValidate();
        if (!ispass) {
          return false;
        }
      }
      this.store();
      this.$emit("signOpinionOk", this.opinion);
    },

    getOpinion: function () {
      return this.opinion;
    },
    onOpinionButtonTap: function (event, button) {
      var _self = this;
      if (isEmpty(_self.opinion.text)) {
        uni.showToast({ title: this.$t("WorkflowWork.opinionManager.message.pleaseSignOpinion", "请先签署意见！") });
        return;
      }

      if (
        _self.enableOpinionPosition &&
        _self.workView.isRequiredOpinionPosition() &&
        isEmpty(_self.opinion.value) &&
        _self.workView.isTodo()
      ) {
        uni.showToast({
          title: this.$t("WorkflowWork.opinionManager.message.pleaseChooseOpinionStanceFirst", "请选择意见立场！"),
        });
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
      let data = uni.getStorageSync("workflow_opinion_data");
      if (data) {
        data = JSON.parse(data);
      } else {
        data = {};
      }
      data[this.getStorageKey()] = this.opinion;
      uni.setStorageSync("workflow_opinion_data", JSON.stringify(data));
    },
    delStoreAndRestore(key, nkey, opinion) {
      let data = uni.getStorageSync("workflow_opinion_data");
      if (data) {
        data = JSON.parse(data);
      } else {
        data = {};
      }
      delete data[key];
      data[nkey] = opinion;
      uni.setStorageSync("workflow_opinion_data", JSON.stringify(data));
    },
    getStore(key) {
      let data = uni.getStorageSync("workflow_opinion_data");
      if (data) {
        data = JSON.parse(data);
      } else {
        data = {};
      }
      return key ? data[key] : data[this.getStorageKey()];
    },
    // 本地还原签署意见
    restore(customOpinion) {
      try {
        let opinion = customOpinion || this.getStore();
        if (opinion) {
          if (/^{.*}$/gs.test(opinion)) {
            this.opinion = JSON.parse(opinion);
            if (!(this.workView.isTodo() || this.workView.isDraft())) {
              this.opinion.label = "";
              this.opinion.value = "";
            }
          }
          if (typeof opinion == "object") {
            this.opinion = opinion;
            if (!(this.workView.isTodo() || this.workView.isDraft())) {
              this.opinion.label = "";
              this.opinion.value = "";
            }
          } else {
            this.opinion.text = opinion;
          }
          this.fileList = this.opinion.files || [];
          this.onSignOpinionOk();
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
      let userId = workData.userId || this._$USER.userId;
      let key = "workflow_opinion_data_" + flowInstUuid + "_" + userId;
      if (workView.isDraft()) {
        let keyDef = "workflow_opinion_data_" + flowDefUuid + "_" + userId;
        let draftOpinion = this.getStore(keyDef);
        if (draftOpinion && flowInstUuid) {
          this.delStoreAndRestore(keyDef, key, draftOpinion); // 删除发起时草稿的签署意见
        }
        if (!flowInstUuid) {
          key = keyDef;
        }
      }
      return key;
    },
    onFileChange({ tempFiles }, callback) {
      this.fileList = tempFiles;
      this.opinion.files = tempFiles
        // .filter((file) => file.status == "success" || file.status == "done")
        .map((file) => ({
          uuid: file.fileID,
          name: file.name,
          fileName: file.name,
          fileID: file.fileID,
          status: file.status,
          size: file.size,
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
        let fieldValue = dyformWidget.dyform.getFieldValue(fieldName);
        record.keepInitFieldValue = true;
        record.initFieldValue = fieldValue;
      }
      let previewValue = record.initFieldValue || "";
      let previewText = `<div class="opinion-preview">
          <div class='opinion-user'>${_this._$USER.userName}（${this.$t("WorkflowWork.opinionManager.Me", "我")}）</div>
          <div class='opinion-text'>${newValue}</div>
        </div>`;
      if (record.assembler == "descTaskFormOpinionAssembler") {
        previewValue = previewText + previewValue;
      } else {
        previewValue += previewText;
      }
      dyformWidget.dyform.setFieldValue(fieldName, previewValue);
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
  max-height: 75vh;
  overflow-y: auto;
  color: $uni-text-color;
  background-color: $uni-bg-secondary-color;
  margin-bottom: 70px;

  .workflow-opinion-editor__container {
    border-radius: 8px;
    border: 1px solid var(--w-border-color-mobile);
    background-color: var(--w-bg-color-mobile-th);
    padding: var(--w-padding-2xs);
    margin-bottom: var(--w-margin-2xs);

    .uni-list:after {
      content: none;
    }

    .workflow-opinion-editor__title {
      font-size: var(--w-font-size-md);
      color: $uni-text-color;
      font-weight: bold;
      padding: var(--w-padding-3xs) 0;
    }

    ::v-deep .uni-file-picker__lists-box {
      background-color: #ffffff;
    }
  }

  .swiper-box {
    flex: 1;
    height: 190px;
    margin-top: var(--w-margin-3xs);
    border-radius: 8px;
    background-color: var(--w-fill-color-base);
    padding: var(--w-padding-2xs) 0;
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
    ::v-deep .uni-list-item__container {
      padding: 8px;
      font-size: var(--w-font-siza-base);
    }
  }

  .opinion-position-container {
    border-top: 1px solid var(--w-border-color-mobile);
    padding-top: var(--w-padding-2xs);
    margin-top: var(--w-margin-3xs);
  }

  .opinion-textarea-container {
    .opinion-textarea {
      margin-top: var(--w-margin-3xs);
      min-height: 100px;
      background-color: var(--w-fill-color-base);
      border-radius: 8px;
      padding: var(--w-padding-xs) var(--w-padding-2xs);
    }
  }

  .dialog-button-group {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    // border-top-color: #f5f5f5;
    // border-top-style: solid;
    // border-top-width: 1px;
    position: fixed;
    bottom: 20px;
    width: 100%;
    background-color: #ffffff;

    .w-button {
      flex: 1;
      margin: 6px 6px;

      &:first-child {
        margin-left: 12px;
      }
      &:last-child {
        margin-right: 12px;
      }
    }
  }
}
</style>
