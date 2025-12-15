<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model-item label="名称">
          <a-input v-model="widget.title" />
        </a-form-model-item>
        <title-button :configuration="configuration" />
        <a-form-model-item label="类型">
          <a-select :options="typeOptions" v-model="configuration.type" />
        </a-form-model-item>
        <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="setting">
          <a-collapse-panel key="setting" header="二维码设置" v-show="configuration.type === typeOptions[0]['value']">
            <a-form-model-item label="扫码内容">
              <a-select :options="qrContentTypeOptions" v-model="configuration.qrContentType" />
            </a-form-model-item>
            <template v-if="configuration.qrContentType === qrContentTypeOptions[0]['value']">
              <a-form-model-item class="display-b" :label-col="{}" :wrapper-col="{}">
                <template slot="label"></template>
                <a-textarea
                  v-model="configuration.qrContentForm"
                  :rows="3"
                  :style="{
                    height: 'auto'
                  }"
                  :allowClear="true"
                />
                <div>
                  <title-define-template :formData="configuration" prop="qrContentForm">
                    <a-button type="link" size="small" slot="trigger" title="设置">
                      <Icon type="pticon iconfont icon-ptkj-shezhi" />
                    </a-button>
                  </title-define-template>
                  可以通过前面的图标插入系统参数
                </div>
              </a-form-model-item>
            </template>
            <template v-else>
              <a-form-model-item label="链接地址">
                <qr-content-link v-model="configuration.qrContentLink" />
              </a-form-model-item>
            </template>
            <a-form-model-item label="尺寸">
              <w-input-number v-model="configuration.qrSize" :min="80" addonAfter="px" />
            </a-form-model-item>
            <a-form-model-item label="上传logo">
              <image-library v-model="configuration.logo" />
            </a-form-model-item>
            <a-form-model-item label="logo尺寸">
              <w-input-number v-model="configuration.logoSize" :min="0" addonAfter="px" />
            </a-form-model-item>
            <a-form-model-item label="有效期">
              <a-select :options="scanTimeLimitTypeOptions" v-model="configuration.scanTimeLimitType" @change="changeScanTimeLimitType" />
            </a-form-model-item>
            <template v-if="configuration.scanTimeLimitType !== scanTimeLimitTypeOptions[0]['value']">
              <a-form-model-item label="">
                <a-range-picker
                  v-model="expirationDateRange"
                  :format="defaultPattern"
                  :showTime="{ format: 'HH:mm:ss' }"
                  :disabled="configuration.scanTimeLimitType !== scanTimeLimitTypeOptions[7]['value']"
                  @change="changeExpirationDateRange"
                />
              </a-form-model-item>
              <a-form-model-item label="扫码显示有效期">
                <a-switch v-model="configuration.showValidityDate" />
              </a-form-model-item>
            </template>
          </a-collapse-panel>

          <a-collapse-panel key="setting" header="条形码设置" v-show="configuration.type === typeOptions[1]['value']">
            <a-form-model-item label="条码类型">
              <a-select :options="barContentTypeOptions" v-model="configuration.barContentType" />
            </a-form-model-item>
            <a-form-model-item class="display-b" :label-col="{}" :wrapper-col="{}">
              <template slot="label">
                扫码内容
                <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                  <div slot="title">不支持中文</div>
                  <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                </a-tooltip>
              </template>
              <a-textarea
                v-model="configuration.barContentForm"
                :rows="3"
                :style="{
                  height: 'auto'
                }"
                :allowClear="true"
              />
              <div>
                <title-define-template :formData="configuration" prop="barContentForm">
                  <a-button type="link" size="small" slot="trigger" title="设置">
                    <Icon type="pticon iconfont icon-ptkj-shezhi" />
                  </a-button>
                </title-define-template>
                可以通过前面的图标插入系统参数
              </div>
            </a-form-model-item>
            <a-form-model-item label="宽度">
              <a-input v-model="configuration.barCodeWidth" :disabled="true" />
            </a-form-model-item>
            <a-form-model-item label="高度">
              <w-input-number v-model="configuration.barCodeHeight" :min="80" addonAfter="px" />
            </a-form-model-item>
            <a-form-model-item label="显示条形码文本">
              <a-switch v-model="configuration.barShowLabel" />
            </a-form-model-item>
          </a-collapse-panel>
        </a-collapse>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import moment from 'moment';
import { typeOptions, qrContentTypeOptions, scanTimeLimitTypeOptions, barContentTypeOptions } from './constant';
import TitleButton from './title-button.vue';
import WInputNumber from '../../widget-carousel/components/w-input-number/index';
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';
import TitleDefineTemplate from '@workflow/app/web/page/workflow-designer/component/commons/title-define-template.vue';
import QrContentLink from './qr-content-link.vue';

export default {
  name: 'WidgetQrBarCodeConfiguration',
  inject: ['appId'],
  props: {
    widget: Object,
    designer: Object
  },
  components: {
    TitleButton,
    QrContentLink,
    WInputNumber,
    ImageLibrary,
    TitleDefineTemplate
  },
  provide() {
    return {
      designer: this.designer,
      widget: this.widget
    };
  },
  data() {
    const { configuration } = this.widget;

    const defaultPattern = 'YYYY-MM-DD HH:mm:ss';
    let expirationDateRange = [null, null];
    if (configuration.expirationDateRange) {
      if (configuration.expirationDateRange[0]) {
        expirationDateRange[0] = moment(configuration.expirationDateRange[0], defaultPattern);
      }
      if (configuration.expirationDateRange[1]) {
        expirationDateRange[1] = moment(configuration.expirationDateRange[1], defaultPattern);
      }
    }

    let scanTimeLimitTypeMap = {};
    scanTimeLimitTypeOptions.map(item => {
      scanTimeLimitTypeMap[item.value] = item;
    });
    return {
      configuration,
      defaultPattern,
      typeOptions,
      qrContentTypeOptions,
      scanTimeLimitTypeOptions,
      scanTimeLimitTypeMap,
      barContentTypeOptions,
      expirationDateRange
    };
  },
  created() {
    if (this.configuration.qrContentLink === undefined) {
      this.$set(this.widget.configuration, 'qrContentLink', {
        type: 'custom',
        value: undefined,
        workflowType: 'start_new_work',
        formMode: 'new_form',
        formOpenType: 'current',
        formSpecify: undefined
      });
    }
    if (this.configuration.expirationDateRange === undefined) {
      this.$set(this.widget.configuration, 'expirationDateRange', []);
    }
  },
  methods: {
    // 更改有效期类型
    changeScanTimeLimitType(value, option) {
      if (value !== this.scanTimeLimitTypeOptions[0]['value']) {
        if (value !== this.scanTimeLimitTypeOptions[7]['value']) {
          const typeItem = this.scanTimeLimitTypeMap[value];
          let limitBeginTime = moment();
          let limitEndTime = moment().add(typeItem.addNum, typeItem.addUnit);
          this.widget.configuration.expirationDateRange = [
            limitBeginTime.format(this.defaultPattern),
            limitEndTime.format(this.defaultPattern)
          ];
          this.expirationDateRange = [limitBeginTime, limitEndTime];
        }
      }
    },
    // 更改有效期
    changeExpirationDateRange(dates, dateStrings) {
      this.widget.configuration.expirationDateRange = dateStrings;
    }
  }
};
</script>
