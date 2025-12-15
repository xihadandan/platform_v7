<template>
  <div class="widget-file-upload-container simpleUpload_component">
    <a-upload
      v-show="editable"
      :multiple="multiple"
      :file-list="fileList"
      :customRequest="customRequest"
      @change="onFileUploadChange"
      :beforeUpload="beforeUpload"
      :showUploadList="false"
      :accept="accept"
      ref="simpleUpload"
    >
      <a-button :size="uploadBtn.size" :type="uploadBtn.type" :disabled="uploading || !editable">
        <Icon :type="uploadBtn.icon || 'pticon iconfont icon-ptkj-shangchuan'" v-if="uploadBtn.hasIcon" />
        {{ uploadBtn.title || '上传' }}
      </a-button>
      <div class="widget-file-upload-tips" v-if="visibleTips">{{ visibleTips }}</div>
    </a-upload>
    <div class="widget-file-upload-list" style="padding-top: 4px">
      <a-popover
        v-for="(file, i) in fileList"
        :key="i"
        :title="file.name"
        trigger="hover"
        :visible="file.hovered"
        @visibleChange="v => onFileItemHoverVisibleChange(v, file)"
        overlayClassName="widget-file-upload-list"
      >
        <template slot="content">
          <a-space :size="16">
            <span class="file-item-desc">{{ file.formatSize }}</span>
            <span class="file-item-desc">{{ file.dbFile.createTimeStr }}</span>
            <span class="file-item-desc">{{ file.dbFile.userName }}</span>
          </a-space>
        </template>
        <div class="file-list-item flex">
          <div class="f_s_0" style="width: 24px">
            <a-progress
              v-if="file.showProgress && file.status == 'uploading'"
              type="circle"
              :percent="file.percent"
              :width="14"
              strokeColor="#52c41a"
              :strokeWidth="10"
              :showInfo="false"
            />
            <template v-else-if="file.status === 'error'">
              <a-icon type="exclamation-circle" theme="filled" class="error-icon" />
            </template>
            <template v-else>
              <Icon :type="file.icon" />
            </template>
          </div>
          <div class="label-filename f_g_1 w-ellipsis" :style="{ color: file.status === 'error' ? 'var(--w-danger-color)' : '' }">
            {{ file.name }}
          </div>
          <!-- <div><a-icon type="check-circle" v-if="file.percent === 100" class="done-icon" /></div> -->
          <template v-if="file.status === 'error'">
            <a-button type="link" size="small" @click="handleReUpload(file)">重新上传</a-button>
          </template>
          <template v-if="file.status == 'uploading' || file.status === 'error'">
            <a-button type="link" size="small" @click="handleCancelUpload(file, i)">取消上传</a-button>
          </template>
          <div class="file-list-item-buttons-absolute" v-show="file.hovered && file.status === 'done'">
            <span class="file-list-item-buttons" v-show="file.status === 'done'">
              <WidgetFormFileListButtons
                visibleTrigger="hover"
                :buttons="rowButtonsComputed"
                :file="file"
                :fileIndex="i"
                @listButtonClicked="onListButtonClicked"
              />
            </span>
          </div>
        </div>
      </a-popover>
    </div>
    <template v-if="fileList.length > 1">
      <span>共{{ fileList.length }}个附件</span>
      <WidgetFormFileListButtons :buttons="headerButtonComputed" @listButtonClicked="onListButtonClicked" />
    </template>
  </div>
</template>
<script type="text/babel">
import { preview } from '@framework/vue/lib/preview/filePreviewApi';
import { generateId, copyToClipboard, getCookie, swapArrayElements } from '@framework/vue/utils/util';
import WidgetFormFileListButtons from '@dyformWidget/widget-form-file-upload/widget-form-file-list-buttons';
import moment from 'moment';
import SparkMD5 from 'spark-md5';
import { each, isEmpty, isArray, findIndex } from 'lodash';

const UPLOAD_STATUS = { UPLOADING: 'uploading', DONE: 'done', ERROR: 'error', REMOVED: 'removed' };
export default {
  name: 'UploadSimpleComponent',
  props: {
    fileIds: {
      type: [String, Array],
      default: ''
    },
    defaultFileList: {
      type: [Object, Array],
      default: () => []
    },
    multiple: {
      type: Boolean,
      default: false
    },
    beforeUpload: {
      type: Function,
      default: undefined
    },
    accept: {
      type: String,
      default: ''
    },
    uploadBtn: {
      type: Object,
      default: () => {
        return {
          hasIcon: true
        };
      }
    },
    visibleTips: {
      type: String,
      default: ''
    },
    editable: {
      type: Boolean,
      default: true
    },
    downloadTitle: {
      type: String,
      default: ''
    },
    separator: {
      type: String,
      default: ';'
    }
  },
  components: {
    WidgetFormFileListButtons
  },
  data() {
    return {
      fileList: [],
      fileUploadMap: {},
      uploading: false,
      fileSourceOptions: [], // 附件来源
      cancelSourceList: [],
      curButton: undefined,
      md5FileObj: {},
      previewServerHost: '', // 预览服务地址
      fileRepositoryServerHost: '',
      userNameMap: [] // 存放用户名
    };
  },
  computed: {
    rowButtonsComputed() {
      let rowButton = [],
        btnShowType = 'show';
      if (this.editable) {
        rowButton = this.simpleListRowButtons.filter(item => {
          return true;
        });
      } else {
        rowButton = this.simpleListRowButtons.filter(item => {
          return btnShowType === item.btnShowType;
        });
      }
      return rowButton;
    },
    simpleListRowButtons() {
      return [
        { btnType: '1', buttonName: '下载', type: 'link', loadding: false, code: 'onClickDownloadFile', btnShowType: 'show' },
        { btnType: '1', buttonName: '删除', type: 'link', loadding: false, code: 'onClickDelFile' },
        { btnType: '1', buttonName: '预览', type: 'link', loadding: false, code: 'onClickPreviewFile', btnShowType: 'show' },
        { btnType: '1', buttonName: '复制名称', type: 'link', loadding: false, code: 'onClickCopyFilename', btnShowType: 'show' }
      ];
    },
    headerButtonComputed() {
      return [
        {
          btnType: '1',
          buttonName: '全部下载',
          type: 'default',
          loadding: false,
          code: 'onClickAllDownload',
          btnShowType: 'show'
        }
      ];
    },
    accpetList() {
      if (this.accpet) {
        return this.accpet.split(',');
      }
      return [];
    },
    onBeforeUpload() {
      if (this.beforeUpload) {
        return this.beforeUpload;
      }
      return this._onBeforeUpload;
    }
  },
  mounted() {
    if (this.fileIds) {
      this.setValue(this.fileIds);
    } else if (this.defaultFileList) {
      this.setValue(this.defaultFileList);
    }
    this.getChunkSize();
    this.getPreviewHostServer();
  },
  methods: {
    _onBeforeUpload() {
      if (this.accpetList.length) {
        let allow = this.checkAcceptFile(file);
        if (!allow) {
          this.$message.error('文件不允许');
          return false;
        }
      }
      if (!this.multiple && this.fileList.length == 1) {
        this.$message.error('文件超过上传限制:1');
        return false;
      }
    },
    customRequest(options, afterUpload) {
      let _this = this;
      const fileResult = this.addFileList(options.file);
      this.setButtonControl(fileResult);
      // 计算文件MD5
      this.computeFileMD5(options.file, function () {
        // 校验文件是否存在，如果已经存在，则上传直接返回删除
        _this.loadFileChunkInfoByMD5(options.file, function (result) {
          _this.upload(options.file, result, function (_file) {
            setTimeout(function () {
              // 进度条100%后延迟一点关闭
              _file.showProgress = false;
              options.onSuccess();
            }, 300);
          });
        });
      });
    },
    handleReUpload(file) {
      file.status = UPLOAD_STATUS.UPLOADING;
      const options = {
        file: file.orgFile
      };
      let _this = this;
      this.computeFileMD5(options.file, function () {
        // 校验文件是否存在，如果已经存在，则上传直接返回删除
        _this.loadFileChunkInfoByMD5(options.file, function (result) {
          _this.upload(options.file, result, function (_file) {
            setTimeout(function () {
              // 进度条100%后延迟一点关闭
              _file.showProgress = false;
              options.onSuccess();
            }, 300);
          });
        });
      });
    },
    handleCancelUpload(file, fileIndex) {
      this.cancelSourceList.forEach(source => {
        source.cancel('取消请求');
      });
      this.cancelSourceList = [];
      this.onClickDelFile(file, fileIndex);
    },
    onClickUpload() {
      this.curButton = {
        button: { code: 'onClickUpload' }
      };
    },
    emitChange() {
      this.$emit('change', this.fileList);
    },
    onFileItemHoverVisibleChange(v, file) {
      if (file.dropdownButtonVisible === true) {
        file.hovered = true;
        return;
      }
      file.hovered = file.status == UPLOAD_STATUS.DONE && v;
    },
    // 按钮功能转发
    onListButtonClicked(params) {
      this.curButton = params;
      let { button } = params;
      if (button && button.code) {
        this[button.code](params);
      }
    },
    // 下载文件
    onClickDownloadFile(params) {
      const file = params.file ? params.file : params;
      if (file.status != UPLOAD_STATUS.DONE) {
        this.$message.info('上传中，请稍后');
        return;
      }
      this.downloadFileSilence(`/proxy-repository/repository/file/mongo/download?fileID=${file.dbFile.fileID}`);
    },
    downloadFileSilence(url) {
      var _this = this;
      var hiddenIFrameID = 'hiddenDownloader' + generateId();
      var iframe = document.createElement('iframe');
      iframe.id = hiddenIFrameID;
      iframe.style.display = 'none';
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
        if (iframeDoc.readyState == 'complete' || iframeDoc.readyState == 'interactive') {
          var _text = iframeDoc.body.innerText;
          if (_text && _text.indexOf('No such file') != -1) {
            //需要等待后端响应无文件的异常
            clearInterval(timer);
            iframe.remove();
            if (_text.indexOf('try later') != -1) {
              setTimeout(function () {
                _this.downloadURL(url); //重新下载
              }, 2000);
            }
          }
          return;
        }
      }, 1000);
    },
    // 全部下载
    onClickAllDownload() {
      let ids = [],
        fileName = this.downloadTitle;
      this.fileList.forEach(item => {
        if (!fileName) {
          fileName = (item.name || item.dbFile.filename || item.dbFile.fileName) + '等文件';
        }
        ids.push({
          fileID: item.dbFile.fileID
        });
      });
      if (ids.length) {
        this.downloadFileSilence(
          `/proxy-repository/repository/file/mongo/downAllFiles?fileIDs=${encodeURIComponent(
            JSON.stringify(ids)
          )}&includeFolder=false&fileName=${fileName}`
        );
      }
    },
    onClickDelFile() {
      // let { file, fileIndex } = params;
      let file, fileIndex;
      if (arguments.length > 1) {
        [file, fileIndex] = [...arguments];
      } else {
        file = arguments[0].file;
        fileIndex = arguments[0].fileIndex;
      }
      delete this.fileUploadMap[file.uid];
      this.fileList.splice(fileIndex, 1);
      this.emitChange();
      return true;
    },
    // 预览文件
    onClickPreviewFile(params) {
      let { file, button } = params;
      if (file.status != UPLOAD_STATUS.DONE) {
        this.$message.info(file.status === UPLOAD_STATUS.UPLOADING ? '文件上传中，请稍后' : '文件上传失败，无法预览');
        return;
      }
      let _this = this;
      if (_this.fileRepositoryServerHost == '') {
        button.loading = true; // 初次加载要有个loading，待预览api初始化成功会回调进行关闭
        this.$clientCommonApi.getSystemParamValue('sys.context.path', function (serv) {
          if (serv) {
            _this.fileRepositoryServerHost = 'null' === serv ? null : serv;
            preview(`${serv}/wopi/files/${file.dbFile.fileID}?access_token=336dc563-1d17-44a3-a916-e8abe2e88cbb`, {
              callback: function () {
                button.loading = false;
              }
            });
          }
        });
        return;
      }
      preview(`${_this.fileRepositoryServerHost}/wopi/files/${file.dbFile.fileID}?access_token=336dc563-1d17-44a3-a916-e8abe2e88cbb`);
    },
    // 复制名称
    onClickCopyFilename(params) {
      let { evt, file } = params;
      let _this = this;
      copyToClipboard(file.name, evt, function (success) {
        if (success) {
          _this.$message.success('已复制');
        }
      });
    },
    // 开始上传、上传中、完成、失败都会调用这个函数
    onFileUploadChange(args, isLib) {
      let { file, fileList } = args;

      let _this = this;
      if (file.status === UPLOAD_STATUS.REMOVED) {
        for (let i = 0, len = this.fileList.length; i < len; i++) {
          if (this.fileList[i].uid === file.uid) {
            this.onClickDelFile(file, i);
            return;
          }
        }
      }
      if (file.status === UPLOAD_STATUS.ERROR) {
        if (this.curButton && this.curButton.button.code == 'onClickReplace') {
          const fileReplace = this.curButton.file;
          const fileIndex = this.curButton.fileIndex;
          this.fileUploadMap[fileReplace.uid] = fileReplace;
          this.fileList.splice(fileIndex, 1, fileReplace);
        }
      }
      if (file.status === UPLOAD_STATUS.DONE) {
        const dbFile = this.fileUploadMap[file.uid].dbFile;
        this.emitChange();
      }
    },
    addFileList(file) {
      // file.uid =  generateId();
      if (file.dbFile && !file.dbFile.userName) {
        if (this.userNameMap[file.dbFile.creator] && this.userNameMap[file.dbFile.creator].userName) {
          file.dbFile.userName = this.userNameMap[file.dbFile.creator].userName;
        } else {
          this.fetchOrgUserDetail(file.dbFile);
        }
      }
      let fileResult = {
        uid: file.uid,
        name: file.name,
        percent: 0, //上传进度
        // url:'',thumbUrl:'',
        status: file.status || UPLOAD_STATUS.UPLOADING,
        hovered: false,
        showProgress: file.showProgress != undefined ? file.showProgress : true,
        dbFile: file.dbFile || {}, // 上传完文件，后端服务返回的文件信息
        formatSize: this.formatSize(file.size),
        buttonControl: {}
      };
      if (file.status === UPLOAD_STATUS.DONE) {
        fileResult.showProgress = false;
        fileResult.percent = 100;
      }
      if (typeof fileResult.dbFile.createTime === 'string') {
        fileResult.dbFile.createTimeStr = fileResult.dbFile.createTime;
      }
      if (fileResult.dbFile.fileID) {
        fileResult.url = `/proxy-repository/repository/file/mongo/download?fileID=${fileResult.dbFile.fileID}`;
      }
      if (file.lastModified) {
        fileResult.orgFile = file;
      }
      fileResult.icon = this.getFileIcon(file.name);
      this.fileList.push(fileResult);
      this.fileUploadMap[fileResult.uid] = fileResult;
      return fileResult;
    },
    upload(file, param, callback) {
      let _this = this,
        fileSize = file.size,
        fileName = file.name,
        chunkSize = this.chunkSize,
        md5FileStored = param.md5FileStored,
        chunkIndexList = param.chunkIndexList || [];
      let formData = new FormData();
      formData.set('frontUUID', file.uid);
      formData.set('md5', file.md5);
      formData.set('localFileSourceIcon', '');
      formData.set('size', fileSize);
      let source = '上传';
      if (this.curButton) {
        source = this.curButton.button.buttonName;
      }
      formData.set('source', source);
      if (this.curButton && this.curButton.button.code == 'onClickReplace') {
        formData.set('origUuid', this.replaceTemp.dbFile.fileID);
      }
      let chunk = chunkSize != undefined && fileSize > chunkSize;
      if (chunk) {
        formData.set('chunkSize', chunkSize);
      }
      let headers = {
        'Content-Disposition': `attachment; filename="${encodeURIComponent(fileName)}"`,
        'Content-Type': 'multipart/form-data'
      };
      let ajaxUpload = function (position) {
        let end = 0;
        if (chunk) {
          // 分块上传的数据
          end = position + chunkSize >= fileSize ? fileSize : position + chunkSize;
          let chunkFile = _this.blobSlice(file, position, end);
          formData.set('file', new File([chunkFile], fileName));
          headers['Content-Range'] = `bytes ${position}-${end - 1}/${fileSize}`;
        } else {
          formData.set('file', file);
        }
        const CancelToken = _this.$axios.CancelToken;
        const source = CancelToken.source();
        _this.cancelSourceList.push(source);
        _this.$axios
          .post('/proxy-repository/repository/file/mongo/savefilesChunk', formData, {
            headers: headers,
            cancelToken: source.token
          })
          .then(({ data }) => {
            console.log(data);
            if (data && data.success) {
              if (data.data === 'continue') {
                if (_this.fileUploadMap[file.uid]) {
                  _this.fileUploadMap[file.uid].percent = parseInt((end / fileSize) * 100);
                  ajaxUpload(end);
                }
              } else if (!chunk || (end == fileSize && Array.isArray(data.data) && data.data.length > 0)) {
                // 全部上传结束了
                _this.fileUploadMap[file.uid].status = UPLOAD_STATUS.DONE;
                _this.fileUploadMap[file.uid].percent = 100;
                data.data[0].createTimeStr = moment(data.data[0].createTime).format('YYYY-MM-DD HH:mm');
                _this.fileUploadMap[file.uid].dbFile = data.data[0];
                _this.fileUploadMap[file.uid].url = `/proxy-repository/repository/file/mongo/download?fileID=${data.data[0].fileID}`;
                if (typeof callback === 'function') {
                  callback.call(_this, _this.fileUploadMap[file.uid]);
                }
              }
            } else {
              if (_this.fileUploadMap && _this.fileUploadMap[file.uid]) {
                _this.fileUploadMap[file.uid].status = UPLOAD_STATUS.ERROR;
              }
            }
          })
          .catch(function (error) {
            console.error('上传文件失败, 异常: ', error);
            if (_this.fileUploadMap && _this.fileUploadMap[file.uid]) {
              _this.fileUploadMap[file.uid].status = UPLOAD_STATUS.ERROR;
            }
          });
      };
      let start = 0,
        chunkStoredCnt = chunkIndexList.length;
      if (chunkStoredCnt > 0) {
        // 断点续传，计算从哪个位置开始
        while (chunkStoredCnt-- > 0) {
          start = start + chunkSize >= fileSize ? fileSize : start + chunkSize;
        }
        this.fileUploadMap[file.uid].percent = parseInt((start / fileSize) * 100); // 更新已上传的进度
      }
      ajaxUpload(md5FileStored ? fileSize - 2 : start);
    },
    loadFileChunkInfoByMD5(file, callback) {
      if (this.chunkSize == undefined) {
        // 无分块上传的情况
        callback(false);
        return;
      }
      let params = {
        md5: file.md5,
        chunkSize: this.chunkSize
      };

      let _this = this;
      const CancelToken = _this.$axios.CancelToken;
      const source = CancelToken.source();
      _this.cancelSourceList.push(source);
      this.$axios
        .get('/proxy-repository/repository/file/mongo/getFileChunkInfo', {
          params: params,
          cancelToken: source.token
        })
        .then(({ data }) => {
          console.log(data);
          if (data.success) {
            _this.md5FileObj[params.md5] = data.data;
            // 可能存在文件名不同，文件内容相同的情况，分块进行去重
            let chunkIndexList = data.data.chunkIndexList.length ? Array.from(new Set(data.data.chunkIndexList)) : [];
            callback({ md5FileStored: data.data.hasMd5FileFlag, chunkIndexList });
          }
        })
        .catch(error => {
          if (_this.fileUploadMap && _this.fileUploadMap[file.uid]) {
            _this.fileUploadMap[file.uid].status = UPLOAD_STATUS.ERROR;
          }
        });
    },
    computeFileMD5(file, callback) {
      var _this = this,
        chunkSize = this.chunkSize,
        chunks = Math.ceil(file.size / chunkSize),
        currentChunk = 0,
        spark = new SparkMD5.ArrayBuffer(),
        fileReader = new FileReader();

      fileReader.onload = function (e) {
        spark.append(e.target.result);
        currentChunk++;

        if (currentChunk < chunks) {
          loadNext();
        } else {
          let md5Str = spark.end();
          console.debug('计算文件hash值: ', md5Str); // Compute hash
          file.md5 = md5Str;
          callback();
        }
      };

      fileReader.onerror = function () {
        console.warn('读取文件失败');
      };

      function loadNext() {
        var start = currentChunk * chunkSize,
          end = start + chunkSize >= file.size ? file.size : start + chunkSize;

        fileReader.readAsArrayBuffer(_this.blobSlice(file, start, end));
      }

      loadNext();
    },
    getChunkSize() {
      //切片每次上传的块最大值
      var __size = (function (name) {
        var arr = document.cookie.match(new RegExp('(^| )' + name + '=([^;]*)(;|$)'));
        return arr ? decodeURIComponent(arr[2]) : null;
      })('fileupload.maxChunkSize');

      this.chunkSize = __size ? parseInt(__size) : undefined;
      return this.chunkSize;
    },
    blobSlice(file, start, end) {
      var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice;
      return blobSlice.call(file, start, end);
    },
    getFileIcon(filename) {
      if (/\.doc[x]?$/i.test(filename)) {
        //word文档
        return 'file-word';
      }

      if (/\.xls[x]?$|\.csv$/i.test(filename)) {
        //excel文档
        return 'file-excel';
      }

      if (/\.ppt[x]?$/i.test(filename)) {
        //ppt文档
        return 'file-ppt';
      }

      if (/\.pdf$/i.test(filename)) {
        //pdf 文档
        return 'file-pdf';
      }

      if (/\.txt$/i.test(filename)) {
        //文本文档
        return 'file-text';
      }

      if (/\.bpm$|\.png$|\.gif$|\.jpg$|\.jpeg/i.test(filename)) {
        //图片
        return 'file-image';
      }
      if (/\.zip$/i.test(filename)) {
        //压缩文件
        return 'file-zip';
      }
      return 'file';
    },
    formatSize(size, pointLength, units) {
      var unit;
      units = units || ['B', 'KB', 'MB', 'GB', 'TB'];
      while ((unit = units.shift()) && size > 1024) {
        size = size / 1024;
      }
      return (unit === 'B' ? size : size.toFixed(pointLength || 2)) + unit;
    },
    setButtonControl(fileResult) {
      // 判断文件是否支持预览
      let regFileType =
        /\.(wps|png|jpe?g|gif|bmp|tif|tiff|zip|rar|7z|rtf|dot|doc|docx|ppt|pptx|xls|xlsx|vsd|vsdx|pdf|mp3|ogg|mp4|webm|text|txt|mthml|html|htm|xml|css|js|json|sql|log|properties|yaml|yml|Zat|sh|md|java|mine|groovy|jsp|ofd|wmv|ods|odt|odp|pot|potx|ppsm|ods|xls|xlsb|xlsm|xlsx|doc|docm|docx|dot|dotm|dotx|odt|odp|pot|potm|potx|pps|ppsm|ppsx|ppt|pptm|pptx)(\?.*)?/i;
      fileResult.buttonControl.canPreview = !regFileType.test(fileResult.name);
    },
    // 获取预览服务地址
    getPreviewHostServer() {
      let _this = this;
      this.$clientCommonApi.getSystemParamValue('document.preview.path', function (path) {
        _this.previewServerHost = path;
      });
    },
    checkAcceptFile(file) {
      if (!this.accpetList.length) {
        return true;
      }
      const fileName = file.name;
      const suffix = fileName.substring(fileName.lastIndexOf('.'));
      if (!accpet.includes(suffix)) {
        return false;
      }
      return true;
    },
    getLogicFileInfo(fileIds) {
      let _this = this;
      if (typeof fileIds == 'string') {
        fileIds = fileIds.split(this.separator);
      }
      if (fileIds.length) {
        each(fileIds, item => {
          this.getLogicFileInfoReq([item]);
        });
      }
    },
    getLogicFileInfoReq(fileId) {
      $axios
        .post('/json/data/services', {
          serviceName: 'mongoFileService',
          methodName: 'getLogicFileInfo',
          args: JSON.stringify(fileId),
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.addFileList({
              uid: data.data.fileID,
              name: data.data.fileName || data.data.filename,
              size: data.data.fileSize,
              status: UPLOAD_STATUS.DONE,
              dbFile: data.data
            });
          }
        });
    },
    // 获取创建附件用户信息
    fetchOrgUserDetail(item) {
      if (!this.userNameMap[item.creator]) {
        this.userNameMap[item.creator] = { files: [], userName: '' };
        $axios
          .get(`/proxy/api/user/org/getUserDetailsUnderSystem`, {
            params: {
              userId: item.creator
            }
          })
          .then(({ data }) => {
            this.userNameMap[item.creator].userName = data.data.userName;
            each(this.userNameMap[item.creator].files, citem => {
              let hasIndex = findIndex(this.fileList, { uid: citem.fileID });
              if (hasIndex > -1) {
                this.$set(this.fileList[hasIndex].dbFile, 'userName', data.data.userName);
              }
            });
          });
      }
      this.userNameMap[item.creator].files.push(item);
    },
    setValue(value) {
      this.fileList = [];
      if (value && typeof value == 'string') {
        value = value.split(this.separator);
        if (value.length) {
          each(value, item => {
            this.getLogicFileInfoReq([item]);
          });
        }
      } else if (value && typeof value == 'object') {
        if (!isEmpty(value)) {
          if (!isArray(value) && value.fileID) {
            value = [value];
          }
          each(value, item => {
            this.addFileList({
              uid: item.fileID,
              name: item.fileName || item.filename,
              size: item.fileSize,
              status: UPLOAD_STATUS.DONE,
              dbFile: item
            });
          });
        }
      }
    },
    getValue() {
      return this.fileList;
    }
  }
};
</script>
<style lang="less" scoped>
.simpleUpload_component {
  .file-list-item {
    position: relative;

    .file-list-item-buttons-absolute {
      position: absolute;
      right: var(--w-upload-list-padding);
      background-color: var(--w-upload-item-background-hover);
      z-index: 1;
    }
  }
}
</style>
