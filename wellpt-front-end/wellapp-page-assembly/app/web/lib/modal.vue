<template>
  <span :style="{ display: elementHide ? 'none' : 'inline-block' }">
    <span v-on:[eventName]="onTrigger">
      <slot></slot>
    </span>
    <a-modal
      :width="fullscreen ? '100%' : width"
      :maskClosable="false"
      :forceRender="forceRender"
      :destroyOnClose="destroyOnClose"
      :visible="modalVisible"
      :wrapClassName="wrapClassName"
      :dialogClass="dialogClass"
      :dialogStyle="dialogStyle"
      :centered="centered"
      :closable="closable"
      :keyboard="escKeyClosable"
      @cancel="onCancel"
      @ok="onOk"
      @afterClose="afterClose"
      :okText="okText"
      :mask="mask"
      :getContainer="container"
      :okButtonProps="okButtonProps"
      :cancelButtonProps="cancelButtonProps"
      :zIndex="zIndex"
      :bodyStyle="{ maxHeight: vBodyHeight, overflowY: 'auto', ...bodyStyle }"
      ref="modalComponentRef"
    >
      <template slot="title" v-if="titleSlots || title">
        <template v-if="titleSlots">
          <slot name="title"></slot>
        </template>
        <template>
          {{ title }}
        </template>
      </template>
      <slot name="content" :closeModal="hide"></slot>
      <template slot="footer" v-if="footerSlots">
        <slot name="footer"></slot>
      </template>
    </a-modal>
  </span>
</template>
<style lang="less">
.modal-wrap.no-footer {
  > .ant-modal > .ant-modal-content > .ant-modal-footer {
    display: none;
  }
  .pt-modal {
    --w-pt-modal-body-padding-b: var(--w-padding-md);
  }
}
.modal-wrap.fullscreen {
  > .ant-modal {
    width: 100%;
    position: absolute;
    top: 0px;
    padding: 0px;
    .ant-modal-body {
      height: e('calc(100vh - 110px)');
      max-height: none;
    }
  }
}
</style>
<script type="text/babel">
import { debounce } from 'lodash';
export default {
  name: 'Modal',
  inject: ['locale'],
  model: {
    prop: 'visible'
  },
  props: {
    fullscreen: { type: Boolean, default: false },
    visible: { type: Boolean, default: false },
    wrapperClass: String,
    dialogClass: { type: String | Object, default: 'pt-modal' },
    bodyStyle: Object,
    width: { type: Number | String, default: 600 },
    zIndex: { type: Number, default: 999 },
    height: { type: Number | String, default: 500 },
    maxHeight: { type: Number | String, default: 500 },
    title: { type: String, default: '' },
    id: String,
    forceRender: {
      type: Boolean,
      default: false
    },
    trigger: {
      type: String,
      default: 'click'
    },
    closable: { type: Boolean, default: true },
    escKeyClosable: { type: Boolean, default: true },
    destroyOnClose: {
      type: Boolean,
      default: false
    },
    centered: Boolean,
    dialogStyle: Object,
    mask: { type: Boolean, default: false },
    afterClose: { type: Function, default: function () {} },
    ok: Function,
    okText: String,
    cancel: Function,
    container: Function, // 返回一个HTMLElement元素
    okButtonProps: Object,
    cancelButtonProps: Object
  },

  data() {
    let titleSlots = this.$scopedSlots.title != undefined;
    let footerSlots = this.$scopedSlots.footer != undefined;
    let elementHide = this.$slots.default == undefined;
    return {
      titleSlots,
      elementHide,
      footerSlots,
      modalVisible: this.visible,
      hasFooter: this.ok != undefined || footerSlots,
      vModel: this.$listeners.input != undefined
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    vBodyHeight() {
      return this.fullscreen ? 'none' : typeof this.maxHeight == 'string' ? this.maxHeight : this.maxHeight + 'px';
    },
    wrapClassName() {
      let className = ['modal-wrap'];
      if (this.wrapperClass) {
        className.push(this.wrapperClass);
      }
      if (!this.hasFooter) {
        className.push('no-footer');
      }
      if (this.fullscreen) {
        className.push('fullscreen');
      }
      return className.join(' ');
    },
    eventName() {
      return this.trigger;
    }
  },
  created() {},
  methods: {
    hide() {
      this.modalVisible = false;
      if (this.vModel) {
        this.$emit('input', false);
      }
    },
    onCancel() {
      if (typeof this.cancel === 'function') {
        this.cancel();
      }
      this.hide();
    },
    onOk() {
      if (typeof this.ok === 'function') {
        let _this = this;
        this.ok(close => {
          if (close) {
            _this.hide();
          }
        });
      }
    },
    onTrigger() {
      this.modalVisible = true;
      if (this.vModel) {
        this.$emit('input', true);
      }
      this.emitShowEvent();
    },
    emitShowEvent: debounce(function () {
      this.$nextTick(() => {
        this.$emit('show');
      });
    }, 200)
  },

  mounted() {},
  watch: {
    visible: {
      handler(v) {
        this.modalVisible = v;
        if (v) {
          this.emitShowEvent();
        }
      }
    }
  }
};
</script>
