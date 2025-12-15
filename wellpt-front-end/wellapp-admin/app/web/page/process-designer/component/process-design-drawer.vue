<template>
  <span>
    <span v-on:[eventName]="onTrigger">
      <slot></slot>
    </span>

    <a-drawer
      v-if="visible"
      :title="title"
      placement="right"
      :get-container="'#' + id"
      :mask="false"
      :closable="true"
      :visible="visible"
      :bodyStyle="{ paddingBottom: hasFooter ? '70px' : '20px', ...bodyStyle }"
      :width="width"
      :zIndex="zIndex"
      class="widget-design-drawer pt-drawer"
      :header-style="{ position: 'fixed', zIndex: 2, width: width + 'px' }"
      :wrap-style="{ position: 'absolute', top: '40px' }"
      @close="
        () => {
          visible = false;
          // 清空当前设计器的drawerVisibleKey
          if (this.designer.drawerVisibleKey === this.id) {
            this.designer.drawerVisibleKey = '';
          }
        }
      "
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
          zIndex: 1,
          background: '#fff',
          borderRadius: '0 0 4px 4px'
        }"
      >
        <slot name="footer"></slot>
      </div>
    </a-drawer>
  </span>
</template>

<script type="text/babel">
export default {
  props: {
    zIndex: { type: Number, default: 999 },
    width: { type: Number, default: 640 },
    title: { type: String, default: '更多配置' },
    closeOpenDrawer: { type: Boolean, default: true },
    id: String,
    bodyStyle: Object,
    disabled: {
      type: Boolean,
      default: false
    },
    trigger: {
      type: String,
      default: 'click'
    },
    container: {
      type: String,
      default: '#design-main'
    }
  },
  inject: ['designer', 'pageContext', 'drawerContainer'],
  data() {
    return { visible: false, hasFooter: this.$slots.footer && this.$slots.footer.length > 0 };
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
    openDrawer() {
      let _this = this;
      if (!document.querySelector('#' + this.id)) {
        // 初始化创建容器
        let container = document.createElement('div');
        container.setAttribute('id', this.id);
        container.setAttribute('style', 'position:absolute;'); // 防止容器抖动现象
        document.querySelector(this.drawerContainer || this.container).appendChild(container);
      }
      if (this.designer.drawerVisibleKey === this.id) {
        this.visible = true;
        return;
      }
      if (this.closeOpenDrawer && this.designer.drawerVisibleKey != undefined) {
        // 关闭已打开的其他抽屉
        this.pageContext.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
      }

      this.designer.drawerVisibleKey = this.id;
      this.visible = true;
      setTimeout(function () {
        // 防止容器抖动现象
        document.querySelector('#' + _this.id).setAttribute('style', '');
      }, 100);
    },

    onTrigger() {
      if (this.disabled) {
        return;
      }
      this.openDrawer();
    }
  },

  mounted() {
    this.pageContext.handleEvent('closeDrawer:' + this.id, () => {
      this.visible = false;
      // 清空当前设计器的drawerVisibleKey
      if (this.designer.drawerVisibleKey === this.id) {
        this.designer.drawerVisibleKey = '';
      }
    });
  }
};
</script>
