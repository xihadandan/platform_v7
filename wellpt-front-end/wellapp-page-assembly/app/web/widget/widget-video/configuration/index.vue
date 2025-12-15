<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model ref="form" :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" />
          </a-form-model-item>

          <a-collapse :bordered="false" expandIconPosition="right" default-active-key="videoSrcSetting">
            <a-collapse-panel key="videoSrcSetting">
              <template slot="header">
                <a-popover trigger="hover" placement="left">
                  <template slot="content">
                    <div style="width: 350px">
                      浏览器和设备支持的视频格式各不相同, 为更好的支持多浏览器, 确保视频内容能够在尽可能多的浏览器和设备上播放,
                      可通过设置多个不同视频格式的视频源
                    </div>
                  </template>
                  视频源设置
                  <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                </a-popover>
              </template>
              <a-table
                rowKey="id"
                :pagination="false"
                size="small"
                :bordered="false"
                :columns="sourceColumn"
                :data-source="widget.configuration.sources"
                class="widget-table-col-table no-border"
              >
                <template slot="srcSlot" slot-scope="text, record">
                  <template v-if="record.videoSourceType == 'dbFile'">
                    {{ record.fileName }}
                  </template>
                  <a-input v-else v-model="record.src" size="small" :style="{ width: '140px' }"></a-input>
                </template>
                <template slot="typeSlot" slot-scope="text, record">
                  <a-select v-model="record.type" style="width: 100%">
                    <a-select-option value="video/mp4">mp4</a-select-option>
                    <a-select-option value="video/webm">webm</a-select-option>
                  </a-select>
                </template>

                <template slot="operationSlot" slot-scope="text, record, index">
                  <WidgetDesignDrawer :id="'videoSrcConfig' + widget.id" title="编辑视频源" :designer="designer">
                    <a-button type="link" size="small" title="编辑视频源">
                      <Icon type="pticon iconfont icon-ptkj-shezhi" />
                    </a-button>
                    <template slot="content">
                      <a-form-model>
                        <a-form-model-item label="来源">
                          <a-radio-group v-model="record.videoSourceType" button-style="solid" size="small">
                            <a-radio-button value="url">视频地址</a-radio-button>
                            <a-radio-button value="dbFile">视频文件</a-radio-button>
                          </a-radio-group>
                          <a-button
                            v-if="record.videoSourceType == 'dbFile'"
                            @click="clickToUploadVideo(record)"
                            size="small"
                            type="link"
                            :icon="uploading ? 'loading' : 'upload'"
                          >
                            上传
                          </a-button>
                          <template v-if="record.videoSourceType == 'dbFile' && record.fileID">
                            <a-divider type="vertical" />
                            <a-button size="small" type="link" icon="video-camera">
                              {{ record.fileName }}
                            </a-button>
                          </template>

                          <a-input v-model="record.src" allow-clear v-if="record.videoSourceType == 'url'"></a-input>
                        </a-form-model-item>
                        <a-form-model-item label="格式">
                          <a-select v-model="record.type" style="width: 120px">
                            <a-select-option value="video/mp4">mp4</a-select-option>
                            <a-select-option value="video/webm">webm</a-select-option>
                          </a-select>
                        </a-form-model-item>
                      </a-form-model>
                    </template>
                  </WidgetDesignDrawer>
                  <a-button type="link" size="small" title="删除" @click="widget.configuration.sources.splice(index, 1)">
                    <Icon type="pticon iconfont icon-ptkj-shanchu" />
                  </a-button>
                </template>
                <template slot="footer">
                  <WidgetDesignDrawer :id="'videoSrcConfig' + widget.id" title="新增视频源" :designer="designer">
                    <a-button type="link" icon="plus" size="small" :style="{ paddingLeft: '7px' }">新增</a-button>
                    <template slot="content">
                      <a-form-model>
                        <a-form-model-item label="来源">
                          <a-radio-group v-model="sourceItem.videoSourceType" button-style="solid" size="small">
                            <a-radio-button value="url">视频地址</a-radio-button>
                            <a-radio-button value="dbFile">视频文件</a-radio-button>
                          </a-radio-group>
                          <a-button
                            v-if="sourceItem.videoSourceType == 'dbFile'"
                            @click="clickToUploadVideo(sourceItem)"
                            size="small"
                            type="link"
                            :icon="uploading ? 'loading' : 'upload'"
                          >
                            上传
                          </a-button>
                          <template v-if="sourceItem.videoSourceType == 'dbFile' && sourceItem.fileID">
                            <a-divider type="vertical" />
                            <a-button size="small" type="link" icon="video-camera">
                              {{ sourceItem.fileName }}
                            </a-button>
                          </template>

                          <a-input v-model="sourceItem.src" allow-clear v-if="sourceItem.videoSourceType == 'url'"></a-input>
                        </a-form-model-item>

                        <a-form-model-item label="视频格式">
                          <a-select v-model="sourceItem.type" style="width: 100%">
                            <a-select-option value="video/mp4">mp4</a-select-option>
                            <a-select-option value="video/webm">webm</a-select-option>
                          </a-select>
                        </a-form-model-item>
                      </a-form-model>
                    </template>
                    <template slot="footer" slot-scope="{ close }">
                      <a-button size="small" type="primary" @click.stop="e => onConfirmOk(e, close)">确定</a-button>
                    </template>
                  </WidgetDesignDrawer>
                </template>
              </a-table>
              <a-form-model-item label="封面">
                <ImageLibrary style="float: right" v-model="widget.configuration.poster" />
              </a-form-model-item>
              <a-form-model-item label="自动播放">
                <a-switch v-model="widget.configuration.autoplay" />
              </a-form-model-item>
              <template v-if="designer.terminalType == 'pc'">
                <a-form-model-item label="宽度">
                  <a-input-number v-model="widget.configuration.style.width" />
                </a-form-model-item>
                <a-form-model-item label="高度">
                  <a-input-number v-model="widget.configuration.style.height" />
                </a-form-model-item>
                <a-form-model-item>
                  <template slot="label">
                    <a-popover placement="left">
                      <template slot="content">
                        <div style="width: 300px">
                          定义了视频播放器的期望宽高比，它帮助播放器保持视频内容的原始比例，防止在缩放或全屏时发生变形。常见的宽高比包括
                          16:9、4:3 等
                        </div>
                      </template>
                      宽高比
                      <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                    </a-popover>
                  </template>
                  <a-input-number v-model="widget.configuration.aspectRatio[0]" :min="1" />
                  :
                  <a-input-number v-model="widget.configuration.aspectRatio[1]" :min="1" />
                </a-form-model-item>
              </template>
            </a-collapse-panel>
          </a-collapse>
        </a-form-model>
      </a-tab-pane>
    </a-tabs>

    <a-upload
      name="video"
      list-type="picture-card"
      :file-list="[]"
      :show-upload-list="false"
      :before-upload="e => beforeUpload(e, 'video')"
      :customRequest="e => customRequest(e, 'video')"
      style="display: none"
    >
      <div ref="uploadTrig">
        <div class="ant-upload-text">点击上传</div>
      </div>
    </a-upload>
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import { customFileUploadRequest } from '@framework/vue/utils/function';

export default {
  name: 'WidgetVideoConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      sourceItem: {
        id: undefined,
        src: undefined,
        type: 'video/mp4',
        dbFileSrc: undefined,
        fileName: undefined,
        fileID: undefined,
        videoSourceType: 'dbFile'
      },
      uploadTargetSourceItem: undefined,
      sourceColumn: [
        { title: '视频', dataIndex: 'src', scopedSlots: { customRender: 'srcSlot' } },
        { title: '格式', dataIndex: 'type', scopedSlots: { customRender: 'typeSlot' }, align: 'center' },
        { title: '操作', dataIndex: 'operation', width: 95, scopedSlots: { customRender: 'operationSlot' }, align: 'center' }
      ],
      uploading: false
    };
  },
  components: {},
  computed: {},
  created() {},
  methods: {
    clickToUploadVideo(item) {
      this.uploadTargetSourceItem = item;
      this.$refs.uploadTrig.click();
    },
    beforeUpload(file) {
      let matched = file.type.startsWith('video/');
      if (!matched) {
        this.$message.error('只允许上传视频格式的文件');
      }
      return matched;
    },
    customRequest(options) {
      this.uploading = true;
      let opt = {
        file: options.file,
        folder: {
          folderID: 'upload-video'
        }
      };
      let _this = this;
      customFileUploadRequest(opt).then(dbFile => {
        _this.uploading = false;
        _this.uploadTargetSourceItem.fileName = dbFile.filename;
        _this.uploadTargetSourceItem.fileID = dbFile.fileID;
        _this.uploadTargetSourceItem.dbFileSrc = `/proxy-repository/repository/file/mongo/downloadVideoSegment?fileID=${dbFile.fileID}`;
      });
    },
    onConfirmOk(e, close) {
      if ((this.sourceItem.src == undefined || this.sourceItem.src == '') && this.sourceItem.dbFileSrc == undefined) {
        return;
      }
      this.sourceItem.id = generateId();
      this.widget.configuration.sources.push(deepClone(this.sourceItem));
      this.sourceItem.src = undefined;
      this.sourceItem.type = 'video/mp4';
      this.sourceItem.dbFileSrc = undefined;
      this.sourceItem.fileName = undefined;
      this.sourceItem.fileID = undefined;
      this.sourceItem.videoSourceType = 'dbFile';
      close();
    }
  },
  beforeMount() {},
  mounted() {},
  configuration(widget) {
    let conf = { autoplay: false, poster: undefined, sources: [], aspectRatio: [undefined, undefined], style: { width: 400, height: 300 } };
    // if (widget != undefined && widget.useScope == 'bigScreen') {
    //   conf.style.width = 600;
    // }
    return conf;
  }
};
</script>
