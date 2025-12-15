<template>
  <uni-forms-item :label="itemLabel" :name="formModelItemProp" :label-position="widget.configuration.labelPosition">
    <view
      class="uni-flex uni-column w-form-upload-simple"
      v-if="widget.configuration.type === 'simpleList' || widget.configuration.type === 'advancedList'"
    >
      <uni-w-file-picker
        v-if="showSimpleUpload"
        v-model="fileList"
        ref="filePicker"
        fileMediatype="all"
        :autoUpload="false"
        :fileExtname="accept.replaceAll('.', '')"
        :buttons="rowButtonsComputed"
        :delIcon="showFileDel"
        @select="onSelectDone"
        @reUploadFiles="batchUploadFiles"
        @delete="onDelete"
        @actionTap="onActionTap"
      >
        <template v-if="widget.configuration.fileSourceExtend && editable">
          <uni-w-data-checkbox
            :multiple="false"
            mode="button"
            class="w-form-radio-outline"
            :localdata="fileSourceOptions"
            @change="onFileSourceButtonClick"
          />
        </template>
        <button v-else-if="editable" type="primary" plain="true" size="mini">
          <uni-icons type="upload" size="14" color="var(--color-primary)"></uni-icons>
          <text style="margin-left: 5px"> 上传 </text>
        </button>
        <view class="widget-file-upload-tips" v-if="widget.configuration.description">{{
          widget.configuration.description
        }}</view>
      </uni-w-file-picker>
      <view v-if="fileList.length > 0" class="upload-simple-bottom">
        <text class="upload-simple-count">共{{ fileList.length }}个附件</text>
        <file-buttons :buttons="headerButtonComputed" @click="onActionTap" style="flex: 1" />
      </view>
    </view>
    <template v-else-if="widget.configuration.type === 'picture'">
      <uni-w-file-picker
        v-model="fileList"
        ref="filePicker"
        fileMediatype="image"
        :autoUpload="false"
        :disablePreview="!showPicturePreview"
        :delIcon="showUploadListPicture.showRemoveIcon"
        :addIcon="showPictureAdd"
        :downloadIcon="showUploadListPicture.showDownloadIcon"
        :beforeChoose="onBeforeChoose"
        :fileExtname="accept.replaceAll('.', '')"
        @select="onSelectDone"
        @reUploadFiles="batchUploadFiles"
        @delete="onDelete"
      />
    </template>
    <uni-popup ref="reNamePopup" type="dialog">
      <uni-popup-dialog
        mode="input"
        title="重命名"
        :value="newFileName"
        placeholder="请输入内容"
        @confirm="confirmRename"
      />
    </uni-popup>
  </uni-forms-item>
</template>

<script>
import { fetchFileSource, loadLogicFileByFiles, fetchPreviewServer, fetchDyformTitle } from "./api";
import formElement from "../w-dyform/form-element.mixin";
import { storage, utils as frameworkUtils } from "wellapp-uni-framework";
import { get_file_ext } from "@/uni_modules/uni-w-file-picker/components/uni-w-file-picker/utils.js";
import FileButtons from "./file-buttons.vue";

const UPLOAD_STATUS = {
  SUCCESS: "success",
  READY: "ready",
  UPLOADING: "uploading",
  DONE: "done",
  ERROR: "error",
  REMOVED: "removed",
};

export default {
  mixins: [formElement],
  components: {
    FileButtons,
  },
  data() {
    return {
      fileComponent: undefined,
      wellappBackendUrl: "",
      backendSaveFile: "",
      backendDownloadFile: "",
      savefilesAnnoymous: "", // 匿名保存
      downloadAnnoymous: "", // 匿名下载
      downloadAllAnnoymous: "", // 批量匿名下载
      downloadPath: "",
      fileList: [],

      userNameMap: [], // 存放用户名
      materialAccept: [], // 关联材料可上传的文件类型

      curButton: undefined, // 当前按钮
      tempFiles: [], // 选择的文件
      showFileDel: false,
      newFileName: "", // 新名称
      previewServer: "", // 预览服务
      fileSourceOptions: [], // 附件来源
      rowSelection: {
        columnWidth: 30,
        selectedRowKeys: [],
        selectedRows: [],
        onChange: this.onSelectFileRowChange,
      },
    };
  },
  computed: {
    // 简易视图是否显示上传
    showSimpleUpload() {
      let showUpload = true;
      const headerButton = this.widget.configuration.headerButton;
      if (headerButton && headerButton.length) {
        const uploadBtn = headerButton.find((item) => item.code === "onClickUpload");
        if (uploadBtn) {
          showUpload = uploadBtn.defaultFlag;
        }
      }
      return showUpload;
    },
    // 头部按钮
    headerButtonComputed() {
      let headerButton = [],
        btnShowType = "show";
      if (this.editable) {
        // 编辑状态下 编辑类和显示类按钮都显示，除非按钮关闭
        headerButton = this.widget.configuration.headerButton.filter((item) => {
          return item.defaultFlag;
        });
      } else {
        // 不可编辑状态下 只能显示“显示类”按钮
        headerButton = this.widget.configuration.headerButton.filter((item) => {
          return item.defaultFlag && btnShowType === item.btnShowType;
        });
      }
      if (this.widget.configuration.type == "simpleList" && !this.designMode) {
        headerButton = headerButton.filter((item) => {
          return item.code != "onClickUpload";
        });
      }
      return headerButton;
    },
    // 显示图片添加
    showPictureAdd() {
      let show = true;
      const headerButton = this.widget.configuration.headerButton;
      show = headerButton[0].defaultFlag;
      if (!this.editable) {
        // 不可编辑状态下不显示添加
        show = false;
      } else if (this.isReplaceImgByLimitOne) {
        show = false;
      }
      return show;
    },
    // 图片是否能预览
    showPicturePreview() {
      let preview = false;
      const rowButton = this.widget.configuration.rowButton;
      if (rowButton && rowButton.length) {
        const previewBtn = rowButton.find((item) => item.code.indexOf("Preview") > -1);
        preview = previewBtn.defaultFlag;
      }
      return preview;
    },
    // 图片按钮
    showUploadListPicture() {
      let showUploadList = { showPreviewIcon: false, showDownloadIcon: false, showRemoveIcon: false };
      const rowButton = this.widget.configuration.rowButton;
      if (rowButton && rowButton.length > 0) {
        showUploadList.showDownloadIcon = rowButton[1].defaultFlag;
        showUploadList.showRemoveIcon = rowButton[2].defaultFlag;
      }
      if (!this.editable) {
        // 不可编辑状态下不显示删除
        showUploadList.showRemoveIcon = false;
      } else if (this.isReplaceImgByLimitOne) {
        showUploadList.showUploadIcon = true;
      }
      return showUploadList;
    },
    // 从图片库添加
    pictureForLib() {
      return this.widget.configuration.type == "picture" && this.widget.configuration.pictureUploadMode == "lib";
    },
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
      let accepts = this.widget.configuration.accept;
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
      const fileLimitNum = this.widget.configuration.fileLimitNum;
      if (fileLimitNum) {
        canCheck = true;
      }
      return canCheck;
    },
    // 行按钮
    rowButtonsComputed() {
      let rowButton = [],
        btnShowType = "show";
      if (this.editable) {
        rowButton = this.widget.configuration.rowButton.filter((item) => {
          return item.defaultFlag;
        });
      } else {
        rowButton = this.widget.configuration.rowButton.filter((item) => {
          return item.defaultFlag && btnShowType === item.btnShowType;
        });
      }
      const hasIndex = rowButton.findIndex((item) => item.code === "onClickDelFile");
      if (hasIndex !== -1) {
        this.showFileDel = true;
        // rowButton.splice(hasIndex, 1);
      }
      return rowButton;
    },
    fileSizeLimit() {
      if (this.widget.configuration.fileSizeLimit != null) {
        return (
          parseInt(this.widget.configuration.fileSizeLimit) *
          Math.pow(1024, ["KB", "MB", "G"].indexOf(this.widget.configuration.fileSizeLimitUnit) + 1)
        );
      }
      return -1;
    },
    isReplaceImgByLimitOne() {
      return (
        this.widget.configuration.type == "picture" &&
        this.widget.configuration.fileLimitNum === 1 &&
        this.fileList.length != 0
      );
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
    const mongoRep = "/repository/file/mongo";

    const backendMongoRep = wellappBackendUrl + mongoRep;
    this.backendSaveFile = backendMongoRep + "/savefiles?jwt=" + jwtToken;
    this.backendDownloadFile = backendMongoRep + "/download?jwt=" + jwtToken + "&fileID=";

    this.savefilesAnnoymous = `${backendMongoRep}/savefiles`;
    this.downloadAnnoymous = `${backendMongoRep}/download4ocx`;
    this.downloadAllAnnoymous = `${backendMongoRep}/downAllFiles4ocx`;
    this.downloadPath = `${mongoRep}/download?fileID=`;

    // 分片上传
    // repository/file/mongo/savefilesChunk

    this.setValue();

    // #ifdef H5
    this.getChunkSize();
    // #endif

    this.getPreviewServer();
    this.fetchRelatedMaterialAccept();
    if (this.widget.configuration.fileSourceExtend) {
      // 开启附件来源扩展
      this.getFileSource();
    }
  },
  mounted() {
    this.fileComponent = this.$refs.filePicker;
  },
  methods: {
    setValue(value = this.formData[this.fieldCode]) {
      console.log("文件上传数据", value);
      if (value && value.length) {
        // 设置的对象只有fileID属性
        if (Object.keys(value[0]).length == 1) {
          loadLogicFileByFiles(value).then((logicFiles) => {
            this.setValue(logicFiles);
          });
        } else {
          for (let index = 0; index < value.length; index++) {
            const item = value[index];
            const fileName = item.fileName || item.filename;
            const extname = get_file_ext(fileName).ext;
            this.fileList.push({
              name: fileName,
              uuid: item.fileID,
              url: this.downloadPath + item.fileID,
              fileID: item.fileID,
              dbFile: item,
              status: UPLOAD_STATUS.SUCCESS,
              size: item.fileSize,

              extname,
              // fileType: "image",  // item.contentType = "image/jpeg"
            });
          }
        }
      }
    },
    // 按钮功能转发
    onActionTap(args) {
      this.curButton = args;
      let { button } = args;
      if (button && button.code) {
        if (this[button.code]) {
          this[button.code](args);
        }
      }
    },
    // 点击重命名
    onClickRename() {
      this.$refs.reNamePopup.open();
      const file = this.curButton.file;
      this.newFileName = file.name.substring(0, file.name.lastIndexOf("."));
    },
    // 确认重命名
    confirmRename(value) {
      this.newFileName = value;
      const file = this.curButton.file;
      const oldFileName = file.name;
      const fileIndex = this.uploadDoneList.findIndex((item) => {
        const curName = item.name.substring(0, item.name.lastIndexOf("."));
        if (curName === this.newFileName && file.uuid !== item.uuid) {
          return true;
        }
        return false;
      });
      if (fileIndex !== -1) {
        uni.showToast({ icon: "error", title: `文件${this.newFileName}已经存在，请重新输入！` });
        return;
      }
      const type = oldFileName.substring(oldFileName.lastIndexOf("."));
      const newFileName = this.newFileName + type;
      file.name = newFileName;
      file.dbFile.fileName = newFileName;
      this.$refs.reNamePopup.close();
      this.emitChange();
    },
    // 点击删除
    onClickDelFile({ fileIndex }) {
      this.fileList.splice(fileIndex, 1);
      this.delFieldValue(fileIndex);
    },
    // 点击预览
    onClickPreviewFile({ file }) {
      let WOPISrc =
        this.wellappBackendUrl +
        "/wopi/files/" +
        file.fileID +
        "?access_token=xxx&wdMobileHost=2&wdTtime=" +
        new Date().getTime();
      const url = this.previewServer + "/document/online/viewer?WOPISrc=" + encodeURIComponent(WOPISrc);
      uni.navigateTo({
        url: "/uni_modules/w-app/pages/web-view/web-view?url=" + url + "&title=" + file.name,
      });
    },
    // 点击下载
    onClickDownloadFile(params) {
      const file = params.file ? params.file : params;
      if (file.status != UPLOAD_STATUS.SUCCESS) {
        uni.showToast({ icon: "error", title: "上传中，请稍后" });
        return;
      }
      // #ifdef H5
      const downloadUrl = `${this.downloadAnnoymous}?fileID=${file.fileID}`;
      this.downloadFileByWeb(downloadUrl);
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
    downloadFileByWeb(url) {
      var _this = this;
      var hiddenIFrameID = "hiddenDownloader" + Date.now();
      var iframe = document.createElement("iframe");
      iframe.id = hiddenIFrameID;
      iframe.style.display = "none";
      document.body.appendChild(iframe);
      iframe.src = url;

      var cnt = 0;
      var timer = setInterval(function () {
        if (cnt++ == 100) {
          clearInterval(timer);
          iframe.remove();
          return;
        }
        var iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
        if (iframeDoc.readyState == "complete" || iframeDoc.readyState == "interactive") {
          var _text = iframeDoc.body.innerText;
          if (_text && _text.indexOf("No such file") != -1) {
            //需要等待后端响应无文件的异常
            clearInterval(timer);
            iframe.remove();
            if (_text.indexOf("try later") != -1) {
              setTimeout(function () {
                _this.downloadFileByWeb(url); //重新下载
              }, 2000);
            }
          }
          return;
        }
      }, 1000);
    },
    // 下载文件
    downloadFile(file) {
      uni.showLoading({
        title: `文件${file.fileName || file.name}下载中...`,
      });

      uni.downloadFile({
        url: `${this.downloadAnnoymous}?fileID=${file.fileID}`,
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
    // 复制名称
    onClickCopyFilename({ file }) {
      uni.setClipboardData({
        data: file.name,
        success: function (args) {
          console.log("success", args);
        },
      });
    },
    // 上移
    onClickMoveUp() {
      this.swapRows("forward");
    },
    // 下移
    onClickMoveDown() {
      this.swapRows();
    },
    swapRows(direction) {
      let ids = [];
      if (this.rowSelection.selectedRows.length) {
        for (let i = 0, len = this.rowSelection.selectedRows.length; i < len; i++) {
          ids.push(this.rowSelection.selectedRows[i].uuid);
        }
      } else {
        ids = [this.curButton.file.uuid];
      }
      frameworkUtils.swapArrayElements(
        ids,
        this.fileList,
        function (a, b) {
          return a == b.uuid;
        },
        direction,
        () => {}
      );
      frameworkUtils.swapArrayElements(
        ids,
        this.formData[this.fieldCode],
        function (a, b) {
          return a == b.fileID;
        },
        direction,
        () => {}
      );
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
        const fileLimitNum = this.widget.configuration.fileLimitNum;

        const isLimit = this.uploadDoneList.length + files.length > fileLimitNum;
        const indexOfFile = this.uploadDoneList.length + index;

        if (fileLimitNum === this.uploadDoneList.length) {
          valid = false;
        } else if (isLimit && indexOfFile >= fileLimitNum) {
          valid = false;
        }
        if (!valid) {
          uni.showToast({ icon: "error", title: `上传文件已达限制数量：${fileLimitNum}` });
        }
      }
      return valid;
    },
    // 检查文件类型
    checkAcceptFile(file) {
      let accpet = this.widget.configuration.accept;
      if (this.materialAccept.length) {
        accpet = this.materialAccept;
      }
      if (!accpet.length) {
        return true;
      }
      const fileName = file.name;
      const suffix = fileName.substring(fileName.lastIndexOf("."));
      if (!accpet.includes(suffix)) {
        uni.showToast({ icon: "error", title: "文件不允许" });
        return false;
      }
      return true;
    },
    // 检查允许上传重名
    checkFileNameRepeat(file, fileList = this.uploadDoneList) {
      if (!this.widget.configuration.fileNameRepeat) {
        const fileIndex = fileList.findIndex((item) => item.name === file.name);
        if (fileIndex !== -1) {
          uni.showToast({ icon: "error", title: "不允许上传重名" });
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
          title: `文件超过上传限制:${
            this.widget.configuration.fileSizeLimit + this.widget.configuration.fileSizeLimitUnit
          }`,
        });
        return false;
      }
      return true;
    },
    // 检查像素
    checkPixel(file) {
      if (this.widget.configuration.type == "picture" && this.widget.configuration.pixelCheck) {
        const pixelWidth = this.widget.configuration.pixelWidth,
          pixelHeight = this.widget.configuration.pixelHeight;
        const valid = file.image.width == pixelWidth && file.image.height == pixelHeight;
        if (!valid) {
          uni.showToast({ icon: "error", title: `像素必须满足${pixelWidth}px*${pixelHeight}px` });
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
        } else if (!this.checkLimitNum({ index, files })) {
          pass = false;
        } else if (!this.checkFileNameRepeat(file)) {
          pass = false;
        } else if (!this.checkPixel(file)) {
          pass = false;
        } else if (!this.checkOneSize(file)) {
          pass = false;
        }

        if (pass) {
          resolve({ file, index });
        } else {
          this.tempFiles.splice(index, 1);
          const hasIndex = this.fileComponent.files.findIndex((item) => item.uuid === file.uuid);
          this.fileComponent.files.splice(hasIndex, 1);
          reject({ file, index });
        }
      });
    },
    // 上传文件
    uploadFile(file, tempIndex, files) {
      this.beforeUpload(file, tempIndex, files).then(
        ({ file: tempFile, index }) => {
          const filePath = tempFile.path;

          const uploadTask = uni.uploadFile({
            url: this.backendSaveFile,
            filePath,
            name: "fileUpload",
            formData: {},
            success: (uploadFileRes) => {
              console.log("upload success", uploadFileRes);
              let result = JSON.parse(uploadFileRes.data);
              if (result.code == 0 && result.data) {
                let dbFile = result.data[0];

                delete tempFile.errMsg;
                tempFile.url = this.downloadPath + dbFile.fileID;
                tempFile.dbFile = dbFile;
                tempFile.fileID = dbFile.fileID;

                this.fileComponent.setSuccessAndError([tempFile]);
                this.addFieldValue(dbFile);
              }
            },
            fail: (err) => {
              console.log("upload fail", err);
              tempFile.errMsg = "request:fail";
              this.fileComponent.setSuccessAndError([tempFile]);
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
    // 全部下载
    onClickAllDownload() {
      let ids = [];
      this.uploadDoneList.forEach((item) => {
        if (this.widget.configuration.downloadAllType == "2") {
          // 下载方式-源文件
          this.onClickDownloadFile(item);
        } else {
          ids.push({
            fileID: item.dbFile.fileID,
          });
        }
      });
      if (this.widget.configuration.downloadAllType == "1") {
        // 下载方式-压缩包
        this.batchDownloadByPackage(ids);
      }
    },
    // 批量下载方式-通过压缩包
    batchDownloadByPackage(ids) {
      this.getPackageName(ids).then((res) => {
        this.downloadFileByWeb(
          `${this.downloadAllAnnoymous}?fileIDs=${encodeURIComponent(
            JSON.stringify(res.ids)
          )}&includeFolder=false&fileName=${res.fileName}`
        );
      });
    },
    // 获取批量下载文件名称
    getPackageName(ids) {
      return new Promise((resolve, reject) => {
        let fileName = `${this.widget.configuration.name}`;
        if (this.$workView && this.$workView.workData.title) {
          fileName += `-${this.$workView.workData.title}`;
          resolve({ ids, fileName });
        } else {
          let dyFormData = this.dyFormData;
          if (!dyFormData && this.form) {
            dyFormData = {
              formUuid: this.form.formUuid,
              formDatas: {},
            };
            dyFormData[this.form.formUuid] = JSON.parse(JSON.stringify(this.form.formData));
          }
          if (dyFormData) {
            fetchDyformTitle(dyFormData).then((dyformTitle) => {
              fileName += `-${dyformTitle}`;
              resolve({ ids, fileName });
            });
          } else {
            resolve({ ids, fileName });
          }
        }
      });
    },
    // 选择文件后触发
    onSelectDone(args) {
      console.log("选择文件：", args);
      const { tempFiles, tempFilePaths } = args;

      this.batchUploadFiles(tempFiles);
    },
    // 设置字段值
    _setFieldValue(value) {
      this.formData[this.fieldCode] = value;
    },
    // 添加字段值
    addFieldValue(dbFile) {
      if (!this.formData[this.fieldCode]) {
        this.formData[this.fieldCode] = [];
      }
      this.formData[this.fieldCode].push(dbFile);
    },
    // 删除字段值
    delFieldValue(index) {
      this.formData[this.fieldCode].splice(index, 1);
    },
    // 删除后触发
    onDelete(args) {
      const { index, tempFile, tempFilePath } = args;
      this.delFieldValue(index);
    },
    // 获取上传快大小
    getChunkSize() {
      //切片每次上传的块最大值
      const _size = (function (name) {
        const arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
        return arr ? decodeURIComponent(arr[2]) : null;
      })("fileupload.maxChunkSize");

      this.chunkSize = _size ? parseInt(_size) : 1 * 1024 * 1024;
      return this.chunkSize;
    },
    // 点击附件来源按钮
    onFileSourceButtonClick({ detail }) {
      const { value, data: button } = detail;
      if ("local_upload" == button.code) {
        this.$refs.filePicker.choose();
      } else if (button.jsModule && /^{.*}$/gs.test(button.jsModule)) {
        let buttonJsModule = JSON.parse(button.jsModule);
        if (this.__developScript && this.__developScript[buttonJsModule.jsModule]) {
          let developJsInstance = new this.__developScript[buttonJsModule.jsModule].default(this);
          if (developJsInstance.chooseFile) {
            developJsInstance.chooseFile();
          }
        }
      }
    },
    // 获取文件来源
    getFileSource() {
      fetchFileSource().then((res) => {
        console.log(res);

        let fileSourceOptions = [];
        let fileSouces = res || [];
        let fileSourceIds = this.widget.configuration.fileSourceIds;
        fileSouces.forEach((fileSource) => {
          if (fileSourceIds.findIndex((sourceId) => sourceId == fileSource.uuid) > -1) {
            fileSource.text = fileSource.sourceName;
            fileSource.value = fileSource.uuid;

            fileSourceOptions.push(fileSource);
          }
        });
        this.fileSourceOptions = fileSourceOptions;
      });
    },
    // 获取预览服务
    getPreviewServer() {
      fetchPreviewServer().then((res) => {
        this.previewServer = res;
      });
    },
    fetchRelatedMaterialAccept() {
      const _this = this;
      let relatedMaterial = _this.widget.configuration.relatedMaterial;
      if (!relatedMaterial || !relatedMaterial.enabled) {
        return;
      }

      let materialCodes = relatedMaterial.materialCodes;
      let materialCodeFields = relatedMaterial.materialCodeFields;
      // 指定材料编码字段
      if (relatedMaterial.way == "2") {
        materialCodes = [];
        materialCodeFields.forEach((codeField) => {
          let codeValue = _this.formData[codeField];
          if (codeValue) {
            materialCodes.push(codeValue);
          }
          // 监听材料编码值变更
          if (!_this.watchRelatedMaterialCode) {
            _this.$watch("formData." + codeField, (newValue, oldValue) => {
              _this.fetchRelatedMaterialAccept();
            });
            _this.watchRelatedMaterialCode = true;
          }
        });
      }

      if (!materialCodes.length) {
        return;
      }

      this.$axios.get(`/api/material/definition/listFormatByCodes?codes=${materialCodes}`).then(({ data: result }) => {
        _this.materialAccept = result.data || [];
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.w-form-upload-simple {
  display: flex;
  flex-direction: column;
  width: 100%;
  .upload-simple-bottom {
    margin-top: 7px;
    display: flex;
    align-items: center;
  }
  .upload-simple-count {
    margin-bottom: 10px;
    margin-right: 7px;
  }
}
</style>
