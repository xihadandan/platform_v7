<template>
  <div @click.stop="() => {}">
    <a-spin v-if="loading"></a-spin>
    <div v-else>
      <template v-if="files.length > 0">
        <div v-for="(file, i) in files" :key="'file_' + i" style="line-height: 26px; display: flex">
          <a-popover :title="file.fileName" placement="topLeft" :mouseEnterDelay="0.3">
            <template slot="content">
              <div class="table-row-cell-file-info-card">
                <p>{{ $t('WidgetTable.time', '时间') + '：' + file.modifyTime }}</p>
                <p>{{ $t('WidgetTable.size', '大小') + '：' + (file.fileSize | fileSizeFormat) }}</p>
                <div style="text-align: right; margin-top: 8px" v-if="slotOption.options.fileButton.enable">
                  <WidgetTableButtons
                    v-if="widgetTableContext"
                    :button="slotOption.options.fileButton"
                    size="small"
                    :developJsInstance="widgetTableContext.developJsInstance"
                    :meta="row"
                    :eventWidget="widgetTableContext"
                    @button-click="e => fileButtonClick(e, file)"
                  ></WidgetTableButtons>
                </div>
              </div>
            </template>
            <div class="file-name-icon">
              <Icon :type="file.icon" />
              <label style="color: var(--w-link-color)" class="cell-filename">
                {{ file.fileName }}
              </label>
            </div>
          </a-popover>
        </div>
      </template>
      <div v-else v-html="slotOption.defaultContentIfNull" />
    </div>
  </div>
</template>
<style lang="less">
td {
  .file-name-icon {
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    text-wrap: nowrap;
  }
  > .ant-descriptions-item-content {
    .file-name-icon {
      max-width: unset;
    }
  }
}
.table-row-cell-file-info-card {
  p {
    margin-bottom: 0px;
    color: #999;
  }
}
</style>
<script type="text/babel">
import { preview, getFileIcon } from '@framework/vue/lib/preview/filePreviewApi';
import 'viewerjs/dist/viewer.min.css';
import { api as viewerApi } from 'v-viewer';
import cellRenderMixin from './cellRenderMixin';
export default {
  name: 'CellDataFileRender',
  mixins: [cellRenderMixin],
  title: '数据文件渲染器',
  scope: ['pc', 'mobile'],
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      files: [],
      loading: false
    };
  },
  filters: {
    fileSizeFormat(text) {
      if (text != undefined) {
        let size = parseInt(text);
        var unit;
        var units = ['B', 'KB', 'MB', 'GB', 'TB'];
        while ((unit = units.shift()) && size > 1024) {
          size = size / 1024;
        }
        return (unit === 'B' ? size : size.toFixed(2)) + ' ' + unit;
      }

      return '-';
    }
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.initFiles();
  },
  methods: {
    fileButtonClick(button, file) {
      if (button.id == 'download') {
        this.downloadFileSilence(`/proxy-repository/repository/file/mongo/download?fileID=${file.fileID}`);
      } else if (button.id == 'previewFile') {
        this.onClickPreviewFile(file);
      }
    },
    isImageType(filename) {
      const extensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg']; // 你可以根据需要添加更多扩展名
      const ext = filename.split('.').pop().toLowerCase();
      return extensions.includes(ext);
    },
    fileNameMatchSuffix(fileName, suffix) {
      let matches = [suffix];
      if (Array.isArray(suffix)) {
        matches = suffix;
      }
      for (let i = 0, len = matches.length; i < len; i++) {
        if (fileName.toLowerCase().endsWith(matches[i].toLowerCase())) {
          return true;
        }
      }
      return false;
    },
    // 预览文件
    onClickPreviewFile(file) {
      if (this.isImageType(file.fileName)) {
        let images = [];
        for (let i = 0; i < this.files.length; i++) {
          if (this.isImageType(this.files[i].fileName)) {
            images.push(
              Object.assign({}, this.files[i], { url: `/proxy-repository/repository/file/mongo/download?fileID=${this.files[i].fileID}` })
            );
          }
        }
        this.previewImage(file, images);
      } else {
        let _this = this;
        if (_this.fileRepositoryServerHost == '') {
          this.$clientCommonApi.getSystemParamValue('sys.context.path', function (serv) {
            if (serv) {
              _this.fileRepositoryServerHost = 'null' === serv ? null : serv;
              preview(`${serv}/wopi/files/${file.fileID}?access_token=336dc563-1d17-44a3-a916-e8abe2e88cbb`, {
                callback: function () {}
              });
            }
          });
          return;
        }
        preview(`${_this.fileRepositoryServerHost}/wopi/files/${file.fileID}?access_token=336dc563-1d17-44a3-a916-e8abe2e88cbb`);
      }
    },
    //  预览图片
    previewImage(file, fileList) {
      let imageObjs = [],
        index = 0;
      for (let i = 0, len = fileList.length; i < len; i++) {
        imageObjs.push({
          src: fileList[i].url
        });
        if (file.fileID === fileList[i].fileID) {
          index = i;
        }
      }
      // options 查阅 ；https://www.npmjs.com/package/viewerjs
      const $viewer = viewerApi({
        options: {
          className: 'widget-file-upload-viewer',
          toolbar: true,
          url: 'src',
          title: false,
          toolbar: {
            zoomIn: 4,
            zoomOut: 4,
            oneToOne: 4,
            reset: 4,
            prev: fileList.length > 1 ? 1 : 0,
            // play: { // 放映模式
            //   show: 4,
            //   size: 'large',
            // },
            next: fileList.length > 1 ? 1 : 0,
            rotateLeft: 4,
            rotateRight: 4,
            flipHorizontal: 4,
            flipVertical: 4
          },
          initialViewIndex: index
        },
        images: imageObjs
      });
    },
    downloadFileSilence(url) {
      var _this = this;
      var hiddenIFrameID = 'hiddenDownloader' + new Date().getTime();
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
    downloadFile(file) {},
    loadFilesByFileUuids(fileUuids) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/repository/file/mongo/getFileInfos`, fileUuids)
          .then(({ data }) => {
            this.setFiles(data.data);
            resolve(data.data);
          })
          .catch(error => {});
      });
    },
    loadFilesByFolderUuidAndPurpose(folderUuid, purpose) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/repository/file/mongo/getFileInfosUnderFolder`, {
            params: {
              folderUuid,
              purpose
            }
          })
          .then(({ data }) => {
            let matchFile = [];
            if (data.data.length) {
              for (let d of data.data) {
                if (purpose == d.purpose) {
                  matchFile.push(d);
                }
              }
            }

            this.setFiles(matchFile);
            resolve(matchFile);
          })
          .catch(error => {});
      });
    },
    setFiles(list) {
      if (list) {
        list.forEach(file => {
          file.icon = getFileIcon(file.fileName);
          file.url = `/proxy-repository/repository/file/mongo/download?fileID=${file.fileID}`;
          let { includeFileTypes, excludeFileTypes } = this.slotOption.options;
          let include = true;
          if (includeFileTypes && includeFileTypes.length) {
            include = this.fileNameMatchSuffix(file.fileName, includeFileTypes);
          }
          let exclude = false;
          if (excludeFileTypes && excludeFileTypes.length) {
            exclude = this.fileNameMatchSuffix(file.fileName, excludeFileTypes);
          }

          if (include && !exclude) {
            this.files.push(file);
          }
        });

        this.row[this.slotOption.dataIndex + '$RenderValue'] = this.files;
      }
    },
    initFiles() {
      let { fileUuidField, folderUuidField, dataIndex } = this.slotOption.options;
      let fileUuids = this.row[fileUuidField];
      if (folderUuidField && this.row[folderUuidField]) {
        // 关联数据模型查询的字段别名带dm前缀
        let purpose = fileUuidField.toLowerCase();
        if (
          this.widgetTableContext &&
          this.widgetTableContext.widgetContext &&
          this.widgetTableContext.widgetContext.widget.wtype == 'WidgetFileManager'
        ) {
          purpose = fileUuidField.replace(/([A-Z])/g, '_$1').toLowerCase();
          if (purpose && purpose.startsWith('dm_')) {
            purpose = purpose.substring(3);
          }
        }
        // 优先按文件夹下文件加载
        this.loadFilesByFolderUuidAndPurpose(this.row[folderUuidField], purpose).then(files => {
          if (files.length == 0) {
            // 尝试不带 purpose 进行查询
            this.loadFilesByFolderUuidAndPurpose(this.row[folderUuidField], null).then(_files => {
              if (_files.length == 0) {
                if (typeof fileUuids == 'object') {
                  this.setFiles(fileUuids);
                } else if (typeof fileUuids == 'string') {
                  this.loadFilesByFileUuids(fileUuids.split(';|,'));
                }
              } else if (fileUuids) {
                try {
                  // 流程签署意见内容字段,需要解析意见内容里面涉及到文件ID
                  var parser = new DOMParser();
                  parser.parseFromString(fileUuids, 'text/html'); // 能转换, 说明是签署意见html内容
                  for (let i = 0; i < this.files.length; i++) {
                    if (fileUuids.indexOf(this.files[i].fileID) == -1) {
                      // 只展示意见内容里面带的文件
                      this.files.splice(i--, 1);
                    }
                  }
                } catch (error) {
                  if (fileUuids) {
                    this.loadFilesByFileUuids(fileUuids.split(';|,'));
                  }
                }
              }
            });
          }
        });
      } else if (fileUuids) {
        if (typeof fileUuids == 'object') {
          this.setFiles(fileUuids);
        } else if (typeof fileUuids == 'string') {
          this.loadFilesByFileUuids(fileUuids.split(';|,'));
        }
      }
    }
  }
};
</script>
