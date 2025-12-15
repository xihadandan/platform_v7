<template>
  <div>
    <a-upload :file-list="fileList" :beforeUpload="beforeUpload" :showUploadList="false" :disabled="true">
      <a-button type="line">
        <a-icon type="upload" />
        上传
      </a-button>
      <div class="widget-file-upload-tips" v-if="visibleTips">支持doc格式附件，附件大小不能超过4M</div>
    </a-upload>
    <div class="widget-file-upload-list">
      <a-popover
        v-for="(file, i) in fileList"
        :key="i"
        :title="file.name"
        trigger="hover"
        :visible="file.hovered"
        :defaultVisible="defaultVisible"
        :getPopupContainer="trigger => trigger.parentNode"
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
              width="var(--w-upload-list-text-size)"
              strokeColor="var(--w-primary-color)"
              :strokeWidth="10"
              :showInfo="false"
            />
            <template v-else-if="file.status === 'error'">
              <a-icon type="exclamation-circle" theme="filled" class="error-icon" />
            </template>
            <template v-else>
              <a-icon :type="file.icon" />
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
              <a-button v-for="(btn, i) in rowBtnSimple" :key="i" size="small" :type="btn.type || 'link'" :loading="btn.loading">
                <Icon v-if="btn.icon" :type="btn.icon" />
                {{ btn.buttonName }}
              </a-button>
            </span>
          </div>
        </div>
      </a-popover>
    </div>
  </div>
</template>
<script type="text/babel">
import { UPLOAD_STATUS, rowBtnSimple, fileList } from './config';
export default {
  name: 'ThemeUploadSimple',
  props: {
    fileList: {
      type: Array,
      default: () => []
    },
    defaultVisible: {
      type: Boolean,
      default: false
    },
    visibleTips: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      rowBtnSimple
      // fileList,
    };
  },
  methods: {
    onFileItemHoverVisibleChange(v, file) {
      if (file.dropdownButtonVisible === true) {
        file.hovered = true;
        return;
      }
      file.hovered = file.status == UPLOAD_STATUS.DONE && v;
    },
    beforeUpload(file) {
      return false;
    }
  }
};
</script>

<style></style>
