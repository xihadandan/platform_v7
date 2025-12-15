<template>
  <span>
    <span v-on:[eventName]="onTrigger">
      <slot></slot>
    </span>
    <a-modal
      :dialogClass="dialogClass"
      :destroyOnClose="destroyOnClose"
      :width="width"
      :maskClosable="false"
      :forceRender="forceRender"
      :getContainer="getContainer"
      :title="title"
      :visible="visible"
      :wrapClassName="hasFooter ? 'widget-build-modal-wrap' : 'widget-build-modal-wrap no-footer'"
      @cancel="cancelModal"
      @ok="okModal"
      :mask="mask"
      :zIndex="zIndex"
      :bodyStyle="{ maxHeight: maxHeight + 'px', overflowY: 'auto', ...bodyStyle }"
    >
      <template slot="title">
        <slot name="title" v-if="hasTitleSlot"></slot>
        <template v-else>
          {{ title || '更多配置' }}
        </template>
      </template>

      <slot name="content"></slot>
      <template slot="footer" v-if="hasFooter">
        <slot name="footer"></slot>
      </template>
    </a-modal>
  </span>
</template>

<script type="text/babel">
export default {
  name: 'WidgetDesignModal',
  props: {
    designer: Object,
    width: { type: Number | String, default: 600 },
    zIndex: { type: Number, default: 999 },
    maxHeight: { type: Number, default: 500 },
    title: { type: String },
    dialogClass: { type: String | Object, default: 'pt-modal' },
    bodyStyle: Object,
    id: String,
    destroyOnClose: {
      type: Boolean,
      default: false
    },
    forceRender: {
      type: Boolean,
      default: false
    },
    trigger: {
      type: String,
      default: 'click'
    },
    ok: Function,
    mask: {
      type: Boolean,
      default: false
    },
    bodyContainer: [Boolean, Function] // true时默认挂载body
  },

  data() {
    return {
      visible: false,
      hasFooter: this.$slots.footer && this.$slots.footer.length > 0,
      hasTitleSlot: this.$slots.title && this.$slots.title.length > 0
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    eventName() {
      return this.trigger;
    }
  },
  created() {},
  methods: {
    onTrigger() {
      this.visible = true;
      this.$emit('onTrigger');
    },
    cancelModal() {
      this.visible = false;
      this.$emit('onCancel');
    },
    okModal() {
      this.visible = false;
      if (this.ok) {
        this.ok();
      }
    },
    getContainer() {
      if (this.bodyContainer === true) {
        return document.body;
      } else if (typeof this.bodyContainer == 'function') {
        return this.bodyContainer();
      } else if (document.querySelector('#design-main')) {
        return document.querySelector('#design-main');
      }
      return document.body;
    }
  },

  mounted() {}
};
</script>
