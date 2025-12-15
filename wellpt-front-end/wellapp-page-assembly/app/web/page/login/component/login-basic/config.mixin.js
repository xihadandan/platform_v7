import StyleColorTreeSelect from '../../../theme-designer/component/design/lib/style-color-tree-select.vue';
import DraggableTreeList from '@pageAssembly/app/web/widget/commons/draggable-tree-list';
import QuillEditor from '@pageAssembly/app/web/lib/quill-editor';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';

export default {
  components: { StyleColorTreeSelect, DraggableTreeList, QuillEditor, ColorPicker, ImageLibrary },
  props: {
    config: Object
  },
  data() {
    return {
      uploading: false,
      colorFetched: false,
      colorConfig: {},
      acceptTypes: ['image/gif', 'image/jpeg', 'image/png', 'image/svg+xml'],
      acceptTip: '只允许上传JPG、PNG、GIF、SVG 的图片格式',
      limitSize: 50
    };
  },
  methods: {
    setTargetUrl(target, propName, url) {
      this.$set(target, propName, url);
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
        .post('/proxy-repository/repository/file/mongo/savefilesChunk?anonymous=true', formData, {
          headers: headers
        })
        .then(({ data, headers }) => {
          this.uploading = false;
          if (data.code == 0 && data.data) {
            options.onSuccess();
            if (typeof afterUpload == 'function') {
              afterUpload.call(this, `/proxy-repository/repository/file/mongo/download?fileID=${data.data[0].fileID}`);
              // afterUpload.call(this, `/proxy-repository/repository/file/mongo/download?guest-uri-token=${headers['guest-uri-token']}`);
            }
          }
        });
    },

    beforeUpload(file, limitSize) {
      let isJpgOrPng = ['image/gif', 'image/jpeg', 'image/png', 'image/svg+xml'].includes(file.type);
      console.log(file);
      if (!isJpgOrPng) {
        this.$message.error('只允许上传 jpeg、png 或者 gif 图片格式');
      }
      let limit = true;
      if (limitSize != undefined) {
        limit = file.size / 1024 / 1024 < limitSize;
        if (!limit) {
          this.$message.error(`图片大小应小于 ${limitSize}M`);
        }
      }

      return isJpgOrPng && limit;
    },
    generateDefaultConfig() {
      return {};
    },
    fetchThemeSpecify() {
      $axios
        .get(`/proxy/api/theme/specify/getEnabled`, { params: {} })
        .then(({ data }) => {
          console.log('获取主题规范', data);
          if (data.code == 0) {
            let specifyDefJson = JSON.parse(data.data.defJson);
            this.colorConfig = specifyDefJson.colorConfig;
            this.colorFetched = true;
          }
        })
        .catch(error => {});
    },
    // 添加轮播图片
    addReplaceBgImage() {
      this.config.backgroundImage.push(undefined);
    }
  },

  beforeMount() {
    this.fetchThemeSpecify();
  }
};
