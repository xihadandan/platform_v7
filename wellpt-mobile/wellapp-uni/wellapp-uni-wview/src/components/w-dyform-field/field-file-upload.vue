<template>
  <dyform-field-base class="w-file-upload" :dyformField="dyformField">
    <template slot="editable">
      <uni-collapse class="field-collapse">
        <uni-collapse-item :showArrow="false" ref="collapseItem">
          <template slot="title">
            <view class="field-title">
              <view style="flex: 1" class="field-collapse-item-label">
                <text v-if="isRequired" class="is-required">*</text>
                <uni-icons type="paperclip"></uni-icons>
                <text>{{ fieldLabel }}</text>
              </view>
              <view class="uni-flex uni-row" style="justify-content: flex-end">
                <view class="file-count-summary">{{ files.length }}个附件</view>
                <view v-if="isAllowUpload" class="mini-btn-group">
                  <!-- 图片附件 -->
                  <view v-if="isImageFileUpload">
                    <view class="uni-flex uni-row" @click.stop="chooseImage">
                      <!-- 添加图片 -->
                      <uni-icons class="mini-btn mini-btn-right icon" type="plus" size="26"></uni-icons>
                    </view>
                  </view>
                  <view v-else>
                    <view class="uni-flex uni-row">
                      <view v-if="files && files.length > 0" class="mini-btn mini-btn-left" @click.stop="clearFiles">
                        <uni-icons class="icon" type="clear" size="26"></uni-icons>
                      </view>
                      <view class="mini-btn mini-btn-right" @click.stop="chooseFile">
                        <uni-icons class="icon" type="plus" size="26"></uni-icons>
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
            <view v-if="dyformField.errorMessage != null && dyformField.errorMessage != ''">
              <text class="error-message-text">{{ dyformField.errorMessage }}</text>
            </view>
          </template>
          <view class="file-item-list">
            <view class="file-item" v-for="(file, index) in files" :key="index">
              <!-- 图片附件 -->
              <image v-if="isImageFileUpload" style="width: 100%" :src="file.previewUrl"></image>
              <view class="uni-flex uni-row file-info-container">
                <view style="flex: 1">
                  <view class="file-info-filename" @tap="onFileNameTap($event, file)">{{ file.fileName }}</view>
                  <view class="uni-flex uni-row">
                    <view class="text" style="width: 70%">
                      <uni-dateformat :date="file.createTime" format="yyyy-MM-dd hh:mm:ss"></uni-dateformat>
                    </view>
                    <view class="text" style="-webkit-flex: 1; flex: 1">{{ formatFileSize(file.fileSize) }}</view>
                  </view>
                </view>
                <uni-icons
                  v-if="fileActions.length > 0"
                  class="file-item-action-icon"
                  type="info"
                  color="#007aff"
                  size="24px"
                  @tap="onFileActionTap($event, file)"
                ></uni-icons>
              </view>
            </view>
          </view>
        </uni-collapse-item>
      </uni-collapse>
    </template>
    <template slot="displayAsLabel">
      <uni-collapse class="field-collapse">
        <uni-collapse-item :showArrow="false">
          <template slot="title">
            <view class="field-title">
              <view style="flex: 1" class="field-collapse-item-label">
                <text v-if="isRequired" class="is-required">*</text>
                <uni-icons type="paperclip"></uni-icons>
                <text>{{ fieldLabel }}</text>
              </view>
              <view class="uni-flex uni-row" style="justify-content: flex-end">
                <view class="file-count-summary">{{ files.length }}个附件</view>
              </view>
            </view>
          </template>
          <view class="file-item-list">
            <view class="file-item" v-for="(file, index) in files" :key="index">
              <!-- 图片附件 -->
              <image v-if="isImageFileUpload" style="width: 100%" :src="file.previewUrl"></image>
              <view class="uni-flex uni-row file-info-container">
                <view style="flex: 1">
                  <view class="file-info-filename" @tap="onFileNameTap($event, file)">{{ file.fileName }}</view>
                  <view class="uni-flex uni-row">
                    <view class="text" style="width: 70%">
                      <uni-dateformat :date="file.createTime" format="yyyy-MM-dd hh:mm:ss"></uni-dateformat>
                    </view>
                    <view class="text" style="-webkit-flex: 1; flex: 1">{{ formatFileSize(file.fileSize) }}</view>
                  </view>
                </view>
                <uni-icons
                  v-if="fileActions.length > 0"
                  class="file-item-action-icon"
                  type="info"
                  color="#007aff"
                  size="24px"
                  @tap="onFileActionTap($event, file)"
                ></uni-icons>
              </view>
            </view>
          </view>
        </uni-collapse-item>
      </uni-collapse>
    </template>
    <template slot="disabled">
      <uni-collapse class="field-collapse">
        <uni-collapse-item>
          <template slot="title">
            <view class="field-title">
              <view style="flex: 1" class="field-collapse-item-label">
                <text v-if="isRequired" class="is-required">*</text>
                <uni-icons type="paperclip"></uni-icons>
                <text>{{ fieldLabel }}</text>
              </view>
              <view class="uni-flex uni-row" style="justify-content: flex-end">
                <view class="file-count-summary">{{ files.length }}个附件</view>
              </view>
            </view>
          </template>
          <view class="file-item-list">
            <view class="file-item" v-for="(file, index) in files" :key="index">
              <!-- 图片附件 -->
              <image v-if="isImageFileUpload" style="width: 100%" :src="file.previewUrl"></image>
              <view class="uni-flex uni-row file-info-container">
                <view style="flex: 1">
                  <view class="file-info-filename" @tap="onFileNameTap($event, file)">{{ file.fileName }}</view>
                  <view class="uni-flex uni-row">
                    <view class="text" style="width: 70%">
                      <uni-dateformat :date="file.createTime" format="yyyy-MM-dd hh:mm:ss"></uni-dateformat>
                    </view>
                    <view class="text" style="-webkit-flex: 1; flex: 1">{{ formatFileSize(file.fileSize) }}</view>
                  </view>
                </view>
                <uni-icons
                  v-if="fileActions.length > 0"
                  class="file-item-action-icon"
                  type="info"
                  color="#007aff"
                  size="24px"
                  @tap="onFileActionTap($event, file)"
                ></uni-icons>
              </view>
            </view>
          </view>
        </uni-collapse-item>
      </uni-collapse>
    </template>
  </dyform-field-base>
</template>

<script>
import { storage } from "wellapp-uni-framework";
import dyformFieldBase from "./field-base.vue";
const _ = require("lodash");
// 请求token
let jwtToken = storage.getAccessToken();
let wellappBackendUrl = storage.getWellappBackendUrl();
var MONGO_REP = wellappBackendUrl + "/repository/file/mongo";
var SAVE_FILE = MONGO_REP + "/savefiles?jwt=" + jwtToken;
// var DELETE_FILE = MONGO_REP + '/deleteFile?fileID=';
var DOWNLOAD_FILE = MONGO_REP + "/download?jwt=" + jwtToken + "&fileID=";
// var DOWNLOAD_ALL_FILES = MONGO_REP + '/downAllFiles?fileIDs=';
// var LOAD_FILE_FROM_FOLDER = MONGO_REP + '/getNonioFilesFromFolder';
import mixin from "./field-mixin";
export default {
  mixins: [mixin],
  components: { dyformFieldBase },
  data() {
    return {
      files: [],
      fileActions: [],
    };
  },
  created() {
    var _self = this;
    var value = _self.dyformField.getValue();
    var files = value == null ? [] : JSON.parse(JSON.stringify(value));
    files = _.isArray(files) ? files : [];
    _.each(files, function (file) {
      file.previewUrl = DOWNLOAD_FILE + file.fileID + "&preview=true";
    });
    _self.files = files;
    // console.log(JSON.stringify(_self.files));
    _self.fileActions = _self.getFileActions();
    // 加载文件预览服务器地址
    _self.loadFilePreviewServer();
  },
  methods: {
    // 文件大小格式化
    formatFileSize: function (size, pointLength, units) {
      var unit;
      units = units || ["B", "K", "M", "G", "TB"];
      while ((unit = units.shift()) && size > 1024) {
        size = size / 1024;
      }
      return (unit === "B" ? size : size.toFixed(pointLength || 2)) + unit;
    },
    // 清空附件
    clearFiles: function () {
      var _self = this;
      uni.showModal({
        title: "提示",
        content: "是否清空附件",
        cancelText: "否",
        confirmText: "是",
        success: function (result) {
          if (result.confirm === true) {
            _self.files = [];
            _self.dyformField.setValue(_self.files);
          }
        },
      });
    },
    // 选择上传文件
    chooseFile: function () {
      var _self = this;
      var fileExt = _self.fieldDefinition.fileExt;
      var extension = _.isEmpty(fileExt) ? ["*"] : JSON.parse(fileExt);
      uni.chooseImage({
        count: 1,
        type: ["all"],
        extension: extension,
        success: (res) => {
          console.log("chooseImage success, temp path is", res.tempFilePaths[0]);
          var fileSrc = res.tempFilePaths[0];
          _self.uploadFile({
            filePath: fileSrc,
            fileType: "application/octet-stream",
          });
        },
        fail: (err) => {
          console.log(err);
          uni.showModal({
            title: "授权失败",
            content: "需要从您的本地存储获取文件，请在设置界面打开相关权限",
            success: (res) => {
              if (res.confirm) {
                uni.openSetting();
              }
            },
          });
        },
      });
    },
    // 上传文件
    uploadFile: function (options) {
      var _self = this;
      let filePath = options.filePath;
      let fileType = options.fileType;
      uni.uploadFile({
        url: SAVE_FILE,
        filePath: filePath,
        fileType: fileType,
        name: "fileUpload",
        success: (res) => {
          console.log("uploadImage success, res is:", res);
          var data = JSON.parse(res.data);
          var uploadFiles = data.data || [];
          console.log(uploadFiles);
          _self.openCollapseItem();
          _.each(uploadFiles, function (uploadFile) {
            var file = {};
            file.fileID = uploadFile.fileID;
            file.fileName = uploadFile.filename;
            file.digestAlgorithm = uploadFile.digestAlgorithm == undefined ? "" : uploadFile.digestAlgorithm;
            file.digestValue = uploadFile.digestValue == undefined ? "" : uploadFile.digestValue;
            file.certificate = uploadFile.certificate == undefined ? "" : uploadFile.certificate;
            file.signatureValue = "";
            file.contentType = uploadFile.contentType == undefined ? "" : uploadFile.contentType;
            file.isNew = true;
            file.fileSize = uploadFile.fileSize;
            file.createTime = uploadFile.createTime; //new Date(uploadFile.createTime);
            file.previewUrl = DOWNLOAD_FILE + file.fileID + "&preview=true&jwt=" + jwtToken;
            _self.files.push(file);
          });
          _self.dyformField.setValue(_self.files);
          uni.showToast({
            title: "上传成功",
            icon: "success",
            duration: 1000,
          });
        },
        fail: (err) => {
          console.log("uploadImage fail", err);
          uni.showModal({
            content: err.errMsg,
            showCancel: false,
          });
        },
      });
    },
    // 选择上传图片
    chooseImage: function () {
      var _self = this;
      uni.chooseImage({
        count: 1,
        sizeType: ["original"],
        sourceType: ["album"],
        success: (res) => {
          console.log("chooseImage success, temp path is", res.tempFilePaths[0]);
          var imageSrc = res.tempFilePaths[0];
          _self.uploadFile({
            filePath: imageSrc,
            fileType: "image",
          });
        },
        fail: (err) => {
          console.log("chooseImage fail", err);
          // #ifdef MP
          uni.getSetting({
            success: (res) => {
              let authStatus = res.authSetting["scope.album"];
              if (!authStatus) {
                uni.showModal({
                  title: "授权失败",
                  content: "需要从您的相册获取图片，请在设置界面打开相关权限",
                  success: (res) => {
                    if (res.confirm) {
                      uni.openSetting();
                    }
                  },
                });
              }
            },
          });
          // #endif
        },
      });
    },
    openCollapseItem: function () {
      if (this.$refs.collapseItem && !this.$refs.collapseItem.isOpen) {
        this.$refs.collapseItem.onClick(true);
      }
    },
    // 文件名点击，预览附件
    onFileNameTap: function (e, file) {
      var _self = this;
      var WOPISrc =
        wellappBackendUrl +
        "/wopi/files/" +
        file.fileID +
        "?access_token=xxx&wdMobileHost=2&wdTtime=" +
        new Date().getTime();
      var filePreviewServer = _self.loadFilePreviewServer();
      var url = filePreviewServer + "/document/online/viewer?WOPISrc=" + encodeURIComponent(WOPISrc);
      var title = file.fileName;
      uni.navigateTo({
        url: "/uni_modules/w-app/pages/web-view/web-view?url=" + url + "&title=" + title,
      });
    },
    // 加载文件预览服务器地址
    loadFilePreviewServer: function () {
      var _self = this;
      if (_self.filePreviewServer) {
        return _self.filePreviewServer;
      }
      _self.filePreviewServer = uni.getStorageSync("filePreviewServer");
      if (_self.filePreviewServer) {
        return _self.filePreviewServer;
      }
      var key = "document.preview.path";
      var getTimestamp = new Date().getTime();
      uni.request({
        url: "/basicdata/system/param/get?key=" + key + "&timestamp=" + getTimestamp,
        method: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (success) {
          _self.filePreviewServer = success.data.data;
          uni.setStorageSync("filePreviewServer", _self.filePreviewServer);
        },
      });
    },
    // 返回文件操作列表
    getFileActions: function () {
      var _self = this;
      var actions = [];
      var fieldDefinition = _self.fieldDefinition;
      // 删除
      if (fieldDefinition.allowDelete) {
        actions.push({
          id: "delete",
          text: "删除",
        });
      }
      // 下载
      if (fieldDefinition.allowDownload) {
        actions.push({
          id: "download",
          text: "下载",
        });
      }
      return actions;
    },
    // 文件操作
    actionTap: function (event, file, action) {
      var _self = this;
      if (action.id == "delete") {
        _self.deleteFile(file);
      } else if (action.id == "download") {
        _self.downloadFile(file);
      }
    },
    // 删除文件
    deleteFile: function (file) {
      var _self = this;
      var _this = this;
      for (var index = 0; index < _this.files.length; index++) {
        if (_this.files[index].fileID == file.fileID) {
          _this.files.splice(index, 1);
        }
      }
      _self.dyformField.setValue(_self.files);
    },
    // 下载文件
    downloadFile: function (file) {
      uni.showLoading({
        title: "文件[" + file.fileName + "]下载中...",
      });
      uni.downloadFile({
        url: DOWNLOAD_FILE,
        success: (res) => {
          console.log("downloadFile success, res is", res);
          uni.hideLoading();
          uni.showToast({ title: "下载成功！" + res.tempFilePath });
        },
        fail: (err) => {
          console.log("downloadFile fail, err is:", err);
        },
      });
    },
    // 文件操作actionSheep
    onFileActionTap: function (event, file) {
      const _self = this;
      var itemList = [];
      _.each(_self.fileActions, function (action) {
        itemList.push(action.text);
      });
      uni.showActionSheet({
        itemList: itemList,
        success: (e) => {
          console.log(e.tapIndex);
          _self.actionTap(e, file, _self.fileActions[e.tapIndex]);
        },
      });
    },
  },
  computed: {
    isRequired: function () {
      return this.dyformField.isRequired();
    },
    // 图片附件
    isImageFileUpload: function () {
      return this.fieldDefinition.inputMode == "33";
    },
    // 可上传文件
    isAllowUpload: function () {
      return this.fieldDefinition.allowUpload;
    },
  },
};
</script>

<style lang="scss" scoped>
.w-file-upload {
  .field-collapse {
    background-color: $uni-bg-secondary-color;
  }
  .field-title {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
  }
  .file-count-summary {
    margin-top: 2px;
    margin-right: 10px;
    color: $uni-text-color;
  }
  .mini-btn-group {
    .icon {
      color: $uni-icon-color !important;
    }
  }
  .mini-btn {
    padding-bottom: 6px;
  }
  .mini-btn-left {
    margin-right: 10px;
  }
  .mini-btn-right {
    margin-right: 10px;
  }

  .file-item {
  }
  .file-info-container {
    margin-left: 15px;
    padding-top: 7px;
    padding-bottom: 8px;
  }
  .file-info-filename {
    font-size: 14px;
    color: black;
    line-height: 1.5;
  }
  .file-item-action-icon {
    width: 24px;
    padding-right: 11px;
  }
  .textarea-label {
    margin-left: 30px;
  }
}
</style>
