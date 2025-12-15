<template>
  <span>
    <span v-on:[eventName]="onTrigger">
      <slot></slot>
    </span>

    <a-drawer
      v-if="containerCreated"
      :title="title"
      :placement="placement"
      :get-container="'#' + id"
      :mask="false"
      :closable="true"
      :destroyOnClose="destroyOnClose"
      :visible="visible"
      :bodyStyle="{ paddingBottom: hasFooter ? '70px' : '20px', ...bodyStyle }"
      :width="vWidth"
      :zIndex="zIndex"
      class="widget-design-drawer pt-drawer"
      :header-style="{ position: 'fixed', zIndex: 3, width: vWidth }"
      :wrap-style="{ position: 'absolute', left: placement == 'left' ? '0px' : 'unset', top: wrapStyleTop }"
      @close="closeDrawer"
      :afterVisibleChange="afterVisibleChange"
    >
      <slot name="content"></slot>
      <div
        v-if="hasFooter"
        class="ant-drawer-footer"
        :style="{
          position: 'absolute',
          bottom: 0,
          width: '100%',
          borderTop: '1px solid #e8e8e8',
          padding: '10px 16px',
          textAlign: 'right',
          left: 0,
          zIndex: 2,
          background: '#fff',
          borderRadius: '0 0 4px 4px'
        }"
      >
        <slot name="footer" :close="closeDrawer"></slot>
      </div>
    </a-drawer>
  </span>
</template>

<script type="text/babel">
export default {
  name: 'WidgetDesignDrawer',
  model: {
    prop: 'drawerVisible'
  },
  props: {
    drawerVisible: { type: Boolean, default: false },
    designer: Object,
    zIndex: { type: Number, default: 999 },
    wrapStyleTop: { type: Number | String, default: '-40px' },
    width: { type: Number | String, default: 640 },
    title: { type: String, default: '更多配置' },
    closeOpenDrawer: { type: Boolean, default: true },
    destroyOnClose: { type: Boolean, default: false },
    bodyStyle: Object,
    id: String,
    placement: {
      type: String,
      default: 'right'
    },
    trigger: {
      type: String,
      default: 'click'
    },
    drawerContainer: Function
  },

  data() {
    return {
      // _id : this.designer.selectedId!=undefined? `${this.designer.selectedId}:${this.id}`
      visible: this.drawerVisible,
      containerCreated: false,
      hasFooter: this.$scopedSlots.footer != undefined,
      vModel: this.$listeners.input != undefined
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    vWidth() {
      return typeof this.width == 'number' ? this.width + 'px' : this.width;
    },
    eventName() {
      return this.trigger;
    }
  },
  created() {},
  methods: {
    afterVisibleChange(visible) {
      if (!visible) {
        this.container.setAttribute('style', 'overflow:hidden;display:none;');
      } else {
        this.container.setAttribute('style', 'overflow:hidden;');
      }
    },
    createDesignDrawerContainer() {
      if (!this.containerCreated || !document.querySelector('#' + this.id)) {
        // 初始化创建容器
        let container = document.createElement('div');
        container.setAttribute('id', this.id);
        container.setAttribute('style', 'overflow:hidden'); // 防止容器抖动现象
        if (typeof this.drawerContainer == 'function') {
          this.drawerContainer().appendChild(container);
        } else {
          document.querySelector('#design-main .widget-build-drop-container').appendChild(container);
        }
        this.container = document.querySelector('#' + this.id);
        this.containerCreated = true;
      }
    },
    closeDrawer() {
      this.visible = false;
      this.onInput();
    },
    onInput() {
      if (this.vModel) {
        this.$emit('input', this.visible);
      }
    },
    openDrawer() {
      let _this = this;
      this.createDesignDrawerContainer();
      if (this.designer.drawerVisibleKey === this.id || this.designer.unForceRenderDrawerVisibleKey == this.id) {
        this.visible = true;
        this.onInput();
        this.container.setAttribute('style', 'overflow:hidden;');
        return;
      }
      let forceRenderWidget = this.designer.selectedWidget && this.designer.selectedWidget.forceRender !== false;
      if (this.closeOpenDrawer) {
        if (forceRenderWidget && this.designer.drawerVisibleKey != undefined) {
          this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
        } else if (!forceRenderWidget && this.designer.unForceRenderDrawerVisibleKey != undefined) {
          this.designer.emitEvent('closeDrawer:' + this.designer.unForceRenderDrawerVisibleKey);
        }
      }
      if (forceRenderWidget) {
        this.designer.drawerVisibleKey = this.id;
      } else {
        this.designer.unForceRenderDrawerVisibleKey = this.id;
      }
      this.visible = true;
      this.container.setAttribute('style', 'overflow:hidden;');
      this.onInput();
    },

    onTrigger(e) {
      this.openDrawer();
      e.stopPropagation();
    }
  },

  mounted() {
    let _this = this;
    this.designer.handleEvent('closeDrawer:' + this.id, function () {
      _this.visible = false;
      _this.onInput();
    });
  },
  updated() {
    if (this.visible) {
      this.openDrawer();
    }
  },
  watch: {
    drawerVisible: {
      handler(v) {
        if (v) {
          this.openDrawer();
        } else {
          this.closeDrawer();
        }
      }
    }
  }
};
</script>
