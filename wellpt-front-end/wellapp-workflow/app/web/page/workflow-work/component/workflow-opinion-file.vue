<template>
  <a-card :title="$t('WorkflowWork.opinionManager.attachment', '附件') + `（${fileList.length}）`" class="file-list-container">
    <template slot="extra">
      <a-upload
        ref="fileUpload"
        name="file"
        :multiple="true"
        action="/repository/file/mongo/savefiles"
        :fileList="fileList"
        :accept="opinionFileAccept"
        :beforeUpload="onBeforeUpload"
        :customRequest="customRequest"
        @change="onFileChange"
        :showUploadList="false"
      >
        <a-button
          type="link"
          size="small"
          :disabled="(workView.$widget && workView.$widget.displayState == 'disable') || workView.loading.visible"
        >
          <Icon type="pticon iconfont icon-ptkj-shangchuan" />
          {{ $t('WorkflowWork.opinionManager.operation.addAttachment', '添加附件') }}
        </a-button>
      </a-upload>
    </template>
    <Scroll :style="listStyle">
      <a-list :style="listStyle" v-if="fileList.length > 0" item-layout="horizontal" :data-source="fileList" :bordered="false">
        <a-list-item slot="renderItem" slot-scope="item, index" class="file-item" :class="item.status">
          <a-button type="link" size="small" slot="actions" v-if="item.status == 'done'" @click="downloadFile(item)">
            {{ $t('WorkflowWork.opinionManager.operation.download', '下载') }}
          </a-button>
          <a-button type="link" size="small" slot="actions" v-if="item.status == 'error'" @click="reupload(item)">
            {{ $t('WorkflowWork.opinionManager.operation.reupload', '重新上传') }}
          </a-button>
          <a-button type="link" size="small" slot="actions" v-if="item.status != 'uploading'" @click="removeFile(item)">
            {{ $t('WorkflowWork.opinionManager.operation.delete', '删除') }}
          </a-button>
          <a-button
            type="link"
            size="small"
            slot="actions"
            v-if="fileCancelTokenMap[item.uid] && item.status == 'uploading'"
            @click="cancelUploadFile(item)"
          >
            {{ $t('WorkflowWork.opinionManager.operation.cancelUpload', '取消上传') }}
          </a-button>
          <a-icon v-if="item.status == 'uploading'" type="loading" style="margin-right: 8px" />
          <Icon v-if="item.status == 'error'" type="pticon iconfont icon-ptkj-mianxingwenxintishi" />
          <Icon v-else :type="getFileIcon(item.name)"></Icon>
          <div class="item-title" :title="item.name">{{ item.name }}</div>
          <a-progress v-show="item.status == 'uploading'" :percent="item.percent" size="small" :show-info="false" />
        </a-list-item>
      </a-list>
    </Scroll>
  </a-card>
</template>

<script>
import { getFileIcon, getFileSize, upload } from '@framework/vue/lib/preview/filePreviewApi';
import { download } from '@framework/vue/utils/util';
import { trim as stringTrim, each as forEach, isEmpty, isFunction, debounce } from 'lodash';
export default {
  name: 'WorkflowOpinionFile',
  props: {
    workView: Object,
    fileList: {
      type: Array,
      default: []
    },
    opinionFileSetting: Object,
    remoteDelete: {
      type: Boolean,
      default: true
    },
    listStyle: {
      type: Object,
      default() {
        return {
          'max-height': '145px'
        };
      }
    }
  },
  data() {
    return {
      opinionFileAccept: (this.opinionFileSetting.accept || []).join(','),
      fileCancelTokenMap: {}
    };
  },
  methods: {
    onBeforeUpload(file, fileList) {
      const _this = this;
      let setting = _this.opinionFileSetting;
      let accept = _this.opinionFileSetting.accept || [];
      if (accept.length && !_this.isAccessFile(accept, file)) {
        _this.showUploadErrorMsg(
          _this.$t('WorkflowWork.message.onlyAcceptUpload', { accept }, `只能上传${accept}格式附件，请检查后重新上传`)
        );
        return false;
      }

      if (setting.sizeLimit && setting.sizeLimit > 0 && getFileSize(setting.sizeLimit, setting.sizeLimitUnit) < file.size) {
        _this.showUploadErrorMsg(
          `${_this.$t('WidgetFormFileUpload.message.fileSizeLimit', '文件超过上传限制')}:${setting.sizeLimit + setting.sizeLimitUnit}`
        );
        return false;
      }

      if (!setting.allowNameRepeat && _this.isFileNameRepeat(file)) {
        _this.$message.error(`${_this.$t('WidgetFormFileUpload.message.notAllowUploadSameName', '不允许上传重名')}：${file.name}`);
        return false;
      }

      let filePosition = fileList.findIndex(item => item.uid == file.uid) + 1;
      if (setting.numLimit && _this.fileList.length + filePosition > setting.numLimit) {
        _this.showUploadErrorMsg(`${_this.$t('WidgetFormFileUpload.message.uploadLimit', '上传文件已达限制数量')}: ${setting.numLimit}`);
        return false;
      }
      return true;
    },
    showUploadErrorMsg: debounce(function (msg) {
      this.$message.error(msg);
    }, 300),
    isAccessFile(accept, file) {
      let fileName = file.name;
      let suffix = fileName && fileName.substring(fileName.lastIndexOf('.'));
      return accept.includes(suffix);
    },
    isFileNameRepeat(file) {
      return this.fileList.findIndex(item => item.name == file.name) != -1;
    },
    customRequest(requestOption) {
      // requestOption.onBeforeRequest = () => {};
      requestOption.onCreateCancelToken = (cancelToken, requestOption) => {
        this.fileCancelTokenMap[requestOption.file.uid] = cancelToken;
      };
      upload(requestOption);
    },
    onFileChange(info) {
      const _this = this;
      if (info.file.status === 'done') {
        info.fileList.forEach(file => {
          let data = (file.response && file.response.data) || [];
          if (data.length > 0) {
            file.uuid = data[0].fileID;
            file.fileID = data[0].fileID;
          }
        });
        _this.fileList = info.fileList;
        //  _this.fileList2Opinion();
      } else if (info.file.status === 'uploading') {
        let uploadingFile = _this.fileList.find(file => file.uid == info.file.uid);
        if (uploadingFile == null) {
          _this.fileList.push(info.file);
        }
      } else if (info.file.status == 'error') {
        _this.showUploadErrorMsg('存在上传异常的附件，请确认后继续操作');
      }
      _this.$emit('change', _this.fileList);
    },
    downloadFile(file) {
      let url = `/proxy-repository/repository/file/mongo/download?fileID=${file.fileID}`;
      download({ url });
    },
    reupload(file) {
      if (file.originFileObj) {
        file.status = 'uploading';
        this.$refs.fileUpload.$refs.uploadRef.$refs.uploaderRef.post(file.originFileObj);
      } else {
        this.$message.error('上传失败！');
      }
    },
    removeFile(file) {
      const _this = this;
      let fileIndex = _this.fileList.findIndex(item => item == file);
      if (fileIndex != -1) {
        _this.fileList.splice(fileIndex, 1);

        _this.$emit('change', _this.fileList);
      }
      if (_this.remoteDelete && file.status == 'done') {
        $axios.get(`/proxy/repository/file/mongo/delete?id=${file.fileID}`);
      }
    },
    cancelUploadFile(file) {
      this.fileCancelTokenMap[file.uid].cancel('Operation canceled by the user.');
      delete this.fileCancelTokenMap[file.uid];
      this.removeFile(file);
    },
    getFileIcon(fileName) {
      return getFileIcon(fileName);
    }
  }
};
</script>

<style lang="less" scoped>
.file-list-container {
  --w-card-border-radius: var(--w-wf-opinion-editor-border-radius);
  --w-card-head-bg-color: var(--w-gray-color-2);
  --w-card-head-min-height: 40px;
  --w-card-head-padding: 0 4px 0 12px;
  --w-card-head-title-padding: 4px 0;
  --w-card-head-title-color: var(--w-text-color-dark);
  --w-card-head-title-size: var(--w-font-size-base);
  --w-card-head-title-weight: bold;
  ::v-deep .ant-card-body {
    padding: 0;
  }
  .file-item {
    line-height: 40px;
    padding: 0 12px;
    border: none;
    color: var(--w-text-color-dark);
    font-size: var(--w-font-size-base);
    justify-content: start;
    position: relative;

    .item-title {
      padding-left: 8px;
      white-space: nowrap;
      text-overflow: ellipsis;
      width: e('calc(100% - 40px)');
      overflow: hidden;
    }
    ::v-deep .ant-list-item-action {
      display: none;
      max-height: 40px;
      padding-top: 5px;
    }

    &:hover {
      background-color: var(--w-primary-color-1);
      ::v-deep .ant-list-item-action {
        display: inline-block;
      }
    }

    &.error {
      color: var(--w-danger-color);
      ::v-deep .ant-list-item-action {
        display: inline-block;
      }
    }
  }
}
</style>
