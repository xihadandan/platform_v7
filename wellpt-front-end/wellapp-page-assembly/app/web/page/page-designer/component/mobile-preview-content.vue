<template>
  <div>
    <mPhoneUi :scrollOptions="{ suppressScrollY: true }" ref="phoneUi" @onResize="onPUiResize" :width="phoneUiWidth">
      <template slot="status-bar-right">
        <a-drawer
          :wrap-style="{ position: 'absolute', left: '21px', width: 'calc(100% - 42px)' }"
          placement="top"
          :closable="false"
          :z-index="99999"
          :getContainer="getPreviewUrlSetDrawerContainer"
          :visible="setPreviewServerUrlVisible"
          height="auto"
          @close="setPreviewServerUrlVisible = false"
        >
          <div>
            <a-alert message="请设置手机预览服务地址" banner :show-icon="false" type="info" style="margin-bottom: 8px" />

            <a-input-group compact style="width: 100%">
              <a-select v-model="uniServerUrlProtocol" style="width: 85px" size="small">
                <a-select-option value="http://">http://</a-select-option>
                <a-select-option value="https://">https://</a-select-option>
              </a-select>
              <a-input size="small" style="width: calc(100% - 85px)" v-model.trim="uniServerUrl" />
            </a-input-group>
          </div>
        </a-drawer>
        <div class="preview-server-icon" @click="setPreviewServerUrlVisible = !setPreviewServerUrlVisible">
          <label>预览服务</label>
        </div>
      </template>
      <template slot="default">
        <iframe
          :style="{
            borderRadius: '7px',
            height: '100%',
            pointerEvents: editable ? 'unset' : 'none'
          }"
          id="iframe_mobile_preview"
          name="iframe_mobile_preview"
          scrolling="no"
          frameborder="0"
          @load="onIframeLoad"
        ></iframe>
      </template>
    </mPhoneUi>
    <div v-show="qrcodeVisible" style="position: fixed; bottom: 35px; right: 40px; outline: 1px solid #ccc; border-radius: 4px">
      <div style="text-align: center; font-size: 12px; color: #999; font-weight: bold; padding-top: 8px">扫一扫, 手机端预览</div>
      <canvas id="qrcode"></canvas>
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import mPhoneUi from './m-phone-ui.vue';
import { getCookie, generateId } from '@framework/vue/utils/util';
import md5 from '@framework/vue/utils/md5';
import { debounce } from 'lodash';
import QRCode from 'qrcode';
export default {
  name: 'MobilePreviewContent',
  props: {
    pageUrl: [String, Function],
    previewJson: [String, Function],
    editable: { type: Boolean, default: true },
    phoneUiWidth: {
      type: [String, Number],
      default: 365
    },
    domain: {
      type: String,
      default: 'http://localhost:7001'
    },
    qrcode: {
      type: Boolean,
      default: false
    },
    h5Url: {
      type: String,
      default: 'http://localhost:8081'
    }
  },

  data() {
    let uniServerUrlProtocol = undefined,
      uniServerUrl = undefined;
    if (this.h5Url) {
      let urlSplits = this.h5Url.split('://');
      uniServerUrlProtocol = urlSplits[0] + '://';
      uniServerUrl = urlSplits[1];
    }
    return {
      uniServerUrlProtocol,
      uniServerUrl,
      loading: true,
      qrcodeVisible: false,
      jsonid: undefined,
      setPreviewServerUrlVisible: false
    };
  },
  beforeCreate() {},
  components: { mPhoneUi },
  computed: {
    fullUniServerUrl() {
      return this.uniServerUrlProtocol + this.uniServerUrl;
    }
  },
  created() {
    if (EASY_ENV_IS_BROWSER) {
      this.jsonid = md5(location.pathname);
    }
  },
  methods: {
    getPreviewUrlSetDrawerContainer() {
      return this.$refs.phoneUi.$el.querySelector('.panel-scroll.ps');
    },
    getContainer() {
      return typeof this.mountContainer == 'function' ? this.mountContainer() : 'body';
    },

    iframeReadyMessage() {
      let _this = this;
      window.addEventListener(
        'message',
        function (event) {
          if (event.origin !== _this.fullUniServerUrl) return;
          _this.$emit('message', event.data);
          if (event.data == 'uni server preview ready') {
            _this.loading = false;
            _this.postWidgetDefJsonMessageToUniPreview();
          }
        },
        false
      );
    },
    getPreviewJson() {
      let json = undefined;
      if (typeof this.previewJson == 'function') {
        json = this.previewJson();
      } else if (typeof this.previewJson == 'string') {
        json = this.previewJson;
      }
      if (json != undefined && typeof json !== 'string' && typeof json.then == 'function') {
        // 返回的是promise函数
        return json;
      }
      if (json != undefined && typeof json !== 'string') {
        json = JSON.stringify(json);
      }
      return json;
    },
    postWidgetDefJsonMessageToUniPreview() {
      var iframes = document.getElementsByName('iframe_mobile_preview');
      var iframe = iframes[0];
      let json = this.getPreviewJson();
      if (typeof json == 'string') {
        iframe.contentWindow.postMessage(this.getPreviewJson(), this.fullUniServerUrl);
      } else {
        json.then(jsonStr => {
          iframe.contentWindow.postMessage(jsonStr, this.fullUniServerUrl);
        });
      }
    },
    onIframeLoad: debounce(function (force) {
      let _this = this;
      if (EASY_ENV_IS_BROWSER) {
        localStorage.setItem('uniServerUrlProtocol', this.uniServerUrlProtocol);
        localStorage.setItem('uniServerUrl', this.uniServerUrl);
      }

      var iframes = document.getElementsByName('iframe_mobile_preview');
      var iframe = iframes[0];

      if (!iframe.src || force === true) {
        var url = this.getFrameUrl(),
          qrcodeUrl = url;

        if (
          this.qrcode
          // && this.domain.indexOf('localhost') == -1 && this.domain.indexOf('127.0.0.1') == -1
        ) {
          var canvas = document.getElementById('qrcode');
          console.log('二维码地址', url);
          $axios
            .post(`/uni-app-design-json/set`, {
              id: this.jsonid,
              json: this.getPreviewJson()
            })
            .then(() => {
              QRCode.toCanvas(canvas, qrcodeUrl, { width: 250 }, function (error) {
                _this.qrcodeVisible = !error;
              });
            });
        }

        url += '&origin=' + window.location.origin;
        iframe.src = url;
      }
    }, 300),
    getFrameUrl() {
      var accessToken = getCookie('jwt');
      let pageUrl = this.pageUrl;
      if (typeof pageUrl == 'function') {
        pageUrl = pageUrl();
      }
      pageUrl += `${pageUrl.indexOf('?') != -1 ? '&' : '?'}` + 'accessToken=' + accessToken;
      let url = this.fullUniServerUrl + '/#' + pageUrl;
      if (this.uriParams) {
        for (var key in this.uriParams) {
          if (this.uriParams[key] != null) {
            url += '&' + key + '=' + this.uriParams[key];
          }
        }
      }
      url += '&jsonid=' + this.jsonid;
      url += '&domain=' + encodeURIComponent(this.domain);
      return url;
    },
    updateIframeWidth(e) {
      if (this.$refs.phoneUi) {
        let rect = this.$refs.phoneUi.$el.getBoundingClientRect();
        let width = rect.width;
        let iframe = document.querySelector('#iframe_mobile_preview');
        iframe.style.width = width - 42 + 'px';
      }
    },
    onPUiResize(e) {
      this.updateIframeWidth(e);
    }
  },

  mounted() {
    this.iframeReadyMessage();
  },

  watch: {
    fullUniServerUrl: {
      handler(v) {
        this.onIframeLoad(true);
      }
    }
  }
};
</script>
