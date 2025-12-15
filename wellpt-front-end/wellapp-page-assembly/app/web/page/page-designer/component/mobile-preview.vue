<template>
  <span>
    <span v-on:[eventName]="onTrigger">
      <slot></slot>
    </span>
    <a-modal
      v-if="displayStyle == 'modal'"
      v-model="visible"
      @cancel="onClose"
      :footer="null"
      :bodyStyle="{ padding: '0px' }"
      :centered="true"
      wrapClassName="mobile-preview-modal"
      :destroyOnClose="true"
    >
      <MobilePreviewContent
        :previewJson="previewJson"
        :h5Url="h5Url"
        :pageUrl="pageUrl"
        @message="onPreviewMessage"
        :domain="domain"
        :qrcode="qrcode"
        :editable="editable"
        :phoneUiWidth="phoneUiWidth"
      />
    </a-modal>
    <template v-else>
      <div style="background-color: #fff">
        <MobilePreviewContent
          :previewJson="previewJson"
          :h5Url="h5Url"
          :pageUrl="pageUrl"
          @message="onPreviewMessage"
          :domain="domain"
          :qrcode="qrcode"
          :editable="editable"
          :phoneUiWidth="phoneUiWidth"
        />
      </div>
    </template>
  </span>
</template>
<style lang="less">
.phone-ui {
  .preview-server-icon {
    background: var(--w-primary-color);
    padding: 3px 8px;
    color: #fff;
    border-radius: 14px;
    font-size: 8px;
  }
}
.mobile-preview-modal {
  .ant-modal-body {
    padding: 0px;
    background: transparent;
  }
  .ant-modal-content {
    background: transparent;
    box-shadow: unset;
  }
  .ant-modal-close {
    background: #ffffff;
    border-radius: 100%;
    color: #000;
    font-weight: bolder;
  }
}
</style>
<script type="text/babel">
import MobilePreviewContent from './mobile-preview-content.vue';

export default {
  name: 'MobilePreview',
  model: {
    prop: 'modalVisible'
  },
  props: {
    modalVisible: { type: Boolean, default: false },
    designer: Object,
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
    },
    trigger: {
      type: String,
      default: 'click'
    },

    displayStyle: {
      type: String,
      default: 'modal'
    }
  },

  data() {
    return {
      visible: this.modalVisible,
      vModel: this.$listeners.input != undefined
    };
  },
  beforeCreate() {},
  components: { MobilePreviewContent },
  computed: {
    eventName() {
      return this.trigger;
    }
  },
  created() {},
  methods: {
    onPreviewMessage(e) {
      this.$emit('message', e);
    },

    onInput() {
      if (this.vModel) {
        this.$emit('input', this.visible);
      }
    },
    onClose() {
      this.visible = false;
      this.loading = true;
      this.onInput();
    },
    onTrigger(e) {
      this.visible = true;
      if (e) {
        e.stopPropagation();
      }
    }
  },

  mounted() {},

  watch: {
    modalVisible: {
      handler(v) {
        this.visible = v;
        if (this.visible) {
          this.onTrigger();
        }
      }
    }
  }
};
</script>
