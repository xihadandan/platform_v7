<template>
  <view class="uni-w-simple-list-upload">
    <uni-w-file-picker
      v-model="fileList"
      ref="filePicker"
      fileMediatype="all"
      :title="title"
      :autoUpload="false"
      :fileExtname="accept.replace(/\./g, '')"
      :buttons="rowButtonsComputed"
      :delIcon="showFileDel"
      :limit="limitNum"
      :showProgress="showProgress"
      @select="onSelectDone"
      @reUploadFiles="batchUploadFiles"
      @actionTap="onActionTap"
      @delete="onClickDelFile"
    >
      <slot v-if="$slots.default || $slots.$default" />
      <block v-else>
        <uni-w-button
          v-if="editable"
          :type="uploadBtn.type || 'link'"
          :size="uploadBtn.size || 'small'"
          :icon="uploadBtn.hasIcon ? uploadBtn.icon || 'iconfont icon-ptkj-shangchuan' : ''"
          :text="uploadBtn.title || $t('WidgetFormFileUpload.upload', '上传附件')"
        ></uni-w-button>
      </block>
    </uni-w-file-picker>
    <view v-if="headerButtonComputed.length && ((onlyDownLoadAll && fileList.length > 1) || !onlyDownLoadAll)">
      <uni-w-button-group
        :buttons="headerButtonComputed"
        :gutter="8"
        :max="2"
        :replaceFields="replaceFields ? replaceFields : undefined"
        @click="onActionTap"
      ></uni-w-button-group>
    </view>
  </view>
</template>

<script>
import { storage } from "wellapp-uni-framework";
import { get_file_ext } from "@/packages/_/components/modified/uni-w-file-picker/components/uni-w-file-picker/utils.js";
import { isEqual, isFunction, every } from "lodash";
const UPLOAD_STATUS = {
  SUCCESS: "success",
  READY: "ready",
  UPLOADING: "uploading",
  DONE: "done",
  ERROR: "error",
  REMOVED: "removed",
};

export default {
  props: {
    fileIds: {
      type: [String, Array],
      default: "",
    },
    value: {
      type: [String, Array],
      default: "",
    },
    defaultFileList: {
      type: [Object, Array],
      default: () => [],
    },
    multiple: {
      type: Boolean,
      default: false,
    },
    title: {
      type: String,
      default: "",
    },
    // beforeUpload: {
    //   type: Function,
    //   default: undefined,
    // },
    fileAccept: {
      type: String,
      default: "",
    },
    uploadBtn: {
      type: Object,
      default: () => {
        return {
          hasIcon: true,
        };
      },
    },
    visibleTips: {
      type: String,
      default: "",
    },
    editable: {
      type: Boolean,
      default: true,
    },
    downloadTitle: {
      type: String,
      default: "",
    },
    separator: {
      type: String,
      default: ";",
    },
    relatedMaterial: {
      type: Object,
    },
    limitNum: {
      type: Number,
    },
    sizeLimit: {
      type: Number,
    },
    sizeLimitUnit: {
      type: String,
    },
    fileNameRepeat: {
      type: Boolean,
      default: true, // true可以重名
    },
    downloadTitle: {
      type: String,
      default: "",
    },
    downloadAllType: {
      type: String,
      default: "1", //1为压缩包，2为源文件
    },
    showProgress: {
      type: Boolean,
      default: true,
    },
    headerButtons: Array,
    buttons: Array,
    replaceFields: {
      type: Object,
      default: () => {
        return {
          type: "type",
          size: "size",
          icon: "icon",
          status: "status",
          title: "buttonName",
        };
      },
    },
  },
  data() {
    return {
      fileComponent: undefined,
      wellappBackendUrl: "",
      backendSaveFile: "",
      backendDownloadFile: "",
      savefilesAnnoymous: "", // 匿名保存
      downloadAnnoymous: "", // 匿名下载
      downloadPath: "",
      fileList: [],
      userNameMap: [], // 存放用户名
      materialAccept: [], // 关联材料可上传的文件类型
      curButton: undefined, // 当前按钮
      tempFiles: [], // 选择的文件
      showFileDel: false,
      newFileName: "", // 新名称
      filePreviewServer: "",
    };
  },
  watch: {
    // value: {
    //   handler(newVal, oldVal) {
    //     this.setValue(newVal);
    //   },
    //   immediate: true,
    // },
  },
  computed: {
    // 上传完成的文件
    uploadDoneList() {
      let list = [];
      if (this.fileComponent) {
        list = this.fileComponent.files.filter((item) => item.status === UPLOAD_STATUS.SUCCESS);
      }
      return list;
    },
    // 上传的文件类型
    accept() {
      let accepts = this.fileAccept;
      if (accepts && typeof accepts == "string") {
        accepts = accepts.split(",");
      }
      if (this.materialAccept.length) {
        accepts = this.materialAccept;
      }
      for (let i = 0, len = accepts.length; i < len; i++) {
        if (accepts[i].indexOf(".") != 0) {
          //修正文件后缀
          accepts[i] = "." + accepts[i];
        }
      }
      return accepts.length ? accepts.join(",") : "";
    },
    // 是否需要校验上传数量
    canCheckLimitNum() {
      let canCheck = false;

      if (this.curButton && this.curButton.button.code === "onClickReplace") {
        return false;
      }
      const fileLimitNum = this.limitNum;
      if (fileLimitNum) {
        canCheck = true;
      }
      return canCheck;
    },

    fileSizeLimit() {
      if (this.sizeLimit != null) {
        return parseInt(this.sizeLimit) * Math.pow(1024, ["KB", "MB", "GB"].indexOf(this.sizeLimitUnit) + 1);
      }
      return -1;
    },
    rowButtonsComputed() {
      let rowButton = [],
        btnShowType = "show";
      if (this.editable) {
        this.showFileDel = true;
        rowButton = this.simpleListRowButtons.filter((item) => {
          item.defaultFlag = item.code != "onClickDelFile";
          return item.defaultFlag;
        });
      } else {
        rowButton = this.simpleListRowButtons.filter((item) => {
          item.defaultFlag = btnShowType === item.btnShowType;
          return btnShowType === item.btnShowType;
        });
      }
      return rowButton;
    },
    simpleListRowButtons() {
      if (this.buttons) {
        return this.buttons.map((item) => {
          if (item.style) {
            if (item.style.type) {
              item.type = item.style.type;
            }
            if (item.style.icon) {
              item.icon = item.style.icon;
            }
            item.textHidden = item.style.textHidden;
          }
          if (!item.title) {
            item.title = item.buttonName;
          }
          return item;
        });
      }
      return [
        {
          buttonName: this.$t("WidgetFormFileUpload.button.onClickDownloadFile", "下载"),
          title: "下载",
          type: "link",
          loadding: false,
          code: "onClickDownloadFile",
          btnShowType: "show",
          icon: "pticon iconfont icon-ptkj-xiazai",
        },
        {
          buttonName: this.$t("WidgetFormFileUpload.button.onClickDelFile", "删除"),
          title: "删除",
          type: "link",
          loadding: false,
          code: "onClickDelFile",
          icon: "pticon iconfont icon-ptkj-shanchu",
        },
        {
          buttonName: this.$t("WidgetFormFileUpload.button.onClickPreviewFile", "预览"),
          title: "预览",
          type: "link",
          loadding: false,
          code: "onClickPreviewFile",
          btnShowType: "show",
          icon: "pticon iconfont icon-szgy-zonghechaxun",
        },
        {
          buttonName: this.$t("WidgetFormFileUpload.button.onClickCopyFilename", "复制名称"),
          title: "复制名称",
          type: "link",
          loadding: false,
          code: "onClickCopyFilename",
          btnShowType: "show",
          icon: "pticon iconfont icon-ptkj-fuzhi",
        },
      ];
    },
    headerButtonComputed() {
      if (this.headerButtons) {
        return this.headerButtons.map((item) => {
          if (item.style) {
            if (item.style.type) {
              item.type = item.style.type;
            }
            if (item.style.icon) {
              item.icon = item.style.icon;
            }
            item.textHidden = item.style.textHidden;
          }
          if (!item.title) {
            item.title = item.buttonName;
          }
          if (!item.type && item.code == "onClickAllDownload") {
            item.type = "primary";
          }
          return item;
        });
      }
      return [
        {
          buttonName: this.$t("WidgetFormFileUpload.button.onClickAllDownload", "全部下载"),
          title: "全部下载",
          type: "link",
          size: "small",
          code: "onClickAllDownload",
          btnShowType: "show",
          icon: "pticon iconfont icon-ptkj-xiazai",
        },
      ];
    },
    onlyDownLoadAll() {
      if (this.headerButtonComputed.length == 1 && this.headerButtonComputed[0].code == "onClickAllDownload") {
        return true;
      }
      return false;
    },
  },
  created() {
    // uni.showModal({
    //   title: "提示",
    //   content: storage.getAccessToken(),
    // });

    const jwtToken = storage.getAccessToken();
    const wellappBackendUrl = storage.getWellappBackendUrl();
    this.wellappBackendUrl = wellappBackendUrl;
    const mongoRep = "/proxy-repository/repository/file/mongo"; //以/proxy-repository开头的图片地址要通过fillAccessResourceUrl转换才可以正常显示

    const backendMongoRep = wellappBackendUrl + mongoRep;
    this.backendSaveFile = backendMongoRep + "/repository/file/mongo/savefiles?jwt=" + jwtToken;
    this.backendDownloadFile = backendMongoRep + "/download?jwt=" + jwtToken + "&fileID=";

    this.savefilesAnnoymous = storage.fillAccessResourceUrl(`${mongoRep}/savefiles`);
    this.downloadAnnoymous = `${wellappBackendUrl}/repository/file/mongo/download4ocx?fileID=`;
    this.downloadPath = `${mongoRep}/download?fileID=`;

    // 分片上传
    // repository/file/mongo/savefilesChunk

    this.setValue(this.value || this.fileIds);

    this.fetchRelatedMaterialAccept();
    this.loadFilePreviewServer();
  },
  mounted() {
    this.fileComponent = this.$refs.filePicker;
  },
  methods: {
    $t() {
      if (this.$i18n) {
        return this.$i18n.$t(this, ...arguments);
      }
      return arguments[1];
    },
    getValue() {
      return this.fileList;
    },
    setValue(value) {
      console.log("文件上传数据", value);
      if (value && value.length) {
        // 设置的对象只有fileID属性
        if (Object.keys(value[0]).length == 1) {
          this.loadLogicFileByFiles(value).then((logicFiles) => {
            this.setValue(logicFiles);
          });
        } else {
          this.fileList.splice(0, this.fileList.length);
          for (let index = 0; index < value.length; index++) {
            const item = value[index];
            const fileName = item.fileName || item.filename || item.name;
            const extname = get_file_ext(fileName).ext;
            this.fileList.push({
              name: fileName,
              uuid: item.fileID,
              url: this.downloadPath + item.fileID,
              fileID: item.fileID,
              dbFile: item,
              status: UPLOAD_STATUS.SUCCESS,
              size: item.fileSize || item.size,
              extname,
              // fileType: "image",  // item.contentType = "image/jpeg"
            });
          }
        }
      }
    },
    // 按钮功能转发
    onActionTap(e, button) {
      if (button && button.code) {
        if (this[button.code]) {
          this[button.code](e);
        } else {
          this.$emit("actionTap", e, button);
        }
      } else if (e && e.button && e.button.code) {
        if (this[e.button.code]) {
          this[e.button.code](e);
        } else {
          this.$emit("actionTap", e, e.button);
        }
      }
      this.$emit("actionTapAfter", e, e.button);
    },
    // 点击删除
    onClickDelFile({ index, tempFile, tempFilePath, files }) {
      this.fileComponent.files = files;
      this.fileList = this.fileComponent.files;
      this.$emit("change", {
        tempFiles: this.fileComponent.files,
      });
    },
    // 点击预览
    onClickPreviewFile({ file }) {
      let WOPISrc =
        this.wellappBackendUrl +
        "/wopi/files/" +
        file.fileID +
        "?access_token=xxx&wdMobileHost=2&wdTtime=" +
        new Date().getTime();
      const filePreviewServer = this.loadFilePreviewServer();
      const url = filePreviewServer + "/document/online/viewer?WOPISrc=" + encodeURIComponent(WOPISrc);
      uni.navigateTo({
        url: "/packages/_/pages/web-view/web-view?url=" + url + "&title=" + file.name,
      });
    },
    // 点击下载
    onClickDownloadFile(params) {
      const file = params.file ? params.file : params;
      if (file.status != UPLOAD_STATUS.SUCCESS) {
        uni.showToast({
          icon: "error",
          title: this.$t("WidgetFormFileUpload.message.fileUploadingPleaseWait", "文件上传中, 请稍后"),
        });
        return;
      }
      // #ifdef H5
      this.downloadFileByWeb(file);
      // #endif
      // #ifndef H5
      this.downloadFile(file);
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
      // var cnt = 0;
      // var timer = setInterval(function () {
      //   console.log(cnt);
      //   if (cnt++ == 100) {
      //     clearInterval(timer);
      //     iframe.remove();
      //     return;
      //   }
      //   var iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
      //   if (iframeDoc.readyState == "complete" || iframeDoc.readyState == "interactive") {
      //     var _text = iframeDoc.body.innerText;
      //     if (_text && _text.indexOf("No such file") != -1) {
      //       //需要等待后端响应无文件的异常
      //       clearInterval(timer);
      //       iframe.remove();
      //       if (_text.indexOf("try later") != -1) {
      //         setTimeout(function () {
      //           _this.downloadFileByWeb(file); //重新下载
      //         }, 2000);
      //       }
      //     }
      //     return;
      //   }
      // }, 1000);
    },
    // 下载文件
    downloadFile(file, url) {
      let fileName = file.fileName || file.name;
      uni.showLoading({
        title: this.$t(
          "WidgetFormFileUpload.message.fileDownLoadingPleaseWait",
          { fileName: fileName },
          "文件" + fileName + "下载中"
        ),
      });

      uni.downloadFile({
        url: url || `${this.downloadAnnoymous}${file.fileID}`,
        success: (res) => {
          console.log("downloadFile success, res is", res);
          uni.hideLoading();
          uni.showToast({
            title: this.$t("WidgetFormFileUpload.message.downloadSuccess", "下载成功！") + res.tempFilePath,
          });
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
    // 全部下载
    onClickAllDownload() {
      let ids = [],
        fileName = this.downloadTitle;
      this.fileList.forEach((item) => {
        if (!fileName) {
          let _fileName = item.name || item.dbFile.filename || item.dbFile.fileName;
          fileName = this.$t("WidgetFormFileUpload.fileNames", { fileName: _fileName }, _fileName + "等文件");
        }
        ids.push({
          fileID: item.dbFile.fileID,
        });
      });
      if (ids.length) {
        // 1为压缩包，2为源文件，默认1
        if (this.downloadAllType == "2") {
          //bug：手机浏览器批量下载源文件有问题（未解决）
          this.fileList.forEach((file) => {
            // #ifdef H5
            this.downloadFileByWeb(file);
            // #endif
            // #ifndef H5
            this.downloadFile(file);
            // #endif
          });
        } else {
          // #ifdef H5
          this.downloadFileByWeb({
            url: `/repository/file/mongo/downAllFiles?fileIDs=${encodeURIComponent(
              JSON.stringify(ids)
            )}&includeFolder=false&fileName=${fileName}`,
          });
          // #endif
          // #ifndef H5
          this.downloadFile(
            {
              fileName: fileName,
            },
            `/repository/file/mongo/downAllFiles?fileIDs=${encodeURIComponent(
              JSON.stringify(ids)
            )}&includeFolder=false&fileName=${fileName}`
          );
          // #endif
        }
      }
    },
    // 复制名称
    onClickCopyFilename({ file }) {
      let _self = this;
      uni.setClipboardData({
        data: file.name,
        showToast: false,
        success: function (args) {
          uni.showToast({
            title: _self.$t("WidgetFormFileUpload.message.copyDone", "已复制"),
            icon: "success",
            duration: 2000,
          });
        },
      });
    },
    // 选择文件前
    onBeforeChoose(args) {
      return new Promise((resolve, reject) => {
        resolve();
        // let valid = this.checkLimitNum();
        // if (valid) {
        //   resolve();
        // } else {
        //   reject();
        // }
      });
    },
    // 检查上传数量
    checkLimitNum({ files = [], index } = {}) {
      let valid = true;

      if (this.canCheckLimitNum) {
        const fileLimitNum = this.limitNum;

        const isLimit = this.uploadDoneList.length + files.length > fileLimitNum;
        const indexOfFile = this.uploadDoneList.length + index;

        if (fileLimitNum === this.uploadDoneList.length) {
          valid = false;
        } else if (isLimit && indexOfFile >= fileLimitNum) {
          valid = false;
        }
        if (!valid) {
          uni.showToast({
            icon: "error",
            title: this.$t("WidgetFormFileUpload.message.uploadLimit", "上传文件已达限制数量") + "：" + fileLimitNum,
          });
        }
      }
      return valid;
    },
    // 检查文件类型
    checkAcceptFile(file) {
      let accpet = this.accept;
      if (this.materialAccept.length) {
        accpet = this.materialAccept;
      }
      if (!accpet.length) {
        return true;
      }
      const fileName = file.name;
      const suffix = fileName.substring(fileName.lastIndexOf("."));
      if (!accpet.includes(suffix)) {
        uni.showToast({
          icon: "error",
          title: this.$t("WidgetFormFileUpload.message.notAllowFile", "文件不允许"),
        });
        return false;
      }
      return true;
    },
    // 检查允许上传重名
    checkFileNameRepeat(file, fileList = this.uploadDoneList) {
      if (!this.fileNameRepeat) {
        const fileIndex = fileList.findIndex((item) => item.name === file.name);
        if (fileIndex !== -1) {
          uni.showToast({
            icon: "error",
            title: this.$t("WidgetFormFileUpload.message.notAllowUploadSameName", "不允许上传重名"),
          });
          return false;
        }
      }
      return true;
    },
    // 检查单个允许上传大小
    checkOneSize(file) {
      if (this.fileSizeLimit != -1 && this.fileSizeLimit < file.size) {
        uni.showToast({
          icon: "error",
          title: `${this.$t("WidgetFormFileUpload.message.fileSizeLimit", "文件超过上传限制")}:${
            this.sizeLimit + this.sizeLimitUnit
          }`,
        });
        return false;
      }
      return true;
    },
    // 检查像素（忽略图片）
    checkPixel(file) {
      if (false && this.type == "picture" && this.pixelCheck) {
        const pixelWidth = this.pixelWidth,
          pixelHeight = this.pixelHeight;
        const valid = file.image.width == pixelWidth && file.image.height == pixelHeight;
        if (!valid) {
          uni.showToast({
            icon: "error",
            title: `${this.$t(
              "WidgetFormFileUpload.message.pixelMustMatch",
              "像素必须满足"
            )}${pixelWidth}px*${pixelHeight}px`,
          });
          return false;
        }
      }
      return true;
    },
    // 上传文件前
    beforeUpload(file, index, files) {
      return new Promise((resolve, reject) => {
        index = this.tempFiles.findIndex((item) => item.uuid === file.uuid);

        let pass = true;
        const allow = this.checkAcceptFile(file);
        if (!allow) {
          pass = false;
        } else if (
          !this.checkLimitNum({
            index,
            files,
          })
        ) {
          pass = false;
        } else if (!this.checkFileNameRepeat(file)) {
          pass = false;
        } else if (!this.checkPixel(file)) {
          pass = false;
        } else if (!this.checkOneSize(file)) {
          pass = false;
        }

        if (pass) {
          resolve({
            file,
            index,
          });
        } else {
          this.tempFiles.splice(index, 1);
          const hasIndex = this.fileComponent.files.findIndex((item) => item.uuid === file.uuid);
          this.fileComponent.files.splice(hasIndex, 1);
          reject({
            file,
            index,
          });
        }
      });
    },
    // 上传文件
    uploadFile(file, tempIndex, files) {
      this.beforeUpload(file, tempIndex, files).then(
        ({ file: tempFile, index }) => {
          let curFile = tempFile;
          const filePath = tempFile.path;

          const uploadTask = uni.uploadFile({
            url: this.savefilesAnnoymous,
            filePath,
            name: "fileUpload",
            formData: {},
            success: (uploadFileRes) => {
              console.log("upload success", uploadFileRes);
              let result = JSON.parse(uploadFileRes.data);
              if (result.code == 0 && result.data) {
                let dbFile = result.data[0];

                delete curFile.errMsg;
                curFile.url = this.downloadPath + dbFile.fileID;
                // curFile.url = filePath; // 本地地址
                curFile.dbFile = dbFile;
                curFile.fileID = dbFile.fileID;
                this.fileComponent.setSuccessAndError([curFile]);
                this.addFileList(dbFile);
              }
            },
            fail: (err) => {
              console.log("upload fail", err);
              curFile.errMsg = "request:fail";
              this.fileComponent.setSuccessAndError([curFile]);
            },
          });

          uploadTask.onProgressUpdate((res) => {
            this.fileComponent.setProgress({
              index,
              loaded: res.totalBytesSent,
              total: res.totalBytesExpectedToSend,
              tempFilePath: filePath,
              tempFile,
            });
          });
        },
        (error) => {}
      );
    },
    // 批量上传
    async batchUploadFiles(files) {
      this.tempFiles = JSON.parse(JSON.stringify(files));
      for (let index = 0; index < files.length; index++) {
        const file = files[index];
        await this.uploadFile(file, index, files);
      }
    },
    // 选择文件后触发
    onSelectDone(args) {
      console.log("选择文件：", args);
      const { tempFiles, tempFilePaths } = args;

      this.batchUploadFiles(tempFiles);
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
    getChunkSize() {
      //切片每次上传的块最大值
      var __size = (function (name) {
        var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
        return arr ? decodeURIComponent(arr[2]) : null;
      })("fileupload.maxChunkSize");

      this.chunkSize = __size ? parseInt(__size) : undefined;
      return this.chunkSize;
    },
    loadLogicFileByFiles(files) {
      return this.$axios
        .get(`/repository/file/mongo/getNonioFiles?fileID=${files.map((file) => file.fileID).join(";")}`)
        .then(({ data: result }) => {
          if (result.data && result.data.length) {
            return result.data;
          }
          return files;
        })
        .catch(() => {
          return files;
        });
    },
    fetchRelatedMaterialAccept() {
      const _this = this;
      let relatedMaterial = _this.relatedMaterial;
      if (!relatedMaterial || !relatedMaterial.enabled) {
        return;
      }

      let materialCodes = relatedMaterial.materialCodes;
      let materialCodeFields = relatedMaterial.materialCodeFields;
      // 指定材料编码字段
      if (relatedMaterial.way == "2") {
        // materialCodes = [];
        // materialCodeFields.forEach((codeField) => {
        //   let codeValue = _this.formData[codeField];
        //   if (codeValue) {
        //     materialCodes.push(codeValue);
        //   }
        //   // 监听材料编码值变更
        //   if (!_this.watchRelatedMaterialCode) {
        //     _this.$watch("formData." + codeField, (newValue, oldValue) => {
        //       _this.fetchRelatedMaterialAccept();
        //     });
        //     _this.watchRelatedMaterialCode = true;
        //   }
        // });
      }

      if (!materialCodes.length) {
        return;
      }

      this.$axios.get(`/api/material/definition/listFormatByCodes?codes=${materialCodes}`).then(({ data: result }) => {
        _this.materialAccept = result.data || [];
      });
    },
    onSuccess(args) {
      this.fileList = this.fileComponent.files;
      this.$emit("change", {
        tempFiles: this.fileList,
      });
      this.$emit("success", args);
    },
    addFileList(dbFile) {
      this.fileList = this.fileComponent.files;
      this.$emit("change", {
        tempFiles: this.fileList,
      });
    },
    filesStatusValidate(isConfirm, callback) {
      if (this.fileComponent.files && this.fileComponent.files.length) {
        let uploading = false;
        let error = false;
        let ispass = every(this.fileComponent.files, (item) => {
          if (item.status == UPLOAD_STATUS.UPLOADING || item.status == UPLOAD_STATUS.READY) {
            uploading = true;
          } else if (item.status == UPLOAD_STATUS.ERROR) {
            error = true;
          }
          // 状态存在未完成的附件
          return !item.status || item.status == UPLOAD_STATUS.DONE || item.status == UPLOAD_STATUS.SUCCESS;
        });
        if (!ispass) {
          let msg = "";
          if (error) {
            msg = this.$t("WidgetFormFileUpload.validateError.error", "存在上传异常的附件,请确认后继续");
          } else if (uploading) {
            msg = this.$t("WidgetFormFileUpload.validateError.uploading", "正在上传附件，请稍后");
          } else {
            msg = this.$t("WidgetFormFileUpload.validateError.error", "存在上传异常的附件,请确认后继续");
          }
          uni.showModal({
            content: msg,
            showCancel: isConfirm ? true : false,
            confirmText: this.$t("global.confirm", "确认"),
            cancelText: this.$t("global.cancel", "取消"),
            success: function (res) {
              if (res.confirm) {
                if (isFunction(callback)) {
                  callback();
                }
              } else if (res.cancel) {
              }
            },
          });
          return false;
        }
      }
      return true;
    },
  },
};
</script>
