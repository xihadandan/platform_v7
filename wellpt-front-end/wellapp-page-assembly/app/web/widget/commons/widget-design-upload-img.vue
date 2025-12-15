<template>
  <div class="widget-design-upload-img-container">
    <div :class="['img-container', bgCover ? 'transparent-bg' : '']">
      <img :src="value" v-if="value" class="img-preview" @click="clickToUploadImg" />
      <a-button
        size="small"
        icon="delete"
        type="danger"
        class="delete-img"
        @click.stop="setTargetUrl(undefined)"
        v-if="url != undefined"
      ></a-button>
      <a-button size="small" type="link" icon="upload" class="upload-button" @click="clickToUploadImg" v-if="url == undefined">
        上传图片
      </a-button>
    </div>
    <div class="img-upload-tip" v-if="accept.length > 0">支持格式: {{ accept.join('、') }}</div>
    <a-upload
      name="file"
      ref="upload"
      :file-list="[]"
      :show-upload-list="false"
      :before-upload="e => beforeUpload(e)"
      :customRequest="e => customRequest(e, 'design-images', (url, data) => setTargetUrl(url, data))"
    >
      <a-button ref="uploadButton" v-show="false"></a-button>
    </a-upload>
  </div>
</template>
<style lang="less">
.widget-design-upload-img-container {
  .img-container {
    width: 100%;
    height: e('calc(100% - 20px)');
    max-height: 200px;
    padding: 10px;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    .img-preview {
      width: 100%;
      height: 100%;
      object-fit: contain;
      cursor: pointer;
    }
    .delete-img {
      position: absolute;
      top: 0px;
      right: 0px;
      border-radius: 0px;
    }
  }
  .img-upload-tip {
    font-size: 12px;
    color: #9e9e9e;
  }
  .transparent-bg {
    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABGdBTUEAALGPC/xhBQAAAFpJREFUWAntljEKADAIA23p6v//qQ+wfUEcCu1yriEgp0FHRJSJcnehmmWm1Dv/lO4HIg1AAAKjTqm03ea88zMCCEDgO4HV5bS757f+7wRoAAIQ4B9gByAAgQ3pfiDmXmAeEwAAAABJRU5ErkJggg==')
      0% 0% / 28px;
  }
}
</style>
<script type="text/babel">
export default {
  name: 'WidgetDesignUploadImg',
  props: {
    wid: {
      type: String,
      required: true
    },
    bgCover: {
      type: Boolean,
      default: true
    },
    accept: {
      type: Array,
      default: function () {
        return ['jpeg', 'png', 'gif', 'svg'];
      }
    },
    value: String,
    limitSize: Number | String // 1024 or 1M
  },
  components: {},
  computed: {
    vLimitSize() {
      if (this.limitSize != null) {
        return typeof this.limitSize == 'string'
          ? parseInt(this.limitSize) * Math.pow(1024, ['KB', 'MB', 'G'].indexOf(this.limitSize) + 1)
          : this.limitSize;
      }
      return undefined;
    }
  },
  data() {
    return { url: this.value };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    clickToUploadImg() {
      this.$refs.uploadButton.$el.click();
    },
    setTargetUrl(url, data) {
      this.$emit('input', url);
      this.$emit('change', data);
      this.url = url;
    },
    customRequest(options, key, afterUpload) {
      this.uploading = true;
      let file = options.file,
        fileSize = file.size,
        fileName = file.name,
        formData = new FormData();
      formData.set('frontUUID', file.uid);
      formData.set('localFileSourceIcon', '');
      formData.set('size', fileSize);
      let headers = {
        'Content-Disposition': `attachment; filename="${encodeURIComponent(fileName)}"`,
        'Content-Type': 'multipart/form-data'
      };
      formData.set('file', file);
      $axios
        .post('/proxy-repository/repository/file/mongo/savefilesChunk', formData, {
          headers: headers
        })
        .then(({ data }) => {
          this.uploading = false;
          if (data.code == 0 && data.data) {
            options.onSuccess();
            if (typeof afterUpload == 'function') {
              afterUpload.call(this, `/proxy-repository/repository/file/mongo/download?fileID=${data.data[0].fileID}`, data.data[0]);
            }
          }
        });
    },

    beforeUpload(file) {
      console.log(file);
      let isJpgOrPng = false;
      for (let t of this.accept) {
        if (file.type.indexOf(t) != -1) {
          isJpgOrPng = true;
          break;
        }
      }

      if (!isJpgOrPng) {
        this.$message.error(`只允许上传 ${this.accept.join(' ')} 图片格式`);
      }
      let limit = true;
      if (this.vLimitSize != undefined) {
        limit = file.size / 1024 / 1024 < this.vLimitSize;
        if (!limit) {
          this.$message.error(`图片大小应小于 ${this.limitSize}`);
        }
      }
      return isJpgOrPng && limit;
    }
  }
};
</script>
