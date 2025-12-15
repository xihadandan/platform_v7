<template>
  <div class="_file-item">
    <div class="_file-icon">
      <Icon :type="fileIcon.type" :style="fileIcon.style" />
    </div>
    <div class="_file-right">
      <div class="_file-name" :title="itemData.fileName">{{ itemData.fileName }}</div>
      <div class="_file-right-conter">
        <span>创建人：{{ itemData.resultSource && itemData.resultSource.creator }}</span>
        <span>创建时间：{{ createTime }}</span>
      </div>
      <div>
        <a-button type="link" size="small" class="btn-link-preview" :loading="previewLoading" @click="handlePreview">
          <Icon type="pticon iconfont icon-szgy-zonghechaxun" />
          预览
        </a-button>
        <template v-if="itemData.resultSource && itemData.resultSource.index !== 'dms_file'">
          <a-button type="link" size="small" @click="handleOpenDetail">
            <Icon type="pticon iconfont icon-luojizujian-huoquwenjianxinxi" />
            原文
          </a-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { preview, getFileIcon } from '@framework/vue/lib/preview/filePreviewApi';
import mixin from './mixin';

export default {
  name: 'FileItem',
  mixins: [mixin],
  props: {
    itemData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    const fileIconColorMap = {
      'file-word': 'var(--w-primary-color)',
      'file-excel': 'var(--w-success-color)',
      'file-ppt': 'var(--w-danger-color)',
      'file-pdf': 'var(--w-info-color)',
      'file-image': 'var(--w-warning-color)'
    };
    return {
      fileIconColorMap,
      fileRepositoryServerHost: '',
      previewLoading: false
    };
  },
  computed: {
    fileIcon() {
      const fileType = getFileIcon(this.itemData.fileName);
      const fileIconStyle = `color:${this.fileIconColorMap[fileType] || 'var(--w-primary-color)'}`;
      return {
        type: fileType,
        style: fileIconStyle
      };
    },
    createTime() {
      let createTime = '';
      if (this.itemData && this.itemData.createTime) {
        createTime = this.itemData.createTime;
        createTime = createTime.substring(0, createTime.length - 3);
      }
      return createTime;
    }
  },
  methods: {
    handlePreview() {
      const file = this.itemData;
      if (this.fileRepositoryServerHost) {
        preview(`${this.fileRepositoryServerHost}/wopi/files/${file.fileID}?access_token=336dc563-1d17-44a3-a916-e8abe2e88cbb`);
      } else {
        this.previewLoading = true; // 初次加载要有个loading，待预览api初始化成功会回调进行关闭
        this.$clientCommonApi.getSystemParamValue('sys.context.path', serv => {
          if (serv) {
            this.fileRepositoryServerHost = 'null' === serv ? null : serv;
            preview(`${serv}/wopi/files/${file.fileID}?access_token=336dc563-1d17-44a3-a916-e8abe2e88cbb`, {
              callback: () => {
                this.previewLoading = false;
              }
            });
          }
        });
      }
    }
  }
};
</script>
<style lang="less" scoped>
._file-item {
  display: flex;
  // width: 360px;
  padding: 8px 12px;
  ._file-icon {
    width: 16px;
    margin-right: 4px;
  }
  ._file-right {
    // width: 316px;
    flex: 1;
    overflow: hidden;
  }
  ._file-name {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  ._file-right-conter {
    color: var(--w-gray-color-9);
    display: flex;
    justify-content: space-between;
    & + div {
      padding-top: 7px;
    }
  }
  .btn-link-preview {
    padding-left: 0;
  }
}
</style>
