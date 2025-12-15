<template>
  <span>
    <div :class="['box-container', boxClass]" :style="boxStyle">
      <div
        :style="vContainerStyle"
        :class="['image-lib-thumbnail ', value !== undefined ? 'has-image' : '']"
        v-show="emptyVisible || (!emptyVisible && value != undefined)"
      >
        <div
          class="transparent-img-bg"
          ref="previewImgDiv"
          style="height: 100%; display: flex; align-items: center; justify-content: center"
        >
          <div
            :style="{ width: '100%', height: '100%', 'background-image': this.value }"
            v-if="value && value.indexOf('-gradient(') > -1"
          ></div>
          <img
            v-else-if="value != undefined && iconSrc == undefined"
            :src="imgSrc"
            :style="{ width: '100%', height: '100%', objectFit: 'scale-down' }"
          />
          <Icon :type="iconSrc" v-if="iconSrc" :style="iconStyle" />
        </div>
        <div class="delete-image" v-show="value != undefined && allowClear" title="删除">
          <Icon type="pticon iconfont icon-ptkj-shanchu" @click.stop="handleClear"></Icon>
        </div>
      </div>
      <div :class="['image-buttons']">
        <slot name="button-before"></slot>
        <a-button type="link" @click.stop="modalOpen" size="small">
          <Icon type="pticon iconfont icon-luojizujian-xuanzebiaoji"></Icon>
          选择
        </a-button>
        <!-- <a-divider type="vertical" /> -->
        <a-button type="link" @click.stop="handleUpload" size="small" v-if="allowSelectType.includes('mongoImages')">
          <Icon type="pticon iconfont icon-ptkj-shangchuan"></Icon>
          上传
        </a-button>
        <slot name="button-after"></slot>
      </div>
      <template v-if="tipVisible && (acceptType.length > 0 || imgTip)">
        <div class="img-upload-tip">
          {{ imgTip ? imgTip : '支持格式: ' + acceptType.join('、') }}
        </div>
      </template>
    </div>
    <a-modal
      :maskClosable="false"
      :z-index="1000000"
      title="请选择"
      :okText="'确定 (' + (url != undefined ? '1' : '0') + ')'"
      v-model="modalVisible"
      @ok="onModalOkSelectDone"
      width="calc(100% - 300px)"
    >
      <a-tabs v-model="activeTabKey" class="image-library-tabs">
        <template v-for="(img, m) in tabOptions">
          <a-tab-pane :key="img.value" :tab="img.label" v-if="imageLib[img.value].length > 0">
            <Scroll style="height: 500px">
              <a-card>
                <div style="text-align: center" v-if="img.value == 'mongoImages' && mongoImageLoading"><a-spin></a-spin></div>
                <a-card-grid
                  v-if="img.value == 'mongoImages' && !mongoImageLoading"
                  class="image-card-grid"
                  style="display: flex; justify-content: center; align-items: center"
                >
                  <a-button type="link" :icon="uploading ? 'loading' : 'plus'" @click="onClickUpload">添加并选择</a-button>
                </a-card-grid>
                <template v-for="(item, i) in imageLib[img.value]">
                  <a-card-grid
                    v-if="img.value != 'icon'"
                    v-show="
                      searchWord[img.value] == undefined ||
                      (item.filename && item.filename.toUpperCase().indexOf(searchWord[img.value].toUpperCase()) > -1) ||
                      (item.url && item.url.toUpperCase().indexOf(searchWord[img.value].toUpperCase()) > -1)
                    "
                    :key="img.value + '-img-grid-' + i"
                    class="transparent-img-bg image-card-grid"
                    @click="
                      onSelectImage(
                        item.fileid
                          ? '/proxy-repository/repository/file/mongo/download?fileID=' + item.fileid
                          : '/app-resources/images' + item.url
                      )
                    "
                  >
                    <svg
                      v-if="
                        item.fileid
                          ? url == '/proxy-repository/repository/file/mongo/download?fileID=' + item.fileid
                          : url == '/static/images' + item.url || url == '/app-resources/images' + item.url
                      "
                      style="position: absolute; top: 0px; right: 0px"
                      t="1721813036132"
                      class="icon"
                      viewBox="0 0 1024 1024"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                      p-id="4264"
                      width="32"
                      height="32"
                    >
                      <path
                        d="M1019.345455 1024c-4.654545-6.981818-9.309091-16.290909-16.29091-20.945455C674.909091 674.909091 346.763636 346.763636 18.618182 20.945455 13.963636 13.963636 6.981818 9.309091 0 4.654545 6.981818 2.327273 13.963636 0 20.945455 0H1024v1003.054545c0 6.981818-2.327273 13.963636-4.654545 20.945455zM702.836364 463.127273c65.163636-65.163636 130.327273-130.327273 195.490909-193.163637-6.981818-9.309091-16.290909-18.618182-27.927273-34.909091-55.854545 58.181818-109.381818 114.036364-165.236364 169.89091-32.581818-32.581818-62.836364-62.836364-90.763636-93.09091l-30.254545 30.254546c39.563636 39.563636 79.127273 81.454545 118.690909 121.018182z"
                        p-id="4265"
                        fill="var(--w-primary-color)"
                      ></path>
                    </svg>
                    <img
                      :src="
                        item.fileid ? '/proxy-repository/repository/file/mongo/download?fileID=' + item.fileid : '/static/images' + item.url
                      "
                      style="width: 100%; height: 100%; object-fit: scale-down"
                    />
                    <div class="img-title">
                      {{ item.filename || item.url }}
                    </div>
                  </a-card-grid>
                  <template v-else>
                    <a-card-grid
                      v-for="(icon, c) in item.glyphs"
                      v-show="
                        searchWord[img.value] == undefined ||
                        icon.icon_class.toUpperCase().indexOf(searchWord[img.value].toUpperCase()) > -1 ||
                        icon.name.toUpperCase().indexOf(searchWord[img.value].toUpperCase()) > -1
                      "
                      :key="icon.icon_id + '-img-grid-' + i"
                      class="transparent-img-bg image-card-grid"
                      @click="onSelectImage(icon.icon_class)"
                    >
                      <svg
                        v-if="url == icon.icon_class"
                        style="position: absolute; top: 0px; right: 0px"
                        t="1721813036132"
                        class="icon"
                        viewBox="0 0 1024 1024"
                        version="1.1"
                        xmlns="http://www.w3.org/2000/svg"
                        p-id="4264"
                        width="32"
                        height="32"
                      >
                        <path
                          d="M1019.345455 1024c-4.654545-6.981818-9.309091-16.290909-16.29091-20.945455C674.909091 674.909091 346.763636 346.763636 18.618182 20.945455 13.963636 13.963636 6.981818 9.309091 0 4.654545 6.981818 2.327273 13.963636 0 20.945455 0H1024v1003.054545c0 6.981818-2.327273 13.963636-4.654545 20.945455zM702.836364 463.127273c65.163636-65.163636 130.327273-130.327273 195.490909-193.163637-6.981818-9.309091-16.290909-18.618182-27.927273-34.909091-55.854545 58.181818-109.381818 114.036364-165.236364 169.89091-32.581818-32.581818-62.836364-62.836364-90.763636-93.09091l-30.254545 30.254546c39.563636 39.563636 79.127273 81.454545 118.690909 121.018182z"
                          p-id="4265"
                          fill="var(--w-primary-color)"
                        ></path>
                      </svg>

                      <i :class="icon.icon_class" :style="{ fontSize: '50px' }"></i>
                      <div class="img-title">
                        {{ icon.name }}
                      </div>
                    </a-card-grid>
                  </template>
                </template>
              </a-card>
            </Scroll>
          </a-tab-pane>
        </template>
        <a-tab-pane v-if="allowSelectType.includes('writeImage')" key="writeImage" tab="自定义">
          <a-row type="flex">
            <a-col flex="auto">
              <a-textarea
                placeholder="请输入图片地址或者图片BASE64编码"
                :autosize="{ minRows: 15, maxRows: 15 }"
                v-model.trim="inputUrl"
                @change="handleInputUrlChange"
                allow-clear
              ></a-textarea>
            </a-col>
            <a-col flex="300px" style="padding: 12px">
              <div
                :style="{ width: '100%', height: '250px', 'object-fit': 'contain', 'background-image': this.inputUrl }"
                v-if="inputUrl && inputUrl.indexOf('-gradient(') > -1"
              ></div>
              <img
                :src="inputUrl"
                v-else-if="inputUrl != undefined && inputUrl != ''"
                style="width: 100%; height: 250px; object-fit: contain"
              />
              <div v-else style="padding: 12px; display: flex; align-items: center; justify-content: center; color: #b7b7b7">图片预览</div>
            </a-col>
          </a-row>
        </a-tab-pane>
        <template slot="tabBarExtraContent">
          <a-input-search v-model="searchWord[activeTabKey]" allow-clear v-show="activeTabKey != 'writeImage'" />
        </template>
      </a-tabs>
    </a-modal>
    <a-upload
      name="thumbnail"
      list-type="picture-card"
      :file-list="[]"
      :accept="accept"
      :show-upload-list="false"
      :before-upload="e => beforeUpload(e, 'thumbnail')"
      :customRequest="e => customRequest(e, 'thumbnail')"
      style="display: none"
    >
      <div ref="uploadTrig">
        <div class="ant-upload-text">点击上传</div>
      </div>
    </a-upload>
  </span>
</template>
<style lang="less">
.box-container,
.image-library-tabs {
  .transparent-img-bg {
    position: relative;
    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABGdBTUEAALGPC/xhBQAAAFpJREFUWAntljEKADAIA23p6v//qQ+wfUEcCu1yriEgp0FHRJSJcnehmmWm1Dv/lO4HIg1AAAKjTqm03ea88zMCCEDgO4HV5bS757f+7wRoAAIQ4B9gByAAgQ3pfiDmXmAeEwAAAABJRU5ErkJggg==')
      0% 0% / 28px;
    border: 1px solid var(--w-input-border-color);
    border-radius: 4px;
  }
  .image-card-grid {
    width: 25%;
    height: 130px;
    text-align: center;
    position: relative;
    .img-title {
      color: #ababab;
      font-size: 11px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-top: 4px;
    }
  }
  .image-lib-thumbnail {
    overflow: hidden;

    .delete-image {
      position: absolute;
      top: 0px;
      right: 0px;
      background: red;
      color: #fff;
      line-height: 18px;
      padding: 5px;
      border-top-right-radius: 4px;
      cursor: pointer;
    }

    &.has-image {
    }
  }
  .image-buttons {
    text-align: left;
  }
  .img-upload-tip {
    padding: 12px;
    border-radius: 4px;
    background-color: var(--w-gray-color-2);
    line-height: 20px;
    font-size: var(--w-font-size-sm);
    color: var(--w-text-color-light);
    text-align: left;
  }
}
</style>
<script type="text/babel">
import { customFileUploadRequest } from '@framework/vue/utils/function';
import { some, findIndex } from 'lodash';
export default {
  name: 'ImageLibrary',
  props: {
    width: {
      type: [Number, String],
      default: 200
    },
    height: {
      type: [Number, String],
      default: 160
    },
    value: String,
    folder: {
      type: String,
      default: 'pictureL-pict-pict-pict-pictureLibpi'
    },
    // 图片为空时，是否显示透明区域
    emptyVisible: {
      typeof: Boolean,
      default: true
    },
    tipVisible: Boolean,
    imgTip: String,
    boxClass: String,
    boxStyle: Object,
    allowClear: {
      type: Boolean,
      default: true
    },
    accept: String, //接受上传的文件类型
    acceptType: {
      type: Array,
      default: function () {
        return ['jpeg', 'png', 'gif', 'svg'];
      }
    },
    allowSelectType: {
      type: Array,
      default: ['projectImages', 'mongoImages', 'writeImage']
    },
    acceptTip: String,
    limitSize: Number | String, // 1024 or 1M,只传数字时默认M
    iconStyle: {
      type: Object
    },
    includeFileSuffix: {
      type: Array
    }
  },
  components: {},
  computed: {
    tabOptions() {
      let t = [];
      if (this.allowSelectType.includes('projectImages')) {
        t.push({ label: '工程图片', value: 'projectImages' });
      }
      if (this.allowSelectType.includes('mongoImages')) {
        t.push({ label: '图片库', value: 'mongoImages' });
      }
      if (this.allowSelectType.includes('icon')) {
        t.push({ label: '工程图标', value: 'icon' });
      }

      return t;
    },
    vContainerStyle() {
      let style = { position: 'relative' };
      style.width = typeof this.width == 'number' ? `${this.width}px` : this.width;
      style.height = typeof this.height == 'number' ? `${this.height}px` : this.height;
      return style;
    },
    iconSrc() {
      if (this.value != undefined && (this.value.startsWith('/') || this.value.startsWith('url('))) {
        return undefined;
      }
      return this.value;
    },
    imgSrc() {
      if (this.value) {
        return this.value.startsWith('url("') ? this.value.substring(5, this.value.length - 2) : this.value;
      }
      return undefined;
    },
    vLimitSize() {
      if (this.limitSize != null) {
        if (typeof this.limitSize == 'string') {
          let unit = this.limitSize.replace(/[^a-zA-Z]/g, '');
          if (unit) {
            this.vLimitSizeTip = `图片大小应小于 ${this.limitSize}`;
            return parseInt(this.limitSize) * Math.pow(1024, ['K', 'M', 'G'].indexOf(unit.toUpperCase()) - 1);
          } else {
            this.vLimitSizeTip = `图片大小应小于 ${this.limitSize}M`;
            return size;
          }
        } else {
          this.vLimitSizeTip = `图片大小应小于 ${this.limitSize}M`;
          return this.limitSize;
        }
      }
      return undefined;
    },
    vIncludeFileSuffix() {
      return this.includeFileSuffix != undefined ? this.includeFileSuffix.map(ext => ext.toLowerCase()) : [];
    }
  },
  data() {
    let isInputUrl = false,
      inputUrl = undefined,
      originalFileid = undefined,
      activeTabKey = undefined;
    if (this.value != undefined) {
      if (this.value.startsWith('url("')) {
        isInputUrl = true;
        inputUrl = this.value.substring(5, this.value.length - 2);
        activeTabKey = 'writeImage';
      }
      if (this.value.startsWith('/proxy-repository/repository/file/mongo/download')) {
        // 兼容旧的图片上传不是通过该组件，需要把图片也加载到图片库内展示
        originalFileid = this.value.replace('/proxy-repository/repository/file/mongo/download?fileID=', '');
        activeTabKey = 'mongoImages';
      }
      if (!this.value.indexOf('-gradient(') > -1) {
        isInputUrl = true;
        activeTabKey = 'writeImage';
        inputUrl = this.value;
      }
    }

    return {
      activeTabKey,
      originalFileid,
      uploading: false,
      modalVisible: false,
      url: this.value,
      inputUrl,
      isInputUrl,
      mongoImageLoading: true,
      searchWord: {
        projectImages: undefined,
        mongoImages: undefined,
        icon: undefined
      },
      imageLib: {
        projectImages: [],
        mongoImages: [],
        icon: []
      },
      vLimitSizeTip: ''
    };
  },
  beforeCreate() {},
  created() {
    if (this.activeTabKey == undefined) {
      this.selectFirstNotEmptyTab();
    }
  },
  beforeMount() {
    this.getProjectImages().then(() => {
      this.selectFirstNotEmptyTab();
    });
    this.getMongoImages().then(() => {
      this.selectFirstNotEmptyTab();
    });
  },
  mounted() {
    if (this.allowSelectType.includes('icon')) {
      // 以下是资源包内的字体图标，不加载项目工程或者其他业务模块的字体图标（主要是提供跨端使用）
      this.fetchIconfontMetadata('/static/css/pt/iconfont/iconfont.json'); // 平台默认出场的图标库
      this.fetchIconfontMetadata('/static/css/pt/ant-iconfont/iconfont.json'); // 平台默认出场的图标库
      if (EXIST_APP_ICONFONT) {
        this.fetchIconfontMetadata('/static/css/app-iconfont/iconfont.json'); // 业务应用图标库
      }
    }
  },
  methods: {
    selectFirstNotEmptyTab() {
      for (let i = 0; i < this.tabOptions.length; i++) {
        let activeTabKey = this.tabOptions[i].value;
        if (this.imageLib[activeTabKey].length > 0) {
          this.activeTabKey = activeTabKey;
          break;
        }
      }
    },
    modalOpen() {
      this.modalVisible = true;
    },
    handleUpload() {
      this.onClickUpload();
    },
    onClickUpload() {
      this.$refs.uploadTrig.click();
    },
    customRequest(options) {
      let opt = {
        file: options.file,
        folder: {
          folderID: this.folder
        }
      };
      let _this = this;
      customFileUploadRequest(opt).then(dbFile => {
        _this.url = `/proxy-repository/repository/file/mongo/download?fileID=${dbFile.fileID}`;
        if (this.modalVisible) {
          _this.imageLib.mongoImages.splice(0, 0, {
            fileid: dbFile.fileID,
            filename: dbFile.filename
          });
        } else {
          this.$emit('input', _this.url);
        }
      });
    },
    beforeUpload(file) {
      let isJpgOrPng = false;
      if (this.acceptType && this.acceptType.length) {
        for (let t of this.acceptType) {
          if (file.type.indexOf(t) != -1) {
            isJpgOrPng = true;
            break;
          }
        }
      } else {
        isJpgOrPng = true;
      }
      if (!isJpgOrPng) {
        this.$message.error(this.acceptTip || `只允许上传 ${this.acceptType.join('、')} 图片格式`);
      }
      let limit = true;
      if (this.vLimitSize != undefined) {
        limit = file.size / 1024 / 1024 < this.vLimitSize;
        if (!limit) {
          this.$message.error(`${this.vLimitSizeTip}`);
        }
      }
      return isJpgOrPng && limit;
    },
    handleClear() {
      this.url = undefined;
      this.$emit('input', this.url);
    },
    onModalOkSelectDone() {
      this.$emit('input', this.url);
      this.modalVisible = false;
    },
    handleInputUrlChange() {
      this.url = undefined;
      if (this.inputUrl) {
        if (this.inputUrl.indexOf('-gradient(') > -1) {
          this.url = this.inputUrl;
        } else {
          this.url = `url("${this.inputUrl}")`;
        }
      }
    },
    onSelectImage(url) {
      this.isInputUrl = false;
      if (url == this.url) {
        this.url = undefined;
      } else {
        this.url = url;
      }
    },
    getProjectImages() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/web/resource/getProjectImages`, { params: {} })
          .then(({ data }) => {
            console.log('返回服务端图片资源数据', data);
            this.imageLib.projectImages = data.project;
            resolve();
          })
          .catch(error => {});
      });
    },
    isIncludeFileSuffix(filename) {
      // 获取最后一个点之后的部分作为后缀
      const lastDotIndex = filename.lastIndexOf('.');
      if (lastDotIndex === -1) return false; // 无后缀

      const extension = filename.slice(lastDotIndex).toLowerCase();

      return this.vIncludeFileSuffix.includes(extension);
    },

    getMongoImages() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/repository/file/mongo/getFilesFromFolder`, {
            params: { folder: this.folder, purpose: '', distinct: 'true' }
          })
          .then(({ data }) => {
            let fileids = [];
            if (data) {
              data.forEach(d => {
                if (this.vIncludeFileSuffix.length == 0 || this.isIncludeFileSuffix(d.filename)) {
                  this.imageLib.mongoImages.push(d);
                  fileids.push(d.fileid);
                }
              });
            }
            if (this.originalFileid != undefined && !fileids.includes(this.originalFileid)) {
              this.imageLib.mongoImages.splice(0, 0, {
                fileid: this.originalFileid
              });
            }
            this.mongoImageLoading = false;
            resolve();
          })
          .catch(error => {});
      });
    },

    fetchIconfontMetadata(url) {
      let _this = this;
      // 获取业务线图标库：业务线图标库要定义不一样的图标设置，例如： iconfont": "app-iconfont", "css_prefix_text": "app-icon-",
      return new Promise((resolve, reject) => {
        $axios.get(url).then(res => {
          let glyphs = res.data.glyphs || [],
            font_family = res.data.font_family,
            css_prefix_text = res.data.css_prefix_text;
          for (let i = 0, len = glyphs.length; i < len; i++) {
            glyphs[i].icon_class = `${font_family} ${css_prefix_text}${glyphs[i].font_class}`;
            // 在已有的图标里找到一样的，就隐藏
            let isDel = some(_this.imageLib.icon, item => {
              let hasIndex = findIndex(item.glyphs, { icon_class: glyphs[i].icon_class });
              return hasIndex > -1;
            });
            if (isDel) {
              glyphs[i].hide = true;
            }
          }
          _this.imageLib.icon.push(res.data);
          resolve();
        });
      });
    }
  }
};
</script>
