<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model ref="form" :model="widget.configuration" :rules="rules" labelAlign="left"
          :wrapper-col="{ style: { textAlign: 'right' } }">
          <a-form-model-item label="文本展示" prop="text">
            <a-input v-model="widget.configuration.text">
              <template slot="addonAfter">
                <WI18nInput :widget="widget" :designer="designer" code="text" v-model="widget.configuration.text" />
              </template>
            </a-input>
          </a-form-model-item>

          <a-form-model-item label="超链接类型" class="item-lh">
            <a-radio-group size="small" v-model="widget.configuration.linkType" button-style="solid">
              <a-radio-button value="url">地址</a-radio-button>
              <a-radio-button value="email">电子邮件</a-radio-button>
            </a-radio-group>
          </a-form-model-item>

          <a-form-model-item label="地址" v-show="widget.configuration.linkType === 'url'" class="display-b">
            <a-input v-model="widget.configuration.url">
              <a-select slot="addonBefore" v-model="widget.configuration.protocol" style="width: 90px">
                <a-select-option value="http://">http://</a-select-option>
                <a-select-option value="https://">https://</a-select-option>
                <a-select-option value="ftp://">ftp://</a-select-option>
                <a-select-option value="/">/</a-select-option>
              </a-select>
            </a-input>
          </a-form-model-item>
          <a-form-model-item label="目标窗口" v-show="widget.configuration.linkType === 'url'">
            <a-select :style="{ width: '100%' }" :options="targetPositionOptions"
              v-model="widget.configuration.targetPosition" />
          </a-form-model-item>
          <a-form-model-item label="选择弹出" v-show="widget.configuration.targetPosition === '_dialog'">
            <a-select :style="{ width: '100%' }" :options="vWidgetModalOptions"
              v-model="widget.configuration.widgetModalId" />
          </a-form-model-item>
          <a-form-model-item label="地址" v-show="widget.configuration.linkType === 'email'">
            <a-input v-model="widget.configuration.mail.to" />
          </a-form-model-item>
          <div v-show="widget.configuration.linkType === 'email'">
            <a-form-model-item label="主题">
              <a-input v-model="widget.configuration.mail.subject" allowClear />
            </a-form-model-item>
            <a-form-model-item label="内容">
              <a-textarea v-model="widget.configuration.mail.body" :maxLength="200" allowClear />
            </a-form-model-item>
          </div>
          <DefaultVisibleConfiguration :designer="designer" :configuration="widget.configuration" :widget="widget" />
        </a-form-model>
      </a-tab-pane>

      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'WidgetHyperlinkConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      targetPositionOptions: [
        { label: '新窗口', value: '_blank' },
        { label: '本窗口', value: '_self' }
        // { label: '弹出窗口', value: '_dialog' }
      ],
      rules: {
        text: {
          required: true,
          message: <a-icon type="close-circle" theme="filled" title="文本展示必填" />,
          trigger: ['blur', 'change'],
          whitespace: true
        },
        linkType: {
          required: true,
          message: <a-icon type="close-circle" theme="filled" title="超链接类型必填" />,
          trigger: ['blur', 'change']
        }
      }
    };
  },

  components: {},
  computed: {
    vWidgetModalOptions() {
      let options = [];

      if (this.designer.WidgetModals) {
        for (let i = 0, len = this.designer.WidgetModals.length; i < len; i++) {
          let w = this.designer.WidgetModals[i];
          options.push({ label: w.title, value: w.id });
        }
      }
      return options;
    }
  },
  created() { },
  methods: {},
  beforeMount() { },
  mounted() { },
  configuration() {
    return {
      text: '',
      linkType: 'url',
      protocol: 'http://',
      widgetModalId: undefined,
      mail: {
        to: '',
        subject: '',
        body: ''
      },
      url: '',
      targetPosition: '_blank'
      // style: {
      //   width: 'auto',
      //   block: false
      // }
    };
  }
};
</script>
