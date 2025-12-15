<template>
  <uni-forms-item
    :label="itemLabel"
    :name="formModelItemProp"
    :label-position="widget.configuration.labelPosition"
    :class="widgetClass"
    :style="itemStyle"
    :ref="fieldCode"
  >
    <view
      class="uni-flex uni-column w-form-upload-simple"
      v-if="widget.configuration.type === 'simpleList' || widget.configuration.type === 'advancedList'"
      @click.stop
    >
      <template v-if="isSubformCell">
        <uni-w-button
          type="link"
          v-if="fileList.length"
          style="--w-button-height: 22px; width: fit-content"
          @click="showFilesListPopup(fileList)"
        >
          {{
            $t("WidgetFormFileUpload.totalFilesSize", { count: fileList.length }, "附件(共" + fileList.length + "个)")
          }}
        </uni-w-button>
        <view class="text-only" v-else>-</view>
      </template>
      <template v-else>
        <uni-w-file-picker
          v-model="fileList"
          ref="filePicker"
          fileMediatype="all"
          :autoUpload="false"
          :fileExtname="accept.replace(/\./g, '')"
          :buttons="rowButtonsComputed"
          :delIcon="showFileDel"
          :limit="widget.configuration.fileLimitNum"
          :beforeChoose="onBeforeChoose"
          @select="onSelectDone"
          @reUploadFiles="batchUploadFiles"
          @delete="onDelete"
          @actionTap="onActionTap"
        >
          <view v-if="widget.configuration.fileSourceExtend && editable" @click.stop>
            <uni-w-button-group :buttons="fileSourceOptions" @click="onFileSourceButtonClick"></uni-w-button-group>
            <!-- <uni-w-data-checkbox
              :multiple="false"
              mode="button"
              class="w-form-radio-outline"
              :localdata="fileSourceOptions"

            /> -->
          </view>
          <uni-w-button
            v-else-if="editable"
            type="link"
            size="small"
            icon="iconfont icon-ptkj-shangchuan"
            :text="$t('WidgetFormFileUpload.upload', '上传附件')"
          ></uni-w-button>
          <file-buttons
            :buttons="headerButtonComputed"
            @click="onActionTap"
            :buttonPredicate="headButtonPredicate"
            :parentWidget="getSelf"
          />
          <template v-slot:description>
            <view class="widget-file-upload-tips" v-if="widget.configuration.description">
              {{ $t("description", widget.configuration.description) }}
            </view>
          </template>
        </uni-w-file-picker>
        <view v-if="fileList.length > 0" class="upload-simple-bottom">
          <text class="upload-simple-count">{{
            $t("WidgetFormFileUpload.totalSize", { count: fileList.length }, "共" + fileList.length + "个附件")
          }}</text>
        </view>
      </template>
    </view>
    <view v-else-if="widget.configuration.type === 'picture'" @click.stop>
      <uni-w-file-picker
        v-model="fileList"
        ref="filePicker"
        fileMediatype="image"
        :autoUpload="false"
        :disablePreview="!showPicturePreview"
        :delIcon="showUploadListPicture.showRemoveIcon"
        :addIcon="showPictureAdd"
        :limit="widget.configuration.fileLimitNum"
        :downloadIcon="showUploadListPicture.showDownloadIcon"
        :beforeChoose="onBeforeChoose"
        :fileExtname="accept.replace(/\./g, '')"
        :sourceType="pictureSourceType"
        :pictureForLib="pictureForLib"
        :pictureLibUuids="widget.configuration.pictureLib || []"
        :picLibBeforeUpload="picLibBeforeUpload"
        :addDbFiles="addDbFiles"
        @select="onSelectDone"
        @reUploadFiles="batchUploadFiles"
        @delete="onDelete"
        @downloadImage="({ item }) => onClickDownloadFile(item)"
      />
    </view>
    <uni-popup ref="reNamePopup" type="dialog">
      <uni-popup-dialog
        mode="input"
        :title="$t('WidgetFormFileUpload.button.onClickRename', '重命名')"
        :value="newFileName"
        :placeholder="$t('WidgetFormFileUpload.button.pleaseInputName', '输入文件名')"
        :confirmText="$t('global.confirm', '确定')"
        :cancelText="$t('global.cancel', '取消')"
        @confirm="confirmRename"
      />
    </uni-popup>
    <MyMaterialFileSource
      ref="myMaterialFileSourceRef"
      :parent="this"
      @confirm="myMaterialFileSelected"
    ></MyMaterialFileSource>
    <!-- 用于扫码的view -->
    <view v-show="false" :id="qrCheckId"></view>
  </uni-forms-item>
</template>

fileList
<script>
import { fetchFileSource, loadLogicFileByFiles, fetchPreviewServer, fetchDyformTitle } from "./api";
import formElement from "../w-dyform/form-element.mixin";
import formCommonMixin from "../w-dyform/form-common.mixin";
import { storage, utils as frameworkUtils } from "wellapp-uni-framework";
import { get_file_ext } from "../../../modified/uni-w-file-picker/components/uni-w-file-picker/utils";
import FileButtons from "./file-buttons.vue";
import MyMaterialFileSource from "./my-material-file-source.vue";
import { each, every, filter, findIndex } from "lodash";
import { Html5Qrcode } from "html5-qrcode";
// #ifndef APP-PLUS
import "./index.scss";
// #endif

const UPLOAD_STATUS = {
  SUCCESS: "success",
  READY: "ready",
  UPLOADING: "uploading",
  DONE: "done",
  ERROR: "error",
  REMOVED: "removed",
};

export default {
  mixins: [formElement, formCommonMixin],
  components: {
    FileButtons,
    MyMaterialFileSource,
  },
  data() {
    if (
      !this.widget.configuration.hasOwnProperty("uniConfiguration") ||
      !this.widget.configuration.uniConfiguration.headerButton
    ) {
      this.$set(this.widget.configuration, "uniConfiguration", {
        headerButton: this.widget.configuration.headerButton,
        rowButton: this.widget.configuration.rowButton,
      });
    }
    return {
      rowButtonHidden: ["onClickReplace", "onClickSaveAs", "onClickLookUp", "onClickSeal", "onClickShowHistory"],
      fileComponent: undefined,
      wellappBackendUrl: "",
      backendSaveFile: "",
      backendDownloadFile: "",
      backendDownloadAll: "",
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
      qrCheckId: frameworkUtils.generateId(),
    };
  },
  computed: {
    pictureSourceType() {
      let type = ["album", "camera"];
      let pictureSourceType =
        this.widget.configuration.uniConfiguration && this.widget.configuration.uniConfiguration.pictureSourceType
          ? this.widget.configuration.uniConfiguration.pictureSourceType
          : this.widget.configuration.pictureSourceType;
      if (pictureSourceType) {
        type = pictureSourceType.split(",");
      }
      return type;
    },
    // 简易视图是否显示上传
    showSimpleUpload() {
      let showUpload = true;
      const headerButton = this.headerButton;
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
      let _headerButton = this.headerButton;
      if (_headerButton && _headerButton.length) {
        if (this.editable) {
          // 编辑状态下 编辑类和显示类按钮都显示，除非按钮关闭
          headerButton = _headerButton.filter((item) => {
            return item.defaultFlag;
          });
        } else {
          // 不可编辑状态下 只能显示“显示类”按钮
          headerButton = _headerButton.filter((item) => {
            return item.defaultFlag && btnShowType === item.btnShowType;
          });
        }
      }
      if (
        (this.widget.configuration.type == "simpleList" || this.widget.configuration.type === "advancedList") &&
        !this.designMode
      ) {
        headerButton = headerButton.filter((item) => {
          return item.code != "onClickUpload";
        });
      }
      return headerButton;
    },
    // 显示图片添加
    showPictureAdd() {
      let show = true;
      const headerButton = this.headerButton;
      show = headerButton && headerButton[0].defaultFlag;
      if (!this.editable) {
        // 不可编辑状态下不显示添加
        show = false;
      } else if (this.isReplaceImgByLimitOne) {
        show = false;
      }
      return show;
    },
    rowButton() {
      return this.widget.configuration.uniConfiguration && this.widget.configuration.uniConfiguration.rowButton
        ? this.widget.configuration.uniConfiguration.rowButton
        : this.widget.configuration.rowButton;
    },
    headerButton() {
      return this.widget.configuration.uniConfiguration && this.widget.configuration.uniConfiguration.headerButton
        ? this.widget.configuration.uniConfiguration.headerButton
        : this.widget.configuration.headerButton;
    },
    // 图片是否能预览
    showPicturePreview() {
      let preview = false;
      const rowButton = this.rowButton;
      if (rowButton && rowButton.length) {
        const previewBtn = rowButton.find((item) => item.code.indexOf("Preview") > -1);
        preview = previewBtn.defaultFlag;
      }
      return preview;
    },
    // 图片按钮
    showUploadListPicture() {
      let showUploadList = { showPreviewIcon: false, showDownloadIcon: false, showRemoveIcon: false };
      const rowButton = this.rowButton;
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
      let _rowButton = this.rowButton;
      if (_rowButton && _rowButton.length) {
        if (this.editable) {
          rowButton = _rowButton.filter((item) => {
            return item.defaultFlag;
          });
        } else {
          rowButton = _rowButton.filter((item) => {
            return item.defaultFlag && btnShowType === item.btnShowType;
          });
        }
      }
      rowButton = rowButton.filter((item) => !this.rowButtonHidden.includes(item.id));
      const delIndex = rowButton.findIndex((item) => item.code === "onClickDelFile");
      if (delIndex !== -1) {
        this.showFileDel = true;
        rowButton.splice(delIndex, 1);
      }
      if (process.env.UNI_PLATFORM === "mp-weixin" && rowButton.length > 6) {
        // 微信、百度、抖音小程序数组长度最大为6个
        rowButton = rowButton.slice(0, 6);
      }
      rowButton.forEach((item) => {
        item.buttonName = this.$tBtn(item.id, item.buttonName, item);
      });
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
    this.setMobileType();
    this.widgetCreatedButtonSetting();

    const jwtToken = storage.getAccessToken();
    const wellappBackendUrl = storage.getWellappBackendUrl();
    this.wellappBackendUrl = wellappBackendUrl;
    const mongoRep = "/repository/file/mongo";

    const backendMongoRep = wellappBackendUrl + mongoRep;
    this.backendSaveFile = backendMongoRep + "/savefiles?jwt=" + jwtToken;
    this.backendDownloadFile = backendMongoRep + "/download?jwt=" + jwtToken;
    this.backendDownloadAll = backendMongoRep + "/downAllFiles?jwt=" + jwtToken;

    this.savefilesAnnoymous = `${backendMongoRep}/savefiles`;
    this.downloadAnnoymous = `${backendMongoRep}/download4ocx`;
    this.downloadAllAnnoymous = `${backendMongoRep}/downAllFiles4ocx`;
    this.downloadPath = `${mongoRep}/download?fileID=`;

    // 分片上传
    // repository/file/mongo/savefilesChunk

    this.setValue(this.formData[this.fieldCode]);

    // #ifdef H5
    this.getChunkSize();
    // #endif

    this.getPreviewServer();
    this.fetchRelatedMaterialAccept();
    if (this.widget.configuration.fileSourceExtend) {
      // 开启附件来源扩展
      this.getFileSource();
    }
    this.addUploadErrorRules();
  },
  mounted() {
    this.fileComponent = this.$refs.filePicker;
  },
  methods: {
    $tBtn() {
      if (arguments[2]) {
        let btn = arguments[2];
        if (btn.btnType == "1") {
          // 默认按钮使用默认配置
          return this.$t("WidgetFormFileUpload.button." + btn.id, btn.buttonName);
        }
      }
      return this.$t(...arguments);
    },
    setMobileType() {
      if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
        this.$set(this.widget.configuration, "uniConfiguration", { fileNormalOrText: "normal" });
      }
      let fileNormalOrText = this.widget.configuration.uniConfiguration.fileNormalOrText;
      if (fileNormalOrText === "text" || !fileNormalOrText) {
        fileNormalOrText = "normal";
        this.widget.configuration.uniConfiguration.fileNormalOrText = "normal";
      }
      if (!this.widget.configuration.uniConfiguration.rowButton) {
        this.initMobileButtons(this.widget.configuration.rowButton, this.widget.configuration.headerButton);
      }
    },
    initMobileButtons(rowButton = [], headerButton = []) {
      this.widget.configuration.uniConfiguration.headerButton = deepClone(headerButton);
      this.widget.configuration.uniConfiguration.rowButton = deepClone(rowButton).filter(
        (item) => !this.mobileFilter.includes(item.code)
      );
    },
    widgetCreatedButtonSetting() {
      // if (this.isText && !this.designMode && EASY_ENV_IS_BROWSER) {
      //   clientCommonApi.getSystemParamValue('app.fileupload.welloffice.enable', wellofficeEnable => {
      // 	if (wellofficeEnable == 'false') {
      // 	  this.wellOfficeEnable = false;
      // 	  const buttonIndex = this.configuration.headerButton.findIndex(item => item.id == 'onPasteFile' || item.code == 'onPasteFile');
      // 	  if (buttonIndex != -1) {
      // 		this.widget.configuration.headerButton.splice(buttonIndex, 1);
      // 	  }
      // 	  const rowButtonIndex = this.configuration.rowButton.findIndex(
      // 		item => item.id == 'onClickSaveAs' || item.code == 'onClickSaveAs'
      // 	  );
      // 	  if (rowButtonIndex != -1) {
      // 		this.widget.configuration.rowButton.splice(rowButtonIndex, 1);
      // 	  }
      // 	}
      //   });
      // }
      // 从表的字段规则：由外部定义传入
      if (
        this.form.formElementRules &&
        this.form.formElementRules[this.widget.id] &&
        this.form.formElementRules[this.widget.id].buttons
      ) {
        if (this.form.formElementRules[this.widget.id].buttons) {
          let headerButton = this.form.formElementRules[this.widget.id].buttons.headerButton;
          let headerButtonArr = headerButton ? headerButton.split(";") : [];
          let rowButton = this.form.formElementRules[this.widget.id].buttons.rowButton;
          let rowButtonArr = rowButton ? rowButton.split(";") : [];
          // 按钮设置
          each(this.widget.configuration.uniConfiguration.headerButton, (item) => {
            if (headerButtonArr.indexOf(item.code || item.id) > -1) {
              item.defaultFlag = true;
            } else {
              item.defaultFlag = false;
            }
          });
          each(this.widget.configuration.uniConfiguration.rowButton, (item) => {
            if (rowButtonArr.indexOf(item.code || item.id) > -1) {
              item.defaultFlag = true;
            } else {
              item.defaultFlag = false;
            }
          });
        }
      }
    },
    setValue(value) {
      console.log("文件上传数据", value);
      this.fileList.splice(0, this.fileList.length);
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
              url: this.backendDownloadFile + "&fileID=" + item.fileID,
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
    addDbFiles(dbFiles) {
      if (dbFiles && dbFiles.length) {
        if (!this.formData[this.fieldCode]) {
          this.formData[this.fieldCode] = [];
        }
        this.formData[this.fieldCode] = this.formData[this.fieldCode].concat(dbFiles);
        dbFiles.forEach((dbFile) => {
          dbFile.userName = this._$USER.userName;
          this.addFileList({
            uid: dbFile.fileID,
            name: dbFile.fileName || dbFile.filename,
            size: dbFile.fileSize,
            status: UPLOAD_STATUS.DONE,
            dbFile,
          });
        });
      }
    },
    addFileList(file, isLib) {
      // file.uid =  generateId();
      if (file.dbFile && !file.dbFile.userName) {
        if (this.userNameMap[file.dbFile.creator] && this.userNameMap[file.dbFile.creator].userName) {
          file.dbFile.userName = this.userNameMap[file.dbFile.creator].userName;
        } else {
          this.fetchOrgUserDetail(file.dbFile);
        }
      }
      const extname = get_file_ext(file.name).ext;
      let fileResult = {
        uid: file.uid,
        name: file.name,
        fileID: file.fileID,
        dbFile: file.dbFile,
        status: UPLOAD_STATUS.SUCCESS,
        size: file.fileSize,

        extname,
      };
      if (typeof fileResult.dbFile.createTime === "string") {
        fileResult.dbFile.createTimeStr = fileResult.dbFile.createTime;
      }
      if (fileResult.dbFile.fileID) {
        fileResult.url = storage.fillAccessResourceUrl(
          `/repository/file/mongo/download?fileID=${fileResult.dbFile.fileID}`
        );
      }
      if (file.lastModified) {
        fileResult.orgFile = file;
      }
      this.fileList.push(fileResult);
      return fileResult;
    },
    // 获取创建附件用户信息
    fetchOrgUserDetail(item) {
      if (!this.userNameMap[item.creator]) {
        this.userNameMap[item.creator] = { files: [], userName: "" };
        this.$axios
          .get(`/api/user/org/getUserDetailsUnderSystem`, {
            params: {
              userId: item.creator,
            },
          })
          .then(({ data }) => {
            this.userNameMap[item.creator].userName = data.data.userName;
            each(this.userNameMap[item.creator].files, (citem) => {
              let hasIndex = findIndex(this.fileList, { uid: citem.fileID });
              if (hasIndex > -1) {
                this.$set(this.fileList[hasIndex].dbFile, "userName", data.data.userName);
              }
            });
          });
      }
      this.userNameMap[item.creator].files.push(item);
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
        uni.showToast({
          icon: "error",
          title: `${this.$t(
            "WidgetFormFileUpload.message.fileNameExistPleaseReInput",
            { newFileName: this.newFileName },
            "文件" + this.newFileName + "已经存在，请重新输入！"
          )}`,
        });
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
      const downloadUrl = `${this.backendDownloadFile}&fileID=${file.fileID}`;
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
      let fileName = file.fileName || file.name;
      uni.showLoading({
        title: this.$t(
          "WidgetFormFileUpload.message.fileDownLoadingPleaseWait",
          { fileName: fileName },
          "文件" + fileName + "下载中"
        ),
      });

      uni.downloadFile({
        url: `${this.downloadAnnoymous}?fileID=${file.fileID}`,
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
    // 下载文件-全部（非h5下载）
    downloadFileAllNotWeb(args) {
      uni.downloadFile({
        url: args.url,
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
      uni.getFileSystemManager().saveFile({
        tempFilePath,
        success: function (res) {
          var savedFilePath = res.savedFilePath;
          console.log(res);
        },
        complete: function (args) {
          console.log(args);
        },
      });
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
        uni.showToast({ icon: "error", title: this.$t("WidgetFormFileUpload.message.notAllowFile", "文件不允许") });
        return false;
      }
      return true;
    },
    // 检查允许上传重名
    checkFileNameRepeat(file, fileList = this.uploadDoneList) {
      if (!this.widget.configuration.fileNameRepeat) {
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
    qrCheck(file) {
      let _this = this;
      return new Promise((resolve, reject) => {
        if (this.widget.configuration.qrCodeNotAllowed !== true) {
          resolve();
          return;
        }
        let ext = file.name.split(".").pop().toLowerCase();
        if (["jpeg", "jpg", "png", "bmp", "jfif", "tif", "tiff"].includes(ext)) {
          // #ifdef H5
          if (file.fileID) {
            let img = new Image();
            img.src = `/proxy-repository/repository/file/mongo/download?fileID=${file.fileID}`;
            img.onload = function () {
              _this
                .checkImageContainQrCode(img)
                .then(() => {
                  resolve();
                })
                .catch(() => {
                  reject();
                });
            };
            img.onerror = function () {
              console.error("图片解析错误, 非正常图片格式");
              uni.showToast({
                icon: "error",
                title: this.$t("WidgetFormFileUpload.message.notAllowFile", "文件不允许"),
              });
              reject();
            };
            return;
          } else if (file.file instanceof File) {
            const reader = new FileReader();
            reader.onload = function (e) {
              const img = new Image();
              img.onload = function () {
                _this
                  .checkImageContainQrCode(img)
                  .then(() => {
                    resolve();
                  })
                  .catch(() => {
                    reject();
                  });
              };
              img.src = e.target.result;
              img.onerror = function () {
                console.error("图片解析错误, 非正常图片格式");
                uni.showToast({
                  icon: "error",
                  title: this.$t("WidgetFormFileUpload.message.notAllowFile", "文件不允许"),
                });
                reject();
              };
            };
            reader.readAsDataURL(file.file);
          } else {
            resolve(true);
          }
          // #endif
          // #ifndef H5
          // 未在app端测试
          let img = file.path;
          if (file.fileID) {
            img = `${this.downloadAnnoymous}?fileID=${file.fileID}`; //storage.fillAccessResourceUrl(`/repository/file/mongo/download?fileID=${file.fileID}`);
            this.urlToBlob(img).then((blob) => {
              // 使用uni-app的上传组件上传文件...
              _this
                .checkImageContainQrCode(blob)
                .then(() => {
                  resolve();
                })
                .catch(() => {
                  reject();
                });
            });
          } else {
            _this
              .checkImageContainQrCode(file.file)
              .then(() => {
                resolve();
              })
              .catch(() => {
                reject();
              });
          }
          // #endif
        } else {
          resolve(true);
        }
      });
    },
    checkImageContainQrCode(img) {
      let _this = this;
      return new Promise((resolve, reject) => {
        // #ifndef H5
        let html5QrCodeDom = new Html5Qrcode(this.qrCheckId);
        html5QrCodeDom
          .scanFile(img, true)
          .then((decodedText, decodedResult) => {
            console.log(`Found QR code: ${decodedText}`); // 处理解码后的文本
            uni.showToast({
              icon: "error",
              title: `${this.$t("WidgetFormFileUpload.message.notAllowQrCodeImage", "不允许上传包含二维码的图片")}`,
            });
            reject();
          })
          .catch((errorMessage) => {
            console.error(`Unable to scan QR Code from canvas: ${errorMessage}`); // 处理错误
            resolve();
          });
        // #endif
        // #ifdef H5
        import("./opencv/4.5.5/opencv").then((cv) => {
          // 创建QRCodeDetector实例
          var qrCodeDetector = new cv.QRCodeDetector();

          // 将图片元素转换为cv.Mat对象
          var src = cv.imread(img);

          // 检测二维码的位置
          var points = new cv.Mat();
          var found = qrCodeDetector.detect(src, points);
          if (found) {
            // 解码二维码
            // var decodedText = qrCodeDetector.decode(src, points);
            uni.showToast({
              icon: "error",
              title: `${this.$t("WidgetFormFileUpload.message.notAllowQrCodeImage", "不允许上传包含二维码的图片")}`,
            });
            // 释放内存
            points.delete();
            reject();
          } else {
            resolve();
          }

          // 释放内存
          src.delete();
        });
        // #endif
      });
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
          this.qrCheck(file)
            .then(() => {
              resolve({ file, index });
            })
            .catch(() => {
              this.tempFiles.splice(index, 1);
              const hasIndex = this.fileComponent.files.findIndex((item) => item.uuid === file.uuid);
              this.fileComponent.files.splice(hasIndex, 1);
              reject({ file, index });
            });
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
      let _this = this;
      this.beforeUpload(file, tempIndex, files).then(
        ({ file: tempFile, index }) => {
          const filePath = tempFile.path;

          const uploadTask = uni.uploadFile({
            url: _this.backendSaveFile,
            filePath,
            name: "fileUpload",
            formData: {},
            success: (uploadFileRes) => {
              console.log("upload success", uploadFileRes);
              let result = JSON.parse(uploadFileRes.data);
              if (result.code == 0 && result.data) {
                let dbFile = result.data[0];

                delete tempFile.errMsg;
                tempFile.url = this.backendDownloadFile + "&fileID=" + dbFile.fileID;
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
        const url = `${this.backendDownloadAll}&fileIDs=${encodeURIComponent(
          JSON.stringify(res.ids)
        )}&includeFolder=false&fileName=${res.fileName}`;
        // #ifdef H5
        this.downloadFileByWeb(url);
        // #endif
        // #ifndef H5
        this.downloadFileAllNotWeb({ url });
        // #endif
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
      this.emitChange();
    },
    // 添加字段值
    addFieldValue(dbFile) {
      if (!this.formData[this.fieldCode]) {
        this.formData[this.fieldCode] = [];
      }
      this.formData[this.fieldCode].push(dbFile);
      this.emitChange();
    },
    // 删除字段值
    delFieldValue(index) {
      this.formData[this.fieldCode].splice(index, 1);
      this.emitChange();
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
    onFileSourceButtonClick(e, button) {
      if ("local_upload" == button.code) {
        this.$refs.filePicker.choose();
      } else if (button.jsModule && /^{.*}$/gs.test(button.jsModule)) {
        let buttonJsModule = JSON.parse(button.jsModule);
        if ("CdMyMaterialFileSourceDevelopment" == buttonJsModule.jsModule) {
          this.$refs.myMaterialFileSourceRef.showPopup();
        }
      }
    },
    myMaterialFileSelected(logicFileInfos) {
      logicFileInfos.forEach((item) => {
        item.source = this.$t("WidgetFormFileUpload.fileSource.referMyMaterial", "引用我的材料");
      });
      this.addDbFiles(logicFileInfos);
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
            if (fileSource.i18ns && fileSource.i18ns.length) {
              let i18n = this.i18nsToI18n(fileSource.i18ns, "defId");
              this.mergeWidgetI18nMessages(i18n);
            }

            fileSource.text = this.$t(fileSource.uuid, fileSource.sourceName);
            fileSource.value = fileSource.uuid;
            fileSource.title = fileSource.text;
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
    showFilesListPopup(files) {
      uni.$emit("ptPopupShow", {
        componentName: "popup-file-list", //弹框内部组件
        title: `${this.$t(
          "WidgetFormFileUpload.totalFilesSize",
          { count: files.length },
          "附件(共" + files.length + "个)"
        )}
          `,
        options: {
          files: files,
          headerButtons: this.headerButtonComputed,
          buttons: this.rowButtonsComputed,
        },
      });
    },
    headButtonPredicate(button) {
      if ("onClickAllDownload" == button.code) {
        // 全部下载按钮：无附件，不展示
        if (this.fileList.length == 0) {
          return false;
        }
      }
      return true;
    },
    // 增加附件上传失败校验
    addUploadErrorRules() {
      this.customRules.push({
        trigger: "change",
        validateFunction: (rule, value, data, callback) => {
          if (this.fileList.length) {
            let uploading = false;
            let error = false;
            let ispass = every(this.fileList, (item) => {
              if (item.status == UPLOAD_STATUS.UPLOADING || item.status == UPLOAD_STATUS.READY) {
                uploading = true;
              } else if (item.status == UPLOAD_STATUS.ERROR) {
                error = true;
              }
              // 状态存在未完成的附件
              return !item.status || item.status == UPLOAD_STATUS.DONE || item.status == UPLOAD_STATUS.SUCCESS;
            });
            if (!ispass) {
              if (error) {
                callback(this.$t("WidgetFormFileUpload.validateError.error", "存在上传异常的附件,请确认后继续"));
              } else if (uploading) {
                callback(this.$t("WidgetFormFileUpload.validateError.uploading", "正在上传附件，请稍后"));
              } else {
                callback(this.$t("WidgetFormFileUpload.validateError.error", "存在上传异常的附件,请确认后继续"));
              }
              return;
            }
          }
          callback();
        },
      });
    },
    getSelf() {
      return this;
    },
    onFilter({ searchValue, comparator, source, ignoreCase }) {
      if (source != undefined) {
        // 文件组件都按模糊匹配处理
        if (Array.isArray(source)) {
          for (let i = 0, len = source.length; i < len; i++) {
            let fileName = source[i].filename || source[i].fileName;
            if (
              ignoreCase
                ? fileName.toLowerCase().indexOf(searchValue.toLowerCase()) != -1
                : fileName.indexOf(searchValue) != -1
            ) {
              return true;
            }
          }
        }
        return false;
      }
      //TODO: 判断本组件值是否匹配
      return false;
    },
    // 图片库提交前校验
    picLibBeforeUpload(files, isLocal) {
      let promise = [];
      for (let index = 0, len = files.length; index < len; index++) {
        let file = files[index];
        promise.push(
          new Promise((resolve, reject) => {
            let pass = true;
            const allow = this.checkAcceptFile(file);
            if (!allow) {
              pass = false;
            } else if (!this.checkLimitNum({ index, files })) {
              pass = false;
            } else if (
              !this.checkFileNameRepeat(
                file,
                filter(files, (item, idx) => {
                  return idx != index;
                })
              )
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
              this.qrCheck(file)
                .then(() => {
                  resolve(file);
                })
                .catch(() => {
                  reject({ file, index });
                });
            } else {
              reject({ file, index });
            }
          })
        );
      }
      return Promise.all(promise);
    },
    // url地址转成blob文件
    urlToBlob(url) {
      return new Promise((resolve, reject) => {
        uni.downloadFile({
          url: url,
          success: (downloadResult) => {
            if (downloadResult.statusCode === 200) {
              // 获取下载的临时文件路径
              const tempFilePath = downloadResult.tempFilePath;

              uni.getFileSystemManager().readFile({
                tempFilePath,
                success: function (res) {
                  var savedFilePath = res.savedFilePath;
                  // 创建File对象
                  const file = new File([arrayBuffer], "image.png", { type: "image/png" });
                  onsole.log(file); // 输出File对象
                  // 接下来你可以使用这个file对象进行其他操作，例如上传等
                  resolve(file);
                },
                fail: (error) => {
                  console.error("Read failed:", error);
                  reject();
                },
              });
            } else {
              console.error("Download failed:", downloadResult.errMsg);
              reject();
            }
          },
          fail: (error) => {
            console.error("Download failed:", error);
            reject();
          },
        });
      });
    },
  },
};
</script>

<style lang="scss" scoped>
/* #ifdef APP-PLUS */
@import "./index.scss";
/* #endif */
</style>
