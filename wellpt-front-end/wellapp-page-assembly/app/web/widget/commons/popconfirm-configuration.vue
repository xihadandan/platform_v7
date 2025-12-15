<template>
  <div v-if="configuration.confirmConfig != undefined">
    <a-form-model-item label="开启确认框提示" class="item-lh">
      <a-switch v-model="configuration.confirmConfig.enable" />
    </a-form-model-item>
    <div v-show="configuration.confirmConfig.enable">
      <a-form-model-item>
        <template slot="label">
          弹出方式
          <slot name="popconfirmTypeHelpSlot"></slot>
        </template>
        <a-radio-group v-model="configuration.confirmConfig.popType" button-style="solid" size="small">
          <a-radio-button value="confirm">居中对话框</a-radio-button>
          <a-radio-button value="popconfirm">附近弹出浮层</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          描述标题
          <slot name="popconfirmHelpSlot"></slot>
        </template>
        <a-input v-model="configuration.confirmConfig.title">
          <template slot="addonAfter">
            <WI18nInput
              :widget="widget"
              :designer="designer"
              :code="i18nCodePrefix + '_popconfirmTitle'"
              v-model="configuration.confirmConfig.title"
              :target="configuration.confirmConfig"
            />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item v-if="configuration.confirmConfig.popType == 'confirm'">
        <template slot="label">
          描述内容
          <slot name="popconfirmHelpSlot"></slot>
        </template>
        <a-input-group compact>
          <a-textarea v-model="configuration.confirmConfig.content" style="width: calc(100% - 40px)"></a-textarea>
          <WI18nInput
            style="width: 32px; padding-left: 13px"
            :widget="widget"
            :designer="designer"
            :code="i18nCodePrefix + '_popconfirmContent'"
            v-model="configuration.confirmConfig.content"
            :target="configuration.confirmConfig"
          />
        </a-input-group>
      </a-form-model-item>
      <a-form-model-item label="确定按钮文字">
        <a-input v-model="configuration.confirmConfig.okText">
          <template slot="addonAfter">
            <WI18nInput
              :widget="widget"
              :designer="designer"
              :code="i18nCodePrefix + '_popconfirmOkText'"
              v-model="configuration.confirmConfig.okText"
              :target="configuration.confirmConfig"
            />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="取消按钮文字">
        <a-input v-model="configuration.confirmConfig.cancelText">
          <template slot="addonAfter">
            <WI18nInput
              :widget="widget"
              :designer="designer"
              :code="i18nCodePrefix + '_popconfirmCancelText'"
              v-model="configuration.confirmConfig.cancelText"
              :target="configuration.confirmConfig"
            />
          </template>
        </a-input>
      </a-form-model-item>
    </div>
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId, copyToClipboard } from '@framework/vue/utils/util';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'PopconfirmConfiguration',
  props: {
    configuration: Object,
    designer: Object,
    popconfirmI18nCodePrefix: [Function, String]
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: { WI18nInput },
  computed: {
    i18nCodePrefix() {
      if (this.popconfirmI18nCodePrefix != undefined) {
        if (typeof this.popconfirmI18nCodePrefix == 'function') {
          return this.popconfirmI18nCodePrefix();
        } else if (typeof this.popconfirmI18nCodePrefix == 'string') {
          return this.popconfirmI18nCodePrefix;
        }
      }
      return this.configuration.id;
    }
  },
  created() {
    if (!this.configuration.hasOwnProperty('confirmConfig')) {
      this.$set(this.configuration, 'confirmConfig', {
        enable: false,
        popType: 'confirm',
        cancelText: '取消',
        okText: '确定',
        title: ''
      });
    }
  },
  methods: {},
  beforeMount() {},
  mounted() {}
};
</script>
