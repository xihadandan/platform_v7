<template>
  <div class="widget-qr-bar-code">
    <!-- 二维码 -->
    <div
      v-if="configuration.type === 'qrCode'"
      class="widget-qr-code"
      :style="{
        width: configuration.qrSize + 'px'
      }"
    >
      <div class="_header" v-if="configuration.enabledTitle">
        <Icon :type="configuration.titleIcon" v-if="configuration.titleIcon" :size="20" />
        <div class="_title">
          {{ widget.title }}
        </div>
      </div>
      <div class="_content" :style="`height:${configuration.qrSize}px`">
        <template v-if="qrContentType === 'custom'">
          <template v-if="hasQrContentCustom">
            <div class="_content-tips" v-if="needComputeTitle">
              <template v-if="designMode">地址中存在待解析的参数变量，需运行时显示！</template>
              <template v-else>
                <template v-if="qrContentText">
                  <!-- <a-spin :spinning="!qrcodeVisible" /> -->
                  <canvas id="qrcode" v-show="qrcodeVisible" :width="configuration.qrSize" :height="configuration.qrSize"></canvas>
                </template>
                <div v-else>请设置二维码内容</div>
              </template>
            </div>
            <template v-else>
              <template v-if="qrContentText">
                <canvas id="qrcode" v-show="qrcodeVisible" :width="configuration.qrSize" :height="configuration.qrSize"></canvas>
              </template>
              <div class="_content-tips" v-else>请设置二维码内容</div>
            </template>
          </template>
          <div class="_content-tips" v-else>请设置二维码内容</div>
        </template>
        <template v-else-if="qrContentType === 'link'">
          <template v-if="qrContentText">
            <canvas id="qrcode" v-show="qrcodeVisible" :width="configuration.qrSize" :height="configuration.qrSize"></canvas>
          </template>
          <div class="_content-tips" v-else>
            <template v-if="qrContentLink.type === 'form' && qrContentLink.formMode === 'open_form' && !qrContentText">
              地址中存在待解析的参数变量，需运行时显示！
            </template>
            <template v-else>请设置二维码内容</template>
          </div>
        </template>
      </div>
    </div>
    <!-- 条形码 -->
    <div
      v-if="configuration.type === 'barCode'"
      class="widget-bar-code"
      :style="{
        width: configuration.barCodeWidth
      }"
    >
      <div class="_header" v-if="configuration.enabledTitle">
        <Icon :type="configuration.titleIcon" v-if="configuration.titleIcon" />
        <div class="_title">
          {{ widget.title }}
        </div>
      </div>
      <div class="_content" :style="`height:${configuration.barCodeHeight}px`">
        <template v-if="configuration.barContentForm">
          <div class="_content-tips" v-if="needComputeBarText">
            <template v-if="designMode">地址中存在待解析的参数变量，需运行时显示！</template>
            <template v-else>
              <template v-if="barContentText">
                <Barcode
                  :type="configuration.barContentType"
                  :text="barContentText"
                  :heightPx="configuration.barCodeHeight"
                  :includetext="configuration.barShowLabel"
                />
              </template>
              <div v-else>请设置条形码内容</div>
            </template>
          </div>
          <template v-else>
            <template v-if="barContentText">
              <Barcode
                :type="configuration.barContentType"
                :text="barContentText"
                :heightPx="configuration.barCodeHeight"
                :includetext="configuration.barShowLabel"
              />
            </template>
            <div class="_content-tips" v-else>请设置条形码内容</div>
          </template>
        </template>
        <div class="_content-tips" v-else>请设置条形码内容</div>
      </div>
    </div>
  </div>
</template>

<script>
import { debounce } from 'lodash';
import QRCode from 'qrcode';
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import Barcode from './components/bar-code.vue';

export default {
  name: 'WidgetQrBarCode',
  mixins: [widgetMixin],
  data() {
    const { qrContentType, qrContentLink } = this.widget.configuration;
    return {
      h5Server: '',
      qrContentType,
      qrContentLink,
      qrcodeVisible: false,
      qrContentText: '',
      barContentText: ''
    };
  },
  components: {
    Barcode
  },
  computed: {
    isQrCode() {
      return this.configuration.type === 'qrCode';
    },
    hasQrContentCustom() {
      return this.configuration.qrContentForm !== '';
    },
    needComputeTitle() {
      if (this.configuration.qrContentForm.indexOf('${') > -1) {
        return true;
      }
      return false;
    },
    needComputeBarText() {
      if (this.configuration.barContentForm.indexOf('${') > -1) {
        return true;
      }
      return false;
    }
  },
  created() {
    // this.designer.h5Server
    if (window.__INITIAL_STATE__) {
      this.h5Server = window.__INITIAL_STATE__.h5Server;
    }
    if (this.configuration.type === 'qrCode') {
      if (this.qrContentType === 'custom') {
        this.qrContentText = this.widget.configuration.qrContentForm;
      } else if (this.qrContentType === 'link') {
        this.getQrContentByLink();
      }
    }
  },
  mounted() {
    if (this.qrContentType === 'custom') {
      if (this.hasQrContentCustom) {
        if (this.needComputeTitle) {
          if (!this.designMode) {
            // this.getTitle({ formUuid: this.dyform.formUuid });
            this.dyform.handleEvent('formDataChanged', res => {
              this.qrcodeVisible = false;
              this.debounceGetTitle(res);
            });
          }
        } else {
          this.renderQRCode();
        }
      }
    }
    // 条形码
    if (this.configuration.type === 'barCode') {
      if (this.needComputeBarText) {
        if (!this.designMode) {
          this.dyform.handleEvent('formDataChanged', res => {
            this.debounceGetTitle(res);
          });
        }
      } else {
        this.barContentText = this.widget.configuration.barContentForm;
      }
    }
  },
  methods: {
    // 获取二维码通过链接地址
    getQrContentByLink() {
      const { qrContentLink } = this.configuration;
      if (qrContentLink) {
        const mobileUrlMap = {
          page: '/packages/_/pages/app/app?pageId=',
          start_new_work: '/packages/_/pages/workflow/start_new_work', // 选择流程页
          work_new: '/packages/_/pages/workflow/work_view?flowDefId=', // 发起流程
          form: '/packages/_/pages/dyform/preview?formUuid=' // 表单  /packages/_/pages/dms/dms_dyform
        };

        let qrContentText;
        const { type, workflowType, formMode, formOpenType, formSpecify, value } = qrContentLink;
        if (type === 'custom') {
          qrContentText = value;
        } else {
          if (type === 'workflow') {
            if (workflowType === 'start_new_work') {
              qrContentText = mobileUrlMap[workflowType];
            } else if (workflowType === 'work_new') {
              qrContentText = mobileUrlMap[workflowType] + value;
            }
          } else if (type === 'page') {
            qrContentText = mobileUrlMap[type] + value;
          } else if (type === 'form') {
            if (formMode === 'new_form') {
              // 新建
              qrContentText = mobileUrlMap[type] + value + '&hasSaveBtn=true';
            } else if (formMode === 'open_form') {
              // 打开单据
              if (formOpenType === 'current') {
                // 当前数据
                if (this.dyform && this.dyform.dataUuid) {
                  qrContentText = mobileUrlMap[type] + this.dyform.formUuid + '&dataUuid=' + this.dyform.dataUuid;
                }
              } else if (formOpenType === 'specify') {
                // 指定
              }
            }
          }
          if (qrContentText) {
            qrContentText = this.h5Server + '/#' + qrContentText;
          }
        }
        if (qrContentText) {
          this.qrContentText = qrContentText;
          this.renderQRCode();
        }
      }
    },
    // 渲染二维码
    renderQRCode(text = this.qrContentText) {
      if (!text) {
        return;
      }
      this.$nextTick(() => {
        const qrSize = this.configuration.qrSize || 80;
        QRCode.toCanvas(
          this.$el.querySelector('#qrcode'),
          text,
          {
            margin: 0,
            width: qrSize
          },
          (error, canvas) => {
            this.qrcodeVisible = !error;
            if (this.qrcodeVisible && this.configuration.logo) {
              this.drawQrLogo(canvas);
            }
          }
        );
      });
    },
    drawQrLogo(canvas) {
      const image = new Image();
      image.onload = () => {
        image.width = this.configuration.logoSize;
        image.height = image.width;
        const ctx = canvas.getContext('2d');
        this.drawLogoCentered(ctx, image, canvas.width, canvas.height);
      };
      image.src = this.configuration.logo;
    },
    drawLogoCentered(ctx, logo, canvasWidth, canvasHeight) {
      // 计算logo在Canvas中的居中位置
      const logoWidth = logo.width;
      const logoHeight = logo.height;
      const x = (canvasWidth - logoWidth) / 2;
      const y = (canvasHeight - logoHeight) / 2;

      ctx.fillStyle = '#ffffff';
      ctx.fillRect(x, y, logoWidth, logoHeight);

      // 绘制logo到Canvas的指定位置
      ctx.drawImage(logo, x, y, logoWidth, logoHeight);
    },
    getTitle(formData) {
      let titleExpression;
      if (this.configuration.type === 'barCode') {
        titleExpression = this.configuration.barContentForm;
      } else {
        titleExpression = this.configuration.qrContentForm;
      }
      titleExpression = encodeURIComponent(titleExpression);
      $axios
        .post(`/proxy/api/dyform/data/getDyformTitle?titleExpression=${titleExpression}`, formData)
        .then(({ data }) => {
          if (this.configuration.type === 'barCode') {
            this.barContentText = data.data;
          } else {
            this.qrContentText = data.data;
            this.renderQRCode();
          }
        })
        .catch(() => {});
    },
    debounceGetTitle: debounce(function (res) {
      this.getTitle(res.wDyform.collectFormData(false).dyFormData);
    }, 300)
  }
};
</script>

<style lang="less">
.widget-qr-bar-code {
  display: flex;
  flex-direction: column;
}
.widget-qr-code,
.widget-bar-code {
  display: flex;
  flex-direction: column;
  > ._header {
    width: 100%;
    flex: 0 0 auto;
    display: flex;
    align-items: center;

    color: var(--w-gray-color-11);
    padding: 0 20px;
    min-height: 40px;
    line-height: 40px;
    border-bottom: 1px solid var(--w-gray-color-5);

    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    > ._title {
      margin-left: 2px;
    }
  }
  ._content-tips {
    width: 100%;
    height: 100%;
    padding: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: dashed 1px var(--w-border-color-darker);
    border-radius: 4px;
  }
}
.widget-bar-code {
  ._content {
    display: flex;
    justify-content: center;
    align-items: center;
  }
}
</style>
